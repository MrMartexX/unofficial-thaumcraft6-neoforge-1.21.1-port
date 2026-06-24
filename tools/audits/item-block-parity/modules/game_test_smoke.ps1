[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$PortManifestPath,
    [string]$PortRoot = "05_neoforge_port",
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path -LiteralPath $RepoRoot).Path
$portPath = Join-Path $RepoRoot $PortRoot
if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw "Port manifest not found: $PortManifestPath" }
if (-not (Test-Path -LiteralPath $portPath -PathType Container)) { throw "Port root not found: $portPath" }
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_game_test_smoke_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_game_test_smoke_report.md" }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}

function Add-Row(
    $Rows,
    [string]$Subcheck,
    [string]$Status,
    [string]$Path,
    [string]$Evidence,
    [string]$Severity = "info"
) {
    $Rows.Add([pscustomobject][ordered]@{
        check = "game_test_smoke"
        subcheck = $Subcheck
        status = $Status
        severity = $Severity
        path = $Path
        evidence = $Evidence
    })
}

function Get-TextFile([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { return "" }
    return Get-Content -Raw -LiteralPath $Path
}

function Find-UniqueMatches([string]$Text, [string]$Pattern) {
    if ([string]::IsNullOrWhiteSpace($Text)) { return @() }
    return @([regex]::Matches($Text, $Pattern) | ForEach-Object {
        if ($_.Groups["value"].Success) { $_.Groups["value"].Value } else { $_.Value }
    } | Where-Object { $_ } | Sort-Object -Unique)
}

$port = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$buildGradle = Join-Path $portPath "build.gradle"
$srcMainJava = Join-Path $portPath "src/main/java"
$srcTestJava = Join-Path $portPath "src/test/java"
$srcGameTestJava = Join-Path $portPath "src/gametest/java"
$srcGameTestResources = Join-Path $portPath "src/gametest/resources"
$rows = [System.Collections.Generic.List[object]]::new()

if (Test-Path -LiteralPath $buildGradle -PathType Leaf) {
    Add-Row $rows "build_gradle_present" "SCRIPTED_SMOKE_PASS" (ConvertTo-RelativeRepoPath $buildGradle) "build.gradle exists" "info"
} else {
    Add-Row $rows "build_gradle_present" "SCRIPTED_SMOKE_REVIEW_NEEDED" (ConvertTo-RelativeRepoPath $buildGradle) "build.gradle missing; cannot inspect run/GameTest wiring" "review"
}

$gradleText = Get-TextFile $buildGradle
if ($gradleText -match 'neoforge\.enabledGameTestNamespaces') {
    Add-Row $rows "gametest_namespace_property" "SCRIPTED_SMOKE_PASS" (ConvertTo-RelativeRepoPath $buildGradle) "neoforge.enabledGameTestNamespaces is configured" "info"
} else {
    Add-Row $rows "gametest_namespace_property" "SCRIPTED_SMOKE_REVIEW_NEEDED" (ConvertTo-RelativeRepoPath $buildGradle) "neoforge.enabledGameTestNamespaces not found; GameTest namespace wiring needs review" "review"
}

foreach ($runName in @("client", "server", "data")) {
    $pattern = "(?s)runs\s*\{.*?$runName\s*\{"
    if ($gradleText -match $pattern) {
        Add-Row $rows "gradle_run_$runName" "SCRIPTED_SMOKE_PASS" (ConvertTo-RelativeRepoPath $buildGradle) "neoForge.runs.$runName block appears to be configured" "info"
    } else {
        Add-Row $rows "gradle_run_$runName" "SCRIPTED_SMOKE_REVIEW_NEEDED" (ConvertTo-RelativeRepoPath $buildGradle) "neoForge.runs.$runName block not found by static scan" "review"
    }
}

foreach ($pathInfo in @(
    [pscustomobject]@{ subcheck = "src_main_java"; path = $srcMainJava; required = $true },
    [pscustomobject]@{ subcheck = "src_test_java"; path = $srcTestJava; required = $false },
    [pscustomobject]@{ subcheck = "src_gametest_java"; path = $srcGameTestJava; required = $false },
    [pscustomobject]@{ subcheck = "src_gametest_resources"; path = $srcGameTestResources; required = $false }
)) {
    if (Test-Path -LiteralPath $pathInfo.path -PathType Container) {
        $javaFiles = @(Get-ChildItem -LiteralPath $pathInfo.path -Recurse -File -Filter "*.java" -ErrorAction SilentlyContinue)
        Add-Row $rows $pathInfo.subcheck "SCRIPTED_SMOKE_PASS" (ConvertTo-RelativeRepoPath $pathInfo.path) "Directory exists; javaFiles=$($javaFiles.Count)" "info"
    } elseif ($pathInfo.required) {
        Add-Row $rows $pathInfo.subcheck "SCRIPTED_SMOKE_REVIEW_NEEDED" (ConvertTo-RelativeRepoPath $pathInfo.path) "Required source directory missing" "review"
    } else {
        Add-Row $rows $pathInfo.subcheck "SCRIPTED_SMOKE_REVIEW_NEEDED" (ConvertTo-RelativeRepoPath $pathInfo.path) "Optional GameTest/scripted smoke fixture directory not present yet" "review"
    }
}

$tcPropertyEvidence = Find-UniqueMatches $gradleText "tc\.[A-Za-z0-9_.-]+"
if ($tcPropertyEvidence.Count -gt 0) {
    Add-Row $rows "gradle_scripted_runtime_hooks" "SCRIPTED_SMOKE_PASS" (ConvertTo-RelativeRepoPath $buildGradle) "Gradle exposes tc.* audit hooks: $($tcPropertyEvidence -join ', ')" "info"
} else {
    Add-Row $rows "gradle_scripted_runtime_hooks" "SCRIPTED_SMOKE_REVIEW_NEEDED" (ConvertTo-RelativeRepoPath $buildGradle) "No tc.* audit hook properties found in build.gradle" "review"
}

$javaFilesAll = @()
if (Test-Path -LiteralPath $srcMainJava -PathType Container) {
    $javaFilesAll = @(Get-ChildItem -LiteralPath $srcMainJava -Recurse -File -Filter "*.java" -ErrorAction SilentlyContinue)
}
$scriptedAuditFiles = [System.Collections.Generic.List[string]]::new()
foreach ($javaFile in $javaFilesAll) {
    $text = Get-TextFile $javaFile.FullName
    if ($text -match 'System\.getProperty\("tc\.' -or $text -match 'Boolean\.getBoolean\("tc\.' -or $text -match 'tc\.[A-Za-z0-9_.-]+') {
        $scriptedAuditFiles.Add((ConvertTo-RelativeRepoPath $javaFile.FullName))
    }
}
$scriptedAuditEvidence = @($scriptedAuditFiles | Sort-Object -Unique)
if ($scriptedAuditEvidence.Count -gt 0) {
    Add-Row $rows "scripted_runtime_hook_evidence" "SCRIPTED_SMOKE_PASS" (ConvertTo-RelativeRepoPath $srcMainJava) "Found runtime/scripted audit references in $($scriptedAuditEvidence.Count) Java files" "info"
} else {
    Add-Row $rows "scripted_runtime_hook_evidence" "SCRIPTED_SMOKE_REVIEW_NEEDED" (ConvertTo-RelativeRepoPath $srcMainJava) "No tc.* runtime/scripted audit references found in Java sources" "review"
}

$recommendedCommands = @(
    '.\\gradlew.bat -p 05_neoforge_port runData',
    '.\\gradlew.bat -p 05_neoforge_port runServer -PtcRunServerWorld=TC_SMOKE -Dtc.arcaneRecipeAudit=true -Dtc.arcaneRecipeAuditPath=../tools/reports/local/runtime/arcane_recipe_smoke.json',
    '.\\gradlew.bat -p 05_neoforge_port runServer -PtcRunServerWorld=TC_SMOKE -Dtc.crucibleBehaviorAudit=true -Dtc.crucibleBehaviorAuditPath=../tools/reports/local/runtime/crucible_smoke.json'
)
foreach ($cmd in $recommendedCommands) {
    Add-Row $rows "recommended_smoke_command" "SCRIPTED_SMOKE_PASS" "" $cmd "info"
}

$ordered = @($rows | Sort-Object severity, subcheck, path)
$summary = [ordered]@{
    rows = $ordered.Count
    pass = @($ordered | Where-Object status -eq "SCRIPTED_SMOKE_PASS").Count
    reviewNeeded = @($ordered | Where-Object status -eq "SCRIPTED_SMOKE_REVIEW_NEEDED").Count
    errors = @($ordered | Where-Object status -eq "SCRIPTED_SMOKE_ERROR").Count
    javaFiles = $javaFilesAll.Count
    scriptedAuditFiles = $scriptedAuditEvidence.Count
    portEntries = @($port.entries).Count
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    policy = "Report-only GameTest/scripted behavior smoke readiness audit. It inventories test/run wiring and existing scripted runtime audit hooks without launching Minecraft or claiming gameplay parity."
    inputs = [ordered]@{
        portManifest = ConvertTo-RelativeRepoPath $PortManifestPath
        portRoot = $PortRoot
        buildGradle = ConvertTo-RelativeRepoPath $buildGradle
    }
    summary = $summary
    scriptedAuditFiles = @($scriptedAuditEvidence)
    recommendedCommands = @($recommendedCommands)
    results = @($ordered)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 14 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# GameTest/scripted behavior smoke readiness report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: report-only readiness audit. This does not launch Minecraft and does not claim gameplay parity.")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("- Rows: $($summary.rows)")
$lines.Add("- Pass: $($summary.pass)")
$lines.Add("- Review needed: $($summary.reviewNeeded)")
$lines.Add("- Errors: $($summary.errors)")
$lines.Add("- Java files scanned: $($summary.javaFiles)")
$lines.Add("- Scripted audit files: $($summary.scriptedAuditFiles)")
$lines.Add("")
$lines.Add("## Recommended smoke commands")
$lines.Add("")
foreach ($cmd in $recommendedCommands) { $lines.Add("- ``$cmd``") }
$lines.Add("")
$lines.Add("## Review/error rows")
$lines.Add("")
$lines.Add("| Status | Subcheck | Path | Evidence |")
$lines.Add("|---|---|---|---|")
foreach ($row in @($ordered | Where-Object { $_.status -ne "SCRIPTED_SMOKE_PASS" } | Select-Object -First 300)) {
    $evidence = if ($row.evidence) { $row.evidence.Replace("|", "\\|") } else { "" }
    $lines.Add("| $($row.status) | $($row.subcheck) | $($row.path) | $evidence |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "GameTest/scripted behavior smoke report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), errors=$($summary.errors), javaFiles=$($summary.javaFiles), scriptedAuditFiles=$($summary.scriptedAuditFiles)"

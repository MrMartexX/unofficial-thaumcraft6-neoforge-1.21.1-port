[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$PortRoot,
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path -LiteralPath $RepoRoot).Path

function Resolve-WithinRepo([string]$PathValue) {
    if ([string]::IsNullOrWhiteSpace($PathValue)) { return "" }
    if ([System.IO.Path]::IsPathRooted($PathValue)) {
        if (Test-Path -LiteralPath $PathValue) { return (Resolve-Path -LiteralPath $PathValue).Path }
        return $PathValue
    }
    $candidate = Join-Path $RepoRoot $PathValue
    if (Test-Path -LiteralPath $candidate) { return (Resolve-Path -LiteralPath $candidate).Path }
    return $candidate
}

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}

function Escape-MarkdownCell([object]$Value) {
    if ($null -eq $Value) { return "" }
    $s = if ($Value -is [array]) { ($Value -join "; ") } else { [string]$Value }
    return $s.Replace("|", "\|").Replace("`r", " ").Replace("`n", " ")
}

function Add-Row {
    param(
        [System.Collections.Generic.List[object]]$Rows,
        [string]$Area,
        [string]$Status,
        [string]$Severity,
        [string]$Evidence,
        [object]$Details
    )
    $Rows.Add([pscustomobject][ordered]@{
        check = "runtime_smoke"
        area = $Area
        status = $Status
        severity = $Severity
        evidence = $Evidence
        details = $Details
    })
}

$PortRoot = Resolve-WithinRepo $PortRoot
if (-not $RulesRoot) { $RulesRoot = Join-Path $RepoRoot "tools/audits/item-block-parity/rules" }
$rulesPath = Join-Path $RulesRoot "runtime-smoke-rules.json"
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
    $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
} else {
    $rules = [pscustomobject]@{
        status = [pscustomobject]@{
            pass = "RUNTIME_SMOKE_PASS"
            review = "RUNTIME_SMOKE_REVIEW_NEEDED"
            error = "RUNTIME_SMOKE_ERROR"
        }
    }
}
$passStatus = if ($rules.status.pass) { [string]$rules.status.pass } else { "RUNTIME_SMOKE_PASS" }
$reviewStatus = if ($rules.status.review) { [string]$rules.status.review } else { "RUNTIME_SMOKE_REVIEW_NEEDED" }
$errorStatus = if ($rules.status.error) { [string]$rules.status.error } else { "RUNTIME_SMOKE_ERROR" }

$reportRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity"
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot "item_block_runtime_smoke_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot "item_block_runtime_smoke_report.md" }

$rows = [System.Collections.Generic.List[object]]::new()
$portExists = Test-Path -LiteralPath $PortRoot -PathType Container
if ($portExists) {
    Add-Row $rows "port_root" $passStatus "info" "Port root exists." ([ordered]@{ path = ConvertTo-RelativeRepoPath $PortRoot })
} else {
    Add-Row $rows "port_root" $errorStatus "error" "Port root is missing; runtime smoke cannot be prepared." ([ordered]@{ path = $PortRoot })
}

$gradleCandidates = @(
    Join-Path $PortRoot "gradlew.bat"
    Join-Path $PortRoot "gradlew"
)
$gradleWrappers = @($gradleCandidates | Where-Object { Test-Path -LiteralPath $_ -PathType Leaf })
if ($gradleWrappers.Count -gt 0) {
    Add-Row $rows "gradle_wrapper" $passStatus "info" "Gradle wrapper is available for opt-in runtime/build smoke commands." ([ordered]@{ files = @($gradleWrappers | ForEach-Object { ConvertTo-RelativeRepoPath $_ }) })
} else {
    Add-Row $rows "gradle_wrapper" $errorStatus "error" "Gradle wrapper is missing under port root." ([ordered]@{ searched = @($gradleCandidates | ForEach-Object { ConvertTo-RelativeRepoPath $_ }) })
}

$buildFiles = @("build.gradle", "build.gradle.kts", "settings.gradle", "settings.gradle.kts", "gradle.properties") | ForEach-Object { Join-Path $PortRoot $_ }
$existingBuildFiles = @($buildFiles | Where-Object { Test-Path -LiteralPath $_ -PathType Leaf })
if ($existingBuildFiles.Count -gt 0) {
    Add-Row $rows "gradle_build_files" $passStatus "info" "Gradle build/configuration files are present." ([ordered]@{ files = @($existingBuildFiles | ForEach-Object { ConvertTo-RelativeRepoPath $_ }) })
} else {
    Add-Row $rows "gradle_build_files" $errorStatus "error" "No Gradle build/configuration files were found." ([ordered]@{ searched = @($buildFiles | ForEach-Object { ConvertTo-RelativeRepoPath $_ }) })
}

$modMetadataCandidates = @(
    Join-Path $PortRoot "src/main/resources/META-INF/neoforge.mods.toml"
    Join-Path $PortRoot "src/main/resources/META-INF/mods.toml"
    Join-Path $PortRoot "src/main/resources/fabric.mod.json"
)
$modMetadata = @($modMetadataCandidates | Where-Object { Test-Path -LiteralPath $_ -PathType Leaf })
if ($modMetadata.Count -gt 0) {
    Add-Row $rows "mod_metadata" $passStatus "info" "Mod metadata required for runtime loading is present." ([ordered]@{ files = @($modMetadata | ForEach-Object { ConvertTo-RelativeRepoPath $_ }) })
} else {
    Add-Row $rows "mod_metadata" $reviewStatus "review" "No committed mod metadata file was found in expected resource locations; review generated metadata setup before strict runtime certification." ([ordered]@{ searched = @($modMetadataCandidates | ForEach-Object { ConvertTo-RelativeRepoPath $_ }) })
}

$runtimeCheckModule = Join-Path $RepoRoot "tools/audits/item-block-parity/modules/runtime_checks.ps1"
$auditScript = Join-Path $RepoRoot "tools/audits/item-block-parity/audit-item-block-parity.ps1"
if ((Test-Path -LiteralPath $runtimeCheckModule -PathType Leaf) -and (Test-Path -LiteralPath $auditScript -PathType Leaf)) {
    $auditText = Get-Content -Raw -LiteralPath $auditScript
    $hasRunSmokeSwitch = $auditText.Contains("[switch]`$RunSmoke") -and $auditText.Contains("modules/runtime_checks.ps1")
    if ($hasRunSmokeSwitch) {
        Add-Row $rows "orchestrator_opt_in" $passStatus "info" "The orchestrator exposes an explicit -RunSmoke path backed by runtime_checks.ps1." ([ordered]@{ module = ConvertTo-RelativeRepoPath $runtimeCheckModule; auditScript = ConvertTo-RelativeRepoPath $auditScript })
    } else {
        Add-Row $rows "orchestrator_opt_in" $reviewStatus "review" "runtime_checks.ps1 exists, but the orchestrator opt-in wiring needs review." ([ordered]@{ module = ConvertTo-RelativeRepoPath $runtimeCheckModule; auditScript = ConvertTo-RelativeRepoPath $auditScript })
    }
} else {
    Add-Row $rows "orchestrator_opt_in" $errorStatus "error" "Runtime check module or audit orchestrator is missing." ([ordered]@{ module = ConvertTo-RelativeRepoPath $runtimeCheckModule; auditScript = ConvertTo-RelativeRepoPath $auditScript })
}

$taskEvidence = [System.Collections.Generic.List[object]]::new()
foreach ($file in $existingBuildFiles) {
    $text = Get-Content -Raw -LiteralPath $file
    foreach ($term in @("runClient", "runServer", "runData", "gameTestServer", "classes", "compileJava", "runs")) {
        if ($text -match [regex]::Escape($term)) {
            $taskEvidence.Add([pscustomobject][ordered]@{
                file = ConvertTo-RelativeRepoPath $file
                term = $term
            })
        }
    }
}
if ($taskEvidence.Count -gt 0) {
    Add-Row $rows "gradle_task_clues" $passStatus "info" "Gradle runtime/build task clues were found in checked build files." ([ordered]@{ clues = @($taskEvidence) })
} else {
    Add-Row $rows "gradle_task_clues" $reviewStatus "review" "No explicit runtime task clues were found in checked build files; Gradle may still provide tasks dynamically." ([ordered]@{ checkedFiles = @($existingBuildFiles | ForEach-Object { ConvertTo-RelativeRepoPath $_ }) })
}

$commands = @(
    "pwsh -NoProfile -File tools/audits/item-block-parity/audit-item-block-parity.ps1 -RepoRoot <repo> -Checks runtime_smoke -RunSmoke -FailMode off",
    "pwsh -NoProfile -File tools/audits/item-block-parity/audit-item-block-parity.ps1 -RepoRoot <repo> -Checks runtime_smoke -RunBuild -RunSmoke -FailMode off",
    "cd 05_neoforge_port; ./gradlew --no-daemon classes"
)
Add-Row $rows "opt_in_commands" $passStatus "info" "Safe default is report-only; runtime/build smoke commands are explicit and operator-triggered." ([ordered]@{ commands = $commands })

$orderedRows = @($rows)
$summary = [ordered]@{
    rows = $orderedRows.Count
    pass = @($orderedRows | Where-Object status -eq $passStatus).Count
    reviewNeeded = @($orderedRows | Where-Object status -eq $reviewStatus).Count
    errors = @($orderedRows | Where-Object status -eq $errorStatus).Count
    portRootExists = $portExists
    gradleWrappers = $gradleWrappers.Count
    buildFiles = $existingBuildFiles.Count
    modMetadataFiles = $modMetadata.Count
    taskClues = $taskEvidence.Count
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    policy = "Report-only runtime smoke readiness and opt-in execution inventory. This check does not launch Minecraft or Gradle by default; separate -RunSmoke/-RunBuild switches remain explicit operator actions."
    inputs = [ordered]@{
        checks = @($Checks)
        portRoot = ConvertTo-RelativeRepoPath $PortRoot
        rules = if (Test-Path -LiteralPath $rulesPath -PathType Leaf) { ConvertTo-RelativeRepoPath $rulesPath } else { "<default>" }
    }
    summary = $summary
    results = @($orderedRows)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block runtime smoke audit")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Rows | PASS | REVIEW | ERROR | Gradle wrappers | Build files | Mod metadata | Task clues |")
$lines.Add("|---:|---:|---:|---:|---:|---:|---:|---:|")
$lines.Add("| $($summary.rows) | $($summary.pass) | $($summary.reviewNeeded) | $($summary.errors) | $($summary.gradleWrappers) | $($summary.buildFiles) | $($summary.modMetadataFiles) | $($summary.taskClues) |")
$lines.Add("")
$lines.Add("## Non-pass rows")
$lines.Add("")
$lines.Add("| Status | Area | Severity | Evidence |")
$lines.Add("|---|---|---|---|")
$nonPass = @($orderedRows | Where-Object { $_.status -ne $passStatus })
foreach ($row in $nonPass) {
    $lines.Add("| $($row.status) | $($row.area) | $($row.severity) | $(Escape-MarkdownCell $row.evidence) |")
}
if ($nonPass.Count -eq 0) {
    $lines.Add("| RUNTIME_SMOKE_PASS | <all> | info | Runtime smoke readiness inventory found no errors or review rows. |")
}
$lines.Add("")
$lines.Add("## Opt-in commands")
$lines.Add("")
foreach ($command in $commands) {
    $lines.Add("- ``$command``")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Runtime smoke audit report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), errors=$($summary.errors), gradleWrappers=$($summary.gradleWrappers), buildFiles=$($summary.buildFiles), modMetadataFiles=$($summary.modMetadataFiles), taskClues=$($summary.taskClues)"

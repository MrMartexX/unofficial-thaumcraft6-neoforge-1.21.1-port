[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [string]$PortRoot = "05_neoforge_port",
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path -LiteralPath $RepoRoot).Path
if (-not $RulesRoot) { $RulesRoot = Join-Path $RepoRoot "tools/audits/item-block-parity/rules" }
$reportRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity"
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot "item_block_json_validity_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot "item_block_json_validity_report.md" }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}
function Test-WildcardAny([string]$Name, [string[]]$Patterns) {
    foreach ($pattern in @($Patterns)) {
        if ([string]::IsNullOrWhiteSpace($pattern)) { continue }
        if ($Name -like $pattern) { return $true }
    }
    return $false
}
function Test-IgnoredPath([string]$RelativePath, [string[]]$Fragments) {
    $normalized = $RelativePath.Replace("\", "/")
    foreach ($fragment in @($Fragments)) {
        if ([string]::IsNullOrWhiteSpace($fragment)) { continue }
        if ($normalized.Contains(([string]$fragment).Replace("\", "/"))) { return $true }
    }
    return $false
}
function Add-Row {
    param(
        [ref]$Rows,
        [string]$Status,
        [string]$Severity,
        [string]$Path,
        [string]$Evidence
    )
    $Rows.Value += [pscustomobject][ordered]@{
        check = "json_validity"
        status = $Status
        severity = $Severity
        path = $Path
        evidence = $Evidence
    }
}

$rulesPath = Join-Path $RulesRoot "json-validity-rules.json"
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
    $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
} else {
    $rules = [pscustomobject]@{
        scanRoots = @("05_neoforge_port/src/main/resources", "tools/audits/item-block-parity/rules")
        filePatterns = @("*.json")
        ignoredPathFragments = @("tools/reports/local/", "/build/", "/.gradle/")
        maxFiles = 12000
        status = [pscustomobject]@{ pass = "JSON_VALIDITY_PASS"; review = "JSON_VALIDITY_REVIEW_NEEDED"; error = "JSON_VALIDITY_ERROR" }
    }
}
$passStatus = if ($rules.status -and $rules.status.pass) { [string]$rules.status.pass } else { "JSON_VALIDITY_PASS" }
$reviewStatus = if ($rules.status -and $rules.status.review) { [string]$rules.status.review } else { "JSON_VALIDITY_REVIEW_NEEDED" }
$errorStatus = if ($rules.status -and $rules.status.error) { [string]$rules.status.error } else { "JSON_VALIDITY_ERROR" }
$filePatterns = @($rules.filePatterns)
if ($filePatterns.Count -eq 0) { $filePatterns = @("*.json") }
$ignoredFragments = @($rules.ignoredPathFragments)
$maxFiles = if ($rules.maxFiles) { [int]$rules.maxFiles } else { 12000 }

$rows = @()
$targets = @()
foreach ($scanRoot in @($rules.scanRoots)) {
    if ([string]::IsNullOrWhiteSpace([string]$scanRoot)) { continue }
    $fullRoot = [System.IO.Path]::GetFullPath((Join-Path $RepoRoot ([string]$scanRoot)))
    if (-not (Test-Path -LiteralPath $fullRoot -PathType Container)) {
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity "review" -Path (ConvertTo-RelativeRepoPath $fullRoot) -Evidence "Configured JSON scan root does not exist."
        continue
    }
    foreach ($file in @(Get-ChildItem -LiteralPath $fullRoot -Recurse -File -ErrorAction SilentlyContinue)) {
        $relative = ConvertTo-RelativeRepoPath $file.FullName
        if (Test-IgnoredPath -RelativePath $relative -Fragments $ignoredFragments) { continue }
        if (-not (Test-WildcardAny -Name $file.Name -Patterns $filePatterns)) { continue }
        $targets += $file
        if ($targets.Count -gt $maxFiles) { break }
    }
    if ($targets.Count -gt $maxFiles) { break }
}

if ($targets.Count -gt $maxFiles) {
    Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity "review" -Path "<scan>" -Evidence "JSON scan reached maxFiles=$maxFiles; increase rule limit if broader validation is needed."
    $targets = @($targets | Select-Object -First $maxFiles)
}

foreach ($file in @($targets | Sort-Object FullName -Unique)) {
    $relative = ConvertTo-RelativeRepoPath $file.FullName
    try {
        $null = Get-Content -Raw -LiteralPath $file.FullName | ConvertFrom-Json -AsHashTable
        Add-Row -Rows ([ref]$rows) -Status $passStatus -Severity "info" -Path $relative -Evidence "JSON parsed successfully."
    } catch {
        Add-Row -Rows ([ref]$rows) -Status $errorStatus -Severity "error" -Path $relative -Evidence ("JSON parse failed: " + $_.Exception.Message)
    }
}

if ($targets.Count -eq 0) {
    Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity "review" -Path "<scan>" -Evidence "No JSON files matched configured scan roots/patterns."
}

$orderedRows = @($rows | Sort-Object severity, path, status)
$summary = [ordered]@{
    rows = @($orderedRows).Count
    pass = @($orderedRows | Where-Object { $_.status -eq $passStatus }).Count
    reviewNeeded = @($orderedRows | Where-Object { $_.status -eq $reviewStatus }).Count
    errors = @($orderedRows | Where-Object { $_.status -eq $errorStatus }).Count
    filesScanned = @($targets).Count
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    policy = "CI-safe mechanical JSON validity check. Error rows are parse failures; review rows are scan configuration gaps or limits."
    inputs = [ordered]@{
        repoRoot = ConvertTo-RelativeRepoPath $RepoRoot
        portRoot = $PortRoot
        rules = ConvertTo-RelativeRepoPath $rulesPath
        checks = @($Checks)
        scanRoots = @($rules.scanRoots)
    }
    summary = $summary
    results = @($orderedRows)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block JSON validity report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: CI-safe mechanical JSON validity check. Error rows are parse failures.")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("- Rows: $($summary.rows)")
$lines.Add("- Pass: $($summary.pass)")
$lines.Add("- Review needed: $($summary.reviewNeeded)")
$lines.Add("- Errors: $($summary.errors)")
$lines.Add("- Files scanned: $($summary.filesScanned)")
$lines.Add("")
$lines.Add("## Non-pass rows")
$lines.Add("")
$lines.Add("| Status | Path | Evidence |")
$lines.Add("|---|---|---|")
$nonPass = @($orderedRows | Where-Object { $_.status -ne $passStatus })
if ($nonPass.Count -eq 0) {
    $lines.Add("| JSON_VALIDITY_PASS | <all> | All targeted JSON files parsed successfully. |")
} else {
    foreach ($row in $nonPass) {
        $evidence = if ($row.evidence) { $row.evidence.Replace("|", "\|") } else { "" }
        $lines.Add("| $($row.status) | $($row.path) | $evidence |")
    }
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "JSON validity report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), errors=$($summary.errors), filesScanned=$($summary.filesScanned)"

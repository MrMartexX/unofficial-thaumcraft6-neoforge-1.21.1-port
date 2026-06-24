[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [string]$ReportRoot,
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
if (-not $ReportRoot) { $ReportRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity" }
if (-not $RulesRoot) { $RulesRoot = Join-Path $RepoRoot "tools/audits/item-block-parity/rules" }
if (-not $OutputJson) { $OutputJson = Join-Path $ReportRoot "report_freshness_guard_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $ReportRoot "report_freshness_guard_report.md" }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}

function Test-WildcardAny {
    param([string]$Name, [string[]]$Patterns)
    foreach ($pattern in @($Patterns)) {
        if ([string]::IsNullOrWhiteSpace($pattern)) { continue }
        if ($Name -like $pattern) { return $true }
    }
    return $false
}

function Resolve-RepoRelativeDependency {
    param([string]$Candidate)
    if ([string]::IsNullOrWhiteSpace($Candidate)) { return $null }
    $normalized = $Candidate.Replace("\", "/").Trim()
    if ($normalized -match '^[A-Za-z]:/' -or $normalized.StartsWith("/")) { return $null }
    if ($normalized -notmatch '^(tools|src|06_docs|05_neoforge_port)/') { return $null }
    return [System.IO.Path]::GetFullPath((Join-Path $RepoRoot $normalized))
}

function Get-StringsRecursive {
    param($Value)
    if ($null -eq $Value) { return }
    if ($Value -is [string]) { $Value; return }
    if ($Value -is [System.Collections.IDictionary]) {
        foreach ($key in $Value.Keys) { Get-StringsRecursive -Value $Value[$key] }
        return
    }
    if ($Value -is [System.Collections.IEnumerable] -and -not ($Value -is [string])) {
        foreach ($item in $Value) { Get-StringsRecursive -Value $item }
        return
    }
    if ($Value.PSObject -and $Value.PSObject.Properties) {
        foreach ($property in $Value.PSObject.Properties) { Get-StringsRecursive -Value $property.Value }
    }
}

function Add-Row {
    param(
        [System.Collections.Generic.List[object]]$Rows,
        [string]$Status,
        [string]$Path,
        [string]$Severity,
        [string]$Evidence,
        [string]$LatestDependency = "",
        [string]$GeneratedAtUtc = "",
        [int]$DependencyCount = 0
    )
    $Rows.Add([pscustomobject]@{
        check = "report_freshness"
        status = $Status
        severity = $Severity
        path = $Path
        latestDependency = $LatestDependency
        generatedAtUtc = $GeneratedAtUtc
        dependencyCount = $DependencyCount
        evidence = $Evidence
    }) | Out-Null
}

$rulesPath = Join-Path $RulesRoot "report-freshness-rules.json"
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
    $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
} else {
    $rules = [pscustomobject]@{
        reportFilePatterns = @("*_report.json", "item_block_parity_not_evaluated_checks.json", "item_block_auto_fix_candidates.json")
        ignoredFilePatterns = @("legacy_primary_manifest.json", "legacy_primary_manifest.focused.json", "port_manifest.json", "port_manifest.focused.json", "report_freshness_guard_report.json")
        dependencyGlobs = @("tools/audits/item-block-parity/audit-item-block-parity.ps1", "tools/audits/item-block-parity/modules/*.ps1", "tools/audits/item-block-parity/rules/*.json", "06_docs/audits/item_block_parity_*.md")
        clockSkewSeconds = 2
        status = [pscustomobject]@{ pass = "REPORT_FRESHNESS_PASS"; review = "REPORT_FRESHNESS_REVIEW_NEEDED"; error = "REPORT_FRESHNESS_ERROR" }
    }
}

$reportPatterns = @($rules.reportFilePatterns)
$ignoredPatterns = @($rules.ignoredFilePatterns)
$dependencyGlobs = @($rules.dependencyGlobs)
$clockSkew = if ($null -ne $rules.clockSkewSeconds) { [int]$rules.clockSkewSeconds } else { 2 }
$passStatus = if ($rules.status.pass) { [string]$rules.status.pass } else { "REPORT_FRESHNESS_PASS" }
$reviewStatus = if ($rules.status.review) { [string]$rules.status.review } else { "REPORT_FRESHNESS_REVIEW_NEEDED" }
$errorStatus = if ($rules.status.error) { [string]$rules.status.error } else { "REPORT_FRESHNESS_ERROR" }

$rows = [System.Collections.Generic.List[object]]::new()

if (-not (Test-Path -LiteralPath $ReportRoot -PathType Container)) {
    Add-Row $rows $reviewStatus (ConvertTo-RelativeRepoPath $ReportRoot) "review" "Report root does not exist yet; run one or more parity checks before freshness validation."
} else {
    $globalDependencies = @()
    foreach ($glob in $dependencyGlobs) {
        $fullGlob = Join-Path $RepoRoot $glob
        $globalDependencies += @(Get-ChildItem -Path $fullGlob -File -ErrorAction SilentlyContinue)
    }
    $globalDependencies = @($globalDependencies | Sort-Object FullName -Unique)

    $files = @(Get-ChildItem -LiteralPath $ReportRoot -File -Filter "*.json" -ErrorAction SilentlyContinue | Sort-Object Name)
    $targets = @(
        foreach ($file in $files) {
            if ($file.FullName -eq $OutputJson) { continue }
            if (Test-WildcardAny -Name $file.Name -Patterns $ignoredPatterns) { continue }
            if (Test-WildcardAny -Name $file.Name -Patterns $reportPatterns) { $file }
        }
    )

    if ($targets.Count -eq 0) {
        Add-Row $rows $reviewStatus (ConvertTo-RelativeRepoPath $ReportRoot) "review" "No freshness-target JSON reports found under report root."
    }

    foreach ($file in $targets) {
        $relative = ConvertTo-RelativeRepoPath $file.FullName
        try {
            $json = Get-Content -Raw -LiteralPath $file.FullName | ConvertFrom-Json
        } catch {
            Add-Row $rows $errorStatus $relative "error" "JSON parse failed: $($_.Exception.Message)"
            continue
        }

        $dependencyPaths = @($globalDependencies | ForEach-Object { $_.FullName })
        foreach ($candidate in Get-StringsRecursive -Value $json.inputs) {
            $resolved = Resolve-RepoRelativeDependency -Candidate $candidate
            if (-not $resolved) { continue }
            if (Test-Path -LiteralPath $resolved -PathType Leaf) {
                $dependencyPaths += $resolved
            } elseif (Test-Path -LiteralPath $resolved -PathType Container) {
                $dependencyPaths += @(Get-ChildItem -LiteralPath $resolved -File -Recurse -ErrorAction SilentlyContinue | ForEach-Object { $_.FullName })
            }
        }
        $dependencyPaths = @($dependencyPaths | Where-Object { Test-Path -LiteralPath $_ -PathType Leaf } | Sort-Object -Unique)
        if ($dependencyPaths.Count -eq 0) {
            Add-Row $rows $reviewStatus $relative "review" "No existing dependency files could be resolved for this report."
            continue
        }

        $dependencyFiles = @($dependencyPaths | ForEach-Object { Get-Item -LiteralPath $_ })
        $latestDependency = @($dependencyFiles | Sort-Object LastWriteTimeUtc -Descending | Select-Object -First 1)[0]
        $latestTime = [DateTime]$latestDependency.LastWriteTimeUtc
        $reportFileTime = [DateTime]$file.LastWriteTimeUtc

        $generatedAtText = ""
        $generatedAt = $null
        $generatedAtOk = $false
        if ($json.PSObject.Properties.Name -contains "generatedAtUtc") {
            $generatedAtText = [string]$json.generatedAtUtc
            try {
                $generatedAt = [DateTime]::Parse($generatedAtText, $null, [System.Globalization.DateTimeStyles]::RoundtripKind).ToUniversalTime()
                $generatedAtOk = $true
            } catch {
                $generatedAtOk = $false
            }
        }

        $dependencyRelative = ConvertTo-RelativeRepoPath $latestDependency.FullName
        if (-not $generatedAtOk) {
            Add-Row $rows $reviewStatus $relative "review" "generatedAtUtc is missing or invalid; file mtime freshness was still checked. Latest dependency: $dependencyRelative." $dependencyRelative $generatedAtText $dependencyFiles.Count
            continue
        }

        $threshold = $latestTime.AddSeconds(-1 * $clockSkew)
        if ($generatedAt -lt $threshold -or $reportFileTime -lt $threshold) {
            $evidence = "Report is stale. generatedAtUtc=$($generatedAt.ToString("o")); fileMtimeUtc=$($reportFileTime.ToString("o")); latestDependencyUtc=$($latestTime.ToString("o")); latestDependency=$dependencyRelative."
            Add-Row $rows $reviewStatus $relative "review" $evidence $dependencyRelative $generatedAt.ToString("o") $dependencyFiles.Count
            continue
        }

        $passEvidence = "Report is fresh relative to $($dependencyFiles.Count) resolved dependency files. Latest dependency: $dependencyRelative at $($latestTime.ToString("o"))."
        Add-Row $rows $passStatus $relative "info" $passEvidence $dependencyRelative $generatedAt.ToString("o") $dependencyFiles.Count
    }
}

$orderedRows = @($rows | Sort-Object severity, path, status)
$summary = [ordered]@{
    rows = $orderedRows.Count
    pass = @($orderedRows | Where-Object status -eq $passStatus).Count
    reviewNeeded = @($orderedRows | Where-Object status -eq $reviewStatus).Count
    errors = @($orderedRows | Where-Object status -eq $errorStatus).Count
    reportRoot = ConvertTo-RelativeRepoPath $ReportRoot
    dependencyGlobs = @($dependencyGlobs)
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    policy = "Report-only stale report guard. Review rows identify local JSON reports older than their audit/rules/manifests inputs; error rows identify unreadable JSON."
    inputs = [ordered]@{
        reportRoot = ConvertTo-RelativeRepoPath $ReportRoot
        rules = ConvertTo-RelativeRepoPath $rulesPath
        checks = @($Checks)
    }
    summary = $summary
    results = @($orderedRows)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block parity report freshness guard")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: report-only stale report guard. Review rows are local-report freshness issues, not gameplay parity failures.")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("- Rows: $($summary.rows)")
$lines.Add("- Pass: $($summary.pass)")
$lines.Add("- Review needed: $($summary.reviewNeeded)")
$lines.Add("- Errors: $($summary.errors)")
$lines.Add("")
$lines.Add("## Non-pass rows")
$lines.Add("")
$lines.Add("| Status | Path | Latest dependency | Evidence |")
$lines.Add("|---|---|---|---|")
$nonPass = @($orderedRows | Where-Object { $_.status -ne $passStatus })
foreach ($row in $nonPass) {
    $evidence = if ($row.evidence) { $row.evidence.Replace("|", "\|") } else { "" }
    $lines.Add("| $($row.status) | $($row.path) | $($row.latestDependency) | $evidence |")
}
if ($nonPass.Count -eq 0) {
    $lines.Add("| REPORT_FRESHNESS_PASS | <all> | <current> | All targeted reports are fresh relative to resolved dependencies |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Report freshness guard report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), errors=$($summary.errors)"
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
if (-not $OutputJson) { $OutputJson = Join-Path $ReportRoot "report_schema_contract_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $ReportRoot "report_schema_contract_report.md" }

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

function Add-Row {
    param(
        [System.Collections.Generic.List[object]]$Rows,
        [string]$Status,
        [string]$Path,
        [string]$Evidence,
        [string]$Severity = "info",
        [string[]]$MissingFields = @()
    )
    $Rows.Add([pscustomobject][ordered]@{
        check = "report_schema"
        status = $Status
        severity = $Severity
        path = $Path
        missingFields = @($MissingFields)
        evidence = $Evidence
    })
}

$rulesPath = Join-Path $RulesRoot "report-schema-rules.json"
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
    $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
} else {
    $rules = [pscustomobject]@{
        requiredTopLevelFields = @("schemaVersion", "generatedAtUtc", "policy", "inputs", "summary", "results")
        reportFilePatterns = @("*_report.json", "item_block_parity_not_evaluated_checks.json", "item_block_auto_fix_candidates.json")
        ignoredFilePatterns = @("legacy_primary_manifest.json", "legacy_primary_manifest.focused.json", "port_manifest.json", "port_manifest.focused.json", "report_schema_contract_report.json")
        summaryCountFields = @("rows", "results")
        status = [pscustomobject]@{ pass = "REPORT_SCHEMA_PASS"; review = "REPORT_SCHEMA_REVIEW_NEEDED"; error = "REPORT_SCHEMA_ERROR" }
    }
}

$requiredFields = @($rules.requiredTopLevelFields)
$reportPatterns = @($rules.reportFilePatterns)
$ignoredPatterns = @($rules.ignoredFilePatterns)
$summaryCountFields = @($rules.summaryCountFields)
$passStatus = if ($rules.status.pass) { [string]$rules.status.pass } else { "REPORT_SCHEMA_PASS" }
$reviewStatus = if ($rules.status.review) { [string]$rules.status.review } else { "REPORT_SCHEMA_REVIEW_NEEDED" }
$errorStatus = if ($rules.status.error) { [string]$rules.status.error } else { "REPORT_SCHEMA_ERROR" }

$rows = [System.Collections.Generic.List[object]]::new()

if (-not (Test-Path -LiteralPath $ReportRoot -PathType Container)) {
    Add-Row $rows $reviewStatus (ConvertTo-RelativeRepoPath $ReportRoot) "Report root does not exist yet; run one or more parity checks before strict schema validation." "review"
} else {
    $files = @(Get-ChildItem -LiteralPath $ReportRoot -File -Filter "*.json" -ErrorAction SilentlyContinue | Sort-Object Name)
    $targets = @(
        foreach ($file in $files) {
            if ($file.FullName -eq $OutputJson) { continue }
            if (Test-WildcardAny -Name $file.Name -Patterns $ignoredPatterns) { continue }
            if (Test-WildcardAny -Name $file.Name -Patterns $reportPatterns) { $file }
        }
    )

    if ($targets.Count -eq 0) {
        Add-Row $rows $reviewStatus (ConvertTo-RelativeRepoPath $ReportRoot) "No schema-target JSON reports found under report root." "review"
    }

    foreach ($file in $targets) {
        $relative = ConvertTo-RelativeRepoPath $file.FullName
        try {
            $json = Get-Content -Raw -LiteralPath $file.FullName | ConvertFrom-Json
        } catch {
            Add-Row $rows $errorStatus $relative "JSON parse failed: $($_.Exception.Message)" "error"
            continue
        }

        $propertyNames = @($json.PSObject.Properties.Name)
        $missing = @($requiredFields | Where-Object { $_ -notin $propertyNames })
        if ($missing.Count -gt 0) {
            Add-Row $rows $reviewStatus $relative "Report is parseable but missing required top-level fields: $($missing -join ', ')" "review" $missing
            continue
        }

        $generatedAtOk = $true
        try { $null = [DateTime]::Parse([string]$json.generatedAtUtc, $null, [System.Globalization.DateTimeStyles]::RoundtripKind) } catch { $generatedAtOk = $false }
        if (-not $generatedAtOk) {
            Add-Row $rows $reviewStatus $relative "generatedAtUtc is present but not an ISO/Roundtrip DateTime." "review"
            continue
        }

        $resultsCount = @($json.results).Count
        $summaryMatches = $false
        foreach ($field in $summaryCountFields) {
            if ($json.summary.PSObject.Properties.Name -contains $field) {
                $value = $json.summary.$field
                if ($null -ne $value -and [int]$value -eq $resultsCount) {
                    $summaryMatches = $true
                    break
                }
            }
        }
        if (-not $summaryMatches) {
            Add-Row $rows $reviewStatus $relative "summary does not expose a rows/results count matching results.Count=$resultsCount." "review"
            continue
        }

        Add-Row $rows $passStatus $relative "Required report schema fields are present and summary count matches results." "info"
    }
}

$orderedRows = @($rows | Sort-Object severity, path, status)
$summary = [ordered]@{
    rows = $orderedRows.Count
    pass = @($orderedRows | Where-Object status -eq $passStatus).Count
    reviewNeeded = @($orderedRows | Where-Object status -eq $reviewStatus).Count
    errors = @($orderedRows | Where-Object status -eq $errorStatus).Count
    reportRoot = ConvertTo-RelativeRepoPath $ReportRoot
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    policy = "Report-only schema contract validation. Review rows identify reports that are parseable but not yet fully contracted; error rows identify invalid JSON or unreadable report files."
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
$lines.Add("# Item/block parity report schema contract")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: report-only schema contract validation. Review rows are framework hardening work, not gameplay parity failures.")
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
$lines.Add("| Status | Path | Missing fields | Evidence |")
$lines.Add("|---|---|---|---|")
$nonPass = @($orderedRows | Where-Object { $_.status -ne $passStatus })
foreach ($row in $nonPass) {
    $missingText = if ($row.missingFields -and @($row.missingFields).Count -gt 0) { @($row.missingFields) -join ", " } else { "" }
    $evidence = if ($row.evidence) { $row.evidence.Replace("|", "\|") } else { "" }
    $lines.Add("| $($row.status) | $($row.path) | $missingText | $evidence |")
}
if ($nonPass.Count -eq 0) {
    $lines.Add("| REPORT_SCHEMA_PASS | <all> | <none> | All targeted reports satisfy the schema contract |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Report schema contract report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), errors=$($summary.errors)"

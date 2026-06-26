[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [string]$ReportRoot,
    [string]$RulesRoot,
    [string[]]$Checks,
    [ValidateSet("off", "safe", "strict")][string]$FailMode = "off",
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path -LiteralPath $RepoRoot).Path
if (-not $ReportRoot) { $ReportRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity" }
if (-not $RulesRoot) { $RulesRoot = Join-Path $RepoRoot "tools/audits/item-block-parity/rules" }
if (-not $OutputJson) { $OutputJson = Join-Path $ReportRoot "ci_strict_safe_policy_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $ReportRoot "ci_strict_safe_policy_report.md" }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}
function Read-JsonOrNull([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { return $null }
    try { return Get-Content -Raw -LiteralPath $Path | ConvertFrom-Json } catch { return $null }
}
function Get-SummaryInt($Report, [string]$Name) {
    if ($null -eq $Report -or $null -eq $Report.summary) { return 0 }
    $value = $Report.summary.$Name
    if ($null -eq $value) { return 0 }
    try { return [int]$value } catch { return 0 }
}
function Add-Row($Rows, [string]$Area, [string]$Status, [string]$Severity, [string]$Evidence, $Details) {
    $Rows.Add([pscustomobject][ordered]@{
        check = "ci_strict_safe_policy"
        area = $Area
        status = $Status
        severity = $Severity
        evidence = $Evidence
        details = $Details
    })
}
function Escape-MarkdownCell([object]$Value) {
    if ($null -eq $Value) { return "" }
    $s = if ($Value -is [array]) { ($Value -join "; ") } else { [string]$Value }
    return $s.Replace("|", "\|").Replace("`r", " ").Replace("`n", " ")
}

$passStatus = "CI_POLICY_PASS"
$reviewStatus = "CI_POLICY_REVIEW_NEEDED"
$errorStatus = "CI_POLICY_ERROR"
$policyMode = switch ($FailMode) {
    "off" { "report_only" }
    "safe" { "safe" }
    "strict" { "strict" }
}
$rows = [System.Collections.Generic.List[object]]::new()
$rulesPath = Join-Path $RulesRoot "ci-strict-safe-policy.json"
$workflowPath = Join-Path $RepoRoot ".github/workflows/item-block-framework-verifier.yml"
$rules = Read-JsonOrNull $rulesPath

if ($null -eq $rules) {
    Add-Row $rows "policy_rules" $errorStatus "error" "CI strict/safe policy rules are missing or invalid JSON." ([ordered]@{ path = ConvertTo-RelativeRepoPath $rulesPath })
} else {
    Add-Row $rows "policy_rules" $passStatus "info" "CI strict/safe policy rules parse successfully." ([ordered]@{ path = ConvertTo-RelativeRepoPath $rulesPath; modes = @($rules.modes).Count; activeMode = $policyMode })
}

if (Test-Path -LiteralPath $workflowPath -PathType Leaf) {
    $workflowText = Get-Content -LiteralPath $workflowPath -Raw
    $requiredTokens = if ($rules -and $rules.requiredWorkflowTokens) { @($rules.requiredWorkflowTokens | ForEach-Object { [string]$_ }) } else { @("policy_mode", "report_only", "safe", "strict", "-FailMode") }
    $missingTokens = @($requiredTokens | Where-Object { $workflowText -notlike "*$_*" })
    if ($missingTokens.Count -eq 0) {
        Add-Row $rows "workflow_policy_wiring" $passStatus "info" "CI workflow exposes report-only, safe and strict policy selection while uploading artifacts." ([ordered]@{ path = ConvertTo-RelativeRepoPath $workflowPath; requiredTokens = @($requiredTokens) })
    } else {
        Add-Row $rows "workflow_policy_wiring" $errorStatus "error" "CI workflow is missing required policy tokens." ([ordered]@{ path = ConvertTo-RelativeRepoPath $workflowPath; missing = @($missingTokens) })
    }
} else {
    Add-Row $rows "workflow_policy_wiring" $errorStatus "error" "CI verifier workflow is missing." ([ordered]@{ path = ConvertTo-RelativeRepoPath $workflowPath })
}

$mechanicalErrors = 0
$mechanicalReports = if ($rules -and $rules.mechanicalReports) { @($rules.mechanicalReports | ForEach-Object { [string]$_ }) } else { @() }
foreach ($reportName in $mechanicalReports) {
    $reportPath = Join-Path $ReportRoot $reportName
    $report = Read-JsonOrNull $reportPath
    if ($null -eq $report) {
        $mechanicalErrors++
        Add-Row $rows "mechanical_report" $errorStatus "error" "Required mechanical policy report is missing or invalid JSON." ([ordered]@{ report = $reportName })
        continue
    }
    $errors = Get-SummaryInt $report "errors"
    $fail = Get-SummaryInt $report "fail"
    if ($errors -gt 0 -or $fail -gt 0) {
        $mechanicalErrors++
        Add-Row $rows "mechanical_report" $errorStatus "error" "Mechanical policy report contains errors or failed steps." ([ordered]@{ report = $reportName; errors = $errors; fail = $fail })
    } else {
        Add-Row $rows "mechanical_report" $passStatus "info" "Mechanical policy report has no errors/failures." ([ordered]@{ report = $reportName; errors = $errors; fail = $fail })
    }
}

$strictBlockerTotal = 0
$strictReports = if ($rules -and $rules.strictBlockerReports) { @($rules.strictBlockerReports) } else { @() }
foreach ($rule in $strictReports) {
    $reportName = [string]$rule.report
    $field = [string]$rule.summaryField
    $reportPath = Join-Path $ReportRoot $reportName
    $report = Read-JsonOrNull $reportPath
    if ($null -eq $report) {
        Add-Row $rows "strict_blocker" $reviewStatus "review" "Strict blocker report is missing; strict promotion cannot be certified." ([ordered]@{ report = $reportName; field = $field; reason = [string]$rule.reason })
        $strictBlockerTotal++
        continue
    }
    $count = Get-SummaryInt $report $field
    $strictBlockerTotal += $count
    if ($count -gt 0) {
        $status = if ($FailMode -eq "strict") { $errorStatus } else { $reviewStatus }
        $severity = if ($FailMode -eq "strict") { "error" } else { "review" }
        Add-Row $rows "strict_blocker" $status $severity "Strict mode is blocked by unresolved report rows." ([ordered]@{ report = $reportName; field = $field; count = $count; reason = [string]$rule.reason; activeMode = $policyMode })
    } else {
        Add-Row $rows "strict_blocker" $passStatus "info" "Strict blocker report has no unresolved rows for this criterion." ([ordered]@{ report = $reportName; field = $field; count = $count; reason = [string]$rule.reason })
    }
}

if ($FailMode -eq "off") {
    Add-Row $rows "mode_boundary" $passStatus "info" "Report-only mode keeps unresolved certification blockers as review rows, not hard failures." ([ordered]@{ activeMode = $policyMode })
} elseif ($FailMode -eq "safe") {
    Add-Row $rows "mode_boundary" $passStatus "info" "Safe mode hard-fails mechanical errors only; strict runtime/visual blockers remain review-only." ([ordered]@{ activeMode = $policyMode; mechanicalErrors = $mechanicalErrors; strictBlockers = $strictBlockerTotal })
} elseif ($FailMode -eq "strict") {
    if ($strictBlockerTotal -gt 0) {
        Add-Row $rows "mode_boundary" $errorStatus "error" "Strict mode is active and unresolved certification blockers remain." ([ordered]@{ activeMode = $policyMode; strictBlockers = $strictBlockerTotal })
    } else {
        Add-Row $rows "mode_boundary" $passStatus "info" "Strict mode has no unresolved runtime/visual blockers." ([ordered]@{ activeMode = $policyMode })
    }
}

$orderedRows = @($rows)
$summary = [ordered]@{
    rows = $orderedRows.Count
    pass = @($orderedRows | Where-Object status -eq $passStatus).Count
    reviewNeeded = @($orderedRows | Where-Object status -eq $reviewStatus).Count
    errors = @($orderedRows | Where-Object status -eq $errorStatus).Count
    policyMode = $policyMode
    failMode = $FailMode
    mechanicalErrors = $mechanicalErrors
    strictBlockers = $strictBlockerTotal
}
$output = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    policy = "Report-only/safe/strict CI policy audit. Report-only is default; safe is mechanical-hard-fail only; strict is manual opt-in and fails unresolved runtime or visual certification blockers."
    inputs = [ordered]@{ repoRoot = $RepoRoot; reportRoot = ConvertTo-RelativeRepoPath $ReportRoot; rulesRoot = ConvertTo-RelativeRepoPath $RulesRoot; failMode = $FailMode; policyMode = $policyMode }
    summary = $summary
    results = @($orderedRows)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$output | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# CI strict/safe policy report")
$lines.Add("")
$lines.Add("Generated: $($output.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($output.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Rows | PASS | REVIEW | ERRORS | Policy mode | Strict blockers |")
$lines.Add("|---:|---:|---:|---:|---|---:|")
$lines.Add("| $($summary.rows) | $($summary.pass) | $($summary.reviewNeeded) | $($summary.errors) | $($summary.policyMode) | $($summary.strictBlockers) |")
$lines.Add("")
$lines.Add("## Non-pass rows")
$lines.Add("")
$lines.Add("| Status | Area | Severity | Evidence |")
$lines.Add("|---|---|---|---|")
$nonPass = @($orderedRows | Where-Object { $_.status -ne $passStatus })
foreach ($row in $nonPass) { $lines.Add("| $($row.status) | $($row.area) | $($row.severity) | $(Escape-MarkdownCell $row.evidence) |") }
if ($nonPass.Count -eq 0) { $lines.Add("| CI_POLICY_PASS | <all> | info | Every CI policy row passed. |") }
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "CI strict/safe policy report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), errors=$($summary.errors), policyMode=$($summary.policyMode), strictBlockers=$($summary.strictBlockers)"

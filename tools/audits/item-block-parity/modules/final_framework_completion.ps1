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
if (-not $OutputJson) { $OutputJson = Join-Path $ReportRoot "final_framework_completion_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $ReportRoot "final_framework_completion_report.md" }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\\", "/")
}
function Read-JsonOrNull([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { return $null }
    try { return Get-Content -Raw -LiteralPath $Path | ConvertFrom-Json } catch { return $null }
}
function Read-TextOrEmpty([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { return "" }
    return Get-Content -Raw -LiteralPath $Path
}
function Get-SummaryInt($Report, [string]$Name) {
    if ($null -eq $Report -or $null -eq $Report.summary) { return 0 }
    $value = $Report.summary.$Name
    if ($null -eq $value) { return 0 }
    try { return [int]$value } catch { return 0 }
}
function Add-Row($Rows, [string]$Area, [string]$Status, [string]$Severity, [string]$Evidence, $Details) {
    $Rows.Add([pscustomobject][ordered]@{
        check = "final_framework_completion"
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

$passStatus = "FINAL_FRAMEWORK_PASS"
$reviewStatus = "FINAL_FRAMEWORK_REVIEW_NEEDED"
$errorStatus = "FINAL_FRAMEWORK_ERROR"
$rows = [System.Collections.Generic.List[object]]::new()

$rulesPath = Join-Path $RulesRoot "final-framework-completion.json"
$rules = Read-JsonOrNull $rulesPath
if ($null -eq $rules) {
    Add-Row $rows "rules" $errorStatus "error" "Final framework completion rules are missing or invalid." ([ordered]@{ path = ConvertTo-RelativeRepoPath $rulesPath })
} else {
    Add-Row $rows "rules" $passStatus "info" "Final framework completion rules parse successfully." ([ordered]@{ path = ConvertTo-RelativeRepoPath $rulesPath })
}

$registryPath = Join-Path $RulesRoot "check-registry.json"
$ownerRulesPath = Join-Path $RulesRoot "check-invocation-rules.json"
$registry = Read-JsonOrNull $registryPath
$ownerRules = Read-JsonOrNull $ownerRulesPath
if ($null -eq $registry) {
    Add-Row $rows "registry" $errorStatus "error" "Check registry is missing or invalid JSON." ([ordered]@{ path = ConvertTo-RelativeRepoPath $registryPath })
} else {
    $implemented = @($registry.checks | Where-Object { $_.status -eq "implemented" })
    $nonImplemented = @($registry.checks | Where-Object { $_.status -ne "implemented" })
    $minimum = if ($rules -and $rules.expectedChecks -and $rules.expectedChecks.minimumImplementedAfterBatch55) { [int]$rules.expectedChecks.minimumImplementedAfterBatch55 } else { 53 }
    if ($implemented.Count -ge $minimum -and $nonImplemented.Count -eq 0) {
        Add-Row $rows "registry" $passStatus "info" "All registered checks are implemented and meet the Batch 55 minimum." ([ordered]@{ implemented = $implemented.Count; nonImplemented = $nonImplemented.Count; minimum = $minimum })
    } else {
        Add-Row $rows "registry" $errorStatus "error" "Registry still has non-implemented checks or too few implemented checks." ([ordered]@{ implemented = $implemented.Count; nonImplemented = @($nonImplemented | Select-Object name, status, layer); minimum = $minimum })
    }
}

if ($null -eq $registry -or $null -eq $ownerRules) {
    Add-Row $rows "owner_coverage" $errorStatus "error" "Registry or owner rules are unavailable for owner coverage check." ([ordered]@{ registry = ConvertTo-RelativeRepoPath $registryPath; owners = ConvertTo-RelativeRepoPath $ownerRulesPath })
} else {
    $ownerNames = @($ownerRules.owners | ForEach-Object { [string]$_.name } | Sort-Object -Unique)
    $implementedNames = @($registry.checks | Where-Object { $_.status -eq "implemented" } | ForEach-Object { [string]$_.name } | Sort-Object -Unique)
    $missingOwners = @($implementedNames | Where-Object { $_ -notin $ownerNames })
    if ($missingOwners.Count -eq 0) {
        Add-Row $rows "owner_coverage" $passStatus "info" "Every implemented check has an invocation owner." ([ordered]@{ implemented = $implementedNames.Count; owners = $ownerNames.Count })
    } else {
        Add-Row $rows "owner_coverage" $errorStatus "error" "Some implemented checks do not have invocation owners." ([ordered]@{ missing = @($missingOwners) })
    }
}

$matrixPath = Join-Path $RepoRoot "06_docs/audits/item_block_parity_layer_completion_matrix.md"
$alignmentPath = Join-Path $RepoRoot "06_docs/audits/item_block_parity_framework_alignment.md"
$matrixText = Read-TextOrEmpty $matrixPath
$alignmentText = Read-TextOrEmpty $alignmentPath
$matrixTokens = @("Completion backlog derived from the current registry", "| <none> | <none> | All currently registered checks are implemented.", "report-only, safe and strict", "Final framework completion gate")
$missingMatrixTokens = @($matrixTokens | Where-Object { $matrixText -notlike "*$_*" })
$alignmentTokens = @("| 55 | Closed: final production-grade framework completion audit", "Batch 55 closure:")
$missingAlignmentTokens = @($alignmentTokens | Where-Object { $alignmentText -notlike "*$_*" })
if ($missingMatrixTokens.Count -eq 0 -and $missingAlignmentTokens.Count -eq 0) {
    Add-Row $rows "docs_alignment" $passStatus "info" "Framework matrix and alignment docs contain final completion policy and Batch 55 closure." ([ordered]@{ matrix = ConvertTo-RelativeRepoPath $matrixPath; alignment = ConvertTo-RelativeRepoPath $alignmentPath })
} else {
    Add-Row $rows "docs_alignment" $errorStatus "error" "Framework completion docs are missing required Batch 55 tokens." ([ordered]@{ missingMatrix = @($missingMatrixTokens); missingAlignment = @($missingAlignmentTokens) })
}

$workflowPath = Join-Path $RepoRoot ".github/workflows/item-block-framework-verifier.yml"
$workflowText = Read-TextOrEmpty $workflowPath
$workflowTokens = @("policy_mode", "report_only", "safe", "strict", "actions/upload-artifact@v4", "tools/reports/local/item-block-parity/*.json", "tools/reports/local/item-block-parity/*.md")
$missingWorkflowTokens = @($workflowTokens | Where-Object { $workflowText -notlike "*$_*" })
if ($missingWorkflowTokens.Count -eq 0) {
    Add-Row $rows "ci_workflow" $passStatus "info" "CI workflow exposes report-only/safe/strict policy modes and uploads report artifacts." ([ordered]@{ path = ConvertTo-RelativeRepoPath $workflowPath })
} else {
    Add-Row $rows "ci_workflow" $errorStatus "error" "CI workflow is missing final framework policy tokens." ([ordered]@{ missing = @($missingWorkflowTokens); path = ConvertTo-RelativeRepoPath $workflowPath })
}

$requiredPolicyFiles = if ($rules -and $rules.requiredPolicyFiles) { @($rules.requiredPolicyFiles | ForEach-Object { [string]$_ }) } else { @() }
$policyMissing = [System.Collections.Generic.List[string]]::new()
foreach ($fileName in $requiredPolicyFiles) {
    $path = Join-Path $RulesRoot $fileName
    if ($null -eq (Read-JsonOrNull $path)) { $policyMissing.Add($fileName) }
}
if ($policyMissing.Count -eq 0) {
    Add-Row $rows "policy_files" $passStatus "info" "All final framework policy rule files parse." ([ordered]@{ files = @($requiredPolicyFiles) })
} else {
    Add-Row $rows "policy_files" $errorStatus "error" "Required policy rule files are missing or invalid." ([ordered]@{ missing = @($policyMissing) })
}

$requiredDocs = if ($rules -and $rules.requiredDocs) { @($rules.requiredDocs | ForEach-Object { [string]$_ }) } else { @() }
$missingDocs = [System.Collections.Generic.List[string]]::new()
foreach ($docName in $requiredDocs) {
    $docPath = Join-Path $RepoRoot "06_docs/audits/$docName"
    if (-not (Test-Path -LiteralPath $docPath -PathType Leaf)) { $missingDocs.Add($docName) }
}
if ($missingDocs.Count -eq 0) {
    Add-Row $rows "policy_docs" $passStatus "info" "All required framework policy documentation files exist." ([ordered]@{ docs = @($requiredDocs) })
} else {
    Add-Row $rows "policy_docs" $errorStatus "error" "Required framework policy documentation files are missing." ([ordered]@{ missing = @($missingDocs) })
}

$requiredReports = if ($rules -and $rules.requiredReports) { @($rules.requiredReports | ForEach-Object { [string]$_ }) } else { @() }
$missingReports = [System.Collections.Generic.List[string]]::new()
$reportErrors = [System.Collections.Generic.List[object]]::new()
$reportReviews = [System.Collections.Generic.List[object]]::new()
foreach ($reportName in $requiredReports) {
    $reportPath = Join-Path $ReportRoot $reportName
    $report = Read-JsonOrNull $reportPath
    if ($null -eq $report) {
        $missingReports.Add($reportName)
        continue
    }
    $errors = Get-SummaryInt $report "errors"
    $fail = Get-SummaryInt $report "fail"
    $reviewNeeded = Get-SummaryInt $report "reviewNeeded"
    if ($errors -gt 0 -or $fail -gt 0) {
        $reportErrors.Add([pscustomobject][ordered]@{ report = $reportName; errors = $errors; fail = $fail })
    } elseif ($reviewNeeded -gt 0) {
        $reportReviews.Add([pscustomobject][ordered]@{ report = $reportName; reviewNeeded = $reviewNeeded })
    }
}
if ($missingReports.Count -gt 0 -or $reportErrors.Count -gt 0) {
    Add-Row $rows "report_inventory" $errorStatus "error" "Required framework reports are missing or contain errors/failures." ([ordered]@{ missing = @($missingReports); errors = @($reportErrors) })
} elseif ($reportReviews.Count -gt 0) {
    Add-Row $rows "report_inventory" $reviewStatus "review" "Required reports are present and error-free; some retain review-needed rows by policy." ([ordered]@{ reviewReports = @($reportReviews); reports = @($requiredReports) })
} else {
    Add-Row $rows "report_inventory" $passStatus "info" "Required framework reports are present and contain no errors/failures." ([ordered]@{ reports = @($requiredReports) })
}

$strictReports = if ($rules -and $rules.strictBlockerReports) { @($rules.strictBlockerReports | ForEach-Object { [string]$_ }) } else { @() }
$strictBlockers = [System.Collections.Generic.List[object]]::new()
foreach ($reportName in $strictReports) {
    $report = Read-JsonOrNull (Join-Path $ReportRoot $reportName)
    if ($null -eq $report) { continue }
    $reviewNeeded = Get-SummaryInt $report "reviewNeeded"
    $strictBlockerCount = Get-SummaryInt $report "strictBlockers"
    $missing = Get-SummaryInt $report "missing"
    $blockers = $reviewNeeded + $strictBlockerCount + $missing
    if ($blockers -gt 0) {
        $strictBlockers.Add([pscustomobject][ordered]@{ report = $reportName; reviewNeeded = $reviewNeeded; strictBlockers = $strictBlockerCount; missing = $missing })
    }
}
if ($strictBlockers.Count -gt 0) {
    Add-Row $rows "strict_certification" $reviewStatus "review" "Strict certification remains manually blocked by unresolved review/runtime/visual rows; report-only framework is still complete." ([ordered]@{ blockers = @($strictBlockers); failMode = $FailMode })
} else {
    Add-Row $rows "strict_certification" $passStatus "info" "No strict blocker rows were detected in the configured blocker reports." ([ordered]@{ reports = @($strictReports); failMode = $FailMode })
}

$verifierPath = Join-Path $RepoRoot "tools/audits/item-block-parity/verify-item-block-parity-framework.ps1"
$verifierText = Read-TextOrEmpty $verifierPath
$verifierTokens = @("final_framework_completion", "final-framework-completion.json", "final_framework_completion_report.json", "FRAMEWORK_VERIFIER")
$missingVerifierTokens = @($verifierTokens | Where-Object { $verifierText -notlike "*$_*" })
if ($missingVerifierTokens.Count -eq 0) {
    Add-Row $rows "verifier_wiring" $passStatus "info" "Verifier v2 validates final framework completion wiring and report summary." ([ordered]@{ path = ConvertTo-RelativeRepoPath $verifierPath })
} else {
    Add-Row $rows "verifier_wiring" $errorStatus "error" "Verifier v2 is missing final framework completion wiring tokens." ([ordered]@{ missing = @($missingVerifierTokens) })
}

Add-Row $rows "scope_statement" $passStatus "info" "Batch 55 certifies the framework, not full gameplay parity." ([ordered]@{ statement = if ($rules) { [string]$rules.completionStatement } else { "Framework completion only; no gameplay parity claim." } })

$orderedRows = @($rows)
$summary = [ordered]@{
    rows = $orderedRows.Count
    pass = @($orderedRows | Where-Object status -eq $passStatus).Count
    reviewNeeded = @($orderedRows | Where-Object status -eq $reviewStatus).Count
    errors = @($orderedRows | Where-Object status -eq $errorStatus).Count
    strictBlockers = @($strictBlockers).Count
}
$output = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    selectedChecks = @("final_framework_completion")
    policy = "Report-only final production-grade framework completion audit. It certifies framework closure and policy consistency, not complete Thaumcraft gameplay parity."
    inputs = [ordered]@{
        repoRoot = $RepoRoot
        failMode = $FailMode
        reportRoot = $ReportRoot
        rulesRoot = $RulesRoot
    }
    summary = $summary
    results = @($orderedRows)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$output | ConvertTo-Json -Depth 18 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block final framework completion report")
$lines.Add("")
$lines.Add("Generated: $($output.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($output.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Rows | PASS | REVIEW | ERRORS | Strict blocker groups |")
$lines.Add("|---:|---:|---:|---:|---:|")
$lines.Add("| $($summary.rows) | $($summary.pass) | $($summary.reviewNeeded) | $($summary.errors) | $($summary.strictBlockers) |")
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
    $lines.Add("| FINAL_FRAMEWORK_PASS | <all> | info | Every final framework row passed. |")
}
$lines.Add("")
$lines.Add("## Scope")
$lines.Add("")
$lines.Add("This audit closes the framework contract only. It does not claim every gameplay, visual or data parity row has been resolved.")
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Final framework completion report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), errors=$($summary.errors), strictBlockers=$($summary.strictBlockers)"

if ($summary.errors -gt 0) { exit 1 }
exit 0

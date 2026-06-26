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
$RepoRoot = (Resolve-Path -LiteralPath $RepoRoot).Path
if (-not $ReportRoot) { $ReportRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity" }
if (-not $RulesRoot) { $RulesRoot = Join-Path $RepoRoot "tools/audits/item-block-parity/rules" }
if (-not $OutputJson) { $OutputJson = Join-Path $ReportRoot "item_block_visual_equivalence_completion_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $ReportRoot "item_block_visual_equivalence_completion_report.md" }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}
function Read-JsonFileOrNull([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { return $null }
    try { return Get-Content -Raw -LiteralPath $Path | ConvertFrom-Json } catch { return $null }
}
function Get-SummaryValue($Report, [string]$Name) {
    if ($null -eq $Report -or $null -eq $Report.summary) { return $null }
    return $Report.summary.$Name
}
function Get-SummaryInt($Report, [string]$Name) {
    $value = Get-SummaryValue $Report $Name
    if ($null -eq $value) { return 0 }
    try { return [int]$value } catch { return 0 }
}
function Add-Row($Rows, [string]$Area, [string]$Status, [string]$Severity, [string]$Evidence, $Details) {
    $Rows.Add([pscustomobject][ordered]@{
        check = "visual_equivalence_completion"
        area = $Area
        status = $Status
        severity = $Severity
        evidence = $Evidence
        details = $Details
    })
}
function Get-StatusCounts($Report) {
    $counts = @{}
    if ($null -eq $Report) { return $counts }
    foreach ($row in @($Report.results)) {
        $status = if ($row.status) { [string]$row.status } else { "<missing>" }
        if (-not $counts.ContainsKey($status)) { $counts[$status] = 0 }
        $counts[$status]++
    }
    return $counts
}
function Count-StatusLike($Report, [string[]]$Needles) {
    $total = 0
    if ($null -eq $Report) { return 0 }
    foreach ($row in @($Report.results)) {
        $status = if ($row.status) { [string]$row.status } else { "" }
        foreach ($needle in $Needles) {
            if ($status -like "*$needle*") { $total++; break }
        }
    }
    return $total
}

$passStatus = "VISUAL_COMPLETION_PASS"
$reviewStatus = "VISUAL_COMPLETION_REVIEW_NEEDED"
$errorStatus = "VISUAL_COMPLETION_ERROR"
$rows = [System.Collections.Generic.List[object]]::new()

$completionRulesPath = Join-Path $RulesRoot "visual-equivalence-completion.json"
$completionRules = Read-JsonFileOrNull $completionRulesPath
if ($null -eq $completionRules) {
    Add-Row $rows "completion_rules" $errorStatus "error" "Visual equivalence completion rules are missing or invalid JSON." ([ordered]@{ path = ConvertTo-RelativeRepoPath $completionRulesPath })
} else {
    Add-Row $rows "completion_rules" $passStatus "info" "Visual equivalence completion policy rules parse successfully." ([ordered]@{ path = ConvertTo-RelativeRepoPath $completionRulesPath; criteria = @($completionRules.criteria).Count })
}

$requiredRuleFiles = if ($completionRules -and $completionRules.requiredRuleFiles) {
    @($completionRules.requiredRuleFiles | ForEach-Object { [string]$_ })
} else {
    @("item-transform-equivalence.json", "model-parent-equivalence.json", "texture-equivalence.json", "intentional-recolor.json")
}
foreach ($ruleFile in $requiredRuleFiles) {
    $rulePath = Join-Path $RulesRoot $ruleFile
    $ruleDoc = Read-JsonFileOrNull $rulePath
    if ($null -eq $ruleDoc) {
        Add-Row $rows "rule_file" $errorStatus "error" "Required visual equivalence rule file is missing or invalid JSON." ([ordered]@{ file = $ruleFile; path = ConvertTo-RelativeRepoPath $rulePath })
    } else {
        Add-Row $rows "rule_file" $passStatus "info" "Required visual equivalence rule file parses." ([ordered]@{ file = $ruleFile; path = ConvertTo-RelativeRepoPath $rulePath; entries = @($ruleDoc.entries).Count })
    }
}

$visualReportPath = Join-Path $ReportRoot "item_block_visual_model_transform_report.json"
$textureReportPath = Join-Path $ReportRoot "item_block_texture_color_report.json"
$soundReportPath = Join-Path $ReportRoot "item_block_sound_particle_fx_report.json"
$visualReport = Read-JsonFileOrNull $visualReportPath
$textureReport = Read-JsonFileOrNull $textureReportPath
$soundReport = Read-JsonFileOrNull $soundReportPath

foreach ($reportInfo in @(
    [pscustomobject]@{ area = "visual_model_report"; path = $visualReportPath; report = $visualReport },
    [pscustomobject]@{ area = "texture_color_report"; path = $textureReportPath; report = $textureReport },
    [pscustomobject]@{ area = "sound_particle_fx_report"; path = $soundReportPath; report = $soundReport }
)) {
    if ($null -eq $reportInfo.report) {
        Add-Row $rows $reportInfo.area $errorStatus "error" "Required visual boundary report is missing or invalid JSON." ([ordered]@{ path = ConvertTo-RelativeRepoPath $reportInfo.path })
    } else {
        $errors = Get-SummaryInt $reportInfo.report "errors"
        $fail = Get-SummaryInt $reportInfo.report "fail"
        if ($errors -gt 0 -or $fail -gt 0) {
            Add-Row $rows $reportInfo.area $errorStatus "error" "Required report has errors or failures in summary." ([ordered]@{ path = ConvertTo-RelativeRepoPath $reportInfo.path; errors = $errors; fail = $fail })
        } else {
            Add-Row $rows $reportInfo.area $passStatus "info" "Required visual boundary report exists and has no error/fail counters." ([ordered]@{ path = ConvertTo-RelativeRepoPath $reportInfo.path })
        }
    }
}

if ($visualReport) {
    $reviewNeeded = Get-SummaryInt $visualReport "reviewNeeded"
    $missing = Get-SummaryInt $visualReport "missing"
    $ruleAccepted = Get-SummaryInt $visualReport "ruleAccepted"
    $status = if ($reviewNeeded -eq 0 -and $missing -eq 0) { $passStatus } else { $reviewStatus }
    $severity = if ($status -eq $passStatus) { "info" } else { "review" }
    Add-Row $rows "model_transform_completion" $status $severity "Model/transform visual report classified; strict mode blocked until review/missing rows are resolved or rule-accepted." ([ordered]@{ reviewNeeded = $reviewNeeded; missing = $missing; ruleAccepted = $ruleAccepted; report = ConvertTo-RelativeRepoPath $visualReportPath })
}

if ($textureReport) {
    $reviewNeeded = Get-SummaryInt $textureReport "reviewNeeded"
    $statusCounts = Get-StatusCounts $textureReport
    $ruleAccepted = 0
    foreach ($statusName in @("TEXTURE_EQUIVALENT_BY_RULE", "INTENTIONAL_VISUAL_DIFFERENCE", "TEXTURE_EXACT_MATCH", "TEXTURE_SIMILAR_MATCH")) {
        if ($statusCounts.ContainsKey($statusName)) { $ruleAccepted += [int]$statusCounts[$statusName] }
    }
    $status = if ($reviewNeeded -eq 0) { $passStatus } else { $reviewStatus }
    $severity = if ($status -eq $passStatus) { "info" } else { "review" }
    Add-Row $rows "texture_color_completion" $status $severity "Texture/color report classified; strict mode blocked until reviewNeeded is zero or each row has reviewed equivalence/intentional-difference policy." ([ordered]@{ reviewNeeded = $reviewNeeded; acceptedOrMatching = $ruleAccepted; report = ConvertTo-RelativeRepoPath $textureReportPath })
}

if ($soundReport) {
    $summaryReview = Get-SummaryInt $soundReport "reviewNeeded"
    $patternReview = Count-StatusLike $soundReport @("REVIEW", "MISSING", "NOT_EVIDENCED")
    $reviewCount = [Math]::Max($summaryReview, $patternReview)
    $status = if ($reviewCount -eq 0) { $passStatus } else { $reviewStatus }
    $severity = if ($status -eq $passStatus) { "info" } else { "review" }
    Add-Row $rows "sound_particle_fx_completion" $status $severity "Sound/particle/FX report classified; strict mode blocked until review-like FX rows are resolved, intentional, superseded, blocked or out of scope." ([ordered]@{ reviewNeeded = $summaryReview; reviewLikeStatuses = $patternReview; report = ConvertTo-RelativeRepoPath $soundReportPath })
}

$orderedRows = @($rows)
$summary = [ordered]@{
    rows = $orderedRows.Count
    pass = @($orderedRows | Where-Object status -eq $passStatus).Count
    reviewNeeded = @($orderedRows | Where-Object status -eq $reviewStatus).Count
    errors = @($orderedRows | Where-Object status -eq $errorStatus).Count
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    selectedChecks = @("visual_equivalence_completion")
    policy = "Report-only visual equivalence completion criteria. Review rows block strict visual certification but are not mechanical errors."
    summary = $summary
    results = @($orderedRows)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Visual equivalence completion report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Rows | PASS | REVIEW | ERRORS |")
$lines.Add("|---:|---:|---:|---:|")
$lines.Add("| $($summary.rows) | $($summary.pass) | $($summary.reviewNeeded) | $($summary.errors) |")
$lines.Add("")
$lines.Add("## Non-pass rows")
$lines.Add("")
$lines.Add("| Status | Area | Severity | Evidence |")
$lines.Add("|---|---|---|---|")
$nonPass = @($orderedRows | Where-Object { $_.status -ne $passStatus })
foreach ($row in $nonPass) {
    $lines.Add("| $($row.status) | $($row.area) | $($row.severity) | $($row.evidence.Replace('|', '\|')) |")
}
if ($nonPass.Count -eq 0) {
    $lines.Add("| VISUAL_COMPLETION_PASS | <all> | info | Every visual equivalence completion row passed. |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Visual equivalence completion report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), errors=$($summary.errors)"

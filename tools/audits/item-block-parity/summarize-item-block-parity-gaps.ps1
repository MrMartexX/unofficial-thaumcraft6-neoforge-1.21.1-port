[CmdletBinding()]
param(
    [string]$RepoRoot,

    [string]$ReportPath,

    [string]$OutputJson,

    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"

if (-not $RepoRoot) {
    $RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "../../..")).Path
}
$RepoRoot = (Resolve-Path $RepoRoot).Path

$reportRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity"
if (-not $ReportPath) {
    $ReportPath = Join-Path $reportRoot "item_block_parity_report.json"
}
if (-not $OutputJson) {
    $OutputJson = Join-Path $reportRoot "item_block_parity_gap_candidates.json"
}
if (-not $OutputMarkdown) {
    $OutputMarkdown = Join-Path $reportRoot "item_block_parity_gap_candidates.md"
}

if (-not (Test-Path -LiteralPath $ReportPath -PathType Leaf)) {
    throw "Parity report not found: $ReportPath"
}

$report = Get-Content -Raw -LiteralPath $ReportPath | ConvertFrom-Json
if ($report.schemaVersion -ne 1 -or -not $report.results) {
    throw "Unsupported or incomplete parity report: $ReportPath"
}

function Get-CandidateCategory($Result) {
    switch ($Result.check) {
        "registry" { return "registry_gap_candidate" }
        "duplicate_registry_id" { return "duplicate_registry_id" }
        "block_item_pairs" { return "no_item_block_candidate" }
        "blockstates" { return "blockstate_resource_gap" }
        "models" { return "model_resource_gap" }
        "lang" { return "lang_resource_gap" }
        "loot" { return "loot_rule_candidate" }
        default { return "unclassified_missing" }
    }
}

function Get-RecommendedAction($Result) {
    switch ($Result.check) {
        "registry" {
            return "Review as real missing content, known rename, metadata variant split, deferred subsystem entry, or inferred-source limitation."
        }
        "duplicate_registry_id" {
            return "Fix duplicate DeferredRegister ID before treating resource parity results as trustworthy."
        }
        "block_item_pairs" {
            return "Decide whether the block needs a player inventory BlockItem or belongs in no-item-block-expected.json."
        }
        "blockstates" {
            return "Add a modern blockstate resource or mark the entry as deferred only with a documented boundary."
        }
        "models" {
            return "Add/repair the model resource path, or defer only if the block/item is intentionally non-rendered at this layer."
        }
        "lang" {
            return "Add the missing translation key unless the entry is deferred or intentionally hidden."
        }
        "loot" {
            return "Add a loot table or classify the block under no-loot-expected.json."
        }
        default {
            return "Review manually."
        }
    }
}

function Get-Confidence($Result) {
    switch ($Result.check) {
        "registry" { return "medium" }
        "duplicate_registry_id" { return "high" }
        "block_item_pairs" { return "medium" }
        "blockstates" { return "high" }
        "models" { return "high" }
        "lang" { return "high" }
        "loot" { return "medium" }
        default { return "low" }
    }
}

$candidates = [System.Collections.Generic.List[object]]::new()
foreach ($result in @($report.results | Where-Object status -eq "MISSING")) {
    $candidates.Add([pscustomobject][ordered]@{
        kind = $result.kind
        id = $result.id
        legacyId = $result.legacyId
        check = $result.check
        category = Get-CandidateCategory $result
        confidence = Get-Confidence $result
        evidence = $result.evidence
        recommendedAction = Get-RecommendedAction $result
    })
}

$orderedCandidates = @($candidates | Sort-Object category, kind, id, check)
$summaryByCategory = @(
    $orderedCandidates |
        Group-Object category |
        Sort-Object Name |
        ForEach-Object {
            [pscustomobject][ordered]@{
                category = $_.Name
                count = $_.Count
            }
        }
)

$summaryByCheck = @(
    $orderedCandidates |
        Group-Object check |
        Sort-Object Name |
        ForEach-Object {
            [pscustomobject][ordered]@{
                check = $_.Name
                count = $_.Count
            }
        }
)

$output = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    sourceReport = $ReportPath
    sourceReportSummary = $report.summary
    candidateCount = $orderedCandidates.Count
    summaryByCategory = $summaryByCategory
    summaryByCheck = $summaryByCheck
    candidates = $orderedCandidates
}

foreach ($path in @($OutputJson, $OutputMarkdown)) {
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $path) | Out-Null
}

$output | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block parity gap candidates")
$lines.Add("")
$lines.Add("Generated: $($output.generatedAtUtc)")
$lines.Add("")
$lines.Add("Source report: ``$ReportPath``")
$lines.Add("")
$lines.Add("This report groups current ``MISSING`` rows into review candidates. It does not edit rule files and does not claim that a candidate is already classified.")
$lines.Add("")
$lines.Add("## Summary by category")
$lines.Add("")
$lines.Add("| Category | Count |")
$lines.Add("|---|---:|")
foreach ($group in $summaryByCategory) {
    $lines.Add("| $($group.category) | $($group.count) |")
}
$lines.Add("")
$lines.Add("## Summary by check")
$lines.Add("")
$lines.Add("| Check | Count |")
$lines.Add("|---|---:|")
foreach ($group in $summaryByCheck) {
    $lines.Add("| $($group.check) | $($group.count) |")
}
$lines.Add("")
$lines.Add("## Candidates")
$lines.Add("")
$lines.Add("| Category | Kind | ID | Check | Confidence | Recommended action | Evidence |")
$lines.Add("|---|---|---|---|---|---|---|")
foreach ($candidate in $orderedCandidates) {
    $evidence = if ($candidate.evidence) { $candidate.evidence.Replace("|", "\|") } else { "" }
    $action = $candidate.recommendedAction.Replace("|", "\|")
    $lines.Add("| $($candidate.category) | $($candidate.kind) | ``$($candidate.id)`` | $($candidate.check) | $($candidate.confidence) | $action | $evidence |")
}

$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Gap candidate report: $OutputMarkdown"
Write-Output "Candidates=$($orderedCandidates.Count)"
foreach ($group in $summaryByCategory) {
    Write-Output "$($group.category)=$($group.count)"
}
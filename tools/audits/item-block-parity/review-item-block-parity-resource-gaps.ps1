[CmdletBinding()]
param(
    [string]$RepoRoot,

    [string]$CandidateReportPath,

    [string]$PortManifestPath,

    [string]$OutputJson,

    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"

if (-not $RepoRoot) {
    $RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "../../..")).Path
}
$RepoRoot = (Resolve-Path $RepoRoot).Path

$reportRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity"
if (-not $CandidateReportPath) {
    $CandidateReportPath = Join-Path $reportRoot "item_block_parity_gap_candidates.json"
}
if (-not $PortManifestPath) {
    $PortManifestPath = Join-Path $reportRoot "port_manifest.json"
}
if (-not $OutputJson) {
    $OutputJson = Join-Path $reportRoot "item_block_parity_resource_gap_review.json"
}
if (-not $OutputMarkdown) {
    $OutputMarkdown = Join-Path $reportRoot "item_block_parity_resource_gap_review.md"
}

foreach ($path in @($CandidateReportPath, $PortManifestPath)) {
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
        throw "Required input not found: $path"
    }
}

$candidateReport = Get-Content -Raw -LiteralPath $CandidateReportPath | ConvertFrom-Json
$portManifest = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json

function Get-PlainId([string]$Id) {
    if ([string]::IsNullOrWhiteSpace($Id)) { return "" }
    if ($Id.Contains(":")) { return $Id.Split(":", 2)[1] }
    return $Id
}

function Get-LangKey([string]$Kind, [string]$Id) {
    $plain = Get-PlainId $Id
    if ($Kind -eq "block") { return "block.thaumcraft.$plain" }
    return "item.thaumcraft.$plain"
}

function Get-ModelPath([string]$Kind, [string]$Id) {
    $plain = Get-PlainId $Id
    if ($Kind -eq "block") { return "assets/thaumcraft/models/block/$plain.json" }
    return "assets/thaumcraft/models/item/$plain.json"
}

function Get-ResourceReviewCategory($Candidate) {
    switch ($Candidate.category) {
        "lang_resource_gap" {
            return "lang_generation_candidate"
        }
        "model_resource_gap" {
            if ($Candidate.kind -eq "item") { return "item_model_generation_candidate" }
            if ($Candidate.kind -eq "block") { return "block_model_review_candidate" }
            return "model_review_candidate"
        }
        "blockstate_resource_gap" {
            return "blockstate_generation_candidate"
        }
        "loot_rule_candidate" {
            return "loot_rule_review_candidate"
        }
        default {
            return "resource_review_candidate"
        }
    }
}

function Get-ResourceAction($Candidate, [string]$ReviewCategory) {
    switch ($ReviewCategory) {
        "lang_generation_candidate" { return "Add lang key unless the entry is intentionally hidden/deferred." }
        "item_model_generation_candidate" { return "Add item model after confirming expected texture or generated-model convention." }
        "block_model_review_candidate" { return "Review blockstate/model pair before generating block model." }
        "blockstate_generation_candidate" { return "Add blockstate only after checking blockstate shape/variants." }
        "loot_rule_review_candidate" { return "Add loot table or classify no-loot expected; not a safe failure yet." }
        default { return "Manual resource-boundary review required." }
    }
}

$portByKey = @{}
foreach ($entry in @($portManifest.entries)) {
    $portByKey["$($entry.kind):$($entry.registryId)"] = $entry
}

$resourceRows = [System.Collections.Generic.List[object]]::new()
foreach ($candidate in @($candidateReport.candidates)) {
    if ($candidate.category -notin @("lang_resource_gap", "model_resource_gap", "blockstate_resource_gap", "loot_rule_candidate")) {
        continue
    }

    $plain = Get-PlainId $candidate.id
    $portKey = "$($candidate.kind):$plain"
    $portEntry = if ($portByKey.ContainsKey($portKey)) { $portByKey[$portKey] } else { $null }
    $reviewCategory = Get-ResourceReviewCategory $candidate

    $resourceRows.Add([pscustomobject][ordered]@{
        kind = $candidate.kind
        id = $candidate.id
        check = $candidate.check
        sourceCategory = $candidate.category
        reviewCategory = $reviewCategory
        portEntryExists = $null -ne $portEntry
        suggestedLangKey = if ($candidate.category -eq "lang_resource_gap") { Get-LangKey $candidate.kind $candidate.id } else { $null }
        suggestedModelPath = if ($candidate.category -eq "model_resource_gap") { Get-ModelPath $candidate.kind $candidate.id } else { $null }
        recommendedAction = Get-ResourceAction $candidate $reviewCategory
        evidence = $candidate.evidence
    })
}

$orderedRows = @($resourceRows | Sort-Object reviewCategory, kind, id)
$summaryByCategory = @(
    $orderedRows |
        Group-Object reviewCategory |
        Sort-Object Name |
        ForEach-Object {
            [pscustomobject][ordered]@{
                reviewCategory = $_.Name
                count = $_.Count
            }
        }
)
$summaryByCheck = @(
    $orderedRows |
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
    candidateReport = $CandidateReportPath
    portManifest = $PortManifestPath
    resourceCandidateCount = $orderedRows.Count
    summaryByCategory = $summaryByCategory
    summaryByCheck = $summaryByCheck
    candidates = $orderedRows
}

foreach ($path in @($OutputJson, $OutputMarkdown)) {
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $path) | Out-Null
}

$output | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block parity resource gap review")
$lines.Add("")
$lines.Add("Generated: $($output.generatedAtUtc)")
$lines.Add("")
$lines.Add("Source candidates: ``$CandidateReportPath``")
$lines.Add("")
$lines.Add("This is a review helper. It does not generate resources and does not change fail-mode behavior.")
$lines.Add("")
$lines.Add("## Summary by review category")
$lines.Add("")
$lines.Add("| Review category | Count |")
$lines.Add("|---|---:|")
foreach ($group in $summaryByCategory) {
    $lines.Add("| $($group.reviewCategory) | $($group.count) |")
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
$lines.Add("## Resource candidates")
$lines.Add("")
$lines.Add("| Category | Kind | ID | Suggested resource | Port entry | Action | Evidence |")
$lines.Add("|---|---|---|---|---|---|---|")
foreach ($row in $orderedRows) {
    $resource = if ($row.suggestedLangKey) { $row.suggestedLangKey } elseif ($row.suggestedModelPath) { $row.suggestedModelPath } else { "" }
    $evidence = if ($row.evidence) { $row.evidence.Replace("|", "\|") } else { "" }
    $lines.Add("| $($row.reviewCategory) | $($row.kind) | ``$($row.id)`` | ``$resource`` | $($row.portEntryExists) | $($row.recommendedAction) | $evidence |")
}

$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Resource gap review: $OutputMarkdown"
Write-Output "ResourceCandidates=$($orderedRows.Count)"
foreach ($group in $summaryByCategory) {
    Write-Output "$($group.reviewCategory)=$($group.count)"
}
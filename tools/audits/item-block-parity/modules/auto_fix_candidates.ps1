[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$LegacyManifestPath,
    [Parameter(Mandatory = $true)][string]$PortManifestPath,
    [string]$PortRoot = "05_neoforge_port",
    [string]$RulesRoot,
    [string]$ReportRoot,
    [string[]]$Checks,
    [string]$ParityReportPath,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
$portPath = Join-Path $RepoRoot $PortRoot
if (-not (Test-Path -LiteralPath $LegacyManifestPath -PathType Leaf)) { throw "Legacy manifest not found: $LegacyManifestPath" }
if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw "Port manifest not found: $PortManifestPath" }
if (-not (Test-Path -LiteralPath $portPath -PathType Container)) { throw "Port root not found: $portPath" }
if (-not $RulesRoot) { $RulesRoot = Join-Path (Split-Path -Parent $PSScriptRoot) "rules" }
if (-not $ReportRoot) { $ReportRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity" }
if (-not $ParityReportPath) { $ParityReportPath = Join-Path $ReportRoot "item_block_parity_report.json" }
if (-not $OutputJson) { $OutputJson = Join-Path $ReportRoot "item_block_auto_fix_candidates.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $ReportRoot "item_block_auto_fix_candidates.md" }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}
function Read-JsonOrNull([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { return $null }
    try { return Get-Content -Raw -LiteralPath $Path | ConvertFrom-Json } catch { return $null }
}
function Read-RuleDocument([string]$Root, [string]$FileName) {
    if ([string]::IsNullOrWhiteSpace($Root)) { return [pscustomobject]@{ schemaVersion = 1; entries = @() } }
    $path = Join-Path $Root $FileName
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { return [pscustomobject]@{ schemaVersion = 1; entries = @() } }
    return Get-Content -Raw -LiteralPath $path | ConvertFrom-Json
}
function Normalize-Id([string]$Id) {
    if ([string]::IsNullOrWhiteSpace($Id)) { return "" }
    if ($Id.StartsWith("thaumcraft:")) { return $Id.Substring("thaumcraft:".Length) }
    return $Id
}
function Format-List($Values) {
    $items = @($Values | Where-Object { $_ } | Sort-Object -Unique)
    if ($items.Count -eq 0) { return "<none>" }
    return $items -join ", "
}
function New-EntryLookup($Manifest) {
    $lookup = @{}
    foreach ($entry in @($Manifest.entries)) {
        if (-not $entry.kind -or -not $entry.registryId) { continue }
        $lookup["$($entry.kind):$($entry.registryId)"] = $entry
    }
    return $lookup
}
function New-RuleLookup($Document) {
    $lookup = @{}
    foreach ($entry in @($Document.entries)) {
        if (-not $entry.kind -or -not $entry.id) { continue }
        $id = Normalize-Id ([string]$entry.id)
        $lookup["$($entry.kind):$id"] = $entry
    }
    return $lookup
}
function Test-RuleAccepted($Lookup, [string]$Kind, [string]$Id, [string]$Category) {
    $key = "${Kind}:$(Normalize-Id $Id)"
    if (-not $Lookup.ContainsKey($key)) { return $false }
    $entry = $Lookup[$key]
    if (-not $entry.acceptedCategories) { return $false }
    return @($entry.acceptedCategories | ForEach-Object { [string]$_ }) -contains $Category
}
function Get-RuleReason($Lookup, [string]$Kind, [string]$Id) {
    $key = "${Kind}:$(Normalize-Id $Id)"
    if ($Lookup.ContainsKey($key) -and $Lookup[$key].reason) { return [string]$Lookup[$key].reason }
    return ""
}
function Add-Candidate(
    $Rows,
    [string]$Category,
    [string]$Confidence,
    [string]$Kind,
    [string]$Id,
    [string]$SourceCheck,
    [string]$Status,
    [string]$Evidence,
    [string]$SuggestedAction,
    [string]$SuggestedPath = "",
    [string]$Reason = ""
) {
    $Rows.Add([pscustomobject][ordered]@{
        category = $Category
        confidence = $Confidence
        kind = $Kind
        id = if ($Id.StartsWith("thaumcraft:")) { $Id } else { "thaumcraft:$Id" }
        sourceCheck = $SourceCheck
        sourceStatus = $Status
        evidence = $Evidence
        suggestedAction = $SuggestedAction
        suggestedPath = $SuggestedPath
        reason = $Reason
    })
}
function Add-FromComparerReport($Rows, $Report, $PortLookup, $Rules) {
    if ($null -eq $Report -or -not $Report.results) { return }
    foreach ($row in @($Report.results)) {
        if ($row.status -notin @("MISSING", "DEFERRED")) { continue }
        $kind = [string]$row.kind
        $id = Normalize-Id ([string]$row.id)
        $check = [string]$row.check
        $entry = if ($PortLookup.ContainsKey("${kind}:$id")) { $PortLookup["${kind}:$id"] } else { $null }
        if (Test-RuleAccepted $Rules $kind $id $check) { continue }
        $ruleReason = Get-RuleReason $Rules $kind $id
        switch ($check) {
            "lang" {
                $key = if ($kind -eq "block" -or ($entry -and $entry.blockItem)) { "block.thaumcraft.$id" } else { "item.thaumcraft.$id" }
                Add-Candidate $Rows "missing_lang_stub" "safe_mechanical" $kind $id $check $row.status $row.evidence "Add a temporary English lang value after naming review" "05_neoforge_port/src/main/resources/assets/thaumcraft/lang/en_us.json" $ruleReason
            }
            "loot" {
                Add-Candidate $Rows "missing_self_drop_loot_table" "safe_if_self_drop_expected" $kind $id $check $row.status $row.evidence "Generate a simple self-drop loot table only after confirming legacy did not use custom drops" "05_neoforge_port/src/main/resources/data/thaumcraft/loot_table/blocks/$id.json" $ruleReason
            }
            "models" {
                if ($kind -eq "item") {
                    $category = if ($entry -and $entry.blockItem) { "missing_block_item_model_proxy" } else { "missing_generated_item_model" }
                    $action = if ($entry -and $entry.blockItem) { "Generate item model that points to the block model" } else { "Generate item/generated model after confirming texture naming" }
                    Add-Candidate $Rows $category "safe_if_texture_exists" $kind $id $check $row.status $row.evidence $action "05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/$id.json" $ruleReason
                } elseif ($kind -eq "block") {
                    Add-Candidate $Rows "missing_block_model_reference" "review_required" $kind $id $check $row.status $row.evidence "Create or remap referenced block model only after reviewing intended geometry" "05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/$id.json" $ruleReason
                }
            }
            "blockstates" {
                Add-Candidate $Rows "missing_blockstate_stub" "review_required" $kind $id $check $row.status $row.evidence "Generate a blockstate only after confirming state/property variants" "05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/$id.json" $ruleReason
            }
            "textures" {
                Add-Candidate $Rows "missing_texture_reference" "review_required" $kind $id $check $row.status $row.evidence "Resolve texture reference by porting/remapping the legacy texture; do not synthesize visual parity blindly" "" $ruleReason
            }
            "orphan_references" {
                Add-Candidate $Rows "orphan_resource_reference" "safe_failure_candidate" $kind $id $check $row.status $row.evidence "Fix broken model/texture reference before enabling safe CI hard-fail" "" $ruleReason
            }
            "block_item_pairs" {
                Add-Candidate $Rows "missing_block_item_registration" "review_required" $kind $id $check $row.status $row.evidence "Add BlockItem registration only if legacy block was obtainable as an item" "05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java" $ruleReason
            }
            default {
                Add-Candidate $Rows "generic_missing_parity_input" "review_required" $kind $id $check $row.status $row.evidence "Review missing parity result and classify before auto-fix" "" $ruleReason
            }
        }
    }
}
function Add-FromModuleReport($Rows, $ReportPath, [string]$DefaultKind, [string]$CategoryPrefix, [string[]]$ReviewStatuses, [string[]]$MismatchStatuses) {
    $report = Read-JsonOrNull $ReportPath
    if ($null -eq $report) { return }
    $dataRows = @()
    if ($report.results) { $dataRows = @($report.results) }
    elseif ($report.rows) { $dataRows = @($report.rows) }
    foreach ($row in $dataRows) {
        $status = [string]$row.status
        if ($status -notin @($ReviewStatuses + $MismatchStatuses)) { continue }
        $kind = if ($row.kind) { [string]$row.kind } else { $DefaultKind }
        $id = if ($row.id) { Normalize-Id ([string]$row.id) } elseif ($row.registryId) { Normalize-Id ([string]$row.registryId) } else { "unknown" }
        $subcheck = if ($row.subcheck) { [string]$row.subcheck } elseif ($row.check) { [string]$row.check } else { "unknown" }
        $confidence = if ($status -in $MismatchStatuses) { "review_required_mismatch" } else { "review_required" }
        $category = if ($status -in $MismatchStatuses) { "${CategoryPrefix}_mismatch" } else { "${CategoryPrefix}_review" }
        $evidence = if ($row.evidence) { [string]$row.evidence } else { "status=$status; subcheck=$subcheck" }
        Add-Candidate $Rows $category $confidence $kind $id $subcheck $status $evidence "Review report row and either implement parity or add a documented equivalence rule" "" ""
    }
}

$legacy = Get-Content -Raw -LiteralPath $LegacyManifestPath | ConvertFrom-Json
$port = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$portLookup = New-EntryLookup $port
$rules = New-RuleLookup (Read-RuleDocument $RulesRoot "auto-fix-candidate-rules.json")
$candidates = [System.Collections.Generic.List[object]]::new()

$parityReport = Read-JsonOrNull $ParityReportPath
Add-FromComparerReport $candidates $parityReport $portLookup $rules

Add-FromModuleReport $candidates (Join-Path $ReportRoot "item_block_item_property_report.json") "item" "item_property" @("ITEM_PROPERTY_REVIEW_NEEDED", "LEGACY_PROPERTY_MISSING", "NOT_EVIDENCED", "LEGACY_MISSING") @("ITEM_PROPERTY_MISMATCH", "VALUE_MISMATCH")
Add-FromModuleReport $candidates (Join-Path $ReportRoot "item_block_block_property_report.json") "block" "block_property" @("BLOCK_PROPERTY_REVIEW_NEEDED", "NOT_EVIDENCED", "LEGACY_MISSING") @("BLOCK_PROPERTY_MISMATCH", "VALUE_MISMATCH")
Add-FromModuleReport $candidates (Join-Path $ReportRoot "item_block_loot_drop_behavior_report.json") "block" "loot_drop" @("DROP_REVIEW_NEEDED", "TABLE_MISSING", "NOT_EVIDENCED", "LEGACY_MISSING") @("DROP_VALUE_MISMATCH", "VALUE_MISMATCH")
Add-FromModuleReport $candidates (Join-Path $ReportRoot "item_block_client_server_safety_report.json") "unknown" "client_server_safety" @("CLIENT_SERVER_REVIEW_NEEDED") @()
Add-FromModuleReport $candidates (Join-Path $ReportRoot "item_block_sound_particle_fx_report.json") "unknown" "sound_particle_fx" @("SOUND_PARTICLE_REVIEW_NEEDED", "NOT_EVIDENCED", "LEGACY_MISSING") @("SOUND_PARTICLE_MISMATCH", "VALUE_MISMATCH")
Add-FromModuleReport $candidates (Join-Path $ReportRoot "item_block_visual_model_transform_report.json") "unknown" "visual_model_transform" @("VISUAL_REVIEW_NEEDED", "VISUAL_MODEL_MISSING") @()
Add-FromModuleReport $candidates (Join-Path $ReportRoot "item_block_texture_color_report.json") "unknown" "texture_color" @("PORT_TEXTURE_MISSING", "LEGACY_TEXTURE_MISSING", "TEXTURE_REVIEW_NEEDED") @("TEXTURE_MISMATCH", "COLOR_MISMATCH")
Add-FromModuleReport $candidates (Join-Path $ReportRoot "item_block_data_reference_report.json") "unknown" "data_reference" @("DATA_REF_REVIEW_NEEDED", "MISSING_REFERENCE", "NOT_EVIDENCED") @()

$ordered = @($candidates | Sort-Object confidence, category, kind, id, sourceCheck)
$summaryByCategory = @($ordered | Group-Object category | Sort-Object Name | ForEach-Object {
    [pscustomobject][ordered]@{ category = $_.Name; count = $_.Count }
})
$summaryByConfidence = @($ordered | Group-Object confidence | Sort-Object Name | ForEach-Object {
    [pscustomobject][ordered]@{ confidence = $_.Name; count = $_.Count }
})

$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    policy = "Report-only auto-fix candidate classifier. It does not modify game resources. safe_mechanical means a candidate is mechanically simple, not automatically legacy-parity-correct."
    inputs = [ordered]@{
        legacyManifest = ConvertTo-RelativeRepoPath $LegacyManifestPath
        portManifest = ConvertTo-RelativeRepoPath $PortManifestPath
        parityReport = ConvertTo-RelativeRepoPath $ParityReportPath
        reportRoot = ConvertTo-RelativeRepoPath $ReportRoot
    }
    summary = [ordered]@{
        candidates = $ordered.Count
        categories = @($summaryByCategory).Count
        safeMechanical = @($ordered | Where-Object confidence -eq "safe_mechanical").Count
        safeIfTextureExists = @($ordered | Where-Object confidence -eq "safe_if_texture_exists").Count
        safeIfSelfDropExpected = @($ordered | Where-Object confidence -eq "safe_if_self_drop_expected").Count
        safeFailureCandidates = @($ordered | Where-Object confidence -eq "safe_failure_candidate").Count
        reviewRequired = @($ordered | Where-Object { $_.confidence -like "review_required*" }).Count
    }
    byCategory = @($summaryByCategory)
    byConfidence = @($summaryByConfidence)
    candidates = @($ordered)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 14 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block auto-fix candidates")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: report-only. A candidate is not automatically parity-safe until reviewed against legacy behavior.")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("- Candidates: $($report.summary.candidates)")
$lines.Add("- safe_mechanical: $($report.summary.safeMechanical)")
$lines.Add("- safe_if_texture_exists: $($report.summary.safeIfTextureExists)")
$lines.Add("- safe_if_self_drop_expected: $($report.summary.safeIfSelfDropExpected)")
$lines.Add("- safe_failure_candidate: $($report.summary.safeFailureCandidates)")
$lines.Add("- review_required*: $($report.summary.reviewRequired)")
$lines.Add("")
$lines.Add("## By category")
$lines.Add("")
$lines.Add("| Category | Count |")
$lines.Add("|---|---:|")
foreach ($row in $summaryByCategory) { $lines.Add("| $($row.category) | $($row.count) |") }
$lines.Add("")
$lines.Add("## Candidates")
$lines.Add("")
$lines.Add("| Confidence | Category | Kind | ID | Source | Suggested action |")
$lines.Add("|---|---|---|---|---|---|")
foreach ($candidate in @($ordered | Select-Object -First 500)) {
    $action = if ($candidate.suggestedAction) { $candidate.suggestedAction.Replace("|", "\\|") } else { "" }
    $lines.Add("| $($candidate.confidence) | $($candidate.category) | $($candidate.kind) | $($candidate.id) | $($candidate.sourceCheck):$($candidate.sourceStatus) | $action |")
}
if ($ordered.Count -gt 500) { $lines.Add(""); $lines.Add("Only first 500 candidates are shown in Markdown. See JSON for all candidates.") }
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Auto-fix candidate report: $OutputMarkdown"
Write-Output "Candidates=$($report.summary.candidates), safeMechanical=$($report.summary.safeMechanical), safeIfTextureExists=$($report.summary.safeIfTextureExists), safeIfSelfDropExpected=$($report.summary.safeIfSelfDropExpected), safeFailureCandidates=$($report.summary.safeFailureCandidates), reviewRequired=$($report.summary.reviewRequired)"
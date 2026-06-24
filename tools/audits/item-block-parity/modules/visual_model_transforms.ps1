[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$PortManifestPath,
    [string]$PortRoot = "05_neoforge_port",
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
$portPath = Join-Path $RepoRoot $PortRoot
if (-not (Test-Path -LiteralPath $portPath -PathType Container)) { throw "Port root not found: $portPath" }
if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw "Port manifest not found: $PortManifestPath" }
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_visual_model_transform_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_visual_model_transform_report.md" }

$port = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$assetsRoot = Join-Path $portPath "src/main/resources/assets/thaumcraft"
if (-not $RulesRoot) { $RulesRoot = Join-Path (Split-Path -Parent $PSScriptRoot) "rules" }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}
function Read-JsonFileOrNull([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { return $null }
    try { return Get-Content -Raw -LiteralPath $Path | ConvertFrom-Json } catch { return $null }
}
function Read-RuleDocument([string]$Root, [string]$FileName) {
    if ([string]::IsNullOrWhiteSpace($Root)) { return [pscustomobject]@{ schemaVersion = 1; entries = @() } }
    $path = Join-Path $Root $FileName
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { return [pscustomobject]@{ schemaVersion = 1; entries = @() } }
    return Get-Content -Raw -LiteralPath $path | ConvertFrom-Json
}
function Get-JsonPropertyNames($Object) {
    if ($null -eq $Object) { return @() }
    return @($Object.PSObject.Properties | ForEach-Object { $_.Name } | Sort-Object -Unique)
}
function Format-List($Values) {
    $items = @($Values | Where-Object { $_ } | Select-Object -Unique)
    if ($items.Count -eq 0) { return "<none>" }
    return $items -join ", "
}
function Get-ObjectAsCompactJson($Object) {
    if ($null -eq $Object) { return "<missing>" }
    return ($Object | ConvertTo-Json -Depth 8 -Compress)
}
function Test-Truthy($Value) {
    if ($null -eq $Value) { return $false }
    if ($Value -is [bool]) { return $Value }
    return [string]$Value -eq "True"
}
function Normalize-Id([string]$Kind, [string]$Id) {
    if ($Id.StartsWith("thaumcraft:")) { return $Id }
    return "thaumcraft:$Id"
}
function New-RuleLookup($Document) {
    $lookup = @{}
    foreach ($entry in @($Document.entries)) {
        if (-not $entry.kind -or -not $entry.id) { continue }
        $kind = [string]$entry.kind
        $id = Normalize-Id $kind ([string]$entry.id)
        $lookup["${kind}:$id"] = $entry
        if ($id.StartsWith("thaumcraft:")) { $lookup["${kind}:$($id.Substring('thaumcraft:'.Length))"] = $entry }
    }
    return $lookup
}
function Get-Rule($Lookup, [string]$Kind, [string]$RegistryId) {
    $qualified = Normalize-Id $Kind $RegistryId
    foreach ($key in @("${Kind}:$qualified", "${Kind}:$RegistryId")) {
        if ($Lookup.ContainsKey($key)) { return $Lookup[$key] }
    }
    return $null
}
function Get-RuleReason($Rule) {
    if ($null -eq $Rule) { return "" }
    if ($Rule.reason) { return [string]$Rule.reason }
    return "Reviewed visual equivalence rule"
}
function Add-ResultRow($Rows, [string]$Kind, [string]$Id, [string]$Subcheck, [string]$Status, [string]$Evidence, [string]$Path = "", $Rule = $null) {
    $Rows.Add([pscustomobject][ordered]@{
        check = "visual_boundary"
        subcheck = $Subcheck
        kind = $Kind
        id = "thaumcraft:$Id"
        status = $Status
        evidence = $Evidence
        path = $Path
        rule = if ($null -eq $Rule) { $null } else { [pscustomobject][ordered]@{ reason = Get-RuleReason $Rule } }
    })
}
function Get-DisplayStatus([string[]]$DisplaySlots, $TransformRule) {
    if ($DisplaySlots.Count -gt 0) { return "VISUAL_EVIDENCE" }
    if ($null -ne $TransformRule -and (Test-Truthy $TransformRule.allowMissingDisplay)) { return "VISUAL_RULE_ACCEPTED" }
    return "VISUAL_REVIEW_NEEDED"
}
function Get-HandheldStatus([string]$Parent, [string[]]$DisplaySlots, $TransformRule) {
    $hasHandheldContext = ($DisplaySlots -contains "firstperson_righthand") -or ($DisplaySlots -contains "thirdperson_righthand")
    if ($Parent -ne "item/handheld" -or $hasHandheldContext) { return "VISUAL_EVIDENCE" }
    if ($null -ne $TransformRule -and (Test-Truthy $TransformRule.allowMissingHandheldTransforms)) { return "VISUAL_RULE_ACCEPTED" }
    return "VISUAL_REVIEW_NEEDED"
}
function Get-ParentRuleStatus([string]$Parent, $ParentRule) {
    if ($null -eq $ParentRule -or -not $ParentRule.acceptedParents) { return "VISUAL_EVIDENCE" }
    $accepted = @($ParentRule.acceptedParents | ForEach-Object { [string]$_ })
    if ($accepted -contains $Parent) { return "VISUAL_RULE_ACCEPTED" }
    return "VISUAL_REVIEW_NEEDED"
}

$displayContexts = @(
    "gui",
    "ground",
    "fixed",
    "firstperson_righthand",
    "firstperson_lefthand",
    "thirdperson_righthand",
    "thirdperson_lefthand",
    "head"
)

$transformRules = New-RuleLookup (Read-RuleDocument $RulesRoot "item-transform-equivalence.json")
$parentRules = New-RuleLookup (Read-RuleDocument $RulesRoot "model-parent-equivalence.json")
$results = [System.Collections.Generic.List[object]]::new()
$modelSummaries = [System.Collections.Generic.List[object]]::new()

foreach ($entry in @($port.entries | Sort-Object kind, registryId)) {
    if ($entry.kind -eq "item") {
        $modelPath = Join-Path $assetsRoot "models/item/$($entry.registryId).json"
        $relativePath = if (Test-Path -LiteralPath $modelPath -PathType Leaf) { ConvertTo-RelativeRepoPath $modelPath } else { "assets/thaumcraft/models/item/$($entry.registryId).json" }
        $json = Read-JsonFileOrNull $modelPath
        if ($null -eq $json) {
            Add-ResultRow $results "item" $entry.registryId "item_model_json" "VISUAL_MODEL_MISSING" "Item model JSON missing or invalid" $relativePath
            continue
        }
        $parent = if ($json.parent) { [string]$json.parent } else { "<none>" }
        $textureSlots = @(Get-JsonPropertyNames $json.textures)
        $displaySlots = @(Get-JsonPropertyNames $json.display)
        $missingDisplaySlots = @($displayContexts | Where-Object { $_ -notin $displaySlots })
        $transformRule = Get-Rule $transformRules "item" $entry.registryId
        $parentRule = Get-Rule $parentRules "item" $entry.registryId
        $modelSummaries.Add([pscustomobject][ordered]@{
            kind = "item"
            id = "thaumcraft:$($entry.registryId)"
            path = $relativePath
            parent = $parent
            textureSlots = @($textureSlots)
            displaySlots = @($displaySlots)
            hasTransformRule = $null -ne $transformRule
            hasParentRule = $null -ne $parentRule
        })
        $parentStatus = Get-ParentRuleStatus $parent $parentRule
        Add-ResultRow $results "item" $entry.registryId "item_model_json" $parentStatus "parent=$parent; textureSlots=$(Format-List $textureSlots); displaySlots=$(Format-List $displaySlots)" $relativePath $parentRule
        $displayStatus = Get-DisplayStatus $displaySlots $transformRule
        if ($displaySlots.Count -eq 0) {
            $evidence = "No explicit item display transforms; vanilla/model defaults may apply, but hand/GUI parity is unchecked"
            if ($displayStatus -eq "VISUAL_RULE_ACCEPTED") { $evidence = "Missing display transforms accepted by reviewed item-transform-equivalence rule: $(Get-RuleReason $transformRule)" }
            Add-ResultRow $results "item" $entry.registryId "item_display_transforms" $displayStatus $evidence $relativePath $transformRule
        } else {
            $presentDisplayEvidence = @($displaySlots | ForEach-Object { "$_=$(Get-ObjectAsCompactJson $json.display.$_)" })
            Add-ResultRow $results "item" $entry.registryId "item_display_transforms" "VISUAL_EVIDENCE" "present=$(Format-List $displaySlots); missing=$(Format-List $missingDisplaySlots); values=$(Format-List $presentDisplayEvidence)" $relativePath
        }
        if ($parent -eq "item/generated" -and $displaySlots.Count -eq 0) {
            Add-ResultRow $results "item" $entry.registryId "generated_icon_default_transform" "VISUAL_EVIDENCE" "Generated item model with vanilla default transforms; verify manually if legacy used custom handheld/GUI transform" $relativePath
        }
        if ($parent -eq "item/handheld" -and ($displaySlots -notcontains "firstperson_righthand" -and $displaySlots -notcontains "thirdperson_righthand")) {
            $handheldStatus = Get-HandheldStatus $parent $displaySlots $transformRule
            $evidence = "Handheld parent with no explicit first/third-person display transform; visual parity may depend on defaults"
            if ($handheldStatus -eq "VISUAL_RULE_ACCEPTED") { $evidence = "Missing handheld transforms accepted by reviewed item-transform-equivalence rule: $(Get-RuleReason $transformRule)" }
            Add-ResultRow $results "item" $entry.registryId "handheld_transform_review" $handheldStatus $evidence $relativePath $transformRule
        }
    } elseif ($entry.kind -eq "block") {
        $referencedModels = @($entry.resources.referencedBlockModels | Where-Object { $_ } | Sort-Object -Unique)
        $parentRule = Get-Rule $parentRules "block" $entry.registryId
        if ($referencedModels.Count -eq 0) {
            Add-ResultRow $results "block" $entry.registryId "block_model_refs" "VISUAL_REVIEW_NEEDED" "No referenced block models found from blockstate manifest evidence" "assets/thaumcraft/blockstates/$($entry.registryId).json"
            continue
        }
        foreach ($modelRef in $referencedModels) {
            $modelPath = Join-Path $assetsRoot "models/$modelRef.json"
            $relativePath = if (Test-Path -LiteralPath $modelPath -PathType Leaf) { ConvertTo-RelativeRepoPath $modelPath } else { "assets/thaumcraft/models/$modelRef.json" }
            $json = Read-JsonFileOrNull $modelPath
            if ($null -eq $json) {
                Add-ResultRow $results "block" $entry.registryId "block_model_json" "VISUAL_MODEL_MISSING" "Referenced block model JSON missing or invalid: $modelRef" $relativePath
                continue
            }
            $parent = if ($json.parent) { [string]$json.parent } else { "<none>" }
            $textureSlots = @(Get-JsonPropertyNames $json.textures)
            $elementCount = @($json.elements).Count
            $modelSummaries.Add([pscustomobject][ordered]@{
                kind = "block"
                id = "thaumcraft:$($entry.registryId)"
                path = $relativePath
                modelRef = $modelRef
                parent = $parent
                textureSlots = @($textureSlots)
                elements = $elementCount
                hasParentRule = $null -ne $parentRule
            })
            $parentStatus = Get-ParentRuleStatus $parent $parentRule
            Add-ResultRow $results "block" $entry.registryId "block_model_json" $parentStatus "model=$modelRef; parent=$parent; textureSlots=$(Format-List $textureSlots); elementCount=$elementCount" $relativePath $parentRule
            if ($parent -eq "<none>" -and $elementCount -eq 0) {
                Add-ResultRow $results "block" $entry.registryId "block_geometry_review" "VISUAL_REVIEW_NEEDED" "Block model has no parent and no elements; geometry/render parity cannot be established" $relativePath
            }
            if ($textureSlots.Count -eq 0) {
                Add-ResultRow $results "block" $entry.registryId "block_texture_slots" "VISUAL_REVIEW_NEEDED" "Block model has no direct texture slots; texture parity may depend on parent or generated model" $relativePath
            }
        }
    }
}

$orderedResults = @($results | Sort-Object status, kind, id, subcheck)
$summaryBySubcheck = @($orderedResults | Group-Object subcheck | Sort-Object Name | ForEach-Object {
    [pscustomobject][ordered]@{
        subcheck = $_.Name
        rows = $_.Count
        evidence = @($_.Group | Where-Object status -eq "VISUAL_EVIDENCE").Count
        ruleAccepted = @($_.Group | Where-Object status -eq "VISUAL_RULE_ACCEPTED").Count
        reviewNeeded = @($_.Group | Where-Object status -eq "VISUAL_REVIEW_NEEDED").Count
        missing = @($_.Group | Where-Object status -eq "VISUAL_MODEL_MISSING").Count
    }
})
$report = [ordered]@{
    schemaVersion = 2
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    selectedChecks = @("visual_boundary")
    policy = "Report-only visual/model transform scan. It records model parent, texture slots, block element presence and item display transforms. Reviewed equivalence rules may convert specific visual review rows into VISUAL_RULE_ACCEPTED, but no rule is applied unless explicitly documented."
    rules = [ordered]@{
        itemTransformEquivalence = "item-transform-equivalence.json"
        modelParentEquivalence = "model-parent-equivalence.json"
    }
    summary = [ordered]@{
        rows = $orderedResults.Count
        evidence = @($orderedResults | Where-Object status -eq "VISUAL_EVIDENCE").Count
        ruleAccepted = @($orderedResults | Where-Object status -eq "VISUAL_RULE_ACCEPTED").Count
        reviewNeeded = @($orderedResults | Where-Object status -eq "VISUAL_REVIEW_NEEDED").Count
        missing = @($orderedResults | Where-Object status -eq "VISUAL_MODEL_MISSING").Count
        models = @($modelSummaries).Count
        bySubcheck = @($summaryBySubcheck)
    }
    models = @($modelSummaries | Sort-Object kind, id, path)
    results = $orderedResults
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputMarkdown) | Out-Null
$report | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block visual model transform report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Rows | Evidence | Rule accepted | Review needed | Missing models | Models inspected |")
$lines.Add("|---:|---:|---:|---:|---:|---:|")
$lines.Add("| $($report.summary.rows) | $($report.summary.evidence) | $($report.summary.ruleAccepted) | $($report.summary.reviewNeeded) | $($report.summary.missing) | $($report.summary.models) |")
$lines.Add("")
$lines.Add("## By subcheck")
$lines.Add("")
$lines.Add("| Subcheck | Rows | Evidence | Rule accepted | Review needed | Missing |")
$lines.Add("|---|---:|---:|---:|---:|---:|")
foreach ($row in $summaryBySubcheck) { $lines.Add("| $($row.subcheck) | $($row.rows) | $($row.evidence) | $($row.ruleAccepted) | $($row.reviewNeeded) | $($row.missing) |") }
$lines.Add("")
$lines.Add("## Review-needed, missing and rule-accepted rows")
$lines.Add("")
$lines.Add("| Kind | ID | Subcheck | Status | Path | Evidence |")
$lines.Add("|---|---|---|---|---|---|")
foreach ($row in $orderedResults | Where-Object { $_.status -ne "VISUAL_EVIDENCE" }) {
    $safeEvidence = $row.evidence.Replace("|", "\|")
    $lines.Add("| $($row.kind) | ``$($row.id)`` | $($row.subcheck) | $($row.status) | ``$($row.path)`` | $safeEvidence |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output "Visual model transform report: $OutputMarkdown"
Write-Output "Rows=$($report.summary.rows), evidence=$($report.summary.evidence), ruleAccepted=$($report.summary.ruleAccepted), reviewNeeded=$($report.summary.reviewNeeded), missing=$($report.summary.missing), models=$($report.summary.models)"

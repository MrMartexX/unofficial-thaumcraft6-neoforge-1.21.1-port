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
function Normalize-ModelRef([string]$Ref) {
    if ([string]::IsNullOrWhiteSpace($Ref)) { return "" }
    if ($Ref.Contains(":")) { return $Ref }
    return "minecraft:$Ref"
}
function Get-ThaumcraftModelPath([string]$Ref) {
    $normalized = Normalize-ModelRef $Ref
    if (-not $normalized.StartsWith("thaumcraft:")) { return $null }
    $relative = $normalized.Substring("thaumcraft:".Length)
    if (-not ($relative.StartsWith("item/") -or $relative.StartsWith("block/"))) { return $null }
    return Join-Path $assetsRoot "models/$relative.json"
}
function Normalize-BlockModelRef([string]$Ref) {
    if ([string]::IsNullOrWhiteSpace($Ref)) { return "" }
    if ($Ref.Contains(":")) { return $Ref }
    return "thaumcraft:$Ref"
}
function Add-ModelRefsFromNode($Node, [System.Collections.Generic.List[string]]$Refs) {
    if ($null -eq $Node) { return }
    if ($Node -is [System.Collections.IDictionary]) {
        foreach ($key in $Node.Keys) {
            if ([string]$key -eq "model" -and $Node[$key] -is [string]) {
                $Refs.Add((Normalize-BlockModelRef ([string]$Node[$key])))
            } else {
                Add-ModelRefsFromNode $Node[$key] $Refs
            }
        }
        return
    }
    if ($Node -is [System.Collections.IEnumerable] -and $Node -isnot [string]) {
        foreach ($child in $Node) { Add-ModelRefsFromNode $child $Refs }
    }
}
function Get-BlockstateModelRefs([string]$Id) {
    $path = Join-Path $assetsRoot "blockstates/$Id.json"
    $refs = [System.Collections.Generic.List[string]]::new()
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { return @() }
    try {
        $json = Get-Content -Raw -LiteralPath $path | ConvertFrom-Json -AsHashtable
        Add-ModelRefsFromNode $json $refs
    } catch {
        return @()
    }
    return @($refs | Sort-Object -Unique)
}
function Test-VanillaModelTemplate([string]$Parent) {
    return $Parent -match '^minecraft:block/(block|cube|cube_all|cube_bottom_top|cube_column|cube_column_horizontal|orientable|orientable_vertical|template_.+|cross|tinted_cross)$'
}
function Get-EffectiveTextureInfo([string]$ModelPath) {
    $visited = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
    $currentPath = $ModelPath
    while (-not [string]::IsNullOrWhiteSpace($currentPath) -and $visited.Add($currentPath)) {
        $model = Read-JsonFileOrNull $currentPath
        if ($null -eq $model) {
            return [pscustomobject]@{ hasTextures = $false; slots = @(); source = ConvertTo-RelativeRepoPath $currentPath }
        }
        $slots = @(Get-JsonPropertyNames $model.textures)
        if ($slots.Count -gt 0) {
            return [pscustomobject]@{ hasTextures = $true; slots = $slots; source = ConvertTo-RelativeRepoPath $currentPath }
        }
        $parent = if ($model.parent) { Normalize-ModelRef ([string]$model.parent) } else { "" }
        if ($parent.StartsWith("minecraft:block/")) {
            return [pscustomobject]@{
                hasTextures = -not (Test-VanillaModelTemplate $parent)
                slots = @()
                source = $parent
            }
        }
        $parentPath = Get-ThaumcraftModelPath $parent
        if ([string]::IsNullOrWhiteSpace($parentPath)) {
            return [pscustomobject]@{ hasTextures = $false; slots = @(); source = $parent }
        }
        $currentPath = $parentPath
    }
    return [pscustomobject]@{ hasTextures = $false; slots = @(); source = "<cycle>" }
}
function Test-KnownVanillaDisplayParent([string]$Parent) {
    if ($Parent -match '^minecraft:item/(generated|handheld|handheld_rod)$' -or $Parent -match '^minecraft:block/') {
        return $true
    }
    return $Parent -match '^minecraft:item/(black|blue|brown|cyan|gray|green|light_blue|light_gray|lime|magenta|orange|pink|purple|red|white|yellow)_banner$'
}
function Get-EffectiveDisplayInfo([string]$ModelPath) {
    $visited = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
    $currentPath = $ModelPath
    while (-not [string]::IsNullOrWhiteSpace($currentPath) -and $visited.Add($currentPath)) {
        $model = Read-JsonFileOrNull $currentPath
        if ($null -eq $model) {
            return [pscustomobject]@{
                hasDisplay = $false
                usesVanillaDefaults = $false
                displaySlots = @()
                source = ConvertTo-RelativeRepoPath $currentPath
                terminalParent = "<invalid>"
            }
        }
        $slots = @(Get-JsonPropertyNames $model.display)
        if ($slots.Count -gt 0) {
            return [pscustomobject]@{
                hasDisplay = $true
                usesVanillaDefaults = $false
                displaySlots = $slots
                source = ConvertTo-RelativeRepoPath $currentPath
                terminalParent = if ($model.parent) { Normalize-ModelRef ([string]$model.parent) } else { "<none>" }
            }
        }
        $parent = if ($model.parent) { Normalize-ModelRef ([string]$model.parent) } else { "<none>" }
        if (Test-KnownVanillaDisplayParent $parent) {
            return [pscustomobject]@{
                hasDisplay = $false
                usesVanillaDefaults = $true
                displaySlots = @()
                source = $parent
                terminalParent = $parent
            }
        }
        $parentPath = Get-ThaumcraftModelPath $parent
        if ([string]::IsNullOrWhiteSpace($parentPath)) {
            return [pscustomobject]@{
                hasDisplay = $false
                usesVanillaDefaults = $false
                displaySlots = @()
                source = ConvertTo-RelativeRepoPath $currentPath
                terminalParent = $parent
            }
        }
        $currentPath = $parentPath
    }
    return [pscustomobject]@{
        hasDisplay = $false
        usesVanillaDefaults = $false
        displaySlots = @()
        source = ConvertTo-RelativeRepoPath $ModelPath
        terminalParent = "<cycle>"
    }
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
        $effectiveDisplay = Get-EffectiveDisplayInfo $modelPath
        $effectiveDisplaySlots = @($effectiveDisplay.displaySlots)
        $missingDisplaySlots = @($displayContexts | Where-Object { $_ -notin $effectiveDisplaySlots })
        $transformRule = Get-Rule $transformRules "item" $entry.registryId
        $parentRule = Get-Rule $parentRules "item" $entry.registryId
        $modelSummaries.Add([pscustomobject][ordered]@{
            kind = "item"
            id = "thaumcraft:$($entry.registryId)"
            path = $relativePath
            parent = $parent
            textureSlots = @($textureSlots)
            displaySlots = @($displaySlots)
            effectiveDisplaySlots = @($effectiveDisplaySlots)
            effectiveDisplaySource = $effectiveDisplay.source
            usesVanillaDisplayDefaults = [bool]$effectiveDisplay.usesVanillaDefaults
            hasTransformRule = $null -ne $transformRule
            hasParentRule = $null -ne $parentRule
        })
        $parentStatus = Get-ParentRuleStatus $parent $parentRule
        Add-ResultRow $results "item" $entry.registryId "item_model_json" $parentStatus "parent=$parent; textureSlots=$(Format-List $textureSlots); displaySlots=$(Format-List $displaySlots)" $relativePath $parentRule
        $hasEffectiveDisplay = $effectiveDisplay.hasDisplay -or $effectiveDisplay.usesVanillaDefaults
        $displayStatus = Get-DisplayStatus $(if ($hasEffectiveDisplay) { @("effective") } else { @() }) $transformRule
        if (-not $hasEffectiveDisplay) {
            $evidence = "No display transforms in the Thaumcraft parent chain and no known vanilla item/block display defaults; hand/GUI flat-front risk remains"
            if ($displayStatus -eq "VISUAL_RULE_ACCEPTED") { $evidence = "Missing display transforms accepted by reviewed item-transform-equivalence rule: $(Get-RuleReason $transformRule)" }
            Add-ResultRow $results "item" $entry.registryId "item_display_transforms" $displayStatus $evidence $relativePath $transformRule
        } elseif ($effectiveDisplay.usesVanillaDefaults) {
            Add-ResultRow $results "item" $entry.registryId "item_display_transforms" "VISUAL_EVIDENCE" "Inherited known vanilla display defaults from $($effectiveDisplay.source)" $relativePath
        } else {
            Add-ResultRow $results "item" $entry.registryId "item_display_transforms" "VISUAL_EVIDENCE" "Inherited/effective display slots=$(Format-List $effectiveDisplaySlots); missing=$(Format-List $missingDisplaySlots); source=$($effectiveDisplay.source)" $relativePath
        }
        $normalizedParent = Normalize-ModelRef $parent
        if ($normalizedParent -eq "minecraft:item/generated" -and $displaySlots.Count -eq 0) {
            Add-ResultRow $results "item" $entry.registryId "generated_icon_default_transform" "VISUAL_EVIDENCE" "Generated item model with vanilla default transforms; verify manually if legacy used custom handheld/GUI transform" $relativePath
        }
        if ($normalizedParent -eq "minecraft:item/handheld" -and ($displaySlots -notcontains "firstperson_righthand" -and $displaySlots -notcontains "thirdperson_righthand")) {
            Add-ResultRow $results "item" $entry.registryId "handheld_transform_review" "VISUAL_EVIDENCE" "Handheld model inherits the defined vanilla first/third-person transforms" $relativePath
        }
    } elseif ($entry.kind -eq "block") {
        $referencedModels = @($entry.resources.referencedBlockModels | Where-Object { $_ } | ForEach-Object { Normalize-BlockModelRef ([string]$_) } | Sort-Object -Unique)
        if ($referencedModels.Count -eq 0) {
            $referencedModels = @(Get-BlockstateModelRefs $entry.registryId)
        }
        $parentRule = Get-Rule $parentRules "block" $entry.registryId
        if ($referencedModels.Count -eq 0) {
            Add-ResultRow $results "block" $entry.registryId "block_model_refs" "VISUAL_REVIEW_NEEDED" "No referenced block models found from blockstate manifest evidence" "assets/thaumcraft/blockstates/$($entry.registryId).json"
            continue
        }
        foreach ($modelRef in $referencedModels) {
            if ($modelRef.StartsWith("minecraft:")) {
                $modelSummaries.Add([pscustomobject][ordered]@{
                    kind = "block"
                    id = "thaumcraft:$($entry.registryId)"
                    path = $modelRef
                    modelRef = $modelRef
                    parent = "<vanilla-model>"
                    textureSlots = @()
                    elements = 0
                    hasParentRule = $null -ne $parentRule
                })
                Add-ResultRow $results "block" $entry.registryId "block_model_json" "VISUAL_EVIDENCE" "Blockstate references the concrete baked vanilla model $modelRef" "assets/thaumcraft/blockstates/$($entry.registryId).json"
                continue
            }
            $modelPath = Get-ThaumcraftModelPath $modelRef
            $relativePath = if (Test-Path -LiteralPath $modelPath -PathType Leaf) { ConvertTo-RelativeRepoPath $modelPath } else { "assets/thaumcraft/models/$modelRef.json" }
            $json = Read-JsonFileOrNull $modelPath
            if ($null -eq $json) {
                Add-ResultRow $results "block" $entry.registryId "block_model_json" "VISUAL_MODEL_MISSING" "Referenced block model JSON missing or invalid: $modelRef" $relativePath
                continue
            }
            $parent = if ($json.parent) { [string]$json.parent } else { "<none>" }
            $textureSlots = @(Get-JsonPropertyNames $json.textures)
            $effectiveTextures = Get-EffectiveTextureInfo $modelPath
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
            if (-not $effectiveTextures.hasTextures) {
                Add-ResultRow $results "block" $entry.registryId "block_texture_slots" "VISUAL_REVIEW_NEEDED" "Block model and its resolvable parent chain provide no texture slots; terminal source=$($effectiveTextures.source)" $relativePath
            } elseif ($textureSlots.Count -eq 0) {
                Add-ResultRow $results "block" $entry.registryId "block_texture_slots" "VISUAL_EVIDENCE" "Texture slots inherited from $($effectiveTextures.source): $(Format-List $effectiveTextures.slots)" $relativePath
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

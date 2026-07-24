[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$RepoRoot,
    [string]$PortManifestPath,
    [string]$PortRoot = '05_neoforge_port',
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$PortRootPath = if ([System.IO.Path]::IsPathRooted($PortRoot)) { $PortRoot } else { Join-Path $RepoRoot $PortRoot }
$JavaRoot = Join-Path $PortRootPath 'src/main/java/thaumcraft'
$AssetRoot = Join-Path $PortRootPath 'src/main/resources/assets/thaumcraft'
$ReportDir = Join-Path $RepoRoot 'tools/reports/local/item-block-parity'
$JsonReport = if ([string]::IsNullOrWhiteSpace($OutputJson)) { Join-Path $ReportDir 'item_block_item_visual_parity_report.json' } else { $OutputJson }
$MdReport = if ([string]::IsNullOrWhiteSpace($OutputMarkdown)) { Join-Path $ReportDir 'item_block_item_visual_parity_report.md' } else { $OutputMarkdown }

$TCItemsPath = Join-Path $JavaRoot 'common/registry/TCItems.java'
$TCBlocksPath = Join-Path $JavaRoot 'common/registry/TCBlocks.java'
$ItemModelRoot = Join-Path $AssetRoot 'models/item'
$BlockModelRoot = Join-Path $AssetRoot 'models/block'
$LegacyAssetRoot = Join-Path $RepoRoot '02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/resources/assets/thaumcraft'
$LegacyItemModelRoot = Join-Path $LegacyAssetRoot 'models/item'
$LegacyBlockstateRoot = Join-Path $LegacyAssetRoot 'blockstates'

if (-not (Test-Path $TCItemsPath)) { throw "Missing TCItems.java at $TCItemsPath" }
if (-not (Test-Path $ReportDir)) { New-Item -ItemType Directory -Force -Path $ReportDir | Out-Null }

function Read-Text([string]$Path) {
    if (-not (Test-Path $Path)) { return '' }
    return Get-Content -Raw -Path $Path
}

function Get-RelativePath([string]$Path) {
    if ([string]::IsNullOrWhiteSpace($Path)) { return $null }
    try {
        return [System.IO.Path]::GetRelativePath($RepoRoot, $Path).Replace('\\', '/')
    } catch {
        return $Path.Replace('\\', '/')
    }
}

function Normalize-ResourceRef([string]$Ref, [string]$DefaultNamespace) {
    if ([string]::IsNullOrWhiteSpace($Ref)) { return $null }
    $value = $Ref.Trim()
    if ($value.Contains(':')) { return $value }
    return "${DefaultNamespace}:$value"
}

function Get-ModelPathFromRef([string]$Ref, [string]$Kind) {
    $normalized = Normalize-ResourceRef $Ref 'thaumcraft'
    if ([string]::IsNullOrWhiteSpace($normalized)) { return $null }
    $parts = $normalized.Split(':', 2)
    if ($parts.Count -ne 2) { return $null }
    $namespace = $parts[0]
    $path = $parts[1]
    if ($namespace -ne 'thaumcraft') { return $null }
    if ($path.StartsWith('item/')) { $path = $path.Substring(5) }
    if ($path.StartsWith('block/')) { $path = $path.Substring(6) }
    if ($Kind -eq 'item') { return Join-Path $ItemModelRoot ($path + '.json') }
    return Join-Path $BlockModelRoot ($path + '.json')
}

function Read-JsonFile([string]$Path) {
    if (-not (Test-Path $Path)) { return $null }
    try {
        return Get-Content -Raw -Path $Path | ConvertFrom-Json
    } catch {
        return $null
    }
}

function Has-JsonProperty($Object, [string]$Name) {
    if ($null -eq $Object) { return $false }
    return $null -ne ($Object.PSObject.Properties[$Name])
}

function Get-JsonPropertyValue($Object, [string]$Name) {
    if (-not (Has-JsonProperty $Object $Name)) { return $null }
    return $Object.PSObject.Properties[$Name].Value
}

function Get-TextureRefs($Model) {
    $refs = New-Object System.Collections.Generic.List[string]
    if ($null -eq $Model) { return $refs }
    $textures = Get-JsonPropertyValue $Model 'textures'
    if ($null -eq $textures) { return $refs }
    foreach ($property in $textures.PSObject.Properties) {
        $value = [string]$property.Value
        if (-not [string]::IsNullOrWhiteSpace($value)) { $refs.Add($value) }
    }
    return $refs
}

function Is-AllowedGenericMinecraftParent([string]$Parent) {
    if ([string]::IsNullOrWhiteSpace($Parent)) { return $true }
    $normalized = Normalize-ResourceRef $Parent 'minecraft'
    $allowed = @(
        'minecraft:item/generated',
        'minecraft:item/handheld',
        'minecraft:item/handheld_rod',
        'minecraft:block/cube',
        'minecraft:block/cube_all',
        'minecraft:block/cube_bottom_top',
        'minecraft:block/cube_column',
        'minecraft:block/cross',
        'minecraft:block/slab',
        'minecraft:block/slab_top',
        'minecraft:block/stairs',
        'minecraft:block/inner_stairs',
        'minecraft:block/outer_stairs',
        'minecraft:block/fence_inventory',
        'minecraft:block/wall_inventory'
    )
    return $allowed -contains $normalized
}

$ExpectedSpecificMinecraftParents = @{
    bannerblack = 'minecraft:item/black_banner'
    bannerblue = 'minecraft:item/blue_banner'
    bannerbrown = 'minecraft:item/brown_banner'
    bannercyan = 'minecraft:item/cyan_banner'
    bannergray = 'minecraft:item/gray_banner'
    bannergreen = 'minecraft:item/green_banner'
    bannerlightblue = 'minecraft:item/light_blue_banner'
    bannerlime = 'minecraft:item/lime_banner'
    bannermagenta = 'minecraft:item/magenta_banner'
    bannerorange = 'minecraft:item/orange_banner'
    bannerpink = 'minecraft:item/pink_banner'
    bannerpurple = 'minecraft:item/purple_banner'
    bannerred = 'minecraft:item/red_banner'
    bannersilver = 'minecraft:item/light_gray_banner'
    bannerwhite = 'minecraft:item/white_banner'
    banneryellow = 'minecraft:item/yellow_banner'
}

function Is-ExpectedSpecificMinecraftParent([string]$Id, [string]$Parent) {
    return $ExpectedSpecificMinecraftParents.ContainsKey($Id) `
        -and $ExpectedSpecificMinecraftParents[$Id] -eq $Parent
}

function Add-Result(
    [System.Collections.Generic.List[object]]$Results,
    [string]$Kind,
    [string]$Id,
    [string]$Subcheck,
    [string]$Status,
    [string]$Path,
    [string]$Evidence,
    [string]$Severity
) {
    $Results.Add([pscustomobject]@{
        kind = $Kind
        id = "thaumcraft:$Id"
        subcheck = $Subcheck
        status = $Status
        path = (Get-RelativePath $Path)
        evidence = $Evidence
        severity = $Severity
    }) | Out-Null
}

function Test-FullCubeElement($Element) {
    if ($null -eq $Element) { return $false }
    $from = Get-JsonPropertyValue $Element 'from'
    $to = Get-JsonPropertyValue $Element 'to'
    if ($null -eq $from -or $null -eq $to) { return $false }
    if ($from.Count -lt 3 -or $to.Count -lt 3) { return $false }
    return ([double]$from[0] -eq 0 -and [double]$from[1] -eq 0 -and [double]$from[2] -eq 0 -and
            [double]$to[0] -eq 16 -and [double]$to[1] -eq 16 -and [double]$to[2] -eq 16)
}

function Test-LikelyCustomOrNonFullBlockModel([string]$BlockModelRef) {
    $blockModelPath = Get-ModelPathFromRef $BlockModelRef 'block'
    if ([string]::IsNullOrWhiteSpace($blockModelPath) -or -not (Test-Path $blockModelPath)) {
        return [pscustomobject]@{ isRisk = $false; reason = 'block model not found or external'; path = $blockModelPath }
    }

    $model = Read-JsonFile $blockModelPath
    if ($null -eq $model) {
        return [pscustomobject]@{ isRisk = $true; reason = 'block model JSON could not be parsed'; path = $blockModelPath }
    }

    $parent = [string](Get-JsonPropertyValue $model 'parent')
    $normalizedParent = Normalize-ResourceRef $parent 'minecraft'
    if ($normalizedParent -match '^minecraft:block/(slab|slab_top|stairs|inner_stairs|outer_stairs|cube|cube_all|cube_column)$') {
        return [pscustomobject]@{ isRisk = $false; reason = "generic vanilla block parent $normalizedParent"; path = $blockModelPath }
    }

    $elements = Get-JsonPropertyValue $model 'elements'
    if ($null -eq $elements) {
        if ($normalizedParent -match '^thaumcraft:block/') {
            return Test-LikelyCustomOrNonFullBlockModel $normalizedParent
        }
        return [pscustomobject]@{ isRisk = $false; reason = 'no direct elements detected'; path = $blockModelPath }
    }

    $count = @($elements).Count
    if ($count -ne 1) {
        return [pscustomobject]@{ isRisk = $true; reason = "custom block model has $count elements"; path = $blockModelPath }
    }

    if (-not (Test-FullCubeElement @($elements)[0])) {
        return [pscustomobject]@{ isRisk = $true; reason = 'single block model element is not a full cube'; path = $blockModelPath }
    }

    return [pscustomobject]@{ isRisk = $false; reason = 'single full-cube element'; path = $blockModelPath }
}

function Test-ModelChainHasDisplay([string]$ModelRef, [System.Collections.Generic.HashSet[string]]$Visited) {
    $normalized = Normalize-ResourceRef $ModelRef 'thaumcraft'
    if ([string]::IsNullOrWhiteSpace($normalized)) { return $false }
    if (-not $Visited.Add($normalized)) { return $false }
    if ($normalized -match '^minecraft:block/') { return $true }
    if ($normalized -notmatch '^thaumcraft:block/') { return $false }

    $modelPath = Get-ModelPathFromRef $normalized 'block'
    $model = Read-JsonFile $modelPath
    if ($null -eq $model) { return $false }
    if (Has-JsonProperty $model 'display') { return $true }

    $parent = [string](Get-JsonPropertyValue $model 'parent')
    if ([string]::IsNullOrWhiteSpace($parent)) { return $false }
    return Test-ModelChainHasDisplay $parent $Visited
}

function Test-LegacyIntentionalFlatItem([string]$Id) {
    $modelCandidates = New-Object System.Collections.Generic.List[string]
    $modelCandidates.Add($Id)
    $separator = $Id.LastIndexOf('_')
    if ($separator -gt 0) {
        $modelCandidates.Add($Id.Substring(0, $separator))
    }
    foreach ($candidate in $modelCandidates) {
        $legacyItemModelPath = Join-Path $LegacyItemModelRoot ($candidate + '.json')
        $legacyItemModel = Read-JsonFile $legacyItemModelPath
        if ($null -ne $legacyItemModel) {
            $legacyParent = Normalize-ResourceRef ([string](Get-JsonPropertyValue $legacyItemModel 'parent')) 'minecraft'
            if ($legacyParent -match '^minecraft:item/(generated|handheld|handheld_rod)$') {
                return $true
            }
        }
    }

    $legacyBlockstatePath = Join-Path $LegacyBlockstateRoot ($Id + '.json')
    $legacyBlockstate = Read-Text $legacyBlockstatePath
    return -not [string]::IsNullOrWhiteSpace($legacyBlockstate) `
        -and $legacyBlockstate -match '"inventory"' `
        -and $legacyBlockstate -match '"model"\s*:\s*"forge:item-layer"'
}

function Get-RegisteredItems([string]$SourceText) {
    $items = New-Object System.Collections.Generic.List[object]
    $byField = @{}
    $lines = $SourceText -split "`r?`n"

    for ($i = 0; $i -lt $lines.Count; $i++) {
        $line = $lines[$i]
        if ($line -notmatch 'public\s+static\s+final\s+Supplier<(?<type>[^>]+)>\s+(?<field>[A-Z0-9_]+)\s*=\s*(?<expr>.+);') { continue }
        $type = $Matches['type'].Trim()
        $field = $Matches['field'].Trim()
        $expr = $Matches['expr'].Trim()
        $id = $null
        $isBlockItem = $type -match 'BlockItem'
        $catalog = $field.StartsWith('CATALOG_PLACEHOLDER_')
        $alias = $null

        if ($expr -match 'blockItem\("(?<id>[^"]+)"') {
            $id = $Matches['id']
            $isBlockItem = $true
        } elseif ($expr -match 'simpleItem\("(?<id>[^"]+)"') {
            $id = $Matches['id']
        } elseif ($expr -match 'ITEMS\.register\("(?<id>[^"]+)"') {
            $id = $Matches['id']
        } elseif ($expr -match '^(?<alias>[A-Z0-9_]+)$') {
            $alias = $Matches['alias']
            if ($byField.ContainsKey($alias)) {
                $id = $byField[$alias].id
                $isBlockItem = [bool]$byField[$alias].isBlockItem
            }
        }

        if ([string]::IsNullOrWhiteSpace($id)) { continue }

        $item = [pscustomobject]@{
            id = $id
            field = $field
            type = $type
            expression = $expr
            alias = $alias
            isBlockItem = $isBlockItem
            catalogPlaceholder = $catalog
            sourceLine = $i + 1
        }
        $items.Add($item) | Out-Null
        $byField[$field] = $item
    }

    return $items
}

function Get-PlaceholderIds([string]$SourceText) {
    $ids = New-Object System.Collections.Generic.HashSet[string]
    $caseMatches = [regex]::Matches($SourceText, 'case\s+(?<labels>[^\r\n]+?)\s*->\s*(?<ctor>new\s+ItemLegacyPlaceholder|legacyMagicPlaceholder)')
    foreach ($match in $caseMatches) {
        $labels = $match.Groups['labels'].Value
        foreach ($label in [regex]::Matches($labels, '"([^"]+)"')) {
            [void]$ids.Add($label.Groups[1].Value)
        }
    }
    return $ids
}

$source = Read-Text $TCItemsPath
$items = Get-RegisteredItems $source
$placeholderIds = Get-PlaceholderIds $source
$results = New-Object System.Collections.Generic.List[object]

$seenIds = New-Object System.Collections.Generic.HashSet[string]
foreach ($item in $items) {
    if (-not $seenIds.Add($item.id)) { continue }

    $modelPath = Join-Path $ItemModelRoot ($item.id + '.json')
    if (-not (Test-Path $modelPath)) {
        Add-Result $results 'item' $item.id 'item_model_exists' 'ITEM_VISUAL_MISSING' $modelPath 'Registered item has no assets/thaumcraft/models/item/<id>.json model.' 'error'
    } else {
        Add-Result $results 'item' $item.id 'item_model_exists' 'ITEM_VISUAL_PASS' $modelPath 'Registered item model exists.' 'info'
    }

    $model = Read-JsonFile $modelPath
    if ($null -ne $model) {
        $parent = [string](Get-JsonPropertyValue $model 'parent')
        if (-not [string]::IsNullOrWhiteSpace($parent)) {
            $normalizedParent = Normalize-ResourceRef $parent 'minecraft'
            if ($normalizedParent -match '^minecraft:(item|block)/' -and -not (Is-AllowedGenericMinecraftParent $normalizedParent)) {
                if (Is-ExpectedSpecificMinecraftParent $item.id $normalizedParent) {
                    Add-Result $results 'item' $item.id 'vanilla_parent_reference' 'ITEM_VISUAL_PASS' $modelPath "Specific vanilla parent is the explicit legacy-flattening alias for this id: $normalizedParent." 'info'
                } else {
                    Add-Result $results 'item' $item.id 'vanilla_parent_reference' 'ITEM_VISUAL_REVIEW_NEEDED' $modelPath "Item model inherits specific vanilla parent $normalizedParent; visible thaumcraft item may be using a vanilla icon/model placeholder." 'warning'
                }
            } else {
                Add-Result $results 'item' $item.id 'vanilla_parent_reference' 'ITEM_VISUAL_PASS' $modelPath "No specific vanilla parent placeholder detected; parent=$parent." 'info'
            }
        }

        $vanillaTextures = @(Get-TextureRefs $model | Where-Object { (Normalize-ResourceRef $_ 'thaumcraft') -match '^minecraft:(item|block)/' })
        if ($vanillaTextures.Count -gt 0) {
            Add-Result $results 'item' $item.id 'vanilla_texture_reference' 'ITEM_VISUAL_REVIEW_NEEDED' $modelPath ("Item model uses vanilla texture reference(s): " + ($vanillaTextures -join ', ') + '. This can appear as a vanilla/default placeholder instead of a Thaumcraft item.') 'warning'
        } else {
            Add-Result $results 'item' $item.id 'vanilla_texture_reference' 'ITEM_VISUAL_PASS' $modelPath 'No direct minecraft:item or minecraft:block texture reference detected.' 'info'
        }

        if ($item.isBlockItem) {
            $parentNorm = Normalize-ResourceRef $parent 'thaumcraft'
            if ($parentNorm -match '^thaumcraft:block/') {
                $risk = Test-LikelyCustomOrNonFullBlockModel $parentNorm
                $hasDisplay = (Has-JsonProperty $model 'display') -or
                        (Test-ModelChainHasDisplay $parentNorm (New-Object 'System.Collections.Generic.HashSet[string]'))
                if ($risk.isRisk -and -not $hasDisplay) {
                    Add-Result $results 'item' $item.id 'blockitem_custom_geometry_display' 'ITEM_VISUAL_REVIEW_NEEDED' $modelPath "BlockItem inherits likely custom/non-full block model without explicit item display transforms; risk of 2D/front-view or bad hand/GUI icon. $($risk.reason)." 'warning'
                } else {
                    Add-Result $results 'item' $item.id 'blockitem_custom_geometry_display' 'ITEM_VISUAL_PASS' $modelPath "BlockItem custom-geometry display risk not detected. modelChainHasDisplay=$hasDisplay; $($risk.reason)." 'info'
                }
            } else {
                if ($parentNorm -match '^minecraft:item/generated$|^minecraft:item/handheld') {
                    if (Test-LegacyIntentionalFlatItem $item.id) {
                        Add-Result $results 'item' $item.id 'blockitem_custom_geometry_display' 'ITEM_VISUAL_PASS' $modelPath "Flat BlockItem icon is intentional: the legacy item model or inventory blockstate also uses an item-layer/generated model." 'info'
                    } else {
                        Add-Result $results 'item' $item.id 'blockitem_custom_geometry_display' 'ITEM_VISUAL_REVIEW_NEEDED' $modelPath "Registered BlockItem uses a flat item parent instead of a block model parent without matching legacy item-layer evidence; verify this is not a front-view placeholder." 'warning'
                    }
                } else {
                    Add-Result $results 'item' $item.id 'blockitem_custom_geometry_display' 'ITEM_VISUAL_PASS' $modelPath "BlockItem does not inherit a thaumcraft block model; parent=$parent." 'info'
                }
            }
        }
    }

    if ($placeholderIds.Contains($item.id)) {
        Add-Result $results 'item' $item.id 'placeholder_implementation' 'ITEM_VISUAL_REVIEW_NEEDED' $TCItemsPath 'Registered item is created through ItemLegacyPlaceholder or legacyMagicPlaceholder; verify whether it should remain visible/use placeholder visuals or be replaced by the real ported item.' 'warning'
    } else {
        Add-Result $results 'item' $item.id 'placeholder_implementation' 'ITEM_VISUAL_PASS' $TCItemsPath 'No ItemLegacyPlaceholder constructor detected for this item id.' 'info'
    }

    if ($item.catalogPlaceholder) {
        Add-Result $results 'item' $item.id 'catalog_bridge_placeholder' 'ITEM_VISUAL_REVIEW_NEEDED' $TCItemsPath "TCItems field $($item.field) is a catalog bridge placeholder registration; verify it is not treated as finished gameplay/visual content." 'warning'
    } else {
        Add-Result $results 'item' $item.id 'catalog_bridge_placeholder' 'ITEM_VISUAL_PASS' $TCItemsPath 'Not a catalog bridge placeholder field.' 'info'
    }
}

$groups = $results | Group-Object subcheck | ForEach-Object {
    [pscustomobject]@{
        subcheck = $_.Name
        rows = $_.Count
        pass = @($_.Group | Where-Object { $_.status -eq 'ITEM_VISUAL_PASS' }).Count
        reviewNeeded = @($_.Group | Where-Object { $_.status -eq 'ITEM_VISUAL_REVIEW_NEEDED' }).Count
        missing = @($_.Group | Where-Object { $_.status -eq 'ITEM_VISUAL_MISSING' }).Count
    }
}

$summary = [pscustomobject]@{
    rows = $results.Count
    pass = @($results | Where-Object { $_.status -eq 'ITEM_VISUAL_PASS' }).Count
    reviewNeeded = @($results | Where-Object { $_.status -eq 'ITEM_VISUAL_REVIEW_NEEDED' }).Count
    missing = @($results | Where-Object { $_.status -eq 'ITEM_VISUAL_MISSING' }).Count
    uniqueItems = @($seenIds).Count
    bySubcheck = $groups
}

$report = [pscustomobject]@{
    schemaVersion = 1
    generatedAt = (Get-Date).ToString('o')
    scope = 'all registered TCItems entries, not only creative tab entries'
    summary = $summary
    results = $results
}

$report | ConvertTo-Json -Depth 12 | Set-Content -Encoding UTF8 -Path $JsonReport

$md = New-Object System.Collections.Generic.List[string]
$md.Add('# Item visual asset parity audit') | Out-Null
$md.Add('') | Out-Null
$md.Add('Scope: all registered `TCItems` entries. This intentionally does not use the creative tab as its source of truth.') | Out-Null
$md.Add('') | Out-Null
$md.Add('## Summary') | Out-Null
$md.Add('') | Out-Null
$md.Add("- Rows: $($summary.rows)") | Out-Null
$md.Add("- Pass: $($summary.pass)") | Out-Null
$md.Add("- Review needed: $($summary.reviewNeeded)") | Out-Null
$md.Add("- Missing: $($summary.missing)") | Out-Null
$md.Add("- Unique items: $($summary.uniqueItems)") | Out-Null
$md.Add('') | Out-Null
$md.Add('## Non-pass rows') | Out-Null
$md.Add('') | Out-Null
$md.Add('| kind | id | subcheck | status | severity | evidence | path |') | Out-Null
$md.Add('|---|---|---|---|---|---|---|') | Out-Null
foreach ($row in ($results | Where-Object { $_.status -ne 'ITEM_VISUAL_PASS' } | Sort-Object subcheck, id)) {
    $evidence = ([string]$row.evidence).Replace('|', '\|')
    $path = ([string]$row.path).Replace('|', '\|')
    $md.Add("| $($row.kind) | $($row.id) | $($row.subcheck) | $($row.status) | $($row.severity) | $evidence | $path |") | Out-Null
}
$md | Set-Content -Encoding UTF8 -Path $MdReport

Write-Host "Item visual asset parity report: $MdReport"
Write-Host "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), missing=$($summary.missing), uniqueItems=$($summary.uniqueItems)"

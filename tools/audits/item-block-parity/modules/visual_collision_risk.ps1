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
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_visual_collision_risk_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_visual_collision_risk_report.md" }

$port = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$assetsRoot = Join-Path $portPath "src/main/resources/assets/thaumcraft"
$javaRoot = Join-Path $portPath "src/main/java"
$tcBlocksPath = Join-Path $portPath "src/main/java/thaumcraft/common/registry/TCBlocks.java"
$tcBlocksText = if (Test-Path -LiteralPath $tcBlocksPath -PathType Leaf) { Get-Content -Raw -LiteralPath $tcBlocksPath } else { "" }

$javaClassTextBySimpleName = @{}
if (Test-Path -LiteralPath $javaRoot -PathType Container) {
    foreach ($file in @(Get-ChildItem -LiteralPath $javaRoot -Recurse -Filter "*.java" -File)) {
        $simple = [System.IO.Path]::GetFileNameWithoutExtension($file.Name)
        if (-not $javaClassTextBySimpleName.ContainsKey($simple)) {
            $javaClassTextBySimpleName[$simple] = Get-Content -Raw -LiteralPath $file.FullName
        }
    }
}

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}
function Read-JsonFileOrNull([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { return $null }
    try { return Get-Content -Raw -LiteralPath $Path | ConvertFrom-Json } catch { return $null }
}
function Get-JsonPropertyNames($Object) {
    if ($null -eq $Object) { return @() }
    return @($Object.PSObject.Properties | ForEach-Object { $_.Name } | Sort-Object -Unique)
}
function Get-Array($Value) {
    if ($null -eq $Value) { return @() }
    if ($Value -is [System.Array]) { return @($Value) }
    return @($Value)
}
function Test-ArrayEquals([object[]]$Value, [double[]]$Expected) {
    if ($Value.Count -ne $Expected.Count) { return $false }
    for ($i = 0; $i -lt $Expected.Count; $i++) {
        if ([math]::Abs(([double]$Value[$i]) - $Expected[$i]) -gt 0.001D) { return $false }
    }
    return $true
}
function Normalize-RotationValue($Value) {
    if ($null -eq $Value) { return 0 }
    $number = [int]$Value
    $number = $number % 360
    if ($number -lt 0) { $number += 360 }
    return $number
}
function Get-BlockDeclarationChunk([string]$Id) {
    if ([string]::IsNullOrWhiteSpace($tcBlocksText)) { return "" }
    $pattern = 'public\s+static\s+final\s+Supplier<Block>\s+[A-Z0-9_]+\s*=\s*BLOCKS\.register\(\s*"' + [regex]::Escape($Id) + '"(?<chunk>.*?)(?=public\s+static\s+final\s+Supplier<Block>|private\s+TCBlocks|\z)'
    $match = [regex]::Match($tcBlocksText, $pattern, [System.Text.RegularExpressions.RegexOptions]::Singleline)
    if ($match.Success) { return $match.Groups["chunk"].Value }
    return ""
}
function Get-PortClassText($Entry) {
    if ($null -eq $Entry -or [string]::IsNullOrWhiteSpace([string]$Entry.portClassFile)) { return "" }
    $path = Join-Path $portPath ([string]$Entry.portClassFile)
    if (Test-Path -LiteralPath $path -PathType Leaf) { return Get-Content -Raw -LiteralPath $path }
    return ""
}
function Get-SimpleClassName([string]$ClassName) {
    if ([string]::IsNullOrWhiteSpace($ClassName)) { return "" }
    $value = $ClassName -replace '\$.*$', ''
    if ($value.Contains(".")) { return $value.Split(".")[-1] }
    return $value
}
function Get-TCBlocksMethodBody([string]$MethodName) {
    if ([string]::IsNullOrWhiteSpace($MethodName) -or [string]::IsNullOrWhiteSpace($tcBlocksText)) { return "" }
    $pattern = '(?m)\b(?:private|public)\s+static\s+[A-Za-z0-9_.$<>?\s,]+\s+' + [regex]::Escape($MethodName) + '\s*\([^)]*\)\s*\{'
    $match = [regex]::Match($tcBlocksText, $pattern)
    if (-not $match.Success) { return "" }
    $start = $match.Index + $match.Length - 1
    $depth = 0
    for ($i = $start; $i -lt $tcBlocksText.Length; $i++) {
        $ch = $tcBlocksText[$i]
        if ($ch -eq '{') { $depth++ }
        elseif ($ch -eq '}') {
            $depth--
            if ($depth -eq 0) { return $tcBlocksText.Substring($match.Index, ($i - $match.Index + 1)) }
        }
    }
    return ""
}
function Get-FactoryMethodNamesFromDeclaration([string]$DeclarationChunk) {
    if ([string]::IsNullOrWhiteSpace($DeclarationChunk)) { return @() }
    $names = [System.Collections.Generic.List[string]]::new()
    foreach ($match in [regex]::Matches($DeclarationChunk, '->\s*(?:new\s+)?(?<name>[A-Za-z_][A-Za-z0-9_]*)\s*\(')) {
        $name = $match.Groups["name"].Value
        if ($name -notin @("new", "Block", "StairBlock", "SlabBlock")) { $names.Add($name) }
    }
    foreach ($match in [regex]::Matches($DeclarationChunk, '\b(?<name>stoneStairBlock|woodStairBlock|stoneSlabBlock|woodSlabBlock|tallowCandleBlock|plantBlock|saplingBlock|crystalPlaceholder|tableBlock|researchTableBlock|crucibleBlock|fluxGooBlock|inlayBlock|stabilizerBlock|infusionPedestalBlock|alembicBlock|wardedJarBlock|nitorBlock)\s*\(')) {
        $names.Add($match.Groups["name"].Value)
    }
    return @($names | Select-Object -Unique)
}
function Get-ResolvedSourceInfo($Entry, [string]$DeclarationChunk, [string]$ClassText) {
    $factoryBodies = [System.Collections.Generic.List[string]]::new()
    $classTexts = [System.Collections.Generic.List[string]]::new()
    if (-not [string]::IsNullOrWhiteSpace($ClassText)) { $classTexts.Add($ClassText) }

    $pending = [System.Collections.Generic.Queue[string]]::new()
    foreach ($name in @(Get-FactoryMethodNamesFromDeclaration $DeclarationChunk)) { $pending.Enqueue($name) }
    $seen = [System.Collections.Generic.HashSet[string]]::new()

    while ($pending.Count -gt 0) {
        $name = $pending.Dequeue()
        if ([string]::IsNullOrWhiteSpace($name)) { continue }
        if (-not $seen.Add($name)) { continue }
        $body = Get-TCBlocksMethodBody $name
        if ([string]::IsNullOrWhiteSpace($body)) { continue }
        $factoryBodies.Add($body)
        foreach ($match in [regex]::Matches($body, '\b(?<name>stoneStairBlock|woodStairBlock|stoneSlabBlock|woodSlabBlock|tallowCandleBlock|plantBlock|saplingBlock|crystalPlaceholder|tableBlock|researchTableBlock|crucibleBlock|fluxGooBlock|inlayBlock|stabilizerBlock|infusionPedestalBlock|alembicBlock|wardedJarBlock|nitorBlock)\s*\(')) {
            $pending.Enqueue($match.Groups["name"].Value)
        }
        foreach ($match in [regex]::Matches($body, '\bnew\s+(?<class>[A-Za-z0-9_.$]+)\s*\(')) {
            $simple = Get-SimpleClassName $match.Groups["class"].Value
            if ($javaClassTextBySimpleName.ContainsKey($simple)) { $classTexts.Add($javaClassTextBySimpleName[$simple]) }
        }
    }

    if ($Entry.declaredClass) {
        $simple = Get-SimpleClassName ([string]$Entry.declaredClass)
        if ($javaClassTextBySimpleName.ContainsKey($simple)) { $classTexts.Add($javaClassTextBySimpleName[$simple]) }
    }

    $factorySource = (@($factoryBodies) -join "`n")
    $resolvedClassSource = (@($classTexts | Select-Object -Unique) -join "`n")
    $combined = @($DeclarationChunk, $factorySource, $resolvedClassSource) -join "`n"
    return [pscustomobject][ordered]@{
        factoryMethods = @($seen | Sort-Object)
        factorySource = $factorySource
        classSource = $resolvedClassSource
        combinedSource = $combined
    }
}
function Test-BuiltInShapeBlock($Entry, [string]$CombinedSource) {
    $text = ""
    if ($Entry.declaredClass) { $text += " $($Entry.declaredClass)" }
    if ($Entry.portExtends) { $text += " $($Entry.portExtends)" }
    if ($Entry.portImplements) { $text += " $(@($Entry.portImplements) -join ' ')" }
    $text += " $CombinedSource"
    return $text -match '\b(StairBlock|SlabBlock|FenceBlock|WallBlock|DoorBlock|TrapDoorBlock|FenceGateBlock|PaneBlock|stoneStairBlock|woodStairBlock|stoneSlabBlock|woodSlabBlock)\b'
}
function Test-BuiltInOcclusionBlock($Entry, [string]$CombinedSource) {
    return Test-BuiltInShapeBlock $Entry $CombinedSource
}

$modelCache = @{}
function Get-ModelAnalysis([string]$ModelRef) {
    if ([string]::IsNullOrWhiteSpace($ModelRef)) { return $null }
    if ($modelCache.ContainsKey($ModelRef)) { return $modelCache[$ModelRef] }

    $modelPath = Join-Path $assetsRoot "models/$ModelRef.json"
    $relativePath = if (Test-Path -LiteralPath $modelPath -PathType Leaf) { ConvertTo-RelativeRepoPath $modelPath } else { "assets/thaumcraft/models/$ModelRef.json" }
    $json = Read-JsonFileOrNull $modelPath
    if ($null -eq $json) {
        $analysis = [pscustomobject][ordered]@{
            modelRef = $ModelRef
            path = $relativePath
            exists = $false
            parent = "<missing>"
            hasElements = $false
            elementCount = 0
            hasFullCubeElement = $false
            parentImpliesFullCube = $false
            parentImpliesBuiltInShape = $false
            likelyNonFull = $false
            hasNorthProjection = $false
        }
        $modelCache[$ModelRef] = $analysis
        return $analysis
    }

    $parent = if ($json.parent) { [string]$json.parent } else { "<none>" }
    $parentLower = $parent.ToLowerInvariant()
    $elements = @(Get-Array $json.elements)
    $hasFullCubeElement = $false
    $hasNorthProjection = $false

    foreach ($element in $elements) {
        $from = @(Get-Array $element.from)
        $to = @(Get-Array $element.to)
        if ($from.Count -ne 3 -or $to.Count -ne 3) { continue }
        $isFullCube = (Test-ArrayEquals $from @(0.0D, 0.0D, 0.0D)) -and (Test-ArrayEquals $to @(16.0D, 16.0D, 16.0D))
        if ($isFullCube) { $hasFullCubeElement = $true }

        $minX = [double]$from[0]
        $minY = [double]$from[1]
        $minZ = [double]$from[2]
        $maxX = [double]$to[0]
        $maxY = [double]$to[1]
        $maxZ = [double]$to[2]
        $widthX = $maxX - $minX
        $heightY = $maxY - $minY
        $narrowProjection = ($minZ -le 0.001D -and $maxZ -le 4.001D -and $widthX -lt 14.0D -and $heightY -lt 14.0D -and -not $isFullCube)
        if ($narrowProjection) { $hasNorthProjection = $true }
    }

    $parentImpliesFullCube = $parentLower -match '(^|:)block/(cube|cube_all|cube_column|orientable|log|column)$'
    $parentImpliesBuiltInShape = $parentLower -match 'stairs|slab|cross|crop|torch|fence|wall|door|trapdoor|pane'
    $hasElements = $elements.Count -gt 0
    $likelyNonFull = $false
    if ($hasElements) {
        $likelyNonFull = -not $hasFullCubeElement
    } elseif ($parentImpliesBuiltInShape) {
        $likelyNonFull = $true
    } elseif ($parentImpliesFullCube) {
        $likelyNonFull = $false
    }

    $analysis = [pscustomobject][ordered]@{
        modelRef = $ModelRef
        path = $relativePath
        exists = $true
        parent = $parent
        hasElements = $hasElements
        elementCount = $elements.Count
        hasFullCubeElement = $hasFullCubeElement
        parentImpliesFullCube = $parentImpliesFullCube
        parentImpliesBuiltInShape = $parentImpliesBuiltInShape
        likelyNonFull = $likelyNonFull
        hasNorthProjection = $hasNorthProjection
    }
    $modelCache[$ModelRef] = $analysis
    return $analysis
}

function Add-ResultRow($Rows, [string]$Kind, [string]$Id, [string]$Subcheck, [string]$Status, [string]$Evidence, [string]$Path = "", $Details = $null) {
    $Rows.Add([pscustomobject][ordered]@{
        check = "visual_collision_risk"
        subcheck = $Subcheck
        kind = $Kind
        id = "thaumcraft:$Id"
        status = $Status
        path = $Path
        evidence = $Evidence
        details = $Details
    })
}
function Get-FacingVariantMap($BlockstateJson) {
    $map = @{}
    if ($null -eq $BlockstateJson -or $null -eq $BlockstateJson.variants) { return $map }
    foreach ($prop in $BlockstateJson.variants.PSObject.Properties) {
        $name = [string]$prop.Name
        $match = [regex]::Match($name, '(^|,)facing=(?<facing>north|south|east|west|up|down)(,|$)')
        if (-not $match.Success) { continue }
        $facing = $match.Groups["facing"].Value
        if (-not $map.ContainsKey($facing)) { $map[$facing] = [System.Collections.Generic.List[object]]::new() }
        $value = $prop.Value
        foreach ($variant in @(Get-Array $value)) { $map[$facing].Add($variant) }
    }
    return $map
}
function Test-FacingVariantRotation($Variants, [int]$ExpectedX, [int]$ExpectedY) {
    if ($null -eq $Variants -or @($Variants).Count -eq 0) { return $false }
    foreach ($variant in @($Variants)) {
        $x = Normalize-RotationValue $variant.x
        $y = Normalize-RotationValue $variant.y
        if ($x -ne $ExpectedX -or $y -ne $ExpectedY) { return $false }
    }
    return $true
}
function Test-SixDirectionFacing($Entry, [string]$ClassText, [string]$CombinedSource, $FacingMap) {
    if ($FacingMap.ContainsKey("up") -or $FacingMap.ContainsKey("down")) { return $true }
    $source = ""
    if ($ClassText) { $source += $ClassText }
    if ($CombinedSource) { $source += $CombinedSource }
    if ($Entry.declaredClass) { $source += " $($Entry.declaredClass)" }
    return $source -match 'BlockStateProperties\.FACING|DirectionProperty\s+FACING|DirectionProperty\s+[A-Z0-9_]+\s*=\s*BlockStateProperties\.FACING'
}

$blockEntriesById = @{}
foreach ($entry in @($port.entries | Where-Object { $_.kind -eq "block" })) {
    if (-not $blockEntriesById.ContainsKey([string]$entry.registryId)) { $blockEntriesById[[string]$entry.registryId] = $entry }
}

$results = [System.Collections.Generic.List[object]]::new()
$modelSummaries = [System.Collections.Generic.List[object]]::new()

foreach ($entry in @($port.entries | Sort-Object kind, registryId)) {
    if ($entry.kind -eq "block") {
        $classText = Get-PortClassText $entry
        $declarationChunk = Get-BlockDeclarationChunk ([string]$entry.registryId)
        $sourceInfo = Get-ResolvedSourceInfo $entry $declarationChunk $classText
        $combinedSource = [string]$sourceInfo.combinedSource
        $hasBuiltInShape = Test-BuiltInShapeBlock $entry $combinedSource
        $hasBuiltInOcclusion = Test-BuiltInOcclusionBlock $entry $combinedSource
        $hasShapeContract = ($combinedSource -match '\bgetShape\s*\(|\bgetCollisionShape\s*\(|\bVoxelShape\b|\bShapes\.or\b|\bShapes\.empty\b|\.noCollission\s*\(') -or $hasBuiltInShape
        $hasOcclusionContract = ($combinedSource -match '\bgetOcclusionShape\s*\(|\buseShapeForLightOcclusion\s*\(|\bnoOcclusion\b|\bnoCollission\b') -or $hasBuiltInOcclusion
        $referencedModels = @($entry.resources.referencedBlockModels | Where-Object { $_ } | Sort-Object -Unique)
        if ($referencedModels.Count -eq 0) { continue }

        $anyNorthProjection = $false
        foreach ($modelRef in $referencedModels) {
            $analysis = Get-ModelAnalysis ([string]$modelRef)
            if ($null -eq $analysis) { continue }
            $modelSummaries.Add($analysis)
            if (-not $analysis.exists) {
                Add-ResultRow $results "block" $entry.registryId "block_model_geometry_read" "VISUAL_MODEL_MISSING" "Referenced block model JSON missing or invalid: $modelRef" $analysis.path $analysis
                continue
            }
            if ($analysis.hasNorthProjection) { $anyNorthProjection = $true }

            if ($analysis.likelyNonFull) {
                if ($hasShapeContract) {
                    Add-ResultRow $results "block" $entry.registryId "non_full_model_shape_contract" "VISUAL_EVIDENCE" "Likely non-full model has detected shape contract, no-collision contract, factory class evidence, or built-in shape class; model=$modelRef" $analysis.path $analysis
                } else {
                    Add-ResultRow $results "block" $entry.registryId "non_full_model_shape_contract" "VISUAL_REVIEW_NEEDED" "Likely non-full model lacks detected getShape/getCollisionShape/VoxelShape/noCollission or built-in shape evidence; may behave like full-cube collision; model=$modelRef" $analysis.path $analysis
                }

                if ($hasOcclusionContract) {
                    Add-ResultRow $results "block" $entry.registryId "non_full_model_occlusion_contract" "VISUAL_EVIDENCE" "Likely non-full model has detected noOcclusion/getOcclusionShape/useShapeForLightOcclusion/noCollission, factory class evidence, or built-in occlusion evidence; model=$modelRef" $analysis.path $analysis
                } else {
                    Add-ResultRow $results "block" $entry.registryId "non_full_model_occlusion_contract" "VISUAL_REVIEW_NEEDED" "Likely non-full model lacks detected noOcclusion/getOcclusionShape/useShapeForLightOcclusion evidence; may cause face-culling/x-ray artifacts; model=$modelRef" $analysis.path $analysis
                }
            } else {
                Add-ResultRow $results "block" $entry.registryId "non_full_model_shape_contract" "VISUAL_EVIDENCE" "Model appears full-cube or vanilla full-cube-parented; custom shape is not required for current port model; model=$modelRef" $analysis.path $analysis
                Add-ResultRow $results "block" $entry.registryId "non_full_model_occlusion_contract" "VISUAL_EVIDENCE" "Model appears full-cube or vanilla full-cube-parented; custom occlusion contract is not required for current port model; model=$modelRef" $analysis.path $analysis
            }
        }

        if ($anyNorthProjection) {
            $blockstatePath = Join-Path $assetsRoot "blockstates/$($entry.registryId).json"
            $blockstateJson = Read-JsonFileOrNull $blockstatePath
            $relativeBlockstatePath = if (Test-Path -LiteralPath $blockstatePath -PathType Leaf) { ConvertTo-RelativeRepoPath $blockstatePath } else { "assets/thaumcraft/blockstates/$($entry.registryId).json" }
            $facingMap = Get-FacingVariantMap $blockstateJson
            if ($facingMap.Count -gt 0) {
                $sixDirection = Test-SixDirectionFacing $entry $classText $combinedSource $facingMap
                $rotationProblems = [System.Collections.Generic.List[string]]::new()
                $expectations = @{
                    north = @(0, 0)
                    south = @(0, 180)
                    east = @(0, 90)
                    west = @(0, 270)
                }
                if ($sixDirection) {
                    $expectations["down"] = @(90, 0)
                    $expectations["up"] = @(270, 0)
                }
                foreach ($key in @($expectations.Keys | Sort-Object)) {
                    if (-not $facingMap.ContainsKey($key)) {
                        $rotationProblems.Add("$key=<missing>")
                        continue
                    }
                    $expected = $expectations[$key]
                    if (-not (Test-FacingVariantRotation $facingMap[$key] $expected[0] $expected[1])) {
                        $seen = @($facingMap[$key] | ForEach-Object { "x=$(Normalize-RotationValue $_.x),y=$(Normalize-RotationValue $_.y)" } | Select-Object -Unique) -join ";"
                        $rotationProblems.Add("$key=$seen expected x=$($expected[0]),y=$($expected[1])")
                    }
                }
                if ($rotationProblems.Count -eq 0) {
                    Add-ResultRow $results "block" $entry.registryId "directional_model_facing_rotation" "VISUAL_EVIDENCE" "North-default projection/nozzle model has expected facing rotations for $(if ($sixDirection) { 'six-direction' } else { 'horizontal' }) placement" $relativeBlockstatePath
                } else {
                    Add-ResultRow $results "block" $entry.registryId "directional_model_facing_rotation" "VISUAL_REVIEW_NEEDED" "North-default projection/nozzle model has suspicious facing rotations: $($rotationProblems -join ', ')" $relativeBlockstatePath
                }
            }
        }
    }
    elseif ($entry.kind -eq "item" -and $entry.blockItem) {
        $itemModelPath = Join-Path $assetsRoot "models/item/$($entry.registryId).json"
        $relativeItemModelPath = if (Test-Path -LiteralPath $itemModelPath -PathType Leaf) { ConvertTo-RelativeRepoPath $itemModelPath } else { "assets/thaumcraft/models/item/$($entry.registryId).json" }
        $json = Read-JsonFileOrNull $itemModelPath
        if ($null -eq $json) {
            Add-ResultRow $results "item" $entry.registryId "block_item_custom_model_display" "VISUAL_MODEL_MISSING" "BlockItem model JSON missing or invalid" $relativeItemModelPath
            continue
        }
        $displaySlots = @(Get-JsonPropertyNames $json.display)
        $parent = if ($json.parent) { [string]$json.parent } else { "" }
        if ($displaySlots.Count -gt 0) {
            Add-ResultRow $results "item" $entry.registryId "block_item_custom_model_display" "VISUAL_EVIDENCE" "BlockItem has explicit display transforms: $($displaySlots -join ', ')" $relativeItemModelPath
            continue
        }
        $blockEntry = if ($blockEntriesById.ContainsKey([string]$entry.registryId)) { $blockEntriesById[[string]$entry.registryId] } else { $null }
        $blockBuiltInShape = $false
        if ($null -ne $blockEntry) {
            $blockClassText = Get-PortClassText $blockEntry
            $blockDeclaration = Get-BlockDeclarationChunk ([string]$blockEntry.registryId)
            $blockSourceInfo = Get-ResolvedSourceInfo $blockEntry $blockDeclaration $blockClassText
            $blockBuiltInShape = Test-BuiltInShapeBlock $blockEntry ([string]$blockSourceInfo.combinedSource)
        }
        if ($parent -match '^thaumcraft:block/(?<name>.+)$') {
            $analysis = Get-ModelAnalysis ("block/$($Matches['name'])")
            if ($null -eq $analysis -or -not $analysis.exists) {
                Add-ResultRow $results "item" $entry.registryId "block_item_custom_model_display" "VISUAL_MODEL_MISSING" "BlockItem parent model missing or invalid: parent=$parent" $relativeItemModelPath
                continue
            }
            if ($blockBuiltInShape) {
                Add-ResultRow $results "item" $entry.registryId "block_item_custom_model_display" "VISUAL_EVIDENCE" "BlockItem uses vanilla built-in shape block parent=$parent; vanilla item display is acceptable unless screenshot review proves otherwise" $relativeItemModelPath $analysis
            } elseif ($analysis.likelyNonFull -or $analysis.hasNorthProjection) {
                Add-ResultRow $results "item" $entry.registryId "block_item_custom_model_display" "VISUAL_REVIEW_NEEDED" "BlockItem inherits likely custom/non-full block model parent=$parent but has no explicit display transforms; GUI/hand item may render as incorrect flat/front view" $relativeItemModelPath $analysis
            } else {
                Add-ResultRow $results "item" $entry.registryId "block_item_custom_model_display" "VISUAL_EVIDENCE" "BlockItem inherits full-cube/vanilla-like block model parent=$parent; default item display is acceptable unless screenshot review proves otherwise" $relativeItemModelPath $analysis
            }
        } else {
            Add-ResultRow $results "item" $entry.registryId "block_item_custom_model_display" "VISUAL_EVIDENCE" "BlockItem model parent=$parent is not a thaumcraft block model requiring custom display transform evidence" $relativeItemModelPath
        }
    }
}

$orderedResults = @($results | Sort-Object status, kind, id, subcheck, path)
$summaryBySubcheck = @($orderedResults | Group-Object subcheck | Sort-Object Name | ForEach-Object {
    [pscustomobject][ordered]@{
        subcheck = $_.Name
        rows = $_.Count
        evidence = @($_.Group | Where-Object status -eq "VISUAL_EVIDENCE").Count
        reviewNeeded = @($_.Group | Where-Object status -eq "VISUAL_REVIEW_NEEDED").Count
        missing = @($_.Group | Where-Object status -eq "VISUAL_MODEL_MISSING").Count
    }
})
$report = [ordered]@{
    schemaVersion = 3
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    selectedChecks = @("visual_collision_risk")
    policy = "Report-only visual/collision risk scan. Full-cube or vanilla-shape models are current-port consistency evidence rows; review-needed rows focus on likely non-full/custom models whose Java shape, occlusion, blockstate rotation, or item display contracts are not mechanically evidenced. This does not claim legacy shape parity."
    summary = [ordered]@{
        rows = $orderedResults.Count
        evidence = @($orderedResults | Where-Object status -eq "VISUAL_EVIDENCE").Count
        reviewNeeded = @($orderedResults | Where-Object status -eq "VISUAL_REVIEW_NEEDED").Count
        missing = @($orderedResults | Where-Object status -eq "VISUAL_MODEL_MISSING").Count
        models = @($modelSummaries | Sort-Object modelRef -Unique).Count
        bySubcheck = @($summaryBySubcheck)
    }
    models = @($modelSummaries | Sort-Object modelRef -Unique)
    results = $orderedResults
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputMarkdown) | Out-Null
$report | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block visual collision risk report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Rows | Evidence | Review needed | Missing models | Models inspected |")
$lines.Add("|---:|---:|---:|---:|---:|")
$lines.Add("| $($report.summary.rows) | $($report.summary.evidence) | $($report.summary.reviewNeeded) | $($report.summary.missing) | $($report.summary.models) |")
$lines.Add("")
$lines.Add("## By subcheck")
$lines.Add("")
$lines.Add("| Subcheck | Rows | Evidence | Review needed | Missing |")
$lines.Add("|---|---:|---:|---:|---:|")
foreach ($row in $summaryBySubcheck) { $lines.Add("| $($row.subcheck) | $($row.rows) | $($row.evidence) | $($row.reviewNeeded) | $($row.missing) |") }
$lines.Add("")
$lines.Add("## Review-needed and missing rows")
$lines.Add("")
$lines.Add("| Kind | ID | Subcheck | Status | Path | Evidence |")
$lines.Add("|---|---|---|---|---|---|")
foreach ($row in $orderedResults | Where-Object { $_.status -ne "VISUAL_EVIDENCE" }) {
    $safeEvidence = if ($row.evidence) { $row.evidence.Replace("|", "\|") } else { "" }
    $lines.Add("| $($row.kind) | ``$($row.id)`` | $($row.subcheck) | $($row.status) | ``$($row.path)`` | $safeEvidence |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output "Visual collision risk report: $OutputMarkdown"
Write-Output "Rows=$($report.summary.rows), evidence=$($report.summary.evidence), reviewNeeded=$($report.summary.reviewNeeded), missing=$($report.summary.missing), models=$($report.summary.models)"
[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$LegacyManifestPath,
    [Parameter(Mandatory = $true)][string]$PortManifestPath,
    [string]$LegacyRoot = "02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master",
    [string]$PortRoot = "05_neoforge_port",
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
$legacyPath = Join-Path $RepoRoot $LegacyRoot
$portPath = Join-Path $RepoRoot $PortRoot
if (-not (Test-Path -LiteralPath $legacyPath -PathType Container)) { throw "Legacy root not found: $legacyPath" }
if (-not (Test-Path -LiteralPath $portPath -PathType Container)) { throw "Port root not found: $portPath" }
if (-not (Test-Path -LiteralPath $LegacyManifestPath -PathType Leaf)) { throw "Legacy manifest not found: $LegacyManifestPath" }
if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw "Port manifest not found: $PortManifestPath" }
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_legacy_shape_parity_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_legacy_shape_parity_report.md" }

$legacy = Get-Content -Raw -LiteralPath $LegacyManifestPath | ConvertFrom-Json
$port = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$assetsRoot = Join-Path $portPath "src/main/resources/assets/thaumcraft"
$javaRoot = Join-Path $portPath "src/main/java"
$tcBlocksPath = Join-Path $portPath "src/main/java/thaumcraft/common/registry/TCBlocks.java"
$tcBlocksText = if (Test-Path -LiteralPath $tcBlocksPath -PathType Leaf) { Get-Content -Raw -LiteralPath $tcBlocksPath } else { "" }

$portClassTextBySimpleName = @{}
if (Test-Path -LiteralPath $javaRoot -PathType Container) {
    foreach ($file in @(Get-ChildItem -LiteralPath $javaRoot -Recurse -Filter "*.java" -File)) {
        $simple = [System.IO.Path]::GetFileNameWithoutExtension($file.Name)
        if (-not $portClassTextBySimpleName.ContainsKey($simple)) { $portClassTextBySimpleName[$simple] = Get-Content -Raw -LiteralPath $file.FullName }
    }
}

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}
function Read-TextOrEmpty([string]$Path) {
    if (Test-Path -LiteralPath $Path -PathType Leaf) { return Get-Content -Raw -LiteralPath $Path }
    return ""
}
function Read-JsonFileOrNull([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { return $null }
    try { return Get-Content -Raw -LiteralPath $Path | ConvertFrom-Json } catch { return $null }
}
function Get-Array($Value) {
    if ($null -eq $Value) { return @() }
    if ($Value -is [System.Array]) { return @($Value) }
    return @($Value)
}
function Get-SimpleClassName([string]$ClassName) {
    if ([string]::IsNullOrWhiteSpace($ClassName)) { return "" }
    $value = $ClassName -replace '\$.*$', ''
    if ($value.Contains(".")) { return $value.Split(".")[-1] }
    return $value
}
function Get-BlockDeclarationChunk([string]$Id) {
    if ([string]::IsNullOrWhiteSpace($tcBlocksText)) { return "" }
    $pattern = 'public\s+static\s+final\s+Supplier<Block>\s+[A-Z0-9_]+\s*=\s*BLOCKS\.register\(\s*"' + [regex]::Escape($Id) + '"(?<chunk>.*?)(?=public\s+static\s+final\s+Supplier<Block>|private\s+TCBlocks|\z)'
    $match = [regex]::Match($tcBlocksText, $pattern, [System.Text.RegularExpressions.RegexOptions]::Singleline)
    if ($match.Success) { return $match.Groups["chunk"].Value }
    return ""
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
    foreach ($match in [regex]::Matches($DeclarationChunk, '\b(?<name>[a-z][A-Za-z0-9_]*)\s*\(')) {
        $names.Add($match.Groups["name"].Value)
    }
    return @($names | Where-Object { $_ -notin @("register", "get", "defaultBlockState", "of", "ofFullCopy", "strength", "sound", "lightLevel") } | Select-Object -Unique)
}
function Get-ResolvedPortSource([string]$Id, $Entry) {
    $declaration = Get-BlockDeclarationChunk $Id
    $classTexts = [System.Collections.Generic.List[string]]::new()
    if ($Entry.portClassFile) { $classTexts.Add((Read-TextOrEmpty (Join-Path $portPath ([string]$Entry.portClassFile)))) }
    if ($Entry.declaredClass) {
        $simple = Get-SimpleClassName ([string]$Entry.declaredClass)
        if ($portClassTextBySimpleName.ContainsKey($simple)) { $classTexts.Add($portClassTextBySimpleName[$simple]) }
    }
    $factoryBodies = [System.Collections.Generic.List[string]]::new()
    $pending = [System.Collections.Generic.Queue[string]]::new()
    foreach ($name in @(Get-FactoryMethodNamesFromDeclaration $declaration)) { $pending.Enqueue($name) }
    $seen = [System.Collections.Generic.HashSet[string]]::new()
    while ($pending.Count -gt 0) {
        $name = $pending.Dequeue()
        if ([string]::IsNullOrWhiteSpace($name)) { continue }
        if (-not $seen.Add($name)) { continue }
        $body = Get-TCBlocksMethodBody $name
        if ([string]::IsNullOrWhiteSpace($body)) { continue }
        $factoryBodies.Add($body)
        foreach ($nested in @(Get-FactoryMethodNamesFromDeclaration $body)) { $pending.Enqueue($nested) }
        foreach ($match in [regex]::Matches($body, '\bnew\s+(?<class>[A-Za-z0-9_.$]+)\s*\(')) {
            $simple = Get-SimpleClassName $match.Groups["class"].Value
            if ($portClassTextBySimpleName.ContainsKey($simple)) { $classTexts.Add($portClassTextBySimpleName[$simple]) }
        }
    }
    return @($declaration, (@($factoryBodies) -join "`n"), (@($classTexts | Select-Object -Unique) -join "`n")) -join "`n"
}
function Test-ArrayEquals([object[]]$Value, [double[]]$Expected) {
    if ($Value.Count -ne $Expected.Count) { return $false }
    for ($i = 0; $i -lt $Expected.Count; $i++) {
        if ([math]::Abs(([double]$Value[$i]) - $Expected[$i]) -gt 0.001D) { return $false }
    }
    return $true
}
function Get-ModelShapeSummary([string[]]$ModelRefs) {
    $hasModel = $false
    $hasLikelyNonFull = $false
    $hasFullCube = $false
    $models = [System.Collections.Generic.List[object]]::new()
    foreach ($modelRef in @($ModelRefs | Where-Object { $_ } | Sort-Object -Unique)) {
        $modelPath = Join-Path $assetsRoot "models/$modelRef.json"
        $relativePath = if (Test-Path -LiteralPath $modelPath -PathType Leaf) { ConvertTo-RelativeRepoPath $modelPath } else { "assets/thaumcraft/models/$modelRef.json" }
        $json = Read-JsonFileOrNull $modelPath
        if ($null -eq $json) { continue }
        $hasModel = $true
        $parent = if ($json.parent) { [string]$json.parent } else { "<none>" }
        $parentLower = $parent.ToLowerInvariant()
        $elements = @(Get-Array $json.elements)
        $hasFullElement = $false
        foreach ($element in $elements) {
            $from = @(Get-Array $element.from)
            $to = @(Get-Array $element.to)
            if ($from.Count -ne 3 -or $to.Count -ne 3) { continue }
            if ((Test-ArrayEquals $from @(0.0D,0.0D,0.0D)) -and (Test-ArrayEquals $to @(16.0D,16.0D,16.0D))) { $hasFullElement = $true }
        }
        $parentFull = $parentLower -match '(^|:)block/(cube|cube_all|cube_column|orientable|log|column)$'
        $parentBuiltIn = $parentLower -match 'stairs|slab|cross|crop|torch|fence|wall|door|trapdoor|pane'
        $likelyNonFull = $false
        if ($elements.Count -gt 0) { $likelyNonFull = -not $hasFullElement }
        elseif ($parentBuiltIn) { $likelyNonFull = $true }
        elseif ($parentFull) { $likelyNonFull = $false }
        if ($likelyNonFull) { $hasLikelyNonFull = $true }
        if ($hasFullElement -or $parentFull) { $hasFullCube = $true }
        $models.Add([pscustomobject][ordered]@{ modelRef = $modelRef; path = $relativePath; parent = $parent; elementCount = $elements.Count; likelyNonFull = $likelyNonFull; fullCube = ($hasFullElement -or $parentFull) })
    }
    return [pscustomobject][ordered]@{ hasModel = $hasModel; hasLikelyNonFull = $hasLikelyNonFull; hasFullCube = $hasFullCube; models = @($models) }
}
function Classify-LegacyShape($Entry, [string]$Source) {
    $text = " $Source "
    if ($Entry.legacyClass) { $text += " $($Entry.legacyClass)" }
    if ($Entry.legacyExtends) { $text += " $($Entry.legacyExtends)" }
    if ($Entry.behaviorClues) { $text += " $(@($Entry.behaviorClues) -join ' ')" }
    if ($Entry.methodPresence) { $text += " $(@($Entry.methodPresence) -join ' ')" }
    $vanillaShape = $text -match '\b(BlockStairs|BlockSlab|BlockFence|BlockWall|BlockDoor|BlockPane)\b' -or $Entry.registryId -match '^(stairs_|slab_)'
    $nonFull = $text -match 'isFullCube\s*\([^)]*\)\s*\{\s*return\s+false|isOpaqueCube\s*\([^)]*\)\s*\{\s*return\s+false|getBoundingBox\s*\(|getCollisionBoundingBox\s*\(|getSelectedBoundingBox\s*\(|AxisAlignedBB|setBlockBounds|BlockFaceShape\.UNDEFINED|getBlockFaceShape\s*\('
    $renderer = [bool]($Entry.hasRenderer -or ($text -match 'TileEntitySpecialRenderer|\bTESR\b|getRenderType\s*\(|MODEL|ENTITYBLOCK_ANIMATED|ISmartBlockModel|IItemRenderer'))
    $tile = [bool]$Entry.hasTileEntity
    if ($vanillaShape) { return [pscustomobject][ordered]@{ shape = "vanilla_shape"; nonFull = $true; specialRenderer = $renderer; evidence = "Legacy class/id indicates vanilla stair/slab-style shape." } }
    if ($nonFull -or $renderer) { return [pscustomobject][ordered]@{ shape = "non_full_or_custom"; nonFull = $true; specialRenderer = $renderer; evidence = "Legacy source has non-full/custom shape or renderer clues." } }
    if ($tile) { return [pscustomobject][ordered]@{ shape = "tile_unknown"; nonFull = $false; specialRenderer = $renderer; evidence = "Legacy TileEntity-backed block without direct static shape clue; verify visual/collision parity." } }
    if ([string]::IsNullOrWhiteSpace($Source)) { return [pscustomobject][ordered]@{ shape = "unknown"; nonFull = $false; specialRenderer = $false; evidence = "Legacy source class not available." } }
    return [pscustomobject][ordered]@{ shape = "likely_full_cube"; nonFull = $false; specialRenderer = $false; evidence = "No legacy non-full/custom shape clue detected." }
}
function Classify-PortShape($Entry, [string]$Source, $ModelSummary) {
    $text = " $Source "
    if ($Entry.declaredClass) { $text += " $($Entry.declaredClass)" }
    if ($Entry.portExtends) { $text += " $($Entry.portExtends)" }
    $builtIn = $text -match '\b(StairBlock|SlabBlock|FenceBlock|WallBlock|DoorBlock|TrapDoorBlock|FenceGateBlock|PaneBlock|stoneStairBlock|woodStairBlock|stoneSlabBlock|woodSlabBlock)\b' -or $Entry.registryId -match '^(stairs_|slab_)'
    $shapeContract = $text -match '\bgetShape\s*\(|\bgetCollisionShape\s*\(|\bVoxelShape\b|\bShapes\.empty\b|\bShapes\.or\b|\.noCollission\s*\('
    $occlusionContract = $text -match '\bgetOcclusionShape\s*\(|\buseShapeForLightOcclusion\s*\(|\bnoOcclusion\b|\bnoCollission\b'
    if ($builtIn) { return [pscustomobject][ordered]@{ shape = "vanilla_shape"; hasParityShapeEvidence = $true; appearsDefaultFullCube = $false; evidence = "Port uses vanilla built-in shape class/factory." } }
    if ($ModelSummary.hasLikelyNonFull -or $shapeContract -or $occlusionContract) { return [pscustomobject][ordered]@{ shape = "non_full_or_custom"; hasParityShapeEvidence = [bool]($shapeContract -or $occlusionContract -or $ModelSummary.hasLikelyNonFull); appearsDefaultFullCube = $false; evidence = "Port has non-full model and/or shape/occlusion source evidence." } }
    if ($ModelSummary.hasFullCube) { return [pscustomobject][ordered]@{ shape = "likely_full_cube"; hasParityShapeEvidence = $true; appearsDefaultFullCube = $true; evidence = "Port model appears full-cube/default and has no custom shape clue." } }
    return [pscustomobject][ordered]@{ shape = "unknown"; hasParityShapeEvidence = $false; appearsDefaultFullCube = $false; evidence = "Port shape could not be classified from model/source clues." }
}
function Add-Row($Rows, [string]$Id, [string]$Status, [string]$Severity, [string]$Evidence, $LegacyShape, $PortShape, $Details) {
    $Rows.Add([pscustomobject][ordered]@{
        check = "legacy_shape_parity"
        kind = "block"
        id = "thaumcraft:$Id"
        status = $Status
        severity = $Severity
        evidence = $Evidence
        legacyShape = $LegacyShape.shape
        portShape = $PortShape.shape
        details = $Details
    })
}

$legacyBlocks = @{}
foreach ($entry in @($legacy.entries | Where-Object { $_.kind -eq "block" })) {
    if (-not $legacyBlocks.ContainsKey([string]$entry.registryId)) { $legacyBlocks[[string]$entry.registryId] = $entry }
}
$rows = [System.Collections.Generic.List[object]]::new()

foreach ($portEntry in @($port.entries | Where-Object { $_.kind -eq "block" } | Sort-Object registryId)) {
    $id = [string]$portEntry.registryId
    $legacyEntry = if ($legacyBlocks.ContainsKey($id)) { $legacyBlocks[$id] } else { $null }
    if ($null -eq $legacyEntry) {
        $emptyLegacy = [pscustomobject][ordered]@{ shape = "missing"; nonFull = $false; specialRenderer = $false; evidence = "No matching legacy block manifest entry." }
        $portShape = [pscustomobject][ordered]@{ shape = "unknown"; hasParityShapeEvidence = $false; appearsDefaultFullCube = $false; evidence = "Port entry exists but legacy entry is missing." }
        Add-Row $rows $id "LEGACY_SHAPE_REVIEW_NEEDED" "review" "No matching legacy block entry; shape parity cannot be established." $emptyLegacy $portShape ([pscustomobject][ordered]@{ port = $portEntry })
        continue
    }

    $legacySource = if ($legacyEntry.legacyClassFile) { Read-TextOrEmpty (Join-Path $legacyPath ([string]$legacyEntry.legacyClassFile)) } else { "" }
    $portSource = Get-ResolvedPortSource $id $portEntry
    $modelSummary = Get-ModelShapeSummary @($portEntry.resources.referencedBlockModels)
    $legacyShape = Classify-LegacyShape $legacyEntry $legacySource
    $portShape = Classify-PortShape $portEntry $portSource $modelSummary
    $details = [pscustomobject][ordered]@{
        legacyClass = $legacyEntry.legacyClass
        legacyClassFile = $legacyEntry.legacyClassFile
        legacyEvidence = $legacyShape.evidence
        portClass = $portEntry.declaredClass
        portClassFile = $portEntry.portClassFile
        portEvidence = $portShape.evidence
        referencedModels = @($portEntry.resources.referencedBlockModels)
        modelSummary = $modelSummary
    }

    if (($legacyShape.shape -eq "non_full_or_custom" -or $legacyShape.shape -eq "vanilla_shape") -and $portShape.appearsDefaultFullCube) {
        Add-Row $rows $id "LEGACY_SHAPE_REVIEW_NEEDED" "review" "Legacy shape is $($legacyShape.shape), but port appears to be default/full-cube; possible false-safe port shape." $legacyShape $portShape $details
    } elseif ($legacyShape.shape -eq "tile_unknown" -and $portShape.appearsDefaultFullCube) {
        Add-Row $rows $id "LEGACY_SHAPE_REVIEW_NEEDED" "review" "Legacy TileEntity block shape is not mechanically classified, while port appears default/full-cube; review before accepting parity." $legacyShape $portShape $details
    } elseif ($legacyShape.shape -eq "unknown" -or $portShape.shape -eq "unknown") {
        Add-Row $rows $id "LEGACY_SHAPE_REVIEW_NEEDED" "review" "Shape parity cannot be mechanically classified from legacy/port evidence." $legacyShape $portShape $details
    } else {
        Add-Row $rows $id "LEGACY_SHAPE_PASS" "info" "Legacy shape classification '$($legacyShape.shape)' is compatible with port shape classification '$($portShape.shape)' at static-audit level." $legacyShape $portShape $details
    }
}

$orderedRows = @($rows | Sort-Object status, id)
$summaryByLegacyShape = @($orderedRows | Group-Object legacyShape | Sort-Object Name | ForEach-Object { [pscustomobject][ordered]@{ legacyShape = $_.Name; rows = $_.Count; reviewNeeded = @($_.Group | Where-Object status -eq "LEGACY_SHAPE_REVIEW_NEEDED").Count; pass = @($_.Group | Where-Object status -eq "LEGACY_SHAPE_PASS").Count } })
$summary = [ordered]@{
    rows = $orderedRows.Count
    pass = @($orderedRows | Where-Object status -eq "LEGACY_SHAPE_PASS").Count
    reviewNeeded = @($orderedRows | Where-Object status -eq "LEGACY_SHAPE_REVIEW_NEEDED").Count
    legacyNonFullOrCustom = @($orderedRows | Where-Object { $_.legacyShape -in @("non_full_or_custom", "vanilla_shape") }).Count
    portDefaultFullCubeRisks = @($orderedRows | Where-Object { $_.status -eq "LEGACY_SHAPE_REVIEW_NEEDED" -and $_.evidence -match "default/full-cube" }).Count
    byLegacyShape = @($summaryByLegacyShape)
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    selectedChecks = @("legacy_shape_parity")
    policy = "Report-only legacy-vs-port shape classification audit. Pass means static clues are compatible; review-needed means legacy non-full/custom/TESR/tile shape evidence may not be represented by the current port model/shape implementation. This audit prevents full-cube current-port models from being treated as legacy parity without legacy evidence."
    summary = $summary
    results = $orderedRows
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputMarkdown) | Out-Null
$report | ConvertTo-Json -Depth 20 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block legacy shape parity report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Rows | Pass | Review needed | Legacy non-full/custom | Port default/full-cube risks |")
$lines.Add("|---:|---:|---:|---:|---:|")
$lines.Add("| $($summary.rows) | $($summary.pass) | $($summary.reviewNeeded) | $($summary.legacyNonFullOrCustom) | $($summary.portDefaultFullCubeRisks) |")
$lines.Add("")
$lines.Add("## By legacy shape")
$lines.Add("")
$lines.Add("| Legacy shape | Rows | Pass | Review needed |")
$lines.Add("|---|---:|---:|---:|")
foreach ($row in $summaryByLegacyShape) { $lines.Add("| $($row.legacyShape) | $($row.rows) | $($row.pass) | $($row.reviewNeeded) |") }
$lines.Add("")
$lines.Add("## Review-needed rows")
$lines.Add("")
$lines.Add("| ID | Legacy shape | Port shape | Evidence |")
$lines.Add("|---|---|---|---|")
foreach ($row in $orderedRows | Where-Object { $_.status -ne "LEGACY_SHAPE_PASS" }) {
    $safeEvidence = if ($row.evidence) { $row.evidence.Replace("|", "\|") } else { "" }
    $lines.Add("| ``$($row.id)`` | $($row.legacyShape) | $($row.portShape) | $safeEvidence |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output "Legacy shape parity report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), legacyNonFullOrCustom=$($summary.legacyNonFullOrCustom), portDefaultFullCubeRisks=$($summary.portDefaultFullCubeRisks)"
[CmdletBinding()]
param(
    [string]$RepoRoot,
    [string]$PortRoot = "05_neoforge_port",
    [string]$OutputPath
)

$ErrorActionPreference = "Stop"

if (-not $RepoRoot) {
    $RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "../../..")).Path
}
$RepoRoot = (Resolve-Path $RepoRoot).Path
$portPath = Join-Path $RepoRoot $PortRoot
if (-not (Test-Path -LiteralPath $portPath -PathType Container)) {
    throw "Port root not found: $portPath"
}
if (-not $OutputPath) {
    $OutputPath = Join-Path $RepoRoot "tools/reports/local/item-block-parity/port_manifest.json"
}

$blocksRelative = "src/main/java/thaumcraft/common/registry/TCBlocks.java"
$itemsRelative = "src/main/java/thaumcraft/common/registry/TCItems.java"
$langRelative = "src/main/resources/assets/thaumcraft/lang/en_us.json"
$blockEntitiesRelative = "src/main/java/thaumcraft/common/registry/TCBlockEntities.java"
$menusRelative = "src/main/java/thaumcraft/common/registry/TCMenus.java"
$blocksPath = Join-Path $portPath $blocksRelative
$itemsPath = Join-Path $portPath $itemsRelative
$langPath = Join-Path $portPath $langRelative
foreach ($required in @($blocksPath, $itemsPath, $langPath)) {
    if (-not (Test-Path -LiteralPath $required -PathType Leaf)) { throw "Required port file not found: $required" }
}

function ConvertTo-RelativePath([string]$FullPath) {
    return [System.IO.Path]::GetRelativePath($portPath, $FullPath).Replace("\", "/")
}
function Get-SimpleClassName([string]$ClassName) {
    if ([string]::IsNullOrWhiteSpace($ClassName)) { return "" }
    $value = $ClassName -replace '\$.*$', ''
    if ($value.Contains(".")) { return $value.Split(".")[-1] }
    return $value
}
function Get-PortClassEvidence {
    param(
        [string]$ClassName,
        [hashtable]$ClassIndex,
        [hashtable]$ContentByRelativePath
    )
    $simple = Get-SimpleClassName $ClassName
    if ([string]::IsNullOrWhiteSpace($simple) -or -not $ClassIndex.ContainsKey($simple)) {
        return [ordered]@{
            classFile = $null
            packageName = $null
            hasClassFile = $false
            extends = $null
            implements = @()
            methodPresence = @()
            behaviorClues = @()
            hasBlockEntityClue = $false
            hasInventoryClue = $false
            hasTickingClue = $false
            hasMenuClue = $false
            hasCapabilityClue = $false
            hasNetworkingClue = $false
            hasRendererClue = $false
            confidence = "class_file_missing"
        }
    }
    $relative = $ClassIndex[$simple]
    $text = $ContentByRelativePath[$relative]
    $packageMatch = [regex]::Match($text, 'package\s+(?<package>[A-Za-z0-9_.]+)\s*;')
    $extendsMatch = [regex]::Match($text, 'class\s+' + [regex]::Escape($simple) + '\s+extends\s+(?<extends>[A-Za-z0-9_.$]+)')
    $implementsMatch = [regex]::Match($text, 'class\s+' + [regex]::Escape($simple) + '.*?implements\s+(?<implements>[A-Za-z0-9_.,\s$<>]+)\s*\{', [System.Text.RegularExpressions.RegexOptions]::Singleline)
    $implements = @()
    if ($implementsMatch.Success) { $implements = @($implementsMatch.Groups["implements"].Value.Split(",") | ForEach-Object { $_.Trim() } | Where-Object { $_ }) }
    $methodNames = @("newBlockEntity", "getTicker", "use", "onRemove", "neighborChanged", "tick", "saveAdditional", "loadAdditional", "getUpdateTag", "getUpdatePacket", "getCapability", "openMenu", "quickMoveStack", "stillValid")
    $methodPresence = @($methodNames | Where-Object { $text -match ('\b' + [regex]::Escape($_) + '\b') })
    $hasBlockEntity = ($text -match '\bEntityBlock\b|\bBlockEntity\b|\bnewBlockEntity\b|\bBlockEntityTicker\b|\bgetTicker\b')
    $hasInventory = ($text -match '\bItemStackHandler\b|\bIItemHandler\b|\bSlotItemHandler\b|\bContainerData\b|\binventory\b|\bitems\b')
    $hasTicking = ($text -match '\btick\s*\(|\bserverTick\s*\(|\bclientTick\s*\(|\bgetTicker\b')
    $hasMenu = ($text -match '\bMenuProvider\b|\bAbstractContainerMenu\b|\bMenuType\b|\bopenMenu\b|\bSimpleMenuProvider\b')
    $hasCapability = ($text -match '\bRegisterCapabilitiesEvent\b|\bCapabilities\.\b|\bIItemHandler\b|\bItemHandler\b|\bgetCapability\b')
    $hasNetworking = ($text -match '\bCustomPacketPayload\b|\bPayloadRegistrar\b|\bStreamCodec\b|\bPacketDistributor\b|\bRegistryFriendlyByteBuf\b')
    $hasRenderer = ($text -match '\bBlockEntityRenderer\b|\bEntityRenderer\b|\bRenderType\b|\bPoseStack\b|\bModel[A-Za-z0-9_]+\b|\bclient\.render\b')
    $behaviorClues = [System.Collections.Generic.List[string]]::new()
    if ($text -match '\bFACING\b|\bDirectionProperty\b|\bHorizontalDirectionalBlock\b|getStateForPlacement') { $behaviorClues.Add("facing") }
    if ($text -match '\bENABLED\b|\bPOWERED\b|\bredstone\b|neighborChanged') { $behaviorClues.Add("enabled_or_redstone") }
    if ($hasBlockEntity) { $behaviorClues.Add("block_entity") }
    if ($hasInventory) { $behaviorClues.Add("inventory") }
    if ($hasTicking) { $behaviorClues.Add("ticking") }
    if ($hasMenu) { $behaviorClues.Add("menu") }
    if ($hasCapability) { $behaviorClues.Add("capability") }
    if ($hasNetworking) { $behaviorClues.Add("networking") }
    if ($hasRenderer) { $behaviorClues.Add("renderer_or_special_model") }
    return [ordered]@{
        classFile = $relative
        packageName = if ($packageMatch.Success) { $packageMatch.Groups["package"].Value } else { $null }
        hasClassFile = $true
        extends = if ($extendsMatch.Success) { $extendsMatch.Groups["extends"].Value } else { $null }
        implements = @($implements)
        methodPresence = @($methodPresence)
        behaviorClues = @($behaviorClues | Select-Object -Unique)
        hasBlockEntityClue = [bool]$hasBlockEntity
        hasInventoryClue = [bool]$hasInventory
        hasTickingClue = [bool]$hasTicking
        hasMenuClue = [bool]$hasMenu
        hasCapabilityClue = [bool]$hasCapability
        hasNetworkingClue = [bool]$hasNetworking
        hasRendererClue = [bool]$hasRenderer
        confidence = "port_source_class_scan"
    }
}

$blocksText = Get-Content -Raw -LiteralPath $blocksPath
$itemsText = Get-Content -Raw -LiteralPath $itemsPath
$lang = Get-Content -Raw -LiteralPath $langPath | ConvertFrom-Json -AsHashtable

$javaFiles = @(Get-ChildItem -LiteralPath (Join-Path $portPath "src/main/java") -Recurse -Filter "*.java" -File)
$classIndex = @{}
$contentByRelativePath = @{}
foreach ($file in $javaFiles) {
    $relative = ConvertTo-RelativePath $file.FullName
    $content = Get-Content -Raw -LiteralPath $file.FullName
    $contentByRelativePath[$relative] = $content
    $className = [System.IO.Path]::GetFileNameWithoutExtension($file.Name)
    if (-not $classIndex.ContainsKey($className)) { $classIndex[$className] = $relative }
}

$blockSymbols = @{}
$blockRecords = @{}
$blockDeclarationPattern = 'public\s+static\s+final\s+Supplier<Block>\s+(?<symbol>[A-Z0-9_]+)\s*=\s*BLOCKS\.register\(\s*"(?<id>[a-z][a-z0-9_]*)"\s*,\s*(?<expression>.*?)\);'
$blockMatches = @([regex]::Matches($blocksText, $blockDeclarationPattern, [Text.RegularExpressions.RegexOptions]::Singleline))
$blockIds = @($blockMatches | ForEach-Object { $_.Groups["id"].Value })
foreach ($match in $blockMatches) {
    $id = $match.Groups["id"].Value
    $symbol = $match.Groups["symbol"].Value
    $expression = $match.Groups["expression"].Value.Trim()
    $classMatch = [regex]::Match($expression, 'new\s+(?<class>[A-Za-z0-9_.$]+)\s*\(')
    $blockSymbols[$id] = $symbol
    $blockRecords[$id] = [ordered]@{
        symbol = $symbol
        expression = $expression
        declaredClass = if ($classMatch.Success) { $classMatch.Groups["class"].Value } else { $null }
        sourceFile = $blocksRelative
    }
}

$itemRecords = @{}
$itemIds = [System.Collections.Generic.List[string]]::new()
$itemDeclarationPattern = 'public\s+static\s+final\s+Supplier<(?<type>[^>]+)>\s+(?<symbol>[A-Z0-9_]+)\s*=\s*(?<expression>.*?);'
foreach ($match in [regex]::Matches($itemsText, $itemDeclarationPattern, [Text.RegularExpressions.RegexOptions]::Singleline)) {
    $idMatch = [regex]::Match($match.Groups["expression"].Value, '"(?<id>[a-z][a-z0-9_]*)"')
    if (-not $idMatch.Success) { continue }
    $id = $idMatch.Groups["id"].Value
    $expression = $match.Groups["expression"].Value.Trim()
    $classMatch = [regex]::Match($expression, 'new\s+(?<class>[A-Za-z0-9_.$]+)\s*\(')
    $itemIds.Add($id)
    $itemRecords[$id] = [ordered]@{
        symbol = $match.Groups["symbol"].Value
        declaredType = $match.Groups["type"].Value.Trim()
        declaredClass = if ($classMatch.Success) { $classMatch.Groups["class"].Value } else { $null }
        expression = $expression
        blockItem = $match.Groups["type"].Value -match "BlockItem"
        sourceFile = $itemsRelative
    }
}

$blockEntityRegistry = [System.Collections.Generic.List[object]]::new()
$blockEntityByBlockSymbol = @{}
$blockEntityPath = Join-Path $portPath $blockEntitiesRelative
if (Test-Path -LiteralPath $blockEntityPath -PathType Leaf) {
    $beText = Get-Content -Raw -LiteralPath $blockEntityPath
    $pattern = 'public\s+static\s+final\s+Supplier<BlockEntityType<(?<type>[^>]+)>>\s+(?<symbol>[A-Z0-9_]+)\s*=\s*BLOCK_ENTITY_TYPES\.register\("(?<id>[a-z][a-z0-9_]*)"(?<chunk>.*?)(?=public\s+static|private\s+TCBlockEntities|\z)'
    foreach ($match in [regex]::Matches($beText, $pattern, [Text.RegularExpressions.RegexOptions]::Singleline)) {
        $chunk = $match.Groups["chunk"].Value
        $blockRefs = @([regex]::Matches($chunk, 'TCBlocks\.([A-Z0-9_]+)\.get\(\)') | ForEach-Object { $_.Groups[1].Value } | Select-Object -Unique)
        $entry = [pscustomobject][ordered]@{
            id = $match.Groups["id"].Value
            symbol = $match.Groups["symbol"].Value
            blockEntityClass = $match.Groups["type"].Value.Trim()
            blockSymbols = @($blockRefs)
            sourceFile = $blockEntitiesRelative
        }
        $blockEntityRegistry.Add($entry)
        foreach ($blockSymbol in $blockRefs) {
            if (-not $blockEntityByBlockSymbol.ContainsKey($blockSymbol)) { $blockEntityByBlockSymbol[$blockSymbol] = [System.Collections.Generic.List[object]]::new() }
            $blockEntityByBlockSymbol[$blockSymbol].Add($entry)
        }
    }
}

$menuRegistry = [System.Collections.Generic.List[object]]::new()
$menusPath = Join-Path $portPath $menusRelative
if (Test-Path -LiteralPath $menusPath -PathType Leaf) {
    $menuText = Get-Content -Raw -LiteralPath $menusPath
    $pattern = 'public\s+static\s+final\s+Supplier<MenuType<(?<type>[^>]+)>>\s+(?<symbol>[A-Z0-9_]+)\s*=\s*MENUS\.register\("(?<id>[a-z][a-z0-9_]*)"'
    foreach ($match in [regex]::Matches($menuText, $pattern, [Text.RegularExpressions.RegexOptions]::Singleline)) {
        $menuRegistry.Add([pscustomobject][ordered]@{
            id = $match.Groups["id"].Value
            symbol = $match.Groups["symbol"].Value
            menuClass = $match.Groups["type"].Value.Trim()
            sourceFile = $menusRelative
        })
    }
}

$capabilityEvidenceFiles = @($contentByRelativePath.GetEnumerator() | Where-Object { $_.Value -match 'RegisterCapabilitiesEvent|Capabilities\.|IItemHandler|ItemHandler' } | ForEach-Object { $_.Key } | Sort-Object)
$payloadEvidenceFiles = @($contentByRelativePath.GetEnumerator() | Where-Object { $_.Value -match 'CustomPacketPayload|PayloadRegistrar|StreamCodec|PacketDistributor|RegistryFriendlyByteBuf' } | ForEach-Object { $_.Key } | Sort-Object)

$assetsRoot = Join-Path $portPath "src/main/resources/assets/thaumcraft"
$dataRoot = Join-Path $portPath "src/main/resources/data/thaumcraft"
$entries = [System.Collections.Generic.List[object]]::new()

foreach ($id in @($blockSymbols.Keys | Sort-Object)) {
    $record = $blockRecords[$id]
    $classEvidence = Get-PortClassEvidence -ClassName $record.declaredClass -ClassIndex $classIndex -ContentByRelativePath $contentByRelativePath
    $blockstateRelative = "blockstates/$id.json"
    $blockstatePath = Join-Path $assetsRoot $blockstateRelative
    $referencedModels = @()
    $missingModels = @()
    if (Test-Path -LiteralPath $blockstatePath -PathType Leaf) {
        $blockstateText = Get-Content -Raw -LiteralPath $blockstatePath
        $referencedModels = @([regex]::Matches($blockstateText, '"model"\s*:\s*"thaumcraft:(?<model>[^"#]+)') | ForEach-Object { $_.Groups["model"].Value } | Sort-Object -Unique)
        $missingModels = @($referencedModels | Where-Object { -not (Test-Path -LiteralPath (Join-Path $assetsRoot "models/$_.json") -PathType Leaf) })
    }
    $hasBlockItem = $itemRecords.ContainsKey($id) -and $itemRecords[$id].blockItem
    $beRefs = @()
    if ($blockEntityByBlockSymbol.ContainsKey($record.symbol)) { $beRefs = @($blockEntityByBlockSymbol[$record.symbol]) }
    $menuRefs = @($menuRegistry | Where-Object { $_.id -eq $id -or ($id -like 'smelter_*' -and $_.id -eq 'smelter') })
    $entries.Add([pscustomobject][ordered]@{
        kind = "block"
        registryId = $id
        symbol = $record.symbol
        declaredClass = $record.declaredClass
        portClassFile = $classEvidence.classFile
        portPackage = $classEvidence.packageName
        portExtends = $classEvidence.extends
        portImplements = @($classEvidence.implements)
        registeredBlock = $true
        registeredItem = $hasBlockItem
        blockItem = $hasBlockItem
        blockEntities = @($beRefs | ForEach-Object { $_.id })
        blockEntityClasses = @($beRefs | ForEach-Object { $_.blockEntityClass } | Select-Object -Unique)
        hasBlockEntity = [bool]($beRefs.Count -gt 0 -or $classEvidence.hasBlockEntityClue)
        menus = @($menuRefs | ForEach-Object { $_.id })
        menuClasses = @($menuRefs | ForEach-Object { $_.menuClass } | Select-Object -Unique)
        hasMenu = [bool]($menuRefs.Count -gt 0 -or $classEvidence.hasMenuClue)
        hasInventory = [bool]$classEvidence.hasInventoryClue
        hasTicking = [bool]$classEvidence.hasTickingClue
        hasCapability = [bool]$classEvidence.hasCapabilityClue
        hasNetworking = [bool]$classEvidence.hasNetworkingClue
        hasRenderer = [bool]$classEvidence.hasRendererClue
        behaviorClues = @($classEvidence.behaviorClues)
        methodPresence = @($classEvidence.methodPresence)
        sourceFile = $record.sourceFile
        resources = [ordered]@{
            blockstate = Test-Path -LiteralPath $blockstatePath -PathType Leaf
            referencedBlockModels = $referencedModels
            missingBlockModels = $missingModels
            blockModelsResolved = (Test-Path -LiteralPath $blockstatePath -PathType Leaf) -and $missingModels.Count -eq 0
            itemModel = Test-Path -LiteralPath (Join-Path $assetsRoot "models/item/$id.json") -PathType Leaf
            langKey = $lang.ContainsKey("block.thaumcraft.$id")
            lootTable = Test-Path -LiteralPath (Join-Path $dataRoot "loot_table/blocks/$id.json") -PathType Leaf
        }
    })
}

foreach ($id in @($itemRecords.Keys | Sort-Object)) {
    $record = $itemRecords[$id]
    $classEvidence = Get-PortClassEvidence -ClassName $record.declaredClass -ClassIndex $classIndex -ContentByRelativePath $contentByRelativePath
    $entries.Add([pscustomobject][ordered]@{
        kind = "item"
        registryId = $id
        symbol = $record.symbol
        declaredType = $record.declaredType
        declaredClass = $record.declaredClass
        portClassFile = $classEvidence.classFile
        portPackage = $classEvidence.packageName
        portExtends = $classEvidence.extends
        portImplements = @($classEvidence.implements)
        registeredBlock = $blockSymbols.ContainsKey($id)
        registeredItem = $true
        blockItem = $record.blockItem
        hasInventory = [bool]$classEvidence.hasInventoryClue
        hasTicking = [bool]$classEvidence.hasTickingClue
        hasMenu = [bool]$classEvidence.hasMenuClue
        hasCapability = [bool]$classEvidence.hasCapabilityClue
        hasNetworking = [bool]$classEvidence.hasNetworkingClue
        hasRenderer = [bool]$classEvidence.hasRendererClue
        behaviorClues = @($classEvidence.behaviorClues)
        methodPresence = @($classEvidence.methodPresence)
        sourceFile = $record.sourceFile
        resources = [ordered]@{
            itemModel = Test-Path -LiteralPath (Join-Path $assetsRoot "models/item/$id.json") -PathType Leaf
            langKey = if ($record.blockItem) { $lang.ContainsKey("block.thaumcraft.$id") } else { $lang.ContainsKey("item.thaumcraft.$id") }
        }
    })
}

$usedSourceFiles = [System.Collections.Generic.HashSet[string]]::new()
foreach ($relativePath in @($blocksRelative, $itemsRelative, $langRelative, $blockEntitiesRelative, $menusRelative)) {
    $fullPath = Join-Path $portPath $relativePath
    if (Test-Path -LiteralPath $fullPath -PathType Leaf) { [void]$usedSourceFiles.Add($relativePath) }
}
foreach ($entry in $entries) { if ($entry.portClassFile) { [void]$usedSourceFiles.Add($entry.portClassFile) } }
foreach ($path in @($capabilityEvidenceFiles + $payloadEvidenceFiles)) { [void]$usedSourceFiles.Add($path) }

$sourceFiles = foreach ($relativePath in @($usedSourceFiles | Sort-Object)) {
    $fullPath = Join-Path $portPath $relativePath
    if (Test-Path -LiteralPath $fullPath -PathType Leaf) {
        [ordered]@{ path = $relativePath; sha256 = (Get-FileHash -Algorithm SHA256 -LiteralPath $fullPath).Hash.ToLowerInvariant() }
    }
}
$orderedEntries = @($entries | Sort-Object kind, registryId)
$duplicateBlocks = @($blockIds | Group-Object | Where-Object Count -gt 1 | ForEach-Object Name | Sort-Object)
$duplicateItems = @($itemIds | Group-Object | Where-Object Count -gt 1 | ForEach-Object Name | Sort-Object)
$manifest = [ordered]@{
    schemaVersion = 2
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    source = $PortRoot.Replace("\", "/")
    extractionPolicy = "Batch 4 live port manifest records registry identity, resource boundaries and source-level BlockEntity/menu/capability/networking behavior clues without claiming runtime parity."
    sourceFiles = @($sourceFiles)
    diagnostics = [ordered]@{ duplicateBlockIds = $duplicateBlocks; duplicateItemIds = $duplicateItems }
    summary = [ordered]@{
        entries = $orderedEntries.Count
        blocks = @($orderedEntries | Where-Object kind -eq "block").Count
        items = @($orderedEntries | Where-Object kind -eq "item").Count
        blockItems = @($orderedEntries | Where-Object { $_.kind -eq "item" -and $_.blockItem }).Count
        classFilesResolved = @($orderedEntries | Where-Object { $_.portClassFile }).Count
        blockEntityBackedBlocks = @($orderedEntries | Where-Object { $_.kind -eq "block" -and $_.hasBlockEntity }).Count
        menuLinkedEntries = @($orderedEntries | Where-Object hasMenu).Count
        capabilityEvidenceFiles = @($capabilityEvidenceFiles).Count
        payloadEvidenceFiles = @($payloadEvidenceFiles).Count
        blockEntityRegistryEntries = @($blockEntityRegistry).Count
        menuRegistryEntries = @($menuRegistry).Count
        sourceFilesFingerprinted = @($sourceFiles).Count
    }
    blockEntityRegistry = @($blockEntityRegistry | Sort-Object id)
    menuRegistry = @($menuRegistry | Sort-Object id)
    capabilityEvidenceFiles = @($capabilityEvidenceFiles)
    payloadEvidenceFiles = @($payloadEvidenceFiles)
    entries = $orderedEntries
}
$outputDirectory = Split-Path -Parent $OutputPath
New-Item -ItemType Directory -Force -Path $outputDirectory | Out-Null
$manifest | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $OutputPath -Encoding utf8NoBOM
Write-Output "Port live manifest: $OutputPath"
Write-Output "Blocks=$($manifest.summary.blocks), items=$($manifest.summary.items), blockItems=$($manifest.summary.blockItems), duplicateIds=$($duplicateBlocks.Count + $duplicateItems.Count), classFiles=$($manifest.summary.classFilesResolved), blockEntityBackedBlocks=$($manifest.summary.blockEntityBackedBlocks), menuLinked=$($manifest.summary.menuLinkedEntries), capabilityFiles=$($manifest.summary.capabilityEvidenceFiles), payloadFiles=$($manifest.summary.payloadEvidenceFiles)"

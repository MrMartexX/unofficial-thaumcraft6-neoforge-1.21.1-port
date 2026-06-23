[CmdletBinding()]
param(
    [string]$RepoRoot,
    [string]$LegacyRoot = "02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master",
    [string]$OutputPath
)

$ErrorActionPreference = "Stop"

if (-not $RepoRoot) {
    $RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "../../..")).Path
}
$RepoRoot = (Resolve-Path $RepoRoot).Path
$legacyPath = Join-Path $RepoRoot $LegacyRoot
if (-not (Test-Path -LiteralPath $legacyPath -PathType Container)) {
    throw "Primary legacy source root not found: $legacyPath"
}
if (-not $OutputPath) {
    $OutputPath = Join-Path $RepoRoot "tools/reports/local/item-block-parity/legacy_primary_manifest.json"
}

$requiredSourceFiles = @(
    "src/main/java/thaumcraft/common/config/ConfigBlocks.java",
    "src/main/java/thaumcraft/common/config/ConfigItems.java",
    "src/main/java/thaumcraft/api/blocks/BlocksTC.java",
    "src/main/java/thaumcraft/api/items/ItemsTC.java"
)
foreach ($relativePath in $requiredSourceFiles) {
    $fullPath = Join-Path $legacyPath $relativePath
    if (-not (Test-Path -LiteralPath $fullPath -PathType Leaf)) {
        throw "Required primary legacy source file not found: $fullPath"
    }
}

function ConvertTo-SnakeCase([string]$Value) {
    return ([regex]::Replace($Value, "(?<=[a-z0-9])([A-Z])", "_`$1")).ToLowerInvariant()
}

function ConvertTo-RelativePath([string]$FullPath) {
    return [System.IO.Path]::GetRelativePath($legacyPath, $FullPath).Replace("\", "/")
}

function Get-SimpleClassName([string]$ClassName) {
    if ([string]::IsNullOrWhiteSpace($ClassName)) { return "" }
    $value = $ClassName -replace '\$.*$', ''
    if ($value.Contains(".")) { return $value.Split(".")[-1] }
    return $value
}

function Get-SourceIdentity([string]$Line, [string]$Symbol) {
    $registryMatch = [regex]::Match($Line, 'setRegistryName\(\s*"(?:thaumcraft)"\s*,\s*"(?<id>[a-z][a-z0-9_]*)"')
    if ($registryMatch.Success) {
        return [ordered]@{ id = $registryMatch.Groups["id"].Value; confidence = "explicit_source" }
    }

    $quoted = @([regex]::Matches($Line, '"(?<id>[a-z][a-z0-9_]*)"') | ForEach-Object {
        $_.Groups["id"].Value
    } | Where-Object { $_ -ne "thaumcraft" })
    if ($quoted.Count -gt 0) {
        return [ordered]@{ id = $quoted[0]; confidence = "explicit_source" }
    }

    return [ordered]@{ id = ConvertTo-SnakeCase $Symbol; confidence = "inferred_symbol" }
}

function Add-Entry([System.Collections.Generic.List[object]]$Entries, [System.Collections.IDictionary]$Entry) {
    $existing = $Entries | Where-Object { $_.kind -eq $Entry.kind -and $_.registryId -eq $Entry.registryId } | Select-Object -First 1
    if ($null -eq $existing) {
        $Entries.Add([pscustomobject]$Entry)
        return
    }
    if ($existing.idConfidence -eq "inferred_symbol" -and $Entry.idConfidence -ne "inferred_symbol") {
        [void]$Entries.Remove($existing)
        $Entries.Add([pscustomobject]$Entry)
    }
}

function Find-QuotedVariants([string]$Line) {
    $quoted = @([regex]::Matches($Line, '"(?<id>[a-z][a-z0-9_]*)"') | ForEach-Object {
        $_.Groups["id"].Value
    } | Where-Object { $_ -ne "thaumcraft" })
    if ($quoted.Count -gt 1) { return @($quoted | Select-Object -Skip 1) }
    return @()
}

function Get-ClassEvidence {
    param(
        [string]$LegacyClass,
        [hashtable]$ClassIndex,
        [hashtable]$ContentByRelativePath
    )
    $simple = Get-SimpleClassName $LegacyClass
    if ([string]::IsNullOrWhiteSpace($simple) -or -not $ClassIndex.ContainsKey($simple)) {
        return [ordered]@{
            classFile = $null
            packageName = $null
            hasClassFile = $false
            extends = $null
            implements = @()
            methodPresence = @()
            propertyCalls = @()
            behaviorClues = @()
            hasTileEntityClue = $false
            hasInventoryClue = $false
            hasTickingClue = $false
            hasGuiClue = $false
            hasRendererClue = $false
            tileEntityClasses = @()
            confidence = "class_file_missing"
        }
    }

    $relative = $ClassIndex[$simple]
    $text = $ContentByRelativePath[$relative]
    $packageMatch = [regex]::Match($text, 'package\s+(?<package>[A-Za-z0-9_.]+)\s*;')
    $extendsMatch = [regex]::Match($text, 'class\s+' + [regex]::Escape($simple) + '\s+extends\s+(?<extends>[A-Za-z0-9_.$]+)')
    $implementsMatch = [regex]::Match($text, 'class\s+' + [regex]::Escape($simple) + '.*?implements\s+(?<implements>[A-Za-z0-9_.,\s$]+)\s*\{', [System.Text.RegularExpressions.RegexOptions]::Singleline)
    $implements = @()
    if ($implementsMatch.Success) {
        $implements = @($implementsMatch.Groups["implements"].Value.Split(",") | ForEach-Object { $_.Trim() } | Where-Object { $_ })
    }

    $methodNames = @(
        "getStateForPlacement", "damageDropped", "getRenderType", "createTileEntity", "hasTileEntity", "onBlockActivated",
        "breakBlock", "neighborChanged", "update", "updateEntity", "getDrops", "getSubItems", "addInformation",
        "onItemRightClick", "onItemUse", "onUpdate", "readFromNBT", "writeToNBT", "getCapability"
    )
    $methodPresence = @($methodNames | Where-Object { $text -match ('\b' + [regex]::Escape($_) + '\b') })

    $propertyNames = @("setHardness", "setResistance", "setSoundType", "setLightLevel", "setCreativeTab", "setHarvestLevel", "setTickRandomly")
    $propertyCalls = @($propertyNames | Where-Object { $text -match ('\b' + [regex]::Escape($_) + '\b') })

    $tileTokens = @([regex]::Matches($text, '\b(Tile[A-Za-z0-9_]+)\b') | ForEach-Object { $_.Groups[1].Value } | Select-Object -Unique)
    $hasTile = ($text -match '\bTileEntity\b|\bBlockContainer\b|\bBlockTCTile\b|\bcreateTileEntity\b|\bhasTileEntity\b' -or $tileTokens.Count -gt 0)
    $hasInventory = ($text -match '\bIInventory\b|\bISidedInventory\b|\bInventory\b|\bgetSizeInventory\b|\bgetStackInSlot\b|\bdecrStackSize\b')
    $hasTicking = ($text -match '\bITickable\b|\bupdate\s*\(|\bupdateEntity\s*\(|\btick\s*\(')
    $hasGui = ($text -match '\bopenGui\b|\bGui\b|\bContainer\b|\bIGuiHandler\b')
    $hasRenderer = ($text -match '\bgetRenderType\b|\bTESR\b|\bTileEntitySpecialRenderer\b|\bModel[A-Za-z0-9_]+\b|\bIItemRenderer\b|\bISmartBlockModel\b')

    $behaviorClues = [System.Collections.Generic.List[string]]::new()
    if ($text -match '\bFACING\b|\bEnumFacing\b|\bPropertyDirection\b|getStateForPlacement') { $behaviorClues.Add("facing") }
    if ($text -match '\bENABLED\b|\bpowered\b|\bredstone\b|neighborChanged') { $behaviorClues.Add("enabled_or_redstone") }
    if ($hasTile) { $behaviorClues.Add("tile_entity") }
    if ($hasInventory) { $behaviorClues.Add("inventory") }
    if ($hasTicking) { $behaviorClues.Add("ticking") }
    if ($hasGui) { $behaviorClues.Add("gui") }
    if ($hasRenderer) { $behaviorClues.Add("renderer_or_special_model") }
    if ($text -match '\bgetDrops\b|\bdamageDropped\b|\bquantityDropped\b') { $behaviorClues.Add("custom_drop_logic") }
    if ($text -match '\bgetSubItems\b|\bgetMetadata\b|\bgetDamage\b|\bmetadata\b|\bmeta\b') { $behaviorClues.Add("metadata_or_subtypes") }
    if ($text -match '\breadFromNBT\b|\bwriteToNBT\b|\bNBTTagCompound\b') { $behaviorClues.Add("nbt_state") }

    return [ordered]@{
        classFile = $relative
        packageName = if ($packageMatch.Success) { $packageMatch.Groups["package"].Value } else { $null }
        hasClassFile = $true
        extends = if ($extendsMatch.Success) { $extendsMatch.Groups["extends"].Value } else { $null }
        implements = @($implements)
        methodPresence = @($methodPresence)
        propertyCalls = @($propertyCalls)
        behaviorClues = @($behaviorClues | Select-Object -Unique)
        hasTileEntityClue = [bool]$hasTile
        hasInventoryClue = [bool]$hasInventory
        hasTickingClue = [bool]$hasTicking
        hasGuiClue = [bool]$hasGui
        hasRendererClue = [bool]$hasRenderer
        tileEntityClasses = @($tileTokens)
        confidence = "primary_source_class_scan"
    }
}

$javaFiles = @(Get-ChildItem -LiteralPath (Join-Path $legacyPath "src/main/java") -Recurse -Filter "*.java" -File)
$classIndex = @{}
$contentByRelativePath = @{}
foreach ($file in $javaFiles) {
    $relative = ConvertTo-RelativePath $file.FullName
    $content = Get-Content -Raw -LiteralPath $file.FullName
    $contentByRelativePath[$relative] = $content
    $className = [System.IO.Path]::GetFileNameWithoutExtension($file.Name)
    if (-not $classIndex.ContainsKey($className)) { $classIndex[$className] = $relative }
}

$tileRegistry = [System.Collections.Generic.List[object]]::new()
foreach ($file in $javaFiles) {
    $relative = ConvertTo-RelativePath $file.FullName
    $text = $contentByRelativePath[$relative]
    foreach ($match in [regex]::Matches($text, 'registerTileEntity\s*\(\s*(?<tile>[A-Za-z0-9_.$]+)\.class\s*,\s*"(?<id>[^"]+)"')) {
        $tileRegistry.Add([pscustomobject][ordered]@{
            tileClass = $match.Groups["tile"].Value
            id = $match.Groups["id"].Value
            sourceFile = $relative
            confidence = "explicit_register_tile_entity"
        })
    }
}

$entries = [System.Collections.Generic.List[object]]::new()
$blocksPath = Join-Path $legacyPath $requiredSourceFiles[0]
$blockLines = Get-Content -LiteralPath $blocksPath
for ($index = 0; $index -lt $blockLines.Count; $index++) {
    $line = $blockLines[$index]
    $assignment = [regex]::Match($line, 'BlocksTC\.(?<symbol>[A-Za-z0-9_]+)\s*=.*?new\s+(?<class>[A-Za-z0-9_.$]+)')
    if (-not $assignment.Success) { continue }

    $symbol = $assignment.Groups["symbol"].Value
    $legacyClass = $assignment.Groups["class"].Value
    $identity = Get-SourceIdentity $line $symbol
    $classEvidence = Get-ClassEvidence -LegacyClass $legacyClass -ClassIndex $classIndex -ContentByRelativePath $contentByRelativePath
    $tileMatches = @($tileRegistry | Where-Object { $_.id -eq $identity.id -or $_.id -eq $symbol -or $_.tileClass -in $classEvidence.tileEntityClasses })
    Add-Entry $entries ([ordered]@{
        kind = "block"
        registryId = $identity.id
        legacySymbol = $symbol
        legacyClass = $legacyClass
        legacyClassFile = $classEvidence.classFile
        legacyPackage = $classEvidence.packageName
        legacyExtends = $classEvidence.extends
        legacyImplements = @($classEvidence.implements)
        legacyTileEntities = @($tileMatches | ForEach-Object { $_.tileClass } | Select-Object -Unique)
        hasTileEntity = [bool]($classEvidence.hasTileEntityClue -or $tileMatches.Count -gt 0)
        hasInventory = [bool]$classEvidence.hasInventoryClue
        hasTicking = [bool]$classEvidence.hasTickingClue
        hasGui = [bool]$classEvidence.hasGuiClue
        hasRenderer = [bool]$classEvidence.hasRendererClue
        behaviorClues = @($classEvidence.behaviorClues)
        methodPresence = @($classEvidence.methodPresence)
        propertyCalls = @($classEvidence.propertyCalls)
        idConfidence = $identity.confidence
        comparable = $identity.confidence -eq "explicit_source"
        variants = @()
        sourceFile = $requiredSourceFiles[0]
        sourceLine = $index + 1
        evidence = $line.Trim()
    })
}

$dyeNames = @("white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black")
foreach ($prefix in @("candle", "banner", "nitor")) {
    foreach ($dye in $dyeNames) {
        $legacyClass = if ($prefix -eq "banner") { "BlockBannerTC" } elseif ($prefix -eq "nitor") { "BlockNitor" } else { "BlockCandle" }
        $classEvidence = Get-ClassEvidence -LegacyClass $legacyClass -ClassIndex $classIndex -ContentByRelativePath $contentByRelativePath
        Add-Entry $entries ([ordered]@{
            kind = "block"
            registryId = "${prefix}_${dye}"
            legacySymbol = "${prefix}[${dye}]"
            legacyClass = $legacyClass
            legacyClassFile = $classEvidence.classFile
            legacyPackage = $classEvidence.packageName
            legacyExtends = $classEvidence.extends
            legacyImplements = @($classEvidence.implements)
            legacyTileEntities = @()
            hasTileEntity = [bool]$classEvidence.hasTileEntityClue
            hasInventory = [bool]$classEvidence.hasInventoryClue
            hasTicking = [bool]$classEvidence.hasTickingClue
            hasGui = [bool]$classEvidence.hasGuiClue
            hasRenderer = [bool]$classEvidence.hasRendererClue
            behaviorClues = @($classEvidence.behaviorClues)
            methodPresence = @($classEvidence.methodPresence)
            propertyCalls = @($classEvidence.propertyCalls)
            idConfidence = "explicit_algorithm"
            comparable = $true
            variants = @()
            sourceFile = $requiredSourceFiles[0]
            sourceLine = if ($prefix -eq "candle") { 279 } elseif ($prefix -eq "banner") { 283 } else { 291 }
            evidence = "${prefix}_ + EnumDyeColor.getUnlocalizedName().toLowerCase()"
        })
    }
}

$itemsPath = Join-Path $legacyPath $requiredSourceFiles[1]
$itemLines = Get-Content -LiteralPath $itemsPath
for ($index = 0; $index -lt $itemLines.Count; $index++) {
    $line = $itemLines[$index]
    $assignment = [regex]::Match($line, 'ItemsTC\.(?<symbol>[A-Za-z0-9_]+)\s*=\s*new\s+(?<class>[A-Za-z0-9_.$]+)')
    if (-not $assignment.Success) { continue }

    $symbol = $assignment.Groups["symbol"].Value
    $legacyClass = $assignment.Groups["class"].Value
    $identity = Get-SourceIdentity $line $symbol
    $classEvidence = Get-ClassEvidence -LegacyClass $legacyClass -ClassIndex $classIndex -ContentByRelativePath $contentByRelativePath
    $variants = Find-QuotedVariants $line

    Add-Entry $entries ([ordered]@{
        kind = "item"
        registryId = $identity.id
        legacySymbol = $symbol
        legacyClass = $legacyClass
        legacyClassFile = $classEvidence.classFile
        legacyPackage = $classEvidence.packageName
        legacyExtends = $classEvidence.extends
        legacyImplements = @($classEvidence.implements)
        hasTileEntity = $false
        hasInventory = [bool]$classEvidence.hasInventoryClue
        hasTicking = [bool]$classEvidence.hasTickingClue
        hasGui = [bool]$classEvidence.hasGuiClue
        hasRenderer = [bool]$classEvidence.hasRendererClue
        behaviorClues = @($classEvidence.behaviorClues)
        methodPresence = @($classEvidence.methodPresence)
        propertyCalls = @($classEvidence.propertyCalls)
        idConfidence = $identity.confidence
        comparable = $identity.confidence -eq "explicit_source"
        variants = @($variants)
        sourceFile = $requiredSourceFiles[1]
        sourceLine = $index + 1
        evidence = $line.Trim()
    })
}

$orderedEntries = @($entries | Sort-Object kind, registryId, legacySymbol)
$usedSourceFiles = [System.Collections.Generic.HashSet[string]]::new()
foreach ($path in $requiredSourceFiles) { [void]$usedSourceFiles.Add($path) }
foreach ($entry in $orderedEntries) {
    if ($entry.legacyClassFile) { [void]$usedSourceFiles.Add($entry.legacyClassFile) }
}
foreach ($tile in $tileRegistry) { [void]$usedSourceFiles.Add($tile.sourceFile) }

$fingerprints = foreach ($relativePath in @($usedSourceFiles | Sort-Object)) {
    $fullPath = Join-Path $legacyPath $relativePath
    if (Test-Path -LiteralPath $fullPath -PathType Leaf) {
        [ordered]@{
            path = $relativePath
            sha256 = (Get-FileHash -Algorithm SHA256 -LiteralPath $fullPath).Hash.ToLowerInvariant()
        }
    }
}

$manifest = [ordered]@{
    schemaVersion = 2
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    source = $LegacyRoot.Replace("\", "/")
    extractionPolicy = "Explicit source strings and deterministic dye loops are comparable. Symbol inference is review-only. Batch 3 adds primary class-role, behavior clue and TileEntity/Menu/Renderer evidence without claiming runtime parity."
    sourceFiles = @($fingerprints)
    summary = [ordered]@{
        entries = $orderedEntries.Count
        blocks = @($orderedEntries | Where-Object kind -eq "block").Count
        items = @($orderedEntries | Where-Object kind -eq "item").Count
        comparable = @($orderedEntries | Where-Object comparable).Count
        inferredReviewRequired = @($orderedEntries | Where-Object { -not $_.comparable }).Count
        classFilesResolved = @($orderedEntries | Where-Object { $_.legacyClassFile }).Count
        tileEntityCandidates = @($orderedEntries | Where-Object hasTileEntity).Count
        inventoryCandidates = @($orderedEntries | Where-Object hasInventory).Count
        tickingCandidates = @($orderedEntries | Where-Object hasTicking).Count
        guiCandidates = @($orderedEntries | Where-Object hasGui).Count
        rendererCandidates = @($orderedEntries | Where-Object hasRenderer).Count
        tileRegistryEntries = @($tileRegistry).Count
        sourceFilesFingerprinted = @($fingerprints).Count
    }
    tileRegistry = @($tileRegistry | Sort-Object tileClass, id)
    entries = $orderedEntries
}

$outputDirectory = Split-Path -Parent $OutputPath
New-Item -ItemType Directory -Force -Path $outputDirectory | Out-Null
$manifest | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $OutputPath -Encoding utf8NoBOM
Write-Output "Legacy primary manifest: $OutputPath"
Write-Output "Entries=$($manifest.summary.entries), comparable=$($manifest.summary.comparable), inferred=$($manifest.summary.inferredReviewRequired), classFiles=$($manifest.summary.classFilesResolved), tileCandidates=$($manifest.summary.tileEntityCandidates), guiCandidates=$($manifest.summary.guiCandidates), rendererCandidates=$($manifest.summary.rendererCandidates)"

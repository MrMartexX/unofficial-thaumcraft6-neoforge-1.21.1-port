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
$blocksPath = Join-Path $portPath $blocksRelative
$itemsPath = Join-Path $portPath $itemsRelative
$langPath = Join-Path $portPath $langRelative
foreach ($required in @($blocksPath, $itemsPath, $langPath)) {
    if (-not (Test-Path -LiteralPath $required -PathType Leaf)) { throw "Required port file not found: $required" }
}

$blocksText = Get-Content -Raw -LiteralPath $blocksPath
$itemsText = Get-Content -Raw -LiteralPath $itemsPath
$lang = Get-Content -Raw -LiteralPath $langPath | ConvertFrom-Json -AsHashtable

$blockSymbols = @{}
$blockDeclarationPattern = 'public\s+static\s+final\s+Supplier<Block>\s+(?<symbol>[A-Z0-9_]+)\s*=\s*BLOCKS\.register\(\s*"(?<id>[a-z][a-z0-9_]*)"'
$blockMatches = @([regex]::Matches($blocksText, $blockDeclarationPattern, [Text.RegularExpressions.RegexOptions]::Singleline))
$blockIds = @($blockMatches | ForEach-Object { $_.Groups["id"].Value })
foreach ($match in $blockMatches) {
    $blockSymbols[$match.Groups["id"].Value] = $match.Groups["symbol"].Value
}

$itemRecords = @{}
$itemIds = [System.Collections.Generic.List[string]]::new()
$itemDeclarationPattern = 'public\s+static\s+final\s+Supplier<(?<type>[^>]+)>\s+(?<symbol>[A-Z0-9_]+)\s*=\s*(?<expression>.*?);'
foreach ($match in [regex]::Matches($itemsText, $itemDeclarationPattern, [Text.RegularExpressions.RegexOptions]::Singleline)) {
    $idMatch = [regex]::Match($match.Groups["expression"].Value, '"(?<id>[a-z][a-z0-9_]*)"')
    if (-not $idMatch.Success) { continue }
    $id = $idMatch.Groups["id"].Value
    $itemIds.Add($id)
    $itemRecords[$id] = [ordered]@{
        symbol = $match.Groups["symbol"].Value
        declaredType = $match.Groups["type"].Value.Trim()
        blockItem = $match.Groups["type"].Value -match "BlockItem"
    }
}

$assetsRoot = Join-Path $portPath "src/main/resources/assets/thaumcraft"
$dataRoot = Join-Path $portPath "src/main/resources/data/thaumcraft"
$entries = [System.Collections.Generic.List[object]]::new()

foreach ($id in @($blockSymbols.Keys | Sort-Object)) {
    $blockstateRelative = "blockstates/$id.json"
    $blockstatePath = Join-Path $assetsRoot $blockstateRelative
    $referencedModels = @()
    $missingModels = @()
    if (Test-Path -LiteralPath $blockstatePath -PathType Leaf) {
        $blockstateText = Get-Content -Raw -LiteralPath $blockstatePath
        $referencedModels = @([regex]::Matches($blockstateText, '"model"\s*:\s*"thaumcraft:(?<model>[^"#]+)') | ForEach-Object {
            $_.Groups["model"].Value
        } | Sort-Object -Unique)
        $missingModels = @($referencedModels | Where-Object {
            -not (Test-Path -LiteralPath (Join-Path $assetsRoot "models/$_.json") -PathType Leaf)
        })
    }
    $hasBlockItem = $itemRecords.ContainsKey($id) -and $itemRecords[$id].blockItem
    $entries.Add([pscustomobject][ordered]@{
        kind = "block"
        registryId = $id
        symbol = $blockSymbols[$id]
        registeredBlock = $true
        registeredItem = $hasBlockItem
        blockItem = $hasBlockItem
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
    $entries.Add([pscustomobject][ordered]@{
        kind = "item"
        registryId = $id
        symbol = $record.symbol
        declaredType = $record.declaredType
        registeredBlock = $blockSymbols.ContainsKey($id)
        registeredItem = $true
        blockItem = $record.blockItem
        resources = [ordered]@{
            itemModel = Test-Path -LiteralPath (Join-Path $assetsRoot "models/item/$id.json") -PathType Leaf
            langKey = if ($record.blockItem) { $lang.ContainsKey("block.thaumcraft.$id") } else { $lang.ContainsKey("item.thaumcraft.$id") }
        }
    })
}

$sourceFiles = foreach ($relativePath in @($blocksRelative, $itemsRelative, $langRelative)) {
    $fullPath = Join-Path $portPath $relativePath
    [ordered]@{
        path = $relativePath
        sha256 = (Get-FileHash -Algorithm SHA256 -LiteralPath $fullPath).Hash.ToLowerInvariant()
    }
}
$orderedEntries = @($entries | Sort-Object kind, registryId)
$duplicateBlocks = @($blockIds | Group-Object | Where-Object Count -gt 1 | ForEach-Object Name | Sort-Object)
$duplicateItems = @($itemIds | Group-Object | Where-Object Count -gt 1 | ForEach-Object Name | Sort-Object)
$manifest = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    source = $PortRoot.Replace("\", "/")
    sourceFiles = @($sourceFiles)
    diagnostics = [ordered]@{
        duplicateBlockIds = $duplicateBlocks
        duplicateItemIds = $duplicateItems
    }
    summary = [ordered]@{
        entries = $orderedEntries.Count
        blocks = @($orderedEntries | Where-Object kind -eq "block").Count
        items = @($orderedEntries | Where-Object kind -eq "item").Count
        blockItems = @($orderedEntries | Where-Object { $_.kind -eq "item" -and $_.blockItem }).Count
    }
    entries = $orderedEntries
}

$outputDirectory = Split-Path -Parent $OutputPath
New-Item -ItemType Directory -Force -Path $outputDirectory | Out-Null
$manifest | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $OutputPath -Encoding utf8NoBOM
Write-Output "Port live manifest: $OutputPath"
Write-Output "Blocks=$($manifest.summary.blocks), items=$($manifest.summary.items), blockItems=$($manifest.summary.blockItems), duplicateIds=$($duplicateBlocks.Count + $duplicateItems.Count)"

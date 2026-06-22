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

$sourceFiles = @(
    "src/main/java/thaumcraft/common/config/ConfigBlocks.java",
    "src/main/java/thaumcraft/common/config/ConfigItems.java",
    "src/main/java/thaumcraft/api/blocks/BlocksTC.java",
    "src/main/java/thaumcraft/api/items/ItemsTC.java"
)
foreach ($relativePath in $sourceFiles) {
    $fullPath = Join-Path $legacyPath $relativePath
    if (-not (Test-Path -LiteralPath $fullPath -PathType Leaf)) {
        throw "Required primary legacy source file not found: $fullPath"
    }
}

function ConvertTo-SnakeCase([string]$Value) {
    return ([regex]::Replace($Value, "(?<=[a-z0-9])([A-Z])", "_`$1")).ToLowerInvariant()
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

$entries = [System.Collections.Generic.List[object]]::new()
$blocksPath = Join-Path $legacyPath $sourceFiles[0]
$blockLines = Get-Content -LiteralPath $blocksPath
for ($index = 0; $index -lt $blockLines.Count; $index++) {
    $line = $blockLines[$index]
    $assignment = [regex]::Match($line, 'BlocksTC\.(?<symbol>[A-Za-z0-9_]+)\s*=.*?new\s+(?<class>[A-Za-z0-9_.$]+)')
    if (-not $assignment.Success) { continue }

    $symbol = $assignment.Groups["symbol"].Value
    $identity = Get-SourceIdentity $line $symbol
    Add-Entry $entries ([ordered]@{
        kind = "block"
        registryId = $identity.id
        legacySymbol = $symbol
        legacyClass = $assignment.Groups["class"].Value
        idConfidence = $identity.confidence
        comparable = $identity.confidence -eq "explicit_source"
        variants = @()
        sourceFile = $sourceFiles[0]
        sourceLine = $index + 1
        evidence = $line.Trim()
    })
}

$dyeNames = @("white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black")
foreach ($prefix in @("candle", "banner", "nitor")) {
    foreach ($dye in $dyeNames) {
        Add-Entry $entries ([ordered]@{
            kind = "block"
            registryId = "${prefix}_${dye}"
            legacySymbol = "${prefix}[${dye}]"
            legacyClass = if ($prefix -eq "banner") { "BlockBannerTC" } elseif ($prefix -eq "nitor") { "BlockNitor" } else { "BlockCandle" }
            idConfidence = "explicit_algorithm"
            comparable = $true
            variants = @()
            sourceFile = $sourceFiles[0]
            sourceLine = if ($prefix -eq "candle") { 279 } elseif ($prefix -eq "banner") { 283 } else { 291 }
            evidence = "${prefix}_ + EnumDyeColor.getUnlocalizedName().toLowerCase()"
        })
    }
}

$itemsPath = Join-Path $legacyPath $sourceFiles[1]
$itemLines = Get-Content -LiteralPath $itemsPath
for ($index = 0; $index -lt $itemLines.Count; $index++) {
    $line = $itemLines[$index]
    $assignment = [regex]::Match($line, 'ItemsTC\.(?<symbol>[A-Za-z0-9_]+)\s*=\s*new\s+(?<class>[A-Za-z0-9_.$]+)')
    if (-not $assignment.Success) { continue }

    $symbol = $assignment.Groups["symbol"].Value
    $identity = Get-SourceIdentity $line $symbol
    $quoted = @([regex]::Matches($line, '"(?<id>[a-z][a-z0-9_]*)"') | ForEach-Object {
        $_.Groups["id"].Value
    } | Where-Object { $_ -ne "thaumcraft" })
    $variants = if ($quoted.Count -gt 1) { @($quoted | Select-Object -Skip 1) } else { @() }

    Add-Entry $entries ([ordered]@{
        kind = "item"
        registryId = $identity.id
        legacySymbol = $symbol
        legacyClass = $assignment.Groups["class"].Value
        idConfidence = $identity.confidence
        comparable = $identity.confidence -eq "explicit_source"
        variants = $variants
        sourceFile = $sourceFiles[1]
        sourceLine = $index + 1
        evidence = $line.Trim()
    })
}

$fingerprints = foreach ($relativePath in $sourceFiles) {
    $fullPath = Join-Path $legacyPath $relativePath
    [ordered]@{
        path = $relativePath
        sha256 = (Get-FileHash -Algorithm SHA256 -LiteralPath $fullPath).Hash.ToLowerInvariant()
    }
}

$orderedEntries = @($entries | Sort-Object kind, registryId, legacySymbol)
$manifest = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    source = $LegacyRoot.Replace("\", "/")
    extractionPolicy = "Explicit source strings and deterministic dye loops are comparable. Symbol inference is review-only."
    sourceFiles = @($fingerprints)
    summary = [ordered]@{
        entries = $orderedEntries.Count
        blocks = @($orderedEntries | Where-Object kind -eq "block").Count
        items = @($orderedEntries | Where-Object kind -eq "item").Count
        comparable = @($orderedEntries | Where-Object comparable).Count
        inferredReviewRequired = @($orderedEntries | Where-Object { -not $_.comparable }).Count
    }
    entries = $orderedEntries
}

$outputDirectory = Split-Path -Parent $OutputPath
New-Item -ItemType Directory -Force -Path $outputDirectory | Out-Null
$manifest | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $OutputPath -Encoding utf8NoBOM
Write-Output "Legacy primary manifest: $OutputPath"
Write-Output "Entries=$($manifest.summary.entries), comparable=$($manifest.summary.comparable), inferred=$($manifest.summary.inferredReviewRequired)"

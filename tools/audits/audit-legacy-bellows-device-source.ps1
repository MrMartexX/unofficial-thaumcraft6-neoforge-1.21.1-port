param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path,
    [string]$LegacyRoot
)

$ErrorActionPreference = 'Stop'

function Resolve-LegacySearchRoot {
    param([string]$Repo, [string]$ExplicitLegacyRoot)

    if (![string]::IsNullOrWhiteSpace($ExplicitLegacyRoot)) {
        if (!(Test-Path -LiteralPath $ExplicitLegacyRoot)) {
            throw "[tc-port] Explicit LegacyRoot does not exist: $ExplicitLegacyRoot"
        }
        return (Resolve-Path -LiteralPath $ExplicitLegacyRoot).Path
    }

    foreach ($candidate in @(
        (Join-Path $Repo '02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master'),
        (Join-Path $Repo '02_existing_decompiled_repo'),
        $Repo
    )) {
        if (Test-Path -LiteralPath $candidate) {
            $found = @(Get-ChildItem -LiteralPath $candidate -Recurse -File -Filter 'TileBellows.java' -ErrorAction SilentlyContinue)
            if ($found.Count -gt 0) {
                return (Resolve-Path -LiteralPath $candidate).Path
            }
        }
    }

    throw '[tc-port] Could not find legacy TileBellows.java. Pass -LegacyRoot explicitly.'
}

function Find-OneJava {
    param([string]$Root, [string]$FileName)

    $matches = @(Get-ChildItem -LiteralPath $Root -Recurse -File -Filter $FileName -ErrorAction SilentlyContinue | Sort-Object FullName)
    if ($matches.Count -eq 0) {
        throw "[tc-port] Could not find Java source file: $FileName under $Root"
    }

    return $matches[0]
}

function Read-Text {
    param([string]$Path)

    if (!(Test-Path -LiteralPath $Path)) {
        throw "[tc-port] Missing file: $Path"
    }

    return (Get-Content -Raw -LiteralPath $Path) -replace "`r`n", "`n"
}

$repo = (Resolve-Path -LiteralPath $RepoRoot).Path
$legacy = Resolve-LegacySearchRoot -Repo $repo -ExplicitLegacyRoot $LegacyRoot

$tileBellows = Find-OneJava -Root $legacy -FileName 'TileBellows.java'
$blockBellows = Find-OneJava -Root $legacy -FileName 'BlockBellows.java'

$tileText = Read-Text $tileBellows.FullName
$blockText = Read-Text $blockBellows.FullName
$portBellowsBlock = Read-Text (Join-Path $repo '05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBellowsBlock.java')
$portSmelter = Read-Text (Join-Path $repo '05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java')

if (!$tileText.Contains('class TileBellows')) {
    throw '[tc-port] Legacy TileBellows.java does not contain class TileBellows.'
}
if (!$blockText.Contains('class BlockBellows')) {
    throw '[tc-port] Legacy BlockBellows.java does not contain class BlockBellows.'
}
if (!($portBellowsBlock.Contains('class TCBellowsBlock') -and $portBellowsBlock.Contains('FACING') -and $portBellowsBlock.Contains('ENABLED'))) {
    throw '[tc-port] Current TCBellowsBlock blockstate boundary was not found.'
}
if (!($portSmelter.Contains('refreshBellows()') -and $portSmelter.Contains('TCBellowsBlock.ENABLED') -and $portSmelter.Contains('smeltTimeForVis'))) {
    throw '[tc-port] Current smelter Bellows bridge was not found.'
}

Write-Host '[tc-port] Legacy Bellows device source audit passed.'

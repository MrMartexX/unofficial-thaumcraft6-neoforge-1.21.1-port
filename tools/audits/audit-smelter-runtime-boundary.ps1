param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
)

$ErrorActionPreference = 'Stop'
$repo = Resolve-Path -LiteralPath $RepoRoot
$resourceRoot = Join-Path $repo '05_neoforge_port/src/main/resources/assets/thaumcraft'

$detailedModels = @(
    'models/block/alembic_normal.json',
    'models/block/bellows.json',
    'models/block/smelter_aux.json',
    'models/block/smelter_vent.json'
)
foreach ($relative in $detailedModels) {
    $path = Join-Path $resourceRoot $relative
    if (!(Test-Path -LiteralPath $path)) {
        throw "[tc-port] Missing smelter resource: $relative"
    }
    $model = Get-Content -Raw -LiteralPath $path | ConvertFrom-Json
    if ($null -eq $model.elements -or $model.elements.Count -lt 2) {
        throw "[tc-port] Smelter resource regressed to a placeholder model: $relative"
    }
}

$fuelMapPath = Join-Path $repo '05_neoforge_port/src/main/resources/data/neoforge/data_maps/item/furnace_fuels.json'
$fuelMap = Get-Content -Raw -LiteralPath $fuelMapPath | ConvertFrom-Json
$alumentumBurn = $fuelMap.values.'thaumcraft:alumentum'.value.burn_time
$greatwoodBurn = $fuelMap.values.'thaumcraft:log_greatwood'.value.burn_time
$silverwoodBurn = $fuelMap.values.'thaumcraft:log_silverwood'.value.burn_time
if (($alumentumBurn -ne 4800) -or ($greatwoodBurn -ne 500) -or ($silverwoodBurn -ne 400)) {
    throw '[tc-port] Legacy Thaumcraft fuel values do not match 4800/500/400.'
}

& (Join-Path $repo 'tools/audits/audit-essentia-transport-behavior.ps1') -RepoRoot $repo
if ($LASTEXITCODE -ne 0) {
    throw "[tc-port] Essentia transport/smelter runtime audit failed with exit code $LASTEXITCODE"
}
Write-Host '[tc-port] Smelter runtime boundary and detailed resource checks passed.'

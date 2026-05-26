param(
    [switch]$NoOverwrite
)

$ErrorActionPreference = 'Stop'

$RepoRoot = Split-Path -Parent $PSScriptRoot
$NeoForgeRoot = Join-Path $RepoRoot '05_neoforge_port'
$ItemModelDir = Join-Path $NeoForgeRoot 'src/main/resources/assets/thaumcraft/models/item'

if (!(Test-Path $ItemModelDir)) {
    New-Item -ItemType Directory -Path $ItemModelDir -Force | Out-Null
}

function Write-ModelJson {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][hashtable]$JsonObject
    )

    $Path = Join-Path $ItemModelDir "$Name.json"
    if ($NoOverwrite -and (Test-Path $Path)) {
        Write-Host "SKIP existing $Path"
        return
    }

    $Json = $JsonObject | ConvertTo-Json -Depth 8
    Set-Content -Path $Path -Value ($Json + [Environment]::NewLine) -Encoding UTF8
    Write-Host "WRITE $Path"
}

function GeneratedItemModel {
    param([Parameter(Mandatory = $true)][string]$Texture)
    return @{
        parent = 'minecraft:item/generated'
        textures = @{
            layer0 = $Texture
        }
    }
}

function HandheldItemModel {
    param([Parameter(Mandatory = $true)][string]$Texture)
    return @{
        parent = 'minecraft:item/handheld'
        textures = @{
            layer0 = $Texture
        }
    }
}

function BlockItemModel {
    param([Parameter(Mandatory = $true)][string]$ParentModel)
    return @{
        parent = $ParentModel
    }
}

# BlockItem placeholders. These deliberately point to vanilla block models until native TC block models are ported.
$blockItemParents = @{
    metal_brass      = 'minecraft:block/iron_block'
    metal_thaumium   = 'minecraft:block/iron_block'
    metal_void       = 'minecraft:block/obsidian'
    nitor_yellow     = 'minecraft:block/glowstone'
    arcane_workbench = 'minecraft:block/crafting_table'
    research_table   = 'minecraft:block/oak_planks'
    crucible         = 'minecraft:block/cauldron'
    smelter_basic    = 'minecraft:block/stone'
    wand_workbench   = 'minecraft:block/crafting_table'
    infusion_matrix  = 'minecraft:block/diamond_block'
}

foreach ($entry in $blockItemParents.GetEnumerator()) {
    Write-ModelJson -Name $entry.Key -JsonObject (BlockItemModel $entry.Value)
}

# Simple material/resource placeholders.
$itemTextures = @{
    thaumium_ingot = 'minecraft:item/iron_ingot'
    brass_ingot = 'minecraft:item/gold_ingot'
    thaumium_plate = 'minecraft:item/iron_ingot'
    void_plate = 'minecraft:item/netherite_ingot'
    rare_earth = 'minecraft:item/gunpowder'
    tallow = 'minecraft:item/slime_ball'
    vis_resonator = 'minecraft:item/echo_shard'
    mirrored_glass = 'minecraft:block/glass'
    brain = 'minecraft:item/rotten_flesh'
    curio_rites = 'minecraft:item/book'
    scribing_tools = 'minecraft:item/feather'
    caster_basic = 'minecraft:item/stick'
    focus_1 = 'minecraft:item/amethyst_shard'
    focus_2 = 'minecraft:item/amethyst_shard'
    focus_3 = 'minecraft:item/amethyst_shard'
    enchanted_placeholder_protection_1 = 'minecraft:item/enchanted_book'
    enchanted_placeholder_sharpness_1 = 'minecraft:item/enchanted_book'
    enchanted_placeholder_silk_touch_1 = 'minecraft:item/enchanted_book'
    enchanted_placeholder_fortune_1 = 'minecraft:item/enchanted_book'
}

foreach ($entry in $itemTextures.GetEnumerator()) {
    Write-ModelJson -Name $entry.Key -JsonObject (GeneratedItemModel $entry.Value)
}

# Tool placeholders. These remove missing texture warnings while proper TC tool models are not ported yet.
$toolTextures = @{
    thaumium_axe = 'minecraft:item/iron_axe'
    thaumium_hoe = 'minecraft:item/iron_hoe'
    thaumium_pick = 'minecraft:item/iron_pickaxe'
    thaumium_shovel = 'minecraft:item/iron_shovel'
    thaumium_sword = 'minecraft:item/iron_sword'
}

foreach ($entry in $toolTextures.GetEnumerator()) {
    Write-ModelJson -Name $entry.Key -JsonObject (HandheldItemModel $entry.Value)
}

# Aspect-specific flattened compatibility placeholders.
$aspects = @(
    'aer', 'terra', 'ignis', 'aqua', 'ordo', 'perditio',
    'vacuos', 'lux', 'motus', 'gelum', 'vitreus', 'metallum', 'victus', 'mortuus', 'potentia', 'permutatio',
    'praecantatio', 'auram', 'alkimia', 'vitium',
    'tenebrae', 'alienis', 'volatus', 'herba',
    'instrumentum', 'fabrico', 'machina', 'vinculum',
    'spiritus', 'cognitio', 'sensus', 'aversio', 'praemunio', 'desiderium',
    'exanimis', 'bestia', 'humanus'
)

foreach ($aspect in $aspects) {
    Write-ModelJson -Name "crystal_essence_$aspect" -JsonObject (GeneratedItemModel 'minecraft:item/amethyst_shard')
    Write-ModelJson -Name "phial_$aspect" -JsonObject (GeneratedItemModel 'minecraft:item/glass_bottle')
}

Write-Host ''
Write-Host 'Placeholder item model generation complete.'
Write-Host 'Next suggested commands:'
Write-Host '  git status'
Write-Host '  git add 05_neoforge_port/src/main/resources/assets/thaumcraft/models/item'
Write-Host '  git commit -m "Add placeholder item models"'

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$target = Join-Path $root "05_neoforge_port"
$legacy = Join-Path $root "02_existing_decompiled_repo\Thaumcraft-6-Source-Code-master\src\main\resources\assets\thaumcraft"

function WriteUtf8NoBom($path, $content) {
    $dir = Split-Path -Parent $path
    if (!(Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }
    [System.IO.File]::WriteAllText($path, $content, [System.Text.UTF8Encoding]::new($false))
}

function CopyTexture($sourceRelative, $targetRelative) {
    $source = Join-Path $legacy $sourceRelative
    $dest = Join-Path $target ("src\main\resources\assets\thaumcraft\" + $targetRelative)

    if (!(Test-Path $source)) {
        throw "Missing legacy texture: $source"
    }

    $destDir = Split-Path -Parent $dest
    if (!(Test-Path $destDir)) {
        New-Item -ItemType Directory -Path $destDir -Force | Out-Null
    }

    Copy-Item $source $dest -Force
    if (!(Test-Path $dest)) {
        throw "Texture copy failed: $dest"
    }
}

WriteUtf8NoBom `
    (Join-Path $target "src\main\java\thaumcraft\common\registry\TCBlocks.java") `
@'
package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public final class TCBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Thaumcraft.MODID);

    public static final Supplier<Block> ORE_AMBER = BLOCKS.register("ore_amber", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(1.5F, 3.0F)
                    .requiresCorrectToolForDrops())
    );

    public static final Supplier<Block> ORE_CINNABAR = BLOCKS.register("ore_cinnabar", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(2.0F, 3.0F)
                    .requiresCorrectToolForDrops())
    );

    public static final Supplier<Block> ORE_QUARTZ = BLOCKS.register("ore_quartz", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(3.0F, 3.0F)
                    .requiresCorrectToolForDrops())
    );

    // Temporary inert visual placeholders. Real BlockCrystal behavior will be rebuilt later.
    public static final Supplier<Block> CRYSTAL_AER = BLOCKS.register("crystal_aer", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(0.25F, 0.25F)
                    .noOcclusion())
    );

    public static final Supplier<Block> CRYSTAL_IGNIS = BLOCKS.register("crystal_ignis", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(0.25F, 0.25F)
                    .noOcclusion())
    );

    public static final Supplier<Block> CRYSTAL_AQUA = BLOCKS.register("crystal_aqua", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(0.25F, 0.25F)
                    .noOcclusion())
    );

    public static final Supplier<Block> CRYSTAL_TERRA = BLOCKS.register("crystal_terra", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(0.25F, 0.25F)
                    .noOcclusion())
    );

    public static final Supplier<Block> CRYSTAL_ORDO = BLOCKS.register("crystal_ordo", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(0.25F, 0.25F)
                    .noOcclusion())
    );

    public static final Supplier<Block> CRYSTAL_PERDITIO = BLOCKS.register("crystal_perditio", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(0.25F, 0.25F)
                    .noOcclusion())
    );

    public static final Supplier<Block> CRYSTAL_VITIUM = BLOCKS.register("crystal_vitium", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(0.25F, 0.25F)
                    .noOcclusion())
    );

    public static final Supplier<Block> STONE_ARCANE = BLOCKS.register("stone_arcane", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(2.0F, 10.0F)
                    .requiresCorrectToolForDrops())
    );

    public static final Supplier<Block> STONE_ARCANE_BRICK = BLOCKS.register("stone_arcane_brick", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)
                    .strength(2.0F, 10.0F)
                    .requiresCorrectToolForDrops())
    );

    public static final Supplier<Block> STONE_ANCIENT = BLOCKS.register("stone_ancient", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(2.0F, 10.0F)
                    .requiresCorrectToolForDrops())
    );

    private TCBlocks() {
    }
}
'@

WriteUtf8NoBom `
    (Join-Path $target "src\main\java\thaumcraft\common\registry\TCItems.java") `
@'
package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public final class TCItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Thaumcraft.MODID);

    public static final Supplier<BlockItem> ORE_AMBER = ITEMS.register("ore_amber", () ->
            new BlockItem(TCBlocks.ORE_AMBER.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> ORE_CINNABAR = ITEMS.register("ore_cinnabar", () ->
            new BlockItem(TCBlocks.ORE_CINNABAR.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> ORE_QUARTZ = ITEMS.register("ore_quartz", () ->
            new BlockItem(TCBlocks.ORE_QUARTZ.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> CRYSTAL_AER = ITEMS.register("crystal_aer", () ->
            new BlockItem(TCBlocks.CRYSTAL_AER.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> CRYSTAL_IGNIS = ITEMS.register("crystal_ignis", () ->
            new BlockItem(TCBlocks.CRYSTAL_IGNIS.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> CRYSTAL_AQUA = ITEMS.register("crystal_aqua", () ->
            new BlockItem(TCBlocks.CRYSTAL_AQUA.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> CRYSTAL_TERRA = ITEMS.register("crystal_terra", () ->
            new BlockItem(TCBlocks.CRYSTAL_TERRA.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> CRYSTAL_ORDO = ITEMS.register("crystal_ordo", () ->
            new BlockItem(TCBlocks.CRYSTAL_ORDO.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> CRYSTAL_PERDITIO = ITEMS.register("crystal_perditio", () ->
            new BlockItem(TCBlocks.CRYSTAL_PERDITIO.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> CRYSTAL_VITIUM = ITEMS.register("crystal_vitium", () ->
            new BlockItem(TCBlocks.CRYSTAL_VITIUM.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> STONE_ARCANE = ITEMS.register("stone_arcane", () ->
            new BlockItem(TCBlocks.STONE_ARCANE.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> STONE_ARCANE_BRICK = ITEMS.register("stone_arcane_brick", () ->
            new BlockItem(TCBlocks.STONE_ARCANE_BRICK.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> STONE_ANCIENT = ITEMS.register("stone_ancient", () ->
            new BlockItem(TCBlocks.STONE_ANCIENT.get(), new Item.Properties())
    );

    public static final Supplier<Item> GOGGLES = ITEMS.register("goggles", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> AMBER = ITEMS.register("amber", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> QUICKSILVER = ITEMS.register("quicksilver", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> FABRIC = ITEMS.register("fabric", () -> new Item(new Item.Properties()));

    private TCItems() {
    }
}
'@

WriteUtf8NoBom `
    (Join-Path $target "src\main\java\thaumcraft\common\registry\TCCreativeTabOrder.java") `
@'
package thaumcraft.common.registry;

import net.minecraft.world.item.CreativeModeTab;

/**
 * Controls the visible Thaumcraft creative tab order.
 *
 * <p>Do not sort this class alphabetically and do not rely on registry declaration order.
 * The visible order should follow the Thaumcraft 6 1.12.2 creative inventory screenshots.</p>
 */
public final class TCCreativeTabOrder {
    private TCCreativeTabOrder() {
    }

    public static void addThaumcraftItems(CreativeModeTab.Output output) {
        output.accept(TCItems.ORE_AMBER.get());
        output.accept(TCItems.ORE_CINNABAR.get());
        output.accept(TCItems.ORE_QUARTZ.get());

        output.accept(TCItems.CRYSTAL_AER.get());
        output.accept(TCItems.CRYSTAL_IGNIS.get());
        output.accept(TCItems.CRYSTAL_AQUA.get());
        output.accept(TCItems.CRYSTAL_TERRA.get());
        output.accept(TCItems.CRYSTAL_ORDO.get());
        output.accept(TCItems.CRYSTAL_PERDITIO.get());
        output.accept(TCItems.CRYSTAL_VITIUM.get());

        output.accept(TCItems.STONE_ARCANE.get());
        output.accept(TCItems.STONE_ARCANE_BRICK.get());
        output.accept(TCItems.STONE_ANCIENT.get());
    }
}
'@

WriteUtf8NoBom `
    (Join-Path $target "src\main\resources\assets\thaumcraft\lang\en_us.json") `
@'
{
  "itemGroup.thaumcraft": "Thaumcraft",
  "block.thaumcraft.ore_amber": "Amber Bearing Stone",
  "block.thaumcraft.ore_cinnabar": "Cinnabar Ore",
  "block.thaumcraft.ore_quartz": "Quartz Ore",
  "block.thaumcraft.crystal_aer": "Air Crystal",
  "block.thaumcraft.crystal_ignis": "Fire Crystal",
  "block.thaumcraft.crystal_aqua": "Water Crystal",
  "block.thaumcraft.crystal_terra": "Earth Crystal",
  "block.thaumcraft.crystal_ordo": "Order Crystal",
  "block.thaumcraft.crystal_perditio": "Entropy Crystal",
  "block.thaumcraft.crystal_vitium": "Flux Crystal",
  "block.thaumcraft.stone_arcane": "Arcane Stone",
  "block.thaumcraft.stone_arcane_brick": "Arcane Stone Brick",
  "block.thaumcraft.stone_ancient": "Ancient Stone",
  "item.thaumcraft.goggles": "Goggles of Revealing",
  "item.thaumcraft.amber": "Amber",
  "item.thaumcraft.quicksilver": "Quicksilver",
  "item.thaumcraft.fabric": "Enchanted Fabric"
}
'@

$crystals = @(
    "crystal_aer",
    "crystal_ignis",
    "crystal_aqua",
    "crystal_terra",
    "crystal_ordo",
    "crystal_perditio",
    "crystal_vitium"
)

foreach ($id in $crystals) {
    WriteUtf8NoBom `
        (Join-Path $target "src\main\resources\assets\thaumcraft\blockstates\$id.json") `
@"
{
  "variants": {
    "": {
      "model": "thaumcraft:block/$id"
    }
  }
}
"@

    WriteUtf8NoBom `
        (Join-Path $target "src\main\resources\assets\thaumcraft\models\block\$id.json") `
@"
{
  "parent": "minecraft:block/cube_all",
  "textures": {
    "all": "thaumcraft:item/crystal_planter"
  }
}
"@

    WriteUtf8NoBom `
        (Join-Path $target "src\main\resources\assets\thaumcraft\models\item\$id.json") `
@"
{
  "parent": "minecraft:item/generated",
  "textures": {
    "layer0": "thaumcraft:item/crystal_planter"
  }
}
"@
}

$stones = @(
    "stone_arcane",
    "stone_arcane_brick",
    "stone_ancient"
)

foreach ($id in $stones) {
    WriteUtf8NoBom `
        (Join-Path $target "src\main\resources\assets\thaumcraft\blockstates\$id.json") `
@"
{
  "variants": {
    "": {
      "model": "thaumcraft:block/$id"
    }
  }
}
"@

    WriteUtf8NoBom `
        (Join-Path $target "src\main\resources\assets\thaumcraft\models\item\$id.json") `
@"
{
  "parent": "thaumcraft:block/$id"
}
"@
}

WriteUtf8NoBom `
    (Join-Path $target "src\main\resources\assets\thaumcraft\models\block\stone_arcane.json") `
@'
{
  "parent": "minecraft:block/cube",
  "textures": {
    "particle": "thaumcraft:block/arcane_stone_1",
    "up": "thaumcraft:block/arcane_stone_1",
    "down": "thaumcraft:block/arcane_stone_1",
    "east": "thaumcraft:block/arcane_stone_2",
    "west": "thaumcraft:block/arcane_stone_2",
    "north": "thaumcraft:block/arcane_stone_3",
    "south": "thaumcraft:block/arcane_stone_3"
  }
}
'@

WriteUtf8NoBom `
    (Join-Path $target "src\main\resources\assets\thaumcraft\models\block\stone_arcane_brick.json") `
@'
{
  "parent": "minecraft:block/cube_all",
  "textures": {
    "all": "thaumcraft:block/arcane_brick_stone"
  }
}
'@

WriteUtf8NoBom `
    (Join-Path $target "src\main\resources\assets\thaumcraft\models\block\stone_ancient.json") `
@'
{
  "parent": "minecraft:block/cube",
  "textures": {
    "particle": "thaumcraft:block/ancient_stone_0",
    "up": "thaumcraft:block/ancient_stone_0",
    "down": "thaumcraft:block/ancient_stone_1",
    "east": "thaumcraft:block/ancient_stone_2",
    "west": "thaumcraft:block/ancient_stone_3",
    "north": "thaumcraft:block/ancient_stone_4",
    "south": "thaumcraft:block/ancient_stone_5"
  }
}
'@

CopyTexture "textures\items\crystal_planter.png" "textures\item\crystal_planter.png"

CopyTexture "textures\blocks\arcane_stone_1.png" "textures\block\arcane_stone_1.png"
CopyTexture "textures\blocks\arcane_stone_2.png" "textures\block\arcane_stone_2.png"
CopyTexture "textures\blocks\arcane_stone_3.png" "textures\block\arcane_stone_3.png"
CopyTexture "textures\blocks\arcane_brick_stone.png" "textures\block\arcane_brick_stone.png"

CopyTexture "textures\blocks\ancient_stone_0.png" "textures\block\ancient_stone_0.png"
CopyTexture "textures\blocks\ancient_stone_1.png" "textures\block\ancient_stone_1.png"
CopyTexture "textures\blocks\ancient_stone_2.png" "textures\block\ancient_stone_2.png"
CopyTexture "textures\blocks\ancient_stone_3.png" "textures\block\ancient_stone_3.png"
CopyTexture "textures\blocks\ancient_stone_4.png" "textures\block\ancient_stone_4.png"
CopyTexture "textures\blocks\ancient_stone_5.png" "textures\block\ancient_stone_5.png"

Write-Host "Next visible creative batch applied."
Write-Host "Added: 7 crystal placeholders + 3 stone blocks."
Write-Host "Next: cd .\05_neoforge_port; .\gradlew.bat clean build --no-daemon"
package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import thaumcraft.common.blocks.world.plants.TCPlantBlock;
import thaumcraft.common.blocks.world.plants.TCSaplingBlock;
import thaumcraft.common.blocks.world.plants.TCLeavesBlock;
import thaumcraft.common.blocks.world.plants.TCLogBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public final class TCBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Thaumcraft.MODID);

    public static final Supplier<Block> ORE_AMBER = BLOCKS.register("ore_amber", () -> stoneBlock(1.5F, 5.0F));
    public static final Supplier<Block> ORE_CINNABAR = BLOCKS.register("ore_cinnabar", () -> stoneBlock(2.0F, 5.0F));
    public static final Supplier<Block> ORE_QUARTZ = BLOCKS.register("ore_quartz", () -> stoneBlock(3.0F, 5.0F));

    public static final Supplier<Block> CRYSTAL_AER = BLOCKS.register("crystal_aer", () -> crystalPlaceholder());
    public static final Supplier<Block> CRYSTAL_IGNIS = BLOCKS.register("crystal_ignis", () -> crystalPlaceholder());
    public static final Supplier<Block> CRYSTAL_AQUA = BLOCKS.register("crystal_aqua", () -> crystalPlaceholder());
    public static final Supplier<Block> CRYSTAL_TERRA = BLOCKS.register("crystal_terra", () -> crystalPlaceholder());
    public static final Supplier<Block> CRYSTAL_ORDO = BLOCKS.register("crystal_ordo", () -> crystalPlaceholder());
    public static final Supplier<Block> CRYSTAL_PERDITIO = BLOCKS.register("crystal_perditio", () -> crystalPlaceholder());
    public static final Supplier<Block> CRYSTAL_VITIUM = BLOCKS.register("crystal_vitium", () -> crystalPlaceholder());

    public static final Supplier<Block> STONE_ARCANE = BLOCKS.register("stone_arcane", () -> stoneBlock(2.0F, 10.0F));
    public static final Supplier<Block> STONE_ARCANE_BRICK = BLOCKS.register("stone_arcane_brick", () -> stoneBlock(2.0F, 10.0F));
    public static final Supplier<Block> STONE_ANCIENT = BLOCKS.register("stone_ancient", () -> stoneBlock(2.0F, 10.0F));
    public static final Supplier<Block> STONE_ANCIENT_TILE = BLOCKS.register("stone_ancient_tile", () -> stoneBlock(2.0F, 10.0F));
    public static final Supplier<Block> STONE_ANCIENT_ROCK = BLOCKS.register("stone_ancient_rock", () -> unbreakableStoneBlock(10.0F));
    public static final Supplier<Block> STONE_ANCIENT_GLYPHED = BLOCKS.register("stone_ancient_glyphed", () -> stoneBlock(2.0F, 10.0F));
    public static final Supplier<Block> STONE_ANCIENT_DOORWAY = BLOCKS.register("stone_ancient_doorway", () -> unbreakableStoneBlock(10.0F));
    public static final Supplier<Block> STONE_ELDRITCH_TILE = BLOCKS.register("stone_eldritch_tile", () -> stoneBlock(15.0F, 1000.0F));
    public static final Supplier<Block> STONE_POROUS = BLOCKS.register("stone_porous", () -> stoneBlock(1.0F, 5.0F));

    public static final Supplier<Block> STAIRS_ARCANE = BLOCKS.register("stairs_arcane", () -> stoneStairBlock(STONE_ARCANE.get().defaultBlockState(), 2.0F, 10.0F));
    public static final Supplier<Block> STAIRS_ARCANE_BRICK = BLOCKS.register("stairs_arcane_brick", () -> stoneStairBlock(STONE_ARCANE_BRICK.get().defaultBlockState(), 2.0F, 10.0F));
    public static final Supplier<Block> STAIRS_ANCIENT = BLOCKS.register("stairs_ancient", () -> stoneStairBlock(STONE_ANCIENT.get().defaultBlockState(), 2.0F, 10.0F));

    public static final Supplier<Block> SLAB_ARCANE_STONE = BLOCKS.register("slab_arcane_stone", () -> stoneSlabBlock(2.0F, 10.0F));
    public static final Supplier<Block> SLAB_ARCANE_BRICK = BLOCKS.register("slab_arcane_brick", () -> stoneSlabBlock(2.0F, 10.0F));
    public static final Supplier<Block> SLAB_ANCIENT = BLOCKS.register("slab_ancient", () -> stoneSlabBlock(2.0F, 10.0F));
    public static final Supplier<Block> SLAB_ELDRITCH = BLOCKS.register("slab_eldritch", () -> stoneSlabBlock(2.0F, 10.0F));

    public static final Supplier<Block> AMBER_BLOCK = BLOCKS.register("amber_block", () -> amberBlock());
    public static final Supplier<Block> AMBER_BRICK = BLOCKS.register("amber_brick", () -> amberBlock());

    public static final Supplier<Block> METAL_BRASS = BLOCKS.register("metal_brass", () -> metalBlock());
    public static final Supplier<Block> METAL_THAUMIUM = BLOCKS.register("metal_thaumium", () -> metalBlock());
    public static final Supplier<Block> METAL_VOID = BLOCKS.register("metal_void", () -> metalBlock());

    public static final Supplier<Block> NITOR_YELLOW = BLOCKS.register("nitor_yellow", () -> nitorBlock(14));

    public static final Supplier<Block> ARCANE_WORKBENCH = BLOCKS.register("arcane_workbench", () -> workbenchBlock());
    public static final Supplier<Block> RESEARCH_TABLE = BLOCKS.register("research_table", () -> woodBlock());
    public static final Supplier<Block> CRUCIBLE = BLOCKS.register("crucible", () -> cauldronLikeBlock());
    public static final Supplier<Block> SMELTER_BASIC = BLOCKS.register("smelter_basic", () -> furnaceLikeBlock());
    public static final Supplier<Block> WAND_WORKBENCH = BLOCKS.register("wand_workbench", () -> workbenchBlock());
    public static final Supplier<Block> INFUSION_MATRIX = BLOCKS.register("infusion_matrix", () -> stoneBlock(5.0F, 1200.0F));

    public static final Supplier<Block> LOG_GREATWOOD = BLOCKS.register("log_greatwood", () -> logBlock(false));
    public static final Supplier<Block> LOG_SILVERWOOD = BLOCKS.register("log_silverwood", () -> logBlock(true));

    public static final Supplier<Block> LEAVES_GREATWOOD = BLOCKS.register("leaves_greatwood", () -> leavesBlock());
    public static final Supplier<Block> LEAVES_SILVERWOOD = BLOCKS.register("leaves_silverwood", () -> leavesBlock());

    public static final Supplier<Block> SAPLING_GREATWOOD = BLOCKS.register("sapling_greatwood", () -> saplingBlock(TCSaplingBlock.Kind.GREATWOOD));
    public static final Supplier<Block> SAPLING_SILVERWOOD = BLOCKS.register("sapling_silverwood", () -> saplingBlock(TCSaplingBlock.Kind.SILVERWOOD));

    public static final Supplier<Block> SHIMMERLEAF = BLOCKS.register("shimmerleaf", () -> plantBlock(TCPlantBlock.Kind.SHIMMERLEAF, 6));
    public static final Supplier<Block> CINDERPEARL = BLOCKS.register("cinderpearl", () -> plantBlock(TCPlantBlock.Kind.CINDERPEARL, 7));
    public static final Supplier<Block> VISHROOM = BLOCKS.register("vishroom", () -> plantBlock(TCPlantBlock.Kind.VISHROOM, 6));

    public static final Supplier<Block> PLANK_GREATWOOD = BLOCKS.register("plank_greatwood", () -> woodBlock());
    public static final Supplier<Block> PLANK_SILVERWOOD = BLOCKS.register("plank_silverwood", () -> woodBlock());

    public static final Supplier<Block> STAIRS_GREATWOOD = BLOCKS.register("stairs_greatwood", () -> woodStairBlock(PLANK_GREATWOOD.get().defaultBlockState()));
    public static final Supplier<Block> STAIRS_SILVERWOOD = BLOCKS.register("stairs_silverwood", () -> woodStairBlock(PLANK_SILVERWOOD.get().defaultBlockState()));

    public static final Supplier<Block> SLAB_GREATWOOD = BLOCKS.register("slab_greatwood", () -> woodSlabBlock(1.2F, 2.0F));
    public static final Supplier<Block> SLAB_SILVERWOOD = BLOCKS.register("slab_silverwood", () -> woodSlabBlock(1.0F, 2.0F));

    private static Block stoneBlock(float strength, float resistance) {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .strength(strength, resistance)
                .requiresCorrectToolForDrops());
    }


    private static Block unbreakableStoneBlock(float resistance) {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .strength(-1.0F, resistance)
                .requiresCorrectToolForDrops());
    }
    private static Block woodBlock() {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                .strength(2.0F, 2.0F));
    }

    private static Block workbenchBlock() {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE)
                .strength(2.5F, 2.5F));
    }

    private static Block cauldronLikeBlock() {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)
                .strength(2.0F, 6.0F)
                .requiresCorrectToolForDrops());
    }

    private static Block furnaceLikeBlock() {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .strength(3.5F, 17.5F)
                .requiresCorrectToolForDrops());
    }

    private static Block metalBlock() {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .strength(5.0F, 10.0F)
                .requiresCorrectToolForDrops());
    }

    private static Block nitorBlock(int lightLevel) {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLOWSTONE)
                .strength(0.3F, 0.3F)
                .lightLevel(state -> lightLevel));
    }

    private static Block amberBlock() {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(0.5F, 2.0F)
                .sound(SoundType.STONE)
                .noOcclusion());
    }

    private static Block logBlock(boolean silverwood) {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
                .strength(2.0F, 5.0F);
        if (silverwood) {
            properties = properties.lightLevel(state -> 5);
        }
        return new TCLogBlock(properties);
    }

    private static Block leavesBlock() {
        return new TCLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                .strength(0.2F)
                .noOcclusion());
    }

    private static Block saplingBlock(TCSaplingBlock.Kind kind) {
        return new TCSaplingBlock(kind, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)
                .sound(SoundType.GRASS)
                .noCollission()
                .instabreak()
                .randomTicks()
                .offsetType(BlockBehaviour.OffsetType.NONE));
    }
    private static Block plantBlock(TCPlantBlock.Kind kind, int lightLevel) {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS)
                .sound(SoundType.GRASS)
                .noCollission()
                .instabreak()
                .offsetType(BlockBehaviour.OffsetType.NONE);

        if (kind == TCPlantBlock.Kind.SHIMMERLEAF || kind == TCPlantBlock.Kind.CINDERPEARL) {
            properties = properties.offsetType(BlockBehaviour.OffsetType.XZ);
        }

        if (lightLevel > 0) {
            properties = properties.lightLevel(state -> lightLevel);
        }

        return new TCPlantBlock(kind, properties);
    }

    private static Block stoneStairBlock(net.minecraft.world.level.block.state.BlockState baseState, float strength, float resistance) {
        return new StairBlock(baseState, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_STAIRS)
                .strength(strength, resistance)
                .requiresCorrectToolForDrops());
    }

    private static Block woodStairBlock(net.minecraft.world.level.block.state.BlockState baseState) {
        return new StairBlock(baseState, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)
                .strength(2.0F, 2.0F));
    }

    private static Block stoneSlabBlock(float strength, float resistance) {
        return new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_SLAB)
                .strength(strength, resistance)
                .requiresCorrectToolForDrops());
    }

    private static Block woodSlabBlock(float strength, float resistance) {
        return new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB)
                .strength(strength, resistance));
    }

    private static Block crystalPlaceholder() {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(0.25F, 0.0F)
                .noOcclusion()
                .noCollission()
                .lightLevel(state -> 1));
    }

    private TCBlocks() {
    }
}

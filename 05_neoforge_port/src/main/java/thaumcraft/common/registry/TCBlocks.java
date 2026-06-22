package thaumcraft.common.registry;

import thaumcraft.common.essentia.transport.block.TCLegacyTubeVariant;

import thaumcraft.common.essentia.transport.block.TCLegacyTubeBlock;

import thaumcraft.common.essentia.transport.block.TCLegacySmelterEndpointBlock;

import thaumcraft.common.essentia.transport.block.TCLegacySmelterEndpoint;

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
import thaumcraft.common.blocks.basic.TCTableBlock;
import thaumcraft.common.blocks.basic.TCInfusionCandleBlock;
import thaumcraft.common.blocks.essentia.TCSmelterBlock;
import thaumcraft.common.blocks.essentia.TCSmelterAuxBlock;
import thaumcraft.common.blocks.essentia.TCSmelterVentBlock;
import thaumcraft.common.blocks.essentia.TCWardedJarBlock;
import thaumcraft.common.blocks.essentia.TCAlembicBlock;
import thaumcraft.common.blocks.essentia.TCBellowsBlock;
import thaumcraft.common.blocks.crafting.TCArcaneWorkbenchBlock;
import thaumcraft.common.blocks.crafting.TCArcaneWorkbenchChargerBlock;
import thaumcraft.common.blocks.crafting.TCCrucibleBlock;
import thaumcraft.common.blocks.crafting.TCInfusionMatrixBlock;
import thaumcraft.common.blocks.crafting.TCInfusionModifierBlock;
import thaumcraft.common.blocks.crafting.TCInfusionPedestalBlock;
import thaumcraft.common.blocks.crafting.TCInfusionPillarBlock;
import thaumcraft.common.blocks.crafting.TCResearchTableBlock;
import thaumcraft.common.blocks.misc.TCNitorBlock;
import thaumcraft.common.blocks.world.taint.TCFluxGooBlock;
import thaumcraft.common.blocks.devices.TCInlayBlock;
import thaumcraft.common.blocks.devices.TCStabilizerBlock;

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
    public static final Supplier<Block> METAL_ALCHEMICAL = BLOCKS.register("metal_alchemical", () -> metalBlock());
    public static final Supplier<Block> METAL_ALCHEMICAL_ADVANCED = BLOCKS.register("metal_alchemical_advanced", () -> metalBlock());
    public static final Supplier<Block> NITOR_BLACK = BLOCKS.register("nitor_black", () -> nitorBlock(14));
    public static final Supplier<Block> NITOR_BLUE = BLOCKS.register("nitor_blue", () -> nitorBlock(14));
    public static final Supplier<Block> NITOR_BROWN = BLOCKS.register("nitor_brown", () -> nitorBlock(14));
    public static final Supplier<Block> NITOR_CYAN = BLOCKS.register("nitor_cyan", () -> nitorBlock(14));
    public static final Supplier<Block> NITOR_GRAY = BLOCKS.register("nitor_gray", () -> nitorBlock(14));
    public static final Supplier<Block> NITOR_GREEN = BLOCKS.register("nitor_green", () -> nitorBlock(14));
    public static final Supplier<Block> NITOR_YELLOW = BLOCKS.register("nitor_yellow", () -> nitorBlock(14));
    public static final Supplier<Block> NITOR_LIGHTBLUE = BLOCKS.register("nitor_lightblue", () -> nitorBlock(14));
    public static final Supplier<Block> NITOR_LIME = BLOCKS.register("nitor_lime", () -> nitorBlock(14));
    public static final Supplier<Block> NITOR_MAGENTA = BLOCKS.register("nitor_magenta", () -> nitorBlock(14));
    public static final Supplier<Block> NITOR_ORANGE = BLOCKS.register("nitor_orange", () -> nitorBlock(14));
    public static final Supplier<Block> NITOR_PINK = BLOCKS.register("nitor_pink", () -> nitorBlock(14));
    public static final Supplier<Block> NITOR_PURPLE = BLOCKS.register("nitor_purple", () -> nitorBlock(14));
    public static final Supplier<Block> NITOR_RED = BLOCKS.register("nitor_red", () -> nitorBlock(14));
    public static final Supplier<Block> NITOR_SILVER = BLOCKS.register("nitor_silver", () -> nitorBlock(14));
    public static final Supplier<Block> NITOR_WHITE = BLOCKS.register("nitor_white", () -> nitorBlock(14));
    public static final Supplier<Block> CANDLE_BLACK = BLOCKS.register("candle_black", () -> tallowCandleBlock(10));
    public static final Supplier<Block> CANDLE_BLUE = BLOCKS.register("candle_blue", () -> tallowCandleBlock(10));
    public static final Supplier<Block> CANDLE_BROWN = BLOCKS.register("candle_brown", () -> tallowCandleBlock(10));
    public static final Supplier<Block> CANDLE_CYAN = BLOCKS.register("candle_cyan", () -> tallowCandleBlock(10));
    public static final Supplier<Block> CANDLE_GRAY = BLOCKS.register("candle_gray", () -> tallowCandleBlock(10));
    public static final Supplier<Block> CANDLE_GREEN = BLOCKS.register("candle_green", () -> tallowCandleBlock(10));
    public static final Supplier<Block> CANDLE_LIGHTBLUE = BLOCKS.register("candle_lightblue", () -> tallowCandleBlock(10));
    public static final Supplier<Block> CANDLE_LIME = BLOCKS.register("candle_lime", () -> tallowCandleBlock(10));
    public static final Supplier<Block> CANDLE_MAGENTA = BLOCKS.register("candle_magenta", () -> tallowCandleBlock(10));
    public static final Supplier<Block> CANDLE_ORANGE = BLOCKS.register("candle_orange", () -> tallowCandleBlock(10));
    public static final Supplier<Block> CANDLE_PINK = BLOCKS.register("candle_pink", () -> tallowCandleBlock(10));
    public static final Supplier<Block> CANDLE_PURPLE = BLOCKS.register("candle_purple", () -> tallowCandleBlock(10));
    public static final Supplier<Block> CANDLE_RED = BLOCKS.register("candle_red", () -> tallowCandleBlock(10));
    public static final Supplier<Block> CANDLE_SILVER = BLOCKS.register("candle_silver", () -> tallowCandleBlock(10));
    public static final Supplier<Block> CANDLE_WHITE = BLOCKS.register("candle_white", () -> tallowCandleBlock(10));
    public static final Supplier<Block> CANDLE_YELLOW = BLOCKS.register("candle_yellow", () -> tallowCandleBlock(10));

    public static final Supplier<Block> TABLE_WOOD = BLOCKS.register("table_wood", () -> tableBlock(true));
    public static final Supplier<Block> TABLE_STONE = BLOCKS.register("table_stone", () -> tableBlock(false));
    public static final Supplier<Block> ARCANE_WORKBENCH = BLOCKS.register("arcane_workbench", () -> arcaneWorkbenchBlock());
    public static final Supplier<Block> ARCANE_WORKBENCH_CHARGER = BLOCKS.register("arcane_workbench_charger", () -> arcaneWorkbenchChargerBlock());
    public static final Supplier<Block> RESEARCH_TABLE = BLOCKS.register("research_table", () -> researchTableBlock());
    public static final Supplier<Block> CRUCIBLE = BLOCKS.register("crucible", () -> crucibleBlock());
    public static final Supplier<Block> SMELTER_BASIC = BLOCKS.register("smelter_basic", () -> smelterBlock());
    public static final Supplier<Block> SMELTER_VENT = BLOCKS.register("smelter_vent",
            () -> new TCSmelterVentBlock(BlockBehaviour.Properties.of()
                    .strength(1.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));
    public static final Supplier<Block> SMELTER_AUX = BLOCKS.register("smelter_aux",
            () -> new TCSmelterAuxBlock(BlockBehaviour.Properties.of()
                    .strength(1.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));
public static final Supplier<Block> BELLOWS = BLOCKS.register("bellows",
            () -> new TCBellowsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    .strength(1.5F, 3.0F)));
public static final Supplier<Block> JAR_NORMAL = BLOCKS.register("jar_normal", () -> wardedJarBlock());
    public static final Supplier<Block> ALEMBIC = BLOCKS.register("alembic", () -> alembicBlock());
    // Legacy-aligned transport/essentia blocks.
public static final Supplier<Block> TUBE = BLOCKS.register("tube", () -> tubeBlock(TCLegacyTubeVariant.TUBE));
public static final Supplier<Block> TUBE_BUFFER = BLOCKS.register("tube_buffer", () -> tubeBlock(TCLegacyTubeVariant.BUFFER));
public static final Supplier<Block> TUBE_FILTER = BLOCKS.register("tube_filter", () -> tubeBlock(TCLegacyTubeVariant.FILTER));
public static final Supplier<Block> TUBE_ONEWAY = BLOCKS.register("tube_oneway", () -> tubeBlock(TCLegacyTubeVariant.ONEWAY));
public static final Supplier<Block> TUBE_RESTRICT = BLOCKS.register("tube_restrict", () -> tubeBlock(TCLegacyTubeVariant.RESTRICT));
public static final Supplier<Block> TUBE_VALVE = BLOCKS.register("tube_valve", () -> tubeBlock(TCLegacyTubeVariant.VALVE));
    public static final Supplier<Block> SMELTER_THAUMIUM = BLOCKS.register("smelter_thaumium", () -> smelterEndpointBlock(TCLegacySmelterEndpoint.THAUMIUM));
    public static final Supplier<Block> SMELTER_VOID = BLOCKS.register("smelter_void", () -> smelterEndpointBlock(TCLegacySmelterEndpoint.VOID));
    public static final Supplier<Block> WAND_WORKBENCH = BLOCKS.register("wand_workbench", () -> workbenchBlock());
    public static final Supplier<Block> INFUSION_MATRIX = BLOCKS.register("infusion_matrix", () -> infusionMatrixBlock());
    public static final Supplier<Block> PILLAR_ARCANE = BLOCKS.register("pillar_arcane", () -> infusionPillarBlock());
    public static final Supplier<Block> PILLAR_ANCIENT = BLOCKS.register("pillar_ancient", () -> infusionPillarBlock());
    public static final Supplier<Block> PILLAR_ELDRITCH = BLOCKS.register("pillar_eldritch", () -> infusionPillarBlock());
    public static final Supplier<Block> MATRIX_SPEED = BLOCKS.register("matrix_speed", () -> infusionModifierBlock());
    public static final Supplier<Block> MATRIX_COST = BLOCKS.register("matrix_cost", () -> infusionModifierBlock());
    public static final Supplier<Block> ARCANE_PEDESTAL = BLOCKS.register("arcane_pedestal", () -> infusionPedestalBlock(2.0F, 10.0F));
    public static final Supplier<Block> ANCIENT_PEDESTAL = BLOCKS.register("ancient_pedestal", () -> infusionPedestalBlock(2.0F, 10.0F));
    public static final Supplier<Block> ELDRITCH_PEDESTAL = BLOCKS.register("eldritch_pedestal", () -> infusionPedestalBlock(15.0F, 1000.0F));
    public static final Supplier<Block> GOLEM_BUILDER = BLOCKS.register("golem_builder", () -> golemBuilderBlock());
    public static final Supplier<Block> INLAY = BLOCKS.register("inlay", () -> inlayBlock());
    public static final Supplier<Block> STABILIZER = BLOCKS.register("stabilizer", () -> stabilizerBlock());
    public static final Supplier<Block> FLUX_GOO = BLOCKS.register("flux_goo", () -> fluxGooBlock());

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

    private static Block arcaneWorkbenchBlock() {
        return new TCArcaneWorkbenchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE)
                .strength(2.5F, 2.5F)
                .noOcclusion());
    }

    private static Block arcaneWorkbenchChargerBlock() {
        return new TCArcaneWorkbenchChargerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                .strength(1.25F, 10.0F)
                .noOcclusion());
    }

    private static Block tableBlock(boolean wood) {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.ofFullCopy(wood ? Blocks.OAK_PLANKS : Blocks.STONE)
                .strength(wood ? 2.0F : 2.5F, wood ? 2.0F : 2.5F)
                .noOcclusion();
        return new TCTableBlock(properties, wood);
    }

    private static Block researchTableBlock() {
        return new TCResearchTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                .strength(2.0F, 2.0F)
                .noOcclusion());
    }

    private static Block crucibleBlock() {
        return new TCCrucibleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)
                .strength(2.0F, 6.0F)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }

    private static Block fluxGooBlock() {
        return new TCFluxGooBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                .randomTicks()
                .noCollission()
                .noOcclusion()
                .strength(100.0F));
    }

    private static Block inlayBlock() {
        return new TCInlayBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .strength(0.5F)
                .sound(SoundType.METAL)
                .noCollission()
                .noOcclusion());
    }

    private static Block stabilizerBlock() {
        return new TCStabilizerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .strength(2.0F, 10.0F)
                .sound(SoundType.METAL)
                .noOcclusion());
    }

    private static Block infusionMatrixBlock() {
        return new TCInfusionMatrixBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .strength(5.0F, 1200.0F)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }

    private static Block infusionModifierBlock() {
        return new TCInfusionModifierBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .strength(2.0F, 10.0F)
                .requiresCorrectToolForDrops());
    }

    private static Block infusionPedestalBlock(float strength, float resistance) {
        return new TCInfusionPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .strength(strength, resistance)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }

    private static Block infusionPillarBlock() {
        return new TCInfusionPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .strength(2.5F)
                .sound(SoundType.STONE)
                .noOcclusion());
    }

    private static Block furnaceLikeBlock() {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .strength(3.5F, 17.5F)
                .requiresCorrectToolForDrops());
    }

    private static Block smelterBlock() {
        return new TCSmelterBlock(smelterProperties());
    }

    private static Block alembicBlock() {
        return new TCAlembicBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                .strength(1.5F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }
    private static Block wardedJarBlock() {
        return new TCWardedJarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(0.3F, 0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion());
    }

    private static BlockBehaviour.Properties smelterProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)
                .strength(3.5F, 17.5F)
                .requiresCorrectToolForDrops()
                .lightLevel(state -> state.getValue(TCSmelterBlock.ENABLED) ? 13 : 0);
    }

    private static Block metalBlock() {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .strength(5.0F, 10.0F)
                .requiresCorrectToolForDrops());
    }

    private static Block nitorBlock(int lightLevel) {
        return new TCNitorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLOWSTONE)
                .strength(0.1F, 0.1F)
                .noOcclusion()
                .lightLevel(state -> lightLevel));
    }

    private static Block golemBuilderBlock() {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .strength(1.5F, 2.0F)
                .sound(SoundType.STONE)
                .noOcclusion());
    }

    private static Block tallowCandleBlock(int ignoredLightLevel) {
        return new TCInfusionCandleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE)
                .strength(0.1F, 0.1F)
                .sound(SoundType.WOOL)
                .noOcclusion()
                .lightLevel(state -> 15));
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


    private static Block tubeBlock(TCLegacyTubeVariant variant) {
        return new TCLegacyTubeBlock(variant, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(0.3F, 0.3F)
                .noOcclusion());
    }

    private static Block smelterEndpointBlock(TCLegacySmelterEndpoint endpoint) {
        return new TCLegacySmelterEndpointBlock(endpoint, smelterProperties());
    }
    private TCBlocks() {
    }
}

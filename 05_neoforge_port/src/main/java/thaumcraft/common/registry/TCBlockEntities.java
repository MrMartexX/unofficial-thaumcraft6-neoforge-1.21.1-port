package thaumcraft.common.registry;

import thaumcraft.common.essentia.transport.blockentity.TCLegacyTubeBlockEntity;

import thaumcraft.common.essentia.transport.blockentity.TCLegacySmelterEndpointBlockEntity;

import thaumcraft.common.essentia.transport.block.TCLegacyTubeVariant;

import thaumcraft.common.essentia.transport.block.TCLegacySmelterEndpoint;

import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.core.BlockPos;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.tiles.crafting.TCArcaneWorkbenchBlockEntity;
import thaumcraft.common.tiles.crafting.TCCrucibleBlockEntity;
import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;
import thaumcraft.common.tiles.crafting.TCInfusionPedestalBlockEntity;
import thaumcraft.common.tiles.crafting.TCFocalManipulatorBlockEntity;
import thaumcraft.common.tiles.essentia.TCWardedJarBlockEntity;
import thaumcraft.common.tiles.essentia.TCSmelterBlockEntity;
import thaumcraft.common.tiles.essentia.TCAlembicBlockEntity;
import thaumcraft.common.tiles.essentia.TCBellowsBlockEntity;
import thaumcraft.common.tiles.essentia.TCEssentiaTransfuserBlockEntity;
import thaumcraft.common.tiles.crafting.TCResearchTableBlockEntity;
import thaumcraft.common.tiles.crafting.TCThaumatoriumBlockEntity;
import thaumcraft.common.tiles.crafting.TCThaumatoriumTopBlockEntity;
import thaumcraft.common.tiles.misc.TCNitorBlockEntity;
import thaumcraft.common.tiles.devices.TCStabilizerBlockEntity;
import thaumcraft.common.tiles.devices.TCMirrorEssentiaBlockEntity;

public final class TCBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Thaumcraft.MODID);

    public static final Supplier<BlockEntityType<TCResearchTableBlockEntity>> RESEARCH_TABLE =
            BLOCK_ENTITY_TYPES.register("research_table", () ->
                    BlockEntityType.Builder.of(TCResearchTableBlockEntity::new, TCBlocks.RESEARCH_TABLE.get()).build(null));
    public static final Supplier<BlockEntityType<TCArcaneWorkbenchBlockEntity>> ARCANE_WORKBENCH =
            BLOCK_ENTITY_TYPES.register("arcane_workbench", () ->
                    BlockEntityType.Builder.of(TCArcaneWorkbenchBlockEntity::new, TCBlocks.ARCANE_WORKBENCH.get()).build(null));
    public static final Supplier<BlockEntityType<TCFocalManipulatorBlockEntity>> FOCAL_MANIPULATOR =
            BLOCK_ENTITY_TYPES.register("wand_workbench", () ->
                    BlockEntityType.Builder.of(TCFocalManipulatorBlockEntity::new, TCBlocks.WAND_WORKBENCH.get()).build(null));
    public static final Supplier<BlockEntityType<TCCrucibleBlockEntity>> CRUCIBLE =
            BLOCK_ENTITY_TYPES.register("crucible", () ->
                    BlockEntityType.Builder.of(TCCrucibleBlockEntity::new, TCBlocks.CRUCIBLE.get()).build(null));
    public static final Supplier<BlockEntityType<TCSmelterBlockEntity>> SMELTER_BASIC =
            BLOCK_ENTITY_TYPES.register("smelter_basic", () ->
                    BlockEntityType.Builder.of(TCSmelterBlockEntity::new, TCBlocks.SMELTER_BASIC.get()).build(null));
    public static final Supplier<BlockEntityType<TCBellowsBlockEntity>> BELLOWS =
            BLOCK_ENTITY_TYPES.register("bellows", () ->
                    BlockEntityType.Builder.of(TCBellowsBlockEntity::new, TCBlocks.BELLOWS.get()).build(null));
    public static final Supplier<BlockEntityType<TCWardedJarBlockEntity>> WARDED_JAR =
            BLOCK_ENTITY_TYPES.register("jar_normal", () ->
                    BlockEntityType.Builder.of(TCWardedJarBlockEntity::new, TCBlocks.JAR_NORMAL.get()).build(null));
    public static final Supplier<BlockEntityType<TCWardedJarBlockEntity>> JAR_VOID =
            BLOCK_ENTITY_TYPES.register("jar_void", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TCWardedJarBlockEntity(
                                    TCBlockEntities.JAR_VOID.get(),
                                    pos,
                                    state,
                                    TCWardedJarBlockEntity.Kind.VOID),
                            TCBlocks.JAR_VOID.get()).build(null));
    public static final Supplier<BlockEntityType<TCAlembicBlockEntity>> ALEMBIC =
            BLOCK_ENTITY_TYPES.register("alembic", () ->
                    BlockEntityType.Builder.of(TCAlembicBlockEntity::new, TCBlocks.ALEMBIC.get()).build(null));
    public static final Supplier<BlockEntityType<TCEssentiaTransfuserBlockEntity>> ESSENTIA_TRANSPORT_IN =
            BLOCK_ENTITY_TYPES.register("essentiatransportin", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TCEssentiaTransfuserBlockEntity(
                                    TCBlockEntities.ESSENTIA_TRANSPORT_IN.get(),
                                    pos,
                                    state,
                                    TCEssentiaTransfuserBlockEntity.Kind.INPUT),
                            TCBlocks.ESSENTIA_TRANSPORT_IN.get()).build(null));
    public static final Supplier<BlockEntityType<TCEssentiaTransfuserBlockEntity>> ESSENTIA_TRANSPORT_OUT =
            BLOCK_ENTITY_TYPES.register("essentiatransportout", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TCEssentiaTransfuserBlockEntity(
                                    TCBlockEntities.ESSENTIA_TRANSPORT_OUT.get(),
                                    pos,
                                    state,
                                    TCEssentiaTransfuserBlockEntity.Kind.OUTPUT),
                            TCBlocks.ESSENTIA_TRANSPORT_OUT.get()).build(null));
    public static final Supplier<BlockEntityType<TCInfusionMatrixBlockEntity>> INFUSION_MATRIX =
            BLOCK_ENTITY_TYPES.register("infusion_matrix", () ->
                    BlockEntityType.Builder.of(TCInfusionMatrixBlockEntity::new, TCBlocks.INFUSION_MATRIX.get()).build(null));
    public static final Supplier<BlockEntityType<TCInfusionPedestalBlockEntity>> INFUSION_PEDESTAL =
            BLOCK_ENTITY_TYPES.register("infusion_pedestal", () ->
                    BlockEntityType.Builder.of(
                            TCInfusionPedestalBlockEntity::new,
                            TCBlocks.ARCANE_PEDESTAL.get(),
                            TCBlocks.ANCIENT_PEDESTAL.get(),
                            TCBlocks.ELDRITCH_PEDESTAL.get()).build(null));
    public static final Supplier<BlockEntityType<TCStabilizerBlockEntity>> STABILIZER =
            BLOCK_ENTITY_TYPES.register("stabilizer", () ->
                    BlockEntityType.Builder.of(TCStabilizerBlockEntity::new, TCBlocks.STABILIZER.get()).build(null));
    public static final Supplier<BlockEntityType<TCMirrorEssentiaBlockEntity>> MIRROR_ESSENTIA =
            BLOCK_ENTITY_TYPES.register("mirror_essentia", () ->
                    BlockEntityType.Builder.of(TCMirrorEssentiaBlockEntity::new, TCBlocks.MIRROR_ESSENTIA.get()).build(null));
    public static final Supplier<BlockEntityType<TCThaumatoriumBlockEntity>> THAUMATORIUM =
            BLOCK_ENTITY_TYPES.register("thaumatorium", () ->
                    BlockEntityType.Builder.of(TCThaumatoriumBlockEntity::new, TCBlocks.THAUMATORIUM.get()).build(null));
    public static final Supplier<BlockEntityType<TCThaumatoriumTopBlockEntity>> THAUMATORIUM_TOP =
            BLOCK_ENTITY_TYPES.register("thaumatorium_top", () ->
                    BlockEntityType.Builder.of(TCThaumatoriumTopBlockEntity::new, TCBlocks.THAUMATORIUM_TOP.get()).build(null));

    public static final Supplier<BlockEntityType<TCNitorBlockEntity>> NITOR =
            BLOCK_ENTITY_TYPES.register("nitor", () ->
                    BlockEntityType.Builder.of(TCNitorBlockEntity::new,
                            TCBlocks.NITOR_BLACK.get(),
                            TCBlocks.NITOR_BLUE.get(),
                            TCBlocks.NITOR_BROWN.get(),
                            TCBlocks.NITOR_CYAN.get(),
                            TCBlocks.NITOR_GRAY.get(),
                            TCBlocks.NITOR_GREEN.get(),
                            TCBlocks.NITOR_YELLOW.get(),
                            TCBlocks.NITOR_LIGHTBLUE.get(),
                            TCBlocks.NITOR_LIME.get(),
                            TCBlocks.NITOR_MAGENTA.get(),
                            TCBlocks.NITOR_ORANGE.get(),
                            TCBlocks.NITOR_PINK.get(),
                            TCBlocks.NITOR_PURPLE.get(),
                            TCBlocks.NITOR_RED.get(),
                            TCBlocks.NITOR_SILVER.get(),
                            TCBlocks.NITOR_WHITE.get()).build(null));

    // Legacy-aligned transport/essentia block entities.
    public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE =
            BLOCK_ENTITY_TYPES.register("tube", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE.get(), pos, state, TCLegacyTubeVariant.TUBE),
                            TCBlocks.TUBE.get()).build(null));
    public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_BUFFER =
            BLOCK_ENTITY_TYPES.register("tube_buffer", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_BUFFER.get(), pos, state, TCLegacyTubeVariant.BUFFER),
                            TCBlocks.TUBE_BUFFER.get()).build(null));
    public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_FILTER =
            BLOCK_ENTITY_TYPES.register("tube_filter", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_FILTER.get(), pos, state, TCLegacyTubeVariant.FILTER),
                            TCBlocks.TUBE_FILTER.get()).build(null));
    public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_ONEWAY =
            BLOCK_ENTITY_TYPES.register("tube_oneway", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_ONEWAY.get(), pos, state, TCLegacyTubeVariant.ONEWAY),
                            TCBlocks.TUBE_ONEWAY.get()).build(null));
    public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_RESTRICT =
            BLOCK_ENTITY_TYPES.register("tube_restrict", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_RESTRICT.get(), pos, state, TCLegacyTubeVariant.RESTRICT),
                            TCBlocks.TUBE_RESTRICT.get()).build(null));
    public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_VALVE =
            BLOCK_ENTITY_TYPES.register("tube_valve", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_VALVE.get(), pos, state, TCLegacyTubeVariant.VALVE),
                            TCBlocks.TUBE_VALVE.get()).build(null));
    public static final Supplier<BlockEntityType<TCLegacySmelterEndpointBlockEntity>> SMELTER_THAUMIUM =
            BLOCK_ENTITY_TYPES.register("smelter_thaumium", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TCLegacySmelterEndpointBlockEntity(TCBlockEntities.SMELTER_THAUMIUM.get(), pos, state, TCLegacySmelterEndpoint.THAUMIUM),
                            TCBlocks.SMELTER_THAUMIUM.get()).build(null));
    public static final Supplier<BlockEntityType<TCLegacySmelterEndpointBlockEntity>> SMELTER_VOID =
            BLOCK_ENTITY_TYPES.register("smelter_void", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TCLegacySmelterEndpointBlockEntity(TCBlockEntities.SMELTER_VOID.get(), pos, state, TCLegacySmelterEndpoint.VOID),
                            TCBlocks.SMELTER_VOID.get()).build(null));

    public static TCLegacyTubeBlockEntity createTubeBlockEntity(TCLegacyTubeVariant variant, BlockPos pos, BlockState state) {
        return switch (variant) {
            case TUBE -> new TCLegacyTubeBlockEntity(TUBE.get(), pos, state, variant);
            case BUFFER -> new TCLegacyTubeBlockEntity(TUBE_BUFFER.get(), pos, state, variant);
            case FILTER -> new TCLegacyTubeBlockEntity(TUBE_FILTER.get(), pos, state, variant);
            case ONEWAY -> new TCLegacyTubeBlockEntity(TUBE_ONEWAY.get(), pos, state, variant);
            case RESTRICT -> new TCLegacyTubeBlockEntity(TUBE_RESTRICT.get(), pos, state, variant);
            case VALVE -> new TCLegacyTubeBlockEntity(TUBE_VALVE.get(), pos, state, variant);
        };
    }

    public static BlockEntityType<TCLegacyTubeBlockEntity> typeForTube(TCLegacyTubeVariant variant) {
        return switch (variant) {
            case TUBE -> TUBE.get();
            case BUFFER -> TUBE_BUFFER.get();
            case FILTER -> TUBE_FILTER.get();
            case ONEWAY -> TUBE_ONEWAY.get();
            case RESTRICT -> TUBE_RESTRICT.get();
            case VALVE -> TUBE_VALVE.get();
        };
    }

    public static TCLegacySmelterEndpointBlockEntity createSmelterEndpointBlockEntity(TCLegacySmelterEndpoint endpoint, BlockPos pos, BlockState state) {
        return switch (endpoint) {
            case THAUMIUM -> new TCLegacySmelterEndpointBlockEntity(SMELTER_THAUMIUM.get(), pos, state, endpoint);
            case VOID -> new TCLegacySmelterEndpointBlockEntity(SMELTER_VOID.get(), pos, state, endpoint);
        };
    }

    public static TCEssentiaTransfuserBlockEntity createEssentiaTransfuserBlockEntity(
            TCEssentiaTransfuserBlockEntity.Kind kind,
            BlockPos pos,
            BlockState state
    ) {
        return switch (kind) {
            case INPUT -> new TCEssentiaTransfuserBlockEntity(ESSENTIA_TRANSPORT_IN.get(), pos, state, kind);
            case OUTPUT -> new TCEssentiaTransfuserBlockEntity(ESSENTIA_TRANSPORT_OUT.get(), pos, state, kind);
        };
    }

    public static BlockEntityType<TCEssentiaTransfuserBlockEntity> typeForEssentiaTransfuser(
            TCEssentiaTransfuserBlockEntity.Kind kind
    ) {
        return switch (kind) {
            case INPUT -> ESSENTIA_TRANSPORT_IN.get();
            case OUTPUT -> ESSENTIA_TRANSPORT_OUT.get();
        };
    }

    public static TCWardedJarBlockEntity createWardedJarBlockEntity(
            TCWardedJarBlockEntity.Kind kind,
            BlockPos pos,
            BlockState state
    ) {
        return switch (kind) {
            case NORMAL -> new TCWardedJarBlockEntity(WARDED_JAR.get(), pos, state, kind);
            case VOID -> new TCWardedJarBlockEntity(JAR_VOID.get(), pos, state, kind);
        };
    }

    public static BlockEntityType<TCWardedJarBlockEntity> typeForWardedJar(TCWardedJarBlockEntity.Kind kind) {
        return switch (kind) {
            case NORMAL -> WARDED_JAR.get();
            case VOID -> JAR_VOID.get();
        };
    }
    private TCBlockEntities() {
    }
}

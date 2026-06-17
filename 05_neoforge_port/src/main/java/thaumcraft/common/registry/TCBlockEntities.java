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
import thaumcraft.common.tiles.crafting.TCResearchTableBlockEntity;
import thaumcraft.common.tiles.misc.TCNitorBlockEntity;

public final class TCBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Thaumcraft.MODID);

    public static final Supplier<BlockEntityType<TCResearchTableBlockEntity>> RESEARCH_TABLE =
            BLOCK_ENTITY_TYPES.register("research_table", () ->
                    BlockEntityType.Builder.of(TCResearchTableBlockEntity::new, TCBlocks.RESEARCH_TABLE.get()).build(null));
    public static final Supplier<BlockEntityType<TCArcaneWorkbenchBlockEntity>> ARCANE_WORKBENCH =
            BLOCK_ENTITY_TYPES.register("arcane_workbench", () ->
                    BlockEntityType.Builder.of(TCArcaneWorkbenchBlockEntity::new, TCBlocks.ARCANE_WORKBENCH.get()).build(null));

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
            BLOCK_ENTITY_TYPES.register("tubebuffer", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_BUFFER.get(), pos, state, TCLegacyTubeVariant.BUFFER),
                            TCBlocks.TUBE_BUFFER.get()).build(null));
    public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_FILTER =
            BLOCK_ENTITY_TYPES.register("tubefilter", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_FILTER.get(), pos, state, TCLegacyTubeVariant.FILTER),
                            TCBlocks.TUBE_FILTER.get()).build(null));
    public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_ONEWAY =
            BLOCK_ENTITY_TYPES.register("tubeoneway", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_ONEWAY.get(), pos, state, TCLegacyTubeVariant.ONEWAY),
                            TCBlocks.TUBE_ONEWAY.get()).build(null));
    public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_RESTRICT =
            BLOCK_ENTITY_TYPES.register("tuberestrict", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_RESTRICT.get(), pos, state, TCLegacyTubeVariant.RESTRICT),
                            TCBlocks.TUBE_RESTRICT.get()).build(null));
    public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_VALVE =
            BLOCK_ENTITY_TYPES.register("tubevalve", () ->
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

    public static TCLegacySmelterEndpointBlockEntity createSmelterEndpointBlockEntity(TCLegacySmelterEndpoint endpoint, BlockPos pos, BlockState state) {
        return switch (endpoint) {
            case THAUMIUM -> new TCLegacySmelterEndpointBlockEntity(SMELTER_THAUMIUM.get(), pos, state, endpoint);
            case VOID -> new TCLegacySmelterEndpointBlockEntity(SMELTER_VOID.get(), pos, state, endpoint);
        };
    }
    private TCBlockEntities() {
    }
}

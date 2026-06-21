# Smelter Variant / Endpoint Boundary Audit

Generated: 2026-06-22 01:19:55 +03:00

## Summary

| Category | Rows |
|---|---:|
| current_basic_smelter_be | 37 |
| current_basic_smelter_block | 16 |
| current_block_entity_registry | 16 |
| current_endpoint_be | 3 |
| current_endpoint_block | 4 |
| current_endpoint_enum | 16 |
| current_item_registry | 6 |
| current_registry | 10 |
| legacy_variant_semantics | 10 |
| total | 118 |

## Interpretation

- Legacy `smelterThaumium` and `smelterVoid` are real smelter machine variants: `getType()` selects type, `getEfficiency()` changes aspect loss, and `getSpeed()` changes Alembic output cadence.
- Current port has `TCSmelterBlockEntity.SmelterType` and static type constants, but only `SMELTER_BASIC` currently owns the basic smelter block entity/ticker path.
- Current `SMELTER_THAUMIUM` and `SMELTER_VOID` are still registered through the legacy transport endpoint block/entity boundary, so converting them into real machine variants must not silently break existing transport audits.
- Next safe implementation slice should add an explicit bridge plan: either make endpoint block entities subclass/own the machine state, or split transport endpoint placeholders away from actual smelter variants before enabling type-specific runtime.

## Evidence

| File | Line | Category | Evidence |
|---|---:|---|---|
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 7 | current_registry | `import thaumcraft.common.essentia.transport.block.TCLegacySmelterEndpointBlock;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 28 | current_registry | `import thaumcraft.common.blocks.essentia.TCSmelterBlock;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 125 | current_registry | `public static final Supplier<Block> SMELTER_BASIC = BLOCKS.register("smelter_basic", () -> smelterBlock());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 126 | current_registry | `public static final Supplier<Block> SMELTER_VENT = BLOCKS.register("smelter_vent",` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 139 | current_registry | `public static final Supplier<Block> SMELTER_THAUMIUM = BLOCKS.register("smelter_thaumium", () -> smelterEndpointBlock(TCLegacySmelterEndpoint.THAUMIUM));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 140 | current_registry | `public static final Supplier<Block> SMELTER_VOID = BLOCKS.register("smelter_void", () -> smelterEndpointBlock(TCLegacySmelterEndpoint.VOID));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 289 | current_registry | `return new TCSmelterBlock(smelterProperties());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 310 | current_registry | `.lightLevel(state -> state.getValue(TCSmelterBlock.ENABLED) ? 13 : 0);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 426 | current_registry | `private static Block smelterEndpointBlock(TCLegacySmelterEndpoint endpoint) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 427 | current_registry | `return new TCLegacySmelterEndpointBlock(endpoint, smelterProperties());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 105 | current_item_registry | `public static final Supplier<BlockItem> SMELTER_BASIC = blockItem("smelter_basic", TCBlocks.SMELTER_BASIC);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 106 | current_item_registry | `public static final Supplier<BlockItem> SMELTER_VENT = blockItem("smelter_vent", TCBlocks.SMELTER_VENT);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 110 | current_item_registry | `public static final Supplier<BlockItem> SMELTER_THAUMIUM = blockItem("smelter_thaumium", TCBlocks.SMELTER_THAUMIUM);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 111 | current_item_registry | `public static final Supplier<BlockItem> SMELTER_VOID = blockItem("smelter_void", TCBlocks.SMELTER_VOID);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 530 | current_item_registry | `public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_ESSENTIASMELTERTHAUMIUM = SMELTER_THAUMIUM;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 531 | current_item_registry | `public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_ESSENTIASMELTERVOID = SMELTER_VOID;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 5 | current_block_entity_registry | `import thaumcraft.common.essentia.transport.blockentity.TCLegacySmelterEndpointBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 25 | current_block_entity_registry | `import thaumcraft.common.tiles.essentia.TCSmelterBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 44 | current_block_entity_registry | `public static final Supplier<BlockEntityType<TCSmelterBlockEntity>> SMELTER_BASIC =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 45 | current_block_entity_registry | `BLOCK_ENTITY_TYPES.register("smelter_basic", () ->` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 46 | current_block_entity_registry | `BlockEntityType.Builder.of(TCSmelterBlockEntity::new, TCBlocks.SMELTER_BASIC.get()).build(null));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 118 | current_block_entity_registry | `public static final Supplier<BlockEntityType<TCLegacySmelterEndpointBlockEntity>> SMELTER_THAUMIUM =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 119 | current_block_entity_registry | `BLOCK_ENTITY_TYPES.register("smelter_thaumium", () ->` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 121 | current_block_entity_registry | `(pos, state) -> new TCLegacySmelterEndpointBlockEntity(TCBlockEntities.SMELTER_THAUMIUM.get(), pos, state, TCLegacySmelterEndpoint.THAUMIUM),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 122 | current_block_entity_registry | `TCBlocks.SMELTER_THAUMIUM.get()).build(null));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 123 | current_block_entity_registry | `public static final Supplier<BlockEntityType<TCLegacySmelterEndpointBlockEntity>> SMELTER_VOID =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 124 | current_block_entity_registry | `BLOCK_ENTITY_TYPES.register("smelter_void", () ->` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 126 | current_block_entity_registry | `(pos, state) -> new TCLegacySmelterEndpointBlockEntity(TCBlockEntities.SMELTER_VOID.get(), pos, state, TCLegacySmelterEndpoint.VOID),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 127 | current_block_entity_registry | `TCBlocks.SMELTER_VOID.get()).build(null));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 151 | current_block_entity_registry | `public static TCLegacySmelterEndpointBlockEntity createSmelterEndpointBlockEntity(TCLegacySmelterEndpoint endpoint, BlockPos pos, BlockState state) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 153 | current_block_entity_registry | `case THAUMIUM -> new TCLegacySmelterEndpointBlockEntity(SMELTER_THAUMIUM.get(), pos, state, endpoint);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 154 | current_block_entity_registry | `case VOID -> new TCLegacySmelterEndpointBlockEntity(SMELTER_VOID.get(), pos, state, endpoint);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 26 | current_basic_smelter_block | `public class TCSmelterBlock extends Block implements EntityBlock {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 27 | current_basic_smelter_block | `public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 28 | current_basic_smelter_block | `public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 33 | current_basic_smelter_block | `.setValue(FACING, Direction.NORTH)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 34 | current_basic_smelter_block | `.setValue(ENABLED, Boolean.FALSE));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 40 | current_basic_smelter_block | `public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 47 | current_basic_smelter_block | `public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 48 | current_basic_smelter_block | `if (level.isClientSide \|\| type != TCBlockEntities.SMELTER_BASIC.get()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 52 | current_basic_smelter_block | `TCSmelterBlockEntity.serverTick(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 62 | current_basic_smelter_block | `.setValue(FACING, context.getHorizontalDirection().getOpposite())` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 63 | current_basic_smelter_block | `.setValue(ENABLED, Boolean.FALSE);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 68 | current_basic_smelter_block | `builder.add(FACING, ENABLED);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 73 | current_basic_smelter_block | `return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 78 | current_basic_smelter_block | `return state.rotate(mirror.getRotation(state.getValue(FACING)));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 83 | current_basic_smelter_block | `if (!state.getValue(ENABLED)) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 93 | current_basic_smelter_block | `switch (state.getValue(FACING)) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 30 | current_basic_smelter_be | `* This class now owns basic fuel/cook progression and input item aspect conversion. It still` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 51 | current_basic_smelter_be | `private int pendingFlux;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 54 | current_basic_smelter_be | `super(TCBlockEntities.SMELTER_BASIC.get(), pos, state);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 61 | current_basic_smelter_be | `public void setStoredItemForValidation(int slot, ItemStack stack) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 104 | current_basic_smelter_be | `public int pendingFlux() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 105 | current_basic_smelter_be | `return pendingFlux;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 112 | current_basic_smelter_be | `public void setBurnStateForValidation(int burnTime, int currentBurnTime, int cookTime, int targetSmeltTime) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 121 | current_basic_smelter_be | `public void setMachineStateForValidation(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 139 | current_basic_smelter_be | `public void setStoredAspectsForValidation(AspectList newAspects) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 163 | current_basic_smelter_be | `public static float efficiencyForType(SmelterType type) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 165 | current_basic_smelter_be | `case BASIC -> 0.8F;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 166 | current_basic_smelter_be | `case THAUMIUM -> 0.9F;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 167 | current_basic_smelter_be | `case VOID -> 0.95F;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 171 | current_basic_smelter_be | `public static int speedForType(SmelterType type) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 172 | current_basic_smelter_be | `return type == SmelterType.THAUMIUM ? 10 : 15;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 179 | current_basic_smelter_be | `public enum SmelterType {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 180 | current_basic_smelter_be | `BASIC,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 181 | current_basic_smelter_be | `THAUMIUM,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 182 | current_basic_smelter_be | `VOID` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 185 | current_basic_smelter_be | `public static void serverTick(Level level, BlockPos pos, BlockState state, TCSmelterBlockEntity smelter) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 192 | current_basic_smelter_be | `private void tickServer() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 220 | current_basic_smelter_be | `dirty \|= pollutePendingFlux();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 270 | current_basic_smelter_be | `float efficiency = efficiencyForType(SmelterType.BASIC);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 290 | current_basic_smelter_be | `pendingFlux += fluxLoss;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 324 | current_basic_smelter_be | `int speed = speedForType(SmelterType.BASIC);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 345 | current_basic_smelter_be | `private boolean pollutePendingFlux() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 346 | current_basic_smelter_be | `if (level == null \|\| level.isClientSide \|\| pendingFlux <= 0) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 349 | current_basic_smelter_be | `int mitigated = mitigatePendingFluxWithVents(pendingFlux);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 350 | current_basic_smelter_be | `int pollution = Math.max(0, pendingFlux - mitigated);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 354 | current_basic_smelter_be | `pendingFlux = 0;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 358 | current_basic_smelter_be | `private int mitigatePendingFluxWithVents(int fluxAmount) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 382 | current_basic_smelter_be | `private void syncEnabledBlockState() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 396 | current_basic_smelter_be | `private void markChangedAndSync() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 415 | current_basic_smelter_be | `protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 427 | current_basic_smelter_be | `tag.putInt("PendingFlux", pendingFlux);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 431 | current_basic_smelter_be | `protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 444 | current_basic_smelter_be | `pendingFlux = Math.max(0, tag.getInt("PendingFlux"));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpointBlock.java | 13 | current_endpoint_block | `public class TCLegacySmelterEndpointBlock extends TCSmelterBlock implements EntityBlock {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpointBlock.java | 21 | current_endpoint_block | `public TCLegacySmelterEndpoint endpoint() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpointBlock.java | 27 | current_endpoint_block | `public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpointBlock.java | 28 | current_endpoint_block | `return TCBlockEntities.createSmelterEndpointBlockEntity(endpoint, pos, state);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 8 | current_endpoint_be | `public class TCLegacySmelterEndpointBlockEntity extends TCAbstractEssentiaTransportBlockEntity {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 12 | current_endpoint_be | `super(type, pos, state, endpoint.mode(), endpoint.storageCapacity());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 16 | current_endpoint_be | `public TCLegacySmelterEndpoint endpoint() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 9 | current_endpoint_enum | `THAUMIUM("smelter_thaumium", TCEssentiaTubeMode.SMELTER_THAUMIUM, 64),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 10 | current_endpoint_enum | `VOID("smelter_void", TCEssentiaTubeMode.SMELTER_VOID, 64);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 12 | current_endpoint_enum | `private final String catalogId;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 14 | current_endpoint_enum | `private final int storageCapacity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 16 | current_endpoint_enum | `TCLegacySmelterEndpoint(String catalogId, TCEssentiaTubeMode mode, int storageCapacity) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 17 | current_endpoint_enum | `this.catalogId = catalogId;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 19 | current_endpoint_enum | `this.storageCapacity = storageCapacity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 22 | current_endpoint_enum | `public String catalogId() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 23 | current_endpoint_enum | `return catalogId;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 26 | current_endpoint_enum | `public TCEssentiaTubeMode mode() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 30 | current_endpoint_enum | `public int storageCapacity() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 31 | current_endpoint_enum | `return storageCapacity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 34 | current_endpoint_enum | `public static Optional<TCLegacySmelterEndpoint> fromCatalogId(String catalogId) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 35 | current_endpoint_enum | `if (catalogId == null \|\| catalogId.isBlank()) return Optional.empty();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 36 | current_endpoint_enum | `String normalized = catalogId.toLowerCase(Locale.ROOT).replace("thaumcraft:", "");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 38 | current_endpoint_enum | `if (endpoint.catalogId.equals(normalized)) {` |
| 02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java | 98 | legacy_variant_semantics | `int speed = getSpeed();` |
| 02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java | 209 | legacy_variant_semantics | `private int getType() {` |
| 02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java | 210 | legacy_variant_semantics | `return (getBlockType() == BlocksTC.smelterBasic) ? 0 : ((getBlockType() == BlocksTC.smelterThaumium) ? 1 : 2);` |
| 02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java | 213 | legacy_variant_semantics | `private float getEfficiency() {` |
| 02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java | 215 | legacy_variant_semantics | `if (getType() == 1) {` |
| 02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java | 218 | legacy_variant_semantics | `if (getType() == 2) {` |
| 02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java | 224 | legacy_variant_semantics | `private int getSpeed() {` |
| 02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java | 225 | legacy_variant_semantics | `int speed = 20 - ((getType() == 1) ? 10 : 5);` |
| 02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java | 234 | legacy_variant_semantics | `if (getEfficiency() < 1.0f) {` |
| 02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java | 236 | legacy_variant_semantics | `if (world.rand.nextFloat() > ((a == Aspect.FLUX) ? (getEfficiency() * 0.66f) : getEfficiency())) {` |

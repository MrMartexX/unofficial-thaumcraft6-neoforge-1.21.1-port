# Smelter Runtime Boundary Audit

Generated: 2026-06-22 03:43:10 +03:00

## Pass criteria

- Exactly one registry entry exists for basic/thaumium/void smelters, vent, Bellows and auxiliary smelter.
- Exactly one `outputBufferedEssentia()` and one `tryOutputToAlembicAt(...)` method exist.
- Smelter runtime includes Bellows refresh, facing-aware vent mitigation, aux Alembic routing and type-aware speed/efficiency.
- Upgraded smelter endpoint block entity bridges both machine state and transport interface.

## Summary

| Category | Rows |
|---|---:|
| bellows_block | 12 |
| registry_block_entities | 14 |
| registry_blocks | 8 |
| registry_items | 8 |
| smelter_block_ticker | 6 |
| smelter_machine_runtime | 23 |
| upgraded_endpoint_machine_bridge | 11 |
| vent_block | 12 |
| total | 94 |

## Evidence

| File | Line | Category | Evidence |
|---|---:|---|---|
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 32 | registry_blocks | `import thaumcraft.common.blocks.essentia.TCBellowsBlock;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 127 | registry_blocks | `public static final Supplier<Block> SMELTER_BASIC = BLOCKS.register("smelter_basic", () -> smelterBlock());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 128 | registry_blocks | `public static final Supplier<Block> SMELTER_VENT = BLOCKS.register("smelter_vent",` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 132 | registry_blocks | `public static final Supplier<Block> SMELTER_AUX = BLOCKS.register("smelter_aux",` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 136 | registry_blocks | `public static final Supplier<Block> BELLOWS = BLOCKS.register("bellows",` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 137 | registry_blocks | `() -> new TCBellowsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 148 | registry_blocks | `public static final Supplier<Block> SMELTER_THAUMIUM = BLOCKS.register("smelter_thaumium", () -> smelterEndpointBlock(TCLegacySmelterEndpoint.THAUMIUM));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 149 | registry_blocks | `public static final Supplier<Block> SMELTER_VOID = BLOCKS.register("smelter_void", () -> smelterEndpointBlock(TCLegacySmelterEndpoint.VOID));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 105 | registry_items | `public static final Supplier<BlockItem> SMELTER_BASIC = blockItem("smelter_basic", TCBlocks.SMELTER_BASIC);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 106 | registry_items | `public static final Supplier<BlockItem> BELLOWS = blockItem("bellows", TCBlocks.BELLOWS);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 107 | registry_items | `public static final Supplier<BlockItem> SMELTER_VENT = blockItem("smelter_vent", TCBlocks.SMELTER_VENT);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 108 | registry_items | `public static final Supplier<BlockItem> SMELTER_AUX = blockItem("smelter_aux", TCBlocks.SMELTER_AUX);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 114 | registry_items | `public static final Supplier<BlockItem> SMELTER_THAUMIUM = blockItem("smelter_thaumium", TCBlocks.SMELTER_THAUMIUM);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 115 | registry_items | `public static final Supplier<BlockItem> SMELTER_VOID = blockItem("smelter_void", TCBlocks.SMELTER_VOID);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 532 | registry_items | `public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_ESSENTIASMELTERTHAUMIUM = SMELTER_THAUMIUM;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 533 | registry_items | `public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_ESSENTIASMELTERVOID = SMELTER_VOID;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 44 | registry_block_entities | `public static final Supplier<BlockEntityType<TCSmelterBlockEntity>> SMELTER_BASIC =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 45 | registry_block_entities | `BLOCK_ENTITY_TYPES.register("smelter_basic", () ->` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 46 | registry_block_entities | `BlockEntityType.Builder.of(TCSmelterBlockEntity::new, TCBlocks.SMELTER_BASIC.get()).build(null));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 118 | registry_block_entities | `public static final Supplier<BlockEntityType<TCLegacySmelterEndpointBlockEntity>> SMELTER_THAUMIUM =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 119 | registry_block_entities | `BLOCK_ENTITY_TYPES.register("smelter_thaumium", () ->` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 121 | registry_block_entities | `(pos, state) -> new TCLegacySmelterEndpointBlockEntity(TCBlockEntities.SMELTER_THAUMIUM.get(), pos, state, TCLegacySmelterEndpoint.THAUMIUM),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 122 | registry_block_entities | `TCBlocks.SMELTER_THAUMIUM.get()).build(null));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 123 | registry_block_entities | `public static final Supplier<BlockEntityType<TCLegacySmelterEndpointBlockEntity>> SMELTER_VOID =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 124 | registry_block_entities | `BLOCK_ENTITY_TYPES.register("smelter_void", () ->` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 126 | registry_block_entities | `(pos, state) -> new TCLegacySmelterEndpointBlockEntity(TCBlockEntities.SMELTER_VOID.get(), pos, state, TCLegacySmelterEndpoint.VOID),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 127 | registry_block_entities | `TCBlocks.SMELTER_VOID.get()).build(null));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 151 | registry_block_entities | `public static TCLegacySmelterEndpointBlockEntity createSmelterEndpointBlockEntity(TCLegacySmelterEndpoint endpoint, BlockPos pos, BlockState state) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 153 | registry_block_entities | `case THAUMIUM -> new TCLegacySmelterEndpointBlockEntity(SMELTER_THAUMIUM.get(), pos, state, endpoint);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 154 | registry_block_entities | `case VOID -> new TCLegacySmelterEndpointBlockEntity(SMELTER_VOID.get(), pos, state, endpoint);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 47 | smelter_block_ticker | `public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 48 | smelter_block_ticker | `if (level.isClientSide \|\| !isSmelterMachineType(type)) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 60 | smelter_block_ticker | `private static boolean isSmelterMachineType(BlockEntityType<?> type) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 61 | smelter_block_ticker | `return type == TCBlockEntities.SMELTER_BASIC.get()` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 62 | smelter_block_ticker | `\|\| type == TCBlockEntities.SMELTER_THAUMIUM.get()` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 63 | smelter_block_ticker | `\|\| type == TCBlockEntities.SMELTER_VOID.get();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 44 | smelter_machine_runtime | `private final SmelterType smelterType;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 57 | smelter_machine_runtime | `this(TCBlockEntities.SMELTER_BASIC.get(), pos, state, SmelterType.BASIC);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 60 | smelter_machine_runtime | `protected TCSmelterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, SmelterType smelterType) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 62 | smelter_machine_runtime | `this.smelterType = smelterType == null ? SmelterType.BASIC : smelterType;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 108 | smelter_machine_runtime | `public SmelterType smelterType() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 109 | smelter_machine_runtime | `return smelterType;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 173 | smelter_machine_runtime | `public static float efficiencyForType(SmelterType type) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 181 | smelter_machine_runtime | `public static int speedForType(SmelterType type) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 182 | smelter_machine_runtime | `return type == SmelterType.THAUMIUM ? 10 : 15;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 189 | smelter_machine_runtime | `public enum SmelterType {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 205 | smelter_machine_runtime | `dirty \|= refreshBellows();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 230 | smelter_machine_runtime | `dirty \|= outputBufferedEssentia();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 231 | smelter_machine_runtime | `dirty \|= pollutePendingFlux();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 241 | smelter_machine_runtime | `private boolean refreshBellows() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 305 | smelter_machine_runtime | `float efficiency = efficiencyForType(smelterType());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 354 | smelter_machine_runtime | `private boolean outputBufferedEssentia() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 359 | smelter_machine_runtime | `int speed = speedForType(smelterType());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 363 | smelter_machine_runtime | `if (tryOutputToAlembicAt(worldPosition)) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 368 | smelter_machine_runtime | `if (level.getBlockState(auxPos).is(TCBlocks.SMELTER_AUX.get()) && tryOutputToAlembicAt(auxPos)) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 375 | smelter_machine_runtime | `private boolean tryOutputToAlembicAt(BlockPos outputPos) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 390 | smelter_machine_runtime | `private boolean pollutePendingFlux() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 422 | smelter_machine_runtime | `if (!neighbour.is(TCBlocks.SMELTER_VENT.get()) \|\| !neighbour.hasProperty(TCSmelterVentBlock.FACING)) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 425 | smelter_machine_runtime | `if (neighbour.getValue(TCSmelterVentBlock.FACING) == direction.getOpposite()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBellowsBlock.java | 6 | bellows_block | `import net.minecraft.world.level.block.Mirror;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBellowsBlock.java | 14 | bellows_block | `/** Minimal Bellows placeholder with facing state for smelter speed parity slices. */` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBellowsBlock.java | 15 | bellows_block | `public class TCBellowsBlock extends Block {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBellowsBlock.java | 16 | bellows_block | `public static final DirectionProperty FACING = BlockStateProperties.FACING;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBellowsBlock.java | 20 | bellows_block | `registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBellowsBlock.java | 24 | bellows_block | `public BlockState getStateForPlacement(BlockPlaceContext context) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBellowsBlock.java | 25 | bellows_block | `return defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBellowsBlock.java | 30 | bellows_block | `builder.add(FACING);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBellowsBlock.java | 34 | bellows_block | `protected BlockState rotate(BlockState state, Rotation rotation) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBellowsBlock.java | 35 | bellows_block | `return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBellowsBlock.java | 39 | bellows_block | `protected BlockState mirror(BlockState state, Mirror mirror) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBellowsBlock.java | 40 | bellows_block | `return state.rotate(mirror.getRotation(state.getValue(FACING)));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterVentBlock.java | 6 | vent_block | `import net.minecraft.world.level.block.Mirror;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterVentBlock.java | 14 | vent_block | `/** Minimal smelter vent placeholder with facing state for flux-mitigation parity slices. */` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterVentBlock.java | 15 | vent_block | `public class TCSmelterVentBlock extends Block {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterVentBlock.java | 16 | vent_block | `public static final DirectionProperty FACING = BlockStateProperties.FACING;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterVentBlock.java | 20 | vent_block | `registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterVentBlock.java | 24 | vent_block | `public BlockState getStateForPlacement(BlockPlaceContext context) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterVentBlock.java | 25 | vent_block | `return defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterVentBlock.java | 30 | vent_block | `builder.add(FACING);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterVentBlock.java | 34 | vent_block | `protected BlockState rotate(BlockState state, Rotation rotation) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterVentBlock.java | 35 | vent_block | `return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterVentBlock.java | 39 | vent_block | `protected BlockState mirror(BlockState state, Mirror mirror) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterVentBlock.java | 40 | vent_block | `return state.rotate(mirror.getRotation(state.getValue(FACING)));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 12 | upgraded_endpoint_machine_bridge | `import thaumcraft.common.essentia.transport.TCLegacyEssentiaTransportNode;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 25 | upgraded_endpoint_machine_bridge | `public class TCLegacySmelterEndpointBlockEntity extends TCSmelterBlockEntity implements TCEssentiaTransport {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 27 | upgraded_endpoint_machine_bridge | `private final TCLegacyEssentiaTransportNode transportNode;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 32 | upgraded_endpoint_machine_bridge | `this.transportNode = new TCLegacyEssentiaTransportNode(endpoint.mode(), endpoint.storageCapacity());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 37 | upgraded_endpoint_machine_bridge | `case THAUMIUM -> SmelterType.THAUMIUM;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 38 | upgraded_endpoint_machine_bridge | `case VOID -> SmelterType.VOID;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 46 | upgraded_endpoint_machine_bridge | `public TCLegacyEssentiaTransportNode transportNode() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 124 | upgraded_endpoint_machine_bridge | `public int addEssentia(String aspect, int amount, Direction face, boolean simulate) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 125 | upgraded_endpoint_machine_bridge | `int accepted = transportNode.addEssentia(aspect, amount, face, simulate);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 133 | upgraded_endpoint_machine_bridge | `public int takeEssentia(String aspect, int amount, Direction face, boolean simulate) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 134 | upgraded_endpoint_machine_bridge | `int taken = transportNode.takeEssentia(aspect, amount, face, simulate);` |

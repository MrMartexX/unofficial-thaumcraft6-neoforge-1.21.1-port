# Infusion Real Source Candidate Audit

Generated: 2026-06-20 15:31:42 +03:00

## Summary

| Metric | Count |
|---|---:|
| Candidate-term hits | 3449 |
| Files with candidate hits | 375 |
| Registry-like candidate files | 17 |
| BlockEntity-like candidate files | 13 |
| Aspect/storage-like candidate files | 120 |
| Placeholder/bridge-like candidate files | 10 |

## Interpretation

- This audit began as the discovery pass for the first real infusion aspect/essentia source policy.
- The selected first source is now the storage-bearing `thaumcraft:jar_normal` BlockEntity through `TCAspectSourceContainer`.
- A source should be selected only if there is a stable block or block entity with explicit storage semantics in the current port.
- Placeholder or bridge-only identities must not be used as real source implementations.
- Transient tube transport buffers are not legacy infusion sources; unsupported source types remain fail-closed and player-facing completion stays disabled.

## Registry-like source candidates

| File | Line | Evidence |
|---|---:|---|
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 114 | `event.register((stack, tintIndex) -> FLUX, TCBlocks.CRYSTAL_VITIUM.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectReloadValidator.java | 515 | `TCAspectStackKey.from(new ItemStack(TCItems.ORE_AMBER.get())), new AspectList().add(Aspect.AIR, 99),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCWardedJarBlock.java | 15 | `import thaumcraft.common.tiles.essentia.TCWardedJarBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCWardedJarBlock.java | 27 | `return new TCWardedJarBlockEntity(pos, state);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCWardedJarBlock.java | 47 | `return level.getBlockEntity(pos) instanceof TCWardedJarBlockEntity jar ? jar.comparatorSignal() : 0;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 28 | `public static Optional<TCInfusionAspectSource> findSource(TCInfusionMatrixBlockEntity matrix, TCInfusionCraftingPlan plan) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 42 | `BlockEntity blockEntity = level.getBlockEntity(sourcePos);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 43 | `if (blockEntity instanceof TCAspectSourceContainer container) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 12 | `import thaumcraft.common.essentia.transport.blockentity.TCLegacyTubeBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 31 | `import thaumcraft.common.tiles.essentia.TCWardedJarBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 264 | `TCInfusionMatrixBlockEntity.Snapshot snapshot = matrix.createSnapshot(new AspectList().add(Aspect.AIR, 50));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 487 | `level.setBlock(tubePos, TCBlocks.TUBE.get().defaultBlockState(), 3);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 490 | `TCLegacyTubeBlockEntity tube = blockEntity(level, tubePos, TCLegacyTubeBlockEntity.class);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 517 | `TCWardedJarBlockEntity nearJar = blockEntity(level, nearJarPos, TCWardedJarBlockEntity.class);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 518 | `TCWardedJarBlockEntity farJar = blockEntity(level, farJarPos, TCWardedJarBlockEntity.class);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 519 | `TCWardedJarBlockEntity outOfRangeJar = blockEntity(level, outOfRangeJarPos, TCWardedJarBlockEntity.class);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 531 | `outOfRangeJar.setStoredForValidation(Aspect.AIR, TCWardedJarBlockEntity.CAPACITY);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 542 | `&& outOfRangeJar.storedAmount() == TCWardedJarBlockEntity.CAPACITY,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacyTubeBlock.java | 31 | `return TCBlockEntities.createTubeBlockEntity(variant, pos, state);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 1 | `package thaumcraft.common.essentia.transport.blockentity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 29 | `public abstract class TCAbstractEssentiaTransportBlockEntity extends BlockEntity implements TCEssentiaTransport {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 32 | `protected TCAbstractEssentiaTransportBlockEntity(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 47 | `public static void serverTick(Level level, BlockPos pos, BlockState state, TCAbstractEssentiaTransportBlockEntity blockEntity) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 1 | `package thaumcraft.common.essentia.transport.blockentity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 8 | `public class TCLegacySmelterEndpointBlockEntity extends TCAbstractEssentiaTransportBlockEntity {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacyTubeBlockEntity.java | 1 | `package thaumcraft.common.essentia.transport.blockentity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacyTubeBlockEntity.java | 8 | `public class TCLegacyTubeBlockEntity extends TCAbstractEssentiaTransportBlockEntity {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacyTubeBlockEntity.java | 11 | `public TCLegacyTubeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, TCLegacyTubeVariant variant) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/menu/TCArcaneWorkbenchMenu.java | 252 | `syncedAvailableVis = blockEntity.availableVis();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 3 | `import thaumcraft.common.essentia.transport.blockentity.TCLegacyTubeBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 5 | `import thaumcraft.common.essentia.transport.blockentity.TCLegacySmelterEndpointBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 7 | `import thaumcraft.common.essentia.transport.block.TCLegacyTubeVariant;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 9 | `import thaumcraft.common.essentia.transport.block.TCLegacySmelterEndpoint;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 24 | `import thaumcraft.common.tiles.essentia.TCWardedJarBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 41 | `public static final Supplier<BlockEntityType<TCWardedJarBlockEntity>> WARDED_JAR =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 43 | `BlockEntityType.Builder.of(TCWardedJarBlockEntity::new, TCBlocks.JAR_NORMAL.get()).build(null));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 75 | `// Legacy-aligned transport/essentia block entities.` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 76 | `public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 77 | `BLOCK_ENTITY_TYPES.register("tube", () ->` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 79 | `(pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE.get(), pos, state, TCLegacyTubeVariant.TUBE),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 80 | `TCBlocks.TUBE.get()).build(null));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 81 | `public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_BUFFER =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 82 | `BLOCK_ENTITY_TYPES.register("tube_buffer", () ->` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 84 | `(pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_BUFFER.get(), pos, state, TCLegacyTubeVariant.BUFFER),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 85 | `TCBlocks.TUBE_BUFFER.get()).build(null));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 86 | `public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_FILTER =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 87 | `BLOCK_ENTITY_TYPES.register("tube_filter", () ->` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 89 | `(pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_FILTER.get(), pos, state, TCLegacyTubeVariant.FILTER),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 90 | `TCBlocks.TUBE_FILTER.get()).build(null));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 91 | `public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_ONEWAY =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 92 | `BLOCK_ENTITY_TYPES.register("tube_oneway", () ->` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 94 | `(pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_ONEWAY.get(), pos, state, TCLegacyTubeVariant.ONEWAY),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 95 | `TCBlocks.TUBE_ONEWAY.get()).build(null));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 96 | `public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_RESTRICT =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 97 | `BLOCK_ENTITY_TYPES.register("tube_restrict", () ->` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 99 | `(pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_RESTRICT.get(), pos, state, TCLegacyTubeVariant.RESTRICT),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 100 | `TCBlocks.TUBE_RESTRICT.get()).build(null));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 101 | `public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_VALVE =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 102 | `BLOCK_ENTITY_TYPES.register("tube_valve", () ->` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 104 | `(pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_VALVE.get(), pos, state, TCLegacyTubeVariant.VALVE),` |

## BlockEntity-like source candidates

| File | Line | Evidence |
|---|---:|---|
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCWardedJarBlock.java | 15 | `import thaumcraft.common.tiles.essentia.TCWardedJarBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCWardedJarBlock.java | 27 | `return new TCWardedJarBlockEntity(pos, state);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCWardedJarBlock.java | 47 | `return level.getBlockEntity(pos) instanceof TCWardedJarBlockEntity jar ? jar.comparatorSignal() : 0;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 28 | `public static Optional<TCInfusionAspectSource> findSource(TCInfusionMatrixBlockEntity matrix, TCInfusionCraftingPlan plan) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 42 | `BlockEntity blockEntity = level.getBlockEntity(sourcePos);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 43 | `if (blockEntity instanceof TCAspectSourceContainer container) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 12 | `import thaumcraft.common.essentia.transport.blockentity.TCLegacyTubeBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 31 | `import thaumcraft.common.tiles.essentia.TCWardedJarBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 264 | `TCInfusionMatrixBlockEntity.Snapshot snapshot = matrix.createSnapshot(new AspectList().add(Aspect.AIR, 50));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 490 | `TCLegacyTubeBlockEntity tube = blockEntity(level, tubePos, TCLegacyTubeBlockEntity.class);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 517 | `TCWardedJarBlockEntity nearJar = blockEntity(level, nearJarPos, TCWardedJarBlockEntity.class);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 518 | `TCWardedJarBlockEntity farJar = blockEntity(level, farJarPos, TCWardedJarBlockEntity.class);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 519 | `TCWardedJarBlockEntity outOfRangeJar = blockEntity(level, outOfRangeJarPos, TCWardedJarBlockEntity.class);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 531 | `outOfRangeJar.setStoredForValidation(Aspect.AIR, TCWardedJarBlockEntity.CAPACITY);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 542 | `&& outOfRangeJar.storedAmount() == TCWardedJarBlockEntity.CAPACITY,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacyTubeBlock.java | 31 | `return TCBlockEntities.createTubeBlockEntity(variant, pos, state);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 1 | `package thaumcraft.common.essentia.transport.blockentity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 14 | `import thaumcraft.common.essentia.transport.TCEssentiaStack;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 15 | `import thaumcraft.common.essentia.transport.TCEssentiaSuction;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 16 | `import thaumcraft.common.essentia.transport.TCEssentiaTransport;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 17 | `import thaumcraft.common.essentia.transport.TCEssentiaTubeMode;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 18 | `import thaumcraft.common.essentia.transport.TCLegacyEssentiaTransportNode;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 26 | `* Legacy concepts are delegated to TCLegacyEssentiaTransportNode, keeping the block entity focused` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 29 | `public abstract class TCAbstractEssentiaTransportBlockEntity extends BlockEntity implements TCEssentiaTransport {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 30 | `protected final TCLegacyEssentiaTransportNode transportNode;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 32 | `protected TCAbstractEssentiaTransportBlockEntity(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 36 | `TCEssentiaTubeMode mode,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 40 | `this.transportNode = new TCLegacyEssentiaTransportNode(mode, capacity);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 43 | `public TCLegacyEssentiaTransportNode transportNode() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 47 | `public static void serverTick(Level level, BlockPos pos, BlockState state, TCAbstractEssentiaTransportBlockEntity blockEntity) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 70 | `CompoundTag essentiaTag = new CompoundTag();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 72 | `essentiaTag.putInt(entry.getKey(), entry.getValue());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 74 | `tag.put("Essentia", essentiaTag);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 82 | `transportNode.setMode(TCEssentiaTubeMode.valueOf(tag.getString("Mode")));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 91 | `if (tag.contains("Essentia")) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 92 | `CompoundTag essentiaTag = tag.getCompound("Essentia");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 93 | `for (String key : essentiaTag.getAllKeys()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 94 | `transportNode.mutableStorage().set(key, essentiaTag.getInt(key));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 128 | `public TCEssentiaSuction getSuction(Direction face) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 138 | `public TCEssentiaStack getEssentia(Direction face) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 139 | `return transportNode.getEssentia(face);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 143 | `public int addEssentia(String aspect, int amount, Direction face, boolean simulate) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 144 | `int accepted = transportNode.addEssentia(aspect, amount, face, simulate);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 150 | `public int takeEssentia(String aspect, int amount, Direction face, boolean simulate) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | 151 | `int taken = transportNode.takeEssentia(aspect, amount, face, simulate);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 1 | `package thaumcraft.common.essentia.transport.blockentity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 6 | `import thaumcraft.common.essentia.transport.block.TCLegacySmelterEndpoint;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 8 | `public class TCLegacySmelterEndpointBlockEntity extends TCAbstractEssentiaTransportBlockEntity {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacyTubeBlockEntity.java | 1 | `package thaumcraft.common.essentia.transport.blockentity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacyTubeBlockEntity.java | 6 | `import thaumcraft.common.essentia.transport.block.TCLegacyTubeVariant;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacyTubeBlockEntity.java | 8 | `public class TCLegacyTubeBlockEntity extends TCAbstractEssentiaTransportBlockEntity {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacyTubeBlockEntity.java | 9 | `private final TCLegacyTubeVariant variant;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacyTubeBlockEntity.java | 11 | `public TCLegacyTubeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, TCLegacyTubeVariant variant) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacyTubeBlockEntity.java | 16 | `public TCLegacyTubeVariant variant() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/menu/TCArcaneWorkbenchMenu.java | 252 | `syncedAvailableVis = blockEntity.availableVis();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 3 | `import thaumcraft.common.essentia.transport.blockentity.TCLegacyTubeBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 5 | `import thaumcraft.common.essentia.transport.blockentity.TCLegacySmelterEndpointBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 24 | `import thaumcraft.common.tiles.essentia.TCWardedJarBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 41 | `public static final Supplier<BlockEntityType<TCWardedJarBlockEntity>> WARDED_JAR =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 43 | `BlockEntityType.Builder.of(TCWardedJarBlockEntity::new, TCBlocks.JAR_NORMAL.get()).build(null));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 76 | `public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 79 | `(pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE.get(), pos, state, TCLegacyTubeVariant.TUBE),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 81 | `public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_BUFFER =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 84 | `(pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_BUFFER.get(), pos, state, TCLegacyTubeVariant.BUFFER),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 86 | `public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_FILTER =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 89 | `(pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_FILTER.get(), pos, state, TCLegacyTubeVariant.FILTER),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 91 | `public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_ONEWAY =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 94 | `(pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_ONEWAY.get(), pos, state, TCLegacyTubeVariant.ONEWAY),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 96 | `public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_RESTRICT =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 99 | `(pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_RESTRICT.get(), pos, state, TCLegacyTubeVariant.RESTRICT),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 101 | `public static final Supplier<BlockEntityType<TCLegacyTubeBlockEntity>> TUBE_VALVE =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 104 | `(pos, state) -> new TCLegacyTubeBlockEntity(TCBlockEntities.TUBE_VALVE.get(), pos, state, TCLegacyTubeVariant.VALVE),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 117 | `public static TCLegacyTubeBlockEntity createTubeBlockEntity(TCLegacyTubeVariant variant, BlockPos pos, BlockState state) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 119 | `case TUBE -> new TCLegacyTubeBlockEntity(TUBE.get(), pos, state, variant);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 120 | `case BUFFER -> new TCLegacyTubeBlockEntity(TUBE_BUFFER.get(), pos, state, variant);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 121 | `case FILTER -> new TCLegacyTubeBlockEntity(TUBE_FILTER.get(), pos, state, variant);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 122 | `case ONEWAY -> new TCLegacyTubeBlockEntity(TUBE_ONEWAY.get(), pos, state, variant);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 123 | `case RESTRICT -> new TCLegacyTubeBlockEntity(TUBE_RESTRICT.get(), pos, state, variant);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | 124 | `case VALVE -> new TCLegacyTubeBlockEntity(TUBE_VALVE.get(), pos, state, variant);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCArcaneWorkbenchBlockEntity.java | 29 | `import thaumcraft.common.world.aura.AuraChunk;` |

## Aspect/storage-like candidates

| File | Line | Evidence |
|---|---:|---|
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/Aspect.java | 27 | `public Aspect(String tag, int color, Aspect[] components, ResourceLocation image, int blend) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/Aspect.java | 146 | `private static ResourceLocation aspectImage(String tag) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/Aspect.java | 147 | `return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/aspects/" + tag.toLowerCase() + ".png");` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/Aspect.java | 169 | `public static Aspect AURA = new Aspect("auram", 0xffc0ff, new Aspect[] { MAGIC, AIR });` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/Aspect.java | 171 | `public static Aspect FLUX = new Aspect("vitium", 0x800080, new Aspect[] { ENTROPY, MAGIC });` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 13 | `public static AspectList cullTags(AspectList temp) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 17 | `public static AspectList cullTags(AspectList temp, int cap) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 18 | `AspectList temp2 = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 66 | `public static AspectList getObjectAspects(ItemStack stack) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 70 | `public static AspectList getScanAspects(ItemStack stack) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 74 | `public static AspectList generateTags(ItemStack stack) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 78 | `public static AspectList getEntityAspects(Entity entity) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 80 | `AspectList tags = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 107 | `AspectList temp = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 109 | `AspectList temp2 = AspectHelper.reduceToPrimals(temp);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 120 | `public static AspectList reduceToPrimals(AspectList in) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 121 | `AspectList out = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 127 | `AspectList temp = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 130 | `AspectList temp2 = reduceToPrimals(temp);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 141 | `public static AspectList getPrimalAspects(AspectList in) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 142 | `AspectList t = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 152 | `public static AspectList getAuraAspects(AspectList in) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 153 | `AspectList t = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 160 | `t.add(Aspect.FLUX, in.getAmount(Aspect.FLUX));` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 10 | `public class AspectList implements Serializable {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 15 | `public AspectList(ItemStack stack) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 17 | `AspectList temp = AspectHelper.getObjectAspects(stack);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 27 | `public AspectList() {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 30 | `public AspectList copy() {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 31 | `AspectList out = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 115 | `public AspectList remove(Aspect key, int amount) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 125 | `public AspectList remove(Aspect key) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 130 | `public AspectList add(Aspect aspect, int amount) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 139 | `public AspectList merge(Aspect aspect, int amount) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 150 | `public AspectList add(AspectList in) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 157 | `public AspectList remove(AspectList in) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 164 | `public AspectList merge(AspectList in) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/crafting/IArcaneRecipe.java | 5 | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/api/crafting/IArcaneRecipe.java | 19 | `AspectList getCrystals();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/research/ScanAspect.java | 10 | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/api/research/ScanAspect.java | 25 | `AspectList aspects = getAspects(player, object);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/research/ScanAspect.java | 47 | `private static AspectList getAspects(ServerPlayer player, Object object) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/AspectTooltipComponent.java | 7 | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/AspectTooltipComponent.java | 12 | `public AspectTooltipComponent(AspectList aspects) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCAspectTooltipEvents.java | 13 | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCAspectTooltipEvents.java | 30 | `AspectList aspects = AspectHelper.getObjectAspects(event.getItemStack());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java | 62 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/aspects/_unknown.png");` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCThaumonomiconBrowserScreen.java | 32 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/aspects/_unknown.png");` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 162 | `if (item.kind() == ItemAspectVariant.Kind.PHIAL && tintIndex != 1) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCThaumometerClientEffects.java | 33 | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCThaumometerClientEffects.java | 160 | `AspectList aspects = AspectHelper.getEntityAspects(target);` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCThaumometerClientEffects.java | 203 | `private static boolean hasAspects(AspectList aspects) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCThaumometerClientEffects.java | 227 | `AspectList tags` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 9 | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 12 | `private final Map<ResourceLocation, AspectList> directObjectTags;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 13 | `private final Map<TagKey<Item>, AspectList> tagObjectTags;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 14 | `private final Map<ResourceLocation, AspectList> complexDirectObjectTags;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 15 | `private final Map<TagKey<Item>, AspectList> complexTagObjectTags;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 18 | `Map<ResourceLocation, AspectList> directObjectTags,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 19 | `Map<TagKey<Item>, AspectList> tagObjectTags,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 20 | `Map<ResourceLocation, AspectList> complexDirectObjectTags,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 21 | `Map<TagKey<Item>, AspectList> complexTagObjectTags) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 28 | `Map<ResourceLocation, AspectList> directObjectTags() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 32 | `Map<TagKey<Item>, AspectList> tagObjectTags() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 36 | `Map<ResourceLocation, AspectList> complexDirectObjectTags() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 40 | `Map<TagKey<Item>, AspectList> complexTagObjectTags() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 44 | `private static Map<ResourceLocation, AspectList> copyDirectMap(Map<ResourceLocation, AspectList> source) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 45 | `LinkedHashMap<ResourceLocation, AspectList> copy = new LinkedHashMap<>();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 46 | `for (Map.Entry<ResourceLocation, AspectList> entry : source.entrySet()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 52 | `private static Map<TagKey<Item>, AspectList> copyTagMap(Map<TagKey<Item>, AspectList> source) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 53 | `LinkedHashMap<TagKey<Item>, AspectList> copy = new LinkedHashMap<>();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentData.java | 54 | `for (Map.Entry<TagKey<Item>, AspectList> entry : source.entrySet()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentParser.java | 22 | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentParser.java | 59 | `static TCAspectAssignmentData parse(Map<ResourceLocation, JsonElement> files) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentParser.java | 91 | `AspectList aspects = parseAspects(fileId, index, GsonHelper.getAsJsonArray(assignment, "aspects"));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentParser.java | 110 | `private static AspectList parseAspects(ResourceLocation fileId, int assignmentIndex, JsonArray elements) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentParser.java | 111 | `AspectList aspects = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentParser.java | 114 | `ResourceLocation aspectId = ResourceLocation.parse(GsonHelper.getAsString(entry, "aspect"));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentParser.java | 135 | `private final LinkedHashMap<ResourceLocation, AspectList> direct = new LinkedHashMap<>();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectAssignmentParser.java | 136 | `private final LinkedHashMap<TagKey<Item>, AspectList> tags = new LinkedHashMap<>();` |

## Placeholder or bridge warning candidates

| File | Line | Evidence |
|---|---:|---|
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/TCEssentiaTubeMode.java | 7 | `* restriction tube, input/output transport bridge, and smelter endpoints.` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 484 | `public static final Supplier<Item> CATALOG_PLACEHOLDER_ALEMBIC = ITEMS.register("alembic", () -> new Item(new Item.Properties()));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 504 | `public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_WARDEDJAR = JAR_NORMAL;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 522 | `public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_ESSENTIASMELTERTHAUMIUM = SMELTER_THAUMIUM;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 523 | `public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_ESSENTIASMELTERVOID = SMELTER_VOID;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 524 | `public static final Supplier<Item> CATALOG_PLACEHOLDER_ESSENTIATRANSPORTIN = ITEMS.register("essentiatransportin", () -> new Item(new Item.Properties()));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 525 | `public static final Supplier<Item> CATALOG_PLACEHOLDER_ESSENTIATRANSPORTOUT = ITEMS.register("essentiatransportout", () -> new Item(new Item.Properties()));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 526 | `public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_TUBE = blockItem("tube", TCBlocks.TUBE);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 527 | `public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_TUBEBUFFER = blockItem("tube_buffer", TCBlocks.TUBE_BUFFER);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 528 | `public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_TUBEFILTER = blockItem("tube_filter", TCBlocks.TUBE_FILTER);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 529 | `public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_TUBEONEWAY = blockItem("tube_oneway", TCBlocks.TUBE_ONEWAY);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 530 | `public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_TUBERESTRICT = blockItem("tube_restrict", TCBlocks.TUBE_RESTRICT);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | 531 | `public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_TUBEVALVE = blockItem("tube_valve", TCBlocks.TUBE_VALVE);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCArcaneRecipeAudit.java | 64 | `private static final ResourceLocation THAUMOMETER_BRIDGE =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCArcaneRecipeAudit.java | 65 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "research_bridge/thaumometer");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCArcaneRecipeAudit.java | 66 | `private static final ResourceLocation VIS_RESONATOR_BRIDGE =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCArcaneRecipeAudit.java | 67 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "research_bridge/vis_resonator");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCArcaneRecipeAudit.java | 68 | `private static final ResourceLocation WAND_WORKBENCH_BRIDGE =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCArcaneRecipeAudit.java | 69 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "research_bridge/wand_workbench");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCArcaneRecipeAudit.java | 70 | `private static final ResourceLocation CASTER_BASIC_BRIDGE =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCArcaneRecipeAudit.java | 71 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "research_bridge/caster_basic");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCArcaneRecipeAudit.java | 72 | `private static final ResourceLocation MIRRORED_GLASS_BRIDGE =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCArcaneRecipeAudit.java | 73 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "research_bridge/mirrored_glass");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCResearchRequirementAudit.java | 117 | `writer.write("Bridge warnings are not parser failures. They mark requirements whose registry identity is resolvable, but whose final gameplay source, item semantics, recipe flow, or legacy container/component behavior is still a migration boundary.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCResearchRequirementAudit.java | 254 | `return hasAspectStackRequirement(resolution) ? "" : "essentia phial bridge: flattened aspect variant before final DataComponent/container semantics";` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCResearchRequirementAudit.java | 298 | `"thaumcraft:vis_resonator" -> "auromancy placeholder before focus/caster/vis semantics";` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCResearchRequirementAuditCommands.java | 57 | `"Bridge warnings mean the registry identity is resolvable, but final gameplay source, item semantics or container/component behavior may still be blocked."` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCResearchRequirementAuditCommands.java | 80 | `context.getSource().sendSuccess(() -> Component.literal("Bridge/placeholder warning summary:"), false);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCThaumonomiconProtocolAudit.java | 128 | `writer.write("- Deferred arcane transport/essentia catalog entries: `" + report.deferredArcaneTransportCatalogEntries().size() + "`\n");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCThaumonomiconProtocolAudit.java | 135 | `writeDeferredList(writer, "Deferred arcane transport/essentia catalog ids", report.deferredArcaneTransportCatalogEntries());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/theorycraft/TCResearchTableDiagnostics.java | 104 | `"First common card bridge should add dependency-free, aspect-crystal/phial, vanilla XP/aid, table-inventory, vanilla-item Golemancy, Artifice item-option, Basic Auromancy, basic Infusion, Basic Golemancy and safe Eldritch option cards only.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/theorycraft/TCResearchTableDiagnostics.java | 105 | `report.check("card_analyze_deferred_by_legacy_bug", !new CardAnalyze().initialize(null, new TCResearchTableData()), "Legacy decompiled CardAnalyze initializes from a null category lookup; kept out of random draws until corrected from a stronger source.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/theorycraft/TCResearchTableDiagnostics.java | 199 | `"The safe Eldritch aid bridge should contain Glyphed Stone, End portal and Nether portal aids; Brain-in-a-Jar, Crimson portal and Basic Eldritch remain deferred.");` |
| 05_neoforge_port/src/main/resources/assets/thaumcraft/lang/en_us.json | 930 | `"tc.placeholder.caster_basic": "Temporary port placeholder: caster focus socket and vis-cost logic are not fully implemented yet.",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/blueprint_page_placeholders.json | 5 | `"legacy_source": "THAUMCRAFT_CATALOG",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/blueprint_page_placeholders.json | 22 | `"legacy_source": "THAUMCRAFT_CATALOG",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/blueprint_page_placeholders.json | 39 | `"legacy_source": "THAUMCRAFT_CATALOG",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/blueprint_page_placeholders.json | 56 | `"legacy_source": "THAUMCRAFT_CATALOG",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json | 1129 | `"legacy_source": "fake_catalog",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json | 1141 | `"legacy_source": "fake_catalog",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json | 1153 | `"legacy_source": "fake_catalog",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json | 1165 | `"legacy_source": "fake_catalog",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json | 1177 | `"legacy_source": "fake_catalog",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json | 1189 | `"legacy_source": "fake_catalog",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json | 1201 | `"legacy_source": "fake_catalog",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json | 1213 | `"legacy_source": "fake_catalog",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json | 2018 | `"legacy_source": "fake_catalog",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json | 2030 | `"legacy_source": "fake_catalog",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json | 2042 | `"legacy_source": "fake_catalog",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json | 2054 | `"legacy_source": "fake_catalog",` |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json | 2840 | `"legacy_source": "fake_catalog",` |

## All candidate hits, first 120

| File | Line | Evidence |
|---|---:|---|
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/Aspect.java | 8 | `import net.minecraft.resources.ResourceLocation;` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/Aspect.java | 18 | `ResourceLocation image;` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/Aspect.java | 27 | `public Aspect(String tag, int color, Aspect[] components, ResourceLocation image, int blend) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/Aspect.java | 90 | `public ResourceLocation getImage() {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/Aspect.java | 142 | `public ResourceLocation getId() {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/Aspect.java | 143 | `return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, tag);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/Aspect.java | 146 | `private static ResourceLocation aspectImage(String tag) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/Aspect.java | 147 | `return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/aspects/" + tag.toLowerCase() + ".png");` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/Aspect.java | 169 | `public static Aspect AURA = new Aspect("auram", 0xffc0ff, new Aspect[] { MAGIC, AIR });` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/Aspect.java | 171 | `public static Aspect FLUX = new Aspect("vitium", 0x800080, new Aspect[] { ENTROPY, MAGIC });` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 13 | `public static AspectList cullTags(AspectList temp) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 17 | `public static AspectList cullTags(AspectList temp, int cap) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 18 | `AspectList temp2 = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 66 | `public static AspectList getObjectAspects(ItemStack stack) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 70 | `public static AspectList getScanAspects(ItemStack stack) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 74 | `public static AspectList generateTags(ItemStack stack) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 78 | `public static AspectList getEntityAspects(Entity entity) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 80 | `AspectList tags = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 107 | `AspectList temp = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 109 | `AspectList temp2 = AspectHelper.reduceToPrimals(temp);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 120 | `public static AspectList reduceToPrimals(AspectList in) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 121 | `AspectList out = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 127 | `AspectList temp = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 130 | `AspectList temp2 = reduceToPrimals(temp);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 141 | `public static AspectList getPrimalAspects(AspectList in) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 142 | `AspectList t = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 152 | `public static AspectList getAuraAspects(AspectList in) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 153 | `AspectList t = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 160 | `t.add(Aspect.FLUX, in.getAmount(Aspect.FLUX));` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 10 | `public class AspectList implements Serializable {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 15 | `public AspectList(ItemStack stack) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 17 | `AspectList temp = AspectHelper.getObjectAspects(stack);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 27 | `public AspectList() {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 30 | `public AspectList copy() {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 31 | `AspectList out = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 115 | `public AspectList remove(Aspect key, int amount) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 125 | `public AspectList remove(Aspect key) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 130 | `public AspectList add(Aspect aspect, int amount) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 139 | `public AspectList merge(Aspect aspect, int amount) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 150 | `public AspectList add(AspectList in) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 157 | `public AspectList remove(AspectList in) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectList.java | 164 | `public AspectList merge(AspectList in) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 1 | `package thaumcraft.api.aura;` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 6 | `import thaumcraft.common.world.aura.AuraHandler;` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 8 | `public final class AuraHelper {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 9 | `private AuraHelper() {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 12 | `public static float drainVis(Level level, BlockPos pos, float amount, boolean simulate) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 13 | `return AuraHandler.drainVis(level, pos, amount, simulate);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 16 | `public static float drainFlux(Level level, BlockPos pos, float amount, boolean simulate) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 17 | `return AuraHandler.drainFlux(level, pos, amount, simulate);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 20 | `public static void addVis(Level level, BlockPos pos, float amount) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 21 | `AuraHandler.addVis(level, pos, amount);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 24 | `public static float getVis(Level level, BlockPos pos) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 25 | `return AuraHandler.getVis(level, pos);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 28 | `public static void polluteAura(Level level, BlockPos pos, float amount, boolean showEffect) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 29 | `AuraHandler.addFlux(level, pos, amount, showEffect);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 32 | `public static float getFlux(Level level, BlockPos pos) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 33 | `return AuraHandler.getFlux(level, pos);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 36 | `public static int getAuraBase(Level level, BlockPos pos) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 37 | `return AuraHandler.getAuraBase(level, pos);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 40 | `public static boolean shouldPreserveAura(Level level, Player player, BlockPos pos) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 41 | `return AuraHandler.shouldPreserveAura(level, player, pos);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/crafting/IArcaneRecipe.java | 5 | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/api/crafting/IArcaneRecipe.java | 15 | `int getVis();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/crafting/IArcaneRecipe.java | 19 | `AspectList getCrystals();` |
| 05_neoforge_port/src/main/java/thaumcraft/api/items/IVisDiscountGear.java | 7 | `* Gear implementing this interface reduces vis costs for the wearer.` |
| 05_neoforge_port/src/main/java/thaumcraft/api/research/ScanAspect.java | 10 | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/api/research/ScanAspect.java | 25 | `AspectList aspects = getAspects(player, object);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/research/ScanAspect.java | 47 | `private static AspectList getAspects(ServerPlayer player, Object object) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/research/ScanOreDictionary.java | 8 | `import net.minecraft.resources.ResourceLocation;` |
| 05_neoforge_port/src/main/java/thaumcraft/api/research/ScanOreDictionary.java | 128 | `return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, path));` |
| 05_neoforge_port/src/main/java/thaumcraft/api/research/ScanOreDictionary.java | 132 | `return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(namespace, path));` |
| 05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java | 20 | `import net.minecraft.resources.ResourceLocation;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java | 31 | `public static final ResourceLocation PARTICLE_TEXTURE =` |
| 05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java | 32 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/particles.png");` |
| 05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java | 182 | `GlStateManager.SourceFactor.SRC_ALPHA,` |
| 05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java | 331 | `GlStateManager.SourceFactor.SRC_ALPHA,` |
| 05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java | 335 | `GlStateManager.SourceFactor.SRC_ALPHA,` |
| 05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java | 355 | `GlStateManager.SourceFactor.SRC_ALPHA,` |
| 05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java | 359 | `GlStateManager.SourceFactor.SRC_ALPHA,` |
| 05_neoforge_port/src/main/java/thaumcraft/client/fx/TCNitorClientEffects.java | 4 | `import net.minecraft.util.RandomSource;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/fx/TCNitorClientEffects.java | 22 | `RandomSource random = level.random;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/fx/TCNitorClientEffects.java | 67 | `RandomSource random = level.random;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/fx/TCNitorClientEffects.java | 103 | `RandomSource random = level.random;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/AspectTooltipComponent.java | 7 | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/AspectTooltipComponent.java | 12 | `public AspectTooltipComponent(AspectList aspects) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCArcaneWorkbenchScreen.java | 8 | `import net.minecraft.resources.ResourceLocation;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCArcaneWorkbenchScreen.java | 17 | `private static final ResourceLocation BACKGROUND =` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCArcaneWorkbenchScreen.java | 18 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/arcaneworkbench.png");` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCArcaneWorkbenchScreen.java | 105 | `menu.availableVis(),` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCArcaneWorkbenchScreen.java | 113 | `menu.availableVis() < menu.visCost() ? LEGACY_AVAILABLE_MISSING_COLOR : LEGACY_AVAILABLE_OK_COLOR` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCAspectTooltipEvents.java | 13 | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCAspectTooltipEvents.java | 30 | `AspectList aspects = AspectHelper.getObjectAspects(event.getItemStack());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java | 10 | `import net.minecraft.client.resources.sounds.SimpleSoundInstance;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java | 11 | `import net.minecraft.resources.ResourceLocation;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java | 29 | `private static final ResourceLocation LAYER_ID =` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java | 30 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "knowledge_gain_hud");` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java | 31 | `private static final ResourceLocation BOOK =` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java | 32 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/items/thaumonomicon.png");` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java | 33 | `private static final ResourceLocation KNOWLEDGE_THEORY =` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java | 34 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/research/knowledge_theory.png");` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java | 35 | `private static final ResourceLocation KNOWLEDGE_OBSERVATION =` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java | 36 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/research/knowledge_observation.png");` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java | 37 | `private static final ResourceLocation PARTICLES =` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java | 38 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/particles.png");` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java | 40 | `SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "learn"));` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java | 231 | `ResourceLocation categoryIcon = categoryIcon(current.category);` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java | 303 | `private static ResourceLocation knowledgeTexture(TCKnowledgeType type) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java | 307 | `private static ResourceLocation categoryIcon(String category) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java | 30 | `import net.minecraft.client.resources.sounds.SimpleSoundInstance;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java | 33 | `import net.minecraft.resources.ResourceLocation;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java | 53 | `private static final ResourceLocation BACKGROUND =` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java | 54 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_research_table.png");` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java | 55 | `private static final ResourceLocation BASE =` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java | 56 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_base.png");` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java | 57 | `private static final ResourceLocation PAPER =` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java | 58 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/paper.png");` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java | 59 | `private static final ResourceLocation PAPER_GILDED =` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java | 60 | `ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/papergilded.png");` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java | 61 | `private static final ResourceLocation UNKNOWN =` |

## Porting conclusion

- `TCWardedJarBlockEntity` is the reviewed first source type: it has persistent single-aspect storage, a blocked flag, simulation and exact drain semantics.
- Keep all other source types fail-closed until each receives its own storage and runtime parity audit.
- Re-run this audit after adding jar, tube, alembic, aura, essentia storage, or related block/entity implementations.

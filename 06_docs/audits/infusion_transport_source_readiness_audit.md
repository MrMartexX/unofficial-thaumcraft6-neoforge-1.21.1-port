# Infusion Transport Source Readiness Audit

Generated: 2026-06-20 15:31:45 +03:00

## Summary

| Metric | Count |
|---|---:|
| Scanned source files | 41 |
| Files with source-readiness signals | 36 |
| Review candidates with read and drain terms | 3 |
| Not-source-ready transport/storage files | 1 |
| Placeholder/bridge warning files | 7 |

## Interpretation

- This audit narrows the broad real-source candidate scan to the current essentia transport and related source API surface.
- A file is not source-ready just because it is a tube or transport block entity; it must expose stable readable storage and an all-or-nothing drain path.
- `TCInfusionAspectSourceResolver` now selects storage-bearing aspect containers; tube transport buffers remain excluded.
- Player-facing infusion completion remains disabled by policy.

## Potential review candidates

| File | Transport | BlockEntity | Storage | Read | Drain | Insert | Placeholder | Status |
|---|---:|---:|---:|---:|---:|---:|---:|---|
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | True | True | True | True | True | True | False | review_candidate_has_read_and_drain_terms |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionMutationExecutor.java | False | True | True | True | True | True | False | review_candidate_has_read_and_drain_terms |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | True | True | True | True | True | True | False | review_candidate_has_read_and_drain_terms |

## Not source ready or transport only

| File | Transport | BlockEntity | Storage | Read | Drain | Insert | Placeholder | Status |
|---|---:|---:|---:|---:|---:|---:|---:|---|
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCWardedJarBlock.java | False | True | True | True | False | False | False | not_source_ready_no_drain_term |

## Placeholder or bridge warnings

| File | Transport | BlockEntity | Storage | Read | Drain | Insert | Placeholder | Status |
|---|---:|---:|---:|---:|---:|---:|---:|---|
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | True | True | True | True | True | True | True | needs_manual_review_placeholder_or_bridge |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/TCEssentiaTubeMode.java | True | False | True | False | False | False | True | needs_manual_review_placeholder_or_bridge |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java | True | True | True | True | False | False | True | needs_manual_review_placeholder_or_bridge |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | True | False | True | True | False | False | True | needs_manual_review_placeholder_or_bridge |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCCreativeTabs.java | False | False | False | True | False | True | True | needs_manual_review_placeholder_or_bridge |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCDataComponents.java | False | False | True | False | False | False | True | needs_manual_review_placeholder_or_bridge |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java | True | False | True | True | False | True | True | needs_manual_review_placeholder_or_bridge |

## Focused evidence, first 160 rows

| File | Line | Category | Evidence |
|---|---:|---|---|
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 1 | storage_term | `package thaumcraft.common.blocks.essentia;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 39 | insert_term | `builder.add(FACING, ENABLED);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 75 | insert_term | `level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 76 | insert_term | `level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCWardedJarBlock.java | 1 | storage_term | `package thaumcraft.common.blocks.essentia;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCWardedJarBlock.java | 9 | block_entity | `import net.minecraft.world.level.block.entity.BlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCWardedJarBlock.java | 15 | block_entity, storage_term | `import thaumcraft.common.tiles.essentia.TCWardedJarBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCWardedJarBlock.java | 26 | block_entity | `public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCWardedJarBlock.java | 27 | block_entity | `return new TCWardedJarBlockEntity(pos, state);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCWardedJarBlock.java | 47 | block_entity, read_term | `return level.getBlockEntity(pos) instanceof TCWardedJarBlockEntity jar ? jar.comparatorSignal() : 0;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 5 | storage_term | `import thaumcraft.api.aspects.Aspect;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 6 | storage_term | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 7 | storage_term | `import thaumcraft.common.essentia.container.TCAspectSourceContainer;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 10 | storage_term | `* Distance-ordered infusion source over legacy-shaped aspect containers.` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 13 | storage_term, drain_term | `* {@code EssentiaHandler}. The executor still drains a complete audit plan at once, so this` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 19 | drain_term | `public static final String DRAIN_CHANGED_DURING_COMMIT = "container_source_drain_changed_during_commit";` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 28 | storage_term, read_term | `public AspectList availableAspects() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 29 | storage_term, read_term | `AspectList available = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 34 | storage_term | `AspectList stored = container.storedAspects();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 35 | storage_term | `if (stored != null) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 36 | storage_term, read_term, insert_term | `available.add(stored);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 43 | storage_term, drain_term | `public DrainResult drain(AspectList requiredAspects) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 44 | storage_term | `AspectList required = requiredAspects == null ? new AspectList() : requiredAspects.copy();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 46 | storage_term, read_term, drain_term | `return DrainResult.success(new AspectList(), availableAspects());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 50 | storage_term | `AspectList missing = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 51 | storage_term, read_term | `for (Aspect aspect : required.getAspects()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 52 | storage_term, read_term | `int remaining = required.getAmount(aspect);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 60 | storage_term, read_term | `int available = container.storedAspects().getAmount(aspect);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 65 | storage_term, insert_term | `allocations.add(new Allocation(container, aspect, allocated));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 69 | storage_term, insert_term | `missing.add(aspect, remaining);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 73 | read_term, drain_term | `return DrainResult.failed(MISSING_ASPECTS, missing, availableAspects());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 77 | storage_term, read_term, drain_term | `if (allocation.container().drainAspect(allocation.aspect(), allocation.amount(), true) != allocation.amount()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 78 | read_term, drain_term | `return DrainResult.failed(SIMULATION_FAILED, required, availableAspects());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 82 | storage_term, drain_term | `AspectList drained = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 84 | storage_term, read_term, drain_term | `int amount = allocation.container().drainAspect(allocation.aspect(), allocation.amount(), false);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 85 | storage_term, read_term | `if (amount != allocation.amount()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 86 | storage_term, drain_term | `AspectList undrained = required.copy().remove(drained);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 87 | read_term, drain_term | `return DrainResult.failed(DRAIN_CHANGED_DURING_COMMIT, undrained, availableAspects());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 89 | storage_term, read_term, drain_term, insert_term | `drained.add(allocation.aspect(), amount);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 91 | read_term, drain_term | `return DrainResult.success(drained, availableAspects());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCContainerInfusionAspectSource.java | 94 | storage_term, read_term | `private record Allocation(TCAspectSourceContainer container, Aspect aspect, int amount) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 3 | storage_term | `import thaumcraft.api.aspects.Aspect;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 4 | storage_term | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 7 | storage_term | `* Audit-only aspect source boundary for infusion completion planning.` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 10 | drain_term, insert_term | `* added behind the same all-or-nothing audit drain contract. Current implementations are` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 14 | storage_term, read_term | `AspectList availableAspects();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 16 | drain_term | `default DrainResult drain(TCInfusionCraftingPlan plan) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 18 | storage_term, read_term, drain_term | `return DrainResult.failed("missing_crafting_plan", new AspectList(), availableAspects());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 20 | drain_term | `return drain(plan.requiredAspects());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 23 | storage_term, drain_term | `DrainResult drain(AspectList requiredAspects);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 25 | storage_term, read_term | `static TCInfusionAspectSource memory(AspectList available) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 29 | storage_term, read_term | `private static AspectList missingAspects(AspectList requiredAspects, AspectList availableAspects) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 30 | storage_term | `AspectList missing = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 31 | storage_term | `AspectList required = requiredAspects == null ? new AspectList() : requiredAspects.copy();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 32 | storage_term, read_term | `AspectList available = availableAspects == null ? new AspectList() : availableAspects.copy();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 33 | storage_term, read_term | `for (Aspect aspect : required.getAspects()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 34 | storage_term, read_term | `int missingAmount = required.getAmount(aspect) - available.getAmount(aspect);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 35 | storage_term, read_term | `if (missingAmount > 0) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 36 | storage_term, read_term, insert_term | `missing.add(aspect, missingAmount);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 43 | storage_term, read_term | `private final AspectList available;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 45 | storage_term, read_term | `private Memory(AspectList available) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 46 | storage_term, read_term | `this.available = available == null ? new AspectList() : available.copy();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 50 | storage_term, read_term | `public AspectList availableAspects() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 55 | storage_term, drain_term | `public DrainResult drain(AspectList requiredAspects) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 56 | storage_term | `AspectList required = requiredAspects == null ? new AspectList() : requiredAspects.copy();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 57 | storage_term, read_term | `AspectList missing = missingAspects(required, available);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 59 | read_term, drain_term | `return DrainResult.failed("missing_aspects", missing, available.copy());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 62 | storage_term, drain_term | `AspectList drained = new AspectList();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 63 | storage_term, read_term | `for (Aspect aspect : required.getAspects()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 64 | storage_term, read_term | `int amount = required.getAmount(aspect);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 65 | storage_term, read_term | `if (amount <= 0) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 68 | storage_term, read_term, drain_term | `available.remove(aspect, amount);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 69 | storage_term, read_term, drain_term, insert_term | `drained.add(aspect, amount);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 71 | read_term, drain_term | `return DrainResult.success(drained, available.copy());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 75 | storage_term, drain_term | `record DrainResult(boolean success, String reason, AspectList drainedAspects, AspectList missingAspects, AspectList remainingAspects) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 76 | drain_term | `public DrainResult {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 78 | storage_term, drain_term | `drainedAspects = drainedAspects == null ? new AspectList() : drainedAspects.copy();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 79 | storage_term | `missingAspects = missingAspects == null ? new AspectList() : missingAspects.copy();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 80 | storage_term | `remainingAspects = remainingAspects == null ? new AspectList() : remainingAspects.copy();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 83 | storage_term, drain_term | `public static DrainResult success(AspectList drainedAspects, AspectList remainingAspects) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 84 | storage_term, drain_term | `return new DrainResult(true, "drained", drainedAspects, new AspectList(), remainingAspects);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 87 | storage_term, drain_term | `public static DrainResult failed(String reason, AspectList missingAspects, AspectList remainingAspects) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 88 | storage_term, drain_term | `return new DrainResult(false, reason, new AspectList(), missingAspects, remainingAspects);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 9 | block_entity | `import net.minecraft.world.level.block.entity.BlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 10 | storage_term | `import thaumcraft.common.essentia.container.TCAspectSourceContainer;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 11 | block_entity | `import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 14 | storage_term | `* Boundary for real essentia/aspect source discovery.` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 16 | storage_term, drain_term | `* <p>Legacy {@code EssentiaHandler.drainEssentia(matrix, aspect, null, 12, ...)} searched a` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 19 | transport, storage_term | `* block entities. Transient tube buffers are deliberately not infusion sources.</p>` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 28 | block_entity | `public static Optional<TCInfusionAspectSource> findSource(TCInfusionMatrixBlockEntity matrix, TCInfusionCraftingPlan plan) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 42 | block_entity, read_term | `BlockEntity blockEntity = level.getBlockEntity(sourcePos);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 43 | block_entity | `if (blockEntity instanceof TCAspectSourceContainer container) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 44 | insert_term | `candidates.add(new Candidate(sourcePos, container));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAssembly.java | 9 | storage_term | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAssembly.java | 12 | storage_term | `* Server-owned snapshot of the legacy infusion altar inputs before any item or essentia mutation.` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAssembly.java | 17 | storage_term | `private final AspectList aspects;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAssembly.java | 19 | storage_term | `private TCInfusionAssembly(ItemStack catalyst, List<ItemStack> components, AspectList aspects) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAssembly.java | 26 | storage_term | `this.aspects = aspects == null ? new AspectList() : aspects.copy();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAssembly.java | 29 | storage_term | `public static TCInfusionAssembly of(ItemStack catalyst, List<ItemStack> components, AspectList aspects) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAssembly.java | 41 | storage_term | `public AspectList aspects() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAssembly.java | 86 | drain_term | `TCInfusionRecipeMatcher.removeRequiredAspects(recipe, aspects),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 10 | transport, storage_term | `import thaumcraft.common.essentia.transport.TCEssentiaTubeMode;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 11 | transport, storage_term | `import thaumcraft.common.essentia.transport.TCLegacyEssentiaTransportNode;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 12 | transport, block_entity, storage_term | `import thaumcraft.common.essentia.transport.blockentity.TCLegacyTubeBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 24 | storage_term | `import thaumcraft.api.aspects.Aspect;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 25 | storage_term | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 29 | block_entity | `import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 30 | block_entity | `import thaumcraft.common.tiles.crafting.TCInfusionPedestalBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 31 | block_entity, storage_term | `import thaumcraft.common.tiles.essentia.TCWardedJarBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 44 | insert_term | `lines.add("# Infusion Behavior Audit");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 45 | insert_term | `lines.add("");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 46 | insert_term | `lines.add("Generated by the NeoForge runtime audit exporter after server recipe reload.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 47 | insert_term | `lines.add("");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 48 | insert_term | `lines.add("## Summary");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 49 | insert_term | `lines.add("");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 50 | insert_term | `lines.add("\| Check \| Result \|");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 51 | insert_term | `lines.add("\|---\|---:\|");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 52 | insert_term | `lines.add("\| Passed \| " + report.passed() + " \|");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 53 | insert_term | `lines.add("\| Failed \| " + report.failed() + " \|");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 54 | insert_term | `lines.add("\| Infusion recipes loaded \| " + report.infusionRecipeCount() + " \|");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 55 | insert_term | `lines.add("");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 56 | insert_term | `lines.add("## Checks");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 57 | insert_term | `lines.add("");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 58 | insert_term | `lines.add("\| Name \| Result \| Notes \|");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 59 | insert_term | `lines.add("\|---\|---\|---\|");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 61 | insert_term | `lines.add("\| " + check.name() + " \| " + (check.passed() ? "PASS" : "FAIL") + " \| " + check.notes().replace("\|", "\\\|") + " \|");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 63 | insert_term | `lines.add("");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 64 | insert_term | `lines.add("## Boundary");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 65 | insert_term | `lines.add("");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 66 | insert_term | `lines.add("- This validates the current server-owned infusion input snapshot, recipe matcher and active-plan readiness boundary.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 67 | insert_term, placeholder_term | `lines.add("- The audit also places a matrix and pedestals in a runtime server world to validate legacy-aligned pedestal discovery.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 68 | insert_term | `lines.add("- Legacy parity point: component matching uses Forge/NeoForge RecipeMatcher 1:1 semantics, so extra pedestal inputs must fail.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 69 | drain_term, insert_term | `lines.add("- The audit-only mutation executor consumes validated inputs, while normal player interaction remains disabled.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 70 | transport, storage_term, drain_term, insert_term | `lines.add("- Legacy-shaped source discovery scans aspect containers in the range-12 volume and drains nearest containers first; transient tube buffers are excluded.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 71 | insert_term | `lines.add("- This does not yet implement the legacy one-point craft cycle, instability events, source beams, particles or matrix animation.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 81 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 91 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 99 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 105 | storage_term, read_term | `&& recipe.aspectCosts().getFirst().resolvedAspect() == Aspect.AIR` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 106 | storage_term, read_term | `&& recipe.aspectCosts().getFirst().amount() == 50` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 120 | storage_term, insert_term | `new AspectList().add(Aspect.AIR, 50)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 123 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 138 | storage_term, insert_term | `new AspectList().add(Aspect.AIR, 50)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 140 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 143 | insert_term | `"legacy RecipeMatcher accepts component order changes"` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 153 | storage_term, insert_term | `new AspectList().add(Aspect.AIR, 50)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 156 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 168 | storage_term, insert_term | `new AspectList().add(Aspect.AIR, 49)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 171 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 183 | storage_term, insert_term | `new AspectList().add(Aspect.AIR, 50)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 186 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 198 | storage_term, insert_term | `new AspectList().add(Aspect.AIR, 50)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 200 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 206 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 212 | insert_term | `addRuntimeMatrixChecks(server, holder, checks);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 213 | insert_term | `addRuntimeMutationExecutorChecks(server, holder, checks);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 214 | insert_term | `addContainerRemainderPolicyChecks(checks);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 215 | insert_term | `addAspectSourceBoundaryChecks(checks);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 216 | insert_term | `addAspectSourceMutationExecutorChecks(server, holder, checks);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 217 | insert_term | `addAspectSourceResolverChecks(checks);` |

## Porting conclusion

- Do not connect infusion completion directly to tubes or transient transport buffers: legacy `EssentiaHandler` discovered `IAspectSource` containers.
- The first selected storage-bearing source is `TCWardedJarBlockEntity` through `TCAspectSourceContainer`; player-facing completion remains audit-only.
- This audit should be re-run after changes under `thaumcraft.common.essentia.transport`, `thaumcraft.common.blocks.essentia`, or infusion source resolver code.

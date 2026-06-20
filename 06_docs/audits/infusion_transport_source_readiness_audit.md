# Infusion Transport Source Readiness Audit

Generated: 2026-06-20 03:06:20 +03:00

## Summary

| Metric | Count |
|---|---:|
| Scanned source files | 38 |
| Files with source-readiness signals | 33 |
| Review candidates with read and drain terms | 2 |
| Not-source-ready transport/storage files | 1 |
| Placeholder/bridge warning files | 7 |

## Interpretation

- This audit narrows the broad real-source candidate scan to the current essentia transport and related source API surface.
- A file is not source-ready just because it is a tube or transport block entity; it must expose stable readable storage and an all-or-nothing drain path.
- If no source-ready file is selected after manual review, `TCInfusionAspectSourceResolver` must remain fail-closed.
- Player-facing infusion completion remains disabled by policy.

## Potential review candidates

| File | Transport | BlockEntity | Storage | Read | Drain | Insert | Placeholder | Status |
|---|---:|---:|---:|---:|---:|---:|---:|---|
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionMutationExecutor.java | False | True | True | True | True | True | False | review_candidate_has_read_and_drain_terms |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCAbstractEssentiaTransportBlockEntity.java | True | True | True | True | True | True | False | review_candidate_has_read_and_drain_terms |

## Not source ready or transport only

| File | Transport | BlockEntity | Storage | Read | Drain | Insert | Placeholder | Status |
|---|---:|---:|---:|---:|---:|---:|---:|---|
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | True | True | True | True | False | False | False | not_source_ready_no_drain_term |

## Placeholder or bridge warnings

| File | Transport | BlockEntity | Storage | Read | Drain | Insert | Placeholder | Status |
|---|---:|---:|---:|---:|---:|---:|---:|---|
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | False | True | True | True | True | True | True | needs_manual_review_placeholder_or_bridge |
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
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 3 | storage_term | `import thaumcraft.api.aspects.Aspect;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 4 | storage_term | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 7 | storage_term | `* Audit-only aspect source boundary for infusion completion planning.` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 9 | transport | `* <p>This interface is intentionally tiny so future jar, tube, alembic or aura-backed` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSource.java | 10 | drain_term, insert_term | `* sources can be added behind the same all-or-nothing drain contract. The current` |
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
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 4 | block_entity | `import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 7 | storage_term | `* Boundary for future real essentia/aspect source discovery.` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 10 | transport | `* audit-only in-memory source path separate from player-facing jar, tube, alembic,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAspectSourceResolver.java | 20 | block_entity | `public static Optional<TCInfusionAspectSource> findSource(TCInfusionMatrixBlockEntity matrix, TCInfusionCraftingPlan plan) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAssembly.java | 9 | storage_term | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAssembly.java | 12 | storage_term | `* Server-owned snapshot of the legacy infusion altar inputs before any item or essentia mutation.` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAssembly.java | 17 | storage_term | `private final AspectList aspects;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAssembly.java | 19 | storage_term | `private TCInfusionAssembly(ItemStack catalyst, List<ItemStack> components, AspectList aspects) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAssembly.java | 26 | storage_term | `this.aspects = aspects == null ? new AspectList() : aspects.copy();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAssembly.java | 29 | storage_term | `public static TCInfusionAssembly of(ItemStack catalyst, List<ItemStack> components, AspectList aspects) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAssembly.java | 41 | storage_term | `public AspectList aspects() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionAssembly.java | 86 | drain_term | `TCInfusionRecipeMatcher.removeRequiredAspects(recipe, aspects),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 20 | storage_term | `import thaumcraft.api.aspects.Aspect;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 21 | storage_term | `import thaumcraft.api.aspects.AspectList;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 25 | block_entity | `import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 26 | block_entity | `import thaumcraft.common.tiles.crafting.TCInfusionPedestalBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 39 | insert_term | `lines.add("# Infusion Behavior Audit");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 40 | insert_term | `lines.add("");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 41 | insert_term | `lines.add("Generated by the NeoForge runtime audit exporter after server recipe reload.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 42 | insert_term | `lines.add("");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 43 | insert_term | `lines.add("## Summary");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 44 | insert_term | `lines.add("");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 45 | insert_term | `lines.add("\| Check \| Result \|");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 46 | insert_term | `lines.add("\|---\|---:\|");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 47 | insert_term | `lines.add("\| Passed \| " + report.passed() + " \|");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 48 | insert_term | `lines.add("\| Failed \| " + report.failed() + " \|");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 49 | insert_term | `lines.add("\| Infusion recipes loaded \| " + report.infusionRecipeCount() + " \|");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 50 | insert_term | `lines.add("");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 51 | insert_term | `lines.add("## Checks");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 52 | insert_term | `lines.add("");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 53 | insert_term | `lines.add("\| Name \| Result \| Notes \|");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 54 | insert_term | `lines.add("\|---\|---\|---\|");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 56 | insert_term | `lines.add("\| " + check.name() + " \| " + (check.passed() ? "PASS" : "FAIL") + " \| " + check.notes().replace("\|", "\\\|") + " \|");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 58 | insert_term | `lines.add("");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 59 | insert_term | `lines.add("## Boundary");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 60 | insert_term | `lines.add("");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 61 | insert_term | `lines.add("- This validates the current server-owned infusion input snapshot, recipe matcher and active-plan readiness boundary.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 62 | insert_term, placeholder_term | `lines.add("- The audit also places a matrix and pedestals in a runtime server world to validate legacy-aligned pedestal discovery.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 63 | insert_term | `lines.add("- Legacy parity point: component matching uses Forge/NeoForge RecipeMatcher 1:1 semantics, so extra pedestal inputs must fail.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 64 | storage_term, insert_term | `lines.add("- The active completion plan is still read-only: it checks current catalyst, planned component pedestals and aspect availability before any future mutation.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 65 | storage_term, insert_term | `lines.add("- This does not implement item consumption, instability events, essentia transport, particles, beams or matrix animation.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 75 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 85 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 93 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 99 | storage_term, read_term | `&& recipe.aspectCosts().getFirst().resolvedAspect() == Aspect.AIR` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 100 | storage_term, read_term | `&& recipe.aspectCosts().getFirst().amount() == 50` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 114 | storage_term, insert_term | `new AspectList().add(Aspect.AIR, 50)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 117 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 132 | storage_term, insert_term | `new AspectList().add(Aspect.AIR, 50)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 134 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 137 | insert_term | `"legacy RecipeMatcher accepts component order changes"` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 147 | storage_term, insert_term | `new AspectList().add(Aspect.AIR, 50)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 150 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 162 | storage_term, insert_term | `new AspectList().add(Aspect.AIR, 49)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 165 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 177 | storage_term, insert_term | `new AspectList().add(Aspect.AIR, 50)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 180 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 192 | storage_term, insert_term | `new AspectList().add(Aspect.AIR, 50)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 194 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 200 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 206 | insert_term | `addRuntimeMatrixChecks(server, holder, checks);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 207 | insert_term | `addRuntimeMutationExecutorChecks(server, holder, checks);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 208 | insert_term | `addContainerRemainderPolicyChecks(checks);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 209 | insert_term | `addAspectSourceBoundaryChecks(checks);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 210 | insert_term | `addAspectSourceMutationExecutorChecks(server, holder, checks);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 211 | insert_term | `addAspectSourceResolverChecks(checks);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 218 | insert_term | `private static void addRuntimeMatrixChecks(MinecraftServer server, RecipeHolder<TCInfusionRecipe> cloudRing, ArrayList<Check> checks) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 234 | block_entity | `TCInfusionMatrixBlockEntity matrix = blockEntity(level, matrixPos, TCInfusionMatrixBlockEntity.class);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 235 | block_entity | `TCInfusionPedestalBlockEntity center = blockEntity(level, centerPos, TCInfusionPedestalBlockEntity.class);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 236 | block_entity | `TCInfusionPedestalBlockEntity feather = blockEntity(level, featherPos, TCInfusionPedestalBlockEntity.class);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 237 | block_entity | `TCInfusionPedestalBlockEntity crystal = blockEntity(level, crystalPos, TCInfusionPedestalBlockEntity.class);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 238 | block_entity | `TCInfusionPedestalBlockEntity emptySameColumn = blockEntity(level, outOfColumnPos, TCInfusionPedestalBlockEntity.class);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 240 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 252 | storage_term, read_term | `center.setStoredForValidation(new ItemStack(TCItems.BAUBLE_RING.get()));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 253 | storage_term | `feather.setStoredForValidation(new ItemStack(Items.FEATHER));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 254 | storage_term, read_term | `crystal.setStoredForValidation(new ItemStack(TCItems.CRYSTAL_ESSENCE_AER.get()));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 256 | block_entity, storage_term, insert_term | `TCInfusionMatrixBlockEntity.Snapshot snapshot = matrix.createSnapshot(new AspectList().add(Aspect.AIR, 50));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 257 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 265 | storage_term, insert_term | `TCInfusionValidationResult runtimeResult = matrix.validateAgainst(cloudRing, new AspectList().add(Aspect.AIR, 50));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 266 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 276 | storage_term, insert_term | `new AspectList().add(Aspect.AIR, 50),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 280 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 288 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 295 | storage_term, read_term | `&& activePlan.get().requiredAspects().getAmount(Aspect.AIR) == 50` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 301 | storage_term, read_term | `+ ", aspects=" + plan.requiredAspectAmount())` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 304 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 312 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 317 | storage_term, read_term | `&& loaded.requiredAspects().getAmount(Aspect.AIR) == 50` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 322 | storage_term, insert_term | `TCInfusionCompletionPlan completionPlan = matrix.createCompletionPlan(new AspectList().add(Aspect.AIR, 50));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 323 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 328 | storage_term, read_term | `&& completionPlan.requiredAspectAmount() == 50` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 332 | storage_term, read_term | `+ ", missing=" + completionPlan.missingAspectAmount()` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 334 | storage_term, insert_term | `TCInfusionCompletionPlan missingAspectCompletion = matrix.createCompletionPlan(new AspectList().add(Aspect.AIR, 49));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 335 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 339 | storage_term, read_term | `&& missingAspectCompletion.missingAspects().getAmount(Aspect.AIR) == 1,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 341 | storage_term, read_term | `+ ", missingAer=" + missingAspectCompletion.missingAspects().getAmount(Aspect.AIR)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 344 | storage_term | `center.setStoredForValidation(new ItemStack(Items.IRON_INGOT));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 345 | storage_term, insert_term | `TCInfusionCompletionPlan changedCatalystCompletion = matrix.createCompletionPlan(new AspectList().add(Aspect.AIR, 50));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 346 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 351 | storage_term, read_term | `center.setStoredForValidation(new ItemStack(TCItems.BAUBLE_RING.get()));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 353 | storage_term | `feather.setStoredForValidation(new ItemStack(Items.STICK));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 354 | storage_term, insert_term | `TCInfusionCompletionPlan changedComponentCompletion = matrix.createCompletionPlan(new AspectList().add(Aspect.AIR, 50));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 355 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 360 | storage_term | `feather.setStoredForValidation(new ItemStack(Items.FEATHER));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 363 | storage_term, insert_term | `TCInfusionCompletionPlan missingComponentPedestalCompletion = matrix.createCompletionPlan(new AspectList().add(Aspect.AIR, 50));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 364 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 371 | block_entity | `feather = blockEntity(level, featherPos, TCInfusionPedestalBlockEntity.class);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 373 | storage_term | `feather.setStoredForValidation(new ItemStack(Items.FEATHER));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 378 | storage_term, insert_term | `new AspectList().add(Aspect.AIR, 50),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 381 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 387 | insert_term | `checks.add(new Check(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 394 | block_entity | `TCInfusionPedestalBlockEntity extra = blockEntity(level, extraPos, TCInfusionPedestalBlockEntity.class);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 396 | storage_term | `extra.setStoredForValidation(new ItemStack(Items.STICK));` |

## Porting conclusion

- Do not connect infusion completion to tubes or transport entities unless a reviewed API provides exact availability and all-or-nothing drain semantics.
- The next implementation should either add a stable storage-bearing source type first, or explicitly document why infusion remains audit-only.
- This audit should be re-run after changes under `thaumcraft.common.essentia.transport`, `thaumcraft.common.blocks.essentia`, or infusion source resolver code.

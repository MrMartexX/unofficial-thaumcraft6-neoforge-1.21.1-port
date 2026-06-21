# Smelter Pending Flux Aura Bridge Audit

Generated: 2026-06-22 00:28:01 +03:00

## Summary

| Metric | Count |
|---|---:|
| Aura/flux/runtime evidence rows | 434 |
| Smelter pending-flux rows | 58 |
| Vent dependency rows | 157 |
| Total evidence rows | 738 |

## Interpretation

- This audit prepares the boundary between `TCSmelterBlockEntity.pendingFlux` and the current aura/flux API.
- The next code slice should use only an existing current-port aura mutation method proven below. If no direct flux/pollution API appears, add a small aura-side boundary first instead of guessing.
- Smelter vent mitigation should remain separate from direct aura pollution because legacy vents intercept pending flux before pollution.

## Aura and flux API evidence, first 220 rows

| File | Line | Category | Evidence |
|---|---:|---|---|
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/Aspect.java | 171 | flux_mutation | `public static Aspect FLUX = new Aspect("vitium", 0x800080, new Aspect[] { ENTROPY, MAGIC });` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java | 160 | flux_mutation | `t.add(Aspect.FLUX, in.getAmount(Aspect.FLUX));` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 1 | aura_api_identity | `package thaumcraft.api.aura;` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 8 | aura_api_identity | `public final class AuraHelper {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 9 | aura_api_identity | `private AuraHelper() {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 12 | runtime_storage | `public static float drainVis(Level level, BlockPos pos, float amount, boolean simulate) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 13 | runtime_storage | `return AuraHandler.drainVis(level, pos, amount, simulate);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 16 | flux_mutation, runtime_storage | `public static float drainFlux(Level level, BlockPos pos, float amount, boolean simulate) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 17 | flux_mutation, runtime_storage | `return AuraHandler.drainFlux(level, pos, amount, simulate);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 21 | runtime_storage | `AuraHandler.addVis(level, pos, amount);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 25 | runtime_storage | `return AuraHandler.getVis(level, pos);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 28 | flux_mutation, runtime_storage | `public static void polluteAura(Level level, BlockPos pos, float amount, boolean showEffect) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 29 | flux_mutation, runtime_storage | `AuraHandler.addFlux(level, pos, amount, showEffect);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 32 | flux_mutation, runtime_storage | `public static float getFlux(Level level, BlockPos pos) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 33 | flux_mutation, runtime_storage | `return AuraHandler.getFlux(level, pos);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 36 | runtime_storage | `public static int getAuraBase(Level level, BlockPos pos) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 37 | runtime_storage | `return AuraHandler.getAuraBase(level, pos);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 40 | runtime_storage | `public static boolean shouldPreserveAura(Level level, Player player, BlockPos pos) {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/aura/AuraHelper.java | 41 | runtime_storage | `return AuraHandler.shouldPreserveAura(level, player, pos);` |
| 05_neoforge_port/src/main/java/thaumcraft/api/potions/PotionFluxTaint.java | 8 | flux_mutation | `/** Legacy Flux Taint effect. Tainted-mob healing remains owned by the future taint entity subsystem. */` |
| 05_neoforge_port/src/main/java/thaumcraft/api/potions/PotionFluxTaint.java | 9 | flux_mutation | `public final class PotionFluxTaint extends MobEffect {` |
| 05_neoforge_port/src/main/java/thaumcraft/api/potions/PotionFluxTaint.java | 10 | flux_mutation | `public PotionFluxTaint() {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 8 | vent_dependency, runtime_storage | `import net.neoforged.neoforge.client.event.RenderLevelStageEvent;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 47 | vent_dependency, runtime_storage | `public static void onRenderLevelStage(RenderLevelStageEvent event) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 48 | vent_dependency, runtime_storage | `if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 60 | vent_dependency, runtime_storage | `if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 32 | flux_mutation | `private static final int FLUX = 0x800080;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 72 | vent_dependency, runtime_storage | `event.register((state, level, pos, tintIndex) -> crystalColor(state.getBlock()),` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 81 | vent_dependency, runtime_storage | `event.register((state, level, pos, tintIndex) -> candleColor(state.getBlock(), tintIndex),` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 114 | flux_mutation, vent_dependency | `event.register((stack, tintIndex) -> FLUX, TCBlocks.CRYSTAL_VITIUM.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 202 | flux_mutation | `return FLUX;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectParityValidator.java | 140 | flux_mutation | `merged.add(Aspect.AIR, 1).merge(new AspectList().add(Aspect.AIR, 7).add(Aspect.FLUX, 2));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectParityValidator.java | 142 | flux_mutation | `expectEquals(2, merged.getAmount(Aspect.FLUX), "AspectList merge list inserts new aspect");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectParityValidator.java | 143 | flux_mutation | `merged.remove(new AspectList().add(Aspect.AIR, 3).add(Aspect.FLUX, 2));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectParityValidator.java | 145 | flux_mutation | `expectEquals(0, merged.getAmount(Aspect.FLUX), "AspectList remove list removes depleted aspect");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectParityValidator.java | 178 | flux_mutation | `AspectList primalOnly = AspectHelper.getPrimalAspects(new AspectList().add(Aspect.AIR, 4).add(Aspect.FLUX, 2));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectParityValidator.java | 182 | flux_mutation | `expectEquals(0, primalOnly.getAmount(Aspect.FLUX), "getPrimalAspects excludes flux");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectParityValidator.java | 184 | flux_mutation | `AspectList auraOnly = AspectHelper.getAuraAspects(new AspectList().add(Aspect.AIR, 4).add(Aspect.FLUX, 2));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectParityValidator.java | 185 | flux_mutation | `expectEquals(7, auraOnly.size(), "getAuraAspects includes six primals and flux");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectParityValidator.java | 186 | flux_mutation | `expectEquals(2, auraOnly.getAmount(Aspect.FLUX), "getAuraAspects keeps flux amount");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectParityValidator.java | 244 | flux_mutation | `expectDirect(tags, "crystal_vitium", amount(Aspect.FLUX, 15), amount(Aspect.CRYSTAL, 10));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectStackRules.java | 487 | flux_mutation | `.add(Aspect.FLUX, 1)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectStackRules.java | 503 | flux_mutation | `.add(Aspect.FLUX, 1)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectStackRules.java | 530 | flux_mutation | `.add(Aspect.FLUX, 1)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectStackRules.java | 540 | flux_mutation | `.add(Aspect.FLUX, 1)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectStackRules.java | 579 | flux_mutation | `.add(Aspect.FLUX, 1)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectStackRules.java | 595 | flux_mutation | `.add(Aspect.FLUX, 1)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectStackRules.java | 622 | flux_mutation | `.add(Aspect.FLUX, 1)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectStackRules.java | 632 | flux_mutation | `.add(Aspect.FLUX, 1)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectStackRules.java | 671 | flux_mutation | `.add(Aspect.FLUX, 1)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectStackRules.java | 686 | flux_mutation | `.add(Aspect.FLUX, 1));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectStackRules.java | 712 | flux_mutation | `.add(Aspect.FLUX, 1)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectStackRules.java | 723 | flux_mutation | `.add(Aspect.FLUX, 1));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectStackRules.java | 761 | flux_mutation | `.add(Aspect.FLUX, 1)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectStackRules.java | 777 | flux_mutation | `.add(Aspect.FLUX, 1)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectStackRules.java | 804 | flux_mutation | `.add(Aspect.FLUX, 1)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectStackRules.java | 814 | flux_mutation | `.add(Aspect.FLUX, 1)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 48 | smelter_pending_flux, runtime_storage | `if (level.isClientSide \|\| type != TCBlockEntities.SMELTER_BASIC.get()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/world/taint/TCFluxGooBlock.java | 21 | aura_api_identity | `import thaumcraft.api.aura.AuraHelper;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/world/taint/TCFluxGooBlock.java | 24 | flux_mutation | `/** Finite legacy Flux Goo state used by infusion and taint world behavior. */` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/world/taint/TCFluxGooBlock.java | 25 | flux_mutation | `public final class TCFluxGooBlock extends Block {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/world/taint/TCFluxGooBlock.java | 27 | flux_mutation | `public static final MapCodec<TCFluxGooBlock> CODEC = simpleCodec(TCFluxGooBlock::new);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/world/taint/TCFluxGooBlock.java | 29 | flux_mutation | `public TCFluxGooBlock(BlockBehaviour.Properties properties) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/world/taint/TCFluxGooBlock.java | 71 | aura_api_identity, flux_mutation, runtime_storage | `AuraHelper.polluteAura(level, pos, 1.0F, true);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/world/taint/TCFluxGooBlock.java | 73 | aura_api_identity, flux_mutation, runtime_storage | `AuraHelper.polluteAura(level, pos, 1.0F, true);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleAspectCost.java | 93 | flux_mutation | `case "flux" -> Aspect.FLUX.getTag();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleBehaviorAudit.java | 21 | aura_api_identity | `import thaumcraft.api.aura.AuraHelper;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleBehaviorAudit.java | 57 | flux_mutation | `lines.add("- It validates manual item use, item-entity absorption boundaries, special-item ignore marking and server-side spill pollution.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleBehaviorAudit.java | 173 | runtime_storage | `AuraHandler.seedAuraChunk(level, spillPos, 200);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleBehaviorAudit.java | 176 | flux_mutation | `spillCrucible.addAspectForValidation(Aspect.FLUX, 2);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleBehaviorAudit.java | 178 | aura_api_identity, flux_mutation, runtime_storage | `float fluxBeforeSpill = AuraHelper.getFlux(level, spillPos);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleBehaviorAudit.java | 180 | aura_api_identity, flux_mutation, runtime_storage | `float fluxAfterSpill = AuraHelper.getFlux(level, spillPos);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleBehaviorAudit.java | 182 | flux_mutation | `"spill_remnants_pollutes_aura_like_legacy",` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleBehaviorAudit.java | 183 | flux_mutation | `closeEnough(fluxAfterSpill - fluxBeforeSpill, 3.0F)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleBehaviorAudit.java | 187 | flux_mutation | `"deltaFlux=" + (fluxAfterSpill - fluxBeforeSpill) + ", heatBefore=" + heatBeforeSpill + ", heatAfter=" + spillCrucible.getHeat()` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleBehaviorAudit.java | 191 | runtime_storage | `AuraHandler.seedAuraChunk(level, overflowPos, 200);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleBehaviorAudit.java | 194 | aura_api_identity, flux_mutation, runtime_storage | `float fluxBeforeOverflow = AuraHelper.getFlux(level, overflowPos);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleBehaviorAudit.java | 196 | aura_api_identity, flux_mutation, runtime_storage | `float fluxAfterOverflow = AuraHelper.getFlux(level, overflowPos);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleBehaviorAudit.java | 198 | flux_mutation | `"overflow_spill_random_removes_one_aspect_and_pollutes",` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleBehaviorAudit.java | 200 | flux_mutation | `&& closeEnough(fluxAfterOverflow - fluxBeforeOverflow, 0.25F),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleBehaviorAudit.java | 201 | flux_mutation | `"aspects=" + overflowCrucible.getAspects().visSize() + ", deltaFlux=" + (fluxAfterOverflow - fluxBeforeOverflow)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 78 | flux_mutation | `lines.add("- Persistent stability math, all 24 legacy instability rolls, Flux Goo placement, harm effects and stabilizer mitigation are audited.");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 338 | flux_mutation, vent_dependency | `&& counts[TCInfusionInstabilityEvent.EJECT_FLUX_GOO_DROP.ordinal()] == 2` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 339 | flux_mutation, vent_dependency | `&& counts[TCInfusionInstabilityEvent.EJECT_FLUX_DROP.ordinal()] == 2` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 340 | flux_mutation, vent_dependency | `&& counts[TCInfusionInstabilityEvent.EJECT_FLUX_GOO_DELETE.ordinal()] == 1` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 341 | flux_mutation, vent_dependency | `&& counts[TCInfusionInstabilityEvent.EJECT_FLUX_DELETE.ordinal()] == 1` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 351 | flux_mutation, vent_dependency | `&& TCInfusionInstabilityEvent.EJECT_FLUX_GOO_DROP.isSupportedByCurrentPort()` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 352 | flux_mutation, vent_dependency | `&& TCInfusionInstabilityEvent.EJECT_FLUX_GOO_DELETE.isSupportedByCurrentPort()` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 393 | flux_mutation, vent_dependency | `TCInfusionInstabilityEvent.EJECT_FLUX_GOO_DROP,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 397 | flux_mutation, vent_dependency | `"runtime_flux_goo_drop_event_matches_legacy_effect",` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 400 | flux_mutation, runtime_storage | `&& level.getBlockState(pedestalPos.above()).is(TCBlocks.FLUX_GOO.get()),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 405 | aura_api_identity, flux_mutation, runtime_storage | `float fluxBefore = thaumcraft.api.aura.AuraHelper.getFlux(level, pedestalPos);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 406 | flux_mutation | `TCInfusionInstabilityExecutor.ExecutionResult fluxDelete = TCInfusionInstabilityExecutor.execute(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 408 | flux_mutation, vent_dependency | `TCInfusionInstabilityEvent.EJECT_FLUX_DELETE,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 411 | aura_api_identity, flux_mutation, runtime_storage | `float fluxDelta = thaumcraft.api.aura.AuraHelper.getFlux(level, pedestalPos) - fluxBefore;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 413 | flux_mutation, vent_dependency | `"runtime_supported_flux_delete_event_matches_legacy_effect",` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 414 | flux_mutation | `fluxDelete.status() == TCInfusionInstabilityExecutor.ExecutionResult.Status.EXECUTED` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 416 | flux_mutation | `&& close(fluxDelta, 5.0F),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 417 | flux_mutation | `"target=" + fluxDelete.targetPos() + ", fluxDelta=" + fluxDelta` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityEvent.java | 9 | flux_mutation | `EJECT_FLUX_GOO_DROP,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityEvent.java | 10 | flux_mutation | `EJECT_FLUX_DROP,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityEvent.java | 11 | flux_mutation | `EJECT_FLUX_GOO_DELETE,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityEvent.java | 12 | flux_mutation | `EJECT_FLUX_DELETE,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityEvent.java | 24 | flux_mutation | `case 12, 13 -> EJECT_FLUX_GOO_DROP;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityEvent.java | 25 | flux_mutation | `case 14, 15 -> EJECT_FLUX_DROP;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityEvent.java | 26 | flux_mutation | `case 16 -> EJECT_FLUX_GOO_DELETE;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityEvent.java | 27 | flux_mutation | `case 17 -> EJECT_FLUX_DELETE;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 16 | aura_api_identity | `import thaumcraft.api.aura.AuraHelper;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 42 | vent_dependency, runtime_storage | `return execute(matrix, event, TCInfusionRandomSource.wrap(matrix.getLevel().getRandom()));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 50 | vent_dependency, runtime_storage | `if (matrix == null \|\| event == null \|\| !(matrix.getLevel() instanceof ServerLevel level)) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 59 | flux_mutation | `case EJECT_FLUX_DROP -> ejectFromPedestal(matrix, EjectEffect.FLUX_DROP, random);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 60 | flux_mutation | `case EJECT_FLUX_GOO_DROP -> ejectFromPedestal(matrix, EjectEffect.FLUX_GOO_DROP, random);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 61 | flux_mutation | `case EJECT_FLUX_DELETE -> ejectFromPedestal(matrix, EjectEffect.FLUX_DELETE, random);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 62 | flux_mutation | `case EJECT_FLUX_GOO_DELETE -> ejectFromPedestal(matrix, EjectEffect.FLUX_GOO_DELETE, random);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 142 | flux_mutation | `if (effect.pollutesAura()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 143 | aura_api_identity, flux_mutation, runtime_storage | `AuraHelper.polluteAura(level, pos, 5 + random.nextInt(5), true);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 155 | flux_mutation | `if (effect.placesFluxGoo()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 156 | flux_mutation, runtime_storage | `level.setBlock(pos.above(), TCBlocks.FLUX_GOO.get().defaultBlockState(), Block.UPDATE_ALL);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 157 | vent_dependency, runtime_storage | `level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.3F, 1.0F);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 171 | flux_mutation | `target.addEffect(new MobEffectInstance(TCMobEffects.FLUX_TAINT, 120, 0, false, true));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 223 | flux_mutation | `FLUX_GOO_DROP(false, false, false, true),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 224 | flux_mutation | `FLUX_DROP(false, true, false, false),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 225 | flux_mutation | `FLUX_GOO_DELETE(true, false, false, true),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 226 | flux_mutation | `FLUX_DELETE(true, true, false, false),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 230 | flux_mutation | `private final boolean pollutesAura;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 232 | flux_mutation | `private final boolean placesFluxGoo;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 234 | flux_mutation | `EjectEffect(boolean deletesItem, boolean pollutesAura, boolean explodes, boolean placesFluxGoo) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 236 | flux_mutation | `this.pollutesAura = pollutesAura;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 238 | flux_mutation | `this.placesFluxGoo = placesFluxGoo;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 245 | flux_mutation | `boolean pollutesAura() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 246 | flux_mutation | `return pollutesAura;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 253 | flux_mutation | `boolean placesFluxGoo() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 254 | flux_mutation | `return placesFluxGoo;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/items/tools/ItemThaumometer.java | 20 | runtime_storage | `import thaumcraft.common.world.aura.AuraChunk;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/items/tools/ItemThaumometer.java | 47 | vent_dependency, runtime_storage | `public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/items/tools/ItemThaumometer.java | 48 | vent_dependency, runtime_storage | `super.inventoryTick(stack, level, entity, slotId, isSelected);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/items/tools/ItemThaumometer.java | 56 | flux_mutation, runtime_storage | `AuraHandler.getAuraChunk(level, player.blockPosition()).ifPresent(chunk -> warnAboutFlux(player, chunk));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/items/tools/ItemThaumometer.java | 72 | flux_mutation, runtime_storage | `private static void warnAboutFlux(ServerPlayer player, AuraChunk chunk) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/items/tools/ItemThaumometer.java | 73 | flux_mutation, runtime_storage | `if ((chunk.getFlux() <= chunk.getVis() && chunk.getFlux() <= chunk.getBase() / 3.0F)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/items/tools/ItemThaumometer.java | 74 | flux_mutation | `\|\| TCResearchManager.isResearchComplete(TCPlayerKnowledgeStore.get(player), "FLUX")) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/items/tools/ItemThaumometer.java | 78 | flux_mutation | `TCResearchManager.startResearchWithPopup(player, "FLUX");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/items/tools/ItemThaumometer.java | 80 | flux_mutation | `Component.translatable("research.FLUX.warn").withStyle(net.minecraft.ChatFormatting.DARK_PURPLE),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 40 | flux_mutation | `import thaumcraft.common.blocks.world.taint.TCFluxGooBlock;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 150 | flux_mutation | `public static final Supplier<Block> FLUX_GOO = BLOCKS.register("flux_goo", () -> fluxGooBlock());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 228 | flux_mutation | `private static Block fluxGooBlock() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 229 | flux_mutation | `return new TCFluxGooBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 306 | smelter_pending_flux, runtime_storage | `.lightLevel(state -> state.getValue(TCSmelterBlock.ENABLED) ? 13 : 0);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCMobEffects.java | 8 | flux_mutation | `import thaumcraft.api.potions.PotionFluxTaint;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCMobEffects.java | 15 | flux_mutation | `public static final DeferredHolder<MobEffect, PotionFluxTaint> FLUX_TAINT =` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCMobEffects.java | 16 | flux_mutation | `MOB_EFFECTS.register("flux_taint", PotionFluxTaint::new);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCResearchData.java | 126 | flux_mutation | `put(categories, "AUROMANCY", "UNLOCKAUROMANCY", tags(Aspect.AURA, 20, Aspect.MAGIC, 20, Aspect.FLUX, 15, Aspect.CRYSTAL, 5, Aspect.COLD, 5, Aspect.AIR, 5),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCResearchData.java | 128 | flux_mutation | `put(categories, "ALCHEMY", "UNLOCKALCHEMY", tags(Aspect.ALCHEMY, 30, Aspect.FLUX, 10, Aspect.MAGIC, 10, Aspect.LIFE, 5, Aspect.AVERSION, 5, Aspect.DESIRE, 5, Aspect.WATER, 5),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/research/TCResearchData.java | 132 | flux_mutation | `put(categories, "INFUSION", "UNLOCKINFUSION", tags(Aspect.MAGIC, 30, Aspect.PROTECT, 10, Aspect.TOOL, 10, Aspect.FLUX, 5, Aspect.CRAFT, 5, Aspect.SOUL, 5, Aspect.EARTH, 3),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCCrucibleBlockEntity.java | 28 | aura_api_identity | `import thaumcraft.api.aura.AuraHelper;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCCrucibleBlockEntity.java | 243 | flux_mutation | `polluteSpillRemnants(totalAspects, aspects.getAmount(Aspect.FLUX));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCCrucibleBlockEntity.java | 311 | flux_mutation | `polluteAura(aspect == Aspect.FLUX ? 1.0F : 0.25F, true);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCCrucibleBlockEntity.java | 316 | flux_mutation | `private void polluteSpillRemnants(int totalAspects, int fluxAspects) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCCrucibleBlockEntity.java | 318 | flux_mutation | `polluteAura(totalAspects * 0.25F, true);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCCrucibleBlockEntity.java | 320 | flux_mutation | `if (fluxAspects > 0) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCCrucibleBlockEntity.java | 321 | flux_mutation | `polluteAura(fluxAspects * 0.75F, false);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCCrucibleBlockEntity.java | 368 | flux_mutation | `private void polluteAura(float amount, boolean showEffect) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCCrucibleBlockEntity.java | 370 | aura_api_identity, flux_mutation, runtime_storage | `AuraHelper.polluteAura(level, worldPosition, amount, showEffect);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCCrucibleBlockEntity.java | 396 | vent_dependency, runtime_storage | `level.blockEvent(worldPosition, getBlockState().getBlock(), eventId, parameter);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCStabilizerBlockEntity.java | 15 | aura_api_identity | `import thaumcraft.api.aura.AuraHelper;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCStabilizerBlockEntity.java | 32 | aura_api_identity, flux_mutation, runtime_storage | `AuraHelper.polluteAura(level, pos, 0.25F, true);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCStabilizerBlockEntity.java | 38 | flux_mutation | `// Flux-rift stabilization is activated when the rift entity subsystem is registered.` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 28 | flux_mutation, vent_dependency | `* intentionally leaves bellows discovery, efficiency/flux loss, vents and Alembic output for` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 48 | flux_mutation, smelter_pending_flux | `private int pendingFlux;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 101 | flux_mutation, smelter_pending_flux | `public int pendingFlux() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 102 | flux_mutation, smelter_pending_flux | `return pendingFlux;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 182 | smelter_pending_flux, runtime_storage | `public static void serverTick(Level level, BlockPos pos, BlockState state, TCSmelterBlockEntity smelter) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 267 | flux_mutation | `int fluxLoss = 0;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 274 | flux_mutation | `float threshold = isFluxAspect(aspect) ? efficiency * 0.66F : efficiency;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 277 | flux_mutation | `fluxLoss++;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 286 | flux_mutation, smelter_pending_flux | `pendingFlux += fluxLoss;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 290 | flux_mutation | `private static boolean isFluxAspect(Aspect aspect) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 291 | flux_mutation | `return aspect != null && "flux".equals(aspect.getTag());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 344 | smelter_pending_flux, runtime_storage | `level.setBlock(worldPosition, state.setValue(TCSmelterBlock.ENABLED, burning), Block.UPDATE_ALL);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 379 | flux_mutation, smelter_pending_flux | `tag.putInt("PendingFlux", pendingFlux);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 396 | flux_mutation, smelter_pending_flux | `pendingFlux = Math.max(0, tag.getInt("PendingFlux"));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 3 | runtime_storage | `import net.minecraft.world.level.ChunkPos;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 5 | aura_api_identity, runtime_storage | `public final class AuraChunk {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 9 | runtime_storage | `private final int chunkX;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 10 | runtime_storage | `private final int chunkZ;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 13 | flux_mutation | `private float flux;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 15 | flux_mutation, runtime_storage | `AuraChunk(int chunkX, int chunkZ, int base, float vis, float flux) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 16 | runtime_storage | `this.chunkX = chunkX;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 17 | runtime_storage | `this.chunkZ = chunkZ;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 20 | flux_mutation | `setFlux(flux);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 23 | runtime_storage | `public int getChunkX() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 24 | runtime_storage | `return chunkX;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 27 | runtime_storage | `public int getChunkZ() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 28 | runtime_storage | `return chunkZ;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 32 | runtime_storage | `return ChunkPos.asLong(chunkX, chunkZ);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 35 | runtime_storage | `public ChunkPos getChunkPos() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 36 | runtime_storage | `return new ChunkPos(chunkX, chunkZ);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 55 | flux_mutation | `public float getFlux() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 56 | flux_mutation | `return flux;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 59 | flux_mutation | `void setFlux(float flux) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 60 | flux_mutation | `this.flux = clampValue(flux);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 63 | runtime_storage | `AuraChunk copy() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraChunk.java | 64 | flux_mutation, runtime_storage | `return new AuraChunk(chunkX, chunkZ, base, vis, flux);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 11 | runtime_storage | `import net.minecraft.world.level.ChunkPos;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 14 | aura_api_identity | `public final class AuraHandler {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 15 | runtime_storage | `public static final int AURA_CEILING = AuraChunk.BASE_CEILING;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 30 | runtime_storage | `public static Optional<AuraChunk> getAuraChunk(Level level, BlockPos pos) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 34 | runtime_storage | `return getAuraChunk(serverLevel, new ChunkPos(pos));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 37 | runtime_storage | `public static Optional<AuraChunk> getAuraChunk(ServerLevel level, ChunkPos pos) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 38 | runtime_storage | `return TCAuraSavedData.get(level)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 39 | runtime_storage | `.getChunk(pos.x, pos.z)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 40 | runtime_storage | `.map(AuraChunk::copy);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 43 | runtime_storage | `public static AuraChunk seedAuraChunk(ServerLevel level, BlockPos pos, int base) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 44 | runtime_storage | `ChunkPos chunkPos = new ChunkPos(pos);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 46 | runtime_storage | `return TCAuraSavedData.get(level)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 47 | runtime_storage | `.setChunk(chunkPos.x, chunkPos.z, clampedBase, clampedBase, 0.0F)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 51 | runtime_storage | `public static AuraChunk ensureAuraChunk(ServerLevel level, ChunkPos chunkPos) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 52 | runtime_storage | `TCAuraSavedData data = TCAuraSavedData.get(level);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 53 | runtime_storage | `data.markLoaded(chunkPos.x, chunkPos.z);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 54 | runtime_storage | `Optional<AuraChunk> existing = data.getChunk(chunkPos.x, chunkPos.z);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/world/aura/AuraHandler.java | 58 | runtime_storage | `AuraChunk generated = generateAuraChunk(level, chunkPos);` |

## Smelter pending flux evidence, first 120 rows

| File | Line | Category | Evidence |
|---|---:|---|---|
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 19 | smelter_pending_flux | `import thaumcraft.common.tiles.essentia.TCSmelterBlockEntity;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 26 | smelter_pending_flux | `public class TCSmelterBlock extends Block implements EntityBlock {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 30 | smelter_pending_flux | `public TCSmelterBlock(BlockBehaviour.Properties properties) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 41 | smelter_pending_flux | `return new TCSmelterBlockEntity(pos, state);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 48 | smelter_pending_flux, runtime_storage | `if (level.isClientSide \|\| type != TCBlockEntities.SMELTER_BASIC.get()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 52 | smelter_pending_flux | `TCSmelterBlockEntity.serverTick(` |
| 05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java | 56 | smelter_pending_flux | `(TCSmelterBlockEntity) blockEntity` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 8 | smelter_pending_flux | `public enum TCLegacySmelterEndpoint {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 9 | smelter_pending_flux | `THAUMIUM("smelter_thaumium", TCEssentiaTubeMode.SMELTER_THAUMIUM, 64),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 10 | smelter_pending_flux | `VOID("smelter_void", TCEssentiaTubeMode.SMELTER_VOID, 64);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 16 | smelter_pending_flux | `TCLegacySmelterEndpoint(String catalogId, TCEssentiaTubeMode mode, int storageCapacity) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 34 | smelter_pending_flux | `public static Optional<TCLegacySmelterEndpoint> fromCatalogId(String catalogId) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpoint.java | 37 | smelter_pending_flux | `for (TCLegacySmelterEndpoint endpoint : values()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpointBlock.java | 8 | smelter_pending_flux | `import thaumcraft.common.blocks.essentia.TCSmelterBlock;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpointBlock.java | 13 | smelter_pending_flux | `public class TCLegacySmelterEndpointBlock extends TCSmelterBlock implements EntityBlock {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpointBlock.java | 14 | smelter_pending_flux | `private final TCLegacySmelterEndpoint endpoint;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpointBlock.java | 16 | smelter_pending_flux | `public TCLegacySmelterEndpointBlock(TCLegacySmelterEndpoint endpoint, BlockBehaviour.Properties properties) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpointBlock.java | 21 | smelter_pending_flux | `public TCLegacySmelterEndpoint endpoint() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/block/TCLegacySmelterEndpointBlock.java | 28 | smelter_pending_flux | `return TCBlockEntities.createSmelterEndpointBlockEntity(endpoint, pos, state);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 6 | smelter_pending_flux | `import thaumcraft.common.essentia.transport.block.TCLegacySmelterEndpoint;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 8 | smelter_pending_flux | `public class TCLegacySmelterEndpointBlockEntity extends TCAbstractEssentiaTransportBlockEntity {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 9 | smelter_pending_flux | `private final TCLegacySmelterEndpoint endpoint;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 11 | smelter_pending_flux | `public TCLegacySmelterEndpointBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, TCLegacySmelterEndpoint endpoint) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacySmelterEndpointBlockEntity.java | 16 | smelter_pending_flux | `public TCLegacySmelterEndpoint endpoint() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 7 | smelter_pending_flux | `import thaumcraft.common.essentia.transport.block.TCLegacySmelterEndpointBlock;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 9 | smelter_pending_flux | `import thaumcraft.common.essentia.transport.block.TCLegacySmelterEndpoint;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 28 | smelter_pending_flux | `import thaumcraft.common.blocks.essentia.TCSmelterBlock;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 125 | smelter_pending_flux | `public static final Supplier<Block> SMELTER_BASIC = BLOCKS.register("smelter_basic", () -> smelterBlock());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 135 | smelter_pending_flux | `public static final Supplier<Block> SMELTER_THAUMIUM = BLOCKS.register("smelter_thaumium", () -> smelterEndpointBlock(TCLegacySmelterEndpoint.THAUMIUM));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 136 | smelter_pending_flux | `public static final Supplier<Block> SMELTER_VOID = BLOCKS.register("smelter_void", () -> smelterEndpointBlock(TCLegacySmelterEndpoint.VOID));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 284 | smelter_pending_flux | `private static Block smelterBlock() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 285 | smelter_pending_flux | `return new TCSmelterBlock(smelterProperties());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 302 | smelter_pending_flux | `private static BlockBehaviour.Properties smelterProperties() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 306 | smelter_pending_flux, runtime_storage | `.lightLevel(state -> state.getValue(TCSmelterBlock.ENABLED) ? 13 : 0);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 422 | smelter_pending_flux | `private static Block smelterEndpointBlock(TCLegacySmelterEndpoint endpoint) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java | 423 | smelter_pending_flux | `return new TCLegacySmelterEndpointBlock(endpoint, smelterProperties());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 21 | smelter_pending_flux | `import thaumcraft.common.blocks.essentia.TCSmelterBlock;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 25 | smelter_pending_flux | `* First server-owned smelter machine model.` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 31 | smelter_pending_flux | `public final class TCSmelterBlockEntity extends BlockEntity {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 48 | flux_mutation, smelter_pending_flux | `private int pendingFlux;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 50 | smelter_pending_flux | `public TCSmelterBlockEntity(BlockPos pos, BlockState state) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 51 | smelter_pending_flux | `super(TCBlockEntities.SMELTER_BASIC.get(), pos, state);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 101 | flux_mutation, smelter_pending_flux | `public int pendingFlux() {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 102 | flux_mutation, smelter_pending_flux | `return pendingFlux;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 160 | smelter_pending_flux | `public static float efficiencyForType(SmelterType type) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 168 | smelter_pending_flux | `public static int speedForType(SmelterType type) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 169 | smelter_pending_flux | `return type == SmelterType.THAUMIUM ? 10 : 15;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 176 | smelter_pending_flux | `public enum SmelterType {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 182 | smelter_pending_flux, runtime_storage | `public static void serverTick(Level level, BlockPos pos, BlockState state, TCSmelterBlockEntity smelter) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 186 | smelter_pending_flux | `smelter.tickServer();` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 266 | smelter_pending_flux | `float efficiency = efficiencyForType(SmelterType.BASIC);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 286 | flux_mutation, smelter_pending_flux | `pendingFlux += fluxLoss;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 316 | smelter_pending_flux | `int speed = speedForType(SmelterType.BASIC);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 339 | smelter_pending_flux | `if (!state.hasProperty(TCSmelterBlock.ENABLED)) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 343 | smelter_pending_flux | `if (state.getValue(TCSmelterBlock.ENABLED) != burning) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 344 | smelter_pending_flux, runtime_storage | `level.setBlock(worldPosition, state.setValue(TCSmelterBlock.ENABLED, burning), Block.UPDATE_ALL);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 379 | flux_mutation, smelter_pending_flux | `tag.putInt("PendingFlux", pendingFlux);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java | 396 | flux_mutation, smelter_pending_flux | `pendingFlux = Math.max(0, tag.getInt("PendingFlux"));` |

## Vent dependency evidence, first 120 rows

| File | Line | Category | Evidence |
|---|---:|---|---|
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCAspectTooltipEvents.java | 6 | vent_dependency | `import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCAspectTooltipEvents.java | 8 | vent_dependency | `import net.neoforged.bus.api.SubscribeEvent;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCAspectTooltipEvents.java | 9 | vent_dependency | `import net.neoforged.fml.common.EventBusSubscriber;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCAspectTooltipEvents.java | 10 | vent_dependency | `import net.neoforged.neoforge.client.event.RenderTooltipEvent;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCAspectTooltipEvents.java | 15 | vent_dependency | `@EventBusSubscriber(modid = Thaumcraft.MODID, value = Dist.CLIENT)` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCAspectTooltipEvents.java | 16 | vent_dependency | `public final class TCAspectTooltipEvents {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCAspectTooltipEvents.java | 17 | vent_dependency | `private TCAspectTooltipEvents() {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCAspectTooltipEvents.java | 20 | vent_dependency | `@SubscribeEvent` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCAspectTooltipEvents.java | 21 | vent_dependency | `public static void gatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCAspectTooltipEvents.java | 26 | vent_dependency | `\|\| event.getItemStack().isEmpty()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCAspectTooltipEvents.java | 30 | vent_dependency | `AspectList aspects = AspectHelper.getObjectAspects(event.getItemStack());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/gui/TCAspectTooltipEvents.java | 33 | vent_dependency | `event.getTooltipElements().add(Either.right(component));` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 5 | vent_dependency | `import net.neoforged.bus.api.SubscribeEvent;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 6 | vent_dependency | `import net.neoforged.fml.common.EventBusSubscriber;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 7 | vent_dependency | `import net.neoforged.neoforge.client.event.ClientTickEvent;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 8 | vent_dependency, runtime_storage | `import net.neoforged.neoforge.client.event.RenderLevelStageEvent;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 19 | vent_dependency | `@EventBusSubscriber(modid = Thaumcraft.MODID, value = Dist.CLIENT)` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 20 | vent_dependency | `public final class TCClientEvents {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 25 | vent_dependency | `private TCClientEvents() {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 28 | vent_dependency | `@SubscribeEvent` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 29 | vent_dependency | `public static void onClientTick(ClientTickEvent.Pre event) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 46 | vent_dependency | `@SubscribeEvent` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 47 | vent_dependency, runtime_storage | `public static void onRenderLevelStage(RenderLevelStageEvent event) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 48 | vent_dependency, runtime_storage | `if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 50 | vent_dependency | `event.getCamera(),` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 51 | vent_dependency | `event.getPartialTick().getGameTimeDeltaPartialTick(false)` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 54 | vent_dependency | `event.getCamera(),` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 55 | vent_dependency | `event.getPartialTick().getGameTimeDeltaPartialTick(false)` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 60 | vent_dependency, runtime_storage | `if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java | 61 | vent_dependency | `TCThaumometerClientEffects.renderAspectOverlay(event);` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 11 | vent_dependency | `import net.neoforged.bus.api.SubscribeEvent;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 12 | vent_dependency | `import net.neoforged.fml.common.EventBusSubscriber;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 13 | vent_dependency | `import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 22 | vent_dependency | `@EventBusSubscriber(modid = Thaumcraft.MODID, value = Dist.CLIENT)` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 54 | vent_dependency | `@SubscribeEvent` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 55 | vent_dependency | `public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 68 | vent_dependency | `event.register(leafColor,` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 72 | vent_dependency, runtime_storage | `event.register((state, level, pos, tintIndex) -> crystalColor(state.getBlock()),` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 81 | vent_dependency, runtime_storage | `event.register((state, level, pos, tintIndex) -> candleColor(state.getBlock(), tintIndex),` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 100 | vent_dependency | `@SubscribeEvent` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 101 | vent_dependency | `public static void registerItemColors(RegisterColorHandlersEvent.Item event) {` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 105 | vent_dependency | `event.register(greatwoodLeavesItem, TCBlocks.LEAVES_GREATWOOD.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 106 | vent_dependency | `event.register(silverwoodLeavesItem, TCBlocks.LEAVES_SILVERWOOD.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 108 | vent_dependency | `event.register((stack, tintIndex) -> AIR, TCBlocks.CRYSTAL_AER.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 109 | vent_dependency | `event.register((stack, tintIndex) -> FIRE, TCBlocks.CRYSTAL_IGNIS.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 110 | vent_dependency | `event.register((stack, tintIndex) -> WATER, TCBlocks.CRYSTAL_AQUA.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 111 | vent_dependency | `event.register((stack, tintIndex) -> EARTH, TCBlocks.CRYSTAL_TERRA.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 112 | vent_dependency | `event.register((stack, tintIndex) -> ORDER, TCBlocks.CRYSTAL_ORDO.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 113 | vent_dependency | `event.register((stack, tintIndex) -> ENTROPY, TCBlocks.CRYSTAL_PERDITIO.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 114 | flux_mutation, vent_dependency | `event.register((stack, tintIndex) -> FLUX, TCBlocks.CRYSTAL_VITIUM.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 115 | vent_dependency | `event.register((stack, tintIndex) -> 0xFF1D1D21, TCBlocks.NITOR_BLACK.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 116 | vent_dependency | `event.register((stack, tintIndex) -> 0xFF3C44AA, TCBlocks.NITOR_BLUE.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 117 | vent_dependency | `event.register((stack, tintIndex) -> 0xFF835432, TCBlocks.NITOR_BROWN.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 118 | vent_dependency | `event.register((stack, tintIndex) -> 0xFF169C9C, TCBlocks.NITOR_CYAN.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 119 | vent_dependency | `event.register((stack, tintIndex) -> 0xFF474F52, TCBlocks.NITOR_GRAY.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 120 | vent_dependency | `event.register((stack, tintIndex) -> 0xFF5E7C16, TCBlocks.NITOR_GREEN.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 121 | vent_dependency | `event.register((stack, tintIndex) -> 0xFFFFFF55, TCBlocks.NITOR_YELLOW.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 122 | vent_dependency | `event.register((stack, tintIndex) -> 0xFF3AB3DA, TCBlocks.NITOR_LIGHTBLUE.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 123 | vent_dependency | `event.register((stack, tintIndex) -> 0xFF80C71F, TCBlocks.NITOR_LIME.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 124 | vent_dependency | `event.register((stack, tintIndex) -> 0xFFC74EBD, TCBlocks.NITOR_MAGENTA.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 125 | vent_dependency | `event.register((stack, tintIndex) -> 0xFFF9801D, TCBlocks.NITOR_ORANGE.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 126 | vent_dependency | `event.register((stack, tintIndex) -> 0xFFF38BAA, TCBlocks.NITOR_PINK.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 127 | vent_dependency | `event.register((stack, tintIndex) -> 0xFF8932B8, TCBlocks.NITOR_PURPLE.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 128 | vent_dependency | `event.register((stack, tintIndex) -> 0xFFB02E26, TCBlocks.NITOR_RED.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 129 | vent_dependency | `event.register((stack, tintIndex) -> 0xFF9D9D97, TCBlocks.NITOR_SILVER.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 130 | vent_dependency | `event.register((stack, tintIndex) -> 0xFFF9FFFE, TCBlocks.NITOR_WHITE.get());` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 131 | vent_dependency | `event.register((stack, tintIndex) -> candleColor(Block.byItem(stack.getItem()), tintIndex),` |
| 05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java | 152 | vent_dependency | `event.register(aspectVariantColor, item);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/crucible/TCCrucibleBehaviorAudit.java | 167 | vent_dependency | `"special_crucible_item_marker_prevents_reabsorption",` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 247 | vent_dependency | `addInstabilityEventMappingChecks(checks);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 314 | vent_dependency | `"stability_event_roll_is_inclusive_at_absolute_value",` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 315 | vent_dependency | `TCInfusionStability.shouldTriggerEvent(-10.0F, 10)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 316 | vent_dependency | `&& !TCInfusionStability.shouldTriggerEvent(-10.0F, 11)` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 317 | vent_dependency | `&& !TCInfusionStability.shouldTriggerEvent(0.0F, 0),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 322 | vent_dependency | `private static void addInstabilityEventMappingChecks(ArrayList<Check> checks) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 323 | vent_dependency | `int[] counts = new int[TCInfusionInstabilityEvent.values().length];` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 326 | vent_dependency | `TCInfusionInstabilityEvent event = TCInfusionInstabilityEvent.fromLegacyRoll(roll);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 327 | vent_dependency | `counts[event.ordinal()]++;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 328 | vent_dependency | `if (event.isSupportedByCurrentPort()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 334 | vent_dependency | `counts[TCInfusionInstabilityEvent.EJECT_ITEM_DROP.ordinal()] == 4` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 335 | vent_dependency | `&& counts[TCInfusionInstabilityEvent.WARP.ordinal()] == 3` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 336 | vent_dependency | `&& counts[TCInfusionInstabilityEvent.ZAP_ONE.ordinal()] == 3` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 337 | vent_dependency | `&& counts[TCInfusionInstabilityEvent.ZAP_ALL.ordinal()] == 2` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 338 | flux_mutation, vent_dependency | `&& counts[TCInfusionInstabilityEvent.EJECT_FLUX_GOO_DROP.ordinal()] == 2` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 339 | flux_mutation, vent_dependency | `&& counts[TCInfusionInstabilityEvent.EJECT_FLUX_DROP.ordinal()] == 2` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 340 | flux_mutation, vent_dependency | `&& counts[TCInfusionInstabilityEvent.EJECT_FLUX_GOO_DELETE.ordinal()] == 1` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 341 | flux_mutation, vent_dependency | `&& counts[TCInfusionInstabilityEvent.EJECT_FLUX_DELETE.ordinal()] == 1` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 342 | vent_dependency | `&& counts[TCInfusionInstabilityEvent.HARM_ONE.ordinal()] == 2` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 343 | vent_dependency | `&& counts[TCInfusionInstabilityEvent.EJECT_EXPLOSIVE.ordinal()] == 2` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 344 | vent_dependency | `&& counts[TCInfusionInstabilityEvent.HARM_ALL.ordinal()] == 1` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 345 | vent_dependency | `&& counts[TCInfusionInstabilityEvent.MATRIX_EXPLOSION.ordinal()] == 1,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 351 | flux_mutation, vent_dependency | `&& TCInfusionInstabilityEvent.EJECT_FLUX_GOO_DROP.isSupportedByCurrentPort()` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 352 | flux_mutation, vent_dependency | `&& TCInfusionInstabilityEvent.EJECT_FLUX_GOO_DELETE.isSupportedByCurrentPort()` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 353 | vent_dependency | `&& TCInfusionInstabilityEvent.HARM_ONE.isSupportedByCurrentPort()` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 354 | vent_dependency | `&& TCInfusionInstabilityEvent.HARM_ALL.isSupportedByCurrentPort(),` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 385 | vent_dependency | `"runtime_event_recovery_matches_legacy_post_clamp_order",` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 393 | flux_mutation, vent_dependency | `TCInfusionInstabilityEvent.EJECT_FLUX_GOO_DROP,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 397 | flux_mutation, vent_dependency | `"runtime_flux_goo_drop_event_matches_legacy_effect",` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 408 | flux_mutation, vent_dependency | `TCInfusionInstabilityEvent.EJECT_FLUX_DELETE,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 413 | flux_mutation, vent_dependency | `"runtime_supported_flux_delete_event_matches_legacy_effect",` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 464 | vent_dependency | `TCInfusionInstabilityEvent.EJECT_ITEM_DROP,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 1325 | vent_dependency | `"runtime_legacy_cycle_catalyst_change_runs_event_then_aborts_without_component_loss",` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 1328 | vent_dependency | `&& TCInfusionInstabilityEvent.WARP.name().equals(matrix.lastInstabilityEvent())` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionBehaviorAudit.java | 1331 | vent_dependency | `"status=" + aborted.status() + ", reason=" + aborted.reason() + ", event=" + matrix.lastInstabilityEvent()` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityEvent.java | 4 | vent_dependency | `public enum TCInfusionInstabilityEvent {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityEvent.java | 18 | vent_dependency | `public static TCInfusionInstabilityEvent fromLegacyRoll(int roll) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 11 | vent_dependency | `import net.minecraft.sounds.SoundEvents;` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 28 | vent_dependency | `/** Reviewed server effects for legacy infusion instability events. */` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 37 | vent_dependency | `TCInfusionInstabilityEvent event` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 40 | vent_dependency | `return ExecutionResult.blocked(event, "missing_server_matrix");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 42 | vent_dependency, runtime_storage | `return execute(matrix, event, TCInfusionRandomSource.wrap(matrix.getLevel().getRandom()));` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 47 | vent_dependency | `TCInfusionInstabilityEvent event,` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 50 | vent_dependency, runtime_storage | `if (matrix == null \|\| event == null \|\| !(matrix.getLevel() instanceof ServerLevel level)) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 51 | vent_dependency | `return ExecutionResult.blocked(event, "missing_server_matrix");` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 53 | vent_dependency | `if (!event.isSupportedByCurrentPort()) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 54 | vent_dependency | `return ExecutionResult.blocked(event, "missing_dependency:" + event.missingDependency());` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 57 | vent_dependency | `BlockPos target = switch (event) {` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 97 | vent_dependency | `return ExecutionResult.executed(event, target);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 157 | vent_dependency, runtime_storage | `level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.3F, 1.0F);` |
| 05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionInstabilityExecutor.java | 260 | vent_dependency | `TCInfusionInstabilityEvent event,` |

## Porting conclusion

- If the aura table exposes a stable direct flux/pollution method, use it in a follow-up smelter pending-flux drain slice.
- If the aura table does not expose such a method, add a minimal aura mutation boundary before wiring smelter pollution.

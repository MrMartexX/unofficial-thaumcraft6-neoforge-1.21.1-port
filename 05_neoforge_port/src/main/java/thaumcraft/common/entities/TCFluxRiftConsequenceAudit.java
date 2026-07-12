package thaumcraft.common.entities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.aspects.TCEntityAspectAssignments;
import thaumcraft.common.blocks.world.taint.TCTaintHelper;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCMobEffects;
import thaumcraft.common.world.aura.AuraHandler;

public final class TCFluxRiftConsequenceAudit {
    public static final String ENABLE_PROPERTY = "tc.fluxRiftConsequenceAudit";
    public static final String OUTPUT_PROPERTY = "tc.fluxRiftConsequenceAuditPath";

    private TCFluxRiftConsequenceAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        if (output.getParent() != null) {
            Files.createDirectories(output.getParent());
        }
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Flux Rift Consequence Audit");
        lines.add("");
        lines.add("Runtime checks for the row-11 Flux Rift event/consequence slice after the initial entity");
        lines.add("foundation. This covers the TC6 weighted event table, Wisp dependency foundation, Prime");
        lines.add("Taint Seed event, infectious vis exhaustion event and collapse effects. Focus-cloud execution");
        lines.add("is explicitly left to the paused focus/projectile owner row.");
        lines.add("");
        lines.add("## Summary");
        lines.add("");
        lines.add("| Check | Result |");
        lines.add("|---|---:|");
        lines.add("| Passed | " + report.passed() + " |");
        lines.add("| Failed | " + report.failed() + " |");
        lines.add("");
        lines.add("## Checks");
        lines.add("");
        lines.add("| Name | Result | Notes |");
        lines.add("|---|---|---|");
        for (Check check : report.checks()) {
            lines.add("| " + check.name() + " | " + (check.passed() ? "PASS" : "FAIL")
                    + " | " + check.notes().replace("|", "\\|") + " |");
        }
        lines.add("");
        lines.add("## Boundary");
        lines.add("");
        lines.add("- Implemented: legacy rift weighted event table `50/10/20/20/1`, near-taint filter, Wisp spawn event, Prime Taint Seed boost/pollution event, infectious vis exhaustion event, collapse aura/explosion/drop/effect path and dynamic Wisp aspect assignment.");
        lines.add("- Implemented dependency: `thaumcraft:wisp` is registered with TC6 tracking values and minimal server state needed by rifts; full Wisp AI/model/particles remain entity/render row work.");
        lines.add("- Deferred by owner, not guessed: event 3 still requires real `EntityFocusCloud` / focus cloud execution before it can be made player-facing.");
        Files.write(output, lines, StandardCharsets.UTF_8);
        return report;
    }

    public static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        BlockPos origin = new BlockPos(level.getSharedSpawnPos().getX() + 260, level.getMinBuildHeight() + 40, level.getSharedSpawnPos().getZ() + 260);
        Difficulty originalDifficulty = level.getDifficulty();
        boolean originalMobGriefing = level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        cleanup(level, origin);
        prepareDarkUndergroundChamber(level, origin);
        TCTaintHelper.clearForValidation(level);
        try {
            level.getServer().setDifficulty(Difficulty.NORMAL, true);
            level.getGameRules().getRule(GameRules.RULE_MOBGRIEFING).set(false, level.getServer());
            AuraHandler.seedAuraChunk(level, origin, 100);
            addRegistrationAndDataChecks(checks);
            addEventChecks(level, origin, checks);
            addCollapseChecks(level, origin.offset(12, 0, 0), checks);
        } finally {
            level.getServer().setDifficulty(originalDifficulty, true);
            level.getGameRules().getRule(GameRules.RULE_MOBGRIEFING).set(originalMobGriefing, level.getServer());
            cleanup(level, origin);
            TCTaintHelper.clearForValidation(level);
        }
        return new Report(List.copyOf(checks));
    }

    private static void addRegistrationAndDataChecks(ArrayList<Check> checks) {
        checks.add(check("infectious_vis_exhaust_effect_registered",
                BuiltInRegistries.MOB_EFFECT.getKey(TCMobEffects.INFECTIOUS_VIS_EXHAUST.get()).equals(id("infectious_vis_exhaust")),
                "effect=" + BuiltInRegistries.MOB_EFFECT.getKey(TCMobEffects.INFECTIOUS_VIS_EXHAUST.get())));
        checks.add(check("wisp_registered_with_legacy_tracking",
                entityId(TCEntityTypes.WISP.get()).equals(id("wisp"))
                        && legacySpecRegistered("Wisp", "wisp", 64, 3, false)
                        && typeShape(TCEntityTypes.WISP.get(), MobCategory.MONSTER, 0.9F, 0.9F, 64, 3, false),
                "entity=" + entityId(TCEntityTypes.WISP.get())));
        checks.add(check("rift_event_table_matches_legacy_weights_costs",
                TCFluxRiftEntity.eventTableForValidation().size() == 5
                        && event(0, 50, 5, true, "wisp")
                        && event(1, 10, 0, false, "taint_seed_prime")
                        && event(2, 20, 10, true, "infectious_vis_exhaust")
                        && event(3, 20, 10, true, "focus_flux_cloud")
                        && event(4, 1, 0, true, "collapse"),
                "events=" + TCFluxRiftEntity.eventTableForValidation()));
    }

    private static void addEventChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        TCWispEntity aspectWisp = new TCWispEntity(level, origin.getX() + 0.5D, origin.getY(), origin.getZ() + 0.5D);
        aspectWisp.setWispType(Aspect.FLUX.getTag());
        checks.add(check("wisp_dynamic_type_aspects_match_legacy",
                hasAspectList(TCEntityAspectAssignments.getEntityAspects(aspectWisp), Aspect.FLUX, 5, Aspect.AURA, 5, Aspect.FLIGHT, 5),
                "type=" + aspectWisp.getWispType()));

        TCFluxRiftEntity wispRift = rift(level, origin, 12, -30.0F);
        int wispBefore = count(level, TCWispEntity.class, origin, 16.0D);
        TCFluxRiftEntity.FluxEventResult wispResult = wispRift.executeRiftEventForValidation(0);
        int wispAfter = count(level, TCWispEntity.class, origin, 48.0D);
        checks.add(check("rift_event_0_spawns_real_wisp_and_adds_stability_cost",
                wispResult.applied()
                        && wispAfter == wispBefore + 1
                        && close(wispRift.getRiftStability(), -25.0F),
                "result=" + wispResult.result() + ", before=" + wispBefore + ", after=" + wispAfter + ", stability=" + wispRift.getRiftStability()));

        TCFluxRiftEntity seedRift = rift(level, origin.offset(4, 0, 0), 14, -40.0F);
        AuraHandler.seedAuraChunk(level, seedRift.blockPosition(), 100);
        float fluxBefore = AuraHelper.getFlux(level, seedRift.blockPosition());
        int seedBefore = count(level, TCTaintSeedEntity.class, seedRift.blockPosition(), 16.0D);
        TCFluxRiftEntity.FluxEventResult seedResult = seedRift.executeRiftEventForValidation(1);
        int seedAfter = count(level, TCTaintSeedEntity.class, seedRift.blockPosition(), 48.0D);
        float fluxAfter = AuraHelper.getFlux(level, seedRift.blockPosition());
        TCTaintSeedEntity prime = level.getEntitiesOfClass(TCTaintSeedEntity.class, new AABB(seedRift.blockPosition()).inflate(16.0D))
                .stream()
                .filter(seed -> seed.getType() == TCEntityTypes.TAINT_SEED_PRIME.get())
                .findFirst()
                .orElse(null);
        checks.add(check("rift_event_1_spawns_prime_seed_boost_pollutes_and_discards_rift",
                seedResult.applied()
                        && !seedRift.isAlive()
                        && seedAfter == seedBefore + 1
                        && prime != null
                        && prime.getBoost() == 14
                        && close(fluxAfter - fluxBefore, 7.0F),
                "result=" + seedResult.result() + ", seeds=" + seedBefore + "->" + seedAfter
                        + ", boost=" + (prime == null ? -1 : prime.getBoost()) + ", fluxDelta=" + (fluxAfter - fluxBefore)));

        Zombie target = EntityType.ZOMBIE.create(level);
        TCFluxRiftEntity exhaustRift = rift(level, origin.offset(8, 0, 0), 10, -50.0F);
        if (target != null) {
            target.moveTo(exhaustRift.getX(), exhaustRift.getY(), exhaustRift.getZ());
            level.addFreshEntity(target);
        }
        TCFluxRiftEntity.FluxEventResult exhaustResult = exhaustRift.executeRiftEventForValidation(2);
        checks.add(check("rift_event_2_applies_infectious_vis_exhaust_and_cost",
                target != null
                        && exhaustResult.applied()
                        && target.hasEffect(TCMobEffects.INFECTIOUS_VIS_EXHAUST)
                        && close(exhaustRift.getRiftStability(), -40.0F),
                "result=" + exhaustResult.result()
                        + ", hasEffect=" + (target != null && target.hasEffect(TCMobEffects.INFECTIOUS_VIS_EXHAUST))
                        + ", stability=" + exhaustRift.getRiftStability()));

        TCFluxRiftEntity focusRift = rift(level, origin.offset(10, 0, 0), 10, -50.0F);
        TCFluxRiftEntity.FluxEventResult focusResult = focusRift.executeRiftEventForValidation(3);
        checks.add(check("rift_event_3_focus_cloud_is_explicitly_deferred_to_focus_owner",
                focusResult.deferredOwner()
                        && !focusResult.applied()
                        && close(focusRift.getRiftStability(), -50.0F),
                "result=" + focusResult.result()));

        TCFluxRiftEntity collapseRift = rift(level, origin.offset(11, 0, 0), 10, -5.0F);
        TCFluxRiftEntity.FluxEventResult collapseResult = collapseRift.executeRiftEventForValidation(4);
        checks.add(check("rift_event_4_sets_collapse_without_stability_cost",
                collapseResult.applied()
                        && collapseRift.isCollapsing()
                        && close(collapseRift.getRiftStability(), -5.0F),
                "collapse=" + collapseRift.isCollapsing() + ", stability=" + collapseRift.getRiftStability()));
    }

    private static void addCollapseChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        Zombie target = EntityType.ZOMBIE.create(level);
        TCFluxRiftEntity collapseRift = rift(level, origin, 16, -30.0F);
        collapseRift.setCollapse(true);
        if (target != null) {
            target.moveTo(collapseRift.getX(), collapseRift.getY(), collapseRift.getZ());
            level.addFreshEntity(target);
        }
        collapseRift.completeCollapseForValidation();
        checks.add(check("rift_complete_collapse_applies_unstable_weakness_effect_and_discards",
                target != null
                        && !collapseRift.isAlive()
                        && target.hasEffect(MobEffects.WEAKNESS),
                "alive=" + collapseRift.isAlive() + ", weakness=" + (target != null && target.hasEffect(MobEffects.WEAKNESS))));

        Zombie fluxTarget = EntityType.ZOMBIE.create(level);
        TCFluxRiftEntity veryUnstableRift = rift(level, origin.offset(3, 0, 0), 16, -80.0F);
        veryUnstableRift.setCollapse(true);
        if (fluxTarget != null) {
            fluxTarget.moveTo(veryUnstableRift.getX(), veryUnstableRift.getY(), veryUnstableRift.getZ());
            level.addFreshEntity(fluxTarget);
        }
        veryUnstableRift.completeCollapseForValidation();
        checks.add(check("rift_complete_collapse_applies_very_unstable_flux_taint_fallthrough",
                fluxTarget != null
                        && fluxTarget.hasEffect(TCMobEffects.FLUX_TAINT)
                        && fluxTarget.hasEffect(MobEffects.WEAKNESS),
                "fluxTaint=" + (fluxTarget != null && fluxTarget.hasEffect(TCMobEffects.FLUX_TAINT))
                        + ", weakness=" + (fluxTarget != null && fluxTarget.hasEffect(MobEffects.WEAKNESS))));
    }

    private static TCFluxRiftEntity rift(ServerLevel level, BlockPos pos, int size, float stability) {
        TCFluxRiftEntity rift = new TCFluxRiftEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
        rift.setRiftSeed(12345 + pos.getX());
        rift.setRiftSize(size);
        rift.setRiftStability(stability);
        level.addFreshEntity(rift);
        return rift;
    }

    private static boolean event(int event, int weight, int cost, boolean nearTaintAllowed, String owner) {
        return TCFluxRiftEntity.eventTableForValidation().stream()
                .anyMatch(entry -> entry.event() == event
                        && entry.weight() == weight
                        && entry.cost() == cost
                        && entry.nearTaintAllowed() == nearTaintAllowed
                        && entry.owner().equals(owner));
    }

    private static boolean hasAspectList(AspectList list, Aspect first, int firstAmount, Aspect second, int secondAmount, Aspect third, int thirdAmount) {
        return list != null
                && list.getAspects().length == 3
                && list.getAspects()[0] == first
                && list.getAmount(first) == firstAmount
                && list.getAspects()[1] == second
                && list.getAmount(second) == secondAmount
                && list.getAspects()[2] == third
                && list.getAmount(third) == thirdAmount;
    }

    private static int count(ServerLevel level, Class<? extends Entity> type, BlockPos origin, double radius) {
        return level.getEntitiesOfClass(type, new AABB(origin).inflate(radius)).size();
    }

    private static void cleanup(ServerLevel level, BlockPos origin) {
        level.getEntities(null, new AABB(origin).inflate(80.0D)).forEach(Entity::discard);
    }

    private static void prepareDarkUndergroundChamber(ServerLevel level, BlockPos origin) {
        BlockPos.betweenClosed(origin.offset(-32, -20, -32), origin.offset(64, 20, 32)).forEach(pos ->
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
    }

    private static boolean legacySpecRegistered(String legacyId, String modernId, int tracking, int update, boolean velocity) {
        return TCEntityTypes.byLegacyId(legacyId)
                .filter(spec -> modernId.equals(spec.modernId()))
                .filter(spec -> spec.trackingRange() == tracking)
                .filter(spec -> spec.updateInterval() == update)
                .filter(spec -> spec.velocityUpdates() == velocity)
                .filter(TCEntityTypes.LegacyEntitySpec::isRegisteredFoundation)
                .isPresent();
    }

    private static boolean typeShape(EntityType<?> type, MobCategory category, float width, float height, int tracking, int update, boolean velocity) {
        return type.getCategory() == category
                && Float.compare(type.getWidth(), width) == 0
                && Float.compare(type.getHeight(), height) == 0
                && type.clientTrackingRange() == tracking
                && type.updateInterval() == update
                && type.trackDeltas() == velocity;
    }

    private static ResourceLocation entityId(EntityType<?> type) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(type);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path);
    }

    private static boolean close(double left, double right) {
        return Math.abs(left - right) < 0.0001D;
    }

    private static Check check(String name, boolean passed, String notes) {
        return new Check(name, passed, notes);
    }

    public record Check(String name, boolean passed, String notes) {
    }

    public record Report(List<Check> checks) {
        public int passed() {
            return (int) checks.stream().filter(Check::passed).count();
        }

        public int failed() {
            return checks.size() - passed();
        }
    }
}

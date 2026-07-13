package thaumcraft.common.warp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.aspects.TCEntityAspectAssignments;
import thaumcraft.common.entities.TCCultistClericEntity;
import thaumcraft.common.entities.TCCultistEntity;
import thaumcraft.common.entities.TCCultistKnightEntity;
import thaumcraft.common.entities.TCCultistPortalLesserEntity;
import thaumcraft.common.entities.TCEldritchGuardianEntity;
import thaumcraft.common.entities.TCEldritchOrbEntity;
import thaumcraft.common.entities.TCEldritchOrbRenderContract;
import thaumcraft.common.entities.TCMindSpiderEntity;
import thaumcraft.common.lib.potions.PotionBlurredVision;
import thaumcraft.common.lib.potions.PotionDeathGaze;
import thaumcraft.common.lib.potions.PotionSunScorned;
import thaumcraft.common.lib.potions.PotionThaumarhia;
import thaumcraft.common.lib.potions.PotionUnnaturalHunger;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCMobEffects;
import thaumcraft.common.research.TCPlayerKnowledge;
import thaumcraft.common.research.TCPlayerKnowledgeStore;

public final class TCWarpEventBehaviorAudit {
    public static final String ENABLE_PROPERTY = "tc.warpEventBehaviorAudit";
    public static final String OUTPUT_PROPERTY = "tc.warpEventBehaviorAuditPath";

    private TCWarpEventBehaviorAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        if (output.getParent() != null) {
            Files.createDirectories(output.getParent());
        }
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Warp Event Behavior Audit");
        lines.add("");
        lines.add("Runtime parity checks for the first server-side TC6 WarpEvents slice.");
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
            lines.add("| " + check.name() + " | " + (check.passed() ? "PASS" : "FAIL") + " | " + check.notes().replace("|", "\\|") + " |");
        }
        lines.add("");
        lines.add("## Boundary");
        lines.add("");
        lines.add("- Implemented: server tick owner, temporary warp decay, legacy trigger/counter math, legacy outcome threshold table, legacy potion/effect outcomes, Death Gaze range/cone basics, warp research unlock thresholds, warp outcome entity spawn foundations, Eldritch Guardian orb projectile path and lesser cultist portal minion spawning.");
        lines.add("- Implemented: rotten flesh / zombie brain relief path for Unnatural Hunger.");
        lines.add("- Deferred by missing owners: PacketMiscEvent client hallucination/stress visuals, exact Guardian/orb/cultist/portal renderer pixel parity, CultistCleric GolemOrb projectile branch and fortress mask mitigation.");
        Files.write(output, lines, StandardCharsets.UTF_8);
        return report;
    }

    private static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerPlayer player = FakePlayerFactory.getMinecraft(server.overworld());
        TCPlayerWarpStore.clear(player);
        TCPlayerKnowledgeStore.set(player, new TCPlayerKnowledge(), false);

        addMathChecks(checks);
        addOutcomeChecks(checks);
        addEntityOutcomeFoundationChecks(server, checks);
        addEffectRegistrationChecks(checks);
        addRuntimeEffectChecks(player, checks);
        addResearchUnlockChecks(player, checks);

        TCPlayerWarpStore.clear(player);
        player.removeEffect(TCMobEffects.VIS_EXHAUST);
        player.removeEffect(TCMobEffects.THAUMARHIA);
        player.removeEffect(TCMobEffects.UNNATURAL_HUNGER);
        player.removeEffect(TCMobEffects.BLURRED_VISION);
        player.removeEffect(TCMobEffects.SUN_SCORNED);
        player.removeEffect(TCMobEffects.INFECTIOUS_VIS_EXHAUST);
        player.removeEffect(TCMobEffects.DEATH_GAZE);
        player.removeEffect(MobEffects.DIG_SLOWDOWN);
        player.removeEffect(MobEffects.NIGHT_VISION);
        player.removeEffect(MobEffects.BLINDNESS);
        return new Report(List.copyOf(checks));
    }

    private static void addMathChecks(ArrayList<Check> checks) {
        checks.add(check("legacy_interval_constants",
                TCWarpEvents.CHECK_INTERVAL_TICKS == 2000 && TCWarpEvents.DEATH_GAZE_INTERVAL_TICKS == 20,
                "warp=" + TCWarpEvents.CHECK_INTERVAL_TICKS + ", gaze=" + TCWarpEvents.DEATH_GAZE_INTERVAL_TICKS));
        checks.add(check("legacy_trigger_uses_sqrt_counter_threshold",
                TCWarpEvents.shouldTrigger(16, 30, 4)
                        && !TCWarpEvents.shouldTrigger(16, 30, 5)
                        && !TCWarpEvents.shouldTrigger(0, 30, 0)
                        && !TCWarpEvents.shouldTrigger(16, 0, 0),
                "counter16 sqrt=4"));
        checks.add(check("legacy_adjusted_warp_formula_caps_at_100",
                TCWarpEvents.adjustedWarp(30, 18) == 26
                        && TCWarpEvents.adjustedWarp(120, 120) == 100,
                "30/18=" + TCWarpEvents.adjustedWarp(30, 18) + ", cap=" + TCWarpEvents.adjustedWarp(120, 120)));
        checks.add(check("legacy_counter_reduction_with_gear",
                TCWarpEvents.reducedCounter(25, 0) == 15
                        && TCWarpEvents.reducedCounter(25, 3) == 20
                        && TCWarpEvents.reducedCounter(4, 0) == 0,
                "25/0=" + TCWarpEvents.reducedCounter(25, 0) + ", 25/3=" + TCWarpEvents.reducedCounter(25, 3)));
        checks.add(check("legacy_amplifier_and_death_gaze_range_formula",
                TCWarpEvents.legacyAmplifier(14) == 0
                        && TCWarpEvents.legacyAmplifier(45) == 3
                        && TCWarpEvents.legacyAmplifier(100) == 3
                        && TCWarpEvents.deathGazeRange(0) == 8
                        && TCWarpEvents.deathGazeRange(3) == 17
                        && TCWarpEvents.deathGazeRange(10) == 24,
                "amp45=" + TCWarpEvents.legacyAmplifier(45) + ", range10=" + TCWarpEvents.deathGazeRange(10)));
    }

    private static void addOutcomeChecks(ArrayList<Check> checks) {
        checks.add(check("legacy_outcome_threshold_boundaries",
                TCWarpEvents.outcomeForEffect(4) == TCWarpEvents.TCWarpEventOutcome.CREEPER_SOUND
                        && TCWarpEvents.outcomeForEffect(5) == TCWarpEvents.TCWarpEventOutcome.EXPLOSION_SOUND
                        && TCWarpEvents.outcomeForEffect(16) == TCWarpEvents.TCWarpEventOutcome.VIS_EXHAUST
                        && TCWarpEvents.outcomeForEffect(17) == TCWarpEvents.TCWarpEventOutcome.THAUMARHIA
                        && TCWarpEvents.outcomeForEffect(52) == TCWarpEvents.TCWarpEventOutcome.NIGHT_VISION
                        && TCWarpEvents.outcomeForEffect(56) == TCWarpEvents.TCWarpEventOutcome.DEATH_GAZE
                        && TCWarpEvents.outcomeForEffect(72) == TCWarpEvents.TCWarpEventOutcome.BLINDNESS
                        && TCWarpEvents.outcomeForEffect(73) == TCWarpEvents.TCWarpEventOutcome.UNNATURAL_HUNGER_LONG
                        && TCWarpEvents.outcomeForEffect(76) == TCWarpEvents.TCWarpEventOutcome.MOMENT_OF_CLARITY
                        && TCWarpEvents.outcomeForEffect(80) == TCWarpEvents.TCWarpEventOutcome.UNNATURAL_HUNGER_LONG
                        && TCWarpEvents.outcomeForEffect(81) == TCWarpEvents.TCWarpEventOutcome.CULTIST_PORTAL
                        && TCWarpEvents.outcomeForEffect(93) == TCWarpEvents.TCWarpEventOutcome.MIST_GUARDIANS_HEAVY,
                "73=" + TCWarpEvents.outcomeForEffect(73) + ", 76=" + TCWarpEvents.outcomeForEffect(76)));
        checks.add(check("legacy_entity_outcomes_are_backed_by_registered_foundations",
                TCWarpEvents.TCWarpEventOutcome.MIST_ONE_GUARDIAN.entityOutcome()
                        && TCWarpEvents.TCWarpEventOutcome.MIND_SPIDERS_FAKE.entityOutcome()
                        && TCWarpEvents.TCWarpEventOutcome.CULTIST_PORTAL.entityOutcome()
                        && TCWarpEvents.TCWarpEventOutcome.MIND_SPIDERS_REAL.entityOutcome()
                        && !TCWarpEvents.TCWarpEventOutcome.VIS_EXHAUST.entityOutcome()
                        && BuiltInRegistries.ENTITY_TYPE.getKey(TCEntityTypes.ELDRITCH_GUARDIAN.get()).equals(id("eldritch_guardian"))
                        && BuiltInRegistries.ENTITY_TYPE.getKey(TCEntityTypes.ELDRITCH_ORB.get()).equals(id("eldritch_orb"))
                        && BuiltInRegistries.ENTITY_TYPE.getKey(TCEntityTypes.MIND_SPIDER.get()).equals(id("mind_spider"))
                        && BuiltInRegistries.ENTITY_TYPE.getKey(TCEntityTypes.CULTIST_PORTAL_LESSER.get()).equals(id("cultist_portal_lesser"))
                        && BuiltInRegistries.ENTITY_TYPE.getKey(TCEntityTypes.CULTIST_KNIGHT.get()).equals(id("cultist_knight"))
                        && BuiltInRegistries.ENTITY_TYPE.getKey(TCEntityTypes.CULTIST_CLERIC.get()).equals(id("cultist_cleric")),
                "entity outcomes use real registered foundations"));
    }

    private static void addEntityOutcomeFoundationChecks(MinecraftServer server, ArrayList<Check> checks) {
        checks.add(check("legacy_guardian_count_formula_and_cap",
                TCWarpEvents.guardianCountForOutcome(TCWarpEvents.TCWarpEventOutcome.MIST_ONE_GUARDIAN, 45) == 1
                        && TCWarpEvents.guardianCountForOutcome(TCWarpEvents.TCWarpEventOutcome.MIST_GUARDIANS_LIGHT, 90) == 3
                        && TCWarpEvents.guardianCountForOutcome(TCWarpEvents.TCWarpEventOutcome.MIST_GUARDIANS_HEAVY, 90) == 6
                        && TCWarpEvents.boundedGuardianCount(12) == 8,
                "one=1, light=warp/30, heavy=warp/15, cap=8"));
        checks.add(check("legacy_entity_type_shapes_for_warp_outcomes",
                TCEntityTypes.ELDRITCH_GUARDIAN.get().getCategory() == MobCategory.MONSTER
                        && Float.compare(TCEntityTypes.ELDRITCH_GUARDIAN.get().getWidth(), 0.8F) == 0
                        && Float.compare(TCEntityTypes.ELDRITCH_GUARDIAN.get().getHeight(), 2.25F) == 0
                        && Float.compare(TCEntityTypes.ELDRITCH_GUARDIAN.get().getDimensions().eyeHeight(), 2.1F) == 0
                        && Float.compare(TCEntityTypes.MIND_SPIDER.get().getWidth(), 0.7F) == 0
                        && Float.compare(TCEntityTypes.MIND_SPIDER.get().getHeight(), 0.5F) == 0
                        && Float.compare(TCEntityTypes.MIND_SPIDER.get().getDimensions().eyeHeight(), 0.45F) == 0
                        && Float.compare(TCEntityTypes.CULTIST_PORTAL_LESSER.get().getWidth(), 1.5F) == 0
                        && Float.compare(TCEntityTypes.CULTIST_PORTAL_LESSER.get().getHeight(), 3.0F) == 0
                        && Float.compare(TCEntityTypes.CULTIST_KNIGHT.get().getWidth(), 0.6F) == 0
                        && Float.compare(TCEntityTypes.CULTIST_KNIGHT.get().getHeight(), 1.8F) == 0
                        && Float.compare(TCEntityTypes.CULTIST_CLERIC.get().getWidth(), 0.6F) == 0
                        && Float.compare(TCEntityTypes.CULTIST_CLERIC.get().getHeight(), 1.8F) == 0,
                "guardian=0.8x2.25 eye2.1, spider=0.7x0.5 eye0.45, portal=1.5x3.0, cultists=0.6x1.8"));
        checks.add(check("legacy_eldritch_orb_type_shape",
                TCEntityTypes.ELDRITCH_ORB.get().getCategory() == MobCategory.MISC
                        && Float.compare(TCEntityTypes.ELDRITCH_ORB.get().getWidth(), 0.25F) == 0
                        && Float.compare(TCEntityTypes.ELDRITCH_ORB.get().getHeight(), 0.25F) == 0
                        && TCEntityTypes.ELDRITCH_ORB.get().clientTrackingRange() == 64
                        && TCEntityTypes.ELDRITCH_ORB.get().updateInterval() == 20
                        && TCEntityTypes.ELDRITCH_ORB.get().trackDeltas(),
                "orb=0.25x0.25, tracking=64, update=20, velocity=true"));

        TCMindSpiderEntity spider = TCEntityTypes.MIND_SPIDER.get().create(server.overworld());
        TCCultistPortalLesserEntity portal = TCEntityTypes.CULTIST_PORTAL_LESSER.get().create(server.overworld());
        TCCultistKnightEntity knight = TCEntityTypes.CULTIST_KNIGHT.get().create(server.overworld());
        TCCultistClericEntity cleric = TCEntityTypes.CULTIST_CLERIC.get().create(server.overworld());
        TCEldritchGuardianEntity guardian = TCEntityTypes.ELDRITCH_GUARDIAN.get().create(server.overworld());
        TCEldritchOrbEntity orb = TCEntityTypes.ELDRITCH_ORB.get().create(server.overworld());
        checks.add(check("legacy_entity_foundation_classes_construct",
                spider != null && portal != null && knight != null && cleric != null && guardian != null && orb != null,
                "spider=" + (spider != null) + ", portal=" + (portal != null) + ", knight=" + (knight != null) + ", cleric=" + (cleric != null) + ", guardian=" + (guardian != null) + ", orb=" + (orb != null)));
        if (orb != null) {
            checks.add(check("eldritch_orb_projectile_contract",
                    TCEldritchOrbEntity.LEGACY_LIFETIME_TICKS == 100
                            && Double.compare(TCEldritchOrbEntity.LEGACY_IMPACT_RADIUS, 2.0D) == 0
                            && Float.compare(TCEldritchOrbEntity.LEGACY_DAMAGE_MULTIPLIER, 0.666F) == 0
                            && TCEldritchOrbEntity.LEGACY_WEAKNESS_DURATION == 160
                            && TCEldritchOrbEntity.LEGACY_WEAKNESS_AMPLIFIER == 0
                            && Double.compare(orb.gravityForValidation(), 0.0D) == 0
                            && Math.abs(TCEldritchOrbEntity.legacyDamageFromAttack(7.0D) - 4.662F) < 0.0001F,
                    "life=100, radius=2, damage=attack*0.666, weakness=160, gravity=" + orb.gravityForValidation()));
            checks.add(check("eldritch_orb_renderer_contract",
                    TCEldritchOrbRenderContract.LEGACY_TENDRIL_COUNT == 12
                            && TCEldritchOrbRenderContract.LEGACY_RANDOM_SEED == 187
                            && TCEldritchOrbRenderContract.LEGACY_FRAME_COUNT == 13
                            && TCEldritchOrbRenderContract.LEGACY_GRID_SIZE == 64
                            && TCEldritchOrbRenderContract.LEGACY_FRAME_V == 3
                            && Float.compare(TCEldritchOrbRenderContract.LEGACY_BILLBOARD_SCALE, 0.75F) == 0,
                    "12 seeded tendrils, 13-frame particle strip, billboard scale 0.75"));
        }
        if (guardian != null) {
            checks.add(check("eldritch_guardian_ranged_orb_contract",
                    Double.compare(TCEldritchGuardianEntity.LEGACY_RANGED_MIN_DISTANCE, 8.0D) == 0
                            && Double.compare(TCEldritchGuardianEntity.LEGACY_RANGED_SPEED, 1.0D) == 0
                            && TCEldritchGuardianEntity.LEGACY_RANGED_INTERVAL_MIN == 20
                            && TCEldritchGuardianEntity.LEGACY_RANGED_INTERVAL_MAX == 40
                            && Float.compare(TCEldritchGuardianEntity.LEGACY_RANGED_RADIUS, 24.0F) == 0
                            && Float.compare(TCEldritchGuardianEntity.LEGACY_SONIC_CHANCE, 0.15F) == 0
                            && Math.abs(TCEldritchGuardianEntity.legacyOrbSideOffset(0.0F, true).x()) < 0.0001D
                            && Math.abs(TCEldritchGuardianEntity.legacyOrbSideOffset(0.0F, true).z() - 0.5D) < 0.0001D,
                    "min=8, speed=1, interval=20..40, radius=24, sonic=15%"));
        }
        if (spider != null) {
            spider.setHarmless(true);
            spider.setViewer("FakePlayer");
            checks.add(check("mind_spider_harmless_viewer_lifespan_contract",
                    spider.isHarmless()
                            && "FakePlayer".equals(spider.getViewer())
                            && spider.lifespanForValidation() == 1200
                            && spider.baseExperienceForValidation() == 0,
                    "harmless=" + spider.isHarmless()
                            + ", viewer=" + spider.getViewer()
                            + ", lifespan=" + spider.lifespanForValidation()));
        }
        if (portal != null) {
            portal.setActive(true);
            checks.add(check("lesser_cultist_portal_active_state_and_budget_contract",
                    portal.isActive()
                            && !portal.isPushable()
                            && portal.fireImmune()
                            && TCCultistPortalLesserEntity.legacyCultistMinionBudget(net.minecraft.world.Difficulty.EASY) == 2
                            && TCCultistPortalLesserEntity.legacyCultistMinionBudget(net.minecraft.world.Difficulty.NORMAL) == 4
                            && TCCultistPortalLesserEntity.legacyCultistMinionBudget(net.minecraft.world.Difficulty.HARD) == 6,
                    "active=" + portal.isActive() + ", budgets easy/normal/hard=2/4/6"));
            portal.setPos(0.5D, server.overworld().getSharedSpawnPos().getY() + 2.0D, 0.5D);
            server.overworld().addFreshEntity(portal);
            float beforeHealth = portal.getHealth();
            TCCultistEntity spawned = portal.spawnLegacyMinionForValidation(false);
            float lostHealth = beforeHealth - portal.getHealth();
            checks.add(check("lesser_cultist_portal_spawns_knight_and_self_damages",
                    spawned instanceof TCCultistKnightEntity
                            && portal.spawnedMinionCountForValidation() == 1
                            && portal.lastSpawnedMinionTypeForValidation() == TCEntityTypes.CULTIST_KNIGHT.get()
                            && lostHealth >= 5.0F
                            && lostHealth <= 9.0F,
                    "spawned=" + (spawned == null ? "null" : BuiltInRegistries.ENTITY_TYPE.getKey(spawned.getType())) + ", lostHealth=" + lostHealth));
            if (spawned != null) {
                spawned.discard();
            }
            TCCultistEntity clericSpawned = portal.spawnLegacyMinionForValidation(true);
            checks.add(check("lesser_cultist_portal_can_force_cleric_spawn_path_for_validation",
                    clericSpawned instanceof TCCultistClericEntity
                            && portal.spawnedMinionCountForValidation() == 2
                            && portal.lastSpawnedMinionTypeForValidation() == TCEntityTypes.CULTIST_CLERIC.get(),
                    "spawned=" + (clericSpawned == null ? "null" : BuiltInRegistries.ENTITY_TYPE.getKey(clericSpawned.getType()))));
            if (clericSpawned != null) {
                clericSpawned.discard();
            }
            portal.discard();
        }

        checks.add(check("custom_warp_entity_aspect_contracts_match_config_aspects",
                hasAspects(TCEntityAspectAssignments.getEntityTypeAspectsForValidation(TCEntityTypes.MIND_SPIDER.get()),
                        Aspect.FLUX, 5, Aspect.FIRE, 5)
                        && hasAspects(TCEntityAspectAssignments.getEntityTypeAspectsForValidation(TCEntityTypes.ELDRITCH_GUARDIAN.get()),
                        Aspect.ELDRITCH, 20, Aspect.DEATH, 20, Aspect.UNDEAD, 20)
                        && hasAspects(TCEntityAspectAssignments.getEntityTypeAspectsForValidation(TCEntityTypes.CULTIST_KNIGHT.get()),
                        Aspect.ELDRITCH, 5, Aspect.MAN, 15, Aspect.AVERSION, 5)
                        && hasAspects(TCEntityAspectAssignments.getEntityTypeAspectsForValidation(TCEntityTypes.CULTIST_CLERIC.get()),
                        Aspect.ELDRITCH, 5, Aspect.MAN, 15, Aspect.AVERSION, 5),
                "MindSpider=vitium5/ignis5, EldritchGuardian=alienis20/mortuus20/exanimis20, Cultists=alienis5/humanus15/aversio5"));
    }

    private static void addEffectRegistrationChecks(ArrayList<Check> checks) {
        checks.add(effectCheck("unnatural_hunger_effect_registered_with_legacy_color",
                "unnatural_hunger",
                PotionUnnaturalHunger.LEGACY_COLOR,
                TCMobEffects.UNNATURAL_HUNGER.get()));
        checks.add(effectCheck("death_gaze_effect_registered_with_legacy_color",
                "death_gaze",
                PotionDeathGaze.LEGACY_COLOR,
                TCMobEffects.DEATH_GAZE.get()));
        checks.add(effectCheck("blurred_vision_effect_registered_with_legacy_color",
                "blurred_vision",
                PotionBlurredVision.LEGACY_COLOR,
                TCMobEffects.BLURRED_VISION.get()));
        checks.add(effectCheck("sun_scorned_effect_registered_with_legacy_color",
                "sun_scorned",
                PotionSunScorned.LEGACY_COLOR,
                TCMobEffects.SUN_SCORNED.get()));
        checks.add(effectCheck("thaumarhia_effect_registered_with_legacy_color",
                "thaumarhia",
                PotionThaumarhia.LEGACY_COLOR,
                TCMobEffects.THAUMARHIA.get()));
    }

    private static void addRuntimeEffectChecks(ServerPlayer player, ArrayList<Check> checks) {
        TCWarpEvents.executeOutcomeForValidation(player, 45, TCWarpEvents.TCWarpEventOutcome.VIS_EXHAUST);
        MobEffectInstance vis = player.getEffect(TCMobEffects.VIS_EXHAUST);
        checks.add(check("vis_exhaust_outcome_matches_legacy_duration_amp",
                vis != null && vis.getDuration() <= 5000 && vis.getDuration() > 4900 && vis.getAmplifier() == 3,
                "effect=" + effectNotes(vis)));

        TCWarpEvents.executeOutcomeForValidation(player, 45, TCWarpEvents.TCWarpEventOutcome.DEATH_GAZE);
        MobEffectInstance gaze = player.getEffect(TCMobEffects.DEATH_GAZE);
        checks.add(check("death_gaze_outcome_matches_legacy_duration_amp",
                gaze != null && gaze.getDuration() <= 6000 && gaze.getDuration() > 5900 && gaze.getAmplifier() == 3,
                "effect=" + effectNotes(gaze)));

        TCWarpEvents.executeOutcomeForValidation(player, 45, TCWarpEvents.TCWarpEventOutcome.UNNATURAL_HUNGER_LONG);
        MobEffectInstance hunger = player.getEffect(TCMobEffects.UNNATURAL_HUNGER);
        checks.add(check("long_unnatural_hunger_outcome_matches_legacy_duration_amp",
                hunger != null && hunger.getDuration() <= 6000 && hunger.getDuration() > 5900 && hunger.getAmplifier() == 3,
                "effect=" + effectNotes(hunger)));

        player.removeEffect(TCMobEffects.UNNATURAL_HUNGER);
        player.addEffect(new MobEffectInstance(TCMobEffects.UNNATURAL_HUNGER, 1200, 1, true, true));
        TCWarpEvents.onFinishUsingItem(new LivingEntityUseItemEvent.Finish(player, new ItemStack(Items.ROTTEN_FLESH), 0, ItemStack.EMPTY));
        MobEffectInstance relieved = player.getEffect(TCMobEffects.UNNATURAL_HUNGER);
        checks.add(check("unnatural_hunger_curative_items_reduce_duration_and_amplifier",
                relieved != null && relieved.getDuration() <= 600 && relieved.getDuration() > 500 && relieved.getAmplifier() == 0,
                "effect=" + effectNotes(relieved)));

        player.removeEffect(TCMobEffects.UNNATURAL_HUNGER);
        player.addEffect(new MobEffectInstance(TCMobEffects.UNNATURAL_HUNGER, 1200, 1, true, true));
        TCWarpEvents.onFinishUsingItem(new LivingEntityUseItemEvent.Finish(player, new ItemStack(Items.APPLE), 0, ItemStack.EMPTY));
        MobEffectInstance normalFood = player.getEffect(TCMobEffects.UNNATURAL_HUNGER);
        checks.add(check("normal_food_does_not_relieve_unnatural_hunger",
                normalFood != null && normalFood.getAmplifier() == 1,
                "effect=" + effectNotes(normalFood)));
    }

    private static void addResearchUnlockChecks(ServerPlayer player, ArrayList<Check> checks) {
        TCPlayerKnowledgeStore.set(player, new TCPlayerKnowledge(), false);
        boolean lowChanged = TCWarpEvents.unlockWarpResearch(player, 10);
        TCPlayerKnowledge low = TCPlayerKnowledgeStore.get(player);
        checks.add(check("actual_warp_10_unlocks_no_eldritch_or_bathsalts",
                !lowChanged
                        && !low.hasResearch("!BATHSALTS")
                        && !low.hasResearch("ELDRITCHMINOR")
                        && !low.hasResearch("ELDRITCHMAJOR"),
                "changed=" + lowChanged));

        TCPlayerKnowledgeStore.set(player, new TCPlayerKnowledge(), false);
        boolean changed = TCWarpEvents.unlockWarpResearch(player, 51);
        TCPlayerKnowledge high = TCPlayerKnowledgeStore.get(player);
        checks.add(check("actual_warp_thresholds_unlock_hidden_bathsalts_and_eldritch_research",
                changed
                        && high.hasResearch("!BATHSALTS")
                        && high.hasResearch("ELDRITCHMINOR")
                        && high.hasResearch("ELDRITCHMAJOR"),
                "bathsalts=" + high.hasResearch("!BATHSALTS")
                        + ", minor=" + high.hasResearch("ELDRITCHMINOR")
                        + ", major=" + high.hasResearch("ELDRITCHMAJOR")));
    }

    private static Check effectCheck(String name, String path, int color, net.minecraft.world.effect.MobEffect effect) {
        ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(effect);
        boolean passed = ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path).equals(id)
                && effect.getColor() == color;
        return check(name, passed, "id=" + id + ", color=" + effect.getColor());
    }

    private static String effectNotes(MobEffectInstance effect) {
        return effect == null ? "missing" : "duration=" + effect.getDuration() + ", amp=" + effect.getAmplifier();
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path);
    }

    private static boolean hasAspects(AspectList list, Object... pairs) {
        if (list == null || list.size() != pairs.length / 2) {
            return false;
        }
        for (int i = 0; i < pairs.length; i += 2) {
            if (list.getAmount((Aspect) pairs[i]) != (Integer) pairs[i + 1]) {
                return false;
            }
        }
        return true;
    }

    private static Check check(String name, boolean passed, String notes) {
        return new Check(name, passed, notes);
    }

    public record Check(String name, boolean passed, String notes) {
    }

    public record Report(List<Check> checks) {
        public long passed() {
            return checks.stream().filter(Check::passed).count();
        }

        public long failed() {
            return checks.size() - passed();
        }
    }
}

package thaumcraft.common.entities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.aspects.TCEntityAspectAssignments;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCEntityTypes.LegacyEntitySpec;

public final class TCEntityFoundationAudit {
    public static final String ENABLE_PROPERTY = "tc.entityFoundationAudit";
    public static final String OUTPUT_PROPERTY = "tc.entityFoundationAuditPath";

    private static final int LEGACY_ENTITY_COUNT = 43;
    private static final int REGISTERED_FOUNDATION_COUNT = 27;

    private TCEntityFoundationAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        if (output.getParent() != null) {
            Files.createDirectories(output.getParent());
        }
        List<Check> checks = runChecks(server);
        StringBuilder markdown = new StringBuilder();
        markdown.append("# Thaumcraft entity foundation audit\n\n");
        markdown.append("This audit intentionally verifies only the entity registry/foundation slice. ");
        markdown.append("AI, mob spawn rules, boss behavior, golems, focus projectile gameplay and custom renderers remain subsystem-specific blockers.\n\n");
        markdown.append("## Checks\n\n");
        markdown.append("| Check | Status | Notes |\n");
        markdown.append("| --- | --- | --- |\n");
        for (Check check : checks) {
            markdown.append("| ")
                    .append(escape(check.name()))
                    .append(" | ")
                    .append(check.passed() ? "PASS" : "FAIL")
                    .append(" | ")
                    .append(escape(check.notes()))
                    .append(" |\n");
        }

        markdown.append("\n## Legacy entity catalog\n\n");
        markdown.append("| Legacy id | Legacy class | Modern id | Tracking | Update | Velocity | Status | Notes |\n");
        markdown.append("| --- | --- | --- | ---: | ---: | --- | --- | --- |\n");
        for (LegacyEntitySpec spec : TCEntityTypes.legacyEntitySpecs()) {
            markdown.append("| ")
                    .append(spec.legacyId())
                    .append(" | ")
                    .append(spec.legacyClass())
                    .append(" | ")
                    .append(spec.modernId() == null ? "" : "thaumcraft:" + spec.modernId())
                    .append(" | ")
                    .append(spec.trackingRange())
                    .append(" | ")
                    .append(spec.updateInterval())
                    .append(" | ")
                    .append(spec.velocityUpdates())
                    .append(" | ")
                    .append(spec.status())
                    .append(" | ")
                    .append(escape(spec.notes()))
                    .append(" |\n");
        }

        Files.writeString(output, markdown.toString(), StandardCharsets.UTF_8);
        long passed = checks.stream().filter(Check::passed).count();
        return new Report((int) passed, checks.size() - (int) passed);
    }

    private static List<Check> runChecks(MinecraftServer server) {
        List<Check> checks = new ArrayList<>();
        List<LegacyEntitySpec> specs = TCEntityTypes.legacyEntitySpecs();
        checks.add(new Check(
                "legacy entity catalog count",
                specs.size() == LEGACY_ENTITY_COUNT,
                "expected " + LEGACY_ENTITY_COUNT + ", got " + specs.size()
        ));
        checks.add(new Check(
                "registered foundation count",
                TCEntityTypes.registeredFoundationSpecs().size() == REGISTERED_FOUNDATION_COUNT,
                "expected item entities, Alumentum, CausalityCollapser, BottleTaint, EldritchOrb, GolemOrb, FluxRift, ArcaneBore, FallingTaint, Wisp, Firebat, Pech, BrainyZombie pair, TaintSeed pair, five taint mob foundations, cultist minion pair and warp outcome foundations"
        ));

        checks.add(checkRegisteredType("SpecialItem", TCEntityTypes.SPECIAL_ITEM.get()));
        checks.add(checkRegisteredType("FollowItem", TCEntityTypes.FOLLOW_ITEM.get()));
        checks.add(checkRegisteredType("FluxRift", TCEntityTypes.FLUX_RIFT.get()));
        checks.add(checkRegisteredType("ArcaneBore", TCEntityTypes.ARCANE_BORE.get()));
        checks.add(checkRegisteredType("FallingTaint", TCEntityTypes.FALLING_TAINT.get()));
        checks.add(checkRegisteredType("Wisp", TCEntityTypes.WISP.get()));
        checks.add(checkRegisteredType("Firebat", TCEntityTypes.FIREBAT.get()));
        checks.add(checkRegisteredType("Pech", TCEntityTypes.PECH.get()));
        checks.add(checkRegisteredType("BrainyZombie", TCEntityTypes.BRAINY_ZOMBIE.get()));
        checks.add(checkRegisteredType("GiantBrainyZombie", TCEntityTypes.GIANT_BRAINY_ZOMBIE.get()));
        checks.add(checkRegisteredType("TaintSeed", TCEntityTypes.TAINT_SEED.get()));
        checks.add(checkRegisteredType("TaintSeedPrime", TCEntityTypes.TAINT_SEED_PRIME.get()));
        checks.add(checkRegisteredType("CultistPortalLesser", TCEntityTypes.CULTIST_PORTAL_LESSER.get()));
        checks.add(checkRegisteredType("CultistKnight", TCEntityTypes.CULTIST_KNIGHT.get()));
        checks.add(checkRegisteredType("CultistCleric", TCEntityTypes.CULTIST_CLERIC.get()));
        checks.add(checkRegisteredType("MindSpider", TCEntityTypes.MIND_SPIDER.get()));
        checks.add(checkRegisteredType("EldritchGuardian", TCEntityTypes.ELDRITCH_GUARDIAN.get()));
        checks.add(checkRegisteredType("ThaumSlime", TCEntityTypes.THAUM_SLIME.get()));
        checks.add(checkRegisteredType("TaintCrawler", TCEntityTypes.TAINT_CRAWLER.get()));
        checks.add(checkRegisteredType("Taintacle", TCEntityTypes.TAINTACLE.get()));
        checks.add(checkRegisteredType("TaintacleTiny", TCEntityTypes.TAINTACLE_TINY.get()));
        checks.add(checkRegisteredType("TaintSwarm", TCEntityTypes.TAINT_SWARM.get()));
        checks.add(checkRegisteredType("Alumentum", TCEntityTypes.ALUMENTUM.get()));
        checks.add(checkRegisteredType("CausalityCollapser", TCEntityTypes.CAUSALITY_COLLAPSER.get()));
        checks.add(checkRegisteredType("BottleTaint", TCEntityTypes.BOTTLE_TAINT.get()));
        checks.add(checkRegisteredType("EldritchOrb", TCEntityTypes.ELDRITCH_ORB.get()));
        checks.add(checkRegisteredType("GolemOrb", TCEntityTypes.GOLEM_ORB.get()));
        checks.add(checkTypeShape("SpecialItem", TCEntityTypes.SPECIAL_ITEM.get(), 64, 20, true));
        checks.add(checkTypeShape("FollowItem", TCEntityTypes.FOLLOW_ITEM.get(), 64, 20, false));
        checks.add(checkTypeShape("FallingTaint", TCEntityTypes.FALLING_TAINT.get(), 64, 3, true, 0.98F, 0.98F));
        checks.add(checkMobTypeShape("Wisp", TCEntityTypes.WISP.get(), 0.9F, 0.9F, 64, 3, false));
        checks.add(checkMobTypeShape("Firebat", TCEntityTypes.FIREBAT.get(), 0.5F, 0.9F, 64, 3, false));
        checks.add(checkMobTypeShape("Pech", TCEntityTypes.PECH.get(), 0.6F, 1.8F, 64, 3, true));
        checks.add(checkMobTypeShape("BrainyZombie", TCEntityTypes.BRAINY_ZOMBIE.get(), 0.6F, 1.95F, 64, 3, true));
        checks.add(checkMobTypeShape("GiantBrainyZombie", TCEntityTypes.GIANT_BRAINY_ZOMBIE.get(), 0.6F, 1.95F, 64, 3, true));
        checks.add(checkMobTypeShape("ThaumSlime", TCEntityTypes.THAUM_SLIME.get(), 2.04F, 2.04F, 64, 3, true));
        checks.add(checkMobTypeShape("TaintCrawler", TCEntityTypes.TAINT_CRAWLER.get(), 0.5F, 0.4F, 64, 3, true));
        checks.add(checkMobTypeShape("Taintacle", TCEntityTypes.TAINTACLE.get(), 0.8F, 3.0F, 64, 3, false));
        checks.add(checkMobTypeShape("TaintacleTiny", TCEntityTypes.TAINTACLE_TINY.get(), 0.22F, 1.0F, 64, 3, false));
        checks.add(checkMobTypeShape("TaintSwarm", TCEntityTypes.TAINT_SWARM.get(), 2.0F, 2.0F, 64, 3, false));
        checks.add(checkTypeShape("Alumentum", TCEntityTypes.ALUMENTUM.get(), 64, 20, true));
        checks.add(checkTypeShape("CausalityCollapser", TCEntityTypes.CAUSALITY_COLLAPSER.get(), 64, 20, true));
        checks.add(checkTypeShape("BottleTaint", TCEntityTypes.BOTTLE_TAINT.get(), 64, 20, true));
        checks.add(checkTypeShape("EldritchOrb", TCEntityTypes.ELDRITCH_ORB.get(), 64, 20, true));
        checks.add(checkTypeShape("GolemOrb", TCEntityTypes.GOLEM_ORB.get(), 64, 3, true));
        checks.add(checkMobTypeShape("CultistPortalLesser", TCEntityTypes.CULTIST_PORTAL_LESSER.get(), 1.5F, 3.0F, 64, 20, false));
        checks.add(checkMobTypeShape("CultistKnight", TCEntityTypes.CULTIST_KNIGHT.get(), 0.6F, 1.8F, 64, 3, true));
        checks.add(checkMobTypeShape("CultistCleric", TCEntityTypes.CULTIST_CLERIC.get(), 0.6F, 1.8F, 64, 3, true));
        checks.add(checkMobTypeShape("MindSpider", TCEntityTypes.MIND_SPIDER.get(), 0.7F, 0.5F, 64, 3, true));
        checks.add(checkMobTypeShape("EldritchGuardian", TCEntityTypes.ELDRITCH_GUARDIAN.get(), 0.8F, 2.25F, 64, 3, true));
        checks.add(checkBrainyZombieContracts(server.overworld()));
        checks.add(checkGiantBrainyZombieContracts(server.overworld()));
        checks.add(checkFirebatContracts(server.overworld()));
        checks.add(checkPechContracts(server.overworld()));
        checks.add(checkConstructors(server.overworld()));
        return checks;
    }

    private static Check checkBrainyZombieContracts(ServerLevel level) {
        TCBrainyZombieEntity brainy = TCEntityTypes.BRAINY_ZOMBIE.get().create(level);
        AspectList aspects = TCEntityAspectAssignments.getEntityTypeAspectsForValidation(TCEntityTypes.BRAINY_ZOMBIE.get());
        boolean passed = brainy != null
                && close(brainy.getAttributeValue(Attributes.MAX_HEALTH), TCBrainyZombieEntity.LEGACY_MAX_HEALTH)
                && close(brainy.getAttributeValue(Attributes.ATTACK_DAMAGE), TCBrainyZombieEntity.LEGACY_ATTACK_DAMAGE)
                && close(brainy.getAttributeValue(Attributes.ARMOR), TCBrainyZombieEntity.LEGACY_ARMOR_BONUS)
                && close(brainy.getAttributeValue(Attributes.SPAWN_REINFORCEMENTS_CHANCE), TCBrainyZombieEntity.LEGACY_REINFORCEMENT_CHANCE)
                && TCBrainyZombieEntity.legacyShouldDropBrainRoll(4, 0)
                && !TCBrainyZombieEntity.legacyShouldDropBrainRoll(5, 0)
                && TCBrainyZombieEntity.legacyShouldDropBrainRoll(5, 1)
                && aspects != null
                && aspects.getAmount(Aspect.UNDEAD) == 20
                && aspects.getAmount(Aspect.MAN) == 10
                && aspects.getAmount(Aspect.MIND) == 5
                && aspects.getAmount(Aspect.AVERSION) == 5;
        return new Check("BrainyZombie legacy contracts", passed, "attributes, reinforcement gate, brain-drop roll and ConfigAspects contract");
    }

    private static Check checkGiantBrainyZombieContracts(ServerLevel level) {
        TCGiantBrainyZombieEntity giant = TCEntityTypes.GIANT_BRAINY_ZOMBIE.get().create(level);
        AspectList aspects = TCEntityAspectAssignments.getEntityTypeAspectsForValidation(TCEntityTypes.GIANT_BRAINY_ZOMBIE.get());
        boolean passed = giant != null
                && close(giant.getAttributeValue(Attributes.MAX_HEALTH), TCGiantBrainyZombieEntity.LEGACY_MAX_HEALTH)
                && close(giant.getAttributeValue(Attributes.ATTACK_DAMAGE), TCGiantBrainyZombieEntity.LEGACY_BASE_ATTACK_DAMAGE)
                && close(TCGiantBrainyZombieEntity.legacyAttackDamageForAnger(0.0F), 2.0D)
                && close(TCGiantBrainyZombieEntity.legacyAttackDamageForAnger(1.0F), 7.0D)
                && close(TCGiantBrainyZombieEntity.legacyAttackDamageForAnger(2.0F), 12.0D)
                && TCBrainyZombieEntity.legacyShouldDropBrainRoll(5, 1)
                && TCGiantBrainyZombieEntity.LEGACY_ROTTEN_FLESH_LOOPS == 12
                && TCGiantBrainyZombieEntity.LEGACY_ROTTEN_FLESH_PER_DROP == 2
                && aspects != null
                && aspects.getAmount(Aspect.UNDEAD) == 25
                && aspects.getAmount(Aspect.MAN) == 15
                && aspects.getAmount(Aspect.MIND) == 5
                && aspects.getAmount(Aspect.AVERSION) == 10;
        if (giant != null) {
            giant.setAnger(2.0F);
            giant.refreshDimensions();
            passed = passed
                    && close(giant.getDimensions(Pose.STANDING).width(), 1.8D)
                    && close(giant.getDimensions(Pose.STANDING).height(), 5.85D)
                    && close(giant.getDimensions(Pose.STANDING).eyeHeight(), 5.22D);
        }
        return new Check("GiantBrainyZombie legacy contracts", passed, "attributes, anger damage/size/eye-height, inherited brain drop, rotten flesh loops and ConfigAspects contract");
    }

    private static Check checkFirebatContracts(ServerLevel level) {
        TCFirebatEntity firebat = TCEntityTypes.FIREBAT.get().create(level);
        AspectList aspects = TCEntityAspectAssignments.getEntityTypeAspectsForValidation(TCEntityTypes.FIREBAT.get());
        boolean passed = firebat != null
                && firebat.isResting()
                && close(firebat.getAttributeValue(Attributes.MAX_HEALTH), TCFirebatEntity.LEGACY_MAX_HEALTH)
                && close(firebat.getAttributeValue(Attributes.ATTACK_DAMAGE), TCFirebatEntity.LEGACY_ATTACK_DAMAGE)
                && firebat.attackTimeForValidation() == 0
                && firebat.damBonusForValidation() == 0
                && TCFirebatEntity.LEGACY_MIN_FLIGHT_TARGET_Y == 1
                && TCFirebatEntity.legacyExplosionInteractionForValidation() == net.minecraft.world.level.Level.ExplosionInteraction.NONE
                && TCFirebatEntity.isLegacyHalloween(LocalDate.of(2026, 10, 31))
                && !TCFirebatEntity.isLegacyHalloween(LocalDate.of(2026, 11, 1))
                && TCFirebatEntity.testLegacySpawnGatesForValidation(true, net.minecraft.world.Difficulty.NORMAL, true, 0, 6, true, false, false)
                && !TCFirebatEntity.testLegacySpawnGatesForValidation(false, net.minecraft.world.Difficulty.NORMAL, true, 0, 6, true, false, false)
                && !TCFirebatEntity.testLegacySpawnGatesForValidation(true, net.minecraft.world.Difficulty.NORMAL, true, 7, 6, true, false, false)
                && !TCFirebatEntity.testLegacySpawnGatesForValidation(true, net.minecraft.world.Difficulty.NORMAL, true, 0, 6, false, true, false)
                && TCFirebatEntity.testLegacySpawnGatesForValidation(true, net.minecraft.world.Difficulty.NORMAL, true, 0, 6, false, true, true)
                && aspects != null
                && aspects.getAmount(Aspect.BEAST) == 5
                && aspects.getAmount(Aspect.FLIGHT) == 5
                && aspects.getAmount(Aspect.FIRE) == 10;
        return new Check("Firebat legacy contracts", passed, "resting state, attributes, fire/explosion profile, Halloween gate, light roll gate and ConfigAspects contract");
    }

    private static Check checkPechContracts(ServerLevel level) {
        TCPechEntity pech = TCEntityTypes.PECH.get().create(level);
        AspectList foragerAspects = TCEntityAspectAssignments.getEntityTypeAspectsForValidation(TCEntityTypes.PECH.get());
        boolean passed = pech != null
                && pech.getPechType() == TCPechEntity.PechType.FORAGER
                && !pech.isTamed()
                && pech.getAnger() == 0
                && pech.getContainerSize() == TCPechEntity.LEGACY_LOOT_SLOTS
                && close(pech.getAttributeValue(Attributes.MAX_HEALTH), TCPechEntity.LEGACY_MAX_HEALTH)
                && close(pech.getAttributeValue(Attributes.ATTACK_DAMAGE), TCPechEntity.LEGACY_ATTACK_DAMAGE)
                && close(pech.getAttributeValue(Attributes.MOVEMENT_SPEED), TCPechEntity.LEGACY_MOVEMENT_SPEED)
                && close(pech.getAttributeValue(Attributes.ARMOR), TCPechEntity.LEGACY_ARMOR_BONUS)
                && pech.getValue(new ItemStack(Items.ENDER_PEARL)) == TCPechEntity.LEGACY_ENDER_PEARL_VALUE
                && TCPechTradeCatalog.entryCountForValidation(TCPechEntity.PechType.FORAGER) >= 20
                && TCPechTradeCatalog.hasTierForValidation(TCPechEntity.PechType.FORAGER, 1)
                && TCPechTradeCatalog.hasTierForValidation(TCPechEntity.PechType.FORAGER, 5)
                && TCPechTradeCatalog.hasTierForValidation(TCPechEntity.PechType.MAGE, 5)
                && TCPechTradeCatalog.hasTierForValidation(TCPechEntity.PechType.STALKER, 5)
                && TCPechEntity.testLegacySpawnGatesForValidation(true, true, true, 3, true)
                && !TCPechEntity.testLegacySpawnGatesForValidation(true, true, true, 4, true)
                && !TCPechEntity.testLegacySpawnGatesForValidation(true, false, true, 0, true)
                && foragerAspects != null
                && foragerAspects.getAmount(Aspect.MAN) == 10
                && foragerAspects.getAmount(Aspect.AURA) == 5
                && foragerAspects.getAmount(Aspect.EXCHANGE) == 10
                && foragerAspects.getAmount(Aspect.DESIRE) == 5;
        if (pech != null) {
            pech.setPechType(TCPechEntity.PechType.MAGE);
            AspectList mageAspects = TCEntityAspectAssignments.getEntityAspects(pech);
            pech.setPechType(TCPechEntity.PechType.STALKER);
            AspectList stalkerAspects = TCEntityAspectAssignments.getEntityAspects(pech);
            passed = passed
                    && mageAspects != null
                    && mageAspects.getAmount(Aspect.AVERSION) == 5
                    && stalkerAspects != null
                    && stalkerAspects.getAmount(Aspect.MAGIC) == 5;
        }
        return new Check("Pech legacy contracts", passed, "type/tamed/anger state, attributes, pack size, explicit ender pearl value, trade tier coverage, spawn gates and subtype aspects");
    }

    private static Check checkRegisteredType(String legacyId, EntityType<?> type) {
        LegacyEntitySpec spec = TCEntityTypes.byLegacyId(legacyId).orElse(null);
        if (spec == null || spec.modernId() == null) {
            return new Check(legacyId + " catalog mapping", false, "missing registered foundation spec");
        }

        ResourceLocation expectedId = ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, spec.modernId());
        ResourceLocation actualId = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        boolean passed = expectedId.equals(actualId);
        return new Check(legacyId + " registry id", passed, "expected " + expectedId + ", got " + actualId);
    }

    private static Check checkTypeShape(String legacyId, EntityType<?> type, int trackingRange, int updateInterval, boolean velocityUpdates) {
        return checkTypeShape(legacyId, type, trackingRange, updateInterval, velocityUpdates, 0.25F, 0.25F);
    }

    private static Check checkTypeShape(String legacyId, EntityType<?> type, int trackingRange, int updateInterval, boolean velocityUpdates, float width, float height) {
        boolean passed = type.getCategory() == MobCategory.MISC
                && Float.compare(type.getWidth(), width) == 0
                && Float.compare(type.getHeight(), height) == 0
                && type.clientTrackingRange() == trackingRange
                && type.updateInterval() == updateInterval
                && type.trackDeltas() == velocityUpdates;
        String notes = "category=" + type.getCategory()
                + ", size=" + type.getWidth() + "x" + type.getHeight()
                + ", tracking=" + type.clientTrackingRange()
                + ", update=" + type.updateInterval()
                + ", velocity=" + type.trackDeltas();
        return new Check(legacyId + " type parameters", passed, notes);
    }

    private static Check checkMobTypeShape(String legacyId, EntityType<?> type, float width, float height, int trackingRange, int updateInterval, boolean velocityUpdates) {
        boolean passed = type.getCategory() == MobCategory.MONSTER
                && Float.compare(type.getWidth(), width) == 0
                && Float.compare(type.getHeight(), height) == 0
                && type.clientTrackingRange() == trackingRange
                && type.updateInterval() == updateInterval
                && type.trackDeltas() == velocityUpdates;
        String notes = "category=" + type.getCategory()
                + ", size=" + type.getWidth() + "x" + type.getHeight()
                + ", tracking=" + type.clientTrackingRange()
                + ", update=" + type.updateInterval()
                + ", velocity=" + type.trackDeltas();
        return new Check(legacyId + " mob type parameters", passed, notes);
    }

    private static Check checkConstructors(ServerLevel level) {
        Entity specialFactory = TCEntityTypes.SPECIAL_ITEM.get().create(level);
        Entity followingFactory = TCEntityTypes.FOLLOW_ITEM.get().create(level);
        Entity alumentumFactory = TCEntityTypes.ALUMENTUM.get().create(level);
        Entity causalityFactory = TCEntityTypes.CAUSALITY_COLLAPSER.get().create(level);
        Entity orbFactory = TCEntityTypes.ELDRITCH_ORB.get().create(level);
        Entity golemOrbFactory = TCEntityTypes.GOLEM_ORB.get().create(level);
        Entity portalFactory = TCEntityTypes.CULTIST_PORTAL_LESSER.get().create(level);
        Entity knightFactory = TCEntityTypes.CULTIST_KNIGHT.get().create(level);
        Entity clericFactory = TCEntityTypes.CULTIST_CLERIC.get().create(level);
        Entity spiderFactory = TCEntityTypes.MIND_SPIDER.get().create(level);
        Entity guardianFactory = TCEntityTypes.ELDRITCH_GUARDIAN.get().create(level);
        Entity firebatFactory = TCEntityTypes.FIREBAT.get().create(level);
        Entity brainyFactory = TCEntityTypes.BRAINY_ZOMBIE.get().create(level);
        Entity giantBrainyFactory = TCEntityTypes.GIANT_BRAINY_ZOMBIE.get().create(level);
        TCSpecialItemEntity special = new TCSpecialItemEntity(level, 1.0D, 2.0D, 3.0D, new ItemStack(Items.DIAMOND));
        TCFollowingItemEntity following = new TCFollowingItemEntity(level, 1.0D, 2.0D, 3.0D, new ItemStack(Items.EMERALD), 4.0D, 5.0D, 6.0D);
        boolean passed = specialFactory instanceof TCSpecialItemEntity
                && followingFactory instanceof TCFollowingItemEntity
                && alumentumFactory instanceof TCAlumentumEntity
                && causalityFactory instanceof TCCausalityCollapserEntity
                && orbFactory instanceof TCEldritchOrbEntity
                && golemOrbFactory instanceof TCGolemOrbEntity
                && portalFactory instanceof TCCultistPortalLesserEntity
                && knightFactory instanceof TCCultistKnightEntity
                && clericFactory instanceof TCCultistClericEntity
                && spiderFactory instanceof TCMindSpiderEntity
                && guardianFactory instanceof TCEldritchGuardianEntity
                && firebatFactory instanceof TCFirebatEntity
                && brainyFactory instanceof TCBrainyZombieEntity
                && giantBrainyFactory instanceof TCGiantBrainyZombieEntity
                && special.getType() == TCEntityTypes.SPECIAL_ITEM.get()
                && following.getType() == TCEntityTypes.FOLLOW_ITEM.get()
                && special.getItem().is(Items.DIAMOND)
                && following.getItem().is(Items.EMERALD)
                && Double.compare(following.targetX(), 4.0D) == 0
                && Double.compare(following.targetY(), 5.0D) == 0
                && Double.compare(following.targetZ(), 6.0D) == 0
                && following.followType() == 3;
        return new Check("foundation constructors", passed, "registry factories plus special/follow item stack and target coordinates");
    }

    public record Report(int passed, int failed) {
    }

    private record Check(String name, boolean passed, String notes) {
    }

    private static String escape(String value) {
        return value.replace("|", "\\|");
    }

    private static boolean close(double actual, double expected) {
        return Math.abs(actual - expected) < 0.0001D;
    }
}

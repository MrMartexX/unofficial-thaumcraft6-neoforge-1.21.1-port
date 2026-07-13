package thaumcraft.common.entities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import thaumcraft.Thaumcraft;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCEntityTypes.LegacyEntitySpec;

public final class TCEntityFoundationAudit {
    public static final String ENABLE_PROPERTY = "tc.entityFoundationAudit";
    public static final String OUTPUT_PROPERTY = "tc.entityFoundationAuditPath";

    private static final int LEGACY_ENTITY_COUNT = 43;
    private static final int REGISTERED_FOUNDATION_COUNT = 21;

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
                "expected item entities, BottleTaint, EldritchOrb, GolemOrb, FluxRift, ArcaneBore, FallingTaint, Wisp, TaintSeed pair, five taint mob foundations, cultist minion pair and warp outcome foundations"
        ));

        checks.add(checkRegisteredType("SpecialItem", TCEntityTypes.SPECIAL_ITEM.get()));
        checks.add(checkRegisteredType("FollowItem", TCEntityTypes.FOLLOW_ITEM.get()));
        checks.add(checkRegisteredType("FluxRift", TCEntityTypes.FLUX_RIFT.get()));
        checks.add(checkRegisteredType("ArcaneBore", TCEntityTypes.ARCANE_BORE.get()));
        checks.add(checkRegisteredType("FallingTaint", TCEntityTypes.FALLING_TAINT.get()));
        checks.add(checkRegisteredType("Wisp", TCEntityTypes.WISP.get()));
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
        checks.add(checkRegisteredType("BottleTaint", TCEntityTypes.BOTTLE_TAINT.get()));
        checks.add(checkRegisteredType("EldritchOrb", TCEntityTypes.ELDRITCH_ORB.get()));
        checks.add(checkRegisteredType("GolemOrb", TCEntityTypes.GOLEM_ORB.get()));
        checks.add(checkTypeShape("SpecialItem", TCEntityTypes.SPECIAL_ITEM.get(), 64, 20, true));
        checks.add(checkTypeShape("FollowItem", TCEntityTypes.FOLLOW_ITEM.get(), 64, 20, false));
        checks.add(checkTypeShape("FallingTaint", TCEntityTypes.FALLING_TAINT.get(), 64, 3, true, 0.98F, 0.98F));
        checks.add(checkMobTypeShape("Wisp", TCEntityTypes.WISP.get(), 0.9F, 0.9F, 64, 3, false));
        checks.add(checkMobTypeShape("ThaumSlime", TCEntityTypes.THAUM_SLIME.get(), 2.04F, 2.04F, 64, 3, true));
        checks.add(checkMobTypeShape("TaintCrawler", TCEntityTypes.TAINT_CRAWLER.get(), 0.5F, 0.4F, 64, 3, true));
        checks.add(checkMobTypeShape("Taintacle", TCEntityTypes.TAINTACLE.get(), 0.8F, 3.0F, 64, 3, false));
        checks.add(checkMobTypeShape("TaintacleTiny", TCEntityTypes.TAINTACLE_TINY.get(), 0.22F, 1.0F, 64, 3, false));
        checks.add(checkMobTypeShape("TaintSwarm", TCEntityTypes.TAINT_SWARM.get(), 2.0F, 2.0F, 64, 3, false));
        checks.add(checkTypeShape("BottleTaint", TCEntityTypes.BOTTLE_TAINT.get(), 64, 20, true));
        checks.add(checkTypeShape("EldritchOrb", TCEntityTypes.ELDRITCH_ORB.get(), 64, 20, true));
        checks.add(checkTypeShape("GolemOrb", TCEntityTypes.GOLEM_ORB.get(), 64, 3, true));
        checks.add(checkMobTypeShape("CultistPortalLesser", TCEntityTypes.CULTIST_PORTAL_LESSER.get(), 1.5F, 3.0F, 64, 20, false));
        checks.add(checkMobTypeShape("CultistKnight", TCEntityTypes.CULTIST_KNIGHT.get(), 0.6F, 1.8F, 64, 3, true));
        checks.add(checkMobTypeShape("CultistCleric", TCEntityTypes.CULTIST_CLERIC.get(), 0.6F, 1.8F, 64, 3, true));
        checks.add(checkMobTypeShape("MindSpider", TCEntityTypes.MIND_SPIDER.get(), 0.7F, 0.5F, 64, 3, true));
        checks.add(checkMobTypeShape("EldritchGuardian", TCEntityTypes.ELDRITCH_GUARDIAN.get(), 0.8F, 2.25F, 64, 3, true));
        checks.add(checkConstructors(server.overworld()));
        return checks;
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
        Entity orbFactory = TCEntityTypes.ELDRITCH_ORB.get().create(level);
        Entity golemOrbFactory = TCEntityTypes.GOLEM_ORB.get().create(level);
        Entity portalFactory = TCEntityTypes.CULTIST_PORTAL_LESSER.get().create(level);
        Entity knightFactory = TCEntityTypes.CULTIST_KNIGHT.get().create(level);
        Entity clericFactory = TCEntityTypes.CULTIST_CLERIC.get().create(level);
        Entity spiderFactory = TCEntityTypes.MIND_SPIDER.get().create(level);
        Entity guardianFactory = TCEntityTypes.ELDRITCH_GUARDIAN.get().create(level);
        TCSpecialItemEntity special = new TCSpecialItemEntity(level, 1.0D, 2.0D, 3.0D, new ItemStack(Items.DIAMOND));
        TCFollowingItemEntity following = new TCFollowingItemEntity(level, 1.0D, 2.0D, 3.0D, new ItemStack(Items.EMERALD), 4.0D, 5.0D, 6.0D);
        boolean passed = specialFactory instanceof TCSpecialItemEntity
                && followingFactory instanceof TCFollowingItemEntity
                && orbFactory instanceof TCEldritchOrbEntity
                && golemOrbFactory instanceof TCGolemOrbEntity
                && portalFactory instanceof TCCultistPortalLesserEntity
                && knightFactory instanceof TCCultistKnightEntity
                && clericFactory instanceof TCCultistClericEntity
                && spiderFactory instanceof TCMindSpiderEntity
                && guardianFactory instanceof TCEldritchGuardianEntity
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
}

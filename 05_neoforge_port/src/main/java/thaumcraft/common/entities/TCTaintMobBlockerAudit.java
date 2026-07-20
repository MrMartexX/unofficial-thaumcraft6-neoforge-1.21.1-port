package thaumcraft.common.entities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.aspects.TCEntityAspectAssignments;
import thaumcraft.common.blocks.world.taint.TCTaintFeatureBlock;
import thaumcraft.common.blocks.world.taint.TCTaintHelper;
import thaumcraft.common.blocks.world.taint.TCTaintTerrainBlock;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCEntityTypes;

public final class TCTaintMobBlockerAudit {
    public static final String ENABLE_PROPERTY = "tc.taintMobBlockerAudit";
    public static final String OUTPUT_PROPERTY = "tc.taintMobBlockerAuditPath";

    private TCTaintMobBlockerAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        if (output.getParent() != null) {
            Files.createDirectories(output.getParent());
        }
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Taint Mob and Thaumic Slime Blocker Audit");
        lines.add("");
        lines.add("Runtime checks for the row-11/14 taint mob foundation slice: TC6 entity registrations,");
        lines.add("server-side taint ecology hooks, scan/aspect identities and safe client renderer registration.");
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
        lines.add("- Implemented: `thaum_slime`, `taint_crawler`, `taintacle`, `taintacle_tiny`, `taintacle_giant` and `taint_swarm` entity types with TC6 tracking/update/velocity values.");
        lines.add("- Implemented: server-side foundations for crawler fibre trail/Flux Taint bite, feature break crawler spawn, geyser swarm spawn, taintacle tiny spawn/lifetime, swarm summoned NBT and Thaumic Slime ranged split.");
        lines.add("- Implemented: legacy scan keys for custom taint mobs and exact ConfigAspects assignments where legacy provided explicit entity tags, including TaintSeed/TaintSeedPrime and Giant Taintacle.");
        lines.add("- Covered separately: FallingTaint crust physics is covered by TCFallingTaintBlockerAudit. Deferred: measured mob model/animation renderer pixel parity, full taint swarm particle renderer and broad natural spawn placement tables.");
        Files.write(output, lines, StandardCharsets.UTF_8);
        return report;
    }

    public static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        BlockPos origin = level.getSharedSpawnPos().offset(224, 8, 224);
        cleanup(level, origin);
        TCTaintHelper.clearForValidation(level);
        addRegistrationChecks(checks);
        addAttributeChecks(level, checks);
        addAspectAndScanChecks(checks);
        addRuntimeHookChecks(level, origin, checks);
        cleanup(level, origin);
        TCTaintHelper.clearForValidation(level);
        return new Report(List.copyOf(checks));
    }

    private static void addRegistrationChecks(ArrayList<Check> checks) {
        checks.add(check("thaum_slime_registered_with_legacy_tracking",
                entityId(TCEntityTypes.THAUM_SLIME.get()).equals(id("thaum_slime"))
                        && legacySpecRegistered("ThaumSlime", "thaum_slime", 64, 3, true)
                        && typeShape(TCEntityTypes.THAUM_SLIME.get(), MobCategory.MONSTER, 2.04F, 2.04F, 64, 3, true),
                "entity=" + entityId(TCEntityTypes.THAUM_SLIME.get())));
        checks.add(check("taint_crawler_registered_with_legacy_tracking",
                entityId(TCEntityTypes.TAINT_CRAWLER.get()).equals(id("taint_crawler"))
                        && legacySpecRegistered("TaintCrawler", "taint_crawler", 64, 3, true)
                        && typeShape(TCEntityTypes.TAINT_CRAWLER.get(), MobCategory.MONSTER, 0.5F, 0.4F, 64, 3, true),
                "entity=" + entityId(TCEntityTypes.TAINT_CRAWLER.get())));
        checks.add(check("taintacle_registered_with_legacy_tracking",
                entityId(TCEntityTypes.TAINTACLE.get()).equals(id("taintacle"))
                        && legacySpecRegistered("Taintacle", "taintacle", 64, 3, false)
                        && typeShape(TCEntityTypes.TAINTACLE.get(), MobCategory.MONSTER, 0.8F, 3.0F, 64, 3, false),
                "entity=" + entityId(TCEntityTypes.TAINTACLE.get())));
        checks.add(check("taintacle_tiny_registered_with_legacy_tracking",
                entityId(TCEntityTypes.TAINTACLE_TINY.get()).equals(id("taintacle_tiny"))
                        && legacySpecRegistered("TaintacleTiny", "taintacle_tiny", 64, 3, false)
                        && typeShape(TCEntityTypes.TAINTACLE_TINY.get(), MobCategory.MONSTER, 0.22F, 1.0F, 64, 3, false),
                "entity=" + entityId(TCEntityTypes.TAINTACLE_TINY.get())));
        checks.add(check("taintacle_giant_registered_with_legacy_tracking",
                entityId(TCEntityTypes.TAINTACLE_GIANT.get()).equals(id("taintacle_giant"))
                        && legacySpecRegistered("TaintacleGiant", "taintacle_giant", 96, 3, false)
                        && typeShape(TCEntityTypes.TAINTACLE_GIANT.get(), MobCategory.MONSTER, 1.1F, 6.0F, 96, 3, false),
                "entity=" + entityId(TCEntityTypes.TAINTACLE_GIANT.get())));
        checks.add(check("taint_swarm_registered_with_legacy_tracking",
                entityId(TCEntityTypes.TAINT_SWARM.get()).equals(id("taint_swarm"))
                        && legacySpecRegistered("TaintSwarm", "taint_swarm", 64, 3, false)
                        && typeShape(TCEntityTypes.TAINT_SWARM.get(), MobCategory.MONSTER, 2.0F, 2.0F, 64, 3, false),
                "entity=" + entityId(TCEntityTypes.TAINT_SWARM.get())));
    }

    private static void addAttributeChecks(ServerLevel level, ArrayList<Check> checks) {
        TCTaintCrawlerEntity crawler = TCEntityTypes.TAINT_CRAWLER.get().create(level);
        TCTaintacleEntity taintacle = TCEntityTypes.TAINTACLE.get().create(level);
        TCTaintacleTinyEntity tiny = TCEntityTypes.TAINTACLE_TINY.get().create(level);
        TCTaintacleGiantEntity giant = TCEntityTypes.TAINTACLE_GIANT.get().create(level);
        TCTaintSwarmEntity swarm = TCEntityTypes.TAINT_SWARM.get().create(level);
        TCThaumicSlimeEntity slime = TCEntityTypes.THAUM_SLIME.get().create(level);
        checks.add(check("taint_mob_attribute_baselines_match_legacy",
                crawler != null
                        && taintacle != null
                        && tiny != null
                        && giant != null
                        && swarm != null
                        && slime != null
                        && close(crawler.getAttributeBaseValue(Attributes.MAX_HEALTH), 8.0D)
                        && close(crawler.getAttributeBaseValue(Attributes.MOVEMENT_SPEED), 0.275D)
                        && close(crawler.getAttributeBaseValue(Attributes.ATTACK_DAMAGE), 2.0D)
                        && close(taintacle.getAttributeBaseValue(Attributes.MAX_HEALTH), 50.0D)
                        && close(taintacle.getAttributeBaseValue(Attributes.ATTACK_DAMAGE), 7.0D)
                        && close(tiny.getAttributeBaseValue(Attributes.MAX_HEALTH), 5.0D)
                        && close(tiny.getAttributeBaseValue(Attributes.ATTACK_DAMAGE), 2.0D)
                        && close(giant.getAttributeBaseValue(Attributes.MAX_HEALTH), TCTaintacleGiantEntity.LEGACY_MAX_HEALTH)
                        && close(giant.getAttributeBaseValue(Attributes.ATTACK_DAMAGE), TCTaintacleGiantEntity.LEGACY_ATTACK_DAMAGE)
                        && close(swarm.getAttributeBaseValue(Attributes.MAX_HEALTH), 30.0D)
                        && close(swarm.getAttributeBaseValue(Attributes.ATTACK_DAMAGE), 2.0D),
                "crawler/tentacle/tiny/swarm base attributes"));
    }

    private static void addAspectAndScanChecks(ArrayList<Check> checks) {
        checks.add(check("custom_taint_entity_aspects_match_legacy_explicit_assignments",
                hasAspects(TCEntityTypes.THAUM_SLIME.get(), Aspect.LIFE, 5, Aspect.WATER, 5, Aspect.FLUX, 5, Aspect.ALCHEMY, 5)
                        && hasAspects(TCEntityTypes.TAINT_SEED.get(), Aspect.PLANT, 20, Aspect.BEAST, 20, Aspect.FLUX, 20)
                        && hasAspects(TCEntityTypes.TAINT_SEED_PRIME.get(), Aspect.PLANT, 30, Aspect.BEAST, 30, Aspect.FLUX, 30)
                        && TCEntityAspectAssignments.getEntityTypeAspectsForValidation(TCEntityTypes.TAINT_CRAWLER.get()) == null
                        && hasAspects(TCEntityTypes.TAINTACLE.get(), Aspect.FLUX, 15, Aspect.BEAST, 10)
                        && hasAspects(TCEntityTypes.TAINTACLE_TINY.get(), Aspect.FLUX, 5, Aspect.BEAST, 5)
                        && hasAspects(TCEntityTypes.TAINTACLE_GIANT.get(), Aspect.ELDRITCH, 40, Aspect.BEAST, 40, Aspect.FLUX, 40)
                        && hasAspects(TCEntityTypes.TAINT_SWARM.get(), Aspect.FLUX, 15, Aspect.AIR, 5),
                "crawler intentionally has no explicit ConfigAspects assignment in TC6 source"));
        String scannables = resourceText("data/thaumcraft/scannables/legacy_core.json");
        checks.add(check("custom_taint_entity_scan_keys_match_legacy",
                scannables.contains("\"!ThaumSlime\"")
                        && scannables.contains("\"thaumcraft:thaum_slime\"")
                        && scannables.contains("\"!TaintCrawler\"")
                        && scannables.contains("\"thaumcraft:taint_crawler\"")
                        && scannables.contains("\"!TaintSeed\"")
                        && scannables.contains("\"thaumcraft:taint_seed_prime\"")
                        && scannables.contains("\"!Taintacle\"")
                        && scannables.contains("\"thaumcraft:taintacle_tiny\"")
                        && scannables.contains("\"thaumcraft:taintacle_giant\"")
                        && scannables.contains("\"!TaintSwarm\"")
                        && scannables.contains("\"f_FLY\"")
                        && scannables.contains("\"thaumcraft:taint_swarm\""),
                "legacy scan keys plus TaintSwarm f_FLY fact"));
    }

    private static void addRuntimeHookChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        level.setBlock(origin.below(), Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
        TCTaintCrawlerEntity crawler = TCEntityTypes.TAINT_CRAWLER.get().create(level);
        if (crawler != null) {
            crawler.moveTo(origin.getX() + 0.5D, origin.getY(), origin.getZ() + 0.5D);
        }
        boolean fibreTrail = crawler != null
                && crawler.placeCrawlerFibreForValidation()
                && level.getBlockState(origin).is(TCBlocks.TAINT_FIBRE.get());
        checks.add(check("taint_crawler_places_legacy_surface_fibre_trail",
                fibreTrail,
                "state=" + BuiltInRegistries.BLOCK.getKey(level.getBlockState(origin).getBlock())));

        BlockPos featurePos = origin.offset(4, 0, 0);
        int crawlersBefore = level.getEntitiesOfClass(TCTaintCrawlerEntity.class, around(featurePos, 8.0D)).size();
        boolean featureSpawned = TCTaintFeatureBlock.spawnCrawlerForValidation(level, featurePos);
        int crawlersAfter = level.getEntitiesOfClass(TCTaintCrawlerEntity.class, around(featurePos, 8.0D)).size();
        checks.add(check("taint_feature_break_hook_spawns_taint_crawler",
                featureSpawned && crawlersAfter == crawlersBefore + 1,
                "before=" + crawlersBefore + ", after=" + crawlersAfter));

        BlockPos geyserPos = origin.offset(8, 0, 0);
        int swarmsBefore = level.getEntitiesOfClass(TCTaintSwarmEntity.class, around(geyserPos, 8.0D)).size();
        boolean swarmSpawned = TCTaintTerrainBlock.trySpawnSwarmForValidation(level, geyserPos);
        int swarmsAfter = level.getEntitiesOfClass(TCTaintSwarmEntity.class, around(geyserPos, 8.0D)).size();
        checks.add(check("taint_geyser_spawn_hook_creates_taint_swarm",
                swarmSpawned && swarmsAfter == swarmsBefore + 1,
                "before=" + swarmsBefore + ", after=" + swarmsAfter));

        BlockPos taintTarget = origin.offset(12, 0, 0);
        level.setBlock(taintTarget.below(), TCBlocks.TAINT_SOIL.get().defaultBlockState(), Block.UPDATE_ALL);
        TCTaintacleEntity taintacle = TCEntityTypes.TAINTACLE.get().create(level);
        if (taintacle != null) {
            taintacle.moveTo(taintTarget.getX() + 0.5D, taintTarget.getY(), taintTarget.getZ() + 0.5D);
        }
        var fakePlayer = FakePlayerFactory.getMinecraft(level);
        BlockPos targetPos = taintTarget.offset(2, 0, 0);
        level.setBlock(targetPos.below(), TCBlocks.TAINT_SOIL.get().defaultBlockState(), Block.UPDATE_ALL);
        fakePlayer.moveTo(targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D);
        int tinyBefore = level.getEntitiesOfClass(TCTaintacleTinyEntity.class, around(taintTarget, 8.0D)).size();
        boolean tinySpawned = taintacle != null && taintacle.spawnTentacleNearForValidation(fakePlayer);
        int tinyAfter = level.getEntitiesOfClass(TCTaintacleTinyEntity.class, around(taintTarget, 8.0D)).size();
        checks.add(check("taintacle_spawns_tiny_taintacle_only_on_taint_substrate",
                taintacle != null && taintacle.isOnLegacyTaint() && tinySpawned && tinyAfter == tinyBefore + 1,
                "tinyBefore=" + tinyBefore + ", tinyAfter=" + tinyAfter));

        TCTaintSwarmEntity swarm = TCEntityTypes.TAINT_SWARM.get().create(level);
        CompoundTag tag = new CompoundTag();
        if (swarm != null) {
            swarm.setIsSummoned(true);
            swarm.setDamageBonusForValidation(3);
            swarm.addAdditionalSaveData(tag);
        }
        TCTaintSwarmEntity restored = TCEntityTypes.TAINT_SWARM.get().create(level);
        if (restored != null) {
            restored.readAdditionalSaveData(tag);
        }
        checks.add(check("taint_swarm_summoned_and_damage_bonus_nbt_roundtrip",
                restored != null && restored.getIsSummoned() && restored.damageBonusForValidation() == 3,
                "tag=" + tag));

        TCThaumicSlimeEntity slime = TCEntityTypes.THAUM_SLIME.get().create(level);
        if (slime != null) {
            slime.setSize(4, true);
        }
        checks.add(check("thaumic_slime_size_controls_xp_like_legacy",
                slime != null && slime.getSize() == 4 && slime.xpRewardForValidation() == 6,
                "size=" + (slime == null ? "null" : slime.getSize())
                        + ", xp=" + (slime == null ? "null" : slime.xpRewardForValidation())));
    }

    private static boolean legacySpecRegistered(String legacyId, String modernId, int range, int interval, boolean velocity) {
        return TCEntityTypes.byLegacyId(legacyId)
                .filter(spec -> spec.isRegisteredFoundation()
                        && modernId.equals(spec.modernId())
                        && spec.trackingRange() == range
                        && spec.updateInterval() == interval
                        && spec.velocityUpdates() == velocity)
                .isPresent();
    }

    private static boolean typeShape(EntityType<?> type, MobCategory category, float width, float height, int range, int interval, boolean velocity) {
        return type.getCategory() == category
                && Float.compare(type.getWidth(), width) == 0
                && Float.compare(type.getHeight(), height) == 0
                && type.clientTrackingRange() == range
                && type.updateInterval() == interval
                && type.trackDeltas() == velocity;
    }

    private static boolean hasAspects(EntityType<?> type, Object... pairs) {
        AspectList aspects = TCEntityAspectAssignments.getEntityTypeAspectsForValidation(type);
        if (aspects == null || aspects.size() != pairs.length / 2) {
            return false;
        }
        for (int i = 0; i < pairs.length; i += 2) {
            if (aspects.getAmount((Aspect) pairs[i]) != (Integer) pairs[i + 1]) {
                return false;
            }
        }
        return true;
    }

    private static net.minecraft.world.phys.AABB around(BlockPos pos, double inflate) {
        return new net.minecraft.world.phys.AABB(pos).inflate(inflate);
    }

    private static void cleanup(ServerLevel level, BlockPos origin) {
        BlockPos.betweenClosed(origin.offset(-4, -2, -4), origin.offset(20, 8, 4)).forEach(pos ->
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
        level.getEntitiesOfClass(Entity.class, around(origin.offset(8, 0, 0), 32.0D), entity ->
                        entity.getType() == TCEntityTypes.THAUM_SLIME.get()
                                || entity.getType() == TCEntityTypes.TAINT_CRAWLER.get()
                                || entity.getType() == TCEntityTypes.TAINTACLE.get()
                                || entity.getType() == TCEntityTypes.TAINTACLE_TINY.get()
                                || entity.getType() == TCEntityTypes.TAINTACLE_GIANT.get()
                                || entity.getType() == TCEntityTypes.TAINT_SWARM.get())
                .forEach(Entity::discard);
    }

    private static ResourceLocation entityId(EntityType<?> type) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(type);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path);
    }

    private static boolean close(double actual, double expected) {
        return Math.abs(actual - expected) < 0.0001D;
    }

    private static String resourceText(String path) {
        try (InputStream stream = TCTaintMobBlockerAudit.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return "";
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            return "";
        }
    }

    public record Report(List<Check> checks) {
        public int passed() {
            return (int) checks.stream().filter(Check::passed).count();
        }

        public int failed() {
            return checks.size() - passed();
        }
    }

    private record Check(String name, boolean passed, String notes) {
    }

    private static Check check(String name, boolean passed, String notes) {
        return new Check(name, passed, notes);
    }
}

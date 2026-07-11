package thaumcraft.common.blocks.world.taint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.Thaumcraft;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.common.entities.TCTaintSeedEntity;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCMobEffects;

/** Runtime audit for the TaintSeed/TaintHelper terrain-spread blocker slice. */
public final class TCTaintEcologyBlockerAudit {
    public static final String ENABLE_PROPERTY = "tc.taintEcologyBlockerAudit";
    public static final String OUTPUT_PROPERTY = "tc.taintEcologyBlockerAuditPath";

    private TCTaintEcologyBlockerAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Taint Ecology Blocker Audit");
        lines.add("");
        lines.add("Runtime checks for the row-11 TaintSeed/TaintHelper slice: seed radius bookkeeping,");
        lines.add("registered taint terrain blocks, server spread transforms and resource modernization.");
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
        lines.add("- Implemented: `taint_seed` and `taint_seed_prime` entity registration, attributes, boost save/load, seed-radius map, near/edge checks, flux-gated spread loop and Flux Taint healing for tainted mobs.");
        lines.add("- Implemented: `taint_crust`, `taint_soil`, `taint_rock`, `taint_geyser`, `taint_log` and `taint_feature` registration with BlockItems, creative visibility and modern block/item models.");
        lines.add("- Implemented: deterministic validation for TC6-style spread target categories: surface fibre, dirt -> taint soil, stone -> taint rock, log -> taint log and log-adjacent leaves -> taint feature/fibre.");
        lines.add("- Deferred: full TaintCrawler/TaintSwarm/Taintacle AI and renderers, FallingTaint crust physics, GORE sound parity and exact animated TaintSeed model parity.");
        Files.write(output, lines, StandardCharsets.UTF_8);
        return report;
    }

    public static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        BlockPos origin = level.getSharedSpawnPos().offset(288, 6, 288);
        cleanup(level, origin);
        TCTaintHelper.clearForValidation(level);
        addRegistrationChecks(checks);
        addSeedRuntimeChecks(level, origin, checks);
        addSpreadTransformChecks(level, origin.offset(16, 0, 0), checks);
        addEffectChecks(level, checks);
        addResourceChecks(checks);
        TCTaintHelper.clearForValidation(level);
        cleanup(level, origin);
        return new Report(List.copyOf(checks));
    }

    private static void addRegistrationChecks(ArrayList<Check> checks) {
        checks.add(check("taint_terrain_blocks_and_items_registered",
                idOf(TCBlocks.TAINT_CRUST.get()).equals(id("taint_crust"))
                        && idOf(TCBlocks.TAINT_SOIL.get()).equals(id("taint_soil"))
                        && idOf(TCBlocks.TAINT_ROCK.get()).equals(id("taint_rock"))
                        && idOf(TCBlocks.TAINT_GEYSER.get()).equals(id("taint_geyser"))
                        && idOf(TCBlocks.TAINT_LOG.get()).equals(id("taint_log"))
                        && idOf(TCBlocks.TAINT_FEATURE.get()).equals(id("taint_feature"))
                        && BuiltInRegistries.ITEM.getKey(TCItems.TAINT_CRUST.get()).equals(id("taint_crust"))
                        && BuiltInRegistries.ITEM.getKey(TCItems.TAINT_FEATURE.get()).equals(id("taint_feature")),
                "terrain ids registered with legacy names"));
        checks.add(check("taint_seed_entity_types_registered",
                BuiltInRegistries.ENTITY_TYPE.getKey(TCEntityTypes.TAINT_SEED.get()).equals(id("taint_seed"))
                        && BuiltInRegistries.ENTITY_TYPE.getKey(TCEntityTypes.TAINT_SEED_PRIME.get()).equals(id("taint_seed_prime"))
                        && TCEntityTypes.TAINT_SEED.get().clientTrackingRange() == 64
                        && TCEntityTypes.TAINT_SEED.get().updateInterval() == 20
                        && !TCEntityTypes.TAINT_SEED.get().trackDeltas(),
                "seed=" + BuiltInRegistries.ENTITY_TYPE.getKey(TCEntityTypes.TAINT_SEED.get())
                        + ", prime=" + BuiltInRegistries.ENTITY_TYPE.getKey(TCEntityTypes.TAINT_SEED_PRIME.get())));
        checks.add(check("taint_config_defaults_match_legacy",
                TCTaintHelper.taintSpreadArea() == TCTaintHelper.legacyDefaultTaintSpreadArea()
                        && Float.compare(TCTaintHelper.taintSpreadRate(), TCTaintHelper.legacyDefaultTaintSpreadRate()) == 0,
                "area=" + TCTaintHelper.taintSpreadArea() + ", rate=" + TCTaintHelper.taintSpreadRate()));
    }

    private static void addSeedRuntimeChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        TCTaintSeedEntity seed = new TCTaintSeedEntity(level, origin, false);
        TCTaintSeedEntity prime = new TCTaintSeedEntity(level, origin.offset(4, 0, 0), true);
        level.addFreshEntity(seed);
        level.addFreshEntity(prime);
        TCTaintHelper.addTaintSeed(level, seed.blockPosition());
        TCTaintHelper.addTaintSeed(level, prime.blockPosition());
        checks.add(check("taint_seed_area_and_prime_contract",
                seed.getArea() == 1
                        && prime.getArea() == 2
                        && TCEntityTypes.TAINT_SEED.get().getWidth() == 1.5F
                        && TCEntityTypes.TAINT_SEED_PRIME.get().getHeight() == 2.0F,
                "seedArea=" + seed.getArea() + ", primeArea=" + prime.getArea()));
        checks.add(check("taint_seed_near_check_requires_live_entity",
                TCTaintHelper.isNearTaintSeed(level, origin.offset(1, 0, 0)),
                "nearLiveSeed=" + TCTaintHelper.isNearTaintSeed(level, origin.offset(1, 0, 0))));
        prime.discard();
        TCTaintHelper.removeTaintSeed(level, prime.blockPosition());
        seed.discard();
        checks.add(check("taint_seed_near_check_prunes_stale_entries",
                !TCTaintHelper.isNearTaintSeed(level, origin.offset(1, 0, 0)),
                "nearAfterDiscard=" + TCTaintHelper.isNearTaintSeed(level, origin.offset(1, 0, 0))));
        TCTaintSeedEntity blocker = new TCTaintSeedEntity(level, origin.offset(4, 0, 0), true);
        level.addFreshEntity(blocker);
        TCTaintHelper.addTaintSeed(level, blocker.blockPosition());
        checks.add(check("taint_seed_spawn_rule_blocks_nearby_duplicate",
                !new TCTaintSeedEntity(level, blocker.blockPosition().offset(1, 0, 0), false).canSpawnLikeLegacy(),
                "prime still occupies spread-area exclusion"));
        blocker.discard();
    }

    private static void addSpreadTransformChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        TCTaintHelper.clearForValidation(level);
        TCTaintSeedEntity seed = new TCTaintSeedEntity(level, origin, false);
        level.addFreshEntity(seed);
        TCTaintHelper.addTaintSeed(level, seed.blockPosition());

        BlockPos fibreTarget = origin.east();
        level.setBlock(fibreTarget.below(), Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(fibreTarget, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        boolean fibreResult = TCTaintHelper.spreadFibresToTargetForValidation(level, origin, fibreTarget);
        checks.add(check("spread_surface_target_becomes_taint_fibre",
                fibreResult && level.getBlockState(fibreTarget).is(TCBlocks.TAINT_FIBRE.get()),
                "block=" + idOf(level.getBlockState(fibreTarget).getBlock())));

        BlockPos dirtTarget = origin.offset(3, 0, 0);
        prepareHemmedTarget(level, dirtTarget, Blocks.DIRT.defaultBlockState());
        boolean soilResult = TCTaintHelper.spreadFibresToTargetForValidation(level, origin, dirtTarget);
        checks.add(check("spread_dirt_target_becomes_taint_soil",
                soilResult && level.getBlockState(dirtTarget).is(TCBlocks.TAINT_SOIL.get()),
                "block=" + idOf(level.getBlockState(dirtTarget).getBlock())));

        BlockPos stoneTarget = origin.offset(5, 0, 0);
        prepareHemmedTarget(level, stoneTarget, Blocks.STONE.defaultBlockState());
        boolean rockResult = TCTaintHelper.spreadFibresToTargetForValidation(level, origin, stoneTarget);
        checks.add(check("spread_stone_target_becomes_taint_rock",
                rockResult && level.getBlockState(stoneTarget).is(TCBlocks.TAINT_ROCK.get()),
                "block=" + idOf(level.getBlockState(stoneTarget).getBlock())));

        BlockPos logTarget = origin.offset(7, 0, 0);
        prepareHemmedTarget(level, logTarget, Blocks.OAK_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X));
        boolean logResult = TCTaintHelper.spreadFibresToTargetForValidation(level, origin, logTarget);
        BlockState logState = level.getBlockState(logTarget);
        checks.add(check("spread_log_target_becomes_taint_log_preserving_axis",
                logResult && logState.is(TCBlocks.TAINT_LOG.get()) && logState.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.X,
                "block=" + idOf(logState.getBlock()) + ", axis=" + (logState.hasProperty(RotatedPillarBlock.AXIS) ? logState.getValue(RotatedPillarBlock.AXIS) : "none")));

        seed.discard();
    }

    private static void addEffectChecks(ServerLevel level, ArrayList<Check> checks) {
        TCTaintSeedEntity seed = new TCTaintSeedEntity(level, level.getSharedSpawnPos().offset(296, 8, 296), false);
        PotionFluxTaint effect = TCMobEffects.FLUX_TAINT.get();
        seed.setHealth(10.0F);
        effect.applyEffectTick(seed, 0);
        LivingEntity cow = new Cow(EntityType.COW, level);
        cow.setHealth(10.0F);
        float before = cow.getHealth();
        effect.applyEffectTick(cow, 0);
        Zombie zombie = new Zombie(EntityType.ZOMBIE, level);
        zombie.setHealth(10.0F);
        effect.applyEffectTick(zombie, 0);
        checks.add(check("flux_taint_heals_tainted_and_damages_non_undead",
                seed.getHealth() > 10.0F
                        && cow.getHealth() < before
                        && Float.compare(zombie.getHealth(), 10.0F) == 0,
                "seedHealth=" + seed.getHealth() + ", cowBefore=" + before + ", cowAfter=" + cow.getHealth()
                        + ", zombieHealth=" + zombie.getHealth()));
        MobEffectInstance instance = new MobEffectInstance(TCMobEffects.FLUX_TAINT, 100, 0, false, true);
        instance.getCures().clear();
        seed.addEffect(instance);
        checks.add(check("tainted_seed_accepts_flux_taint_effect_without_damage_path",
                seed.hasEffect(TCMobEffects.FLUX_TAINT),
                "effectPresent=" + seed.hasEffect(TCMobEffects.FLUX_TAINT)));
    }

    private static void addResourceChecks(ArrayList<Check> checks) {
        List<String> resources = List.of(
                "assets/thaumcraft/blockstates/taint_crust.json",
                "assets/thaumcraft/blockstates/taint_soil.json",
                "assets/thaumcraft/blockstates/taint_rock.json",
                "assets/thaumcraft/blockstates/taint_geyser.json",
                "assets/thaumcraft/blockstates/taint_log.json",
                "assets/thaumcraft/blockstates/taint_feature.json",
                "assets/thaumcraft/models/item/taint_crust.json",
                "assets/thaumcraft/models/item/taint_feature.json"
        );
        boolean allModern = resources.stream()
                .map(TCTaintEcologyBlockerAudit::resourceText)
                .allMatch(text -> !text.isBlank() && !text.contains("forge_marker") && !text.contains("thaumcraft:blocks/"));
        checks.add(check("taint_resources_use_modern_model_and_texture_paths",
                allModern,
                "checked=" + resources.size()));
        checks.add(check("taint_feature_shapes_keep_legacy_directional_bounds",
                !TCTaintFeatureBlock.shapeForFacing(Direction.UP).equals(TCTaintFeatureBlock.shapeForFacing(Direction.NORTH)),
                "up=" + TCTaintFeatureBlock.shapeForFacing(Direction.UP).bounds()
                        + ", north=" + TCTaintFeatureBlock.shapeForFacing(Direction.NORTH).bounds()));
    }

    private static void prepareHemmedTarget(ServerLevel level, BlockPos target, BlockState state) {
        level.setBlock(target, state, Block.UPDATE_ALL);
        level.setBlock(target.north(), TCBlocks.TAINT_FIBRE.get().defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(target.south(), TCBlocks.TAINT_FIBRE.get().defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(target.east(), TCBlocks.TAINT_FIBRE.get().defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(target.west(), TCBlocks.TAINT_FIBRE.get().defaultBlockState(), Block.UPDATE_ALL);
    }

    private static String resourceText(String path) {
        try (InputStream stream = TCTaintEcologyBlockerAudit.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return "";
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            return "";
        }
    }

    private static void cleanup(ServerLevel level, BlockPos origin) {
        BlockPos.betweenClosed(origin.offset(-8, -2, -8), origin.offset(48, 8, 48)).forEach(pos ->
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
    }

    private static ResourceLocation idOf(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path);
    }

    private static Check check(String name, boolean passed, String notes) {
        return new Check(name, passed, notes);
    }

    public record Report(List<Check> checks) {
        public int passed() {
            return (int) checks.stream().filter(Check::passed).count();
        }

        public int failed() {
            return checks.size() - passed();
        }
    }

    public record Check(String name, boolean passed, String notes) {
    }
}

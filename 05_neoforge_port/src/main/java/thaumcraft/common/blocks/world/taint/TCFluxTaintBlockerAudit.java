package thaumcraft.common.blocks.world.taint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import thaumcraft.Thaumcraft;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCMobEffects;

public final class TCFluxTaintBlockerAudit {
    private static final double EPSILON = 0.0001D;

    private TCFluxTaintBlockerAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Flux Goo and Taint Fibre Blocker Audit");
        lines.add("");
        lines.add("Runtime checks for the row-11 finite taint world-mutation slice: Flux Goo level-zero");
        lines.add("decay can now produce Taint Fibre, and Taint Fibre keeps the TC6 state, shape and effect contract.");
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
        lines.add("- Implemented: Taint Fibre registration, block item, creative visibility, 10-property legacy state, deterministic growth, face/growth shapes, light levels, walk taint and Flux Goo level-zero alternate result.");
        lines.add("- Implemented: resource path modernization for Taint Fibre multipart blockstate and growth models.");
        lines.add("- Deferred from this earlier Flux Goo/Taint Fibre audit: later TaintSeed/terrain ecology and taint mob server-foundation hooks are covered by their focused audits; FallingTaint crust physics and final taint visuals remain separate blockers.");
        lines.add("- Until TaintSeed exists, Taint Fibre intentionally withers on random tick just like legacy fibres outside seed range.");
        Files.write(output, lines);
        return report;
    }

    public static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        BlockPos origin = level.getSharedSpawnPos().offset(224, 6, 224);
        cleanup(level, origin);
        addRegistrationChecks(checks);
        addStateAndShapeChecks(level, origin, checks);
        addFluxGooTransitionChecks(level, origin.offset(8, 0, 0), checks);
        addEffectAndBoundaryChecks(level, origin.offset(16, 0, 0), checks);
        addResourceChecks(checks);
        cleanup(level, origin);
        return new Report(List.copyOf(checks));
    }

    private static void addRegistrationChecks(ArrayList<Check> checks) {
        checks.add(check("taint_fibre_block_and_item_registered_with_legacy_id",
                blockId(TCBlocks.TAINT_FIBRE.get()).equals(id("taint_fibre"))
                        && BuiltInRegistries.ITEM.getKey(TCItems.TAINT_FIBRE.get()).equals(id("taint_fibre")),
                "block=" + blockId(TCBlocks.TAINT_FIBRE.get())
                        + ", item=" + BuiltInRegistries.ITEM.getKey(TCItems.TAINT_FIBRE.get())));
        checks.add(check("flux_goo_block_item_registered_for_world_slice",
                BuiltInRegistries.ITEM.getKey(TCItems.FLUX_GOO.get()).equals(id("flux_goo")),
                "item=" + BuiltInRegistries.ITEM.getKey(TCItems.FLUX_GOO.get())));
        BlockState state = TCBlocks.TAINT_FIBRE.get().defaultBlockState();
        List<BooleanProperty> properties = List.of(
                TCTaintFibreBlock.NORTH,
                TCTaintFibreBlock.EAST,
                TCTaintFibreBlock.SOUTH,
                TCTaintFibreBlock.WEST,
                TCTaintFibreBlock.UP,
                TCTaintFibreBlock.DOWN,
                TCTaintFibreBlock.GROWTH1,
                TCTaintFibreBlock.GROWTH2,
                TCTaintFibreBlock.GROWTH3,
                TCTaintFibreBlock.GROWTH4
        );
        boolean defaultFalse = properties.stream().allMatch(property -> state.hasProperty(property) && !state.getValue(property));
        checks.add(check("taint_fibre_default_state_has_all_legacy_false_properties",
                defaultFalse,
                "propertyCount=" + properties.size() + ", defaultFalse=" + defaultFalse));
    }

    private static void addStateAndShapeChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        BlockState allFaces = TCBlocks.TAINT_FIBRE.get().defaultBlockState()
                .setValue(TCTaintFibreBlock.UP, true)
                .setValue(TCTaintFibreBlock.DOWN, true)
                .setValue(TCTaintFibreBlock.NORTH, true)
                .setValue(TCTaintFibreBlock.SOUTH, true)
                .setValue(TCTaintFibreBlock.WEST, true)
                .setValue(TCTaintFibreBlock.EAST, true);
        VoxelShape faceShape = TCTaintFibreBlock.shapeForState(allFaces);
        checks.add(check("taint_fibre_face_shapes_use_legacy_0_05_block_thickness",
                !faceShape.isEmpty()
                        && boundsClose(TCTaintFibreBlock.shapeForState(stateWith(TCTaintFibreBlock.UP)), 0.0D, 0.95D, 0.0D, 1.0D, 1.0D, 1.0D)
                        && boundsClose(TCTaintFibreBlock.shapeForState(stateWith(TCTaintFibreBlock.DOWN)), 0.0D, 0.0D, 0.0D, 1.0D, 0.05D, 1.0D)
                        && boundsClose(TCTaintFibreBlock.shapeForState(stateWith(TCTaintFibreBlock.EAST)), 0.95D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)
                        && boundsClose(TCTaintFibreBlock.shapeForState(stateWith(TCTaintFibreBlock.NORTH)), 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.05D),
                "allFacesBounds=" + faceShape.bounds()));

        checks.add(check("taint_fibre_growth_shapes_and_light_match_legacy",
                boundsClose(TCTaintFibreBlock.shapeForState(stateWith(TCTaintFibreBlock.GROWTH1)), 0.1D, 0.0D, 0.1D, 0.9D, 0.4D, 0.9D)
                        && boundsClose(TCTaintFibreBlock.shapeForState(stateWith(TCTaintFibreBlock.GROWTH2)), 0.2D, 0.0D, 0.2D, 0.8D, 1.0D, 0.8D)
                        && boundsClose(TCTaintFibreBlock.shapeForState(stateWith(TCTaintFibreBlock.GROWTH3)), 0.25D, 0.0D, 0.25D, 0.75D, 0.3125D, 0.75D)
                        && boundsClose(TCTaintFibreBlock.shapeForState(stateWith(TCTaintFibreBlock.GROWTH4)), 0.1D, 0.3D, 0.1D, 0.9D, 1.0D, 0.9D)
                        && TCTaintFibreBlock.lightForState(stateWith(TCTaintFibreBlock.GROWTH3)) == 12
                        && TCTaintFibreBlock.lightForState(stateWith(TCTaintFibreBlock.GROWTH2)) == 6
                        && TCTaintFibreBlock.lightForState(stateWith(TCTaintFibreBlock.GROWTH4)) == 6,
                "growth1=" + TCTaintFibreBlock.shapeForState(stateWith(TCTaintFibreBlock.GROWTH1)).bounds()));

        BlockPos growthPos = findLegacyGrowthPos(origin, 3, true);
        level.setBlock(growthPos.below(), Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(growthPos, TCBlocks.TAINT_FIBRE.get().defaultBlockState(), Block.UPDATE_ALL);
        BlockState actual = ((TCTaintFibreBlock) TCBlocks.TAINT_FIBRE.get()).stateForWorld(level, growthPos);
        checks.add(check("taint_fibre_actual_state_uses_pos_seeded_growth_and_support_faces",
                actual.getValue(TCTaintFibreBlock.DOWN)
                        && actual.getValue(TCTaintFibreBlock.GROWTH3)
                        && !actual.getValue(TCTaintFibreBlock.UP),
                "pos=" + growthPos + ", q=" + new Random(growthPos.asLong()).nextInt(50)
                        + ", down=" + actual.getValue(TCTaintFibreBlock.DOWN)
                        + ", growth3=" + actual.getValue(TCTaintFibreBlock.GROWTH3)));
    }

    private static void addFluxGooTransitionChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        level.setBlock(origin.below(), Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
        TCFluxGooBlock fluxGoo = (TCFluxGooBlock) TCBlocks.FLUX_GOO.get();
        BlockState fibre = fluxGoo.taintFibreState(level, origin);
        checks.add(check("flux_goo_level_zero_alternate_result_now_resolves_taint_fibre",
                fibre.is(TCBlocks.TAINT_FIBRE.get())
                        && fibre.getValue(TCTaintFibreBlock.DOWN),
                "block=" + blockId(fibre.getBlock())
                        + ", down=" + fibre.getValue(TCTaintFibreBlock.DOWN)));
        level.setBlock(origin, fibre, Block.UPDATE_ALL);
        checks.add(check("taint_fibre_replacement_is_non_full_collision_shape",
                !level.getBlockState(origin).getCollisionShape(level, origin).isEmpty()
                        && !level.getBlockState(origin).getCollisionShape(level, origin).bounds().equals(new AABB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)),
                "bounds=" + level.getBlockState(origin).getCollisionShape(level, origin).bounds()));
    }

    private static void addEffectAndBoundaryChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        var fakePlayer = FakePlayerFactory.getMinecraft(level);
        boolean applied = TCTaintFibreBlock.applyWalkTaintForValidation(fakePlayer);
        Zombie zombie = new Zombie(EntityType.ZOMBIE, level);
        boolean undeadApplied = TCTaintFibreBlock.applyWalkTaintForValidation(zombie);
        checks.add(check("taint_fibre_walk_applies_flux_taint_to_living_non_undead_only",
                applied
                        && fakePlayer.hasEffect(TCMobEffects.FLUX_TAINT)
                        && !undeadApplied
                        && !zombie.hasEffect(TCMobEffects.FLUX_TAINT),
                "playerApplied=" + applied + ", undeadApplied=" + undeadApplied));

        checks.add(check("taint_seed_dependent_spread_is_explicitly_deferred",
                !TCTaintFibreBlock.isNearTaintSeed(level, origin),
                "nearSeed=" + TCTaintFibreBlock.isNearTaintSeed(level, origin)
                        + ", deferred=TaintSeed registry/entity and full TaintHelper spread"));
    }

    private static void addResourceChecks(ArrayList<Check> checks) {
        String blockstate = resourceText("assets/thaumcraft/blockstates/taint_fibre.json");
        String blockModel = resourceText("assets/thaumcraft/models/block/taint_fibre.json");
        String itemModel = resourceText("assets/thaumcraft/models/item/taint_fibre.json");
        String fluxGooItemModel = resourceText("assets/thaumcraft/models/item/flux_goo.json");
        checks.add(check("taint_fibre_blockstate_uses_modern_block_model_paths",
                blockstate.contains("thaumcraft:block/taint_fibre")
                        && blockstate.contains("thaumcraft:block/taint_growth_1")
                        && !blockstate.contains("\"thaumcraft:taint_fibre\"")
                        && !blockstate.contains("\"thaumcraft:taint_growth_"),
                "blockstateLength=" + blockstate.length()));
        checks.add(check("taint_fibre_models_use_modern_block_texture_paths",
                blockModel.contains("thaumcraft:block/taint_fibres")
                        && itemModel.contains("thaumcraft:block/taint_fibres")
                        && !blockModel.contains("thaumcraft:blocks/")
                        && !itemModel.contains("thaumcraft:blocks/"),
                "blockModelLength=" + blockModel.length() + ", itemModelLength=" + itemModel.length()));
        checks.add(check("flux_goo_block_item_has_modern_item_model",
                fluxGooItemModel.contains("thaumcraft:block/flux_goo")
                        && !fluxGooItemModel.contains("thaumcraft:blocks/"),
                "itemModelLength=" + fluxGooItemModel.length()));
    }

    private static BlockState stateWith(BooleanProperty property) {
        return TCBlocks.TAINT_FIBRE.get().defaultBlockState().setValue(property, true);
    }

    private static boolean boundsClose(VoxelShape shape, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (shape.isEmpty()) {
            return false;
        }
        AABB bounds = shape.bounds();
        return close(bounds.minX, minX)
                && close(bounds.minY, minY)
                && close(bounds.minZ, minZ)
                && close(bounds.maxX, maxX)
                && close(bounds.maxY, maxY)
                && close(bounds.maxZ, maxZ);
    }

    private static BlockPos findLegacyGrowthPos(BlockPos origin, int growth, boolean floorSupport) {
        for (int x = 0; x < 64; x++) {
            for (int z = 0; z < 64; z++) {
                BlockPos pos = origin.offset(x, 0, z);
                int q = new Random(pos.asLong()).nextInt(50);
                if (floorSupport && growth == 3 && q == 6) {
                    return pos;
                }
            }
        }
        return origin;
    }

    private static String resourceText(String path) {
        try (InputStream stream = TCFluxTaintBlockerAudit.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return "";
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            return "";
        }
    }

    private static void cleanup(ServerLevel level, BlockPos origin) {
        BlockPos.betweenClosed(origin.offset(-4, -2, -4), origin.offset(96, 8, 96)).forEach(pos ->
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
    }

    private static boolean close(double actual, double expected) {
        return Math.abs(actual - expected) < EPSILON;
    }

    private static ResourceLocation blockId(Block block) {
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

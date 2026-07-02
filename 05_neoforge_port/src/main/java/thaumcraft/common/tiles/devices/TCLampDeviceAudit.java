package thaumcraft.common.tiles.devices;

import java.io.IOException;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blocks.devices.TCLampBlock;
import thaumcraft.common.essentia.transport.TCEssentiaCapabilities;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.tiles.essentia.TCWardedJarBlockEntity;

public final class TCLampDeviceAudit {
    private TCLampDeviceAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Lamp Device Audit");
        lines.add("");
        lines.add("Runtime checks for the TC6 Arcane Lamp, Lamp of Growth and Lamp of Fertility blocker slice.");
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
        lines.add("- Covers server-side device identity, blockstate/light/shape contracts and first gameplay behavior for the three legacy lamps.");
        lines.add("- Uses legacy ids `lamp_arcane`, `lamp_growth`, `lamp_fertility` and keeps `arcanelamp` as the research recipe/page id only.");
        lines.add("- Final pixel/model comparison, colored light illusion and advanced crop blacklist/inter-mod behavior remain visual/integration follow-ups.");
        Files.write(output, lines);
        return report;
    }

    public static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        BlockPos origin = level.getSharedSpawnPos().offset(76, 4, 76);
        cleanup(level, origin);
        addRegistrationChecks(checks);
        addShapeAndLightChecks(level, origin, checks);
        addArcaneLampChecks(level, origin.offset(0, 0, 0), checks);
        addGrowthLampChecks(level, origin.offset(12, 0, 0), checks);
        addFertilityLampChecks(level, origin.offset(24, 0, 0), checks);
        cleanup(level, origin);
        return new Report(List.copyOf(checks));
    }

    private static void addRegistrationChecks(ArrayList<Check> checks) {
        checks.add(check("legacy_lamp_blocks_registered",
                TCBlocks.LAMP_ARCANE.get() instanceof TCLampBlock arcane
                        && arcane.kind() == TCLampBlock.Kind.ARCANE
                        && TCBlocks.LAMP_GROWTH.get() instanceof TCLampBlock growth
                        && growth.kind() == TCLampBlock.Kind.GROWTH
                        && TCBlocks.LAMP_FERTILITY.get() instanceof TCLampBlock fertility
                        && fertility.kind() == TCLampBlock.Kind.FERTILITY,
                "blocks=lamp_arcane,lamp_growth,lamp_fertility"));
        checks.add(check("legacy_lamp_items_are_block_items",
                TCItems.LAMP_ARCANE.get() instanceof BlockItem
                        && TCItems.LAMP_GROWTH.get() instanceof BlockItem
                        && TCItems.LAMP_FERTILITY.get() instanceof BlockItem,
                "items=" + TCItems.LAMP_ARCANE.get().getClass().getSimpleName()
                        + "," + TCItems.LAMP_GROWTH.get().getClass().getSimpleName()
                        + "," + TCItems.LAMP_FERTILITY.get().getClass().getSimpleName()));
        checks.add(check("effect_glimmer_is_light_block_without_item",
                BuiltInRegistries.BLOCK.getKey(TCBlocks.EFFECT_GLIMMER.get()).equals(id("effect_glimmer"))
                        && BuiltInRegistries.ITEM.getOptional(id("effect_glimmer")).isEmpty(),
                "block=" + BuiltInRegistries.BLOCK.getKey(TCBlocks.EFFECT_GLIMMER.get())));
    }

    private static void addShapeAndLightChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        BlockState on = TCBlocks.LAMP_ARCANE.get().defaultBlockState()
                .setValue(TCLampBlock.FACING, Direction.DOWN)
                .setValue(TCLampBlock.ENABLED, true);
        BlockState off = on.setValue(TCLampBlock.ENABLED, false);
        AABB bounds = on.getShape(level, origin).bounds();
        checks.add(check("lamp_shape_matches_legacy_aabb",
                sameBounds(bounds, 4, 2, 4, 12, 14, 12),
                "bounds=" + bounds));
        checks.add(check("lamp_enabled_light_matches_legacy",
                on.getLightEmission() == 15 && off.getLightEmission() == 0,
                "on=" + on.getLightEmission() + ", off=" + off.getLightEmission()));
    }

    private static void addArcaneLampChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        BlockState lampState = TCBlocks.LAMP_ARCANE.get().defaultBlockState()
                .setValue(TCLampBlock.FACING, Direction.DOWN)
                .setValue(TCLampBlock.ENABLED, true);
        level.setBlock(pos, lampState, Block.UPDATE_ALL);
        TCLampBlockEntity lamp = lampAt(level, pos);
        BlockPos glimmer = pos.offset(1, 1, 0);
        boolean placed = lamp != null && lamp.tryPlaceGlimmerAt(glimmer);
        BlockPos placedGlimmer = findGlimmer(level, pos);
        boolean isGlimmer = placedGlimmer != null;
        if (lamp != null) {
            lamp.removeGlimmers();
        }
        boolean removed = placedGlimmer != null && level.getBlockState(placedGlimmer).isAir();
        checks.add(check("arcane_lamp_places_and_removes_glimmer",
                placed && isGlimmer && removed,
                "placed=" + placed + ", glimmerBeforeRemove=" + isGlimmer
                        + ", placedAt=" + (placedGlimmer == null ? "missing" : placedGlimmer)
                        + ", removed=" + removed));
    }

    private static void addGrowthLampChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        BlockPos sourcePos = pos.below();
        level.setBlock(sourcePos, TCBlocks.JAR_NORMAL.get().defaultBlockState(), Block.UPDATE_ALL);
        TCWardedJarBlockEntity jar = level.getBlockEntity(sourcePos) instanceof TCWardedJarBlockEntity wardedJar
                ? wardedJar : null;
        if (jar != null) {
            jar.addEssentia(Aspect.PLANT.getTag(), 2, Direction.UP, false);
        }
        BlockState lampState = TCBlocks.LAMP_GROWTH.get().defaultBlockState()
                .setValue(TCLampBlock.FACING, Direction.DOWN)
                .setValue(TCLampBlock.ENABLED, false);
        BlockPos powerPos = pos.north();
        level.setBlock(powerPos, Blocks.REDSTONE_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(pos, lampState, Block.UPDATE_ALL);
        TCLampBlockEntity lamp = lampAt(level, pos);
        TCEssentiaTransport capability = level.getCapability(TCEssentiaCapabilities.BLOCK, pos, Direction.DOWN);
        for (int i = 0; i < 5 && lamp != null; i++) {
            TCLampBlockEntity.serverTick(level, pos, lamp.getBlockState(), lamp);
        }
        checks.add(check("growth_lamp_draws_herba_like_legacy",
                lamp != null
                        && jar != null
                        && capability != null
                        && capability.getSuction(Direction.DOWN).amount() == 128
                        && capability.getSuction(Direction.DOWN).aspect().equals(Aspect.PLANT.getTag())
                        && lamp.charges() == 20
                        && jar.storedAspects().getAmount(Aspect.PLANT) == 1,
                "charges=" + (lamp == null ? "missing" : lamp.charges())
                        + ", jarHerba=" + (jar == null ? "missing" : jar.storedAspects().getAmount(Aspect.PLANT))));
        level.removeBlock(powerPos, false);

        BlockPos cropPos = pos.offset(1, 0, 0);
        level.setBlock(cropPos.below(), Blocks.FARMLAND.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(cropPos, Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 0), Block.UPDATE_ALL);
        if (lamp != null) {
            lamp.setCharges(3);
        }
        boolean grewTarget = lamp != null && lamp.tryGrowPlantAt(cropPos);
        checks.add(check("growth_lamp_targets_and_ticks_ungrown_plants",
                grewTarget && lamp.charges() == 2 && lamp.lastGrowthTarget().equals(cropPos),
                "grewTarget=" + grewTarget
                        + ", charges=" + (lamp == null ? "missing" : lamp.charges())
                        + ", target=" + (lamp == null ? "missing" : lamp.lastGrowthTarget())));
    }

    private static void addFertilityLampChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        BlockPos sourcePos = pos.below();
        level.setBlock(sourcePos, TCBlocks.JAR_NORMAL.get().defaultBlockState(), Block.UPDATE_ALL);
        TCWardedJarBlockEntity jar = level.getBlockEntity(sourcePos) instanceof TCWardedJarBlockEntity wardedJar
                ? wardedJar : null;
        if (jar != null) {
            jar.addEssentia(Aspect.DESIRE.getTag(), 2, Direction.UP, false);
        }
        BlockState lampState = TCBlocks.LAMP_FERTILITY.get().defaultBlockState()
                .setValue(TCLampBlock.FACING, Direction.DOWN)
                .setValue(TCLampBlock.ENABLED, false);
        level.setBlock(pos, lampState, Block.UPDATE_ALL);
        TCLampBlockEntity lamp = lampAt(level, pos);
        TCEssentiaTransport capability = level.getCapability(TCEssentiaCapabilities.BLOCK, pos, Direction.DOWN);
        for (int i = 0; i < 5 && lamp != null; i++) {
            TCLampBlockEntity.serverTick(level, pos, lamp.getBlockState(), lamp);
        }
        checks.add(check("fertility_lamp_draws_desiderium_like_legacy",
                lamp != null
                        && jar != null
                        && capability != null
                        && capability.getSuction(Direction.DOWN).aspect().equals(Aspect.DESIRE.getTag())
                        && lamp.charges() == 1
                        && jar.storedAspects().getAmount(Aspect.DESIRE) == 1,
                "charges=" + (lamp == null ? "missing" : lamp.charges())
                        + ", jarDesiderium=" + (jar == null ? "missing" : jar.storedAspects().getAmount(Aspect.DESIRE))));

        Cow first = EntityType.COW.create(level);
        Cow second = EntityType.COW.create(level);
        boolean animalsAdded = false;
        if (first != null && second != null && lamp != null) {
            first.moveTo(pos.getX() + 1.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
            second.moveTo(pos.getX() + 2.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
            animalsAdded = level.addFreshEntity(first) && level.addFreshEntity(second);
            lamp.setCharges(6);
            lamp.updateAnimals();
        }
        checks.add(check("fertility_lamp_sets_two_adult_animals_in_love",
                animalsAdded
                        && first != null
                        && second != null
                        && first.isInLove()
                        && second.isInLove()
                        && lamp != null
                        && lamp.charges() == 1,
                "animalsAdded=" + animalsAdded
                        + ", firstLove=" + (first != null && first.isInLove())
                        + ", secondLove=" + (second != null && second.isInLove())
                        + ", charges=" + (lamp == null ? "missing" : lamp.charges())));
        if (first != null) {
            first.discard();
        }
        if (second != null) {
            second.discard();
        }
    }

    private static TCLampBlockEntity lampAt(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof TCLampBlockEntity lamp ? lamp : null;
    }

    private static BlockPos findGlimmer(ServerLevel level, BlockPos origin) {
        for (BlockPos target : BlockPos.betweenClosed(origin.offset(-15, -15, -15), origin.offset(15, 15, 15))) {
            if (level.getBlockState(target).is(TCBlocks.EFFECT_GLIMMER.get())) {
                return target.immutable();
            }
        }
        return null;
    }

    private static void cleanup(ServerLevel level, BlockPos origin) {
        BlockPos.betweenClosed(origin.offset(-4, -2, -4), origin.offset(34, 8, 8)).forEach(pos ->
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
    }

    private static boolean sameBounds(AABB bounds, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return close(bounds.minX, minX / 16.0D)
                && close(bounds.minY, minY / 16.0D)
                && close(bounds.minZ, minZ / 16.0D)
                && close(bounds.maxX, maxX / 16.0D)
                && close(bounds.maxY, maxY / 16.0D)
                && close(bounds.maxZ, maxZ / 16.0D);
    }

    private static boolean close(double actual, double expected) {
        return Math.abs(actual - expected) < 0.0001D;
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

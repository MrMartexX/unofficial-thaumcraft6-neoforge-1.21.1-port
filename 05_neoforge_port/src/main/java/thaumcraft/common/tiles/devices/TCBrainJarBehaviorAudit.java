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
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.essentia.TCBrainJarBlock;
import thaumcraft.common.items.TCBrainJarBlockItem;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.research.theorycraft.TCTheorycraftAid;
import thaumcraft.common.research.theorycraft.TCTheorycraftManager;

/** Runtime audit for the TC6 Brain-in-a-Jar blocker slice. */
public final class TCBrainJarBehaviorAudit {
    public static final String ENABLE_PROPERTY = "tcBrainJarBehaviorAudit";
    public static final String PATH_PROPERTY = "tcBrainJarBehaviorAuditPath";
    public static final Path DEFAULT_OUTPUT = Path.of(
            "../../06_docs/audits/generated/thaumcraft_1_21_brain_jar_behavior_audit.md"
    );

    private TCBrainJarBehaviorAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Brain-in-a-Jar Behavior Audit");
        lines.add("");
        lines.add("Runtime checks for the TC6 Brain-in-a-Jar XP jar and theorycraft aid blocker slice.");
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
        lines.add("- Covers real block, BlockItem and BlockEntity identities for `jar_brain`.");
        lines.add("- Covers legacy server XP storage, close-orb absorption, 8-block orb pull, right-click release delay, comparator and enchanting bonus.");
        lines.add("- Covers the `AidBrainInAJar` theorycraft aid source for `CardDarkWhispers`.");
        lines.add("- Does not claim final animated brain BER/model rotation, full-client spark pixel parity or the legacy item-warp registry.");
        Files.write(output, lines);
        return report;
    }

    public static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        BlockPos origin = level.getSharedSpawnPos().offset(220, 4, 220);
        cleanup(level, origin);
        addRegistrationChecks(checks);
        addBlockStateChecks(level, origin, checks);
        addXpBehaviorChecks(level, origin.offset(4, 0, 0), checks);
        addTheorycraftChecks(checks);
        cleanup(level, origin);
        return new Report(List.copyOf(checks));
    }

    private static void addRegistrationChecks(ArrayList<Check> checks) {
        checks.add(check("jar_brain_registered_as_real_block_item",
                BuiltInRegistries.BLOCK.getKey(TCBlocks.JAR_BRAIN.get()).equals(id("jar_brain"))
                        && BuiltInRegistries.ITEM.getKey(TCItems.JAR_BRAIN.get()).equals(id("jar_brain"))
                        && TCItems.JAR_BRAIN.get() instanceof BlockItem
                        && ((BlockItem) TCItems.JAR_BRAIN.get()).getBlock() == TCBlocks.JAR_BRAIN.get(),
                "block=" + BuiltInRegistries.BLOCK.getKey(TCBlocks.JAR_BRAIN.get())
                        + ", item=" + BuiltInRegistries.ITEM.getKey(TCItems.JAR_BRAIN.get())
                        + ", itemClass=" + TCItems.JAR_BRAIN.get().getClass().getSimpleName()));
        checks.add(check("jar_brain_block_entity_registered",
                BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(TCBlockEntities.JAR_BRAIN.get()).equals(id("jar_brain")),
                "blockEntity=" + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(TCBlockEntities.JAR_BRAIN.get())));
    }

    private static void addBlockStateChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        BlockState state = TCBlocks.JAR_BRAIN.get().defaultBlockState();
        VoxelShape shape = state.getShape(level, pos);
        checks.add(check("jar_brain_shape_matches_legacy_aabb",
                boundsEqual(shape.bounds(), new AABB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.75D, 0.8125D)),
                "outline=" + shape.bounds()));

        level.setBlock(pos, state, Block.UPDATE_ALL);
        TCBrainJarBlockEntity jar = level.getBlockEntity(pos) instanceof TCBrainJarBlockEntity be ? be : null;
        boolean noAutomationCapabilities = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, Direction.UP) == null
                && level.getCapability(Capabilities.FluidHandler.BLOCK, pos, Direction.UP) == null;
        float enchant = TCBlocks.JAR_BRAIN.get() instanceof TCBrainJarBlock block
                ? block.getEnchantPowerBonus(state, level, pos)
                : 0.0F;
        checks.add(check("jar_brain_entity_capability_enchant_contract",
                jar != null && noAutomationCapabilities && enchant == 5.0F,
                "jar=" + (jar != null) + ", noCaps=" + noAutomationCapabilities + ", enchant=" + enchant));

        if (jar != null) {
            jar.setXpForValidation(1234);
            ItemStack stack = TCBrainJarBlockItem.stackFromJar(jar);
            Integer xp = stack.get(TCDataComponents.BRAIN_JAR_XP.get());
            checks.add(check("jar_brain_item_preserves_xp_payload",
                    stack.is(TCItems.JAR_BRAIN.get()) && xp != null && xp == 1234,
                    "stack=" + BuiltInRegistries.ITEM.getKey(stack.getItem()) + ", xp=" + xp));

            jar.setXpForValidation(0);
            int emptySignal = jar.comparatorSignal();
            jar.setXpForValidation(1000);
            int halfSignal = jar.comparatorSignal();
            jar.setXpForValidation(TCBrainJarBlockEntity.XP_MAX);
            int fullSignal = jar.comparatorSignal();
            checks.add(check("jar_brain_comparator_matches_legacy_formula",
                    emptySignal == 0 && halfSignal == 8 && fullSignal == 15,
                    "signals=" + emptySignal + "/" + halfSignal + "/" + fullSignal));
        }
    }

    private static void addXpBehaviorChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        level.setBlock(pos, TCBlocks.JAR_BRAIN.get().defaultBlockState(), Block.UPDATE_ALL);
        TCBrainJarBlockEntity jar = level.getBlockEntity(pos) instanceof TCBrainJarBlockEntity be ? be : null;
        if (jar == null) {
            checks.add(check("jar_brain_legacy_pull_formula", false, "missing block entity"));
            checks.add(check("jar_brain_absorbs_close_xp_orb", false, "missing block entity"));
            checks.add(check("jar_brain_release_sets_delay_and_spawns_xp", false, "missing block entity"));
            return;
        }

        Vec3 orbPos = Vec3.atCenterOf(pos).add(4.0D, 0.0D, 0.0D);
        Vec3 pull = TCBrainJarBlockEntity.legacyPullDelta(pos, orbPos);
        checks.add(check("jar_brain_legacy_pull_formula",
                Math.abs(pull.x() + 0.21168D) < 0.00001D && Math.abs(pull.y()) < 0.0001D && Math.abs(pull.z()) < 0.0001D,
                "pull=" + pull));

        jar.setXpForValidation(0);
        ExperienceOrb orb = new ExperienceOrb(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 13);
        orb.setDeltaMovement(Vec3.ZERO);
        level.addFreshEntity(orb);
        jar.tickServerForValidation(1);
        checks.add(check("jar_brain_absorbs_close_xp_orb",
                jar.xp() == 13 && orb.isRemoved(),
                "xp=" + jar.xp() + ", orbRemoved=" + orb.isRemoved()));

        jar.setXpForValidation(100);
        jar.setEatDelayForValidation(0);
        jar.releaseExperienceForValidation(37);
        checks.add(check("jar_brain_release_sets_delay_and_spawns_xp",
                jar.xp() == 63 && jar.eatDelay() == TCBrainJarBlockEntity.RELEASE_EAT_DELAY_TICKS,
                "xp=" + jar.xp() + ", eatDelay=" + jar.eatDelay()));
    }

    private static void addTheorycraftChecks(ArrayList<Check> checks) {
        TCTheorycraftManager.bootstrap();
        TCTheorycraftAid aid = TCTheorycraftManager.aids().get(TCTheorycraftManager.AID_BRAIN_IN_A_JAR);
        checks.add(check("aid_brain_in_a_jar_registers_dark_whispers",
                aid != null
                        && aid.matchesBlock(TCBlocks.JAR_BRAIN.get().defaultBlockState())
                        && aid.cardKeys().equals(List.of("thaumcraft.common.lib.research.theorycraft.CardDarkWhispers"))
                        && aid.displayStack().is(TCItems.JAR_BRAIN.get()),
                "aid=" + (aid == null ? "missing" : aid.cardKeys())));
    }

    private static void cleanup(ServerLevel level, BlockPos origin) {
        BlockPos min = origin.offset(-8, -4, -8);
        BlockPos max = origin.offset(16, 8, 16);
        AABB box = new AABB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
        level.getEntitiesOfClass(ExperienceOrb.class, box).forEach(ExperienceOrb::discard);
        BlockPos.betweenClosed(origin.offset(-8, -2, -8), origin.offset(16, 4, 16)).forEach(pos ->
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
    }

    private static boolean boundsEqual(AABB actual, AABB expected) {
        double epsilon = 0.000001D;
        return Math.abs(actual.minX - expected.minX) < epsilon
                && Math.abs(actual.minY - expected.minY) < epsilon
                && Math.abs(actual.minZ - expected.minZ) < epsilon
                && Math.abs(actual.maxX - expected.maxX) < epsilon
                && Math.abs(actual.maxY - expected.maxY) < epsilon
                && Math.abs(actual.maxZ - expected.maxZ) < epsilon;
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path);
    }

    private static Check check(String name, boolean passed, String notes) {
        return new Check(name, passed, notes);
    }

    public record Report(List<Check> checks) {
        public long passed() {
            return checks.stream().filter(Check::passed).count();
        }

        public long failed() {
            return checks.size() - passed();
        }
    }

    public record Check(String name, boolean passed, String notes) {
    }
}

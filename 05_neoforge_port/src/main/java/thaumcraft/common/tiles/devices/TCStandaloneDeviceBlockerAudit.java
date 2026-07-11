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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.devices.TCInfernalFurnaceBlock;
import thaumcraft.common.blocks.devices.TCVoidSiphonBlock;
import thaumcraft.common.crafting.TCSalisMundusActivation;
import thaumcraft.common.items.resources.ItemSalisMundus;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;

public final class TCStandaloneDeviceBlockerAudit {
    private TCStandaloneDeviceBlockerAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Standalone Device Blocker Audit");
        lines.add("");
        lines.add("Runtime checks for row-13 blockers closed after the Infernal Furnace runtime slice.");
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
        lines.add("- Covers Salis Mundus IDustTrigger-style Infernal Furnace multiblock detection, conversion placeholders, furnace rollback and placeholder-triggered teardown.");
        lines.add("- Covers Void Siphon block identity, shape, redstone enabled state, one extract-only slot, GUI progress data, rift-drain math and void-seed output conversion.");
        lines.add("- Does not implement full Flux Rift spawning/lifecycle/rendering; Void Siphon consumes entities that implement the explicit `TCVoidSiphonRiftAccess` adapter.");
        Files.write(output, lines);
        return report;
    }

    public static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        BlockPos origin = level.getSharedSpawnPos().offset(152, 4, 152);
        cleanup(level, origin);
        addRegistrationChecks(checks);
        addSalisMundusInfernalFurnaceChecks(level, origin, checks);
        addVoidSiphonChecks(level, origin.offset(12, 0, 0), checks);
        cleanup(level, origin);
        return new Report(List.copyOf(checks));
    }

    private static void addRegistrationChecks(ArrayList<Check> checks) {
        checks.add(check("salis_mundus_uses_real_dust_item",
                TCItems.SALIS_MUNDUS.get() instanceof ItemSalisMundus,
                "itemClass=" + TCItems.SALIS_MUNDUS.get().getClass().getSimpleName()));
        checks.add(check("void_siphon_block_blockitem_and_blockentity_registered",
                BuiltInRegistries.BLOCK.getKey(TCBlocks.VOID_SIPHON.get()).equals(id("void_siphon"))
                        && TCItems.VOID_SIPHON.get() instanceof BlockItem
                        && ((BlockItem) TCItems.VOID_SIPHON.get()).getBlock() == TCBlocks.VOID_SIPHON.get(),
                "block=" + BuiltInRegistries.BLOCK.getKey(TCBlocks.VOID_SIPHON.get())
                        + ", itemClass=" + TCItems.VOID_SIPHON.get().getClass().getSimpleName()));
    }

    private static void addSalisMundusInfernalFurnaceChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        buildInfernalFurnaceBlueprint(level, origin);
        boolean activated = TCSalisMundusActivation.tryActivateInfernalFurnaceForValidation(level, origin.offset(1, 1, 1));
        BlockPos center = origin.offset(1, 1, 1);
        BlockState furnaceState = level.getBlockState(center);
        Direction output = furnaceState.hasProperty(TCInfernalFurnaceBlock.FACING)
                ? furnaceState.getValue(TCInfernalFurnaceBlock.FACING).getOpposite()
                : Direction.SOUTH;
        checks.add(check("infernal_furnace_dust_blueprint_activates",
                activated
                        && furnaceState.is(TCBlocks.INFERNAL_FURNACE.get())
                        && countBlocks(level, origin, TCBlocks.PLACEHOLDER_NETHER_BRICK.get()) == 12
                        && countBlocks(level, origin, TCBlocks.PLACEHOLDER_OBSIDIAN.get()) == 12
                        && level.getBlockState(center.relative(output)).isAir(),
                "activated=" + activated
                        + ", placeholders="
                        + countBlocks(level, origin, TCBlocks.PLACEHOLDER_NETHER_BRICK.get())
                        + "/"
                        + countBlocks(level, origin, TCBlocks.PLACEHOLDER_OBSIDIAN.get())
                        + ", output=" + output));

        level.setBlock(center, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        checks.add(check("infernal_furnace_break_rolls_structure_back",
                level.getBlockState(center).is(Blocks.LAVA)
                        && countBlocks(level, origin, Blocks.NETHER_BRICKS) == 12
                        && countBlocks(level, origin, Blocks.OBSIDIAN) == 12
                        && level.getBlockState(center.relative(output)).is(Blocks.IRON_BARS),
                "lava=" + level.getBlockState(center).is(Blocks.LAVA)
                        + ", netherBricks=" + countBlocks(level, origin, Blocks.NETHER_BRICKS)
                        + ", obsidian=" + countBlocks(level, origin, Blocks.OBSIDIAN)));

        cleanup(level, origin);
        buildInfernalFurnaceBlueprint(level, origin);
        TCSalisMundusActivation.tryActivateInfernalFurnaceForValidation(level, center);
        BlockPos placeholder = firstPlaceholder(level, origin);
        level.setBlock(placeholder, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        checks.add(check("infernal_placeholder_break_destroys_linked_furnace",
                level.getBlockState(center).is(Blocks.LAVA),
                "brokenPlaceholder=" + placeholder + ", center=" + BuiltInRegistries.BLOCK.getKey(level.getBlockState(center).getBlock())));
    }

    private static void addVoidSiphonChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        BlockState state = TCBlocks.VOID_SIPHON.get().defaultBlockState().setValue(TCVoidSiphonBlock.ENABLED, true);
        VoxelShape shape = state.getShape(level, pos);
        VoxelShape collision = state.getCollisionShape(level, pos);
        checks.add(check("void_siphon_shapes_match_legacy_aabbs",
                boundsEqual(shape.bounds(), new AABB(0.1875D, 0.0D, 0.1875D, 0.8125D, 1.0D, 0.8125D))
                        && collision.toAabbs().size() == 3
                        && hasBox(collision, new AABB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.125D, 0.8125D))
                        && hasBox(collision, new AABB(0.25D, 0.125D, 0.25D, 0.75D, 0.6875D, 0.75D))
                        && hasBox(collision, new AABB(0.3125D, 0.75D, 0.3125D, 0.625D, 1.0D, 0.625D)),
                "outline=" + shape.bounds() + ", collisionBoxes=" + collision.toAabbs().size()));

        level.setBlock(pos, state, Block.UPDATE_ALL);
        TCVoidSiphonBlockEntity siphon = level.getBlockEntity(pos) instanceof TCVoidSiphonBlockEntity be ? be : null;
        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, Direction.UP);
        ItemStack insertRemainder = handler == null ? ItemStack.EMPTY : handler.insertItem(0, new ItemStack(TCItems.VOID_SEED.get()), false);
        checks.add(check("void_siphon_extract_only_slot_contract",
                siphon != null
                        && handler != null
                        && handler.getSlots() == 1
                        && !insertRemainder.isEmpty(),
                "handler=" + (handler == null ? "missing" : handler.getSlots())
                        + ", insertRemainder=" + insertRemainder.getCount()));

        if (siphon == null) {
            checks.add(check("void_siphon_rift_drain_math_matches_legacy", false, "missing block entity"));
            checks.add(check("void_siphon_progress_outputs_void_seed", false, "missing block entity"));
            return;
        }

        FakeRift rift = new FakeRift(new Vec3(pos.getX() + 2.0D, pos.getY() + 1.0D, pos.getZ() + 2.0D), 9, 1.0D);
        int drained = siphon.drainRiftsForValidation(List.of(rift), Boolean.FALSE);
        checks.add(check("void_siphon_rift_drain_math_matches_legacy",
                drained == 3
                        && siphon.progress() == 3
                        && close(rift.stability, 0.8D)
                        && rift.size == 9,
                "drained=" + drained + ", progress=" + siphon.progress()
                        + ", stability=" + rift.stability + ", size=" + rift.size));

        siphon.setProgressForValidation(TCVoidSiphonBlockEntity.PROGRESS_REQUIRED - 1);
        FakeRift small = new FakeRift(new Vec3(pos.getX() + 2.0D, pos.getY() + 1.0D, pos.getZ() + 2.0D), 4, 1.0D);
        siphon.drainRiftsForValidation(List.of(small), Boolean.FALSE);
        checks.add(check("void_siphon_progress_outputs_void_seed",
                siphon.getItem(TCVoidSiphonBlockEntity.SLOT_OUTPUT).is(TCItems.VOID_SEED.get())
                        && siphon.getItem(TCVoidSiphonBlockEntity.SLOT_OUTPUT).getCount() == 1
                        && siphon.progress() == 1,
                "slot=" + BuiltInRegistries.ITEM.getKey(siphon.getItem(TCVoidSiphonBlockEntity.SLOT_OUTPUT).getItem())
                        + "x" + siphon.getItem(TCVoidSiphonBlockEntity.SLOT_OUTPUT).getCount()
                        + ", progress=" + siphon.progress()));
    }

    private static void buildInfernalFurnaceBlueprint(ServerLevel level, BlockPos origin) {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    level.setBlock(origin.offset(x, y, z), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }

        placeLayer(level, origin, 2, new Block[][]{
                {Blocks.NETHER_BRICKS, Blocks.OBSIDIAN, Blocks.NETHER_BRICKS},
                {Blocks.OBSIDIAN, Blocks.AIR, Blocks.OBSIDIAN},
                {Blocks.NETHER_BRICKS, Blocks.OBSIDIAN, Blocks.NETHER_BRICKS}
        });
        level.setBlock(origin.offset(0, 1, 0), Blocks.NETHER_BRICKS.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(origin.offset(1, 1, 0), Blocks.OBSIDIAN.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(origin.offset(2, 1, 0), Blocks.NETHER_BRICKS.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(origin.offset(0, 1, 1), Blocks.OBSIDIAN.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(origin.offset(1, 1, 1), Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(origin.offset(2, 1, 1), Blocks.OBSIDIAN.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(origin.offset(0, 1, 2), Blocks.NETHER_BRICKS.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(origin.offset(1, 1, 2), Blocks.IRON_BARS.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(origin.offset(2, 1, 2), Blocks.NETHER_BRICKS.defaultBlockState(), Block.UPDATE_ALL);
        placeLayer(level, origin, 0, new Block[][]{
                {Blocks.NETHER_BRICKS, Blocks.OBSIDIAN, Blocks.NETHER_BRICKS},
                {Blocks.OBSIDIAN, Blocks.OBSIDIAN, Blocks.OBSIDIAN},
                {Blocks.NETHER_BRICKS, Blocks.OBSIDIAN, Blocks.NETHER_BRICKS}
        });
    }

    private static void placeLayer(ServerLevel level, BlockPos origin, int y, Block[][] blocks) {
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                level.setBlock(origin.offset(x, y, z), blocks[x][z].defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    private static int countBlocks(ServerLevel level, BlockPos origin, Block block) {
        int count = 0;
        for (BlockPos pos : BlockPos.betweenClosed(origin, origin.offset(2, 2, 2))) {
            if (level.getBlockState(pos).is(block)) {
                count++;
            }
        }
        return count;
    }

    private static BlockPos firstPlaceholder(ServerLevel level, BlockPos origin) {
        for (BlockPos pos : BlockPos.betweenClosed(origin, origin.offset(2, 2, 2))) {
            if (level.getBlockState(pos).is(TCBlocks.PLACEHOLDER_NETHER_BRICK.get())
                    || level.getBlockState(pos).is(TCBlocks.PLACEHOLDER_OBSIDIAN.get())) {
                return pos.immutable();
            }
        }
        return origin;
    }

    private static void cleanup(ServerLevel level, BlockPos origin) {
        BlockPos.betweenClosed(origin.offset(-4, -2, -4), origin.offset(20, 8, 8)).forEach(pos ->
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
    }

    private static boolean hasBox(VoxelShape shape, AABB expected) {
        return shape.toAabbs().stream().anyMatch(actual -> boundsEqual(actual, expected));
    }

    private static boolean boundsEqual(AABB actual, AABB expected) {
        return close(actual.minX, expected.minX)
                && close(actual.minY, expected.minY)
                && close(actual.minZ, expected.minZ)
                && close(actual.maxX, expected.maxX)
                && close(actual.maxY, expected.maxY)
                && close(actual.maxZ, expected.maxZ);
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

    private static final class FakeRift implements TCVoidSiphonRiftAccess {
        private final Vec3 pos;
        private int size;
        private double stability;

        private FakeRift(Vec3 pos, int size, double stability) {
            this.pos = pos;
            this.size = size;
            this.stability = stability;
        }

        @Override
        public Vec3 voidSiphonPosition() {
            return pos;
        }

        @Override
        public int voidSiphonRiftSize() {
            return size;
        }

        @Override
        public void voidSiphonSetRiftSize(int size) {
            this.size = size;
        }

        @Override
        public double voidSiphonStability() {
            return stability;
        }

        @Override
        public void voidSiphonSetStability(double stability) {
            this.stability = stability;
        }

        @Override
        public boolean voidSiphonAlive() {
            return true;
        }

        @Override
        public boolean voidSiphonCanBeSeenFrom(net.minecraft.world.level.Level level, Vec3 source) {
            return true;
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

    public record Check(String name, boolean passed, String notes) {
    }
}

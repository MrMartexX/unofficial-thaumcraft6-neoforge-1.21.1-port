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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.devices.TCInfernalFurnaceBlock;
import thaumcraft.common.blocks.essentia.TCBellowsBlock;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.world.aura.AuraHandler;

public final class TCInfernalFurnaceBehaviorAudit {
    private TCInfernalFurnaceBehaviorAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Infernal Furnace Behavior Audit");
        lines.add("");
        lines.add("Runtime checks for the TC6 Infernal Furnace standalone-device blocker slice.");
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
        lines.add("- Covers the active runtime machine: block identity, half-height shape/light, top-only input capability, lava destruction, vanilla smelting lookup, aura-speed drain, legacy bellows distance formula and front ejection.");
        lines.add("- Includes the internal legacy default smelting bonus table and modern flattened bonus output ids for the current port.");
        lines.add("- Does not implement the Salis Mundus multiblock/dust activation blueprint or final in-game pixel parity; those remain separate blocker slices.");
        Files.write(output, lines);
        return report;
    }

    public static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        BlockPos origin = level.getSharedSpawnPos().offset(104, 4, 104);
        cleanup(level, origin);
        addRegistrationChecks(checks);
        addShapeLightAndFacingChecks(level, origin, checks);
        addCapabilityAndInsertionChecks(level, origin.offset(8, 0, 0), checks);
        addAuraAndBellowsChecks(level, origin.offset(16, 0, 0), checks);
        addSmeltingCompletionChecks(level, origin.offset(28, 0, 0), checks);
        cleanup(level, origin);
        return new Report(List.copyOf(checks));
    }

    private static void addRegistrationChecks(ArrayList<Check> checks) {
        checks.add(check("infernal_furnace_block_and_blockitem_registered",
                BuiltInRegistries.BLOCK.getKey(TCBlocks.INFERNAL_FURNACE.get()).equals(id("infernal_furnace"))
                        && TCItems.INFERNAL_FURNACE.get() instanceof BlockItem
                        && ((BlockItem) TCItems.INFERNAL_FURNACE.get()).getBlock() == TCBlocks.INFERNAL_FURNACE.get(),
                "block=" + BuiltInRegistries.BLOCK.getKey(TCBlocks.INFERNAL_FURNACE.get())
                        + ", itemClass=" + TCItems.INFERNAL_FURNACE.get().getClass().getSimpleName()));
        checks.add(check("legacy_bonus_outputs_registered",
                TCItems.COPPER_NUGGET.get() != null
                        && TCItems.TIN_NUGGET.get() != null
                        && TCItems.SILVER_NUGGET.get() != null
                        && TCItems.LEAD_NUGGET.get() != null
                        && TCItems.QUARTZ_NUGGET.get() != null
                        && TCItems.CHUNK_BEEF.get() != null
                        && TCItems.CHUNK_FISH.get() != null,
                "flattened old nugget/chunk metadata outputs are real item ids"));
    }

    private static void addShapeLightAndFacingChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        BlockState state = TCBlocks.INFERNAL_FURNACE.get().defaultBlockState()
                .setValue(TCInfernalFurnaceBlock.FACING, Direction.NORTH);
        AABB bounds = state.getShape(level, pos).bounds();
        checks.add(check("shape_matches_legacy_half_height_box",
                close(bounds.minX, 0.0D)
                        && close(bounds.minY, 0.0D)
                        && close(bounds.minZ, 0.0D)
                        && close(bounds.maxX, 1.0D)
                        && close(bounds.maxY, 0.5D)
                        && close(bounds.maxZ, 1.0D),
                "bounds=" + bounds));
        checks.add(check("light_level_matches_legacy_point_nine",
                state.getLightEmission() == 14,
                "light=" + state.getLightEmission()));
        checks.add(check("output_direction_is_facing_opposite",
                state.getValue(TCInfernalFurnaceBlock.FACING).getOpposite() == Direction.SOUTH,
                "facing=" + state.getValue(TCInfernalFurnaceBlock.FACING)));
    }

    private static void addCapabilityAndInsertionChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        placeFurnace(level, pos);
        TCInfernalFurnaceBlockEntity furnace = furnaceAt(level, pos);
        IItemHandler top = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, Direction.UP);
        IItemHandler side = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, Direction.EAST);
        ItemStack topRemainder = top == null ? new ItemStack(Items.COBBLESTONE) : top.insertItem(0, new ItemStack(Items.COBBLESTONE), false);
        ItemStack sideRemainder = side == null ? ItemStack.EMPTY : side.insertItem(0, new ItemStack(Items.COBBLESTONE), false);
        checks.add(check("top_only_input_capability_matches_legacy",
                top != null
                        && side != null
                        && top.getSlots() == TCInfernalFurnaceBlockEntity.SLOT_COUNT
                        && side.getSlots() == 0
                        && topRemainder.isEmpty()
                        && !sideRemainder.isEmpty()
                        && furnace != null
                        && furnace.getStoredItemForValidation(0).is(Items.COBBLESTONE),
                "topSlots=" + (top == null ? "missing" : top.getSlots())
                        + ", sideSlots=" + (side == null ? "missing" : side.getSlots())
                        + ", sideRemainder=" + sideRemainder.getCount()));

        if (furnace != null) {
            furnace.clearCountersForValidation();
            ItemStack destroyed = furnace.addItemsToInventory(new ItemStack(Items.STICK));
            checks.add(check("non_smeltable_items_are_destroyed_like_lava",
                    destroyed.isEmpty() && furnace.destroyedItems() == 1,
                    "destroyedItems=" + furnace.destroyedItems()
                            + ", remainder=" + destroyed.getCount()));
        } else {
            checks.add(check("non_smeltable_items_are_destroyed_like_lava", false, "missing block entity"));
        }
    }

    private static void addAuraAndBellowsChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        placeFurnace(level, pos);
        TCInfernalFurnaceBlockEntity furnace = furnaceAt(level, pos);
        if (furnace == null) {
            checks.add(check("aura_speedy_drain_sets_20_ticks", false, "missing block entity"));
            checks.add(check("bellows_distance_two_formula_matches_legacy", false, "missing block entity"));
            return;
        }
        AuraHandler.seedAuraChunk(level, pos, 100);
        float before = AuraHandler.getVis(level, pos);
        furnace.setMachineStateForValidation(0, 0, 0);
        TCInfernalFurnaceBlockEntity.serverTick(level, pos, furnace.getBlockState(), furnace);
        float after = AuraHandler.getVis(level, pos);
        checks.add(check("aura_speedy_drain_sets_20_ticks",
                furnace.speedyTime() == 20 && Math.abs(before - after - 20.0F) < 0.01F,
                "before=" + before + ", after=" + after + ", speedy=" + furnace.speedyTime()));

        level.setBlock(pos.relative(Direction.NORTH, 2), TCBlocks.BELLOWS.get().defaultBlockState()
                .setValue(TCBellowsBlock.FACING, Direction.SOUTH)
                .setValue(TCBellowsBlock.ENABLED, true), Block.UPDATE_ALL);
        level.setBlock(pos.relative(Direction.EAST, 2), TCBlocks.BELLOWS.get().defaultBlockState()
                .setValue(TCBellowsBlock.FACING, Direction.WEST)
                .setValue(TCBellowsBlock.ENABLED, true), Block.UPDATE_ALL);
        furnace.setMachineStateForValidation(0, 0, 0);
        checks.add(check("bellows_distance_two_formula_matches_legacy",
                furnace.bellowsForValidation() == 2
                        && furnace.calcCookTimeForValidation() == 102
                        && TCInfernalFurnaceBlockEntity.calcCookTimeForValidation(false, 4) == 72
                        && TCInfernalFurnaceBlockEntity.calcCookTimeForValidation(true, 4) == 12,
                "bellows=" + furnace.bellowsForValidation()
                        + ", cookNoSpeed=" + furnace.calcCookTimeForValidation()
                        + ", cook4=" + TCInfernalFurnaceBlockEntity.calcCookTimeForValidation(false, 4)
                        + ", speedy4=" + TCInfernalFurnaceBlockEntity.calcCookTimeForValidation(true, 4)));
    }

    private static void addSmeltingCompletionChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        placeFurnace(level, pos);
        TCInfernalFurnaceBlockEntity furnace = furnaceAt(level, pos);
        if (furnace == null) {
            checks.add(check("vanilla_smelting_recipe_lookup_active", false, "missing block entity"));
            checks.add(check("cook_completion_ejects_front_and_consumes_input", false, "missing block entity"));
            checks.add(check("bonus_table_has_legacy_meat_and_ore_candidates", false, "missing block entity"));
            return;
        }
        ItemStack rawIron = new ItemStack(Items.RAW_IRON);
        ItemStack smeltingResult = furnace.smeltingResultForValidation(rawIron);
        checks.add(check("vanilla_smelting_recipe_lookup_active",
                smeltingResult.is(Items.IRON_INGOT),
                "rawIronResult=" + BuiltInRegistries.ITEM.getKey(smeltingResult.getItem())));

        furnace.clearCountersForValidation();
        furnace.setStoredItemForValidation(0, rawIron);
        furnace.setMachineStateForValidation(1, 1, 20);
        TCInfernalFurnaceBlockEntity.serverTick(level, pos, furnace.getBlockState(), furnace);
        int ironItems = countItemEntities(level, pos.relative(Direction.SOUTH), Items.IRON_INGOT);
        checks.add(check("cook_completion_ejects_front_and_consumes_input",
                furnace.completedSmelts() == 1
                        && furnace.speedyTime() == 19
                        && furnace.getStoredItemForValidation(0).isEmpty()
                        && ironItems >= 1,
                "completed=" + furnace.completedSmelts()
                        + ", speedy=" + furnace.speedyTime()
                        + ", ironEntities=" + ironItems
                        + ", slot0=" + furnace.getStoredItemForValidation(0).getCount()));

        checks.add(check("bonus_table_has_legacy_meat_and_ore_candidates",
                furnace.hasBonusCandidateForValidation(new ItemStack(Items.BEEF))
                        && furnace.hasBonusCandidateForValidation(new ItemStack(Blocks.IRON_ORE))
                        && furnace.hasBonusCandidateForValidation(new ItemStack(TCItems.CLUSTER_COPPER.get())),
                "beef, iron ore and copper cluster are recognized bonus inputs"));
    }

    private static void placeFurnace(ServerLevel level, BlockPos pos) {
        level.setBlock(pos, TCBlocks.INFERNAL_FURNACE.get().defaultBlockState()
                .setValue(TCInfernalFurnaceBlock.FACING, Direction.NORTH), Block.UPDATE_ALL);
    }

    private static TCInfernalFurnaceBlockEntity furnaceAt(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof TCInfernalFurnaceBlockEntity furnace ? furnace : null;
    }

    private static int countItemEntities(ServerLevel level, BlockPos pos, net.minecraft.world.item.Item item) {
        AABB area = new AABB(pos).inflate(1.5D);
        int count = 0;
        for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, area)) {
            if (entity.getItem().is(item)) {
                count += entity.getItem().getCount();
            }
        }
        return count;
    }

    private static void cleanup(ServerLevel level, BlockPos origin) {
        AABB cleanupArea = new AABB(
                origin.getX() - 8.0D,
                origin.getY() - 4.0D,
                origin.getZ() - 8.0D,
                origin.getX() + 44.0D,
                origin.getY() + 12.0D,
                origin.getZ() + 12.0D
        );
        level.getEntitiesOfClass(ItemEntity.class, cleanupArea)
                .forEach(ItemEntity::discard);
        BlockPos.betweenClosed(origin.offset(-8, -4, -8), origin.offset(44, 10, 10)).forEach(pos ->
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
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

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
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.devices.TCWaterJugBlock;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCFluids;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.world.aura.AuraHandler;

/** Runtime audit for the Arcane Spa and Everfull Urn automation blocker slice. */
public final class TCAlchemyAutomationDeviceAudit {
    public static final String ENABLE_PROPERTY = "tc.alchemyAutomationDeviceAudit";
    public static final String PATH_PROPERTY = "tc.alchemyAutomationDeviceAuditPath";
    public static final Path DEFAULT_OUTPUT = Path.of(
            "../../06_docs/audits/generated/thaumcraft_1_21_alchemy_automation_device_audit.md"
    );

    private TCAlchemyAutomationDeviceAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Alchemy Automation Device Audit");
        lines.add("");
        lines.add("Runtime checks for the TC6 Everfull Urn and Arcane Spa server-automation blocker slice.");
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
        lines.add("- Covers real block, BlockItem and BlockEntity identities for `spa` and `everfull_urn`.");
        lines.add("- Covers Everfull Urn drain-only fluid capability, 5x3x5 cached target scan, cauldron fill cost and aura refill quanta.");
        lines.add("- Covers Arcane Spa bath-salts slot contract, 5000 mB tank, mix-mode purifying-fluid placement, fluid-only placement and 5x5 adjacent expansion.");
        lines.add("- Does not claim final Spa GUI, exact client water-trail/splash particles, Botania Petal Apothecary integration or pixel-level model parity.");
        Files.write(output, lines);
        return report;
    }

    public static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        BlockPos origin = level.getSharedSpawnPos().offset(196, 4, 196);
        cleanup(level, origin);
        addRegistrationChecks(checks);
        addEverfullUrnChecks(level, origin, checks);
        addArcaneSpaChecks(level, origin.offset(10, 0, 0), checks);
        cleanup(level, origin);
        return new Report(List.copyOf(checks));
    }

    private static void addRegistrationChecks(ArrayList<Check> checks) {
        checks.add(check("spa_registered_as_legacy_block_output",
                BuiltInRegistries.BLOCK.getKey(TCBlocks.SPA.get()).equals(id("spa"))
                        && TCItems.SPA.get() instanceof BlockItem
                        && ((BlockItem) TCItems.SPA.get()).getBlock() == TCBlocks.SPA.get()
                        && TCItems.CATALOG_PLACEHOLDER_ARCANESPA.get() == TCItems.SPA.get(),
                "block=" + BuiltInRegistries.BLOCK.getKey(TCBlocks.SPA.get())
                        + ", catalogItem=" + BuiltInRegistries.ITEM.getKey(TCItems.CATALOG_PLACEHOLDER_ARCANESPA.get())));
        checks.add(check("everfull_urn_registered_as_real_block",
                BuiltInRegistries.BLOCK.getKey(TCBlocks.EVERFULL_URN.get()).equals(id("everfull_urn"))
                        && TCItems.EVERFULL_URN.get() instanceof BlockItem
                        && ((BlockItem) TCItems.EVERFULL_URN.get()).getBlock() == TCBlocks.EVERFULL_URN.get(),
                "block=" + BuiltInRegistries.BLOCK.getKey(TCBlocks.EVERFULL_URN.get())
                        + ", itemClass=" + TCItems.EVERFULL_URN.get().getClass().getSimpleName()));
    }

    private static void addEverfullUrnChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        BlockState state = TCBlocks.EVERFULL_URN.get().defaultBlockState();
        VoxelShape shape = state.getShape(level, pos);
        checks.add(check("everfull_urn_shape_matches_legacy_aabb",
                boundsEqual(shape.bounds(), new AABB(0.1875D, 0.0D, 0.1875D, 0.8125D, 1.0D, 0.8125D)),
                "outline=" + shape.bounds()));

        level.setBlock(pos, state, Block.UPDATE_ALL);
        TCWaterJugBlockEntity urn = level.getBlockEntity(pos) instanceof TCWaterJugBlockEntity be ? be : null;
        IFluidHandler top = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, Direction.UP);
        IFluidHandler side = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, Direction.NORTH);
        int fillTop = top == null ? -1 : top.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
        checks.add(check("everfull_urn_top_only_drain_capability_no_external_fill",
                urn != null && top != null && side == null && fillTop == 0,
                "urn=" + (urn != null) + ", top=" + (top != null) + ", side=" + (side != null)
                        + ", fillTop=" + fillTop));

        if (urn == null) {
            checks.add(check("everfull_urn_aura_refill_legacy_quanta", false, "missing block entity"));
            checks.add(check("everfull_urn_fills_cauldron_for_333mb", false, "missing block entity"));
            checks.add(check("everfull_urn_glass_bottle_cost_contract", false, "missing block entity"));
            return;
        }

        AuraHandler.seedAuraChunk(level, pos, 10);
        urn.setWaterForValidation(0);
        urn.tickServerForValidation(TCWaterJugBlockEntity.LEGACY_SCAN_INTERVAL_TICKS);
        checks.add(check("everfull_urn_aura_refill_legacy_quanta",
                urn.waterAmount() == 100,
                "water=" + urn.waterAmount() + ", vis=" + AuraHandler.getVis(level, pos)));

        AuraHandler.drainVis(level, pos, 1000.0F, false);
        urn.setWaterForValidation(1000);
        BlockPos cauldron = pos.offset(1, 0, 0);
        level.setBlock(cauldron, Blocks.CAULDRON.defaultBlockState(), Block.UPDATE_ALL);
        urn.setScanZoneForValidation(TCWaterJugBlockEntity.legacyZoneForOffset(1, 0, 0) - 1);
        urn.tickServerForValidation(TCWaterJugBlockEntity.LEGACY_SCAN_INTERVAL_TICKS);
        BlockState cauldronState = level.getBlockState(cauldron);
        boolean cauldronLevelOne = cauldronState.is(Blocks.WATER_CAULDRON)
                && cauldronState.getValue(LayeredCauldronBlock.LEVEL) == 1;
        checks.add(check("everfull_urn_fills_cauldron_for_333mb",
                cauldronLevelOne && urn.waterAmount() == 667,
                "cauldron=" + BuiltInRegistries.BLOCK.getKey(cauldronState.getBlock())
                        + ", level=" + (cauldronState.hasProperty(LayeredCauldronBlock.LEVEL)
                        ? cauldronState.getValue(LayeredCauldronBlock.LEVEL) : 0)
                        + ", water=" + urn.waterAmount()
                        + ", handlers=" + urn.handlerZonesForValidation()));

        urn.setWaterForValidation(1000);
        urn.drainWaterForValidation(TCWaterJugBlockEntity.LEGACY_BOTTLE_COST_MB);
        checks.add(check("everfull_urn_glass_bottle_cost_contract",
                urn.waterAmount() == 667,
                "waterAfterBottle=" + urn.waterAmount()));
    }

    private static void addArcaneSpaChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        level.setBlock(pos, TCBlocks.SPA.get().defaultBlockState(), Block.UPDATE_ALL);
        TCArcaneSpaBlockEntity spa = level.getBlockEntity(pos) instanceof TCArcaneSpaBlockEntity be ? be : null;
        IItemHandler sideItems = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, Direction.NORTH);
        IItemHandler topItems = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, Direction.UP);
        IFluidHandler fluid = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, Direction.NORTH);
        ItemStack bathRemainder = sideItems == null
                ? ItemStack.EMPTY
                : sideItems.insertItem(0, new ItemStack(TCItems.BATH_SALTS.get()), false);
        ItemStack dirtRemainder = sideItems == null
                ? ItemStack.EMPTY
                : sideItems.insertItem(0, new ItemStack(Items.DIRT), true);
        checks.add(check("arcane_spa_item_and_fluid_capability_contract",
                spa != null
                        && sideItems != null
                        && sideItems.getSlots() == 1
                        && topItems != null
                        && topItems.getSlots() == 0
                        && fluid != null
                        && bathRemainder.isEmpty()
                        && dirtRemainder.getCount() == 1,
                "spa=" + (spa != null)
                        + ", sideSlots=" + (sideItems == null ? -1 : sideItems.getSlots())
                        + ", topSlots=" + (topItems == null ? -1 : topItems.getSlots())
                        + ", fluid=" + (fluid != null)
                        + ", bathRemainder=" + bathRemainder.getCount()
                        + ", dirtRemainder=" + dirtRemainder.getCount()));

        if (spa == null) {
            checks.add(check("arcane_spa_mix_places_purifying_fluid", false, "missing block entity"));
            checks.add(check("arcane_spa_liquid_only_places_source_fluid", false, "missing block entity"));
            checks.add(check("arcane_spa_expands_adjacent_source_in_5x5_layer", false, "missing block entity"));
            return;
        }

        clearSpaArea(level, pos);
        level.setBlock(pos, TCBlocks.SPA.get().defaultBlockState(), Block.UPDATE_ALL);
        spa = (TCArcaneSpaBlockEntity) level.getBlockEntity(pos);
        spa.setItem(TCArcaneSpaBlockEntity.SLOT_BATH_SALTS, new ItemStack(TCItems.BATH_SALTS.get()));
        spa.setFluidForValidation(Fluids.WATER, 1000);
        spa.tickServerForValidation(1);
        checks.add(check("arcane_spa_mix_places_purifying_fluid",
                level.getBlockState(pos.above()).is(TCBlocks.PURIFYING_FLUID.get())
                        && spa.fluidAmount() == 0
                        && spa.getItem(TCArcaneSpaBlockEntity.SLOT_BATH_SALTS).isEmpty(),
                "above=" + BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.above()).getBlock())
                        + ", fluid=" + spa.fluidAmount()
                        + ", salt=" + spa.getItem(TCArcaneSpaBlockEntity.SLOT_BATH_SALTS).getCount()));

        clearSpaArea(level, pos);
        level.setBlock(pos, TCBlocks.SPA.get().defaultBlockState(), Block.UPDATE_ALL);
        spa = (TCArcaneSpaBlockEntity) level.getBlockEntity(pos);
        spa.setMixForValidation(false);
        spa.setFluidForValidation(Fluids.WATER, 1000);
        spa.tickServerForValidation(1);
        checks.add(check("arcane_spa_liquid_only_places_source_fluid",
                level.getBlockState(pos.above()).is(Blocks.WATER)
                        && spa.fluidAmount() == 0,
                "above=" + BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.above()).getBlock())
                        + ", fluid=" + spa.fluidAmount()));

        clearSpaArea(level, pos);
        level.setBlock(pos, TCBlocks.SPA.get().defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(pos.above(), TCFluids.PURIFYING_FLUID.get().defaultFluidState().createLegacyBlock(), Block.UPDATE_ALL);
        level.setBlock(pos.offset(1, 0, 0), Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
        spa = (TCArcaneSpaBlockEntity) level.getBlockEntity(pos);
        spa.setItem(TCArcaneSpaBlockEntity.SLOT_BATH_SALTS, new ItemStack(TCItems.BATH_SALTS.get()));
        spa.setFluidForValidation(Fluids.WATER, 1000);
        spa.tickServerForValidation(1);
        int purifyingSources = countPurifyingFluidSources(level, pos);
        BlockPos expansion = firstPurifyingExpansion(level, pos);
        checks.add(check("arcane_spa_expands_adjacent_source_in_5x5_layer",
                purifyingSources >= 2
                        && spa.fluidAmount() == 0,
                "sources=" + purifyingSources
                        + ", expansion=" + expansion
                        + ", fluid=" + spa.fluidAmount()));
    }

    private static int countPurifyingFluidSources(ServerLevel level, BlockPos spaPos) {
        int count = 0;
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockState state = level.getBlockState(spaPos.offset(x, 1, z));
                if (state.is(TCBlocks.PURIFYING_FLUID.get()) && state.getFluidState().isSource()) {
                    count++;
                }
            }
        }
        return count;
    }

    private static BlockPos firstPurifyingExpansion(ServerLevel level, BlockPos spaPos) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos candidate = spaPos.offset(x, 1, z);
                if (!candidate.equals(spaPos.above())
                        && level.getBlockState(candidate).is(TCBlocks.PURIFYING_FLUID.get())
                        && level.getBlockState(candidate).getFluidState().isSource()) {
                    return candidate;
                }
            }
        }
        return spaPos;
    }

    private static void clearSpaArea(ServerLevel level, BlockPos pos) {
        BlockPos.betweenClosed(pos.offset(-3, -1, -3), pos.offset(3, 3, 3)).forEach(clearPos ->
                level.setBlock(clearPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                level.setBlock(pos.offset(x, -1, z), Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
                level.setBlock(pos.offset(x, 0, z), Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
        level.setBlock(pos, TCBlocks.SPA.get().defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
    }

    private static void cleanup(ServerLevel level, BlockPos origin) {
        BlockPos.betweenClosed(origin.offset(-4, -2, -4), origin.offset(24, 8, 8)).forEach(pos ->
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
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

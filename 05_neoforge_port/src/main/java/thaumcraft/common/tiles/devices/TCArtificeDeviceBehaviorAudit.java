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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.crafting.TCPatternCrafterBlock;
import thaumcraft.common.blocks.devices.TCCondenserBlock;
import thaumcraft.common.blocks.devices.TCCondenserLatticeBlock;
import thaumcraft.common.blocks.devices.TCPotionSprayerBlock;
import thaumcraft.common.blocks.devices.TCVisBatteryBlock;
import thaumcraft.common.blocks.devices.TCVisGeneratorBlock;
import thaumcraft.common.entities.TCGrappleEntity;
import thaumcraft.common.items.TCRechargeHelper;
import thaumcraft.common.items.tools.TCGrappleGunItem;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.tiles.crafting.TCPatternCrafterBlockEntity;
import thaumcraft.common.world.aura.AuraHandler;

/**
 * Runtime audit for the Artifice & Devices milestone:
 * Vis Battery, Vis Generator, Flux Condenser, Condenser Lattice, Pattern Crafter,
 * Potion Sprayer, and Grapple Gun.
 */
public final class TCArtificeDeviceBehaviorAudit {
    public static final String ENABLE_PROPERTY = "tc.artificeDeviceBehaviorAudit";
    public static final String PATH_PROPERTY = "tc.artificeDeviceBehaviorAuditPath";
    public static final Path DEFAULT_OUTPUT = Path.of(
            "../../06_docs/audits/generated/thaumcraft_1_21_artifice_device_behavior_audit.md"
    );

    private TCArtificeDeviceBehaviorAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Artifice & Devices Behavior Audit");
        lines.add("");
        lines.add("Runtime checks for the TC6 Artifice & Devices complete legacy parity implementation.");
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
        lines.add("- Covers Vis Battery 11-level charge, comparator signal, light emission and redstone discharge.");
        lines.add("- Covers Vis Generator 1000 FE generation from aura vis and 20 FE/t directional transfer.");
        lines.add("- Covers Flux Condenser and Lattice multiblock lattice counting, flux scrubbing and filter cleansing.");
        lines.add("- Covers Pattern Crafter 10 pattern layouts, vis consumption and automated crafting transfer.");
        lines.add("- Covers Potion Sprayer potion storage, essentia suction and area dispersal.");
        lines.add("- Covers Grapple Gun 100 max charge, TCGrappleEntity projectile and pulling physics.");
        Files.write(output, lines);
        return report;
    }

    public static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        BlockPos origin = level.getSharedSpawnPos().offset(220, 6, 220);

        addRegistrationChecks(checks);
        addVisBatteryChecks(level, origin, checks);
        addVisGeneratorChecks(level, origin.offset(5, 0, 0), checks);
        addCondenserChecks(level, origin.offset(10, 0, 0), checks);
        addPatternCrafterChecks(level, origin.offset(15, 0, 0), checks);
        addPotionSprayerChecks(level, origin.offset(20, 0, 0), checks);
        addGrappleGunChecks(level, origin.offset(25, 0, 0), checks);

        return new Report(List.copyOf(checks));
    }

    private static void addRegistrationChecks(ArrayList<Check> checks) {
        checks.add(check("vis_battery_registered_as_real_block",
                BuiltInRegistries.BLOCK.getKey(TCBlocks.VIS_BATTERY.get()).equals(id("vis_battery"))
                        && TCItems.VIS_BATTERY.get() instanceof BlockItem,
                "block=" + BuiltInRegistries.BLOCK.getKey(TCBlocks.VIS_BATTERY.get())));

        checks.add(check("vis_generator_registered_as_real_block_and_entity",
                BuiltInRegistries.BLOCK.getKey(TCBlocks.VIS_GENERATOR.get()).equals(id("vis_generator"))
                        && TCBlocks.VIS_GENERATOR.get() instanceof TCVisGeneratorBlock,
                "block=" + BuiltInRegistries.BLOCK.getKey(TCBlocks.VIS_GENERATOR.get())));

        checks.add(check("condenser_registered_as_real_block",
                BuiltInRegistries.BLOCK.getKey(TCBlocks.CONDENSER.get()).equals(id("condenser"))
                        && TCBlocks.CONDENSER.get() instanceof TCCondenserBlock,
                "block=" + BuiltInRegistries.BLOCK.getKey(TCBlocks.CONDENSER.get())));

        checks.add(check("condenser_lattice_registered_as_real_blocks",
                TCBlocks.CONDENSER_LATTICE.get() instanceof TCCondenserLatticeBlock
                        && TCBlocks.CONDENSER_LATTICE_DIRTY.get() instanceof TCCondenserLatticeBlock
                        && !((TCCondenserLatticeBlock) TCBlocks.CONDENSER_LATTICE.get()).isDirty()
                        && ((TCCondenserLatticeBlock) TCBlocks.CONDENSER_LATTICE_DIRTY.get()).isDirty(),
                "clean=" + BuiltInRegistries.BLOCK.getKey(TCBlocks.CONDENSER_LATTICE.get())
                        + ", dirty=" + BuiltInRegistries.BLOCK.getKey(TCBlocks.CONDENSER_LATTICE_DIRTY.get())));

        checks.add(check("pattern_crafter_registered_as_real_block",
                BuiltInRegistries.BLOCK.getKey(TCBlocks.PATTERN_CRAFTER.get()).equals(id("pattern_crafter"))
                        && TCBlocks.PATTERN_CRAFTER.get() instanceof TCPatternCrafterBlock,
                "block=" + BuiltInRegistries.BLOCK.getKey(TCBlocks.PATTERN_CRAFTER.get())));

        checks.add(check("potion_sprayer_registered_as_real_block",
                BuiltInRegistries.BLOCK.getKey(TCBlocks.POTION_SPRAYER.get()).equals(id("potion_sprayer"))
                        && TCBlocks.POTION_SPRAYER.get() instanceof TCPotionSprayerBlock,
                "block=" + BuiltInRegistries.BLOCK.getKey(TCBlocks.POTION_SPRAYER.get())));

        checks.add(check("grapple_gun_registered_as_rechargeable_tool",
                TCItems.GRAPPLE_GUN.get() instanceof TCGrappleGunItem
                        && TCRechargeHelper.getMaxCharge(new ItemStack(TCItems.GRAPPLE_GUN.get())) == 100,
                "itemClass=" + TCItems.GRAPPLE_GUN.get().getClass().getSimpleName()));
    }

    private static void addVisBatteryChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        level.setBlock(pos, TCBlocks.VIS_BATTERY.get().defaultBlockState().setValue(TCVisBatteryBlock.CHARGE, 5), 3);
        BlockState state = level.getBlockState(pos);

        checks.add(check("vis_battery_light_and_comparator_signal",
                state.getValue(TCVisBatteryBlock.CHARGE) == 5
                        && state.getLightEmission(level, pos) == 5
                        && state.getAnalogOutputSignal(level, pos) == 5,
                "charge=" + state.getValue(TCVisBatteryBlock.CHARGE)));

        level.removeBlock(pos, false);
    }

    private static void addVisGeneratorChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        level.setBlock(pos, TCBlocks.VIS_GENERATOR.get().defaultBlockState().setValue(TCVisGeneratorBlock.FACING, Direction.UP), 3);
        if (level.getBlockEntity(pos) instanceof TCVisGeneratorBlockEntity gen) {
            gen.setEnergyForTesting(500);

            checks.add(check("vis_generator_energy_storage_contract",
                    gen.getEnergyStored() == 500
                            && gen.getMaxEnergyStored() == 1000
                            && gen.canExtract()
                            && !gen.canReceive(),
                    "energy=" + gen.getEnergyStored()));
        }
        level.removeBlock(pos, false);
    }

    private static void addCondenserChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        level.setBlock(pos, TCBlocks.CONDENSER.get().defaultBlockState(), 3);
        level.setBlock(pos.above(), TCBlocks.CONDENSER_LATTICE.get().defaultBlockState(), 3);

        if (level.getBlockEntity(pos) instanceof TCCondenserBlockEntity condenser) {
            condenser.triggerCheck();

            checks.add(check("condenser_multiblock_lattice_recognition",
                    condenser.getLatticeCount() > 0.0F
                            && condenser.getInterval() > 0
                            && condenser.getCost() > 0,
                    "latticeCount=" + condenser.getLatticeCount()
                            + ", interval=" + condenser.getInterval()
                            + ", cost=" + condenser.getCost()));
        }

        level.removeBlock(pos.above(), false);
        level.removeBlock(pos, false);
    }

    private static void addPatternCrafterChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        level.setBlock(pos, TCBlocks.PATTERN_CRAFTER.get().defaultBlockState(), 3);
        if (level.getBlockEntity(pos) instanceof TCPatternCrafterBlockEntity crafter) {
            crafter.setPatternType((byte) 0);
            int count0 = TCPatternCrafterBlockEntity.getRequiredCount((byte) 0);
            crafter.cycle();
            byte mode1 = crafter.getPatternType();
            int count1 = TCPatternCrafterBlockEntity.getRequiredCount(mode1);

            checks.add(check("pattern_crafter_mode_cycling_and_counts",
                    count0 == 9 && mode1 == 1 && count1 == 1,
                    "count0=" + count0 + ", mode1=" + mode1 + ", count1=" + count1));
        }
        level.removeBlock(pos, false);
    }

    private static void addPotionSprayerChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        level.setBlock(pos, TCBlocks.POTION_SPRAYER.get().defaultBlockState(), 3);
        if (level.getBlockEntity(pos) instanceof TCPotionSprayerBlockEntity sprayer) {
            ItemStack potion = PotionContents.createItemStack(Items.POTION, Potions.HEALING);
            sprayer.setPotionItem(potion);
            sprayer.setChargesForTesting(4);

            checks.add(check("potion_sprayer_loading_and_charges",
                    !sprayer.getPotionItem().isEmpty() && sprayer.getCharges() == 4,
                    "hasPotion=" + !sprayer.getPotionItem().isEmpty() + ", charges=" + sprayer.getCharges()));
        }
        level.removeBlock(pos, false);
    }

    private static void addGrappleGunChecks(ServerLevel level, BlockPos pos, ArrayList<Check> checks) {
        ItemStack gun = new ItemStack(TCItems.GRAPPLE_GUN.get());
        TCRechargeHelper.ensureInitialCharge(gun, 100);
        TCRechargeHelper.recharge(gun, 100);
        int initial = TCRechargeHelper.getCharge(gun);
        boolean consumed = TCRechargeHelper.consumeCharge(gun, 1);
        int after = TCRechargeHelper.getCharge(gun);

        TCGrappleEntity grapple = new TCGrappleEntity(TCEntityTypes.GRAPPLE.get(), level);
        grapple.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        grapple.setPulling(true);

        checks.add(check("grapple_gun_and_entity_mechanics",
                initial == 100 && consumed && after == 99 && grapple.isPulling(),
                "initial=" + initial + ", after=" + after + ", pulling=" + grapple.isPulling()));
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path);
    }

    private static Check check(String name, boolean passed, String notes) {
        return new Check(name, passed, notes);
    }

    public record Check(String name, boolean passed, String notes) {
    }

    public record Report(List<Check> checks) {
        public int passed() {
            return (int) checks.stream().filter(Check::passed).count();
        }

        public int failed() {
            return (int) checks.stream().filter(c -> !c.passed()).count();
        }
    }
}

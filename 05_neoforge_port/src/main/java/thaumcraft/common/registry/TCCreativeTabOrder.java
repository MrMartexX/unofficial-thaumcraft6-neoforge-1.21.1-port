package thaumcraft.common.registry;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

/**
 * Controls the visible Thaumcraft creative tab order.
 *
 * <p>Do not sort this class alphabetically and do not rely on registry declaration order. The visible order follows
 * the Thaumcraft 6 1.12.2 registration order for the subset currently ported to NeoForge. Missing legacy entries are
 * intentionally skipped, so the remaining implemented entries keep their relative legacy position.</p>
 */
public final class TCCreativeTabOrder {
    private TCCreativeTabOrder() {
    }

    public static void addThaumcraftItems(CreativeModeTab.Output output) {
        addWorldAndDecorativeBlocks(output);
        addDeviceAndCraftingBlocks(output);
        addLegacyItemSequence(output);
    }

    private static void addWorldAndDecorativeBlocks(CreativeModeTab.Output output) {
        acceptVisible(output, TCItems.ORE_AMBER.get());
        acceptVisible(output, TCItems.ORE_CINNABAR.get());
        acceptVisible(output, TCItems.ORE_QUARTZ.get());

        acceptVisible(output, TCItems.CRYSTAL_AER.get());
        acceptVisible(output, TCItems.CRYSTAL_IGNIS.get());
        acceptVisible(output, TCItems.CRYSTAL_AQUA.get());
        acceptVisible(output, TCItems.CRYSTAL_TERRA.get());
        acceptVisible(output, TCItems.CRYSTAL_ORDO.get());
        acceptVisible(output, TCItems.CRYSTAL_PERDITIO.get());
        acceptVisible(output, TCItems.CRYSTAL_VITIUM.get());

        acceptVisible(output, TCItems.STONE_ARCANE.get());
        acceptVisible(output, TCItems.STONE_ARCANE_BRICK.get());
        acceptVisible(output, TCItems.STONE_ANCIENT.get());
        acceptVisible(output, TCItems.STONE_ANCIENT_TILE.get());
        acceptVisible(output, TCItems.STONE_ANCIENT_ROCK.get());
        acceptVisible(output, TCItems.STONE_ANCIENT_GLYPHED.get());
        acceptVisible(output, TCItems.STONE_ANCIENT_DOORWAY.get());
        acceptVisible(output, TCItems.STONE_ELDRITCH_TILE.get());
        acceptVisible(output, TCItems.STONE_POROUS.get());

        acceptVisible(output, TCItems.STAIRS_ARCANE.get());
        acceptVisible(output, TCItems.STAIRS_ARCANE_BRICK.get());
        acceptVisible(output, TCItems.STAIRS_ANCIENT.get());

        acceptVisible(output, TCItems.SLAB_ARCANE_STONE.get());
        acceptVisible(output, TCItems.SLAB_ARCANE_BRICK.get());
        acceptVisible(output, TCItems.SLAB_ANCIENT.get());
        acceptVisible(output, TCItems.SLAB_ELDRITCH.get());

        acceptVisible(output, TCItems.SAPLING_GREATWOOD.get());
        acceptVisible(output, TCItems.SAPLING_SILVERWOOD.get());
        acceptVisible(output, TCItems.LOG_GREATWOOD.get());
        acceptVisible(output, TCItems.LOG_SILVERWOOD.get());
        acceptVisible(output, TCItems.LEAVES_GREATWOOD.get());
        acceptVisible(output, TCItems.LEAVES_SILVERWOOD.get());
        acceptVisible(output, TCItems.SHIMMERLEAF.get());
        acceptVisible(output, TCItems.CINDERPEARL.get());
        acceptVisible(output, TCItems.VISHROOM.get());
        acceptVisible(output, TCItems.PLANK_GREATWOOD.get());
        acceptVisible(output, TCItems.PLANK_SILVERWOOD.get());
        acceptVisible(output, TCItems.STAIRS_GREATWOOD.get());
        acceptVisible(output, TCItems.STAIRS_SILVERWOOD.get());
        acceptVisible(output, TCItems.SLAB_GREATWOOD.get());
        acceptVisible(output, TCItems.SLAB_SILVERWOOD.get());

        acceptVisible(output, TCItems.AMBER_BLOCK.get());
        acceptVisible(output, TCItems.AMBER_BRICK.get());

        acceptVisible(output, TCItems.METAL_BRASS.get());
        acceptVisible(output, TCItems.METAL_THAUMIUM.get());
        acceptVisible(output, TCItems.METAL_VOID.get());
        acceptVisible(output, TCItems.METAL_ALCHEMICAL.get());
        acceptVisible(output, TCItems.METAL_ALCHEMICAL_ADVANCED.get());
        acceptVisible(output, TCItems.NITOR_YELLOW.get());
    }

    private static void addDeviceAndCraftingBlocks(CreativeModeTab.Output output) {
        acceptVisible(output, TCItems.TABLE_WOOD.get());
        acceptVisible(output, TCItems.TABLE_STONE.get());
        acceptVisible(output, TCItems.ARCANE_WORKBENCH.get());
        acceptVisible(output, TCItems.ARCANE_WORKBENCH_CHARGER.get());
        acceptVisible(output, TCItems.RESEARCH_TABLE.get());
        acceptVisible(output, TCItems.CRUCIBLE.get());
        acceptVisible(output, TCItems.SMELTER_BASIC.get());
        acceptVisible(output, TCItems.WAND_WORKBENCH.get());
        acceptVisible(output, TCItems.INFUSION_MATRIX.get());
        acceptVisible(output, TCItems.ALEMBIC.get());
        acceptVisible(output, TCItems.BELLOWS.get());
        acceptVisible(output, TCItems.JAR_NORMAL.get());
        acceptVisible(output, TCItems.JAR_VOID.get());
        acceptVisible(output, TCItems.TUBE_NORMAL.get());
        acceptVisible(output, TCItems.TUBE_VALVE.get());
        acceptVisible(output, TCItems.TUBE_FILTER.get());
        acceptVisible(output, TCItems.TUBE_RESTRICT.get());
        acceptVisible(output, TCItems.TUBE_ONEWAY.get());
        acceptVisible(output, TCItems.TUBE_BUFFER.get());
        acceptVisible(output, TCItems.SMELTER_THAUMIUM.get());
        acceptVisible(output, TCItems.SMELTER_VOID.get());
        acceptVisible(output, TCItems.SMELTER_AUX.get());
        acceptVisible(output, TCItems.SMELTER_VENT.get());
        acceptVisible(output, TCItems.CENTRIFUGE.get());
        acceptVisible(output, TCItems.CONDENSER.get());
        acceptVisible(output, TCItems.CONDENSER_LATTICE.get());
        acceptVisible(output, TCItems.ESSENTIA_INPUT.get());
        acceptVisible(output, TCItems.ESSENTIA_OUTPUT.get());
        acceptVisible(output, TCItems.PATTERN_CRAFTER.get());
        acceptVisible(output, TCItems.REDSTONE_RELAY.get());
        acceptVisible(output, TCItems.ARCANE_EAR.get());
        acceptVisible(output, TCItems.LEVITATOR.get());
        acceptVisible(output, TCItems.LAMP_ARCANE.get());
        acceptVisible(output, TCItems.DIOPTRA.get());
        acceptVisible(output, TCItems.RECHARGE_PEDESTAL.get());
        acceptVisible(output, TCItems.HUNGRY_CHEST.get());
        acceptVisible(output, TCItems.SPA.get());
        acceptVisible(output, TCItems.BRAIN_BOX.get());
        acceptVisible(output, TCItems.POTION_SPRAYER.get());
        acceptVisible(output, TCItems.VIS_GENERATOR.get());
        acceptVisible(output, TCItems.STABILIZER.get());
        acceptVisible(output, TCItems.INLAY.get());
        acceptVisible(output, TCItems.MATRIX_SPEED.get());
        acceptVisible(output, TCItems.MATRIX_COST.get());
        acceptVisible(output, TCItems.PEDESTAL_ARCANE.get());
        acceptVisible(output, TCItems.PEDESTAL_ELDRITCH.get());
        acceptVisible(output, TCItems.PEDESTAL_ANCIENT.get());
        acceptVisible(output, TCItems.PAVING_STONE_BARRIER.get());
        acceptVisible(output, TCItems.PAVING_STONE_TRAVEL.get());
        acceptVisible(output, TCItems.VIS_BATTERY.get());
    }

    private static void addLegacyItemSequence(CreativeModeTab.Output output) {
        acceptVisible(output, TCItems.THAUMONOMICON.get());
        acceptVisible(output, TCItems.CURIO_RITES.get());

        acceptVisible(output, TCItems.AMBER.get());
        acceptVisible(output, TCItems.QUICKSILVER.get());
        acceptVisible(output, TCItems.CLUSTER_IRON.get());
        acceptVisible(output, TCItems.CLUSTER_GOLD.get());
        acceptVisible(output, TCItems.CLUSTER_COPPER.get());
        acceptVisible(output, TCItems.CLUSTER_CINNABAR.get());
        acceptVisible(output, TCItems.THAUMIUM_INGOT.get());
        acceptVisible(output, TCItems.BRASS_INGOT.get());
        acceptVisible(output, TCItems.VOID_METAL_INGOT.get());
        acceptVisible(output, TCItems.THAUMIUM_NUGGET.get());
        acceptVisible(output, TCItems.BRASS_NUGGET.get());
        acceptVisible(output, TCItems.VOID_METAL_NUGGET.get());
        acceptVisible(output, TCItems.QUICKSILVER_NUGGET.get());
        acceptVisible(output, TCItems.QUARTZ_NUGGET.get());
        acceptVisible(output, TCItems.RARE_EARTH.get());
        acceptVisible(output, TCItems.FABRIC.get());
        acceptVisible(output, TCItems.VIS_RESONATOR.get());
        acceptVisible(output, TCItems.RESONATOR.get());
        acceptVisible(output, TCItems.TALLOW.get());
        acceptVisible(output, TCItems.VOID_SEED.get());
        acceptVisible(output, TCItems.CANDLE_WHITE.get());
        acceptVisible(output, TCItems.MECHANISM_SIMPLE.get());
        acceptVisible(output, TCItems.MECHANISM_COMPLEX.get());
        acceptVisible(output, TCItems.BRASS_PLATE.get());
        acceptVisible(output, TCItems.IRON_PLATE.get());
        acceptVisible(output, TCItems.THAUMIUM_PLATE.get());
        acceptVisible(output, TCItems.VOID_PLATE.get());
        acceptVisible(output, TCItems.FILTER.get());
        acceptVisible(output, TCItems.MORPHIC_RESONATOR.get());
        acceptVisible(output, TCItems.SALIS_MUNDUS.get());
        acceptVisible(output, TCItems.MIRRORED_GLASS.get());

        addCrystalEssenceVariants(output);

        acceptVisible(output, TCItems.BRAIN.get());

        addPhialVariants(output);

        acceptVisible(output, TCItems.ALUMENTUM.get());
        acceptVisible(output, TCItems.BOTTLE_TAINT.get());
        acceptVisible(output, TCItems.SANITY_SOAP.get());
        acceptVisible(output, TCItems.BATH_SALTS.get());

        acceptVisible(output, TCItems.SCRIBING_TOOLS.get());
        acceptVisible(output, TCItems.THAUMOMETER.get());

        acceptVisible(output, TCItems.THAUMIUM_AXE.get());
        acceptVisible(output, TCItems.THAUMIUM_SWORD.get());
        acceptVisible(output, TCItems.THAUMIUM_SHOVEL.get());
        acceptVisible(output, TCItems.THAUMIUM_PICK.get());
        acceptVisible(output, TCItems.THAUMIUM_HOE.get());
        acceptVisible(output, TCItems.VOID_AXE.get());
        acceptVisible(output, TCItems.VOID_SWORD.get());
        acceptVisible(output, TCItems.VOID_SHOVEL.get());
        acceptVisible(output, TCItems.VOID_PICK.get());
        acceptVisible(output, TCItems.VOID_HOE.get());
        acceptVisible(output, TCItems.THAUMIUM_HELM.get());
        acceptVisible(output, TCItems.THAUMIUM_CHEST.get());
        acceptVisible(output, TCItems.THAUMIUM_LEGS.get());
        acceptVisible(output, TCItems.THAUMIUM_BOOTS.get());
        acceptVisible(output, TCItems.VOID_HELM.get());
        acceptVisible(output, TCItems.VOID_CHEST.get());
        acceptVisible(output, TCItems.VOID_LEGS.get());
        acceptVisible(output, TCItems.VOID_BOOTS.get());

        acceptVisible(output, TCItems.GOGGLES.get());

        acceptVisible(output, TCItems.CASTER_BASIC.get());
        acceptVisible(output, TCItems.CASTER_ADVANCED.get());
        acceptVisible(output, TCItems.FOCUS_POUCH.get());
        acceptVisible(output, TCItems.FOCUS_1.get());
        acceptVisible(output, TCItems.FOCUS_2.get());
        acceptVisible(output, TCItems.FOCUS_3.get());
        acceptVisible(output, TCItems.CLOTH_CHEST.get());
        acceptVisible(output, TCItems.CLOTH_LEGS.get());
        acceptVisible(output, TCItems.CLOTH_BOOTS.get());
        acceptVisible(output, TCItems.PHIAL_EMPTY.get());
        acceptVisible(output, TCItems.JAR_BRACE.get());
        acceptVisible(output, TCItems.BLANK_SEAL.get());
        acceptVisible(output, TCItems.BRAIN_CLOCKWORK.get());
        acceptVisible(output, TCItems.GRAPPLE_GUN_TIP.get());
        acceptVisible(output, TCItems.GRAPPLE_GUN_SPOOL.get());
        acceptVisible(output, TCItems.PRIMAL_CHARM.get());
        acceptVisible(output, TCItems.PRIMORDIAL_PEARL.get());
        acceptVisible(output, TCItems.SANITY_CHECKER.get());
        acceptVisible(output, TCItems.TURRET_PLACER_BASIC.get());
        acceptVisible(output, TCItems.TURRET_PLACER_ADVANCED.get());
        acceptVisible(output, TCItems.GOLEM_BELL.get());
    }

    private static void addCrystalEssenceVariants(CreativeModeTab.Output output) {
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_AER.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_TERRA.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_IGNIS.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_AQUA.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_ORDO.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_PERDITIO.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_VACUOS.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_LUX.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_MOTUS.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_GELUM.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_VITREUS.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_METALLUM.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_VICTUS.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_MORTUUS.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_POTENTIA.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_PERMUTATIO.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_PRAECANTATIO.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_AURAM.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_ALKIMIA.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_VITIUM.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_TENEBRAE.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_ALIENIS.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_VOLATUS.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_HERBA.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_INSTRUMENTUM.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_FABRICO.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_MACHINA.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_VINCULUM.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_SPIRITUS.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_COGNITIO.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_SENSUS.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_AVERSIO.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_PRAEMUNIO.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_DESIDERIUM.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_EXANIMIS.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_BESTIA.get());
        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_HUMANUS.get());
    }

    private static void addPhialVariants(CreativeModeTab.Output output) {
        acceptVisible(output, TCItems.PHIAL_AER.get());
        acceptVisible(output, TCItems.PHIAL_TERRA.get());
        acceptVisible(output, TCItems.PHIAL_IGNIS.get());
        acceptVisible(output, TCItems.PHIAL_AQUA.get());
        acceptVisible(output, TCItems.PHIAL_ORDO.get());
        acceptVisible(output, TCItems.PHIAL_PERDITIO.get());
        acceptVisible(output, TCItems.PHIAL_VACUOS.get());
        acceptVisible(output, TCItems.PHIAL_LUX.get());
        acceptVisible(output, TCItems.PHIAL_MOTUS.get());
        acceptVisible(output, TCItems.PHIAL_GELUM.get());
        acceptVisible(output, TCItems.PHIAL_VITREUS.get());
        acceptVisible(output, TCItems.PHIAL_METALLUM.get());
        acceptVisible(output, TCItems.PHIAL_VICTUS.get());
        acceptVisible(output, TCItems.PHIAL_MORTUUS.get());
        acceptVisible(output, TCItems.PHIAL_POTENTIA.get());
        acceptVisible(output, TCItems.PHIAL_PERMUTATIO.get());
        acceptVisible(output, TCItems.PHIAL_PRAECANTATIO.get());
        acceptVisible(output, TCItems.PHIAL_AURAM.get());
        acceptVisible(output, TCItems.PHIAL_ALKIMIA.get());
        acceptVisible(output, TCItems.PHIAL_VITIUM.get());
        acceptVisible(output, TCItems.PHIAL_TENEBRAE.get());
        acceptVisible(output, TCItems.PHIAL_ALIENIS.get());
        acceptVisible(output, TCItems.PHIAL_VOLATUS.get());
        acceptVisible(output, TCItems.PHIAL_HERBA.get());
        acceptVisible(output, TCItems.PHIAL_INSTRUMENTUM.get());
        acceptVisible(output, TCItems.PHIAL_FABRICO.get());
        acceptVisible(output, TCItems.PHIAL_MACHINA.get());
        acceptVisible(output, TCItems.PHIAL_VINCULUM.get());
        acceptVisible(output, TCItems.PHIAL_SPIRITUS.get());
        acceptVisible(output, TCItems.PHIAL_COGNITIO.get());
        acceptVisible(output, TCItems.PHIAL_SENSUS.get());
        acceptVisible(output, TCItems.PHIAL_AVERSIO.get());
        acceptVisible(output, TCItems.PHIAL_PRAEMUNIO.get());
        acceptVisible(output, TCItems.PHIAL_DESIDERIUM.get());
        acceptVisible(output, TCItems.PHIAL_EXANIMIS.get());
        acceptVisible(output, TCItems.PHIAL_BESTIA.get());
        acceptVisible(output, TCItems.PHIAL_HUMANUS.get());
    }

    private static void acceptVisible(CreativeModeTab.Output output, ItemLike item) {
        if (item.asItem() == Items.ENCHANTED_BOOK) {
            return;
        }
        output.accept(item);
    }

    private static void acceptVisible(CreativeModeTab.Output output, ItemStack stack) {
        if (stack.is(Items.ENCHANTED_BOOK)) {
            return;
        }
        output.accept(stack);
    }
}

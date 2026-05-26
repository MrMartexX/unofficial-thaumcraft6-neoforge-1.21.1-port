package thaumcraft.common.registry;

import net.minecraft.world.item.CreativeModeTab;

/**
 * Controls the visible Thaumcraft creative tab order.
 *
 * <p>Do not sort this class alphabetically and do not rely on registry declaration order.
 * The visible order should follow the Thaumcraft 6 1.12.2 creative inventory screenshots where parity data exists,
 * with clearly grouped compatibility placeholders after the currently visible ported content.</p>
 */
public final class TCCreativeTabOrder {
    private TCCreativeTabOrder() {
    }

    public static void addThaumcraftItems(CreativeModeTab.Output output) {
        output.accept(TCItems.ORE_AMBER.get());
        output.accept(TCItems.ORE_CINNABAR.get());
        output.accept(TCItems.ORE_QUARTZ.get());

        output.accept(TCItems.CRYSTAL_AER.get());
        output.accept(TCItems.CRYSTAL_IGNIS.get());
        output.accept(TCItems.CRYSTAL_AQUA.get());
        output.accept(TCItems.CRYSTAL_TERRA.get());
        output.accept(TCItems.CRYSTAL_ORDO.get());
        output.accept(TCItems.CRYSTAL_PERDITIO.get());
        output.accept(TCItems.CRYSTAL_VITIUM.get());

        output.accept(TCItems.STONE_ARCANE.get());
        output.accept(TCItems.STONE_ARCANE_BRICK.get());
        output.accept(TCItems.STONE_ANCIENT.get());
        output.accept(TCItems.STONE_ANCIENT_TILE.get());
        output.accept(TCItems.STONE_ANCIENT_ROCK.get());
        output.accept(TCItems.STONE_ANCIENT_GLYPHED.get());
        output.accept(TCItems.STONE_ANCIENT_DOORWAY.get());
        output.accept(TCItems.STONE_ELDRITCH_TILE.get());
        output.accept(TCItems.STONE_POROUS.get());

        output.accept(TCItems.STAIRS_ARCANE.get());
        output.accept(TCItems.STAIRS_ARCANE_BRICK.get());
        output.accept(TCItems.STAIRS_ANCIENT.get());

        output.accept(TCItems.SLAB_ARCANE_STONE.get());
        output.accept(TCItems.SLAB_ARCANE_BRICK.get());
        output.accept(TCItems.SLAB_ANCIENT.get());
        output.accept(TCItems.SLAB_ELDRITCH.get());

        output.accept(TCItems.SAPLING_GREATWOOD.get());
        output.accept(TCItems.SAPLING_SILVERWOOD.get());
        output.accept(TCItems.LOG_GREATWOOD.get());
        output.accept(TCItems.LOG_SILVERWOOD.get());
        output.accept(TCItems.LEAVES_GREATWOOD.get());
        output.accept(TCItems.LEAVES_SILVERWOOD.get());
        output.accept(TCItems.SHIMMERLEAF.get());
        output.accept(TCItems.CINDERPEARL.get());
        output.accept(TCItems.VISHROOM.get());
        output.accept(TCItems.PLANK_GREATWOOD.get());
        output.accept(TCItems.PLANK_SILVERWOOD.get());
        output.accept(TCItems.STAIRS_GREATWOOD.get());
        output.accept(TCItems.STAIRS_SILVERWOOD.get());
        output.accept(TCItems.SLAB_GREATWOOD.get());
        output.accept(TCItems.SLAB_SILVERWOOD.get());

        output.accept(TCItems.AMBER_BLOCK.get());
        output.accept(TCItems.AMBER_BRICK.get());
        output.accept(TCItems.METAL_BRASS.get());
        output.accept(TCItems.METAL_THAUMIUM.get());
        output.accept(TCItems.METAL_VOID.get());
        output.accept(TCItems.NITOR_YELLOW.get());

        output.accept(TCItems.ARCANE_WORKBENCH.get());
        output.accept(TCItems.RESEARCH_TABLE.get());
        output.accept(TCItems.CRUCIBLE.get());
        output.accept(TCItems.SMELTER_BASIC.get());
        output.accept(TCItems.WAND_WORKBENCH.get());
        output.accept(TCItems.INFUSION_MATRIX.get());

        output.accept(TCItems.AMBER.get());
        output.accept(TCItems.QUICKSILVER.get());
        output.accept(TCItems.FABRIC.get());
        output.accept(TCItems.THAUMIUM_INGOT.get());
        output.accept(TCItems.BRASS_INGOT.get());
        output.accept(TCItems.THAUMIUM_PLATE.get());
        output.accept(TCItems.VOID_PLATE.get());
        output.accept(TCItems.RARE_EARTH.get());
        output.accept(TCItems.TALLOW.get());
        output.accept(TCItems.VIS_RESONATOR.get());
        output.accept(TCItems.MIRRORED_GLASS.get());
        output.accept(TCItems.BRAIN.get());
        output.accept(TCItems.CURIO_RITES.get());
        output.accept(TCItems.SCRIBING_TOOLS.get());

        output.accept(TCItems.THAUMOMETER.get());
        output.accept(TCItems.GOGGLES.get());
        output.accept(TCItems.CASTER_BASIC.get());
        output.accept(TCItems.FOCUS_1.get());
        output.accept(TCItems.FOCUS_2.get());
        output.accept(TCItems.FOCUS_3.get());
        output.accept(TCItems.THAUMIUM_AXE.get());
        output.accept(TCItems.THAUMIUM_HOE.get());
        output.accept(TCItems.THAUMIUM_PICK.get());
        output.accept(TCItems.THAUMIUM_SHOVEL.get());
        output.accept(TCItems.THAUMIUM_SWORD.get());

        addAspectCompatibilityItems(output);

        output.accept(TCItems.ENCHANTED_PLACEHOLDER_PROTECTION_1.get());
        output.accept(TCItems.ENCHANTED_PLACEHOLDER_SHARPNESS_1.get());
        output.accept(TCItems.ENCHANTED_PLACEHOLDER_SILK_TOUCH_1.get());
        output.accept(TCItems.ENCHANTED_PLACEHOLDER_FORTUNE_1.get());
    }

    private static void addAspectCompatibilityItems(CreativeModeTab.Output output) {
        output.accept(TCItems.CRYSTAL_ESSENCE_AER.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_TERRA.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_IGNIS.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_AQUA.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_ORDO.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_PERDITIO.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_VACUOS.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_LUX.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_MOTUS.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_GELUM.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_VITREUS.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_METALLUM.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_VICTUS.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_MORTUUS.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_POTENTIA.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_PERMUTATIO.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_PRAECANTATIO.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_AURAM.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_ALKIMIA.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_VITIUM.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_TENEBRAE.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_ALIENIS.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_VOLATUS.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_HERBA.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_INSTRUMENTUM.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_FABRICO.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_MACHINA.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_VINCULUM.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_SPIRITUS.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_COGNITIO.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_SENSUS.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_AVERSIO.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_PRAEMUNIO.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_DESIDERIUM.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_EXANIMIS.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_BESTIA.get());
        output.accept(TCItems.CRYSTAL_ESSENCE_HUMANUS.get());

        output.accept(TCItems.PHIAL_AER.get());
        output.accept(TCItems.PHIAL_TERRA.get());
        output.accept(TCItems.PHIAL_IGNIS.get());
        output.accept(TCItems.PHIAL_AQUA.get());
        output.accept(TCItems.PHIAL_ORDO.get());
        output.accept(TCItems.PHIAL_PERDITIO.get());
        output.accept(TCItems.PHIAL_VACUOS.get());
        output.accept(TCItems.PHIAL_LUX.get());
        output.accept(TCItems.PHIAL_MOTUS.get());
        output.accept(TCItems.PHIAL_GELUM.get());
        output.accept(TCItems.PHIAL_VITREUS.get());
        output.accept(TCItems.PHIAL_METALLUM.get());
        output.accept(TCItems.PHIAL_VICTUS.get());
        output.accept(TCItems.PHIAL_MORTUUS.get());
        output.accept(TCItems.PHIAL_POTENTIA.get());
        output.accept(TCItems.PHIAL_PERMUTATIO.get());
        output.accept(TCItems.PHIAL_PRAECANTATIO.get());
        output.accept(TCItems.PHIAL_AURAM.get());
        output.accept(TCItems.PHIAL_ALKIMIA.get());
        output.accept(TCItems.PHIAL_VITIUM.get());
        output.accept(TCItems.PHIAL_TENEBRAE.get());
        output.accept(TCItems.PHIAL_ALIENIS.get());
        output.accept(TCItems.PHIAL_VOLATUS.get());
        output.accept(TCItems.PHIAL_HERBA.get());
        output.accept(TCItems.PHIAL_INSTRUMENTUM.get());
        output.accept(TCItems.PHIAL_FABRICO.get());
        output.accept(TCItems.PHIAL_MACHINA.get());
        output.accept(TCItems.PHIAL_VINCULUM.get());
        output.accept(TCItems.PHIAL_SPIRITUS.get());
        output.accept(TCItems.PHIAL_COGNITIO.get());
        output.accept(TCItems.PHIAL_SENSUS.get());
        output.accept(TCItems.PHIAL_AVERSIO.get());
        output.accept(TCItems.PHIAL_PRAEMUNIO.get());
        output.accept(TCItems.PHIAL_DESIDERIUM.get());
        output.accept(TCItems.PHIAL_EXANIMIS.get());
        output.accept(TCItems.PHIAL_BESTIA.get());
        output.accept(TCItems.PHIAL_HUMANUS.get());
    }
}

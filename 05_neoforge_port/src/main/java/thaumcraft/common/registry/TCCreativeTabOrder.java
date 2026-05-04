package thaumcraft.common.registry;

import net.minecraft.world.item.CreativeModeTab;

/**
 * Controls the visible Thaumcraft creative tab order.
 *
 * <p>Do not sort this class alphabetically and do not rely on registry declaration order.
 * The visible order should follow the Thaumcraft 6 1.12.2 creative inventory screenshots.</p>
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
    }
}
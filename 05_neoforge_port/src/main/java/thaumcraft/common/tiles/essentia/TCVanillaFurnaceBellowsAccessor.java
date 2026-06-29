package thaumcraft.common.tiles.essentia;

import java.lang.reflect.Field;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

/**
 * Isolated legacy Bellows bridge for vanilla furnace cook progress.
 *
 * <p>Mojang keeps furnace progress package-private in 1.21.1. TC6 Bellows directly modified
 * {@code TileEntityFurnace#cookTime}, so the port keeps that behavior behind one small adapter
 * instead of spreading reflective access through the device code.</p>
 */
final class TCVanillaFurnaceBellowsAccessor {
    private static final Field COOKING_PROGRESS = field("cookingProgress");
    private static final Field COOKING_TOTAL_TIME = field("cookingTotalTime");

    private TCVanillaFurnaceBellowsAccessor() {
    }

    static boolean isAvailable() {
        return COOKING_PROGRESS != null && COOKING_TOTAL_TIME != null;
    }

    static boolean boostCookProgress(AbstractFurnaceBlockEntity furnace) {
        if (!isAvailable()) {
            return false;
        }
        try {
            int progress = Math.max(0, COOKING_PROGRESS.getInt(furnace));
            int total = Math.max(1, COOKING_TOTAL_TIME.getInt(furnace));
            if (progress <= 0 || progress >= total - 1) {
                return false;
            }
            COOKING_PROGRESS.setInt(furnace, progress + 1);
            furnace.setChanged();
            return true;
        } catch (IllegalAccessException ignored) {
            return false;
        }
    }

    private static Field field(String name) {
        try {
            Field field = AbstractFurnaceBlockEntity.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}

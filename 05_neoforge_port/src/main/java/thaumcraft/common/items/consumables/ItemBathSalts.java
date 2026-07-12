package thaumcraft.common.items.consumables;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** TC6 bath salts item contract: short dropped-item lifetime before purifying-fluid conversion. */
public class ItemBathSalts extends Item {
    public static final int LEGACY_ENTITY_LIFESPAN = 200;

    public ItemBathSalts() {
        super(new Item.Properties());
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, Level level) {
        return LEGACY_ENTITY_LIFESPAN;
    }
}

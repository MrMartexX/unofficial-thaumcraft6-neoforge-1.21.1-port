package thaumcraft.common.items.armor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.items.IVisDiscountGear;

public class ItemVisDiscountGear extends Item implements IVisDiscountGear {
    private final int visDiscount;

    public ItemVisDiscountGear(int visDiscount) {
        this(visDiscount, new Item.Properties().stacksTo(1));
    }

    public ItemVisDiscountGear(int visDiscount, Item.Properties properties) {
        super(properties);
        this.visDiscount = visDiscount;
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return visDiscount;
    }
}

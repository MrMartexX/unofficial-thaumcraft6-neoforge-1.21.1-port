package thaumcraft.common.items.curios;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import thaumcraft.api.items.IVisDiscountGear;

public class TCBaubleItem extends Item implements IVisDiscountGear {
    public enum Type {
        AMULET,
        RING,
        BELT,
        CHARM
    }

    private final Type type;
    private final int visDiscount;

    public TCBaubleItem(Type type, Rarity rarity, int visDiscount) {
        super(new Item.Properties().stacksTo(1).rarity(rarity));
        this.type = type;
        this.visDiscount = visDiscount;
    }

    public Type getBaubleType() {
        return type;
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return visDiscount;
    }
}

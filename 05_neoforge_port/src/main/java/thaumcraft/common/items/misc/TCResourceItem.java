package thaumcraft.common.items.misc;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class TCResourceItem extends Item {
    public TCResourceItem(Rarity rarity) {
        super(new Item.Properties().rarity(rarity));
    }
}

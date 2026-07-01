package thaumcraft.common.items.casters;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ItemFocusPouch extends Item {
    public static final int LEGACY_SLOT_COUNT = 18;

    public ItemFocusPouch() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }
}

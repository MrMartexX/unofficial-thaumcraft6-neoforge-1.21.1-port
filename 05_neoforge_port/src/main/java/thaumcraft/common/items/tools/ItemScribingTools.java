package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;
import thaumcraft.api.items.IScribeTools;

/**
 * Port-safe version of the legacy scribing tools item.
 */
public class ItemScribingTools extends Item implements IScribeTools {
    public ItemScribingTools() {
        super(new Properties().stacksTo(1).durability(100));
    }
}

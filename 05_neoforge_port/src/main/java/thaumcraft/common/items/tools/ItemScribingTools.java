package thaumcraft.common.items.tools;

import thaumcraft.common.items.ItemLegacyPlaceholder;

/**
 * Port-safe version of the legacy scribing tools item.
 *
 * <p>Legacy TC6 used a stack size of 1 and max damage 100. The research-table ink consumption behaviour is not ported
 * yet, but the stack/durability shape is now compatible with that future implementation.</p>
 */
public class ItemScribingTools extends ItemLegacyPlaceholder {
    public ItemScribingTools() {
        super(new Properties().durability(100), "tc.placeholder.scribing_tools");
    }
}

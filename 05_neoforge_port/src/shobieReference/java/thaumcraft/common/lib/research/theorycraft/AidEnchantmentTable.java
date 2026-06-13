package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Enchantment table aid - adds enchantment cards when near the research table.
 */
public class AidEnchantmentTable implements ITheorycraftAid {

    @Override
    public Object getAidObject() {
        return new ItemStack(Blocks.ENCHANTING_TABLE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends TheorycraftCard>[] getCards() {
        return new Class[] { CardEnchantment.class };
    }
}

package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Dragon egg aid - adds dragon egg cards when near the research table.
 * Very rare and powerful.
 */
public class AidDragonEgg implements ITheorycraftAid {

    @Override
    public Object getAidObject() {
        return new ItemStack(Blocks.DRAGON_EGG);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends TheorycraftCard>[] getCards() {
        return new Class[] { CardDragonEgg.class };
    }
}

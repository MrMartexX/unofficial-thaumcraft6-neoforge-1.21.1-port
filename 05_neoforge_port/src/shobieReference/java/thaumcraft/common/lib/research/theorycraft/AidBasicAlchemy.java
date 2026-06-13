package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.init.ModBlocks;

/**
 * Alchemy aid - adds alchemy-focused cards when a crucible is nearby.
 * Provides cards related to essence manipulation and alchemical synthesis.
 */
public class AidBasicAlchemy implements ITheorycraftAid {

    @Override
    public Object getAidObject() {
        return new ItemStack(ModBlocks.CRUCIBLE.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends TheorycraftCard>[] getCards() {
        return new Class[] { CardConcentrate.class, CardReactions.class, CardSynthesis.class };
    }
}

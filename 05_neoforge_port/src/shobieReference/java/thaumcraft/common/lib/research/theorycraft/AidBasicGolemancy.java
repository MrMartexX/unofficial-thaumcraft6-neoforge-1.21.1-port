package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.init.ModBlocks;

/**
 * Golemancy aid - adds golemancy-focused cards when a golem builder is nearby.
 * Provides cards related to golem construction and automation.
 */
public class AidBasicGolemancy implements ITheorycraftAid {

    @Override
    public Object getAidObject() {
        return new ItemStack(ModBlocks.GOLEM_BUILDER.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends TheorycraftCard>[] getCards() {
        return new Class[] { CardSculpting.class, CardScripting.class, CardSynergy.class };
    }
}

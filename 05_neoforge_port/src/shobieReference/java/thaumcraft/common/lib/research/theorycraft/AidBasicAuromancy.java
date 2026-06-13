package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.init.ModBlocks;

/**
 * Auromancy aid - adds auromancy-focused cards when a focal manipulator is nearby.
 * Provides cards related to focus construction and magical channeling.
 * 
 * Note: In TC6, the wand workbench was replaced with the focal manipulator.
 */
public class AidBasicAuromancy implements ITheorycraftAid {

    @Override
    public Object getAidObject() {
        return new ItemStack(ModBlocks.FOCAL_MANIPULATOR.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends TheorycraftCard>[] getCards() {
        return new Class[] { CardFocus.class, CardAwareness.class, CardSpellbinding.class };
    }
}

package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.init.ModBlocks;

/**
 * Infusion aid - adds infusion-focused cards when an infusion matrix is nearby.
 * Provides cards related to magical infusion and stability management.
 */
public class AidBasicInfusion implements ITheorycraftAid {

    @Override
    public Object getAidObject() {
        return new ItemStack(ModBlocks.INFUSION_MATRIX.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends TheorycraftCard>[] getCards() {
        return new Class[] { CardMeasure.class, CardChannel.class, CardInfuse.class };
    }
}

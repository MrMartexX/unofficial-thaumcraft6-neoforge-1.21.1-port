package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.init.ModBlocks;

/**
 * Eldritch aid - adds eldritch-focused cards when eldritch stone is nearby.
 * Provides cards related to forbidden knowledge and cosmic truths.
 * 
 * Note: Uses ELDRITCH_STONE_TILE as a representative eldritch block.
 */
public class AidBasicEldritch implements ITheorycraftAid {

    @Override
    public Object getAidObject() {
        return new ItemStack(ModBlocks.ELDRITCH_STONE_TILE.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends TheorycraftCard>[] getCards() {
        return new Class[] { CardRealization.class, CardRevelation.class, CardTruth.class };
    }
}

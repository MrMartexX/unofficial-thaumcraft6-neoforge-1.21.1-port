package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.world.level.block.Blocks;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Bookshelf aid - adds Balance, Notation, and Study cards when
 * a bookshelf is near the research table.
 */
public class AidBookshelf implements ITheorycraftAid {

    @Override
    public Object getAidObject() {
        return Blocks.BOOKSHELF;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends TheorycraftCard>[] getCards() {
        return new Class[] {
            CardBalance.class, 
            CardNotation.class, 
            CardNotation.class, 
            CardStudy.class, 
            CardStudy.class, 
            CardStudy.class
        };
    }
}

package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Beacon aid - adds beacon cards when a beacon is near the research table.
 */
public class AidBeacon implements ITheorycraftAid {

    @Override
    public Object getAidObject() {
        return new ItemStack(Blocks.BEACON);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends TheorycraftCard>[] getCards() {
        return new Class[] { CardBeacon.class };
    }
}

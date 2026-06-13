package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.init.ModBlocks;

/**
 * Artifice aid - adds artifice-focused cards when an arcane workbench is nearby.
 * Provides cards related to magical construction and calibration.
 */
public class AidBasicArtifice implements ITheorycraftAid {

    @Override
    public Object getAidObject() {
        return new ItemStack(ModBlocks.ARCANE_WORKBENCH.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends TheorycraftCard>[] getCards() {
        return new Class[] { CardCalibrate.class, CardTinker.class, CardMindOverMatter.class };
    }
}

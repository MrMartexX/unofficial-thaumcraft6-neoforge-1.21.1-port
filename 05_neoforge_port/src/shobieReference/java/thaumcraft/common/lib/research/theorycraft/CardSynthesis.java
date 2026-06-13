package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Synthesis card - Alchemy card about combining elements.
 * Grants Alchemy progress.
 */
public class CardSynthesis extends TheorycraftCard {

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "ALCHEMY";
    }

    @Override
    public String getLocalizedName() {
        return "card.synthesis.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.synthesis.text";
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), Mth.nextInt(player.getRandom(), 20, 30));
        return true;
    }
}

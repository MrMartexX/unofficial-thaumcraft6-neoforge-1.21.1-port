package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Synergy card - Infusion card about combining magical effects.
 * Grants Infusion progress with bonus draws.
 */
public class CardSynergy extends TheorycraftCard {

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "INFUSION";
    }

    @Override
    public String getLocalizedName() {
        return "card.synergy.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.synergy.text";
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), Mth.nextInt(player.getRandom(), 10, 20));
        data.bonusDraws++;
        return true;
    }
}

package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Reactions card - Alchemy card focused on alchemical reactions.
 * Grants progress to Alchemy with chance for bonus inspiration.
 */
public class CardReactions extends TheorycraftCard {

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
        return "card.reactions.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.reactions.text";
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), Mth.nextInt(player.getRandom(), 15, 25));
        if (player.getRandom().nextFloat() < 0.25f) {
            data.addInspiration(1);
        }
        return true;
    }
}

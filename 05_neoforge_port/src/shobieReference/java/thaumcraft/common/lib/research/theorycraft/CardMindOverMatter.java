package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Mind Over Matter card - Golemancy card that grants progress and bonus draws.
 * Represents mastering the control of constructs through willpower.
 */
public class CardMindOverMatter extends TheorycraftCard {

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "GOLEMANCY";
    }

    @Override
    public String getLocalizedName() {
        return "card.mindovermatter.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.mindovermatter.text";
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), Mth.nextInt(player.getRandom(), 15, 25));
        if (player.getRandom().nextFloat() < 0.5f) {
            data.bonusDraws++;
        }
        return true;
    }
}

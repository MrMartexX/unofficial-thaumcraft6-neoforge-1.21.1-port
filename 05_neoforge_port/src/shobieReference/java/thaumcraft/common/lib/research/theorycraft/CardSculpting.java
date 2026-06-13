package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Sculpting card - Golemancy card about shaping golem forms.
 * Grants Golemancy progress with chance for bonus draw.
 */
public class CardSculpting extends TheorycraftCard {

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
        return "card.sculpting.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.sculpting.text";
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), Mth.nextInt(player.getRandom(), 15, 25));
        if (player.getRandom().nextBoolean()) {
            data.bonusDraws++;
        }
        return true;
    }
}

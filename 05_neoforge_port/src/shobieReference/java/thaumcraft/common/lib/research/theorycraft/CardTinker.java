package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Tinker card - Artifice card about making small adjustments.
 * Grants Artifice progress with chance for inspiration.
 */
public class CardTinker extends TheorycraftCard {

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "ARTIFICE";
    }

    @Override
    public String getLocalizedName() {
        return "card.tinker.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.tinker.text";
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), Mth.nextInt(player.getRandom(), 15, 25));
        if (player.getRandom().nextFloat() < 0.2f) {
            data.addInspiration(1);
        }
        return true;
    }
}

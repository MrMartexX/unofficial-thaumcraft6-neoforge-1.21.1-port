package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Spellbinding card - Infusion card about binding magical effects.
 * Grants Infusion progress.
 */
public class CardSpellbinding extends TheorycraftCard {

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
        return "card.spellbinding.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.spellbinding.text";
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), Mth.nextInt(player.getRandom(), 15, 25));
        return true;
    }
}

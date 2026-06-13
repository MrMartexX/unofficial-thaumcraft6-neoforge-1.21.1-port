package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Truth card - A breakthrough that grants progress to all categories.
 * High cost but universal benefit.
 */
public class CardTruth extends TheorycraftCard {

    @Override
    public int getInspirationCost() {
        return 2;
    }

    @Override
    public String getLocalizedName() {
        return "card.truth.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.truth.text";
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        for (String category : ResearchCategories.researchCategories.keySet()) {
            data.addTotal(category, Mth.nextInt(player.getRandom(), 5, 10));
        }
        return true;
    }
}

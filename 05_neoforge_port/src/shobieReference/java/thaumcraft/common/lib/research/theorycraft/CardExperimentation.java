package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Experimentation card - adds random progress to a random category.
 * Risk/reward card with variable outcome.
 */
public class CardExperimentation extends TheorycraftCard {

    @Override
    public int getInspirationCost() {
        return 2;
    }

    @Override
    public String getLocalizedName() {
        return "card.experimentation.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.experimentation.text";
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        try {
            String[] s = ResearchCategories.researchCategories.keySet().toArray(new String[0]);
            if (s.length == 0) return false;
            
            String cat = s[player.getRandom().nextInt(s.length)];
            data.addTotal(cat, Mth.nextInt(player.getRandom(), 15, 30));
            data.addTotal("BASICS", Mth.nextInt(player.getRandom(), 1, 10));
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}

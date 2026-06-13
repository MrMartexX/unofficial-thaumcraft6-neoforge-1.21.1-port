package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Glyphs card - Aid-only card that grants progress to Eldritch category.
 * Comes from glyphed ancient stone.
 */
public class CardGlyphs extends TheorycraftCard {

    @Override
    public boolean isAidOnly() {
        return true;
    }

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "ELDRITCH";
    }

    @Override
    public String getLocalizedName() {
        return "card.glyphs.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.glyphs.text";
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

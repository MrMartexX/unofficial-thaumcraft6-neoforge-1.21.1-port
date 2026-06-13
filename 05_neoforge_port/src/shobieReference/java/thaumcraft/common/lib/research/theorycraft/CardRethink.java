package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.world.entity.player.Player;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Rethink card - unblocks all blocked categories.
 * Useful when you've blocked important categories.
 */
public class CardRethink extends TheorycraftCard {

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getLocalizedName() {
        return "card.rethink.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.rethink.text";
    }

    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        // Only useful if there are blocked categories
        return !data.categoriesBlocked.isEmpty();
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        if (data.categoriesBlocked.isEmpty()) return false;
        data.categoriesBlocked.clear();
        data.addTotal("BASICS", 2);
        return true;
    }
}

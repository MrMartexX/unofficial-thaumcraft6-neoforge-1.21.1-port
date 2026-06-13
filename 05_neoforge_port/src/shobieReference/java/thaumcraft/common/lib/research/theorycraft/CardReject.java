package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.world.entity.player.Player;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Reject card - discard current cards and gain minor progress.
 * Useful when you don't like your current options.
 */
public class CardReject extends TheorycraftCard {

    @Override
    public int getInspirationCost() {
        return 0; // Free to use
    }

    @Override
    public String getLocalizedName() {
        return "card.reject.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.reject.text";
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        // Small bonus to basics for discarding
        data.addTotal("BASICS", 3);
        return true;
    }
}

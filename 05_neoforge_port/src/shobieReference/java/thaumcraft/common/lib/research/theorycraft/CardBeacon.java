package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Beacon card - Aid-only card from nearby beacon.
 * Grants significant progress to Auromancy and Artifice.
 */
public class CardBeacon extends TheorycraftCard {

    @Override
    public boolean isAidOnly() {
        return true;
    }

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getLocalizedName() {
        return "card.beacon.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.beacon.text";
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal("AUROMANCY", Mth.nextInt(player.getRandom(), 15, 25));
        data.addTotal("ARTIFICE", Mth.nextInt(player.getRandom(), 15, 25));
        data.penaltyStart++;
        return true;
    }
}

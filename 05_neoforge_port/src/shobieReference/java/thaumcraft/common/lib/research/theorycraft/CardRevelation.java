package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Revelation card - Eldritch card with high risk/reward.
 * Grants major Eldritch progress but adds warp.
 */
public class CardRevelation extends TheorycraftCard {

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
        return "card.revelation.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.revelation.text";
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), Mth.nextInt(player.getRandom(), 25, 40));
        ThaumcraftApi.internalMethods.addWarpToPlayer(player, 2, IPlayerWarp.EnumWarpType.NORMAL);
        data.bonusDraws++;
        return true;
    }
}

package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Portal card - Aid-only card from nearby nether/end portal.
 * Grants Eldritch progress with some warp.
 */
public class CardPortal extends TheorycraftCard {

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
        return "card.portal.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.portal.text";
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal("ELDRITCH", Mth.nextInt(player.getRandom(), 20, 30));
        ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.TEMPORARY);
        if (player.getRandom().nextBoolean()) {
            data.bonusDraws++;
        }
        return true;
    }
}

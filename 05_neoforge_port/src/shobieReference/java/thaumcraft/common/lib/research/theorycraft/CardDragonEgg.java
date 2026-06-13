package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Dragon Egg card - Aid-only card from nearby dragon egg.
 * Grants massive Eldritch progress but significant warp.
 */
public class CardDragonEgg extends TheorycraftCard {

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
        return "card.dragonegg.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.dragonegg.text";
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal("ELDRITCH", Mth.nextInt(player.getRandom(), 30, 50));
        data.addTotal("AUROMANCY", Mth.nextInt(player.getRandom(), 10, 20));
        ThaumcraftApi.internalMethods.addWarpToPlayer(player, 3, IPlayerWarp.EnumWarpType.NORMAL);
        data.bonusDraws += 2;
        return true;
    }
}

package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

/**
 * Awareness card - Auromancy category card that has a chance to add Eldritch progress and warp.
 * Represents glimpsing beyond the veil.
 */
public class CardAwareness extends TheorycraftCard {

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "AUROMANCY";
    }

    @Override
    public String getLocalizedName() {
        return "card.awareness.name";
    }

    @Override
    public String getLocalizedText() {
        return "card.awareness.text";
    }

    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 20);
        if (player.getRandom().nextFloat() < 0.33f) {
            data.addTotal("ELDRITCH", Mth.nextInt(player.getRandom(), 1, 5));
            ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.NORMAL);
        }
        return true;
    }
}

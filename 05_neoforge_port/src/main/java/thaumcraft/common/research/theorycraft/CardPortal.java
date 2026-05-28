package thaumcraft.common.research.theorycraft;

import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import thaumcraft.common.warp.TCPlayerWarpStore;
import thaumcraft.common.warp.TCWarpType;

final class CardPortal extends TCTheorycraftCard {
    @Override
    public boolean isAidOnly() {
        return true;
    }

    @Override
    public int getInspirationCost() {
        return -1;
    }

    @Override
    public String getResearchCategory() {
        return "ELDRITCH";
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.portal.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.portal.text");
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        Random fallback = TCTheorycraftRandom.seeded(getSeed());
        data.addTotal(TCTheorycraftRandom.category(player, fallback), TCTheorycraftRandom.between(player, fallback, 5, 10));
        data.addTotal(TCTheorycraftRandom.category(player, fallback), TCTheorycraftRandom.between(player, fallback, 5, 10));
        data.addTotal("ELDRITCH", TCTheorycraftRandom.between(player, fallback, 5, 10));
        data.bonusDraws += 2;
        if (player != null) {
            TCPlayerWarpStore.add(player, TCWarpType.TEMPORARY, 5);
            TCPlayerWarpStore.add(player, TCWarpType.NORMAL, 1);
        }
        return true;
    }
}

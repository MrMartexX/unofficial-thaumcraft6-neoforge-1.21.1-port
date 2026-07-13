package thaumcraft.common.research.theorycraft;

import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import thaumcraft.common.warp.TCWarpManager;
import thaumcraft.common.warp.TCWarpType;

final class CardTruth extends TCTheorycraftCard {
    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "ELDRITCH";
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.truth.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.truth.text");
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        Random fallback = TCTheorycraftRandom.seeded(getSeed());
        data.addTotal("ELDRITCH", TCTheorycraftRandom.between(player, fallback, 10, 25));
        data.bonusDraws++;
        if (player != null) {
            TCWarpManager.add(player, TCWarpType.TEMPORARY, 3);
        }
        return true;
    }
}

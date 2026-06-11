package thaumcraft.common.research.theorycraft;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class CardBeacon extends TCTheorycraftCard {
    @Override
    public int getInspirationCost() {
        return -2;
    }

    @Override
    public boolean isAidOnly() {
        return true;
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.beacon.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.beacon.text");
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        data.bonusDraws++;
        data.penaltyStart++;
        return true;
    }
}

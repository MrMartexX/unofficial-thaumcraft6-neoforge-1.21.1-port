package thaumcraft.common.research.theorycraft;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class CardMeasure extends TCTheorycraftCard {
    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "INFUSION";
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.measure.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.measure.text");
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        data.addTotal(getResearchCategory(), 15);
        data.bonusDraws++;
        return true;
    }
}

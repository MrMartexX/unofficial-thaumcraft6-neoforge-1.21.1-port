package thaumcraft.common.research.theorycraft;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class CardFocus extends TCTheorycraftCard {
    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "AUROMANCY";
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.focus.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.focus.text");
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        data.addTotal(getResearchCategory(), 15);
        data.bonusDraws++;
        return true;
    }
}

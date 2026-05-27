package thaumcraft.common.research.theorycraft;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class CardSpellbinding extends TCTheorycraftCard {
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
        return Component.translatable("card.spellbinding.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.spellbinding.text");
    }

    @Override
    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        return player != null && player.experienceLevel > 0;
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        if (player == null || player.experienceLevel <= 0) {
            return false;
        }

        int levels = Math.min(5, player.experienceLevel);
        data.addTotal(getResearchCategory(), levels * 5);
        player.giveExperienceLevels(-levels);
        return true;
    }
}

package thaumcraft.common.research.theorycraft;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class CardEnchantment extends TCTheorycraftCard {
    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public boolean isAidOnly() {
        return true;
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.enchantment.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.enchantment.text");
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        if (player == null || player.experienceLevel < 5) {
            return false;
        }

        player.giveExperienceLevels(-5);
        data.addTotal("INFUSION", CardStudy.randomBetween(player, 15, 20));
        data.addTotal("AUROMANCY", CardStudy.randomBetween(player, 15, 20));
        return true;
    }
}

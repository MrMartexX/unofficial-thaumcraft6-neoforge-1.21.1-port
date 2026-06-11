package thaumcraft.common.research.theorycraft;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import thaumcraft.common.research.TCResearchManager;

final class CardExperimentation extends TCTheorycraftCard {
    @Override
    public int getInspirationCost() {
        return 2;
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.experimentation.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.experimentation.text");
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        List<String> categories = TCResearchManager.categoryKeys();
        if (categories.isEmpty()) {
            return false;
        }
        String category = categories.get(player.getRandom().nextInt(categories.size()));
        data.addTotal(category, CardStudy.randomBetween(player, 15, 30));
        data.addTotal("BASICS", CardStudy.randomBetween(player, 1, 10));
        return true;
    }
}

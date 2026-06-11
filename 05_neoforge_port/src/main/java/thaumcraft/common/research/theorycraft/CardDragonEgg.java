package thaumcraft.common.research.theorycraft;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import thaumcraft.common.research.TCResearchManager;

final class CardDragonEgg extends TCTheorycraftCard {
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
        return Component.translatable("card.dragonegg.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.dragonegg.text");
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        List<String> categories = TCResearchManager.categories().stream()
                .map(category -> category.key())
                .toList();
        if (categories.isEmpty()) {
            return false;
        }

        RandomSource random = player == null ? RandomSource.create(getSeed()) : player.getRandom();
        for (int index = 0; index < 10; index++) {
            String category = categories.get(random.nextInt(categories.size()));
            data.addTotal(category, 2 + random.nextInt(4));
        }
        return true;
    }
}

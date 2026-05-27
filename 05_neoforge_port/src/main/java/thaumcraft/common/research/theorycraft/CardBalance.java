package thaumcraft.common.research.theorycraft;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class CardBalance extends TCTheorycraftCard {
    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.balance.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.balance.text");
    }

    @Override
    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        int total = 0;
        int size = 0;
        for (String category : data.categoryTotals.keySet()) {
            if (data.categoriesBlocked.contains(category)) {
                continue;
            }
            total += data.categoryTotals.get(category);
            size++;
        }
        return data.categoriesBlocked.size() < data.categoryTotals.size() - 1 && total >= size;
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        int total = 0;
        int size = 0;
        for (String category : data.categoryTotals.keySet()) {
            if (data.categoriesBlocked.contains(category)) {
                continue;
            }
            total += data.categoryTotals.get(category);
            size++;
        }
        if (data.categoriesBlocked.size() >= data.categoryTotals.size() - 1 || total < size || size <= 0) {
            return false;
        }
        for (String category : data.categoryTotals.keySet()) {
            if (!data.categoriesBlocked.contains(category)) {
                data.categoryTotals.put(category, total / size);
            }
        }
        data.addTotal("BASICS", 5);
        data.penaltyStart++;
        return true;
    }
}

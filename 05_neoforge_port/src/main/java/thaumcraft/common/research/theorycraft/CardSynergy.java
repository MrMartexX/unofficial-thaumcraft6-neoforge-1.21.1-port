package thaumcraft.common.research.theorycraft;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class CardSynergy extends TCTheorycraftCard {
    private static final String[] INPUT_CATEGORIES = {"ARTIFICE", "ALCHEMY", "INFUSION"};

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "GOLEMANCY";
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.synergy.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.synergy.text");
    }

    @Override
    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        return inputTotal(data) >= 15;
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        if (inputTotal(data) < 15) {
            return false;
        }

        int remaining = 15;
        int tries = 0;
        while (remaining > 0 && tries < 1000) {
            tries++;
            for (String category : INPUT_CATEGORIES) {
                if (data.getTotal(category) > 0) {
                    data.addTotal(category, -1);
                    remaining--;
                    if (remaining <= 0) {
                        break;
                    }
                }
            }
        }

        data.addTotal("GOLEMANCY", 30);
        data.penaltyStart++;
        return true;
    }

    private static int inputTotal(TCResearchTableData data) {
        int total = 0;
        for (String category : INPUT_CATEGORIES) {
            total += data.getTotal(category);
        }
        return total;
    }
}

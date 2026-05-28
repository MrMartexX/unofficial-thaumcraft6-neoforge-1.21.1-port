package thaumcraft.common.research.theorycraft;

import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import thaumcraft.common.warp.TCPlayerWarpStore;
import thaumcraft.common.warp.TCWarpType;

final class CardRealization extends TCTheorycraftCard {
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
        return Component.translatable("card.realization.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.realization.text");
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        Random fallback = TCTheorycraftRandom.seeded(getSeed());
        data.addTotal(TCTheorycraftRandom.category(player, fallback), TCTheorycraftRandom.between(player, fallback, 5, 10));
        data.addTotal(TCTheorycraftRandom.category(player, fallback), TCTheorycraftRandom.between(player, fallback, 5, 10));
        data.addTotal("ELDRITCH", 15);
        if (player != null) {
            TCPlayerWarpStore.add(player, TCWarpType.TEMPORARY, 5);
            if (TCTheorycraftRandom.nextBoolean(player, fallback)) {
                TCPlayerWarpStore.add(player, TCWarpType.NORMAL, 1);
            }
        }
        return true;
    }
}

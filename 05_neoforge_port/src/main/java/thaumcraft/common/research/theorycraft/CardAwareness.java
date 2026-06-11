package thaumcraft.common.research.theorycraft;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import thaumcraft.common.warp.TCPlayerWarpStore;
import thaumcraft.common.warp.TCWarpType;

final class CardAwareness extends TCTheorycraftCard {
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
        return Component.translatable("card.awareness.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.awareness.text");
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        data.addTotal(getResearchCategory(), 20);
        if (player != null && player.getRandom().nextFloat() < 0.33F) {
            data.addTotal("ELDRITCH", Mth.nextInt(player.getRandom(), 1, 5));
            TCPlayerWarpStore.add(player, TCWarpType.NORMAL, 1);
        }
        return true;
    }
}

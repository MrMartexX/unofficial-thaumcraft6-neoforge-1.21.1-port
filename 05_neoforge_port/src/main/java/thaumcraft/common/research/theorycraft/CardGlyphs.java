package thaumcraft.common.research.theorycraft;

import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import thaumcraft.common.warp.TCPlayerWarpStore;
import thaumcraft.common.warp.TCWarpType;

final class CardGlyphs extends TCTheorycraftCard {
    @Override
    public boolean isAidOnly() {
        return true;
    }

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
        return Component.translatable("card.glyph.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.glyph.text");
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        Random fallback = TCTheorycraftRandom.seeded(getSeed());
        data.addTotal(TCTheorycraftRandom.category(player, fallback), TCTheorycraftRandom.between(player, fallback, 10, 20));
        data.addTotal("ELDRITCH", TCTheorycraftRandom.between(player, fallback, 10, 20));
        if (player != null) {
            TCPlayerWarpStore.add(player, TCWarpType.TEMPORARY, 5);
        }
        return true;
    }
}

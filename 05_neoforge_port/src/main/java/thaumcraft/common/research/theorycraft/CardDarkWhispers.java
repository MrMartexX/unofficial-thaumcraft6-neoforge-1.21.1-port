package thaumcraft.common.research.theorycraft;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import thaumcraft.common.research.TCResearchManager;
import thaumcraft.common.warp.TCPlayerWarpStore;
import thaumcraft.common.warp.TCWarpType;

final class CardDarkWhispers extends TCTheorycraftCard {
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
        return Component.translatable("card.darkwhisper.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.darkwhisper.text");
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        if (player == null) {
            return false;
        }

        int levels = player.experienceLevel;
        player.giveExperienceLevels(-(10 + levels));
        if (levels > 0) {
            for (String category : TCResearchManager.categoryKeys()) {
                if (!player.getRandom().nextBoolean()) {
                    data.addTotal(category, Mth.nextInt(player.getRandom(), 0, Math.max(1, (int) Math.sqrt(levels))));
                }
            }
        }

        data.addTotal("ELDRITCH", Mth.nextInt(player.getRandom(), Math.max(1, levels / 5), Math.max(5, levels / 2)));
        TCPlayerWarpStore.add(player, TCWarpType.NORMAL, Math.max(1, (int) Math.sqrt(levels)));
        if (player.getRandom().nextBoolean()) {
            data.bonusDraws++;
        }
        return true;
    }
}

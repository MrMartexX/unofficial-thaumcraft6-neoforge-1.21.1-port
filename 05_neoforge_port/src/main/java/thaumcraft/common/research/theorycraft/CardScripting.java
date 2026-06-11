package thaumcraft.common.research.theorycraft;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import thaumcraft.common.tiles.crafting.TCResearchTableBlockEntity;

final class CardScripting extends TCTheorycraftCard {
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
        return Component.translatable("card.scripting.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.scripting.text");
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        TCResearchTableBlockEntity table = data == null ? null : data.getTable();
        if (table == null || !table.hasUsableScribingTools() || table.getPaperCount() <= 0) {
            return false;
        }
        table.consumeInkFromTable();
        table.consumePaperFromTable();
        data.addTotal(getResearchCategory(), 25);
        return true;
    }
}

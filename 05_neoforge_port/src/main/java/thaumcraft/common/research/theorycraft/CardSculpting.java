package thaumcraft.common.research.theorycraft;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

final class CardSculpting extends TCTheorycraftCard {
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
        return Component.translatable("card.sculpting.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.sculpting.text");
    }

    @Override
    public List<ItemStack> getRequiredItems() {
        return List.of(new ItemStack(Items.CLAY_BALL));
    }

    @Override
    public List<Boolean> getRequiredItemsConsumed() {
        return List.of(true);
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        data.addTotal(getResearchCategory(), 20);
        data.bonusDraws++;
        return true;
    }
}

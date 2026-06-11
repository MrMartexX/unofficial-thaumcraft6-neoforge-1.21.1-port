package thaumcraft.common.research.theorycraft;

import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.TCAspectVariantStacks;

final class CardReactions extends TCTheorycraftCard {
    private Aspect aspect1;
    private Aspect aspect2;

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putString("aspect1", aspect1 == null ? "" : aspect1.getTag());
        tag.putString("aspect2", aspect2 == null ? "" : aspect2.getTag());
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        aspect1 = Aspect.getAspect(tag.getString("aspect1"));
        aspect2 = Aspect.getAspect(tag.getString("aspect2"));
    }

    @Override
    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        if (Aspect.getCompoundAspects().size() < 2) {
            return false;
        }

        Random random = new Random(getSeed());
        int firstIndex = random.nextInt(Aspect.getCompoundAspects().size());
        int secondIndex = firstIndex;
        while (secondIndex == firstIndex) {
            secondIndex = random.nextInt(Aspect.getCompoundAspects().size());
        }

        aspect1 = Aspect.getCompoundAspects().get(firstIndex);
        aspect2 = Aspect.getCompoundAspects().get(secondIndex);
        return aspect1 != null && aspect2 != null;
    }

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "ALCHEMY";
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.reactions.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.reactions.text", CardConcentrate.aspectName(aspect1), CardConcentrate.aspectName(aspect2));
    }

    @Override
    public List<ItemStack> getRequiredItems() {
        return List.of(TCAspectVariantStacks.crystal(aspect1), TCAspectVariantStacks.crystal(aspect2));
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        data.addTotal(getResearchCategory(), 25);
        if (player != null && player.getRandom().nextFloat() < 0.33F) {
            data.addInspiration(1);
        }
        return true;
    }
}

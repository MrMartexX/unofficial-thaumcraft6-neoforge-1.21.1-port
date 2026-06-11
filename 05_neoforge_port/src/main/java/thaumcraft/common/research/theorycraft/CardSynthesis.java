package thaumcraft.common.research.theorycraft;

import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.TCAspectVariantStacks;

final class CardSynthesis extends TCTheorycraftCard {
    private Aspect aspect1;
    private Aspect aspect2;
    private Aspect aspect3;

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putString("aspect1", aspect1 == null ? "" : aspect1.getTag());
        tag.putString("aspect2", aspect2 == null ? "" : aspect2.getTag());
        tag.putString("aspect3", aspect3 == null ? "" : aspect3.getTag());
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        aspect1 = Aspect.getAspect(tag.getString("aspect1"));
        aspect2 = Aspect.getAspect(tag.getString("aspect2"));
        aspect3 = Aspect.getAspect(tag.getString("aspect3"));
    }

    @Override
    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        if (Aspect.getCompoundAspects().isEmpty()) {
            return false;
        }

        Random random = new Random(getSeed());
        aspect3 = Aspect.getCompoundAspects().get(random.nextInt(Aspect.getCompoundAspects().size()));
        if (aspect3 == null || aspect3.getComponents() == null || aspect3.getComponents().length < 2) {
            return false;
        }
        aspect1 = aspect3.getComponents()[0];
        aspect2 = aspect3.getComponents()[1];
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
        return Component.translatable("card.synthesis.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.synthesis.text", CardConcentrate.aspectName(aspect1), CardConcentrate.aspectName(aspect2));
    }

    @Override
    public List<ItemStack> getRequiredItems() {
        return List.of(TCAspectVariantStacks.crystal(aspect1), TCAspectVariantStacks.crystal(aspect2));
    }

    @Override
    public List<Boolean> getRequiredItemsConsumed() {
        return List.of(true, true);
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        data.addTotal(getResearchCategory(), 40);
        if (player != null && player.getRandom().nextFloat() < 0.33F) {
            data.addInspiration(1);
        }
        if (player != null) {
            ItemStack result = TCAspectVariantStacks.crystal(aspect3);
            if (!result.isEmpty() && !player.getInventory().add(result)) {
                player.drop(result, true);
            }
        }
        return true;
    }
}

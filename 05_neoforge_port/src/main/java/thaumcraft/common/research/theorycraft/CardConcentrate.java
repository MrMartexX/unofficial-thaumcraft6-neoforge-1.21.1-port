package thaumcraft.common.research.theorycraft;

import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.TCAspectVariantStacks;

final class CardConcentrate extends TCTheorycraftCard {
    private Aspect aspect;

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putString("aspect", aspect == null ? "" : aspect.getTag());
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        aspect = Aspect.getAspect(tag.getString("aspect"));
    }

    @Override
    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        Random random = new Random(getSeed());
        if (Aspect.getCompoundAspects().isEmpty()) {
            return false;
        }
        aspect = Aspect.getCompoundAspects().get(random.nextInt(Aspect.getCompoundAspects().size()));
        return aspect != null;
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
        return Component.translatable("card.concentrate.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.concentrate.text", aspectName(aspect));
    }

    @Override
    public List<ItemStack> getRequiredItems() {
        return List.of(TCAspectVariantStacks.crystal(aspect));
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        data.addTotal(getResearchCategory(), 15);
        data.bonusDraws++;
        if (player != null && player.getRandom().nextFloat() < 0.33F) {
            data.addInspiration(1);
        }
        return true;
    }

    static Component aspectName(Aspect aspect) {
        if (aspect == null) {
            return Component.empty();
        }
        return Component.translatable("tc.aspect." + aspect.getTag());
    }
}

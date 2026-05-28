package thaumcraft.common.research.theorycraft;

import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.common.items.TCAspectVariantStacks;

final class CardInfuse extends TCTheorycraftCard {
    private Aspect aspect;
    private ItemStack stack = ItemStack.EMPTY;

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putString("aspect", aspect == null ? "" : aspect.getTag());
        if (!stack.isEmpty()) {
            tag.putString("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        }
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        aspect = Aspect.getAspect(tag.getString("aspect"));
        stack = TCTheorycraftItemOptions.stackFromId(tag.getString("item"));
    }

    @Override
    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        if (Aspect.getCompoundAspects().isEmpty()) {
            return false;
        }

        List<ItemStack> options = TCTheorycraftItemOptions.infusionOptions();
        if (options.isEmpty()) {
            return false;
        }

        Random random = new Random(getSeed());
        aspect = Aspect.getCompoundAspects().get(random.nextInt(Aspect.getCompoundAspects().size()));
        stack = options.get(random.nextInt(options.size())).copy();
        return aspect != null && !stack.isEmpty();
    }

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "INFUSION";
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.infuse.name");
    }

    @Override
    public Component getLocalizedText() {
        Component aspectText = CardConcentrate.aspectName(aspect).copy().withStyle(ChatFormatting.BOLD);
        return Component.translatable("card.infuse.text", aspectText, stack.getHoverName(), getVal());
    }

    @Override
    public List<ItemStack> getRequiredItems() {
        ItemStack phial = TCAspectVariantStacks.phial(aspect);
        return stack.isEmpty() || phial.isEmpty() ? List.of() : List.of(stack.copy(), phial);
    }

    @Override
    public List<Boolean> getRequiredItemsConsumed() {
        return stack.isEmpty() || aspect == null ? List.of() : List.of(true, true);
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        data.addTotal(getResearchCategory(), getVal());
        return true;
    }

    private int getVal() {
        int value = 10;
        try {
            value += (int) (Math.sqrt(AspectHelper.getObjectAspects(stack).visSize()) * 1.5D);
        } catch (RuntimeException ignored) {
        }
        return value;
    }
}

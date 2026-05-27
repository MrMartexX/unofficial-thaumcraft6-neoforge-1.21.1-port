package thaumcraft.common.research.theorycraft;

import java.util.List;
import java.util.Random;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.AspectHelper;

final class CardMindOverMatter extends TCTheorycraftCard {
    private ItemStack stack = ItemStack.EMPTY;

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        if (!stack.isEmpty()) {
            tag.putString("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        }
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        stack = TCTheorycraftItemOptions.stackFromId(tag.getString("item"));
    }

    @Override
    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        List<ItemStack> options = TCTheorycraftItemOptions.artificeOptions();
        if (options.isEmpty()) {
            return false;
        }
        stack = options.get(new Random(getSeed()).nextInt(options.size())).copy();
        return !stack.isEmpty();
    }

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return "ARTIFICE";
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.mindmatter.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.mindmatter.text", getVal());
    }

    @Override
    public List<ItemStack> getRequiredItems() {
        return stack.isEmpty() ? List.of() : List.of(stack.copy());
    }

    @Override
    public List<Boolean> getRequiredItemsConsumed() {
        return stack.isEmpty() ? List.of() : List.of(true);
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        data.addTotal(getResearchCategory(), getVal());
        return true;
    }

    private int getVal() {
        try {
            return 10 + (int) Math.sqrt(AspectHelper.getObjectAspects(stack).visSize());
        } catch (RuntimeException ignored) {
            return 10;
        }
    }
}

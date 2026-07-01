package thaumcraft.common.items.casters;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import thaumcraft.common.items.components.TCFocusPackageComponent;

public class ItemFocus extends Item {
    private final int maxComplexity;

    public ItemFocus(int maxComplexity) {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
        this.maxComplexity = maxComplexity;
    }

    public int maxComplexity() {
        return maxComplexity;
    }

    public TCFocusPackageComponent getPackage(ItemStack stack) {
        return TCFocusPackageHelper.getPackage(stack);
    }

    public void setPackage(ItemStack stack, TCFocusPackageComponent packageData) {
        TCFocusPackageHelper.setPackage(stack, packageData);
    }

    public float getVisCost(ItemStack stack) {
        return TCFocusPackageHelper.getVisCost(getPackage(stack));
    }

    public int getActivationTime(ItemStack stack) {
        return TCFocusPackageHelper.getActivationTime(getPackage(stack));
    }

    public int getFocusColor(ItemStack stack) {
        return getPackage(stack).color();
    }

    public String getSortingHelper(ItemStack stack) {
        return TCFocusPackageHelper.sortingHelper(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        TCFocusPackageComponent packageData = getPackage(stack);
        tooltipComponents.add(Component.translatable("tc.focus.max_complexity", maxComplexity).withStyle(ChatFormatting.DARK_PURPLE));
        if (!packageData.isEmpty()) {
            tooltipComponents.add(Component.translatable("tc.focus.complexity", packageData.complexity()).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(Component.translatable("tc.focus.vis_cost", getVisCost(stack)).withStyle(ChatFormatting.GRAY));
        }
    }
}

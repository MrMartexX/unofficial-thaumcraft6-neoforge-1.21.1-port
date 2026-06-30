package thaumcraft.common.items;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.registry.TCDataComponents;

/** Blank/filled jar label item pair used by the modern registry split. */
public final class TCJarLabelItem extends Item {
    private final boolean filledVariant;

    public TCJarLabelItem(boolean filledVariant) {
        super(new Item.Properties());
        this.filledVariant = filledVariant;
    }

    public boolean filledVariant() {
        return filledVariant;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        Aspect aspect = TCEssentiaItemHelper.aspectFromStack(stack);
        if (aspect != null) {
            tooltipComponents.add(Component.translatable("tc.aspect." + aspect.getTag()).withStyle(ChatFormatting.DARK_PURPLE));
        } else if (stack.has(TCDataComponents.ASPECT_STACK.get())) {
            tooltipComponents.add(Component.literal("Invalid aspect label").withStyle(ChatFormatting.DARK_RED));
        }
    }
}

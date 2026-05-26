package thaumcraft.common.items;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thaumcraft.api.aspects.Aspect;

/**
 * Temporary flattened compatibility item for legacy aspect-sensitive stacks.
 *
 * <p>Thaumcraft 6 represented crystal essence and filled phials as a shared item id plus NBT aspect data.
 * Until the port has a full DataComponent-based stack representation, these flattened registry ids keep research,
 * recipes, creative testing and display names aspect-aware without losing the aspect semantic.</p>
 */
public class ItemAspectVariant extends Item {
    private final Kind kind;
    private final String aspectTag;
    private final int amount;

    public ItemAspectVariant(Kind kind, String aspectTag, int amount) {
        super(new Item.Properties());
        this.kind = kind;
        this.aspectTag = aspectTag;
        this.amount = amount;
    }

    public Kind kind() {
        return kind;
    }

    public String aspectTag() {
        return aspectTag;
    }

    public int amount() {
        return amount;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        Aspect aspect = Aspect.getAspect(aspectTag);
        Component aspectName = aspect == null
                ? Component.literal(aspectTag)
                : Component.translatable("tc.aspect." + aspectTag);

        tooltipComponents.add(Component.literal("Aspect: ")
                .append(aspectName)
                .withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.literal("Amount: " + amount)
                .withStyle(ChatFormatting.DARK_AQUA));
    }

    public enum Kind {
        CRYSTAL_ESSENCE,
        PHIAL
    }
}

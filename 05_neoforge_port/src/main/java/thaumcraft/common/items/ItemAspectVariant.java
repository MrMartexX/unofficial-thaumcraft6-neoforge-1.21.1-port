package thaumcraft.common.items;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.components.TCAspectStackComponent;
import thaumcraft.common.registry.TCDataComponents;

/**
 * Flattened compatibility item for legacy aspect-sensitive stacks.
 *
 * <p>Thaumcraft 6 represented crystal essence and filled phials as a shared item id plus NBT aspect data. During this
 * migration checkpoint the port still keeps one flattened registry id per aspect for research and creative testing, but
 * the stack now also carries the modern aspect payload through a DataComponent.</p>
 */
public class ItemAspectVariant extends Item {
    private final Kind kind;
    private final TCAspectStackComponent defaultAspectStack;

    public ItemAspectVariant(Kind kind, String aspectTag, int amount) {
        this(kind, new TCAspectStackComponent(aspectTag, amount));
    }

    private ItemAspectVariant(Kind kind, TCAspectStackComponent defaultAspectStack) {
        super(new Item.Properties().component(TCDataComponents.ASPECT_STACK.get(), defaultAspectStack));
        this.kind = kind;
        this.defaultAspectStack = defaultAspectStack;
    }

    public Kind kind() {
        return kind;
    }

    public String aspectTag() {
        return defaultAspectStack.aspect();
    }

    public int amount() {
        return defaultAspectStack.amount();
    }

    public TCAspectStackComponent defaultAspectStack() {
        return defaultAspectStack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        TCAspectStackComponent aspectStack = stack.get(TCDataComponents.ASPECT_STACK.get());
        if (aspectStack == null || aspectStack.isEmpty()) {
            aspectStack = defaultAspectStack;
        }

        String aspectTag = aspectStack.aspect();
        Aspect aspect = Aspect.getAspect(aspectTag);
        Component aspectName = aspect == null
                ? Component.literal(aspectTag)
                : Component.translatable("tc.aspect." + aspectTag);

        tooltipComponents.add(Component.literal("Aspect: ")
                .append(aspectName)
                .withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.literal("Amount: " + aspectStack.amount())
                .withStyle(ChatFormatting.DARK_AQUA));
    }

    public enum Kind {
        CRYSTAL_ESSENCE,
        PHIAL
    }
}

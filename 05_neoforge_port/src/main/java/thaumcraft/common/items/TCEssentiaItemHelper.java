package thaumcraft.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.components.TCAspectStackComponent;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;

public final class TCEssentiaItemHelper {
    private TCEssentiaItemHelper() {
    }

    public static Aspect aspectFromStack(ItemStack stack) {
        TCAspectStackComponent component = stack.get(TCDataComponents.ASPECT_STACK.get());
        return component == null ? null : Aspect.getAspect(component.aspect());
    }

    public static int aspectAmount(ItemStack stack) {
        TCAspectStackComponent component = stack.get(TCDataComponents.ASPECT_STACK.get());
        return component == null ? 0 : component.amount();
    }

    public static String filterTag(ItemStack stack) {
        String filter = stack.get(TCDataComponents.ASPECT_FILTER.get());
        return filter == null ? "" : filter.trim().toLowerCase(java.util.Locale.ROOT);
    }

    public static Aspect filterAspect(ItemStack stack) {
        return Aspect.getAspect(filterTag(stack));
    }

    public static void setAspect(ItemStack stack, Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) {
            stack.remove(TCDataComponents.ASPECT_STACK.get());
            return;
        }
        stack.set(TCDataComponents.ASPECT_STACK.get(), new TCAspectStackComponent(aspect.getTag(), amount));
    }

    public static void setFilter(ItemStack stack, Aspect aspect) {
        if (aspect == null) {
            stack.remove(TCDataComponents.ASPECT_FILTER.get());
            return;
        }
        stack.set(TCDataComponents.ASPECT_FILTER.get(), aspect.getTag());
    }

    public static ItemStack emptyPhial() {
        return new ItemStack(TCItems.PHIAL.get());
    }

    public static ItemStack filledPhial(Aspect aspect) {
        return TCAspectVariantStacks.phial(aspect);
    }

    public static ItemStack blankLabel() {
        return new ItemStack(TCItems.JAR_LABEL.get());
    }

    public static ItemStack filledLabel(Aspect aspect) {
        ItemStack stack = new ItemStack(TCItems.JAR_LABEL_ESSENCE.get());
        setAspect(stack, aspect, 1);
        return stack;
    }

    public static boolean isEmptyPhial(ItemStack stack) {
        return stack.is(TCItems.PHIAL.get());
    }

    public static boolean isFilledPhial(ItemStack stack) {
        return stack.getItem() instanceof ItemAspectVariant variant
                && variant.kind() == ItemAspectVariant.Kind.PHIAL
                && aspectFromStack(stack) != null
                && aspectAmount(stack) >= TCPhialItem.BASE_AMOUNT;
    }

    public static boolean isLabel(ItemStack stack) {
        return stack.is(TCItems.JAR_LABEL.get()) || stack.is(TCItems.JAR_LABEL_ESSENCE.get());
    }

    public static void replaceOneInHand(Player player, ItemStack held, ItemStack result, BlockPos dropPos) {
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }
        if (!result.isEmpty() && !player.getInventory().add(result)) {
            Level level = player.level();
            double x = dropPos.getX() + 0.5D;
            double y = dropPos.getY() + 0.5D;
            double z = dropPos.getZ() + 0.5D;
            level.addFreshEntity(new ItemEntity(level, x, y, z, result));
        }
        player.inventoryMenu.broadcastChanges();
    }
}

package thaumcraft.common.items.armor;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.common.registry.TCArmorMaterials;
import thaumcraft.common.registry.TCDataComponents;

public class ItemFortressArmor extends ArmorItem implements IVisDiscountGear, IGoggles, IRevealer {
    public ItemFortressArmor(Type type) {
        super(
                TCArmorMaterials.FORTRESS,
                type,
                TCArmorMaterials.armorProperties(type, 40).rarity(Rarity.RARE)
        );
    }

    @Override
    public boolean showNodes(ItemStack stack, LivingEntity player) {
        return hasGoggles(stack);
    }

    @Override
    public boolean showIngamePopups(ItemStack stack, LivingEntity player) {
        return hasGoggles(stack);
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        int discount = 2;
        if (hasGoggles(stack)) {
            discount += 5;
        }
        return discount;
    }

    public static boolean hasGoggles(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (Boolean.TRUE.equals(stack.get(TCDataComponents.FORTRESS_GOGGLES.get()))) {
            return true;
        }
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        return customData != null && customData.contains("goggles");
    }

    public static int getMask(ItemStack stack) {
        if (stack.isEmpty()) {
            return -1;
        }
        Integer mask = stack.get(TCDataComponents.FORTRESS_MASK.get());
        if (mask != null) {
            return mask;
        }
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("mask")) {
            return customData.copyTag().getInt("mask");
        }
        return -1;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (hasGoggles(stack)) {
            tooltipComponents.add(Component.translatable("item.goggles.name").withStyle(ChatFormatting.DARK_PURPLE));
        }
        int mask = getMask(stack);
        if (mask >= 0) {
            tooltipComponents.add(Component.translatable("item.fortress_helm.mask." + mask).withStyle(ChatFormatting.GOLD));
        }
    }
}

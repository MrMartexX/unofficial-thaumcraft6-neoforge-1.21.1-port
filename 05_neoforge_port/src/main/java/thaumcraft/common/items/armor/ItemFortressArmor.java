package thaumcraft.common.items.armor;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import thaumcraft.Thaumcraft;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.common.registry.TCArmorMaterials;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;

public class ItemFortressArmor extends ArmorItem implements IVisDiscountGear, IGoggles, IRevealer {
    private static final ResourceLocation FORTRESS_MASK_ARMOR =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "fortress_mask_armor");

    public ItemFortressArmor(Type type) {
        this(type, false);
    }

    public ItemFortressArmor(Type type, boolean defaultGoggles) {
        super(
                TCArmorMaterials.FORTRESS,
                type,
                applyGoggles(TCArmorMaterials.armorProperties(type, 40).rarity(Rarity.RARE), defaultGoggles)
        );
    }

    private static Item.Properties applyGoggles(Item.Properties props, boolean goggles) {
        if (goggles) {
            props.component(TCDataComponents.FORTRESS_GOGGLES.get(), true);
        }
        return props;
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

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, net.minecraft.world.entity.EquipmentSlot slot, net.minecraft.world.item.ArmorMaterial.Layer layer, boolean innerModel) {
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/armor/fortress_armor.png");
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers base = super.getDefaultAttributeModifiers(stack);
        if (getType() == Type.HELMET && getMask(stack) >= 0) {
            return base.withModifierAdded(
                    Attributes.ARMOR,
                    new AttributeModifier(
                            FORTRESS_MASK_ARMOR,
                            1.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.HEAD
            );
        }
        return base;
    }

    public static boolean hasGoggles(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (Boolean.TRUE.equals(stack.get(TCDataComponents.FORTRESS_GOGGLES.get()))) {
            return true;
        }
        try {
            if (TCItems.THAUMIUM_FORTRESS_HELM_GOGGLES != null && stack.is(TCItems.THAUMIUM_FORTRESS_HELM_GOGGLES.get())) {
                return true;
            }
        } catch (Exception ignored) {
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

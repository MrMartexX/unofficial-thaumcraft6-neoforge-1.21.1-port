package thaumcraft.common.items.armor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.registry.TCArmorMaterials;

public class ItemVoidRobeArmor extends ArmorItem implements IVisDiscountGear, IRevealer, IGoggles, IWarpingGear {
    public ItemVoidRobeArmor(Type type) {
        super(
                TCArmorMaterials.VOID_ROBE,
                type,
                TCArmorMaterials.armorProperties(type, 18)
                        .rarity(Rarity.EPIC)
                        .component(DataComponents.DYED_COLOR, new DyedItemColor(ItemRobeArmor.LEGACY_DEFAULT_COLOR, false))
        );
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!level.isClientSide() && stack.isDamaged() && entity instanceof LivingEntity && entity.tickCount % 20 == 0) {
            stack.setDamageValue(Math.max(0, stack.getDamageValue() - 1));
        }
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return 5;
    }

    @Override
    public boolean showNodes(ItemStack stack, LivingEntity wearer) {
        return getType() == Type.HELMET;
    }

    @Override
    public boolean showIngamePopups(ItemStack stack, LivingEntity wearer) {
        return getType() == Type.HELMET;
    }

    @Override
    public int getWarp(ItemStack stack, Player player) {
        return 3;
    }
}

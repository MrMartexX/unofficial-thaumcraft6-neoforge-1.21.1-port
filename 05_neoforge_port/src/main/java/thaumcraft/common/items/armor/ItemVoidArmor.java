package thaumcraft.common.items.armor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.registry.TCArmorMaterials;

/** TC6 Void Metal armor: light durability, passive warp and one durability restored per second. */
public class ItemVoidArmor extends ArmorItem implements IWarpingGear {
    public ItemVoidArmor(Type type) {
        super(
                TCArmorMaterials.VOID,
                type,
                TCArmorMaterials.armorProperties(type, 10).rarity(Rarity.UNCOMMON)
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
    public int getWarp(ItemStack stack, Player player) {
        return 1;
    }
}

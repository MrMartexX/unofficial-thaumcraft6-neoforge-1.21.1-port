package thaumcraft.common.items.armor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.common.registry.TCArmorMaterials;
import thaumcraft.common.world.aura.TCAuraNetwork;

public class ItemGoggles extends ArmorItem implements IVisDiscountGear, IRevealer, IGoggles {
    public ItemGoggles() {
        super(TCArmorMaterials.GOGGLES, Type.HELMET, new Properties().durability(350).rarity(Rarity.RARE));
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return 5;
    }

    @Override
    public boolean showNodes(ItemStack stack, LivingEntity wearer) {
        return true;
    }

    @Override
    public boolean showIngamePopups(ItemStack stack, LivingEntity wearer) {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!level.isClientSide() && entity instanceof ServerPlayer player
                && player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).is(this)
                && player.tickCount % 20 == 0) {
            TCAuraNetwork.sendAuraToPlayer(player, player.blockPosition());
        }
    }
}

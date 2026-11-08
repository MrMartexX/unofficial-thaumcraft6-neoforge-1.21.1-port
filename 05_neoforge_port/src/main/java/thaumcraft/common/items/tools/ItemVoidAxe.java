package thaumcraft.common.items.tools;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.IWarpingGear;

public class ItemVoidAxe extends AxeItem implements IWarpingGear {
    public ItemVoidAxe() {
        super(
                TCToolTiers.VOID,
                new Item.Properties()
                        .rarity(Rarity.UNCOMMON)
                        .attributes(AxeItem.createAttributes(TCToolTiers.VOID, 5.0F, -3.0F))
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
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.level().isClientSide()) {
            if (!(target instanceof Player) || (attacker.getServer() != null && attacker.getServer().isPvpAllowed())) {
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80));
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public int getWarp(ItemStack stack, Player player) {
        return 1;
    }
}

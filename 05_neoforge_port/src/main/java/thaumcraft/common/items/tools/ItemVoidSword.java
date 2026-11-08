package thaumcraft.common.items.tools;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.IWarpingGear;

public class ItemVoidSword extends SwordItem implements IWarpingGear {
    public ItemVoidSword() {
        super(
                TCToolTiers.VOID,
                new Item.Properties()
                        .rarity(Rarity.UNCOMMON)
                        .attributes(SwordItem.createAttributes(TCToolTiers.VOID, 3, -2.4F))
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
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60));
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public int getWarp(ItemStack stack, Player player) {
        return 1;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("enchantment.special.sapless").withStyle(ChatFormatting.GOLD));
    }
}

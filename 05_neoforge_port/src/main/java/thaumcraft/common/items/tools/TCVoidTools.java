package thaumcraft.common.items.tools;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;

public final class TCVoidTools {
    private TCVoidTools() {
    }

    static void repairTick(ItemStack stack, Level level, Entity entity) {
        if (!level.isClientSide && stack.isDamaged() && entity instanceof LivingEntity && entity.tickCount % 20 == 0) {
            stack.setDamageValue(Math.max(0, stack.getDamageValue() - 1));
        }
    }

    static void applyWeakness(LivingEntity target, LivingEntity attacker, int ticks) {
        if (target.level().isClientSide) {
            return;
        }

        MinecraftServer server = attacker.getServer();
        if (target instanceof Player && attacker instanceof Player && server != null && !server.isPvpAllowed()) {
            return;
        }

        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, ticks));
    }

    public static class Axe extends AxeItem {
        public Axe(Tier tier, Item.Properties properties) {
            super(tier, properties);
        }

        @Override
        public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
            super.inventoryTick(stack, level, entity, slotId, isSelected);
            repairTick(stack, level, entity);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            applyWeakness(target, attacker, 80);
            return super.hurtEnemy(stack, target, attacker);
        }
    }

    public static class Pickaxe extends PickaxeItem {
        public Pickaxe(Tier tier, Item.Properties properties) {
            super(tier, properties);
        }

        @Override
        public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
            super.inventoryTick(stack, level, entity, slotId, isSelected);
            repairTick(stack, level, entity);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            applyWeakness(target, attacker, 80);
            return super.hurtEnemy(stack, target, attacker);
        }
    }

    public static class Shovel extends ShovelItem {
        public Shovel(Tier tier, Item.Properties properties) {
            super(tier, properties);
        }

        @Override
        public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
            super.inventoryTick(stack, level, entity, slotId, isSelected);
            repairTick(stack, level, entity);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            applyWeakness(target, attacker, 80);
            return super.hurtEnemy(stack, target, attacker);
        }
    }

    public static class Hoe extends HoeItem {
        public Hoe(Tier tier, Item.Properties properties) {
            super(tier, properties);
        }

        @Override
        public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
            super.inventoryTick(stack, level, entity, slotId, isSelected);
            repairTick(stack, level, entity);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            applyWeakness(target, attacker, 80);
            return super.hurtEnemy(stack, target, attacker);
        }
    }

    public static class Sword extends SwordItem {
        public Sword(Tier tier, Item.Properties properties) {
            super(tier, properties);
        }

        @Override
        public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
            super.inventoryTick(stack, level, entity, slotId, isSelected);
            repairTick(stack, level, entity);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            applyWeakness(target, attacker, 60);
            return super.hurtEnemy(stack, target, attacker);
        }
    }
}

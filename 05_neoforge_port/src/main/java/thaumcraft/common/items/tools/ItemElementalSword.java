package thaumcraft.common.items.tools;

import java.util.List;
import java.util.Map;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.items.components.TCInfusionEnchantmentsComponent;
import thaumcraft.common.registry.TCDataComponents;

public class ItemElementalSword extends SwordItem {
    public ItemElementalSword() {
        super(
                TCToolTiers.ELEMENTAL,
                new Item.Properties()
                        .rarity(Rarity.RARE)
                        .attributes(SwordItem.createAttributes(TCToolTiers.ELEMENTAL, 3, -2.4F))
                        .component(
                                TCDataComponents.INFUSION_ENCHANTS.get(),
                                new TCInfusionEnchantmentsComponent(Map.of(
                                        TCEnumInfusionEnchantment.ARCING, 2
                                ))
                        )
        );
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(player.getItemInHand(usedHand));
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        int ticks = getUseDuration(stack, entity) - count;
        if (entity instanceof Player player) {
            Vec3 motion = player.getDeltaMovement();
            if (motion.y < 0.0D) {
                player.setDeltaMovement(motion.x, motion.y + 0.08D, motion.z);
                if (player.getDeltaMovement().y > 0.5D) {
                    player.setDeltaMovement(player.getDeltaMovement().x, 0.5D, player.getDeltaMovement().z);
                }
                player.fallDistance = 0.0F;
            } else if (!player.onGround()) {
                player.setDeltaMovement(motion.x, Math.min(motion.y + 0.05D, 0.25D), motion.z);
                player.fallDistance = 0.0F;
            }

            AABB box = player.getBoundingBox().inflate(2.5D);
            List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && !e.isAlliedTo(player));
            for (LivingEntity target : list) {
                Vec3 push = target.position().subtract(player.position()).normalize().scale(0.3D);
                target.setDeltaMovement(target.getDeltaMovement().add(push.x, 0.1D, push.z));
            }

            if (level.isClientSide() && ticks % 2 == 0) {
                double px = player.getX() + (level.random.nextDouble() - 0.5D) * 1.5D;
                double py = player.getY() + level.random.nextDouble() * 1.5D;
                double pz = player.getZ() + (level.random.nextDouble() - 0.5D) * 1.5D;
                level.addParticle(ParticleTypes.CLOUD, px, py, pz, 0.0D, 0.05D, 0.0D);
            }
            if (ticks > 0 && ticks % 20 == 0) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.5F, 1.8F);
                if (!level.isClientSide()) {
                    stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                }
            }
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isDeadOrDying() && attacker instanceof Player player) {
            Level level = attacker.level();
            AABB dropBox = target.getBoundingBox().inflate(4.0D);
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, dropBox);
            for (ItemEntity item : items) {
                Vec3 toPlayer = player.position().subtract(item.position()).normalize().scale(0.3D);
                item.setDeltaMovement(toPlayer);
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}

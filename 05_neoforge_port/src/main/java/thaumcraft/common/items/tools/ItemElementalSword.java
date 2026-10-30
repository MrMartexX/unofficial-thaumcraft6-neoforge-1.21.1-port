package thaumcraft.common.items.tools;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ItemElementalSword extends SwordItem {
    public ItemElementalSword() {
        super(
                TCToolTiers.ELEMENTAL,
                new Item.Properties()
                        .rarity(Rarity.RARE)
                        .attributes(SwordItem.createAttributes(TCToolTiers.ELEMENTAL, 3, -2.4F))
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        // Zephyr wind gust: push mobs away and propel player forward
        Vec3 look = player.getLookAngle();
        player.setDeltaMovement(player.getDeltaMovement().add(look.x * 0.4D, 0.2D, look.z * 0.4D));
        player.fallDistance = 0.0F;

        AABB box = player.getBoundingBox().inflate(5.0D);
        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && !e.isAlliedTo(player));
        for (LivingEntity target : list) {
            Vec3 push = target.position().subtract(player.position()).normalize().scale(0.8D);
            target.setDeltaMovement(target.getDeltaMovement().add(push.x, 0.3D, push.z));
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.5F);

        if (level.isClientSide()) {
            for (int i = 0; i < 15; i++) {
                double px = player.getX() + (level.random.nextDouble() - 0.5D) * 3.0D;
                double py = player.getY() + level.random.nextDouble() * 2.0D;
                double pz = player.getZ() + (level.random.nextDouble() - 0.5D) * 3.0D;
                level.addParticle(ParticleTypes.CLOUD, px, py, pz, look.x * 0.2D, 0.05D, look.z * 0.2D);
            }
        }

        player.getCooldowns().addCooldown(this, 20);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Pull drops towards attacker when killing
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

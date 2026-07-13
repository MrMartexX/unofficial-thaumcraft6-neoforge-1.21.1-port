package thaumcraft.common.items.consumables;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import thaumcraft.common.entities.TCAlumentumEntity;

/** TC6 Alumentum throw/fuel item. Smelter fuel behavior is handled by the fuel and smelter audits. */
public class ItemAlumentum extends Item implements ProjectileItem {
    public ItemAlumentum() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        float pitch = TCAlumentumEntity.LEGACY_THROW_SOUND_PITCH_NUMERATOR
                / (level.random.nextFloat() * TCAlumentumEntity.LEGACY_THROW_SOUND_RANDOM_MULTIPLIER
                + TCAlumentumEntity.LEGACY_THROW_SOUND_BASE);
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.EGG_THROW,
                SoundSource.NEUTRAL,
                TCAlumentumEntity.LEGACY_THROW_SOUND_VOLUME,
                pitch
        );

        if (!level.isClientSide) {
            TCAlumentumEntity entity = new TCAlumentumEntity(level, player);
            entity.shootFromRotation(
                    player,
                    player.getXRot(),
                    player.getYRot(),
                    TCAlumentumEntity.LEGACY_THROW_X_ROT_OFFSET,
                    TCAlumentumEntity.LEGACY_THROW_REQUESTED_VELOCITY,
                    TCAlumentumEntity.LEGACY_THROW_INACCURACY
            );
            level.addFreshEntity(entity);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public Projectile asProjectile(Level level, Position position, ItemStack stack, Direction direction) {
        TCAlumentumEntity entity = new TCAlumentumEntity(level, position.x(), position.y(), position.z());
        entity.setItem(stack);
        return entity;
    }

    @Override
    public DispenseConfig createDispenseConfig() {
        return DispenseConfig.builder()
                .power(TCAlumentumEntity.LEGACY_ACTUAL_PROJECTILE_VELOCITY)
                .uncertainty(TCAlumentumEntity.LEGACY_THROW_INACCURACY)
                .build();
    }
}

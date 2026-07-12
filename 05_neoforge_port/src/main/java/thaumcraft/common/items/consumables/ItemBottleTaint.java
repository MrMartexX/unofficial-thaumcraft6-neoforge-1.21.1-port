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
import thaumcraft.common.entities.TCBottleTaintEntity;

/** TC6 Bottle of Taint: egg-like throw item with legacy projectile constants. */
public class ItemBottleTaint extends Item implements ProjectileItem {
    public static final int LEGACY_MAX_STACK_SIZE = 8;
    public static final float LEGACY_THROW_VELOCITY = 0.66F;
    public static final float LEGACY_THROW_INACCURACY = 1.0F;
    public static final float LEGACY_THROW_X_ROT_OFFSET = -5.0F;

    public ItemBottleTaint() {
        super(new Item.Properties().stacksTo(LEGACY_MAX_STACK_SIZE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        float pitch = 0.4F / (level.random.nextFloat() * 0.4F + 0.8F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EGG_THROW, SoundSource.NEUTRAL, 0.5F, pitch);

        if (!level.isClientSide) {
            TCBottleTaintEntity entity = new TCBottleTaintEntity(level, player);
            entity.setItem(stack);
            entity.shootFromRotation(
                    player,
                    player.getXRot(),
                    player.getYRot(),
                    LEGACY_THROW_X_ROT_OFFSET,
                    LEGACY_THROW_VELOCITY,
                    LEGACY_THROW_INACCURACY
            );
            level.addFreshEntity(entity);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public Projectile asProjectile(Level level, Position position, ItemStack stack, Direction direction) {
        TCBottleTaintEntity entity = new TCBottleTaintEntity(level, position.x(), position.y(), position.z());
        entity.setItem(stack);
        return entity;
    }

    @Override
    public DispenseConfig createDispenseConfig() {
        return DispenseConfig.builder()
                .power(LEGACY_THROW_VELOCITY)
                .uncertainty(LEGACY_THROW_INACCURACY)
                .build();
    }
}

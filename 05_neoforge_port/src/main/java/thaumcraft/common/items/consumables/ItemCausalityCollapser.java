package thaumcraft.common.items.consumables;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.common.entities.TCCausalityCollapserEntity;

/** TC6 Causality Collapser throw item. Rift collapse behavior is owned by the projectile. */
public class ItemCausalityCollapser extends Item {
    public ItemCausalityCollapser() {
        super(new Item.Properties().stacksTo(TCCausalityCollapserEntity.LEGACY_MAX_STACK_SIZE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        float pitch = TCCausalityCollapserEntity.LEGACY_THROW_SOUND_PITCH_NUMERATOR
                / (level.random.nextFloat() * TCCausalityCollapserEntity.LEGACY_THROW_SOUND_RANDOM_MULTIPLIER
                + TCCausalityCollapserEntity.LEGACY_THROW_SOUND_BASE);
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.EGG_THROW,
                SoundSource.NEUTRAL,
                TCCausalityCollapserEntity.LEGACY_THROW_SOUND_VOLUME,
                pitch
        );

        if (!level.isClientSide) {
            TCCausalityCollapserEntity entity = new TCCausalityCollapserEntity(level, player);
            entity.shootFromRotation(
                    player,
                    player.getXRot(),
                    player.getYRot(),
                    TCCausalityCollapserEntity.LEGACY_THROW_X_ROT_OFFSET,
                    TCCausalityCollapserEntity.LEGACY_PROJECTILE_VELOCITY,
                    TCCausalityCollapserEntity.LEGACY_THROW_INACCURACY
            );
            level.addFreshEntity(entity);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}

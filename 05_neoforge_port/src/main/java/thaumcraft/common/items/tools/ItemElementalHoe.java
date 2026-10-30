package thaumcraft.common.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ItemElementalHoe extends HoeItem {
    public ItemElementalHoe() {
        super(
                TCToolTiers.ELEMENTAL,
                new Item.Properties()
                        .rarity(Rarity.RARE)
                        .attributes(HoeItem.createAttributes(TCToolTiers.ELEMENTAL, -3.0F, 0.0F))
        );
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        // Check if clicked block or adjacent blocks can be bonemealed (growth boost)
        boolean grew = false;
        if (!level.isClientSide()) {
            for (BlockPos p : BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 1, 1))) {
                BlockState s = level.getBlockState(p);
                if (s.getBlock() instanceof BonemealableBlock growable) {
                    if (growable.isValidBonemealTarget(level, p, s)) {
                        growable.performBonemeal(level.getServer().getLevel(level.dimension()), level.random, p, s);
                        grew = true;
                    }
                }
            }
            if (grew) {
                level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                if (player != null && !player.getAbilities().instabuild) {
                    context.getItemInHand().hurtAndBreak(1, player, Player.getSlotForHand(context.getHand()));
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }
}

package thaumcraft.common.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
        ItemStack stack = context.getItemInHand();

        // 1. Try 3x3 growth bonemeal boost
        boolean grew = false;
        if (!level.isClientSide()) {
            for (BlockPos p : BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 1, 1))) {
                BlockState s = level.getBlockState(p);
                if (s.getBlock() instanceof BonemealableBlock growable) {
                    if (growable.isValidBonemealTarget(level, p, s)) {
                        ServerLevel serverLevel = (ServerLevel) level;
                        growable.performBonemeal(serverLevel, level.random, p, s);
                        grew = true;
                    }
                }
            }
            if (grew) {
                level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                if (player != null && !player.getAbilities().instabuild) {
                    stack.hurtAndBreak(1, player, Player.getSlotForHand(context.getHand()));
                }
                return InteractionResult.SUCCESS;
            }
        } else if (level.getBlockState(pos).getBlock() instanceof BonemealableBlock) {
            return InteractionResult.SUCCESS;
        }

        // 2. 3x3 tilling when not sneaking
        if (player != null && !player.isShiftKeyDown() && context.getClickedFace() == Direction.UP) {
            boolean tilled = false;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos target = pos.offset(dx, 0, dz);
                    BlockState targetState = level.getBlockState(target);
                    BlockPos above = target.above();
                    BlockState aboveState = level.getBlockState(above);

                    if (aboveState.isAir() || aboveState.canBeReplaced()) {
                        if (targetState.is(Blocks.GRASS_BLOCK) || targetState.is(Blocks.DIRT) || targetState.is(Blocks.DIRT_PATH)) {
                            if (!level.isClientSide()) {
                                level.setBlock(target, Blocks.FARMLAND.defaultBlockState(), 11);
                            }
                            tilled = true;
                        } else if (targetState.is(Blocks.COARSE_DIRT)) {
                            if (!level.isClientSide()) {
                                level.setBlock(target, Blocks.DIRT.defaultBlockState(), 11);
                            }
                            tilled = true;
                        } else if (targetState.is(Blocks.ROOTED_DIRT)) {
                            if (!level.isClientSide()) {
                                level.setBlock(target, Blocks.DIRT.defaultBlockState(), 11);
                                Block.popResource(level, above, new ItemStack(Blocks.HANGING_ROOTS));
                            }
                            tilled = true;
                        }
                    }
                }
            }
            if (tilled) {
                level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!level.isClientSide() && !player.getAbilities().instabuild) {
                    stack.hurtAndBreak(1, player, Player.getSlotForHand(context.getHand()));
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return super.useOn(context);
    }
}

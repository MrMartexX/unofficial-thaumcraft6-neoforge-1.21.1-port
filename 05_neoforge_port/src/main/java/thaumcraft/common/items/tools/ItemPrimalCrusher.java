package thaumcraft.common.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.items.IWarpingGear;

public class ItemPrimalCrusher extends DiggerItem implements IWarpingGear {
    public ItemPrimalCrusher() {
        super(
                TCToolTiers.PRIMALVOID,
                BlockTags.MINEABLE_WITH_PICKAXE,
                new Item.Properties()
                        .rarity(Rarity.EPIC)
                        .attributes(PickaxeItem.createAttributes(TCToolTiers.PRIMALVOID, 3.5F, -2.8F))
        );
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            return TCToolTiers.PRIMALVOID.getSpeed();
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!level.isClientSide() && stack.isDamaged() && entity instanceof LivingEntity && entity.tickCount % 20 == 0) {
            stack.setDamageValue(Math.max(0, stack.getDamageValue() - 1));
        }
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!level.isClientSide() && (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL))) {
            // 3x3x1 excavation
            Direction facing = entityLiving.getDirection();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    BlockPos target = pos.offset(dx, dy, 0);
                    BlockState s = level.getBlockState(target);
                    if (s.is(BlockTags.MINEABLE_WITH_PICKAXE) || s.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
                        level.destroyBlock(target, true, entityLiving);
                    }
                }
            }
        }
        return super.mineBlock(stack, level, state, pos, entityLiving);
    }

    @Override
    public int getWarp(ItemStack stack, Player player) {
        return 2;
    }
}

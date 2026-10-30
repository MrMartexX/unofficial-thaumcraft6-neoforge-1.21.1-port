package thaumcraft.common.items.tools;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ItemElementalAxe extends AxeItem {
    public ItemElementalAxe() {
        super(
                TCToolTiers.ELEMENTAL,
                new Item.Properties()
                        .rarity(Rarity.RARE)
                        .attributes(AxeItem.createAttributes(TCToolTiers.ELEMENTAL, 5.0F, -3.0F))
        );
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!level.isClientSide() && state.is(BlockTags.LOGS)) {
            // Tree felling: recursively destroy adjacent connected log blocks up to 50
            fellTree(level, pos, stack, entityLiving);
        }
        return super.mineBlock(stack, level, state, pos, entityLiving);
    }

    private void fellTree(Level level, BlockPos startPos, ItemStack stack, LivingEntity entity) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(startPos);
        visited.add(startPos);

        int count = 0;
        while (!queue.isEmpty() && count < 64 && !stack.isEmpty()) {
            BlockPos current = queue.poll();
            for (BlockPos neighbor : BlockPos.betweenClosed(current.offset(-1, -1, -1), current.offset(1, 1, 1))) {
                BlockPos immutable = neighbor.immutable();
                if (!visited.contains(immutable)) {
                    visited.add(immutable);
                    BlockState s = level.getBlockState(immutable);
                    if (s.is(BlockTags.LOGS)) {
                        level.destroyBlock(immutable, true, entity);
                        count++;
                        queue.add(immutable);
                        if (count % 4 == 0 && entity instanceof net.minecraft.server.level.ServerPlayer sp) {
                            stack.hurtAndBreak(1, sp, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
                        }
                    }
                }
            }
        }
    }
}

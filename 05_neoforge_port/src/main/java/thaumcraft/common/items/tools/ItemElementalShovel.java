package thaumcraft.common.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ItemElementalShovel extends ShovelItem {
    public ItemElementalShovel() {
        super(
                TCToolTiers.ELEMENTAL,
                new Item.Properties()
                        .rarity(Rarity.RARE)
                        .attributes(ShovelItem.createAttributes(TCToolTiers.ELEMENTAL, 1.5F, -3.0F))
        );
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!level.isClientSide() && !TCToolEvents.isHarvesting() && !entityLiving.isShiftKeyDown() && state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            // Dig 3x3 earth oriented according to face mined
            Player player = entityLiving instanceof Player p ? p : null;
            Direction face = player != null ? TCToolEvents.getMiningFace(player, pos) : entityLiving.getDirection();

            for (int aa = -1; aa <= 1; aa++) {
                for (int bb = -1; bb <= 1; bb++) {
                    if (aa == 0 && bb == 0) continue;
                    int xx = 0;
                    int yy = 0;
                    int zz = 0;
                    if (face.getAxis() == Direction.Axis.Y) {
                        xx = aa;
                        zz = bb;
                    } else if (face.getAxis() == Direction.Axis.Z) {
                        xx = aa;
                        yy = bb;
                    } else {
                        zz = aa;
                        yy = bb;
                    }
                    BlockPos target = pos.offset(xx, yy, zz);
                    BlockState s = level.getBlockState(target);
                    if (s.getDestroySpeed(level, target) >= 0.0F && s.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
                        TCToolEvents.destroyNeighborBlock(entityLiving, level, target, stack);
                        if (stack.isEmpty()) {
                            break;
                        }
                    }
                }
                if (stack.isEmpty()) {
                    break;
                }
            }
        }
        return super.mineBlock(stack, level, state, pos, entityLiving);
    }
}

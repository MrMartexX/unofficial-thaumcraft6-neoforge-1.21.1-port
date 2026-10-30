package thaumcraft.common.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
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
        if (!level.isClientSide() && state.is(BlockTags.MINEABLE_WITH_SHOVEL) && entityLiving.isShiftKeyDown()) {
            // Dig 3x3 earth
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0) continue;
                    BlockPos p = pos.offset(dx, 0, dz);
                    BlockState s = level.getBlockState(p);
                    if (s.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
                        level.destroyBlock(p, true, entityLiving);
                    }
                }
            }
        }
        return super.mineBlock(stack, level, state, pos, entityLiving);
    }
}

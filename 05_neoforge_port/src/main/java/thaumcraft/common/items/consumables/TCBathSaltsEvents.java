package thaumcraft.common.items.consumables;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;

public final class TCBathSaltsEvents {
    private TCBathSaltsEvents() {
    }

    public static void onItemExpire(ItemExpireEvent event) {
        ItemEntity itemEntity = event.getEntity();
        if (itemEntity.getItem().is(TCItems.BATH_SALTS.get())) {
            tryConvertBathSaltsWater(itemEntity.level(), itemEntity.blockPosition());
        }
    }

    public static boolean tryConvertBathSaltsWater(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return false;
        }
        if (!level.getBlockState(pos).is(Blocks.WATER) || !level.getFluidState(pos).isSource()) {
            return false;
        }
        return level.setBlock(pos, TCBlocks.PURIFYING_FLUID.get().defaultBlockState(), Block.UPDATE_ALL);
    }
}

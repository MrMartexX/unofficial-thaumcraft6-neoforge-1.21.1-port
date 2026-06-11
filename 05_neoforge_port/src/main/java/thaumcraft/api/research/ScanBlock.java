package thaumcraft.api.research;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ScanBlock implements IScanThing {
    private final String research;
    private final Block[] blocks;

    public ScanBlock(Block block) {
        this("!" + BuiltInRegistries.BLOCK.getKey(block), block);
    }

    public ScanBlock(String research, Block... blocks) {
        this.research = research;
        this.blocks = blocks == null ? new Block[0] : blocks.clone();

        for (Block block : this.blocks) {
            ScanningManager.addScannableThing(new ScanItem(research, new ItemStack(block)));
        }
    }

    @Override
    public boolean checkThing(ServerPlayer player, Object object) {
        if (!(object instanceof BlockPos pos)) {
            return false;
        }

        Block scanned = player.level().getBlockState(pos).getBlock();

        for (Block block : blocks) {
            if (scanned == block) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getResearchKey(ServerPlayer player, Object object) {
        return research;
    }
}

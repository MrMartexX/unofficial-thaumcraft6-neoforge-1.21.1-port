package thaumcraft.common.tiles.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCBlockEntities;

/**
 * Client-ticked block entity for Nitor FX.
 */
public class TCNitorBlockEntity extends BlockEntity {
    @FunctionalInterface
    public interface ClientTickSink {
        void tick(Level level, BlockPos pos, BlockState state, int count);
    }

    private static volatile ClientTickSink clientTickSink = (level, pos, state, count) -> {
    };
    private int count;

    public TCNitorBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.NITOR.get(), pos, blockState);
    }

    public static void setClientTickSink(ClientTickSink sink) {
        clientTickSink = sink == null ? (level, pos, state, count) -> {
        } : sink;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, TCNitorBlockEntity blockEntity) {
        blockEntity.count++;
        clientTickSink.tick(level, pos, state, blockEntity.count);
    }
}

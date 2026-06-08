package thaumcraft.common.tiles.misc;

import java.lang.reflect.Method;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCBlockEntities;

/**
 * Client-ticked block entity for Nitor FX.
 */
public class TCNitorBlockEntity extends BlockEntity {
    private static Method clientTickMethod;
    private int count;

    public TCNitorBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.NITOR.get(), pos, blockState);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, TCNitorBlockEntity blockEntity) {
        blockEntity.count++;
        invokeClientEffects(level, pos, state, blockEntity.count);
    }

    private static void invokeClientEffects(Level level, BlockPos pos, BlockState state, int count) {
        try {
            if (clientTickMethod == null) {
                Class<?> effectsClass = Class.forName("thaumcraft.client.fx.TCNitorClientEffects");
                clientTickMethod = effectsClass.getMethod("tick", Level.class, BlockPos.class, BlockState.class, Integer.TYPE);
            }
            clientTickMethod.invoke(null, level, pos, state, count);
        } catch (ReflectiveOperationException ignored) {
            // Dedicated server and missing-client-class safe no-op.
        }
    }
}

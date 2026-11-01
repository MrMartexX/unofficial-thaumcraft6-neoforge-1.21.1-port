package thaumcraft.api.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.tasks.Task;

public interface ISeal {

    String getKey();

    boolean canPlaceAt(Level level, BlockPos pos, Direction side);

    void tickSeal(Level level, ISealEntity seal);

    void onTaskStarted(Level level, IGolemAPI golem, Task task);

    boolean onTaskCompletion(Level level, IGolemAPI golem, Task task);

    void onTaskSuspension(Level level, Task task);

    boolean canGolemPerformTask(IGolemAPI golem, Task task);

    void readCustomNBT(CompoundTag tag);

    void writeCustomNBT(CompoundTag tag);

    ResourceLocation getSealIcon();

    void onRemoval(Level level, BlockPos pos, Direction side);

    default Object returnContainer(Level level, Player player, BlockPos pos, Direction side, ISealEntity seal) {
        return null;
    }

    default Object returnGui(Level level, Player player, BlockPos pos, Direction side, ISealEntity seal) {
        return null;
    }

    EnumGolemTrait[] getRequiredTags();

    EnumGolemTrait[] getForbiddenTags();
}

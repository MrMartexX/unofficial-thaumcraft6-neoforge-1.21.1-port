package thaumcraft.common.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;

public class SealLumber implements ISeal, ISealGui, ISealConfigArea {

    private int delay = 0;
    private int count = 0;

    @Override
    public String getKey() {
        return "thaumcraft:lumber";
    }

    @Override
    public boolean canPlaceAt(Level level, BlockPos pos, Direction side) {
        return !level.isEmptyBlock(pos);
    }

    @Override
    public void tickSeal(Level level, ISealEntity seal) {
        if (delay++ % 10 != 0) return;

        BlockPos targetPos = GolemHelper.getPosInArea(seal, count++);
        BlockState state = level.getBlockState(targetPos);
        if (state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)) {
            Task task = new Task(seal.getSealPos(), targetPos);
            task.setPriority(seal.getPriority());
            TaskHandler.addTask(level, task);
        }
    }

    @Override
    public void onTaskStarted(Level level, IGolemAPI golem, Task task) {
    }

    @Override
    public boolean onTaskCompletion(Level level, IGolemAPI golem, Task task) {
        if (task.getPos() != null) {
            BlockState state = level.getBlockState(task.getPos());
            if (state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)) {
                level.destroyBlock(task.getPos(), true);
                golem.addRankXp(1);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onTaskSuspension(Level level, Task task) {
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        return golem.getProperties().hasTrait(EnumGolemTrait.BREAKER);
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/seals/seal_lumber.png");
    }

    @Override
    public void onRemoval(Level level, BlockPos pos, Direction side) {
    }

    @Override
    public void readCustomNBT(net.minecraft.nbt.CompoundTag tag) {
    }

    @Override
    public void writeCustomNBT(net.minecraft.nbt.CompoundTag tag) {
    }

    @Override
    public int[] getGuiCategories() {
        return new int[]{CAT_PRIORITY, CAT_AREA};
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.BREAKER};
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[0];
    }
}

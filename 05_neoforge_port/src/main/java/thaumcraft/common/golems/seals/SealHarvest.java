package thaumcraft.common.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;

public class SealHarvest implements ISeal, ISealGui, ISealConfigArea, ISealConfigToggles {

    private int delay = 0;
    private int count = 0;
    private final SealToggle[] toggles = new SealToggle[]{
            new SealToggle(true, "replant", "golem.prop.replant"),
            new SealToggle(false, "provision", "golem.prop.provision")
    };

    @Override
    public String getKey() {
        return "thaumcraft:harvest";
    }

    @Override
    public boolean canPlaceAt(Level level, BlockPos pos, Direction side) {
        return !level.isEmptyBlock(pos);
    }

    public static boolean isGrownCrop(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof CropBlock crop) {
            return crop.isMaxAge(state);
        }
        if (block instanceof NetherWartBlock) {
            return state.getValue(NetherWartBlock.AGE) >= 3;
        }
        if (block instanceof CocoaBlock) {
            return state.getValue(CocoaBlock.AGE) >= 2;
        }
        if (state.is(Blocks.MELON) || state.is(Blocks.PUMPKIN)) {
            return true;
        }
        if (state.is(Blocks.SUGAR_CANE) || state.is(Blocks.CACTUS)) {
            return level.getBlockState(pos.below()).is(block);
        }
        return false;
    }

    @Override
    public void tickSeal(Level level, ISealEntity seal) {
        if (delay++ % 5 != 0) return;

        BlockPos targetPos = GolemHelper.getPosInArea(seal, count++);
        if (isGrownCrop(level, targetPos)) {
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
        BlockPos pos = task.getPos();
        if (pos != null && isGrownCrop(level, pos)) {
            BlockState oldState = level.getBlockState(pos);
            level.destroyBlock(pos, true);

            // Replant if configured
            if (toggles[0].getValue()) {
                if (oldState.getBlock() instanceof CropBlock crop) {
                    level.setBlock(pos, crop.getStateForAge(0), Block.UPDATE_ALL);
                } else if (oldState.getBlock() instanceof NetherWartBlock) {
                    level.setBlock(pos, Blocks.NETHER_WART.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
            golem.addRankXp(1);
            return true;
        }
        return false;
    }

    @Override
    public void onTaskSuspension(Level level, Task task) {
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        return true;
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/seals/seal_harvest.png");
    }

    @Override
    public void onRemoval(Level level, BlockPos pos, Direction side) {
    }

    @Override
    public void readCustomNBT(net.minecraft.nbt.CompoundTag tag) {
        if (tag.contains("replant")) {
            toggles[0].setValue(tag.getBoolean("replant"));
        }
    }

    @Override
    public void writeCustomNBT(net.minecraft.nbt.CompoundTag tag) {
        tag.putBoolean("replant", toggles[0].getValue());
    }

    @Override
    public int[] getGuiCategories() {
        return new int[]{CAT_PRIORITY, CAT_AREA, CAT_TOGGLES};
    }

    @Override
    public SealToggle[] getToggles() {
        return toggles;
    }

    @Override
    public void setToggle(int index, boolean value) {
        if (index >= 0 && index < toggles.length) {
            toggles[index].setValue(value);
        }
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[0];
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[0];
    }
}

package thaumcraft.common.golems.seals;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
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

public class SealGuard implements ISeal, ISealGui, ISealConfigArea, ISealConfigToggles {

    private int delay = 0;
    protected final SealToggle[] toggles = new SealToggle[]{
            new SealToggle(true, "mobs", "golem.prop.mob"),
            new SealToggle(false, "animals", "golem.prop.animal"),
            new SealToggle(false, "players", "golem.prop.player")
    };

    @Override
    public String getKey() {
        return "thaumcraft:guard";
    }

    @Override
    public boolean canPlaceAt(Level level, BlockPos pos, Direction side) {
        return !level.isEmptyBlock(pos);
    }

    public boolean isValidTarget(LivingEntity target) {
        if (!target.isAlive()) return false;
        if (toggles[0].getValue() && target instanceof Enemy) {
            return true;
        }
        if (toggles[1].getValue() && target instanceof Animal) {
            return true;
        }
        if (toggles[2].getValue() && target instanceof Player) {
            return true;
        }
        return false;
    }

    @Override
    public void tickSeal(Level level, ISealEntity seal) {
        if (delay++ % 20 != 0) return;

        AABB area = GolemHelper.getBoundsForArea(seal);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area);

        for (LivingEntity target : targets) {
            if (isValidTarget(target)) {
                Task task = new Task(seal.getSealPos(), target);
                task.setPriority(seal.getPriority());
                task.setLifespan((short) 10);
                TaskHandler.addTask(level, task);
            }
        }
    }

    @Override
    public void onTaskStarted(Level level, IGolemAPI golem, Task task) {
        if (task.getEntity() instanceof LivingEntity target && isValidTarget(target)) {
            if (golem instanceof Mob mob) {
                mob.setTarget(target);
            }
            golem.addRankXp(1);
        }
        task.setSuspended(true);
    }

    @Override
    public boolean onTaskCompletion(Level level, IGolemAPI golem, Task task) {
        task.setSuspended(true);
        return true;
    }

    @Override
    public void onTaskSuspension(Level level, Task task) {
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        return golem.getProperties().hasTrait(EnumGolemTrait.FIGHTER);
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/seals/seal_guard.png");
    }

    @Override
    public void onRemoval(Level level, BlockPos pos, Direction side) {
    }

    @Override
    public void readCustomNBT(CompoundTag tag) {
        if (tag.contains("mobs")) toggles[0].setValue(tag.getBoolean("mobs"));
        if (tag.contains("animals")) toggles[1].setValue(tag.getBoolean("animals"));
        if (tag.contains("players")) toggles[2].setValue(tag.getBoolean("players"));
    }

    @Override
    public void writeCustomNBT(CompoundTag tag) {
        tag.putBoolean("mobs", toggles[0].getValue());
        tag.putBoolean("animals", toggles[1].getValue());
        tag.putBoolean("players", toggles[2].getValue());
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
        return new EnumGolemTrait[]{EnumGolemTrait.FIGHTER};
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[0];
    }
}

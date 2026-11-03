package thaumcraft.common.golems.seals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;

public class SealButcher implements ISeal, ISealGui, ISealConfigArea {

    private int delay = 0;

    @Override
    public String getKey() {
        return "thaumcraft:butcher";
    }

    @Override
    public boolean canPlaceAt(Level level, BlockPos pos, Direction side) {
        return !level.isEmptyBlock(pos);
    }

    @Override
    public void tickSeal(Level level, ISealEntity seal) {
        if (delay++ % 40 != 0) return;

        AABB area = GolemHelper.getBoundsForArea(seal);
        List<Animal> animals = level.getEntitiesOfClass(Animal.class, area);

        // Group animals by type to preserve breeding pairs
        Map<EntityType<?>, Integer> counts = new HashMap<>();
        for (Animal a : animals) {
            if (a.isAlive() && !a.isBaby()) {
                counts.put(a.getType(), counts.getOrDefault(a.getType(), 0) + 1);
            }
        }

        for (Animal a : animals) {
            if (a.isAlive() && !a.isBaby()) {
                int count = counts.getOrDefault(a.getType(), 0);
                if (count > 2) {
                    Task task = new Task(seal.getSealPos(), a);
                    task.setPriority(seal.getPriority());
                    TaskHandler.addTask(level, task);
                    counts.put(a.getType(), count - 1);
                }
            }
        }
    }

    @Override
    public void onTaskStarted(Level level, IGolemAPI golem, Task task) {
        if (task.getEntity() instanceof LivingEntity target && golem instanceof Mob mob) {
            mob.setTarget(target);
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
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/seals/seal_butcher.png");
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
        return new int[]{CAT_AREA, CAT_PRIORITY, CAT_TAGS};
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.FIGHTER, EnumGolemTrait.SMART};
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[0];
    }
}

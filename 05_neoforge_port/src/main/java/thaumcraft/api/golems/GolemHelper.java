package thaumcraft.api.golems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.seals.ItemSealPlacer;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.golems.tasks.TaskHandler;

public class GolemHelper {

    public static void registerSeal(ISeal seal) {
        SealHandler.registerSeal(seal);
    }

    public static ISeal getSeal(String key) {
        return SealHandler.getSeal(key);
    }

    public static ItemStack getSealStack(String key) {
        return ItemSealPlacer.getSealStack(key);
    }

    public static ISealEntity getSealEntity(Level level, SealPos pos) {
        return SealHandler.getSealEntity(level, pos);
    }

    public static void addGolemTask(Level level, Task task) {
        TaskHandler.addTask(level, task);
    }

    public static final Map<ResourceKey<Level>, List<ProvisionRequest>> provisionRequests = new HashMap<>();
    private static final int LIST_LIMIT = 1000;

    public static void requestProvisioning(Level level, ISealEntity seal, ItemStack stack) {
        requestProvisioning(level, seal, stack, false);
    }

    public static void requestProvisioning(Level level, ISealEntity seal, ItemStack stack, boolean ignoreMetadata) {
        List<ProvisionRequest> list = provisionRequests.computeIfAbsent(level.dimension(), k -> new ArrayList<>());
        ProvisionRequest pr = new ProvisionRequest(seal, stack.copy());
        pr.setIgnoreMetadata(ignoreMetadata);
        if (refreshExisting(list, pr)) return;
        if (!list.contains(pr)) {
            list.add(pr);
        }
        if (list.size() > LIST_LIMIT) {
            list.remove(0);
        }
    }

    public static void requestProvisioning(Level level, BlockPos pos, Direction side, ItemStack stack) {
        List<ProvisionRequest> list = provisionRequests.computeIfAbsent(level.dimension(), k -> new ArrayList<>());
        ProvisionRequest pr = new ProvisionRequest(pos, side, stack.copy());
        if (refreshExisting(list, pr)) return;
        if (!list.contains(pr)) {
            list.add(pr);
        }
        if (list.size() > LIST_LIMIT) {
            list.remove(0);
        }
    }

    public static void requestProvisioning(Level level, Entity entity, ItemStack stack) {
        List<ProvisionRequest> list = provisionRequests.computeIfAbsent(level.dimension(), k -> new ArrayList<>());
        ProvisionRequest pr = new ProvisionRequest(entity, stack.copy());
        if (refreshExisting(list, pr)) return;
        if (!list.contains(pr)) {
            list.add(pr);
        }
        if (list.size() > LIST_LIMIT) {
            list.remove(0);
        }
    }

    private static boolean refreshExisting(List<ProvisionRequest> requests, ProvisionRequest proposed) {
        for (ProvisionRequest existing : requests) {
            if (!existing.isInvalid() && existing.getSeal() == proposed.getSeal()
                    && existing.getEntity() == proposed.getEntity()
                    && java.util.Objects.equals(existing.getPos(), proposed.getPos())
                    && existing.getSide() == proposed.getSide()
                    && existing.ignoresMetadata() == proposed.ignoresMetadata()
                    && existing.getRequestedStack().getCount() == proposed.getRequestedStack().getCount()
                    && ItemStack.isSameItemSameComponents(existing.getRequestedStack(), proposed.getRequestedStack())) {
                existing.refreshTimeout();
                return true;
            }
        }
        return false;
    }

    public static BlockPos getPosInArea(ISealEntity seal, int count) {
        Direction face = seal.getSealPos().face;
        int stepX = face.getStepX();
        int stepY = face.getStepY();
        int stepZ = face.getStepZ();

        int xx = 1 + (seal.getArea().getX() - 1) * (stepX == 0 ? 2 : 1);
        int yy = 1 + (seal.getArea().getY() - 1) * (stepY == 0 ? 2 : 1);
        int zz = 1 + (seal.getArea().getZ() - 1) * (stepZ == 0 ? 2 : 1);

        int qx = stepX != 0 ? stepX : 1;
        int qy = stepY != 0 ? stepY : 1;
        int qz = stepZ != 0 ? stepZ : 1;

        int safeZ = Math.max(1, zz);
        int safeX = Math.max(1, xx);
        int safeY = Math.max(1, yy);

        int y = qy * ((count / safeZ) / safeX) % safeY + stepY;
        int x = qx * (count / safeZ) % safeX + stepX;
        int z = qz * count % safeZ + stepZ;

        return seal.getSealPos().pos.offset(
                x - (stepX == 0 ? xx / 2 : 0),
                y - (stepY == 0 ? yy / 2 : 0),
                z - (stepZ == 0 ? zz / 2 : 0));
    }

    public static AABB getBoundsForArea(ISealEntity seal) {
        Direction face = seal.getSealPos().face;
        int stepX = face.getStepX();
        int stepY = face.getStepY();
        int stepZ = face.getStepZ();

        AABB box = new AABB(seal.getSealPos().pos).move(stepX, stepY, stepZ);

        double expandX = stepX != 0 ? (seal.getArea().getX() - 1) * stepX : 0;
        double expandY = stepY != 0 ? (seal.getArea().getY() - 1) * stepY : 0;
        double expandZ = stepZ != 0 ? (seal.getArea().getZ() - 1) * stepZ : 0;
        box = box.expandTowards(expandX, expandY, expandZ);

        double inflateX = stepX == 0 ? seal.getArea().getX() - 1 : 0;
        double inflateY = stepY == 0 ? seal.getArea().getY() - 1 : 0;
        double inflateZ = stepZ == 0 ? seal.getArea().getZ() - 1 : 0;
        return box.inflate(inflateX, inflateY, inflateZ);
    }
}

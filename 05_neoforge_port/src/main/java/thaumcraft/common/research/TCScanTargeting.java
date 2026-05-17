package thaumcraft.common.research;

import java.util.List;
import java.util.Optional;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class TCScanTargeting {
    private TCScanTargeting() {
    }

    public static Entity findPointedEntity(
            Level level,
            Entity source,
            double minRange,
            double range,
            float padding,
            boolean nonCollide
    ) {
        Vec3 start = source.position().add(0.0D, source.getEyeHeight(), 0.0D);
        Vec3 look = source.getLookAngle();
        Vec3 end = start.add(look.x * range, look.y * range, look.z * range);
        AABB sourceBox = source.getBoundingBox();
        AABB searchBox = sourceBox.expandTowards(look.x * range, look.y * range, look.z * range)
                .inflate(padding, padding, padding);
        List<Entity> entities = level.getEntities(source, searchBox);
        Entity pointedEntity = null;
        double bestDistance = 0.0D;

        for (Entity entity : entities) {
            if (entity == source || entity.isSpectator()) {
                continue;
            }

            if (start.distanceTo(entity.position()) < minRange) {
                continue;
            }

            if (!nonCollide && !entity.isPickable()) {
                continue;
            }

            if (!hasLineOfSight(level, source, start, entity)) {
                continue;
            }

            float border = Math.max(0.8F, entity.getPickRadius());
            AABB hitBox = entity.getBoundingBox().inflate(border, border, border);
            Optional<Vec3> hit = hitBox.clip(start, end);

            if (hitBox.contains(start)) {
                if (0.0D < bestDistance || bestDistance == 0.0D) {
                    pointedEntity = entity;
                    bestDistance = 0.0D;
                }
            } else if (hit.isPresent()) {
                double distance = start.distanceTo(hit.get());
                if (distance < bestDistance || bestDistance == 0.0D) {
                    pointedEntity = entity;
                    bestDistance = distance;
                }
            }
        }

        return pointedEntity;
    }

    public static boolean isThaumometerEntityTarget(Entity entity) {
        return entity instanceof ItemEntity || entity.isPickable();
    }

    public static BlockHitResult rayTrace(Level level, Player player, double range, boolean useLiquids) {
        Vec3 start = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = start.add(look.scale(range));
        return level.clip(new ClipContext(
                start,
                end,
                ClipContext.Block.OUTLINE,
                useLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE,
                player
        ));
    }

    public static BlockHitResult wildRayTrace(Level level, Player player, double range, boolean useLiquids, RandomSource random) {
        float pitch = player.xRotO + (player.getXRot() - player.xRotO) + random.nextInt(25) - random.nextInt(25);
        float yaw = player.yRotO + (player.getYRot() - player.yRotO) + random.nextInt(25) - random.nextInt(25);

        Vec3 start = player.position().add(0.0D, player.getEyeHeight(), 0.0D);
        Vec3 look = lookVector(pitch, yaw);
        Vec3 end = start.add(look.scale(range));

        return level.clip(new ClipContext(
                start,
                end,
                ClipContext.Block.OUTLINE,
                useLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE,
                player
        ));
    }

    private static boolean hasLineOfSight(Level level, Entity source, Vec3 start, Entity entity) {
        Vec3 target = new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());
        HitResult hit = level.clip(new ClipContext(
                start,
                target,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                source
        ));
        return hit.getType() == HitResult.Type.MISS;
    }

    private static Vec3 lookVector(float pitch, float yaw) {
        float xz = -Mth.cos(-pitch * Mth.DEG_TO_RAD);
        float y = Mth.sin(-pitch * Mth.DEG_TO_RAD);
        float x = Mth.sin(-yaw * Mth.DEG_TO_RAD - Mth.PI) * xz;
        float z = Mth.cos(-yaw * Mth.DEG_TO_RAD - Mth.PI) * xz;
        return new Vec3(x, y, z);
    }
}

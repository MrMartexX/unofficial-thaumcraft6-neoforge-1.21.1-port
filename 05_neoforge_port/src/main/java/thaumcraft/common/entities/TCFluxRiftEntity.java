package thaumcraft.common.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.tiles.devices.TCVoidSiphonRiftAccess;

/** Modern Flux Rift entity retaining TC6's seed/size/stability/collapse contract. */
public class TCFluxRiftEntity extends Entity implements TCVoidSiphonRiftAccess {
    private static final EntityDataAccessor<Integer> SEED =
            SynchedEntityData.defineId(TCFluxRiftEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SIZE =
            SynchedEntityData.defineId(TCFluxRiftEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> STABILITY =
            SynchedEntityData.defineId(TCFluxRiftEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> COLLAPSE =
            SynchedEntityData.defineId(TCFluxRiftEntity.class, EntityDataSerializers.BOOLEAN);

    private final ArrayList<Vec3> points = new ArrayList<>();
    private final ArrayList<Float> widths = new ArrayList<>();
    private int maxSize;
    private int lastSize = -1;

    public TCFluxRiftEntity(EntityType<? extends TCFluxRiftEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    public TCFluxRiftEntity(Level level, double x, double y, double z) {
        this(TCEntityTypes.FLUX_RIFT.get(), level);
        setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SEED, 0);
        builder.define(SIZE, 5);
        builder.define(STABILITY, 0.0F);
        builder.define(COLLAPSE, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (lastSize != getRiftSize() || points.isEmpty()) {
            rebuildGeometry();
        }
        if (level().isClientSide) {
            tickClientParticles();
            return;
        }

        if (getRiftSeed() == 0) {
            setRiftSeed(random.nextInt());
        }
        damageWorldAndEntities();
        if (points.size() < 3 && !isCollapsing()) {
            setCollapse(true);
        }
        if (isCollapsing()) {
            tickCollapse();
            if (!isAlive()) {
                return;
            }
        }
        if (tickCount % 120 == 0) {
            setRiftStability(getRiftStability() - 0.2F);
        }
        if (tickCount % 600 == getId() % 600) {
            tickFluxGrowth();
        }
        if (tickCount % 300 == 0) {
            level().playSound(null, blockPosition(), TCSounds.EVILPORTAL.get(), SoundSource.BLOCKS,
                    (float) (0.15D + random.nextGaussian() * 0.066D),
                    (float) (0.75D + random.nextGaussian() * 0.1D));
        }
    }

    public List<Vec3> renderPoints() {
        if (points.isEmpty()) {
            rebuildGeometry();
        }
        return List.copyOf(points);
    }

    public List<Float> renderWidths() {
        if (widths.isEmpty()) {
            rebuildGeometry();
        }
        return List.copyOf(widths);
    }

    public int getRiftSeed() {
        return entityData.get(SEED);
    }

    public void setRiftSeed(int seed) {
        entityData.set(SEED, seed);
        rebuildGeometry();
    }

    public int getRiftSize() {
        return entityData.get(SIZE);
    }

    public void setRiftSize(int size) {
        entityData.set(SIZE, Math.max(0, size));
        rebuildGeometry();
    }

    public float getRiftStability() {
        return entityData.get(STABILITY);
    }

    public void setRiftStability(float stability) {
        entityData.set(STABILITY, Mth.clamp(stability, -100.0F, 100.0F));
    }

    public void addStability() {
        setRiftStability(getRiftStability() + 0.125F);
    }

    public boolean isCollapsing() {
        return entityData.get(COLLAPSE);
    }

    public void setCollapse(boolean collapse) {
        if (collapse && !isCollapsing()) {
            maxSize = getRiftSize();
        }
        entityData.set(COLLAPSE, collapse);
    }

    public Stability getStability() {
        float stability = getRiftStability();
        if (stability > 50.0F) {
            return Stability.VERY_STABLE;
        }
        if (stability >= 0.0F) {
            return Stability.STABLE;
        }
        return stability > -25.0F ? Stability.UNSTABLE : Stability.VERY_UNSTABLE;
    }

    public void completeCollapseForValidation() {
        completeCollapse();
    }

    public static boolean createRift(Level level, BlockPos origin) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        BlockPos pos = origin.offset(level.random.nextInt(16), 0, level.random.nextInt(16));
        BlockPos surface = serverLevel.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, pos);
        if (surface.getY() >= serverLevel.getMaxBuildHeight() - 4) {
            return false;
        }
        AABB nearby = new AABB(surface).inflate(32.0D);
        if (!serverLevel.getEntitiesOfClass(TCFluxRiftEntity.class, nearby).isEmpty()) {
            return false;
        }
        float flux = AuraHelper.getFlux(serverLevel, surface);
        double size = Math.sqrt(flux * 3.0F);
        if (size <= 5.0D) {
            return false;
        }
        TCFluxRiftEntity rift = new TCFluxRiftEntity(serverLevel, surface.getX() + 0.5D, surface.getY() + 0.5D, surface.getZ() + 0.5D);
        rift.setYRot(level.random.nextInt(360));
        rift.setRiftSeed(level.random.nextInt());
        rift.setRiftSize((int) size);
        boolean spawned = serverLevel.addFreshEntity(rift);
        if (spawned) {
            AuraHelper.drainFlux(serverLevel, surface, (float) size, false);
        }
        return spawned;
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        // TC6 rifts are fixed-space tears. External movement is ignored.
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("MaxSize", maxSize);
        tag.putInt("RiftSize", getRiftSize());
        tag.putInt("RiftSeed", getRiftSeed());
        tag.putFloat("Stability", getRiftStability());
        tag.putBoolean("collapse", isCollapsing());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        maxSize = tag.getInt("MaxSize");
        setRiftSeed(tag.getInt("RiftSeed"));
        setRiftSize(tag.contains("RiftSize") ? tag.getInt("RiftSize") : 5);
        setRiftStability(tag.getFloat("Stability"));
        setCollapse(tag.getBoolean("collapse"));
    }

    @Override
    public Vec3 voidSiphonPosition() {
        return position();
    }

    @Override
    public int voidSiphonRiftSize() {
        return getRiftSize();
    }

    @Override
    public void voidSiphonSetRiftSize(int size) {
        setRiftSize(size);
    }

    @Override
    public double voidSiphonStability() {
        return getRiftStability();
    }

    @Override
    public void voidSiphonSetStability(double stability) {
        setRiftStability((float) stability);
    }

    @Override
    public boolean voidSiphonAlive() {
        return isAlive();
    }

    @Override
    public boolean voidSiphonCanBeSeenFrom(Level level, Vec3 source) {
        BlockHitResult hit = level.clip(new ClipContext(source, position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return hit.getType() == HitResult.Type.MISS || hit.getBlockPos().equals(blockPosition());
    }

    private void damageWorldAndEntities() {
        if (points.size() < 2 || !(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        int index = random.nextInt(points.size() - 1);
        Vec3 first = points.get(index).add(position());
        Vec3 second = points.get(index + 1).add(position());
        BlockHitResult hit = level().clip(new ClipContext(first, second, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            BlockState state = level().getBlockState(pos);
            if (!state.isAir() && state.getDestroySpeed(level(), pos) >= 0.0F && !state.getCollisionShape(level(), pos).isEmpty()) {
                level().levelEvent(null, 2001, pos, Block.getId(state));
                level().removeBlock(pos, false);
            }
        }
        AABB damageArea = new AABB(first, first).inflate(0.5D);
        for (Entity entity : level().getEntities(this, damageArea)) {
            if (entity instanceof Player player && player.isCreative()) {
                continue;
            }
            entity.hurt(serverLevel.damageSources().magic(), 2.0F);
            if (entity instanceof ItemEntity) {
                entity.discard();
            }
        }
    }

    private void tickCollapse() {
        setRiftSize(getRiftSize() - 1);
        if (random.nextBoolean()) {
            AuraHelper.addVis(level(), blockPosition(), 1.0F);
        } else {
            AuraHelper.polluteAura(level(), blockPosition(), 1.0F, false);
        }
        if (getRiftSize() <= 1) {
            completeCollapse();
        }
    }

    private void tickFluxGrowth() {
        float flux = AuraHelper.getFlux(level(), blockPosition());
        double size = Math.sqrt(getRiftSize() * 2.0D);
        if (flux >= size && getRiftSize() < 100 && getStability() != Stability.VERY_STABLE) {
            AuraHelper.drainFlux(level(), blockPosition(), (float) size, false);
            setRiftSize(getRiftSize() + 1);
        }
        if (getRiftStability() < 0.0F && random.nextInt(1000) < Math.abs(getRiftStability()) + getRiftSize()) {
            if (random.nextInt(10) == 0) {
                setCollapse(true);
            } else {
                setRiftStability(getRiftStability() + 5.0F);
            }
        }
    }

    private void tickClientParticles() {
        if (points.size() <= 2) {
            return;
        }
        boolean unstable = !isCollapsing() && getRiftStability() < 0.0F && random.nextInt(150) < Math.abs(getRiftStability());
        if (!unstable && !isCollapsing()) {
            return;
        }
        int index = 1 + random.nextInt(points.size() - 2);
        Vec3 point = points.get(index).add(position());
        level().addParticle(
                net.minecraft.core.particles.ParticleTypes.PORTAL,
                point.x,
                point.y,
                point.z,
                random.nextGaussian() * 0.01D,
                random.nextGaussian() * 0.01D,
                random.nextGaussian() * 0.01D
        );
    }

    private void completeCollapse() {
        int drops = (int) Math.sqrt(Math.max(maxSize, getRiftSize()));
        if (random.nextInt(100) < drops) {
            spawnAtLocation(new ItemStack(TCItems.PRIMORDIAL_PEARL.get()));
        }
        for (int index = 0; index < drops; index++) {
            spawnAtLocation(new ItemStack(TCItems.VOID_SEED.get()));
        }
        discard();
    }

    private void rebuildGeometry() {
        points.clear();
        widths.clear();
        Random seeded = new Random(getRiftSeed());
        Vec3 right = normalizeOrDefault(new Vec3(seeded.nextGaussian(), seeded.nextGaussian(), seeded.nextGaussian()));
        Vec3 left = right.scale(-1.0D);
        Vec3 rightPos = Vec3.ZERO;
        Vec3 leftPos = Vec3.ZERO;
        int steps = Mth.ceil(getRiftSize() / 3.0F);
        if (steps <= 0) {
            lastSize = getRiftSize();
            return;
        }
        float girth = getRiftSize() / 300.0F;
        double angle = 0.33D;
        float decrement = girth / steps;
        for (int index = 0; index < steps; index++) {
            girth -= decrement;
            right = right.xRot((float) (seeded.nextGaussian() * angle)).yRot((float) (seeded.nextGaussian() * angle));
            rightPos = rightPos.add(right.scale(0.2D));
            points.add(rightPos);
            widths.add(girth);
            left = left.xRot((float) (seeded.nextGaussian() * angle)).yRot((float) (seeded.nextGaussian() * angle));
            leftPos = leftPos.add(left.scale(0.2D));
            points.add(0, leftPos);
            widths.add(0, girth);
        }
        rightPos = rightPos.add(right.scale(0.1D));
        points.add(rightPos);
        widths.add(0.0F);
        leftPos = leftPos.add(left.scale(0.1D));
        points.add(0, leftPos);
        widths.add(0, 0.0F);
        lastSize = getRiftSize();
    }

    private static Vec3 normalizeOrDefault(Vec3 vec) {
        return vec.lengthSqr() < 1.0E-6D ? new Vec3(1.0D, 0.0D, 0.0D) : vec.normalize();
    }

    public enum Stability {
        VERY_STABLE,
        STABLE,
        UNSTABLE,
        VERY_UNSTABLE
    }
}

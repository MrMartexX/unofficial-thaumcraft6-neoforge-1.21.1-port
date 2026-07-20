package thaumcraft.common.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.lib.fx.TCFXDispatcher;
import thaumcraft.common.registry.TCEntityTypes;

/** TC6 EntityFocusCloud slice used by Flux Rift event 3: ROOT -> CLOUD -> FLUX. */
public class TCFocusCloudEntity extends Entity {
    public static final float LEGACY_ENTITY_WIDTH = 0.15F;
    public static final float LEGACY_ENTITY_HEIGHT = 0.15F;
    public static final float LEGACY_ACTIVE_HEIGHT = 0.5F;
    public static final int LEGACY_MIN_RADIUS = 1;
    public static final int LEGACY_MAX_RADIUS = 3;
    public static final int LEGACY_TICK_INTERVAL = 5;
    public static final long LEGACY_TARGET_COOLDOWN_MS = 2000L;
    public static final int LEGACY_MAX_DURATION_TICKS_MULTIPLIER = 20;
    public static final int LEGACY_FLUX_COLOR = 0x800080;
    public static final int LEGACY_FLUX_POWER = 1;
    public static final float LEGACY_CLOUD_POWER_MULTIPLIER = 0.5F;
    public static final float LEGACY_FLUX_DAMAGE = (3 + LEGACY_FLUX_POWER) * LEGACY_CLOUD_POWER_MULTIPLIER;
    public static final String LEGACY_EFFECT_KEY = "thaumcraft.FLUX";

    private static final EntityDataAccessor<Float> RADIUS =
            SynchedEntityData.defineId(TCFocusCloudEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DURATION_SECONDS =
            SynchedEntityData.defineId(TCFocusCloudEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> EFFECT_COLOR =
            SynchedEntityData.defineId(TCFocusCloudEntity.class, EntityDataSerializers.INT);

    private static final Map<Long, Long> COOLDOWN_MAP = new HashMap<>();

    private LivingEntity owner;
    private UUID ownerUuid;
    private String effectKey = LEGACY_EFFECT_KEY;
    private int cloudExecutions;
    private int entityHits;
    private int blockHits;

    public TCFocusCloudEntity(EntityType<? extends TCFocusCloudEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    public TCFocusCloudEntity(Level level, double x, double y, double z) {
        this(TCEntityTypes.FOCUS_CLOUD.get(), level);
        setPos(x, y, z);
    }

    public TCFocusCloudEntity(Level level, Vec3 source, LivingEntity owner, float radius, int durationSeconds) {
        this(level, source.x, source.y, source.z);
        setOwner(owner);
        setRadius(radius);
        setDurationSeconds(durationSeconds);
        setEffectColor(LEGACY_FLUX_COLOR);
        effectKey = LEGACY_EFFECT_KEY;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(RADIUS, 0.5F);
        builder.define(DURATION_SECONDS, 0);
        builder.define(EFFECT_COLOR, LEGACY_FLUX_COLOR);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            tickClientParticles();
            return;
        }
        if (tickCount > getDurationSeconds() * LEGACY_MAX_DURATION_TICKS_MULTIPLIER || getOwner() == null) {
            discard();
            return;
        }
        if (tickCount % LEGACY_TICK_INTERVAL == 0) {
            executeCloudTick();
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        float radius = getRadius();
        if (radius > 0.0F) {
            return EntityDimensions.scalable(radius * 2.0F, LEGACY_ACTIVE_HEIGHT);
        }
        return super.getDimensions(pose);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Age", tickCount);
        tag.putInt("Duration", getDurationSeconds());
        tag.putFloat("Radius", getRadius());
        tag.putString("Effect", effectKey);
        tag.putInt("EffectColor", getEffectColor());
        if (ownerUuid != null) {
            tag.putUUID("OwnerUUID", ownerUuid);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        tickCount = tag.getInt("Age");
        setDurationSeconds(tag.getInt("Duration"));
        setRadius(tag.contains("Radius") ? tag.getFloat("Radius") : 0.5F);
        effectKey = tag.contains("Effect") ? tag.getString("Effect") : LEGACY_EFFECT_KEY;
        setEffectColor(tag.contains("EffectColor") ? tag.getInt("EffectColor") : LEGACY_FLUX_COLOR);
        ownerUuid = tag.hasUUID("OwnerUUID") ? tag.getUUID("OwnerUUID") : null;
        owner = null;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
        ownerUuid = owner == null ? null : owner.getUUID();
    }

    public LivingEntity getOwner() {
        if (owner == null && ownerUuid != null && level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(ownerUuid);
            if (entity instanceof LivingEntity living) {
                owner = living;
            }
        }
        return owner;
    }

    public void setRadius(float radius) {
        double x = getX();
        double y = getY();
        double z = getZ();
        entityData.set(RADIUS, Mth.clamp(radius, 0.0F, LEGACY_MAX_RADIUS));
        refreshDimensions();
        setPos(x, y, z);
    }

    public float getRadius() {
        return entityData.get(RADIUS);
    }

    public void setDurationSeconds(int durationSeconds) {
        entityData.set(DURATION_SECONDS, Math.max(0, durationSeconds));
    }

    public int getDurationSeconds() {
        return entityData.get(DURATION_SECONDS);
    }

    public void setEffectColor(int color) {
        entityData.set(EFFECT_COLOR, color);
    }

    public int getEffectColor() {
        return entityData.get(EFFECT_COLOR);
    }

    public String getEffectKey() {
        return effectKey;
    }

    public int cloudExecutionsForValidation() {
        return cloudExecutions;
    }

    public int entityHitsForValidation() {
        return entityHits;
    }

    public int blockHitsForValidation() {
        return blockHits;
    }

    public static void clearCooldownsForValidation() {
        COOLDOWN_MAP.clear();
    }

    public static int cooldownSizeForValidation() {
        return COOLDOWN_MAP.size();
    }

    public static int randomRiftRadius(RandomSource random) {
        return randomInclusive(random, LEGACY_MIN_RADIUS, LEGACY_MAX_RADIUS);
    }

    public static int minRiftDuration(int riftSize) {
        return Math.min(riftSize / 2, 30);
    }

    public static int maxRiftDuration(int riftSize) {
        return Math.min(riftSize, 120);
    }

    public static int randomRiftDuration(RandomSource random, int riftSize) {
        int min = minRiftDuration(riftSize);
        int max = maxRiftDuration(riftSize);
        if (max < min) {
            max = min;
        }
        return randomInclusive(random, min, max);
    }

    public static Vec3 legacySourceVector(LivingEntity owner) {
        return owner.position().add(0.0D, owner.getEyeHeight() - 0.10000000149011612D, 0.0D);
    }

    public static boolean spawnRiftFluxCloud(ServerLevel level, LivingEntity owner, int riftSize, RandomSource random) {
        if (owner == null) {
            return false;
        }
        TCFocusCloudEntity cloud = new TCFocusCloudEntity(
                level,
                legacySourceVector(owner),
                owner,
                randomRiftRadius(random),
                randomRiftDuration(random, riftSize)
        );
        playFluxCastSound(level, owner, random);
        return level.addFreshEntity(cloud);
    }

    private static void playFluxCastSound(ServerLevel level, LivingEntity owner, RandomSource random) {
        BlockPos soundPos = owner.blockPosition().above();
        level.playSound(
                null,
                soundPos,
                SoundEvents.CHORUS_FLOWER_GROW,
                SoundSource.PLAYERS,
                2.0F,
                2.0F + (float) (random.nextGaussian() * 0.10000000149011612D)
        );
    }

    private void executeCloudTick() {
        long now = System.currentTimeMillis();
        cleanupCooldowns(now);
        float radius = getRadius();
        if (radius <= 0.0F) {
            return;
        }

        AABB range = new AABB(position(), position()).inflate(radius);
        List<Entity> nearby = level().getEntities(this, range);
        for (Entity entity : nearby) {
            if (!entity.isAlive()) {
                continue;
            }
            if (entity instanceof TCFocusCloudEntity cloud) {
                Vec3 push = cloud.position().subtract(position()).scale(1.0D / 50.0D);
                cloud.move(MoverType.SELF, push);
            }
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }
            long key = living.getId();
            if (isCoolingDown(key, now)) {
                continue;
            }
            COOLDOWN_MAP.put(key, now + LEGACY_TARGET_COOLDOWN_MS);
            applyFluxEffect(living);
        }

        int blockSamples = Math.max(0, (int) radius);
        for (int index = 0; index < blockSamples; index++) {
            Vec3 direction = new Vec3(random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
            if (direction.lengthSqr() < 1.0E-6D) {
                continue;
            }
            direction = direction.normalize();
            BlockHitResult hit = level().clip(new ClipContext(
                    position(),
                    position().add(direction.scale(radius)),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    this
            ));
            if (hit.getType() != HitResult.Type.BLOCK) {
                continue;
            }
            long key = hit.getBlockPos().asLong();
            if (isCoolingDown(key, now)) {
                continue;
            }
            COOLDOWN_MAP.put(key, now + LEGACY_TARGET_COOLDOWN_MS);
            blockHits++;
            cloudExecutions++;
        }
    }

    private void applyFluxEffect(LivingEntity target) {
        LivingEntity livingOwner = getOwner();
        Entity indirect = livingOwner == null ? this : livingOwner;
        DamageSource source = damageSources().indirectMagic(this, indirect);
        if (target.hurt(source, LEGACY_FLUX_DAMAGE)) {
            entityHits++;
        }
        cloudExecutions++;
    }

    private void tickClientParticles() {
        float radius = getRadius();
        int samples = Math.max(0, (int) radius);
        int color = getEffectColor();
        for (int index = 0; index < samples; index++) {
            double x = getX() + random.nextGaussian() * radius / 2.0D * 0.85D;
            double y = getY() + random.nextGaussian() * radius / 2.0D * 0.85D;
            double z = getZ() + random.nextGaussian() * radius / 2.0D * 0.85D;
            TCFXDispatcher.drawFocusCloudParticle(
                    level(),
                    x,
                    y,
                    z,
                    random.nextGaussian() * 0.01D,
                    random.nextGaussian() * 0.01D,
                    random.nextGaussian() * 0.01D,
                    color
            );
            TCFXDispatcher.drawFluxFocusEffectParticle(
                    level(),
                    getX() + random.nextGaussian() * radius / 2.0D,
                    getY() + random.nextGaussian() * radius / 2.0D,
                    getZ() + random.nextGaussian() * radius / 2.0D,
                    random.nextGaussian() * 0.009999999776482582D,
                    random.nextGaussian() * 0.009999999776482582D,
                    random.nextGaussian() * 0.009999999776482582D
            );
        }
    }

    private static boolean isCoolingDown(long key, long now) {
        Long expires = COOLDOWN_MAP.get(key);
        return expires != null && expires > now;
    }

    private static void cleanupCooldowns(long now) {
        COOLDOWN_MAP.entrySet().removeIf(entry -> entry.getValue() <= now);
    }

    private static int randomInclusive(RandomSource random, int min, int max) {
        if (max <= min) {
            return min;
        }
        return min + random.nextInt(max - min + 1);
    }
}

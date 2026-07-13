package thaumcraft.common.entities;

import java.time.LocalDate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import thaumcraft.Thaumcraft;
import thaumcraft.common.config.TCConfig;

public class TCFirebatEntity extends Monster {
    public static final double LEGACY_MAX_HEALTH = 5.0D;
    public static final double LEGACY_ATTACK_DAMAGE = 1.0D;
    public static final float LEGACY_WIDTH = 0.5F;
    public static final float LEGACY_HEIGHT = 0.9F;
    public static final int LEGACY_ATTACK_COOLDOWN_MIN = 20;
    public static final int LEGACY_ATTACK_COOLDOWN_RANDOM = 20;
    public static final int LEGACY_EXPLOSION_ROLL_BOUND = 10;
    public static final float LEGACY_EXPLOSION_RADIUS = 1.5F;
    public static final int LEGACY_SPAWN_LIGHT_ROLL_BOUND = 7;
    public static final int LEGACY_MIN_FLIGHT_TARGET_Y = 1;
    public static final double LEGACY_TARGET_RANGE = 12.0D;
    public static final double LEGACY_WAKE_RANGE = 4.0D;
    public static final TagKey<Biome> LEGACY_HALLOWEEN_SPAWN_BIOMES = TagKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "legacy_firebat_halloween_spawn_biomes")
    );

    private static final TargetingConditions WAKE_TARGETING = TargetingConditions.forNonCombat().range(LEGACY_WAKE_RANGE);
    private static final TargetingConditions ATTACK_TARGETING = TargetingConditions.forCombat().range(LEGACY_TARGET_RANGE);
    private static final EntityDataAccessor<Boolean> HANGING =
            SynchedEntityData.defineId(TCFirebatEntity.class, EntityDataSerializers.BOOLEAN);

    @Nullable
    private BlockPos currentFlightTarget;
    private int attackTime;
    private int damBonus;

    public TCFirebatEntity(EntityType<? extends TCFirebatEntity> type, Level level) {
        super(type, level);
        setResting(true);
        setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, LEGACY_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, LEGACY_ATTACK_DAMAGE);
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HANGING, false);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    protected float getSoundVolume() {
        return 0.1F;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 0.95F;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isResting() && random.nextInt(4) != 0 ? null : SoundEvents.BAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.BAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BAT_DEATH;
    }

    @Override
    public void aiStep() {
        if (isInWaterRainOrBubble()) {
            hurt(damageSources().drown(), 1.0F);
        }
        super.aiStep();
    }

    @Override
    public void tick() {
        setNoGravity(true);
        super.tick();
        if (isResting()) {
            setDeltaMovement(Vec3.ZERO);
            setPosRaw(getX(), (double) Mth.floor(getY()) + 1.0D - getBbHeight(), getZ());
        } else {
            setDeltaMovement(getDeltaMovement().multiply(1.0D, 0.6000000238418579D, 1.0D));
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (attackTime > 0) {
            attackTime--;
        }

        BlockPos pos = blockPosition();
        BlockPos above = pos.above();
        if (isResting()) {
            if (!level().getBlockState(above).isRedstoneConductor(level(), above)) {
                setResting(false);
                level().levelEvent(null, 1025, pos, 0);
            } else {
                if (random.nextInt(200) == 0) {
                    yHeadRot = (float) random.nextInt(360);
                }
                if (level().getNearestPlayer(WAKE_TARGETING, this) != null) {
                    setResting(false);
                    level().levelEvent(null, 1025, pos, 0);
                }
            }
        } else if (getTarget() == null) {
            updateFreeFlight(pos);
        } else {
            updateTargetFlight(getTarget());
        }

        LivingEntity target = getTarget();
        if (target == null) {
            setTarget(findPlayerToAttack());
        } else if (target.isAlive()) {
            float distance = distanceTo(target);
            if (isAlive() && hasLineOfSight(target)) {
                attackEntity(target, distance);
            }
        } else {
            setTarget(null);
        }

        if (getTarget() instanceof Player player && player.getAbilities().invulnerable) {
            setTarget(null);
        }
    }

    private void updateFreeFlight(BlockPos pos) {
        if (currentFlightTarget != null
                && (!level().isEmptyBlock(currentFlightTarget) || currentFlightTarget.getY() < LEGACY_MIN_FLIGHT_TARGET_Y)) {
            currentFlightTarget = null;
        }
        if (currentFlightTarget == null || random.nextInt(30) == 0 || currentFlightTarget.closerToCenterThan(position(), 2.0D)) {
            currentFlightTarget = BlockPos.containing(
                    getX() + random.nextInt(7) - random.nextInt(7),
                    getY() + random.nextInt(6) - 2.0D,
                    getZ() + random.nextInt(7) - random.nextInt(7)
            );
        }
        flyToward(currentFlightTarget.getX() + 0.5D, currentFlightTarget.getY() + 0.1D, currentFlightTarget.getZ() + 0.5D);
        if (random.nextInt(100) == 0 && level().getBlockState(pos.above()).isRedstoneConductor(level(), pos.above())) {
            setResting(true);
        }
    }

    private void updateTargetFlight(@Nullable LivingEntity target) {
        if (target != null) {
            flyToward(target.getX(), target.getY() + target.getEyeHeight() * 0.66F, target.getZ());
        }
    }

    private void flyToward(double x, double y, double z) {
        Vec3 movement = getDeltaMovement();
        double dx = x - getX();
        double dy = y - getY();
        double dz = z - getZ();
        Vec3 next = movement.add(
                (Math.signum(dx) * 0.5D - movement.x) * 0.10000000149011612D,
                (Math.signum(dy) * 0.699999988079071D - movement.y) * 0.10000000149011612D,
                (Math.signum(dz) * 0.5D - movement.z) * 0.10000000149011612D
        );
        setDeltaMovement(next);
        float yaw = (float) (Mth.atan2(next.z, next.x) * 180.0F / (float) Math.PI) - 90.0F;
        float deltaYaw = Mth.wrapDegrees(yaw - getYRot());
        zza = 0.5F;
        setYRot(getYRot() + deltaYaw);
        yBodyRot = getYRot();
    }

    @Nullable
    protected LivingEntity findPlayerToAttack() {
        return level().getNearestPlayer(ATTACK_TARGETING, this);
    }

    protected void attackEntity(LivingEntity target, float distance) {
        if (attackTime <= 0
                && distance < Math.max(2.5F, target.getBbWidth() * 1.1F)
                && target.getBoundingBox().maxY > getBoundingBox().minY
                && target.getBoundingBox().minY < getBoundingBox().maxY) {
            attackTime = LEGACY_ATTACK_COOLDOWN_MIN + random.nextInt(LEGACY_ATTACK_COOLDOWN_RANDOM);
            if (random.nextInt(LEGACY_EXPLOSION_ROLL_BOUND) == 0 && !level().isClientSide) {
                target.invulnerableTime = 0;
                level().explode(this, getX(), getY(), getZ(), LEGACY_EXPLOSION_RADIUS, Level.ExplosionInteraction.NONE);
                discard();
            }
            playSound(SoundEvents.BAT_HURT, 0.5F, 0.9F + random.nextFloat() * 0.2F);
            doHurtTarget(target);
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (isControlledByLocalInstance()) {
            moveRelative(0.02F, travelVector);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.91D));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source) || source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_EXPLOSION)) {
            return false;
        }
        if (!level().isClientSide && isResting()) {
            setResting(false);
        }
        return super.hurt(source, amount);
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.server.level.ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        spawnAtLocation(new ItemStack(Items.GUNPOWDER));
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setResting(tag.getBoolean("hang"));
        damBonus = tag.getByte("damBonus");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("hang", isResting());
        tag.putByte("damBonus", (byte) damBonus);
    }

    public boolean isResting() {
        return entityData.get(HANGING);
    }

    public void setResting(boolean resting) {
        entityData.set(HANGING, resting);
    }

    public int attackTimeForValidation() {
        return attackTime;
    }

    public int damBonusForValidation() {
        return damBonus;
    }

    public static boolean checkFirebatSpawnRules(
            EntityType<TCFirebatEntity> type,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        boolean netherBiome = level.getBiome(pos).is(BiomeTags.IS_NETHER);
        boolean halloweenBiome = level.getBiome(pos).is(LEGACY_HALLOWEEN_SPAWN_BIOMES);
        boolean halloweenDate = isLegacyHalloween(LocalDate.now());
        int lightRoll = random.nextInt(LEGACY_SPAWN_LIGHT_ROLL_BOUND);
        return testLegacySpawnGatesForValidation(
                TCConfig.ALLOW_SPAWN_FIREBAT.get(),
                level.getDifficulty(),
                Monster.checkAnyLightMonsterSpawnRules(type, level, spawnType, pos, random),
                level.getMaxLocalRawBrightness(pos),
                lightRoll,
                netherBiome,
                halloweenBiome,
                halloweenDate
        );
    }

    public static boolean testLegacySpawnGatesForValidation(
            boolean configEnabled,
            Difficulty difficulty,
            boolean mobRulesAllow,
            int localRawBrightness,
            int lightRoll,
            boolean netherBiome,
            boolean halloweenBiome,
            boolean halloweenDate
    ) {
        return configEnabled
                && difficulty != Difficulty.PEACEFUL
                && mobRulesAllow
                && localRawBrightness <= lightRoll
                && (netherBiome || (halloweenBiome && halloweenDate));
    }

    public static boolean isLegacyHalloween(LocalDate date) {
        return date.getMonthValue() == 10 && date.getDayOfMonth() == 31;
    }

    public static Level.ExplosionInteraction legacyExplosionInteractionForValidation() {
        return Level.ExplosionInteraction.NONE;
    }
}

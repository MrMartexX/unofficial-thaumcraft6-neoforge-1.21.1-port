package thaumcraft.common.entities;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class TCGiantBrainyZombieEntity extends TCBrainyZombieEntity {
    public static final double LEGACY_MAX_HEALTH = 60.0D;
    public static final double LEGACY_BASE_ATTACK_DAMAGE = 7.0D;
    public static final int LEGACY_XP_REWARD = 15;
    public static final float LEGACY_LEAP_MOTION = 0.4F;
    public static final float LEGACY_ANGER_DAMAGE_BASE = 7.0F;
    public static final float LEGACY_ANGER_DAMAGE_SCALE = 5.0F;
    public static final float LEGACY_ANGER_DECAY = 0.002F;
    public static final float LEGACY_ANGER_HURT_INCREMENT = 0.1F;
    public static final float LEGACY_MAX_ANGER = 2.0F;
    public static final int LEGACY_ROTTEN_FLESH_LOOPS = 12;
    public static final int LEGACY_ROTTEN_FLESH_PER_DROP = 2;

    private static final EntityDataAccessor<Float> ANGER =
            SynchedEntityData.defineId(TCGiantBrainyZombieEntity.class, EntityDataSerializers.FLOAT);

    public TCGiantBrainyZombieEntity(EntityType<? extends TCGiantBrainyZombieEntity> type, Level level) {
        super(type, level);
        xpReward = LEGACY_XP_REWARD;
        goalSelector.addGoal(2, new LeapAtTargetGoal(this, LEGACY_LEAP_MOTION));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, LEGACY_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, LEGACY_BASE_ATTACK_DAMAGE)
                .add(Attributes.ARMOR, LEGACY_ARMOR_BONUS)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, LEGACY_REINFORCEMENT_CHANCE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANGER, 0.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (getAnger() > 1.0F) {
            setAnger(getAnger() - LEGACY_ANGER_DECAY);
            refreshDimensions();
        }
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(legacyAttackDamageForAnger(getAnger()));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        setAnger(Math.min(LEGACY_MAX_ANGER, getAnger() + LEGACY_ANGER_HURT_INCREMENT));
        return super.hurt(source, amount);
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        float anger = getAnger();
        EntityDimensions dimensions = anger > 1.0F
                ? EntityDimensions.scalable(0.6F + anger * 0.6F, 1.95F + anger * 1.95F)
                : super.getDefaultDimensions(pose);
        float eyeHeight = 1.74F + getAnger() * 1.74F;
        return dimensions.withEyeHeight(isBaby() ? eyeHeight - 0.81F : eyeHeight);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        for (int i = 0; i < LEGACY_ROTTEN_FLESH_LOOPS; i++) {
            if (random.nextBoolean()) {
                spawnAtLocation(new ItemStack(Items.ROTTEN_FLESH, LEGACY_ROTTEN_FLESH_PER_DROP));
            }
        }
    }

    public float getAnger() {
        return entityData.get(ANGER);
    }

    public void setAnger(float anger) {
        entityData.set(ANGER, anger);
    }

    public static double legacyAttackDamageForAnger(float anger) {
        return LEGACY_ANGER_DAMAGE_BASE + (anger - 1.0F) * LEGACY_ANGER_DAMAGE_SCALE;
    }
}

package thaumcraft.common.entities.boss;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.entities.TCGolemOrbEntity;
import thaumcraft.common.entities.TCLongRangeAttackGoal;
import thaumcraft.common.registry.TCSounds;

/**
 * 100% legacy parity implementation of TC6 EntityEldritchGolem:
 * Arm smash attack, ground tremor block crush, Phase 2 headless explosion & health reset,
 * jacobs ladder beam charging, and homing GolemOrb barrages.
 */
public class TCEldritchGolemEntity extends TCBossEntity implements RangedAttackMob {
    private static final EntityDataAccessor<Boolean> HEADLESS =
            SynchedEntityData.defineId(TCEldritchGolemEntity.class, EntityDataSerializers.BOOLEAN);

    private int beamCharge = 0;
    private boolean chargingBeam = false;
    private int attackTimer = 0;
    private boolean headlessGoalsInitialized = false;

    public TCEldritchGolemEntity(EntityType<? extends TCEldritchGolemEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.95D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HEADLESS, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1D, false));
        goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 0.8D));
        goalSelector.addGoal(7, new RandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public boolean isHeadless() {
        return entityData.get(HEADLESS);
    }

    public void setHeadless(boolean headless) {
        entityData.set(HEADLESS, headless);
        if (headless && !headlessGoalsInitialized) {
            makeHeadless();
        }
    }

    private void makeHeadless() {
        headlessGoalsInitialized = true;
        goalSelector.addGoal(2, new TCLongRangeAttackGoal(this, 3.0D, 1.0D, 5, 5, 24.0F));
    }

    @Override
    public void generateBossName() {
        setCustomName(Component.translatable("entity.EldritchGolem.name"));
        if (this.bossEvent != null) {
            this.bossEvent.setName(getDisplayName());
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        setSpawnTimer(100);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    public int getAttackTimer() {
        return attackTimer;
    }

    public int getBeamCharge() {
        return beamCharge;
    }

    public boolean isChargingBeam() {
        return chargingBeam;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && amount >= getHealth() && !isHeadless()) {
            setHeadless(true);
            setSpawnTimer(100);
            setHealth(getMaxHealth());

            double xx = Mth.cos(getYRot() * Mth.DEG_TO_RAD) * 0.75D;
            double zz = Mth.sin(getYRot() * Mth.DEG_TO_RAD) * 0.75D;
            level().explode(this, getX() + xx, getY() + getEyeHeight(), getZ() + zz, 2.0F, Level.ExplosionInteraction.MOB);
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (attackTimer > 0) {
            return false;
        }
        attackTimer = 10;
        level().broadcastEntityEvent(this, (byte) 4);
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.75F;
        boolean hit = target.hurt(damageSources().mobAttack(this), damage);
        if (hit) {
            target.setDeltaMovement(target.getDeltaMovement().add(0.0D, 0.2D, 0.0D));
            if (isHeadless()) {
                target.push(
                        -Mth.sin(getYRot() * Mth.DEG_TO_RAD) * 1.5D,
                        0.1D,
                        Mth.cos(getYRot() * Mth.DEG_TO_RAD) * 1.5D
                );
            }
        }
        return hit;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (hasLineOfSight(target) && !chargingBeam && beamCharge > 0) {
            beamCharge -= 15 + random.nextInt(5);
            Vec3 look = getLookAngle();
            TCGolemOrbEntity blast = new TCGolemOrbEntity(level(), this, target, false);
            blast.setPos(blast.getX() + look.x, blast.getY(), blast.getZ() + look.z);

            double d0 = target.getX() + target.getDeltaMovement().x - getX();
            double d1 = target.getY() + target.getEyeHeight() / 2.0D - getY();
            double d2 = target.getZ() + target.getDeltaMovement().z - getZ();
            blast.shoot(d0, d1, d2, 0.66F, 5.0F);
            playSound(TCSounds.EGATTACK.get(), 1.0F, 1.0F + random.nextFloat() * 0.1F);
            level().addFreshEntity(blast);
        }
    }

    @Override
    public void tick() {
        if (getSpawnTimer() == 150 && !level().isClientSide) {
            level().broadcastEntityEvent(this, (byte) 18);
        }
        if (getSpawnTimer() > 0 && !level().isClientSide) {
            heal(2.0F);
        }
        super.tick();

        if (attackTimer > 0) {
            attackTimer--;
        }

        if (level().isClientSide) {
            if (isHeadless()) {
                Vec3 look = getLookAngle();
                if (random.nextInt(20) == 0) {
                    level().addParticle(
                            ParticleTypes.ELECTRIC_SPARK,
                            getX() + look.x * 0.4D + (random.nextDouble() - 0.5D) * 0.4D,
                            getY() + getEyeHeight() - 0.75D,
                            getZ() + look.z * 0.4D + (random.nextDouble() - 0.5D) * 0.4D,
                            0.0D, 0.05D, 0.0D
                    );
                }
                level().addParticle(
                        ParticleTypes.SMOKE,
                        getX() + look.x * 0.4D,
                        getY() + getEyeHeight() - 1.25D,
                        getZ() + look.z * 0.4D,
                        0.0D, 0.001D, 0.0D
                );
            }
        } else {
            if (isHeadless() && beamCharge <= 0) {
                chargingBeam = true;
            }
            if (isHeadless() && chargingBeam) {
                beamCharge++;
                level().broadcastEntityEvent(this, (byte) 19);
                if (beamCharge >= 150) {
                    chargingBeam = false;
                }
            }

            // Fragile block crushing
            if (getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D) {
                BlockPos pos = blockPosition();
                BlockState state = level().getBlockState(pos);
                float destroySpeed = state.getDestroySpeed(level(), pos);
                if (destroySpeed >= 0.0F && destroySpeed <= 0.15F && !state.isAir()) {
                    level().destroyBlock(pos, true);
                }
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            attackTimer = 10;
            playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        } else if (id == 18) {
            setSpawnTimer(150);
        } else if (id == 19) {
            playSound(TCSounds.ZAP.get(), 0.8F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.05F);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Headless", isHeadless());
        tag.putInt("BeamCharge", beamCharge);
        tag.putBoolean("ChargingBeam", chargingBeam);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setHeadless(tag.getBoolean("Headless"));
        this.beamCharge = tag.getInt("BeamCharge");
        this.chargingBeam = tag.getBoolean("ChargingBeam");
    }
}

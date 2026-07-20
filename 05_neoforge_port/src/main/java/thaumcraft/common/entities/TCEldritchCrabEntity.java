package thaumcraft.common.entities;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.common.registry.TCSounds;

/** TC6 Eldritch Crab foundation: helm state, riding attack cadence, eldritch team rules and husk handoff. */
public class TCEldritchCrabEntity extends Monster implements IEldritchMob {
    public static final double LEGACY_MAX_HEALTH = 20.0D;
    public static final double LEGACY_ATTACK_DAMAGE = 4.0D;
    public static final double LEGACY_SPEED_WITH_HELM = 0.275D;
    public static final double LEGACY_SPEED_NO_HELM = 0.3D;
    public static final int LEGACY_XP_REWARD = 6;
    public static final int LEGACY_RIDING_ATTACK_MIN = 10;
    public static final int LEGACY_RIDING_ATTACK_RANDOM = 10;

    private static final EntityDataAccessor<Boolean> HELM =
            SynchedEntityData.defineId(TCEldritchCrabEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> RIDING =
            SynchedEntityData.defineId(TCEldritchCrabEntity.class, EntityDataSerializers.INT);

    private int attackTime;

    public TCEldritchCrabEntity(EntityType<? extends TCEldritchCrabEntity> type, Level level) {
        super(type, level);
        xpReward = LEGACY_XP_REWARD;
        setCanPickUpLoot(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, LEGACY_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, LEGACY_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, LEGACY_SPEED_NO_HELM);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HELM, false);
        builder.define(RIDING, -1);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.63F));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        goalSelector.addGoal(7, new RandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TCCultistEntity.class, true));
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        setHelm(level.getDifficulty() == Difficulty.HARD || random.nextFloat() < 0.33F);
        if (data == null) {
            data = new SpiderEffectsGroupData();
        }
        if (data instanceof SpiderEffectsGroupData spiderData) {
            spiderData.applyTo(this);
        }
        return data;
    }

    @Override
    public void tick() {
        super.tick();
        attackTime--;
        if (tickCount < 20) {
            fallDistance = 0.0F;
        }
        if (!level().isClientSide) {
            tickLegacyRidingAttack();
        } else if (getVehicle() == null && getRidingId() != -1) {
            Entity entity = level().getEntity(getRidingId());
            if (entity != null) {
                startRiding(entity, true);
            }
        } else if (getVehicle() != null && getRidingId() == -1) {
            stopRiding();
        }
    }

    private void tickLegacyRidingAttack() {
        LivingEntity target = getTarget();
        if (getVehicle() == null
                && target != null
                && !target.isPassenger()
                && !onGround()
                && !hasHelm()
                && target.isAlive()
                && getY() - target.getY() >= target.getBbHeight() / 2.0F
                && distanceToSqr(target) < 4.0D) {
            startRiding(target, true);
            setRidingId(target.getId());
        }

        if (getVehicle() != null && isAlive() && attackTime <= 0) {
            attackTime = LEGACY_RIDING_ATTACK_MIN + random.nextInt(LEGACY_RIDING_ATTACK_RANDOM);
            doHurtTarget(getVehicle());
            if (random.nextFloat() < 0.2F) {
                stopRiding();
                setRidingId(-1);
            }
        }
        if (getVehicle() == null && getRidingId() != -1) {
            setRidingId(-1);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            playSound(TCSounds.CRAB_CLAW.get(), 1.0F, 0.9F + random.nextFloat() * 0.2F);
        }
        return hit;
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        boolean hit = super.hurt(source, amount);
        if (hit && hasHelm() && getHealth() / getMaxHealth() <= 0.5F) {
            setHelm(false);
            getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(LEGACY_SPEED_NO_HELM);
        }
        return hit;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return !effect.getEffect().is(MobEffects.POISON) && super.canBeAffected(effect);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        return entity instanceof TCEldritchCrabEntity || entity instanceof IEldritchMob || super.isAlliedTo(entity);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return TCSounds.CRAB_TALK.get();
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource damageSource) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TCSounds.CRAB_DEATH.get();
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState block) {
        playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 160;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, net.minecraft.world.damagesource.DamageSource damageSource, boolean recentlyHit) {
        if (recentlyHit && random.nextInt(3) == 0) {
            spawnAtLocation(new ItemStack(Items.ENDER_PEARL), 0.0F);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("helm", hasHelm());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setHelm(tag.getBoolean("helm"));
    }

    public boolean hasHelm() {
        return entityData.get(HELM);
    }

    public void setHelm(boolean helm) {
        entityData.set(HELM, helm);
        if (getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(helm ? LEGACY_SPEED_WITH_HELM : LEGACY_SPEED_NO_HELM);
        }
    }

    public int getRidingId() {
        return entityData.get(RIDING);
    }

    public void setRidingId(int ridingId) {
        entityData.set(RIDING, ridingId);
    }

    public int attackTimeForValidation() {
        return attackTime;
    }

    private static final class SpiderEffectsGroupData implements SpawnGroupData {
        private final net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect;

        private SpiderEffectsGroupData() {
            effect = null;
        }

        private void applyTo(TCEldritchCrabEntity crab) {
            if (effect != null) {
                crab.addEffect(new MobEffectInstance(effect, -1));
            }
        }
    }
}

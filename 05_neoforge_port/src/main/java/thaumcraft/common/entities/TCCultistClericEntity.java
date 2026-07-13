package thaumcraft.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.registry.TCSounds;

public class TCCultistClericEntity extends TCCultistEntity implements RangedAttackMob {
    public static final double LEGACY_MAX_HEALTH = 24.0D;
    public static final double LEGACY_RANGED_MIN_DISTANCE = 2.0D;
    public static final double LEGACY_RANGED_SPEED = 1.0D;
    public static final int LEGACY_RANGED_INTERVAL_MIN = 20;
    public static final int LEGACY_RANGED_INTERVAL_MAX = 40;
    public static final float LEGACY_RANGED_RADIUS = 24.0F;
    public static final float LEGACY_GOLEM_ORB_BRANCH_THRESHOLD = 0.66F;
    public static final int LEGACY_FIREBALL_COUNT = 3;

    private static final EntityDataAccessor<Boolean> RITUALIST =
            SynchedEntityData.defineId(TCCultistClericEntity.class, EntityDataSerializers.BOOLEAN);

    public int rage;
    private boolean lastUsedGolemOrbBranch;

    public TCCultistClericEntity(EntityType<? extends TCCultistClericEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createCultistAttributes(LEGACY_MAX_HEALTH);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RITUALIST, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new TCLongRangeAttackGoal(
                this,
                LEGACY_RANGED_MIN_DISTANCE,
                LEGACY_RANGED_SPEED,
                LEGACY_RANGED_INTERVAL_MIN,
                LEGACY_RANGED_INTERVAL_MAX,
                LEGACY_RANGED_RADIUS
        ));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        goalSelector.addGoal(5, new OpenDoorGoal(this, true));
        goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 0.8D));
        goalSelector.addGoal(7, new RandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TCEldritchGuardianEntity.class, true));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractIllager.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && isRitualist() && rage >= 5) {
            setRitualist(false);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) {
            return false;
        }
        setRitualist(false);
        return super.hurt(source, amount);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !isRitualist() && super.removeWhenFarAway(distanceToClosestPlayer);
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return level().getDifficulty() == Difficulty.PEACEFUL && !isRitualist();
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        double dx = target.getX() - getX();
        double dy = target.getBoundingBox().minY + target.getBbHeight() / 2.0F - (getY() + getBbHeight() / 2.0F);
        double dz = target.getZ() - getZ();
        swing(InteractionHand.MAIN_HAND);
        lastUsedGolemOrbBranch = false;

        if (random.nextFloat() > LEGACY_GOLEM_ORB_BRANCH_THRESHOLD) {
            lastUsedGolemOrbBranch = true;
            playSound(TCSounds.EGATTACK.get(), 1.0F, 1.0F + random.nextFloat() * 0.1F);
            return;
        }

        float spread = Mth.sqrt(velocity) * 0.5F;
        level().levelEvent(null, 1009, blockPosition(), 0);
        for (int i = 0; i < LEGACY_FIREBALL_COUNT; i++) {
            SmallFireball fireball = new SmallFireball(level(), this, new Vec3(
                    dx + random.nextGaussian() * spread,
                    dy,
                    dz + random.nextGaussian() * spread
            ));
            fireball.setPos(fireball.getX(), getY() + getBbHeight() / 2.0F + 0.5D, fireball.getZ());
            level().addFreshEntity(fireball);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return TCSounds.CHANT.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 500;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 19) {
            return;
        }
        super.handleEntityEvent(id);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("ritualist", isRitualist());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setRitualist(tag.getBoolean("ritualist"));
    }

    public boolean isRitualist() {
        return entityData.get(RITUALIST);
    }

    public void setRitualist(boolean ritualist) {
        entityData.set(RITUALIST, ritualist);
    }

    public boolean lastUsedGolemOrbBranchForValidation() {
        return lastUsedGolemOrbBranch;
    }
}

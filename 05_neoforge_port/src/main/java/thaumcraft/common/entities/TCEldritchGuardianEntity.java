package thaumcraft.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.warp.TCWarpManager;
import thaumcraft.common.warp.TCWarpType;

/** TC6 Eldritch Guardian foundation: attributes, eldritch team rules and non-projectile ranged curse branch. */
public class TCEldritchGuardianEntity extends Monster implements IEldritchMob {
    private BlockPos legacyHomePos;
    private int legacyHomeDistance;
    private boolean lastBlast;

    public float armLiftL;
    public float armLiftR;

    public TCEldritchGuardianEntity(EntityType<? extends TCEldritchGuardianEntity> type, Level level) {
        super(type, level);
        xpReward = 20;
        setCanPickUpLoot(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.ARMOR, 4.0D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (armLiftL > 0.0F) {
                armLiftL -= 0.05F;
            }
            if (armLiftR > 0.0F) {
                armLiftR -= 0.05F;
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        float adjusted = source.is(DamageTypeTags.WITCH_RESISTANT_TO) ? amount / 2.0F : amount;
        return super.hurt(source, adjusted);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living && getMainHandItem().isEmpty() && isOnFire()) {
            int difficultyId = level().getDifficulty().getId();
            if (random.nextFloat() < difficultyId * 0.3F) {
                living.igniteForSeconds(2 * difficultyId);
            }
        }
        return hit;
    }

    @Override
    public boolean canAttackType(EntityType<?> type) {
        return type != TCEntityTypes.ELDRITCH_GUARDIAN.get() && super.canAttackType(type);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof IEldritchMob) && super.canAttack(target);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        return entity instanceof IEldritchMob || super.isAlliedTo(entity);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return legacyHomePos == null;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return level().getDifficulty() == Difficulty.PEACEFUL;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return TCSounds.EGIDLE.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TCSounds.EGDEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 1.5F;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 500;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 15) {
            armLiftL = 0.5F;
        } else if (id == 16) {
            armLiftR = 0.5F;
        } else if (id == 17) {
            armLiftL = 0.9F;
            armLiftR = 0.9F;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (legacyHomePos != null && legacyHomeDistance > 0) {
            tag.putInt("HomeD", legacyHomeDistance);
            tag.putInt("HomeX", legacyHomePos.getX());
            tag.putInt("HomeY", legacyHomePos.getY());
            tag.putInt("HomeZ", legacyHomePos.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HomeD")) {
            legacyHomeDistance = tag.getInt("HomeD");
            legacyHomePos = new BlockPos(tag.getInt("HomeX"), tag.getInt("HomeY"), tag.getInt("HomeZ"));
        }
    }

    public boolean performLegacyRangedAttack(LivingEntity target) {
        if (!hasLineOfSight(target)) {
            return false;
        }
        if (random.nextFloat() > 0.15F) {
            lastBlast = !lastBlast;
            level().broadcastEntityEvent(this, (byte) (lastBlast ? 16 : 15));
            playSound(TCSounds.EGATTACK.get(), 2.0F, 1.0F + random.nextFloat() * 0.1F);
            return false;
        }

        level().broadcastEntityEvent(this, (byte) 17);
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 400, 0));
        if (target instanceof ServerPlayer player) {
            TCWarpManager.add(player, TCWarpType.TEMPORARY, 1 + random.nextInt(3));
        }
        playSound(TCSounds.EGSCREECH.get(), 3.0F, 1.0F + random.nextFloat() * 0.1F);
        return true;
    }

    public boolean canSpawnLikeLegacy() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        AABB checkBox = new AABB(getX(), getY(), getZ(), getX() + 1.0D, getY() + 1.0D, getZ() + 1.0D)
                .inflate(32.0D, 16.0D, 32.0D);
        return serverLevel.getEntitiesOfClass(TCEldritchGuardianEntity.class, checkBox, guardian -> guardian != this).isEmpty()
                && level().noCollision(this)
                && !level().containsAnyLiquid(getBoundingBox());
    }

    public void setLegacyHomeForValidation(BlockPos pos, int distance) {
        legacyHomePos = pos;
        legacyHomeDistance = distance;
    }
}

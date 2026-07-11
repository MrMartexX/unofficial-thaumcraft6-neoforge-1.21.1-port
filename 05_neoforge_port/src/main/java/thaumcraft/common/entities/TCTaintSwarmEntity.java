package thaumcraft.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.blocks.world.taint.TCTaintHelper;
import thaumcraft.common.registry.TCItems;

/** TC6 taint swarm foundation: summoned state, no-push flight movement, weakness attack and taint-seed flight target. */
public class TCTaintSwarmEntity extends Monster implements ITaintedMob {
    private static final EntityDataAccessor<Boolean> SUMMONED =
            SynchedEntityData.defineId(TCTaintSwarmEntity.class, EntityDataSerializers.BOOLEAN);
    private BlockPos currentFlightTarget;
    private int attackTime;
    private int damageBonus;

    public TCTaintSwarmEntity(EntityType<? extends TCTaintSwarmEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SUMMONED, false);
    }

    @Override
    protected void registerGoals() {
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 8, true, false,
                player -> !getIsSummoned()));
    }

    @Override
    public void tick() {
        super.tick();
        setDeltaMovement(getDeltaMovement().multiply(1.0D, 0.6000000238418579D, 1.0D));
        if (level().isClientSide) {
            return;
        }
        if (attackTime > 0) {
            attackTime--;
        }
        LivingEntity target = getTarget();
        if (target == null) {
            if (getIsSummoned() && tickCount % 20 == 0) {
                hurt(damageSources().generic(), 5.0F);
            }
            flyTowardWanderTarget();
        } else {
            flyToward(target.getX(), target.getY() + target.getEyeHeight(), target.getZ(), 0.025D);
            if (target.isAlive() && hasLineOfSight(target) && distanceTo(target) < 3.0F) {
                attackSwarmTarget(target);
            }
            if (target instanceof Player player && (player.isCreative() || player.isSpectator())) {
                setTarget(null);
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result && !level().isClientSide && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
        }
        return result;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof ITaintedMob) && super.canAttack(target);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        return entity instanceof ITaintedMob || super.isAlliedTo(entity);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false;
    }

    @Override
    protected boolean isAffectedByFluids() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("summoned", getIsSummoned());
        tag.putByte("damBonus", (byte) damageBonus);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setIsSummoned(tag.getBoolean("summoned"));
        setDamageBonusForValidation(tag.getByte("damBonus"));
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, net.minecraft.world.damagesource.DamageSource damageSource, boolean recentlyHit) {
        if (random.nextBoolean()) {
            spawnAtLocation(new ItemStack(TCItems.CRYSTAL_ESSENCE_VITIUM.get()), getBbHeight() / 2.0F);
        }
    }

    public boolean getIsSummoned() {
        return entityData.get(SUMMONED);
    }

    public void setIsSummoned(boolean summoned) {
        entityData.set(SUMMONED, summoned);
    }

    public void setDamageBonusForValidation(int damageBonus) {
        this.damageBonus = damageBonus;
        if (getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D + damageBonus);
        }
    }

    public int damageBonusForValidation() {
        return damageBonus;
    }

    private void attackSwarmTarget(LivingEntity target) {
        if (attackTime > 0
                || target.getBoundingBox().maxY <= getBoundingBox().minY
                || target.getBoundingBox().minY >= getBoundingBox().maxY) {
            return;
        }
        attackTime = 15 + random.nextInt(10);
        Vec3 previousMotion = target.getDeltaMovement();
        if (doHurtTarget(target)) {
            target.setDeltaMovement(previousMotion);
            target.hasImpulse = false;
        }
    }

    private void flyTowardWanderTarget() {
        if (currentFlightTarget != null
                && (!level().isEmptyBlock(currentFlightTarget)
                || currentFlightTarget.getY() < 1
                || !TCTaintHelper.isNearTaintSeed(level(), currentFlightTarget))) {
            currentFlightTarget = null;
        }
        if (currentFlightTarget == null || random.nextInt(30) == 0 || distanceToSqr(Vec3.atCenterOf(currentFlightTarget)) < 4.0D) {
            currentFlightTarget = blockPosition().offset(
                    random.nextInt(7) - random.nextInt(7),
                    random.nextInt(6) - 2,
                    random.nextInt(7) - random.nextInt(7)
            );
        }
        flyToward(currentFlightTarget.getX() + 0.5D, currentFlightTarget.getY() + 0.1D, currentFlightTarget.getZ() + 0.5D, 0.015D);
    }

    private void flyToward(double x, double y, double z, double horizontalFactor) {
        Vec3 motion = getDeltaMovement();
        double dx = x - getX();
        double dy = y - getY();
        double dz = z - getZ();
        Vec3 next = new Vec3(
                motion.x + (Math.signum(dx) * 0.5D - motion.x) * horizontalFactor,
                motion.y + (Math.signum(dy) * 0.699999988079071D - motion.y) * 0.10000000149011612D,
                motion.z + (Math.signum(dz) * 0.5D - motion.z) * horizontalFactor
        );
        setDeltaMovement(next);
        setYRot(getYRot() + Mth.wrapDegrees((float) (Mth.atan2(next.z, next.x) * Mth.RAD_TO_DEG) - 90.0F - getYRot()));
        zza = 0.1F;
    }
}

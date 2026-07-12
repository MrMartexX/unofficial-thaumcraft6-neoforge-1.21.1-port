package thaumcraft.common.entities;

import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.TCAspectVariantStacks;
import thaumcraft.common.lib.fx.TCFXDispatcher;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCSounds;

/** TC6 Wisp server behavior ported from legacy EntityWisp. Client-only particles/rendering stay outside this class. */
public class TCWispEntity extends FlyingMob {
    private static final EntityDataAccessor<String> TYPE =
            SynchedEntityData.defineId(TCWispEntity.class, EntityDataSerializers.STRING);
    private int aggroCooldown;
    private int prevAttackCounter;
    private int attackCounter;
    private BlockPos currentFlightTarget;

    public TCWispEntity(EntityType<? extends TCWispEntity> type, Level level) {
        super(type, level);
        xpReward = 5;
        setNoGravity(true);
    }

    public TCWispEntity(Level level, double x, double y, double z) {
        this(TCEntityTypes.WISP.get(), level);
        setPos(x, y, z);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 22.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.15D)
                .add(Attributes.FLYING_SPEED, 0.15D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TYPE, "");
    }

    @Override
    public void tick() {
        super.tick();
        setNoGravity(true);
        setDeltaMovement(getDeltaMovement().multiply(1.0D, 0.6000000238418579D, 1.0D));
        if (level().isClientSide && isAlive() && random.nextBoolean()) {
            Aspect aspect = Aspect.getAspect(getWispType());
            if (aspect != null) {
                TCFXDispatcher.drawWispParticles(
                        level(),
                        getX() + (random.nextFloat() - random.nextFloat()) * 0.7F,
                        getY() + (random.nextFloat() - random.nextFloat()) * 0.7F,
                        getZ() + (random.nextFloat() - random.nextFloat()) * 0.7F,
                        0.0D,
                        0.0D,
                        0.0D,
                        aspect.getColor(),
                        0
                );
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (level().getDifficulty() == Difficulty.PEACEFUL) {
            discard();
            return;
        }
        if (Aspect.getAspect(getWispType()) == null) {
            setWispType(randomAspectTag());
        }
        tickLegacyAi();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (attacker instanceof LivingEntity living) {
            setTarget(living);
            aggroCooldown = 200;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected int decreaseAirSupply(int air) {
        return air;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return TCSounds.WISPLIVE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return net.minecraft.sounds.SoundEvents.LAVA_EXTINGUISH;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TCSounds.WISPDEAD.get();
    }

    @Override
    protected float getSoundVolume() {
        return 0.25F;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        Aspect aspect = Aspect.getAspect(getWispType());
        if (aspect != null) {
            spawnAtLocation(TCAspectVariantStacks.crystal(aspect), 0.0F);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Type", getWispType());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setWispType(tag.getString("Type"));
    }

    public String getWispType() {
        return entityData.get(TYPE);
    }

    public void setWispType(String type) {
        entityData.set(TYPE, type == null ? "" : type);
    }

    public int getAggroCooldownForValidation() {
        return aggroCooldown;
    }

    public int getAttackCounterForValidation() {
        return attackCounter;
    }

    public int getPreviousAttackCounterForValidation() {
        return prevAttackCounter;
    }

    public BlockPos getCurrentFlightTargetForValidation() {
        return currentFlightTarget;
    }

    public void setAggroCooldownForValidation(int aggroCooldown) {
        this.aggroCooldown = aggroCooldown;
    }

    public void setAttackCounterForValidation(int attackCounter) {
        this.attackCounter = attackCounter;
    }

    public void setCurrentFlightTargetForValidation(BlockPos currentFlightTarget) {
        this.currentFlightTarget = currentFlightTarget;
    }

    public void tickLegacyAiForValidation() {
        tickLegacyAi();
    }

    public boolean canSpawnLikeLegacy() {
        int nearby = level().getEntitiesOfClass(TCWispEntity.class, getBoundingBox().inflate(16.0D), wisp -> wisp != this).size();
        return nearby < 8
                && level().getDifficulty() != Difficulty.PEACEFUL
                && level().noCollision(this)
                && isValidLightLevelLikeLegacy();
    }

    public int getMaxSpawnClusterSize() {
        return 2;
    }

    private boolean isValidLightLevelLikeLegacy() {
        if (level().getBrightness(net.minecraft.world.level.LightLayer.SKY, blockPosition()) > random.nextInt(32)) {
            return false;
        }
        return level().getMaxLocalRawBrightness(blockPosition()) <= random.nextInt(8);
    }

    private void tickLegacyAi() {
        prevAttackCounter = attackCounter;
        double attackRange = 16.0D;
        LivingEntity target = getTarget();

        if (target != null && !target.isAlive()) {
            setTarget(null);
            target = null;
        }
        if (target instanceof Player player && player.isCreative()) {
            setTarget(null);
            target = null;
        }

        if (target == null || !hasLineOfSight(target)) {
            wanderLikeLegacy();
        } else if (distanceToSqr(target) > attackRange * attackRange / 2.0D && hasLineOfSight(target)) {
            moveToward(
                    target.getX() - getX(),
                    target.getY() + target.getEyeHeight() * 0.66D - getY(),
                    target.getZ() - getZ(),
                    0.5D,
                    0.699999988079071D,
                    0.5D,
                    0.5F
            );
        }

        --aggroCooldown;
        target = getTarget();
        if (random.nextInt(1000) == 0 && (target == null || aggroCooldown-- <= 0)) {
            Player closest = level().getNearestPlayer(this, 16.0D);
            if (closest != null) {
                setTarget(closest);
                aggroCooldown = 50;
                target = closest;
            }
        }

        if (isAlive() && target != null && distanceToSqr(target) < attackRange * attackRange) {
            getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (hasLineOfSight(target)) {
                ++attackCounter;
                if (attackCounter == 20) {
                    playSound(TCSounds.ZAP.get(), 1.0F, 1.1F);
                    if (level() instanceof ServerLevel serverLevel) {
                        TCEntityFXNetwork.sendWispZap(serverLevel, this, target);
                    }
                    applyLegacyZapDamage(target);
                    attackCounter = -20 + random.nextInt(20);
                }
            } else if (attackCounter > 0) {
                --attackCounter;
            }
        }
    }

    private void wanderLikeLegacy() {
        if (currentFlightTarget != null && (!level().isEmptyBlock(currentFlightTarget)
                || currentFlightTarget.getY() < 1
                || currentFlightTarget.getY() > level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, currentFlightTarget).above(8).getY())) {
            currentFlightTarget = null;
        }

        if (currentFlightTarget == null
                || random.nextInt(30) == 0
                || currentFlightTarget.distToCenterSqr(position()) < 16.0D) {
            currentFlightTarget = new BlockPos(
                    (int) getX() + random.nextInt(7) - random.nextInt(7),
                    (int) getY() + random.nextInt(6) - 2,
                    (int) getZ() + random.nextInt(7) - random.nextInt(7)
            );
        }

        moveToward(
                currentFlightTarget.getX() + 0.5D - getX(),
                currentFlightTarget.getY() + 0.5D - getY(),
                currentFlightTarget.getZ() + 0.5D - getZ(),
                0.5D,
                0.699999988079071D,
                0.5D,
                0.15F
        );
    }

    private void moveToward(double deltaX, double deltaY, double deltaZ, double horizontalSpeed, double verticalSpeed, double zSpeed, float speed) {
        Vec3 motion = getDeltaMovement();
        double motionX = motion.x + (Math.signum(deltaX) * horizontalSpeed - motion.x) * 0.10000000149011612D;
        double motionY = motion.y + (Math.signum(deltaY) * verticalSpeed - motion.y) * 0.10000000149011612D;
        double motionZ = motion.z + (Math.signum(deltaZ) * zSpeed - motion.z) * 0.10000000149011612D;
        setDeltaMovement(motionX, motionY, motionZ);

        float wantedYaw = (float) (Math.atan2(motionZ, motionX) * Mth.RAD_TO_DEG) - 90.0F;
        setYRot(getYRot() + Mth.wrapDegrees(wantedYaw - getYRot()));
        yBodyRot = getYRot();
        setSpeed(speed);
    }

    private void applyLegacyZapDamage(LivingEntity target) {
        Vec3 targetMotion = target.getDeltaMovement();
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (Math.abs(targetMotion.x) > 0.1D || Math.abs(targetMotion.y) > 0.1D || Math.abs(targetMotion.z) > 0.1D) {
            if (random.nextInt(5) < 2) {
                target.hurt(damageSources().mobAttack(this), damage);
            }
        } else if (random.nextInt(3) != 0) {
            target.hurt(damageSources().mobAttack(this), damage + 1.0F);
        }
    }

    private String randomAspectTag() {
        if (random.nextInt(10) != 0) {
            ArrayList<Aspect> primals = Aspect.getPrimalAspects();
            return primals.get(random.nextInt(primals.size())).getTag();
        }
        ArrayList<Aspect> compounds = Aspect.getCompoundAspects();
        return compounds.get(random.nextInt(compounds.size())).getTag();
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (isControlledByLocalInstance()) {
            moveRelative(0.02F, travelVector);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.8D));
        }
    }
}

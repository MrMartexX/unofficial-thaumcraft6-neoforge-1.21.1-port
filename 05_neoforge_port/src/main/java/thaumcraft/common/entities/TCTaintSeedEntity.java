package thaumcraft.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.blocks.world.taint.TCTaintHelper;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCMobEffects;

/** TC6 Taint Seed/Prime server behavior: immobile seed registry, flux-fed spread and aura pollution. */
public class TCTaintSeedEntity extends PathfinderMob implements ITaintedMob {
    private static final EntityDataAccessor<Integer> BOOST =
            SynchedEntityData.defineId(TCTaintSeedEntity.class, EntityDataSerializers.INT);
    private boolean firstRun;
    private float attackAnim;

    public TCTaintSeedEntity(EntityType<? extends TCTaintSeedEntity> type, Level level) {
        super(type, level);
        xpReward = isPrime() ? 12 : 8;
    }

    public TCTaintSeedEntity(Level level, BlockPos pos, boolean prime) {
        this(prime ? TCEntityTypes.TAINT_SEED_PRIME.get() : TCEntityTypes.TAINT_SEED.get(), level);
        setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 75.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public static AttributeSupplier.Builder createPrimeAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 150.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BOOST, 0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            attackAnim *= 0.75F;
            if (attackAnim < 0.001F) {
                attackAnim = 0.0F;
            }
            return;
        }
        if (!firstRun || tickCount % 1200 == 0) {
            TCTaintHelper.removeTaintSeed(level(), blockPosition());
            TCTaintHelper.addTaintSeed(level(), blockPosition());
            firstRun = true;
        }
        if (!isAlive()) {
            return;
        }
        boolean tickFlag = tickCount % 20 == 0;
        if (getBoost() > 0 || tickFlag) {
            float mod = getBoost() > 0 ? 1.0F : thaumcraft.common.world.aura.AuraHandler.getFluxSaturation(level(), blockPosition());
            if (getBoost() > 0) {
                setBoost(getBoost() - 1);
            }
            if (mod <= 0.0F) {
                hurt(damageSources().magic(), 0.5F);
                AuraHelper.polluteAura(level(), blockPosition(), 0.1F, false);
            } else {
                TCTaintHelper.spreadFibres(level(), randomSpreadPos(), true);
            }
        }
        if (tickFlag) {
            LivingEntity target = getTarget();
            if (target != null
                    && distanceToSqr(target) < getArea() * 256.0D
                    && getSensing().hasLineOfSight(target)) {
                doHurtTarget(target);
            }
            for (LivingEntity living : level().getEntitiesOfClass(LivingEntity.class,
                    new AABB(blockPosition()).inflate(getArea() * 4.0D),
                    living -> living != this && !(living instanceof ITaintedMob))) {
                living.addEffect(new MobEffectInstance(TCMobEffects.FLUX_TAINT, 100, getArea() - 1, false, true));
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        level().broadcastEntityEvent(this, (byte) 16);
        level().playSound(null, blockPosition(), SoundEvents.SLIME_ATTACK, SoundSource.HOSTILE, getSoundVolume(), getVoicePitch());
        return super.doHurtTarget(target);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 16) {
            attackAnim = 0.5F;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void die(net.minecraft.world.damagesource.DamageSource source) {
        TCTaintHelper.removeTaintSeed(level(), blockPosition());
        super.die(source);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide) {
            TCTaintHelper.removeTaintSeed(level(), blockPosition());
        }
        super.remove(reason);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("boost", getBoost());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setBoost(tag.getInt("boost"));
    }

    @Override
    public void travel(Vec3 travelVector) {
    }

    @Override
    public void move(MoverType type, Vec3 pos) {
        super.move(type, new Vec3(0.0D, Math.min(0.0D, pos.y), 0.0D));
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canAttackType(EntityType<?> type) {
        return type != TCEntityTypes.TAINT_SEED.get()
                && type != TCEntityTypes.TAINT_SEED_PRIME.get()
                && super.canAttackType(type);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        return entity instanceof ITaintedMob || super.isAlliedTo(entity);
    }

    @Override
    protected int decreaseAirSupply(int air) {
        return air;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, net.minecraft.world.damagesource.DamageSource damageSource, boolean recentlyHit) {
        int drops = isPrime() ? 1 + random.nextInt(3) : 1;
        for (int index = 0; index < drops; index++) {
            spawnAtLocation(new ItemStack(TCItems.CRYSTAL_ESSENCE_VITIUM.get()), getBbHeight() / 2.0F);
        }
    }

    public int getArea() {
        return isPrime() ? 2 : 1;
    }

    public int getBoost() {
        return entityData.get(BOOST);
    }

    public void setBoost(int boost) {
        entityData.set(BOOST, Math.max(0, boost));
    }

    public float attackAnim() {
        return attackAnim;
    }

    public boolean canSpawnLikeLegacy() {
        return level().getDifficulty() != Difficulty.PEACEFUL
                && level().noCollision(this)
                && level().getEntitiesOfClass(TCTaintSeedEntity.class,
                        new AABB(blockPosition()).inflate(TCTaintHelper.taintSpreadArea() * 0.8D),
                        seed -> seed != this).isEmpty();
    }

    private boolean isPrime() {
        return getType() == TCEntityTypes.TAINT_SEED_PRIME.get();
    }

    private BlockPos randomSpreadPos() {
        int area = getArea();
        return blockPosition().offset(
                random.nextInt(area * 6 + 1) - area * 3,
                random.nextInt(area * 2 + 1) - area,
                random.nextInt(area * 6 + 1) - area * 3
        );
    }
}

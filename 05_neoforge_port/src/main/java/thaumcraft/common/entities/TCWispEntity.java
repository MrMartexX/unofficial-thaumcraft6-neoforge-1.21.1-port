package thaumcraft.common.entities;

import java.util.ArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.TCAspectVariantStacks;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCSounds;

/** Minimal TC6 Wisp server foundation needed by Flux Rift event 0. Full AI/render parity remains a later entity slice. */
public class TCWispEntity extends FlyingMob {
    private static final EntityDataAccessor<String> TYPE =
            SynchedEntityData.defineId(TCWispEntity.class, EntityDataSerializers.STRING);

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
        if (level().isClientSide) {
            return;
        }
        if (level().getDifficulty() == Difficulty.PEACEFUL) {
            discard();
            return;
        }
        if (Aspect.getAspect(getWispType()) == null) {
            setWispType(randomAspectTag());
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (attacker instanceof LivingEntity living) {
            setTarget(living);
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
            move(net.minecraft.world.entity.MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.8D));
        }
    }
}

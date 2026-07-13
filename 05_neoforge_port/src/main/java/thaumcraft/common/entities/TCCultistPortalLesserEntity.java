package thaumcraft.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCSounds;

/** TC6 lesser Crimson portal foundation with legacy activation, collision and minion-spawn cadence. */
public class TCCultistPortalLesserEntity extends Monster {
    private static final EntityDataAccessor<Boolean> ACTIVE =
            SynchedEntityData.defineId(TCCultistPortalLesserEntity.class, EntityDataSerializers.BOOLEAN);

    private int stageCounter = 100;
    private int activeCounter;
    private int pulse;
    private int spawnedMinionCount;
    private EntityType<?> lastSpawnedMinionType;

    public TCCultistPortalLesserEntity(EntityType<? extends TCCultistPortalLesserEntity> type, Level level) {
        super(type, level);
        xpReward = 10;
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.ATTACK_DAMAGE, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 4.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ACTIVE, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (isActive()) {
            activeCounter++;
        }
        if (!level().isClientSide) {
            tickLegacyActivation();
        }
        if (pulse > 0) {
            pulse--;
        }
    }

    private void tickLegacyActivation() {
        if (!isActive()) {
            if (tickCount % 10 == 0 && level().getNearestPlayer(this, 32.0D) != null) {
                setActive(true);
                playSound(TCSounds.CRAFTSTART.get(), 1.0F, 1.0F);
            }
            return;
        }

        if (stageCounter-- <= 0) {
            Player player = level().getNearestPlayer(this, 32.0D);
            if (player != null && hasLineOfSight(player) && cultistMinionBudgetAfterNearbyCultists() > 0) {
                level().broadcastEntityEvent(this, (byte) 16);
                spawnLegacyMinion();
            }
            stageCounter = 50 + random.nextInt(50);
        }
    }

    private TCCultistEntity spawnLegacyMinion() {
        return spawnLegacyMinion(random.nextFloat() <= 0.33F);
    }

    private TCCultistEntity spawnLegacyMinion(boolean cleric) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        TCCultistEntity cultist = cleric
                ? TCEntityTypes.CULTIST_CLERIC.get().create(serverLevel)
                : TCEntityTypes.CULTIST_KNIGHT.get().create(serverLevel);
        if (cultist == null) {
            return null;
        }

        cultist.setPos(
                getX() + random.nextFloat() - random.nextFloat(),
                getY() + 0.25D,
                getZ() + random.nextFloat() - random.nextFloat()
        );
        cultist.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(cultist.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
        serverLevel.addFreshEntity(cultist);
        cultist.spawnExplosionParticle();
        cultist.playSound(TCSounds.WANDFAIL.get(), 1.0F, 1.0F);
        hurt(damageSources().fellOutOfWorld(), 5.0F + random.nextInt(5));
        spawnedMinionCount++;
        lastSpawnedMinionType = cultist.getType();
        return cultist;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public void playerTouch(Player player) {
        if (distanceToSqr(player) < 3.0D && player.hurt(damageSources().indirectMagic(this, this), 4.0F)) {
            playSound(TCSounds.ZAP.get(), 1.0F, (random.nextFloat() - random.nextFloat()) * 0.1F + 1.0F);
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean addEffect(MobEffectInstance effect, Entity source) {
        return false;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return TCSounds.MONOLITH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return TCSounds.ZAP.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TCSounds.SHOCK.get();
    }

    @Override
    protected float getSoundVolume() {
        return 0.75F;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 540;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!level().isClientSide) {
            level().explode(this, getX(), getY(), getZ(), 1.5F, Level.ExplosionInteraction.NONE);
        }
        super.die(damageSource);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 16) {
            pulse = 10;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("active", isActive());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setActive(tag.getBoolean("active"));
    }

    public boolean isActive() {
        return entityData.get(ACTIVE);
    }

    public void setActive(boolean active) {
        entityData.set(ACTIVE, active);
    }

    public int activeCounterForValidation() {
        return activeCounter;
    }

    public int pulseForValidation() {
        return pulse;
    }

    public int spawnedMinionCountForValidation() {
        return spawnedMinionCount;
    }

    public EntityType<?> lastSpawnedMinionTypeForValidation() {
        return lastSpawnedMinionType;
    }

    public int cultistMinionBudget() {
        return legacyCultistMinionBudget(level().getDifficulty());
    }

    public int cultistMinionBudgetAfterNearbyCultists() {
        int existing = level().getEntitiesOfClass(
                TCCultistEntity.class,
                getBoundingBox().inflate(32.0D, 32.0D, 32.0D)
        ).size();
        return cultistMinionBudget() - existing;
    }

    public TCCultistEntity spawnLegacyMinionForValidation(boolean cleric) {
        return spawnLegacyMinion(cleric);
    }

    public static int legacyCultistMinionBudget(Difficulty difficulty) {
        return switch (difficulty) {
            case HARD -> 6;
            case NORMAL -> 4;
            case PEACEFUL, EASY -> 2;
        };
    }
}

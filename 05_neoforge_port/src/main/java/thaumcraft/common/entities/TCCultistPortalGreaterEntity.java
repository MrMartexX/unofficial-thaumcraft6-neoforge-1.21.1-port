package thaumcraft.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSounds;

/** TC6 greater Crimson portal foundation: staged minion wave, Praetor spawn, bossbar and player-touch damage. */
public class TCCultistPortalGreaterEntity extends Monster {
    public static final int LEGACY_INITIAL_STAGE_COUNTER = 200;
    public static final int LEGACY_BOSS_STAGE = 12;
    public static final double LEGACY_MAX_HEALTH = 500.0D;

    private final ServerBossEvent bossInfo = new ServerBossEvent(
            Component.translatable("entity.thaumcraft.cultist_portal_greater"),
            BossEvent.BossBarColor.RED,
            BossEvent.BossBarOverlay.NOTCHED_6
    );

    private int stage;
    private int stageCounter = LEGACY_INITIAL_STAGE_COUNTER;
    public int pulse;
    private EntityType<?> lastSpawnedType;

    public TCCultistPortalGreaterEntity(EntityType<? extends TCCultistPortalGreaterEntity> type, Level level) {
        super(type, level);
        xpReward = 30;
        setPersistenceRequired();
        bossInfo.setDarkenScreen(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, LEGACY_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 5.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            tickLegacyStageMachine();
        }
        if (pulse > 0) {
            pulse--;
        }
    }

    private void tickLegacyStageMachine() {
        if (stageCounter > 0) {
            stageCounter--;
            if (stageCounter == 160 && stage == 0) {
                level().broadcastEntityEvent(this, (byte) 16);
            }
            if (stageCounter > 20 && stageCounter < 150 && stage == 0 && stageCounter % 13 == 0) {
                level().broadcastEntityEvent(this, (byte) 16);
                playSound(TCSounds.WANDFAIL.get(), 1.0F, 1.0F);
            }
        } else if (level().getNearestPlayer(this, 48.0D) != null) {
            level().broadcastEntityEvent(this, (byte) 16);
            if (stage == LEGACY_BOSS_STAGE) {
                stageCounter = 50 + getTiming() * 2 + random.nextInt(50);
                spawnBoss();
            } else {
                int timing = stage <= 4 ? 15 + random.nextInt(Math.max(1, 10 - stage)) - stage : getTiming();
                stageCounter = Math.max(1, timing + (stage <= 4 ? 0 : random.nextInt(5 + timing / 3)));
                spawnMinion();
            }
            stage++;
        } else {
            stageCounter = 30 + random.nextInt(30);
        }
        if (stage < LEGACY_BOSS_STAGE) {
            heal(1.0F);
        }
    }

    private int getTiming() {
        return level().getEntitiesOfClass(TCCultistEntity.class, getBoundingBox().inflate(32.0D)).size() * 20;
    }

    private TCCultistEntity spawnMinion() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        TCCultistEntity cultist = random.nextFloat() > 0.33F
                ? TCEntityTypes.CULTIST_KNIGHT.get().create(serverLevel)
                : TCEntityTypes.CULTIST_CLERIC.get().create(serverLevel);
        if (cultist == null) {
            return null;
        }
        cultist.moveTo(getX() + random.nextFloat() - random.nextFloat(), getY() + 0.25D, getZ() + random.nextFloat() - random.nextFloat(), getYRot(), 0.0F);
        cultist.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(cultist.blockPosition()), net.minecraft.world.entity.MobSpawnType.MOB_SUMMONED, null);
        cultist.setLegacyHome(blockPosition(), 32);
        serverLevel.addFreshEntity(cultist);
        cultist.spawnExplosionParticle();
        cultist.playSound(TCSounds.WANDFAIL.get(), 1.0F, 1.0F);
        if (stage > LEGACY_BOSS_STAGE) {
            hurt(damageSources().fellOutOfWorld(), 5.0F + random.nextInt(5));
        }
        lastSpawnedType = cultist.getType();
        return cultist;
    }

    private TCCultistLeaderEntity spawnBoss() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        TCCultistLeaderEntity cultist = TCEntityTypes.CULTIST_LEADER.get().create(serverLevel);
        if (cultist == null) {
            return null;
        }
        cultist.moveTo(getX() + random.nextFloat() - random.nextFloat(), getY() + 0.25D, getZ() + random.nextFloat() - random.nextFloat(), getYRot(), 0.0F);
        cultist.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(cultist.blockPosition()), net.minecraft.world.entity.MobSpawnType.MOB_SUMMONED, null);
        cultist.setLegacyHome(blockPosition(), 32);
        serverLevel.addFreshEntity(cultist);
        cultist.handleEntityEvent(TCCultistEntity.LEGACY_SPAWN_PARTICLE_EVENT);
        cultist.playSound(TCSounds.WANDFAIL.get(), 1.0F, 1.0F);
        lastSpawnedType = cultist.getType();
        return cultist;
    }

    @Override
    public void playerTouch(Player player) {
        if (distanceToSqr(player) < 3.0D && player.hurt(damageSources().indirectMagic(this, this), 8.0F)) {
            playSound(TCSounds.ZAP.get(), 1.0F, (random.nextFloat() - random.nextFloat()) * 0.1F + 1.0F);
        }
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
    public void handleEntityEvent(byte id) {
        if (id == 16) {
            pulse = 10;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        spawnAtLocation(new ItemStack(TCItems.PRIMORDIAL_PEARL.get()), getBbHeight() / 2.0F);
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!level().isClientSide) {
            level().explode(this, getX(), getY(), getZ(), 2.0F, Level.ExplosionInteraction.NONE);
        }
        super.die(damageSource);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossInfo.setProgress(getHealth() / getMaxHealth());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossInfo.removePlayer(player);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("stage", stage);
        tag.putInt("stageCounter", stageCounter);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        stage = tag.getInt("stage");
        stageCounter = tag.contains("stageCounter") ? tag.getInt("stageCounter") : LEGACY_INITIAL_STAGE_COUNTER;
    }

    public int stageForValidation() {
        return stage;
    }

    public void setStageForValidation(int stage) {
        this.stage = stage;
        this.stageCounter = 0;
    }

    public int stageCounterForValidation() {
        return stageCounter;
    }

    public EntityType<?> lastSpawnedTypeForValidation() {
        return lastSpawnedType;
    }

    public TCCultistEntity spawnMinionForValidation() {
        return spawnMinion();
    }

    public TCCultistLeaderEntity spawnBossForValidation() {
        return spawnBoss();
    }
}

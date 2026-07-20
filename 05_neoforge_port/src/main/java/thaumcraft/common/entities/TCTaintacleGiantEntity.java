package thaumcraft.common.entities;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;

/** TC6 Giant Taintacle foundation: bossbar, no natural spawn, enrage damage cap and pearl uniqueness drop. */
public class TCTaintacleGiantEntity extends TCTaintacleEntity implements IEldritchMob {
    public static final double LEGACY_MAX_HEALTH = 175.0D;
    public static final double LEGACY_ATTACK_DAMAGE = 9.0D;
    public static final float LEGACY_DAMAGE_CAP = 35.0F;
    public static final int LEGACY_ANGER_TICKS = 200;
    public static final int LEGACY_REGEN_INTERVAL = 30;

    private static final EntityDataAccessor<Integer> ANGER =
            SynchedEntityData.defineId(TCTaintacleGiantEntity.class, EntityDataSerializers.INT);

    private final ServerBossEvent bossInfo = new ServerBossEvent(
            Component.translatable("entity.thaumcraft.taintacle_giant"),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
    );

    public TCTaintacleGiantEntity(EntityType<? extends TCTaintacleGiantEntity> type, Level level) {
        super(type, level);
        xpReward = 20;
        bossInfo.setDarkenScreen(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, LEGACY_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, LEGACY_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANGER, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (getAnger() > 0) {
            setAnger(getAnger() - 1);
        }
        if (!level().isClientSide && tickCount % LEGACY_REGEN_INTERVAL == 0) {
            heal(1.0F);
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossInfo.setProgress(getHealth() / getMaxHealth());
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        float adjusted = amount;
        if (!level().isClientSide && amount > LEGACY_DAMAGE_CAP) {
            if (getAnger() == 0) {
                addEffect(new MobEffectInstance(MobEffects.REGENERATION, LEGACY_ANGER_TICKS, Math.max(0, (int) (amount / 15.0F))));
                addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, LEGACY_ANGER_TICKS, Math.max(0, (int) (amount / 10.0F))));
                addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, LEGACY_ANGER_TICKS, Math.max(0, (int) (amount / 40.0F))));
                setAnger(LEGACY_ANGER_TICKS);
            }
            adjusted = LEGACY_DAMAGE_CAP;
        }
        return super.hurt(source, adjusted);
    }

    @Override
    public boolean checkSpawnRules(net.minecraft.world.level.LevelAccessor level, net.minecraft.world.entity.MobSpawnType spawnReason) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, net.minecraft.world.damagesource.DamageSource damageSource, boolean recentlyHit) {
        List<TCTaintacleGiantEntity> nearby = level.getEntitiesOfClass(
                TCTaintacleGiantEntity.class,
                new AABB(getX(), getY(), getZ(), getX(), getY(), getZ()).inflate(48.0D),
                giant -> giant != this && giant.isAlive()
        );
        if (nearby.isEmpty()) {
            spawnAtLocation(new ItemStack(TCItems.PRIMORDIAL_PEARL.get()), getBbHeight() / 2.0F);
        }
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
    public boolean isAlliedTo(net.minecraft.world.entity.Entity entity) {
        return entity instanceof IEldritchMob || super.isAlliedTo(entity);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("anger", getAnger());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setAnger(tag.getInt("anger"));
    }

    public int getAnger() {
        return entityData.get(ANGER);
    }

    public void setAnger(int anger) {
        entityData.set(ANGER, Math.max(0, anger));
    }
}

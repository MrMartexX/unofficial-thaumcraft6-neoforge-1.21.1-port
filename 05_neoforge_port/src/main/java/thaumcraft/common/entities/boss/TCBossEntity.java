package thaumcraft.common.entities.boss;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.fluids.FluidType;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.common.registry.TCItems;

/**
 * Base class for Thaumcraft boss monsters (Eldritch Warden, Eldritch Golem).
 * Replicates legacy EntityThaumcraftBoss mechanics: ServerBossEvent, damage cap at 35,
 * enrage state on heavy attacks, aggro map tracking, and primordial pearl / rare loot bag drops.
 */
public abstract class TCBossEntity extends Monster implements IEldritchMob {
    public static final float LEGACY_DAMAGE_CAP = 35.0F;

    protected static final EntityDataAccessor<Integer> AGGRO =
            SynchedEntityData.defineId(TCBossEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> SPAWN_TIMER =
            SynchedEntityData.defineId(TCBossEntity.class, EntityDataSerializers.INT);

    protected final ServerBossEvent bossEvent;
    protected final Map<UUID, Integer> aggroMap = new HashMap<>();

    protected BlockPos homePos;
    protected int homeDistance = 24;

    protected TCBossEntity(EntityType<? extends TCBossEntity> type, Level level) {
        super(type, level);
        this.bossEvent = (ServerBossEvent) new ServerBossEvent(
                getDisplayName(),
                BossEvent.BossBarColor.PURPLE,
                BossEvent.BossBarOverlay.PROGRESS
        ).setDarkenScreen(true);
        this.xpReward = 50;
        setCanPickUpLoot(false);
    }

    public static AttributeSupplier.Builder createBossAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.95D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(AGGRO, 0);
        builder.define(SPAWN_TIMER, 0);
    }

    public int getAnger() {
        return entityData.get(AGGRO);
    }

    public void setAnger(int anger) {
        entityData.set(AGGRO, Math.max(0, anger));
    }

    public int getSpawnTimer() {
        return entityData.get(SPAWN_TIMER);
    }

    public void setSpawnTimer(int timer) {
        entityData.set(SPAWN_TIMER, Math.max(0, timer));
    }

    public BlockPos getHomePos() {
        return homePos != null ? homePos : blockPosition();
    }

    public void setHomePos(BlockPos pos, int distance) {
        this.homePos = pos;
        this.homeDistance = distance;
    }

    public int getHomeDistance() {
        return homeDistance;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.homePos = blockPosition();
        this.homeDistance = 24;
        generateBossName();
        this.bossEvent.setName(getDisplayName());
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    public void generateBossName() {
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(getDisplayName());
    }

    @Override
    protected void customServerAiStep() {
        if (getSpawnTimer() == 0) {
            super.customServerAiStep();
        }
        if (getTarget() != null && !getTarget().isAlive()) {
            setTarget(null);
        }
        this.bossEvent.setProgress(getHealth() / getMaxHealth());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return getSpawnTimer() > 0 || super.isInvulnerableTo(source);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) {
            return false;
        }
        if (!level().isClientSide) {
            Entity attacker = source.getEntity();
            if (attacker instanceof LivingEntity living) {
                UUID id = living.getUUID();
                int currentAggro = aggroMap.getOrDefault(id, 0);
                aggroMap.put(id, currentAggro + (int) amount);
            }
            if (amount > LEGACY_DAMAGE_CAP) {
                if (getAnger() == 0) {
                    int regenAmp = Math.max(0, (int) (amount / 15.0F));
                    int strAmp = Math.max(0, (int) (amount / 10.0F));
                    int hasteAmp = Math.max(0, (int) (amount / 40.0F));
                    addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, regenAmp));
                    addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, strAmp));
                    addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 200, hasteAmp));
                    setAnger(200);
                    if (attacker instanceof Player player) {
                        Component bossEnrage = Component.translatable("tc.boss.enrage");
                        player.displayClientMessage(
                                Component.literal(getName().getString() + " " + bossEnrage.getString()),
                                true
                        );
                    }
                }
                amount = LEGACY_DAMAGE_CAP;
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();
        if (getSpawnTimer() > 0) {
            setSpawnTimer(getSpawnTimer() - 1);
        }
        if (getAnger() > 0) {
            setAnger(getAnger() - 1);
        }
        if (level().isClientSide && random.nextInt(15) == 0 && getAnger() > 0) {
            level().addParticle(
                    ParticleTypes.ANGRY_VILLAGER,
                    getX() + (random.nextDouble() - 0.5D) * getBbWidth(),
                    getY() + getBbHeight() + random.nextDouble() * 0.5D,
                    getZ() + (random.nextDouble() - 0.5D) * getBbWidth(),
                    random.nextGaussian() * 0.02D,
                    random.nextGaussian() * 0.02D,
                    random.nextGaussian() * 0.02D
            );
        }
        if (!level().isClientSide) {
            if (tickCount % 30 == 0) {
                heal(1.0F);
            }
            if (getTarget() != null && tickCount % 20 == 0) {
                updateAggroTarget();
            }
        }
    }

    protected void updateAggroTarget() {
        if (aggroMap.isEmpty() || !(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        UUID bestTarget = null;
        int highestAggro = -1;
        for (Map.Entry<UUID, Integer> entry : aggroMap.entrySet()) {
            Entity entity = serverLevel.getEntity(entry.getKey());
            if (entity instanceof LivingEntity living && living.isAlive() && distanceToSqr(living) <= 16384.0D) {
                if (entry.getValue() > highestAggro) {
                    highestAggro = entry.getValue();
                    bestTarget = entry.getKey();
                }
            }
        }
        if (bestTarget != null) {
            Entity target = serverLevel.getEntity(bestTarget);
            if (target instanceof LivingEntity living && living != getTarget()) {
                setTarget(living);
            }
        }
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean isAlliedTo(Entity other) {
        return other instanceof IEldritchMob || super.isAlliedTo(other);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        ItemEntity pearl = new ItemEntity(level, getX(), getY() + getBbHeight() / 2.0F, getZ(), new ItemStack(TCItems.PRIMORDIAL_PEARL.get()));
        pearl.setDefaultPickUpDelay();
        level.addFreshEntity(pearl);

        ItemEntity bag = new ItemEntity(level, getX(), getY() + 1.5D, getZ(), new ItemStack(TCItems.LOOT_BAG_RARE.get()));
        bag.setDefaultPickUpDelay();
        level.addFreshEntity(bag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("SpawnTimer", getSpawnTimer());
        tag.putInt("Anger", getAnger());
        if (homePos != null) {
            tag.putInt("HomeX", homePos.getX());
            tag.putInt("HomeY", homePos.getY());
            tag.putInt("HomeZ", homePos.getZ());
            tag.putInt("HomeD", homeDistance);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setSpawnTimer(tag.getInt("SpawnTimer"));
        setAnger(tag.getInt("Anger"));
        if (tag.contains("HomeD")) {
            this.homeDistance = tag.getInt("HomeD");
            this.homePos = new BlockPos(tag.getInt("HomeX"), tag.getInt("HomeY"), tag.getInt("HomeZ"));
        }
    }
}

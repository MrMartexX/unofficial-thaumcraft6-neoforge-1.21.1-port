package thaumcraft.common.entities;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import thaumcraft.common.registry.TCEntityTypes;

public abstract class TCCultistEntity extends Monster {
    public static final double LEGACY_FOLLOW_RANGE = 32.0D;
    public static final double LEGACY_MOVEMENT_SPEED = 0.3D;
    public static final double LEGACY_ATTACK_DAMAGE = 4.0D;
    public static final int LEGACY_XP_REWARD = 10;
    public static final byte LEGACY_SPAWN_PARTICLE_EVENT = 20;

    private BlockPos legacyHomePos;
    private int legacyHomeDistance;

    protected TCCultistEntity(EntityType<? extends TCCultistEntity> type, Level level) {
        super(type, level);
        xpReward = LEGACY_XP_REWARD;
        setCanPickUpLoot(false);
        setDropChance(EquipmentSlot.HEAD, 0.05F);
        setDropChance(EquipmentSlot.CHEST, 0.05F);
        setDropChance(EquipmentSlot.LEGS, 0.05F);
        setDropChance(EquipmentSlot.FEET, 0.05F);
    }

    protected static AttributeSupplier.Builder createCultistAttributes(double maxHealth) {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, maxHealth)
                .add(Attributes.FOLLOW_RANGE, LEGACY_FOLLOW_RANGE)
                .add(Attributes.MOVEMENT_SPEED, LEGACY_MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, LEGACY_ATTACK_DAMAGE);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        populateLegacyEquipment(difficulty);
        enchantLegacyEquipment(level, random, difficulty);
        return data;
    }

    protected void populateLegacyEquipment(DifficultyInstance difficulty) {
    }

    protected void enchantLegacyEquipment(ServerLevelAccessor level, RandomSource random, DifficultyInstance difficulty) {
    }

    @Override
    public boolean canAttackType(EntityType<?> type) {
        return type != TCEntityTypes.CULTIST_KNIGHT.get()
                && type != TCEntityTypes.CULTIST_CLERIC.get()
                && type != TCEntityTypes.CULTIST_LEADER.get()
                && super.canAttackType(type);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        return entity instanceof TCCultistEntity || entity instanceof TCCultistLeaderEntity || super.isAlliedTo(entity);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, net.minecraft.world.damagesource.DamageSource damageSource, boolean recentlyHit) {
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == LEGACY_SPAWN_PARTICLE_EVENT) {
            return;
        }
        super.handleEntityEvent(id);
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
            setLegacyHome(new BlockPos(tag.getInt("HomeX"), tag.getInt("HomeY"), tag.getInt("HomeZ")), tag.getInt("HomeD"));
        }
    }

    public void setLegacyHome(BlockPos pos, int distance) {
        legacyHomePos = pos;
        legacyHomeDistance = distance;
    }

    @Nullable
    public BlockPos legacyHomePosForValidation() {
        return legacyHomePos;
    }

    public int legacyHomeDistanceForValidation() {
        return legacyHomeDistance;
    }

    public void spawnExplosionParticle() {
        if (!level().isClientSide) {
            level().broadcastEntityEvent(this, LEGACY_SPAWN_PARTICLE_EVENT);
        }
    }
}

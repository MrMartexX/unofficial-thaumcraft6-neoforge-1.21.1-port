package thaumcraft.common.entities;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import thaumcraft.api.entities.IEldritchMob;

/** TC6 Mind Spider foundation: harmless/viewer hallucination state, short fake lifespan and no loot. */
public class TCMindSpiderEntity extends Spider implements IEldritchMob {
    private static final EntityDataAccessor<Boolean> HARMLESS =
            SynchedEntityData.defineId(TCMindSpiderEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> VIEWER =
            SynchedEntityData.defineId(TCMindSpiderEntity.class, EntityDataSerializers.STRING);

    private int lifespan = Integer.MAX_VALUE;

    public TCMindSpiderEntity(EntityType<? extends TCMindSpiderEntity> type, Level level) {
        super(type, level);
        xpReward = 1;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Spider.createAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.MAX_HEALTH, 1.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HARMLESS, false);
        builder.define(VIEWER, "");
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount > lifespan) {
            discard();
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        return !isHarmless() && super.doHurtTarget(target);
    }

    @Override
    protected int getBaseExperienceReward() {
        return isHarmless() ? 0 : xpReward;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, net.minecraft.world.damagesource.DamageSource damageSource, boolean recentlyHit) {
    }

    @Override
    public float getVoicePitch() {
        return 0.7F;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        return spawnGroupData;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("harmless", isHarmless());
        tag.putString("viewer", getViewer());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setHarmless(tag.getBoolean("harmless"));
        setViewer(tag.getString("viewer"));
    }

    public String getViewer() {
        return entityData.get(VIEWER);
    }

    public void setViewer(String viewer) {
        entityData.set(VIEWER, viewer == null ? "" : viewer);
    }

    public boolean isHarmless() {
        return entityData.get(HARMLESS);
    }

    public void setHarmless(boolean harmless) {
        if (harmless) {
            lifespan = 1200;
        }
        entityData.set(HARMLESS, harmless);
    }

    public int lifespanForValidation() {
        return lifespan;
    }

    public int baseExperienceForValidation() {
        return getBaseExperienceReward();
    }

    public static boolean legacySpawnGate(ServerLevel level, TCMindSpiderEntity spider) {
        return level.getBlockState(spider.blockPosition().below()).isSolid()
                && level.noCollision(spider)
                && !level.containsAnyLiquid(spider.getBoundingBox());
    }

    public static int legacySpawnOffset(RandomSource random) {
        return net.minecraft.util.Mth.nextInt(random, 7, 24) * net.minecraft.util.Mth.nextInt(random, -1, 1);
    }
}

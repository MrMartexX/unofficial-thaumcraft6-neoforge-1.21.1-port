package thaumcraft.common.entities;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSounds;

/** TC6 Crimson Praetor foundation: title state, leader gear, ranged red orb and cultist regeneration aura. */
public class TCCultistLeaderEntity extends Monster implements RangedAttackMob {
    public static final double LEGACY_MAX_HEALTH = 150.0D;
    public static final double LEGACY_ATTACK_DAMAGE = 5.0D;
    public static final double LEGACY_MOVEMENT_SPEED = 0.32D;
    public static final int LEGACY_XP_REWARD = 40;
    public static final String[] LEGACY_TITLES = {
            "Alberic", "Anselm", "Bastian", "Beturian", "Chabier", "Chorache", "Chuse", "Dodorol",
            "Ebardo", "Ferrando", "Fertus", "Guillen", "Larpe", "Obano", "Zelipe"
    };

    private static final EntityDataAccessor<Byte> TITLE =
            SynchedEntityData.defineId(TCCultistLeaderEntity.class, EntityDataSerializers.BYTE);

    private net.minecraft.core.BlockPos legacyHomePos;
    private int legacyHomeDistance;

    public TCCultistLeaderEntity(EntityType<? extends TCCultistLeaderEntity> type, Level level) {
        super(type, level);
        xpReward = LEGACY_XP_REWARD;
        setCanPickUpLoot(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, LEGACY_MOVEMENT_SPEED)
                .add(Attributes.MAX_HEALTH, LEGACY_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, LEGACY_ATTACK_DAMAGE)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TITLE, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new TCLongRangeAttackGoal(this, 16.0D, 1.0D, 30, 40, 24.0F));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1D, false));
        goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 0.8D));
        goalSelector.addGoal(7, new RandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        populateLegacyEquipment(level.getDifficulty());
        enchantLegacyEquipment(level, difficulty);
        setTitle(random.nextInt(LEGACY_TITLES.length));
        return data;
    }

    private void populateLegacyEquipment(Difficulty difficulty) {
        setItemSlot(EquipmentSlot.HEAD, new ItemStack(TCItems.THAUMIUM_HELM.get()));
        setItemSlot(EquipmentSlot.CHEST, new ItemStack(TCItems.THAUMIUM_CHEST.get()));
        setItemSlot(EquipmentSlot.LEGS, new ItemStack(TCItems.THAUMIUM_LEGS.get()));
        setItemSlot(EquipmentSlot.FEET, new ItemStack(TCItems.THAUMIUM_BOOTS.get()));
        setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(difficulty == Difficulty.EASY
                ? TCItems.VOID_SWORD.get()
                : TCItems.THAUMIUM_SWORD.get()));
    }

    private void enchantLegacyEquipment(ServerLevelAccessor level, DifficultyInstance difficulty) {
        float localDifficulty = difficulty.getSpecialMultiplier();
        ItemStack held = getMainHandItem();
        if (!held.isEmpty() && random.nextFloat() < 0.5F * localDifficulty) {
            int enchantLevel = (int) (7.0F + localDifficulty * random.nextInt(22));
            setItemSlot(EquipmentSlot.MAINHAND, EnchantmentHelper.enchantItem(random, held, enchantLevel, level.registryAccess(), Optional.empty()));
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (level().isClientSide) {
            return;
        }
        for (TCCultistEntity cultist : level().getEntitiesOfClass(TCCultistEntity.class, getBoundingBox().inflate(8.0D))) {
            if (!cultist.hasEffect(net.minecraft.world.effect.MobEffects.REGENERATION)) {
                cultist.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.REGENERATION, 60, 1));
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        if (!hasLineOfSight(target) || level().isClientSide) {
            return;
        }
        swing(InteractionHand.MAIN_HAND);
        getLookControl().setLookAt(target, 30.0F, 30.0F);
        TCGolemOrbEntity blast = new TCGolemOrbEntity(level(), this, target, true);
        Vec3 aim = target.position()
                .add(0.0D, target.getBbHeight() / 2.0D + 2.0D, 0.0D)
                .subtract(position());
        blast.shoot(aim.x(), aim.y(), aim.z(), 0.66F, 3.0F);
        playSound(TCSounds.EGATTACK.get(), 1.0F, 1.0F + random.nextFloat() * 0.1F);
        level().addFreshEntity(blast);
    }

    @Override
    public boolean canAttackType(EntityType<?> type) {
        return type != TCEntityTypes.CULTIST_CLERIC.get()
                && type != TCEntityTypes.CULTIST_KNIGHT.get()
                && type != TCEntityTypes.CULTIST_LEADER.get()
                && super.canAttackType(type);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof TCCultistEntity) && !(target instanceof TCCultistLeaderEntity) && super.canAttack(target);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        return entity instanceof TCCultistEntity || entity instanceof TCCultistLeaderEntity || super.isAlliedTo(entity);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return TCSounds.CHANT.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 500;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, net.minecraft.world.damagesource.DamageSource damageSource, boolean recentlyHit) {
    }

    @Override
    public Component getName() {
        if (hasCustomName()) {
            return getCustomName();
        }
        return Component.translatable("entity.thaumcraft.cultist_leader");
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == TCCultistEntity.LEGACY_SPAWN_PARTICLE_EVENT) {
            return;
        }
        super.handleEntityEvent(id);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("title", (byte) getTitleIndex());
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
        setTitle(tag.getByte("title"));
        if (tag.contains("HomeD")) {
            setLegacyHome(new net.minecraft.core.BlockPos(tag.getInt("HomeX"), tag.getInt("HomeY"), tag.getInt("HomeZ")), tag.getInt("HomeD"));
        }
    }

    public void setLegacyHome(net.minecraft.core.BlockPos pos, int distance) {
        legacyHomePos = pos;
        legacyHomeDistance = distance;
        restrictTo(pos, distance);
    }

    public int getTitleIndex() {
        return Mth.clamp(entityData.get(TITLE), 0, LEGACY_TITLES.length - 1);
    }

    public void setTitle(int title) {
        entityData.set(TITLE, (byte) Mth.clamp(title, 0, LEGACY_TITLES.length - 1));
    }

    public String getLegacyTitleForValidation() {
        return LEGACY_TITLES[getTitleIndex()];
    }

    public boolean regenerateNearbyCultistsForValidation() {
        List<TCCultistEntity> nearby = level().getEntitiesOfClass(TCCultistEntity.class, getBoundingBox().inflate(8.0D));
        customServerAiStep();
        return nearby.stream().allMatch(cultist -> cultist.hasEffect(net.minecraft.world.effect.MobEffects.REGENERATION));
    }
}

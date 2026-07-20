package thaumcraft.common.entities;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSounds;

/** TC6 Shambling Husk foundation: eldritch zombie shell that releases a helmed Eldritch Crab on death. */
public class TCInhabitedZombieEntity extends Zombie implements IEldritchMob {
    public static final double LEGACY_MAX_HEALTH = 30.0D;
    public static final double LEGACY_ATTACK_DAMAGE = 5.0D;

    private boolean spawnedDeathCrab;

    public TCInhabitedZombieEntity(EntityType<? extends TCInhabitedZombieEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, LEGACY_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, LEGACY_ATTACK_DAMAGE)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TCCultistEntity.class, true));
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        populateLegacyArmor(level.getDifficulty());
        return data;
    }

    private void populateLegacyArmor(Difficulty difficulty) {
        float chance = difficulty == Difficulty.HARD ? 0.9F : 0.6F;
        setItemSlot(EquipmentSlot.HEAD, new ItemStack(TCItems.VOID_HELM.get()));
        if (random.nextFloat() <= chance) {
            setItemSlot(EquipmentSlot.CHEST, new ItemStack(TCItems.VOID_CHEST.get()));
        }
        if (random.nextFloat() <= chance) {
            setItemSlot(EquipmentSlot.LEGS, new ItemStack(TCItems.VOID_LEGS.get()));
        }
    }

    @Override
    public boolean killedEntity(ServerLevel level, LivingEntity entity) {
        return false;
    }

    @Override
    public void die(net.minecraft.world.damagesource.DamageSource damageSource) {
        if (!level().isClientSide && !spawnedDeathCrab) {
            spawnDeathCrabForValidation();
        }
        super.die(damageSource);
    }

    public TCEldritchCrabEntity spawnDeathCrabForValidation() {
        if (!(level() instanceof ServerLevel serverLevel) || spawnedDeathCrab) {
            return null;
        }
        spawnedDeathCrab = true;
        TCEldritchCrabEntity crab = TCEntityTypes.ELDRITCH_CRAB.get().create(serverLevel);
        if (crab == null) {
            return null;
        }
        crab.moveTo(getX(), getY() + getEyeHeight(), getZ(), getYRot(), getXRot());
        crab.setHelm(true);
        serverLevel.addFreshEntity(crab);
        return crab;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return TCSounds.CRAB_TALK.get();
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource damageSource) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TCSounds.CRAB_DEATH.get();
    }

    @Override
    public boolean checkSpawnRules(net.minecraft.world.level.LevelAccessor level, MobSpawnType spawnReason) {
        List<TCInhabitedZombieEntity> nearby = level().getEntitiesOfClass(
                TCInhabitedZombieEntity.class,
                new AABB(getX(), getY(), getZ(), getX() + 1.0D, getY() + 1.0D, getZ() + 1.0D)
                        .inflate(32.0D, 16.0D, 32.0D),
                zombie -> zombie != this
        );
        return nearby.isEmpty() && super.checkSpawnRules(level, spawnReason);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        return entity instanceof IEldritchMob || super.isAlliedTo(entity);
    }
}

package thaumcraft.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.blocks.world.taint.TCTaintHelper;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;

/** TC6 stationary taintacle foundation: tainted team rules, no horizontal movement, taint substrate and tiny spawn contract. */
public class TCTaintacleEntity extends Monster implements ITaintedMob {
    private float flailIntensity = 1.0F;

    public TCTaintacleEntity(EntityType<? extends TCTaintacleEntity> type, Level level) {
        super(type, level);
        xpReward = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (flailIntensity > 1.0F) {
                flailIntensity -= 0.01F;
            }
            return;
        }
        if (tickCount % 20 == 0 && !isOnLegacyTaint()) {
            hurt(damageSources().starve(), 1.0F);
        }
        LivingEntity target = getTarget();
        if (tickCount % 40 == 0
                && getType() != TCEntityTypes.TAINTACLE_TINY.get()
                && target != null
                && distanceToSqr(target) > 16.0D
                && distanceToSqr(target) < 256.0D
                && getSensing().hasLineOfSight(target)) {
            spawnTentacleNear(target);
        }
    }

    @Override
    public void move(MoverType type, Vec3 pos) {
        super.move(type, new Vec3(0.0D, Math.min(0.0D, pos.y), 0.0D));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        level().broadcastEntityEvent(this, (byte) 16);
        playSound(getHurtSound(damageSources().mobAttack(this)), getSoundVolume(), getVoicePitch());
        return super.doHurtTarget(target);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 16) {
            flailIntensity = 3.0F;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean canAttackType(EntityType<?> type) {
        return type != TCEntityTypes.TAINTACLE.get()
                && type != TCEntityTypes.TAINTACLE_TINY.get()
                && super.canAttackType(type);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof ITaintedMob) && super.canAttack(target);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        return entity instanceof ITaintedMob || super.isAlliedTo(entity);
    }

    @Override
    public boolean checkSpawnRules(net.minecraft.world.level.LevelAccessor level, net.minecraft.world.entity.MobSpawnType spawnReason) {
        return level().getDifficulty() != Difficulty.PEACEFUL && isOnLegacyTaint();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 200;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.CHORUS_FLOWER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SLIME_ATTACK;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SLIME_ATTACK;
    }

    @Override
    public float getVoicePitch() {
        return 1.3F - getBbHeight() / 10.0F;
    }

    @Override
    protected float getSoundVolume() {
        return getBbHeight() / 8.0F;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        spawnAtLocation(new ItemStack(TCItems.CRYSTAL_ESSENCE_VITIUM.get()), getBbHeight() / 2.0F);
    }

    public boolean isOnLegacyTaint() {
        BlockPos pos = blockPosition();
        return TCTaintHelper.isTaintState(level().getBlockState(pos))
                || TCTaintHelper.isTaintState(level().getBlockState(pos.below()));
    }

    public boolean spawnTentacleNearForValidation(LivingEntity target) {
        return spawnTentacleNear(target);
    }

    public float flailIntensity() {
        return flailIntensity;
    }

    protected boolean spawnTentacleNear(LivingEntity target) {
        if (level().isClientSide) {
            return false;
        }
        BlockPos targetPos = target.blockPosition();
        boolean onTaint = TCTaintHelper.isTaintState(level().getBlockState(targetPos))
                || TCTaintHelper.isTaintState(level().getBlockState(targetPos.below()));
        if (!onTaint) {
            return false;
        }
        TCTaintacleTinyEntity taintlet = TCEntityTypes.TAINTACLE_TINY.get().create(level());
        if (taintlet == null) {
            return false;
        }
        taintlet.moveTo(
                target.getX() + random.nextFloat() - random.nextFloat(),
                target.getY(),
                target.getZ() + random.nextFloat() - random.nextFloat(),
                0.0F,
                0.0F
        );
        level().addFreshEntity(taintlet);
        playSound(getHurtSound(damageSources().mobAttack(this)), getSoundVolume(), getVoicePitch());
        return true;
    }
}

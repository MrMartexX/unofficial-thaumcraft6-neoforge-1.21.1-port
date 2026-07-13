package thaumcraft.common.entities;

import java.util.List;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import thaumcraft.common.registry.TCEntityTypes;

/** TC6 EntityEldritchOrb projectile: no gravity, short lifetime, magic AoE and Weakness on impact. */
public class TCEldritchOrbEntity extends ThrowableProjectile {
    public static final int LEGACY_LIFETIME_TICKS = 100;
    public static final double LEGACY_IMPACT_RADIUS = 2.0D;
    public static final float LEGACY_DAMAGE_MULTIPLIER = 0.666F;
    public static final int LEGACY_WEAKNESS_DURATION = 160;
    public static final int LEGACY_WEAKNESS_AMPLIFIER = 0;
    public static final float LEGACY_INITIAL_VELOCITY = 0.75F;
    public static final float LEGACY_INITIAL_INACCURACY = 0.0F;
    public static final float LEGACY_INITIAL_PITCH_OFFSET = -5.0F;
    public static final float LEGACY_TARGETED_VELOCITY = 1.1F;
    public static final float LEGACY_TARGETED_INACCURACY = 2.0F;

    public TCEldritchOrbEntity(EntityType<? extends TCEldritchOrbEntity> type, Level level) {
        super(type, level);
    }

    public TCEldritchOrbEntity(Level level, LivingEntity shooter) {
        super(TCEntityTypes.ELDRITCH_ORB.get(), shooter, level);
        shootFromRotation(
                shooter,
                shooter.getXRot(),
                shooter.getYRot(),
                LEGACY_INITIAL_PITCH_OFFSET,
                LEGACY_INITIAL_VELOCITY,
                LEGACY_INITIAL_INACCURACY
        );
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount > LEGACY_LIFETIME_TICKS) {
            discard();
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0D;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide) {
            return;
        }
        Entity owner = getOwner();
        if (!(owner instanceof LivingEntity livingOwner)) {
            return;
        }

        int affected = applyLegacyImpact(livingOwner);
        if (affected >= 0) {
            playSound(SoundEvents.LAVA_EXTINGUISH, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
            discard();
        }
    }

    public int applyLegacyImpact(LivingEntity livingOwner) {
        AABB area = getBoundingBox().inflate(LEGACY_IMPACT_RADIUS, LEGACY_IMPACT_RADIUS, LEGACY_IMPACT_RADIUS);
        List<LivingEntity> targets = level().getEntitiesOfClass(LivingEntity.class, area, target -> shouldAffect(target, livingOwner));
        float damage = legacyDamageFromAttack(livingOwner.getAttributeValue(Attributes.ATTACK_DAMAGE));
        DamageSource source = damageSources().indirectMagic(this, livingOwner);
        for (LivingEntity target : targets) {
            target.hurt(source, damage);
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, LEGACY_WEAKNESS_DURATION, LEGACY_WEAKNESS_AMPLIFIER));
        }
        return targets.size();
    }

    public static boolean shouldAffect(LivingEntity target, Entity owner) {
        return target != owner && !target.isInvertedHealAndHarm();
    }

    public static float legacyDamageFromAttack(double attackDamage) {
        return (float) attackDamage * LEGACY_DAMAGE_MULTIPLIER;
    }

    public double gravityForValidation() {
        return getDefaultGravity();
    }
}

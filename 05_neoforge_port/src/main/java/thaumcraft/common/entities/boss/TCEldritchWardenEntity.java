package thaumcraft.common.entities.boss;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.common.entities.TCEldritchGuardianEntity;
import thaumcraft.common.entities.TCEldritchOrbEntity;
import thaumcraft.common.entities.TCLongRangeAttackGoal;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.warp.TCWarpManager;
import thaumcraft.common.warp.TCWarpType;

/**
 * 100% legacy parity implementation of TC6 EntityEldritchWarden:
 * Lovecraftian titles, dual boss bars (Health & Absorption Shield), shield regeneration,
 * Field Frenzy invulnerability & radial blast, teleport home, homing eldritch orbs,
 * and sonic screech dealing knockback, wither, weakness, and temporary warp.
 */
public class TCEldritchWardenEntity extends TCBossEntity implements RangedAttackMob {
    public static final String[] LEGACY_TITLES = {
            "Aphoom-Zhah", "Basatan", "Chaugnar Faugn", "Mnomquah", "Nyogtha", "Oorn",
            "Shaikorth", "Rhan-Tegoth", "Rhogog", "Shudde M'ell", "Vulthoom", "Yag-Kosha",
            "Yibb-Tstll", "Zathog", "Zushakon"
    };

    public static final float LEGACY_MAX_SHIELD = 264.0F; // 400.0 * 0.66

    private static final EntityDataAccessor<Byte> TITLE =
            SynchedEntityData.defineId(TCEldritchWardenEntity.class, EntityDataSerializers.BYTE);

    protected final ServerBossEvent shieldBossEvent;

    private boolean fieldFrenzy = false;
    private int fieldFrenzyCounter = 0;
    private boolean lastBlast = false;

    public float armLiftL = 0.0F;
    public float armLiftR = 0.0F;

    public TCEldritchWardenEntity(EntityType<? extends TCEldritchWardenEntity> type, Level level) {
        super(type, level);
        this.shieldBossEvent = (ServerBossEvent) new ServerBossEvent(
                Component.translatable("entity.thaumcraft.eldritch_warden.shield"),
                BossEvent.BossBarColor.BLUE,
                BossEvent.BossBarOverlay.NOTCHED_10
        );
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.MAX_ABSORPTION, 264.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.33D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.95D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TITLE, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new TCLongRangeAttackGoal(this, 3.0D, 1.0D, 20, 40, 24.0F));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1D, false));
        goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 0.8D));
        goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public String getTitle() {
        byte index = entityData.get(TITLE);
        if (index >= 0 && index < LEGACY_TITLES.length) {
            return LEGACY_TITLES[index];
        }
        return LEGACY_TITLES[0];
    }

    public void setTitle(int titleIndex) {
        entityData.set(TITLE, (byte) Mth.clamp(titleIndex, 0, LEGACY_TITLES.length - 1));
        generateBossName();
    }

    @Override
    public void generateBossName() {
        setCustomName(Component.literal("Eldritch Warden, " + getTitle()));
        if (this.bossEvent != null) {
            this.bossEvent.setName(getDisplayName());
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        setSpawnTimer(150);
        setTitle(random.nextInt(LEGACY_TITLES.length));
        setAbsorptionAmount(LEGACY_MAX_SHIELD);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.shieldBossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.shieldBossEvent.removePlayer(player);
    }

    @Override
    protected void customServerAiStep() {
        if (fieldFrenzyCounter == 0) {
            super.customServerAiStep();
        }
        if (hurtTime <= 0 && tickCount % 25 == 0 && getAbsorptionAmount() < LEGACY_MAX_SHIELD) {
            setAbsorptionAmount(getAbsorptionAmount() + 1.0F);
        }
        this.shieldBossEvent.setProgress(Mth.clamp(getAbsorptionAmount() / LEGACY_MAX_SHIELD, 0.0F, 1.0F));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return fieldFrenzyCounter > 0 || super.isInvulnerableTo(source);
    }

    public boolean isFieldFrenzyActive() {
        return fieldFrenzyCounter > 0;
    }

    public int getFieldFrenzyCounter() {
        return fieldFrenzyCounter;
    }

    public void triggerFieldFrenzy() {
        this.fieldFrenzy = true;
        this.fieldFrenzyCounter = 150;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source) || source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (fieldFrenzyCounter > 0) {
                return false;
            }
        }
        boolean hurt = super.hurt(source, amount);
        if (!level().isClientSide && hurt && !fieldFrenzy && getAbsorptionAmount() <= 0.0F) {
            triggerFieldFrenzy();
        }
        return hurt;
    }

    @Override
    public void tick() {
        if (getSpawnTimer() == 150 && !level().isClientSide) {
            level().broadcastEntityEvent(this, (byte) 18);
        }
        super.tick();

        if (level().isClientSide) {
            if (armLiftL > 0.0F) {
                armLiftL -= 0.05F;
            }
            if (armLiftR > 0.0F) {
                armLiftR -= 0.05F;
            }
            if (getSpawnTimer() > 0) {
                double he = Math.max(1.0D, getBbHeight() * ((150 - getSpawnTimer()) / 150.0D));
                for (int i = 0; i < 4; i++) {
                    level().addParticle(
                            ParticleTypes.PORTAL,
                            getX() + (random.nextDouble() - 0.5D) * getBbWidth() * 1.5D,
                            getY() + he / 2.0D,
                            getZ() + (random.nextDouble() - 0.5D) * getBbWidth() * 1.5D,
                            (random.nextDouble() - 0.5D) * 0.2D,
                            (random.nextDouble() - 0.5D) * 0.2D,
                            (random.nextDouble() - 0.5D) * 0.2D
                    );
                }
            }
        } else {
            if (fieldFrenzyCounter > 0) {
                if (fieldFrenzyCounter == 150) {
                    teleportHome();
                }
                performFieldFrenzy();
            }
        }
    }

    protected void performFieldFrenzy() {
        if (fieldFrenzyCounter < 121 && fieldFrenzyCounter % 10 == 0) {
            level().broadcastEntityEvent(this, (byte) 17);
            double radius = (150 - fieldFrenzyCounter) / 8.0D;
            AABB shockArea = getBoundingBox().inflate(radius, 2.0D, radius);
            List<LivingEntity> targets = level().getEntitiesOfClass(LivingEntity.class, shockArea,
                    e -> e != this && !(e instanceof IEldritchMob) && e.isAlive());
            for (LivingEntity target : targets) {
                target.hurt(damageSources().magic(), 6.0F);
                double dx = target.getX() - getX();
                double dz = target.getZ() - getZ();
                double dist = Math.max(0.1D, Math.sqrt(dx * dx + dz * dz));
                target.push((dx / dist) * 1.2D, 0.3D, (dz / dist) * 1.2D);
            }
            playSound(TCSounds.ZAP.get(), 1.0F, 0.9F + random.nextFloat() * 0.1F);
        }
        fieldFrenzyCounter--;
    }

    protected void teleportHome() {
        BlockPos home = getHomePos();
        teleportTo(home.getX() + 0.5D, home.getY() + 0.1D, home.getZ() + 0.5D);
        playSound(net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (random.nextFloat() > 0.2F) {
            // Eldritch Orb volley
            TCEldritchOrbEntity blast = new TCEldritchOrbEntity(level(), this);
            lastBlast = !lastBlast;
            level().broadcastEntityEvent(this, (byte) (lastBlast ? 16 : 15));

            int rr = lastBlast ? 90 : 180;
            double xx = Mth.cos((getYRot() + rr) * Mth.DEG_TO_RAD) * 0.5D;
            double yy = 0.13D;
            double zz = Mth.sin((getYRot() + rr) * Mth.DEG_TO_RAD) * 0.5D;
            blast.setPos(blast.getX() - xx, blast.getY() - yy, blast.getZ() - zz);

            double d0 = target.getX() + target.getDeltaMovement().x - getX();
            double d1 = target.getY() + target.getEyeHeight() / 2.0D - getY();
            double d2 = target.getZ() + target.getDeltaMovement().z - getZ();
            blast.shoot(d0, d1, d2, 1.0F, 2.0F);
            playSound(TCSounds.EGATTACK.get(), 2.0F, 1.0F + random.nextFloat() * 0.1F);
            level().addFreshEntity(blast);
        } else if (hasLineOfSight(target)) {
            // Sonic Screech
            level().broadcastEntityEvent(this, (byte) 17);
            playSound(TCSounds.EGSCREECH.get(), 4.0F, 1.0F + random.nextFloat() * 0.1F);

            target.push(
                    -Mth.sin(getYRot() * Mth.DEG_TO_RAD) * 1.5D,
                    0.1D,
                    Mth.cos(getYRot() * Mth.DEG_TO_RAD) * 1.5D
            );
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 400, 0));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400, 0));

            if (target instanceof ServerPlayer player) {
                TCWarpManager.add(player, TCWarpType.TEMPORARY, 3 + random.nextInt(3));
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 15) {
            armLiftL = 0.5F;
        } else if (id == 16) {
            armLiftR = 0.5F;
        } else if (id == 17) {
            armLiftL = 0.9F;
            armLiftR = 0.9F;
        } else if (id == 18) {
            setSpawnTimer(150);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean canAttackType(EntityType<?> type) {
        return type != TCEntityTypes.ELDRITCH_GUARDIAN.get() && super.canAttackType(type);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return TCSounds.EGIDLE.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TCSounds.EGDEATH.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 500;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("Title", entityData.get(TITLE));
        tag.putBoolean("FieldFrenzy", fieldFrenzy);
        tag.putInt("FieldFrenzyCounter", fieldFrenzyCounter);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Title")) {
            setTitle(tag.getByte("Title"));
        }
        this.fieldFrenzy = tag.getBoolean("FieldFrenzy");
        this.fieldFrenzyCounter = tag.getInt("FieldFrenzyCounter");
    }
}

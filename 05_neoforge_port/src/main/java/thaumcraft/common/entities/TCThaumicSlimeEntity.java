package thaumcraft.common.entities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;

/** TC6 Thaumic Slime foundation: tainted slime size/xp, ranged spit split and flux crystal drop. */
public class TCThaumicSlimeEntity extends Slime implements ITaintedMob {
    private int launched = 10;
    private int spitCounter = 100;

    public TCThaumicSlimeEntity(EntityType<? extends TCThaumicSlimeEntity> type, Level level) {
        super(type, level);
        if (!level.isClientSide) {
            setSize(1 << (1 + random.nextInt(3)), true);
        }
    }

    public TCThaumicSlimeEntity(Level level, LivingEntity owner, LivingEntity target) {
        this(TCEntityTypes.THAUM_SLIME.get(), level);
        setSize(1, true);
        double y = (owner.getBoundingBox().minY + owner.getBoundingBox().maxY) / 2.0D;
        double dx = target.getX() - owner.getX();
        double dy = target.getBoundingBox().minY + target.getBbHeight() / 3.0F - y;
        double dz = target.getZ() - owner.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        if (horizontal >= 1.0E-7D) {
            double nx = dx / horizontal;
            double nz = dz / horizontal;
            moveTo(owner.getX() + nx, y, owner.getZ() + nz,
                    (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F,
                    (float) (-(Mth.atan2(dy, horizontal) * Mth.RAD_TO_DEG)));
            shoot(dx, dy + horizontal * 0.2D, dz, 1.5F, 1.0F);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (launched > 0) {
                launched--;
            }
            return;
        }
        Player target = level().getNearestPlayer(this, 16.0D);
        if (target == null || !isAlive()) {
            return;
        }
        if (spitCounter > 0) {
            spitCounter--;
        }
        lookAt(target, 10.0F, 20.0F);
        if (distanceTo(target) > 4.0F && spitCounter <= 0 && getSize() > 2) {
            spitCounter = 101;
            TCThaumicSlimeEntity flySlime = new TCThaumicSlimeEntity(level(), this, target);
            level().addFreshEntity(flySlime);
            playSound(SoundEvents.SLIME_ATTACK, 1.0F, ((random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
            setSize(getSize() - 1, true);
        }
    }

    @Override
    public void setSize(int size, boolean resetHealth) {
        super.setSize(size, resetHealth);
        xpReward = size + 2;
    }

    @Override
    protected float getAttackDamage() {
        return getSize() + 1 + (launched > 0 ? 2 : 0);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, net.minecraft.world.damagesource.DamageSource damageSource, boolean recentlyHit) {
        if (getSize() > 1) {
            spawnAtLocation(new ItemStack(TCItems.CRYSTAL_ESSENCE_VITIUM.get()), getBbHeight() / 2.0F);
        }
    }

    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        double length = Math.sqrt(x * x + y * y + z * z);
        x /= length;
        y /= length;
        z /= length;
        x += random.nextGaussian() * 0.007499999832361937D * inaccuracy;
        y += random.nextGaussian() * 0.007499999832361937D * inaccuracy;
        z += random.nextGaussian() * 0.007499999832361937D * inaccuracy;
        x *= velocity;
        y *= velocity;
        z *= velocity;
        setDeltaMovement(x, y, z);
        double horizontal = Math.sqrt(x * x + z * z);
        setYRot((float) (Mth.atan2(x, z) * Mth.RAD_TO_DEG));
        yRotO = getYRot();
        setXRot((float) (Mth.atan2(y, horizontal) * Mth.RAD_TO_DEG));
        xRotO = getXRot();
    }

    public int spitCounterForValidation() {
        return spitCounter;
    }

    public int launchedTicksForValidation() {
        return launched;
    }

    public int xpRewardForValidation() {
        return xpReward;
    }
}

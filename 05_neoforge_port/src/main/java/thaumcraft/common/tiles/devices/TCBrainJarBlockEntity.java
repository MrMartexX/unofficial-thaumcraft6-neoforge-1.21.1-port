package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.registry.TCBlockEntities;

/** Server-side TC6 Brain-in-a-Jar XP storage and orb attraction contract. */
public final class TCBrainJarBlockEntity extends BlockEntity {
    public static final int XP_MAX = 2000;
    public static final int RELEASE_EAT_DELAY_TICKS = 40;
    public static final double PULL_RADIUS = 8.0D;
    private static final String XP_TAG = "XP";

    private int xp;
    private int eatDelay;

    public TCBrainJarBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.JAR_BRAIN.get(), pos, state);
    }

    public int xp() {
        return xp;
    }

    public int eatDelay() {
        return eatDelay;
    }

    public void setXpForValidation(int value) {
        xp = Mth.clamp(value, 0, XP_MAX);
        markChangedAndSync();
    }

    public void setEatDelayForValidation(int value) {
        eatDelay = Math.max(0, value);
        markChangedAndSync();
    }

    public int comparatorSignal() {
        return xp <= 0 ? 0 : Math.min(15, 1 + Mth.floor((xp / (float) XP_MAX) * 14.0F));
    }

    public void releaseExperience() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        eatDelay = RELEASE_EAT_DELAY_TICKS;
        int released = serverLevel.random.nextInt(Math.min(xp + 1, 64));
        releaseExperienceForValidation(released);
    }

    public void releaseExperienceForValidation(int amount) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        eatDelay = RELEASE_EAT_DELAY_TICKS;
        int released = Math.max(0, Math.min(amount, xp));
        if (released > 0) {
            xp -= released;
            ExperienceOrb.award(serverLevel, Vec3.atCenterOf(worldPosition), released);
        }
        markChangedAndSync();
    }

    public void tickServerForValidation(int ticks) {
        if (level == null || ticks <= 0) {
            return;
        }
        for (int index = 0; index < ticks; index++) {
            serverTick(level, worldPosition, getBlockState(), this);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCBrainJarBlockEntity jar) {
        if (level == null || level.isClientSide) {
            return;
        }

        boolean changed = jar.clampXp();
        if (jar.xp < XP_MAX && jar.eatDelay == 0) {
            ExperienceOrb orb = jar.getClosestXPOrb();
            if (orb != null) {
                pullOrbTowardsJar(pos, orb);
            }
        }

        if (jar.eatDelay > 0) {
            jar.eatDelay--;
            changed = true;
        } else if (jar.xp < XP_MAX) {
            changed |= jar.absorbCloseOrbs();
        }

        if (changed) {
            jar.markChangedAndSync();
        }
    }

    @Nullable
    private ExperienceOrb getClosestXPOrb() {
        if (level == null) {
            return null;
        }
        AABB box = new AABB(worldPosition).inflate(PULL_RADIUS, PULL_RADIUS, PULL_RADIUS);
        double closestDistance = Double.MAX_VALUE;
        ExperienceOrb closest = null;
        for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class, box, ExperienceOrb::isAlive)) {
            double distance = orb.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = orb;
            }
        }
        return closest;
    }

    private boolean absorbCloseOrbs() {
        if (level == null) {
            return false;
        }
        AABB box = new AABB(
                worldPosition.getX() - 0.1D,
                worldPosition.getY() - 0.1D,
                worldPosition.getZ() - 0.1D,
                worldPosition.getX() + 1.1D,
                worldPosition.getY() + 1.1D,
                worldPosition.getZ() + 1.1D
        );
        boolean absorbed = false;
        for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class, box, ExperienceOrb::isAlive)) {
            xp += orb.value;
            level.playSound(
                    null,
                    orb.blockPosition(),
                    SoundEvents.GENERIC_EAT,
                    SoundSource.BLOCKS,
                    0.1F,
                    (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F
            );
            orb.discard();
            absorbed = true;
        }
        return absorbed;
    }

    private boolean clampXp() {
        int clamped = Mth.clamp(xp, 0, XP_MAX);
        if (clamped == xp) {
            return false;
        }
        xp = clamped;
        return true;
    }

    public static Vec3 legacyPullDelta(BlockPos pos, Vec3 orbPosition) {
        double x = (pos.getX() + 0.5D - orbPosition.x()) / 25.0D;
        double y = (pos.getY() + 0.5D - orbPosition.y()) / 25.0D;
        double z = (pos.getZ() + 0.5D - orbPosition.z()) / 25.0D;
        double distance = Math.sqrt(x * x + y * y + z * z);
        if (distance <= 0.0D) {
            return Vec3.ZERO;
        }
        double strength = 1.0D - distance;
        if (strength <= 0.0D) {
            return Vec3.ZERO;
        }
        strength *= strength;
        return new Vec3(x / distance * strength * 0.3D, y / distance * strength * 0.5D, z / distance * strength * 0.3D);
    }

    private static void pullOrbTowardsJar(BlockPos pos, ExperienceOrb orb) {
        Vec3 delta = legacyPullDelta(pos, orb.position());
        if (delta == Vec3.ZERO) {
            return;
        }
        orb.setDeltaMovement(orb.getDeltaMovement().add(delta));
        orb.hasImpulse = true;
    }

    private void markChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(XP_TAG, xp);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        xp = Mth.clamp(tag.getInt(XP_TAG), 0, XP_MAX);
        eatDelay = 0;
    }
}

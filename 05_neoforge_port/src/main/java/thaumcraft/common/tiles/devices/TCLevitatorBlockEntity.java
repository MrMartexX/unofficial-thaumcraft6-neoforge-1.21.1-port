package thaumcraft.common.tiles.devices;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.devices.TCLevitatorBlock;
import thaumcraft.common.registry.TCBlockEntities;

public class TCLevitatorBlockEntity extends BlockEntity {
    private static final int[] RANGES = {4, 8, 16, 32};
    private int rangeIdx = 1; // Default legacy range is 8 (index 1)
    private int vis = 0;

    public TCLevitatorBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.LEVITATOR.get(), pos, state);
    }

    public int getRange() {
        return RANGES[this.rangeIdx % RANGES.length];
    }

    public int getCost() {
        return getRange() * 2;
    }

    public int getVis() {
        return this.vis;
    }

    public void setVisForTesting(int vis) {
        this.vis = vis;
    }

    public void increaseRange(Player player) {
        this.rangeIdx = (this.rangeIdx + 1) % RANGES.length;
        setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            player.displayClientMessage(Component.translatable("tc.levitator", getRange(), getCost()), true);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TCLevitatorBlockEntity entity) {
        if (!state.getValue(TCLevitatorBlock.ENABLED)) {
            return;
        }

        // Refill internal vis buffer from aura (legacy: when vis < 10, drains 1 vis -> adds 1200 units)
        if (!level.isClientSide && entity.vis < 10) {
            float drained = AuraHelper.drainVis(level, pos, 1.0F, false);
            if (drained > 0.0F) {
                entity.vis += (int) (drained * 1200.0F);
                entity.setChanged();
            }
        }

        Direction dir = state.getValue(TCLevitatorBlock.FACING);
        int maxConfiguredRange = entity.getRange();

        // Truncate range if obstructed by solid/opaque blocks
        int actualRange = 0;
        for (int i = 1; i <= maxConfiguredRange; i++) {
            BlockPos checkPos = pos.relative(dir, i);
            BlockState checkState = level.getBlockState(checkPos);
            if (checkState.isSolidRender(level, checkPos)) {
                break;
            }
            actualRange = i;
        }

        if (actualRange <= 0) {
            return;
        }

        // Entity lifting requires available vis buffer
        if (entity.vis > 0) {
            AABB box = switch (dir) {
                case UP -> new AABB(pos.getX(), pos.getY() + 1, pos.getZ(),
                        pos.getX() + 1, pos.getY() + 1 + actualRange, pos.getZ() + 1);
                case DOWN -> new AABB(pos.getX(), pos.getY() - actualRange, pos.getZ(),
                        pos.getX() + 1, pos.getY(), pos.getZ() + 1);
                case NORTH -> new AABB(pos.getX(), pos.getY(), pos.getZ() - actualRange,
                        pos.getX() + 1, pos.getY() + 1, pos.getZ());
                case SOUTH -> new AABB(pos.getX(), pos.getY(), pos.getZ() + 1,
                        pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1 + actualRange);
                case WEST -> new AABB(pos.getX() - actualRange, pos.getY(), pos.getZ(),
                        pos.getX(), pos.getY() + 1, pos.getZ() + 1);
                case EAST -> new AABB(pos.getX() + 1, pos.getY(), pos.getZ(),
                        pos.getX() + 1 + actualRange, pos.getY() + 1, pos.getZ() + 1);
            };

            List<Entity> list = level.getEntities((Entity) null, box,
                    e -> !e.isSpectator() && (e instanceof ItemEntity || e.isPushable() || e instanceof LivingEntity));

            int cost = entity.getCost();
            boolean liftedAny = false;

            for (Entity e : list) {
                Vec3 motion = e.getDeltaMovement();
                if (e.isShiftKeyDown() && dir == Direction.UP) {
                    if (motion.y < 0.0D) {
                        e.setDeltaMovement(motion.x, motion.y * 0.9D, motion.z);
                    }
                } else {
                    double mx = motion.x + 0.1D * dir.getStepX();
                    double my = motion.y + 0.1D * dir.getStepY();
                    double mz = motion.z + 0.1D * dir.getStepZ();
                    if (dir.getAxis() != Direction.Axis.Y && !e.onGround()) {
                        if (motion.y < 0.0D) {
                            my *= 0.9D;
                        }
                        my += 0.08D;
                    }
                    mx = Math.max(-0.35D, Math.min(0.35D, mx));
                    my = Math.max(-0.35D, Math.min(0.35D, my));
                    mz = Math.max(-0.35D, Math.min(0.35D, mz));
                    e.setDeltaMovement(mx, my, mz);
                }

                e.resetFallDistance();
                liftedAny = true;

                if (!level.isClientSide) {
                    entity.vis -= cost;
                    if (entity.vis <= 0) {
                        break;
                    }
                }
            }

            if (liftedAny && !level.isClientSide) {
                entity.setChanged();
            }
        }

        if (level.isClientSide && level.random.nextFloat() < 0.2F) {
            double x = pos.getX() + 0.25D + level.random.nextDouble() * 0.5D;
            double y = pos.getY() + 0.25D + level.random.nextDouble() * 0.5D;
            double z = pos.getZ() + 0.25D + level.random.nextDouble() * 0.5D;
            level.addParticle(ParticleTypes.CLOUD, x, y, z,
                    dir.getStepX() * 0.1D, dir.getStepY() * 0.1D, dir.getStepZ() * 0.1D);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.rangeIdx = tag.getByte("range");
        this.vis = tag.getInt("vis");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putByte("range", (byte) this.rangeIdx);
        tag.putInt("vis", this.vis);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putByte("range", (byte) this.rangeIdx);
        tag.putInt("vis", this.vis);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}

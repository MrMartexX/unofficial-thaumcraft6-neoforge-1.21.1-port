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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.blocks.devices.TCLevitatorBlock;
import thaumcraft.common.registry.TCBlockEntities;

public class TCLevitatorBlockEntity extends BlockEntity {
    private static final int[] RANGES = {4, 8, 16, 24, 32};
    private int rangeIdx = 0;

    public TCLevitatorBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.LEVITATOR.get(), pos, state);
    }

    public int getRange() {
        return RANGES[this.rangeIdx % RANGES.length];
    }

    public void increaseRange(Player player) {
        this.rangeIdx = (this.rangeIdx + 1) % RANGES.length;
        setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            player.displayClientMessage(Component.translatable("tc.levitator", getRange(), getRange() * 2), true);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TCLevitatorBlockEntity entity) {
        if (!state.getValue(TCLevitatorBlock.ENABLED)) {
            return;
        }

        Direction dir = state.getValue(TCLevitatorBlock.FACING);
        int maxRange = entity.getRange();

        AABB box = switch (dir) {
            case UP -> new AABB(pos.getX(), pos.getY() + 1, pos.getZ(),
                    pos.getX() + 1, pos.getY() + 1 + maxRange, pos.getZ() + 1);
            case DOWN -> new AABB(pos.getX(), pos.getY() - maxRange, pos.getZ(),
                    pos.getX() + 1, pos.getY(), pos.getZ() + 1);
            case NORTH -> new AABB(pos.getX(), pos.getY(), pos.getZ() - maxRange,
                    pos.getX() + 1, pos.getY() + 1, pos.getZ());
            case SOUTH -> new AABB(pos.getX(), pos.getY(), pos.getZ() + 1,
                    pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1 + maxRange);
            case WEST -> new AABB(pos.getX() - maxRange, pos.getY(), pos.getZ(),
                    pos.getX(), pos.getY() + 1, pos.getZ() + 1);
            case EAST -> new AABB(pos.getX() + 1, pos.getY(), pos.getZ(),
                    pos.getX() + 1 + maxRange, pos.getY() + 1, pos.getZ() + 1);
        };

        List<Entity> list = level.getEntities((Entity) null, box, e -> !e.isSpectator());
        for (Entity e : list) {
            Vec3 motion = e.getDeltaMovement();
            switch (dir) {
                case UP -> {
                    if (e.isShiftKeyDown()) {
                        e.setDeltaMovement(motion.x * 0.9D, Math.max(-0.1D, motion.y * 0.9D), motion.z * 0.9D);
                    } else {
                        double newY = Math.min(0.35D, motion.y * 0.9D + 0.08D);
                        e.setDeltaMovement(motion.x * 0.9D, newY, motion.z * 0.9D);
                    }
                }
                case DOWN -> {
                    double newY = Math.max(-0.35D, motion.y * 0.9D - 0.08D);
                    e.setDeltaMovement(motion.x * 0.9D, newY, motion.z * 0.9D);
                }
                case NORTH -> {
                    double newZ = Math.max(-0.35D, motion.z * 0.9D - 0.08D);
                    e.setDeltaMovement(motion.x * 0.9D, motion.y * 0.9D, newZ);
                }
                case SOUTH -> {
                    double newZ = Math.min(0.35D, motion.z * 0.9D + 0.08D);
                    e.setDeltaMovement(motion.x * 0.9D, motion.y * 0.9D, newZ);
                }
                case WEST -> {
                    double newX = Math.max(-0.35D, motion.x * 0.9D - 0.08D);
                    e.setDeltaMovement(newX, motion.y * 0.9D, motion.z * 0.9D);
                }
                case EAST -> {
                    double newX = Math.min(0.35D, motion.x * 0.9D + 0.08D);
                    e.setDeltaMovement(newX, motion.y * 0.9D, motion.z * 0.9D);
                }
            }
            e.resetFallDistance();
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
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putByte("range", (byte) this.rangeIdx);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putByte("range", (byte) this.rangeIdx);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}

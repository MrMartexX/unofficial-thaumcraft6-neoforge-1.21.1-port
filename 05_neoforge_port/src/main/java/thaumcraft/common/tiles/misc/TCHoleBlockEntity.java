package thaumcraft.common.tiles.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;

public class TCHoleBlockEntity extends BlockEntity {
    private BlockState oldBlockState = Blocks.AIR.defaultBlockState();
    private short countdown = 0;
    private short countdownMax = 120;
    private byte count = 0;
    private Direction direction = null;

    public TCHoleBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.HOLE.get(), pos, state);
    }

    public void init(BlockState oldState, short maxCountdown, byte tunnelCount, Direction dir) {
        this.oldBlockState = oldState != null ? oldState : Blocks.AIR.defaultBlockState();
        this.countdownMax = maxCountdown;
        this.count = tunnelCount;
        this.direction = dir;
        setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TCHoleBlockEntity entity) {
        if (level.isClientSide()) {
            if (level.random.nextFloat() < 0.3F) {
                level.addParticle(
                        ParticleTypes.PORTAL,
                        pos.getX() + level.random.nextDouble(),
                        pos.getY() + level.random.nextDouble(),
                        pos.getZ() + level.random.nextDouble(),
                        (level.random.nextDouble() - 0.5D) * 0.2D,
                        (level.random.nextDouble() - 0.5D) * 0.2D,
                        (level.random.nextDouble() - 0.5D) * 0.2D
                );
            }
            return;
        }

        if (entity.countdown == 0 && entity.count > 1 && entity.direction != null) {
            BlockPos nextPos = pos.relative(entity.direction.getOpposite());
            createHole(level, nextPos, entity.direction, (byte)(entity.count - 1), entity.countdownMax);
        }

        entity.countdown++;
        if (entity.countdown % 20 == 0) {
            entity.setChanged();
        }
        if (entity.countdown >= entity.countdownMax) {
            BlockState restoreState = entity.oldBlockState != null ? entity.oldBlockState : Blocks.AIR.defaultBlockState();
            level.setBlock(pos, restoreState, 3);
        }
    }

    public static boolean createHole(Level level, BlockPos pos, Direction side, byte count, int maxCountdown) {
        if (level.isClientSide()) {
            return false;
        }
        BlockState current = level.getBlockState(pos);
        if (current.is(TCBlocks.HOLE.get()) || current.is(Blocks.BEDROCK) || current.isAir()) {
            return false;
        }
        if (current.getDestroySpeed(level, pos) == -1.0F) {
            return false;
        }
        if (level.getBlockEntity(pos) != null) {
            return false; // don't replace tile entities
        }

        if (level.setBlock(pos, TCBlocks.HOLE.get().defaultBlockState(), 3)) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TCHoleBlockEntity hole) {
                hole.init(current, (short)maxCountdown, count, side);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (oldBlockState != null) {
            tag.put("old_state", NbtUtils.writeBlockState(oldBlockState));
        }
        tag.putShort("countdown", countdown);
        tag.putShort("countdown_max", countdownMax);
        tag.putByte("count", count);
        if (direction != null) {
            tag.putString("direction", direction.getName());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("old_state")) {
            oldBlockState = NbtUtils.readBlockState(registries.lookupOrThrow(net.minecraft.core.registries.Registries.BLOCK), tag.getCompound("old_state"));
        }
        countdown = tag.getShort("countdown");
        countdownMax = tag.getShort("countdown_max");
        count = tag.getByte("count");
        if (tag.contains("direction")) {
            direction = Direction.byName(tag.getString("direction"));
        }
    }
}

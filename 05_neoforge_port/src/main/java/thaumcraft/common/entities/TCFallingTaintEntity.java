package thaumcraft.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.blocks.world.taint.TCTaintTerrainBlock;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCSounds;

/** TC6 EntityFallingTaint port: tainted crust falls as its own world-mutation entity. */
public class TCFallingTaintEntity extends Entity {
    private static final EntityDataAccessor<Integer> FALL_STATE_ID =
            SynchedEntityData.defineId(TCFallingTaintEntity.class, EntityDataSerializers.INT);

    private BlockPos oldPos = BlockPos.ZERO;
    private int fallTime;
    private int fallHurtMax = 40;
    private float fallHurtAmount = 2.0F;

    public TCFallingTaintEntity(EntityType<? extends TCFallingTaintEntity> type, Level level) {
        super(type, level);
        blocksBuilding = true;
    }

    public TCFallingTaintEntity(Level level, double x, double y, double z, BlockState fallState, BlockPos oldPos) {
        this(TCEntityTypes.FALLING_TAINT.get(), level);
        setFallState(fallState);
        this.oldPos = oldPos.immutable();
        setPos(x, y, z);
        setDeltaMovement(Vec3.ZERO);
        xo = x;
        yo = y;
        zo = z;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(FALL_STATE_ID, Block.getId(TCBlocks.TAINT_CRUST.get().defaultBlockState()));
    }

    @Override
    public void tick() {
        BlockState fallState = getFallState();
        if (fallState.isAir()) {
            discard();
            return;
        }

        xo = getX();
        yo = getY();
        zo = getZ();
        fallTime++;
        setDeltaMovement(getDeltaMovement().add(0.0D, -0.03999999910593033D, 0.0D));
        move(MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().multiply(0.9800000190734863D, 0.9800000190734863D, 0.9800000190734863D));

        BlockPos pos = blockPosition();
        if (level().isClientSide) {
            return;
        }
        if (fallTime == 1) {
            if (level().getBlockState(oldPos) != fallState) {
                discard();
                return;
            }
            level().removeBlock(oldPos, false);
        }
        if (onGround() || level().getBlockState(pos.below()).is(TCBlocks.FLUX_GOO.get())) {
            setDeltaMovement(getDeltaMovement().multiply(0.699999988079071D, -0.5D, 0.699999988079071D));
            Block block = level().getBlockState(pos).getBlock();
            if (block != Blocks.PISTON && block != Blocks.MOVING_PISTON && block != Blocks.PISTON_HEAD) {
                level().playSound(null, blockPosition(), TCSounds.GORE.get(), SoundSource.BLOCKS,
                        0.5F, ((random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
                discard();
                if (canPlace(pos) && !TCTaintTerrainBlock.canFallBelow(level(), pos.below())) {
                    level().setBlock(pos, fallState, Block.UPDATE_ALL);
                }
            }
        } else if ((fallTime > 100 && (pos.getY() < level().getMinBuildHeight() || pos.getY() > level().getMaxBuildHeight()))
                || fallTime > 600) {
            discard();
        }
    }

    @Override
    public boolean isPickable() {
        return isAlive();
    }

    @Override
    public boolean canBeCollidedWith() {
        return isAlive();
    }

    public boolean canRenderOnFire() {
        return false;
    }

    public BlockState getFallState() {
        return Block.stateById(entityData.get(FALL_STATE_ID));
    }

    public void setFallState(BlockState state) {
        entityData.set(FALL_STATE_ID, Block.getId(state));
    }

    public int fallTime() {
        return fallTime;
    }

    public BlockPos oldPos() {
        return oldPos;
    }

    public int fallHurtMax() {
        return fallHurtMax;
    }

    public float fallHurtAmount() {
        return fallHurtAmount;
    }

    public void tickForValidation() {
        tick();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        BlockState fallState = getFallState();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(fallState.getBlock());
        tag.putString("Block", id == null ? "" : id.toString());
        tag.putInt("State", Block.getId(fallState));
        tag.putByte("Time", (byte) fallTime);
        tag.putFloat("FallHurtAmount", fallHurtAmount);
        tag.putInt("FallHurtMax", fallHurtMax);
        tag.putLong("Old", oldPos.asLong());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("State")) {
            setFallState(Block.stateById(tag.getInt("State")));
        } else if (tag.contains("Block")) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("Block"));
            Block block = id == null ? Blocks.AIR : BuiltInRegistries.BLOCK.get(id);
            setFallState(block.defaultBlockState());
        } else {
            setFallState(TCBlocks.TAINT_CRUST.get().defaultBlockState());
        }
        fallTime = tag.getByte("Time") & 0xFF;
        oldPos = tag.contains("Old") ? BlockPos.of(tag.getLong("Old")) : BlockPos.ZERO;
        if (tag.contains("FallHurtAmount")) {
            fallHurtAmount = tag.getFloat("FallHurtAmount");
        }
        if (tag.contains("FallHurtMax")) {
            fallHurtMax = tag.getInt("FallHurtMax");
        }
    }

    private boolean canPlace(BlockPos pos) {
        BlockState current = level().getBlockState(pos);
        return current.is(TCBlocks.TAINT_FIBRE.get())
                || current.is(TCBlocks.FLUX_GOO.get())
                || current.isAir()
                || current.canBeReplaced()
                || current.getRenderShape() == RenderShape.INVISIBLE;
    }
}

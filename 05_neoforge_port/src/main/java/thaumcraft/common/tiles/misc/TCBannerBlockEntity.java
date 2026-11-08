package thaumcraft.common.tiles.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.registry.TCBlockEntities;

/** Port of legacy TileBanner: rotation (0..15, wall: 0/4/8/12), optional aspect symbol, wall flag. */
public class TCBannerBlockEntity extends BlockEntity {
    private byte facing;
    private Aspect aspect;
    private boolean onWall;

    public TCBannerBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.BANNER.get(), pos, state);
    }

    public byte getBannerFacing() {
        return facing;
    }

    public void setBannerFacing(byte facing) {
        this.facing = facing;
        syncChanged();
    }

    public boolean getWall() {
        return onWall;
    }

    public void setWall(boolean wall) {
        this.onWall = wall;
        syncChanged();
    }

    @Nullable
    public Aspect getAspect() {
        return aspect;
    }

    public void setAspect(@Nullable Aspect aspect) {
        this.aspect = aspect;
        syncChanged();
    }

    private void syncChanged() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public AABB getRenderBox() {
        return new AABB(worldPosition.getX(), worldPosition.getY() - 1, worldPosition.getZ(),
                worldPosition.getX() + 1, worldPosition.getY() + 2, worldPosition.getZ() + 1);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putByte("facing", facing);
        tag.putString("aspect", aspect == null ? "" : aspect.getTag());
        tag.putBoolean("wall", onWall);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        facing = tag.getByte("facing");
        String tagName = tag.getString("aspect");
        aspect = tagName.isEmpty() ? null : Aspect.getAspect(tagName);
        onWall = tag.getBoolean("wall");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}

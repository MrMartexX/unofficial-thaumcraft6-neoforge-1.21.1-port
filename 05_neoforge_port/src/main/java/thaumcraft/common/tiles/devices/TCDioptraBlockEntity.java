package thaumcraft.common.tiles.devices;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.devices.TCDioptraBlock;
import thaumcraft.common.registry.TCBlockEntities;

public class TCDioptraBlockEntity extends BlockEntity {
    private int counter = 0;
    private byte[] gridAmt = new byte[169];

    public TCDioptraBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.DIOPTRA.get(), pos, state);
        Arrays.fill(this.gridAmt, (byte) 0);
    }

    public byte[] getGridAmt() {
        return this.gridAmt;
    }

    public byte getCenterValue() {
        return this.gridAmt.length > 84 ? this.gridAmt[84] : 0;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TCDioptraBlockEntity entity) {
        entity.counter++;
        if (!level.isClientSide) {
            if (entity.counter % 20 == 0) {
                Arrays.fill(entity.gridAmt, (byte) 0);
                boolean isVisMode = state.getValue(TCDioptraBlock.ENABLED);
                for (int xx = 0; xx < 13; xx++) {
                    for (int zz = 0; zz < 13; zz++) {
                        int chunkX = (pos.getX() >> 4) + xx - 6;
                        int chunkZ = (pos.getZ() >> 4) + zz - 6;
                        BlockPos chunkPos = new BlockPos(chunkX << 4, pos.getY(), chunkZ << 4);
                        if (isVisMode) {
                            float vis = AuraHelper.getVis(level, chunkPos);
                            entity.gridAmt[xx + zz * 13] = (byte) Math.min(64.0F, vis / 500.0F * 64.0F);
                        } else {
                            float flux = AuraHelper.getFlux(level, chunkPos);
                            entity.gridAmt[xx + zz * 13] = (byte) Math.min(64.0F, flux / 500.0F * 64.0F);
                        }
                    }
                }
                entity.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("grid_a")) {
            this.gridAmt = tag.getByteArray("grid_a");
            if (this.gridAmt.length != 169) {
                this.gridAmt = Arrays.copyOf(this.gridAmt, 169);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putByteArray("grid_a", this.gridAmt);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putByteArray("grid_a", this.gridAmt);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}

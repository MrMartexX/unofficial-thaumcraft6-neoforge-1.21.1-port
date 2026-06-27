package thaumcraft.common.tiles.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.blocks.essentia.TCBellowsBlock;
import thaumcraft.common.essentia.transport.block.TCLegacyTubeVariant;
import thaumcraft.common.essentia.transport.blockentity.TCLegacyTubeBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;

/**
 * Server-owned Bellows device boundary.
 *
 * <p>Legacy Bellows behavior is split across the attached machine/tube and this device state:
 * smelters still count physically attached enabled bellows when calculating smelt time, buffer tubes
 * derive suction pressure from enabled bellows, and this BlockEntity owns persistent animation and
 * target classification state for client rendering and audit visibility.</p>
 */
public class TCBellowsBlockEntity extends BlockEntity {
    private int pumpTicks;
    private int activeTicks;
    private String lastTargetKind = "";

    public TCBellowsBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.BELLOWS.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCBellowsBlockEntity bellows) {
        if (level == null || level.isClientSide) {
            return;
        }
        bellows.tickServer(state);
    }

    public int pumpTicks() {
        return pumpTicks;
    }

    public int activeTicks() {
        return activeTicks;
    }

    public String lastTargetKind() {
        return lastTargetKind;
    }

    public boolean isActive() {
        return activeTicks > 0;
    }

    private void tickServer(BlockState state) {
        boolean enabled = state.hasProperty(TCBellowsBlock.ENABLED) && state.getValue(TCBellowsBlock.ENABLED);
        String targetKind = enabled ? resolveTargetKind(state) : "";
        boolean dirty = false;

        if (!lastTargetKind.equals(targetKind)) {
            lastTargetKind = targetKind;
            dirty = true;
        }

        if (enabled && !targetKind.isBlank()) {
            pumpTicks++;
            activeTicks = 6;
            dirty = true;
        } else if (activeTicks > 0) {
            activeTicks--;
            dirty = true;
        }

        if (dirty) {
            markChangedAndSync();
        }
    }

    private String resolveTargetKind(BlockState state) {
        if (level == null || !state.hasProperty(TCBellowsBlock.FACING)) {
            return "";
        }

        Direction facing = state.getValue(TCBellowsBlock.FACING);
        BlockPos targetPos = worldPosition.relative(facing);
        BlockState targetState = level.getBlockState(targetPos);
        BlockEntity target = level.getBlockEntity(targetPos);

        if (target instanceof TCSmelterBlockEntity) {
            return "smelter";
        }
        if (target instanceof TCLegacyTubeBlockEntity tube && tube.variant() == TCLegacyTubeVariant.BUFFER) {
            return "tube_buffer";
        }
        if (targetState.is(Blocks.FURNACE) || targetState.is(Blocks.BLAST_FURNACE) || targetState.is(Blocks.SMOKER)) {
            return "vanilla_furnace";
        }
        return "";
    }

    private void markChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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
        tag.putInt("PumpTicks", pumpTicks);
        tag.putInt("ActiveTicks", activeTicks);
        tag.putString("LastTargetKind", lastTargetKind);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        pumpTicks = Math.max(0, tag.getInt("PumpTicks"));
        activeTicks = Math.max(0, tag.getInt("ActiveTicks"));
        lastTargetKind = tag.getString("LastTargetKind");
    }
}

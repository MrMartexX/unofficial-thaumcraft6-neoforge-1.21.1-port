package thaumcraft.common.tiles.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
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
    private int serverDelay;
    private int vanillaFurnaceBoosts;
    private String lastTargetKind = "";
    private float previousInflation = 1.0F;
    private float inflation = 1.0F;
    private boolean inflating;
    private boolean firstClientRun = true;

    public TCBellowsBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.BELLOWS.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCBellowsBlockEntity bellows) {
        if (level == null || level.isClientSide) {
            return;
        }
        bellows.tickServer(state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, TCBellowsBlockEntity bellows) {
        if (level == null || !level.isClientSide) {
            return;
        }
        bellows.tickClient(level, state);
    }

    public int pumpTicks() {
        return pumpTicks;
    }

    public int activeTicks() {
        return activeTicks;
    }

    public int vanillaFurnaceBoosts() {
        return vanillaFurnaceBoosts;
    }

    public String lastTargetKind() {
        return lastTargetKind;
    }

    public boolean isActive() {
        return activeTicks > 0;
    }

    public boolean hasTubeBufferExtension() {
        return "tube_buffer".equals(lastTargetKind);
    }

    public float inflation(float partialTick) {
        return Mth.lerp(Mth.clamp(partialTick, 0.0F, 1.0F), previousInflation, inflation);
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
            serverDelay++;
            if (serverDelay >= 2) {
                serverDelay = 0;
                dirty |= boostVanillaFurnace(state);
            }
        } else if (activeTicks > 0) {
            activeTicks--;
            serverDelay = 0;
            dirty = true;
        } else {
            serverDelay = 0;
        }

        if (dirty) {
            markChangedAndSync();
        }
    }

    private void tickClient(Level level, BlockState state) {
        boolean enabled = state.hasProperty(TCBellowsBlock.ENABLED) && state.getValue(TCBellowsBlock.ENABLED);
        previousInflation = inflation;
        if (!enabled) {
            return;
        }

        if (firstClientRun) {
            inflation = 0.35F + level.random.nextFloat() * 0.55F;
            previousInflation = inflation;
            firstClientRun = false;
        }

        if (inflation > 0.35F && !inflating) {
            inflation -= 0.075F;
        }
        if (inflation <= 0.35F && !inflating) {
            inflating = true;
        }
        if (inflation < 1.0F && inflating) {
            inflation += 0.025F;
        }
        if (inflation >= 1.0F && inflating) {
            inflation = 1.0F;
            inflating = false;
            float pitch = 0.5F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F;
            level.playLocalSound(
                    worldPosition.getX() + 0.5D,
                    worldPosition.getY() + 0.5D,
                    worldPosition.getZ() + 0.5D,
                    SoundEvents.GHAST_SHOOT,
                    SoundSource.BLOCKS,
                    0.01F,
                    pitch,
                    false
            );
        }
    }

    private String resolveTargetKind(BlockState state) {
        if (level == null || !state.hasProperty(TCBellowsBlock.FACING)) {
            return "";
        }

        Direction facing = state.getValue(TCBellowsBlock.FACING);
        BlockPos targetPos = worldPosition.relative(facing);
        BlockEntity target = level.getBlockEntity(targetPos);

        if (target instanceof TCSmelterBlockEntity) {
            return "smelter";
        }
        if (target instanceof TCLegacyTubeBlockEntity tube && tube.variant() == TCLegacyTubeVariant.BUFFER) {
            return "tube_buffer";
        }
        if (target instanceof AbstractFurnaceBlockEntity) {
            return "vanilla_furnace";
        }
        return "";
    }

    private boolean boostVanillaFurnace(BlockState state) {
        if (level == null || !state.hasProperty(TCBellowsBlock.FACING)) {
            return false;
        }
        BlockEntity target = level.getBlockEntity(worldPosition.relative(state.getValue(TCBellowsBlock.FACING)));
        if (target instanceof AbstractFurnaceBlockEntity furnace
                && TCVanillaFurnaceBellowsAccessor.boostCookProgress(furnace)) {
            vanillaFurnaceBoosts++;
            return true;
        }
        return false;
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
        tag.putInt("ServerDelay", serverDelay);
        tag.putInt("VanillaFurnaceBoosts", vanillaFurnaceBoosts);
        tag.putString("LastTargetKind", lastTargetKind);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        pumpTicks = Math.max(0, tag.getInt("PumpTicks"));
        activeTicks = Math.max(0, tag.getInt("ActiveTicks"));
        serverDelay = Math.max(0, tag.getInt("ServerDelay"));
        vanillaFurnaceBoosts = Math.max(0, tag.getInt("VanillaFurnaceBoosts"));
        lastTargetKind = tag.getString("LastTargetKind");
    }
}

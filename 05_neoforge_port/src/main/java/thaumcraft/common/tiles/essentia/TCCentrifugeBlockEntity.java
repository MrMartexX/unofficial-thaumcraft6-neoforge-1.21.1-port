package thaumcraft.common.tiles.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.essentia.container.TCAspectSourceContainer;
import thaumcraft.common.essentia.transport.TCEssentiaCapabilities;
import thaumcraft.common.essentia.transport.TCEssentiaStack;
import thaumcraft.common.essentia.transport.TCEssentiaSuction;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCSounds;

/** TC6 Essentia Centrifuge processing endpoint. */
public final class TCCentrifugeBlockEntity extends BlockEntity implements TCAspectSourceContainer, TCEssentiaTransport {

    public Aspect aspectIn;
    public Aspect aspectOut;
    private int count = 0;
    private int process = 0;

    // Client-side animation
    public float rotation = 0.0F;
    public float rotationSpeed = 0.0F;

    public TCCentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.CENTRIFUGE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCCentrifugeBlockEntity centrifuge) {
        if (level.hasNeighborSignal(pos)) {
            return;
        }

        boolean dirty = false;

        if (centrifuge.aspectOut == null && centrifuge.aspectIn == null && ++centrifuge.count % 5 == 0) {
            dirty |= centrifuge.drawEssentia();
        }

        if (centrifuge.process > 0) {
            centrifuge.process--;
        }

        if (centrifuge.aspectOut == null && centrifuge.aspectIn != null && centrifuge.process == 0) {
            centrifuge.processEssentia();
            dirty = true;
        }

        if (dirty) {
            centrifuge.markChangedAndSync();
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, TCCentrifugeBlockEntity centrifuge) {
        boolean powered = level.hasNeighborSignal(pos);
        if (centrifuge.aspectIn != null && !powered && centrifuge.rotationSpeed < 20.0F) {
            centrifuge.rotationSpeed += 2.0F;
        }
        if ((centrifuge.aspectIn == null || powered) && centrifuge.rotationSpeed > 0.0F) {
            centrifuge.rotationSpeed -= 0.5F;
        }

        int pr = (int) centrifuge.rotation;
        centrifuge.rotation += centrifuge.rotationSpeed;

        if (centrifuge.rotation % 180.0F <= 20.0F && pr % 180 >= 160 && centrifuge.rotationSpeed > 0.0F) {
            level.playSound(null, pos, TCSounds.PUMP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    private boolean drawEssentia() {
        if (level == null) return false;
        TCEssentiaTransport downTransport = level.getCapability(TCEssentiaCapabilities.BLOCK, worldPosition.below(), Direction.UP);
        if (downTransport != null && downTransport.canOutputTo(Direction.UP)) {
            TCEssentiaStack ess = downTransport.getEssentia(Direction.UP);
            if (ess != null && ess.amount() > 0) {
                Aspect ta = Aspect.getAspect(ess.aspect());
                TCEssentiaSuction upSuction = downTransport.getSuction(Direction.UP);
                TCEssentiaSuction downMySuction = getSuction(Direction.DOWN);
                if (ta != null && !ta.isPrimal() && upSuction.amount() < downMySuction.amount() && downMySuction.amount() >= downTransport.getMinimumSuction()) {
                    if (downTransport.takeEssentia(ta.getTag(), 1, Direction.UP, false) == 1) {
                        aspectIn = ta;
                        process = 39;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void processEssentia() {
        if (aspectIn != null) {
            Aspect[] comps = aspectIn.getComponents();
            if (comps != null && comps.length == 2) {
                int idx = (level != null && level.random != null) ? level.random.nextInt(2) : 0;
                aspectOut = comps[idx];
            }
            aspectIn = null;
        }
    }

    public int getProcess() {
        return process;
    }

    public void setProcessForTesting(int process) {
        this.process = process;
    }

    public void processEssentiaForTesting() {
        processEssentia();
    }

    private void markChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    }

    // NBT

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
        if (aspectIn != null) {
            tag.putString("aspectIn", aspectIn.getTag());
        }
        if (aspectOut != null) {
            tag.putString("aspectOut", aspectOut.getTag());
        }
        tag.putInt("process", process);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        aspectIn = Aspect.getAspect(tag.getString("aspectIn"));
        aspectOut = Aspect.getAspect(tag.getString("aspectOut"));
        process = tag.getInt("process");
    }

    // TCAspectSourceContainer

    @Override
    public AspectList storedAspects() {
        AspectList al = new AspectList();
        if (aspectOut != null) {
            al.add(aspectOut, 1);
        }
        return al;
    }

    @Override
    public int drainAspect(Aspect requestedAspect, int requestedAmount, boolean simulate) {
        if (aspectOut != null && aspectOut == requestedAspect && requestedAmount > 0) {
            if (!simulate) {
                aspectOut = null;
                markChangedAndSync();
            }
            return 1;
        }
        return 0;
    }

    @Override
    public boolean isSourceBlocked() {
        return false;
    }

    // TCEssentiaTransport

    @Override
    public boolean isConnectable(Direction face) {
        return face == Direction.UP || face == Direction.DOWN;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return face == Direction.UP;
    }

    @Override
    public TCEssentiaSuction getSuction(Direction face) {
        if (face == Direction.DOWN) {
            boolean powered = level != null && level.hasNeighborSignal(worldPosition);
            return new TCEssentiaSuction("", powered ? 0 : (aspectIn == null ? 128 : 64));
        }
        return TCEssentiaSuction.NONE;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Nullable
    @Override
    public TCEssentiaStack getEssentia(Direction face) {
        if (canOutputTo(face) && aspectOut != null) {
            return new TCEssentiaStack(aspectOut.getTag(), 1);
        }
        return null;
    }

    @Override
    public int addEssentia(String aspectTag, int requestedAmount, Direction face, boolean simulate) {
        if (canInputFrom(face) && requestedAmount > 0) {
            Aspect aspect = Aspect.getAspect(aspectTag);
            if (aspectIn == null && aspect != null && !aspect.isPrimal()) {
                if (!simulate) {
                    aspectIn = aspect;
                    process = 39;
                    markChangedAndSync();
                }
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int takeEssentia(String aspectTag, int requestedAmount, Direction face, boolean simulate) {
        if (canOutputTo(face) && requestedAmount > 0 && aspectOut != null && aspectOut.getTag().equals(aspectTag)) {
            if (!simulate) {
                aspectOut = null;
                markChangedAndSync();
            }
            return 1;
        }
        return 0;
    }
}

package thaumcraft.common.tiles.devices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.devices.TCCondenserBlock;
import thaumcraft.common.blocks.devices.TCCondenserLatticeBlock;
import thaumcraft.common.essentia.transport.TCEssentiaCapabilities;
import thaumcraft.common.essentia.transport.TCEssentiaStack;
import thaumcraft.common.essentia.transport.TCEssentiaSuction;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;

/**
 * TC6 Flux Condenser tile entity.
 * Sucks essentia horizontally from connected tubes to power the cleansing of flux
 * from the chunk aura. Condenses aura flux into liquid flux essentia output at the bottom.
 */
public class TCCondenserBlockEntity extends BlockEntity implements TCEssentiaTransport {
    public static final int MAX_CAPACITY = 100;

    private int essentia = 0;
    private int flux = 0;
    private int count = 0;
    private float latticeCount = -1.0F;
    private int interval = 0;
    private int cost = 0;

    private final Set<Long> checkHistory = new HashSet<>();
    private final List<Long> blockList = new ArrayList<>();
    private final List<Long> uncloggedList = new ArrayList<>();

    public TCCondenserBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.CONDENSER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCCondenserBlockEntity condenser) {
        condenser.tick(level, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (latticeCount < 0.0F) {
            triggerCheck();
        }
        count++;

        boolean enabled = !state.hasProperty(TCCondenserBlock.ENABLED) || state.getValue(TCCondenserBlock.ENABLED);
        if (enabled && latticeCount > 0.0F) {
            if (level.isClientSide) {
                if (essentia > 0 && !uncloggedList.isEmpty() && count % Math.max(3, interval / 50) == 0) {
                    BlockPos p = BlockPos.of(uncloggedList.get(level.random.nextInt(uncloggedList.size())));
                    level.addParticle(ParticleTypes.ELECTRIC_SPARK,
                            p.getX() + 0.5 + (level.random.nextFloat() - 0.5) * 0.4,
                            p.getY() + 0.5 + (level.random.nextFloat() - 0.5) * 0.4,
                            p.getZ() + 0.5 + (level.random.nextFloat() - 0.5) * 0.4,
                            0, 0, 0);
                }
            } else {
                if (count % 5 == 0 && essentia < MAX_CAPACITY) {
                    fillFromPipes();
                }
                if (interval > 0 && essentia >= cost && flux < MAX_CAPACITY && count % interval == 0) {
                    if (AuraHelper.getFlux(level, pos) >= 1.0F) {
                        AuraHelper.drainFlux(level, pos, 1.0F, false);
                        essentia -= cost;
                        flux++;
                        if (level.random.nextInt(50) == 0) {
                            makeLatticeDirty();
                        }
                        setChanged();
                        level.sendBlockUpdated(pos, state, state, 3);
                    }
                }
            }
        }
    }

    private void fillFromPipes() {
        if (level == null || level.isClientSide) return;
        for (Direction face : Direction.Plane.HORIZONTAL) {
            BlockPos targetPos = worldPosition.relative(face);
            TCEssentiaTransport pipe = level.getCapability(TCEssentiaCapabilities.BLOCK, targetPos, face.getOpposite());
            if (pipe != null && pipe.canOutputTo(face.getOpposite())) {
                TCEssentiaStack available = pipe.getEssentia(face.getOpposite());
                if (available != null && !available.isEmpty()) {
                    String aspect = available.aspect();
                    int space = MAX_CAPACITY - essentia;
                    int taken = pipe.takeEssentia(aspect, Math.min(space, 1), face.getOpposite(), false);
                    if (taken > 0) {
                        if ("vitium".equals(aspect) || "flux".equals(aspect)) {
                            makeLatticeDirty();
                        } else {
                            essentia += taken;
                        }
                        setChanged();
                        if (essentia >= MAX_CAPACITY) break;
                    }
                }
            }
        }
    }

    public void invalidateLatticeCheck() {
        this.latticeCount = -1.0F;
    }

    public void triggerCheck() {
        if (level == null) return;
        checkHistory.clear();
        blockList.clear();
        uncloggedList.clear();
        latticeCount = 0.0F;
        interval = 0;
        performCheck(worldPosition, true, false);
        checkHistory.clear();

        if (latticeCount <= 0.0F) {
            latticeCount = 0.0F;
        } else {
            if (latticeCount > 40.0F) {
                latticeCount = 40.0F;
            }
            interval = Math.round(600.0F - latticeCount * 15.0F);
            if (interval < 5) interval = 5;
            cost = (int) (4.0 + Math.sqrt(blockList.size()));
        }
    }

    private void performCheck(BlockPos currentPos, boolean skip, boolean clogged) {
        if (latticeCount < 0.0F || level == null) return;
        checkHistory.add(currentPos.asLong());
        boolean found = false;
        int sides = 0;

        for (Direction face : Direction.values()) {
            if (!skip || face == Direction.UP) {
                BlockPos nextPos = currentPos.relative(face);
                BlockState bs = level.getBlockState(nextPos);
                boolean lattice = bs.getBlock() == TCBlocks.CONDENSER_LATTICE.get();
                boolean latticeDirty = bs.getBlock() == TCBlocks.CONDENSER_LATTICE_DIRTY.get();

                if (skip && latticeDirty) clogged = true;
                if (lattice || latticeDirty) sides++;

                if (!checkHistory.contains(nextPos.asLong())) {
                    if (face == Direction.DOWN && bs.getBlock() == TCBlocks.CONDENSER.get()) {
                        latticeCount = -99.0F;
                        return;
                    }
                    if (worldPosition.getY() < nextPos.getY() && worldPosition.distSqr(nextPos) <= 74.0) {
                        if (lattice || latticeDirty) {
                            blockList.add(nextPos.asLong());
                            if (lattice) uncloggedList.add(nextPos.asLong());
                            found = true;
                            performCheck(nextPos, false, clogged || latticeDirty);
                            if (skip) break;
                        }
                    }
                }
            }
        }

        if (found && !clogged) {
            float f = 1.0F - 0.15F * sides;
            latticeCount += f;
        }
    }

    private void makeLatticeDirty() {
        if (level == null || level.isClientSide || uncloggedList.isEmpty()) return;
        int idx = level.random.nextInt(uncloggedList.size());
        BlockPos target = BlockPos.of(uncloggedList.get(idx));
        BlockState bs = level.getBlockState(target);
        if (bs.getBlock() == TCBlocks.CONDENSER_LATTICE.get()) {
            BlockState dirtyState = TCBlocks.CONDENSER_LATTICE_DIRTY.get().defaultBlockState()
                    .setValue(TCCondenserLatticeBlock.DOWN, bs.getValue(TCCondenserLatticeBlock.DOWN))
                    .setValue(TCCondenserLatticeBlock.UP, bs.getValue(TCCondenserLatticeBlock.UP))
                    .setValue(TCCondenserLatticeBlock.NORTH, bs.getValue(TCCondenserLatticeBlock.NORTH))
                    .setValue(TCCondenserLatticeBlock.SOUTH, bs.getValue(TCCondenserLatticeBlock.SOUTH))
                    .setValue(TCCondenserLatticeBlock.WEST, bs.getValue(TCCondenserLatticeBlock.WEST))
                    .setValue(TCCondenserLatticeBlock.EAST, bs.getValue(TCCondenserLatticeBlock.EAST));
            level.setBlock(target, dirtyState, 3);
            if (dirtyState.getBlock() instanceof TCCondenserLatticeBlock dirtyLattice) {
                dirtyLattice.triggerUpdate(level, target);
            }
            invalidateLatticeCheck();
        }
    }

    public int getEssentia() {
        return essentia;
    }

    public int getFlux() {
        return flux;
    }

    public float getLatticeCount() {
        return latticeCount;
    }

    public int getInterval() {
        return interval;
    }

    public int getCost() {
        return cost;
    }

    public void setEssentiaForTesting(int essentia) {
        this.essentia = essentia;
        setChanged();
    }

    public void setFluxForTesting(int flux) {
        this.flux = flux;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putShort("essentia", (short) essentia);
        tag.putShort("flux", (short) flux);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        essentia = tag.getShort("essentia");
        flux = tag.getShort("flux");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putShort("essentia", (short) essentia);
        tag.putShort("flux", (short) flux);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // TCEssentiaTransport implementation
    @Override
    public boolean isConnectable(Direction face) {
        return face != Direction.UP;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return face != Direction.UP && face != Direction.DOWN;
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public TCEssentiaSuction getSuction(Direction face) {
        if (face == Direction.DOWN || essentia >= MAX_CAPACITY) {
            return TCEssentiaSuction.NONE;
        }
        return new TCEssentiaSuction("", 128);
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public TCEssentiaStack getEssentia(Direction face) {
        return flux > 0 ? new TCEssentiaStack("vitium", flux) : TCEssentiaStack.EMPTY;
    }

    @Override
    public int addEssentia(String aspect, int amount, Direction face, boolean simulate) {
        if (!canInputFrom(face) || amount <= 0) return 0;
        int space = MAX_CAPACITY - essentia;
        int accepted = Math.min(space, amount);
        if (!simulate && accepted > 0) {
            if ("vitium".equals(aspect) || "flux".equals(aspect)) {
                makeLatticeDirty();
            } else {
                essentia += accepted;
            }
            setChanged();
        }
        return accepted;
    }

    @Override
    public int takeEssentia(String aspect, int amount, Direction face, boolean simulate) {
        if (!canOutputTo(face) || amount <= 0) return 0;
        if (aspect != null && !aspect.isBlank() && !aspect.equals("vitium") && !aspect.equals("flux")) {
            return 0;
        }
        int taken = Math.min(flux, amount);
        if (!simulate && taken > 0) {
            flux -= taken;
            setChanged();
        }
        return taken;
    }
}

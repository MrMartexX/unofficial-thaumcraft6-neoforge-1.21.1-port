package thaumcraft.common.tiles.devices;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.registry.TCBlockEntities;

/** Server-owned Everfull Urn behavior based on legacy {@code TileWaterJug}. */
public final class TCWaterJugBlockEntity extends BlockEntity {
    public static final int CAPACITY_MB = 1000;
    public static final int LEGACY_HANDLER_STEP_MB = 25;
    public static final int LEGACY_CAULDRON_COST_MB = 333;
    public static final int LEGACY_BOTTLE_COST_MB = 333;
    public static final int LEGACY_SCAN_INTERVAL_TICKS = 5;

    private final EverfullUrnTank tank = new EverfullUrnTank();
    private final ArrayList<Integer> handlers = new ArrayList<>();
    private int zone;
    private int counter;
    private int lastClientTrailZone;
    private int clientTrailTicks;

    public TCWaterJugBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.EVERFULL_URN.get(), pos, state);
        tank.setFluid(FluidStack.EMPTY);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCWaterJugBlockEntity urn) {
        if (!level.isClientSide) {
            urn.tickServer();
        }
    }

    public IFluidHandler fluidHandler() {
        return tank;
    }

    public IFluidHandler fluidHandler(Direction side) {
        return side == Direction.UP ? tank : null;
    }

    public int waterAmount() {
        return tank.getFluidAmount();
    }

    public List<Integer> handlerZonesForValidation() {
        return List.copyOf(handlers);
    }

    public static int legacyZoneForOffset(int xOffset, int yOffset, int zOffset) {
        return (zOffset + 2) + 5 * (xOffset + 2) + 25 * (yOffset + 1);
    }

    public void setScanZoneForValidation(int encodedZone) {
        zone = Math.max(0, encodedZone);
        counter = 0;
        handlers.clear();
        setChanged();
    }

    public void setWaterForValidation(int amount) {
        tank.setFluid(amount <= 0 ? FluidStack.EMPTY : new FluidStack(Fluids.WATER, Math.min(CAPACITY_MB, amount)));
        markChangedAndSync();
    }

    public void drainWaterForValidation(int amount) {
        tank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
    }

    public void tickServerForValidation(int ticks) {
        for (int i = 0; i < ticks; i++) {
            tickServer();
        }
    }

    private void tickServer() {
        counter++;
        if (counter % LEGACY_SCAN_INTERVAL_TICKS != 0) {
            return;
        }

        zone++;
        BlockPos scanned = posForZone(zone);
        if (isCandidateHandler(scanned) && !handlers.contains(zone)) {
            handlers.add(zone);
            setChanged();
        }

        for (int index = 0; index < handlers.size() && tank.getFluidAmount() >= LEGACY_HANDLER_STEP_MB; index++) {
            int cachedZone = handlers.get(index);
            BlockPos target = posForZone(cachedZone);
            TransferResult result = tryFillCachedTarget(target);
            if (result == TransferResult.TRANSFERRED) {
                if (level != null) {
                    level.blockEvent(worldPosition, getBlockState().getBlock(), 1, cachedZone);
                }
                markChangedAndSync();
                break;
            }
            if (result == TransferResult.INVALID) {
                handlers.remove(index);
                index--;
                setChanged();
            }
        }

        refillFromAura();
    }

    private BlockPos posForZone(int encodedZone) {
        int x = encodedZone / 5 % 5;
        int y = encodedZone / 25 % 3;
        int z = encodedZone % 5;
        return worldPosition.offset(x - 2, y - 1, z - 2);
    }

    private boolean isCandidateHandler(BlockPos target) {
        if (level == null) {
            return false;
        }
        BlockState state = level.getBlockState(target);
        if (state.is(Blocks.CAULDRON) || state.is(Blocks.WATER_CAULDRON)) {
            return true;
        }
        if (level.getCapability(Capabilities.FluidHandler.BLOCK, target, Direction.UP) != null) {
            return true;
        }
        return false;
    }

    private TransferResult tryFillCachedTarget(BlockPos target) {
        if (level == null) {
            return TransferResult.INVALID;
        }

        BlockState state = level.getBlockState(target);
        if (state.is(Blocks.CAULDRON) || state.is(Blocks.WATER_CAULDRON)) {
            if (tank.getFluidAmount() < LEGACY_CAULDRON_COST_MB) {
                return TransferResult.VALID_IDLE;
            }
            if (fillCauldron(target, state)) {
                tank.drain(LEGACY_CAULDRON_COST_MB, IFluidHandler.FluidAction.EXECUTE);
                return TransferResult.TRANSFERRED;
            }
            return TransferResult.VALID_IDLE;
        }

        IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, target, Direction.UP);
        if (handler != null) {
            int filled = handler.fill(new FluidStack(Fluids.WATER, LEGACY_HANDLER_STEP_MB), IFluidHandler.FluidAction.EXECUTE);
            if (filled > 0) {
                tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                return TransferResult.TRANSFERRED;
            }
            return TransferResult.VALID_IDLE;
        }

        return TransferResult.INVALID;
    }

    private boolean fillCauldron(BlockPos target, BlockState state) {
        if (level == null) {
            return false;
        }
        if (state.is(Blocks.CAULDRON)) {
            level.setBlock(target, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 1), Block.UPDATE_ALL);
            level.updateNeighbourForOutputSignal(target, Blocks.WATER_CAULDRON);
            return true;
        }
        if (state.is(Blocks.WATER_CAULDRON)) {
            int levelValue = state.getValue(LayeredCauldronBlock.LEVEL);
            if (levelValue >= 3) {
                return false;
            }
            level.setBlock(target, state.setValue(LayeredCauldronBlock.LEVEL, levelValue + 1), Block.UPDATE_ALL);
            level.updateNeighbourForOutputSignal(target, state.getBlock());
            return true;
        }
        return false;
    }

    private void refillFromAura() {
        if (level == null || tank.getFluidAmount() >= CAPACITY_MB) {
            return;
        }
        float request = (CAPACITY_MB - tank.getFluidAmount()) / (float) CAPACITY_MB;
        if (request > 0.1F) {
            request = 0.1F;
        }
        float drainedVis = AuraHelper.drainVis(level, worldPosition, request, false);
        int water = (int) (CAPACITY_MB * drainedVis);
        if (water > 0) {
            tank.fillInternal(water);
        }
    }

    @Override
    public boolean triggerEvent(int id, int data) {
        if (id == 1 && level != null && level.isClientSide) {
            lastClientTrailZone = data;
            clientTrailTicks = 5;
            return true;
        }
        return super.triggerEvent(id, data);
    }

    public int lastClientTrailZone() {
        return lastClientTrailZone;
    }

    public int clientTrailTicks() {
        return clientTrailTicks;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tank.writeToNBT(registries, tag);
        tag.putInt("Zone", zone);
        tag.putInt("Counter", counter);
        tag.putIntArray("Handlers", handlers.stream().mapToInt(Integer::intValue).toArray());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tank.readFromNBT(registries, tag);
        zone = Math.max(0, tag.getInt("Zone"));
        counter = Math.max(0, tag.getInt("Counter"));
        handlers.clear();
        for (int handler : tag.getIntArray("Handlers")) {
            handlers.add(handler);
        }
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

    private void markChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private enum TransferResult {
        TRANSFERRED,
        VALID_IDLE,
        INVALID
    }

    private final class EverfullUrnTank extends FluidTank {
        private EverfullUrnTank() {
            super(CAPACITY_MB, stack -> stack.is(Fluids.WATER));
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }

        private int fillInternal(int amount) {
            int accepted = Math.min(getSpace(), Math.max(0, amount));
            if (accepted <= 0) {
                return 0;
            }
            if (fluid.isEmpty()) {
                fluid = new FluidStack(Fluids.WATER, accepted);
            } else {
                fluid.grow(accepted);
            }
            onContentsChanged();
            return accepted;
        }

        @Override
        protected void onContentsChanged() {
            markChangedAndSync();
        }
    }
}

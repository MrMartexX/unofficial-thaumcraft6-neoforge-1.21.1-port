package thaumcraft.common.essentia.transport.blockentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.essentia.transport.TCEssentiaStack;
import thaumcraft.common.essentia.transport.TCEssentiaSuction;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.essentia.transport.TCEssentiaCapabilities;
import thaumcraft.common.essentia.transport.block.TCLegacyTubeVariant;
import thaumcraft.common.essentia.transport.block.TCLegacyTubeBlock;
import thaumcraft.common.blocks.essentia.TCBellowsBlock;
import thaumcraft.common.lib.fx.TCFXDispatcher;
import thaumcraft.common.registry.TCBlocks;

/**
 * Server-owned port of the TC6 {@code TileTube} family.
 *
 * <p>The cadence, one-point normal-tube buffer, ten-point mixed buffer, suction attenuation,
 * restrict halving and one-way direction tests intentionally follow the legacy implementation.
 * Rendering and caster sub-part ray tracing remain separate client/interaction work.</p>
 */
public class TCLegacyTubeBlockEntity extends TCAbstractEssentiaTransportBlockEntity {
    public static final int SUCTION_INTERVAL = 2;
    public static final int TRANSFER_INTERVAL = 5;
    public static final int BUFFER_CAPACITY = 10;

    private final TCLegacyTubeVariant variant;
    private final boolean[] openSides = {true, true, true, true, true, true};
    private final byte[] chokedSides = new byte[6];
    private Direction facing = Direction.NORTH;
    private String suctionAspect = "";
    private int suctionAmount;
    private String filterAspect = "";
    private boolean allowFlow = true;
    private boolean wasPoweredLastTick;
    private int tickCounter;
    private int venting;
    private int ventColor = 0xAAAAAA;
    private int bellows;
    private float valveRotation;
    private float previousValveRotation;

    public TCLegacyTubeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, TCLegacyTubeVariant variant) {
        super(type, pos, state, variant.mode(), variant.storageCapacity());
        this.variant = variant;
    }

    public TCLegacyTubeVariant variant() {
        return variant;
    }

    public Direction facing() {
        return facing;
    }

    public void setFacing(Direction facing) {
        if (facing != null && this.facing != facing) {
            this.facing = facing;
            markTransportDirty();
            if (level != null) {
                level.invalidateCapabilities(worldPosition);
            }
        }
    }

    public boolean isSideOpen(Direction side) {
        return side != null && openSides[side.get3DDataValue()];
    }

    public void setSideOpen(Direction side, boolean open) {
        if (side == null || openSides[side.get3DDataValue()] == open) {
            return;
        }
        openSides[side.get3DDataValue()] = open;
        markTransportDirty();
        if (level != null) {
            level.invalidateCapabilities(worldPosition);
        }
        synchronizeNeighbourTubeSide(side, open);
    }

    public byte chokedSide(Direction side) {
        return side == null ? 0 : chokedSides[side.get3DDataValue()];
    }

    public void setChokedSide(Direction side, int choke) {
        if (side == null || variant != TCLegacyTubeVariant.BUFFER) {
            return;
        }
        int wrapped = choke % 3;
        if (wrapped < 0) {
            wrapped += 3;
        }
        byte normalized = (byte) wrapped;
        if (chokedSides[side.get3DDataValue()] != normalized) {
            chokedSides[side.get3DDataValue()] = normalized;
            markTransportDirty();
        }
    }

    public String filterAspect() {
        return filterAspect;
    }

    public void setFilterAspect(String aspect) {
        String normalized = aspect == null ? "" : aspect;
        if (!filterAspect.equals(normalized)) {
            filterAspect = normalized;
            markTransportDirty();
        }
    }

    public boolean allowsFlow() {
        return allowFlow;
    }

    public void setAllowFlow(boolean allowFlow) {
        if (variant != TCLegacyTubeVariant.VALVE || this.allowFlow == allowFlow) {
            return;
        }
        this.allowFlow = allowFlow;
        if (!allowFlow) {
            setCalculatedSuction("", 0);
        }
        markTransportDirty();
    }

    public int ventingTicks() {
        return venting;
    }

    public int ventColor() {
        return ventColor;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCLegacyTubeBlockEntity tube) {
        if (level == null || level.isClientSide) {
            return;
        }
        tube.tickTransportServer();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, TCLegacyTubeBlockEntity tube) {
        if (level == null || !level.isClientSide) {
            return;
        }
        tube.tickTransportClient();
    }

    private void tickTransportClient() {
        previousValveRotation = valveRotation;
        if (variant == TCLegacyTubeVariant.VALVE) {
            if (!allowFlow && valveRotation < 360.0F) {
                valveRotation = Math.min(360.0F, valveRotation + 20.0F);
            } else if (allowFlow && valveRotation > 0.0F) {
                valveRotation = Math.max(0.0F, valveRotation - 20.0F);
            }
        }
        if (venting > 0 && level != null) {
            venting--;
            RandomSource random = level.getRandom();
            float pitch = random.nextFloat() * 360.0F;
            float yaw = random.nextFloat() * 360.0F;
            double motionX = -Mth.sin(yaw / 180.0F * Mth.PI) * Mth.cos(pitch / 180.0F * Mth.PI) / 5.0D;
            double motionZ = Mth.cos(yaw / 180.0F * Mth.PI) * Mth.cos(pitch / 180.0F * Mth.PI) / 5.0D;
            double motionY = -Mth.sin(pitch / 180.0F * Mth.PI) / 5.0D;
            TCFXDispatcher.drawVentParticles(
                    level,
                    worldPosition.getX() + 0.5D,
                    worldPosition.getY() + 0.5D,
                    worldPosition.getZ() + 0.5D,
                    motionX,
                    motionY,
                    motionZ,
                    ventColor
            );
        }
    }

    public float valveRotation(float partialTick) {
        return Mth.lerp(partialTick, previousValveRotation, valveRotation);
    }

    @Override
    protected void tickTransportServer() {
        if (tickCounter % TRANSFER_INTERVAL == 0) {
            refreshConnectionState();
        }
        if (variant == TCLegacyTubeVariant.BUFFER) {
            tickBuffer();
            return;
        }

        if (variant == TCLegacyTubeVariant.VALVE && tickCounter % TRANSFER_INTERVAL == 0) {
            updateValvePower();
        }
        if (venting > 0) {
            venting--;
            if (venting > 0) {
                return;
            }
        }
        if (tickCounter == 0 && level != null) {
            tickCounter = level.random.nextInt(10);
        }
        tickCounter++;
        if (tickCounter % SUCTION_INTERVAL == 0) {
            calculateSuction();
            checkVenting();
            clearEmptyNormalAspect();
        }
        if (tickCounter % TRANSFER_INTERVAL == 0 && suctionAmount > 0) {
            equalizeWithNeighbours();
        }
    }

    private void tickBuffer() {
        boolean pressureChanged = refreshBellowsPressure();
        tickCounter++;
        if (tickCounter % TRANSFER_INTERVAL == 0 && transportNode.storage().totalAmount() < BUFFER_CAPACITY) {
            fillBuffer();
        }
        if (pressureChanged) {
            markTransportDirty();
        }
    }

    private boolean refreshBellowsPressure() {
        if (level == null || variant != TCLegacyTubeVariant.BUFFER) {
            return false;
        }
        int previousBellows = bellows;
        int foundBellows = 0;
        for (Direction direction : Direction.values()) {
            BlockState neighbour = level.getBlockState(worldPosition.relative(direction));
            if (neighbour.is(TCBlocks.BELLOWS.get())
                    && neighbour.hasProperty(TCBellowsBlock.FACING)
                    && neighbour.hasProperty(TCBellowsBlock.ENABLED)
                    && neighbour.getValue(TCBellowsBlock.ENABLED)
                    && neighbour.getValue(TCBellowsBlock.FACING) == direction.getOpposite()) {
                foundBellows++;
            }
        }
        bellows = foundBellows;
        return previousBellows != bellows;
    }

    private void calculateSuction() {
        suctionAmount = 0;
        suctionAspect = "";
        String filter = variant == TCLegacyTubeVariant.FILTER ? filterAspect : "";
        boolean directional = variant == TCLegacyTubeVariant.ONEWAY;

        for (Direction direction : Direction.values()) {
            if (directional && facing != direction.getOpposite()) {
                continue;
            }
            TCEssentiaTransport neighbour = connectableNeighbour(direction);
            if (neighbour == null) {
                continue;
            }
            Direction neighbourFace = direction.getOpposite();
            TCEssentiaSuction neighbourSuction = neighbour.getSuction(neighbourFace);
            String neighbourType = neighbourSuction.aspect();
            String storedType = getEssentia(direction).aspect();
            int storedAmount = getEssentia(direction).amount();

            if (!filter.isBlank() && !neighbourType.isBlank() && !filter.equals(neighbourType)) {
                continue;
            }
            if (filter.isBlank() && storedAmount > 0 && !neighbourType.isBlank() && !storedType.equals(neighbourType)) {
                continue;
            }
            if (!filter.isBlank() && storedAmount > 0 && !storedType.isBlank()
                    && !neighbourType.isBlank() && !storedType.equals(neighbourType)) {
                continue;
            }
            int neighbourAmount = neighbourSuction.amount();
            if (neighbourAmount <= 0 || neighbourAmount <= suctionAmount + 1) {
                continue;
            }
            String propagatedType = neighbourType.isBlank() ? filter : neighbourType;
            int propagatedAmount = variant == TCLegacyTubeVariant.RESTRICT
                    ? neighbourAmount / 2
                    : neighbourAmount - 1;
            setCalculatedSuction(propagatedType, propagatedAmount);
        }
    }

    private void equalizeWithNeighbours() {
        if (transportNode.storage().totalAmount() > 0) {
            return;
        }
        boolean directional = variant == TCLegacyTubeVariant.ONEWAY;
        for (Direction direction : Direction.values()) {
            if (directional && facing == direction.getOpposite()) {
                continue;
            }
            TCEssentiaTransport neighbour = connectableNeighbour(direction);
            if (neighbour == null || !neighbour.canOutputTo(direction.getOpposite())) {
                continue;
            }
            Direction neighbourFace = direction.getOpposite();
            TCEssentiaStack neighbourEssentia = neighbour.getEssentia(neighbourFace);
            TCEssentiaSuction neighbourSuction = neighbour.getSuction(neighbourFace);
            if (!suctionAspect.isBlank() && !neighbourEssentia.isEmpty()
                    && !suctionAspect.equals(neighbourEssentia.aspect())) {
                continue;
            }
            if (suctionAmount <= neighbourSuction.amount() || suctionAmount < neighbour.getMinimumSuction()) {
                continue;
            }

            String aspect = suctionAspect;
            if (aspect.isBlank()) {
                aspect = neighbourEssentia.aspect();
                if (aspect.isBlank()) {
                    aspect = neighbour.getEssentia(null).aspect();
                }
            }
            if (aspect.isBlank()) {
                continue;
            }
            int available = neighbour.takeEssentia(aspect, 1, neighbourFace, true);
            int accepted = addEssentia(aspect, available, direction, true);
            if (available <= 0 || accepted <= 0) {
                continue;
            }
            int taken = neighbour.takeEssentia(aspect, 1, neighbourFace, false);
            if (taken > 0 && addEssentia(aspect, taken, direction, false) > 0) {
                return;
            }
        }
    }

    private void fillBuffer() {
        for (Direction direction : Direction.values()) {
            if (!canInputFrom(direction)) {
                continue;
            }
            TCEssentiaTransport neighbour = connectableNeighbour(direction);
            if (neighbour == null) {
                continue;
            }
            Direction neighbourFace = direction.getOpposite();
            TCEssentiaStack visible = neighbour.getEssentia(neighbourFace);
            int ownSuction = getSuction(direction).amount();
            if (visible.isEmpty()
                    || neighbour.getSuction(neighbourFace).amount() >= ownSuction
                    || ownSuction < neighbour.getMinimumSuction()) {
                continue;
            }
            int available = neighbour.takeEssentia(visible.aspect(), 1, neighbourFace, true);
            if (available <= 0 || addEssentia(visible.aspect(), 1, direction, true) <= 0) {
                continue;
            }
            int taken = neighbour.takeEssentia(visible.aspect(), 1, neighbourFace, false);
            if (taken > 0) {
                addEssentia(visible.aspect(), taken, direction, false);
                return;
            }
        }
    }

    private void checkVenting() {
        for (Direction direction : Direction.values()) {
            TCEssentiaTransport neighbour = connectableNeighbour(direction);
            if (neighbour == null || neighbour instanceof TCLegacyTubeBlockEntity tube
                    && tube.variant == TCLegacyTubeVariant.FILTER) {
                continue;
            }
            TCEssentiaSuction neighbourSuction = neighbour.getSuction(direction.getOpposite());
            int neighbourAmount = neighbourSuction.amount();
            if (suctionAmount > 0
                    && (neighbourAmount == suctionAmount || neighbourAmount == suctionAmount - 1)
                    && !sameAspect(suctionAspect, neighbourSuction.aspect())) {
                venting = 40;
                ventColor = colorForAspect(suctionAspect);
                markTransportDirty();
            }
        }
    }

    private void updateValvePower() {
        if (level == null) {
            return;
        }
        boolean powered = level.hasNeighborSignal(worldPosition);
        if (powered != wasPoweredLastTick) {
            allowFlow = !powered;
            wasPoweredLastTick = powered;
            if (!allowFlow) {
                setCalculatedSuction("", 0);
            }
            markTransportDirty();
        }
    }

    private void clearEmptyNormalAspect() {
        if (transportNode.storage().totalAmount() <= 0) {
            transportNode.mutableStorage().clear();
        }
    }

    private void setCalculatedSuction(String aspect, int amount) {
        if (variant == TCLegacyTubeVariant.VALVE && !allowFlow && amount > 0) {
            return;
        }
        suctionAspect = aspect == null ? "" : aspect;
        suctionAmount = Math.max(0, amount);
        transportNode.setSuction(new TCEssentiaSuction(suctionAspect, suctionAmount));
    }

    private TCEssentiaTransport connectableNeighbour(Direction direction) {
        if (level == null || direction == null || !isConnectable(direction)) {
            return null;
        }
        return level.getCapability(
                TCEssentiaCapabilities.BLOCK,
                worldPosition.relative(direction),
                direction.getOpposite()
        );
    }

    private void synchronizeNeighbourTubeSide(Direction side, boolean open) {
        if (level == null || level.isClientSide || side == null) {
            return;
        }
        BlockEntity neighbour = level.getBlockEntity(worldPosition.relative(side));
        if (neighbour instanceof TCLegacyTubeBlockEntity tube) {
            Direction opposite = side.getOpposite();
            if (tube.openSides[opposite.get3DDataValue()] != open) {
                tube.openSides[opposite.get3DDataValue()] = open;
                tube.markTransportDirty();
                level.invalidateCapabilities(tube.worldPosition);
            }
        }
    }

    public boolean hasConnectableNeighbour(Direction side) {
        if (level == null || side == null) {
            return false;
        }
        return level.getCapability(
                TCEssentiaCapabilities.BLOCK,
                worldPosition.relative(side),
                side.getOpposite()
        ) != null;
    }

    public boolean casterToggleSide(Direction side, boolean sneaking) {
        if (side == null) {
            return false;
        }
        if (variant == TCLegacyTubeVariant.BUFFER && sneaking) {
            setChokedSide(side, chokedSide(side) + 1);
            return true;
        }
        setSideOpen(side, !isSideOpen(side));
        refreshConnectionState();
        BlockEntity neighbour = level == null ? null : level.getBlockEntity(worldPosition.relative(side));
        if (neighbour instanceof TCLegacyTubeBlockEntity tube) {
            tube.refreshConnectionState();
        }
        return true;
    }

    public boolean casterRotateCenter() {
        if (variant == TCLegacyTubeVariant.BUFFER) {
            return false;
        }
        int start = facing.get3DDataValue();
        for (int step = 1; step < 20; step++) {
            Direction candidate = Direction.from3DDataValue((start + step) % 6);
            if (variant == TCLegacyTubeVariant.VALVE) {
                if (!hasConnectableNeighbour(candidate)) {
                    setFacing(candidate);
                    refreshConnectionState();
                    return true;
                }
            } else {
                Direction acceptedSide = candidate.getOpposite();
                if (hasConnectableNeighbour(acceptedSide) && isSideOpen(acceptedSide)) {
                    setFacing(candidate);
                    refreshConnectionState();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isConnectable(Direction face) {
        if (face == null || !isSideOpen(face)) {
            return false;
        }
        return variant != TCLegacyTubeVariant.VALVE || face != facing;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return face != null && isSideOpen(face);
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return face != null && isSideOpen(face);
    }

    @Override
    public TCEssentiaSuction getSuction(Direction face) {
        if (variant == TCLegacyTubeVariant.BUFFER) {
            if (face == null) {
                return new TCEssentiaSuction("", bellows > 0 ? bellows * 32 : 1);
            }
            int choke = chokedSides[face.get3DDataValue()];
            int amount = choke == 2 ? 0 : (bellows <= 0 || choke == 1 ? 1 : bellows * 32);
            return new TCEssentiaSuction("", amount);
        }
        return new TCEssentiaSuction(suctionAspect, suctionAmount);
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public TCEssentiaStack getEssentia(Direction face) {
        if (variant != TCLegacyTubeVariant.BUFFER) {
            return super.getEssentia(face);
        }
        List<Map.Entry<String, Integer>> stored = new ArrayList<>(transportNode.storage().snapshot().entrySet());
        if (stored.isEmpty()) {
            return TCEssentiaStack.EMPTY;
        }
        Map.Entry<String, Integer> selected = level == null
                ? stored.getFirst()
                : stored.get(level.random.nextInt(stored.size()));
        return TCEssentiaStack.of(selected.getKey(), selected.getValue());
    }

    @Override
    public int addEssentia(String aspect, int amount, Direction face, boolean simulate) {
        if (!canInputFrom(face) || aspect == null || aspect.isBlank() || amount <= 0) {
            return 0;
        }
        int accepted;
        if (variant == TCLegacyTubeVariant.BUFFER) {
            if (amount != 1 || transportNode.storage().totalAmount() >= BUFFER_CAPACITY) {
                return 0;
            }
            accepted = transportNode.mutableStorage().add(aspect, 1, simulate);
        } else {
            if (transportNode.storage().totalAmount() > 0) {
                return 0;
            }
            accepted = transportNode.mutableStorage().add(aspect, 1, simulate);
        }
        if (!simulate && accepted > 0) {
            markTransportDirty();
        }
        return accepted;
    }

    @Override
    public int takeEssentia(String aspect, int amount, Direction face, boolean simulate) {
        if (!canOutputTo(face) || aspect == null || aspect.isBlank() || amount <= 0) {
            return 0;
        }
        int requested = variant == TCLegacyTubeVariant.BUFFER
                ? Math.min(amount, transportNode.storage().amount(aspect))
                : Math.min(1, amount);
        if (requested <= 0 || variant == TCLegacyTubeVariant.BUFFER && hasStrongerBufferDemand(aspect, face)) {
            return 0;
        }
        int taken = transportNode.mutableStorage().take(aspect, requested, simulate);
        if (!simulate && taken > 0) {
            markTransportDirty();
        }
        return taken;
    }

    private boolean hasStrongerBufferDemand(String aspect, Direction requestedFace) {
        TCEssentiaTransport requester = connectableNeighbour(requestedFace);
        int requesterSuction = requester == null ? 0 : requester.getSuction(requestedFace.getOpposite()).amount();
        for (Direction direction : Direction.values()) {
            if (direction == requestedFace || !canOutputTo(direction)) {
                continue;
            }
            TCEssentiaTransport neighbour = connectableNeighbour(direction);
            if (neighbour == null) {
                continue;
            }
            TCEssentiaSuction suction = neighbour.getSuction(direction.getOpposite());
            if ((suction.aspect().isBlank() || suction.aspect().equals(aspect))
                    && requesterSuction < suction.amount()
                    && getSuction(direction).amount() < suction.amount()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Facing", facing.getName());
        tag.putString("SuctionAspect", suctionAspect);
        tag.putInt("SuctionAmount", suctionAmount);
        tag.putString("FilterAspect", filterAspect);
        tag.putBoolean("AllowFlow", allowFlow);
        tag.putBoolean("WasPowered", wasPoweredLastTick);
        tag.putInt("TickCounter", tickCounter);
        tag.putInt("Venting", venting);
        tag.putInt("VentColor", ventColor);
        tag.put("OpenSides", new IntArrayTag(booleanArrayToInts(openSides)));
        tag.put("ChokedSides", new IntArrayTag(byteArrayToInts(chokedSides)));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        Direction loadedFacing = Direction.byName(tag.getString("Facing"));
        facing = loadedFacing == null ? Direction.NORTH : loadedFacing;
        suctionAspect = tag.getString("SuctionAspect");
        suctionAmount = Math.max(0, tag.getInt("SuctionAmount"));
        filterAspect = tag.getString("FilterAspect");
        allowFlow = !tag.contains("AllowFlow") || tag.getBoolean("AllowFlow");
        wasPoweredLastTick = tag.getBoolean("WasPowered");
        tickCounter = Math.max(0, tag.getInt("TickCounter"));
        venting = Math.max(0, tag.getInt("Venting"));
        ventColor = tag.contains("VentColor") ? tag.getInt("VentColor") : 0xAAAAAA;
        copyIntsToBooleans(tag.getIntArray("OpenSides"), openSides, true);
        copyIntsToBytes(tag.getIntArray("ChokedSides"), chokedSides);
        transportNode.setSuction(new TCEssentiaSuction(suctionAspect, suctionAmount));
    }

    public void setSuctionForValidation(String aspect, int amount) {
        setCalculatedSuction(aspect, amount);
    }

    public void setBellowsForValidation(int bellows) {
        setBellowsPressure(bellows);
    }

    public void setBellowsPressure(int bellows) {
        int normalized = Math.max(0, bellows);
        if (this.bellows != normalized) {
            this.bellows = normalized;
            markTransportDirty();
        }
    }

    public void setVentingForValidation(int venting) {
        this.venting = Math.max(0, venting);
    }

    public void recalculateSuctionForValidation() {
        calculateSuction();
    }

    public void equalizeForValidation() {
        equalizeWithNeighbours();
    }

    public void refreshConnectionState() {
        if (level == null || level.isClientSide
                || !(getBlockState().getBlock() instanceof TCLegacyTubeBlock tubeBlock)) {
            return;
        }
        BlockState current = getBlockState();
        BlockState connected = tubeBlock.connectionState(level, worldPosition, current, this);
        if (connected != current) {
            level.setBlock(worldPosition, connected, Block.UPDATE_CLIENTS);
        }
    }

    private static boolean sameAspect(String first, String second) {
        String left = first == null ? "" : first;
        String right = second == null ? "" : second;
        return left.equals(right);
    }

    private static int colorForAspect(String aspect) {
        if (aspect == null || aspect.isBlank()) {
            return 0xAAAAAA;
        }
        thaumcraft.api.aspects.Aspect resolved = thaumcraft.api.aspects.Aspect.getAspect(aspect);
        return resolved == null ? 0xAAAAAA : resolved.getColor();
    }

    private static int[] booleanArrayToInts(boolean[] values) {
        int[] result = new int[values.length];
        for (int index = 0; index < values.length; index++) {
            result[index] = values[index] ? 1 : 0;
        }
        return result;
    }

    private static int[] byteArrayToInts(byte[] values) {
        int[] result = new int[values.length];
        for (int index = 0; index < values.length; index++) {
            result[index] = values[index];
        }
        return result;
    }

    private static void copyIntsToBooleans(int[] source, boolean[] target, boolean defaultValue) {
        if (source.length != target.length) {
            for (int index = 0; index < target.length; index++) {
                target[index] = defaultValue;
            }
            return;
        }
        for (int index = 0; index < target.length; index++) {
            target[index] = source[index] != 0;
        }
    }

    private static void copyIntsToBytes(int[] source, byte[] target) {
        for (int index = 0; index < target.length; index++) {
            target[index] = index < source.length ? (byte) Math.max(0, Math.min(2, source[index])) : 0;
        }
    }
}

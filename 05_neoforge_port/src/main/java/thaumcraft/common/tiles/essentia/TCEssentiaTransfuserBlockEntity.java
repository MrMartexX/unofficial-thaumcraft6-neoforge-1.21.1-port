package thaumcraft.common.tiles.essentia;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.essentia.TCEssentiaTransportBlock;
import thaumcraft.common.essentia.container.TCAspectSourceContainer;
import thaumcraft.common.essentia.container.TCEssentiaSourceSearch;
import thaumcraft.common.essentia.transport.TCEssentiaCapabilities;
import thaumcraft.common.essentia.transport.TCEssentiaStack;
import thaumcraft.common.essentia.transport.TCEssentiaSuction;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;

/** Legacy TileEssentiaInput/TileEssentiaOutput server behavior. */
public final class TCEssentiaTransfuserBlockEntity extends BlockEntity implements TCEssentiaTransport {
    public static final int RANGE = 16;
    private static final int TRANSFER_INTERVAL = 5;
    private static final int INPUT_SUCTION = 128;

    private final Kind kind;
    private int tickCounter;

    public TCEssentiaTransfuserBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            Kind kind
    ) {
        super(type, pos, state);
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }

    public Direction facing() {
        BlockState state = getBlockState();
        return state.hasProperty(TCEssentiaTransportBlock.FACING)
                ? state.getValue(TCEssentiaTransportBlock.FACING)
                : Direction.UP;
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            TCEssentiaTransfuserBlockEntity transfuser
    ) {
        if (level == null || level.isClientSide || ++transfuser.tickCounter % TRANSFER_INTERVAL != 0) {
            return;
        }
        if (transfuser.kind == Kind.INPUT) {
            transfuser.fillRemoteContainer();
        } else {
            transfuser.fillConnectedTransport();
        }
    }

    @Override
    public boolean isConnectable(Direction face) {
        return face != null && face == facing().getOpposite();
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return kind == Kind.INPUT && isConnectable(face);
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return kind == Kind.OUTPUT && isConnectable(face);
    }

    @Override
    public TCEssentiaSuction getSuction(Direction face) {
        if (kind == Kind.INPUT && isConnectable(face)) {
            return new TCEssentiaSuction("", INPUT_SUCTION);
        }
        return TCEssentiaSuction.NONE;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public TCEssentiaStack getEssentia(Direction face) {
        return TCEssentiaStack.EMPTY;
    }

    @Override
    public int addEssentia(String aspect, int amount, Direction face, boolean simulate) {
        return canInputFrom(face) && amount > 0 ? amount : 0;
    }

    @Override
    public int takeEssentia(String aspect, int amount, Direction face, boolean simulate) {
        return 0;
    }

    private void fillRemoteContainer() {
        if (level == null) {
            return;
        }
        Direction facing = facing();
        TCEssentiaTransport source = adjacentTransport(facing);
        if (source == null || !source.canOutputTo(facing)) {
            return;
        }

        TCEssentiaStack visible = source.getEssentia(facing);
        if (visible.isEmpty()) {
            return;
        }
        TCEssentiaSuction sourceSuction = source.getSuction(facing);
        if (sourceSuction.amount() >= INPUT_SUCTION || INPUT_SUCTION < source.getMinimumSuction()) {
            return;
        }

        Aspect aspect = Aspect.getAspect(visible.aspect());
        if (aspect != null && addToNearestContainer(aspect)) {
            source.takeEssentia(aspect.getTag(), 1, facing, false);
        }
    }

    private void fillConnectedTransport() {
        if (level == null) {
            return;
        }
        Direction facing = facing();
        TCEssentiaTransport target = adjacentTransport(facing);
        if (target == null || !target.canInputFrom(facing)) {
            return;
        }

        TCEssentiaSuction demand = target.getSuction(facing);
        if (demand.amount() <= 0 || demand.aspect().isBlank()) {
            return;
        }
        Aspect aspect = Aspect.getAspect(demand.aspect());
        if (aspect == null || target.addEssentia(aspect.getTag(), 1, facing, true) <= 0) {
            return;
        }
        if (drainNearestContainer(aspect)) {
            target.addEssentia(aspect.getTag(), 1, facing, false);
        }
    }

    private TCEssentiaTransport adjacentTransport(Direction facing) {
        return level == null ? null : level.getCapability(
                TCEssentiaCapabilities.BLOCK,
                worldPosition.relative(facing.getOpposite()),
                facing
        );
    }

    private boolean addToNearestContainer(Aspect aspect) {
        ArrayList<TCAspectSourceContainer> emptyAcceptingContainers = new ArrayList<>();
        for (TCAspectSourceContainer container : containersInLegacyOrder()) {
            if (container.isSourceBlocked() || !container.doesContainerAccept(aspect)) {
                continue;
            }
            AspectList stored = container.storedAspects();
            if (stored == null || stored.visSize() == 0) {
                emptyAcceptingContainers.add(container);
                continue;
            }
            if (container.addToContainer(aspect, 1) == 0) {
                return true;
            }
        }
        for (TCAspectSourceContainer container : emptyAcceptingContainers) {
            if (!container.isSourceBlocked()
                    && container.doesContainerAccept(aspect)
                    && container.addToContainer(aspect, 1) == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean drainNearestContainer(Aspect aspect) {
        for (TCAspectSourceContainer container : containersInLegacyOrder()) {
            if (container.isSourceBlocked() || container.drainAspect(aspect, 1, true) != 1) {
                continue;
            }
            return container.drainAspect(aspect, 1, false) == 1;
        }
        return false;
    }

    private List<TCAspectSourceContainer> containersInLegacyOrder() {
        if (level == null) {
            return List.of();
        }
        return TCEssentiaSourceSearch.discover(level, worldPosition, facing(), RANGE).stream()
                .map(level::getBlockEntity)
                .filter(TCAspectSourceContainer.class::isInstance)
                .map(TCAspectSourceContainer.class::cast)
                .toList();
    }

    public enum Kind {
        INPUT,
        OUTPUT
    }
}

package thaumcraft.common.essentia.transport.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.TCEssentiaItemHelper;
import thaumcraft.common.essentia.transport.TCEssentiaCapabilities;
import thaumcraft.common.essentia.transport.blockentity.TCLegacyTubeBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSounds;

/** Block shell and connection geometry for the TC6 tube family. */
public class TCLegacyTubeBlock extends Block implements EntityBlock {
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;

    private static final VoxelShape NORMAL_CORE = box(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);
    private static final VoxelShape BUFFER_CORE = box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D);
    private static final VoxelShape DOWN_ARM = box(6.0D, 0.0D, 6.0D, 10.0D, 5.0D, 10.0D);
    private static final VoxelShape UP_ARM = box(6.0D, 11.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    private static final VoxelShape NORTH_ARM = box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 5.0D);
    private static final VoxelShape SOUTH_ARM = box(6.0D, 6.0D, 11.0D, 10.0D, 10.0D, 16.0D);
    private static final VoxelShape WEST_ARM = box(0.0D, 6.0D, 6.0D, 5.0D, 10.0D, 10.0D);
    private static final VoxelShape EAST_ARM = box(11.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

    private final TCLegacyTubeVariant variant;

    public TCLegacyTubeBlock(TCLegacyTubeVariant variant, BlockBehaviour.Properties properties) {
        super(properties);
        this.variant = variant;
        registerDefaultState(stateDefinition.any()
                .setValue(DOWN, false)
                .setValue(UP, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false));
    }

    public TCLegacyTubeVariant variant() {
        return variant;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TCBlockEntities.createTubeBlockEntity(variant, pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (type != TCBlockEntities.typeForTube(variant)) {
            return null;
        }
        if (level.isClientSide) {
            return (tickerLevel, pos, tickerState, blockEntity) ->
                    TCLegacyTubeBlockEntity.clientTick(
                            tickerLevel,
                            pos,
                            tickerState,
                            (TCLegacyTubeBlockEntity) blockEntity
                    );
        }
        return (tickerLevel, pos, tickerState, blockEntity) ->
                TCLegacyTubeBlockEntity.serverTick(
                        tickerLevel,
                        pos,
                        tickerState,
                        (TCLegacyTubeBlockEntity) blockEntity
                );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer != null && level.getBlockEntity(pos) instanceof TCLegacyTubeBlockEntity tube) {
            tube.setFacing(legacyPlacementFacing(pos, placer));
            tube.refreshConnectionState();
        }
    }

    public BlockState connectionState(
            Level level,
            BlockPos pos,
            BlockState state,
            TCLegacyTubeBlockEntity tube
    ) {
        BlockState updated = state;
        for (Direction direction : Direction.values()) {
            boolean connected = tube.isConnectable(direction)
                    && level.getCapability(
                            TCEssentiaCapabilities.BLOCK,
                            pos.relative(direction),
                            direction.getOpposite()
                    ) != null;
            updated = updated.setValue(property(direction), connected);
        }
        return updated;
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        VoxelShape shape = variant == TCLegacyTubeVariant.BUFFER ? BUFFER_CORE : NORMAL_CORE;
        if (state.getValue(DOWN)) shape = Shapes.or(shape, DOWN_ARM);
        if (state.getValue(UP)) shape = Shapes.or(shape, UP_ARM);
        if (state.getValue(NORTH)) shape = Shapes.or(shape, NORTH_ARM);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_ARM);
        if (state.getValue(WEST)) shape = Shapes.or(shape, WEST_ARM);
        if (state.getValue(EAST)) shape = Shapes.or(shape, EAST_ARM);
        return shape;
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (!(level.getBlockEntity(pos) instanceof TCLegacyTubeBlockEntity tube)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (isCaster(stack)) {
            TubeSubHit subHit = traceSubHit(tube, pos, hitResult);
            if (subHit.kind == TubeSubHit.Kind.SIDE) {
                if (!level.isClientSide && tube.casterToggleSide(subHit.side, player.isShiftKeyDown())) {
                    level.playSound(
                            null,
                            pos,
                            tube.variant() == TCLegacyTubeVariant.BUFFER && player.isShiftKeyDown()
                                    ? TCSounds.SQUEEK.get()
                                    : TCSounds.TOOL.get(),
                            SoundSource.BLOCKS,
                            tube.variant() == TCLegacyTubeVariant.BUFFER && player.isShiftKeyDown() ? 0.6F : 0.5F,
                            tube.variant() == TCLegacyTubeVariant.BUFFER && player.isShiftKeyDown()
                                    ? 2.0F + level.random.nextFloat() * 0.2F
                                    : 0.9F + level.random.nextFloat() * 0.2F
                    );
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            if (subHit.kind == TubeSubHit.Kind.CENTER) {
                if (!level.isClientSide && tube.casterRotateCenter()) {
                    level.playSound(null, pos, TCSounds.TOOL.get(), SoundSource.BLOCKS, 0.5F, 0.9F + level.random.nextFloat() * 0.2F);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        if (tube.variant() == TCLegacyTubeVariant.FILTER) {
            if (player.isShiftKeyDown() && !tube.filterAspect().isBlank()) {
                if (!level.isClientSide) {
                    tube.setFilterAspect("");
                    level.playSound(null, pos, TCSounds.KEY.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            Aspect aspect = TCEssentiaItemHelper.aspectFromStack(stack);
            if (aspect != null && tube.filterAspect().isBlank()) {
                if (!level.isClientSide) {
                    tube.setFilterAspect(aspect.getTag());
                    level.playSound(null, pos, TCSounds.KEY.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (!(level.getBlockEntity(pos) instanceof TCLegacyTubeBlockEntity tube)) {
            return InteractionResult.PASS;
        }
        if (tube.variant() == TCLegacyTubeVariant.VALVE) {
            if (!level.isClientSide) {
                tube.setAllowFlow(!tube.allowsFlow());
                level.playSound(null, pos, TCSounds.SQUEEK.get(), SoundSource.BLOCKS, 0.7F, 0.9F + level.random.nextFloat() * 0.2F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (tube.variant() == TCLegacyTubeVariant.FILTER && player.isShiftKeyDown() && !tube.filterAspect().isBlank()) {
            if (!level.isClientSide) {
                tube.setFilterAspect("");
                level.playSound(null, pos, TCSounds.KEY.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return variant == TCLegacyTubeVariant.BUFFER;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (variant != TCLegacyTubeVariant.BUFFER
                || !(level.getBlockEntity(pos) instanceof TCLegacyTubeBlockEntity tube)) {
            return 0;
        }
        int amount = tube.transportNode().storage().totalAmount();
        return amount <= 0 ? 0 : Math.min(15, 1 + (int) Math.floor(amount / 10.0D * 14.0D));
    }

    private static boolean isCaster(ItemStack stack) {
        return stack.is(TCItems.CASTER_BASIC.get());
    }

    private TubeSubHit traceSubHit(TCLegacyTubeBlockEntity tube, BlockPos pos, BlockHitResult hitResult) {
        Vec3 local = hitResult.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        boolean buffer = tube.variant() == TCLegacyTubeVariant.BUFFER;
        double min = buffer ? 0.375D : 0.375D;
        double max = buffer ? 0.625D : 0.625D;
        double sideEnd = buffer ? 0.5D : 0.375D;
        for (Direction direction : Direction.values()) {
            if (tube.hasConnectableNeighbour(direction) && containsSide(direction, local, min, max, sideEnd)) {
                return TubeSubHit.side(direction);
            }
        }
        double centerMin = buffer ? 0.25D : 0.375D;
        double centerMax = buffer ? 0.75D : 0.625D;
        if (between(local.x, centerMin, centerMax)
                && between(local.y, centerMin, centerMax)
                && between(local.z, centerMin, centerMax)) {
            return TubeSubHit.center();
        }
        return TubeSubHit.side(hitResult.getDirection());
    }

    private static boolean containsSide(Direction direction, Vec3 local, double min, double max, double sideEnd) {
        return switch (direction) {
            case DOWN -> between(local.x, min, max) && between(local.z, min, max) && between(local.y, 0.0D, sideEnd);
            case UP -> between(local.x, min, max) && between(local.z, min, max) && between(local.y, 1.0D - sideEnd, 1.0D);
            case NORTH -> between(local.x, min, max) && between(local.y, min, max) && between(local.z, 0.0D, sideEnd);
            case SOUTH -> between(local.x, min, max) && between(local.y, min, max) && between(local.z, 1.0D - sideEnd, 1.0D);
            case WEST -> between(local.y, min, max) && between(local.z, min, max) && between(local.x, 0.0D, sideEnd);
            case EAST -> between(local.y, min, max) && between(local.z, min, max) && between(local.x, 1.0D - sideEnd, 1.0D);
        };
    }

    private static boolean between(double value, double min, double max) {
        return value >= min - 1.0E-4D && value <= max + 1.0E-4D;
    }

    private record TubeSubHit(Kind kind, Direction side) {
        private static TubeSubHit side(Direction side) {
            return new TubeSubHit(Kind.SIDE, side);
        }

        private static TubeSubHit center() {
            return new TubeSubHit(Kind.CENTER, null);
        }

        private enum Kind {
            SIDE,
            CENTER
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DOWN, UP, NORTH, SOUTH, WEST, EAST);
    }

    private static BooleanProperty property(Direction direction) {
        return switch (direction) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    private static Direction legacyPlacementFacing(BlockPos pos, LivingEntity placer) {
        if (Math.abs(placer.getX() - (pos.getX() + 0.5D)) < 2.0D
                && Math.abs(placer.getZ() - (pos.getZ() + 0.5D)) < 2.0D) {
            double eyeY = placer.getY() + placer.getEyeHeight();
            if (eyeY - pos.getY() > 2.0D) {
                return Direction.UP;
            }
            if (pos.getY() - eyeY > 0.0D) {
                return Direction.DOWN;
            }
        }
        return placer.getDirection().getOpposite();
    }
}

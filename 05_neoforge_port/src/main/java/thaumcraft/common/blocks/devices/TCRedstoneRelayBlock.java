package thaumcraft.common.blocks.devices;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.tiles.devices.TCRedstoneRelayBlockEntity;

public class TCRedstoneRelayBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<TCRedstoneRelayBlock> CODEC = simpleCodec(TCRedstoneRelayBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");

    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

    public TCRedstoneRelayBlock(BlockBehaviour.Properties properties) {
        super(properties.sound(SoundType.WOOD).strength(0.0F));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ENABLED, false));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isSolid();
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ENABLED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCRedstoneRelayBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof TCRedstoneRelayBlockEntity relay)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        // Determine if clicked front (output) or back (input) dial based on facing
        Direction facing = state.getValue(FACING);
        double hitX = hit.getLocation().x - pos.getX();
        double hitZ = hit.getLocation().z - pos.getZ();
        double frontDist = switch (facing) {
            case NORTH -> 1.0 - hitZ;
            case SOUTH -> hitZ;
            case WEST -> 1.0 - hitX;
            case EAST -> hitX;
            default -> 0.5;
        };

        level.playSound(null, pos, TCSounds.KEY.get(), SoundSource.BLOCKS, 0.5F, 1.0F);

        if (frontDist >= 0.5) {
            relay.increaseOut();
            player.displayClientMessage(Component.literal("Output strength: " + relay.getOut()), true);
        } else {
            relay.increaseIn();
            player.displayClientMessage(Component.literal("Input threshold: " + relay.getIn()), true);
        }

        updateRelayState(level, pos, state, relay);
        return ItemInteractionResult.CONSUME;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TCRedstoneRelayBlockEntity relay) {
                updateRelayState(level, pos, state, relay);
            }
        }
    }

    private void updateRelayState(Level level, BlockPos pos, BlockState state, TCRedstoneRelayBlockEntity relay) {
        Direction facing = state.getValue(FACING);
        Direction rear = facing.getOpposite();
        int inputSignal = level.getSignal(pos.relative(rear), rear);
        boolean shouldBeEnabled = inputSignal >= relay.getIn();

        if (shouldBeEnabled != state.getValue(ENABLED)) {
            level.setBlock(pos, state.setValue(ENABLED, shouldBeEnabled), 3);
            level.updateNeighborsAt(pos, this);
            level.updateNeighborsAt(pos.relative(facing), this);
        }
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (!state.getValue(ENABLED)) {
            return 0;
        }
        Direction facing = state.getValue(FACING);
        if (direction == facing.getOpposite()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TCRedstoneRelayBlockEntity relay) {
                return relay.getOut();
            }
            return 15;
        }
        return 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getSignal(state, level, pos, direction);
    }
}

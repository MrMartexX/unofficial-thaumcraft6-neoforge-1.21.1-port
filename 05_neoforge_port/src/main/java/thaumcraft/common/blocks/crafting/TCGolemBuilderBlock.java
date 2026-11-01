package thaumcraft.common.blocks.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.tiles.crafting.TCGolemBuilderBlockEntity;

/**
 * Controller block for the Golem Press multiblock.
 * Handles multiblock teardown, GUI access, and rollback of placeholder components.
 */
public final class TCGolemBuilderBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static boolean destroyingStructure = false;

    public TCGolemBuilderBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCGolemBuilderBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != TCBlockEntities.GOLEM_BUILDER.get()) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof TCGolemBuilderBlockEntity builder) {
                if (lvl.isClientSide) {
                    TCGolemBuilderBlockEntity.clientTick(lvl, pos, st, builder);
                } else {
                    TCGolemBuilderBlockEntity.serverTick(lvl, pos, st, builder);
                }
            }
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MenuProvider provider) {
                player.openMenu(provider);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction playerFacing = context.getHorizontalDirection();
        boolean reversed = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        return defaultBlockState().setValue(FACING, reversed ? playerFacing : playerFacing.getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public static boolean isDestroyingStructure() {
        return destroyingStructure;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !destroyingStructure && !level.isClientSide) {
            destroyStructure(level, pos, state);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    public static void destroyStructure(Level level, BlockPos controllerPos, BlockState state) {
        if (level.isClientSide || destroyingStructure) {
            return;
        }

        destroyingStructure = true;
        try {
            // Revert controller position back to upward-facing piston
            if (level.getBlockState(controllerPos).is(TCBlocks.GOLEM_BUILDER.get())) {
                level.setBlock(controllerPos, Blocks.PISTON.defaultBlockState()
                        .setValue(BlockStateProperties.FACING, Direction.UP), Block.UPDATE_ALL);
            }

            // Scan adjacent multiblock area for placeholders and revert each to original block
            for (BlockPos target : BlockPos.betweenClosed(controllerPos.offset(-2, -1, -2), controllerPos.offset(2, 1, 2))) {
                BlockState targetState = level.getBlockState(target);
                if (targetState.is(TCBlocks.PLACEHOLDER_BARS.get())) {
                    level.setBlock(target, Blocks.IRON_BARS.defaultBlockState(), Block.UPDATE_ALL);
                } else if (targetState.is(TCBlocks.PLACEHOLDER_ANVIL.get())) {
                    level.setBlock(target, Blocks.ANVIL.defaultBlockState(), Block.UPDATE_ALL);
                } else if (targetState.is(TCBlocks.PLACEHOLDER_CAULDRON.get())) {
                    level.setBlock(target, Blocks.CAULDRON.defaultBlockState(), Block.UPDATE_ALL);
                } else if (targetState.is(TCBlocks.PLACEHOLDER_TABLE.get())) {
                    level.setBlock(target, TCBlocks.TABLE_STONE.get().defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        } finally {
            destroyingStructure = false;
        }
    }
}

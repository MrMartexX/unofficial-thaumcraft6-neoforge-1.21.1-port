package thaumcraft.common.blocks.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.tiles.crafting.TCInfusionPedestalBlockEntity;
import thaumcraft.common.blocks.devices.TCInlayNetwork;
import thaumcraft.common.crafting.infusion.TCInfusionSurroundingsInvalidator;

public class TCInfusionPedestalBlock extends Block implements EntityBlock, IInfusionStabiliserExt {
    public static final IntegerProperty CHARGE = IntegerProperty.create("charge", 0, 15);
    private static final VoxelShape SHAPE = Shapes.or(
            box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D),
            box(2.0D, 12.0D, 2.0D, 14.0D, 16.0D, 14.0D)
    );

    public TCInfusionPedestalBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(CHARGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGE);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level instanceof ServerLevel serverLevel) {
            TCInlayNetwork.recalculateAround(serverLevel, pos);
            TCInfusionSurroundingsInvalidator.requestNearby(serverLevel, pos);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean movedByPiston) {
        if (level instanceof ServerLevel serverLevel) {
            TCInlayNetwork.recalculateAround(serverLevel, pos);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCInfusionPedestalBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof TCInfusionPedestalBlockEntity pedestal)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!pedestal.getStoredStack().isEmpty()) {
            extractStored(level, pos, pedestal);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide && pedestal.insertOne(stack)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            playPickupSound(level, pos);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof TCInfusionPedestalBlockEntity pedestal) || pedestal.getStoredStack().isEmpty()) {
            return InteractionResult.PASS;
        }
        extractStored(level, pos, pedestal);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof TCInfusionPedestalBlockEntity pedestal) {
            pedestal.dropContents(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel serverLevel) {
            TCInlayNetwork.recalculateAround(serverLevel, pos);
            TCInfusionSurroundingsInvalidator.requestNearby(serverLevel, pos);
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canStabaliseInfusion(net.minecraft.world.level.LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    public float getStabilizationAmount(net.minecraft.world.level.LevelReader level, BlockPos pos) {
        return level.getBlockState(pos).is(TCBlocks.ELDRITCH_PEDESTAL.get()) ? 0.1F : 0.0F;
    }

    @Override
    public boolean hasSymmetryPenalty(net.minecraft.world.level.LevelReader level, BlockPos pos1, BlockPos pos2) {
        BlockEntity first = level.getBlockEntity(pos1);
        BlockEntity second = level.getBlockEntity(pos2);
        if (first instanceof TCInfusionPedestalBlockEntity firstPedestal
                && second instanceof TCInfusionPedestalBlockEntity secondPedestal) {
            return firstPedestal.getStoredStack().isEmpty() != secondPedestal.getStoredStack().isEmpty();
        }
        return false;
    }

    @Override
    public float getSymmetryPenalty(net.minecraft.world.level.LevelReader level, BlockPos pos) {
        return 0.1F;
    }

    private static void extractStored(Level level, BlockPos pos, TCInfusionPedestalBlockEntity pedestal) {
        if (!level.isClientSide) {
            pedestal.dropStored(level, pos);
            playPickupSound(level, pos);
        }
    }

    private static void playPickupSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2F, 1.0F);
    }
}

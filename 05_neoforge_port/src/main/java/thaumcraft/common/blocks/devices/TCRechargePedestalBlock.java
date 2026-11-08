package thaumcraft.common.blocks.devices;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.items.TCRechargeHelper;
import thaumcraft.common.lib.fx.TCFXDispatcher;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.tiles.devices.TCRechargePedestalBlockEntity;

public class TCRechargePedestalBlock extends BaseEntityBlock {
    public static final MapCodec<TCRechargePedestalBlock> CODEC = simpleCodec(TCRechargePedestalBlock::new);

    private static final VoxelShape SHAPE = Shapes.or(
            box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D),
            box(2.0D, 12.0D, 2.0D, 14.0D, 16.0D, 14.0D)
    );

    public TCRechargePedestalBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCRechargePedestalBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, TCBlockEntities.RECHARGE_PEDESTAL.get(), TCRechargePedestalBlockEntity::serverTick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof TCRechargePedestalBlockEntity pedestal)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!pedestal.getStoredStack().isEmpty()) {
            extractStored(level, pos, pedestal, player);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.isEmpty() || !TCRechargeHelper.canRecharge(stack)) {
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
        if (!(level.getBlockEntity(pos) instanceof TCRechargePedestalBlockEntity pedestal) || pedestal.getStoredStack().isEmpty()) {
            return InteractionResult.PASS;
        }
        extractStored(level, pos, pedestal, player);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void extractStored(Level level, BlockPos pos, TCRechargePedestalBlockEntity pedestal, Player player) {
        if (!level.isClientSide) {
            ItemStack extracted = pedestal.extractStored();
            if (!extracted.isEmpty()) {
                if (!player.getInventory().add(extracted)) {
                    Containers.dropItemStack(level, player.getX(), player.getY(), player.getZ(), extracted);
                }
                playPickupSound(level, pos);
            }
        }
    }

    private static void playPickupSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2F, 1.0F);
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        if (id == 5) {
            if (level.isClientSide()) {
                RandomSource rand = level.random;
                double x = pos.getX() + rand.nextInt(3) - rand.nextInt(3) + 0.5D;
                double y = pos.getY() + 1 + rand.nextInt(3);
                double z = pos.getZ() + rand.nextInt(3) - rand.nextInt(3) + 0.5D;
                double tx = pos.getX() + 0.5D;
                double ty = pos.getY() + 1.2D;
                double tz = pos.getZ() + 0.5D;
                TCFXDispatcher.visSparkle(level, x, y, z, tx, ty, tz, param);
            }
            return true;
        }
        return super.triggerEvent(state, level, pos, id, param);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof TCRechargePedestalBlockEntity pedestal) {
            pedestal.dropContents(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}

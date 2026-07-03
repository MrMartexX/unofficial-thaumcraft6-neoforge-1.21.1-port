package thaumcraft.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.tiles.devices.TCInfernalFurnaceBlockEntity;

/** Legacy Infernal Furnace shell: horizontal facing, half-height collision and item/living contact handling. */
public final class TCInfernalFurnaceBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);

    public TCInfernalFurnaceBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCInfernalFurnaceBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != TCBlockEntities.INFERNAL_FURNACE.get()) {
            return null;
        }
        return (tickerLevel, pos, tickerState, blockEntity) ->
                TCInfernalFurnaceBlockEntity.serverTick(
                        tickerLevel,
                        pos,
                        tickerState,
                        (TCInfernalFurnaceBlockEntity) blockEntity
                );
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
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        nudgeTowardCenter(pos, entity);
        if (!level.isClientSide && entity.tickCount % 10 == 0) {
            if (entity instanceof ItemEntity itemEntity) {
                Vec3 motion = itemEntity.getDeltaMovement();
                itemEntity.setDeltaMovement(motion.x, 0.02500000037252903D, motion.z);
                if (itemEntity.onGround()
                        && level.getBlockEntity(pos) instanceof TCInfernalFurnaceBlockEntity furnace) {
                    itemEntity.setItem(furnace.addItemsToInventory(itemEntity.getItem()));
                    if (itemEntity.getItem().isEmpty()) {
                        itemEntity.discard();
                    }
                }
            } else if (entity instanceof LivingEntity living && !living.fireImmune()) {
                living.hurt(level.damageSources().lava(), 3.0F);
                living.igniteForSeconds(10.0F);
            }
        }
        super.entityInside(state, level, pos, entity);
    }

    private static void nudgeTowardCenter(BlockPos pos, Entity entity) {
        Vec3 motion = entity.getDeltaMovement();
        double x = motion.x;
        double z = motion.z;
        if (entity.getX() < pos.getX() + 0.3F) {
            x += 9.999999747378752E-5D;
        }
        if (entity.getX() > pos.getX() + 0.7F) {
            x -= 9.999999747378752E-5D;
        }
        if (entity.getZ() < pos.getZ() + 0.3F) {
            z += 9.999999747378752E-5D;
        }
        if (entity.getZ() > pos.getZ() + 0.7F) {
            z -= 9.999999747378752E-5D;
        }
        if (x != motion.x || z != motion.z) {
            entity.setDeltaMovement(x, motion.y, z);
        }
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}

package thaumcraft.common.blocks.basic;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.TCEssentiaItemHelper;
import thaumcraft.common.tiles.misc.TCBannerBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;

/** Port of legacy BlockBannerTC. {@code dye == null} is the Crimson Cult banner. */
public class TCBannerBlock extends Block implements EntityBlock {
    private static final VoxelShape POLE = box(5.28D, 0.0D, 5.28D, 10.56D, 16.0D, 10.56D);
    private static final VoxelShape WALL_0 = box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
    private static final VoxelShape WALL_4 = box(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape WALL_8 = box(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape WALL_12 = box(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);

    @Nullable
    private final DyeColor dye;

    public TCBannerBlock(BlockBehaviour.Properties properties, @Nullable DyeColor dye) {
        super(properties);
        this.dye = dye;
    }

    @Nullable
    public DyeColor dye() {
        return dye;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCBannerBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!(level.getBlockEntity(pos) instanceof TCBannerBlockEntity banner)) {
            return Shapes.block();
        }
        if (!banner.getWall()) {
            return POLE;
        }
        return switch (banner.getBannerFacing()) {
            case 0 -> WALL_0;
            case 4 -> WALL_4;
            case 8 -> WALL_8;
            case 12 -> WALL_12;
            default -> Shapes.block();
        };
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (dye == null || !(level.getBlockEntity(pos) instanceof TCBannerBlockEntity banner)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (TCEssentiaItemHelper.isFilledPhial(stack)) {
            if (!level.isClientSide) {
                Aspect aspect = TCEssentiaItemHelper.aspectFromStack(stack);
                if (aspect != null) {
                    banner.setAspect(aspect);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (dye != null && player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof TCBannerBlockEntity banner) {
            if (!level.isClientSide) {
                banner.setAspect(null);
                level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        if (!level.isClientSide && !isSupported(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    private static boolean isSupported(LevelReader level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof TCBannerBlockEntity banner)) {
            return true;
        }
        Direction supportDir = Direction.DOWN;
        if (banner.getWall()) {
            supportDir = switch (banner.getBannerFacing()) {
                case 0 -> Direction.NORTH;
                case 4 -> Direction.EAST;
                case 8 -> Direction.SOUTH;
                case 12 -> Direction.WEST;
                default -> Direction.DOWN;
            };
        }
        BlockPos support = pos.relative(supportDir);
        return level.getBlockState(support).isFaceSturdy(level, support, supportDir.getOpposite());
    }

    public ItemStack stackFor(@Nullable TCBannerBlockEntity banner) {
        ItemStack stack = new ItemStack(this);
        if (banner != null && banner.getAspect() != null) {
            TCEssentiaItemHelper.setFilter(stack, banner.getAspect());
        }
        return stack;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return stackFor(level.getBlockEntity(pos) instanceof TCBannerBlockEntity banner ? banner : null);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        return List.of(stackFor(be instanceof TCBannerBlockEntity banner ? banner : null));
    }

    public static boolean registeredType(BlockEntity be) {
        return be.getType() == TCBlockEntities.BANNER.get();
    }
}

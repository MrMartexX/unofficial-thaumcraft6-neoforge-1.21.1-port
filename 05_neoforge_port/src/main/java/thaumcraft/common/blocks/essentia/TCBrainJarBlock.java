package thaumcraft.common.blocks.essentia;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.items.TCBrainJarBlockItem;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.tiles.devices.TCBrainJarBlockEntity;

public final class TCBrainJarBlock extends Block implements EntityBlock {
    public static final VoxelShape SHAPE = box(3.0D, 0.0D, 3.0D, 13.0D, 12.0D, 13.0D);

    public TCBrainJarBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCBrainJarBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != TCBlockEntities.JAR_BRAIN.get()) {
            return null;
        }
        return (tickerLevel, pos, tickerState, blockEntity) ->
                TCBrainJarBlockEntity.serverTick(tickerLevel, pos, tickerState, (TCBrainJarBlockEntity) blockEntity);
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
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        return releaseExperience(level, pos);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof TCBrainJarBlockEntity)) {
            return InteractionResult.PASS;
        }
        releaseExperience(level, pos);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private ItemInteractionResult releaseExperience(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof TCBrainJarBlockEntity jar)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            jar.releaseExperience();
            level.playSound(null, pos, TCSounds.JAR.get(), SoundSource.BLOCKS, 0.2F, 1.0F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof TCBrainJarBlockEntity jar) {
            return List.of(TCBrainJarBlockItem.stackFromJar(jar));
        }
        return super.getDrops(state, params);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof TCBrainJarBlockEntity jar ? jar.comparatorSignal() : 0;
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof TCBrainJarBlockEntity ? 5.0F : super.getEnchantPowerBonus(state, level, pos);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof TCBrainJarBlockEntity jar && jar.xp() >= TCBrainJarBlockEntity.XP_MAX) {
            level.addParticle(
                    ParticleTypes.ENCHANT,
                    pos.getX() + 0.5D,
                    pos.getY() + 0.8D,
                    pos.getZ() + 0.5D,
                    (random.nextDouble() - 0.5D) * 0.05D,
                    random.nextDouble() * 0.05D,
                    (random.nextDouble() - 0.5D) * 0.05D
            );
        }
    }
}

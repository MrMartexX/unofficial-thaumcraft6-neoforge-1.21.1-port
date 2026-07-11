package thaumcraft.common.blocks.world.taint;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCMobEffects;

/** Finite legacy Flux Goo state used by infusion and taint world behavior. */
public final class TCFluxGooBlock extends Block {
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 7);
    public static final MapCodec<TCFluxGooBlock> CODEC = simpleCodec(TCFluxGooBlock::new);

    public TCFluxGooBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(LEVEL, 7));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        int gooLevel = state.getValue(LEVEL);
        double retainedMotion = 1.0D - (gooLevel + 1) / 8.0D;
        Vec3 motion = entity.getDeltaMovement();
        entity.setDeltaMovement(motion.x * retainedMotion, motion.y, motion.z * retainedMotion);
        if (!level.isClientSide && entity instanceof LivingEntity living) {
            MobEffectInstance effect = new MobEffectInstance(
                    TCMobEffects.VIS_EXHAUST,
                    600,
                    gooLevel / 3,
                    true,
                    true
            );
            effect.getCures().clear();
            living.addEffect(effect);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(4) != 0) {
            return;
        }
        int gooLevel = state.getValue(LEVEL);
        if (gooLevel > 0) {
            level.setBlock(pos, state.setValue(LEVEL, gooLevel - 1), Block.UPDATE_CLIENTS);
            AuraHelper.polluteAura(level, pos, 1.0F, true);
        } else if (random.nextBoolean()) {
            AuraHelper.polluteAura(level, pos, 1.0F, true);
            level.removeBlock(pos, false);
        } else {
            level.setBlock(pos, taintFibreState(level, pos), Block.UPDATE_CLIENTS);
        }
    }

    public BlockState taintFibreState(Level level, BlockPos pos) {
        if (TCBlocks.TAINT_FIBRE.get() instanceof TCTaintFibreBlock taintFibre) {
            return taintFibre.stateForWorld(level, pos);
        }
        return TCBlocks.TAINT_FIBRE.get().defaultBlockState();
    }

    @Override
    protected boolean canBeReplaced(BlockState state, net.minecraft.world.item.context.BlockPlaceContext context) {
        return state.getValue(LEVEL) < 4 || super.canBeReplaced(state, context);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return box(0.0D, 0.0D, 0.0D, 16.0D, (state.getValue(LEVEL) + 1) * 2.0D, 16.0D);
    }
}

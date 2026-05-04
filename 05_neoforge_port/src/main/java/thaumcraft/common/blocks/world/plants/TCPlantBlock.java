package thaumcraft.common.blocks.world.plants;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TCPlantBlock extends BushBlock {
    public static final MapCodec<TCPlantBlock> CODEC = simpleCodec(properties ->
            new TCPlantBlock(Kind.SAPLING, properties)
    );

    private static final VoxelShape SAPLING_SHAPE = Block.box(
            1.6D, 0.0D, 1.6D,
            14.4D, 12.8D, 14.4D
    );

    private static final VoxelShape SHIMMERLEAF_SHAPE = Block.box(
            2.0D, 0.0D, 2.0D,
            14.0D, 12.0D, 14.0D
    );

    private static final VoxelShape CINDERPEARL_SHAPE = Block.box(
            2.0D, 0.0D, 2.0D,
            14.0D, 13.0D, 14.0D
    );

    private static final VoxelShape VISHROOM_SHAPE = Block.box(
            3.0D, 0.0D, 3.0D,
            13.0D, 10.0D, 13.0D
    );

    public enum Kind {
        SAPLING,
        SHIMMERLEAF,
        CINDERPEARL,
        VISHROOM
    }

    private final Kind kind;

    public TCPlantBlock(Kind kind, BlockBehaviour.Properties properties) {
        super(properties);
        this.kind = kind;
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (kind) {
            case SAPLING -> SAPLING_SHAPE;
            case SHIMMERLEAF -> SHIMMERLEAF_SHAPE;
            case CINDERPEARL -> CINDERPEARL_SHAPE;
            case VISHROOM -> VISHROOM_SHAPE;
        };
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return switch (kind) {
            case SHIMMERLEAF -> isLegacyGrassOrDirt(state);

            case CINDERPEARL -> state.is(Blocks.SAND)
                    || state.is(Blocks.RED_SAND)
                    || isLegacyDirt(state)
                    || state.is(Blocks.TERRACOTTA)
                    || state.is(BlockTags.TERRACOTTA);

            case VISHROOM -> state.is(Blocks.MYCELIUM)
                    || state.is(Blocks.PODZOL)
                    || state.is(Blocks.MOSS_BLOCK)
                    || state.is(Blocks.STONE)
                    || state.is(Blocks.DEEPSLATE)
                    || state.is(Blocks.TUFF)
                    || state.isFaceSturdy(level, pos, Direction.UP);

            case SAPLING -> isLegacyGrassOrDirt(state)
                    || state.is(Blocks.PODZOL)
                    || state.is(Blocks.MOSS_BLOCK);
        };
    }

    private static boolean isLegacyGrassOrDirt(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || isLegacyDirt(state);
    }

    private static boolean isLegacyDirt(BlockState state) {
        return state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.ROOTED_DIRT)
                || state.is(Blocks.PODZOL);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (kind == Kind.CINDERPEARL && random.nextBoolean()) {
            double x = pos.getX() + 0.5D + (random.nextDouble() - random.nextDouble()) * 0.1D;
            double y = pos.getY() + 0.6D + (random.nextDouble() - random.nextDouble()) * 0.1D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - random.nextDouble()) * 0.1D;

            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
            level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
        }

        if (kind == Kind.SHIMMERLEAF && random.nextInt(3) == 0) {
            double x = pos.getX() + 0.5D + random.nextGaussian() * 0.1D;
            double y = pos.getY() + 0.4D + random.nextGaussian() * 0.1D;
            double z = pos.getZ() + 0.5D + random.nextGaussian() * 0.1D;

            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0D, 0.01D, 0.0D);
        }

        if (kind == Kind.VISHROOM && random.nextInt(3) == 0) {
            double x = pos.getX() + 0.5D + (random.nextDouble() - random.nextDouble()) * 0.4D;
            double y = pos.getY() + 0.3D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - random.nextDouble()) * 0.4D;

            level.addParticle(ParticleTypes.WITCH, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        if (kind == Kind.VISHROOM
                && !level.isClientSide()
                && entity instanceof LivingEntity living
                && level.random.nextInt(5) == 0) {
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        }
    }
}
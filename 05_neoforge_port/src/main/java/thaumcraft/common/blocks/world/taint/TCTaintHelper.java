package thaumcraft.common.blocks.world.taint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.config.TCConfig;
import thaumcraft.common.entities.TCTaintSeedEntity;
import thaumcraft.common.registry.TCBlocks;

/** Runtime taint seed registry and TC6-style fibre/terrain spread rules. */
public final class TCTaintHelper {
    private static final Map<ResourceKey<Level>, Set<BlockPos>> TAINT_SEEDS = new ConcurrentHashMap<>();
    private static final float LEGACY_DEFAULT_TAINT_SPREAD_RATE = 100.0F;
    private static final int LEGACY_DEFAULT_TAINT_SPREAD_AREA = 32;

    private TCTaintHelper() {
    }

    public static void addTaintSeed(Level level, BlockPos pos) {
        if (level == null) {
            return;
        }
        TAINT_SEEDS.computeIfAbsent(level.dimension(), ignored -> ConcurrentHashMap.newKeySet()).add(pos.immutable());
    }

    public static void removeTaintSeed(Level level, BlockPos pos) {
        if (level == null) {
            return;
        }
        Set<BlockPos> seeds = TAINT_SEEDS.get(level.dimension());
        if (seeds != null) {
            seeds.remove(pos);
        }
    }

    public static void clearForValidation(Level level) {
        if (level != null) {
            TAINT_SEEDS.remove(level.dimension());
        }
    }

    public static boolean isNearTaintSeed(LevelReader reader, BlockPos pos) {
        if (!(reader instanceof Level level)) {
            return false;
        }
        double area = taintSpreadArea();
        area *= area;
        Set<BlockPos> seeds = TAINT_SEEDS.get(level.dimension());
        if (seeds == null || seeds.isEmpty()) {
            return false;
        }
        for (BlockPos seed : new ArrayList<>(seeds)) {
            if (seed.distSqr(pos) <= area) {
                if (level.getEntitiesOfClass(TCTaintSeedEntity.class, new AABB(seed).inflate(1.0D)).isEmpty()) {
                    removeTaintSeed(level, seed);
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean isAtTaintSeedEdge(Level level, BlockPos pos) {
        double area = taintSpreadArea();
        double fringe = area * 0.8D;
        area *= area;
        fringe *= fringe;
        Set<BlockPos> seeds = TAINT_SEEDS.get(level.dimension());
        if (seeds == null || seeds.isEmpty()) {
            return false;
        }
        for (BlockPos seed : seeds) {
            double distance = seed.distSqr(pos);
            if (distance < area && distance > fringe) {
                return true;
            }
        }
        return false;
    }

    public static boolean spreadFibres(Level level, BlockPos pos) {
        return spreadFibres(level, pos, false);
    }

    public static boolean spreadFibres(Level level, BlockPos pos, boolean ignoreGate) {
        if (level == null || level.isClientSide) {
            return false;
        }
        if (!ignoreGate && TCConfig.WUSS_MODE.get()) {
            return false;
        }
        RandomSource random = level.getRandom();
        float mod = 0.001F + thaumcraft.common.world.aura.AuraHandler.getFluxSaturation(level, pos) * 2.0F;
        if (!ignoreGate && random.nextFloat() > taintSpreadRate() / 100.0F * mod) {
            return false;
        }
        if (!isNearTaintSeed(level, pos)) {
            return false;
        }
        BlockPos target = pos.offset(random.nextInt(3) - 1, random.nextInt(3) - 1, random.nextInt(3) - 1);
        if (target.equals(pos)) {
            return false;
        }
        return spreadFibresToTarget(level, target);
    }

    public static boolean spreadFibresToTargetForValidation(Level level, BlockPos source, BlockPos target) {
        if (level == null || level.isClientSide || source.equals(target) || !isNearTaintSeed(level, source)) {
            return false;
        }
        return spreadFibresToTarget(level, target);
    }

    private static boolean spreadFibresToTarget(Level level, BlockPos target) {
        RandomSource random = level.getRandom();
        BlockState state = level.getBlockState(target);
        if (!canMutate(level, target, state)) {
            return false;
        }

        if (shouldBecomeSurfaceFibre(level, target, state)) {
            setTaintFibre(level, target);
            return true;
        }
        if (state.is(BlockTags.LEAVES)) {
            Optional<Direction> touchingLog = faceTouching(level, target, TCBlocks.TAINT_LOG.get());
            if (random.nextFloat() < 0.6F && touchingLog.isPresent()) {
                level.setBlock(target, TCBlocks.TAINT_FEATURE.get().defaultBlockState()
                        .setValue(TCTaintFeatureBlock.FACING, touchingLog.get().getOpposite()), Block.UPDATE_ALL);
                return true;
            }
            setTaintFibre(level, target);
            return true;
        }
        if (TCTaintFibreBlock.isHemmedByTaint(level, target) && state.getDestroySpeed(level, target) < 5.0F) {
            if (state.is(BlockTags.LOGS) && !state.is(TCBlocks.TAINT_LOG.get())) {
                Direction.Axis axis = state.hasProperty(RotatedPillarBlock.AXIS)
                        ? state.getValue(RotatedPillarBlock.AXIS)
                        : Direction.Axis.Y;
                level.setBlock(target, TCBlocks.TAINT_LOG.get().defaultBlockState()
                        .setValue(RotatedPillarBlock.AXIS, axis), Block.UPDATE_ALL);
                return true;
            }
            BlockState replacement = taintReplacementFor(state);
            if (replacement != null) {
                level.setBlock(target, replacement, Block.UPDATE_ALL);
                level.blockEvent(target, replacement.getBlock(), 1, 0);
                AuraHelper.drainFlux(level, target, 0.01F, false);
                return true;
            }
        }
        if ((state.is(TCBlocks.TAINT_SOIL.get()) || state.is(TCBlocks.TAINT_ROCK.get()))
                && level.isEmptyBlock(target.above())
                && AuraHelper.getFlux(level, target) >= 5.0F
                && random.nextFloat() < taintSpreadRate() / 100.0F * 0.33F
                && isAtTaintSeedEdge(level, target)) {
            TCTaintSeedEntity seed = new TCTaintSeedEntity(level, target.above(), false);
            seed.setYRot(random.nextInt(360));
            if (seed.canSpawnLikeLegacy()) {
                AuraHelper.drainFlux(level, target, 5.0F, false);
                level.addFreshEntity(seed);
                return true;
            }
        }
        return false;
    }

    static boolean isTaintState(BlockState state) {
        Block block = state.getBlock();
        return block == TCBlocks.TAINT_FIBRE.get()
                || block == TCBlocks.FLUX_GOO.get()
                || block == TCBlocks.TAINT_CRUST.get()
                || block == TCBlocks.TAINT_SOIL.get()
                || block == TCBlocks.TAINT_ROCK.get()
                || block == TCBlocks.TAINT_GEYSER.get()
                || block == TCBlocks.TAINT_FEATURE.get()
                || block == TCBlocks.TAINT_LOG.get();
    }

    public static int taintSpreadArea() {
        return TCConfig.TAINT_SPREAD_AREA.get();
    }

    public static float taintSpreadRate() {
        return TCConfig.TAINT_SPREAD_RATE.get().floatValue();
    }

    static int legacyDefaultTaintSpreadArea() {
        return LEGACY_DEFAULT_TAINT_SPREAD_AREA;
    }

    static float legacyDefaultTaintSpreadRate() {
        return LEGACY_DEFAULT_TAINT_SPREAD_RATE;
    }

    private static void setTaintFibre(Level level, BlockPos target) {
        BlockState fibre = TCBlocks.TAINT_FIBRE.get() instanceof TCTaintFibreBlock block
                ? block.stateForWorld(level, target)
                : TCBlocks.TAINT_FIBRE.get().defaultBlockState();
        level.setBlock(target, fibre, Block.UPDATE_ALL);
        level.blockEvent(target, TCBlocks.TAINT_FIBRE.get(), 1, 0);
        AuraHelper.drainFlux(level, target, 0.01F, false);
    }

    private static boolean shouldBecomeSurfaceFibre(Level level, BlockPos target, BlockState state) {
        boolean replaceableSurface = state.isAir()
                || state.getCollisionShape(level, target).isEmpty()
                || state.getBlock() instanceof BushBlock;
        return replaceableSurface
                && isAdjacentToSolidBlock(level, target)
                && !TCTaintFibreBlock.isOnlyAdjacentToTaint(level, target)
                && !state.is(BlockTags.LEAVES);
    }

    private static BlockState taintReplacementFor(BlockState state) {
        if (state.getBlock() instanceof SnowLayerBlock
                || state.getFluidState().is(Fluids.WATER)
                || state.is(BlockTags.LEAVES)
                || state.is(BlockTags.FLOWERS)
                || state.getBlock() instanceof BushBlock) {
            return TCBlocks.TAINT_CRUST.get().defaultBlockState();
        }
        if (state.is(BlockTags.DIRT) || state.is(BlockTags.SAND)) {
            return TCBlocks.TAINT_SOIL.get().defaultBlockState();
        }
        if (state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.BASE_STONE_NETHER)) {
            return TCBlocks.TAINT_ROCK.get().defaultBlockState();
        }
        return null;
    }

    private static boolean canMutate(Level level, BlockPos pos, BlockState state) {
        float hardness = state.getDestroySpeed(level, pos);
        return hardness >= 0.0F && hardness <= 10.0F && !isTaintState(state);
    }

    private static boolean isAdjacentToSolidBlock(LevelReader level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighbor = level.getBlockState(neighborPos);
            if (neighbor.isFaceSturdy(level, neighborPos, direction.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    private static Optional<Direction> faceTouching(LevelReader level, BlockPos pos, Block block) {
        for (Direction direction : Direction.values()) {
            if (level.getBlockState(pos.relative(direction)).is(block)) {
                return Optional.of(direction);
            }
        }
        return Optional.empty();
    }
}

package thaumcraft.common.crafting;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blocks.devices.TCInfernalFurnaceBlock;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.research.TCResearchManager;

/** Modern IDustTrigger host for Salis Mundus transformations that are already ported. */
public final class TCSalisMundusActivation {
    public static final String INFERNAL_FURNACE_RESEARCH = "INFERNALFURNACE";

    private static final Direction[] LEGACY_HORIZONTAL_ORDER = {
            Direction.SOUTH,
            Direction.WEST,
            Direction.NORTH,
            Direction.EAST
    };

    private static final Part[][][] INFERNAL_FURNACE_BLUEPRINT = {
            {
                    {Part.NETHER_BRICK, Part.OBSIDIAN, Part.NETHER_BRICK},
                    {Part.OBSIDIAN, Part.EMPTY, Part.OBSIDIAN},
                    {Part.NETHER_BRICK, Part.OBSIDIAN, Part.NETHER_BRICK}
            },
            {
                    {Part.NETHER_BRICK, Part.OBSIDIAN, Part.NETHER_BRICK},
                    {Part.OBSIDIAN, Part.LAVA, Part.OBSIDIAN},
                    {Part.NETHER_BRICK, Part.IRON_BARS_TO_AIR, Part.NETHER_BRICK}
            },
            {
                    {Part.NETHER_BRICK, Part.OBSIDIAN, Part.NETHER_BRICK},
                    {Part.OBSIDIAN, Part.OBSIDIAN, Part.OBSIDIAN},
                    {Part.NETHER_BRICK, Part.OBSIDIAN, Part.NETHER_BRICK}
            }
    };

    private TCSalisMundusActivation() {
    }

    public static Result tryActivate(UseOnContext context) {
        Level level = context.getLevel();
        Optional<InfernalFurnacePlacement> placement = findInfernalFurnacePlacement(level, context.getClickedPos());
        if (placement.isEmpty()) {
            return Result.none();
        }

        if (context.getPlayer() instanceof ServerPlayer player
                && !TCResearchManager.knowsResearchStrict(
                TCPlayerKnowledgeStore.get(player),
                INFERNAL_FURNACE_RESEARCH
        )) {
            return Result.blocked("missing_research:" + INFERNAL_FURNACE_RESEARCH);
        }

        if (!level.isClientSide) {
            applyInfernalFurnacePlacement((ServerLevel) level, placement.get());
        }
        return Result.activated("infernal_furnace");
    }

    public static Optional<InfernalFurnacePlacement> findInfernalFurnacePlacement(BlockGetter level, BlockPos clickedPos) {
        for (int yy = -3; yy <= 0; yy++) {
            for (int xx = -3; xx <= 0; xx++) {
                for (int zz = -3; zz <= 0; zz++) {
                    BlockPos origin = clickedPos.offset(xx, yy, zz);
                    for (Direction facing : LEGACY_HORIZONTAL_ORDER) {
                        if (fitsInfernalFurnace(level, origin, facing)) {
                            return Optional.of(new InfernalFurnacePlacement(origin, facing));
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    public static boolean tryActivateInfernalFurnaceForValidation(ServerLevel level, BlockPos clickedPos) {
        Optional<InfernalFurnacePlacement> placement = findInfernalFurnacePlacement(level, clickedPos);
        placement.ifPresent(value -> applyInfernalFurnacePlacement(level, value));
        return placement.isPresent();
    }

    private static boolean fitsInfernalFurnace(BlockGetter level, BlockPos origin, Direction facing) {
        for (int y = 0; y < 3; y++) {
            Part[][] layer = rotatedLayer(INFERNAL_FURNACE_BLUEPRINT[y], facing);
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    Part part = layer[x][z];
                    if (part == Part.EMPTY) {
                        continue;
                    }
                    BlockPos pos = origin.offset(x, 2 - y, z);
                    if (!matches(level.getBlockState(pos), part)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void applyInfernalFurnacePlacement(ServerLevel level, InfernalFurnacePlacement placement) {
        for (int y = 0; y < 3; y++) {
            Part[][] layer = rotatedLayer(INFERNAL_FURNACE_BLUEPRINT[y], placement.facing());
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    Part part = layer[x][z];
                    if (part == Part.EMPTY) {
                        continue;
                    }
                    BlockPos pos = placement.origin().offset(x, 2 - y, z);
                    BlockState state = replacement(part, placement.facing());
                    if (state != null) {
                        level.setBlock(pos, state, Block.UPDATE_ALL);
                    }
                }
            }
        }
        BlockPos center = placement.origin().offset(1, 1, 1);
        level.playSound(null, center, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.8F, 1.2F);
        level.sendParticles(ParticleTypes.ENCHANT, center.getX() + 0.5D, center.getY() + 0.7D, center.getZ() + 0.5D,
                24, 1.0D, 0.8D, 1.0D, 0.02D);
    }

    private static boolean matches(BlockState state, Part part) {
        return switch (part) {
            case NETHER_BRICK -> state.is(Blocks.NETHER_BRICKS);
            case OBSIDIAN -> state.is(Blocks.OBSIDIAN);
            case IRON_BARS_TO_AIR -> state.is(Blocks.IRON_BARS);
            case LAVA -> state.is(Blocks.LAVA) || state.getFluidState().is(FluidTags.LAVA);
            case EMPTY -> true;
        };
    }

    private static BlockState replacement(Part part, Direction facing) {
        return switch (part) {
            case NETHER_BRICK -> TCBlocks.PLACEHOLDER_NETHER_BRICK.get().defaultBlockState();
            case OBSIDIAN -> TCBlocks.PLACEHOLDER_OBSIDIAN.get().defaultBlockState();
            case IRON_BARS_TO_AIR -> Blocks.AIR.defaultBlockState();
            case LAVA -> TCBlocks.INFERNAL_FURNACE.get().defaultBlockState()
                    .setValue(TCInfernalFurnaceBlock.FACING, facing.getOpposite());
            case EMPTY -> null;
        };
    }

    private static Part[][] rotatedLayer(Part[][] source, Direction facing) {
        int rotations = Math.floorMod(3 - horizontalIndex(facing), 4);
        Part[][] rotated = copyLayer(source);
        for (int index = 0; index < rotations; index++) {
            rotated = rotateRight(rotated);
        }
        return rotated;
    }

    private static int horizontalIndex(Direction facing) {
        return switch (facing) {
            case SOUTH -> 0;
            case WEST -> 1;
            case NORTH -> 2;
            case EAST -> 3;
            default -> 0;
        };
    }

    private static Part[][] copyLayer(Part[][] source) {
        Part[][] copy = new Part[3][3];
        for (int x = 0; x < 3; x++) {
            System.arraycopy(source[x], 0, copy[x], 0, 3);
        }
        return copy;
    }

    private static Part[][] rotateRight(Part[][] source) {
        Part[][] rotated = new Part[3][3];
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                rotated[x][z] = source[2 - z][x];
            }
        }
        return rotated;
    }

    private enum Part {
        NETHER_BRICK,
        OBSIDIAN,
        IRON_BARS_TO_AIR,
        LAVA,
        EMPTY
    }

    public record InfernalFurnacePlacement(BlockPos origin, Direction facing) {
    }

    public record Result(boolean activated, String key, String reason) {
        public static Result none() {
            return new Result(false, "", "");
        }

        public static Result blocked(String reason) {
            return new Result(false, "", reason);
        }

        public static Result activated(String key) {
            return new Result(true, key, "");
        }
    }
}

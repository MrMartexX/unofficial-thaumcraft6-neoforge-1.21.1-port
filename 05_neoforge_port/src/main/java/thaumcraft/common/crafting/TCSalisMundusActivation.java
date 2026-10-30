package thaumcraft.common.crafting;

import java.util.Optional;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.crafting.SalisMundusTriggerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blocks.devices.TCInfernalFurnaceBlock;
import thaumcraft.common.blocks.crafting.TCGolemBuilderBlock;
import thaumcraft.common.blocks.crafting.TCThaumatoriumBlock;
import thaumcraft.common.crafting.infusion.TCInfusionStructureProfile;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.research.TCResearchDiscoveryEvents;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.research.TCResearchManager;
import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;

/** Modern IDustTrigger host for Salis Mundus transformations that are already ported. */
public final class TCSalisMundusActivation {
    public static final String ARCANE_WORKBENCH_RESEARCH = "FIRSTSTEPS@1";
    public static final String CRUCIBLE_RESEARCH = "UNLOCKALCHEMY@1";
    public static final String INFERNAL_FURNACE_RESEARCH = "INFERNALFURNACE";
    public static final String INFUSION_RESEARCH = "INFUSION";
    public static final String ANCIENT_INFUSION_RESEARCH = "INFUSIONANCIENT";
    public static final String ELDRITCH_INFUSION_RESEARCH = "INFUSIONELDRITCH";
    public static final String THAUMATORIUM_RESEARCH = "THAUMATORIUM";
    public static final String GOLEM_PRESS_RESEARCH = "MINDCLOCKWORK";

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
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        Result workbench = trySimpleTransformation(
                context,
                Blocks.CRAFTING_TABLE,
                TCBlocks.ARCANE_WORKBENCH.get(),
                TCItems.ARCANE_WORKBENCH.get(),
                ARCANE_WORKBENCH_RESEARCH,
                "arcane_workbench"
        );
        if (workbench.activated() || !workbench.reason().isEmpty()) {
            return workbench;
        }

        Result crucible = trySimpleTransformation(
                context,
                Blocks.CAULDRON,
                TCBlocks.CRUCIBLE.get(),
                TCItems.CRUCIBLE.get(),
                CRUCIBLE_RESEARCH,
                "crucible"
        );
        if (crucible.activated() || !crucible.reason().isEmpty()) {
            return crucible;
        }

        // Original TC6 bootstrap: after the Strange Dreams discovery, Salis Mundus used on a
        // bookshelf consumes the shelf and produces the Thaumonomicon.
        if (clickedState.is(Blocks.BOOKSHELF)) {
            if (context.getPlayer() instanceof ServerPlayer player
                    && !TCResearchManager.knowsResearchStrict(
                    TCPlayerKnowledgeStore.get(player),
                    TCResearchDiscoveryEvents.GOT_DREAM
            )) {
                return Result.blocked("missing_research:" + TCResearchDiscoveryEvents.GOT_DREAM);
            }

            if (!level.isClientSide) {
                ServerLevel serverLevel = (ServerLevel) level;
                BlockPos pos = clickedPos;
                serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                Block.popResource(serverLevel, pos, new ItemStack(TCItems.THAUMONOMICON.get()));
                serverLevel.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE,
                        SoundSource.BLOCKS, 0.9F, 1.15F);
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                        pos.getX() + 0.5D, pos.getY() + 0.65D, pos.getZ() + 0.5D,
                        32, 0.7D, 0.7D, 0.7D, 0.03D);
            }
            return Result.activated("thaumonomicon");
        }

        Optional<InfusionAltarPlacement> infusion = findInfusionAltarPlacement(level, clickedPos);
        if (infusion.isPresent()) {
            InfusionAltarPlacement altar = infusion.get();
            if (context.getPlayer() instanceof ServerPlayer player
                    && !TCResearchManager.knowsResearchStrict(TCPlayerKnowledgeStore.get(player), altar.research())) {
                return Result.blocked("missing_research:" + altar.research());
            }
            if (!level.isClientSide) {
                applyInfusionAltarPlacement((ServerLevel) level, altar);
            }
            return Result.activated(altar.key());
        }

        Optional<InfernalFurnacePlacement> placement = findInfernalFurnacePlacement(level, clickedPos);
        if (placement.isPresent()) {
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

        Optional<BlockPos> thaumatorium = findThaumatoriumBase(level, clickedPos);
        if (thaumatorium.isPresent()) {
            if (context.getPlayer() instanceof ServerPlayer player
                    && !TCResearchManager.knowsResearchStrict(
                    TCPlayerKnowledgeStore.get(player),
                    THAUMATORIUM_RESEARCH
            )) {
                return Result.blocked("missing_research:" + THAUMATORIUM_RESEARCH);
            }
            if (!level.isClientSide) {
                Direction facing = clickedFacing(context);
                applyThaumatoriumPlacement((ServerLevel) level, thaumatorium.get(), facing);
                if (context.getPlayer() instanceof ServerPlayer player) {
                    TCResearchManager.markCraftedResearchReferences(player, new ItemStack(TCItems.THAUMATORIUM.get()));
                }
            }
            return Result.activated("thaumatorium");
        }

        Optional<GolemPressPlacement> golemPress = findGolemPressPlacement(level, clickedPos);
        if (golemPress.isPresent()) {
            if (context.getPlayer() instanceof ServerPlayer player
                    && !TCResearchManager.knowsResearchStrict(
                    TCPlayerKnowledgeStore.get(player),
                    GOLEM_PRESS_RESEARCH
            )) {
                return Result.blocked("missing_research:" + GOLEM_PRESS_RESEARCH);
            }
            if (!level.isClientSide) {
                applyGolemPressPlacement((ServerLevel) level, golemPress.get());
                if (context.getPlayer() instanceof ServerPlayer player) {
                    TCResearchManager.markCraftedResearchReferences(player, new ItemStack(TCItems.GOLEM_BUILDER.get()));
                }
            }
            return Result.activated("golem_press");
        }

        IDustTrigger.DustTriggerResult addonResult = SalisMundusTriggerRegistry.tryActivate(context);
        if (addonResult.matched()) {
            return addonResult.activated()
                    ? Result.activated(addonResult.key())
                    : Result.blocked(addonResult.reason());
        }
        return Result.none();
    }

    private static Optional<BlockPos> findThaumatoriumBase(Level level, BlockPos clickedPos) {
        for (int offset = -2; offset <= 0; offset++) {
            BlockPos cruciblePos = clickedPos.offset(0, offset, 0);
            if (level.getBlockState(cruciblePos).is(TCBlocks.CRUCIBLE.get())
                    && level.getBlockState(cruciblePos.above()).is(TCBlocks.METAL_ALCHEMICAL.get())
                    && level.getBlockState(cruciblePos.above(2)).is(TCBlocks.METAL_ALCHEMICAL.get())) {
                return Optional.of(cruciblePos);
            }
        }
        return Optional.empty();
    }

    private static Direction clickedFacing(UseOnContext context) {
        Direction clickedFace = context.getClickedFace();
        if (clickedFace.getAxis().isHorizontal()) {
            return clickedFace;
        }
        return context.getPlayer() == null ? Direction.NORTH : context.getPlayer().getDirection().getOpposite();
    }

    private static void applyThaumatoriumPlacement(ServerLevel level, BlockPos cruciblePos, Direction facing) {
        BlockPos bottom = cruciblePos.above();
        BlockPos top = cruciblePos.above(2);
        level.setBlock(
                bottom,
                TCBlocks.THAUMATORIUM.get().defaultBlockState().setValue(TCThaumatoriumBlock.FACING, facing),
                Block.UPDATE_ALL
        );
        level.setBlock(
                top,
                TCBlocks.THAUMATORIUM_TOP.get().defaultBlockState().setValue(TCThaumatoriumBlock.FACING, facing),
                Block.UPDATE_ALL
        );
        emitDustEffects(level, bottom, 42, 0.85D);
        level.sendParticles(ParticleTypes.ENCHANT,
                top.getX() + 0.5D, top.getY() + 0.5D, top.getZ() + 0.5D,
                28, 0.6D, 0.8D, 0.6D, 0.025D);
    }

    private static Result trySimpleTransformation(
            UseOnContext context,
            Block source,
            Block target,
            net.minecraft.world.item.Item targetItem,
            String research,
            String key
    ) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.getBlockState(pos).is(source)) {
            return Result.none();
        }
        if (context.getPlayer() instanceof ServerPlayer player
                && !TCResearchManager.knowsResearchStrict(TCPlayerKnowledgeStore.get(player), research)) {
            return Result.blocked("missing_research:" + research);
        }
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            serverLevel.setBlock(pos, target.defaultBlockState(), Block.UPDATE_ALL);
            emitDustEffects(serverLevel, pos, 20, 0.55D);
            if (context.getPlayer() instanceof ServerPlayer player) {
                TCResearchManager.markCraftedResearchReferences(player, new ItemStack(targetItem));
            }
        }
        return Result.activated(key);
    }

    private static Optional<InfusionAltarPlacement> findInfusionAltarPlacement(Level level, BlockPos matrixPos) {
        if (!level.getBlockState(matrixPos).is(TCBlocks.INFUSION_MATRIX.get())) {
            return Optional.empty();
        }
        InfusionAltarVariant[] variants = {
                new InfusionAltarVariant(
                        TCBlocks.STONE_ARCANE.get(), TCBlocks.PILLAR_ARCANE.get(), TCBlocks.ARCANE_PEDESTAL.get(),
                        INFUSION_RESEARCH, "infusion_altar"
                ),
                new InfusionAltarVariant(
                        TCBlocks.STONE_ANCIENT.get(), TCBlocks.PILLAR_ANCIENT.get(), TCBlocks.ANCIENT_PEDESTAL.get(),
                        ANCIENT_INFUSION_RESEARCH, "ancient_infusion_altar"
                ),
                new InfusionAltarVariant(
                        TCBlocks.STONE_ELDRITCH_TILE.get(), TCBlocks.PILLAR_ELDRITCH.get(), TCBlocks.ELDRITCH_PEDESTAL.get(),
                        ELDRITCH_INFUSION_RESEARCH, "eldritch_infusion_altar"
                )
        };
        for (InfusionAltarVariant variant : variants) {
            if (fitsRawInfusionAltar(level, matrixPos, variant)) {
                return Optional.of(new InfusionAltarPlacement(matrixPos, variant, true));
            }
            if (fitsCompletedInfusionAltar(level, matrixPos, variant)) {
                return Optional.of(new InfusionAltarPlacement(matrixPos, variant, false));
            }
        }
        return Optional.empty();
    }

    private static boolean fitsRawInfusionAltar(Level level, BlockPos matrixPos, InfusionAltarVariant variant) {
        if (!level.getBlockState(matrixPos.below(2)).is(variant.pedestal())) {
            return false;
        }
        for (Corner corner : Corner.values()) {
            if (!level.getBlockState(matrixPos.offset(corner.x, -1, corner.z)).is(variant.rawStone())
                    || !level.getBlockState(matrixPos.offset(corner.x, -2, corner.z)).is(variant.rawStone())) {
                return false;
            }
        }
        return true;
    }

    private static boolean fitsCompletedInfusionAltar(Level level, BlockPos matrixPos, InfusionAltarVariant variant) {
        if (!level.getBlockState(matrixPos.below(2)).is(variant.pedestal())) {
            return false;
        }
        for (Corner corner : Corner.values()) {
            if (!level.getBlockState(matrixPos.offset(corner.x, -1, corner.z)).isAir()
                    || !level.getBlockState(matrixPos.offset(corner.x, -2, corner.z)).is(variant.pillar())) {
                return false;
            }
        }
        return true;
    }

    private static void applyInfusionAltarPlacement(ServerLevel level, InfusionAltarPlacement placement) {
        BlockPos matrixPos = placement.matrixPos();
        if (placement.raw()) {
            for (Corner corner : Corner.values()) {
                level.setBlock(matrixPos.offset(corner.x, -1, corner.z), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                level.setBlock(
                        matrixPos.offset(corner.x, -2, corner.z),
                        placement.variant().pillar().defaultBlockState()
                                .setValue(HorizontalDirectionalBlock.FACING, corner.facing),
                        Block.UPDATE_ALL
                );
            }
        }
        if (level.getBlockEntity(matrixPos) instanceof TCInfusionMatrixBlockEntity matrix) {
            matrix.activate();
        }
        emitDustEffects(level, matrixPos.below(), 48, 1.25D);
    }

    private static void emitDustEffects(ServerLevel level, BlockPos pos, int count, double spread) {
        level.playSound(null, pos, TCSounds.DUST.get(), SoundSource.BLOCKS, 0.9F, 1.0F);
        level.sendParticles(ParticleTypes.ENCHANT,
                pos.getX() + 0.5D, pos.getY() + 0.75D, pos.getZ() + 0.5D,
                count, spread, spread, spread, 0.03D);
    }

    /**
     * TC6 ConfigRecipes used a two-by-two, two-high blueprint for the golem press.
     * The lower layer is cauldron/anvil/piston/table-stone; an iron-bars block sits
     * directly above the piston.  Only the piston becomes the controller, while the
     * other blocks remain visible pieces of the finished machine just as in the
     * original multiblock.
     */
    private static Optional<GolemPressPlacement> findGolemPressPlacement(Level level, BlockPos clickedPos) {
        for (int y = -1; y <= 0; y++) {
            for (int x = -1; x <= 0; x++) {
                for (int z = -1; z <= 0; z++) {
                    BlockPos origin = clickedPos.offset(x, y, z);
                    for (Direction facing : LEGACY_HORIZONTAL_ORDER) {
                        if (fitsGolemPress(level, origin, facing)) {
                            return Optional.of(new GolemPressPlacement(origin, facing));
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static boolean fitsGolemPress(BlockGetter level, BlockPos origin, Direction facing) {
        BlockPos cauldron = golemPressPos(origin, 0, 0, 0, facing);
        BlockPos anvil = golemPressPos(origin, 0, 0, 1, facing);
        BlockPos piston = golemPressPos(origin, 1, 0, 0, facing);
        BlockPos table = golemPressPos(origin, 1, 0, 1, facing);
        BlockPos bars = golemPressPos(origin, 1, 1, 0, facing);
        return level.getBlockState(cauldron).is(Blocks.CAULDRON)
                && level.getBlockState(anvil).is(Blocks.ANVIL)
                && level.getBlockState(piston).is(Blocks.PISTON)
                && level.getBlockState(piston).hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)
                && level.getBlockState(piston).getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING) == Direction.UP
                && level.getBlockState(table).is(TCBlocks.TABLE_STONE.get())
                && level.getBlockState(bars).is(Blocks.IRON_BARS);
    }

    private static BlockPos golemPressPos(BlockPos origin, int localX, int localY, int localZ, Direction facing) {
        return switch (facing) {
            case SOUTH -> origin.offset(localX, localY, localZ);
            case WEST -> origin.offset(-localZ, localY, localX);
            case NORTH -> origin.offset(-localX, localY, -localZ);
            case EAST -> origin.offset(localZ, localY, -localX);
            default -> origin.offset(localX, localY, localZ);
        };
    }

    private static void applyGolemPressPlacement(ServerLevel level, GolemPressPlacement placement) {
        BlockPos piston = golemPressPos(placement.origin(), 1, 0, 0, placement.facing());
        level.setBlock(
                piston,
                TCBlocks.GOLEM_BUILDER.get().defaultBlockState()
                        .setValue(TCGolemBuilderBlock.FACING, placement.facing().getOpposite()),
                Block.UPDATE_ALL
        );
        emitDustEffects(level, piston, 40, 0.9D);
        BlockPos bars = piston.above();
        level.sendParticles(ParticleTypes.ENCHANT,
                bars.getX() + 0.5D, bars.getY() + 0.35D, bars.getZ() + 0.5D,
                20, 0.35D, 0.25D, 0.35D, 0.02D);
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

    private record GolemPressPlacement(BlockPos origin, Direction facing) {
    }

    private enum Corner {
        NORTH_WEST(-1, -1, Direction.EAST),
        NORTH_EAST(1, -1, Direction.NORTH),
        SOUTH_EAST(1, 1, Direction.WEST),
        SOUTH_WEST(-1, 1, Direction.SOUTH);

        private final int x;
        private final int z;
        private final Direction facing;

        Corner(int x, int z, Direction facing) {
            this.x = x;
            this.z = z;
            this.facing = facing;
        }
    }

    private record InfusionAltarVariant(
            Block rawStone,
            Block pillar,
            Block pedestal,
            String research,
            String key
    ) {
    }

    private record InfusionAltarPlacement(BlockPos matrixPos, InfusionAltarVariant variant, boolean raw) {
        private String research() {
            return variant.research();
        }

        private String key() {
            return variant.key();
        }
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

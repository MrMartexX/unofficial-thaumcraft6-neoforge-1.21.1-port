package thaumcraft.common.tiles.devices;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blocks.devices.TCLampBlock;
import thaumcraft.common.essentia.transport.TCEssentiaCapabilities;
import thaumcraft.common.essentia.transport.TCEssentiaStack;
import thaumcraft.common.essentia.transport.TCEssentiaSuction;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;

/** Server-owned TC6 Arcane/Growth/Fertility lamp behavior. */
public final class TCLampBlockEntity extends BlockEntity implements TCEssentiaTransport {
    private static final int ARCANE_SCAN_INTERVAL = 5;
    private static final int DRAW_INTERVAL = 5;
    private static final int GROWTH_RADIUS = 6;
    private static final int FERTILITY_RADIUS = 7;
    private static final int FERTILITY_INTERVAL = 300;
    private static final int GROWTH_MAX_CHARGES = 20;
    private static final int FERTILITY_MAX_CHARGES = 10;

    private boolean reserve;
    private int charges;
    private int drawDelay;
    private int fertilityCounter;
    private int arcaneCounter;
    private final ArrayList<BlockPos> growthChecklist = new ArrayList<>();
    private BlockPos lastGrowthTarget = BlockPos.ZERO;

    public TCLampBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.LAMP.get(), pos, state);
        charges = kind(state) == TCLampBlock.Kind.GROWTH ? -1 : 0;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCLampBlockEntity lamp) {
        if (level == null || level.isClientSide) {
            return;
        }
        switch (kind(state)) {
            case ARCANE -> lamp.tickArcane();
            case GROWTH -> lamp.tickGrowth();
            case FERTILITY -> lamp.tickFertility();
        }
    }

    public int charges() {
        return charges;
    }

    public boolean reserve() {
        return reserve;
    }

    public BlockPos lastGrowthTarget() {
        return lastGrowthTarget;
    }

    public void setCharges(int charges) {
        this.charges = charges;
        setChangedAndSync();
    }

    public boolean canRedstoneEnable() {
        return switch (kind()) {
            case ARCANE -> true;
            case GROWTH, FERTILITY -> charges > 0;
        };
    }

    public void removeGlimmers() {
        if (level == null || level.isClientSide || kind() != TCLampBlock.Kind.ARCANE) {
            return;
        }
        for (int x = -15; x <= 15; x++) {
            for (int y = -15; y <= 15; y++) {
                for (int z = -15; z <= 15; z++) {
                    BlockPos target = worldPosition.offset(x, y, z);
                    if (level.getBlockState(target).is(TCBlocks.EFFECT_GLIMMER.get())) {
                        level.removeBlock(target, false);
                    }
                }
            }
        }
    }

    boolean tryPlaceGlimmerAt(BlockPos target) {
        if (level == null || level.isClientSide || kind() != TCLampBlock.Kind.ARCANE) {
            return false;
        }
        BlockPos adjusted = clampArcaneTarget(target);
        if (!level.isEmptyBlock(adjusted)) {
            return false;
        }
        if (level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, adjusted) >= 11) {
            return false;
        }
        if (!hasLineOfSight(adjusted)) {
            return false;
        }
        return level.setBlock(adjusted, TCBlocks.EFFECT_GLIMMER.get().defaultBlockState(), Block.UPDATE_ALL);
    }

    boolean tryGrowPlantAt(BlockPos target) {
        if (!(level instanceof ServerLevel serverLevel) || kind() != TCLampBlock.Kind.GROWTH || charges <= 0) {
            return false;
        }
        BlockState state = level.getBlockState(target);
        if (!isGrowablePlant(state) || isGrownPlant(state, target)) {
            return false;
        }
        if (target.distSqr(worldPosition) >= GROWTH_RADIUS * GROWTH_RADIUS) {
            return false;
        }
        charges--;
        lastGrowthTarget = target.immutable();
        state.randomTick(serverLevel, target, level.random);
        setChangedAndSync();
        return true;
    }

    boolean updateAnimals() {
        if (!(level instanceof ServerLevel) || kind() != TCLampBlock.Kind.FERTILITY || charges <= 1) {
            return false;
        }
        List<Animal> animals = level.getEntitiesOfClass(
                Animal.class,
                new AABB(worldPosition).inflate(FERTILITY_RADIUS, FERTILITY_RADIUS, FERTILITY_RADIUS)
        );
        for (LivingEntity entity : animals) {
            Animal first = (Animal) entity;
            if (first.getAge() != 0 || first.isInLove()) {
                continue;
            }
            ArrayList<Animal> sameType = new ArrayList<>();
            for (Animal candidate : animals) {
                if (candidate.getClass().equals(first.getClass())) {
                    sameType.add(candidate);
                }
            }
            if (sameType.size() > 9) {
                continue;
            }
            Animal partner = null;
            for (Animal candidate : sameType) {
                if (candidate.getAge() != 0 || candidate.isInLove()) {
                    continue;
                }
                if (partner != null && partner != candidate) {
                    charges -= 5;
                    candidate.setInLove(null);
                    partner.setInLove(null);
                    setChangedAndSync();
                    return true;
                }
                partner = candidate;
            }
        }
        return false;
    }

    private void tickArcane() {
        if (++arcaneCounter % ARCANE_SCAN_INTERVAL != 0 || gettingPower() || !enabled()) {
            return;
        }
        int x = level.random.nextInt(16) - level.random.nextInt(16);
        int y = level.random.nextInt(16) - level.random.nextInt(16);
        int z = level.random.nextInt(16) - level.random.nextInt(16);
        tryPlaceGlimmerAt(worldPosition.offset(x, y, z));
    }

    private void tickGrowth() {
        if (charges <= 0) {
            if (reserve) {
                charges = GROWTH_MAX_CHARGES;
                reserve = false;
                setChangedAndSync();
            } else if (drawEssentia(Aspect.PLANT, true)) {
                charges = GROWTH_MAX_CHARGES;
                setChangedAndSync();
            }
            if (charges <= 0) {
                setEnabled(false);
            } else if (!gettingPower()) {
                setEnabled(true);
            }
        }
        if (!reserve && drawEssentia(Aspect.PLANT, true)) {
            reserve = true;
            setChangedAndSync();
        }
        if (charges == 0) {
            charges = -1;
            setChangedAndSync();
        }
        if (!gettingPower() && charges > 0) {
            updatePlant();
        }
    }

    private void tickFertility() {
        if (charges < FERTILITY_MAX_CHARGES) {
            if (drawEssentia(Aspect.DESIRE, false)) {
                charges++;
                setChangedAndSync();
            }
            if (charges <= 1) {
                setEnabled(false);
            } else if (!gettingPower()) {
                setEnabled(true);
            }
        }
        if (!gettingPower() && charges > 1 && fertilityCounter++ % FERTILITY_INTERVAL == 0) {
            updateAnimals();
        }
    }

    private void updatePlant() {
        if (growthChecklist.isEmpty()) {
            for (int x = -GROWTH_RADIUS; x <= GROWTH_RADIUS; x++) {
                for (int z = -GROWTH_RADIUS; z <= GROWTH_RADIUS; z++) {
                    growthChecklist.add(worldPosition.offset(x, GROWTH_RADIUS, z));
                }
            }
            for (int i = growthChecklist.size() - 1; i > 0; i--) {
                int j = level.random.nextInt(i + 1);
                BlockPos current = growthChecklist.get(i);
                growthChecklist.set(i, growthChecklist.get(j));
                growthChecklist.set(j, current);
            }
        }
        BlockPos start = growthChecklist.remove(0);
        for (int y = start.getY(); y >= worldPosition.getY() - GROWTH_RADIUS; y--) {
            if (tryGrowPlantAt(new BlockPos(start.getX(), y, start.getZ()))) {
                return;
            }
        }
    }

    private boolean drawEssentia(Aspect aspect, boolean growthReserveRule) {
        if (level == null || aspect == null || ++drawDelay % DRAW_INTERVAL != 0) {
            return false;
        }
        if (growthReserveRule && reserve && charges > 0) {
            return false;
        }
        Direction ownFace = facing();
        Direction sourceFace = ownFace.getOpposite();
        TCEssentiaTransport source = level.getCapability(
                TCEssentiaCapabilities.BLOCK,
                worldPosition.relative(ownFace),
                sourceFace
        );
        if (source == null || !source.canOutputTo(sourceFace)) {
            return false;
        }
        TCEssentiaSuction sourceSuction = source.getSuction(sourceFace);
        TCEssentiaSuction ownSuction = getSuction(ownFace);
        if (sourceSuction.amount() >= ownSuction.amount()) {
            return false;
        }
        return source.takeEssentia(aspect.getTag(), 1, sourceFace, false) == 1;
    }

    private BlockPos clampArcaneTarget(BlockPos target) {
        if (level == null) {
            return target;
        }
        BlockPos precipitation = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, target);
        if (target.getY() > precipitation.getY() + 4) {
            target = new BlockPos(target.getX(), precipitation.getY() + 4, target.getZ());
        }
        if (target.getY() < 5) {
            target = new BlockPos(target.getX(), 5, target.getZ());
        }
        return target;
    }

    private boolean hasLineOfSight(BlockPos target) {
        if (level == null) {
            return false;
        }
        double dx = target.getX() + 0.5D - (worldPosition.getX() + 0.5D);
        double dy = target.getY() + 0.5D - (worldPosition.getY() + 0.5D);
        double dz = target.getZ() + 0.5D - (worldPosition.getZ() + 0.5D);
        int steps = Math.max(1, (int) Math.ceil(Math.sqrt(dx * dx + dy * dy + dz * dz) * 2.0D));
        for (int i = 1; i < steps; i++) {
            double t = i / (double) steps;
            BlockPos sample = BlockPos.containing(
                    worldPosition.getX() + 0.5D + dx * t,
                    worldPosition.getY() + 0.5D + dy * t,
                    worldPosition.getZ() + 0.5D + dz * t
            );
            if (!sample.equals(worldPosition)
                    && !sample.equals(target)
                    && !level.getBlockState(sample).isAir()
                    && !level.getBlockState(sample).is(TCBlocks.EFFECT_GLIMMER.get())) {
                return false;
            }
        }
        return true;
    }

    private boolean isGrowablePlant(BlockState state) {
        return state.getBlock() instanceof BonemealableBlock
                || state.getBlock() instanceof CactusBlock
                || state.getBlock() instanceof SugarCaneBlock;
    }

    private boolean isGrownPlant(BlockState state, BlockPos pos) {
        if (level == null || state.isAir()) {
            return true;
        }
        if (state.getBlock() instanceof BonemealableBlock bonemealable) {
            return !bonemealable.isValidBonemealTarget(level, pos, state);
        }
        Block block = state.getBlock();
        return block instanceof CactusBlock && level.getBlockState(pos.above()).is(block)
                || block instanceof SugarCaneBlock && level.getBlockState(pos.above()).is(block);
    }

    private boolean gettingPower() {
        return level != null && level.hasNeighborSignal(worldPosition);
    }

    private boolean enabled() {
        BlockState state = getBlockState();
        return state.hasProperty(TCLampBlock.ENABLED) && state.getValue(TCLampBlock.ENABLED);
    }

    private void setEnabled(boolean enabled) {
        if (level == null || level.isClientSide) {
            return;
        }
        BlockState state = getBlockState();
        if (state.hasProperty(TCLampBlock.ENABLED) && state.getValue(TCLampBlock.ENABLED) != enabled) {
            level.setBlock(worldPosition, state.setValue(TCLampBlock.ENABLED, enabled), Block.UPDATE_ALL);
        }
    }

    private Direction facing() {
        BlockState state = getBlockState();
        return state.hasProperty(TCLampBlock.FACING) ? state.getValue(TCLampBlock.FACING) : Direction.DOWN;
    }

    private TCLampBlock.Kind kind() {
        return kind(getBlockState());
    }

    private static TCLampBlock.Kind kind(BlockState state) {
        return state.getBlock() instanceof TCLampBlock lamp ? lamp.kind() : TCLampBlock.Kind.ARCANE;
    }

    private void setChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    }

    @Override
    public boolean isConnectable(Direction face) {
        TCLampBlock.Kind kind = kind();
        return (kind == TCLampBlock.Kind.GROWTH || kind == TCLampBlock.Kind.FERTILITY) && face == facing();
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return isConnectable(face);
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return false;
    }

    @Override
    public TCEssentiaSuction getSuction(Direction face) {
        if (!isConnectable(face)) {
            return TCEssentiaSuction.NONE;
        }
        return switch (kind()) {
            case ARCANE -> TCEssentiaSuction.NONE;
            case GROWTH -> (!reserve || charges <= 0)
                    ? new TCEssentiaSuction(Aspect.PLANT.getTag(), 128)
                    : TCEssentiaSuction.NONE;
            case FERTILITY -> new TCEssentiaSuction(Aspect.DESIRE.getTag(), Math.max(0, 128 - charges * 10));
        };
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public TCEssentiaStack getEssentia(Direction face) {
        return TCEssentiaStack.EMPTY;
    }

    @Override
    public int addEssentia(String aspect, int amount, Direction face, boolean simulate) {
        return 0;
    }

    @Override
    public int takeEssentia(String aspect, int amount, Direction face, boolean simulate) {
        return 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("Reserve", reserve);
        tag.putInt("Charges", charges);
        tag.putInt("DrawDelay", drawDelay);
        tag.putInt("FertilityCounter", fertilityCounter);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        reserve = tag.getBoolean("Reserve");
        charges = tag.contains("Charges") ? tag.getInt("Charges") : (kind() == TCLampBlock.Kind.GROWTH ? -1 : 0);
        drawDelay = tag.getInt("DrawDelay");
        fertilityCounter = tag.getInt("FertilityCounter");
    }
}

package thaumcraft.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.world.aura.AuraHandler;

/**
 * TC6 Vis Battery block.
 * Stores up to 10 points of vis. Absorbs vis from aura when abundant (>90% base),
 * slowly discharges back when aura is depleted (<75% base), and rapidly discharges
 * to aura when redstone powered. Emits light and comparator signal proportional to charge.
 */
public class TCVisBatteryBlock extends Block {
    public static final IntegerProperty CHARGE = IntegerProperty.create("charge", 0, 10);

    public TCVisBatteryBlock() {
        this(BlockBehaviour.Properties.of()
                .sound(SoundType.STONE)
                .strength(0.5F)
                .lightLevel(state -> state.hasProperty(CHARGE) ? state.getValue(CHARGE) : 0));
    }

    public TCVisBatteryBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CHARGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGE);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        doBatteryTick(state, level, pos, random);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        doBatteryTick(state, level, pos, random);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide && level.hasNeighborSignal(pos)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    private void doBatteryTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        int charge = state.getValue(CHARGE);
        if (world.hasNeighborSignal(pos)) {
            if (charge > 0) {
                AuraHandler.addVis(world, pos, 1.0F);
                world.setBlock(pos, state.setValue(CHARGE, charge - 1), 3);
                world.scheduleTick(pos, this, 5);
            }
        } else {
            float aura = AuraHelper.getVis(world, pos);
            int base = AuraHelper.getAuraBase(world, pos);
            if (charge < 10 && aura > base * 0.9F && aura > 1.0F) {
                AuraHandler.drainVis(world, pos, 1.0F, false);
                world.setBlock(pos, state.setValue(CHARGE, charge + 1), 3);
                world.scheduleTick(pos, this, 100 + rand.nextInt(100));
            } else if (charge > 0 && aura < base * 0.75F) {
                AuraHandler.addVis(world, pos, 1.0F);
                world.setBlock(pos, state.setValue(CHARGE, charge - 1), 3);
                world.scheduleTick(pos, this, 20 + rand.nextInt(20));
            }
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(CHARGE);
    }
}

package thaumcraft.common.blocks.world.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class TCLeavesBlock extends LeavesBlock {
    public static final BooleanProperty CHECK_DECAY = BooleanProperty.create("check_decay");
    public static final BooleanProperty DECAYABLE = BooleanProperty.create("decayable");

    public TCLeavesBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(CHECK_DECAY, true)
                .setValue(DECAYABLE, true));
    }

    public static BlockState legacyGeneratedState(BlockState state) {
        if (state.hasProperty(CHECK_DECAY)) {
            state = state.setValue(CHECK_DECAY, false);
        }

        if (state.hasProperty(DECAYABLE)) {
            state = state.setValue(DECAYABLE, true);
        }

        if (state.hasProperty(PERSISTENT)) {
            state = state.setValue(PERSISTENT, false);
        }

        return state;
    }

    public static BlockState beginLegacyDecay(BlockState state) {
        if (state.hasProperty(CHECK_DECAY) && state.hasProperty(DECAYABLE) && state.getValue(DECAYABLE)) {
            return state.setValue(CHECK_DECAY, true);
        }

        return state;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide() && placer instanceof Player && state.hasProperty(DECAYABLE)) {
            BlockState placedState = level.getBlockState(pos)
                    .setValue(DECAYABLE, false)
                    .setValue(CHECK_DECAY, false);

            level.setBlock(pos, placedState, 3);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.hasProperty(CHECK_DECAY)
                || !state.hasProperty(DECAYABLE)
                || !state.getValue(DECAYABLE)
                || !state.getValue(CHECK_DECAY)) {
            return;
        }

        if (hasLegacyLogSupport(level, pos)) {
            level.setBlock(pos, state.setValue(CHECK_DECAY, false), 3);
            return;
        }

        Block.dropResources(state, level, pos);
        level.removeBlock(pos, false);
    }

    private static boolean hasLegacyLogSupport(BlockGetter level, BlockPos center) {
        int radius = 4;

        for (BlockPos scanPos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
            if (level.getBlockState(scanPos).is(BlockTags.LOGS)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CHECK_DECAY, DECAYABLE);
    }
}
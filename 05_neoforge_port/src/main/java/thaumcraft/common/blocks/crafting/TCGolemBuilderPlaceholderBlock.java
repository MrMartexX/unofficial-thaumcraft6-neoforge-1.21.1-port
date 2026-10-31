package thaumcraft.common.blocks.crafting;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.registry.TCBlocks;

/**
 * Legacy multiblock placeholder for Golem Press components.
 * Matches legacy BlockPlaceholder behavior: provides component hitboxes,
 * light emission for cauldron (13), drops corresponding component, and triggers
 * rollback if broken.
 */
public final class TCGolemBuilderPlaceholderBlock extends Block {
    public enum Kind {
        BARS,
        ANVIL,
        CAULDRON,
        TABLE
    }

    private final Kind kind;

    public TCGolemBuilderPlaceholderBlock(BlockBehaviour.Properties properties, Kind kind) {
        super(properties);
        this.kind = kind;
    }

    public Kind kind() {
        return this.kind;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (this.kind) {
            case BARS -> Blocks.IRON_BARS.defaultBlockState().getShape(level, pos, context);
            case ANVIL -> Blocks.ANVIL.defaultBlockState().getShape(level, pos, context);
            case CAULDRON -> Blocks.CAULDRON.defaultBlockState().getShape(level, pos, context);
            case TABLE -> TCBlocks.TABLE_STONE.get().defaultBlockState().getShape(level, pos, context);
        };
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return switch (this.kind) {
            case BARS -> List.of(new ItemStack(Items.IRON_BARS));
            case ANVIL -> List.of(new ItemStack(Items.ANVIL));
            case CAULDRON -> List.of(new ItemStack(Items.CAULDRON));
            case TABLE -> List.of(new ItemStack(TCBlocks.TABLE_STONE.get()));
        };
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide && !TCGolemBuilderBlock.isDestroyingStructure()) {
            for (BlockPos target : BlockPos.betweenClosed(pos.offset(-2, -1, -2), pos.offset(2, 1, 2))) {
                BlockState targetState = level.getBlockState(target);
                if (targetState.is(TCBlocks.GOLEM_BUILDER.get())) {
                    TCGolemBuilderBlock.destroyStructure(level, target, targetState);
                    break;
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}

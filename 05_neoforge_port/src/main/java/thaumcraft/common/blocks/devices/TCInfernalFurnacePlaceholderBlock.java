package thaumcraft.common.blocks.devices;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.registry.TCBlocks;

/** Invisible legacy structure placeholders used by the Salis Mundus Infernal Furnace multiblock. */
public final class TCInfernalFurnacePlaceholderBlock extends Block {
    public enum Kind {
        NETHER_BRICK,
        OBSIDIAN
    }

    private final Kind kind;

    public TCInfernalFurnacePlaceholderBlock(BlockBehaviour.Properties properties, Kind kind) {
        super(properties);
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(new ItemStack(kind == Kind.NETHER_BRICK ? Items.NETHER_BRICKS : Items.OBSIDIAN));
    }

    @Override
    protected void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean movedByPiston
    ) {
        if (!state.is(newState.getBlock()) && !level.isClientSide && !TCInfernalFurnaceBlock.isDestroyingStructure()) {
            for (BlockPos target : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                BlockState targetState = level.getBlockState(target);
                if (targetState.is(TCBlocks.INFERNAL_FURNACE.get())) {
                    TCInfernalFurnaceBlock.destroyStructure(level, target, targetState);
                    break;
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}

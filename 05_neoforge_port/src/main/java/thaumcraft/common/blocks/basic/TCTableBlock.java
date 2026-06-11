package thaumcraft.common.blocks.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.common.blocks.crafting.TCResearchTableBlock;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.research.TCResearchManager;
import thaumcraft.common.tiles.crafting.TCResearchTableBlockEntity;

public class TCTableBlock extends Block {
    private static final VoxelShape SHAPE = Shapes.or(
            box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0),
            box(11.0, 0.0, 1.0, 15.0, 12.0, 5.0),
            box(11.0, 0.0, 11.0, 15.0, 12.0, 15.0),
            box(1.0, 0.0, 11.0, 5.0, 12.0, 15.0),
            box(1.0, 0.0, 1.0, 5.0, 12.0, 5.0),
            box(3.0, 3.0, 3.0, 13.0, 5.0, 13.0)
    );

    private final boolean wood;

    public TCTableBlock(BlockBehaviour.Properties properties, boolean wood) {
        super(properties);
        this.wood = wood;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!wood || !(stack.getItem() instanceof IScribeTools)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide) {
            ItemStack storedTools = stack.copy();
            storedTools.setCount(1);

            BlockState researchTable = TCBlocks.RESEARCH_TABLE.get()
                    .defaultBlockState()
                    .setValue(TCResearchTableBlock.FACING, player.getDirection());
            level.setBlock(pos, researchTable, UPDATE_ALL);

            if (level.getBlockEntity(pos) instanceof TCResearchTableBlockEntity table) {
                table.setScribingTools(storedTools);
            }

            stack.shrink(1);
            if (player instanceof ServerPlayer serverPlayer) {
                TCResearchManager.markCraftedResearchReferences(serverPlayer, new ItemStack(TCItems.RESEARCH_TABLE.get()));
            }
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}

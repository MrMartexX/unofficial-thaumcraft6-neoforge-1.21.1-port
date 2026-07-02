package thaumcraft.common.items;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.items.components.TCMirrorLinkComponent;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.tiles.devices.TCMirrorEssentiaBlockEntity;

/** BlockItem bridge for legacy mirror linking NBT using 1.21 Data Components. */
public final class TCMirrorBlockItem extends BlockItem {
    public TCMirrorBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockState clickedState = level.getBlockState(context.getClickedPos());
        if (!clickedState.is(TCBlocks.MIRROR_ESSENTIA.get())) {
            return super.useOn(context);
        }

        Player player = context.getPlayer();
        if (level.isClientSide) {
            if (player != null) {
                player.swing(context.getHand());
            }
            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(context.getClickedPos()) instanceof TCMirrorEssentiaBlockEntity mirror
                && !mirror.isLinkValid()) {
            ItemStack linkedStack = context.getItemInHand().copyWithCount(1);
            linkedStack.set(TCDataComponents.MIRROR_LINK.get(), TCMirrorLinkComponent.of(level, context.getClickedPos()));
            level.playSound(null, context.getClickedPos(), TCSounds.JAR.get(), SoundSource.BLOCKS, 1.0F, 2.0F);
            if (player != null) {
                if (!player.getInventory().add(linkedStack)) {
                    player.drop(linkedStack, false);
                }
                if (!player.getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
                player.inventoryMenu.broadcastChanges();
            }
            return InteractionResult.SUCCESS;
        }

        if (player != null) {
            player.displayClientMessage(Component.translatable("tc.mirror.already_linked")
                    .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC), true);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        boolean placed = super.placeBlock(context, state);
        if (!placed) {
            return false;
        }
        Level level = context.getLevel();
        if (!level.isClientSide
                && level.getBlockEntity(context.getClickedPos()) instanceof TCMirrorEssentiaBlockEntity mirror) {
            mirror.applyLinkComponent(context.getItemInHand().get(TCDataComponents.MIRROR_LINK.get()));
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        TCMirrorLinkComponent link = stack.get(TCDataComponents.MIRROR_LINK.get());
        if (link != null) {
            tooltipComponents.add(Component.literal("Linked to " + link.x() + "," + link.y() + "," + link.z()
                    + " in " + link.dimension()).withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(TCDataComponents.MIRROR_LINK.get()) || super.isFoil(stack);
    }

    public static ItemStack stackFromMirror(TCMirrorEssentiaBlockEntity mirror) {
        ItemStack stack = new ItemStack(TCBlocks.MIRROR_ESSENTIA.get());
        if (mirror.isLinked()) {
            stack.set(TCDataComponents.MIRROR_LINK.get(), mirror.linkComponent());
        }
        return stack;
    }
}

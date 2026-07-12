package thaumcraft.common.items;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.tiles.devices.TCBrainJarBlockEntity;

/** BlockItem state bridge for the legacy Brain-in-a-Jar XP payload. */
public final class TCBrainJarBlockItem extends BlockItem {
    public TCBrainJarBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        boolean placed = super.placeBlock(context, state);
        if (!placed) {
            return false;
        }
        Level level = context.getLevel();
        if (!level.isClientSide && level.getBlockEntity(context.getClickedPos()) instanceof TCBrainJarBlockEntity jar) {
            Integer xp = context.getItemInHand().get(TCDataComponents.BRAIN_JAR_XP.get());
            if (xp != null) {
                jar.setXpForValidation(xp);
            }
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        Integer xp = stack.get(TCDataComponents.BRAIN_JAR_XP.get());
        if (xp != null && xp > 0) {
            tooltipComponents.add(Component.literal(Mth.clamp(xp, 0, TCBrainJarBlockEntity.XP_MAX) + " xp")
                    .withStyle(ChatFormatting.GREEN));
        }
    }

    public static ItemStack stackFromJar(TCBrainJarBlockEntity jar) {
        ItemStack stack = new ItemStack(jar.getBlockState().getBlock());
        if (jar.xp() > 0) {
            stack.set(TCDataComponents.BRAIN_JAR_XP.get(), Mth.clamp(jar.xp(), 0, TCBrainJarBlockEntity.XP_MAX));
        }
        return stack;
    }
}

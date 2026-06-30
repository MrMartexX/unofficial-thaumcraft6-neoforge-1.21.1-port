package thaumcraft.common.items;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.essentia.TCWardedJarBlockEntity;

/** BlockItem state bridge for TC6 warded jars carrying essentia/filter payloads. */
public final class TCWardedJarBlockItem extends BlockItem {
    public TCWardedJarBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        boolean placed = super.placeBlock(context, state);
        if (!placed) {
            return false;
        }
        Level level = context.getLevel();
        if (!level.isClientSide && level.getBlockEntity(context.getClickedPos()) instanceof TCWardedJarBlockEntity jar) {
            ItemStack stack = context.getItemInHand();
            Aspect aspect = TCEssentiaItemHelper.aspectFromStack(stack);
            int amount = TCEssentiaItemHelper.aspectAmount(stack);
            jar.setStoredForValidation(aspect, amount);
            jar.setFilter(TCEssentiaItemHelper.filterAspect(stack));
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        Aspect filter = TCEssentiaItemHelper.filterAspect(stack);
        Aspect aspect = TCEssentiaItemHelper.aspectFromStack(stack);
        if (filter != null) {
            tooltipComponents.add(Component.translatable("tc.aspect." + filter.getTag()).withStyle(ChatFormatting.DARK_PURPLE));
        }
        if (aspect != null && TCEssentiaItemHelper.aspectAmount(stack) > 0) {
            tooltipComponents.add(Component.literal(TCEssentiaItemHelper.aspectAmount(stack) + " ")
                    .append(Component.translatable("tc.aspect." + aspect.getTag()))
                    .withStyle(ChatFormatting.DARK_AQUA));
        }
    }

    public static ItemStack stackFromJar(TCWardedJarBlockEntity jar) {
        ItemStack stack = new ItemStack(jar.getBlockState().getBlock());
        if (jar.storedAspect() != null && jar.storedAmount() > 0) {
            TCEssentiaItemHelper.setAspect(stack, jar.storedAspect(), jar.storedAmount());
        }
        if (jar.aspectFilter() != null) {
            TCEssentiaItemHelper.setFilter(stack, jar.aspectFilter());
        }
        return stack;
    }
}

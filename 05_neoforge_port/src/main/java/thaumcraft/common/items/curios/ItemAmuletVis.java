package thaumcraft.common.items.curios;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

public class ItemAmuletVis extends Item {
    public ItemAmuletVis() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.amulet_vis.text").withStyle(ChatFormatting.AQUA));
    }
}

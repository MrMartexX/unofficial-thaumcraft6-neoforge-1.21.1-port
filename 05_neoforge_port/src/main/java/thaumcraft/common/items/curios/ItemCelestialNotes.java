package thaumcraft.common.items.curios;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import thaumcraft.common.registry.TCItems;

public class ItemCelestialNotes extends Item {

    public static final String[] VARIANTS = new String[] {
            "sun",
            "stars_1", "stars_2", "stars_3", "stars_4",
            "moon_1", "moon_2", "moon_3", "moon_4", "moon_5", "moon_6", "moon_7", "moon_8"
    };

    public ItemCelestialNotes(Properties properties) {
        super(properties);
    }

    public static int getVariant(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("variant")) {
            int v = customData.copyTag().getInt("variant");
            if (v >= 0 && v < VARIANTS.length) {
                return v;
            }
        }
        return 0;
    }

    public static ItemStack createStack(int variant, int count) {
        ItemStack stack = new ItemStack(TCItems.CELESTIAL_NOTES.get(), count);
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt("variant", Math.max(0, Math.min(variant, VARIANTS.length - 1))));
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("item.celestial_notes");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int v = getVariant(stack);
        tooltip.add(Component.translatable("item.celestial_notes." + VARIANTS[v] + ".text").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}

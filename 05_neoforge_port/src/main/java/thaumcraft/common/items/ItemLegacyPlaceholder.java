package thaumcraft.common.items;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * Small compatibility item used while full TC6 item behavior is being ported.
 *
 * <p>This keeps stack limits, rarity, glint and user-visible intent close to legacy without pretending the full
 * behaviour exists yet. Each call site should be replaced by a dedicated item class as the corresponding system is
 * ported.</p>
 */
public class ItemLegacyPlaceholder extends Item {
    private final String tooltipKey;
    private final boolean foil;

    public ItemLegacyPlaceholder(Properties properties, String tooltipKey) {
        this(properties, tooltipKey, false);
    }

    public ItemLegacyPlaceholder(Properties properties, String tooltipKey, boolean foil) {
        super(properties);
        this.tooltipKey = tooltipKey;
        this.foil = foil;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (tooltipKey != null && !tooltipKey.isBlank()) {
            tooltipComponents.add(Component.translatable(tooltipKey).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return foil || super.isFoil(stack);
    }
}

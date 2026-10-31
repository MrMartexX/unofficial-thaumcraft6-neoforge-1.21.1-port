package thaumcraft.common.items.curios;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.warp.TCWarpClientCache;
import thaumcraft.common.warp.TCWarpManager;
import thaumcraft.common.warp.TCWarpType;

public class ItemVoidseerPearl extends Item implements IVisDiscountGear, IWarpingGear {
    public ItemVoidseerPearl() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        if (player == null) {
            return 0;
        }
        int pw = 0;
        if (player instanceof ServerPlayer serverPlayer) {
            pw = TCWarpManager.get(serverPlayer).get(TCWarpType.PERMANENT);
        } else if (player.level().isClientSide) {
            pw = TCWarpClientCache.currentWarp().get(TCWarpType.PERMANENT);
        }
        pw = Math.min(100, Math.max(0, pw));
        return (int) (pw / 100.0F * 25.0F);
    }

    @Override
    public int getWarp(ItemStack stack, Player player) {
        return getVisDiscount(stack, player) / 5;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.voidseer_charm.text").withStyle(ChatFormatting.DARK_BLUE, ChatFormatting.ITALIC));
    }
}

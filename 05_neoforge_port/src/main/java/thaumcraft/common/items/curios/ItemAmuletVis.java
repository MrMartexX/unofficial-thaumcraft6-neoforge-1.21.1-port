package thaumcraft.common.items.curios;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.items.TCRechargeHelper;

public class ItemAmuletVis extends Item {
    public ItemAmuletVis() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof Player player && player.tickCount % 40 == 0) {
            for (ItemStack s : player.getInventory().items) {
                if (!s.isEmpty() && s != stack && TCRechargeHelper.canRecharge(s) && TCRechargeHelper.getCharge(s) < TCRechargeHelper.getMaxCharge(s)) {
                    float drained = AuraHelper.drainVis(level, player.blockPosition(), 1.0F, false);
                    if (drained > 0.0F) {
                        TCRechargeHelper.recharge(s, 1);
                        return;
                    }
                }
            }
            for (ItemStack s : player.getInventory().armor) {
                if (!s.isEmpty() && TCRechargeHelper.canRecharge(s) && TCRechargeHelper.getCharge(s) < TCRechargeHelper.getMaxCharge(s)) {
                    float drained = AuraHelper.drainVis(level, player.blockPosition(), 1.0F, false);
                    if (drained > 0.0F) {
                        TCRechargeHelper.recharge(s, 1);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.amulet_vis.text").withStyle(ChatFormatting.AQUA));
    }
}

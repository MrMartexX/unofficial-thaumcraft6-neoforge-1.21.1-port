package thaumcraft.common.items.curios;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import thaumcraft.common.items.TCRechargeHelper;

import java.util.List;

public class ItemVerdantHeart extends Item {
    public enum Type {
        BASE,
        LIFE,
        SUSTAIN
    }

    private final Type type;

    public ItemVerdantHeart(Type type) {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return TCRechargeHelper.getCharge(stack) < TCRechargeHelper.getMaxCharge(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int max = TCRechargeHelper.getMaxCharge(stack);
        if (max <= 0) {
            return 0;
        }
        return Math.round(13.0F * TCRechargeHelper.getCharge(stack) / (float) max);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x33CC55;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (type == Type.LIFE) {
            tooltipComponents.add(Component.translatable("item.verdant_charm.life.text").withStyle(ChatFormatting.GOLD));
        } else if (type == Type.SUSTAIN) {
            tooltipComponents.add(Component.translatable("item.verdant_charm.sustain.text").withStyle(ChatFormatting.GOLD));
        }
        int charge = TCRechargeHelper.getCharge(stack);
        int max = TCRechargeHelper.getMaxCharge(stack);
        tooltipComponents.add(Component.literal("Charge: " + charge + " / " + max).withStyle(ChatFormatting.DARK_GREEN));
    }
}

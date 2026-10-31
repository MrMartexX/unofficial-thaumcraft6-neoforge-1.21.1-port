package thaumcraft.common.items.tools;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thaumcraft.common.entities.TCGrappleEntity;
import thaumcraft.common.items.TCRechargeHelper;
import thaumcraft.common.registry.TCEntityTypes;

/**
 * TC6 Grappling Gun item.
 * Rechargable tool (100 max charge). Fires an anchor projectile that pulls the player
 * forward upon impact while negating fall damage.
 */
public class TCGrappleGunItem extends Item {
    public static final int MAX_CHARGE = 100;

    public TCGrappleGunItem(Item.Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        TCRechargeHelper.ensureInitialCharge(stack, MAX_CHARGE);

        if (!level.isClientSide) {
            boolean hasCharge = player.getAbilities().instabuild || TCRechargeHelper.getCharge(stack) > 0;
            if (hasCharge) {
                TCGrappleEntity grapple = new TCGrappleEntity(TCEntityTypes.GRAPPLE.get(), player, level);
                grapple.shootFromRotation(player, player.getXRot(), player.getYRot(), -5.0F, 1.5F, 0.0F);
                level.addFreshEntity(grapple);

                if (!player.getAbilities().instabuild) {
                    TCRechargeHelper.consumeCharge(stack, 1);
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.2F);
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return TCRechargeHelper.getCharge(stack) < MAX_CHARGE;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * TCRechargeHelper.getCharge(stack) / (float) MAX_CHARGE);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x2FD5E6;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int charge = TCRechargeHelper.getCharge(stack);
        tooltip.add(Component.translatable("item.charge")
                .append(": " + charge + " / " + MAX_CHARGE)
                .withStyle(ChatFormatting.YELLOW));
    }
}

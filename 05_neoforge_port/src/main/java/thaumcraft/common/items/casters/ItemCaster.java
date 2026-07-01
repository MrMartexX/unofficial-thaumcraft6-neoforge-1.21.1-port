package thaumcraft.common.items.casters;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import thaumcraft.api.casters.ICaster;
import thaumcraft.common.items.components.TCCasterFocusComponent;
import thaumcraft.common.items.components.TCFocusPackageComponent;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.world.aura.TCAuraNetwork;

public class ItemCaster extends Item implements ICaster {
    private final int area;

    public ItemCaster(int area) {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
        this.area = Math.max(0, area);
    }

    public int area() {
        return area;
    }

    public boolean hasFocus(ItemStack stack) {
        return stack.has(TCDataComponents.CASTER_FOCUS.get())
                && !stack.getOrDefault(TCDataComponents.CASTER_FOCUS.get(), TCCasterFocusComponent.EMPTY).isEmpty();
    }

    @Override
    public float getConsumptionModifier(ItemStack stack, Player player, boolean crafting) {
        return CasterManager.getCasterConsumptionModifier(player);
    }

    @Override
    public boolean consumeVis(ItemStack stack, Player player, float amount, boolean crafting, boolean simulate) {
        return CasterManager.consumeVis(this, stack, player, amount, crafting, simulate);
    }

    @Override
    public ItemStack getFocusStack(ItemStack stack) {
        TCCasterFocusComponent component = stack.getOrDefault(TCDataComponents.CASTER_FOCUS.get(), TCCasterFocusComponent.EMPTY);
        if (component.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return TCFocusPackageHelper.focusStack(component.focusItem(), component.customName(), component.packageData());
    }

    @Override
    public void setFocus(ItemStack stack, ItemStack focus) {
        if (focus.isEmpty()) {
            stack.remove(TCDataComponents.CASTER_FOCUS.get());
            return;
        }
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(focus.getItem());
        if (!(focus.getItem() instanceof ItemFocus) || id == null) {
            return;
        }
        TCFocusPackageComponent packageData = TCFocusPackageHelper.getPackage(focus);
        Component customName = focus.get(DataComponents.CUSTOM_NAME);
        stack.set(
                TCDataComponents.CASTER_FOCUS.get(),
                new TCCasterFocusComponent(id.toString(), customName == null ? "" : customName.getString(), packageData)
        );
    }

    @Override
    public ItemStack getPickedBlock(ItemStack stack) {
        return ItemStack.EMPTY;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ItemStack focus = getFocusStack(stack);
        if (focus.isEmpty() || !(focus.getItem() instanceof ItemFocus focusItem)) {
            return InteractionResultHolder.pass(stack);
        }
        if (level.isClientSide()) {
            return InteractionResultHolder.consume(stack);
        }
        if (!(player instanceof ServerPlayer serverPlayer) || CasterManager.isOnCooldown(serverPlayer)) {
            return InteractionResultHolder.consume(stack);
        }
        float visCost = focusItem.getVisCost(focus);
        if (!consumeVis(stack, serverPlayer, visCost, false, false)) {
            level.playSound(null, serverPlayer.blockPosition(), TCSounds.WANDFAIL.get(), SoundSource.PLAYERS, 0.4F, 1.0F);
            return InteractionResultHolder.fail(stack);
        }
        CasterManager.setCooldown(serverPlayer, stack, focusItem.getActivationTime(focus));
        level.playSound(null, serverPlayer.blockPosition(), TCSounds.WAND.get(), SoundSource.PLAYERS, 0.35F, 1.0F);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!level.isClientSide() && isSelected && entity instanceof ServerPlayer player && entity.tickCount % 10 == 0) {
            TCAuraNetwork.sendAuraToPlayer(player, player.blockPosition());
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, net.minecraft.world.entity.LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        ItemStack focus = getFocusStack(stack);
        if (focus.isEmpty()) {
            tooltipComponents.add(Component.translatable("tc.caster.no_focus").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }
        tooltipComponents.add(Component.translatable("tc.caster.focus", focus.getHoverName()).withStyle(ChatFormatting.DARK_PURPLE));
        if (focus.getItem() instanceof ItemFocus focusItem) {
            tooltipComponents.add(Component.translatable("tc.focus.vis_cost", focusItem.getVisCost(focus)).withStyle(ChatFormatting.GRAY));
        }
    }
}

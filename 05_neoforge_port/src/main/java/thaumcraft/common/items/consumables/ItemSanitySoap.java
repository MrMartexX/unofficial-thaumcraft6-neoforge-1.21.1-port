package thaumcraft.common.items.consumables;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCMobEffects;
import thaumcraft.common.warp.TCPlayerWarp;
import thaumcraft.common.warp.TCPlayerWarpStore;
import thaumcraft.common.warp.TCWarpManager;
import thaumcraft.common.warp.TCWarpType;

public class ItemSanitySoap extends Item {
    public static final int USE_DURATION = 100;
    public static final int CLEANSE_THRESHOLD_TICKS = 95;

    public ItemSanitySoap() {
        super(new Item.Properties());
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (getUseDuration(stack, livingEntity) - remainingUseDuration > CLEANSE_THRESHOLD_TICKS) {
            livingEntity.releaseUsingItem();
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int remainingUseDuration) {
        int usedTicks = getUseDuration(stack, livingEntity) - remainingUseDuration;
        if (usedTicks <= CLEANSE_THRESHOLD_TICKS || !(livingEntity instanceof Player player)) {
            return;
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            applyWarpCleansing(serverPlayer);
        }
    }

    public static boolean applyWarpCleansing(ServerPlayer player) {
        TCPlayerWarp before = TCPlayerWarpStore.get(player);
        int normal = before.get(TCWarpType.NORMAL);
        int temporary = before.get(TCWarpType.TEMPORARY);
        if (normal <= 0 && temporary <= 0) {
            return false;
        }

        int amount = legacyCleansingAmount(player);
        if (normal > 0) {
            TCWarpManager.reduce(player, TCWarpType.NORMAL, amount);
        }
        if (temporary > 0) {
            TCWarpManager.reduce(player, TCWarpType.TEMPORARY, temporary);
        }
        return true;
    }

    public static int legacyCleansingAmount(ServerPlayer player) {
        int amount = 1;
        if (player.hasEffect(TCMobEffects.WARP_WARD)) {
            amount++;
        }
        if (player.level().getBlockState(player.blockPosition()).is(TCBlocks.PURIFYING_FLUID.get())) {
            amount++;
        }
        return amount;
    }
}

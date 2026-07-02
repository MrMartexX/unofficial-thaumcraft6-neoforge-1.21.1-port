package thaumcraft.common.items.tools;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.common.items.components.TCMirrorLinkComponent;
import thaumcraft.common.menu.TCHandMirrorMenu;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.tiles.devices.TCMirrorBlockEntity;

/** Portable one-way sender for linked normal item mirrors. */
public final class ItemHandMirror extends Item {
    public ItemHandMirror() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.getBlockState(context.getClickedPos()).is(TCBlocks.MIRROR.get())) {
            return InteractionResult.PASS;
        }

        Player player = context.getPlayer();
        if (level.isClientSide) {
            if (player != null) {
                player.swing(context.getHand());
            }
            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(context.getClickedPos()) instanceof TCMirrorBlockEntity) {
            context.getItemInHand().set(TCDataComponents.MIRROR_LINK.get(), TCMirrorLinkComponent.of(level, context.getClickedPos()));
            level.playSound(null, context.getClickedPos(), TCSounds.JAR.get(), SoundSource.BLOCKS, 1.0F, 2.0F);
            if (player != null) {
                player.displayClientMessage(Component.translatable("tc.handmirrorlinked"), true);
                player.inventoryMenu.broadcastChanges();
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack mirror = player.getItemInHand(hand);
        if (!level.isClientSide && mirror.has(TCDataComponents.MIRROR_LINK.get()) && player instanceof ServerPlayer serverPlayer) {
            if (!targetMirrorExists(serverPlayer, mirror)) {
                breakLinkWithError(mirror, player);
                return InteractionResultHolder.success(mirror);
            }
            int selectedSlot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : -1;
            serverPlayer.openMenu(
                    new SimpleMenuProvider(
                            (containerId, inventory, menuPlayer) -> new TCHandMirrorMenu(containerId, inventory, hand, selectedSlot),
                            Component.translatable("container.thaumcraft.hand_mirror")
                    ),
                    buffer -> writeMenuData(buffer, hand, selectedSlot)
            );
        }
        return InteractionResultHolder.sidedSuccess(mirror, level.isClientSide);
    }

    private static void writeMenuData(RegistryFriendlyByteBuf buffer, InteractionHand hand, int selectedSlot) {
        buffer.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
        buffer.writeVarInt(selectedSlot);
    }

    public static boolean transport(ItemStack mirror, ItemStack items, Player player) {
        if (mirror == null || !mirror.has(TCDataComponents.MIRROR_LINK.get()) || items == null || items.isEmpty()) {
            return false;
        }
        if (!targetMirrorExists(player, mirror)) {
            breakLinkWithError(mirror, player);
            return false;
        }
        TCMirrorBlockEntity target = targetMirror(player, mirror);
        if (target != null && target.transportDirect(items)) {
            player.playSound(SoundEvents.ENDERMAN_TELEPORT, 0.1F, 1.0F);
            return true;
        }
        return false;
    }

    private static boolean targetMirrorExists(Player player, ItemStack mirror) {
        return targetMirror(player, mirror) != null;
    }

    private static TCMirrorBlockEntity targetMirror(Player player, ItemStack mirror) {
        if (player == null || mirror == null) {
            return null;
        }
        TCMirrorLinkComponent link = mirror.get(TCDataComponents.MIRROR_LINK.get());
        if (link == null || !(player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return null;
        }
        net.minecraft.server.level.ServerLevel targetLevel = serverLevel.getServer().getLevel(link.dimensionKey());
        if (targetLevel == null) {
            return null;
        }
        BlockEntity blockEntity = targetLevel.getBlockEntity(link.pos());
        return blockEntity instanceof TCMirrorBlockEntity mirrorBlock ? mirrorBlock : null;
    }

    private static void breakLinkWithError(ItemStack mirror, Player player) {
        mirror.remove(TCDataComponents.MIRROR_LINK.get());
        if (player != null) {
            player.playSound(TCSounds.ZAP.get(), 1.0F, 0.8F);
            player.displayClientMessage(Component.translatable("tc.handmirrorerror"), false);
            player.inventoryMenu.broadcastChanges();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        TCMirrorLinkComponent link = stack.get(TCDataComponents.MIRROR_LINK.get());
        if (link != null) {
            tooltipComponents.add(Component.translatable(
                    "tc.handmirrorlinkedto",
                    link.x(),
                    link.y(),
                    link.z(),
                    link.dimension()
            ).withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(TCDataComponents.MIRROR_LINK.get()) || super.isFoil(stack);
    }

    public static boolean isHandMirror(ItemStack stack) {
        return !stack.isEmpty() && stack.is(TCItems.HAND_MIRROR.get());
    }
}

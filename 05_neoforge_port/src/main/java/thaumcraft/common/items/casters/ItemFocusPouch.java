package thaumcraft.common.items.casters;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import thaumcraft.common.menu.TCFocusPouchMenu;

public class ItemFocusPouch extends Item {
    public static final int LEGACY_SLOT_COUNT = 18;

    public ItemFocusPouch() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : -1;
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new TCFocusPouchMenu(id, inv, hand, slot),
                    Component.translatable("item.thaumcraft.focus_pouch")
            ), buffer -> {
                buffer.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
                buffer.writeVarInt(slot);
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public static NonNullList<ItemStack> getInventory(ItemStack pouch) {
        NonNullList<ItemStack> list = NonNullList.withSize(LEGACY_SLOT_COUNT, ItemStack.EMPTY);
        if (pouch.isEmpty()) {
            return list;
        }
        ItemContainerContents contents = pouch.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        contents.copyInto(list);
        return list;
    }

    public static void setInventory(ItemStack pouch, List<ItemStack> items) {
        if (pouch.isEmpty()) {
            return;
        }
        pouch.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        NonNullList<ItemStack> items = getInventory(stack);
        int count = 0;
        for (ItemStack s : items) {
            if (!s.isEmpty()) {
                count++;
            }
        }
        tooltipComponents.add(Component.translatable("tc.focus_pouch.foci", count, LEGACY_SLOT_COUNT));
    }
}

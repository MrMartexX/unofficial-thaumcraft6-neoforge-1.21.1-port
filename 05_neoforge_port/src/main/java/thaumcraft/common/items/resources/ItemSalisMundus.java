package thaumcraft.common.items.resources;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import thaumcraft.common.crafting.TCSalisMundusActivation;

/** Legacy Salis Mundus dust item entry point for IDustTrigger-style block transformations. */
public final class ItemSalisMundus extends Item {
    public ItemSalisMundus() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON));
    }

    /**
     * Salis transformations must run before the clicked block handles the interaction. Vanilla
     * crafting tables otherwise open their menu and {@link #useOn(UseOnContext)} is never reached.
     */
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return activate(context, stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return activate(context, context.getItemInHand());
    }

    private static InteractionResult activate(UseOnContext context, ItemStack stack) {
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (context.getPlayer() != null
                && !context.getPlayer().mayUseItemAt(context.getClickedPos(), context.getClickedFace(), stack)) {
            return InteractionResult.FAIL;
        }

        TCSalisMundusActivation.Result result = TCSalisMundusActivation.tryActivate(context);
        if (!result.activated()) {
            return InteractionResult.PASS;
        }

        if (!context.getLevel().isClientSide
                && context.getPlayer() instanceof ServerPlayer player
                && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        if (context.getPlayer() != null) {
            context.getPlayer().swing(context.getHand());
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
}

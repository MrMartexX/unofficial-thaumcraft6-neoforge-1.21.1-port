package thaumcraft.common.items.casters;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.BlockEvent;
import thaumcraft.api.aura.AuraHelper;

public final class TCExchangeHelper {
    private record Active(net.minecraft.server.level.ServerLevel level, BlockPos pos) {}
    private static final java.util.Set<Active> ACTIVE = new java.util.HashSet<>();
    private TCExchangeHelper() {}

    public static boolean exchange(ServerPlayer player, BlockPos pos, Direction side, boolean silk, int fortune) {
        var level = player.serverLevel();
        if (!level.hasChunkAt(pos)) return false;
        ItemStack selected = selected(player);
        return exchangeSnapshot(player, pos, side, silk, fortune, level.getBlockState(pos), selected);
    }

    static ItemStack selected(ServerPlayer player) {
        ItemStack casterStack = player.getMainHandItem();
        if (!(casterStack.getItem() instanceof ItemCaster)) casterStack = player.getOffhandItem();
        return casterStack.getItem() instanceof ItemCaster caster ? caster.getPickedBlock(casterStack).copyWithCount(1) : ItemStack.EMPTY;
    }

    static boolean exchangeSnapshot(ServerPlayer player, BlockPos pos, Direction side, boolean silk, int fortune,
                                    net.minecraft.world.level.block.state.BlockState expected, ItemStack selected) {
        var level = player.serverLevel();
        if (!level.getServer().isSameThread()) return false;
        var key = new Active(level, pos.immutable());
        if (!ACTIVE.add(key)) return false;
        try { return replace(player, pos, side, silk, fortune, expected, selected); }
        finally { ACTIVE.remove(key); }
    }

    private static boolean replace(ServerPlayer player, BlockPos pos, Direction side, boolean silk, int fortune,
                                   net.minecraft.world.level.block.state.BlockState expected, ItemStack selected) {
        var level = player.serverLevel();
        if (!level.hasChunkAt(pos) || !level.mayInteract(player, pos) || player.isSpectator()
                || player.blockActionRestricted(level, pos, player.gameMode.getGameModeForPlayer())) return false;
        if (!(selected.getItem() instanceof BlockItem blockItem)) return false;
        var source = level.getBlockState(pos);
        var target = blockItem.getBlock().defaultBlockState();
        if (source != expected || source.isAir() || source.getDestroySpeed(level, pos) < 0 || source.getBlock() == target.getBlock()
                || target.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF)
                || target.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.BED_PART)
                || !player.mayUseItemAt(pos, side, selected)
                || !target.canSurvive(level, pos)) return false;
        fortune = Math.max(0, Math.min(4, fortune));
        float cost = 0.25F + (silk ? 0.25F : 0) + fortune * 0.1F;
        if (AuraHelper.getVis(level, pos) < cost) return false;
        int slot = -1;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (ItemStack.isSameItemSameComponents(selected, player.getInventory().getItem(i))) { slot = i; break; }
        }
        if (!player.isCreative() && slot < 0) return false;
        if (NeoForge.EVENT_BUS.post(new BlockEvent.BreakEvent(level, pos, source, player)).isCanceled()
                || EventHooks.onBlockPlace(player, BlockSnapshot.create(level.dimension(), level, pos), side)
                || level.getBlockState(pos) != source) return false;
        ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
        var enchantments = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        final int fortuneLevel = fortune;
        EnchantmentHelper.updateEnchantments(tool, mutable -> {
            if (silk) mutable.set(enchantments.getOrThrow(Enchantments.SILK_TOUCH), 1);
            if (fortuneLevel > 0) mutable.set(enchantments.getOrThrow(Enchantments.FORTUNE), fortuneLevel);
        });
        List<ItemStack> drops = player.isCreative() ? List.of()
                : Block.getDrops(source, level, pos, level.getBlockEntity(pos), player, tool);
        if (level.getBlockState(pos) != source || AuraHelper.getVis(level, pos) < cost) return false;
        ItemStack escrow = ItemStack.EMPTY;
        if (!player.isCreative()) {
            if (slot < 0 || !ItemStack.isSameItemSameComponents(selected, player.getInventory().getItem(slot))) return false;
            float drained = AuraHelper.drainVis(level, pos, cost, false);
            if (drained < cost) {
                AuraHelper.addVis(level, pos, drained);
                return false;
            }
            escrow = player.getInventory().getItem(slot).split(1);
        }
        boolean placed = false;
        try {
            placed = level.setBlock(pos, target, 3);
        } finally {
            if (!placed && !player.isCreative()) {
                AuraHelper.addVis(level, pos, cost);
                giveOrDrop(player, pos, escrow);
            }
        }
        if (!placed) return false;
        if (!player.isCreative()) {
            for (ItemStack drop : drops) {
                giveOrDrop(player, pos, drop);
            }
            player.getInventory().setChanged();
        }
        TCExchangeQueue.sendEffects(level, pos);
        return true;
    }

    private static void giveOrDrop(ServerPlayer player, BlockPos pos, ItemStack stack) {
        if (!stack.isEmpty() && !player.getInventory().add(stack)) player.serverLevel().addFreshEntity(
                new net.minecraft.world.entity.item.ItemEntity(player.level(), pos.getX() + .5,
                        pos.getY() + .5, pos.getZ() + .5, stack));
    }
}

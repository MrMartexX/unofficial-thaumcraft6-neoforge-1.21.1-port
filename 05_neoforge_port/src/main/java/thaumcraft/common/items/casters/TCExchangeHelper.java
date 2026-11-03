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
    private TCExchangeHelper() {}

    public static boolean exchange(ServerPlayer player, BlockPos pos, Direction side, boolean silk, int fortune) {
        var level = player.serverLevel();
        if (!level.hasChunkAt(pos) || !level.mayInteract(player, pos) || player.isSpectator()
                || player.blockActionRestricted(level, pos, player.gameMode.getGameModeForPlayer())) return false;
        ItemStack casterStack = player.getMainHandItem();
        if (!(casterStack.getItem() instanceof ItemCaster)) casterStack = player.getOffhandItem();
        if (!(casterStack.getItem() instanceof ItemCaster caster)) return false;
        ItemStack selected = caster.getPickedBlock(casterStack);
        if (!(selected.getItem() instanceof BlockItem blockItem)) return false;
        var source = level.getBlockState(pos);
        var target = blockItem.getBlock().defaultBlockState();
        if (source.isAir() || source.getDestroySpeed(level, pos) < 0 || source.getBlock() == target.getBlock()
                || !player.mayUseItemAt(pos, side, selected)
                || !target.canSurvive(level, pos)) return false;
        int slot = -1;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (ItemStack.isSameItemSameComponents(selected, player.getInventory().getItem(i))) { slot = i; break; }
        }
        if (!player.isCreative() && slot < 0) return false;
        if (NeoForge.EVENT_BUS.post(new BlockEvent.BreakEvent(level, pos, source, player)).isCanceled()
                || EventHooks.onBlockPlace(player, BlockSnapshot.create(level.dimension(), level, pos), side)
                || level.getBlockState(pos) != source) return false;
        fortune = Math.max(0, Math.min(4, fortune));
        float cost = 0.25F + (silk ? 0.25F : 0) + fortune * 0.1F;
        ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
        var enchantments = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        final int fortuneLevel = fortune;
        EnchantmentHelper.updateEnchantments(tool, mutable -> {
            if (silk) mutable.set(enchantments.getOrThrow(Enchantments.SILK_TOUCH), 1);
            else if (fortuneLevel > 0) mutable.set(enchantments.getOrThrow(Enchantments.FORTUNE), fortuneLevel);
        });
        List<ItemStack> drops = player.isCreative() ? List.of()
                : Block.getDrops(source, level, pos, level.getBlockEntity(pos), player, tool);
        if (!player.isCreative()) {
            if (slot < 0 || !ItemStack.isSameItemSameComponents(selected, player.getInventory().getItem(slot))) return false;
            if (AuraHelper.getVis(level, pos) < cost) return false;
            float drained = AuraHelper.drainVis(level, pos, cost, false);
            if (drained < cost) {
                AuraHelper.addVis(level, pos, drained);
                return false;
            }
        }
        if (!level.setBlock(pos, target, 3)) {
            if (!player.isCreative()) AuraHelper.addVis(level, pos, cost);
            return false;
        }
        if (!player.isCreative()) {
            player.getInventory().getItem(slot).shrink(1);
            for (ItemStack drop : drops) {
                if (!player.getInventory().add(drop)) Block.popResource(level, pos, drop);
            }
            player.getInventory().setChanged();
        }
        return true;
    }
}

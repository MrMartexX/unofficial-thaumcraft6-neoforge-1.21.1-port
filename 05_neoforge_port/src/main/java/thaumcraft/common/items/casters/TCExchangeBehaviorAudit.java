package thaumcraft.common.items.casters;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import thaumcraft.api.aura.AuraHelper;

/** Destructive fixtures: invoked only by the opt-in server parity audit. */
final class TCExchangeBehaviorAudit {
    static void run(ServerLevel level, List<TCFocusMediumEffectParityAudit.Row> rows,
                    ItemStack casterTemplate, ItemStack focus) {
        var player = FakePlayerFactory.getMinecraft(level);
        var inventory = player.getInventory();
        var saved = new ArrayList<ItemStack>();
        for (int i = 0; i < inventory.getContainerSize(); i++) saved.add(inventory.getItem(i).copy());
        var mode = player.gameMode.getGameModeForPlayer();
        BlockPos pos = level.getSharedSpawnPos().above(100);
        level.getChunkAt(pos);
        var source = level.getBlockState(pos);
        if (level.getBlockEntity(pos) != null) throw new IllegalStateException("Exchange fixture overlaps a block entity");
        float vis = AuraHelper.getVis(level, pos);
        try {
            inventory.clearContent();
            ItemStack stack = casterTemplate.copy();
            ItemCaster caster = (ItemCaster) stack.getItem();
            caster.setFocus(stack, focus.copy());
            caster.storePickedBlock(stack, new ItemStack(Blocks.OAK_PLANKS));
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            player.gameMode.changeGameModeForPlayer(GameType.CREATIVE);
            AuraHelper.addVis(level, pos, 20);
            level.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
            boolean queued = TCExchangeQueue.enqueue(player, pos, Direction.UP, false, 0)
                    && level.getBlockState(pos).is(Blocks.STONE);
            caster.storePickedBlock(stack, new ItemStack(Blocks.DIRT));
            TCExchangeQueue.tick(new LevelTickEvent.Post(() -> true, level));
            row(rows, "exchange_end_tick_selection_snapshot", queued && level.getBlockState(pos).is(Blocks.OAK_PLANKS),
                    "No immediate mutation; queued material survives a later selection change");
            level.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
            TCExchangeQueue.enqueue(player, pos, Direction.UP, false, 0);
            level.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 3);
            float before = AuraHelper.getVis(level, pos);
            TCExchangeQueue.tick(new LevelTickEvent.Post(() -> true, level));
            row(rows, "exchange_stale_source_rejected", level.getBlockState(pos).is(Blocks.COBBLESTONE)
                    && AuraHelper.getVis(level, pos) == before, "Changed source cancels queued replacement without vis consumption");
            AuraHelper.drainVis(level, pos, AuraHelper.getVis(level, pos), false);
            row(rows, "exchange_creative_minimum_vis", !TCExchangeHelper.exchange(player, pos, Direction.UP, false, 0)
                    && level.getBlockState(pos).is(Blocks.COBBLESTONE), "Creative also requires minimum vis, as the legacy queue did");
            AuraHelper.addVis(level, pos, 20);
            player.gameMode.changeGameModeForPlayer(GameType.SURVIVAL);
            caster.storePickedBlock(stack, new ItemStack(Blocks.OAK_PLANKS));
            inventory.setItem(9, new ItemStack(Blocks.OAK_PLANKS, 3));
            level.setBlock(pos, Blocks.GRAVEL.defaultBlockState(), 3);
            boolean gravel = TCExchangeHelper.exchange(player, pos, Direction.UP, false, 4);
            row(rows, "exchange_fortune_gravel", gravel && inventory.countItem(Items.FLINT) == 1
                    && inventory.countItem(Items.GRAVEL) == 0 && inventory.getItem(9).getCount() == 2,
                    "Fortune IV guarantees flint when silk is not selected; gravel does support silk");
            level.setBlock(pos, Blocks.DIAMOND_ORE.defaultBlockState(), 3);
            boolean ore = TCExchangeHelper.exchange(player, pos, Direction.UP, true, 4);
            row(rows, "exchange_supported_silk_precedes_fortune", ore && inventory.countItem(Items.DIAMOND_ORE) == 1
                    && inventory.countItem(Items.DIAMOND) == 0, "Ore silk branch wins over Fortune without granting ore XP");
            caster.storePickedBlock(stack, new ItemStack(Blocks.OAK_DOOR));
            inventory.setItem(10, new ItemStack(Blocks.OAK_DOOR));
            level.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
            before = AuraHelper.getVis(level, pos);
            row(rows, "exchange_multiblock_target_rejected", !TCExchangeHelper.exchange(player, pos, Direction.UP, false, 0)
                    && inventory.getItem(10).getCount() == 1 && AuraHelper.getVis(level, pos) == before
                    && level.getBlockState(pos).is(Blocks.STONE), "A single-state swap must not manufacture an incomplete door");
            caster.storePickedBlock(stack, new ItemStack(Blocks.OAK_PLANKS));
            final boolean[] nestedRejected = {false};
            java.util.function.Consumer<net.neoforged.neoforge.event.level.BlockEvent.BreakEvent> cancel = event -> {
                if (!event.getPos().equals(pos)) return;
                nestedRejected[0] = !TCExchangeHelper.exchange(player, pos, Direction.UP, false, 0);
                event.setCanceled(true);
            };
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(cancel);
            boolean canceled;
            try { canceled = !TCExchangeHelper.exchange(player, pos, Direction.UP, false, 0); }
            finally { net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(cancel); }
            row(rows, "exchange_cancel_and_reentry", canceled && nestedRejected[0]
                    && inventory.getItem(9).getCount() == 1 && AuraHelper.getVis(level, pos) == before
                    && level.getBlockState(pos).is(Blocks.STONE), "Canceled break and same-position recursive callback mutate no currencies or block");
            var buffer = new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), level.registryAccess());
            boolean codec;
            try {
                var payload = new TCExchangeFXPayload(level.dimension(), pos);
                TCExchangeFXPayload.STREAM_CODEC.encode(buffer, payload);
                codec = payload.equals(TCExchangeFXPayload.STREAM_CODEC.decode(buffer)) && !buffer.isReadable();
            } finally { buffer.release(); }
            row(rows, "exchange_fx_payload_codec", codec, "Dimension and immutable block position survive the actual registry-friendly payload codec");
            boolean fx = true;
            for (int seed = 0; seed < 128; seed++) {
                var burst = TCExchangeBamf.generate(net.minecraft.util.RandomSource.create(seed));
                fx &= burst.particles().size() >= 8 && burst.particles().size() <= 12;
                for (var particle : burst.particles()) fx &= Double.isFinite(particle.x())
                        && Float.isFinite(particle.rotationStart()) && particle.rotationDegrees() != 0;
            }
            row(rows, "exchange_bamf_seeded_descriptors", fx,
                    "128 seeded sound/puff/curly bursts have bounded populations, finite offsets and explicit legacy rotation");
            boolean crop = true;
            var tool = new ItemStack(Items.DIAMOND_PICKAXE);
            var enchantments = level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
            net.minecraft.world.item.enchantment.EnchantmentHelper.updateEnchantments(tool,
                    mutable -> mutable.set(enchantments.getOrThrow(net.minecraft.world.item.enchantment.Enchantments.FORTUNE), 4));
            var sequence = level.getRandomSequence(net.minecraft.resources.ResourceLocation.withDefaultNamespace("blocks/wheat"));
            for (int seed = 0; seed < 32; seed++) {
                inventory.clearContent();
                player.setItemInHand(InteractionHand.MAIN_HAND, stack);
                inventory.setItem(9, new ItemStack(Blocks.OAK_PLANKS));
                var wheat = Blocks.WHEAT.defaultBlockState().setValue(net.minecraft.world.level.block.CropBlock.AGE, 7);
                level.setBlock(pos, wheat, 2);
                sequence.setSeed(seed);
                var expected = net.minecraft.world.level.block.Block.getDrops(wheat, level, pos, null, player, tool);
                int seeds = expected.stream().filter(s -> s.is(Items.WHEAT_SEEDS)).mapToInt(ItemStack::getCount).sum();
                sequence.setSeed(seed);
                AuraHelper.addVis(level, pos, 2);
                crop &= TCExchangeHelper.exchange(player, pos, Direction.UP, true, 4)
                        && inventory.countItem(Items.WHEAT) == 1 && inventory.countItem(Items.WHEAT_SEEDS) == seeds;
            }
            row(rows, "exchange_unsupported_silk_fortune_oracle", crop,
                    "32 seeded mature-wheat swaps with silk+Fortune IV match Fortune-only vanilla loot using the same loot random sequence");
            inventory.setItem(9, new ItemStack(Blocks.OAK_PLANKS, 2));
            level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
            ((net.minecraft.world.level.block.entity.ChestBlockEntity)level.getBlockEntity(pos))
                    .setItem(0, new ItemStack(Items.DIAMOND, 4));
            int[] diamonds = {0};
            java.util.function.Consumer<net.neoforged.neoforge.event.entity.EntityJoinLevelEvent> capture = event -> {
                if (event.getLevel() == level && event.getEntity() instanceof net.minecraft.world.entity.item.ItemEntity item
                        && item.position().distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(pos)) < 4) {
                    if (item.getItem().is(Items.DIAMOND)) diamonds[0] += item.getItem().getCount();
                    event.setCanceled(true);
                }
            };
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(capture);
            boolean chest;
            try { chest = TCExchangeHelper.exchange(player, pos, Direction.UP, false, 0); }
            finally { net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(capture); }
            row(rows, "exchange_container_contents_conserved", chest && diamonds[0] == 4
                    && inventory.countItem(Items.CHEST) == 1 && level.getBlockEntity(pos) == null,
                    "Chest replacement returns its block item and drops all four stored diamonds through the actual removal callback");
            level.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
            inventory.setItem(9, new ItemStack(Blocks.OAK_PLANKS, 2));
            boolean[] observed = {false};
            java.util.function.Consumer<net.neoforged.neoforge.event.level.BlockEvent.NeighborNotifyEvent> moveSlot = event -> {
                if (event.getLevel() != level || !event.getPos().equals(pos) || !level.getBlockState(pos).is(Blocks.OAK_PLANKS)) return;
                observed[0] = true;
                inventory.setItem(11, inventory.getItem(9));
                inventory.setItem(9, new ItemStack(Items.EMERALD, 3));
            };
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(moveSlot);
            boolean callback;
            try { callback = TCExchangeHelper.exchange(player, pos, Direction.UP, false, 0); }
            finally { net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(moveSlot); }
            row(rows, "exchange_material_escrow_before_callbacks", callback && observed[0]
                    && inventory.getItem(11).is(Blocks.OAK_PLANKS.asItem()) && inventory.getItem(11).getCount() == 1
                    && inventory.getItem(9).is(Items.EMERALD) && inventory.getItem(9).getCount() == 3,
                    "Actual neighbor callback moves the source slot: one plank was already escrowed and the replacement emerald stack is untouched");
            for (int i = 0; i < inventory.getContainerSize(); i++) inventory.setItem(i, new ItemStack(Items.DIAMOND, 64));
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            inventory.setItem(9, new ItemStack(Blocks.OAK_PLANKS, 64));
            level.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
            int[] overflow = {0};
            boolean[] centered = {true};
            java.util.function.Consumer<net.neoforged.neoforge.event.entity.EntityJoinLevelEvent> catchOverflow = event -> {
                if (event.getLevel() == level && event.getEntity() instanceof net.minecraft.world.entity.item.ItemEntity item
                        && item.getItem().is(Items.COBBLESTONE)
                        && item.position().distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(pos)) < 4) {
                    overflow[0] += item.getItem().getCount();
                    centered[0] &= item.position().equals(net.minecraft.world.phys.Vec3.atCenterOf(pos)) && !item.hasPickUpDelay();
                    event.setCanceled(true);
                }
            };
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(catchOverflow);
            boolean full;
            try { full = TCExchangeHelper.exchange(player, pos, Direction.UP, false, 0); }
            finally { net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(catchOverflow); }
            row(rows, "exchange_overflow_center_and_pickup", full && overflow[0] == 1 && centered[0]
                    && inventory.getItem(9).getCount() == 63,
                    "Full inventory emits one actual item entity at exact block center with no invented pickup delay");
            level.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
            boolean pending = TCExchangeQueue.enqueue(player, pos, Direction.UP, false, 0);
            TCExchangeQueue.unload(new net.neoforged.neoforge.event.level.LevelEvent.Unload(level));
            before = AuraHelper.getVis(level, pos);
            TCExchangeQueue.tick(new LevelTickEvent.Post(() -> true, level));
            row(rows, "exchange_unload_clears_pending", pending && level.getBlockState(pos).is(Blocks.STONE)
                    && AuraHelper.getVis(level, pos) == before && inventory.getItem(9).getCount() == 63,
                    "World-unload handler discards pending work before a later Post event without consuming currencies");
        } finally {
            TCExchangeQueue.tick(new LevelTickEvent.Post(() -> true, level));
            level.setBlock(pos, source, 3);
            AuraHelper.drainVis(level, pos, AuraHelper.getVis(level, pos), false);
            AuraHelper.addVis(level, pos, vis);
            for (int i = 0; i < saved.size(); i++) inventory.setItem(i, saved.get(i));
            player.gameMode.changeGameModeForPlayer(mode);
        }
    }
    private static void row(List<TCFocusMediumEffectParityAudit.Row> rows, String name, boolean pass, String details) {
        rows.add(new TCFocusMediumEffectParityAudit.Row(name, pass, details));
    }
}

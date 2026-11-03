package thaumcraft.common.golems;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import thaumcraft.api.golems.seals.*;
import thaumcraft.common.golems.seals.*;
import thaumcraft.common.menu.TCSealMenu;

final class TCSealMenuAudit {
    static void run(ServerLevel level, List<TCGolemBehaviorAudit.Row> rows) {
        if (level == null) throw new IllegalStateException("Seal menu audit needs a server world");
        var player = FakePlayerFactory.get(level, new com.mojang.authlib.GameProfile(
                java.util.UUID.nameUUIDFromBytes("TCSealMenuAudit".getBytes(java.nio.charset.StandardCharsets.UTF_8)), "TCSealAudit"));
        var oldPos = player.position();
        BlockPos pos = new BlockPos(1055, 180, 1055);
        level.getChunkAt(pos);
        SealPos sealPos = new SealPos(pos, Direction.UP);
        if (SealHandler.getSealEntity(level, sealPos) != null) throw new IllegalStateException("Audit position occupied");
        boolean defaults = true;
        for (Direction face : Direction.values()) {
            var seal = new SealEntity(level, new SealPos(pos, face), new SealPickup());
            defaults &= seal.getColor() == 0 && seal.getArea().equals(new BlockPos(face.getStepX() == 0 ? 3 : 1,
                    face.getStepY() == 0 ? 3 : 1, face.getStepZ() == 0 ? 3 : 1));
        }
        rows.add(new TCGolemBehaviorAudit.Row("seal_legacy_initial_area_and_color", defaults, "All six faces use legacy 3x3 tangential area and neutral color zero"));
        try {
            player.setPos(pos.getX() + .5, pos.getY(), pos.getZ() + .5);
            SealHandler.addSealEntity(level, pos, Direction.UP, new SealStock(), player);
            SealEntity seal = SealHandler.getSealEntity(level, sealPos);
            TCSealMenu menu = new TCSealMenu(1, player.getInventory(), seal);
            boolean bounds = menu.stillValid(player);
            for (int i = 0; i < menu.categories.length; i++) if (menu.categories[i] == 0) menu.clickMenuButton(player, i);
            for (int i = 0; i < 20; i++) menu.clickMenuButton(player, 81);
            bounds &= seal.getPriority() == 5;
            for (int i = 0; i < 20; i++) menu.clickMenuButton(player, 80);
            bounds &= seal.getPriority() == -5;
            for (int i = 0; i < 20; i++) menu.clickMenuButton(player, 83);
            bounds &= seal.getColor() == 16;
            for (int i = 0; i < 20; i++) menu.clickMenuButton(player, 82);
            bounds &= seal.getColor() == 0;
            rows.add(new TCGolemBehaviorAudit.Row("seal_menu_legacy_bounds", bounds, "Vanilla menu actions enforce priority -5..5 and color 0..16"));
            int filterCategory = -1;
            for (int i = 0; i < menu.categories.length; i++) if (menu.categories[i] == 1) filterCategory = i;
            menu.clickMenuButton(player, filterCategory);
            menu.setCarried(new ItemStack(Items.IRON_INGOT, 7));
            menu.clicked(0, 0, ClickType.PICKUP, player);
            ISealConfigFilter filter = (ISealConfigFilter)seal.getSeal();
            boolean ghost = menu.getCarried().getCount() == 7 && filter.getFilterSlot(0).is(Items.IRON_INGOT)
                    && filter.getFilterSlot(0).getCount() == 1 && filter.getFilterSlotSize(0) == 0;
            menu.clicked(0, 0, ClickType.PICKUP, player);
            ghost &= filter.getFilterSlotSize(0) == 7 && menu.getCarried().getCount() == 7;
            menu.setCarried(ItemStack.EMPTY);
            menu.clicked(0, 0, ClickType.QUICK_MOVE, player);
            ghost &= filter.getFilterSlotSize(0) == 17;
            menu.clicked(0, 1, ClickType.QUICK_MOVE, player);
            ghost &= filter.getFilterSlotSize(0) == 7;
            menu.clicked(0, 0, ClickType.SWAP, player);
            ghost &= filter.getFilterSlotSize(0) == 7 && filter.getFilterSlot(0).getCount() == 1;
            rows.add(new TCGolemBehaviorAudit.Row("seal_menu_ghost_filters", ghost, "Ghost copy never consumes cursor items; legacy size increment/decrement and swap rejection"));
            var buffer = new RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), level.registryAccess());
            boolean wire;
            try {
                var packet = new TCSealSyncPayload(level.dimension(), TCSealNetwork.UPSERT, seal.writeNBT());
                TCSealSyncPayload.STREAM_CODEC.encode(buffer, packet);
                var decoded = TCSealSyncPayload.STREAM_CODEC.decode(buffer);
                wire = decoded.dimension().equals(packet.dimension()) && decoded.operation() == packet.operation() && decoded.data().equals(packet.data());
            } finally { buffer.release(); }
            rows.add(new TCGolemBehaviorAudit.Row("seal_sync_wire_roundtrip", wire, "Dimension, owner, filters, quotas and seal state survive the actual registered codec"));
            ItemStack potion = new ItemStack(Items.POTION);
            potion.set(net.minecraft.core.component.DataComponents.POTION_CONTENTS,
                    new net.minecraft.world.item.alchemy.PotionContents(net.minecraft.world.item.alchemy.Potions.HEALING));
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            book.enchant(level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                    .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.FORTUNE), 2);
            filter.setFilterSlot(1, potion);
            filter.setFilterSlot(2, book);
            var restoredSeal = new SealEntity();
            restoredSeal.readNBT(seal.writeNBT(level.registryAccess()), level.registryAccess());
            var restoredFilter = (ISealConfigFilter)restoredSeal.getSeal();
            rows.add(new TCGolemBehaviorAudit.Row("seal_filter_dynamic_components_persist",
                    ItemStack.matches(potion, restoredFilter.getFilterSlot(1)) && ItemStack.matches(book, restoredFilter.getFilterSlot(2)),
                    "Potion and enchantment holders serialize against the actual server registries, without silent filter loss"));
            byte before = seal.getPriority();
            seal.setOwner(java.util.UUID.randomUUID().toString());
            boolean denied = !menu.stillValid(player) && !menu.clickMenuButton(player, 81) && seal.getPriority() == before;
            seal.setOwner(player.getStringUUID());
            player.setPos(pos.getX() + 30, pos.getY(), pos.getZ());
            denied &= !menu.stillValid(player) && !menu.clickMenuButton(player, 81);
            player.setPos(pos.getX() + .5, pos.getY(), pos.getZ() + .5);
            SealHandler.removeSealEntity(level, sealPos, true);
            denied &= !menu.stillValid(player) && !menu.clickMenuButton(player, 81);
            rows.add(new TCGolemBehaviorAudit.Row("seal_menu_authority", denied, "Foreign owner, out-of-range player and removed/replaced seals cannot mutate the original menu target"));
            SealHandler.addSealEntity(level, pos, Direction.UP, new SealGuardAdvanced(), player);
            SealEntity guard = SealHandler.getSealEntity(level, sealPos);
            TCSealMenu guardMenu = new TCSealMenu(2, player.getInventory(), guard);
            for (int i = 0; i < guardMenu.categories.length; i++) if (guardMenu.categories[i] == 3) guardMenu.clickMenuButton(player, i);
            boolean toggled = guardMenu.clickMenuButton(player, 60) && !((ISealConfigToggles)guard.getSeal()).getToggles()[0].getValue()
                    && guardMenu.clickMenuButton(player, 30) && ((ISealConfigToggles)guard.getSeal()).getToggles()[0].getValue();
            for (int i = 0; i < 20; i++) guardMenu.clickMenuButton(player, 93);
            toggled &= guard.getArea().getX() == 8;
            for (int i = 0; i < 20; i++) guardMenu.clickMenuButton(player, 92);
            toggled &= guard.getArea().getX() == 1;
            rows.add(new TCGolemBehaviorAudit.Row("seal_menu_toggle_and_area_bounds", toggled, "Legacy desired-value toggle actions and 1..8 area bounds"));
            BlockPos powerPos = pos.above(2);
            var oldPower = level.getBlockState(powerPos);
            var task = new thaumcraft.api.golems.tasks.Task(sealPos, pos);
            thaumcraft.common.golems.tasks.TaskHandler.addTask(level, task);
            try {
                guard.setRedstoneSensitive(true);
                level.setBlock(powerPos, net.minecraft.world.level.block.Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
                guard.tickSealEntity(level);
                rows.add(new TCGolemBehaviorAudit.Row("seal_redstone_face_side_suspends_tasks", guard.isStoppedByRedstone(level) && task.isSuspended(),
                        "Power at the attached face, not just support block, suspends queued work on the stopped transition"));
            } finally {
                level.setBlock(powerPos, oldPower, 3);
                thaumcraft.common.golems.tasks.TaskHandler.getTasks(level).remove(task.getId());
            }
        } finally {
            player.setPos(oldPos);
            SealHandler.removeSealEntity(level, sealPos, true);
        }
    }
}

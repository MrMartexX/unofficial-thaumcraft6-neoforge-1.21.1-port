package thaumcraft.common.golems;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.ProvisionRequest;
import thaumcraft.api.golems.parts.*;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.entities.TCGolemEntity;
import thaumcraft.common.golems.seals.*;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.registry.TCEntityTypes;

final class TCSealActionAudit {
    static void run(ServerLevel level, List<TCGolemBehaviorAudit.Row> rows) {
        BlockPos source = new BlockPos(1068, 180, 1060), support = source.above(), leverPos = source.above(2), orePos = leverPos.east(3), emptyPos = leverPos.east(4);
        level.getChunkAt(source);
        Map<BlockPos, net.minecraft.world.level.block.state.BlockState> oldBlocks = new LinkedHashMap<>();
        Map<BlockPos, net.minecraft.nbt.CompoundTag> oldData = new HashMap<>();
        for (BlockPos pos : List.of(source, support, leverPos, orePos, emptyPos)) {
            oldBlocks.put(pos, level.getBlockState(pos));
            if (level.getBlockEntity(pos) != null) oldData.put(pos, level.getBlockEntity(pos).saveWithFullMetadata(level.registryAccess()));
        }
        var previousRequests = GolemHelper.provisionRequests.get(level.dimension());
        GolemHelper.provisionRequests.put(level.dimension(), new ArrayList<>());
        Set<Integer> previousTasks = new HashSet<>(TaskHandler.getTasks(level).keySet());
        TCGolemEntity golem = new TCGolemEntity(TCEntityTypes.GOLEM.get(), level);
        var props = new GolemProperties();
        for (GolemHead head : GolemHead.getHeads()) if (Arrays.asList(head.traits).contains(EnumGolemTrait.SMART)) { props.setHead(head); break; }
        for (GolemArm arms : GolemArm.getArms()) if (Arrays.asList(arms.traits).contains(EnumGolemTrait.DEFT)) { props.setArms(arms); break; }
        golem.setProperties(props);
        golem.setPos(leverPos.getX() + 2.5, leverPos.getY(), leverPos.getZ() + .5);
        try {
            level.setBlock(support, Blocks.STONE.defaultBlockState(), 3);
            level.setBlock(leverPos, Blocks.LEVER.defaultBlockState().setValue(BlockStateProperties.ATTACH_FACE,
                    net.minecraft.world.level.block.state.properties.AttachFace.FLOOR), 3);
            SealPos usePos = new SealPos(leverPos, Direction.UP);
            SealUse use = new SealUse();
            use.setToggle(6, true);
            Task right = new Task(usePos, leverPos);
            boolean emptyHand = use.canGolemPerformTask(golem, right);
            use.onTaskCompletion(level, golem, right);
            emptyHand &= level.getBlockState(leverPos).is(Blocks.LEVER) && level.getBlockState(leverPos).getValue(BlockStateProperties.POWERED)
                    && golem.getCarrying().isEmpty();
            rows.add(new TCGolemBehaviorAudit.Row("seal_use_empty_hand_right_click", emptyHand, "Empty-hand mode activates a real lever without inventing or consuming a held item"));
            level.setBlock(orePos, Blocks.REDSTONE_ORE.defaultBlockState(), 3);
            use.setToggle(4, true);
            Task left = new Task(new SealPos(orePos, Direction.UP), orePos);
            use.onTaskCompletion(level, golem, left);
            rows.add(new TCGolemBehaviorAudit.Row("seal_use_left_click_pipeline", level.getBlockState(orePos).is(Blocks.REDSTONE_ORE)
                    && level.getBlockState(orePos).getValue(BlockStateProperties.LIT),
                    "Left-click uses the real game-mode START_DESTROY_BLOCK pipeline, including redstone-ore activation"));
            use.setToggle(4, false);
            use.setToggle(6, false);
            use.setToggle(7, true);
            golem.holdItem(new ItemStack(Items.BOOK));
            boolean[] sneaking = {false};
            java.util.function.Consumer<net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock> observer = event -> {
                if (event.getPos().equals(leverPos)) sneaking[0] = event.getEntity().isShiftKeyDown() && event.getItemStack().is(Items.BOOK);
            };
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(observer);
            try { use.onTaskCompletion(level, golem, new Task(usePos, leverPos)); }
            finally { net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(observer); }
            rows.add(new TCGolemBehaviorAudit.Row("seal_use_sneak_and_remainders", sneaking[0] && golem.getCarrying().stream().anyMatch(stack -> stack.is(Items.BOOK)),
                    "Sneak mode reaches the actual interaction event and returns the unused book"));
            golem.getCarrying().clear();
            SealUse provision = new SealUse();
            provision.setBlacklist(false);
            provision.setFilterSlot(0, new ItemStack(Items.IRON_INGOT));
            provision.setToggle(8, true);
            if (SealHandler.getSealEntity(level, usePos) != null) throw new IllegalStateException("Audit seal occupied");
            SealHandler.addSealEntity(level, leverPos, Direction.UP, provision, null);
            boolean requested = !provision.canGolemPerformTask(golem, new Task(usePos, leverPos))
                    && GolemHelper.provisionRequests.get(level.dimension()).stream().anyMatch(request -> request.getSeal() != null && request.getStack().is(Items.IRON_INGOT));
            rows.add(new TCGolemBehaviorAudit.Row("seal_use_whitelist_provisioning", requested, "Missing whitelist tool generates a real seal provisioning request"));
            SealUse air = new SealUse();
            air.setToggle(5, true);
            air.setToggle(6, true);
            level.setBlock(emptyPos, Blocks.AIR.defaultBlockState(), 3);
            var airSeal = new SealEntity(level, new SealPos(emptyPos, Direction.UP), air);
            for (int i = 0; i < 5; i++) air.tickSeal(level, airSeal);
            rows.add(new TCGolemBehaviorAudit.Row("seal_use_empty_target_option", TaskHandler.getTasks(level).values().stream().anyMatch(task -> airSeal.getSealPos().equals(task.getSealPos())),
                    "Empty-target mode queues work against air rather than always rejecting it"));
            level.setBlock(source, Blocks.CHEST.defaultBlockState(), 3);
            var chest = (net.minecraft.world.level.block.entity.ChestBlockEntity)level.getBlockEntity(source);
            chest.clearContent();
            chest.setItem(0, new ItemStack(Items.IRON_INGOT, 2));
            SealProvide provider = new SealProvide();
            provider.setToggle(4, true);
            provider.setToggle(5, true);
            var request = new ProvisionRequest(source.east(6), Direction.UP, new ItemStack(Items.IRON_INGOT, 3));
            Task pickup = new Task(new SealPos(source, Direction.UP), source);
            pickup.setLinkedProvision(request);
            request.setLinkedTask(pickup);
            provider.onTaskCompletion(level, golem, pickup);
            Task delivery = request.getLinkedTask();
            boolean limits = chest.getItem(0).getCount() == 1 && golem.getCarrying().size() == 1
                    && golem.getCarrying().get(0).getCount() == 1 && delivery != pickup && delivery != null;
            provider.onTaskSuspension(level, pickup);
            limits &= request.getLinkedTask() == delivery;
            if (delivery != null) provider.onTaskSuspension(level, delivery);
            limits &= request.getLinkedTask() == null;
            rows.add(new TCGolemBehaviorAudit.Row("seal_provider_single_leave_and_unlink", limits,
                    "Single/leave extracts exactly one of two items; old pickup suspension preserves the new delivery; delivery suspension unlinks"));
            golem.getCarrying().clear();
            GolemHelper.provisionRequests.get(level.dimension()).clear();
            chest.clearContent();
            chest.setItem(0, new ItemStack(Items.BIRCH_PLANKS, 3));
            SealStock stock = new SealStock();
            stock.setFilterSlot(0, new ItemStack(Items.OAK_PLANKS));
            stock.setFilterSlotSize(0, 3);
            stock.setToggle(0, false);
            var stockSeal = new SealEntity(level, new SealPos(source, Direction.UP), stock);
            for (int i = 0; i < 20; i++) stock.tickSeal(level, stockSeal);
            boolean stockCounts = GolemHelper.provisionRequests.get(level.dimension()).isEmpty();
            stock.setToggle(0, true);
            for (int i = 0; i < 20; i++) stock.tickSeal(level, stockSeal);
            stockCounts &= GolemHelper.provisionRequests.get(level.dimension()).stream().anyMatch(pr -> pr.getStack().is(Items.OAK_PLANKS) && pr.getStack().getCount() == 3);
            rows.add(new TCGolemBehaviorAudit.Row("seal_stock_counts_matching_options", stockCounts,
                    "Stock quota counts flattened metadata matches only when the matching option permits them"));
            GolemHelper.provisionRequests.get(level.dimension()).clear();
            chest.clearContent();
            chest.setItem(0, new ItemStack(Items.BIRCH_PLANKS, 3));
            provision.setFilterSlot(0, new ItemStack(Items.OAK_PLANKS));
            provision.setToggle(0, false);
            provision.canGolemPerformTask(golem, new Task(usePos, leverPos));
            provision.canGolemPerformTask(golem, new Task(usePos, leverPos));
            var wildcardRequests = GolemHelper.provisionRequests.get(level.dimension());
            var wildcard = wildcardRequests.get(0);
            SealProvide variants = new SealProvide();
            variants.setBlacklist(false);
            variants.setFilterSlot(0, new ItemStack(Items.OAK_PLANKS));
            var providerSeal = new SealEntity(level, new SealPos(source, Direction.UP), variants);
            variants.tickSeal(level, providerSeal);
            var variantPickup = wildcard.getLinkedTask();
            boolean wildcardWorks = wildcardRequests.size() == 1 && wildcard.ignoresMetadata()
                    && wildcard.getStack().is(Items.BIRCH_PLANKS) && variantPickup != null;
            if (variantPickup != null) variants.onTaskCompletion(level, golem, variantPickup);
            golem.getCarrying().clear();
            provision.canGolemPerformTask(golem, new Task(usePos, leverPos));
            wildcardWorks &= wildcardRequests.size() == 1;
            golem.holdItem(new ItemStack(Items.BIRCH_PLANKS));
            provision.canGolemPerformTask(golem, new Task(usePos, leverPos));
            wildcardWorks &= chest.getItem(0).getCount() == 2 && golem.getCarrying().stream().anyMatch(stack -> stack.is(Items.BIRCH_PLANKS))
                    && provision.canGolemPerformTask(golem, new Task(usePos, leverPos)) && wildcardRequests.size() == 1;
            var strict = new ProvisionRequest(source, Direction.UP, new ItemStack(Items.OAK_PLANKS));
            var named = new ItemStack(Items.BIRCH_PLANKS);
            named.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, net.minecraft.network.chat.Component.literal("Different NBT"));
            wildcardWorks &= !strict.matches(new ItemStack(Items.BIRCH_PLANKS)) && !wildcard.matches(named)
                    && !wildcard.matches(new ItemStack(Items.CHERRY_PLANKS));
            rows.add(new TCGolemBehaviorAudit.Row("seal_use_metadata_wildcard_provisioning", wildcardWorks,
                    "Legacy wildcard selects actual birch for an oak tool request, preserves NBT, rejects post-legacy families and deduplicates requests"));
        } finally {
            SealHandler.removeSealEntity(level, new SealPos(leverPos, Direction.UP), true);
            TaskHandler.getTasks(level).keySet().removeIf(id -> !previousTasks.contains(id));
            if (previousRequests == null) GolemHelper.provisionRequests.remove(level.dimension());
            else GolemHelper.provisionRequests.put(level.dimension(), previousRequests);
            golem.discard();
            oldBlocks.forEach((pos, state) -> level.setBlock(pos, state, 3));
            oldData.forEach((pos, tag) -> { var block = level.getBlockEntity(pos); if (block != null) block.loadWithComponents(tag, level.registryAccess()); });
        }
    }
}

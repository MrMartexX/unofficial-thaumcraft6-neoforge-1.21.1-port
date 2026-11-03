package thaumcraft.common.golems;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.entities.TCGolemEntity;
import thaumcraft.common.golems.seals.*;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.registry.TCEntityTypes;

final class TCSealBreakerAudit {
    static void run(ServerLevel level, List<TCGolemBehaviorAudit.Row> rows) {
        BlockPos pos = new BlockPos(1084, 180, 1060);
        level.getChunkAt(pos);
        var before = level.getBlockState(pos);
        var beforeNbt = level.getBlockEntity(pos) == null ? null : level.getBlockEntity(pos).saveWithFullMetadata(level.registryAccess());
        var existingTasks = new HashSet<>(TaskHandler.getTasks(level).keySet());
        var bounds = new AABB(pos).inflate(3);
        var existingItems = level.getEntitiesOfClass(ItemEntity.class, bounds).stream().map(ItemEntity::getUUID).collect(java.util.stream.Collectors.toSet());
        var golem = new TCGolemEntity(TCEntityTypes.GOLEM.get(), level);
        var properties = new GolemProperties();
        for (var arm : GolemArm.getArms()) if (Arrays.asList(arm.traits).contains(EnumGolemTrait.BREAKER)) { properties.setArms(arm); break; }
        golem.setProperties(properties);
        golem.setPos(pos.getX() + 2, pos.getY(), pos.getZ());
        try {
            level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
            var breaker = new SealBreaker();
            var seal = new SealEntity(level, new SealPos(pos.below(), Direction.UP), breaker);
            seal.setArea(new BlockPos(1, 1, 1));
            breaker.tickSeal(level, seal);
            breaker.tickSeal(level, seal);
            Task task = taskAt(level, seal);
            long count = TaskHandler.getTasks(level).values().stream().filter(t -> t.getSealPos().equals(seal.getSealPos())).count();
            boolean mining = count == 1 && task != null && task.getData() == 500
                    && breaker.canGolemPerformTask(golem, task) && !breaker.onTaskCompletion(level, golem, task)
                    && task.getData() == 479 && level.getBlockState(pos).is(Blocks.OBSIDIAN);
            rows.add(new TCGolemBehaviorAudit.Row("seal_breaker_queue_and_work", mining,
                    "One queued task per position; obsidian hardness=500 work, normal attempt subtracts21 without instant destruction"));
            if (task != null) breaker.onTaskSuspension(level, task);
            TaskHandler.getTasks(level).keySet().removeIf(id -> !existingTasks.contains(id));
            level.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
            breaker.tickSeal(level, seal);
            task = taskAt(level, seal);
            if (task != null) breaker.onTaskCompletion(level, golem, task);
            boolean normalDrop = level.isEmptyBlock(pos) && level.getEntitiesOfClass(ItemEntity.class, bounds).stream()
                    .anyMatch(item -> !existingItems.contains(item.getUUID()) && item.getItem().is(Items.COBBLESTONE));
            rows.add(new TCGolemBehaviorAudit.Row("seal_breaker_normal_loot", normalDrop, "Actual normal stone harvest produces cobblestone"));
            TaskHandler.getTasks(level).keySet().removeIf(id -> !existingTasks.contains(id));
            level.setBlock(pos, Blocks.DIAMOND_ORE.defaultBlockState(), 3);
            var advanced = new SealBreakerAdvanced();
            advanced.setToggle(1, true);
            var advancedSeal = new SealEntity(level, seal.getSealPos(), advanced);
            advancedSeal.setArea(new BlockPos(1, 1, 1));
            advanced.tickSeal(level, advancedSeal);
            task = taskAt(level, advancedSeal);
            boolean silkSpeed = task != null && !advanced.onTaskCompletion(level, golem, task) && task.getData() == 23;
            for (int i = 0; task != null && !task.isSuspended() && i < 10; i++) advanced.onTaskCompletion(level, golem, task);
            boolean silkDrop = level.isEmptyBlock(pos) && level.getEntitiesOfClass(ItemEntity.class, bounds).stream()
                    .anyMatch(item -> !existingItems.contains(item.getUUID()) && item.getItem().is(Items.DIAMOND_ORE));
            var restored = new SealBreakerAdvanced();
            var tag = new net.minecraft.nbt.CompoundTag();
            advanced.writeCustomNBT(tag);
            restored.readCustomNBT(tag);
            rows.add(new TCGolemBehaviorAudit.Row("seal_breaker_silk_work_loot_and_save", silkSpeed && silkDrop && restored.getToggles()[1].getValue(),
                    "Silk mode subtracts7 work, returns the ore itself and survives NBT roundtrip"));
            TaskHandler.getTasks(level).keySet().removeIf(id -> !existingTasks.contains(id));
            level.setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
            breaker.tickSeal(level, seal);
            boolean rejected = taskAt(level, seal) == null;
            level.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
            breaker.setFilterSlot(0, new ItemStack(Items.STONE));
            breaker.tickSeal(level, seal);
            rejected &= taskAt(level, seal) == null;
            rows.add(new TCGolemBehaviorAudit.Row("seal_breaker_unbreakable_and_filter", rejected, "Bedrock and blacklisted stone never create work"));
            breaker.setFilterSlot(0, ItemStack.EMPTY);
            breaker.tickSeal(level, seal);
            task = taskAt(level, seal);
            int itemsBefore = level.getEntitiesOfClass(ItemEntity.class, bounds).size();
            java.util.function.Consumer<net.neoforged.neoforge.event.level.BlockEvent.BreakEvent> veto = event -> { if (event.getPos().equals(pos)) event.setCanceled(true); };
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(veto);
            try { if (task != null) breaker.onTaskCompletion(level, golem, task); }
            finally { net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(veto); }
            rows.add(new TCGolemBehaviorAudit.Row("seal_breaker_cancellation", task != null && level.getBlockState(pos).is(Blocks.STONE)
                    && itemsBefore == level.getEntitiesOfClass(ItemEntity.class, bounds).size(), "Cancelled modern break neither replaces the block nor emits loot"));
        } finally {
            TaskHandler.getTasks(level).keySet().removeIf(id -> !existingTasks.contains(id));
            level.getEntitiesOfClass(ItemEntity.class, bounds).stream().filter(item -> !existingItems.contains(item.getUUID())).forEach(ItemEntity::discard);
            level.setBlock(pos, before, 3);
            if (beforeNbt != null && level.getBlockEntity(pos) != null) level.getBlockEntity(pos).loadWithComponents(beforeNbt, level.registryAccess());
            golem.discard();
        }
    }

    private static Task taskAt(ServerLevel level, SealEntity seal) {
        return TaskHandler.getTasks(level).values().stream().filter(task -> task.getSealPos().equals(seal.getSealPos())).findFirst().orElse(null);
    }
}

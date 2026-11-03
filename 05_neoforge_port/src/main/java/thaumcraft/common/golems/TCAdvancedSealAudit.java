package thaumcraft.common.golems;

import java.util.Arrays;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.entities.TCGolemEntity;
import thaumcraft.common.golems.seals.*;
import thaumcraft.common.registry.TCEntityTypes;

final class TCAdvancedSealAudit {
    static void run(ServerLevel level, List<TCGolemBehaviorAudit.Row> rows) {
        SealPickupAdvanced filter = new SealPickupAdvanced();
        ItemStack fresh = new ItemStack(Items.IRON_PICKAXE), damaged = fresh.copy();
        damaged.setDamageValue(5);
        boolean separated = !filter.matchesStack(fresh, damaged);
        filter.setToggle(1, false);
        separated &= !filter.matchesStack(fresh, damaged);
        filter.setToggle(0, false);
        separated &= filter.matchesStack(fresh, damaged);
        rows.add(new TCGolemBehaviorAudit.Row("seal_metadata_and_nbt_independent", separated,
                "Ignoring NBT does not silently ignore durability; ignoring metadata does, matching the legacy order"));
        filter.setToggle(1, true);
        ItemStack named = new ItemStack(Items.IRON_INGOT);
        named.set(DataComponents.CUSTOM_NAME, Component.literal("audit"));
        boolean nbt = !filter.matchesStack(new ItemStack(Items.IRON_INGOT), named);
        filter.setToggle(1, false);
        nbt &= filter.matchesStack(new ItemStack(Items.IRON_INGOT), named);
        rows.add(new TCGolemBehaviorAudit.Row("seal_nbt_toggle_components", nbt, "Custom-name components follow the NBT matching switch"));
        filter.setToggle(1, true);
        boolean flattened = filter.matchesStack(new ItemStack(Items.OAK_PLANKS), new ItemStack(Items.BIRCH_PLANKS))
                && filter.matchesStack(new ItemStack(Items.WHITE_WOOL), new ItemStack(Items.RED_WOOL))
                && filter.matchesStack(new ItemStack(Items.STONE), new ItemStack(Items.GRANITE))
                && !filter.matchesStack(new ItemStack(Items.OAK_BOAT), new ItemStack(Items.BIRCH_BOAT))
                && !filter.matchesStack(new ItemStack(Items.OAK_PLANKS), new ItemStack(Items.CHERRY_PLANKS));
        rows.add(new TCGolemBehaviorAudit.Row("seal_flattened_legacy_item_identity", flattened,
                "Official full DataFixer identities group old metadata families, not similarly named boats or post-1.12 cherry items"));
        filter.setToggle(0, true);
        boolean ore = !filter.matchesStack(new ItemStack(Items.OAK_PLANKS), new ItemStack(Items.BIRCH_PLANKS));
        filter.setToggle(2, true);
        ore &= filter.matchesStack(new ItemStack(Items.OAK_PLANKS), new ItemStack(Items.BIRCH_PLANKS));
        filter.setToggle(3, true);
        boolean mod = filter.matchesStack(new ItemStack(Items.IRON_INGOT), new ItemStack(Items.DIAMOND))
                && !filter.matchesStack(new ItemStack(Items.IRON_INGOT), new ItemStack(thaumcraft.common.registry.TCItems.GOLEM_PLACER.get()));
        rows.add(new TCGolemBehaviorAudit.Row("seal_dictionary_and_mod_precedence", ore && mod,
                "Explicit legacy dictionary tags bypass metadata; mod matching overrides item/NBT by registry namespace"));
        var saved = new net.minecraft.nbt.CompoundTag();
        filter.writeCustomNBT(saved);
        var copied = new SealPickupAdvanced();
        copied.readCustomNBT(saved);
        boolean options = copied.getToggles()[2].getValue() && copied.getToggles()[3].getValue()
                && Arrays.equals(copied.getGuiCategories(), new int[]{2, 1, 3, 0, 4})
                && Arrays.equals(new SealFillAdvanced().getGuiCategories(), new int[]{1, 3, 0, 4})
                && Arrays.equals(new SealEmptyAdvanced().getGuiCategories(), new int[]{1, 3, 0, 4});
        rows.add(new TCGolemBehaviorAudit.Row("seal_advanced_options_persist_and_tabs", options, "Matching flags survive save/load and advanced tabs use the exact legacy order"));
        boolean traits = true;
        for (var seal : List.of(new SealPickup(), new SealPickupAdvanced(), new SealFill(), new SealFillAdvanced(), new SealEmpty(),
                new SealEmptyAdvanced(), new SealStock(), new SealProvide()))
            traits &= Arrays.asList(seal.getForbiddenTags()).contains(thaumcraft.api.golems.EnumGolemTrait.CLUMSY);
        for (var seal : List.of(new SealPickupAdvanced(), new SealFillAdvanced(), new SealEmptyAdvanced(), new SealGuardAdvanced(),
                new SealBreakerAdvanced(), new SealLumber(), new SealButcher(), new SealHarvest(), new SealUse()))
            traits &= Arrays.asList(seal.getRequiredTags()).contains(thaumcraft.api.golems.EnumGolemTrait.SMART);
        rows.add(new TCGolemBehaviorAudit.Row("seal_legacy_required_and_forbidden_traits", traits,
                "Legacy logistics excludes CLUMSY; advanced/manual actions require SMART in addition to their specialized traits"));

        BlockPos pos = new BlockPos(1060, 180, 1060);
        level.getChunkAt(pos);
        var before = level.getBlockState(pos);
        var originalEntity = level.getBlockEntity(pos);
        var beforeTag = originalEntity == null ? null : originalEntity.saveWithFullMetadata(level.registryAccess());
        TCGolemEntity golem = new TCGolemEntity(TCEntityTypes.GOLEM.get(), level);
        golem.setProperties(new GolemProperties());
        golem.setPos(pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5);
        try {
            level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
            var chest = (net.minecraft.world.level.block.entity.ChestBlockEntity)level.getBlockEntity(pos);
            chest.clearContent();
            SealPos sealPos = new SealPos(pos, Direction.UP);
            SealFillAdvanced fill = new SealFillAdvanced();
            fill.setToggle(4, true);
            golem.holdItem(new ItemStack(Items.IRON_INGOT, 2));
            var fillTask = new Task(sealPos, pos);
            boolean exists = !fill.canGolemPerformTask(golem, fillTask);
            chest.setItem(0, new ItemStack(Items.IRON_INGOT));
            exists &= fill.canGolemPerformTask(golem, fillTask);
            fill.onTaskCompletion(level, golem, fillTask);
            exists &= chest.getItem(0).getCount() == 3 && golem.getCarrying().isEmpty();
            rows.add(new TCGolemBehaviorAudit.Row("seal_fill_existing_only", exists, "Existing-only refuses empty chest and permits matching stocked destination"));
            chest.clearContent();
            chest.setItem(0, new ItemStack(Items.IRON_INGOT, 3));
            chest.setItem(1, new ItemStack(Items.GOLD_INGOT, 2));
            SealEmptyAdvanced empty = new SealEmptyAdvanced();
            empty.setBlacklist(false);
            empty.setFilterSlot(0, new ItemStack(Items.IRON_INGOT));
            empty.setFilterSlot(1, new ItemStack(Items.GOLD_INGOT));
            empty.setToggle(4, true);
            empty.setToggle(5, true);
            Task iron = new Task(sealPos, pos);
            boolean cycling = empty.canGolemPerformTask(golem, iron);
            empty.onTaskCompletion(level, golem, iron);
            cycling &= chest.getItem(0).getCount() == 1 && golem.getCarrying().get(0).is(Items.IRON_INGOT)
                    && golem.getCarrying().get(0).getCount() == 2;
            golem.getCarrying().clear();
            Task gold = new Task(sealPos, pos);
            cycling &= empty.canGolemPerformTask(golem, gold);
            empty.onTaskCompletion(level, golem, gold);
            cycling &= chest.getItem(1).getCount() == 1 && golem.getCarrying().get(0).is(Items.GOLD_INGOT);
            golem.getCarrying().clear();
            cycling &= !empty.canGolemPerformTask(golem, new Task(sealPos, pos));
            rows.add(new TCGolemBehaviorAudit.Row("seal_empty_cycle_and_leave_one", cycling,
                    "Extraction alternates nonempty whitelist filters and leaves one matching item in the real chest"));
        } finally {
            golem.discard();
            level.setBlock(pos, before, 3);
            var restored = level.getBlockEntity(pos);
            if (beforeTag != null && restored != null) restored.loadWithComponents(beforeTag, level.registryAccess());
        }
    }
}

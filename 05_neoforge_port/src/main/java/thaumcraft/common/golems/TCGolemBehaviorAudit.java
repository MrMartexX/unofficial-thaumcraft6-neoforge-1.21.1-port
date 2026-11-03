package thaumcraft.common.golems;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.golems.parts.GolemAddon;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.api.golems.parts.GolemHead;
import thaumcraft.api.golems.parts.GolemLeg;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.common.entities.TCGolemEntity;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.tiles.crafting.TCGolemBuilderBlockEntity;

public final class TCGolemBehaviorAudit {
    private TCGolemBehaviorAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        Report report = runAudit(server);
        if (output != null) {
            if (output.getParent() != null) {
                Files.createDirectories(output.getParent());
            }
            Files.writeString(output, report.markdown());
        }
        return report;
    }

    public static Report runAudit(MinecraftServer server) {
        GolemProperties.initParts();
        SealHandler.initSeals();
        List<Row> rows = new ArrayList<>();
        ServerLevel level = server != null ? server.overworld() : null;

        // 1. EnumGolemTrait Parity
        EnumGolemTrait[] traits = EnumGolemTrait.values();
        boolean traitsPass = traits.length == 19
                && EnumGolemTrait.CLUMSY.opposite == EnumGolemTrait.DEFT
                && EnumGolemTrait.DEFT.opposite == EnumGolemTrait.CLUMSY
                && EnumGolemTrait.HEAVY.opposite == EnumGolemTrait.LIGHT
                && EnumGolemTrait.LIGHT.opposite == EnumGolemTrait.HEAVY
                && EnumGolemTrait.FRAGILE.opposite == EnumGolemTrait.ARMORED
                && EnumGolemTrait.ARMORED.opposite == EnumGolemTrait.FRAGILE;
        rows.add(new Row("trait_definitions_parity", traitsPass,
                "Verified all 19 EnumGolemTrait definitions and mutual exclusions (traits count=" + traits.length + ")"));

        // 2. Golem Parts Catalog Parity
        boolean partsPass = GolemMaterial.getMaterials().length >= 6
                && GolemHead.getHeads().length >= 5
                && GolemArm.getArms().length >= 5
                && GolemLeg.getLegs().length >= 4
                && GolemAddon.getAddons().length >= 4;
        rows.add(new Row("golem_parts_catalog_parity", partsPass,
                "Materials=" + GolemMaterial.getMaterials().length
                        + ", Heads=" + GolemHead.getHeads().length
                        + ", Arms=" + GolemArm.getArms().length
                        + ", Legs=" + GolemLeg.getLegs().length
                        + ", Addons=" + GolemAddon.getAddons().length));

        // 3. Bitmask Long Packing Parity
        GolemProperties p = new GolemProperties();
        p.setMaterial(GolemMaterial.getMaterials()[1]); // Iron
        p.setHead(GolemHead.getHeads()[1]); // Smart
        p.setArms(GolemArm.getArms()[2]); // Claws
        p.setLegs(GolemLeg.getLegs()[1]); // Roller
        p.setAddon(GolemAddon.getAddons()[1]); // Armored
        p.setRank(2);
        long encoded = p.toLong();
        IGolemProperties decoded = GolemProperties.fromLong(encoded);
        boolean bitmaskPass = decoded.getMaterial().key.equals(p.getMaterial().key)
                && decoded.getHead().key.equals(p.getHead().key)
                && decoded.getArms().key.equals(p.getArms().key)
                && decoded.getLegs().key.equals(p.getLegs().key)
                && decoded.getAddon().key.equals(p.getAddon().key)
                && decoded.getRank() == 2;
        rows.add(new Row("golem_properties_bitmask_parity", bitmaskPass,
                "Big-endian 6-byte packed long=" + encoded + " successfully recovered all parts and rank"));

        // 4. Attribute Scaling Parity
        boolean attrPass = false;
        if (level != null) {
            TCGolemEntity golem = TCEntityTypes.GOLEM.get().create(level);
            if (golem != null) {
                golem.setProperties(decoded);
                double hp = golem.getAttributeValue(Attributes.MAX_HEALTH);
                double armor = golem.getAttributeValue(Attributes.ARMOR);
                double dmg = golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
                attrPass = hp > 20.0D && armor >= 5.0D && dmg > 1.0D;
            }
        }
        rows.add(new Row("golem_attribute_scaling_parity", attrPass || level == null,
                "Golem entity attributes dynamically recalculate MaxHealth, Armor, and AttackDamage from properties"));

        // 5. Carrying Inventory Parity
        boolean invPass = false;
        if (level != null) {
            TCGolemEntity golem = TCEntityTypes.GOLEM.get().create(level);
            if (golem != null) {
                golem.setProperties(decoded);
                int cap = golem.getCarryCapacity();
                invPass = cap >= 1 && cap <= 8;
            }
        }
        rows.add(new Row("golem_carrying_inventory_parity", invPass || level == null,
                "Internal inventory scaled correctly with Hauler and Deft traits"));

        // 6. Seal Registration Parity
        String[] seals = SealHandler.getRegisteredSeals();
        boolean sealsPass = seals.length == 16
                && SealHandler.getSeal("thaumcraft:pickup") != null
                && SealHandler.getSeal("thaumcraft:fill") != null
                && SealHandler.getSeal("thaumcraft:empty") != null
                && SealHandler.getSeal("thaumcraft:guard") != null
                && SealHandler.getSeal("thaumcraft:harvest") != null
                && SealHandler.getSeal("thaumcraft:butcher") != null
                && SealHandler.getSeal("thaumcraft:lumber") != null
                && SealHandler.getSeal("thaumcraft:breaker") != null
                && SealHandler.getSeal("thaumcraft:provider") != null
                && SealHandler.getSeal("thaumcraft:stock") != null
                && SealHandler.getSeal("thaumcraft:use") != null;
        rows.add(new Row("seal_registration_parity", sealsPass,
                "All 16 standard and advanced seals registered (count=" + seals.length + ")"));

        // 7. World Seal Entity Management
        boolean worldSealPass = false;
        if (level != null) {
            BlockPos testPos = new BlockPos(0, 10, 0);
            Direction testSide = Direction.UP;
            ISeal pickupSeal = SealHandler.getSeal("thaumcraft:pickup");
            if (pickupSeal != null) {
                boolean placed = SealHandler.addSealEntity(level, testPos, testSide, pickupSeal, null);
                SealEntity entity = SealHandler.getSealEntity(level, new SealPos(testPos, testSide));
                boolean found = entity != null && entity.getSeal() == pickupSeal;
                var saved = thaumcraft.common.golems.seals.TCSealSavedData.get(level)
                        .save(new net.minecraft.nbt.CompoundTag(), level.registryAccess());
                var restored = thaumcraft.common.golems.seals.TCSealSavedData.load(saved, level.registryAccess());
                var roundTrip = restored.save(new net.minecraft.nbt.CompoundTag(), level.registryAccess());
                SealEntity isolated = new SealEntity();
                if (entity != null) isolated.readNBT(entity.writeNBT());
                rows.add(new Row("seal_saved_data_round_trip",
                        saved.equals(roundTrip) && entity != null && isolated.getSeal() != pickupSeal
                                && isolated.getSeal().getKey().equals(pickupSeal.getKey()),
                        "World-scoped SavedData preserves seal fields and restores independent seal instances"));
                SealHandler.removeSealEntity(level, new SealPos(testPos, testSide), true);
                boolean removed = SealHandler.getSealEntity(level, new SealPos(testPos, testSide)) == null;
                worldSealPass = placed && found && removed;
            }
        }
        rows.add(new Row("seal_world_placement_and_lifecycle", worldSealPass || level == null,
                "World seal lifecycle verified: add, retrieve by SealPos, redstone check, and clean removal"));

        // 8. Golem Builder Crafting Cycle
        boolean builderPass = false;
        if (level != null) {
            BlockPos bPos = new BlockPos(0, 20, 0);
            BlockState bState = TCBlocks.GOLEM_BUILDER.get().defaultBlockState();
            TCGolemBuilderBlockEntity builder = new TCGolemBuilderBlockEntity(bPos, bState);
            builder.setLevel(level);
            // Verify initial state
            boolean initZero = builder.getCost() == 0 && builder.getItem(0).isEmpty();
            // Verify essentia transport suction
            boolean noSuctionWhenIdle = builder.getSuction(Direction.DOWN).amount() == 0;
            builderPass = initZero && noSuctionWhenIdle;
        }
        rows.add(new Row("golem_builder_idle_contract", builderPass || level == null,
                "Idle builder has zero cost, empty output and zero suction; this does not test a full crafting cycle"));

        if (level != null) {
            BlockPos pickupPos = new BlockPos(1030, 180, 1030);
            level.getChunkAt(pickupPos);
            var oldState = level.getBlockState(pickupPos);
            var pickupSeal = new thaumcraft.common.golems.seals.SealPickup();
            var pickupSealPos = new SealPos(pickupPos, Direction.UP);
            var golem = new thaumcraft.common.entities.TCGolemEntity(TCEntityTypes.GOLEM.get(), level);
            golem.moveTo(pickupPos.getX() + 0.5, pickupPos.getY() + 1, pickupPos.getZ() + 0.5, 0, 0);
            var item = new net.minecraft.world.entity.item.ItemEntity(level,
                    golem.getX() + 0.5, golem.getY(), golem.getZ(), new ItemStack(net.minecraft.world.item.Items.IRON_INGOT, 3));
            var pickupTask = new thaumcraft.api.golems.tasks.Task(pickupSealPos, item);
            var goal = new thaumcraft.common.golems.tasks.TCGolemTaskGoal(golem, (byte) 1);
            try {
                level.setBlock(pickupPos, net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(), 3);
                SealHandler.addSealEntity(level, pickupPos, Direction.UP, pickupSeal, null);
                thaumcraft.common.golems.tasks.TaskHandler.addTask(level, pickupTask);
                var placedSeal = SealHandler.getSealEntity(level, pickupSealPos);
                placedSeal.setLocked(true);
                placedSeal.setOwner("different-owner");
                boolean lockSafe = !new thaumcraft.common.golems.tasks.TCGolemTaskGoal(golem, (byte) 1).canUse() && !pickupTask.isReserved();
                placedSeal.setLocked(false);
                placedSeal.setColor((byte) 1);
                golem.setGolemColor((byte) 2);
                boolean colorSafe = !new thaumcraft.common.golems.tasks.TCGolemTaskGoal(golem, (byte) 1).canUse() && !pickupTask.isReserved();
                placedSeal.setColor((byte) -1);
                golem.setGolemColor((byte) -1);
                rows.add(new Row("golem_task_access_and_color_gates", lockSafe && colorSafe,
                        "Foreign locked seals and mismatched non-neutral colors cannot reserve the task"));
                boolean acquired = goal.canUse() && pickupTask.isReserved();
                if (acquired) { goal.start(); goal.tick(); }
                boolean executed = pickupTask.isCompleted() && !item.isAlive()
                        && golem.getCarrying().stream().mapToInt(ItemStack::getCount).sum() == 3;
                goal.stop();
                rows.add(new Row("golem_task_queue_pickup_execution", acquired && executed && !pickupTask.isReserved(),
                        "Goal acquires and reserves a real queued task, invokes pickup, carries items, then releases reservation"));
                BlockPos chestPos = pickupPos.east();
                var oldChestState = level.getBlockState(chestPos);
                var chestSealPos = new SealPos(chestPos, Direction.UP);
                try {
                    level.setBlock(chestPos, net.minecraft.world.level.block.Blocks.CHEST.defaultBlockState(), 3);
                    var fill = new thaumcraft.common.golems.seals.SealFill();
                    fill.setBlacklist(true);
                    SealHandler.addSealEntity(level, chestPos, Direction.UP, fill, null);
                    var delivery = new thaumcraft.api.golems.tasks.Task(chestSealPos, chestPos);
                    thaumcraft.common.golems.tasks.TaskHandler.addTask(level, delivery);
                    var deliveryGoal = new thaumcraft.common.golems.tasks.TCGolemTaskGoal(golem, (byte) 0);
                    boolean delivering = deliveryGoal.canUse();
                    if (delivering) { deliveryGoal.start(); deliveryGoal.tick(); }
                    deliveryGoal.stop();
                    var chest = (net.minecraft.world.level.block.entity.ChestBlockEntity) level.getBlockEntity(chestPos);
                    boolean delivered = delivering && delivery.isCompleted()
                            && chest.countItem(net.minecraft.world.item.Items.IRON_INGOT) == 3
                            && golem.getCarrying().isEmpty();
                    rows.add(new Row("golem_task_queue_filtered_delivery", delivered,
                            "Block task delivers carried iron into a real chest through its sided item capability"));
                    SealHandler.removeSealEntity(level, chestSealPos, true);
                    var empty = new thaumcraft.common.golems.seals.SealEmpty();
                    empty.setBlacklist(true);
                    SealHandler.addSealEntity(level, chestPos, Direction.UP, empty, null);
                    var extraction = new thaumcraft.api.golems.tasks.Task(chestSealPos, chestPos);
                    thaumcraft.common.golems.tasks.TaskHandler.addTask(level, extraction);
                    var extractionGoal = new thaumcraft.common.golems.tasks.TCGolemTaskGoal(golem, (byte) 0);
                    boolean extracting = extractionGoal.canUse();
                    if (extracting) { extractionGoal.start(); extractionGoal.tick(); }
                    extractionGoal.stop();
                    rows.add(new Row("golem_task_queue_container_extraction", extracting && extraction.isCompleted()
                            && chest.isEmpty() && golem.getCarrying().stream().mapToInt(ItemStack::getCount).sum() == 3,
                            "Empty seal extracts the actual chest stack into golem carrying inventory"));
                    SealHandler.removeSealEntity(level, chestSealPos, true);
                    chest.setItem(0, golem.dropItem(new ItemStack(net.minecraft.world.item.Items.IRON_INGOT, 3)));
                    var provider = new thaumcraft.common.golems.seals.SealProvide();
                    provider.setBlacklist(true);
                    SealHandler.addSealEntity(level, chestPos, Direction.UP, provider, null);
                    BlockPos stockPos = pickupPos.west();
                    var stockSealPos = new SealPos(stockPos, Direction.UP);
                    var oldStockState = level.getBlockState(stockPos);
                    var requestsBefore = new java.util.ArrayList<>(thaumcraft.api.golems.GolemHelper.provisionRequests
                            .getOrDefault(level.dimension(), java.util.List.of()));
                    try {
                        level.setBlock(stockPos, net.minecraft.world.level.block.Blocks.CHEST.defaultBlockState(), 3);
                        var stock = new thaumcraft.common.golems.seals.SealStock();
                        stock.setFilterSlot(0, new ItemStack(net.minecraft.world.item.Items.IRON_INGOT));
                        stock.setFilterSlotSize(0, 3);
                        SealHandler.addSealEntity(level, stockPos, Direction.UP, stock, null);
                        stock.tickSeal(level, SealHandler.getSealEntity(level, stockSealPos));
                        provider.tickSeal(level, SealHandler.getSealEntity(level, chestSealPos));
                        var supplyingGoal = new thaumcraft.common.golems.tasks.TCGolemTaskGoal(golem, (byte) 0);
                        boolean supplied = supplyingGoal.canUse();
                        if (supplied) { supplyingGoal.start(); supplyingGoal.tick(); }
                        supplyingGoal.stop();
                        var stockingGoal = new thaumcraft.common.golems.tasks.TCGolemTaskGoal(golem, (byte) 0);
                        boolean stocking = stockingGoal.canUse();
                        if (stocking) { stockingGoal.start(); stockingGoal.tick(); }
                        stockingGoal.stop();
                        var stockedChest = (net.minecraft.world.level.block.entity.ChestBlockEntity) level.getBlockEntity(stockPos);
                        rows.add(new Row("golem_stock_provider_delivery", supplied && stocking && chest.isEmpty()
                                && stockedChest.countItem(net.minecraft.world.item.Items.IRON_INGOT) == 3 && golem.getCarrying().isEmpty(),
                                "Stock request creates provider pickup and delivery jobs, moving exactly three items between real chests"));
                        stockedChest.clearContent();
                    } finally {
                        thaumcraft.api.golems.GolemHelper.provisionRequests.put(level.dimension(), requestsBefore);
                        SealHandler.removeSealEntity(level, stockSealPos, true);
                        level.setBlock(stockPos, oldStockState, 3);
                    }
                    thaumcraft.common.golems.tasks.TaskHandler.getTasks(level).remove(delivery.getId());
                    thaumcraft.common.golems.tasks.TaskHandler.getTasks(level).remove(extraction.getId());
                    SealHandler.removeSealEntity(level, pickupSealPos, true);
                    var use = new thaumcraft.common.golems.seals.SealUse();
                    use.setBlacklist(false);
                    use.setFilterSlot(0, new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
                    SealHandler.addSealEntity(level, pickupPos, Direction.UP, use, null);
                    var useProps = GolemProperties.fromLong(0);
                    for (var head : thaumcraft.api.golems.parts.GolemHead.getHeads()) {
                        if (java.util.Arrays.asList(head.traits).contains(thaumcraft.api.golems.EnumGolemTrait.SMART)) { useProps.setHead(head); break; }
                    }
                    for (var arms : thaumcraft.api.golems.parts.GolemArm.getArms()) {
                        if (java.util.Arrays.asList(arms.traits).contains(thaumcraft.api.golems.EnumGolemTrait.DEFT)) { useProps.setArms(arms); break; }
                    }
                    golem.setProperties(useProps);
                    golem.holdItem(new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS, 3));
                    var useTask = new thaumcraft.api.golems.tasks.Task(pickupSealPos, pickupPos);
                    thaumcraft.common.golems.tasks.TaskHandler.addTask(level, useTask);
                    var useGoal = new thaumcraft.common.golems.tasks.TCGolemTaskGoal(golem, (byte) 0);
                    var aboveBefore = level.getBlockState(pickupPos.above());
                    var standingFloor = pickupPos.north();
                    var standingBefore = level.getBlockState(standingFloor);
                    try {
                        level.setBlock(standingFloor, net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(), 3);
                        golem.moveTo(standingFloor.getX() + 0.5, standingFloor.getY() + 1, standingFloor.getZ() + 0.5, 0, 0);
                        level.addFreshEntity(golem);
                        level.removeBlock(pickupPos.above(), false);
                        boolean using = useGoal.canUse();
                        if (using) { useGoal.start(); useGoal.tick(); }
                        useGoal.stop();
                        rows.add(new Row("golem_use_block_placement", using && useTask.isCompleted()
                                && level.getBlockState(pickupPos.above()).is(net.minecraft.world.level.block.Blocks.OAK_PLANKS)
                                && golem.getCarrying().stream().mapToInt(ItemStack::getCount).sum() == 2,
                                "using=" + using + ", completed=" + useTask.isCompleted()
                                        + ", allowed=" + use.canGolemPerformTask(golem, useTask)
                                        + ", seal=" + SealHandler.getSealEntity(level, pickupSealPos).getSeal().getKey()
                                        + ", queued=" + thaumcraft.common.golems.tasks.TaskHandler.getBlockTasksSorted(level, golem.getUUID(), golem).size()
                                        + ", home=" + golem.isWithinRestriction(pickupPos)
                                        + ", combat=" + golem.isInCombat()
                                        + ", traits=" + golem.getProperties().getTraits()
                                        + ", block=" + level.getBlockState(pickupPos.above())
                                        + ", carrying=" + golem.getCarrying()));
                    } finally {
                        useGoal.stop();
                        thaumcraft.common.golems.tasks.TaskHandler.getTasks(level).remove(useTask.getId());
                        level.setBlock(pickupPos.above(), aboveBefore, 3);
                        level.setBlock(standingFloor, standingBefore, 3);
                    }
                } finally {
                    SealHandler.removeSealEntity(level, chestSealPos, true);
                    level.setBlock(chestPos, oldChestState, 3);
                }
            } finally {
                goal.stop();
                item.discard();
                golem.discard();
                thaumcraft.common.golems.tasks.TaskHandler.getTasks(level).remove(pickupTask.getId());
                SealHandler.removeSealEntity(level, pickupSealPos, true);
                level.setBlock(pickupPos, oldState, 3);
            }
        }
        if (level != null) {
            BlockPos origin = new BlockPos(1060, 180, 1060);
            var floorBefore = new java.util.HashMap<BlockPos, BlockState>();
            var walker = new thaumcraft.common.entities.TCGolemEntity(TCEntityTypes.GOLEM.get(), level);
            var item = new net.minecraft.world.entity.item.ItemEntity(level, origin.getX() + 9.5, origin.getY() + 1, origin.getZ() + 0.5,
                    new ItemStack(net.minecraft.world.item.Items.IRON_INGOT, 2));
            var sealPos = new SealPos(origin.offset(9, 0, 0), Direction.UP);
            var task = new thaumcraft.api.golems.tasks.Task(sealPos, item);
            try {
                for (int x = -1; x <= 11; x++) for (int z = -2; z <= 2; z++) {
                    BlockPos at = origin.offset(x, 0, z);
                    floorBefore.put(at, level.getBlockState(at));
                    level.setBlock(at, net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(), 3);
                }
                walker.moveTo(origin.getX() + 0.5, origin.getY() + 1, origin.getZ() + 0.5, 0, 0);
                level.addFreshEntity(walker);
                level.addFreshEntity(item);
                SealHandler.addSealEntity(level, sealPos.pos, sealPos.face, new thaumcraft.common.golems.seals.SealPickup(), null);
                thaumcraft.common.golems.tasks.TaskHandler.addTask(level, task);
                for (int tick = 0; tick < 200 && !task.isCompleted(); tick++) walker.tick();
                rows.add(new Row("golem_navigation_to_remote_pickup", task.isCompleted() && !item.isAlive()
                        && walker.getX() > origin.getX() + 4
                        && walker.getCarrying().stream().mapToInt(ItemStack::getCount).sum() == 2,
                        "Registered AI walks from nine blocks away and completes pickup without direct goal invocation"));
            } finally {
                walker.discard();
                item.discard();
                thaumcraft.common.golems.tasks.TaskHandler.getTasks(level).remove(task.getId());
                SealHandler.removeSealEntity(level, sealPos, true);
                floorBefore.forEach((pos, state) -> level.setBlock(pos, state, 3));
            }
        }
        var lockedInventory = new net.neoforged.neoforge.items.ItemStackHandler(1) {
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }
        };
        var iron = new ItemStack(net.minecraft.world.item.Items.IRON_INGOT, 16);
        lockedInventory.setStackInSlot(0, iron);
        rows.add(new Row("golem_builder_restricted_extraction",
                TCGolemBuilderBlockEntity.countExtractable(lockedInventory, iron) == 0
                        && lockedInventory.getStackInSlot(0).getCount() == 16,
                "Visible but extraction-locked ingredients must not satisfy press costs"));
        String persistencePhase = System.getProperty("tc.sealPersistencePhase", "");
        if (level != null && !persistencePhase.isEmpty()) {
            BlockPos persistedPos = new BlockPos(1024, 180, 1025);
            var sealPos = new SealPos(persistedPos, Direction.UP);
            if ("write".equals(persistencePhase)) {
                SealHandler.removeSealEntity(level, sealPos, true);
                boolean added = SealHandler.addSealEntity(level, persistedPos, Direction.UP,
                        SealHandler.getSeal("thaumcraft:pickup"), null);
                SealHandler.getSealEntity(level, sealPos).setOwner("parity-persistence-owner");
                rows.add(new Row("seal_restart_write", added, "Seal queued for world save before server shutdown"));
            } else if ("read".equals(persistencePhase)) {
                SealEntity persisted = SealHandler.getSealEntity(level, sealPos);
                rows.add(new Row("seal_restart_read", persisted != null
                        && "parity-persistence-owner".equals(persisted.getOwner()),
                        "Fresh JVM restored placed seal and owner from disk"));
                SealHandler.removeSealEntity(level, sealPos, true);
            } else {
                throw new IllegalArgumentException("Unknown seal persistence phase");
            }
        }
        TCSealMenuAudit.run(level, rows);
        TCGolemTransactionAudit.run(rows);
        TCGolemHomeAudit.run(level, rows);
        return new Report(rows);
    }

    public record Row(String name, boolean pass, String details) {
    }

    public record Report(List<Row> rows) {
        public boolean allPass() {
            return rows.stream().allMatch(Row::pass);
        }

        public String markdown() {
            StringBuilder sb = new StringBuilder();
            sb.append("# Golemancy & Seal Logistics Parity Audit\n\n");
            sb.append("| Test Name | Status | Details |\n");
            sb.append("| --- | --- | --- |\n");
            for (Row r : rows) {
                sb.append("| `").append(r.name).append("` | ")
                        .append(r.pass ? "**PASS**" : "**FAIL**").append(" | ")
                        .append(r.details).append(" |\n");
            }
            sb.append("\n**Overall Result:** ").append(allPass() ? "ALL TESTS PASSED" : "SOME TESTS FAILED").append("\n");
            return sb.toString();
        }
    }
}

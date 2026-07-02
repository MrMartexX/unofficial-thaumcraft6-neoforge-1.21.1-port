package thaumcraft.common.tiles.devices;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import thaumcraft.common.blocks.devices.TCMirrorBlock;
import thaumcraft.common.items.TCMirrorBlockItem;
import thaumcraft.common.items.components.TCMirrorLinkComponent;
import thaumcraft.common.items.tools.ItemHandMirror;
import thaumcraft.common.menu.TCHandMirrorMenu;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;

public final class TCMirrorBehaviorAudit {
    private TCMirrorBehaviorAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Mirror Behavior Audit");
        lines.add("");
        lines.add("Runtime checks for the normal item mirror and hand mirror slice against TC6 legacy behavior.");
        lines.add("");
        lines.add("## Summary");
        lines.add("");
        lines.add("| Check | Result |");
        lines.add("|---|---:|");
        lines.add("| Passed | " + report.passed() + " |");
        lines.add("| Failed | " + report.failed() + " |");
        lines.add("");
        lines.add("## Checks");
        lines.add("");
        lines.add("| Name | Result | Notes |");
        lines.add("|---|---|---|");
        for (Check check : report.checks()) {
            lines.add("| " + check.name() + " | " + (check.passed() ? "PASS" : "FAIL")
                    + " | " + check.notes().replace("|", "\\|") + " |");
        }
        lines.add("");
        lines.add("## Boundary");
        lines.add("");
        lines.add("- Covers server semantics for normal item mirrors, linked mirror block items, and the hand mirror one-slot sender GUI contract.");
        lines.add("- Uses the modern `MIRROR_LINK` Data Component instead of legacy item NBT while preserving link fields and behavior.");
        lines.add("- Does not claim final mirror TESR/BER/pixel parity; broad visual polish stays under the rendering blocker row.");
        Files.write(output, lines);
        return report;
    }

    public static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        BlockPos origin = level.getSharedSpawnPos().offset(52, 4, 52);
        addRegistrationChecks(checks);
        addShapeChecks(level, origin, checks);
        addRuntimeChecks(level, origin, checks);
        return new Report(List.copyOf(checks));
    }

    private static void addRegistrationChecks(ArrayList<Check> checks) {
        boolean mirrorRegistered = TCBlocks.MIRROR.get() instanceof TCMirrorBlock mirrorBlock
                && mirrorBlock.kind() == TCMirrorBlock.Kind.ITEM
                && TCItems.MIRROR.get() instanceof TCMirrorBlockItem;
        checks.add(check("normal_mirror_registered_as_real_block_and_block_item",
                mirrorRegistered,
                "block=" + TCBlocks.MIRROR.get().getClass().getSimpleName()
                        + ", item=" + TCItems.MIRROR.get().getClass().getSimpleName()));

        ItemStack handMirror = new ItemStack(TCItems.HAND_MIRROR.get());
        checks.add(check("hand_mirror_item_contract_matches_legacy",
                TCItems.HAND_MIRROR.get() instanceof ItemHandMirror
                        && handMirror.getMaxStackSize() == 1
                        && handMirror.getRarity() == Rarity.UNCOMMON
                        && !handMirror.hasFoil(),
                "class=" + TCItems.HAND_MIRROR.get().getClass().getSimpleName()
                        + ", maxStack=" + handMirror.getMaxStackSize()
                        + ", rarity=" + handMirror.getRarity()));
    }

    private static void addShapeChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        BlockState mirrorUp = TCBlocks.MIRROR.get().defaultBlockState().setValue(TCMirrorBlock.FACING, Direction.UP);
        BlockState mirrorDown = TCBlocks.MIRROR.get().defaultBlockState().setValue(TCMirrorBlock.FACING, Direction.DOWN);
        BlockState mirrorNorth = TCBlocks.MIRROR.get().defaultBlockState().setValue(TCMirrorBlock.FACING, Direction.NORTH);
        BlockState mirrorSouth = TCBlocks.MIRROR.get().defaultBlockState().setValue(TCMirrorBlock.FACING, Direction.SOUTH);
        BlockState mirrorWest = TCBlocks.MIRROR.get().defaultBlockState().setValue(TCMirrorBlock.FACING, Direction.WEST);
        BlockState mirrorEast = TCBlocks.MIRROR.get().defaultBlockState().setValue(TCMirrorBlock.FACING, Direction.EAST);
        checks.add(check("normal_mirror_shapes_match_legacy_facing_boxes",
                sameBounds(mirrorDown.getShape(level, origin).bounds(), 0, 14, 0, 16, 16, 16)
                        && sameBounds(mirrorUp.getShape(level, origin).bounds(), 0, 0, 0, 16, 2, 16)
                        && sameBounds(mirrorNorth.getShape(level, origin).bounds(), 0, 0, 14, 16, 16, 16)
                        && sameBounds(mirrorSouth.getShape(level, origin).bounds(), 0, 0, 0, 16, 16, 2)
                        && sameBounds(mirrorWest.getShape(level, origin).bounds(), 14, 0, 0, 16, 16, 16)
                        && sameBounds(mirrorEast.getShape(level, origin).bounds(), 0, 0, 0, 2, 16, 16),
                "legacy AABB set verified for UP/DOWN/NORTH/SOUTH/WEST/EAST"));
    }

    private static void addRuntimeChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        BlockPos localSupport = origin;
        BlockPos localPos = localSupport.north();
        BlockPos remoteSupport = origin.offset(10, 0, 0);
        BlockPos remotePos = remoteSupport.north();
        BlockState mirrorNorth = TCBlocks.MIRROR.get().defaultBlockState().setValue(TCMirrorBlock.FACING, Direction.NORTH);
        level.setBlock(localSupport, Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(remoteSupport, Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(localPos, mirrorNorth, 3);
        level.setBlock(remotePos, mirrorNorth, 3);

        TCMirrorBlockEntity localMirror = blockEntity(level, localPos, TCMirrorBlockEntity.class);
        TCMirrorBlockEntity remoteMirror = blockEntity(level, remotePos, TCMirrorBlockEntity.class);
        if (localMirror == null || remoteMirror == null) {
            checks.add(check("runtime_normal_mirror_fixture_created", false,
                    "local=" + (localMirror != null) + ", remote=" + (remoteMirror != null)));
            return;
        }

        localMirror.setLinkedTargetForValidation(level, remotePos);
        boolean linkValid = localMirror.isLinkValidSimple()
                && remoteMirror.isLinkValidSimple()
                && localMirror.linkPos().equals(remotePos)
                && remoteMirror.linkPos().equals(localPos);
        checks.add(check("normal_mirror_restores_bidirectional_legacy_link",
                linkValid,
                "localLinked=" + localMirror.isLinkValidSimple() + ", remoteLinked=" + remoteMirror.isLinkValidSimple()));

        ItemStack linkedBlockItem = TCMirrorBlockItem.stackFromMirror(localMirror);
        checks.add(check("normal_mirror_block_item_preserves_link_component",
                linkedBlockItem.is(TCItems.MIRROR.get())
                        && linkedBlockItem.has(TCDataComponents.MIRROR_LINK.get()),
                "stack=" + linkedBlockItem + ", linked=" + linkedBlockItem.has(TCDataComponents.MIRROR_LINK.get())));

        BlockPos componentTargetSupport = origin.offset(0, 0, 10);
        BlockPos componentTargetPos = componentTargetSupport.north();
        BlockPos componentPlacedSupport = origin.offset(4, 0, 10);
        BlockPos componentPlacedPos = componentPlacedSupport.north();
        level.setBlock(componentTargetSupport, Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(componentPlacedSupport, Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(componentTargetPos, mirrorNorth, 3);
        level.setBlock(componentPlacedPos, mirrorNorth, 3);
        TCMirrorBlockEntity componentTargetMirror = blockEntity(level, componentTargetPos, TCMirrorBlockEntity.class);
        TCMirrorBlockEntity componentPlacedMirror = blockEntity(level, componentPlacedPos, TCMirrorBlockEntity.class);
        TCMirrorLinkComponent placementLink = TCMirrorLinkComponent.of(level, componentTargetPos);
        if (componentPlacedMirror != null) {
            componentPlacedMirror.applyLinkComponent(placementLink);
        }
        checks.add(check("normal_mirror_placed_linked_stack_restores_pair",
                componentTargetMirror != null
                        && componentPlacedMirror != null
                        && componentPlacedMirror.isLinkValidSimple()
                        && componentTargetMirror.isLinkValidSimple()
                        && componentPlacedMirror.linkPos().equals(componentTargetPos)
                        && componentTargetMirror.linkPos().equals(componentPlacedPos),
                "target=" + (componentTargetMirror != null)
                        + ", placed=" + (componentPlacedMirror != null)
                        + ", placedLinked=" + (componentPlacedMirror != null && componentPlacedMirror.isLinkValidSimple())));

        ItemEntity itemEntity = new ItemEntity(level, localPos.getX() + 0.5D, localPos.getY() + 0.2D, localPos.getZ() + 0.5D,
                new ItemStack(Items.COBBLESTONE, 3));
        boolean transported = localMirror.transportEntity(itemEntity);
        checks.add(check("normal_mirror_transports_item_entity_to_remote_queue",
                transported
                        && itemEntity.isRemoved()
                        && remoteMirror.queuedItemCount() == 3
                        && localMirror.instability() == 3,
                "transported=" + transported + ", removed=" + itemEntity.isRemoved()
                        + ", remoteQueued=" + remoteMirror.queuedItemCount()
                        + ", localInstability=" + localMirror.instability()));

        for (int tick = 0; tick < 22; tick++) {
            TCMirrorBlockEntity.serverTick(level, remotePos, remoteMirror.getBlockState(), remoteMirror);
        }
        checks.add(check("normal_mirror_ejects_one_queued_item_after_legacy_delay",
                remoteMirror.queuedItemCount() == 2 && remoteMirror.instability() == 1,
                "remoteQueued=" + remoteMirror.queuedItemCount() + ", remoteInstability=" + remoteMirror.instability()));

        remoteMirror.clearQueuedStacksForValidation();
        remoteMirror.setInstabilityForValidation(129);
        TCMirrorBlockEntity.serverTick(level, remotePos, remoteMirror.getBlockState(), remoteMirror);
        checks.add(check("normal_mirror_instability_pollutes_and_decays_like_legacy",
                remoteMirror.instability() == 1,
                "instabilityAfterTick=" + remoteMirror.instability()));

        ItemStack handMirror = new ItemStack(TCItems.HAND_MIRROR.get());
        handMirror.set(TCDataComponents.MIRROR_LINK.get(), TCMirrorLinkComponent.of(level, remotePos));
        checks.add(check("linked_hand_mirror_uses_data_component_and_foil",
                handMirror.hasFoil(),
                "linked=" + handMirror.has(TCDataComponents.MIRROR_LINK.get())));

        var fakePlayer = FakePlayerFactory.getMinecraft(level);
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, handMirror.copy());
        TCHandMirrorMenu menu = new TCHandMirrorMenu(1, fakePlayer.getInventory(), InteractionHand.MAIN_HAND, fakePlayer.getInventory().selected);
        boolean rejectsMirror = !menu.getSlot(TCHandMirrorMenu.SLOT_INPUT).mayPlace(new ItemStack(TCItems.HAND_MIRROR.get()));
        menu.getSlot(TCHandMirrorMenu.SLOT_INPUT).set(new ItemStack(Items.DIRT, 2));
        menu.onInputChanged();
        checks.add(check("hand_mirror_menu_rejects_self_and_transports_input_stack",
                rejectsMirror
                        && menu.getSlot(TCHandMirrorMenu.SLOT_INPUT).getItem().isEmpty()
                        && remoteMirror.queuedItemCount() == 2,
                "rejectsMirror=" + rejectsMirror + ", slotEmpty=" + menu.getSlot(TCHandMirrorMenu.SLOT_INPUT).getItem().isEmpty()
                        + ", remoteQueued=" + remoteMirror.queuedItemCount()));

        ItemStack missingTargetMirror = new ItemStack(TCItems.HAND_MIRROR.get());
        BlockPos missingPos = remotePos.offset(0, 3, 0);
        missingTargetMirror.set(TCDataComponents.MIRROR_LINK.get(), TCMirrorLinkComponent.of(level, missingPos));
        boolean missingResult = ItemHandMirror.transport(missingTargetMirror, new ItemStack(Items.STONE), fakePlayer);
        checks.add(check("hand_mirror_missing_target_breaks_link_without_transport",
                !missingResult && !missingTargetMirror.has(TCDataComponents.MIRROR_LINK.get()),
                "result=" + missingResult + ", linked=" + missingTargetMirror.has(TCDataComponents.MIRROR_LINK.get())));
    }

    private static boolean sameBounds(AABB bounds, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        double scale = 1.0D / 16.0D;
        return close(bounds.minX, minX * scale)
                && close(bounds.minY, minY * scale)
                && close(bounds.minZ, minZ * scale)
                && close(bounds.maxX, maxX * scale)
                && close(bounds.maxY, maxY * scale)
                && close(bounds.maxZ, maxZ * scale);
    }

    private static boolean close(double left, double right) {
        return Math.abs(left - right) < 0.0001D;
    }

    private static <T> T blockEntity(ServerLevel level, BlockPos pos, Class<T> type) {
        Object blockEntity = level.getBlockEntity(pos);
        return type.isInstance(blockEntity) ? type.cast(blockEntity) : null;
    }

    private static Check check(String name, boolean passed, String notes) {
        return new Check(name, passed, notes);
    }

    public record Report(List<Check> checks) {
        public long passed() {
            return checks.stream().filter(Check::passed).count();
        }

        public long failed() {
            return checks.size() - passed();
        }
    }

    private record Check(String name, boolean passed, String notes) {
    }
}

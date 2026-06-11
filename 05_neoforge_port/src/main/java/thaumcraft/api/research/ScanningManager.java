package thaumcraft.api.research;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;
import thaumcraft.common.research.TCPlayerKnowledge;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.research.TCResearchManager;

public final class ScanningManager {
    private static final CopyOnWriteArrayList<IScanThing> THINGS = new CopyOnWriteArrayList<>();

    private ScanningManager() {
    }

    public static void addScannableThing(IScanThing object) {
        if (object != null) {
            if (object instanceof ScanAspect && THINGS.contains(object)) {
                return;
            }
            THINGS.add(object);
        }
    }

    public static void clearScannableThings() {
        THINGS.clear();
    }

    public static List<IScanThing> getScannableThings() {
        return List.copyOf(THINGS);
    }

    public static ScanEvaluation evaluateScan(ServerPlayer player, Object object) {
        boolean found = false;
        boolean suppressMessage = false;
        ArrayList<String> researchKeys = new ArrayList<>();
        ArrayList<IScanThing> matchedThings = new ArrayList<>();

        for (IScanThing thing : THINGS) {
            if (thing.checkThing(player, object)) {
                found = true;
                matchedThings.add(thing);

                String key = thing.getResearchKey(player, object);
                if (key == null || key.isBlank()) {
                    suppressMessage = true;
                } else {
                    researchKeys.add(TCPlayerKnowledge.normalizeResearchKey(key));
                }
            }
        }

        return new ScanEvaluation(found, suppressMessage, List.copyOf(researchKeys), List.copyOf(matchedThings));
    }

    public static ScanEvaluation scanTheThing(ServerPlayer player, Object object) {
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        boolean found = false;
        boolean suppressMessage = false;
        ArrayList<String> researchKeys = new ArrayList<>();
        ArrayList<IScanThing> matchedThings = new ArrayList<>();

        for (IScanThing thing : THINGS) {
            if (!thing.checkThing(player, object)) {
                continue;
            }

            String key = thing.getResearchKey(player, object);
            if (key == null || key.isBlank()) {
                found = true;
                suppressMessage = true;
                matchedThings.add(thing);
                thing.onSuccess(player, object);
            } else {
                String researchKey = TCPlayerKnowledge.normalizeResearchKey(key);
                if (TCResearchManager.isResearchKnown(knowledge, researchKey) || researchKeys.contains(researchKey)) {
                    continue;
                }

                if (TCResearchManager.progressResearch(player, researchKey)) {
                    found = true;
                    researchKeys.add(researchKey);
                    matchedThings.add(thing);
                    thing.onSuccess(player, object);
                }
            }
        }

        scanInventoryAbove(player, object);
        return new ScanEvaluation(found, suppressMessage, List.copyOf(researchKeys), List.copyOf(matchedThings));
    }

    private static void scanInventoryAbove(ServerPlayer player, Object object) {
        if (!(object instanceof BlockPos pos)) {
            return;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(pos.above());
        if (!(blockEntity instanceof Container container)) {
            return;
        }

        int scanned = 0;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (!stack.isEmpty()) {
                scanTheThing(player, stack);
                scanned++;
            }

            if (scanned >= 100) {
                player.displayClientMessage(Component.translatable("tc.invtoolarge"), true);
                break;
            }
        }
    }

    public static boolean isThingStillScannable(ServerPlayer player, Object object) {
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);

        for (IScanThing thing : THINGS) {
            if (thing.checkThing(player, object)) {
                String key = thing.getResearchKey(player, object);
                String researchKey = TCPlayerKnowledge.normalizeResearchKey(key);
                if (key != null && !key.isBlank() && !TCResearchManager.isResearchKnown(knowledge, researchKey)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static ItemStack getItemFromParams(ServerPlayer player, Object object) {
        if (object instanceof ItemStack stack) {
            return stack;
        }

        if (object instanceof ItemEntity itemEntity) {
            return itemEntity.getItem();
        }

        if (object instanceof BlockPos pos) {
            BlockState state = player.level().getBlockState(pos);
            ItemStack stack = new ItemStack(state.getBlock().asItem());

            if (!stack.isEmpty()) {
                return stack;
            }

            if (state.getFluidState().is(FluidTags.WATER)) {
                return new ItemStack(Items.WATER_BUCKET);
            }

            if (state.getFluidState().is(FluidTags.LAVA)) {
                return new ItemStack(Items.LAVA_BUCKET);
            }
        }

        return ItemStack.EMPTY;
    }

    public record ScanEvaluation(
            boolean found,
            boolean suppressMessage,
            List<String> researchKeys,
            List<IScanThing> matchedThings
    ) {
    }
}

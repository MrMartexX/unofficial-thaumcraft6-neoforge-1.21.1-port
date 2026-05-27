package thaumcraft.common.tiles.crafting;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.common.menu.TCResearchTableMenu;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.research.TCKnowledgeType;
import thaumcraft.common.research.TCResearchManager;
import thaumcraft.common.research.theorycraft.TCResearchTableData;
import thaumcraft.common.research.theorycraft.TCResearchTableSyncPayload;

public class TCResearchTableBlockEntity extends BlockEntity implements Container, MenuProvider {
    public static final int SLOT_SCRIBING_TOOLS = 0;
    public static final int SLOT_PAPER = 1;
    private static final int SLOT_COUNT = 2;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private TCResearchTableData theoryData;

    public TCResearchTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.RESEARCH_TABLE.get(), pos, blockState);
    }

    public ItemStack getScribingTools() {
        return items.get(SLOT_SCRIBING_TOOLS);
    }

    public int getPaperCount() {
        return items.get(SLOT_PAPER).getCount();
    }

    public boolean canInsertPaper() {
        return items.get(SLOT_PAPER).getCount() < Items.PAPER.getDefaultInstance().getMaxStackSize();
    }

    public TCResearchTableData getTheoryData() {
        return theoryData;
    }

    public void setTheoryData(@Nullable TCResearchTableData theoryData) {
        this.theoryData = theoryData;
        setChanged();
    }

    public boolean hasUsableScribingTools() {
        ItemStack tools = items.get(SLOT_SCRIBING_TOOLS);
        return tools.getItem() instanceof IScribeTools
                && tools.isDamageableItem()
                && tools.getDamageValue() < tools.getMaxDamage();
    }

    public boolean setScribingTools(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof IScribeTools)) {
            return false;
        }
        ItemStack stored = stack.copy();
        stored.setCount(1);
        items.set(SLOT_SCRIBING_TOOLS, stored);
        setChanged();
        return true;
    }

    public int insertPaper(ItemStack stack) {
        if (stack.isEmpty() || !stack.is(Items.PAPER)) {
            return 0;
        }

        ItemStack current = items.get(SLOT_PAPER);
        int max = stack.getMaxStackSize();
        int inserted = Math.min(stack.getCount(), max - current.getCount());
        if (inserted <= 0) {
            return 0;
        }

        if (current.isEmpty()) {
            ItemStack stored = new ItemStack(Items.PAPER, inserted);
            items.set(SLOT_PAPER, stored);
        } else {
            current.grow(inserted);
        }
        setChanged();
        return inserted;
    }

    public boolean consumeInkFromTable() {
        ItemStack tools = items.get(SLOT_SCRIBING_TOOLS);
        if (tools.getItem() instanceof IScribeTools
                && tools.isDamageableItem()
                && tools.getDamageValue() < tools.getMaxDamage()) {
            tools.setDamageValue(tools.getDamageValue() + 1);
            setChanged();
            return true;
        }
        return false;
    }

    public void finishTheory(ServerPlayer player) {
        if (theoryData == null) {
            return;
        }

        for (Map.Entry<String, Integer> entry : calculateTheoryRawAwards(theoryData).entrySet()) {
            int rawAmount = entry.getValue();
            TCResearchManager.addKnowledgeRaw(player, TCKnowledgeType.THEORY, entry.getKey(), rawAmount);
        }

        theoryData = null;
        setChanged();
    }

    public static Map<String, Integer> calculateTheoryRawAwards(TCResearchTableData data) {
        if (data == null) {
            return Map.of();
        }

        ArrayList<Map.Entry<String, Integer>> sorted = new ArrayList<>(data.categoryTotals.entrySet());
        sorted.sort((left, right) -> right.getValue().compareTo(left.getValue()));

        LinkedHashMap<String, Integer> awards = new LinkedHashMap<>();
        int index = 0;
        for (Map.Entry<String, Integer> entry : sorted) {
            int rawAmount = Math.round(entry.getValue() / 100.0F * TCKnowledgeType.THEORY.rawUnitsPerPoint());
            if (index > data.penaltyStart) {
                rawAmount = (int) Math.max(1.0D, rawAmount * 0.666666667D);
            }
            awards.put(entry.getKey(), rawAmount);
            index++;
        }
        return awards;
    }

    public boolean consumePaperFromTable() {
        ItemStack paper = items.get(SLOT_PAPER);
        if (paper.is(Items.PAPER) && !paper.isEmpty()) {
            paper.shrink(1);
            if (paper.isEmpty()) {
                items.set(SLOT_PAPER, ItemStack.EMPTY);
            }
            setChanged();
            return true;
        }
        return false;
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < items.size() ? items.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) {
            setChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= items.size()) {
            return;
        }
        ItemStack stored = stack.copy();
        stored.limitSize(getMaxStackSize(stored));
        items.set(slot, stored);
        setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case SLOT_SCRIBING_TOOLS -> stack.getItem() instanceof IScribeTools;
            case SLOT_PAPER -> stack.is(Items.PAPER);
            default -> false;
        };
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.thaumcraft.research_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new TCResearchTableMenu(containerId, playerInventory, this);
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(worldPosition);
    }

    public void dropContents(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }

        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                items.set(slot, ItemStack.EMPTY);
            }
        }
    }

    public TCResearchTableSyncPayload toSyncPayload() {
        return new TCResearchTableSyncPayload(
                worldPosition,
                theoryData != null,
                theoryData == null ? new CompoundTag() : theoryData.serialize()
        );
    }

    public void applyTheoryDataFromSync(TCResearchTableSyncPayload payload) {
        if (!worldPosition.equals(payload.pos())) {
            return;
        }
        if (!payload.hasData()) {
            theoryData = null;
            return;
        }
        TCResearchTableData data = new TCResearchTableData();
        data.deserialize(payload.data());
        theoryData = data;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        if (theoryData != null) {
            tag.put("note", theoryData.serialize());
        } else {
            tag.remove("note");
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int slot = 0; slot < items.size(); slot++) {
            items.set(slot, ItemStack.EMPTY);
        }
        ContainerHelper.loadAllItems(tag, items, registries);
        if (tag.contains("note")) {
            TCResearchTableData data = new TCResearchTableData();
            data.deserialize(tag.getCompound("note"));
            theoryData = data;
        } else {
            theoryData = null;
        }
    }
}

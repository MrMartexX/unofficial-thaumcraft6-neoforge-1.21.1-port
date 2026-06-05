package thaumcraft.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.common.crafting.arcane.TCArcaneWorkbenchCrafting;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCMenus;
import thaumcraft.common.tiles.crafting.TCArcaneWorkbenchBlockEntity;

public class TCArcaneWorkbenchMenu extends AbstractContainerMenu {
    public static final int SLOT_RESULT = 0;
    public static final int SLOT_MATRIX_START = 1;
    public static final int SLOT_MATRIX_END = SLOT_MATRIX_START + TCArcaneWorkbenchBlockEntity.MATRIX_SLOT_COUNT;
    public static final int SLOT_CRYSTAL_START = SLOT_MATRIX_END;
    public static final int SLOT_CRYSTAL_END = SLOT_CRYSTAL_START + TCArcaneWorkbenchBlockEntity.CRYSTAL_SLOT_COUNT;
    public static final int PLAYER_INVENTORY_START = SLOT_CRYSTAL_END;
    public static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    public static final int PLAYER_HOTBAR_START = PLAYER_INVENTORY_END;
    public static final int PLAYER_HOTBAR_END = PLAYER_HOTBAR_START + 9;

    public static final int[] CRYSTAL_X = new int[] {64, 17, 112, 17, 112, 64};
    public static final int[] CRYSTAL_Y = new int[] {13, 35, 35, 93, 93, 115};
    private static final int FLAG_HAS_VIS = 1;
    private static final int FLAG_HAS_CRYSTALS = 1 << 1;
    private static final int FLAG_HAS_RESEARCH = 1 << 2;
    private static final int FLAG_ARCANE_RECIPE = 1 << 3;
    private static final int FLAG_BLOCKED = 1 << 4;

    private final Inventory playerInventory;
    private final Container workbench;
    private final TCArcaneWorkbenchBlockEntity blockEntity;
    private final ResultContainer resultContainer = new ResultContainer();
    private TCArcaneWorkbenchCrafting.ResolvedCraft currentCraft = TCArcaneWorkbenchCrafting.ResolvedCraft.empty();
    private int syncedKind = TCArcaneWorkbenchCrafting.Kind.EMPTY.ordinal();
    private int syncedVisCost;
    private int syncedAvailableVis;
    private int syncedFlags = FLAG_HAS_VIS | FLAG_HAS_CRYSTALS | FLAG_HAS_RESEARCH;
    private int syncedCrystalMask;

    public TCArcaneWorkbenchMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, getClientWorkbench(playerInventory, extraData));
    }

    public TCArcaneWorkbenchMenu(int containerId, Inventory playerInventory, TCArcaneWorkbenchBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, blockEntity);
    }

    private TCArcaneWorkbenchMenu(int containerId, Inventory playerInventory, Container workbench) {
        this(containerId, playerInventory, workbench, workbench instanceof TCArcaneWorkbenchBlockEntity arcaneWorkbench ? arcaneWorkbench : null);
    }

    private TCArcaneWorkbenchMenu(int containerId, Inventory playerInventory, Container workbench, TCArcaneWorkbenchBlockEntity blockEntity) {
        super(TCMenus.ARCANE_WORKBENCH.get(), containerId);
        checkContainerSize(workbench, TCArcaneWorkbenchBlockEntity.SLOT_COUNT);
        this.playerInventory = playerInventory;
        this.workbench = workbench;
        this.blockEntity = blockEntity;

        addSlot(new ArcaneResultSlot(this, resultContainer, 0, 160, 64));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int slot = column + row * 3;
                addSlot(new WorkbenchInputSlot(workbench, slot, 40 + column * 24, 40 + row * 24));
            }
        }

        for (int index = 0; index < TCArcaneWorkbenchBlockEntity.CRYSTAL_SLOT_COUNT; index++) {
            addSlot(new CrystalSlot(
                    workbench,
                    TCArcaneWorkbenchBlockEntity.CRYSTAL_SLOT_START + index,
                    CRYSTAL_X[index],
                    CRYSTAL_Y[index],
                    TCArcaneWorkbenchCrafting.PRIMAL_ASPECT_ORDER.get(index)
            ));
        }

        bindPlayerInventory(playerInventory);
        bindArcaneFeedbackData();
        updateResult();
    }

    public TCArcaneWorkbenchBlockEntity blockEntity() {
        return blockEntity;
    }

    public BlockPos blockPos() {
        return blockEntity == null ? BlockPos.ZERO : blockEntity.getBlockPos();
    }

    public TCArcaneWorkbenchCrafting.ResolvedCraft currentCraft() {
        return currentCraft;
    }

    public int recipeKind() {
        return syncedKind;
    }

    public int visCost() {
        return syncedVisCost;
    }

    public int availableVis() {
        return syncedAvailableVis;
    }

    public int requiredCrystalMask() {
        return syncedCrystalMask;
    }

    public boolean hasVis() {
        return (syncedFlags & FLAG_HAS_VIS) != 0;
    }

    public boolean hasCrystals() {
        return (syncedFlags & FLAG_HAS_CRYSTALS) != 0;
    }

    public boolean hasResearch() {
        return (syncedFlags & FLAG_HAS_RESEARCH) != 0;
    }

    public boolean hasArcaneRecipe() {
        return (syncedFlags & FLAG_ARCANE_RECIPE) != 0;
    }

    public boolean isBlocked() {
        return (syncedFlags & FLAG_BLOCKED) != 0;
    }

    public boolean isCrystalRequired(int crystalIndex) {
        return (syncedCrystalMask & crystalMask(crystalIndex)) != 0;
    }

    public static int crystalMask(int crystalIndex) {
        return crystalIndex < 0 ? 0 : 1 << crystalIndex;
    }

    private static Container getClientWorkbench(Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        if (extraData == null) {
            return new SimpleContainer(TCArcaneWorkbenchBlockEntity.SLOT_COUNT);
        }
        BlockPos pos = extraData.readBlockPos();
        Level level = playerInventory.player.level();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof TCArcaneWorkbenchBlockEntity workbench
                ? workbench
                : new SimpleContainer(TCArcaneWorkbenchBlockEntity.SLOT_COUNT);
    }

    private void bindPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 16 + column * 18, 151 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 16 + column * 18, 209));
        }
    }

    private void bindArcaneFeedbackData() {
        addDataSlot(slot(
                () -> syncedKind,
                value -> syncedKind = value
        ));
        addDataSlot(slot(
                () -> syncedVisCost,
                value -> syncedVisCost = value
        ));
        addDataSlot(slot(
                () -> syncedAvailableVis,
                value -> syncedAvailableVis = value
        ));
        addDataSlot(slot(
                () -> syncedFlags,
                value -> syncedFlags = value
        ));
        addDataSlot(slot(
                () -> syncedCrystalMask,
                value -> syncedCrystalMask = value
        ));
    }

    private DataSlot slot(IntGetter getter, IntSetter setter) {
        return new DataSlot() {
            @Override
            public int get() {
                return getter.get();
            }

            @Override
            public void set(int value) {
                setter.set(value);
            }
        };
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        updateResult();
    }

    private void updateResult() {
        refreshCraftState();
        broadcastChanges();
    }

    private void refreshCraftState() {
        if (blockEntity != null && playerInventory.player instanceof ServerPlayer serverPlayer) {
            currentCraft = TCArcaneWorkbenchCrafting.resolve(serverPlayer, blockEntity);
            resultContainer.setItem(0, currentCraft.output());
            syncedKind = currentCraft.kind().ordinal();
            syncedVisCost = currentCraft.vis();
            syncedAvailableVis = blockEntity.availableVis();
            syncedFlags = flags(currentCraft);
            syncedCrystalMask = crystalMask(currentCraft);
        } else {
            currentCraft = TCArcaneWorkbenchCrafting.ResolvedCraft.empty();
            resultContainer.setItem(0, ItemStack.EMPTY);
        }
    }

    private static int flags(TCArcaneWorkbenchCrafting.ResolvedCraft craft) {
        int flags = 0;
        if (craft.hasVis()) {
            flags |= FLAG_HAS_VIS;
        }
        if (craft.hasCrystals()) {
            flags |= FLAG_HAS_CRYSTALS;
        }
        if (craft.hasResearch()) {
            flags |= FLAG_HAS_RESEARCH;
        }
        if (craft.kind() == TCArcaneWorkbenchCrafting.Kind.ARCANE
                || craft.kind() == TCArcaneWorkbenchCrafting.Kind.ARCANE_BLOCKED) {
            flags |= FLAG_ARCANE_RECIPE;
        }
        if (craft.kind() == TCArcaneWorkbenchCrafting.Kind.ARCANE_BLOCKED) {
            flags |= FLAG_BLOCKED;
        }
        return flags;
    }

    private static int crystalMask(TCArcaneWorkbenchCrafting.ResolvedCraft craft) {
        int mask = 0;
        for (var cost : craft.crystalCosts()) {
            int index = TCArcaneWorkbenchCrafting.PRIMAL_ASPECT_ORDER.indexOf(cost.aspect());
            mask |= crystalMask(index);
        }
        return mask;
    }

    private void craftResult(Player player, ItemStack crafted) {
        if (!(player instanceof ServerPlayer serverPlayer) || blockEntity == null || currentCraft.output().isEmpty()) {
            return;
        }
        TCArcaneWorkbenchCrafting.craft(serverPlayer, blockEntity, currentCraft);
        updateResult();
    }

    @Override
    public void broadcastChanges() {
        refreshCraftState();
        super.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = slot.getItem();
        ItemStack original = stackInSlot.copy();
        if (index == SLOT_RESULT) {
            if (!moveItemStackTo(stackInSlot, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, original);
        } else if (index >= SLOT_MATRIX_START && index < SLOT_CRYSTAL_END) {
            if (!moveItemStackTo(stackInSlot, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= PLAYER_INVENTORY_START && index < PLAYER_HOTBAR_END) {
            int crystalSlot = crystalMenuSlotFor(stackInSlot);
            if (crystalSlot >= SLOT_CRYSTAL_START && crystalSlot < SLOT_CRYSTAL_END
                    && moveItemStackTo(stackInSlot, crystalSlot, crystalSlot + 1, false)) {
                // Crystal moved into its legacy fixed crystal slot.
            } else if (index >= PLAYER_INVENTORY_START && index < PLAYER_INVENTORY_END) {
                if (!moveItemStackTo(stackInSlot, PLAYER_HOTBAR_START, PLAYER_HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= PLAYER_HOTBAR_START && index < PLAYER_HOTBAR_END
                    && !moveItemStackTo(stackInSlot, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stackInSlot.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    private static int crystalMenuSlotFor(ItemStack stack) {
        for (int index = 0; index < TCArcaneWorkbenchCrafting.PRIMAL_ASPECT_ORDER.size(); index++) {
            if (TCArcaneWorkbenchCrafting.isCrystal(stack, TCArcaneWorkbenchCrafting.PRIMAL_ASPECT_ORDER.get(index))) {
                return SLOT_CRYSTAL_START + index;
            }
        }
        return -1;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null || blockEntity.getLevel() == null) {
            return false;
        }
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player,
                TCBlocks.ARCANE_WORKBENCH.get()
        );
    }

    private class WorkbenchInputSlot extends Slot {
        private WorkbenchInputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            TCArcaneWorkbenchMenu.this.slotsChanged(container);
        }
    }

    private final class CrystalSlot extends WorkbenchInputSlot {
        private final String aspectTag;

        private CrystalSlot(Container container, int slot, int x, int y, String aspectTag) {
            super(container, slot, x, y);
            this.aspectTag = aspectTag;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return TCArcaneWorkbenchCrafting.isCrystal(stack, aspectTag);
        }
    }

    private static final class ArcaneResultSlot extends Slot {
        private final TCArcaneWorkbenchMenu menu;

        private ArcaneResultSlot(TCArcaneWorkbenchMenu menu, Container container, int slot, int x, int y) {
            super(container, slot, x, y);
            this.menu = menu;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            menu.craftResult(player, stack);
            super.onTake(player, stack);
        }
    }

    @FunctionalInterface
    private interface IntGetter {
        int get();
    }

    @FunctionalInterface
    private interface IntSetter {
        void set(int value);
    }
}

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
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCMenus;
import thaumcraft.common.research.theorycraft.TCResearchTableNetwork;
import thaumcraft.common.tiles.crafting.TCResearchTableBlockEntity;

public class TCResearchTableMenu extends AbstractContainerMenu {
    public static final int TABLE_SLOT_COUNT = 2;
    public static final int SLOT_SCRIBING_TOOLS = 0;
    public static final int SLOT_PAPER = 1;

    private static final int PLAYER_INVENTORY_START = TABLE_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int PLAYER_HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int PLAYER_HOTBAR_END = PLAYER_HOTBAR_START + 9;

    private final TCResearchTableBlockEntity blockEntity;

    public TCResearchTableMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, getClientTable(playerInventory, extraData));
    }

    public TCResearchTableMenu(int containerId, Inventory playerInventory, TCResearchTableBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, blockEntity);
    }

    private TCResearchTableMenu(int containerId, Inventory playerInventory, Container table) {
        this(containerId, playerInventory, table, table instanceof TCResearchTableBlockEntity researchTable ? researchTable : null);
    }

    private TCResearchTableMenu(int containerId, Inventory playerInventory, Container table, TCResearchTableBlockEntity blockEntity) {
        super(TCMenus.RESEARCH_TABLE.get(), containerId);
        checkContainerSize(table, TABLE_SLOT_COUNT);
        this.blockEntity = blockEntity;

        addSlot(new Slot(table, SLOT_SCRIBING_TOOLS, 16, 15) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof IScribeTools;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        addSlot(new Slot(table, SLOT_PAPER, 224, 16) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.PAPER);
            }
        });

        bindPlayerInventory(playerInventory);

        if (blockEntity != null && playerInventory.player instanceof ServerPlayer serverPlayer) {
            TCResearchTableNetwork.syncToPlayer(serverPlayer, blockEntity);
        }
    }

    public TCResearchTableBlockEntity blockEntity() {
        return blockEntity;
    }

    public BlockPos blockPos() {
        return blockEntity == null ? BlockPos.ZERO : blockEntity.getBlockPos();
    }

    private static Container getClientTable(Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        if (extraData == null) {
            return new SimpleContainer(TABLE_SLOT_COUNT);
        }
        BlockPos pos = extraData.readBlockPos();
        Level level = playerInventory.player.level();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof TCResearchTableBlockEntity table ? table : new SimpleContainer(TABLE_SLOT_COUNT);
    }

    private void bindPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 77 + column * 18, 190 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                addSlot(new Slot(playerInventory, column + row * 3, 20 + column * 18, 190 + row * 18));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = slot.getItem();
        ItemStack original = stackInSlot.copy();
        if (index < TABLE_SLOT_COUNT) {
            if (!moveItemStackTo(stackInSlot, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (stackInSlot.getItem() instanceof IScribeTools) {
            if (!moveItemStackTo(stackInSlot, SLOT_SCRIBING_TOOLS, SLOT_SCRIBING_TOOLS + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (stackInSlot.is(Items.PAPER)) {
            if (!moveItemStackTo(stackInSlot, SLOT_PAPER, SLOT_PAPER + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= PLAYER_INVENTORY_START && index < PLAYER_INVENTORY_END) {
            if (!moveItemStackTo(stackInSlot, PLAYER_HOTBAR_START, PLAYER_HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= PLAYER_HOTBAR_START && index < PLAYER_HOTBAR_END
                && !moveItemStackTo(stackInSlot, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
            return ItemStack.EMPTY;
        }

        if (stackInSlot.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null || blockEntity.getLevel() == null) {
            return false;
        }
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player,
                TCBlocks.RESEARCH_TABLE.get()
        );
    }
}

package thaumcraft.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import thaumcraft.common.registry.TCMenus;
import thaumcraft.common.tiles.devices.TCPotionSprayerBlockEntity;

/**
 * Menu for the Potion Sprayer.
 * Handles inserted potion slot, essentia charge progress and player inventory.
 */
public final class TCPotionSprayerMenu extends AbstractContainerMenu {
    public static final int SLOT_POTION = 0;
    public static final int PLAYER_INVENTORY_START = 1;
    public static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    public static final int PLAYER_HOTBAR_START = PLAYER_INVENTORY_END;
    public static final int PLAYER_HOTBAR_END = PLAYER_HOTBAR_START + 9;

    private final TCPotionSprayerBlockEntity blockEntity;
    private int charges = 0;
    private int color = 0x385DC6;

    public TCPotionSprayerMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, inventory, clientSprayer(inventory, extraData));
    }

    public TCPotionSprayerMenu(int containerId, Inventory inventory, TCPotionSprayerBlockEntity sprayer) {
        super(TCMenus.POTION_SPRAYER.get(), containerId);
        this.blockEntity = sprayer;

        // Potion slot at (56, 64)
        if (sprayer != null) {
            addSlot(new SlotItemHandler(sprayer.getItemHandler(), SLOT_POTION, 56, 64) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.has(DataComponents.POTION_CONTENTS);
                }
            });
        } else {
            addSlot(new Slot(new SimpleContainer(1), SLOT_POTION, 56, 64) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.has(DataComponents.POTION_CONTENTS);
                }
            });
        }

        // Player inventory (3 rows x 9 columns) at (16, 151)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 16 + col * 18, 151 + row * 18));
            }
        }

        // Player hotbar (9 slots) at (16, 209)
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 16 + col * 18, 209));
        }

        // DataSlot for charges (0..8)
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity != null ? blockEntity.getCharges() : charges;
            }

            @Override
            public void set(int value) {
                charges = value;
            }
        });

        // DataSlots for RGB color (lower 16 bits and upper 16 bits)
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                int c = blockEntity != null ? blockEntity.getColor() : color;
                return c & 0xFFFF;
            }

            @Override
            public void set(int value) {
                color = (color & 0xFFFF0000) | (value & 0xFFFF);
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                int c = blockEntity != null ? blockEntity.getColor() : color;
                return (c >> 16) & 0xFFFF;
            }

            @Override
            public void set(int value) {
                color = (color & 0x0000FFFF) | ((value & 0xFFFF) << 16);
            }
        });
    }

    public int getCharges() {
        return blockEntity != null ? blockEntity.getCharges() : charges;
    }

    public int getColor() {
        return blockEntity != null ? blockEntity.getColor() : color;
    }

    public TCPotionSprayerBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity == null || blockEntity.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index == SLOT_POTION) {
                if (!moveItemStackTo(slotStack, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (slotStack.has(DataComponents.POTION_CONTENTS)) {
                    if (!moveItemStackTo(slotStack, SLOT_POTION, SLOT_POTION + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= PLAYER_INVENTORY_START && index < PLAYER_INVENTORY_END) {
                    if (!moveItemStackTo(slotStack, PLAYER_HOTBAR_START, PLAYER_HOTBAR_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= PLAYER_HOTBAR_START && index < PLAYER_HOTBAR_END
                        && !moveItemStackTo(slotStack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    private static TCPotionSprayerBlockEntity clientSprayer(Inventory inventory, RegistryFriendlyByteBuf extraData) {
        if (extraData != null) {
            BlockPos pos = extraData.readBlockPos();
            Level level = inventory.player.level();
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TCPotionSprayerBlockEntity sprayer) {
                return sprayer;
            }
        }
        return null;
    }
}

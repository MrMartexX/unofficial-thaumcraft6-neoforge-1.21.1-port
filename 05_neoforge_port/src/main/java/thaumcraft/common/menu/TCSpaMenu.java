package thaumcraft.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCMenus;
import thaumcraft.common.tiles.devices.TCArcaneSpaBlockEntity;

/**
 * Menu for the Arcane Spa.
 * Controls bath salts slot, fluid level and mix setting.
 */
public final class TCSpaMenu extends AbstractContainerMenu {
    public static final int SLOT_SALTS = 0;
    public static final int PLAYER_INVENTORY_START = 1;
    public static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    public static final int PLAYER_HOTBAR_START = PLAYER_INVENTORY_END;
    public static final int PLAYER_HOTBAR_END = PLAYER_HOTBAR_START + 9;

    private final Container spa;
    private final TCArcaneSpaBlockEntity blockEntity;
    private int mixState = 1;
    private int fluidAmount = 0;
    private int fluidId = 0;

    public TCSpaMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, inventory, clientSpa(inventory, extraData));
    }

    public TCSpaMenu(int containerId, Inventory inventory, TCArcaneSpaBlockEntity spa) {
        this(containerId, inventory, spa, spa);
    }

    private TCSpaMenu(int containerId, Inventory inventory, Container spa) {
        this(containerId, inventory, spa, spa instanceof TCArcaneSpaBlockEntity be ? be : null);
    }

    private TCSpaMenu(int containerId, Inventory inventory, Container spa, TCArcaneSpaBlockEntity blockEntity) {
        super(TCMenus.SPA.get(), containerId);
        checkContainerSize(spa, TCArcaneSpaBlockEntity.SLOT_COUNT);
        this.spa = spa;
        this.blockEntity = blockEntity;

        // Bath salts slot at (65, 31)
        addSlot(new Slot(spa, SLOT_SALTS, 65, 31) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(TCItems.BATH_SALTS.get());
            }
        });

        // Player inventory (3 rows x 9 columns)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player hotbar (9 slots)
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }

        // Data slots for mix, fluidAmount and fluidId
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity != null ? (blockEntity.mix() ? 1 : 0) : mixState;
            }

            @Override
            public void set(int value) {
                mixState = value;
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity != null ? blockEntity.fluidAmount() : fluidAmount;
            }

            @Override
            public void set(int value) {
                fluidAmount = value;
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                if (blockEntity != null) {
                    var fluidStack = blockEntity.storedFluidForValidation();
                    return fluidStack.isEmpty() ? 0 : BuiltInRegistries.FLUID.getId(fluidStack.getFluid());
                }
                return fluidId;
            }

            @Override
            public void set(int value) {
                fluidId = value;
            }
        });
    }

    public boolean isMix() {
        return (blockEntity != null ? (blockEntity.mix() ? 1 : 0) : mixState) != 0;
    }

    public int getFluidAmount() {
        return blockEntity != null ? blockEntity.fluidAmount() : fluidAmount;
    }

    public Fluid getFluid() {
        int id = blockEntity != null
                ? (blockEntity.storedFluidForValidation().isEmpty() ? 0 : BuiltInRegistries.FLUID.getId(blockEntity.storedFluidForValidation().getFluid()))
                : fluidId;
        return id == 0 ? Fluids.EMPTY : BuiltInRegistries.FLUID.byId(id);
    }

    @Override
    public boolean clickMenuButton(Player player, int button) {
        if (button == 1 && blockEntity != null) {
            blockEntity.toggleMix();
            return true;
        }
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return spa.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index == SLOT_SALTS) {
                if (!moveItemStackTo(slotStack, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (slotStack.is(TCItems.BATH_SALTS.get())) {
                    if (!moveItemStackTo(slotStack, SLOT_SALTS, SLOT_SALTS + 1, false)) {
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

    private static Container clientSpa(Inventory inventory, RegistryFriendlyByteBuf extraData) {
        if (extraData != null) {
            BlockPos pos = extraData.readBlockPos();
            Level level = inventory.player.level();
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TCArcaneSpaBlockEntity spaBe) {
                return spaBe;
            }
        }
        return new SimpleContainer(TCArcaneSpaBlockEntity.SLOT_COUNT);
    }
}

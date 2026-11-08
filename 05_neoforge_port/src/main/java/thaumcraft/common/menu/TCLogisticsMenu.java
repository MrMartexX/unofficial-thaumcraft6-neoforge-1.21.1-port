package thaumcraft.common.menu;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import thaumcraft.common.container.slot.SlotGhostFull;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.golems.seals.SealProvide;
import thaumcraft.common.registry.TCMenus;

/**
 * 1:1 legacy ContainerLogistics:
 * Displays all items available across Provide seals within 32 blocks of the player.
 * Player can browse 81 ghost slots, search, and dispatch golem delivery requests.
 */
public final class TCLogisticsMenu extends AbstractContainerMenu {
    public static final int GRID_SIZE = 81;

    private final Level level;
    private final Player player;
    private final Container input;
    private final TreeMap<String, ItemStack> items = new TreeMap<>();
    private final BlockPos targetPos;
    private final Direction targetSide;

    private int lastTotal = 0;
    private int start = 0;
    private int end = 0;
    private String searchText = "";

    public static void open(ServerPlayer player, BlockPos pos, Direction side) {
        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, inv, p) -> new TCLogisticsMenu(containerId, inv, pos, side),
                        Component.translatable("gui.thaumcraft.logistics")
                ),
                buffer -> {
                    buffer.writeBoolean(pos != null && side != null);
                    if (pos != null && side != null) {
                        buffer.writeBlockPos(pos);
                        buffer.writeByte(side.ordinal());
                    }
                }
        );
    }

    public TCLogisticsMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, inventory, readTargetPos(extraData), readTargetSide(extraData));
    }

    public TCLogisticsMenu(int containerId, Inventory inventory, BlockPos targetPos, Direction targetSide) {
        super(TCMenus.LOGISTICS.get(), containerId);
        this.level = inventory.player.level();
        this.player = inventory.player;
        this.input = new SimpleContainer(GRID_SIZE);
        this.targetPos = targetPos;
        this.targetSide = targetSide;

        // 81 ghost slots (9 x 9) at (19 + col * 19, 19 + row * 19)
        for (int i = 0; i < GRID_SIZE; i++) {
            addSlot(new SlotGhostFull(input, i, 19 + (i % 9) * 19, 19 + (i / 9) * 19));
        }

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return start;
            }

            @Override
            public void set(int value) {
                start = value;
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return end;
            }

            @Override
            public void set(int value) {
                end = value;
            }
        });

        if (!level.isClientSide) {
            refreshItemList(true);
        }
    }

    public BlockPos getTargetPos() {
        return targetPos;
    }

    public Direction getTargetSide() {
        return targetSide;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public void setSearchText(String search) {
        this.searchText = search == null ? "" : search.trim();
        if (!level.isClientSide) {
            refreshItemList(true);
        }
    }

    public void refreshItemList(boolean full) {
        int newTotal = lastTotal;
        TreeMap<String, ItemStack> ti = new TreeMap<>();
        if (full) {
            newTotal = 0;
            List<SealEntity> seals = SealHandler.getSealsInRange(level, player.blockPosition(), 32);
            for (SealEntity seal : seals) {
                if (seal.getSeal() instanceof SealProvide provide && player.getStringUUID().equals(seal.getOwner())) {
                    IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, seal.getSealPos().pos, seal.getSealPos().face);
                    if (handler == null) {
                        continue;
                    }
                    for (int slot = 0; slot < handler.getSlots(); slot++) {
                        ItemStack stack = handler.getStackInSlot(slot).copy();
                        if (!stack.isEmpty() && provide.matchesFilter(stack)) {
                            String name = stack.getHoverName().getString();
                            if (searchText.isEmpty() || name.toLowerCase().contains(searchText.toLowerCase())) {
                                String key = name + "_" + stack.getItem().getDescriptionId();
                                if (ti.containsKey(key)) {
                                    stack.grow(ti.get(key).getCount());
                                }
                                ti.put(key, stack);
                                newTotal += stack.getCount();
                            }
                        }
                    }
                }
            }
        }

        lastTotal = newTotal;
        if (full) {
            items.clear();
            items.putAll(ti);
        }

        input.clearContent();
        int j = 0;
        int q = 0;
        for (String key : items.keySet()) {
            if (++j <= start * 9) {
                continue;
            }
            input.setItem(q, items.get(key));
            if (++q >= input.getContainerSize()) {
                break;
            }
        }
        end = Math.max(0, items.size() / 9 - 8);
    }

    @Override
    public boolean clickMenuButton(Player player, int button) {
        if (button == 22) {
            refreshItemList(true);
            return true;
        }
        if (button == 0) {
            if (start < end) {
                start++;
                refreshItemList(false);
            }
            return true;
        }
        if (button == 1) {
            if (start > 0) {
                start--;
                refreshItemList(false);
            }
            return true;
        }
        if (button >= 100) {
            int s = button - 100;
            if (s >= 0 && s <= end) {
                start = s;
                refreshItemList(false);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive() && (targetPos == null || player.distanceToSqr(targetPos.getX(), targetPos.getY(), targetPos.getZ()) <= 64.0D);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    private static BlockPos readTargetPos(RegistryFriendlyByteBuf buffer) {
        if (buffer != null && buffer.readBoolean()) {
            return buffer.readBlockPos();
        }
        return null;
    }

    private static Direction readTargetSide(RegistryFriendlyByteBuf buffer) {
        if (buffer != null && buffer.isReadable()) {
            return Direction.values()[buffer.readByte()];
        }
        return null;
    }
}

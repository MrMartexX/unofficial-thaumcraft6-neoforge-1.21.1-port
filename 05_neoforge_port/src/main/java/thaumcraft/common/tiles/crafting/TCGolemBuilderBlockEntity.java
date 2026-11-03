package thaumcraft.common.tiles.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.common.essentia.transport.TCEssentiaCapabilities;
import thaumcraft.common.essentia.transport.TCEssentiaStack;
import thaumcraft.common.essentia.transport.TCEssentiaSuction;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.golems.GolemProperties;
import thaumcraft.common.golems.ItemGolemPlacer;
import thaumcraft.common.menu.TCGolemBuilderMenu;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSounds;

public class TCGolemBuilderBlockEntity extends BlockEntity implements Container, MenuProvider, TCEssentiaTransport {

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private long golem = -1L;
    private int cost = 0;
    private int maxCost = 0;
    private boolean bufferedEssentia = false;
    private int ticks = 0;
    public int press = 0;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> cost;
                case 1 -> maxCost;
                case 2 -> (int) (golem & 0xFFFFFFFFL);
                case 3 -> (int) ((golem >>> 32) & 0xFFFFFFFFL);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> cost = value;
                case 1 -> maxCost = value;
                case 2 -> golem = (golem & 0xFFFFFFFF00000000L) | (value & 0xFFFFFFFFL);
                case 3 -> golem = (golem & 0x00000000FFFFFFFFL) | (((long) value) << 32);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public TCGolemBuilderBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.GOLEM_BUILDER.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCGolemBuilderBlockEntity entity) {
        entity.ticks++;
        if (entity.ticks % 5 == 0 && entity.cost > 0 && entity.golem >= 0L) {
            if (entity.bufferedEssentia || entity.drawEssentia()) {
                entity.bufferedEssentia = false;
                entity.cost--;
                entity.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }

            if (entity.cost <= 0) {
                ItemStack placer = new ItemStack(TCItems.GOLEM_PLACER.get());
                ItemGolemPlacer.setProperties(placer, GolemProperties.fromLong(entity.golem));
                entity.setItem(0, placer);
                level.playSound(null, pos, TCSounds.WAND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                entity.cost = 0;
                entity.maxCost = 0;
                entity.golem = -1L;
                entity.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, TCGolemBuilderBlockEntity entity) {
        if (entity.cost > 0 && entity.golem >= 0L) {
            if (entity.press < 90) {
                entity.press += 6;
                if (entity.press >= 60) {
                    level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                            SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.66F, 1.0F + level.random.nextFloat() * 0.1F, false);
                }
            }
        } else {
            if (entity.press > 0) {
                entity.press = Math.max(0, entity.press - 3);
            }
        }
    }

    private boolean drawEssentia() {
        if (level == null) return false;
        for (Direction face : Direction.values()) {
            if (!isConnectable(face)) continue;
            BlockPos neighborPos = worldPosition.relative(face);
            TCEssentiaTransport transport = level.getCapability(TCEssentiaCapabilities.BLOCK, neighborPos, face.getOpposite());
            if (transport != null && transport.canOutputTo(face.getOpposite())) {
                if (transport.takeEssentia("machina", 1, face.getOpposite(), false) == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean startCraft(long id, Player player) {
        if (cost > 0 || id < 0 || GolemProperties.fromLong(id).getRank() != 0) {
            return false;
        }
        if (!getItem(0).isEmpty()) {
            return false;
        }

        IGolemProperties props = GolemProperties.fromLong(id);
        if (!(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) || (id & 0xFFFFL) != 0
                || GolemProperties.getByteInLong(id, 0) != props.getMaterial().id
                || GolemProperties.getByteInLong(id, 1) != props.getHead().id
                || GolemProperties.getByteInLong(id, 2) != props.getArms().id
                || GolemProperties.getByteInLong(id, 3) != props.getLegs().id
                || GolemProperties.getByteInLong(id, 4) != props.getAddon().id) return false;
        var knowledge = thaumcraft.common.research.TCPlayerKnowledgeStore.get(serverPlayer);
        for (String[] requirements : new String[][]{props.getMaterial().research, props.getHead().research,
                props.getArms().research, props.getLegs().research, props.getAddon().research}) {
            if (requirements != null && !thaumcraft.common.research.TCResearchManager.knowsResearchStrict(knowledge, requirements)) return false;
        }
        ItemStack[] components = props.generateComponents();

        if (!hasComponents(player, components)) {
            return false;
        }

        consumeComponents(player, components);

        int calculatedCost = props.getTraits().size() * 2;
        for (ItemStack s : components) {
            calculatedCost += s.getCount();
        }

        this.golem = id;
        this.cost = calculatedCost;
        this.maxCost = calculatedCost;
        setChanged();
        if (level != null) {
            level.playSound(null, worldPosition, TCSounds.WAND.get(), SoundSource.BLOCKS, 0.25F, 1.0F);
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        return true;
    }

    public boolean hasComponents(Player player, ItemStack[] components) {
        for (ItemStack req : components) {
            int needed = req.getCount();
            int found = countItem(player, req);
            if (found < needed) {
                return false;
            }
        }
        return true;
    }

    private int countItem(Player player, ItemStack target) {
        int count = 0;
        if (player != null) {
            for (ItemStack s : player.getInventory().items) {
                if (ItemStack.isSameItemSameComponents(s, target)) {
                    count += s.getCount();
                }
            }
        }
        if (level != null) {
            for (Direction dir : Direction.values()) {
                BlockPos p = worldPosition.relative(dir);
                IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, p, dir.getOpposite());
                if (handler != null) {
                    count += countExtractable(handler, target);
                } else {
                    BlockEntity be = level.getBlockEntity(p);
                    if (be instanceof Container c) {
                        for (int i = 0; i < c.getContainerSize(); i++) {
                            ItemStack s = c.getItem(i);
                            if (ItemStack.isSameItemSameComponents(s, target)) {
                                count += s.getCount();
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    public static int countExtractable(IItemHandler handler, ItemStack target) {
        int count = 0;
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack accessible = handler.extractItem(slot, Integer.MAX_VALUE, true);
            if (ItemStack.isSameItemSameComponents(accessible, target)) count += accessible.getCount();
        }
        return count;
    }

    private void consumeComponents(Player player, ItemStack[] components) {
        for (ItemStack req : components) {
            int needed = req.getCount();

            // First from player
            if (player != null) {
                for (int i = 0; i < player.getInventory().items.size() && needed > 0; i++) {
                    ItemStack s = player.getInventory().items.get(i);
                    if (ItemStack.isSameItemSameComponents(s, req)) {
                        int take = Math.min(needed, s.getCount());
                        s.shrink(take);
                        needed -= take;
                    }
                }
            }

            // Then from adjacent containers
            if (level != null && needed > 0) {
                for (Direction dir : Direction.values()) {
                    if (needed <= 0) break;
                    BlockPos p = worldPosition.relative(dir);
                    IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, p, dir.getOpposite());
                    if (handler != null) {
                        for (int i = 0; i < handler.getSlots() && needed > 0; i++) {
                            ItemStack s = handler.getStackInSlot(i);
                            if (ItemStack.isSameItemSameComponents(s, req)) {
                                ItemStack extracted = handler.extractItem(i, needed, false);
                                needed -= extracted.getCount();
                            }
                        }
                    } else {
                        BlockEntity be = level.getBlockEntity(p);
                        if (be instanceof Container c) {
                            for (int i = 0; i < c.getContainerSize() && needed > 0; i++) {
                                ItemStack s = c.getItem(i);
                                if (ItemStack.isSameItemSameComponents(s, req)) {
                                    int take = Math.min(needed, s.getCount());
                                    s.shrink(take);
                                    needed -= take;
                                    c.setChanged();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Container implementation
    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    // MenuProvider
    @Override
    public Component getDisplayName() {
        return Component.translatable("tile.golem_builder.name");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new TCGolemBuilderMenu(id, inventory, this, dataAccess);
    }

    // TCEssentiaTransport
    @Override
    public boolean isConnectable(Direction face) {
        return face.getAxis().isHorizontal() || face == Direction.DOWN;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return isConnectable(face);
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return false;
    }

    @Override
    public TCEssentiaSuction getSuction(Direction face) {
        return (cost > 0 && golem >= 0L && canInputFrom(face))
                ? new TCEssentiaSuction("machina", 128)
                : TCEssentiaSuction.NONE;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public TCEssentiaStack getEssentia(Direction face) {
        return TCEssentiaStack.EMPTY;
    }

    @Override
    public int addEssentia(String aspect, int amount, Direction face, boolean simulate) {
        if ("machina".equals(aspect) && cost > 0 && golem >= 0L && canInputFrom(face)) {
            if (!simulate) {
                this.bufferedEssentia = true;
            }
            return 1;
        }
        return 0;
    }

    @Override
    public int takeEssentia(String aspect, int amount, Direction face, boolean simulate) {
        return 0;
    }

    // Save & Load
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putLong("golem", golem);
        tag.putInt("cost", cost);
        tag.putInt("mcost", maxCost);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, items, registries);
        this.golem = tag.getLong("golem");
        this.cost = tag.getInt("cost");
        this.maxCost = tag.getInt("mcost");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public long getGolem() {
        return golem;
    }

    public int getCost() {
        return cost;
    }

    public int getMaxCost() {
        return maxCost;
    }
}

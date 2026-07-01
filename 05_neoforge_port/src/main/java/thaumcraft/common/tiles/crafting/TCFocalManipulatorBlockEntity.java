package thaumcraft.common.tiles.crafting;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.crafting.arcane.TCArcaneWorkbenchCrafting;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.casters.TCFocusElementDefinition;
import thaumcraft.common.items.casters.TCFocusElements;
import thaumcraft.common.items.casters.TCFocusPackageHelper;
import thaumcraft.common.items.components.TCFocusPackageComponent;
import thaumcraft.common.menu.TCFocalManipulatorMenu;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.research.TCResearchManager;
import thaumcraft.common.world.aura.AuraHandler;

public class TCFocalManipulatorBlockEntity extends BlockEntity implements Container, MenuProvider {
    public static final int SLOT_FOCUS = 0;
    public static final int SLOT_COUNT = 1;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private String designNodes = "";
    private String focusName = "";
    private TCFocusPackageComponent pendingPackage = TCFocusPackageComponent.EMPTY;
    private float remainingVis;
    private int xpCost;
    private Map<String, Integer> crystalCosts = Map.of();
    private int ticks;

    public TCFocalManipulatorBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.FOCAL_MANIPULATOR.get(), pos, blockState);
    }

    public void setDesign(String encodedNodes, String name) {
        designNodes = encodedNodes == null ? "" : encodedNodes.trim();
        focusName = name == null ? "" : name.trim();
        setChanged();
    }

    public boolean applyDesignRequest(ServerPlayer player, String encodedNodes, String name) {
        if (player == null || level == null || level.isClientSide || remainingVis > 0.0F) {
            return false;
        }
        List<TCFocusPackageHelper.NodeInstance> nodes = TCFocusPackageHelper.decode(encodedNodes);
        if (nodes.isEmpty() || !nodesHaveKnownDefinitions(nodes)) {
            failSound();
            return false;
        }
        TCFocusPackageComponent packageData = TCFocusPackageHelper.buildPackage(nodes);
        if (packageData.isEmpty() || packageData.complexity() <= 0) {
            failSound();
            return false;
        }
        designNodes = packageData.nodes();
        focusName = name == null ? "" : name.trim();
        if (focusName.length() > 64) {
            focusName = focusName.substring(0, 64);
        }
        setChanged();
        return true;
    }

    public String designNodes() {
        return designNodes;
    }

    public float remainingVis() {
        return remainingVis;
    }

    public int xpCost() {
        return xpCost;
    }

    public TCFocusPackageComponent pendingPackage() {
        return pendingPackage;
    }

    public Map<String, Integer> crystalCosts() {
        return crystalCosts;
    }

    public boolean startCraft(ServerPlayer player) {
        if (player == null || level == null || level.isClientSide || remainingVis > 0.0F) {
            return false;
        }
        ItemStack focus = getItem(SLOT_FOCUS);
        if (focus.isEmpty() || !(focus.getItem() instanceof ItemFocus focusItem)) {
            failSound();
            return false;
        }
        List<TCFocusPackageHelper.NodeInstance> nodes = TCFocusPackageHelper.decode(designNodes);
        if (nodes.isEmpty()) {
            failSound();
            return false;
        }
        if (!hasResearch(player, nodes)) {
            failSound();
            return false;
        }

        TCFocusPackageComponent packageData = TCFocusPackageHelper.buildPackage(nodes);
        if (packageData.complexity() <= 0 || packageData.complexity() > focusItem.maxComplexity()) {
            failSound();
            return false;
        }
        Map<String, Integer> requiredCrystals = TCFocusPackageHelper.crystalCosts(packageData);
        int requiredXp = Math.max(1, Math.round((float)Math.sqrt(packageData.complexity())));
        if (!player.getAbilities().instabuild) {
            if (player.experienceLevel < requiredXp || !hasCrystals(player, requiredCrystals)) {
                failSound();
                return false;
            }
            consumeCrystals(player, requiredCrystals);
            player.giveExperienceLevels(-requiredXp);
        }

        pendingPackage = packageData;
        crystalCosts = Map.copyOf(requiredCrystals);
        xpCost = requiredXp;
        remainingVis = packageData.complexity() * 10.0F + focusItem.maxComplexity() / 5.0F;
        level.playSound(null, worldPosition, TCSounds.CRAFTSTART.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
        setChanged();
        return true;
    }

    private static boolean nodesHaveKnownDefinitions(List<TCFocusPackageHelper.NodeInstance> nodes) {
        boolean hasRoot = false;
        for (TCFocusPackageHelper.NodeInstance node : nodes) {
            TCFocusElementDefinition definition = TCFocusElements.get(node.key()).orElse(null);
            if (definition == null) {
                return false;
            }
            hasRoot |= definition.kind() == TCFocusElementDefinition.Kind.ROOT;
        }
        return hasRoot;
    }

    private boolean hasResearch(ServerPlayer player, List<TCFocusPackageHelper.NodeInstance> nodes) {
        for (TCFocusPackageHelper.NodeInstance node : nodes) {
            TCFocusElementDefinition definition = TCFocusElements.get(node.key()).orElse(null);
            if (definition == null) {
                return false;
            }
            if (!definition.research().isBlank()
                    && !TCResearchManager.knowsResearchStrict(TCPlayerKnowledgeStore.get(player), definition.research())) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasCrystals(ServerPlayer player, Map<String, Integer> required) {
        for (Map.Entry<String, Integer> entry : required.entrySet()) {
            if (countCrystals(player, entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    private static int countCrystals(ServerPlayer player, String aspect) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (TCArcaneWorkbenchCrafting.isCrystal(stack, aspect)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static void consumeCrystals(ServerPlayer player, Map<String, Integer> required) {
        LinkedHashMap<String, Integer> remaining = new LinkedHashMap<>(required);
        for (int slot = 0; slot < player.getInventory().items.size(); slot++) {
            ItemStack stack = player.getInventory().items.get(slot);
            if (stack.isEmpty()) {
                continue;
            }
            for (Map.Entry<String, Integer> entry : remaining.entrySet()) {
                if (entry.getValue() <= 0 || !TCArcaneWorkbenchCrafting.isCrystal(stack, entry.getKey())) {
                    continue;
                }
                int take = Math.min(stack.getCount(), entry.getValue());
                stack.shrink(take);
                entry.setValue(entry.getValue() - take);
                if (stack.isEmpty()) {
                    player.getInventory().items.set(slot, ItemStack.EMPTY);
                }
                break;
            }
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCFocalManipulatorBlockEntity manipulator) {
        manipulator.tickServer();
    }

    private void tickServer() {
        ticks++;
        if (level == null || level.isClientSide || ticks % 20 != 0 || remainingVis <= 0.0F) {
            return;
        }
        ItemStack focus = getItem(SLOT_FOCUS);
        if (focus.isEmpty() || !(focus.getItem() instanceof ItemFocus)) {
            remainingVis = 0.0F;
            pendingPackage = TCFocusPackageComponent.EMPTY;
            failSound();
            setChanged();
            return;
        }
        float drained = spendAura(Math.min(20.0F, remainingVis));
        if (drained > 0.0F) {
            remainingVis -= drained;
            if (remainingVis <= 0.0F) {
                completeCraft(focus);
            }
            setChanged();
        }
    }

    public float spendAura(float vis) {
        if (level == null || vis <= 0.0F) {
            return 0.0F;
        }
        if (level.getBlockState(worldPosition.above()).is(TCBlocks.ARCANE_WORKBENCH_CHARGER.get())) {
            float remaining = vis;
            float chunkDrain = vis / 9.0F;
            for (int xx = -1; xx <= 1; xx++) {
                for (int zz = -1; zz <= 1; zz++) {
                    if (chunkDrain > remaining) {
                        chunkDrain = remaining;
                    }
                    remaining -= AuraHandler.drainVis(level, worldPosition.offset(xx * 16, 0, zz * 16), chunkDrain, false);
                    if (remaining <= 0.0F) {
                        return vis;
                    }
                }
            }
            return vis - remaining;
        }
        return AuraHandler.drainVis(level, worldPosition, vis, false);
    }

    private void completeCraft(ItemStack focus) {
        if (focus.getItem() instanceof ItemFocus focusItem && !pendingPackage.isEmpty()) {
            focusItem.setPackage(focus, pendingPackage);
            if (!focusName.isBlank()) {
                focus.set(DataComponents.CUSTOM_NAME, Component.literal(focusName));
            }
            level.playSound(null, worldPosition, TCSounds.WAND.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
        }
        remainingVis = 0.0F;
        xpCost = 0;
        pendingPackage = TCFocusPackageComponent.EMPTY;
        crystalCosts = Map.of();
        designNodes = "";
        focusName = "";
    }

    private void failSound() {
        if (level != null) {
            level.playSound(null, worldPosition, TCSounds.WANDFAIL.get(), SoundSource.BLOCKS, 0.33F, 1.0F);
        }
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return items.get(SLOT_FOCUS).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == SLOT_FOCUS ? items.get(SLOT_FOCUS) : ItemStack.EMPTY;
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
        return slot == SLOT_FOCUS ? ContainerHelper.takeItem(items, SLOT_FOCUS) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot != SLOT_FOCUS) {
            return;
        }
        ItemStack stored = stack.copy();
        stored.limitSize(1);
        items.set(SLOT_FOCUS, stored);
        setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == SLOT_FOCUS && stack.getItem() instanceof ItemFocus;
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
        return Component.translatable("container.thaumcraft.focal_manipulator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new TCFocalManipulatorMenu(containerId, playerInventory, this);
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(worldPosition);
    }

    public void dropContents(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            ItemStack stack = items.get(SLOT_FOCUS);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
                items.set(SLOT_FOCUS, ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putString("DesignNodes", designNodes);
        tag.putString("FocusName", focusName);
        tag.putString("PendingNodes", pendingPackage.nodes());
        tag.putInt("PendingComplexity", pendingPackage.complexity());
        tag.putInt("PendingColor", pendingPackage.color());
        tag.putInt("PendingSortingHash", pendingPackage.sortingHash());
        tag.putFloat("RemainingVis", remainingVis);
        tag.putInt("XpCost", xpCost);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items.set(SLOT_FOCUS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        designNodes = tag.getString("DesignNodes");
        focusName = tag.getString("FocusName");
        pendingPackage = new TCFocusPackageComponent(
                tag.getString("PendingNodes"),
                tag.getInt("PendingComplexity"),
                tag.getInt("PendingColor"),
                tag.getInt("PendingSortingHash")
        );
        remainingVis = tag.getFloat("RemainingVis");
        xpCost = tag.getInt("XpCost");
        crystalCosts = pendingPackage.isEmpty() ? Map.of() : TCFocusPackageHelper.crystalCosts(pendingPackage);
    }
}

package thaumcraft.common.tiles.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.crafting.infusion.TCInfusionAssembly;
import thaumcraft.common.crafting.infusion.TCInfusionCompletionPlan;
import thaumcraft.common.crafting.infusion.TCInfusionCraftingPlan;
import thaumcraft.common.crafting.infusion.TCInfusionRecipe;
import thaumcraft.common.crafting.infusion.TCInfusionStartResult;
import thaumcraft.common.crafting.infusion.TCInfusionValidationResult;
import thaumcraft.common.registry.TCBlockEntities;

public class TCInfusionMatrixBlockEntity extends BlockEntity {
    public static final int LEGACY_HORIZONTAL_SCAN_RANGE = 8;
    public static final int LEGACY_SCAN_MIN_Y_OFFSET = -7;
    public static final int LEGACY_SCAN_MAX_Y_OFFSET = 3;
    public static final int LEGACY_CENTRAL_PEDESTAL_Y_OFFSET = -2;

    private String lastValidationReason = "";
    private String lastRecipeId = "";
    private int lastPedestalCount;
    private int lastComponentCount;
    private TCInfusionCraftingPlan activePlan;

    public TCInfusionMatrixBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.INFUSION_MATRIX.get(), pos, state);
    }

    public boolean hasCentralPedestal() {
        return centralPedestal().isPresent();
    }

    public Optional<TCInfusionPedestalBlockEntity> centralPedestal() {
        if (level == null) {
            return Optional.empty();
        }
        BlockEntity blockEntity = level.getBlockEntity(worldPosition.offset(0, LEGACY_CENTRAL_PEDESTAL_Y_OFFSET, 0));
        return blockEntity instanceof TCInfusionPedestalBlockEntity pedestal ? Optional.of(pedestal) : Optional.empty();
    }

    public List<TCInfusionPedestalBlockEntity> findSurroundingPedestals() {
        ArrayList<TCInfusionPedestalBlockEntity> pedestals = new ArrayList<>();
        if (level == null) {
            return List.of();
        }
        for (int dx = -LEGACY_HORIZONTAL_SCAN_RANGE; dx <= LEGACY_HORIZONTAL_SCAN_RANGE; dx++) {
            for (int dz = -LEGACY_HORIZONTAL_SCAN_RANGE; dz <= LEGACY_HORIZONTAL_SCAN_RANGE; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                for (int dy = LEGACY_SCAN_MIN_Y_OFFSET; dy <= LEGACY_SCAN_MAX_Y_OFFSET; dy++) {
                    BlockEntity blockEntity = level.getBlockEntity(worldPosition.offset(dx, dy, dz));
                    if (blockEntity instanceof TCInfusionPedestalBlockEntity pedestal) {
                        pedestals.add(pedestal);
                    }
                }
            }
        }
        return List.copyOf(pedestals);
    }

    public Snapshot createSnapshot(AspectList aspects) {
        Optional<TCInfusionPedestalBlockEntity> center = centralPedestal();
        ItemStack catalyst = center.map(TCInfusionPedestalBlockEntity::getStoredStack).orElse(ItemStack.EMPTY);
        ArrayList<ItemStack> components = new ArrayList<>();
        List<TCInfusionPedestalBlockEntity> pedestals = findSurroundingPedestals();
        for (TCInfusionPedestalBlockEntity pedestal : pedestals) {
            ItemStack stack = pedestal.getStoredStack();
            if (!stack.isEmpty()) {
                components.add(stack.copyWithCount(1));
            }
        }
        TCInfusionAssembly assembly = TCInfusionAssembly.of(catalyst, components, aspects);
        return new Snapshot(center.isPresent(), catalyst.copy(), List.copyOf(components), pedestals.size(), assembly);
    }

    public TCInfusionValidationResult validateFor(ServerPlayer player, AspectList aspects) {
        Snapshot snapshot = createSnapshot(aspects);
        if (!snapshot.hasCentralPedestal()) {
            return remember(TCInfusionValidationResult.failed("missing_central_pedestal"), snapshot);
        }
        if (snapshot.catalyst().isEmpty()) {
            return remember(TCInfusionValidationResult.failed("missing_catalyst"), snapshot);
        }
        if (snapshot.components().isEmpty()) {
            return remember(TCInfusionValidationResult.failed("missing_components"), snapshot);
        }
        if (level == null) {
            return remember(TCInfusionValidationResult.failed("missing_level"), snapshot);
        }
        return remember(snapshot.assembly().validateBest(level.getRecipeManager(), player), snapshot);
    }

    public TCInfusionValidationResult validateAgainst(RecipeHolder<TCInfusionRecipe> recipe, AspectList aspects) {
        Snapshot snapshot = createSnapshot(aspects);
        if (!snapshot.hasCentralPedestal()) {
            return remember(TCInfusionValidationResult.failed("missing_central_pedestal"), snapshot);
        }
        if (snapshot.catalyst().isEmpty()) {
            return remember(TCInfusionValidationResult.failed("missing_catalyst"), snapshot);
        }
        if (snapshot.components().isEmpty()) {
            return remember(TCInfusionValidationResult.failed("missing_components"), snapshot);
        }
        return remember(snapshot.assembly().validateAgainst(recipe), snapshot);
    }

    public TCInfusionStartResult tryStartCrafting(ServerPlayer player, AspectList aspects) {
        Snapshot snapshot = createSnapshot(aspects);
        if (activePlan != null) {
            TCInfusionValidationResult validation = remember(TCInfusionValidationResult.failed("already_crafting"), snapshot);
            return TCInfusionStartResult.failed("already_crafting", validation);
        }
        if (player == null) {
            TCInfusionValidationResult validation = remember(TCInfusionValidationResult.failed("missing_player"), snapshot);
            return TCInfusionStartResult.failed("missing_player", validation);
        }
        if (!snapshot.hasCentralPedestal()) {
            TCInfusionValidationResult validation = remember(TCInfusionValidationResult.failed("missing_central_pedestal"), snapshot);
            return TCInfusionStartResult.failed(validation.reason(), validation);
        }
        if (snapshot.catalyst().isEmpty()) {
            TCInfusionValidationResult validation = remember(TCInfusionValidationResult.failed("missing_catalyst"), snapshot);
            return TCInfusionStartResult.failed(validation.reason(), validation);
        }
        if (snapshot.components().isEmpty()) {
            TCInfusionValidationResult validation = remember(TCInfusionValidationResult.failed("missing_components"), snapshot);
            return TCInfusionStartResult.failed(validation.reason(), validation);
        }
        if (level == null) {
            TCInfusionValidationResult validation = remember(TCInfusionValidationResult.failed("missing_level"), snapshot);
            return TCInfusionStartResult.failed(validation.reason(), validation);
        }
        Optional<RecipeHolder<TCInfusionRecipe>> match = snapshot.assembly().findMatchingRecipe(level.getRecipeManager(), player);
        if (match.isEmpty()) {
            TCInfusionValidationResult validation = remember(TCInfusionValidationResult.failed("no_matching_researched_recipe"), snapshot);
            return TCInfusionStartResult.failed(validation.reason(), validation);
        }
        TCInfusionValidationResult validation = remember(snapshot.assembly().validateAgainst(match.get()), snapshot);
        if (!validation.valid()) {
            return TCInfusionStartResult.failed(validation.reason(), validation);
        }
        return storeValidatedPlan(match.get(), snapshot, validation, player.getName().getString());
    }

    public TCInfusionStartResult startCraftingForValidation(
            RecipeHolder<TCInfusionRecipe> recipe,
            AspectList aspects,
            String playerName
    ) {
        Snapshot snapshot = createSnapshot(aspects);
        if (activePlan != null) {
            TCInfusionValidationResult validation = remember(TCInfusionValidationResult.failed("already_crafting"), snapshot);
            return TCInfusionStartResult.failed("already_crafting", validation);
        }
        TCInfusionValidationResult validation = validateAgainst(recipe, aspects);
        if (!validation.valid()) {
            return TCInfusionStartResult.failed(validation.reason(), validation);
        }
        return storeValidatedPlan(recipe, snapshot, validation, playerName);
    }

    public boolean isCrafting() {
        return activePlan != null;
    }

    public Optional<TCInfusionCraftingPlan> activePlan() {
        return Optional.ofNullable(activePlan);
    }

    public TCInfusionCompletionPlan createCompletionPlan(AspectList availableAspects) {
        if (activePlan == null) {
            return TCInfusionCompletionPlan.missingActivePlan(availableAspects);
        }
        if (level == null) {
            return TCInfusionCompletionPlan.failed("missing_level", activePlan, availableAspects);
        }

        Optional<TCInfusionPedestalBlockEntity> center = centralPedestal();
        if (center.isEmpty()) {
            return TCInfusionCompletionPlan.failed("missing_central_pedestal", activePlan, availableAspects);
        }
        if (!activePlan.catalystMatches(center.get().getStoredStack())) {
            return TCInfusionCompletionPlan.failed("catalyst_changed", activePlan, availableAspects);
        }

        ArrayList<TCInfusionCompletionPlan.ComponentConsumption> componentConsumptions = new ArrayList<>();
        List<BlockPos> positions = activePlan.componentPedestalPositions();
        for (int index = 0; index < positions.size(); index++) {
            BlockPos pedestalPos = positions.get(index);
            BlockEntity blockEntity = level.getBlockEntity(pedestalPos);
            if (!(blockEntity instanceof TCInfusionPedestalBlockEntity pedestal)) {
                return TCInfusionCompletionPlan.failed("missing_component_pedestal", activePlan, availableAspects);
            }

            ItemStack currentStack = pedestal.getStoredStack();
            if (!activePlan.componentMatches(index, currentStack)) {
                return TCInfusionCompletionPlan.failed("component_changed", activePlan, availableAspects);
            }
            componentConsumptions.add(new TCInfusionCompletionPlan.ComponentConsumption(
                    pedestalPos,
                    activePlan.component(index),
                    currentStack
            ));
        }

        return TCInfusionCompletionPlan.fromValidatedInputs(activePlan, availableAspects, componentConsumptions);
    }

    public void abortCrafting() {
        if (activePlan == null) {
            return;
        }
        activePlan = null;
        markChangedAndSync();
    }

    public String lastValidationReason() {
        return lastValidationReason;
    }

    public String lastRecipeId() {
        return lastRecipeId;
    }

    public int lastPedestalCount() {
        return lastPedestalCount;
    }

    public int lastComponentCount() {
        return lastComponentCount;
    }

    private TCInfusionStartResult storeValidatedPlan(
            RecipeHolder<TCInfusionRecipe> recipe,
            Snapshot snapshot,
            TCInfusionValidationResult validation,
            String playerName
    ) {
        TCInfusionCraftingPlan.BuildResult buildResult = TCInfusionCraftingPlan.build(
                recipe.id(),
                recipe.value(),
                snapshot.catalyst(),
                filledSurroundingPedestalComponents(),
                playerName
        );
        if (!buildResult.valid()) {
            TCInfusionValidationResult failed = remember(
                    TCInfusionValidationResult.failed(buildResult.reason()).withRecipeId(recipe.id().toString()),
                    snapshot
            );
            return TCInfusionStartResult.failed(buildResult.reason(), failed);
        }

        activePlan = buildResult.plan();
        markChangedAndSync();
        return TCInfusionStartResult.started(activePlan, validation);
    }

    private List<TCInfusionCraftingPlan.PedestalComponent> filledSurroundingPedestalComponents() {
        ArrayList<TCInfusionCraftingPlan.PedestalComponent> components = new ArrayList<>();
        for (TCInfusionPedestalBlockEntity pedestal : findSurroundingPedestals()) {
            ItemStack stack = pedestal.getStoredStack();
            if (!stack.isEmpty()) {
                components.add(new TCInfusionCraftingPlan.PedestalComponent(pedestal.getBlockPos(), stack));
            }
        }
        return List.copyOf(components);
    }

    private TCInfusionValidationResult remember(TCInfusionValidationResult result, Snapshot snapshot) {
        lastValidationReason = result.reason();
        lastRecipeId = result.recipeId();
        lastPedestalCount = snapshot.surroundingPedestalCount();
        lastComponentCount = snapshot.componentCount();
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
        return result;
    }

    private void markChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("LastValidationReason", lastValidationReason);
        tag.putString("LastRecipeId", lastRecipeId);
        tag.putInt("LastPedestalCount", lastPedestalCount);
        tag.putInt("LastComponentCount", lastComponentCount);
        if (activePlan != null) {
            tag.put("ActiveInfusionPlan", activePlan.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        lastValidationReason = tag.getString("LastValidationReason");
        lastRecipeId = tag.getString("LastRecipeId");
        lastPedestalCount = tag.getInt("LastPedestalCount");
        lastComponentCount = tag.getInt("LastComponentCount");
        activePlan = tag.contains("ActiveInfusionPlan", Tag.TAG_COMPOUND)
                ? TCInfusionCraftingPlan.load(tag.getCompound("ActiveInfusionPlan"), registries)
                : null;
    }

    public record Snapshot(
            boolean hasCentralPedestal,
            ItemStack catalyst,
            List<ItemStack> components,
            int surroundingPedestalCount,
            TCInfusionAssembly assembly
    ) {
        public Snapshot {
            catalyst = catalyst == null ? ItemStack.EMPTY : catalyst.copy();
            components = components == null ? List.of() : components.stream()
                    .map(stack -> stack == null ? ItemStack.EMPTY : stack.copy())
                    .toList();
        }

        public int componentCount() {
            return components.size();
        }
    }
}

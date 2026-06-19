package thaumcraft.common.tiles.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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
import thaumcraft.common.crafting.infusion.TCInfusionRecipe;
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
        return remember(snapshot.assembly().validateAgainst(recipe), snapshot);
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
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        lastValidationReason = tag.getString("LastValidationReason");
        lastRecipeId = tag.getString("LastRecipeId");
        lastPedestalCount = tag.getInt("LastPedestalCount");
        lastComponentCount = tag.getInt("LastComponentCount");
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

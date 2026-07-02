package thaumcraft.common.tiles.devices;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.devices.TCMirrorBlock;
import thaumcraft.common.items.components.TCMirrorLinkComponent;
import thaumcraft.common.registry.TCBlockEntities;

/** Legacy item mirror port: paired block links, remote output queue and aura instability pressure. */
public final class TCMirrorBlockEntity extends BlockEntity {
    private static final String MIRROR_COOLDOWN_TAG = "ThaumcraftMirrorCooldown";
    private static final int MIRROR_COOLDOWN_TICKS = 20;
    private static final int INSTABILITY_FLUX_THRESHOLD = 128;

    private boolean linked;
    private BlockPos linkPos = BlockPos.ZERO;
    private ResourceKey<Level> linkDimension = Level.OVERWORLD;
    private int instability;
    private int count;
    private int relinkInterval = 40;
    private final ArrayList<ItemStack> outputStacks = new ArrayList<>();

    public TCMirrorBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.MIRROR.get(), pos, blockState);
    }

    public boolean isLinked() {
        return linked;
    }

    public BlockPos linkPos() {
        return linkPos;
    }

    public ResourceKey<Level> linkDimension() {
        return linkDimension;
    }

    public int instability() {
        return instability;
    }

    public int queuedStacks() {
        return outputStacks.size();
    }

    public int queuedItemCount() {
        int total = 0;
        for (ItemStack stack : outputStacks) {
            if (!stack.isEmpty()) {
                total += stack.getCount();
            }
        }
        return total;
    }

    public void setInstabilityForValidation(int value) {
        instability = Math.max(0, value);
        markChangedAndSync();
    }

    public void clearQueuedStacksForValidation() {
        outputStacks.clear();
        markChangedAndSync();
    }

    public void setLinkedTargetForValidation(Level targetLevel, BlockPos targetPos) {
        if (targetLevel == null || targetPos == null) {
            clearLink();
            return;
        }
        linkDimension = targetLevel.dimension();
        linkPos = targetPos.immutable();
        linked = false;
        restoreLink();
    }

    public TCMirrorLinkComponent linkComponent() {
        return new TCMirrorLinkComponent(linkDimension.location().toString(), linkPos.getX(), linkPos.getY(), linkPos.getZ());
    }

    public void applyLinkComponent(TCMirrorLinkComponent component) {
        if (component == null) {
            return;
        }
        linkDimension = component.dimensionKey();
        linkPos = component.pos();
        linked = false;
        restoreLink();
    }

    public boolean restoreLink() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        ServerLevel targetLevel = serverLevel.getServer().getLevel(linkDimension);
        if (targetLevel == null || !(targetLevel.getBlockEntity(linkPos) instanceof TCMirrorBlockEntity target)) {
            linked = false;
            markChangedAndSync();
            return false;
        }
        if (target == this || target.isLinkValidSimple()) {
            linked = false;
            markChangedAndSync();
            return false;
        }

        linked = true;
        target.linked = true;
        target.linkDimension = level.dimension();
        target.linkPos = worldPosition.immutable();
        target.relinkInterval = 40;
        relinkInterval = 40;
        target.markChangedAndSync();
        markChangedAndSync();
        return true;
    }

    public boolean isLinkValid() {
        if (!linked || !(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        ServerLevel targetLevel = serverLevel.getServer().getLevel(linkDimension);
        if (targetLevel == null || !(targetLevel.getBlockEntity(linkPos) instanceof TCMirrorBlockEntity target)) {
            linked = false;
            markChangedAndSync();
            return false;
        }
        boolean valid = target.linked
                && target.linkPos.equals(worldPosition)
                && target.linkDimension.equals(level.dimension());
        if (!valid) {
            linked = false;
            markChangedAndSync();
        }
        return valid;
    }

    public boolean isLinkValidSimple() {
        if (!linked || !(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        ServerLevel targetLevel = serverLevel.getServer().getLevel(linkDimension);
        if (targetLevel == null || !(targetLevel.getBlockEntity(linkPos) instanceof TCMirrorBlockEntity target)) {
            return false;
        }
        return target.linked
                && target.linkPos.equals(worldPosition)
                && target.linkDimension.equals(level.dimension());
    }

    public void invalidateLink() {
        if (level instanceof ServerLevel serverLevel) {
            ServerLevel targetLevel = serverLevel.getServer().getLevel(linkDimension);
            if (targetLevel != null && targetLevel.getBlockEntity(linkPos) instanceof TCMirrorBlockEntity target) {
                target.linked = false;
                target.markChangedAndSync();
            }
        }
        linked = false;
        markChangedAndSync();
    }

    public boolean transportEntity(ItemEntity itemEntity) {
        if (itemEntity == null || itemEntity.isRemoved()) {
            return false;
        }
        CompoundTag data = itemEntity.getPersistentData();
        int cooldown = data.getInt(MIRROR_COOLDOWN_TAG);
        if (cooldown > 0) {
            data.putInt(MIRROR_COOLDOWN_TAG, cooldown - 1);
            return false;
        }
        ItemStack items = itemEntity.getItem();
        if (items.isEmpty() || !isLinkValid()) {
            return false;
        }
        TCMirrorBlockEntity target = targetMirror();
        if (target == null) {
            return false;
        }
        ItemStack transported = items.copy();
        target.addStack(transported);
        addInstability(transported.getCount());
        itemEntity.discard();
        if (level != null) {
            level.blockEvent(worldPosition, getBlockState().getBlock(), 1, 0);
        }
        return true;
    }

    public boolean transportDirect(ItemStack items) {
        if (items == null || items.isEmpty() || items.getCount() <= 0) {
            return false;
        }
        addStack(items.copy());
        return true;
    }

    public void addStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        outputStacks.add(stack.copy());
        markChangedAndSync();
    }

    public void eject() {
        if (level == null || level.isClientSide || outputStacks.isEmpty() || count <= 20) {
            return;
        }
        int index = level.random.nextInt(outputStacks.size());
        ItemStack stack = outputStacks.get(index);
        if (stack == null || stack.isEmpty()) {
            outputStacks.remove(index);
            markChangedAndSync();
            return;
        }

        ItemStack outItem = stack.copyWithCount(1);
        if (spawnItem(outItem)) {
            stack.shrink(1);
            addInstability(1);
            level.blockEvent(worldPosition, getBlockState().getBlock(), 1, 0);
            if (stack.isEmpty()) {
                outputStacks.remove(index);
            }
            markChangedAndSync();
        }
    }

    public boolean spawnItem(ItemStack stack) {
        if (level == null || stack.isEmpty()) {
            return false;
        }
        Direction face = facingOf(getBlockState());
        ItemEntity item = new ItemEntity(
                level,
                worldPosition.getX() + 0.5D,
                worldPosition.getY() + 0.25D,
                worldPosition.getZ() + 0.5D,
                stack.copy()
        );
        item.setDeltaMovement(face.getStepX() * 0.15D, face.getStepY() * 0.15D, face.getStepZ() * 0.15D);
        item.getPersistentData().putInt(MIRROR_COOLDOWN_TAG, MIRROR_COOLDOWN_TICKS);
        return level.addFreshEntity(item);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCMirrorBlockEntity mirror) {
        if (level == null || level.isClientSide) {
            return;
        }
        mirror.eject();
        mirror.checkInstability();
        if (mirror.count++ % mirror.relinkInterval == 0) {
            if (!mirror.isLinkValidSimple()) {
                if (mirror.relinkInterval < 600) {
                    mirror.relinkInterval += 20;
                }
                mirror.restoreLink();
            } else {
                mirror.relinkInterval = 40;
            }
        }
    }

    public void checkInstability() {
        if (instability > INSTABILITY_FLUX_THRESHOLD) {
            if (level != null) {
                AuraHelper.polluteAura(level, worldPosition, 1.0F, true);
            }
            instability -= INSTABILITY_FLUX_THRESHOLD;
            markChangedAndSync();
        }
        if (instability > 0 && count % 100 == 0) {
            instability--;
            markChangedAndSync();
        }
    }

    private TCMirrorBlockEntity targetMirror() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }
        ServerLevel targetLevel = serverLevel.getServer().getLevel(linkDimension);
        if (targetLevel == null || !(targetLevel.getBlockEntity(linkPos) instanceof TCMirrorBlockEntity target)) {
            return null;
        }
        return target;
    }

    private void addInstability(int amount) {
        instability += Math.max(0, amount);
        markChangedAndSync();
    }

    private void clearLink() {
        linked = false;
        linkPos = BlockPos.ZERO;
        linkDimension = Level.OVERWORLD;
        markChangedAndSync();
    }

    private static Direction facingOf(BlockState state) {
        return state.hasProperty(TCMirrorBlock.FACING) ? state.getValue(TCMirrorBlock.FACING) : Direction.DOWN;
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
        tag.putBoolean("Linked", linked);
        tag.putInt("linkX", linkPos.getX());
        tag.putInt("linkY", linkPos.getY());
        tag.putInt("linkZ", linkPos.getZ());
        tag.putString("linkDim", linkDimension.location().toString());
        tag.putInt("instability", instability);
        tag.putInt("count", count);
        tag.putInt("inc", relinkInterval);
        NonNullList<ItemStack> queue = NonNullList.withSize(outputStacks.size(), ItemStack.EMPTY);
        for (int index = 0; index < outputStacks.size(); index++) {
            queue.set(index, outputStacks.get(index));
        }
        ContainerHelper.saveAllItems(tag, queue, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        linked = tag.getBoolean("Linked");
        linkPos = new BlockPos(tag.getInt("linkX"), tag.getInt("linkY"), tag.getInt("linkZ"));
        ResourceLocation dimensionId = ResourceLocation.tryParse(tag.getString("linkDim"));
        linkDimension = dimensionId == null
                ? Level.OVERWORLD
                : ResourceKey.create(Registries.DIMENSION, dimensionId);
        instability = Math.max(0, tag.getInt("instability"));
        count = Math.max(0, tag.getInt("count"));
        relinkInterval = tag.contains("inc") ? Math.max(40, tag.getInt("inc")) : 40;
        outputStacks.clear();
        int queueSize = tag.contains("Items", Tag.TAG_LIST)
                ? Math.max(1, tag.getList("Items", Tag.TAG_COMPOUND).size())
                : 1;
        NonNullList<ItemStack> queue = NonNullList.withSize(queueSize, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, queue, registries);
        for (ItemStack stack : queue) {
            if (!stack.isEmpty()) {
                outputStacks.add(stack);
            }
        }
    }

    public List<ItemStack> queuedStacksForValidation() {
        return List.copyOf(outputStacks);
    }
}

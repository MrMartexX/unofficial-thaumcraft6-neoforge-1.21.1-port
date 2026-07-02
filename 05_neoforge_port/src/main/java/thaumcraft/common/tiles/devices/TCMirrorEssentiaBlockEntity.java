package thaumcraft.common.tiles.devices;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.devices.TCMirrorBlock;
import thaumcraft.common.essentia.container.TCAspectSourceContainer;
import thaumcraft.common.essentia.container.TCEssentiaSourceSearch;
import thaumcraft.common.items.components.TCMirrorLinkComponent;
import thaumcraft.common.registry.TCBlockEntities;

/** Legacy-shaped essentia mirror: paired link plus remote IAspectSource bridge. */
public final class TCMirrorEssentiaBlockEntity extends BlockEntity implements TCAspectSourceContainer {
    private static final int LEGACY_SOURCE_RANGE = 8;

    private boolean linked;
    private BlockPos linkPos = BlockPos.ZERO;
    private ResourceKey<Level> linkDimension = Level.OVERWORLD;
    private Direction linkedFacing = Direction.DOWN;
    private int instability;
    private int count;
    private int relinkInterval = 40;

    public TCMirrorEssentiaBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.MIRROR_ESSENTIA.get(), pos, blockState);
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

    public Direction linkedFacing() {
        return linkedFacing;
    }

    public int instability() {
        return instability;
    }

    public void setInstabilityForValidation(int value) {
        instability = Math.max(0, value);
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
        if (targetLevel == null || !(targetLevel.getBlockEntity(linkPos) instanceof TCMirrorEssentiaBlockEntity target)) {
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
        linkedFacing = facingOf(target.getBlockState());
        target.linked = true;
        target.linkDimension = level.dimension();
        target.linkPos = worldPosition.immutable();
        target.linkedFacing = facingOf(getBlockState());
        target.relinkInterval = 40;
        relinkInterval = 40;
        target.markChangedAndSync();
        markChangedAndSync();
        return true;
    }

    public boolean isLinkValid() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        ServerLevel targetLevel = serverLevel.getServer().getLevel(linkDimension);
        if (targetLevel == null || !(targetLevel.getBlockEntity(linkPos) instanceof TCMirrorEssentiaBlockEntity target)) {
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
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        ServerLevel targetLevel = serverLevel.getServer().getLevel(linkDimension);
        if (targetLevel == null || !(targetLevel.getBlockEntity(linkPos) instanceof TCMirrorEssentiaBlockEntity target)) {
            return false;
        }
        return linked
                && target.linked
                && target.linkPos.equals(worldPosition)
                && target.linkDimension.equals(level.dimension());
    }

    public void invalidateLink() {
        if (level instanceof ServerLevel serverLevel) {
            ServerLevel targetLevel = serverLevel.getServer().getLevel(linkDimension);
            if (targetLevel != null && targetLevel.getBlockEntity(linkPos) instanceof TCMirrorEssentiaBlockEntity target) {
                target.linked = false;
                target.markChangedAndSync();
            }
        }
        linked = false;
        markChangedAndSync();
    }

    @Override
    public boolean isSourceBlocked() {
        return false;
    }

    @Override
    public AspectList storedAspects() {
        AspectList available = new AspectList();
        if (!isLinkValid()) {
            return available;
        }
        for (TCAspectSourceContainer container : remoteContainersInLegacyOrder()) {
            if (container.isSourceBlocked()) {
                continue;
            }
            AspectList stored = container.storedAspects();
            if (stored != null) {
                available.add(stored);
            }
        }
        return available;
    }

    @Override
    public int drainAspect(Aspect aspect, int amount, boolean simulate) {
        if (amount <= 0) {
            return 0;
        }
        int drained = 0;
        for (int index = 0; index < amount; index++) {
            if (!drainOne(aspect, simulate)) {
                break;
            }
            drained++;
        }
        if (!simulate && drained > 0) {
            addInstability(drained);
        }
        return drained;
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return aspect != null && isLinkValid() && remoteContainerAccepts(aspect);
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        if (aspect == null || amount > 1 || amount <= 0 || !isLinkValid()) {
            return amount;
        }
        boolean accepted = addOne(aspect);
        if (accepted) {
            addInstability(amount);
        }
        return accepted ? 0 : amount;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (aspect == null || amount > 1 || amount <= 0 || !isLinkValid()) {
            return false;
        }
        boolean drained = drainOne(aspect, false);
        if (drained) {
            addInstability(amount);
        }
        return drained;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCMirrorEssentiaBlockEntity mirror) {
        if (level == null || level.isClientSide) {
            return;
        }
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
        if (instability > 64) {
            if (level != null) {
                AuraHelper.polluteAura(level, worldPosition, 1.0F, true);
            }
            instability -= 64;
            markChangedAndSync();
        }
        if (instability > 0 && count % 100 == 0) {
            instability--;
            markChangedAndSync();
        }
    }

    private boolean addOne(Aspect aspect) {
        TCAspectSourceContainer firstEmpty = null;
        for (TCAspectSourceContainer container : remoteContainersInLegacyOrder()) {
            if (container.isSourceBlocked() || !container.doesContainerAccept(aspect)) {
                continue;
            }
            AspectList stored = container.storedAspects();
            if (stored != null && stored.getAmount(aspect) > 0 && container.addToContainer(aspect, 1) == 0) {
                return true;
            }
            if (firstEmpty == null && (stored == null || stored.visSize() == 0)) {
                firstEmpty = container;
            }
        }
        return firstEmpty != null && firstEmpty.addToContainer(aspect, 1) == 0;
    }

    private boolean drainOne(Aspect aspect, boolean simulate) {
        if (aspect == null || !isLinkValid()) {
            return false;
        }
        for (TCAspectSourceContainer container : remoteContainersInLegacyOrder()) {
            if (container.isSourceBlocked() || container.drainAspect(aspect, 1, true) != 1) {
                continue;
            }
            return simulate || container.drainAspect(aspect, 1, false) == 1;
        }
        return false;
    }

    private boolean remoteContainerAccepts(Aspect aspect) {
        for (TCAspectSourceContainer container : remoteContainersInLegacyOrder()) {
            if (!container.isSourceBlocked() && container.doesContainerAccept(aspect)) {
                return true;
            }
        }
        return false;
    }

    private List<TCAspectSourceContainer> remoteContainersInLegacyOrder() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return List.of();
        }
        ServerLevel targetLevel = serverLevel.getServer().getLevel(linkDimension);
        if (targetLevel == null || !(targetLevel.getBlockEntity(linkPos) instanceof TCMirrorEssentiaBlockEntity target)) {
            return List.of();
        }
        Direction direction = linkedFacing;
        if (direction == Direction.DOWN) {
            direction = facingOf(target.getBlockState());
            linkedFacing = direction;
        }
        ArrayList<TCAspectSourceContainer> containers = new ArrayList<>();
        for (BlockPos candidate : TCEssentiaSourceSearch.discover(targetLevel, linkPos, direction, LEGACY_SOURCE_RANGE)) {
            BlockEntity blockEntity = targetLevel.getBlockEntity(candidate);
            if (blockEntity instanceof TCAspectSourceContainer container
                    && !(blockEntity instanceof TCMirrorEssentiaBlockEntity)) {
                containers.add(container);
            }
        }
        return containers;
    }

    private void addInstability(int amount) {
        instability += Math.max(0, amount);
        markChangedAndSync();
    }

    private void clearLink() {
        linked = false;
        linkPos = BlockPos.ZERO;
        linkDimension = Level.OVERWORLD;
        linkedFacing = Direction.DOWN;
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
        tag.putByte("linkedFacing", (byte) linkedFacing.get3DDataValue());
        tag.putInt("instability", instability);
        tag.putInt("count", count);
        tag.putInt("inc", relinkInterval);
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
        linkedFacing = tag.contains("linkedFacing") ? Direction.from3DDataValue(tag.getByte("linkedFacing")) : Direction.DOWN;
        instability = Math.max(0, tag.getInt("instability"));
        count = Math.max(0, tag.getInt("count"));
        relinkInterval = tag.contains("inc") ? Math.max(40, tag.getInt("inc")) : 40;
    }
}

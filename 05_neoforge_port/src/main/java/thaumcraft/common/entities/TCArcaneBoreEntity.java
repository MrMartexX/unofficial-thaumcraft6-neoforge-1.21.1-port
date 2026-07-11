package thaumcraft.common.entities;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.menu.TCArcaneBoreMenu;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSounds;

/** Entity-backed Arcane Bore foundation: one pickaxe slot, redstone activation and legacy vis mining loop. */
public class TCArcaneBoreEntity extends PathfinderMob implements Container, MenuProvider {
    public static final int SLOT_TOOL = 0;
    public static final int SLOT_COUNT = 1;
    public static final float DIG_COST = 0.25F;

    private static final EntityDataAccessor<Direction> FACING =
            SynchedEntityData.defineId(TCArcaneBoreEntity.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Boolean> ACTIVE =
            SynchedEntityData.defineId(TCArcaneBoreEntity.class, EntityDataSerializers.BOOLEAN);

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    @Nullable
    private UUID owner;
    @Nullable
    private BlockPos digTarget;
    private int digDelay;
    private int breakCounter;
    private float charge;

    public TCArcaneBoreEntity(EntityType<? extends TCArcaneBoreEntity> type, Level level) {
        super(type, level);
        setNoAi(true);
        xpReward = 0;
    }

    public TCArcaneBoreEntity(Level level, BlockPos pos, Direction facing, @Nullable UUID owner) {
        this(TCEntityTypes.ARCANE_BORE.get(), level);
        setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
        setFacing(facing);
        this.owner = owner;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FACING, Direction.DOWN);
        builder.define(ACTIVE, false);
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            return;
        }
        if (tickCount % 50 == 0 && getHealth() < getMaxHealth()) {
            heal(1.0F);
        }
        if (tickCount % 10 == 0 && charge < 10.0F) {
            rechargeVis();
        }
        setActive(level().hasNeighborSignal(blockPosition().below()));
        if (!isActive()) {
            digTarget = null;
            return;
        }
        if (!validInventory()) {
            digTarget = null;
            return;
        }
        if (digTarget == null || !canMine(digTarget)) {
            digTarget = findNextBlockToDig();
            if (digTarget != null) {
                digDelay = digDelayFor(digTarget);
                level().broadcastEntityEvent(this, (byte) 16);
            } else {
                level().broadcastEntityEvent(this, (byte) 17);
                return;
            }
        }
        if (charge < DIG_COST) {
            return;
        }
        if (digDelay-- <= 0 && mineTarget()) {
            charge -= DIG_COST;
            if (tickCount % 20 == 0) {
                level().playSound(null, blockPosition(), TCSounds.RUMBLE.get(), SoundSource.BLOCKS, 0.25F, 0.9F);
            }
        }
    }

    public Direction facing() {
        return entityData.get(FACING);
    }

    public void setFacing(Direction facing) {
        entityData.set(FACING, facing == null ? Direction.DOWN : facing);
        setYRot(rotationYawFor(entityData.get(FACING)));
    }

    public boolean isActive() {
        return entityData.get(ACTIVE);
    }

    public void setActive(boolean active) {
        entityData.set(ACTIVE, active);
    }

    public boolean validInventory() {
        ItemStack tool = getItem(SLOT_TOOL);
        return !tool.isEmpty() && tool.is(ItemTags.PICKAXES) && (!tool.isDamageableItem() || tool.getDamageValue() < tool.getMaxDamage() - 1);
    }

    public float charge() {
        return charge;
    }

    public int breakCounter() {
        return breakCounter;
    }

    public int digDelay() {
        return digDelay;
    }

    @Nullable
    public BlockPos digTarget() {
        return digTarget;
    }

    public void setChargeForValidation(float charge) {
        this.charge = Math.max(0.0F, charge);
    }

    public void setDigTargetForValidation(@Nullable BlockPos pos) {
        digTarget = pos;
        digDelay = pos == null ? 0 : digDelayFor(pos);
    }

    public int getDigRadius() {
        ItemStack tool = getItem(SLOT_TOOL);
        if (tool.isEmpty()) {
            return 2;
        }
        if (tool.is(TCItems.THAUMIUM_PICK.get()) || tool.is(TCItems.VOID_PICK.get())) {
            return 3;
        }
        return 2;
    }

    public int getDigDepth() {
        return getDigRadius() * 8;
    }

    public boolean mineTargetForValidation() {
        return mineTarget();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (!level().isClientSide && attacker instanceof Player player && mayControl(player)) {
            Direction direction = player.getDirection();
            setFacing(direction == Direction.DOWN ? Direction.NORTH : direction);
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!mayControl(player)) {
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown()) {
            dropStoredTool();
            spawnAtLocation(new ItemStack(TCItems.ARCANE_BORE.get()));
            discard();
            player.swing(hand, true);
            level().playSound(null, blockPosition(), SoundEvents.ARMOR_STAND_BREAK, SoundSource.BLOCKS, 0.75F, 0.8F);
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(this, buffer -> buffer.writeVarInt(getId()));
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        HolderLookup.Provider registries = level().registryAccess();
        ContainerHelper.saveAllItems(tag, items, registries);
        if (owner != null) {
            tag.putUUID("Owner", owner);
        }
        tag.putByte("Facing", (byte) facing().get3DDataValue());
        tag.putBoolean("Active", isActive());
        tag.putFloat("charge", charge);
        tag.putInt("BreakCounter", breakCounter);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        items.set(SLOT_TOOL, ItemStack.EMPTY);
        HolderLookup.Provider registries = level().registryAccess();
        ContainerHelper.loadAllItems(tag, items, registries);
        owner = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        setFacing(Direction.from3DDataValue(tag.getByte("Facing")));
        setActive(tag.getBoolean("Active"));
        charge = Math.max(0.0F, tag.getFloat("charge"));
        breakCounter = Math.max(0, tag.getInt("BreakCounter"));
    }

    @Override
    public void die(DamageSource source) {
        dropStoredTool();
        super.die(source);
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return getItem(SLOT_TOOL).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == SLOT_TOOL ? items.get(SLOT_TOOL) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = slot == SLOT_TOOL ? ContainerHelper.removeItem(items, slot, amount) : ItemStack.EMPTY;
        if (!removed.isEmpty()) {
            setItemSlot(EquipmentSlot.MAINHAND, getItem(SLOT_TOOL));
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return slot == SLOT_TOOL ? ContainerHelper.takeItem(items, slot) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot != SLOT_TOOL) {
            return;
        }
        ItemStack stored = stack == null ? ItemStack.EMPTY : stack.copy();
        stored.limitSize(1);
        items.set(SLOT_TOOL, stored);
        setItemSlot(EquipmentSlot.MAINHAND, stored);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == SLOT_TOOL && stack != null && stack.is(ItemTags.PICKAXES);
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return isAlive() && player.distanceToSqr(this) <= 64.0D;
    }

    @Override
    public void clearContent() {
        items.set(SLOT_TOOL, ItemStack.EMPTY);
        setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.thaumcraft.arcane_bore");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new TCArcaneBoreMenu(containerId, inventory, this);
    }

    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(getId());
    }

    private void rechargeVis() {
        charge += AuraHelper.drainVis(level(), blockPosition(), 10.0F, false);
        charge = Math.min(charge, 10.0F);
    }

    @Nullable
    private BlockPos findNextBlockToDig() {
        Direction facing = facing();
        int radius = getDigRadius();
        int depth = getDigDepth();
        BlockPos origin = blockPosition().relative(facing);
        for (int d = 1; d <= depth; d++) {
            BlockPos center = origin.relative(facing, d);
            for (int r = 0; r <= radius; r++) {
                for (BlockPos candidate : planeRing(center, facing, r)) {
                    if (canMine(candidate)) {
                        return candidate.immutable();
                    }
                }
            }
        }
        return null;
    }

    private List<BlockPos> planeRing(BlockPos center, Direction facing, int radius) {
        ArrayListBuilder builder = new ArrayListBuilder();
        if (radius == 0) {
            builder.add(center);
            return builder.build();
        }
        Direction axisA = facing.getAxis() == Direction.Axis.X ? Direction.NORTH : Direction.EAST;
        Direction axisB = facing.getAxis() == Direction.Axis.Y ? Direction.NORTH : Direction.UP;
        if (facing.getAxis() == Direction.Axis.Z) {
            axisB = Direction.UP;
        }
        for (int a = -radius; a <= radius; a++) {
            for (int b = -radius; b <= radius; b++) {
                if (Math.max(Math.abs(a), Math.abs(b)) == radius) {
                    builder.add(center.relative(axisA, a).relative(axisB, b));
                }
            }
        }
        return builder.build();
    }

    private boolean canMine(BlockPos pos) {
        BlockState state = level().getBlockState(pos);
        return !state.isAir()
                && state.getDestroySpeed(level(), pos) >= 0.0F
                && !state.getCollisionShape(level(), pos).isEmpty();
    }

    private int digDelayFor(BlockPos pos) {
        BlockState state = level().getBlockState(pos);
        float speed = Math.max(0.0F, getItem(SLOT_TOOL).getDestroySpeed(state) / 2.0F);
        float hardness = Math.max(0.0F, state.getDestroySpeed(level(), pos));
        return Math.max(1, (int) Math.max(10.0F - speed, hardness * 2.0F - speed * 2.0F));
    }

    private boolean mineTarget() {
        if (digTarget == null || !(level() instanceof ServerLevel serverLevel) || !canMine(digTarget)) {
            digTarget = null;
            return false;
        }
        ItemStack tool = getItem(SLOT_TOOL);
        if (tool.isEmpty()) {
            digTarget = null;
            return false;
        }
        BlockState state = level().getBlockState(digTarget);
        List<ItemStack> drops = Block.getDrops(state, serverLevel, digTarget, level().getBlockEntity(digTarget), this, tool);
        level().levelEvent(null, 2001, digTarget, Block.getId(state));
        level().removeBlock(digTarget, false);
        Direction output = facing().getOpposite();
        for (ItemStack drop : drops) {
            ejectOrInsert(drop, output);
        }
        breakCounter++;
        if (breakCounter >= 50) {
            breakCounter = 0;
            damageTool();
        }
        digTarget = null;
        return true;
    }

    private void ejectOrInsert(ItemStack stack, Direction output) {
        if (stack.isEmpty()) {
            return;
        }
        BlockPos targetPos = blockPosition().relative(output);
        IItemHandler handler = level().getCapability(Capabilities.ItemHandler.BLOCK, targetPos, output.getOpposite());
        ItemStack remaining = stack.copy();
        if (handler != null) {
            for (int slot = 0; slot < handler.getSlots() && !remaining.isEmpty(); slot++) {
                remaining = handler.insertItem(slot, remaining, false);
            }
        }
        if (!remaining.isEmpty()) {
            double x = getX() + output.getStepX() * 0.75D;
            double y = getY() + 0.5D;
            double z = getZ() + output.getStepZ() * 0.75D;
            net.minecraft.world.Containers.dropItemStack(level(), x, y, z, remaining);
        }
    }

    private void damageTool() {
        ItemStack tool = getItem(SLOT_TOOL);
        if (tool.isEmpty() || !tool.isDamageableItem()) {
            return;
        }
        tool.setDamageValue(tool.getDamageValue() + 1);
        if (tool.getDamageValue() >= tool.getMaxDamage()) {
            setItem(SLOT_TOOL, ItemStack.EMPTY);
        }
    }

    private void dropStoredTool() {
        ItemStack tool = getItem(SLOT_TOOL);
        if (!tool.isEmpty()) {
            spawnAtLocation(tool.copy());
            setItem(SLOT_TOOL, ItemStack.EMPTY);
        }
    }

    private boolean mayControl(Player player) {
        return owner == null || player.getUUID().equals(owner) || player.isCreative();
    }

    private static float rotationYawFor(Direction direction) {
        return switch (direction) {
            case SOUTH -> 0.0F;
            case WEST -> 90.0F;
            case NORTH -> 180.0F;
            case EAST -> 270.0F;
            default -> 0.0F;
        };
    }

    private static final class ArrayListBuilder {
        private final java.util.ArrayList<BlockPos> positions = new java.util.ArrayList<>();

        private void add(BlockPos pos) {
            positions.add(pos);
        }

        private List<BlockPos> build() {
            return List.copyOf(positions);
        }
    }
}

package thaumcraft.common.tiles.devices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.phys.AABB;
import thaumcraft.common.menu.TCPotionSprayerMenu;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.blocks.devices.TCPotionSprayerBlock;
import thaumcraft.common.essentia.transport.TCEssentiaCapabilities;
import thaumcraft.common.essentia.transport.TCEssentiaStack;
import thaumcraft.common.essentia.transport.TCEssentiaSuction;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.registry.TCBlockEntities;

/**
 * TC6 Potion Sprayer tile entity.
 * Absorbs essentia via attached tubes to build up to 8 charges from an inserted potion.
 * Discharges aerosolized potion effect in facing direction upon activation.
 */
public class TCPotionSprayerBlockEntity extends BlockEntity implements TCEssentiaTransport, MenuProvider {
    public static final int MAX_CHARGES = 8;

    private ItemStack potionItem = ItemStack.EMPTY;
    private int charges = 0;
    private int counter = 0;
    private boolean activated = false;
    private final Map<String, Integer> requiredAspects = new HashMap<>();
    private final Map<String, Integer> progressAspects = new HashMap<>();

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.has(DataComponents.POTION_CONTENTS);
        }

        @Override
        protected void onContentsChanged(int slot) {
            potionItem = getStackInSlot(0);
            recalculateAspects();
            setChanged();
        }
    };

    public TCPotionSprayerBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.POTION_SPRAYER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCPotionSprayerBlockEntity sprayer) {
        if (!level.isClientSide) {
            sprayer.tickServer(state);
        }
    }

    private void tickServer(BlockState state) {
        if (level == null || level.isClientSide) return;
        counter++;

        Direction facing = state.hasProperty(TCPotionSprayerBlock.FACING) ? state.getValue(TCPotionSprayerBlock.FACING) : Direction.UP;
        boolean enabled = !state.hasProperty(TCPotionSprayerBlock.ENABLED) || state.getValue(TCPotionSprayerBlock.ENABLED);

        // Fill charges from connected essentia transport
        if (counter % 5 == 0 && !potionItem.isEmpty() && charges < MAX_CHARGES) {
            fillFromEssentia(facing);
        }

        // Sprayer triggers when disabled / receiving redstone pulse
        if (!enabled) {
            if (!activated && charges > 0) {
                charges--;
                sprayEffects(facing);
                setChanged();
                level.sendBlockUpdated(worldPosition, state, state, 3);
            }
            activated = true;
        } else if (activated) {
            activated = false;
        }
    }

    private void fillFromEssentia(Direction facing) {
        if (level == null) return;
        String nextNeeded = getNextNeededAspect();
        if (nextNeeded == null) {
            // All required aspects met for a charge
            progressAspects.clear();
            charges++;
            setChanged();
            return;
        }

        for (Direction dir : Direction.values()) {
            if (dir == facing) continue;
            BlockPos pipePos = worldPosition.relative(dir);
            TCEssentiaTransport transport = level.getCapability(TCEssentiaCapabilities.BLOCK, pipePos, dir.getOpposite());
            if (transport != null && transport.canOutputTo(dir.getOpposite())) {
                TCEssentiaStack st = transport.getEssentia(dir.getOpposite());
                if (st != null && st.aspect().equals(nextNeeded) && st.amount() > 0) {
                    int taken = transport.takeEssentia(nextNeeded, 1, dir.getOpposite(), false);
                    if (taken > 0) {
                        progressAspects.merge(nextNeeded, taken, Integer::sum);
                        setChanged();
                        break;
                    }
                }
            }
        }
    }

    private String getNextNeededAspect() {
        for (Map.Entry<String, Integer> entry : requiredAspects.entrySet()) {
            int current = progressAspects.getOrDefault(entry.getKey(), 0);
            if (current < entry.getValue()) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void recalculateAspects() {
        requiredAspects.clear();
        progressAspects.clear();
        charges = 0;
        if (!potionItem.isEmpty()) {
            PotionContents contents = potionItem.get(DataComponents.POTION_CONTENTS);
            if (contents != null) {
                requiredAspects.put("praecantatio", 2);
                requiredAspects.put("aqua", 2);
                requiredAspects.put("victus", 2);
            }
        }
    }

    private void sprayEffects(Direction facing) {
        if (level == null) return;
        PotionContents contents = potionItem.get(DataComponents.POTION_CONTENTS);
        if (contents == null) return;

        Iterable<MobEffectInstance> effects = contents.getAllEffects();
        BlockPos center = worldPosition.relative(facing, 2);
        AABB box = new AABB(center).inflate(1.5);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, box);

        for (LivingEntity target : targets) {
            for (MobEffectInstance effect : effects) {
                target.addEffect(new MobEffectInstance(effect));
            }
        }

        level.playSound(null, worldPosition, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.4F, 2.0F);

        // Particle dispersal
        int potionColor = contents.getColor();
        for (int i = 0; i < 20; i++) {
            double dx = facing.getStepX() * 0.5 + (level.random.nextGaussian() * 0.2);
            double dy = facing.getStepY() * 0.5 + (level.random.nextGaussian() * 0.2);
            double dz = facing.getStepZ() * 0.5 + (level.random.nextGaussian() * 0.2);
            level.addParticle(net.minecraft.core.particles.ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, potionColor),
                    worldPosition.getX() + 0.5 + facing.getStepX() * 0.6,
                    worldPosition.getY() + 0.5 + facing.getStepY() * 0.6,
                    worldPosition.getZ() + 0.5 + facing.getStepZ() * 0.6,
                    dx, dy, dz);
        }
    }

    public ItemStack getPotionItem() {
        return potionItem;
    }

    public void setPotionItem(ItemStack stack) {
        this.potionItem = stack.copy();
        itemHandler.setStackInSlot(0, potionItem);
        recalculateAspects();
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public int getCharges() {
        return charges;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public Map<String, Integer> getRequiredAspects() {
        return java.util.Collections.unmodifiableMap(requiredAspects);
    }

    public Map<String, Integer> getProgressAspects() {
        return java.util.Collections.unmodifiableMap(progressAspects);
    }

    public int getColor() {
        if (potionItem.isEmpty()) {
            return 0x385DC6;
        }
        return potionItem.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor();
    }

    public boolean stillValid(net.minecraft.world.entity.player.Player player) {
        return net.minecraft.world.Container.stillValidBlockEntity(this, player);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("tile.potion_sprayer.name");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, net.minecraft.world.entity.player.Player player) {
        return new TCPotionSprayerMenu(containerId, inventory, this);
    }

    public void setChargesForTesting(int charges) {
        this.charges = Math.max(0, Math.min(MAX_CHARGES, charges));
        setChanged();
    }

    public IItemHandler itemHandler(@Nullable Direction side) {
        return itemHandler;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!potionItem.isEmpty()) {
            tag.put("potion", potionItem.save(registries));
        }
        tag.putInt("charges", charges);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("potion")) {
            potionItem = ItemStack.parseOptional(registries, tag.getCompound("potion"));
            itemHandler.setStackInSlot(0, potionItem);
        } else {
            potionItem = ItemStack.EMPTY;
            itemHandler.setStackInSlot(0, ItemStack.EMPTY);
        }
        charges = tag.getInt("charges");
        recalculateAspects();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        if (!potionItem.isEmpty()) {
            tag.put("potion", potionItem.save(registries));
        }
        tag.putInt("charges", charges);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // TCEssentiaTransport
    @Override
    public boolean isConnectable(Direction face) {
        Direction facing = getBlockState().hasProperty(TCPotionSprayerBlock.FACING)
                ? getBlockState().getValue(TCPotionSprayerBlock.FACING)
                : Direction.UP;
        return face != facing;
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
        if (!isConnectable(face) || charges >= MAX_CHARGES) {
            return TCEssentiaSuction.NONE;
        }
        String needed = getNextNeededAspect();
        return needed != null ? new TCEssentiaSuction(needed, 128) : TCEssentiaSuction.NONE;
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
        if (!isConnectable(face) || amount <= 0 || charges >= MAX_CHARGES) return 0;
        String needed = getNextNeededAspect();
        if (needed != null && needed.equals(aspect)) {
            if (!simulate) {
                progressAspects.merge(aspect, 1, Integer::sum);
                setChanged();
            }
            return 1;
        }
        return 0;
    }

    @Override
    public int takeEssentia(String aspect, int amount, Direction face, boolean simulate) {
        return 0;
    }
}

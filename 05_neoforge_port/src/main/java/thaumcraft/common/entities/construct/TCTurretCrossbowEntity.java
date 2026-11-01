package thaumcraft.common.entities.construct;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.menu.TCTurretBasicMenu;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;

public class TCTurretCrossbowEntity extends TCOwnedConstructEntity implements RangedAttackMob, ContainerListener {
    private final SimpleContainer ammoInventory = new SimpleContainer(1);
    public float loadProgress = 0.0F;
    public float prevLoadProgress = 0.0F;
    private int loadTicks = 0;

    public TCTurretCrossbowEntity(EntityType<? extends TCTurretCrossbowEntity> type, Level level) {
        super(type, level);
        this.ammoInventory.addListener(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 0.0D, 20, 60, 24.0F));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, LivingEntity.class, 24.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 5, true, false,
                target -> target instanceof Enemy && !(target instanceof TCOwnedConstructEntity construct && isAlliedTo(construct))));
    }

    public SimpleContainer getAmmoInventory() {
        return ammoInventory;
    }

    public boolean isAdvanced() {
        return false;
    }

    @Override
    public void containerChanged(Container container) {
        // Track ammo changes
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack ammo = ammoInventory.getItem(0);
        if (ammo.isEmpty() || !(ammo.getItem() instanceof ArrowItem arrowItem)) {
            return;
        }

        AbstractArrow arrow = arrowItem.createArrow(level(), ammo, this, null);
        arrow.setBaseDamage(2.25D + distanceFactor * 2.0D + (random.nextDouble() - 0.5D) * 0.5D);
        arrow.pickup = AbstractArrow.Pickup.DISALLOWED;

        double dx = target.getX() - getX();
        double dy = target.getY(0.3333333333333333D) - arrow.getY();
        double dz = target.getZ() - getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);

        arrow.shoot(dx, dy + dist * 0.1D, dz, 1.8F, 2.0F);
        level().addFreshEntity(arrow);
        playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));

        ammo.shrink(1);
        if (ammo.isEmpty()) {
            ammoInventory.setItem(0, ItemStack.EMPTY);
        }
        loadTicks = 10;
    }

    @Override
    public void tick() {
        super.tick();
        prevLoadProgress = loadProgress;
        if (loadTicks > 0) {
            loadTicks--;
            loadProgress = (10 - loadTicks) / 10.0F;
        } else {
            loadProgress = 0.0F;
        }

        if (!level().isClientSide) {
            // Keep head yaw synced
            setYRot(getYHeadRot());

            if (tickCount % 80 == 0) {
                heal(1.0F);
            }

            // Activator rail AI check
            BlockPos pos = blockPosition();
            BlockState state = level().getBlockState(pos);
            if (!state.is(Blocks.ACTIVATOR_RAIL)) {
                state = level().getBlockState(pos.below());
            }
            if (state.is(Blocks.ACTIVATOR_RAIL) && state.hasProperty(PoweredRailBlock.POWERED) && state.getValue(PoweredRailBlock.POWERED)) {
                if (!isNoAi()) {
                    setNoAi(true);
                }
            } else if (isNoAi()) {
                setNoAi(false);
            }

            // Dispenser auto-reload
            if (ammoInventory.getItem(0).isEmpty()) {
                BlockEntity be = level().getBlockEntity(blockPosition().below());
                if (be instanceof DispenserBlockEntity dispenser) {
                    for (int i = 0; i < dispenser.getContainerSize(); i++) {
                        ItemStack inSlot = dispenser.getItem(i);
                        if (!inSlot.isEmpty() && inSlot.getItem() instanceof ArrowItem) {
                            ammoInventory.setItem(0, dispenser.removeItem(i, inSlot.getCount()));
                            playSound(SoundEvents.DISPENSER_DISPENSE, 0.75F, 1.2F);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && isOwner(player) && isAlive()) {
            if (player.isShiftKeyDown()) {
                // Dismantle turret
                dismantle(player, hand);
                return InteractionResult.SUCCESS;
            } else if (player instanceof ServerPlayer serverPlayer) {
                openTurretMenu(serverPlayer);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    public void dismantle(Player player, InteractionHand hand) {
        // Drop ammo
        if (!ammoInventory.getItem(0).isEmpty()) {
            spawnAtLocation(ammoInventory.getItem(0));
            ammoInventory.setItem(0, ItemStack.EMPTY);
        }
        // Drop turret item
        ItemStack placer = new ItemStack(isAdvanced() ? TCItems.ADVANCEDCROSSBOW.get() : TCItems.AUTOMATEDCROSSBOW.get());
        spawnAtLocation(placer);
        playSound(SoundEvents.ITEM_BREAK, 1.0F, 1.0F);
        player.swing(hand, true);
        discard();
    }

    protected void openTurretMenu(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, p) -> new TCTurretBasicMenu(containerId, inventory, this),
                Component.translatable("entity.thaumcraft.turret_basic")
        ), buf -> buf.writeInt(getId()));
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        if (!ammoInventory.getItem(0).isEmpty()) {
            spawnAtLocation(ammoInventory.getItem(0));
            ammoInventory.setItem(0, ItemStack.EMPTY);
        }
        float b = 0.15F;
        if (random.nextFloat() < 0.2F + b) {
            spawnAtLocation(new ItemStack(isAdvanced() ? TCItems.MIND_CLOCKWORK_ADVANCED.get() : TCItems.CATALOG_PLACEHOLDER_MINDCLOCKWORK.get()));
        }
        if (random.nextFloat() < 0.5F + b) {
            spawnAtLocation(new ItemStack(TCItems.MECHANISM_SIMPLE.get()));
        }
        if (random.nextFloat() < 0.5F + b) {
            spawnAtLocation(new ItemStack(TCBlocks.PLANK_GREATWOOD.get()));
        }
        if (isAdvanced()) {
            if (random.nextFloat() < 0.3F + b) {
                spawnAtLocation(new ItemStack(TCItems.IRON_PLATE.get()));
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (!ammoInventory.getItem(0).isEmpty()) {
            tag.put("Ammo", ammoInventory.getItem(0).save(level().registryAccess()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Ammo")) {
            ammoInventory.setItem(0, ItemStack.parseOptional(level().registryAccess(), tag.getCompound("Ammo")));
        }
    }
}

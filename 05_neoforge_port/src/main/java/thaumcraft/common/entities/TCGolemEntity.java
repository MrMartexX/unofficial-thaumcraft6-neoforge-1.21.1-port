package thaumcraft.common.entities;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.common.golems.GolemProperties;
import thaumcraft.common.golems.ItemGolemBell;
import thaumcraft.common.golems.ItemGolemPlacer;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;

public class TCGolemEntity extends PathfinderMob implements IGolemAPI, RangedAttackMob, OwnableEntity {

    private static final EntityDataAccessor<Integer> PROPS1 =
            SynchedEntityData.defineId(TCGolemEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PROPS2 =
            SynchedEntityData.defineId(TCGolemEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> GOLEM_COLOR =
            SynchedEntityData.defineId(TCGolemEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> CLIMBING =
            SynchedEntityData.defineId(TCGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(TCGolemEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private final NonNullList<ItemStack> carrying = NonNullList.create();
    private int rankXp = 0;
    private boolean owned = false;

    public TCGolemEntity(EntityType<? extends TCGolemEntity> type, Level level) {
        super(type, level);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 0.0D)
                .add(Attributes.ARMOR, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PROPS1, 0);
        builder.define(PROPS2, 0);
        builder.define(GOLEM_COLOR, (byte) -1);
        builder.define(CLIMBING, false);
        builder.define(OWNER_UUID, Optional.empty());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.25D, true) {
            @Override
            public boolean canUse() {
                return getProperties().hasTrait(EnumGolemTrait.FIGHTER)
                        && !getProperties().hasTrait(EnumGolemTrait.RANGED)
                        && super.canUse();
            }
        });
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0D, 20, 40, 16.0F) {
            @Override
            public boolean canUse() {
                return getProperties().hasTrait(EnumGolemTrait.RANGED) && super.canUse();
            }
        });
        this.goalSelector.addGoal(2, new thaumcraft.common.golems.tasks.TCGolemTaskGoal(this, (byte) 1));
        this.goalSelector.addGoal(3, new thaumcraft.common.golems.tasks.TCGolemTaskGoal(this, (byte) 0));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false,
                entity -> entity instanceof Enemy && getProperties().hasTrait(EnumGolemTrait.FIGHTER)));
    }

    @Override
    public LivingEntity getGolemEntity() {
        return this;
    }

    @Override
    public Level getGolemWorld() {
        return level();
    }

    @Override
    public IGolemProperties getProperties() {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putInt(entityData.get(PROPS1));
        bb.putInt(entityData.get(PROPS2));
        return GolemProperties.fromLong(bb.getLong(0));
    }

    @Override
    public void setProperties(IGolemProperties prop) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(prop.toLong());
        bb.rewind();
        entityData.set(PROPS1, bb.getInt());
        entityData.set(PROPS2, bb.getInt());
        updateAttributesFromProperties();
    }

    public void updateAttributesFromProperties() {
        IGolemProperties props = getProperties();
        GolemMaterial mat = props.getMaterial();

        double maxHealth = 10.0D + mat.healthMod;
        if (props.hasTrait(EnumGolemTrait.FRAGILE)) {
            maxHealth *= 0.75D;
        }
        maxHealth += props.getRank();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        if (getHealth() > maxHealth) {
            setHealth((float) maxHealth);
        }

        double armor = mat.armor;
        if (props.hasTrait(EnumGolemTrait.ARMORED)) {
            armor += 4.0D;
        }
        getAttribute(Attributes.ARMOR).setBaseValue(armor);

        double speed = 0.30D;
        if (props.hasTrait(EnumGolemTrait.WHEELED)) {
            speed = 0.38D;
        } else if (props.hasTrait(EnumGolemTrait.LIGHT)) {
            speed = 0.34D;
        } else if (props.hasTrait(EnumGolemTrait.HEAVY)) {
            speed = 0.26D;
        }
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);

        double follow = props.hasTrait(EnumGolemTrait.SCOUT) ? 56.0D : 40.0D;
        getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(follow);

        if (props.hasTrait(EnumGolemTrait.FIGHTER)) {
            double damage = mat.damage;
            if (props.hasTrait(EnumGolemTrait.BRUTAL)) {
                damage = Math.max(damage * 1.5D, damage + 1.0D);
            }
            damage += props.getRank() * 0.25D;
            getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damage);
        } else {
            getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0.0D);
        }
    }

    public int getMaxCarryingSlots() {
        IGolemProperties props = getProperties();
        if (props.hasTrait(EnumGolemTrait.HAULER) && props.hasTrait(EnumGolemTrait.DEFT)) {
            return 8;
        } else if (props.hasTrait(EnumGolemTrait.HAULER)) {
            return 4;
        } else if (props.hasTrait(EnumGolemTrait.DEFT)) {
            return 2;
        }
        return 1;
    }

    @Override
    public ItemStack holdItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return ItemStack.EMPTY;
        int maxSlots = getMaxCarryingSlots();

        // Merge into existing stacks first
        for (ItemStack carried : carrying) {
            if (ItemStack.isSameItemSameComponents(carried, stack)) {
                int space = carried.getMaxStackSize() - carried.getCount();
                int move = Math.min(space, stack.getCount());
                if (move > 0) {
                    carried.grow(move);
                    stack.shrink(move);
                    if (stack.isEmpty()) return ItemStack.EMPTY;
                }
            }
        }

        // Add to new slots if space allows
        while (carrying.size() < maxSlots && !stack.isEmpty()) {
            int take = Math.min(stack.getMaxStackSize(), stack.getCount());
            ItemStack add = stack.split(take);
            carrying.add(add);
        }

        return stack;
    }

    @Override
    public ItemStack dropItem(ItemStack stack) {
        if (carrying.isEmpty()) return ItemStack.EMPTY;
        if (stack == null || stack.isEmpty()) {
            return carrying.remove(carrying.size() - 1);
        }
        for (int i = 0; i < carrying.size(); i++) {
            ItemStack carried = carrying.get(i);
            if (ItemStack.isSameItemSameComponents(carried, stack)) {
                int dropCount = Math.min(carried.getCount(), stack.getCount());
                ItemStack dropped = carried.split(dropCount);
                if (carried.isEmpty()) {
                    carrying.remove(i);
                }
                return dropped;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCarry(ItemStack stack, boolean partial) {
        return canCarryAmount(stack) >= (partial ? 1 : (stack != null ? stack.getCount() : 1));
    }

    @Override
    public int canCarryAmount(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        int totalSpace = 0;
        int maxSlots = getMaxCarryingSlots();

        for (ItemStack carried : carrying) {
            if (ItemStack.isSameItemSameComponents(carried, stack)) {
                totalSpace += (carried.getMaxStackSize() - carried.getCount());
            }
        }

        int emptySlots = maxSlots - carrying.size();
        if (emptySlots > 0) {
            totalSpace += emptySlots * stack.getMaxStackSize();
        }

        return totalSpace;
    }

    @Override
    public boolean isCarrying(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        for (ItemStack carried : carrying) {
            if (ItemStack.isSameItemSameComponents(carried, stack) && carried.getCount() >= stack.getCount()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NonNullList<ItemStack> getCarrying() {
        return carrying;
    }

    @Override
    public void addRankXp(int xp) {
        this.rankXp += xp;
        IGolemProperties props = getProperties();
        if (props.hasTrait(EnumGolemTrait.SMART) && props.getRank() < 10) {
            int targetXp = (props.getRank() + 1) * (props.getRank() + 1) * 1000;
            if (rankXp >= targetXp) {
                props.setRank(props.getRank() + 1);
                setProperties(props);
                level().playSound(null, blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 0.5F, 1.0F);
            }
        }
    }

    public int getRankXp() {
        return rankXp;
    }

    public void setRankXp(int xp) {
        this.rankXp = xp;
    }

    @Override
    public byte getGolemColor() {
        return entityData.get(GOLEM_COLOR);
    }

    public void setGolemColor(byte color) {
        entityData.set(GOLEM_COLOR, color);
    }

    @Override
    public void swingArm() {
        swing(InteractionHand.MAIN_HAND);
    }

    @Override
    public boolean isInCombat() {
        return getTarget() != null && getTarget().isAlive();
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        TCGolemDartEntity dart = new TCGolemDartEntity(TCEntityTypes.GOLEM_DART.get(), this, level());
        double dx = target.getX() - getX();
        double dy = target.getY(0.3333333333333333D) - dart.getY();
        double dz = target.getZ() - getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        dart.shoot(dx, dy + dist * 0.2D, dz, 1.6F, 12.0F);
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.2F / (getRandom().nextFloat() * 0.2F + 0.9F));
        level().addFreshEntity(dart);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (held.getItem() instanceof ItemGolemBell) {
            if (!player.getUUID().equals(getOwnerUUID())) {
                return InteractionResult.FAIL;
            }
            if (player.isShiftKeyDown()) {
                if (!level().isClientSide) {
                    ItemStack placer = new ItemStack(TCItems.GOLEM_PLACER.get());
                    ItemGolemPlacer.setProperties(placer, getProperties());
                    ItemGolemPlacer.setXp(placer, rankXp);

                    // Drop any carried items
                    for (ItemStack item : carrying) {
                        spawnAtLocation(item);
                    }
                    carrying.clear();

                    if (!player.getInventory().add(placer)) {
                        spawnAtLocation(placer);
                    }
                    level().playSound(null, blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.6F, 1.0F);
                    discard();
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            } else {
                if (level().isClientSide) {
                    IGolemProperties props = getProperties();
                    player.displayClientMessage(Component.literal("§6Golem: " + props.getMaterial().getLocalizedName().getString()
                            + " §7| §bRank: §f" + props.getRank()
                            + (props.hasTrait(EnumGolemTrait.SMART) ? " §a(" + rankXp + ")" : "")), true);
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
        }

        if (held.getItem() instanceof DyeItem dye) {
            if (!player.getUUID().equals(getOwnerUUID())) {
                return InteractionResult.FAIL;
            }
            if (!level().isClientSide) {
                setGolemColor((byte) dye.getDyeColor().getId());
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                level().playSound(null, blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6F, 1.2F);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return entityData.get(OWNER_UUID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        entityData.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        UUID uuid = getOwnerUUID();
        return uuid == null ? null : level().getPlayerByUUID(uuid);
    }

    public int getCarryCapacity() {
        int cap = 1;
        if (getProperties().hasTrait(EnumGolemTrait.HAULER)) {
            cap += 2;
        }
        if (getProperties().hasTrait(EnumGolemTrait.DEFT)) {
            cap += 1;
        }
        return Math.min(8, cap);
    }

    public boolean isOwned() {
        return owned;
    }

    public void setOwned(boolean owned) {
        this.owned = owned;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putLong("props", getProperties().toLong());
        tag.putInt("xp", rankXp);
        tag.putByte("color", getGolemColor());
        tag.putBoolean("owned", owned);
        if (getOwnerUUID() != null) {
            tag.putUUID("owner", getOwnerUUID());
        }

        ListTag list = new ListTag();
        for (ItemStack item : carrying) {
            if (!item.isEmpty()) {
                list.add(item.save(level().registryAccess()));
            }
        }
        tag.put("carrying", list);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("props")) {
            setProperties(GolemProperties.fromLong(tag.getLong("props")));
        }
        if (tag.contains("xp")) {
            this.rankXp = tag.getInt("xp");
        }
        if (tag.contains("color")) {
            setGolemColor(tag.getByte("color"));
        }
        this.owned = tag.getBoolean("owned");
        if (tag.hasUUID("owner")) {
            setOwnerUUID(tag.getUUID("owner"));
        }

        carrying.clear();
        if (tag.contains("carrying", Tag.TAG_LIST)) {
            ListTag list = tag.getList("carrying", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag itemTag = list.getCompound(i);
                ItemStack.parse(level().registryAccess(), itemTag).ifPresent(carrying::add);
            }
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        updateAttributesFromProperties();
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }
}

package thaumcraft.common.entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.aspects.TCAspectAssignments;
import thaumcraft.common.config.TCConfig;
import thaumcraft.common.menu.TCPechMenu;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSounds;

public class TCPechEntity extends Monster implements Container, MenuProvider {
    public static final double LEGACY_MAX_HEALTH = 30.0D;
    public static final double LEGACY_ATTACK_DAMAGE = 6.0D;
    public static final double LEGACY_MOVEMENT_SPEED = 0.5D;
    public static final double LEGACY_ARMOR_BONUS = 2.0D;
    public static final int LEGACY_LOOT_SLOTS = 9;
    public static final int LEGACY_XP_REWARD = 8;
    public static final int LEGACY_ENDER_PEARL_VALUE = 15;
    public static final int LEGACY_ANGER_MIN = 400;
    public static final int LEGACY_ANGER_RANDOM = 400;
    public static final double LEGACY_PICKUP_RANGE = 16.0D;
    public static final double LEGACY_PICKUP_DISTANCE = 1.5D;
    public static final int LEGACY_CHARGE_SOUND_INTERVAL = 100;
    public static final int LEGACY_REGEN_INTERVAL = 40;

    private static final EntityDataAccessor<Byte> TYPE =
            SynchedEntityData.defineId(TCPechEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ANGER =
            SynchedEntityData.defineId(TCPechEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> TAMED =
            SynchedEntityData.defineId(TCPechEntity.class, EntityDataSerializers.BOOLEAN);

    private final NonNullList<ItemStack> loot = NonNullList.withSize(LEGACY_LOOT_SLOTS, ItemStack.EMPTY);
    private int chargeCount;
    private boolean trading;
    public float mumble;

    public TCPechEntity(EntityType<? extends TCPechEntity> type, Level level) {
        super(type, level);
        xpReward = LEGACY_XP_REWARD;
        setCanPickUpLoot(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, LEGACY_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, LEGACY_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, LEGACY_MOVEMENT_SPEED)
                .add(Attributes.ARMOR, LEGACY_ARMOR_BONUS);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new TradePlayerGoal());
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.6D, false));
        goalSelector.addGoal(3, new PickupItemGoal());
        goalSelector.addGoal(4, new AvoidUntamedPlayerGoal());
        goalSelector.addGoal(5, new OpenDoorGoal(this, true));
        goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 0.5D));
        goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0D, false, 4, () -> false));
        goalSelector.addGoal(9, new RandomStrollGoal(this, 0.6D));
        goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        goalSelector.addGoal(10, new LookAtPlayerGoal(this, LivingEntity.class, 8.0F));
        goalSelector.addGoal(11, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new AngryNearestPlayerTargetGoal());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TYPE, (byte) 0);
        builder.define(ANGER, 0);
        builder.define(TAMED, false);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        populateLegacyEquipment(difficulty);
        setCanPickUpLoot(random.nextFloat() < 0.75F * difficulty.getSpecialMultiplier());
        return data;
    }

    private void populateLegacyEquipment(net.minecraft.world.DifficultyInstance difficulty) {
        int roll = random.nextInt(20);
        ItemStack held = switch (roll) {
            case 0, 12 -> new ItemStack(TCItems.PECH_WAND.get());
            case 1 -> new ItemStack(Items.STONE_SWORD);
            case 3 -> new ItemStack(Items.STONE_AXE);
            case 5 -> new ItemStack(Items.IRON_SWORD);
            case 6 -> new ItemStack(Items.IRON_AXE);
            case 7 -> new ItemStack(Items.FISHING_ROD);
            case 8 -> new ItemStack(Items.STONE_PICKAXE);
            case 9 -> new ItemStack(Items.IRON_PICKAXE);
            case 2, 4, 10, 11, 13 -> new ItemStack(Items.BOW);
            default -> ItemStack.EMPTY;
        };
        if (!held.isEmpty()) {
            setItemSlot(EquipmentSlot.MAINHAND, held);
            setDropChance(EquipmentSlot.MAINHAND, held.is(TCItems.PECH_WAND.get()) ? 0.1F : 0.2F);
            if (held.is(TCItems.PECH_WAND.get())) {
                setPechType(PechType.MAGE);
            } else if (held.is(Items.BOW)) {
                setPechType(PechType.STALKER);
            }
        }
    }

    @Override
    public Component getName() {
        if (hasCustomName()) {
            return getCustomName();
        }
        return Component.translatable(getPechType().translationKey());
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    public void tick() {
        if (mumble > 0.0F) {
            mumble *= 0.75F;
        }
        if (getAnger() > 0) {
            setAnger(getAnger() - 1);
        }
        if (getAnger() > 0 && getTarget() != null) {
            if (chargeCount > 0) {
                chargeCount--;
            }
            if (chargeCount == 0) {
                chargeCount = LEGACY_CHARGE_SOUND_INTERVAL;
                playSound(TCSounds.PECH_CHARGE.get(), getSoundVolume(), getVoicePitch());
            }
            level().broadcastEntityEvent(this, (byte) 17);
        }
        if (level().isClientSide && random.nextInt(15) == 0 && getAnger() > 0) {
            spawnClientParticle(ParticleTypes.ANGRY_VILLAGER);
        }
        if (level().isClientSide && random.nextInt(25) == 0 && isTamed()) {
            spawnClientParticle(ParticleTypes.HAPPY_VILLAGER);
        }
        super.tick();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (tickCount % LEGACY_REGEN_INTERVAL == 0) {
            heal(1.0F);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 16) {
            mumble = (float) Math.PI;
        } else if (id == 17) {
            mumble = (float) Math.PI * 2.0F;
        } else if (id == 18) {
            for (int i = 0; i < 5; i++) {
                spawnClientParticle(ParticleTypes.HAPPY_VILLAGER);
            }
        } else if (id == 19) {
            for (int i = 0; i < 5; i++) {
                spawnClientParticle(ParticleTypes.ANGRY_VILLAGER);
            }
            mumble = (float) Math.PI * 2.0F;
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void spawnClientParticle(net.minecraft.core.particles.ParticleOptions particle) {
        double x = getX() + random.nextFloat() * getBbWidth() * 2.0F - getBbWidth();
        double y = getY() + 0.5D + random.nextFloat() * getBbHeight();
        double z = getZ() + random.nextFloat() * getBbWidth() * 2.0F - getBbWidth();
        level().addParticle(
                particle,
                x,
                y,
                z,
                random.nextGaussian() * 0.02D,
                random.nextGaussian() * 0.02D,
                random.nextGaussian() * 0.02D
        );
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) {
            return false;
        }
        Entity attacker = source.getEntity();
        if (!level().isClientSide && attacker instanceof Player player && !player.getAbilities().instabuild) {
            becomeAngryAt(player);
            for (TCPechEntity pech : level().getEntitiesOfClass(TCPechEntity.class, getBoundingBox().inflate(32.0D, 16.0D, 32.0D))) {
                pech.becomeAngryAt(player);
            }
        }
        return super.hurt(source, amount);
    }

    private void becomeAngryAt(Entity entity) {
        if (!(entity instanceof LivingEntity living) || (entity instanceof Player player && player.getAbilities().instabuild)) {
            return;
        }
        if (getAnger() <= 0) {
            level().broadcastEntityEvent(this, (byte) 19);
            playSound(TCSounds.PECH_CHARGE.get(), getSoundVolume(), getVoicePitch());
        }
        setTarget(living);
        setAnger(LEGACY_ANGER_MIN + random.nextInt(LEGACY_ANGER_RANDOM));
        setTamed(false);
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
    }

    @Override
    public void playAmbientSound() {
        if (!level().isClientSide) {
            if (random.nextInt(3) == 0) {
                List<TCPechEntity> nearby = level().getEntitiesOfClass(
                        TCPechEntity.class,
                        getBoundingBox().inflate(4.0D, 2.0D, 4.0D),
                        pech -> pech != this
                );
                if (!nearby.isEmpty()) {
                    level().broadcastEntityEvent(this, (byte) 17);
                    playSound(TCSounds.PECH_TRADE.get(), getSoundVolume(), getVoicePitch());
                    return;
                }
            }
            level().broadcastEntityEvent(this, (byte) 16);
        }
        super.playAmbientSound();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Nullable
    @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSound() {
        return TCSounds.PECH_IDLE.get();
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(DamageSource damageSource) {
        return TCSounds.PECH_HIT.get();
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getDeathSound() {
        return TCSounds.PECH_DEATH.get();
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (player.isShiftKeyDown() || held.getItem() instanceof NameTagItem) {
            return InteractionResult.PASS;
        }
        if (!isTamed()) {
            return super.mobInteract(player, hand);
        }
        if (level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            setTrading(true);
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, inventory, ignored) -> new TCPechMenu(containerId, inventory, this),
                    getDisplayName()
            ), buffer -> buffer.writeVarInt(getId()));
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("PechType", (byte) getPechType().legacyId());
        tag.putShort("Anger", (short) getAnger());
        tag.putBoolean("Tamed", isTamed());
        HolderLookup.Provider registries = level().registryAccess();
        ContainerHelper.saveAllItems(tag, loot, registries);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setPechType(PechType.byLegacyId(tag.getByte("PechType")));
        setAnger(tag.getShort("Anger"));
        setTamed(tag.getBoolean("Tamed"));
        loot.clear();
        for (int i = 0; i < LEGACY_LOOT_SLOTS; i++) {
            loot.add(ItemStack.EMPTY);
        }
        HolderLookup.Provider registries = level().registryAccess();
        ContainerHelper.loadAllItems(tag, loot, registries);
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.server.level.ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        for (ItemStack stack : loot) {
            if (!stack.isEmpty() && random.nextFloat() < 0.33F) {
                spawnAtLocation(stack.copy(), 1.5F);
            }
        }
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return loot.stream().filter(stack -> !stack.isEmpty()).count() < 5;
    }

    public PechType getPechType() {
        return PechType.byLegacyId(entityData.get(TYPE));
    }

    public void setPechType(PechType type) {
        entityData.set(TYPE, (byte) (type == null ? PechType.FORAGER.legacyId() : type.legacyId()));
    }

    public int getAnger() {
        return entityData.get(ANGER);
    }

    public void setAnger(int anger) {
        entityData.set(ANGER, Math.max(0, anger));
    }

    public boolean isTamed() {
        return entityData.get(TAMED);
    }

    public void setTamed(boolean tamed) {
        entityData.set(TAMED, tamed);
    }

    public boolean isTrading() {
        return trading;
    }

    public void setTrading(boolean trading) {
        this.trading = trading;
    }

    public boolean canPickup(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (!isTamed() && explicitValue(stack) > 0) {
            return true;
        }
        for (ItemStack stored : loot) {
            if (stored.isEmpty()) {
                return true;
            }
            if (ItemStack.isSameItemSameComponents(stack.copyWithCount(1), stored.copyWithCount(1))
                    && stack.getCount() + stored.getCount() <= stored.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    public ItemStack pickupItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (isTamed() || !isValued(stack)) {
            return storeLoot(stack);
        }
        if (random.nextInt(10) < getValue(stack)) {
            setTamed(true);
            level().broadcastEntityEvent(this, (byte) 18);
        }
        stack.shrink(1);
        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    private ItemStack storeLoot(ItemStack stack) {
        ItemStack remaining = stack.copy();
        for (int i = 0; i < loot.size(); i++) {
            ItemStack stored = loot.get(i);
            if (stored.isEmpty()) {
                continue;
            }
            if (ItemStack.isSameItemSameComponents(remaining.copyWithCount(1), stored.copyWithCount(1))) {
                int move = Math.min(remaining.getCount(), stored.getMaxStackSize() - stored.getCount());
                if (move > 0) {
                    stored.grow(move);
                    remaining.shrink(move);
                }
            }
            if (remaining.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        for (int i = 0; i < loot.size(); i++) {
            if (loot.get(i).isEmpty()) {
                loot.set(i, remaining.copy());
                return ItemStack.EMPTY;
            }
        }
        return remaining;
    }

    public boolean isValued(ItemStack stack) {
        return getValue(stack) > 0;
    }

    public int getValue(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        int explicit = explicitValue(stack);
        if (explicit > 0) {
            return explicit;
        }
        AspectList aspects = TCAspectAssignments.getObjectAspects(stack);
        if (aspects == null || aspects.getAmount(Aspect.DESIRE) <= 1) {
            return 0;
        }
        return Math.min(32, aspects.getAmount(Aspect.DESIRE) / 2);
    }

    private int explicitValue(ItemStack stack) {
        return stack.is(Items.ENDER_PEARL) ? LEGACY_ENDER_PEARL_VALUE : 0;
    }

    public static boolean checkPechSpawnRules(
            EntityType<TCPechEntity> type,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        boolean magicalBiome = level.getBiome(pos).is(TCEntitySpawnRules.LEGACY_MAGICAL_BIOME_TAG);
        boolean overworldOrThaumcraftBiome = level.getLevel().dimension() == Level.OVERWORLD
                || isLegacyThaumcraftMagicBiome(level.getBiome(pos));
        int nearbyPech = level.getLevel().getEntitiesOfClass(
                TCPechEntity.class,
                new AABB(pos).inflate(16.0D, 16.0D, 16.0D)
        ).size();
        return testLegacySpawnGatesForValidation(
                TCConfig.ALLOW_SPAWN_PECH.get(),
                magicalBiome,
                overworldOrThaumcraftBiome,
                nearbyPech,
                Monster.checkMonsterSpawnRules(type, level, spawnType, pos, random)
        );
    }

    private static boolean isLegacyThaumcraftMagicBiome(Holder<Biome> biome) {
        return biome.unwrapKey()
                .map(key -> {
                    ResourceLocation location = key.location();
                    return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "magical_forest").equals(location)
                            || ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "eerie").equals(location);
                })
                .orElse(false);
    }

    public NonNullList<ItemStack> lootForValidation() {
        return loot;
    }

    public static boolean testLegacySpawnGatesForValidation(boolean configEnabled, boolean magicalBiome, boolean overworldOrThaumcraftBiome, int nearbyPech, boolean mobRulesAllow) {
        return configEnabled && magicalBiome && overworldOrThaumcraftBiome && nearbyPech < 4 && mobRulesAllow;
    }

    @Override
    public int getContainerSize() {
        return LEGACY_LOOT_SLOTS;
    }

    @Override
    public boolean isEmpty() {
        return loot.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < loot.size() ? loot.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return slot >= 0 && slot < loot.size() ? ContainerHelper.removeItem(loot, slot, amount) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return slot >= 0 && slot < loot.size() ? ContainerHelper.takeItem(loot, slot) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < loot.size()) {
            loot.set(slot, stack == null ? ItemStack.EMPTY : stack.copy());
        }
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return isAlive() && isTamed() && player.distanceToSqr(this) <= 64.0D;
    }

    @Override
    public void clearContent() {
        loot.replaceAll(ignored -> ItemStack.EMPTY);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new TCPechMenu(containerId, inventory, this);
    }

    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(getId());
    }

    private final class TradePlayerGoal extends Goal {
        @Override
        public boolean canUse() {
            return isAlive() && isTamed() && trading && onGround() && getDeltaMovement().lengthSqr() < 0.01D && !isInWater();
        }

        @Override
        public void start() {
            getNavigation().stop();
        }

        @Override
        public void stop() {
            trading = false;
        }
    }

    private final class PickupItemGoal extends Goal {
        @Nullable
        private ItemEntity targetItem;

        @Override
        public boolean canUse() {
            if (!TCPechEntity.this.canPickUpLoot() || trading || getTarget() != null) {
                return false;
            }
            targetItem = level().getEntitiesOfClass(
                            ItemEntity.class,
                            getBoundingBox().inflate(LEGACY_PICKUP_RANGE),
                            item -> item.isAlive() && canPickup(item.getItem())
                    )
                    .stream()
                    .min(Comparator.comparingDouble(item -> item.distanceToSqr(TCPechEntity.this)))
                    .orElse(null);
            return targetItem != null;
        }

        @Override
        public boolean canContinueToUse() {
            return targetItem != null && targetItem.isAlive() && canPickup(targetItem.getItem()) && !trading && getTarget() == null;
        }

        @Override
        public void tick() {
            if (targetItem == null) {
                return;
            }
            getNavigation().moveTo(targetItem, LEGACY_MOVEMENT_SPEED * 1.5D);
            if (distanceTo(targetItem) <= LEGACY_PICKUP_DISTANCE) {
                ItemStack before = targetItem.getItem().copy();
                ItemStack remaining = pickupItem(targetItem.getItem().copy());
                if (remaining.isEmpty()) {
                    targetItem.discard();
                } else {
                    targetItem.setItem(remaining);
                }
                if (before.getCount() != (remaining.isEmpty() ? 0 : remaining.getCount())) {
                    level().playSound(null, blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.2F, 1.0F);
                }
                targetItem = null;
            }
        }

        @Override
        public void stop() {
            targetItem = null;
            getNavigation().stop();
        }
    }

    private final class AvoidUntamedPlayerGoal extends AvoidEntityGoal<Player> {
        private AvoidUntamedPlayerGoal() {
            super(TCPechEntity.this, Player.class, 8.0F, 0.5D, 0.6D);
        }

        @Override
        public boolean canUse() {
            return !isTamed() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !isTamed() && super.canContinueToUse();
        }
    }

    private final class AngryNearestPlayerTargetGoal extends NearestAttackableTargetGoal<Player> {
        private AngryNearestPlayerTargetGoal() {
            super(TCPechEntity.this, Player.class, true);
        }

        @Override
        public boolean canUse() {
            return getAnger() > 0 && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return getAnger() > 0 && super.canContinueToUse();
        }
    }

    public enum PechType {
        FORAGER(0, "entity.thaumcraft.pech"),
        MAGE(1, "entity.thaumcraft.pech.1"),
        STALKER(2, "entity.thaumcraft.pech.2");

        private final int legacyId;
        private final String translationKey;

        PechType(int legacyId, String translationKey) {
            this.legacyId = legacyId;
            this.translationKey = translationKey;
        }

        public int legacyId() {
            return legacyId;
        }

        public String translationKey() {
            return translationKey;
        }

        public static PechType byLegacyId(int id) {
            for (PechType type : values()) {
                if (type.legacyId == id) {
                    return type;
                }
            }
            return FORAGER;
        }
    }
}

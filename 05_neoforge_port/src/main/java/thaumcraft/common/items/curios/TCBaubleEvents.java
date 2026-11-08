package thaumcraft.common.items.curios;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.integration.curios.TCCuriosCompat;
import thaumcraft.common.items.TCRechargeHelper;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.items.equipment.TCEquipmentHelper;
import thaumcraft.common.items.tools.TCEnumInfusionEnchantment;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCMobEffects;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.research.TCKnowledgeType;
import thaumcraft.common.research.TCResearchManager;

/**
 * Runtime handler for TC6 Magical Jewelry and Curios integration.
 * Full legacy parity:
 * - Vis Amulet: periodic aura drain to recharge equipped & hotbar items
 * - Cloud Ring: mid-air double jump, sprint boost, fall distance & damage reduction
 * - Curiosity Band: XP orb pickup extracts research knowledge (theories & observations)
 * - Charm of Undying: fatal damage rescue, effect purge, regen + absorption, totem broadcast
 * - Verdant Heart (Base, Life, Sustain): condition curing, health regen, air & food sustenance
 * - Runic Shielding: calculate runic charge across armor + curios, absorption health pool,
 *   aura vis recharge, break cooldown & tooltips.
 * - Fortress Armor: mask combat triggers and 2+ piece set bonus.
 * - Infusion Enchantments & Warping tooltips.
 */
@EventBusSubscriber(modid = Thaumcraft.MODID)
public final class TCBaubleEvents {
    private static final int CRAFTED_VIS_AMULET_INTERVAL_TICKS = 5;
    private static final int VERDANT_HEART_INTERVAL_TICKS = 20;
    private static final int RUNIC_SCAN_INTERVAL_TICKS = 20;
    private static final int HOTBAR_SIZE = 9;
    private static final double CLOUD_RING_JUMP_VELOCITY = 0.75D;
    private static final double CLOUD_RING_SPRINT_PUSH = 0.2D;
    private static final long SHIELD_RECHARGE_MS = 2000L;
    private static final long SHIELD_WAIT_MS = 4000L;
    private static final float SHIELD_COST = 1.0F;
    private static final ResourceLocation RUNIC_ABSORPTION_LIMIT =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "runic_absorption_limit");
    private static final ResourceLocation FORTRESS_SET_ARMOR =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "fortress_set_armor");
    private static final ResourceLocation FORTRESS_SET_TOUGHNESS =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "fortress_set_toughness");

    private static final Set<UUID> CLOUD_RING_AIRBORNE_JUMPS = new HashSet<>();
    private static final Map<UUID, Integer> RUNIC_INFO = new HashMap<>();
    private static final Map<UUID, Integer> LAST_MAX_CHARGE = new HashMap<>();
    private static final Map<UUID, Integer> LAST_CHARGE = new HashMap<>();
    private static final Map<UUID, Long> NEXT_CYCLE = new HashMap<>();

    public static void clearAll() {
        CLOUD_RING_AIRBORNE_JUMPS.clear();
        RUNIC_INFO.clear();
        LAST_MAX_CHARGE.clear();
        LAST_CHARGE.clear();
        NEXT_CYCLE.clear();
    }

    private TCBaubleEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        updateRunicAbsorptionLimit(event.getEntity(), 0);
        updateFortressSetBonus(event.getEntity(), false);
        UUID uuid = event.getEntity().getUUID();
        CLOUD_RING_AIRBORNE_JUMPS.remove(uuid);
        RUNIC_INFO.remove(uuid);
        LAST_MAX_CHARGE.remove(uuid);
        LAST_CHARGE.remove(uuid);
        NEXT_CYCLE.remove(uuid);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        if (player.onGround() || player.isInWater()) {
            CLOUD_RING_AIRBORNE_JUMPS.remove(player.getUUID());
        }

        // 1. Vis Amulet recharging
        if (player.tickCount % CRAFTED_VIS_AMULET_INTERVAL_TICKS == 0) {
            ItemStack visAmulet = getWornBauble(player, TCItems.VIS_AMULET.get());
            if (!visAmulet.isEmpty()) {
                rechargeFirstEligibleStack(player, visAmulet);
            }
        }

        // 2. Verdant Heart ticking
        if (player.tickCount % VERDANT_HEART_INTERVAL_TICKS == 0) {
            tickVerdantHearts(player);
        }

        // 3. Runic Shielding
        handleRunicShielding(player);

        // 4. Fortress armor set bonus (2+ pieces = +1 armor, +1 toughness)
        int fortressPieces = 0;
        for (ItemStack piece : player.getArmorSlots()) {
            if (piece.getItem() instanceof ItemFortressArmor) {
                fortressPieces++;
            }
        }
        updateFortressSetBonus(player, fortressPieces >= 2);
    }

    @SubscribeEvent
    public static void onPickupXp(PlayerXpEvent.PickupXp event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (getWornBauble(player, TCItems.CURIOSITY_BAND.get()).isEmpty()) {
            return;
        }

        ExperienceOrb orb = event.getOrb();
        int xp = orb.getValue();
        if (xp > 1) {
            int d = xp / 2;
            orb.value -= d;
            float r = player.getRandom().nextFloat();
            List<String> cats = TCResearchManager.categoryKeys();
            if (!cats.isEmpty()) {
                if (r < 0.05F * d) {
                    String cat = cats.get(player.getRandom().nextInt(cats.size()));
                    TCResearchManager.addKnowledgeRaw(player, TCKnowledgeType.THEORY, cat, TCKnowledgeType.THEORY.pointsToRaw(1));
                } else if (r < 0.2F * d) {
                    String cat = cats.get(player.getRandom().nextInt(cats.size()));
                    TCResearchManager.addKnowledgeRaw(player, TCKnowledgeType.OBSERVATION, cat, TCKnowledgeType.OBSERVATION.pointsToRaw(1));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (consumeWornBauble(player, TCItems.CHARM_UNDYING.get())) {
            player.setHealth(1.0F);
            player.removeAllEffects();
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            player.level().broadcastEntityEvent(player, (byte) 35);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        int charge = getRunicCharge(event.getItemStack());
        if (charge > 0) {
            event.getToolTip().add(Component.translatable("item.runic.charge").append(" +" + charge).withStyle(ChatFormatting.GOLD));
        }

        int warp = TCEquipmentHelper.getStackWarp(event.getItemStack(), event.getEntity());
        if (warp > 0) {
            event.getToolTip().add(Component.translatable("item.warping").append(" " + warp).withStyle(ChatFormatting.DARK_PURPLE));
        }

        for (TCEnumInfusionEnchantment ench : TCEnumInfusionEnchantment.getInfusionEnchantments(event.getItemStack())) {
            int lvl = TCEnumInfusionEnchantment.getInfusionEnchantmentLevel(event.getItemStack(), ench);
            var comp = Component.translatable("enchantment.infusion." + ench.name());
            if (ench.maxLevel > 1) {
                comp = comp.append(" ").append(Component.translatable("enchantment.level." + lvl));
            }
            event.getToolTip().add(comp.withStyle(ChatFormatting.GOLD));
        }
    }

    private static void tickVerdantHearts(Player player) {
        ItemStack heart = getWornBauble(player, TCItems.VERDANT_HEART.get());
        if (heart.isEmpty()) {
            heart = getWornBauble(player, TCItems.VERDANT_HEART_LIFE.get());
        }
        if (heart.isEmpty()) {
            heart = getWornBauble(player, TCItems.VERDANT_HEART_SUSTAIN.get());
        }
        if (heart.isEmpty()) {
            return;
        }

        if (player.hasEffect(MobEffects.WITHER) && TCRechargeHelper.consumeCharge(heart, 20)) {
            player.removeEffect(MobEffects.WITHER);
            return;
        }
        if (player.hasEffect(MobEffects.POISON) && TCRechargeHelper.consumeCharge(heart, 10)) {
            player.removeEffect(MobEffects.POISON);
            return;
        }
        if (player.hasEffect(TCMobEffects.FLUX_TAINT) && TCRechargeHelper.consumeCharge(heart, 5)) {
            player.removeEffect(TCMobEffects.FLUX_TAINT);
            return;
        }

        if (heart.is(TCItems.VERDANT_HEART_LIFE.get()) && player.getHealth() < player.getMaxHealth() && TCRechargeHelper.consumeCharge(heart, 5)) {
            player.heal(1.0F);
            return;
        }

        if (heart.is(TCItems.VERDANT_HEART_SUSTAIN.get())) {
            if (player.getAirSupply() < 100 && TCRechargeHelper.consumeCharge(heart, 1)) {
                player.setAirSupply(300);
                return;
            }
            if (player.canEat(false) && TCRechargeHelper.consumeCharge(heart, 1)) {
                player.getFoodData().eat(1, 0.3F);
            }
        }
    }

    private static void handleRunicShielding(Player player) {
        UUID uuid = player.getUUID();
        if (player.tickCount % RUNIC_SCAN_INTERVAL_TICKS == 0) {
            int max = 0;
            for (ItemStack armor : player.getInventory().armor) {
                max += getRunicCharge(armor);
            }
            for (ItemStack curio : TCCuriosCompat.getAllEquippedCurios(player)) {
                max += getRunicCharge(curio);
            }
            // Fallback: check offhand
            if (!TCCuriosCompat.isLoaded()) {
                max += getRunicCharge(player.getOffhandItem());
            }

            if (LAST_MAX_CHARGE.containsKey(uuid)) {
                int lastMax = LAST_MAX_CHARGE.get(uuid);
                if (lastMax > max) {
                    player.setAbsorptionAmount(Math.max(0.0F, player.getAbsorptionAmount() - (lastMax - max)));
                }
                if (max <= 0) {
                    LAST_MAX_CHARGE.remove(uuid);
                }
            }
            updateRunicAbsorptionLimit(player, max);
            if (max > 0) {
                RUNIC_INFO.put(uuid, max);
                LAST_MAX_CHARGE.put(uuid, max);
            } else {
                RUNIC_INFO.remove(uuid);
            }
        }

        if (RUNIC_INFO.containsKey(uuid)) {
            if (!NEXT_CYCLE.containsKey(uuid)) {
                NEXT_CYCLE.put(uuid, 0L);
            }
            long time = System.currentTimeMillis();
            int charge = (int) player.getAbsorptionAmount();

            if (charge == 0 && LAST_CHARGE.containsKey(uuid) && LAST_CHARGE.get(uuid) > 0) {
                NEXT_CYCLE.put(uuid, time + SHIELD_WAIT_MS);
                LAST_CHARGE.put(uuid, 0);
            }

            int maxRunic = RUNIC_INFO.get(uuid);
            if (charge < maxRunic && NEXT_CYCLE.get(uuid) < time) {
                Level level = player.level();
                BlockPos pos = player.blockPosition();
                if (!AuraHelper.shouldPreserveAura(level, player, pos) && AuraHelper.getVis(level, pos) >= SHIELD_COST) {
                    if (AuraHelper.drainVis(level, pos, SHIELD_COST, false) < SHIELD_COST) {
                        return;
                    }
                    NEXT_CYCLE.put(uuid, time + SHIELD_RECHARGE_MS);
                    player.setAbsorptionAmount((float) (charge + 1));
                    LAST_CHARGE.put(uuid, charge + 1);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            TCSounds.RUNIC_SHIELD_CHARGE.get(), SoundSource.PLAYERS, 0.66F,
                            1.1F + (float) level.random.nextGaussian() * 0.05F);
                }
            }
        }
    }

    public static void updateRunicAbsorptionLimit(Player player, int charge) {
        var attribute = player.getAttribute(Attributes.MAX_ABSORPTION);
        if (attribute == null) return;
        double amount = Math.max(0, charge);
        var previous = attribute.getModifier(RUNIC_ABSORPTION_LIMIT);
        if (previous != null && previous.amount() == amount) return;
        if (amount > 0) {
            attribute.addOrUpdateTransientModifier(new AttributeModifier(
                    RUNIC_ABSORPTION_LIMIT, amount, AttributeModifier.Operation.ADD_VALUE));
        } else if (previous != null) {
            attribute.removeModifier(RUNIC_ABSORPTION_LIMIT);
        }
        player.setAbsorptionAmount(Math.min(player.getAbsorptionAmount(), player.getMaxAbsorption()));
    }

    public static void updateFortressSetBonus(Player player, boolean active) {
        var armorAttr = player.getAttribute(Attributes.ARMOR);
        var toughAttr = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (armorAttr != null) {
            var prev = armorAttr.getModifier(FORTRESS_SET_ARMOR);
            if (active && prev == null) {
                armorAttr.addOrUpdateTransientModifier(new AttributeModifier(
                        FORTRESS_SET_ARMOR, 1.0D, AttributeModifier.Operation.ADD_VALUE));
            } else if (!active && prev != null) {
                armorAttr.removeModifier(FORTRESS_SET_ARMOR);
            }
        }
        if (toughAttr != null) {
            var prev = toughAttr.getModifier(FORTRESS_SET_TOUGHNESS);
            if (active && prev == null) {
                toughAttr.addOrUpdateTransientModifier(new AttributeModifier(
                        FORTRESS_SET_TOUGHNESS, 1.0D, AttributeModifier.Operation.ADD_VALUE));
            } else if (!active && prev != null) {
                toughAttr.removeModifier(FORTRESS_SET_TOUGHNESS);
            }
        }
    }

    public static int getRunicCharge(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        int charge = stack.getOrDefault(TCDataComponents.RUNIC_CHARGE.get(), 0);
        if (charge > 0) {
            return charge;
        }
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("TC.RUNIC")) {
            return customData.copyTag().getByte("TC.RUNIC");
        }
        return 0;
    }

    @SubscribeEvent
    public static void onLivingJump(LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            if (boots.getItem() == TCItems.TRAVELLER_BOOTS.get() && TCRechargeHelper.hasUsableEnergy(boots)) {
                Vec3 movement = player.getDeltaMovement();
                player.setDeltaMovement(movement.x, movement.y + 0.2750000059604645D, movement.z);
                player.hasImpulse = true;
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // Runic shield hit effect
        UUID uuid = player.getUUID();
        if (RUNIC_INFO.containsKey(uuid) && player.getAbsorptionAmount() > 0.0F) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    TCSounds.RUNIC_SHIELD_EFFECT.get(), SoundSource.PLAYERS, 0.66F,
                    1.1F + (float) player.level().random.nextGaussian() * 0.05F);
        }

        if (event.getSource().is(DamageTypeTags.IS_FALL)) {
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            if (boots.getItem() == TCItems.TRAVELLER_BOOTS.get()) {
                float damage = event.getNewDamage();
                float reduced = Math.max(0.0F, damage / 2.0F - 1.0F);
                event.setNewDamage(reduced);
            } else if (hasCloudRing(player)) {
                float damage = event.getNewDamage();
                float reduced = Math.max(0.0F, damage / 3.0F - 2.0F);
                event.setNewDamage(reduced);
            }
        } else {
            int fortressPieces = 0;
            for (ItemStack armor : player.getArmorSlots()) {
                if (armor.getItem() instanceof ItemFortressArmor) {
                    fortressPieces++;
                }
            }
            if (fortressPieces > 0) {
                float damage = event.getNewDamage();
                if (event.getSource().is(DamageTypeTags.IS_FIRE) || event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
                    damage *= (1.0F - 0.05F * fortressPieces);
                    event.setNewDamage(damage);
                } else if (event.getSource().is(DamageTypeTags.WITCH_RESISTANT_TO) || event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) {
                    damage *= (1.0F - 0.03F * fortressPieces);
                    event.setNewDamage(damage);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        // Fortress mask 1 (Angry Ghost): wither retaliation
        if (event.getEntity() instanceof Player victim) {
            ItemStack helm = victim.getItemBySlot(EquipmentSlot.HEAD);
            if (helm.getItem() instanceof ItemFortressArmor && ItemFortressArmor.getMask(helm) == 1) {
                if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                    if (victim.getRandom().nextFloat() < event.getNewDamage() / 10.0F) {
                        attacker.addEffect(new MobEffectInstance(MobEffects.WITHER, 80));
                    }
                }
            }
        }

        // Fortress mask 2 (Sipping Fiend): lifesteal
        if (event.getSource().getEntity() instanceof Player attacker) {
            ItemStack helm = attacker.getItemBySlot(EquipmentSlot.HEAD);
            if (helm.getItem() instanceof ItemFortressArmor && ItemFortressArmor.getMask(helm) == 2) {
                if (attacker.getRandom().nextFloat() < event.getNewDamage() / 12.0F) {
                    attacker.heal(1.0F);
                }
            }
        }
    }

    public static boolean tryCloudRingJump(ServerPlayer player) {
        if (!canAttemptCloudRingJump(player) || !hasCloudRing(player)) {
            return false;
        }

        UUID uuid = player.getUUID();
        if (CLOUD_RING_AIRBORNE_JUMPS.contains(uuid)) {
            return false;
        }

        CLOUD_RING_AIRBORNE_JUMPS.add(uuid);
        applyCloudRingImpulse(player);
        player.level().playSound(
                player,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS,
                0.1F,
                1.0F + (float) player.level().random.nextGaussian() * 0.05F
        );
        return true;
    }

    public static boolean canAttemptCloudRingJump(Player player) {
        return player != null
                && !player.onGround()
                && !player.isInWater()
                && !player.isFallFlying()
                && !player.isShiftKeyDown();
    }

    public static boolean hasCloudRing(Player player) {
        return !getWornBauble(player, TCItems.CLOUD_RING.get()).isEmpty();
    }

    public static void applyCloudRingImpulse(Player player) {
        Vec3 current = player.getDeltaMovement();
        double motionX = current.x;
        double motionY = CLOUD_RING_JUMP_VELOCITY;
        double motionZ = current.z;

        MobEffectInstance jumpBoost = player.getEffect(MobEffects.JUMP);
        if (jumpBoost != null) {
            motionY += (jumpBoost.getAmplifier() + 1) * 0.1D;
        }

        if (player.isSprinting()) {
            float yaw = player.getYRot() * ((float) Math.PI / 180.0F);
            motionX -= Mth.sin(yaw) * CLOUD_RING_SPRINT_PUSH;
            motionZ += Mth.cos(yaw) * CLOUD_RING_SPRINT_PUSH;
        }

        player.setDeltaMovement(motionX, motionY, motionZ);
        player.hasImpulse = true;
        player.resetFallDistance();
    }

    private static boolean rechargeFirstEligibleStack(Player player, ItemStack source) {
        Level level = player.level();
        BlockPos pos = player.blockPosition();
        if (AuraHelper.shouldPreserveAura(level, player, pos)) {
            return false;
        }

        Inventory inventory = player.getInventory();
        int hotbarEnd = Math.min(HOTBAR_SIZE, inventory.items.size());

        if (rechargeFrom(player, inventory.items, source, 0, hotbarEnd)) {
            return true;
        }
        for (ItemStack curio : TCCuriosCompat.getAllEquippedCurios(player)) {
            if (!curio.isEmpty() && curio != source && tryRechargeTarget(player, curio, source)) {
                return true;
            }
        }
        if (rechargeFrom(player, inventory.armor, source, 0, inventory.armor.size())) {
            return true;
        }
        return rechargeFrom(player, inventory.offhand, source, 0, inventory.offhand.size());
    }

    private static boolean tryRechargeTarget(Player player, ItemStack target, ItemStack source) {
        if (target.isEmpty() || target == source || !TCRechargeHelper.canRecharge(target)) {
            return false;
        }
        int needed = TCRechargeHelper.getMaxCharge(target) - TCRechargeHelper.getCharge(target);
        if (needed <= 0) {
            return false;
        }
        Level level = player.level();
        BlockPos pos = player.blockPosition();
        if (AuraHelper.shouldPreserveAura(level, player, pos)) {
            return false;
        }
        float drained = AuraHelper.drainVis(level, pos, 1.0F, false);
        if (drained >= 1.0F) {
            return TCRechargeHelper.recharge(target, 1) > 0;
        }
        return false;
    }

    private static boolean rechargeFrom(Player player, List<ItemStack> stacks, ItemStack source, int startInclusive, int endExclusive) {
        int start = Math.max(0, startInclusive);
        int end = Math.min(stacks.size(), endExclusive);
        for (int i = start; i < end; i++) {
            if (tryRechargeTarget(player, stacks.get(i), source)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack getWornBauble(Player player, Item item) {
        if (player == null) {
            return ItemStack.EMPTY;
        }
        ItemStack curio = TCCuriosCompat.findFirstCurio(player, item);
        if (!curio.isEmpty()) {
            return curio;
        }
        // Fallback for environment without Curios: allow offhand slot
        if (player.getOffhandItem().getItem() == item) {
            return player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }

    public static boolean consumeWornBauble(Player player, Item item) {
        if (player == null) {
            return false;
        }
        if (TCCuriosCompat.consumeCurio(player, item)) {
            return true;
        }
        if (player.getOffhandItem().getItem() == item) {
            player.getOffhandItem().shrink(1);
            return true;
        }
        return false;
    }
}

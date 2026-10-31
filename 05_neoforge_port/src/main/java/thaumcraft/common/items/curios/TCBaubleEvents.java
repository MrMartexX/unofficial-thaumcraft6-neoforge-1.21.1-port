package thaumcraft.common.items.curios;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.items.TCRechargeHelper;
import thaumcraft.common.registry.TCItems;

/**
 * Runtime bridge for TC6 Baubles behaviour while the NeoForge 1.21.1 port still
 * uses split item ids and data driven Curios slots.
 *
 * Implemented legacy behaviour:
 * - Baubles (Cloud Ring, Vis Amulet) require being worn in a Curios slot (or offhand fallback);
 *   inventory items in main storage or hotbar do NOT activate bauble powers.
 * - crafted Vis Amulet: every 5 ticks it recharges the first eligible item;
 * - Cloud Ring: one mid-air jump per airtime, using the original 0.75 vertical
 *   impulse, Jump Boost bonus, sprint push, fall-distance reset and sweep sound.
 */
@EventBusSubscriber(modid = Thaumcraft.MODID)
public final class TCBaubleEvents {
    private static final int CRAFTED_VIS_AMULET_INTERVAL_TICKS = 5;
    private static final int HOTBAR_SIZE = 9;
    private static final double CLOUD_RING_JUMP_VELOCITY = 0.75D;
    private static final double CLOUD_RING_SPRINT_PUSH = 0.2D;
    private static final Set<UUID> CLOUD_RING_AIRBORNE_JUMPS = new HashSet<>();

    private TCBaubleEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        CLOUD_RING_AIRBORNE_JUMPS.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide && (player.onGround() || player.isInWater())) {
            CLOUD_RING_AIRBORNE_JUMPS.remove(player.getUUID());
        }

        if (player.level().isClientSide || player.tickCount % CRAFTED_VIS_AMULET_INTERVAL_TICKS != 0) {
            return;
        }

        ItemStack visAmulet = getWornBauble(player, TCItems.VIS_AMULET.get());
        if (visAmulet.isEmpty()) {
            return;
        }

        rechargeFirstEligibleStack(player, visAmulet);
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
                if (armor.getItem() instanceof thaumcraft.common.items.armor.ItemFortressArmor) {
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
        if (rechargeFromCurios(player, source)) {
            return true;
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
        ItemStack curiosStack = firstMatchingCurios(player, item);
        if (!curiosStack.isEmpty()) {
            return curiosStack;
        }
        // Fallback for environment without Curios: allow offhand slot
        if (player.getOffhandItem().getItem() == item) {
            return player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }

    /**
     * Optional Curios bridge without a hard compile dependency. The port keeps
     * Curios slot tags as data, so this reflection layer makes Cloud Ring and
     * Vis Amulet behaviour work when Curios is present, while still allowing the
     * mod to load by itself during early parity tests.
     */
    private static Optional<?> curiosInventory(Player player) {
        try {
            Class<?> curiosApi = Class.forName("top.theillusivec4.curios.api.CuriosApi");
            Object optional = curiosApi.getMethod("getCuriosInventory", LivingEntity.class).invoke(null, player);
            if (optional instanceof Optional<?> result) {
                return result;
            }
        } catch (ReflectiveOperationException | ClassCastException | LinkageError ignored) {
            // Curios is optional for this alpha port.
        }
        return Optional.empty();
    }

    private static ItemStack firstMatchingCurios(Player player, Item item) {
        Optional<?> handler = curiosInventory(player);
        if (handler.isEmpty()) {
            return ItemStack.EMPTY;
        }

        try {
            Object resultOptional = handler.get().getClass().getMethod("findFirstCurio", Item.class).invoke(handler.get(), item);
            if (!(resultOptional instanceof Optional<?> result) || result.isEmpty()) {
                return ItemStack.EMPTY;
            }

            Object stack = result.get().getClass().getMethod("stack").invoke(result.get());
            return stack instanceof ItemStack itemStack ? itemStack : ItemStack.EMPTY;
        } catch (ReflectiveOperationException | ClassCastException | LinkageError ignored) {
            return ItemStack.EMPTY;
        }
    }

    private static boolean rechargeFromCurios(Player player, ItemStack source) {
        Optional<?> handler = curiosInventory(player);
        if (handler.isEmpty()) {
            return false;
        }

        try {
            Object equipped = handler.get().getClass().getMethod("getEquippedCurios").invoke(handler.get());
            int slots = (Integer) equipped.getClass().getMethod("getSlots").invoke(equipped);
            for (int slot = 0; slot < slots; slot++) {
                Object stack = equipped.getClass().getMethod("getStackInSlot", int.class).invoke(equipped, slot);
                if (stack instanceof ItemStack itemStack
                        && !itemStack.isEmpty()
                        && itemStack != source
                        && tryRechargeTarget(player, itemStack, source)) {
                    return true;
                }
            }
        } catch (ReflectiveOperationException | ClassCastException | LinkageError ignored) {
            return false;
        }
        return false;
    }
}

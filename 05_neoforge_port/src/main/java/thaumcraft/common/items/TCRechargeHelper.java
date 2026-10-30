package thaumcraft.common.items;

import net.minecraft.world.item.ItemStack;
import thaumcraft.common.items.armor.ItemTravellerBoots;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;

/**
 * Small NeoForge data-component replacement for TC6's IRechargable +
 * RechargeHelper path. The original mod stored a charge pool on several magic
 * items and then buffered that charge into a short lived "energy" value while
 * the item was actively used. Keeping both numbers as synced components makes
 * Traveller Boots and the next armor/bauble ports deterministic on server and
 * visible on client HUD/tooltip code later.
 */
public final class TCRechargeHelper {
    private TCRechargeHelper() {
    }

    public static int getMaxCharge(ItemStack stack) {
        if (stack.getItem() == TCItems.TRAVELLER_BOOTS.get()) {
            return ItemTravellerBoots.MAX_CHARGE;
        }
        return 0;
    }

    public static boolean canRecharge(ItemStack stack) {
        return getMaxCharge(stack) > 0;
    }

    public static int getCharge(ItemStack stack) {
        return Math.max(0, stack.getOrDefault(TCDataComponents.CHARGE.get(), 0));
    }

    public static int getActiveEnergy(ItemStack stack) {
        return Math.max(0, stack.getOrDefault(TCDataComponents.ACTIVE_ENERGY.get(), 0));
    }

    public static boolean hasUsableEnergy(ItemStack stack) {
        return getCharge(stack) > 0 || getActiveEnergy(stack) > 0;
    }

    public static void ensureInitialCharge(ItemStack stack, int maxCharge) {
        if (!stack.has(TCDataComponents.CHARGE.get())) {
            stack.set(TCDataComponents.CHARGE.get(), Math.max(0, maxCharge));
        }
        if (!stack.has(TCDataComponents.ACTIVE_ENERGY.get())) {
            stack.set(TCDataComponents.ACTIVE_ENERGY.get(), 0);
        }
    }

    public static void clampCharge(ItemStack stack, int maxCharge) {
        int max = Math.max(0, maxCharge);
        int charge = Math.min(getCharge(stack), max);
        int energy = Math.min(getActiveEnergy(stack), max);
        stack.set(TCDataComponents.CHARGE.get(), charge);
        stack.set(TCDataComponents.ACTIVE_ENERGY.get(), energy);
    }

    public static int recharge(ItemStack stack, int amount) {
        int maxCharge = getMaxCharge(stack);
        if (maxCharge <= 0 || amount <= 0) {
            return 0;
        }

        ensureInitialCharge(stack, maxCharge);
        clampCharge(stack, maxCharge);

        int charge = getCharge(stack);
        int accepted = Math.min(amount, maxCharge - charge);
        if (accepted <= 0) {
            return 0;
        }

        stack.set(TCDataComponents.CHARGE.get(), charge + accepted);
        return accepted;
    }

    public static int discharge(ItemStack stack, int amount) {
        int maxCharge = getMaxCharge(stack);
        if (maxCharge <= 0 || amount <= 0) {
            return 0;
        }

        ensureInitialCharge(stack, maxCharge);
        clampCharge(stack, maxCharge);

        int charge = getCharge(stack);
        int consumed = Math.min(amount, charge);
        if (consumed <= 0) {
            return 0;
        }

        stack.set(TCDataComponents.CHARGE.get(), charge - consumed);
        return consumed;
    }

    /**
     * Mirrors TC6 Traveller Boots: every second active energy ticks down; when
     * it empties, one stored charge is consumed and converted into a 60 second
     * active energy buffer.
     */
    public static boolean tickOneSecondEnergyBuffer(ItemStack stack, int maxCharge, int refillEnergy) {
        ensureInitialCharge(stack, maxCharge);
        clampCharge(stack, maxCharge);

        int energy = getActiveEnergy(stack);
        if (energy > 0) {
            stack.set(TCDataComponents.ACTIVE_ENERGY.get(), energy - 1);
            return true;
        }

        int charge = getCharge(stack);
        if (charge <= 0) {
            return false;
        }

        stack.set(TCDataComponents.CHARGE.get(), charge - 1);
        stack.set(TCDataComponents.ACTIVE_ENERGY.get(), Math.max(0, refillEnergy));
        return true;
    }
}

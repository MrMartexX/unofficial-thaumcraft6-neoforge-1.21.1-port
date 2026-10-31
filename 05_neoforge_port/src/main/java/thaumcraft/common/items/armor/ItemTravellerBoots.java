package thaumcraft.common.items.armor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.TCRechargeHelper;
import thaumcraft.common.registry.TCArmorMaterials;

/**
 * Boots of the Traveller ported from TC6's ItemBootsTraveller.
 *
 * TC6 behaviour kept here:
 * - max charge: 240;
 * - missing/initial charge starts at zero until recharged from aura/amulet;
 * - one charge refills a 60 second active-energy buffer;
 * - while charged and driving forward (positive forward movement input zza > 0),
 *   the boots improve ground, water, and airborne travel;
 * - step-height attribute is granted dynamically only when the boots have usable energy.
 */
public final class ItemTravellerBoots extends ArmorItem {
    public static final int MAX_CHARGE = 240;
    private static final int ENERGY_REFILL_SECONDS = 60;
    private static final ResourceLocation STEP_ID =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "traveller_boots_step");

    public ItemTravellerBoots() {
        super(
                TCArmorMaterials.TRAVELLER,
                Type.BOOTS,
                TCArmorMaterials.armorProperties(Type.BOOTS, 25).rarity(Rarity.RARE)
        );
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!(entity instanceof LivingEntity living) || living.getItemBySlot(EquipmentSlot.FEET) != stack) {
            return;
        }

        TCRechargeHelper.ensureInitialCharge(stack, MAX_CHARGE);
        boolean hasEnergy = TCRechargeHelper.hasUsableEnergy(stack);
        if (!level.isClientSide && entity.tickCount % 20 == 0) {
            hasEnergy = TCRechargeHelper.tickOneSecondEnergyBuffer(stack, MAX_CHARGE, ENERGY_REFILL_SECONDS);
        }

        if (!hasEnergy || living.isFallFlying() || living.isShiftKeyDown()) {
            return;
        }

        // Must be driving forward (positive forward movement input)
        if (living.zza <= 0.0F) {
            return;
        }

        Vec3 motion = entity.getDeltaMovement();
        if (living.onGround()) {
            double bonus = living.isInWater() ? 0.0125D : 0.05D;
            addLookMovement(entity, motion, bonus);
        } else if (living.isInWater()) {
            addLookMovement(entity, motion, 0.025D);
        } else {
            // Legacy airborne handling (jumpMovementFactor = 0.05F vs default 0.02F)
            addLookMovement(entity, motion, 0.03D);
        }
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers base = super.getDefaultAttributeModifiers(stack);
        if (TCRechargeHelper.hasUsableEnergy(stack)) {
            return base.withModifierAdded(
                    Attributes.STEP_HEIGHT,
                    new AttributeModifier(STEP_ID, 0.40D, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.FEET
            );
        }
        return base;
    }

    private static void addLookMovement(Entity entity, Vec3 currentMotion, double bonus) {
        Vec3 look = entity.getLookAngle();
        double x = look.x;
        double z = look.z;
        double length = Math.sqrt(x * x + z * z);
        if (length <= 1.0E-5D) {
            return;
        }
        entity.setDeltaMovement(currentMotion.add(x / length * bonus, 0.0D, z / length * bonus));
    }
}

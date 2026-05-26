package thaumcraft.common.items.consumables;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;

/**
 * Initial NeoForge port of the legacy Zombie Brain consumable.
 *
 * <p>Legacy TC6 restored 4 hunger, used 0.2 saturation, was edible by wolves, applied Hunger with high probability,
 * and awarded warp on eat. Warp is not ported yet, so this class currently keeps the food and Hunger behaviour only.</p>
 */
public class ItemZombieBrain extends Item {
    private static final FoodProperties FOOD = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(0.2F)
            .meat()
            .build();

    public ItemZombieBrain() {
        super(new Item.Properties().food(FOOD));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        if (!level.isClientSide() && livingEntity instanceof ServerPlayer player && level.random.nextFloat() < 0.8F) {
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 30, 0));
        }
        return result;
    }
}

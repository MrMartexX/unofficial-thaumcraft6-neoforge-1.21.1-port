package thaumcraft.common.items.consumables;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ItemTripleMeatTreat extends Item {
    public static final FoodProperties TRIPLE_MEAT_FOOD = new FoodProperties.Builder()
            .nutrition(6)
            .saturationModifier(0.8F)
            .alwaysEdible()
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 0.66F)
            .build();

    public ItemTripleMeatTreat() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON).food(TRIPLE_MEAT_FOOD));
    }
}

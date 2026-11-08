package thaumcraft.common.items.consumables;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemEdibleChunk extends Item {
    public static final FoodProperties CHUNK_FOOD = new FoodProperties.Builder()
            .nutrition(1)
            .saturationModifier(0.3F)
            .fast()
            .build();

    public ItemEdibleChunk() {
        super(new Item.Properties().food(CHUNK_FOOD));
    }

    public ItemEdibleChunk(Item.Properties properties) {
        super(properties.food(CHUNK_FOOD));
    }
}

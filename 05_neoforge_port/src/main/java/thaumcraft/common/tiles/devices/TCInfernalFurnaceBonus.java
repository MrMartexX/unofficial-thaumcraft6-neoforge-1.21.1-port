package thaumcraft.common.tiles.devices;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import thaumcraft.Thaumcraft;
import thaumcraft.api.crafting.InfernalFurnaceBonusRegistry;
import thaumcraft.common.registry.TCItems;

/** Internal legacy default smelting bonus table for the Infernal Furnace. */
public final class TCInfernalFurnaceBonus {
    private static final float DEFAULT_CHANCE = 0.33F;

    private TCInfernalFurnaceBonus() {
    }

    public static List<ItemStack> roll(Level level, ItemStack input) {
        ArrayList<ItemStack> output = new ArrayList<>();
        if (level == null || input == null || input.isEmpty()) {
            return output;
        }

        addIfRolled(level, output, input, ore("gold"), Items.GOLD_NUGGET, DEFAULT_CHANCE);
        addIfRolled(level, output, input, ore("iron"), Items.IRON_NUGGET, DEFAULT_CHANCE);
        addIfRolled(level, output, input, ore("cinnabar"), TCItems.QUICKSILVER_NUGGET, DEFAULT_CHANCE);
        addIfRolled(level, output, input, ore("copper"), TCItems.COPPER_NUGGET, DEFAULT_CHANCE);
        addIfRolled(level, output, input, ore("tin"), TCItems.TIN_NUGGET, DEFAULT_CHANCE);
        addIfRolled(level, output, input, ore("silver"), TCItems.SILVER_NUGGET, DEFAULT_CHANCE);
        addIfRolled(level, output, input, ore("lead"), TCItems.LEAD_NUGGET, DEFAULT_CHANCE);
        addIfRolled(level, output, input, ore("quartz"), TCItems.QUARTZ_NUGGET, DEFAULT_CHANCE);

        addIfRolled(level, output, input, TCItems.CLUSTER_IRON.get(), Items.IRON_NUGGET, DEFAULT_CHANCE);
        addIfRolled(level, output, input, TCItems.CLUSTER_GOLD.get(), Items.GOLD_NUGGET, DEFAULT_CHANCE);
        addIfRolled(level, output, input, TCItems.CLUSTER_CINNABAR.get(), TCItems.QUICKSILVER_NUGGET, DEFAULT_CHANCE);
        addIfRolled(level, output, input, TCItems.CLUSTER_COPPER.get(), TCItems.COPPER_NUGGET, DEFAULT_CHANCE);
        addIfRolled(level, output, input, TCItems.CLUSTER_TIN.get(), TCItems.TIN_NUGGET, DEFAULT_CHANCE);
        addIfRolled(level, output, input, TCItems.CLUSTER_SILVER.get(), TCItems.SILVER_NUGGET, DEFAULT_CHANCE);
        addIfRolled(level, output, input, TCItems.CLUSTER_LEAD.get(), TCItems.LEAD_NUGGET, DEFAULT_CHANCE);

        addIfRolled(level, output, input, Items.BEEF, TCItems.CHUNK_BEEF, DEFAULT_CHANCE);
        addIfRolled(level, output, input, Items.CHICKEN, TCItems.CHUNK_CHICKEN, DEFAULT_CHANCE);
        addIfRolled(level, output, input, Items.PORKCHOP, TCItems.CHUNK_PORK, DEFAULT_CHANCE);
        addIfRolled(level, output, input, Items.COD, TCItems.CHUNK_FISH, DEFAULT_CHANCE);
        addIfRolled(level, output, input, Items.SALMON, TCItems.CHUNK_FISH, DEFAULT_CHANCE);
        addIfRolled(level, output, input, Items.TROPICAL_FISH, TCItems.CHUNK_FISH, DEFAULT_CHANCE);
        addIfRolled(level, output, input, Items.RABBIT, TCItems.CHUNK_RABBIT, DEFAULT_CHANCE);
        addIfRolled(level, output, input, Items.MUTTON, TCItems.CHUNK_MUTTON, DEFAULT_CHANCE);

        addIfRolled(level, output, input, ore("diamond"), TCItems.RARE_EARTH, 0.025F);
        addIfRolled(level, output, input, ore("redstone"), TCItems.RARE_EARTH, 0.01F);
        addIfRolled(level, output, input, ore("lapis"), TCItems.RARE_EARTH, 0.01F);
        addIfRolled(level, output, input, ore("emerald"), TCItems.RARE_EARTH, 0.025F);
        addIfRolled(level, output, input, ore("gold"), TCItems.RARE_EARTH, 0.02F);
        addIfRolled(level, output, input, ore("iron"), TCItems.RARE_EARTH, 0.01F);
        addIfRolled(level, output, input, ore("cinnabar"), TCItems.RARE_EARTH, 0.025F);
        addIfRolled(level, output, input, ore("copper"), TCItems.RARE_EARTH, 0.01F);
        addIfRolled(level, output, input, ore("tin"), TCItems.RARE_EARTH, 0.01F);
        addIfRolled(level, output, input, ore("silver"), TCItems.RARE_EARTH, 0.02F);
        addIfRolled(level, output, input, ore("lead"), TCItems.RARE_EARTH, 0.01F);
        addIfRolled(level, output, input, ore("quartz"), TCItems.RARE_EARTH, 0.01F);
        addIfRolled(level, output, input, TCItems.CLUSTER_IRON.get(), TCItems.RARE_EARTH, 0.02F);
        addIfRolled(level, output, input, TCItems.CLUSTER_GOLD.get(), TCItems.RARE_EARTH, 0.02F);
        addIfRolled(level, output, input, TCItems.CLUSTER_CINNABAR.get(), TCItems.RARE_EARTH, 0.02F);
        addIfRolled(level, output, input, TCItems.CLUSTER_COPPER.get(), TCItems.RARE_EARTH, 0.02F);
        addIfRolled(level, output, input, TCItems.CLUSTER_TIN.get(), TCItems.RARE_EARTH, 0.02F);
        addIfRolled(level, output, input, TCItems.CLUSTER_SILVER.get(), TCItems.RARE_EARTH, 0.02F);
        addIfRolled(level, output, input, TCItems.CLUSTER_LEAD.get(), TCItems.RARE_EARTH, 0.02F);
        InfernalFurnaceBonusRegistry.appendRolls(level, input, output);
        return output;
    }

    public static boolean hasKnownBonusCandidate(ItemStack input) {
        return input != null && !input.isEmpty() && (InfernalFurnaceBonusRegistry.hasCandidate(input) || (
                input.is(ore("gold"))
                        || input.is(ore("iron"))
                        || input.is(ore("cinnabar"))
                        || input.is(ore("copper"))
                        || input.is(ore("tin"))
                        || input.is(ore("silver"))
                        || input.is(ore("lead"))
                        || input.is(ore("quartz"))
                        || input.is(ore("diamond"))
                        || input.is(ore("redstone"))
                        || input.is(ore("lapis"))
                        || input.is(ore("emerald"))
                        || input.is(TCItems.CLUSTER_IRON.get())
                        || input.is(TCItems.CLUSTER_GOLD.get())
                        || input.is(TCItems.CLUSTER_CINNABAR.get())
                        || input.is(TCItems.CLUSTER_COPPER.get())
                        || input.is(TCItems.CLUSTER_TIN.get())
                        || input.is(TCItems.CLUSTER_SILVER.get())
                        || input.is(TCItems.CLUSTER_LEAD.get())
                        || input.is(Items.BEEF)
                        || input.is(Items.CHICKEN)
                        || input.is(Items.PORKCHOP)
                        || input.is(Items.COD)
                        || input.is(Items.SALMON)
                        || input.is(Items.TROPICAL_FISH)
                        || input.is(Items.RABBIT)
                        || input.is(Items.MUTTON)));
    }

    private static void addIfRolled(Level level, List<ItemStack> output, ItemStack input, Item item, Item bonus, float chance) {
        if (input.is(item) && level.random.nextFloat() <= chance) {
            output.add(new ItemStack(bonus));
        }
    }

    private static void addIfRolled(
            Level level,
            List<ItemStack> output,
            ItemStack input,
            Item item,
            Supplier<? extends Item> bonus,
            float chance
    ) {
        if (input.is(item) && level.random.nextFloat() <= chance) {
            output.add(new ItemStack(bonus.get()));
        }
    }

    private static void addIfRolled(
            Level level,
            List<ItemStack> output,
            ItemStack input,
            TagKey<Item> tag,
            Item bonus,
            float chance
    ) {
        if (input.is(tag) && level.random.nextFloat() <= chance) {
            output.add(new ItemStack(bonus));
        }
    }

    private static void addIfRolled(
            Level level,
            List<ItemStack> output,
            ItemStack input,
            TagKey<Item> tag,
            Supplier<? extends Item> bonus,
            float chance
    ) {
        if (input.is(tag) && level.random.nextFloat() <= chance) {
            output.add(new ItemStack(bonus.get()));
        }
    }

    private static TagKey<Item> ore(String material) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/" + material));
    }
}

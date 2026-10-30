package thaumcraft.api.crafting;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

/**
 * Public extension point for addon-specific Infernal Furnace bonus outputs.
 * Rules are evaluated after Thaumcraft's built-in legacy table.
 */
public final class InfernalFurnaceBonusRegistry {
    private static final List<Rule> RULES = new CopyOnWriteArrayList<>();

    public static void register(
            Supplier<? extends ItemLike> input,
            Supplier<? extends ItemLike> output,
            float chance
    ) {
        validateChance(chance);
        RULES.add(new Rule(stack -> stack.is(input.get().asItem()),
                () -> new ItemStack(output.get()), chance));
    }

    public static void register(
            Supplier<? extends ItemLike> input,
            TagKey<Item> output,
            float chance
    ) {
        validateChance(chance);
        RULES.add(new Rule(stack -> stack.is(input.get().asItem()),
                () -> firstStack(output), chance));
    }

    public static void register(TagKey<Item> input, TagKey<Item> output, float chance) {
        validateChance(chance);
        RULES.add(new Rule(stack -> stack.is(input), () -> firstStack(output), chance));
    }

    public static void appendRolls(Level level, ItemStack input, List<ItemStack> output) {
        if (level == null || input == null || input.isEmpty()) {
            return;
        }
        for (Rule rule : RULES) {
            if (rule.input().test(input) && level.random.nextFloat() <= rule.chance()) {
                ItemStack result = rule.output().get();
                if (!result.isEmpty()) {
                    output.add(result);
                }
            }
        }
    }

    public static boolean hasCandidate(ItemStack input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return RULES.stream().anyMatch(rule -> rule.input().test(input) && !rule.output().get().isEmpty());
    }

    private static ItemStack firstStack(TagKey<Item> tag) {
        return BuiltInRegistries.ITEM.getTag(tag)
                .flatMap(set -> set.stream().findFirst())
                .map(holder -> new ItemStack(holder.value()))
                .orElse(ItemStack.EMPTY);
    }

    private static void validateChance(float chance) {
        if (!Float.isFinite(chance) || chance < 0.0F || chance > 1.0F) {
            throw new IllegalArgumentException("Infernal Furnace bonus chance must be between 0 and 1");
        }
    }

    private record Rule(ItemPredicate input, Supplier<ItemStack> output, float chance) { }

    @FunctionalInterface
    private interface ItemPredicate {
        boolean test(ItemStack stack);
    }

    private InfernalFurnaceBonusRegistry() {
    }
}

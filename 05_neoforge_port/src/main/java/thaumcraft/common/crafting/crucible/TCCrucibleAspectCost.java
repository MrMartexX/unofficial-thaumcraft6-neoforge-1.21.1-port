package thaumcraft.common.crafting.crucible;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.TCAspectVariantStacks;

public record TCCrucibleAspectCost(String aspect, int amount) {
    public static final Codec<TCCrucibleAspectCost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.comapFlatMap(TCCrucibleAspectCost::validateAspect, value -> value)
                    .fieldOf("aspect")
                    .forGetter(TCCrucibleAspectCost::aspect),
            Codec.intRange(1, Integer.MAX_VALUE)
                    .fieldOf("amount")
                    .forGetter(TCCrucibleAspectCost::amount)
    ).apply(instance, TCCrucibleAspectCost::new));

    public static final Codec<List<TCCrucibleAspectCost>> LIST_CODEC = CODEC.listOf().comapFlatMap(
            TCCrucibleAspectCost::validateList,
            List::copyOf
    );

    public TCCrucibleAspectCost {
        aspect = canonicalAspect(aspect);
        if (Aspect.getAspect(aspect) == null) {
            throw new IllegalArgumentException("Unknown Thaumcraft aspect in crucible recipe: " + aspect);
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Crucible aspect cost must be positive");
        }
    }

    public Aspect resolvedAspect() {
        return Aspect.getAspect(aspect);
    }

    public ItemStack displayStack() {
        ItemStack stack = TCAspectVariantStacks.crystal(resolvedAspect());
        if (!stack.isEmpty()) {
            stack.setCount(amount);
        }
        return stack;
    }

    private static DataResult<String> validateAspect(String rawAspect) {
        String aspect = canonicalAspect(rawAspect);
        return Aspect.getAspect(aspect) == null
                ? DataResult.error(() -> "Unknown Thaumcraft aspect in crucible recipe: " + rawAspect)
                : DataResult.success(aspect);
    }

    private static DataResult<List<TCCrucibleAspectCost>> validateList(List<TCCrucibleAspectCost> costs) {
        if (costs.size() > Aspect.aspects.size()) {
            return DataResult.error(() -> "Too many aspects in crucible recipe: " + costs.size());
        }
        ArrayList<String> seen = new ArrayList<>();
        for (TCCrucibleAspectCost cost : costs) {
            if (seen.contains(cost.aspect())) {
                return DataResult.error(() -> "Duplicate crucible aspect cost: " + cost.aspect());
            }
            seen.add(cost.aspect());
        }
        return DataResult.success(List.copyOf(costs));
    }

    private static String canonicalAspect(String rawAspect) {
        return rawAspect == null ? "" : rawAspect.trim().toLowerCase(Locale.ROOT);
    }
}

package thaumcraft.common.crafting.arcane;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.TCAspectVariantStacks;

public record TCArcaneCrystalCost(String aspect, int amount) {
    static final Codec<TCArcaneCrystalCost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.comapFlatMap(TCArcaneCrystalCost::validateAspect, value -> value)
                    .fieldOf("aspect")
                    .forGetter(TCArcaneCrystalCost::aspect),
            Codec.intRange(1, Integer.MAX_VALUE)
                    .fieldOf("amount")
                    .forGetter(TCArcaneCrystalCost::amount)
    ).apply(instance, TCArcaneCrystalCost::new));

    static final Codec<List<TCArcaneCrystalCost>> LIST_CODEC = CODEC.listOf().comapFlatMap(
            TCArcaneCrystalCost::validateList,
            List::copyOf
    );

    public TCArcaneCrystalCost {
        aspect = canonicalAspect(aspect);
        if (Aspect.getAspect(aspect) == null) {
            throw new IllegalArgumentException("Unknown Thaumcraft aspect in arcane recipe: " + aspect);
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Arcane crystal cost must be positive");
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

    static AspectList toAspectList(List<TCArcaneCrystalCost> costs) {
        AspectList aspects = new AspectList();
        for (TCArcaneCrystalCost cost : costs) {
            aspects.add(cost.resolvedAspect(), cost.amount());
        }
        return aspects;
    }

    static List<TCArcaneCrystalCost> fromAspectList(AspectList aspects) {
        if (aspects == null || aspects.size() == 0) {
            return List.of();
        }
        ArrayList<TCArcaneCrystalCost> costs = new ArrayList<>(aspects.size());
        for (Aspect aspect : aspects.getAspects()) {
            costs.add(new TCArcaneCrystalCost(aspect.getTag(), aspects.getAmount(aspect)));
        }
        return List.copyOf(costs);
    }

    private static DataResult<String> validateAspect(String rawAspect) {
        String aspect = canonicalAspect(rawAspect);
        return Aspect.getAspect(aspect) == null
                ? DataResult.error(() -> "Unknown Thaumcraft aspect in arcane recipe: " + rawAspect)
                : DataResult.success(aspect);
    }

    private static DataResult<List<TCArcaneCrystalCost>> validateList(List<TCArcaneCrystalCost> costs) {
        if (costs.size() > Aspect.aspects.size()) {
            return DataResult.error(() -> "Too many crystal costs in arcane recipe: " + costs.size());
        }
        ArrayList<String> seen = new ArrayList<>();
        for (TCArcaneCrystalCost cost : costs) {
            if (seen.contains(cost.aspect())) {
                return DataResult.error(() -> "Duplicate arcane crystal cost: " + cost.aspect());
            }
            seen.add(cost.aspect());
        }
        return DataResult.success(List.copyOf(costs));
    }

    private static String canonicalAspect(String rawAspect) {
        return rawAspect == null ? "" : rawAspect.trim().toLowerCase(Locale.ROOT);
    }
}

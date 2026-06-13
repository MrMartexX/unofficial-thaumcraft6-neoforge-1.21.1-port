package thaumcraft.common.crafting.crucible;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public record TCCrucibleAspectCost(String aspect, int amount) {
    public static final Codec<List<TCCrucibleAspectCost>> MAP_CODEC = Codec.unboundedMap(
            Codec.STRING.comapFlatMap(TCCrucibleAspectCost::validateAspect, value -> value),
            Codec.intRange(1, Integer.MAX_VALUE)
    ).comapFlatMap(TCCrucibleAspectCost::fromMap, TCCrucibleAspectCost::toMap);

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

    public static AspectList toAspectList(List<TCCrucibleAspectCost> costs) {
        AspectList aspects = new AspectList();
        for (TCCrucibleAspectCost cost : costs) {
            aspects.add(cost.resolvedAspect(), cost.amount());
        }
        return aspects;
    }

    private static DataResult<String> validateAspect(String rawAspect) {
        String aspect = canonicalAspect(rawAspect);
        return Aspect.getAspect(aspect) == null
                ? DataResult.error(() -> "Unknown Thaumcraft aspect in crucible recipe: " + rawAspect)
                : DataResult.success(aspect);
    }

    private static DataResult<List<TCCrucibleAspectCost>> fromMap(Map<String, Integer> values) {
        if (values.isEmpty()) {
            return DataResult.error(() -> "Crucible recipe must define at least one aspect cost");
        }
        ArrayList<TCCrucibleAspectCost> costs = new ArrayList<>(values.size());
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            costs.add(new TCCrucibleAspectCost(entry.getKey(), entry.getValue()));
        }
        return DataResult.success(List.copyOf(costs));
    }

    private static Map<String, Integer> toMap(List<TCCrucibleAspectCost> costs) {
        LinkedHashMap<String, Integer> values = new LinkedHashMap<>();
        for (TCCrucibleAspectCost cost : costs) {
            values.put(cost.aspect(), cost.amount());
        }
        return values;
    }

    private static String canonicalAspect(String rawAspect) {
        return rawAspect == null ? "" : rawAspect.trim().toLowerCase(Locale.ROOT);
    }
}

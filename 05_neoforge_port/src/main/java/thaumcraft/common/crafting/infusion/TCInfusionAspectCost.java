package thaumcraft.common.crafting.infusion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public record TCInfusionAspectCost(String aspect, int amount) {
    public static final Codec<List<TCInfusionAspectCost>> MAP_CODEC = Codec.unboundedMap(
            Codec.STRING.comapFlatMap(TCInfusionAspectCost::validateAspect, value -> value),
            Codec.intRange(1, Integer.MAX_VALUE)
    ).comapFlatMap(TCInfusionAspectCost::fromMap, TCInfusionAspectCost::toMap);

    public TCInfusionAspectCost {
        aspect = canonicalAspect(aspect);
        if (Aspect.getAspect(aspect) == null) {
            throw new IllegalArgumentException("Unknown Thaumcraft aspect in infusion recipe: " + aspect);
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Infusion aspect cost must be positive");
        }
    }

    public Aspect resolvedAspect() {
        return Aspect.getAspect(aspect);
    }

    public static AspectList toAspectList(List<TCInfusionAspectCost> costs) {
        AspectList aspects = new AspectList();
        for (TCInfusionAspectCost cost : costs) {
            aspects.add(cost.resolvedAspect(), cost.amount());
        }
        return aspects;
    }

    private static DataResult<String> validateAspect(String rawAspect) {
        String aspect = canonicalAspect(rawAspect);
        return Aspect.getAspect(aspect) == null
                ? DataResult.error(() -> "Unknown Thaumcraft aspect in infusion recipe: " + rawAspect)
                : DataResult.success(aspect);
    }

    private static DataResult<List<TCInfusionAspectCost>> fromMap(Map<String, Integer> values) {
        if (values.isEmpty()) {
            return DataResult.error(() -> "Infusion recipe must define at least one aspect cost");
        }
        ArrayList<TCInfusionAspectCost> costs = new ArrayList<>(values.size());
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            costs.add(new TCInfusionAspectCost(entry.getKey(), entry.getValue()));
        }
        return DataResult.success(List.copyOf(costs));
    }

    private static Map<String, Integer> toMap(List<TCInfusionAspectCost> costs) {
        LinkedHashMap<String, Integer> values = new LinkedHashMap<>();
        for (TCInfusionAspectCost cost : costs) {
            values.put(cost.aspect(), cost.amount());
        }
        return values;
    }

    private static String canonicalAspect(String rawAspect) {
        return rawAspect == null ? "" : rawAspect.trim().toLowerCase(Locale.ROOT);
    }
}

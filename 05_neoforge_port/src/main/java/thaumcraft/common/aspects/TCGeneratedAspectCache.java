package thaumcraft.common.aspects;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.AspectList;

final class TCGeneratedAspectCache {
    private static volatile Map<TCAspectStackKey, AspectList> generatedObjectTags = Map.of();

    static AspectList get(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        TCAspectStackKey exactKey = TCAspectStackKey.from(stack);
        AspectList aspects = generatedObjectTags.get(exactKey);
        if (aspects == null) {
            TCAspectStackKey legacyBaseKey = TCAspectStackKey.from(TCAspectStackRules.generatedLookupBaseStack(stack));
            if (!legacyBaseKey.equals(exactKey)) {
                aspects = generatedObjectTags.get(legacyBaseKey);
            }
        }
        return aspects == null ? null : aspects.copy();
    }

    static int size() {
        return generatedObjectTags.size();
    }

    static void clear() {
        generatedObjectTags = Map.of();
    }

    static void replaceGeneratedObjectTags(Map<TCAspectStackKey, AspectList> values) {
        generatedObjectTags = copy(values);
    }

    static Map<TCAspectStackKey, AspectList> snapshot() {
        return copy(generatedObjectTags);
    }

    static void replaceForValidation(Map<TCAspectStackKey, AspectList> values) {
        generatedObjectTags = copy(values);
    }

    private static Map<TCAspectStackKey, AspectList> copy(Map<TCAspectStackKey, AspectList> values) {
        LinkedHashMap<TCAspectStackKey, AspectList> copy = new LinkedHashMap<>();
        for (Map.Entry<TCAspectStackKey, AspectList> entry : values.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().copy());
        }
        return Collections.unmodifiableMap(copy);
    }

    private TCGeneratedAspectCache() {
    }
}

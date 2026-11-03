package thaumcraft.common.golems.seals;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public final class TCSealItemIdentity {
    private static final Map<String, Set<String>> FAMILIES = load();
    private TCSealItemIdentity() {}

    public static boolean matchesWithoutMetadata(ItemStack a, ItemStack b) {
        if (a.isEmpty() || b.isEmpty()) return false;
        var component = thaumcraft.common.registry.TCDataComponents.LEGACY_ITEM.get();
        var leftLegacy = a.get(component);
        var rightLegacy = b.get(component);
        boolean same = a.getItem() == b.getItem() || sameLegacyItem(a, b)
                || (leftLegacy != null && rightLegacy != null
                && BuiltInRegistries.ITEM.getKey(a.getItem()).getNamespace().equals(BuiltInRegistries.ITEM.getKey(b.getItem()).getNamespace())
                && leftLegacy.family().equals(rightLegacy.family()));
        if (!same) return false;
        ItemStack left = a.copy(), right = b.copy();
        left.remove(net.minecraft.core.component.DataComponents.DAMAGE);
        right.remove(net.minecraft.core.component.DataComponents.DAMAGE);
        left.remove(component);
        right.remove(component);
        return left.getComponentsPatch().equals(right.getComponentsPatch());
    }

    static boolean sameLegacyItem(ItemStack a, ItemStack b) {
        Set<String> left = FAMILIES.getOrDefault(BuiltInRegistries.ITEM.getKey(a.getItem()).toString(), Set.of());
        Set<String> right = FAMILIES.getOrDefault(BuiltInRegistries.ITEM.getKey(b.getItem()).toString(), Set.of());
        return left.stream().anyMatch(right::contains);
    }

    static boolean belongs(ItemStack stack, String legacyId) {
        return FAMILIES.getOrDefault(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), Set.of()).contains(legacyId);
    }

    private static Map<String, Set<String>> load() {
        var stream = TCSealItemIdentity.class.getResourceAsStream("/data/thaumcraft/golem_legacy_item_families.json");
        if (stream == null) throw new IllegalStateException("Missing DataFixer-derived seal item identities");
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            var data = JsonParser.parseReader(reader).getAsJsonObject();
            if (data.get("source_data_version").getAsInt() != 1343
                    || data.get("target_data_version").getAsInt() != net.minecraft.SharedConstants.getCurrentVersion().getDataVersion().getVersion())
                throw new IllegalStateException("Seal item identities need regeneration for this Minecraft version");
            Map<String, Set<String>> result = new HashMap<>();
            for (var family : data.getAsJsonObject("families").entrySet()) for (var item : family.getValue().getAsJsonArray())
                result.computeIfAbsent(item.getAsString(), key -> new HashSet<>()).add(family.getKey());
            Map<String, Set<String>> immutable = new HashMap<>();
            result.forEach((key, value) -> immutable.put(key, Set.copyOf(value)));
            return Map.copyOf(immutable);
        } catch (java.io.IOException exception) { throw new IllegalStateException("Cannot read seal item identities", exception); }
    }
}

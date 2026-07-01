package thaumcraft.common.items.casters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.items.components.TCFocusPackageComponent;
import thaumcraft.common.registry.TCDataComponents;

public final class TCFocusPackageHelper {
    private TCFocusPackageHelper() {
    }

    public static TCFocusPackageComponent getPackage(ItemStack focusStack) {
        TCFocusPackageComponent component = focusStack.get(TCDataComponents.FOCUS_PACKAGE.get());
        return component == null ? TCFocusPackageComponent.EMPTY : component;
    }

    public static void setPackage(ItemStack focusStack, TCFocusPackageComponent packageData) {
        if (packageData == null || packageData.isEmpty()) {
            focusStack.remove(TCDataComponents.FOCUS_PACKAGE.get());
            return;
        }
        focusStack.set(TCDataComponents.FOCUS_PACKAGE.get(), packageData);
    }

    public static boolean hasPackage(ItemStack focusStack) {
        return !getPackage(focusStack).isEmpty();
    }

    public static float getVisCost(TCFocusPackageComponent packageData) {
        return packageData == null ? 0.0F : packageData.complexity() / 5.0F;
    }

    public static int getActivationTime(TCFocusPackageComponent packageData) {
        if (packageData == null || packageData.isEmpty()) {
            return 0;
        }
        int complexity = packageData.complexity();
        return Math.max(5, complexity / 5 * (complexity / 4));
    }

    public static TCFocusPackageComponent buildPackage(List<NodeInstance> nodes) {
        List<NodeInstance> normalized = normalize(nodes);
        String encoded = encode(normalized);
        int complexity = computeComplexity(normalized);
        int color = computeColor(normalized);
        int sortingHash = computeSortingHash(normalized);
        return new TCFocusPackageComponent(encoded, complexity, color, sortingHash);
    }

    public static List<NodeInstance> decode(String encoded) {
        ArrayList<NodeInstance> nodes = new ArrayList<>();
        if (encoded == null || encoded.isBlank()) {
            return List.of();
        }
        String[] rows = encoded.split(";", -1);
        for (String row : rows) {
            if (row.isBlank()) {
                continue;
            }
            String[] parts = row.split("\\|", -1);
            if (parts.length < 5) {
                continue;
            }
            int id = parseInt(parts[0], 0);
            String key = TCFocusElements.normalizeKey(parts[1]);
            int parent = parseInt(parts[2], -1);
            List<Integer> children = parseChildren(parts[3]);
            Map<String, Integer> settings = parseSettings(parts[4]);
            nodes.add(new NodeInstance(id, key, parent, children, settings));
        }
        return normalize(nodes);
    }

    public static String encode(List<NodeInstance> nodes) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (NodeInstance node : normalize(nodes)) {
            if (!first) {
                builder.append(';');
            }
            first = false;
            builder.append(node.id())
                    .append('|')
                    .append(TCFocusElements.normalizeKey(node.key()))
                    .append('|')
                    .append(node.parent())
                    .append('|')
                    .append(encodeChildren(node.children()))
                    .append('|')
                    .append(encodeSettings(node));
        }
        return builder.toString();
    }

    public static Map<String, Integer> crystalCosts(TCFocusPackageComponent packageData) {
        LinkedHashMap<String, Integer> costs = new LinkedHashMap<>();
        for (NodeInstance node : decode(packageData == null ? "" : packageData.nodes())) {
            TCFocusElements.get(node.key()).ifPresent(definition -> {
                if (definition.hasAspect()) {
                    costs.merge(definition.aspect(), 1, Integer::sum);
                }
            });
        }
        return costs;
    }

    public static String sortingHelper(ItemStack focusStack) {
        String display = focusStack.getHoverName().getString();
        TCFocusPackageComponent packageData = getPackage(focusStack);
        int hash = packageData.isEmpty() ? 0 : packageData.sortingHash();
        return display + hash;
    }

    public static ItemStack focusStack(String itemId, String customName, TCFocusPackageComponent packageData) {
        ResourceLocation id = ResourceLocation.parse(itemId);
        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(id));
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (packageData != null && !packageData.isEmpty()) {
            setPackage(stack, packageData);
        }
        if (customName != null && !customName.isBlank()) {
            stack.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal(customName));
        }
        return stack;
    }

    private static List<NodeInstance> normalize(List<NodeInstance> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return List.of();
        }
        ArrayList<NodeInstance> copy = new ArrayList<>();
        for (NodeInstance node : nodes) {
            if (node == null || TCFocusElements.normalizeKey(node.key()).isBlank()) {
                continue;
            }
            copy.add(new NodeInstance(
                    Math.max(0, node.id()),
                    TCFocusElements.normalizeKey(node.key()),
                    node.parent(),
                    List.copyOf(node.children()),
                    Map.copyOf(node.settings())
            ));
        }
        copy.sort(Comparator.comparingInt(NodeInstance::id));
        return List.copyOf(copy);
    }

    private static int computeComplexity(List<NodeInstance> nodes) {
        HashMap<String, Integer> counts = new HashMap<>();
        int total = 0;
        for (NodeInstance node : nodes) {
            TCFocusElementDefinition definition = TCFocusElements.get(node.key()).orElse(null);
            if (definition == null) {
                continue;
            }
            int count = counts.getOrDefault(definition.key(), 0) + 1;
            counts.put(definition.key(), count);
            float multiplier = 0.5F * (count + 1);
            total += (int)(definition.complexity(node.settings()) * multiplier);
        }
        return Math.max(0, total);
    }

    private static int computeColor(List<NodeInstance> nodes) {
        int red = 0;
        int green = 0;
        int blue = 0;
        int effects = 0;
        for (NodeInstance node : nodes) {
            TCFocusElementDefinition definition = TCFocusElements.get(node.key()).orElse(null);
            if (definition == null || !definition.contributesFocusColor()) {
                continue;
            }
            red += (definition.color() >> 16) & 0xFF;
            green += (definition.color() >> 8) & 0xFF;
            blue += definition.color() & 0xFF;
            effects++;
        }
        if (effects <= 0) {
            return 0xFFFFFF;
        }
        return ((red / effects) << 16) | ((green / effects) << 8) | (blue / effects);
    }

    private static int computeSortingHash(List<NodeInstance> nodes) {
        StringBuilder builder = new StringBuilder();
        for (NodeInstance node : nodes) {
            TCFocusElements.get(node.key()).ifPresent(definition -> {
                builder.append(legacyKey(definition.key()));
                for (String setting : definition.settingOrder()) {
                    builder.append(node.settings().getOrDefault(setting, defaultSetting(setting, definition.key())));
                }
            });
        }
        return builder.toString().hashCode();
    }

    private static String legacyKey(String normalizedKey) {
        if ("root".equals(normalizedKey)) {
            return "ROOT";
        }
        int dot = normalizedKey.indexOf('.');
        if (dot < 0) {
            return normalizedKey;
        }
        return normalizedKey.substring(0, dot + 1).toLowerCase(Locale.ROOT)
                + normalizedKey.substring(dot + 1).toUpperCase(Locale.ROOT);
    }

    private static int defaultSetting(String setting, String definitionKey) {
        return switch (setting) {
            case "power" -> 1;
            case "duration" -> switch (definitionKey) {
                case "thaumcraft.fire" -> 0;
                case "thaumcraft.cloud" -> 5;
                case "thaumcraft.curse" -> 1;
                default -> 2;
            };
            case "radius" -> 1;
            case "depth" -> 8;
            case "forks" -> 2;
            case "cone" -> 10;
            case "speed" -> 1;
            default -> 0;
        };
    }

    private static String encodeChildren(List<Integer> children) {
        if (children == null || children.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < children.size(); index++) {
            if (index > 0) {
                builder.append(',');
            }
            builder.append(children.get(index));
        }
        return builder.toString();
    }

    private static List<Integer> parseChildren(String encoded) {
        if (encoded == null || encoded.isBlank()) {
            return List.of();
        }
        ArrayList<Integer> children = new ArrayList<>();
        for (String value : encoded.split(",", -1)) {
            if (!value.isBlank()) {
                children.add(parseInt(value, 0));
            }
        }
        return List.copyOf(children);
    }

    private static String encodeSettings(NodeInstance node) {
        TCFocusElementDefinition definition = TCFocusElements.get(node.key()).orElse(null);
        if (definition == null || definition.settingOrder().isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String key : definition.settingOrder()) {
            if (!first) {
                builder.append(',');
            }
            first = false;
            builder.append(key).append('=').append(node.settings().getOrDefault(key, defaultSetting(key, definition.key())));
        }
        return builder.toString();
    }

    private static Map<String, Integer> parseSettings(String encoded) {
        if (encoded == null || encoded.isBlank()) {
            return Map.of();
        }
        LinkedHashMap<String, Integer> settings = new LinkedHashMap<>();
        for (String entry : encoded.split(",", -1)) {
            if (entry.isBlank()) {
                continue;
            }
            String[] parts = entry.split("=", 2);
            if (parts.length == 2) {
                settings.put(parts[0].trim(), parseInt(parts[1], 0));
            }
        }
        return Map.copyOf(settings);
    }

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    public record NodeInstance(int id, String key, int parent, List<Integer> children, Map<String, Integer> settings) {
        public NodeInstance {
            key = TCFocusElements.normalizeKey(key);
            children = children == null ? List.of() : List.copyOf(children);
            settings = settings == null ? Map.of() : Map.copyOf(settings);
        }
    }
}

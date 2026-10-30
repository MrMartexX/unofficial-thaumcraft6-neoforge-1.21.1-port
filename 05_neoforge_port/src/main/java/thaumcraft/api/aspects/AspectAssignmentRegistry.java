package thaumcraft.api.aspects;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Runtime aspect overrides for scripts and integration addons. */
public final class AspectAssignmentRegistry {
    private static final Map<ResourceLocation, AspectList> ITEMS = new ConcurrentHashMap<>();
    private static final Map<TagKey<Item>, AspectList> TAGS = new ConcurrentHashMap<>();

    public static void set(ResourceLocation itemId, AspectList aspects) {
        if (itemId == null || aspects == null) {
            throw new IllegalArgumentException("Item id and aspect list are required");
        }
        ITEMS.put(itemId, aspects.copy());
    }

    public static void set(TagKey<Item> tag, AspectList aspects) {
        if (tag == null || aspects == null) {
            throw new IllegalArgumentException("Item tag and aspect list are required");
        }
        TAGS.put(tag, aspects.copy());
    }

    public static void setTag(ResourceLocation tagId, AspectList aspects) {
        set(TagKey.create(Registries.ITEM, tagId), aspects);
    }

    public static void remove(ResourceLocation itemId) {
        ITEMS.remove(itemId);
    }

    public static AspectList get(ResourceLocation itemId) {
        AspectList aspects = ITEMS.get(itemId);
        return aspects == null ? null : aspects.copy();
    }

    public static void removeTag(ResourceLocation tagId) {
        TAGS.remove(TagKey.create(Registries.ITEM, tagId));
    }

    public static AspectList getTag(ResourceLocation tagId) {
        AspectList aspects = TAGS.get(TagKey.create(Registries.ITEM, tagId));
        return aspects == null ? null : aspects.copy();
    }

    public static void clear() {
        ITEMS.clear();
        TAGS.clear();
    }

    public static AspectList getOverride(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        AspectList direct = ITEMS.get(id);
        if (direct != null) {
            return direct.copy();
        }
        for (Map.Entry<TagKey<Item>, AspectList> entry : TAGS.entrySet()) {
            if (stack.is(entry.getKey())) {
                return entry.getValue().copy();
            }
        }
        return null;
    }

    private AspectAssignmentRegistry() {
    }
}

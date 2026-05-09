package thaumcraft.common.aspects;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import thaumcraft.api.aspects.AspectList;

final class TCAspectAssignmentData {
    private final Map<ResourceLocation, AspectList> directObjectTags;
    private final Map<TagKey<Item>, AspectList> tagObjectTags;
    private final Map<ResourceLocation, AspectList> complexDirectObjectTags;
    private final Map<TagKey<Item>, AspectList> complexTagObjectTags;

    TCAspectAssignmentData(
            Map<ResourceLocation, AspectList> directObjectTags,
            Map<TagKey<Item>, AspectList> tagObjectTags,
            Map<ResourceLocation, AspectList> complexDirectObjectTags,
            Map<TagKey<Item>, AspectList> complexTagObjectTags) {
        this.directObjectTags = copyDirectMap(directObjectTags);
        this.tagObjectTags = copyTagMap(tagObjectTags);
        this.complexDirectObjectTags = copyDirectMap(complexDirectObjectTags);
        this.complexTagObjectTags = copyTagMap(complexTagObjectTags);
    }

    Map<ResourceLocation, AspectList> directObjectTags() {
        return directObjectTags;
    }

    Map<TagKey<Item>, AspectList> tagObjectTags() {
        return tagObjectTags;
    }

    Map<ResourceLocation, AspectList> complexDirectObjectTags() {
        return complexDirectObjectTags;
    }

    Map<TagKey<Item>, AspectList> complexTagObjectTags() {
        return complexTagObjectTags;
    }

    private static Map<ResourceLocation, AspectList> copyDirectMap(Map<ResourceLocation, AspectList> source) {
        LinkedHashMap<ResourceLocation, AspectList> copy = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, AspectList> entry : source.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().copy());
        }
        return Collections.unmodifiableMap(copy);
    }

    private static Map<TagKey<Item>, AspectList> copyTagMap(Map<TagKey<Item>, AspectList> source) {
        LinkedHashMap<TagKey<Item>, AspectList> copy = new LinkedHashMap<>();
        for (Map.Entry<TagKey<Item>, AspectList> entry : source.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().copy());
        }
        return Collections.unmodifiableMap(copy);
    }
}

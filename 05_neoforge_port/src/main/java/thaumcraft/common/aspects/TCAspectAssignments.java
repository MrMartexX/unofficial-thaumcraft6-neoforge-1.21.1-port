package thaumcraft.common.aspects;

import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectAssignmentRegistry;
import thaumcraft.api.aspects.AspectList;

public final class TCAspectAssignments {
    private static volatile TCAspectAssignmentData activeData = new TCAspectAssignmentData(Map.of(), Map.of(), Map.of(), Map.of());

    public static void bootstrap() {
        if (Aspect.aspects.size() != 37) {
            throw new IllegalStateException("Thaumcraft aspect bootstrap expected 37 aspects, found " + Aspect.aspects.size());
        }
        TCGeneratedAspectCache.clear();
        activeData = TCAspectAssignmentParser.loadBundledDefaults();
        TCAspectParityValidator.validate(false);
        Thaumcraft.LOGGER.info(
                "Thaumcraft aspect bootstrap initialized from bundled data: {} aspects, {} exact object assignments, {} tag assignments, {} complex exact assignments, {} complex tag assignments, parity validation passed.",
                Aspect.aspects.size(),
                activeData.directObjectTags().size(),
                activeData.tagObjectTags().size(),
                activeData.complexDirectObjectTags().size(),
                activeData.complexTagObjectTags().size());
    }

    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new TCAspectAssignmentReloadListener());
        TCGeneratedAspectRecipeGenerator.captureReloadContext(event);
    }

    static void reload(TCAspectAssignmentData data) {
        TCGeneratedAspectCache.clear();
        activeData = data;
        TCAspectParityValidator.validate(true);
        Thaumcraft.LOGGER.info(
                "Thaumcraft aspect assignments reloaded: {} exact object assignments, {} tag assignments, {} complex exact assignments, {} complex tag assignments, parity validation passed.",
                activeData.directObjectTags().size(),
                activeData.tagObjectTags().size(),
                activeData.complexDirectObjectTags().size(),
                activeData.complexTagObjectTags().size());
    }

    public static AspectList getObjectAspects(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        AspectList potionAspects = TCAspectStackRules.getPotionContentAspects(stack);
        if (potionAspects != null) {
            return potionAspects;
        }
        if (TCAspectStackRules.isLegacyNoAspectStack(stack)) {
            return new AspectList();
        }

        AspectList explicit = getExplicitObjectAspects(stack);
        AspectList base = explicit != null ? explicit : TCGeneratedAspectCache.get(stack);
        return TCAspectStackRules.applyStackBonuses(stack, base);
    }

    public static AspectList getScanAspects(ItemStack stack) {
        return TCAspectStackRules.applyLegacyScanAspectQuirks(stack, getObjectAspects(stack));
    }

    public static AspectList getGeneratedObjectAspects(ItemStack stack) {
        return TCGeneratedAspectCache.get(stack);
    }

    static AspectList getExplicitObjectAspects(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        AspectList runtimeOverride = AspectAssignmentRegistry.getOverride(stack);
        if (runtimeOverride != null) {
            return runtimeOverride;
        }

        TCAspectAssignmentData data = activeData;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        AspectList aspects = data.directObjectTags().get(id);
        if (aspects != null) {
            return aspects.copy();
        }

        for (Map.Entry<TagKey<Item>, AspectList> entry : data.tagObjectTags().entrySet()) {
            if (stack.is(entry.getKey())) {
                return entry.getValue().copy();
            }
        }

        return null;
    }

    static AspectList getComplexObjectAspects(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        TCAspectAssignmentData data = activeData;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        AspectList aspects = data.complexDirectObjectTags().get(id);
        if (aspects != null) {
            return aspects.copy();
        }

        for (Map.Entry<TagKey<Item>, AspectList> entry : data.complexTagObjectTags().entrySet()) {
            if (stack.is(entry.getKey())) {
                return entry.getValue().copy();
            }
        }

        return null;
    }

    public static Map<ResourceLocation, AspectList> directObjectTags() {
        return activeData.directObjectTags();
    }

    public static Map<TagKey<Item>, AspectList> tagObjectTags() {
        return activeData.tagObjectTags();
    }

    static Map<ResourceLocation, AspectList> complexDirectObjectTags() {
        return activeData.complexDirectObjectTags();
    }

    static Map<TagKey<Item>, AspectList> complexTagObjectTags() {
        return activeData.complexTagObjectTags();
    }

    private TCAspectAssignments() {
    }
}

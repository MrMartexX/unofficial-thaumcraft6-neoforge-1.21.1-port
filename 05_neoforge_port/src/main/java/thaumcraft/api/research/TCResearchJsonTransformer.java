package thaumcraft.api.research;

import com.google.gson.JsonElement;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Allows addons and pack tools to transform the mutable research JSON set
 * immediately before Thaumcraft parses it on a resource reload.
 */
@FunctionalInterface
public interface TCResearchJsonTransformer {
    void transform(Map<ResourceLocation, JsonElement> researchFiles, ResourceManager resourceManager);
}


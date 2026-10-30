package thaumcraft.common.research;

import com.google.gson.JsonElement;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import thaumcraft.api.research.TCResearchJsonTransformers;

final class TCResearchReloadListener extends SimpleJsonResourceReloadListener {
    TCResearchReloadListener() {
        super(TCResearchParser.GSON, TCResearchParser.DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profiler) {
        LinkedHashMap<ResourceLocation, JsonElement> transformed = new LinkedHashMap<>();
        files.forEach((id, json) -> transformed.put(id, json.deepCopy()));
        TCResearchJsonTransformers.apply(transformed, resourceManager);
        TCResearchManager.reload(TCResearchParser.parse(transformed));
    }
}

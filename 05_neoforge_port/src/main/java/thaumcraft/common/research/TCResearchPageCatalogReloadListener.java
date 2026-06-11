package thaumcraft.common.research;

import com.google.gson.JsonElement;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

final class TCResearchPageCatalogReloadListener extends SimpleJsonResourceReloadListener {
    TCResearchPageCatalogReloadListener() {
        super(TCResearchPageCatalogParser.GSON, TCResearchPageCatalogParser.DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profiler) {
        TCResearchPageCatalogManager.reload(TCResearchPageCatalogParser.parse(files));
    }
}

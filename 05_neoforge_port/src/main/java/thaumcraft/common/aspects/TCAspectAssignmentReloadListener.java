package thaumcraft.common.aspects;

import com.google.gson.JsonElement;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

final class TCAspectAssignmentReloadListener extends SimpleJsonResourceReloadListener {
    TCAspectAssignmentReloadListener() {
        super(TCAspectAssignmentParser.GSON, TCAspectAssignmentParser.DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profiler) {
        TCAspectAssignments.reload(TCAspectAssignmentParser.parse(files));
    }
}

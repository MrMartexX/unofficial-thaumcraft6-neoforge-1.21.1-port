package thaumcraft.common.research;

import com.google.gson.JsonElement;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

final class TCScannableReloadListener extends SimpleJsonResourceReloadListener {
    TCScannableReloadListener() {
        super(TCScannableParser.GSON, TCScannableParser.DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profiler) {
        TCScanningManager.reload(TCScannableParser.parse(files));
    }
}

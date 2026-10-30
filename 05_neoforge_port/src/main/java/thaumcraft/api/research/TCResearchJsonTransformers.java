package thaumcraft.api.research;

import com.google.gson.JsonElement;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

/** Thread-safe registration point for research JSON reload transforms. */
public final class TCResearchJsonTransformers {
    private static final CopyOnWriteArrayList<TCResearchJsonTransformer> TRANSFORMERS =
            new CopyOnWriteArrayList<>();

    private TCResearchJsonTransformers() { }

    public static void register(TCResearchJsonTransformer transformer) {
        TCResearchJsonTransformer checked = Objects.requireNonNull(transformer, "transformer");
        if (!TRANSFORMERS.contains(checked)) {
            TRANSFORMERS.add(checked);
        }
    }

    public static void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager) {
        for (TCResearchJsonTransformer transformer : TRANSFORMERS) {
            transformer.transform(files, resourceManager);
        }
    }
}


package thaumcraft.api.crafting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.UseOnContext;

/** Thread-safe registry used by addon ports to contribute Salis Mundus activations. */
public final class SalisMundusTriggerRegistry {
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final Comparator<Entry> ORDER = Comparator
            .comparingInt(Entry::priority).reversed()
            .thenComparing(entry -> entry.id().toString());

    private SalisMundusTriggerRegistry() {
    }

    public static synchronized void register(ResourceLocation id, int priority, IDustTrigger trigger) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(trigger, "trigger");
        ENTRIES.removeIf(entry -> entry.id().equals(id));
        ENTRIES.add(new Entry(id, priority, trigger));
        ENTRIES.sort(ORDER);
    }

    public static synchronized boolean unregister(ResourceLocation id) {
        return ENTRIES.removeIf(entry -> entry.id().equals(id));
    }

    public static synchronized List<ResourceLocation> registeredIds() {
        return ENTRIES.stream().map(Entry::id).toList();
    }

    public static IDustTrigger.DustTriggerResult tryActivate(UseOnContext context) {
        List<Entry> snapshot;
        synchronized (SalisMundusTriggerRegistry.class) {
            snapshot = List.copyOf(ENTRIES);
        }
        for (Entry entry : snapshot) {
            IDustTrigger.DustTriggerResult result = entry.trigger().tryActivate(context);
            if (result != null && result.matched()) {
                return result;
            }
        }
        return IDustTrigger.DustTriggerResult.none();
    }

    private record Entry(ResourceLocation id, int priority, IDustTrigger trigger) {
    }
}

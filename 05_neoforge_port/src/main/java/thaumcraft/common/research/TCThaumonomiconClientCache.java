package thaumcraft.common.research;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public final class TCThaumonomiconClientCache {
    private static TCThaumonomiconIndexPayload index = new TCThaumonomiconIndexPayload(java.util.List.of(), java.util.List.of());
    private static Map<String, TCThaumonomiconEntryView> entries = Map.of();
    private static TCThaumonomiconEntryPayload lastEntryResult;
    private static TCThaumonomiconDrilldownPayload lastDrilldownResult;
    private static boolean openRequested;

    private TCThaumonomiconClientCache() {
    }

    public static void accept(TCThaumonomiconIndexPayload payload) {
        index = payload == null
                ? new TCThaumonomiconIndexPayload(java.util.List.of(), java.util.List.of())
                : payload;
        entries = Map.of();
        lastEntryResult = null;
        lastDrilldownResult = null;
        openRequested |= payload != null && payload.openScreen();
    }

    public static void accept(TCThaumonomiconEntryPayload payload) {
        if (payload == null) {
            return;
        }
        TreeMap<String, TCThaumonomiconEntryView> updated = new TreeMap<>(entries);
        if (payload.entry().isPresent()) {
            updated.put(payload.researchKey(), payload.entry().get());
        } else {
            updated.remove(payload.researchKey());
        }
        entries = Map.copyOf(updated);
        lastEntryResult = payload;
    }

    public static void accept(TCThaumonomiconDrilldownPayload payload) {
        if (payload == null) {
            return;
        }
        lastDrilldownResult = payload;
    }

    public static TCThaumonomiconIndexPayload index() {
        return index;
    }

    public static int revision() {
        return index.revision();
    }

    public static Optional<TCThaumonomiconEntryView> entry(String researchKey) {
        return Optional.ofNullable(entries.get(TCPlayerKnowledge.baseResearchKey(researchKey)));
    }

    public static TCThaumonomiconEntryPayload pollLastEntryResult() {
        TCThaumonomiconEntryPayload result = lastEntryResult;
        lastEntryResult = null;
        return result;
    }

    public static TCThaumonomiconDrilldownPayload pollLastDrilldownResult() {
        TCThaumonomiconDrilldownPayload result = lastDrilldownResult;
        lastDrilldownResult = null;
        return result;
    }

    public static boolean pollOpenRequested() {
        boolean requested = openRequested;
        openRequested = false;
        return requested;
    }

    public static void clear() {
        index = new TCThaumonomiconIndexPayload(java.util.List.of(), java.util.List.of());
        entries = Map.of();
        lastEntryResult = null;
        lastDrilldownResult = null;
        openRequested = false;
    }
}

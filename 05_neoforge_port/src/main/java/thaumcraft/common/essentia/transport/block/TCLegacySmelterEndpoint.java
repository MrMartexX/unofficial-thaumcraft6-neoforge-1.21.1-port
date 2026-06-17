package thaumcraft.common.essentia.transport.block;

import thaumcraft.common.essentia.transport.TCEssentiaTubeMode;

import java.util.Locale;
import java.util.Optional;

public enum TCLegacySmelterEndpoint {
    THAUMIUM("smelter_thaumium", TCEssentiaTubeMode.SMELTER_THAUMIUM, 64),
    VOID("smelter_void", TCEssentiaTubeMode.SMELTER_VOID, 64);

    private final String catalogId;
    private final TCEssentiaTubeMode mode;
    private final int storageCapacity;

    TCLegacySmelterEndpoint(String catalogId, TCEssentiaTubeMode mode, int storageCapacity) {
        this.catalogId = catalogId;
        this.mode = mode;
        this.storageCapacity = storageCapacity;
    }

    public String catalogId() {
        return catalogId;
    }

    public TCEssentiaTubeMode mode() {
        return mode;
    }

    public int storageCapacity() {
        return storageCapacity;
    }

    public static Optional<TCLegacySmelterEndpoint> fromCatalogId(String catalogId) {
        if (catalogId == null || catalogId.isBlank()) return Optional.empty();
        String normalized = catalogId.toLowerCase(Locale.ROOT).replace("thaumcraft:", "");
        for (TCLegacySmelterEndpoint endpoint : values()) {
            if (endpoint.catalogId.equals(normalized)) {
                return Optional.of(endpoint);
            }
        }
        return Optional.empty();
    }
}

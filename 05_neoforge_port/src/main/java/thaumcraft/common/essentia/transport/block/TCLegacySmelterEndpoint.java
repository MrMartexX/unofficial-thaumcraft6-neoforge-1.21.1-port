package thaumcraft.common.essentia.transport.block;

import java.util.Locale;
import java.util.Optional;

public enum TCLegacySmelterEndpoint {
    THAUMIUM("smelter_thaumium"),
    VOID("smelter_void");

    private final String catalogId;

    TCLegacySmelterEndpoint(String catalogId) {
        this.catalogId = catalogId;
    }

    public String catalogId() {
        return catalogId;
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

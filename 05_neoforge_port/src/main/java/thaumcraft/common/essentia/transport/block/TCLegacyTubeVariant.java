package thaumcraft.common.essentia.transport.block;

import thaumcraft.common.essentia.transport.TCEssentiaTubeMode;

import java.util.Locale;
import java.util.Optional;

/**
 * Tube block variants mapped from legacy TC6 tube catalog ids.
 */
public enum TCLegacyTubeVariant {
    TUBE("tube", TCEssentiaTubeMode.NORMAL, 16),
    BUFFER("tubebuffer", TCEssentiaTubeMode.BUFFER, 128),
    FILTER("tubefilter", TCEssentiaTubeMode.FILTER, 16),
    ONEWAY("tubeoneway", TCEssentiaTubeMode.ONEWAY, 16),
    RESTRICT("tuberestrict", TCEssentiaTubeMode.RESTRICT, 16),
    VALVE("tubevalve", TCEssentiaTubeMode.VALVE, 16);

    private final String catalogId;
    private final TCEssentiaTubeMode mode;
    private final int storageCapacity;

    TCLegacyTubeVariant(String catalogId, TCEssentiaTubeMode mode, int storageCapacity) {
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

    public static Optional<TCLegacyTubeVariant> fromCatalogId(String catalogId) {
        if (catalogId == null || catalogId.isBlank()) return Optional.empty();
        String normalized = catalogId.toLowerCase(Locale.ROOT).replace("thaumcraft:", "");
        for (TCLegacyTubeVariant variant : values()) {
            if (variant.catalogId.equals(normalized)) {
                return Optional.of(variant);
            }
        }
        return Optional.empty();
    }
}
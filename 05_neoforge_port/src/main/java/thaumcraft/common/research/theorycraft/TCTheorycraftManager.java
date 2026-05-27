package thaumcraft.common.research.theorycraft;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class TCTheorycraftManager {
    private static final HashMap<String, Supplier<? extends TCTheorycraftCard>> CARDS = new HashMap<>();

    private TCTheorycraftManager() {
    }

    public static void bootstrap() {
        CARDS.clear();
        registerCard("thaumcraft.api.research.theorycraft.CardStudy", CardStudy::new);
        registerCard("thaumcraft.api.research.theorycraft.CardAnalyze", CardAnalyze::new);
        registerCard("thaumcraft.api.research.theorycraft.CardBalance", CardBalance::new);
        registerCard("thaumcraft.api.research.theorycraft.CardNotation", CardNotation::new);
        registerCard("thaumcraft.api.research.theorycraft.CardPonder", CardPonder::new);
        registerCard("thaumcraft.api.research.theorycraft.CardRethink", CardRethink::new);
        registerCard("thaumcraft.api.research.theorycraft.CardReject", CardReject::new);
        registerCard("thaumcraft.api.research.theorycraft.CardExperimentation", CardExperimentation::new);
        registerCard("thaumcraft.api.research.theorycraft.CardInspired", CardInspired::new);
    }

    public static void registerCard(String legacyKey, Supplier<? extends TCTheorycraftCard> factory) {
        if (legacyKey == null || legacyKey.isBlank() || factory == null) {
            return;
        }
        CARDS.put(legacyKey, factory);
    }

    static Map<String, Supplier<? extends TCTheorycraftCard>> cards() {
        return CARDS;
    }

    static TCTheorycraftCard create(String legacyKey) {
        Supplier<? extends TCTheorycraftCard> factory = CARDS.get(legacyKey);
        return factory == null ? null : factory.get();
    }
}

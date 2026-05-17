package thaumcraft.common.research;

import java.util.List;

record TCScannableData(List<TCScannableDefinition> definitions) {
    TCScannableData {
        definitions = List.copyOf(definitions);
    }

    static TCScannableData empty() {
        return new TCScannableData(List.of());
    }
}

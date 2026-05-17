package thaumcraft.common.research;

import java.util.List;
import thaumcraft.api.aspects.AspectList;

public record TCScanResult(
        boolean success,
        String objectKey,
        String displayName,
        AspectList aspects,
        boolean generatedFallback,
        List<String> researchKeys,
        boolean suppressMessage,
        String message
) {
    public TCScanResult {
        aspects = aspects == null ? new AspectList() : aspects;
        researchKeys = researchKeys == null ? List.of() : List.copyOf(researchKeys);
    }

    public static TCScanResult failure(String message) {
        return new TCScanResult(false, "", "", new AspectList(), false, List.of(), false, message);
    }

    public static TCScanResult success(
            String objectKey,
            String displayName,
            AspectList aspects,
            boolean generatedFallback,
            List<String> researchKeys
    ) {
        return success(objectKey, displayName, aspects, generatedFallback, researchKeys, false);
    }

    public static TCScanResult success(
            String objectKey,
            String displayName,
            AspectList aspects,
            boolean generatedFallback,
            List<String> researchKeys,
            boolean suppressMessage
    ) {
        return new TCScanResult(true, objectKey, displayName, aspects, generatedFallback, researchKeys, suppressMessage, "");
    }
}

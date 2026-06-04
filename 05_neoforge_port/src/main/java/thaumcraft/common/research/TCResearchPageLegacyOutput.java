package thaumcraft.common.research;

import net.minecraft.resources.ResourceLocation;

public record TCResearchPageLegacyOutput(
        ResourceLocation item,
        int metadata,
        int count,
        String nbt
) {
    public TCResearchPageLegacyOutput {
        count = Math.max(1, count);
        nbt = nbt == null ? "" : nbt;
    }
}

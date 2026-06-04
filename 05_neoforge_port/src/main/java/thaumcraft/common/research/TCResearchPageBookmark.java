package thaumcraft.common.research;

import java.util.List;
import net.minecraft.resources.ResourceLocation;

public record TCResearchPageBookmark(
        ResourceLocation id,
        List<TCResearchPageView> pages
) {
    public TCResearchPageBookmark {
        pages = List.copyOf(pages);
    }
}

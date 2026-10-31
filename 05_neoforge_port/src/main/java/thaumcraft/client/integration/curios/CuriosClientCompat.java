package thaumcraft.client.integration.curios;

import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.registry.TCItems;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public final class CuriosClientCompat {
    private static final ResourceLocation CURIOSITY_BAND_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/item/curiosity_band_worn.png");
    private static final ResourceLocation GOGGLES_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/item/goggles_bauble.png");

    private CuriosClientCompat() {
    }

    public static void registerRenderers() {
        CuriosRendererRegistry.register(
                TCItems.CURIOSITY_BAND.get(),
                () -> new HeadCurioRenderer(CURIOSITY_BAND_TEXTURE)
        );
        CuriosRendererRegistry.register(
                TCItems.GOGGLES.get(),
                () -> new HeadCurioRenderer(GOGGLES_TEXTURE)
        );
    }
}

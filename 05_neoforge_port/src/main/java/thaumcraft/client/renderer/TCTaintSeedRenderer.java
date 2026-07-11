package thaumcraft.client.renderer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCTaintSeedEntity;

/** Placeholder renderer registration for server-complete Taint Seed behavior. Full model parity is a later visual slice. */
final class TCTaintSeedRenderer extends EntityRenderer<TCTaintSeedEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/taintseed.png");

    TCTaintSeedRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.4F;
    }

    @Override
    public ResourceLocation getTextureLocation(TCTaintSeedEntity entity) {
        return TEXTURE;
    }
}

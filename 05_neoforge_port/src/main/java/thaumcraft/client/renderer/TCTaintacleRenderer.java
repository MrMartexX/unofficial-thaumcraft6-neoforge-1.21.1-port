package thaumcraft.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCTaintacleEntity;

final class TCTaintacleRenderer<T extends TCTaintacleEntity> extends MobRenderer<T, TCTaintacleModel<T>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/taintacle.png");

    TCTaintacleRenderer(EntityRendererProvider.Context context, int length, float shadow) {
        super(context, new TCTaintacleModel<>(length), shadow);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}

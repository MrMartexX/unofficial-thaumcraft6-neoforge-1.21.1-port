package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCTaintCrawlerEntity;

final class TCTaintCrawlerRenderer extends MobRenderer<TCTaintCrawlerEntity, SilverfishModel<TCTaintCrawlerEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/crawler.png");

    TCTaintCrawlerRenderer(EntityRendererProvider.Context context) {
        super(context, new SilverfishModel<>(context.bakeLayer(ModelLayers.SILVERFISH)), 0.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(TCTaintCrawlerEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(TCTaintCrawlerEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.7F, 0.7F, 0.7F);
    }
}

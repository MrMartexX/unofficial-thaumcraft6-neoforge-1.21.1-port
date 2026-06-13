package thaumcraft.client.renderers.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.monster.tainted.EntityTaintCrawler;

/**
 * Renderer for Taint Crawlers - small tainted insects.
 * Uses the silverfish model scaled down with a custom texture.
 */
@OnlyIn(Dist.CLIENT)
public class TaintCrawlerRenderer extends MobRenderer<EntityTaintCrawler, SilverfishModel<EntityTaintCrawler>> {
    
    private static final ResourceLocation TEXTURE = 
            new ResourceLocation(Thaumcraft.MODID, "textures/entity/crawler.png");
    
    public TaintCrawlerRenderer(EntityRendererProvider.Context context) {
        super(context, new SilverfishModel<>(context.bakeLayer(ModelLayers.SILVERFISH)), 0.2F);
    }
    
    @Override
    public ResourceLocation getTextureLocation(EntityTaintCrawler entity) {
        return TEXTURE;
    }
    
    @Override
    protected void scale(EntityTaintCrawler entity, PoseStack poseStack, float partialTicks) {
        poseStack.scale(0.7F, 0.7F, 0.7F);
    }
    
    @Override
    protected float getFlipDegrees(EntityTaintCrawler entity) {
        return 180.0F;
    }
}

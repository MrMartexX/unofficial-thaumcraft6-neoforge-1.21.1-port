package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCEldritchCrabEntity;

final class TCEldritchCrabRenderer extends MobRenderer<TCEldritchCrabEntity, SpiderModel<TCEldritchCrabEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/crab.png");

    TCEldritchCrabRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiderModel<>(context.bakeLayer(ModelLayers.SPIDER)), 0.35F);
    }

    @Override
    public ResourceLocation getTextureLocation(TCEldritchCrabEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(TCEldritchCrabEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.55F, 0.45F, 0.55F);
    }
}

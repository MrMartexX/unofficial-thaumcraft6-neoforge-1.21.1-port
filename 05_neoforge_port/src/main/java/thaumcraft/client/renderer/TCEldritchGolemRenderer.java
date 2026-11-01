package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.boss.TCEldritchGolemEntity;

public class TCEldritchGolemRenderer extends MobRenderer<TCEldritchGolemEntity, TCEldritchGolemModel> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "eldritch_golem"), "main");

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/eldritch_golem.png");

    public TCEldritchGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new TCEldritchGolemModel(TCEldritchGolemModel.createBodyLayer().bakeRoot()), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(TCEldritchGolemEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(TCEldritchGolemEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(1.7F, 1.7F, 1.7F);
    }
}

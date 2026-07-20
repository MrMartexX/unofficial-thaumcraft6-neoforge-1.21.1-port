package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCEldritchGuardianEntity;

final class TCEldritchGuardianRenderer extends HumanoidMobRenderer<TCEldritchGuardianEntity, HumanoidModel<TCEldritchGuardianEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/eldritch_guardian.png");

    TCEldritchGuardianRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(TCEldritchGuardianEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(TCEldritchGuardianEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(1.12F, 1.12F, 1.12F);
    }
}

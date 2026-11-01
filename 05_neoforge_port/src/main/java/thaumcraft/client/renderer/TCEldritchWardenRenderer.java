package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.boss.TCEldritchWardenEntity;

public class TCEldritchWardenRenderer extends HumanoidMobRenderer<TCEldritchWardenEntity, HumanoidModel<TCEldritchWardenEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/eldritch_warden.png");

    public TCEldritchWardenRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.75F);
    }

    @Override
    public ResourceLocation getTextureLocation(TCEldritchWardenEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(TCEldritchWardenEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(1.9F, 1.9F, 1.9F);
        if (entity.getSpawnTimer() > 0) {
            poseStack.translate(0.0D, - (entity.getSpawnTimer() / 150.0D) * 0.8D, 0.0D);
        }
    }
}

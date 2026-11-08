package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thaumcraft.Thaumcraft;
import thaumcraft.client.lib.TCRenderTypes;
import thaumcraft.common.entities.boss.TCEldritchWardenEntity;

/**
 * Authentic renderer for Eldritch Warden:
 * Solid boss figure, 1.9 scale, rising from ground during spawn timer,
 * and a glowing eye layer pulsing between 195 and 225 light.
 */
public class TCEldritchWardenRenderer extends MobRenderer<TCEldritchWardenEntity, TCEldritchGuardianModel<TCEldritchWardenEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/eldritch_warden.png");

    public TCEldritchWardenRenderer(EntityRendererProvider.Context context) {
        super(context, new TCEldritchGuardianModel<>(), 0.75F);
        addLayer(new WardenEyeLayer(this));
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

    static class WardenEyeLayer extends RenderLayer<TCEldritchWardenEntity, TCEldritchGuardianModel<TCEldritchWardenEntity>> {
        WardenEyeLayer(RenderLayerParent<TCEldritchWardenEntity, TCEldritchGuardianModel<TCEldritchWardenEntity>> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack pose, MultiBufferSource buffer, int light, TCEldritchWardenEntity entity,
                           float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                           float netHeadYaw, float headPitch) {
            if (!entity.isInvisible()) {
                int glowLight = (int) (195.0f + Mth.sin(entity.tickCount / 3.0f) * 15.0f + 15.0f);
                getParentModel().renderEye(pose, buffer.getBuffer(TCRenderTypes.entityGlow(TEXTURE)), glowLight, OverlayTexture.NO_OVERLAY);
            }
        }
    }
}

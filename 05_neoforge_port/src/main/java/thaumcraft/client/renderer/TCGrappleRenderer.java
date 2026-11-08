package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCGrappleEntity;

/**
 * Authentic hook renderer for Grapple projectile.
 */
public class TCGrappleRenderer extends EntityRenderer<TCGrappleEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/grappler.png");
    private final TCModelGrappler model = new TCModelGrappler();

    public TCGrappleRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(TCGrappleEntity entity, float entityYaw, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        pose.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        model.render(pose, buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        pose.popPose();
        super.render(entity, entityYaw, partialTicks, pose, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TCGrappleEntity entity) {
        return TEXTURE;
    }
}

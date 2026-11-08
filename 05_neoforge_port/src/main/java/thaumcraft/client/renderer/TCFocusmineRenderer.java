package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCFocusmineEntity;

/**
 * Authentic renderer for Focus Mine:
 * Rotating, armed blinking grappler model, full-bright.
 */
public class TCFocusmineRenderer extends EntityRenderer<TCFocusmineEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/mine.png");
    private final TCModelGrappler model = new TCModelGrappler();

    public TCFocusmineRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(TCFocusmineEntity mine, float entityYaw, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        pose.pushPose();
        float f = (mine.tickCount + partialTicks) % 8.0F / 8.0F;
        pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, mine.yRotO, mine.getYRot()) - 90.0F));
        pose.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, mine.xRotO, mine.getXRot())));
        int color = FastColor.ARGB32.colorFromFloat(1.0F, 1.0F, 1.0F - f, 1.0F - f);
        model.render(pose, buffer.getBuffer(RenderType.entityTranslucent(TEXTURE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);
        pose.popPose();
        super.render(mine, entityYaw, partialTicks, pose, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TCFocusmineEntity entity) {
        return TEXTURE;
    }
}

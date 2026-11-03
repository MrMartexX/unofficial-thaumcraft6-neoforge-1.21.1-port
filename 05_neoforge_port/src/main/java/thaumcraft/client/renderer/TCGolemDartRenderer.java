package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.common.entities.TCGolemDartEntity;

public final class TCGolemDartRenderer extends ArrowRenderer<TCGolemDartEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/arrow.png");
    public TCGolemDartRenderer(EntityRendererProvider.Context context) { super(context); }
    @Override public ResourceLocation getTextureLocation(TCGolemDartEntity entity) { return TEXTURE; }
    @Override public void render(TCGolemDartEntity dart, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        pose.pushPose();
        float scale = .033F / .05625F;
        pose.scale(scale, scale, scale);
        super.render(dart, yaw, partial, pose, buffers, light);
        pose.popPose();
    }
}

package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.entities.TCFluxRiftEntity;

final class TCFluxRiftRenderer extends EntityRenderer<TCFluxRiftEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/end_portal.png");

    TCFluxRiftRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.0F;
    }

    @Override
    public void render(TCFluxRiftEntity rift, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        List<Vec3> points = rift.renderPoints();
        List<Float> widths = rift.renderWidths();
        if (points.size() < 2 || widths.size() != points.size()) {
            return;
        }
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        for (int index = 0; index < points.size() - 1; index++) {
            Vec3 first = points.get(index);
            Vec3 second = points.get(index + 1);
            float width = Math.max(widths.get(index), widths.get(index + 1)) * 6.0F + 0.02F;
            addSegment(consumer, pose, first, second, width, 0.25F + index * 0.05F);
        }
        super.render(rift, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TCFluxRiftEntity entity) {
        return TEXTURE;
    }

    private static void addSegment(VertexConsumer consumer, PoseStack.Pose pose, Vec3 first, Vec3 second, float width, float u) {
        Vec3 direction = second.subtract(first);
        Vec3 normal = new Vec3(-direction.z, direction.y * 0.25D + 0.25D, direction.x);
        if (normal.lengthSqr() < 1.0E-6D) {
            normal = new Vec3(0.0D, 1.0D, 0.0D);
        }
        normal = normal.normalize().scale(width);
        vertex(consumer, pose, first.add(normal), u, 0.0F);
        vertex(consumer, pose, second.add(normal), u + 0.3F, 0.0F);
        vertex(consumer, pose, second.subtract(normal), u + 0.3F, 1.0F);
        vertex(consumer, pose, first.subtract(normal), u, 1.0F);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, Vec3 point, float u, float v) {
        consumer.addVertex(pose.pose(), (float) point.x, (float) point.y, (float) point.z)
                .setColor(180, 70, 255, 180)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}

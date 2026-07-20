package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCTaintacleEntity;

final class TCTaintacleRenderer<T extends TCTaintacleEntity> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/taintacle.png");

    private final ModelPart root;
    private final List<ModelPart> segments;
    private final int length;

    TCTaintacleRenderer(EntityRendererProvider.Context context, int length, float shadow) {
        super(context);
        this.length = length;
        shadowRadius = shadow;
        root = createLegacyFoundationModel(length).bakeRoot();
        segments = new ArrayList<>();
        for (int index = 0; index < length; index++) {
            segments.add(root.getChild("segment_" + index));
        }
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        animate(entity, partialTick);
        poseStack.pushPose();
        poseStack.translate(0.0F, 1.5F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
        float scale = Math.max(0.045F, entity.getBbHeight() / Math.max(1.0F, length * 8.0F));
        poseStack.scale(-scale, -scale, scale);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE));
        root.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    private void animate(T entity, float partialTick) {
        float age = entity.tickCount + partialTick;
        float flail = entity.flailIntensity();
        for (int index = 0; index < segments.size(); index++) {
            ModelPart segment = segments.get(index);
            segment.resetPose();
            float offset = age * 0.08F + index * 0.65F;
            segment.xRot = Mth.sin(offset) * 0.08F * flail;
            segment.yRot = Mth.cos(offset * 0.9F) * 0.18F * flail;
            segment.zRot = Mth.sin(offset * 0.7F) * 0.08F * flail;
        }
    }

    private static LayerDefinition createLegacyFoundationModel(int length) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        for (int index = 0; index < length; index++) {
            float scale = Math.max(0.45F, 1.0F - index * 0.035F);
            float half = 4.0F * scale;
            root.addOrReplaceChild(
                    "segment_" + index,
                    CubeListBuilder.create()
                            .texOffs((index % 4) * 12, 0)
                            .addBox(-half, -8.0F - index * 8.0F, -half, half * 2.0F, 8.0F, half * 2.0F),
                    PartPose.ZERO
            );
        }
        return LayerDefinition.create(mesh, 64, 64);
    }
}

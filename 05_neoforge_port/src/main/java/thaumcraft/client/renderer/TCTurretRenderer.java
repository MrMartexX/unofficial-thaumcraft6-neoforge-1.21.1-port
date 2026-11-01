package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.construct.TCTurretCrossbowEntity;

public class TCTurretRenderer<T extends TCTurretCrossbowEntity> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE_BASIC =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/crossbow.png");
    private static final ResourceLocation TEXTURE_ADVANCED =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/crossbow_advanced.png");

    private final ModelPart tripod;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;
    private final ModelPart head;

    public TCTurretRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        ModelPart root = createLegacyCrossbowModel().bakeRoot();
        this.tripod = root.getChild("tripod");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
        this.leg3 = root.getChild("leg3");
        this.leg4 = root.getChild("leg4");
        this.head = root.getChild("head");
    }

    @Override
    public void render(T turret, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        ResourceLocation texture = getTextureLocation(turret);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));

        poseStack.pushPose();
        poseStack.translate(0.0D, 1.5D, 0.0D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        // Legs and tripod stay fixed
        tripod.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        leg1.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        leg2.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        leg3.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        leg4.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);

        // Head rotates with turret aim
        poseStack.pushPose();
        float yaw = turret.getViewYRot(partialTick);
        float pitch = turret.getViewXRot(partialTick);
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        head.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.popPose();
        super.render(turret, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity.isAdvanced() ? TEXTURE_ADVANCED : TEXTURE_BASIC;
    }

    private static LayerDefinition createLegacyCrossbowModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("tripod", CubeListBuilder.create().texOffs(13, 0)
                .addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 3.0F), PartPose.offset(0.0F, 12.0F, 0.0F));

        root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(20, 10)
                .addBox(-1.0F, 1.0F, -1.0F, 2.0F, 13.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.5235988F, 0.0F, 0.0F));
        root.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(20, 10)
                .addBox(-1.0F, 1.0F, -1.0F, 2.0F, 13.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.5235988F, 1.570796F, 0.0F));
        root.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(20, 10)
                .addBox(-1.0F, 1.0F, -1.0F, 2.0F, 13.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.5235988F, 3.141593F, 0.0F));
        root.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(20, 10)
                .addBox(-1.0F, 1.0F, -1.0F, 2.0F, 13.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.5235988F, 4.712389F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(28, 14)
                .addBox(-2.0F, -2.0F, -7.0F, 4.0F, 2.0F, 14.0F), PartPose.offset(0.0F, 10.0F, 0.0F));

        head.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(20, 28)
                .addBox(-1.0F, -3.0F, -8.0F, 2.0F, 2.0F, 2.0F), PartPose.ZERO);
        head.addOrReplaceChild("ammobox", CubeListBuilder.create().texOffs(38, 0)
                .addBox(-2.0F, -7.0F, -6.0F, 4.0F, 5.0F, 9.0F), PartPose.ZERO);
        head.addOrReplaceChild("basebarcross", CubeListBuilder.create().texOffs(0, 13)
                .addBox(-2.0F, -1.5F, 10.0F, 4.0F, 1.0F, 1.0F), PartPose.ZERO);

        head.addOrReplaceChild("crossl1", CubeListBuilder.create().texOffs(0, 0)
                .addBox(0.0F, -2.0F, -6.0F, 5.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2443461F, 0.0F));
        head.addOrReplaceChild("crossl2", CubeListBuilder.create().texOffs(0, 0)
                .addBox(4.0F, -2.0F, -5.0F, 3.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2443461F, 0.0F));
        head.addOrReplaceChild("crossl3", CubeListBuilder.create().texOffs(0, 0)
                .addBox(6.0F, -2.0F, -4.0F, 2.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2443461F, 0.0F));

        head.addOrReplaceChild("crossr1", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-5.0F, -2.0F, -6.0F, 5.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2443461F, 0.0F));
        head.addOrReplaceChild("crossr2", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-7.0F, -2.0F, -5.0F, 3.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2443461F, 0.0F));
        head.addOrReplaceChild("crossr3", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-8.0F, -2.0F, -4.0F, 2.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2443461F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }
}

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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCArcaneBoreEntity;

final class TCArcaneBoreRenderer extends EntityRenderer<TCArcaneBoreEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/arcanebore.png");

    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;
    private final ModelPart tripod;
    private final ModelPart base;

    TCArcaneBoreRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.5F;
        ModelPart root = createLegacyModel().bakeRoot();
        leg1 = root.getChild("leg1");
        leg2 = root.getChild("leg2");
        leg3 = root.getChild("leg3");
        leg4 = root.getChild("leg4");
        tripod = root.getChild("tripod");
        base = root.getChild("base");
    }

    @Override
    public void render(TCArcaneBoreEntity bore, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE));
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.5D, 0.0D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(yawFor(bore.facing())));
        leg1.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        leg2.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        leg3.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        leg4.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        tripod.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        base.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(bore, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TCArcaneBoreEntity entity) {
        return TEXTURE;
    }

    private static float yawFor(Direction facing) {
        return switch (facing) {
            case SOUTH -> 0.0F;
            case WEST -> 90.0F;
            case NORTH -> 180.0F;
            case EAST -> 270.0F;
            default -> 0.0F;
        };
    }

    private static LayerDefinition createLegacyModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(20, 10).addBox(-1.0F, 1.0F, -1.0F, 2.0F, 13.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.5235988F, 0.0F, 0.0F));
        root.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(20, 10).addBox(-1.0F, 1.0F, -1.0F, 2.0F, 13.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.5235988F, 1.570796F, 0.0F));
        root.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(20, 10).addBox(-1.0F, 1.0F, -1.0F, 2.0F, 13.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.5235988F, 3.141593F, 0.0F));
        root.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(20, 10).addBox(-1.0F, 1.0F, -1.0F, 2.0F, 13.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.5235988F, 4.712389F, 0.0F));
        root.addOrReplaceChild("tripod", CubeListBuilder.create().texOffs(13, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 3.0F), PartPose.offset(0.0F, 12.0F, 0.0F));
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.offset(0.0F, 13.0F, 0.0F));
        base.addOrReplaceChild("crystal", CubeListBuilder.create().texOffs(32, 25).addBox(-1.0F, -4.0F, 5.0F, 2.0F, 2.0F, 2.0F), PartPose.ZERO);
        base.addOrReplaceChild("domebase", CubeListBuilder.create().texOffs(32, 19).addBox(-2.0F, -5.0F, 3.0F, 4.0F, 4.0F, 1.0F), PartPose.ZERO);
        base.addOrReplaceChild("dome", CubeListBuilder.create().texOffs(44, 16).addBox(-2.0F, -5.0F, 4.0F, 4.0F, 4.0F, 4.0F), PartPose.ZERO);
        base.addOrReplaceChild("magbase", CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, -4.0F, -6.0F, 2.0F, 2.0F, 3.0F), PartPose.ZERO);
        base.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(0, 9).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 3.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -3.0F, -6.0F, -1.570796F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }
}

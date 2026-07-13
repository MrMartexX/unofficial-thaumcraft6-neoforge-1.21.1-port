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
import net.minecraft.util.Mth;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCPechEntity;

final class TCPechRenderer extends EntityRenderer<TCPechEntity> {
    private static final ResourceLocation[] TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/pech_forage.png"),
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/pech_thaum.png"),
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/pech_stalker.png")
    };

    private final ModelPart body;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart head;
    private final ModelPart jowls;
    private final ModelPart lowerPack;
    private final ModelPart upperPack;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    TCPechRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.5F;
        ModelPart root = createLegacyModel().bakeRoot();
        body = root.getChild("body");
        rightLeg = root.getChild("right_leg");
        leftLeg = root.getChild("left_leg");
        head = root.getChild("head");
        jowls = root.getChild("jowls");
        lowerPack = root.getChild("lower_pack");
        upperPack = root.getChild("upper_pack");
        rightArm = root.getChild("right_arm");
        leftArm = root.getChild("left_arm");
    }

    @Override
    public void render(TCPechEntity pech, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        setupLegacyAnim(pech, partialTick);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(getTextureLocation(pech)));
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
        poseStack.translate(0.0D, 1.5D, 0.0D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        body.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        rightLeg.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        leftLeg.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        head.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        jowls.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        lowerPack.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        upperPack.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        rightArm.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        leftArm.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(pech, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TCPechEntity entity) {
        int index = Mth.clamp(entity.getPechType().legacyId(), 0, TEXTURES.length - 1);
        return TEXTURES[index];
    }

    private void setupLegacyAnim(TCPechEntity pech, float partialTick) {
        body.resetPose();
        rightLeg.resetPose();
        leftLeg.resetPose();
        head.resetPose();
        jowls.resetPose();
        lowerPack.resetPose();
        upperPack.resetPose();
        rightArm.resetPose();
        leftArm.resetPose();

        body.xRot = 0.3129957F;
        lowerPack.xRot = 0.3013602F;
        upperPack.xRot = 0.4537856F;

        float headYaw = Mth.rotLerp(partialTick, pech.yHeadRotO, pech.yHeadRot) - Mth.rotLerp(partialTick, pech.yBodyRotO, pech.yBodyRot);
        float headPitch = Mth.lerp(partialTick, pech.xRotO, pech.getXRot());
        head.yRot = headYaw * ((float) Math.PI / 180.0F);
        head.xRot = headPitch * ((float) Math.PI / 180.0F);

        float limbSwing = pech.walkAnimation.position(partialTick);
        float limbAmount = Math.min(pech.walkAnimation.speed(partialTick), 1.0F);
        jowls.yRot = head.yRot;
        jowls.xRot = head.xRot
                + (0.2617994F + Mth.cos(limbSwing * 0.6662F) * limbAmount * 0.25F)
                + 0.34906587F * Math.abs(Mth.sin(pech.mumble / 8.0F));
        rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbAmount;
        leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * limbAmount;
        rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbAmount;
        leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbAmount;
        lowerPack.yRot = Mth.cos(limbSwing * 0.6662F) * limbAmount * 0.25F;
        lowerPack.zRot = lowerPack.yRot;

        float age = pech.tickCount + partialTick;
        rightArm.zRot += Mth.cos(age * 0.09F) * 0.05F + 0.05F;
        leftArm.zRot -= Mth.cos(age * 0.09F) * 0.05F + 0.05F;
        rightArm.xRot += Mth.sin(age * 0.067F) * 0.05F;
        leftArm.xRot -= Mth.sin(age * 0.067F) * 0.05F;
    }

    private static LayerDefinition createLegacyModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(34, 12).mirror().addBox(-3.0F, 0.0F, 0.0F, 6.0F, 10.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 9.0F, -3.0F, 0.3129957F, 0.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(35, 1).mirror().addBox(-2.9F, 0.0F, 0.0F, 3.0F, 6.0F, 3.0F),
                PartPose.offset(0.0F, 18.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(35, 1).mirror().addBox(-0.1F, 0.0F, 0.0F, 3.0F, 6.0F, 3.0F),
                PartPose.offset(0.0F, 18.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(2, 11).mirror().addBox(-3.5F, -5.0F, -5.0F, 7.0F, 5.0F, 5.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "jowls",
                CubeListBuilder.create().texOffs(1, 21).mirror().addBox(-4.0F, -1.0F, -6.0F, 8.0F, 3.0F, 5.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "lower_pack",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-5.0F, 0.0F, 0.0F, 10.0F, 5.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 10.0F, 3.5F, 0.3013602F, 0.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "upper_pack",
                CubeListBuilder.create().texOffs(64, 1).mirror().addBox(-7.5F, -14.0F, 0.0F, 15.0F, 14.0F, 11.0F),
                PartPose.offsetAndRotation(0.0F, 10.0F, 3.0F, 0.4537856F, 0.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(52, 2).mirror().addBox(-2.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offset(-3.0F, 10.0F, -1.0F)
        );
        root.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(52, 2).mirror().addBox(0.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offset(3.0F, 10.0F, -1.0F)
        );
        return LayerDefinition.create(mesh, 128, 64);
    }
}

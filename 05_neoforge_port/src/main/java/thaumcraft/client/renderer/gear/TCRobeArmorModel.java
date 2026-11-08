package thaumcraft.client.renderer.gear;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * Authentic 1:1 port of 1.12 ModelRobe (128x64 texture).
 * Used by Void Thaumaturge Robes and Cultist Robes.
 * Features 3D pointed wizard hood, chest overlay & bib, segmented wizard pauldrons,
 * focus pouch, and flowing animated skirt/cloth panels that sway with walking motions.
 */
public class TCRobeArmorModel extends HumanoidModel<LivingEntity> {
    private final boolean inner;

    // Swaying cloth panels (inner model / legs)
    private final ModelPart frontclothR1;
    private final ModelPart frontclothR2;
    private final ModelPart frontclothL1;
    private final ModelPart frontclothL2;
    private final ModelPart clothBackR1;
    private final ModelPart clothBackR2;
    private final ModelPart clothBackR3;
    private final ModelPart clothBackL1;
    private final ModelPart clothBackL2;
    private final ModelPart clothBackL3;

    public TCRobeArmorModel(ModelPart root, boolean inner) {
        super(root);
        this.inner = inner;

        ModelPart b = root.getChild("body");
        if (inner) {
            this.frontclothR1 = b.getChild("frontcloth_r1");
            this.frontclothR2 = b.getChild("frontcloth_r2");
            this.frontclothL1 = b.getChild("frontcloth_l1");
            this.frontclothL2 = b.getChild("frontcloth_l2");
            this.clothBackR1 = b.getChild("cloth_back_r1");
            this.clothBackR2 = b.getChild("cloth_back_r2");
            this.clothBackR3 = b.getChild("cloth_back_r3");
            this.clothBackL1 = b.getChild("cloth_back_l1");
            this.clothBackL2 = b.getChild("cloth_back_l2");
            this.clothBackL3 = b.getChild("cloth_back_l3");
        } else {
            this.frontclothR1 = null;
            this.frontclothR2 = null;
            this.frontclothL1 = null;
            this.frontclothL2 = null;
            this.clothBackR1 = null;
            this.clothBackR2 = null;
            this.clothBackR3 = null;
            this.clothBackL1 = null;
            this.clothBackL2 = null;
            this.clothBackL3 = null;
        }
    }

    public static LayerDefinition createOuterLayer() {
        return createLayer(false);
    }

    public static LayerDefinition createInnerLayer() {
        return createLayer(true);
    }

    private static LayerDefinition createLayer(boolean inner) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Base parts with no default vanilla cubes, matching 1.12 cubeList.clear()
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition rightArm = root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition leftArm = root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
        PartDefinition rightLeg = root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        PartDefinition leftLeg = root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));

        // Head components (hood)
        head.addOrReplaceChild("hood1", CubeListBuilder.create().texOffs(16, 7)
                .addBox(-4.5F, -9.0F, -4.6F, 9, 9, 9), PartPose.ZERO);
        head.addOrReplaceChild("hood2", CubeListBuilder.create().texOffs(52, 13)
                .addBox(-4.0F, -9.7F, 2.0F, 8, 9, 3), PartPose.rotation(-0.2268928F, 0.0F, 0.0F));
        head.addOrReplaceChild("hood3", CubeListBuilder.create().texOffs(52, 14)
                .addBox(-3.5F, -10.0F, 3.5F, 7, 8, 3), PartPose.rotation(-0.3490659F, 0.0F, 0.0F));
        head.addOrReplaceChild("hood4", CubeListBuilder.create().texOffs(53, 15)
                .addBox(-3.0F, -10.7F, 3.5F, 6, 7, 3), PartPose.rotation(-0.5759587F, 0.0F, 0.0F));

        // Body components
        if (inner) {
            body.addOrReplaceChild("mbelt", CubeListBuilder.create().texOffs(16, 55)
                    .addBox(-4.0F, 7.0F, -3.0F, 8, 5, 1), PartPose.ZERO);
            body.addOrReplaceChild("mbelt_b", CubeListBuilder.create().texOffs(16, 55)
                    .addBox(-4.0F, 7.0F, -4.0F, 8, 5, 1), PartPose.rotation(0.0F, (float) Math.PI, 0.0F));
            body.addOrReplaceChild("mbelt_l", CubeListBuilder.create().texOffs(76, 44)
                    .addBox(4.0F, 7.0F, -3.0F, 1, 5, 6), PartPose.ZERO);
            body.addOrReplaceChild("mbelt_r", CubeListBuilder.create().texOffs(76, 44)
                    .addBox(-5.0F, 7.0F, -3.0F, 1, 5, 6), PartPose.ZERO);

            body.addOrReplaceChild("frontcloth_r1", CubeListBuilder.create().texOffs(100, 33)
                    .addBox(0.0F, 0.0F, 0.0F, 3, 8, 1), PartPose.offsetAndRotation(-3.0F, 11.0F, -2.9F, -0.1047198F, 0.0F, 0.0F));
            body.addOrReplaceChild("frontcloth_r2", CubeListBuilder.create().texOffs(100, 42)
                    .addBox(0.0F, 7.5F, 1.7F, 3, 3, 1), PartPose.offsetAndRotation(-3.0F, 11.0F, -2.9F, -0.3316126F, 0.0F, 0.0F));
            body.addOrReplaceChild("frontcloth_l1", CubeListBuilder.create().texOffs(100, 33).mirror()
                    .addBox(0.0F, 0.0F, 0.0F, 3, 8, 1), PartPose.offsetAndRotation(0.0F, 11.0F, -2.9F, -0.1047198F, 0.0F, 0.0F));
            body.addOrReplaceChild("frontcloth_l2", CubeListBuilder.create().texOffs(100, 42).mirror()
                    .addBox(0.0F, 7.5F, 1.7F, 3, 3, 1), PartPose.offsetAndRotation(0.0F, 11.0F, -2.9F, -0.3316126F, 0.0F, 0.0F));

            body.addOrReplaceChild("cloth_back_r1", CubeListBuilder.create().texOffs(88, 32)
                    .addBox(0.0F, 0.0F, 0.0F, 3, 4, 1), PartPose.offsetAndRotation(-3.0F, 11.5F, 2.9F, 0.1047198F, 0.0F, 0.0F));
            body.addOrReplaceChild("cloth_back_r2", CubeListBuilder.create().texOffs(88, 37)
                    .addBox(0.0F, 3.5F, -0.8F, 3, 3, 1), PartPose.offsetAndRotation(-3.0F, 11.5F, 2.9F, 0.2268928F, 0.0F, 0.0F));
            body.addOrReplaceChild("cloth_back_r3", CubeListBuilder.create().texOffs(88, 41)
                    .addBox(0.0F, 6.0F, -1.9F, 3, 3, 1), PartPose.offsetAndRotation(-3.0F, 11.5F, 2.9F, 0.2268928F, 0.0F, 0.0F));
            body.addOrReplaceChild("cloth_back_l1", CubeListBuilder.create().texOffs(88, 32).mirror()
                    .addBox(0.0F, 0.0F, 0.0F, 3, 4, 1), PartPose.offsetAndRotation(0.0F, 11.5F, 2.9F, 0.1047198F, 0.0F, 0.0F));
            body.addOrReplaceChild("cloth_back_l2", CubeListBuilder.create().texOffs(88, 37).mirror()
                    .addBox(0.0F, 3.5F, -0.8F, 3, 3, 1), PartPose.offsetAndRotation(0.0F, 11.5F, 2.9F, 0.2268928F, 0.0F, 0.0F));
            body.addOrReplaceChild("cloth_back_l3", CubeListBuilder.create().texOffs(88, 41).mirror()
                    .addBox(0.0F, 6.0F, -1.9F, 3, 3, 1), PartPose.offsetAndRotation(0.0F, 11.5F, 2.9F, 0.2268928F, 0.0F, 0.0F));
        } else {
            body.addOrReplaceChild("chestplate", CubeListBuilder.create().texOffs(16, 25)
                    .addBox(-4.0F, 1.0F, -2.0F, 8, 12, 4), PartPose.ZERO);
            body.addOrReplaceChild("chestthing", CubeListBuilder.create().texOffs(56, 50)
                    .addBox(-2.5F, 1.0F, -4.0F, 5, 7, 1), PartPose.ZERO);
            body.addOrReplaceChild("scroll", CubeListBuilder.create().texOffs(34, 27)
                    .addBox(-2.0F, 9.5F, 4.0F, 8, 3, 3), PartPose.rotation(0.0F, 0.0F, 0.1919862F));
            body.addOrReplaceChild("backplate", CubeListBuilder.create().texOffs(36, 45)
                    .addBox(-4.0F, 1.0F, 2.0F, 8, 11, 2), PartPose.ZERO);
            body.addOrReplaceChild("book", CubeListBuilder.create().texOffs(100, 8)
                    .addBox(1.0F, -0.3F, 4.0F, 5, 7, 2), PartPose.rotation(0.0F, 0.0F, 0.7679449F));
            body.addOrReplaceChild("clothchest_l", CubeListBuilder.create().texOffs(108, 38).mirror()
                    .addBox(2.1F, 0.5F, -3.5F, 2, 8, 1), PartPose.ZERO);
            body.addOrReplaceChild("clothchest_r", CubeListBuilder.create().texOffs(108, 38)
                    .addBox(-4.1F, 0.5F, -3.5F, 2, 8, 1), PartPose.ZERO);
        }

        // Right Arm components
        rightArm.addOrReplaceChild("shoulder_r", CubeListBuilder.create().texOffs(56, 35)
                .addBox(-3.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.ZERO);
        rightArm.addOrReplaceChild("rarm1", CubeListBuilder.create().texOffs(40, 24)
                .addBox(-2.5F, 2.5F, -2.0F, 1, 7, 4), PartPose.ZERO);
        rightArm.addOrReplaceChild("rarm2", CubeListBuilder.create().texOffs(46, 24)
                .addBox(0.5F, 2.5F, -2.0F, 1, 7, 4), PartPose.ZERO);
        rightArm.addOrReplaceChild("rarm3", CubeListBuilder.create().texOffs(52, 25)
                .addBox(-1.5F, 2.5F, -2.0F, 2, 7, 1), PartPose.ZERO);
        rightArm.addOrReplaceChild("shoulderplate_top_r", CubeListBuilder.create().texOffs(110, 37)
                .addBox(-4.5F, -2.5F, -3.5F, 2, 1, 7), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightArm.addOrReplaceChild("shoulderplate_r1", CubeListBuilder.create().texOffs(110, 45)
                .addBox(-3.5F, -1.5F, -3.5F, 1, 4, 7), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightArm.addOrReplaceChild("shoulderplate_r2", CubeListBuilder.create().texOffs(94, 45)
                .addBox(-2.5F, 1.5F, -3.5F, 1, 3, 7), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightArm.addOrReplaceChild("shoulderplate_r3", CubeListBuilder.create().texOffs(94, 45)
                .addBox(-1.5F, 3.5F, -3.5F, 1, 3, 7), PartPose.rotation(0.0F, 0.0F, 0.4363323F));

        // Left Arm components
        leftArm.addOrReplaceChild("shoulder_l", CubeListBuilder.create().texOffs(56, 35).mirror()
                .addBox(-1.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.ZERO);
        leftArm.addOrReplaceChild("larm1", CubeListBuilder.create().texOffs(40, 24).mirror()
                .addBox(1.5F, 2.5F, -2.0F, 1, 7, 4), PartPose.ZERO);
        leftArm.addOrReplaceChild("larm2", CubeListBuilder.create().texOffs(46, 24).mirror()
                .addBox(-1.5F, 2.5F, -2.0F, 1, 7, 4), PartPose.ZERO);
        leftArm.addOrReplaceChild("larm3", CubeListBuilder.create().texOffs(52, 25).mirror()
                .addBox(-0.5F, 2.5F, -2.0F, 2, 7, 1), PartPose.ZERO);
        leftArm.addOrReplaceChild("shoulderplate_top_l", CubeListBuilder.create().texOffs(110, 37).mirror()
                .addBox(2.5F, -2.5F, -3.5F, 2, 1, 7), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftArm.addOrReplaceChild("shoulderplate_l1", CubeListBuilder.create().texOffs(110, 45).mirror()
                .addBox(2.5F, -1.5F, -3.5F, 1, 4, 7), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftArm.addOrReplaceChild("shoulderplate_l2", CubeListBuilder.create().texOffs(94, 45).mirror()
                .addBox(1.5F, 1.5F, -3.5F, 1, 3, 7), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftArm.addOrReplaceChild("shoulderplate_l3", CubeListBuilder.create().texOffs(94, 45).mirror()
                .addBox(0.5F, 3.5F, -3.5F, 1, 3, 7), PartPose.rotation(0.0F, 0.0F, -0.4363323F));

        // Right Leg components
        rightLeg.addOrReplaceChild("legpanel_r4", CubeListBuilder.create().texOffs(76, 38)
                .addBox(-3.0F, 0.5F, -3.5F, 2, 3, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        rightLeg.addOrReplaceChild("legpanel_r5", CubeListBuilder.create().texOffs(76, 42)
                .addBox(-3.0F, 2.5F, -2.5F, 2, 3, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        rightLeg.addOrReplaceChild("legpanel_r6", CubeListBuilder.create().texOffs(82, 38)
                .addBox(-3.0F, 4.5F, -1.5F, 2, 3, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        rightLeg.addOrReplaceChild("sidepanel_r1", CubeListBuilder.create().texOffs(116, 25).mirror()
                .addBox(-2.5F, 0.5F, -2.5F, 1, 4, 5), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightLeg.addOrReplaceChild("sidecloth_r1", CubeListBuilder.create().texOffs(116, 42).mirror()
                .addBox(-2.5F, 0.5F, -2.5F, 1, 5, 5), PartPose.rotation(0.0F, 0.0F, 0.122173F));
        rightLeg.addOrReplaceChild("sidecloth_r2", CubeListBuilder.create().texOffs(116, 52).mirror()
                .addBox(-2.5F, 4.5F, -2.5F, 1, 4, 5), PartPose.rotation(0.0F, 0.0F, 0.3490659F));
        rightLeg.addOrReplaceChild("sidecloth_r3", CubeListBuilder.create().texOffs(116, 1).mirror()
                .addBox(0.4F, 8.4F, -2.5F, 1, 3, 5), PartPose.rotation(0.0F, 0.0F, 0.5235988F));

        // Left Leg components
        leftLeg.addOrReplaceChild("legpanel_l4", CubeListBuilder.create().texOffs(76, 38).mirror()
                .addBox(1.0F, 0.5F, -3.5F, 2, 3, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        leftLeg.addOrReplaceChild("legpanel_l5", CubeListBuilder.create().texOffs(76, 42).mirror()
                .addBox(1.0F, 2.5F, -2.5F, 2, 3, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        leftLeg.addOrReplaceChild("legpanel_l6", CubeListBuilder.create().texOffs(82, 38).mirror()
                .addBox(1.0F, 4.5F, -1.5F, 2, 3, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        leftLeg.addOrReplaceChild("sidepanel_l1", CubeListBuilder.create().texOffs(116, 25)
                .addBox(1.5F, 0.5F, -2.5F, 1, 4, 5), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftLeg.addOrReplaceChild("sidecloth_l1", CubeListBuilder.create().texOffs(116, 42)
                .addBox(1.5F, 0.5F, -2.5F, 1, 5, 5), PartPose.rotation(0.0F, 0.0F, -0.122173F));
        leftLeg.addOrReplaceChild("sidecloth_l2", CubeListBuilder.create().texOffs(116, 52)
                .addBox(1.5F, 4.5F, -2.5F, 1, 4, 5), PartPose.rotation(0.0F, 0.0F, -0.3490659F));
        leftLeg.addOrReplaceChild("sidecloth_l3", CubeListBuilder.create().texOffs(116, 1)
                .addBox(-1.4F, 8.4F, -2.5F, 1, 3, 5), PartPose.rotation(0.0F, 0.0F, -0.5235988F));
        leftLeg.addOrReplaceChild("focipouch", CubeListBuilder.create().texOffs(100, 20)
                .addBox(3.5F, 0.5F, -2.5F, 3, 6, 5), PartPose.rotation(0.0F, 0.0F, -0.122173F));

        return LayerDefinition.create(mesh, 128, 64);
    }

    public void setupRobeAnimations(float limbSwing, float limbSwingAmount) {
        float a = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        float b = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        float c = Math.min(a, b);

        if (frontclothL1 != null) frontclothL1.xRot = c - 0.1047198F;
        if (frontclothR1 != null) frontclothR1.xRot = c - 0.1047198F;
        if (frontclothL2 != null) frontclothL2.xRot = c - 0.3316126F;
        if (frontclothR2 != null) frontclothR2.xRot = c - 0.3316126F;

        if (clothBackL1 != null) clothBackL1.xRot = -c + 0.1047198F;
        if (clothBackR1 != null) clothBackR1.xRot = -c + 0.1047198F;
        if (clothBackL2 != null) clothBackL2.xRot = -c + 0.2268928F;
        if (clothBackR2 != null) clothBackR2.xRot = -c + 0.2268928F;
        if (clothBackL3 != null) clothBackL3.xRot = -c + 0.2268928F;
        if (clothBackR3 != null) clothBackR3.xRot = -c + 0.2268928F;

        // 1.01f scale on head to avoid z-fighting with skin, identical to legacy
        head.xScale = 1.01F;
        head.yScale = 1.01F;
        head.zScale = 1.01F;
    }
}

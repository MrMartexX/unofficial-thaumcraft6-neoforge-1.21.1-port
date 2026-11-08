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
 * Authentic 1:1 port of 1.12 ModelLeaderArmor (128x64 texture).
 * Used by Cultist Leader / Praetor Armor.
 * Features high ornate standing collar, winged commander pauldrons with spikes,
 * ceremonial chest ornament, front sashes, and layered segmented back panels & cloak.
 */
public class TCLeaderArmorModel extends HumanoidModel<LivingEntity> {
    private final boolean inner;

    // Animated cloth & cloak parts
    private final ModelPart legClothR;
    private final ModelPart legClothL;
    private final ModelPart cloak1;
    private final ModelPart cloak2;
    private final ModelPart cloak3;

    public TCLeaderArmorModel(ModelPart root, boolean inner) {
        super(root);
        this.inner = inner;

        ModelPart b = root.getChild("body");
        if (inner) {
            this.legClothR = b.getChild("legcloth_r");
            this.legClothL = b.getChild("legcloth_l");
            this.cloak1 = null;
            this.cloak2 = null;
            this.cloak3 = null;
        } else {
            this.legClothR = null;
            this.legClothL = null;
            this.cloak1 = b.getChild("cloak1");
            this.cloak2 = b.getChild("cloak2");
            this.cloak3 = b.getChild("cloak3");
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

        // Head components (leader helm)
        head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(41, 8)
                .addBox(-4.5F, -9.0F, -4.5F, 9, 9, 9), PartPose.ZERO);

        // Body components
        if (inner) {
            body.addOrReplaceChild("mbelt", CubeListBuilder.create().texOffs(56, 55)
                    .addBox(-4.0F, 8.0F, -3.0F, 8, 4, 1), PartPose.ZERO);
            body.addOrReplaceChild("mbelt_l", CubeListBuilder.create().texOffs(76, 44)
                    .addBox(4.0F, 8.0F, -3.0F, 1, 3, 6), PartPose.ZERO);
            body.addOrReplaceChild("mbelt_r", CubeListBuilder.create().texOffs(76, 44)
                    .addBox(-5.0F, 8.0F, -3.0F, 1, 3, 6), PartPose.ZERO);
            body.addOrReplaceChild("belt_l", CubeListBuilder.create().texOffs(76, 44)
                    .addBox(4.0F, 4.0F, -3.0F, 1, 3, 6), PartPose.ZERO);
            body.addOrReplaceChild("belt_r", CubeListBuilder.create().texOffs(76, 44)
                    .addBox(-5.0F, 4.0F, -3.0F, 1, 3, 6), PartPose.ZERO);
            body.addOrReplaceChild("legcloth_r", CubeListBuilder.create().texOffs(68, 50)
                    .addBox(-1.0F, 0.0F, 0.0F, 2, 8, 1), PartPose.offsetAndRotation(-2.0F, 10.0F, -3.0F, -0.1047198F, 0.0F, 0.0F));
            body.addOrReplaceChild("legcloth_l", CubeListBuilder.create().texOffs(68, 50)
                    .addBox(-1.0F, 0.0F, 0.0F, 2, 8, 1), PartPose.offsetAndRotation(2.0F, 10.0F, -3.0F, -0.1047198F, 0.0F, 0.0F));
        } else {
            body.addOrReplaceChild("chestplate", CubeListBuilder.create().texOffs(16, 20)
                    .addBox(-4.0F, 1.0F, -2.0F, 8, 12, 4), PartPose.ZERO);
            body.addOrReplaceChild("chest_ornament", CubeListBuilder.create().texOffs(76, 32)
                    .addBox(-3.0F, 2.0F, -3.0F, 6, 6, 1), PartPose.rotation(0.0F, 0.0F, 0.7853982F));
            body.addOrReplaceChild("chest_cloth_r", CubeListBuilder.create().texOffs(76, 40)
                    .addBox(-3.0F, 1.5F, -2.5F, 3, 8, 1), PartPose.ZERO);
            body.addOrReplaceChild("chest_cloth_l", CubeListBuilder.create().texOffs(76, 40).mirror()
                    .addBox(0.0F, 1.5F, -2.5F, 3, 8, 1), PartPose.ZERO);
            body.addOrReplaceChild("backplate", CubeListBuilder.create().texOffs(36, 45)
                    .addBox(-4.0F, 1.0F, 2.0F, 8, 11, 2), PartPose.ZERO);

            // Standing collar
            body.addOrReplaceChild("collar_b", CubeListBuilder.create().texOffs(24, 45)
                    .addBox(-4.5F, -1.5F, 3.0F, 9, 4, 1), PartPose.rotation(0.2617994F, 0.0F, 0.0F));
            body.addOrReplaceChild("collar_r", CubeListBuilder.create().texOffs(24, 41)
                    .addBox(-5.5F, -1.5F, -3.0F, 1, 4, 7), PartPose.rotation(0.0F, 0.0F, -0.2617994F));
            body.addOrReplaceChild("collar_l", CubeListBuilder.create().texOffs(24, 41).mirror()
                    .addBox(4.5F, -1.5F, -3.0F, 1, 4, 7), PartPose.rotation(0.0F, 0.0F, 0.2617994F));
            body.addOrReplaceChild("collar_f", CubeListBuilder.create().texOffs(24, 36)
                    .addBox(-4.5F, 1.5F, -3.0F, 9, 1, 1), PartPose.ZERO);

            body.addOrReplaceChild("belt_l", CubeListBuilder.create().texOffs(76, 44)
                    .addBox(4.0F, 4.0F, -3.0F, 1, 3, 6), PartPose.ZERO);
            body.addOrReplaceChild("belt_r", CubeListBuilder.create().texOffs(76, 44)
                    .addBox(-5.0F, 4.0F, -3.0F, 1, 3, 6), PartPose.ZERO);

            body.addOrReplaceChild("cloakt_l", CubeListBuilder.create().texOffs(0, 43).mirror()
                    .addBox(2.0F, 1.0F, 2.1F, 2, 1, 3), PartPose.ZERO);
            body.addOrReplaceChild("cloakt_r", CubeListBuilder.create().texOffs(0, 43)
                    .addBox(-4.0F, 1.0F, 2.1F, 2, 1, 3), PartPose.ZERO);
            body.addOrReplaceChild("cloak1", CubeListBuilder.create().texOffs(0, 13)
                    .addBox(-4.5F, 1.5F, 4.3F, 9, 12, 1), PartPose.rotation(0.1396263F, 0.0F, 0.0F));
            body.addOrReplaceChild("cloak2", CubeListBuilder.create().texOffs(0, 27)
                    .addBox(-4.5F, 12.0F, -1.0F, 9, 8, 1), PartPose.rotation(0.3069452F, 0.0F, 0.0F));
            body.addOrReplaceChild("cloak3", CubeListBuilder.create().texOffs(0, 37)
                    .addBox(-4.5F, 17.5F, -7.5F, 9, 4, 1), PartPose.rotation(0.4465716F, 0.0F, 0.0F));
        }

        // Right Arm components
        rightArm.addOrReplaceChild("shoulder_r", CubeListBuilder.create().texOffs(56, 35)
                .addBox(-3.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.ZERO);
        rightArm.addOrReplaceChild("shoulder_r1", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.5F, -1.5F, -3.0F, 3, 5, 6), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightArm.addOrReplaceChild("shoulder_r2", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-3.5F, 1.5F, -3.0F, 2, 2, 6), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightArm.addOrReplaceChild("shoulder_r3", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-2.5F, 2.5F, -3.0F, 2, 2, 6), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightArm.addOrReplaceChild("shoulder_r4", CubeListBuilder.create().texOffs(18, 0)
                .addBox(-2.5F, -3.0F, -3.0F, 1, 2, 6), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightArm.addOrReplaceChild("shoulder_r5", CubeListBuilder.create().texOffs(18, 0)
                .addBox(-3.5F, -2.5F, -3.0F, 1, 2, 6), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightArm.addOrReplaceChild("gauntlet_r", CubeListBuilder.create().texOffs(100, 26)
                .addBox(-3.5F, 3.5F, -2.5F, 2, 6, 5), PartPose.ZERO);
        rightArm.addOrReplaceChild("gauntlet_r2", CubeListBuilder.create().texOffs(84, 13)
                .addBox(-3.0F, 5.5F, -3.5F, 2, 4, 1), PartPose.rotation(0.2617994F, 0.0F, 0.0F));
        rightArm.addOrReplaceChild("gauntletstrap_r1", CubeListBuilder.create().texOffs(84, 31)
                .addBox(-1.5F, 3.5F, -2.5F, 3, 1, 5), PartPose.ZERO);
        rightArm.addOrReplaceChild("gauntletstrap_r2", CubeListBuilder.create().texOffs(84, 31)
                .addBox(-1.5F, 6.5F, -2.5F, 3, 1, 5), PartPose.ZERO);

        // Left Arm components
        leftArm.addOrReplaceChild("shoulder_l", CubeListBuilder.create().texOffs(56, 35).mirror()
                .addBox(-1.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.ZERO);
        leftArm.addOrReplaceChild("shoulder_l1", CubeListBuilder.create().texOffs(0, 0).mirror()
                .addBox(1.5F, -1.5F, -3.0F, 3, 5, 6), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftArm.addOrReplaceChild("shoulder_l2", CubeListBuilder.create().texOffs(0, 0).mirror()
                .addBox(1.5F, 1.5F, -3.0F, 2, 2, 6), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftArm.addOrReplaceChild("shoulder_l3", CubeListBuilder.create().texOffs(0, 0).mirror()
                .addBox(0.5F, 2.5F, -3.0F, 2, 2, 6), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftArm.addOrReplaceChild("shoulder_l4", CubeListBuilder.create().texOffs(18, 0).mirror()
                .addBox(1.5F, -3.0F, -3.0F, 1, 2, 6), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftArm.addOrReplaceChild("shoulder_l5", CubeListBuilder.create().texOffs(18, 0).mirror()
                .addBox(2.5F, -2.5F, -3.0F, 1, 2, 6), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftArm.addOrReplaceChild("gauntlet_l", CubeListBuilder.create().texOffs(114, 26)
                .addBox(1.5F, 3.5F, -2.5F, 2, 6, 5), PartPose.ZERO);
        leftArm.addOrReplaceChild("gauntlet_l2", CubeListBuilder.create().texOffs(84, 13).mirror()
                .addBox(1.0F, 5.5F, -3.5F, 2, 4, 1), PartPose.rotation(0.2617994F, 0.0F, 0.0F));
        leftArm.addOrReplaceChild("gauntletstrap_l1", CubeListBuilder.create().texOffs(84, 31).mirror()
                .addBox(-1.5F, 3.5F, -2.5F, 3, 1, 5), PartPose.ZERO);
        leftArm.addOrReplaceChild("gauntletstrap_l2", CubeListBuilder.create().texOffs(84, 31).mirror()
                .addBox(-1.5F, 6.5F, -2.5F, 3, 1, 5), PartPose.ZERO);

        // Right Leg components
        rightLeg.addOrReplaceChild("backpanel_r1", CubeListBuilder.create().texOffs(0, 18)
                .addBox(-3.0F, 0.5F, 2.5F, 5, 3, 1), PartPose.rotation(0.3490659F, 0.0F, 0.0F));
        rightLeg.addOrReplaceChild("backpanel_r2", CubeListBuilder.create().texOffs(0, 18)
                .addBox(-3.0F, 2.5F, 1.8F, 5, 3, 1), PartPose.rotation(0.2617994F, 0.0F, 0.0F));
        rightLeg.addOrReplaceChild("backpanel_r3", CubeListBuilder.create().texOffs(0, 18)
                .addBox(-3.0F, 4.5F, 1.1F, 5, 3, 1), PartPose.rotation(0.1745329F, 0.0F, 0.0F));
        rightLeg.addOrReplaceChild("backpanel_r4", CubeListBuilder.create().texOffs(0, 18)
                .addBox(-3.0F, 6.5F, 0.5F, 5, 3, 1), PartPose.rotation(0.0872665F, 0.0F, 0.0F));

        // Left Leg components
        leftLeg.addOrReplaceChild("backpanel_l1", CubeListBuilder.create().texOffs(0, 18).mirror()
                .addBox(-2.0F, 0.5F, 2.5F, 5, 3, 1), PartPose.rotation(0.3490659F, 0.0F, 0.0F));
        leftLeg.addOrReplaceChild("backpanel_l2", CubeListBuilder.create().texOffs(0, 18).mirror()
                .addBox(-2.0F, 2.5F, 1.8F, 5, 3, 1), PartPose.rotation(0.2617994F, 0.0F, 0.0F));
        leftLeg.addOrReplaceChild("backpanel_l3", CubeListBuilder.create().texOffs(0, 18).mirror()
                .addBox(-2.0F, 4.5F, 1.1F, 5, 3, 1), PartPose.rotation(0.1745329F, 0.0F, 0.0F));
        leftLeg.addOrReplaceChild("backpanel_l4", CubeListBuilder.create().texOffs(0, 18).mirror()
                .addBox(-2.0F, 6.5F, 0.5F, 5, 3, 1), PartPose.rotation(0.0872665F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 64);
    }

    public void setupLeaderAnimations(float limbSwing, float limbSwingAmount) {
        float a = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        float b = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        float c = Math.min(a, b);

        if (legClothR != null) legClothR.xRot = a - 0.1047198F;
        if (legClothL != null) legClothL.xRot = b - 0.1047198F;
        if (cloak1 != null) cloak1.xRot = -c / 2.0F + 0.1396263F;
        if (cloak2 != null) cloak2.xRot = -c / 2.0F + 0.3069452F;
        if (cloak3 != null) cloak3.xRot = -c / 2.0F + 0.4465716F;

        // 1.01f scale on head to avoid z-fighting with skin, identical to legacy
        head.xScale = 1.01F;
        head.yScale = 1.01F;
        head.zScale = 1.01F;
    }
}

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
 * Authentic 1:1 port of 1.12 ModelKnightArmor (128x64 texture).
 * Used by Cultist Knight Armor and Inhabited Zombie plates.
 * Features 3D closed greathelm, heavy segmented knight pauldrons, tabard,
 * layered flank plates, and swaying back cloaks.
 */
public class TCKnightArmorModel extends HumanoidModel<LivingEntity> {
    private final boolean inner;

    // Outer cloth & cloak parts
    private final ModelPart frontcloth1;
    private final ModelPart frontcloth2;
    private final ModelPart cloak1;
    private final ModelPart cloak2;
    private final ModelPart cloak3;

    public TCKnightArmorModel(ModelPart root, boolean inner) {
        super(root);
        this.inner = inner;

        ModelPart b = root.getChild("body");
        if (!inner) {
            this.frontcloth1 = b.getChild("frontcloth1");
            this.frontcloth2 = b.getChild("frontcloth2");
            this.cloak1 = b.getChild("cloak1");
            this.cloak2 = b.getChild("cloak2");
            this.cloak3 = b.getChild("cloak3");
        } else {
            this.frontcloth1 = null;
            this.frontcloth2 = null;
            this.cloak1 = null;
            this.cloak2 = null;
            this.cloak3 = null;
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

        // Head components (knight helm)
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
        } else {
            body.addOrReplaceChild("chestplate", CubeListBuilder.create().texOffs(16, 20)
                    .addBox(-4.0F, 1.0F, -2.0F, 8, 12, 4), PartPose.ZERO);
            body.addOrReplaceChild("tabbard", CubeListBuilder.create().texOffs(114, 48)
                    .addBox(-3.0F, 1.0F, -2.1F, 6, 10, 1), PartPose.ZERO);
            body.addOrReplaceChild("backplate", CubeListBuilder.create().texOffs(36, 45)
                    .addBox(-4.0F, 1.0F, 2.0F, 8, 11, 2), PartPose.ZERO);
            body.addOrReplaceChild("cloakat_l", CubeListBuilder.create().texOffs(0, 43).mirror()
                    .addBox(2.0F, 1.0F, 2.1F, 2, 1, 3), PartPose.ZERO);
            body.addOrReplaceChild("cloakat_r", CubeListBuilder.create().texOffs(0, 43)
                    .addBox(-4.0F, 1.0F, 2.1F, 2, 1, 3), PartPose.ZERO);
            body.addOrReplaceChild("cloak1", CubeListBuilder.create().texOffs(0, 13)
                    .addBox(-4.5F, 1.5F, 4.3F, 9, 12, 1), PartPose.rotation(0.1396263F, 0.0F, 0.0F));
            body.addOrReplaceChild("cloak2", CubeListBuilder.create().texOffs(0, 27)
                    .addBox(-4.5F, 12.0F, -1.0F, 9, 8, 1), PartPose.rotation(0.3069452F, 0.0F, 0.0F));
            body.addOrReplaceChild("cloak3", CubeListBuilder.create().texOffs(0, 37)
                    .addBox(-4.5F, 17.5F, -7.5F, 9, 4, 1), PartPose.rotation(0.4465716F, 0.0F, 0.0F));
            body.addOrReplaceChild("frontcloth1", CubeListBuilder.create().texOffs(120, 39)
                    .addBox(0.0F, 0.0F, 0.0F, 6, 8, 1), PartPose.offsetAndRotation(-3.0F, 11.0F, -3.5F, -0.1047198F, 0.0F, 0.0F));
            body.addOrReplaceChild("frontcloth2", CubeListBuilder.create().texOffs(100, 37)
                    .addBox(0.0F, 7.5F, 1.8F, 6, 3, 1), PartPose.offsetAndRotation(-3.0F, 11.0F, -3.5F, -0.3316126F, 0.0F, 0.0F));
        }

        // Right Arm components
        rightArm.addOrReplaceChild("shoulder_r", CubeListBuilder.create().texOffs(56, 35)
                .addBox(-3.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.ZERO);
        rightArm.addOrReplaceChild("shoulder_r0", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.5F, -1.5F, -3.0F, 6, 4, 6), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightArm.addOrReplaceChild("shoulder_r1", CubeListBuilder.create().texOffs(0, 48)
                .addBox(-3.5F, 1.5F, -2.5F, 5, 2, 5), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightArm.addOrReplaceChild("shoulder_r2", CubeListBuilder.create().texOffs(0, 56)
                .addBox(-2.5F, 2.5F, -2.0F, 5, 3, 4), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightArm.addOrReplaceChild("gauntlet_r", CubeListBuilder.create().texOffs(100, 26)
                .addBox(-3.5F, 3.5F, -2.5F, 2, 6, 5), PartPose.ZERO);
        rightArm.addOrReplaceChild("gauntletstrap_r1", CubeListBuilder.create().texOffs(84, 31)
                .addBox(-1.5F, 3.5F, -2.5F, 3, 1, 5), PartPose.ZERO);
        rightArm.addOrReplaceChild("gauntletstrap_r2", CubeListBuilder.create().texOffs(84, 31)
                .addBox(-1.5F, 6.5F, -2.5F, 3, 1, 5), PartPose.ZERO);

        // Left Arm components
        leftArm.addOrReplaceChild("shoulder_l", CubeListBuilder.create().texOffs(56, 35).mirror()
                .addBox(-1.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.ZERO);
        leftArm.addOrReplaceChild("shoulder_l0", CubeListBuilder.create().texOffs(0, 0).mirror()
                .addBox(-1.5F, -1.5F, -3.0F, 6, 4, 6), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftArm.addOrReplaceChild("shoulder_l1", CubeListBuilder.create().texOffs(0, 48).mirror()
                .addBox(-1.5F, 1.5F, -2.5F, 5, 2, 5), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftArm.addOrReplaceChild("shoulder_l2", CubeListBuilder.create().texOffs(0, 56).mirror()
                .addBox(-2.5F, 2.5F, -2.0F, 5, 3, 4), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftArm.addOrReplaceChild("gauntlet_l", CubeListBuilder.create().texOffs(114, 26)
                .addBox(1.5F, 3.5F, -2.5F, 2, 6, 5), PartPose.ZERO);
        leftArm.addOrReplaceChild("gauntletstrap_l1", CubeListBuilder.create().texOffs(84, 31).mirror()
                .addBox(-1.5F, 3.5F, -2.5F, 3, 1, 5), PartPose.ZERO);
        leftArm.addOrReplaceChild("gauntletstrap_l2", CubeListBuilder.create().texOffs(84, 31).mirror()
                .addBox(-1.5F, 6.5F, -2.5F, 3, 1, 5), PartPose.ZERO);

        // Right Leg components
        rightLeg.addOrReplaceChild("sidepanel_r0", CubeListBuilder.create().texOffs(96, 14)
                .addBox(-3.0F, -0.5F, -2.5F, 5, 3, 5), PartPose.rotation(0.0F, 0.0F, 0.1396263F));
        rightLeg.addOrReplaceChild("sidepanel_r1", CubeListBuilder.create().texOffs(96, 7).mirror()
                .addBox(0.0F, 2.5F, -2.5F, 2, 2, 5), PartPose.rotation(0.0F, 0.0F, 0.1396263F));
        rightLeg.addOrReplaceChild("sidepanel_r2", CubeListBuilder.create().texOffs(114, 5).mirror()
                .addBox(-2.0F, 2.5F, -2.5F, 2, 3, 5), PartPose.rotation(0.0F, 0.0F, 0.1396263F));
        rightLeg.addOrReplaceChild("sidepanel_r3", CubeListBuilder.create().texOffs(116, 13).mirror()
                .addBox(-3.0F, 2.5F, -2.5F, 1, 4, 5), PartPose.rotation(0.0F, 0.0F, 0.1396263F));

        // Left Leg components
        leftLeg.addOrReplaceChild("sidepanel_l0", CubeListBuilder.create().texOffs(96, 14)
                .addBox(-2.0F, -0.5F, -2.5F, 5, 3, 5), PartPose.rotation(0.0F, 0.0F, -0.1396263F));
        leftLeg.addOrReplaceChild("sidepanel_l1", CubeListBuilder.create().texOffs(96, 7)
                .addBox(-2.0F, 2.5F, -2.5F, 2, 2, 5), PartPose.rotation(0.0F, 0.0F, -0.1396263F));
        leftLeg.addOrReplaceChild("sidepanel_l2", CubeListBuilder.create().texOffs(114, 5)
                .addBox(0.0F, 2.5F, -2.5F, 2, 3, 5), PartPose.rotation(0.0F, 0.0F, -0.1396263F));
        leftLeg.addOrReplaceChild("sidepanel_l3", CubeListBuilder.create().texOffs(116, 13)
                .addBox(2.0F, 2.5F, -2.5F, 1, 4, 5), PartPose.rotation(0.0F, 0.0F, -0.1396263F));

        return LayerDefinition.create(mesh, 128, 64);
    }

    public void setupKnightAnimations(float limbSwing, float limbSwingAmount) {
        float a = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        float b = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        float c = Math.min(a, b);

        if (frontcloth1 != null) frontcloth1.xRot = c - 0.1047198F;
        if (frontcloth2 != null) frontcloth2.xRot = c - 0.3316126F;
        if (cloak1 != null) cloak1.xRot = -c / 2.0F + 0.1396263F;
        if (cloak2 != null) cloak2.xRot = -c / 2.0F + 0.3069452F;
        if (cloak3 != null) cloak3.xRot = -c / 2.0F + 0.4465716F;

        // 1.01f scale on head to avoid z-fighting with skin, identical to legacy
        head.xScale = 1.01F;
        head.yScale = 1.01F;
        head.zScale = 1.01F;
    }
}

package thaumcraft.client.renderer;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thaumcraft.common.entities.boss.TCEldritchGolemEntity;

public class TCEldritchGolemModel extends HierarchicalModel<TCEldritchGolemEntity> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart head2;
    private final ModelPart torso;
    private final ModelPart armR;
    private final ModelPart armL;
    private final ModelPart legR;
    private final ModelPart legL;

    public TCEldritchGolemModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.head2 = root.getChild("head2");
        this.torso = root.getChild("torso");
        this.armR = root.getChild("armR");
        this.armL = root.getChild("armL");
        this.legR = root.getChild("legR");
        this.legL = root.getChild("legL");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(47, 12).addBox(-3.5F, -6.0F, -2.5F, 7.0F, 7.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 4.5F, -3.8F, -0.1047198F, 0.0F, 0.0F)
        );

        root.addOrReplaceChild("head2",
                CubeListBuilder.create()
                        .texOffs(26, 16).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -5.0F, -0.1047198F, 0.0F, 0.0F)
        );

        PartDefinition torso = root.addOrReplaceChild("torso",
                CubeListBuilder.create()
                        .texOffs(34, 45).addBox(-5.0F, 2.5F, -3.0F, 10.0F, 10.0F, 6.0F)
                        .texOffs(0, 47).addBox(-5.0F, 1.5F, 4.0F, 10.0F, 12.0F, 1.0F)
                        .texOffs(0, 59).addBox(-5.0F, 13.5F, 1.7F, 10.0F, 4.0F, 1.0F)
                        .texOffs(0, 37).addBox(-5.0F, 17.5F, -0.8F, 10.0F, 4.0F, 1.0F)
                        .texOffs(0, 43).addBox(3.0F, 0.5F, 2.0F, 2.0F, 1.0F, 3.0F)
                        .texOffs(0, 43).addBox(-5.0F, 0.5F, 2.0F, 2.0F, 1.0F, 3.0F)
                        .texOffs(75, 50).addBox(3.5F, -0.5F, -7.0F, 1.0F, 4.0F, 10.0F)
                        .texOffs(67, 50).addBox(-4.5F, -0.5F, -7.0F, 1.0F, 4.0F, 10.0F)
                        .texOffs(77, 59).addBox(-3.5F, -0.5F, 2.0F, 7.0F, 4.0F, 1.0F)
                        .texOffs(77, 59).addBox(-3.5F, -0.5F, -7.0F, 7.0F, 4.0F, 1.0F)
                        .texOffs(22, 0).addBox(-3.5F, 0.0F, -6.0F, 7.0F, 1.0F, 8.0F)
                        .texOffs(114, 52).addBox(-3.0F, 3.2F, -3.5F, 6.0F, 10.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -2.5F, 0.1745329F, 0.0F, 0.0F)
        );

        root.addOrReplaceChild("armR",
                CubeListBuilder.create()
                        .texOffs(78, 32).addBox(-3.5F, 1.5F, -2.0F, 4.0F, 13.0F, 5.0F)
                        .texOffs(56, 31).addBox(-4.5F, -1.5F, -2.5F, 5.0F, 6.0F, 6.0F)
                        .texOffs(0, 0).addBox(-4.3F, -1.0F, -3.0F, 4.0F, 5.0F, 7.0F)
                        .texOffs(0, 23).addBox(-3.3F, 4.0F, -2.5F, 1.0F, 2.0F, 6.0F)
                        .texOffs(0, 12).addBox(-2.3F, 4.0F, -3.0F, 2.0F, 3.0F, 7.0F),
                PartPose.offsetAndRotation(-5.0F, 3.0F, -2.0F, 0.0F, 0.0F, 0.1047198F)
        );

        root.addOrReplaceChild("armL",
                CubeListBuilder.create()
                        .texOffs(78, 32).mirror().addBox(-0.5F, 1.5F, -2.0F, 4.0F, 13.0F, 5.0F)
                        .texOffs(56, 31).mirror().addBox(-0.5F, -1.5F, -2.5F, 5.0F, 6.0F, 6.0F)
                        .texOffs(0, 0).mirror().addBox(0.3F, -1.0F, -3.0F, 4.0F, 5.0F, 7.0F)
                        .texOffs(0, 23).mirror().addBox(2.3F, 4.0F, -2.5F, 1.0F, 2.0F, 6.0F)
                        .texOffs(0, 12).mirror().addBox(0.3F, 4.0F, -3.0F, 2.0F, 3.0F, 7.0F),
                PartPose.offsetAndRotation(5.0F, 3.0F, -2.0F, 0.0F, 0.0F, -0.1047198F)
        );

        root.addOrReplaceChild("legR",
                CubeListBuilder.create()
                        .texOffs(79, 19).addBox(-2.5F, 2.5F, -2.0F, 4.0F, 9.0F, 4.0F)
                        .texOffs(96, 14).addBox(-3.0F, -0.5F, -2.5F, 5.0F, 3.0F, 5.0F)
                        .texOffs(116, 13).addBox(-3.0F, 2.5F, -2.5F, 1.0F, 4.0F, 5.0F)
                        .texOffs(114, 5).mirror().addBox(-2.0F, 2.5F, -2.5F, 2.0F, 3.0F, 5.0F)
                        .texOffs(96, 7).addBox(0.0F, 2.5F, -2.5F, 2.0F, 2.0F, 5.0F),
                PartPose.offset(-2.0F, 12.5F, 0.0F)
        );

        root.addOrReplaceChild("legL",
                CubeListBuilder.create()
                        .texOffs(79, 19).mirror().addBox(-1.5F, 2.5F, -2.0F, 4.0F, 9.0F, 4.0F)
                        .texOffs(96, 14).mirror().addBox(-2.0F, -0.5F, -2.5F, 5.0F, 3.0F, 5.0F)
                        .texOffs(116, 13).mirror().addBox(2.0F, 2.5F, -2.5F, 1.0F, 4.0F, 5.0F)
                        .texOffs(114, 5).mirror().addBox(0.0F, 2.5F, -2.5F, 2.0F, 3.0F, 5.0F)
                        .texOffs(96, 7).mirror().addBox(-2.0F, 2.5F, -2.5F, 2.0F, 2.0F, 5.0F),
                PartPose.offset(2.0F, 12.5F, 0.0F)
        );

        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(TCEldritchGolemEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean headless = entity.isHeadless();
        head.visible = !headless;
        head2.visible = headless;

        if (!headless) {
            head.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.25F;
            head.xRot = headPitch * Mth.DEG_TO_RAD * 0.5F;
        } else {
            head2.yRot = netHeadYaw * Mth.DEG_TO_RAD;
            head2.xRot = headPitch * Mth.DEG_TO_RAD;
        }

        legR.xRot = Mth.cos(limbSwing * 0.4662F) * 1.4F * limbSwingAmount;
        legL.xRot = Mth.cos(limbSwing * 0.4662F + (float) Math.PI) * 1.4F * limbSwingAmount;

        int attackTimer = entity.getAttackTimer();
        if (attackTimer > 0) {
            float progress = (attackTimer - ageInTicks % 1.0F) / 10.0F;
            float slamAngle = -2.0F + 1.5F * (1.0F - progress);
            armR.xRot = slamAngle;
            armL.xRot = slamAngle;
        } else {
            armR.xRot = Mth.cos(limbSwing * 0.4F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            armL.xRot = Mth.cos(limbSwing * 0.4F) * 2.0F * limbSwingAmount * 0.5F;
        }
    }
}

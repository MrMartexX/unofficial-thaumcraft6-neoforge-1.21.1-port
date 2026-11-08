package thaumcraft.client.renderer.gear;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.items.armor.ItemFortressArmor;

/**
 * Authentic 1:1 port of 1.12 ModelFortressArmor (128x64 texture).
 * Features horned ornate helmet with crest gem, swappable masks (sipping fiend, grinning devil, angry ghost),
 * goggles attachment, heavy segmented pauldrons, waist belt, scroll tube, grimoire, and skirt plates.
 */
public class TCFortressArmorModel extends HumanoidModel<LivingEntity> {
    private final boolean inner;

    // Helm ornaments & attachments
    private final ModelPart ornamentL;
    private final ModelPart ornamentL2;
    private final ModelPart ornamentR;
    private final ModelPart ornamentR2;
    private final ModelPart gemornament;
    private final ModelPart gem;
    private final ModelPart flapR;
    private final ModelPart flapL;
    private final ModelPart gogglesPart;
    private final ModelPart[] maskParts = new ModelPart[3];

    // Body ornaments (outer model only)
    private final ModelPart scroll;
    private final ModelPart book;

    // Arm pauldrons
    private final ModelPart shoulderplateRtop;
    private final ModelPart shoulderplateR1;
    private final ModelPart shoulderplateR2;
    private final ModelPart shoulderplateR3;
    private final ModelPart shoulderplateLtop;
    private final ModelPart shoulderplateL1;
    private final ModelPart shoulderplateL2;
    private final ModelPart shoulderplateL3;

    // Leg panels
    private final ModelPart sidepanelR2;
    private final ModelPart sidepanelR3;
    private final ModelPart sidepanelL2;
    private final ModelPart sidepanelL3;

    public TCFortressArmorModel(ModelPart root, boolean inner) {
        super(root);
        this.inner = inner;

        ModelPart h = root.getChild("head");
        this.ornamentL = h.getChild("ornament_l");
        this.ornamentL2 = h.getChild("ornament_l2");
        this.ornamentR = h.getChild("ornament_r");
        this.ornamentR2 = h.getChild("ornament_r2");
        this.gemornament = h.getChild("gemornament");
        this.gem = h.getChild("gem");
        this.flapR = h.getChild("flap_r");
        this.flapL = h.getChild("flap_l");
        this.gogglesPart = h.getChild("goggles");
        this.maskParts[0] = h.getChild("mask0");
        this.maskParts[1] = h.getChild("mask1");
        this.maskParts[2] = h.getChild("mask2");

        ModelPart b = root.getChild("body");
        if (!inner) {
            this.scroll = b.getChild("scroll");
            this.book = b.getChild("book");
        } else {
            this.scroll = null;
            this.book = null;
        }

        ModelPart ra = root.getChild("right_arm");
        this.shoulderplateRtop = ra.getChild("shoulderplate_rtop");
        this.shoulderplateR1 = ra.getChild("shoulderplate_r1");
        this.shoulderplateR2 = ra.getChild("shoulderplate_r2");
        this.shoulderplateR3 = ra.getChild("shoulderplate_r3");

        ModelPart la = root.getChild("left_arm");
        this.shoulderplateLtop = la.getChild("shoulderplate_ltop");
        this.shoulderplateL1 = la.getChild("shoulderplate_l1");
        this.shoulderplateL2 = la.getChild("shoulderplate_l2");
        this.shoulderplateL3 = la.getChild("shoulderplate_l3");

        ModelPart rl = root.getChild("right_leg");
        this.sidepanelR2 = rl.getChild("sidepanel_r2");
        this.sidepanelR3 = rl.getChild("sidepanel_r3");

        ModelPart ll = root.getChild("left_leg");
        this.sidepanelL2 = ll.getChild("sidepanel_l2");
        this.sidepanelL3 = ll.getChild("sidepanel_l3");
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

        // Head components
        head.addOrReplaceChild("ornament_l", CubeListBuilder.create().texOffs(78, 8).mirror()
                .addBox(1.5F, -9.0F, -6.5F, 2, 2, 1), PartPose.rotation(-0.1396263F, 0.0F, 0.0F));
        head.addOrReplaceChild("ornament_l2", CubeListBuilder.create().texOffs(78, 8).mirror()
                .addBox(3.5F, -10.0F, -6.5F, 1, 2, 1), PartPose.rotation(-0.1396263F, 0.0F, 0.0F));
        head.addOrReplaceChild("ornament_r", CubeListBuilder.create().texOffs(78, 8)
                .addBox(-3.5F, -9.0F, -6.5F, 2, 2, 1), PartPose.rotation(-0.1396263F, 0.0F, 0.0F));
        head.addOrReplaceChild("ornament_r2", CubeListBuilder.create().texOffs(78, 8)
                .addBox(-4.5F, -10.0F, -6.5F, 1, 2, 1), PartPose.rotation(-0.1396263F, 0.0F, 0.0F));
        head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(41, 8)
                .addBox(-4.5F, -9.0F, -4.5F, 9, 4, 9), PartPose.ZERO);
        head.addOrReplaceChild("helmet_r", CubeListBuilder.create().texOffs(21, 13)
                .addBox(-6.5F, -3.0F, -4.5F, 1, 5, 9), PartPose.rotation(0.0F, 0.0F, 0.5235988F));
        head.addOrReplaceChild("helmet_l", CubeListBuilder.create().texOffs(21, 13).mirror()
                .addBox(5.5F, -3.0F, -4.5F, 1, 5, 9), PartPose.rotation(0.0F, 0.0F, -0.5235988F));
        head.addOrReplaceChild("helmet_b", CubeListBuilder.create().texOffs(41, 21)
                .addBox(-4.5F, -3.0F, 5.5F, 9, 5, 1), PartPose.rotation(0.5235988F, 0.0F, 0.0F));
        head.addOrReplaceChild("capsthingy", CubeListBuilder.create().texOffs(21, 0)
                .addBox(-4.5F, -6.0F, -6.5F, 9, 1, 2), PartPose.ZERO);
        head.addOrReplaceChild("flap_r", CubeListBuilder.create().texOffs(59, 10)
                .addBox(-10.0F, -2.0F, -1.0F, 3, 3, 1), PartPose.rotation(0.0F, -0.5235988F, 0.5235988F));
        head.addOrReplaceChild("flap_l", CubeListBuilder.create().texOffs(59, 10).mirror()
                .addBox(7.0F, -2.0F, -1.0F, 3, 3, 1), PartPose.rotation(0.0F, 0.5235988F, -0.5235988F));
        head.addOrReplaceChild("gemornament", CubeListBuilder.create().texOffs(68, 11)
                .addBox(-1.5F, -9.0F, -7.0F, 3, 3, 2), PartPose.rotation(-0.1396263F, 0.0F, 0.0F));
        head.addOrReplaceChild("gem", CubeListBuilder.create().texOffs(72, 8)
                .addBox(-1.0F, -8.5F, -7.5F, 2, 2, 1), PartPose.rotation(-0.1396263F, 0.0F, 0.0F));
        head.addOrReplaceChild("mask0", CubeListBuilder.create().texOffs(52, 2)
                .addBox(-4.5F, -5.0F, -4.6F, 9, 5, 1), PartPose.ZERO);
        head.addOrReplaceChild("mask1", CubeListBuilder.create().texOffs(76, 2)
                .addBox(-4.5F, -5.0F, -4.6F, 9, 5, 1), PartPose.ZERO);
        head.addOrReplaceChild("mask2", CubeListBuilder.create().texOffs(100, 2)
                .addBox(-4.5F, -5.0F, -4.6F, 9, 5, 1), PartPose.ZERO);
        head.addOrReplaceChild("goggles", CubeListBuilder.create().texOffs(100, 18)
                .addBox(-4.5F, -5.0F, -4.25F, 9, 5, 1), PartPose.ZERO);

        // Body components
        if (inner) {
            body.addOrReplaceChild("mbelt", CubeListBuilder.create().texOffs(56, 55)
                    .addBox(-4.0F, 8.0F, -3.0F, 8, 4, 1), PartPose.ZERO);
            body.addOrReplaceChild("mbelt_l", CubeListBuilder.create().texOffs(76, 44)
                    .addBox(4.0F, 8.0F, -3.0F, 1, 3, 6), PartPose.ZERO);
            body.addOrReplaceChild("mbelt_r", CubeListBuilder.create().texOffs(76, 44)
                    .addBox(-5.0F, 8.0F, -3.0F, 1, 3, 6), PartPose.ZERO);
        } else {
            body.addOrReplaceChild("belt_r", CubeListBuilder.create().texOffs(76, 44)
                    .addBox(-5.0F, 4.0F, -3.0F, 1, 3, 6), PartPose.ZERO);
            body.addOrReplaceChild("belt_l", CubeListBuilder.create().texOffs(76, 44)
                    .addBox(4.0F, 4.0F, -3.0F, 1, 3, 6), PartPose.ZERO);
            body.addOrReplaceChild("chestplate", CubeListBuilder.create().texOffs(56, 45)
                    .addBox(-4.0F, 1.0F, -4.0F, 8, 7, 2), PartPose.ZERO);
            body.addOrReplaceChild("scroll", CubeListBuilder.create().texOffs(34, 27)
                    .addBox(-2.0F, 9.5F, 4.0F, 8, 3, 3), PartPose.rotation(0.0F, 0.0F, 0.1919862F));
            body.addOrReplaceChild("backplate", CubeListBuilder.create().texOffs(36, 45)
                    .addBox(-4.0F, 1.0F, 2.0F, 8, 11, 2), PartPose.ZERO);
            body.addOrReplaceChild("book", CubeListBuilder.create().texOffs(100, 8)
                    .addBox(1.0F, -0.3F, 4.0F, 5, 7, 2), PartPose.rotation(0.0F, 0.0F, 0.7679449F));
        }

        // Right Arm components
        rightArm.addOrReplaceChild("shoulder_r", CubeListBuilder.create().texOffs(56, 35)
                .addBox(-3.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.ZERO);
        rightArm.addOrReplaceChild("gauntlet_r", CubeListBuilder.create().texOffs(100, 26)
                .addBox(-3.5F, 3.5F, -2.5F, 2, 6, 5), PartPose.ZERO);
        rightArm.addOrReplaceChild("gauntletstrap_r1", CubeListBuilder.create().texOffs(84, 31)
                .addBox(-1.5F, 3.5F, -2.5F, 3, 1, 5), PartPose.ZERO);
        rightArm.addOrReplaceChild("gauntletstrap_r2", CubeListBuilder.create().texOffs(84, 31)
                .addBox(-1.5F, 6.5F, -2.5F, 3, 1, 5), PartPose.ZERO);
        rightArm.addOrReplaceChild("shoulderplate_rtop", CubeListBuilder.create().texOffs(110, 37)
                .addBox(-5.5F, -2.5F, -3.5F, 2, 1, 7), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightArm.addOrReplaceChild("shoulderplate_r1", CubeListBuilder.create().texOffs(110, 45)
                .addBox(-4.5F, -1.5F, -3.5F, 1, 4, 7), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightArm.addOrReplaceChild("shoulderplate_r2", CubeListBuilder.create().texOffs(94, 45)
                .addBox(-3.5F, 1.5F, -3.5F, 1, 3, 7), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightArm.addOrReplaceChild("shoulderplate_r3", CubeListBuilder.create().texOffs(94, 45)
                .addBox(-2.5F, 3.5F, -3.5F, 1, 3, 7), PartPose.rotation(0.0F, 0.0F, 0.4363323F));

        // Left Arm components
        leftArm.addOrReplaceChild("shoulder_l", CubeListBuilder.create().texOffs(56, 35).mirror()
                .addBox(-1.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.ZERO);
        leftArm.addOrReplaceChild("gauntlet_l", CubeListBuilder.create().texOffs(114, 26)
                .addBox(1.5F, 3.5F, -2.5F, 2, 6, 5), PartPose.ZERO);
        leftArm.addOrReplaceChild("gauntletstrap_l1", CubeListBuilder.create().texOffs(84, 31).mirror()
                .addBox(-1.5F, 3.5F, -2.5F, 3, 1, 5), PartPose.ZERO);
        leftArm.addOrReplaceChild("gauntletstrap_l2", CubeListBuilder.create().texOffs(84, 31).mirror()
                .addBox(-1.5F, 6.5F, -2.5F, 3, 1, 5), PartPose.ZERO);
        leftArm.addOrReplaceChild("shoulderplate_ltop", CubeListBuilder.create().texOffs(110, 37).mirror()
                .addBox(3.5F, -2.5F, -3.5F, 2, 1, 7), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftArm.addOrReplaceChild("shoulderplate_l1", CubeListBuilder.create().texOffs(110, 45).mirror()
                .addBox(3.5F, -1.5F, -3.5F, 1, 4, 7), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftArm.addOrReplaceChild("shoulderplate_l2", CubeListBuilder.create().texOffs(94, 45).mirror()
                .addBox(2.5F, 1.5F, -3.5F, 1, 3, 7), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftArm.addOrReplaceChild("shoulderplate_l3", CubeListBuilder.create().texOffs(94, 45).mirror()
                .addBox(1.5F, 3.5F, -3.5F, 1, 3, 7), PartPose.rotation(0.0F, 0.0F, -0.4363323F));

        // Right Leg components
        rightLeg.addOrReplaceChild("legpanel_r1", CubeListBuilder.create().texOffs(0, 51)
                .addBox(-1.0F, 0.5F, -3.5F, 3, 4, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        rightLeg.addOrReplaceChild("legpanel_r2", CubeListBuilder.create().texOffs(8, 51)
                .addBox(-1.0F, 3.5F, -2.5F, 3, 4, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        rightLeg.addOrReplaceChild("legpanel_r3", CubeListBuilder.create().texOffs(0, 56)
                .addBox(-1.0F, 6.5F, -1.5F, 3, 3, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        rightLeg.addOrReplaceChild("legpanel_r4", CubeListBuilder.create().texOffs(0, 43)
                .addBox(-3.0F, 0.5F, -3.5F, 2, 3, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        rightLeg.addOrReplaceChild("legpanel_r5", CubeListBuilder.create().texOffs(0, 47)
                .addBox(-3.0F, 2.5F, -2.5F, 2, 3, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        rightLeg.addOrReplaceChild("legpanel_r6", CubeListBuilder.create().texOffs(6, 43)
                .addBox(-3.0F, 4.5F, -1.5F, 2, 3, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        rightLeg.addOrReplaceChild("sidepanel_r1", CubeListBuilder.create().texOffs(0, 22)
                .addBox(-2.5F, 0.5F, -2.5F, 1, 4, 5), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightLeg.addOrReplaceChild("sidepanel_r2", CubeListBuilder.create().texOffs(0, 31)
                .addBox(-1.5F, 3.5F, -2.5F, 1, 3, 5), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightLeg.addOrReplaceChild("sidepanel_r3", CubeListBuilder.create().texOffs(12, 31)
                .addBox(-0.5F, 5.5F, -2.5F, 1, 3, 5), PartPose.rotation(0.0F, 0.0F, 0.4363323F));
        rightLeg.addOrReplaceChild("backpanel_r1", CubeListBuilder.create().texOffs(0, 18)
                .addBox(-3.0F, 0.5F, 2.5F, 5, 3, 1), PartPose.rotation(0.4363323F, 0.0F, 0.0F));
        rightLeg.addOrReplaceChild("backpanel_r2", CubeListBuilder.create().texOffs(0, 18)
                .addBox(-3.0F, 2.5F, 1.5F, 5, 3, 1), PartPose.rotation(0.4363323F, 0.0F, 0.0F));
        rightLeg.addOrReplaceChild("backpanel_r3", CubeListBuilder.create().texOffs(0, 18)
                .addBox(-3.0F, 4.5F, 0.5F, 5, 3, 1), PartPose.rotation(0.4363323F, 0.0F, 0.0F));

        // Left Leg components
        leftLeg.addOrReplaceChild("legpanel_l1", CubeListBuilder.create().texOffs(0, 51).mirror()
                .addBox(-2.0F, 0.5F, -3.5F, 3, 4, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        leftLeg.addOrReplaceChild("legpanel_l2", CubeListBuilder.create().texOffs(8, 51).mirror()
                .addBox(-2.0F, 3.5F, -2.5F, 3, 4, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        leftLeg.addOrReplaceChild("legpanel_l3", CubeListBuilder.create().texOffs(0, 56).mirror()
                .addBox(-2.0F, 6.5F, -1.5F, 3, 3, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        leftLeg.addOrReplaceChild("legpanel_l4", CubeListBuilder.create().texOffs(0, 43).mirror()
                .addBox(1.0F, 0.5F, -3.5F, 2, 3, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        leftLeg.addOrReplaceChild("legpanel_l5", CubeListBuilder.create().texOffs(0, 47).mirror()
                .addBox(1.0F, 2.5F, -2.5F, 2, 3, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        leftLeg.addOrReplaceChild("legpanel_l6", CubeListBuilder.create().texOffs(6, 43).mirror()
                .addBox(1.0F, 4.5F, -1.5F, 2, 3, 1), PartPose.rotation(-0.4363323F, 0.0F, 0.0F));
        leftLeg.addOrReplaceChild("sidepanel_l1", CubeListBuilder.create().texOffs(0, 22).mirror()
                .addBox(1.5F, 0.5F, -2.5F, 1, 4, 5), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftLeg.addOrReplaceChild("sidepanel_l2", CubeListBuilder.create().texOffs(0, 31).mirror()
                .addBox(0.5F, 3.5F, -2.5F, 1, 3, 5), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftLeg.addOrReplaceChild("sidepanel_l3", CubeListBuilder.create().texOffs(12, 31).mirror()
                .addBox(-0.5F, 5.5F, -2.5F, 1, 3, 5), PartPose.rotation(0.0F, 0.0F, -0.4363323F));
        leftLeg.addOrReplaceChild("backpanel_l1", CubeListBuilder.create().texOffs(0, 18).mirror()
                .addBox(-2.0F, 0.5F, 2.5F, 5, 3, 1), PartPose.rotation(0.4363323F, 0.0F, 0.0F));
        leftLeg.addOrReplaceChild("backpanel_l2", CubeListBuilder.create().texOffs(0, 18).mirror()
                .addBox(-2.0F, 2.5F, 1.5F, 5, 3, 1), PartPose.rotation(0.4363323F, 0.0F, 0.0F));
        leftLeg.addOrReplaceChild("backpanel_l3", CubeListBuilder.create().texOffs(0, 18).mirror()
                .addBox(-2.0F, 4.5F, 0.5F, 5, 3, 1), PartPose.rotation(0.4363323F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 64);
    }

    public void setupFortressProperties(LivingEntity entity, ItemStack stack, EquipmentSlot slot) {
        int set = 0;
        if (entity != null) {
            for (EquipmentSlot s : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS}) {
                if (entity.getItemBySlot(s).getItem() instanceof ItemFortressArmor) {
                    set++;
                }
            }
        }
        if (set == 0 && !stack.isEmpty() && stack.getItem() instanceof ItemFortressArmor) {
            set = 1;
        }

        ItemStack helmStack = (slot == EquipmentSlot.HEAD) ? stack :
                (entity != null ? entity.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY);
        int mask = ItemFortressArmor.getMask(helmStack);
        boolean hasGoggles = ItemFortressArmor.hasGoggles(helmStack);

        if (gogglesPart != null) gogglesPart.visible = hasGoggles;
        for (int i = 0; i < 3; i++) {
            if (maskParts[i] != null) maskParts[i].visible = (mask == i);
        }

        if (scroll != null) scroll.visible = (set >= 3);
        if (book != null) book.visible = (set >= 2);

        if (ornamentL != null) ornamentL.visible = (set >= 3);
        if (ornamentL2 != null) ornamentL2.visible = (set >= 3);
        if (ornamentR != null) ornamentR.visible = (set >= 3);
        if (ornamentR2 != null) ornamentR2.visible = (set >= 3);
        if (gemornament != null) gemornament.visible = (set >= 3);
        if (gem != null) gem.visible = (set >= 3);

        if (flapL != null) flapL.visible = (set >= 2);
        if (flapR != null) flapR.visible = (set >= 2);

        if (shoulderplateLtop != null) shoulderplateLtop.visible = (set >= 2);
        if (shoulderplateL1 != null) shoulderplateL1.visible = (set >= 2);
        if (shoulderplateL2 != null) shoulderplateL2.visible = (set >= 3);
        if (shoulderplateL3 != null) shoulderplateL3.visible = (set >= 3);

        if (shoulderplateRtop != null) shoulderplateRtop.visible = (set >= 2);
        if (shoulderplateR1 != null) shoulderplateR1.visible = (set >= 2);
        if (shoulderplateR2 != null) shoulderplateR2.visible = (set >= 3);
        if (shoulderplateR3 != null) shoulderplateR3.visible = (set >= 3);

        if (sidepanelR2 != null) sidepanelR2.visible = (set >= 2);
        if (sidepanelL2 != null) sidepanelL2.visible = (set >= 2);
        if (sidepanelR3 != null) sidepanelR3.visible = (set >= 3);
        if (sidepanelL3 != null) sidepanelL3.visible = (set >= 3);

        // 1.01f scale on head to avoid z-fighting with skin, identical to legacy
        head.xScale = 1.01F;
        head.yScale = 1.01F;
        head.zScale = 1.01F;
    }
}

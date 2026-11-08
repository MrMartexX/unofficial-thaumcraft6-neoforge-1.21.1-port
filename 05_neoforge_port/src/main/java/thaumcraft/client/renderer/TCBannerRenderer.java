package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.AABB;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blocks.basic.TCBannerBlock;
import thaumcraft.common.tiles.misc.TCBannerBlockEntity;

/** 1:1 port of legacy TileBannerRenderer + ModelBanner (pole, beam, tabs, swaying cloth, aspect symbol). */
final class TCBannerRenderer implements BlockEntityRenderer<TCBannerBlockEntity> {
    private static final ResourceLocation TEX_CULT =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/models/banner_cultist.png");
    private static final ResourceLocation TEX_BLANK =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/models/banner_blank.png");

    private final ModelPart pole;
    private final ModelPart beam;
    private final ModelPart tab1;
    private final ModelPart tab2;
    private final ModelPart cloth;

    TCBannerRenderer(BlockEntityRendererProvider.Context context) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("tab1", CubeListBuilder.create().texOffs(0, 29)
                .addBox(-5.0F, -7.5F, -1.5F, 2.0F, 3.0F, 3.0F), PartPose.ZERO);
        root.addOrReplaceChild("tab2", CubeListBuilder.create().texOffs(0, 29)
                .addBox(3.0F, -7.5F, -1.5F, 2.0F, 3.0F, 3.0F), PartPose.ZERO);
        root.addOrReplaceChild("beam", CubeListBuilder.create().texOffs(30, 0)
                .addBox(-7.0F, -7.0F, -1.0F, 14.0F, 2.0F, 2.0F), PartPose.ZERO);
        root.addOrReplaceChild("cloth", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-7.0F, 0.0F, -0.5F, 14.0F, 28.0F, 1.0F), PartPose.offset(0.0F, -5.0F, 0.0F));
        root.addOrReplaceChild("pole", CubeListBuilder.create().texOffs(62, 0)
                .addBox(0.0F, 0.0F, -1.0F, 2.0F, 31.0F, 2.0F), PartPose.offset(-1.0F, -7.0F, -2.0F));
        ModelPart baked = LayerDefinition.create(mesh, 128, 64).bakeRoot();
        pole = baked.getChild("pole");
        beam = baked.getChild("beam");
        tab1 = baked.getChild("tab1");
        tab2 = baked.getChild("tab2");
        cloth = baked.getChild("cloth");
    }

    @Override
    public void render(
            TCBannerBlockEntity banner,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        DyeColor dye = banner.getBlockState().getBlock() instanceof TCBannerBlock block ? block.dye() : null;
        int color = dye == null ? -1 : (0xFF000000 | dye.getTextureDiffuseColor());
        Aspect aspect = banner.getAspect();
        ResourceLocation texture = aspect == null && dye == null ? TEX_CULT : TEX_BLANK;
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(texture));

        poseStack.pushPose();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        if (banner.getLevel() != null) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(banner.getBannerFacing() * 360 / 16.0F));
        }
        if (!banner.getWall()) {
            pole.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        } else {
            poseStack.translate(0.0D, 1.0D, -0.4125D);
        }
        beam.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);

        int tint = color == -1 ? 0xFFFFFFFF : color;
        tab1.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, tint);
        tab2.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, tint);

        BlockPosHash hash = new BlockPosHash(banner);
        float ticks = Minecraft.getInstance().player == null ? 0.0F : Minecraft.getInstance().player.tickCount;
        float f3 = hash.value + ticks + partialTick;
        float rx = 0.02F - Mth.sin(f3 / 11.0F) * 0.02F;
        cloth.xRot = rx;
        cloth.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, tint);

        if (aspect != null) {
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.0F, 0.05001F);
            poseStack.scale(0.0375F, 0.0375F, 0.0375F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(-rx * (180.0F / (float) Math.PI) * 2.0F));
            drawAspect(buffers.getBuffer(RenderType.entityTranslucent(aspect.getImage())),
                    poseStack.last(), -8.0F, 0.0F, aspect.getColor(), 0.75F, packedLight);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private static void drawAspect(VertexConsumer c, PoseStack.Pose pose, float x, float y, int rgb, float alpha, int light) {
        float r = ((rgb >> 16) & 255) / 255.0F;
        float g = ((rgb >> 8) & 255) / 255.0F;
        float b = (rgb & 255) / 255.0F;
        float x2 = x + 16.0F;
        float y2 = y + 16.0F;
        vertex(c, pose, x, y2, 0.0F, 0.0F, 1.0F, r, g, b, alpha, light, 1.0F);
        vertex(c, pose, x2, y2, 0.0F, 1.0F, 1.0F, r, g, b, alpha, light, 1.0F);
        vertex(c, pose, x2, y, 0.0F, 1.0F, 0.0F, r, g, b, alpha, light, 1.0F);
        vertex(c, pose, x, y, 0.0F, 0.0F, 0.0F, r, g, b, alpha, light, 1.0F);
        vertex(c, pose, x, y, 0.0F, 0.0F, 0.0F, r, g, b, alpha, light, -1.0F);
        vertex(c, pose, x2, y, 0.0F, 1.0F, 0.0F, r, g, b, alpha, light, -1.0F);
        vertex(c, pose, x2, y2, 0.0F, 1.0F, 1.0F, r, g, b, alpha, light, -1.0F);
        vertex(c, pose, x, y2, 0.0F, 0.0F, 1.0F, r, g, b, alpha, light, -1.0F);
    }

    private static void vertex(VertexConsumer c, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                               float r, float g, float b, float a, int light, float nz) {
        c.addVertex(pose.pose(), x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 0.0F, nz);
    }

    @Override
    public AABB getRenderBoundingBox(TCBannerBlockEntity banner) {
        return banner.getRenderBox();
    }

    /** Legacy phase offset: x*7 + y*9 + z*13 so neighbouring banners do not sway in sync. */
    private record BlockPosHash(float value) {
        BlockPosHash(TCBannerBlockEntity banner) {
            this((float) (banner.getBlockPos().getX() * 7 + banner.getBlockPos().getY() * 9 + banner.getBlockPos().getZ() * 13));
        }
    }
}

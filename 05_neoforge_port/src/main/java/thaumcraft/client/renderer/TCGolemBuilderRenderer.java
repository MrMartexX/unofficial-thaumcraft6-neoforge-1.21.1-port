package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.crafting.TCGolemBuilderBlock;
import thaumcraft.common.tiles.crafting.TCGolemBuilderBlockEntity;

final class TCGolemBuilderRenderer implements BlockEntityRenderer<TCGolemBuilderBlockEntity> {
    private static final ResourceLocation GOLEMBUILDER_OBJ =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "models/block/golembuilder.obj");
    private static final ResourceLocation GOLEMBUILDER_TEX =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/block/golembuilder.png");

    private final ItemRenderer itemRenderer;

    TCGolemBuilderRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(
            TCGolemBuilderBlockEntity entity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay) {
        Direction facing = entity.getBlockState().hasProperty(TCGolemBuilderBlock.FACING)
                ? entity.getBlockState().getValue(TCGolemBuilderBlock.FACING)
                : Direction.NORTH;

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.0f, 0.5f);
        switch (facing) {
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(270.0f));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
            default -> {}
        }

        // 1. Render dynamic moving press part (static parts table/anvil/box are baked in the block model)
        poseStack.pushPose();
        double pressOffset = Math.sin(Math.toRadians(entity.press)) * 0.625;
        poseStack.translate(0.0, -pressOffset, 0.0);
        TCGolemObjMesh mesh = TCGolemObjMesh.get(GOLEMBUILDER_OBJ);
        VertexConsumer pressConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(GOLEMBUILDER_TEX));
        mesh.render("press", poseStack, pressConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
        poseStack.popPose();

        // 2. Render lava surface in the cauldron
        poseStack.pushPose();
        poseStack.translate(-0.3125f, 0.625f, 1.3125f);
        poseStack.mulPose(Axis.XN.rotationDegrees(90.0f));
        poseStack.scale(0.625f, 0.625f, 0.625f);
        TextureAtlasSprite lavaSprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModelShaper()
                .getParticleIcon(Blocks.LAVA.defaultBlockState());
        VertexConsumer lavaConsumer = bufferSource.getBuffer(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
        PoseStack.Pose lastPose = poseStack.last();
        lavaConsumer.addVertex(lastPose, 0, 0, 0).setColor(-1).setUv(lavaSprite.getU1(), lavaSprite.getV1()).setOverlay(packedOverlay).setLight(200).setNormal(lastPose, 0, 0, 1);
        lavaConsumer.addVertex(lastPose, 1, 0, 0).setColor(-1).setUv(lavaSprite.getU0(), lavaSprite.getV1()).setOverlay(packedOverlay).setLight(200).setNormal(lastPose, 0, 0, 1);
        lavaConsumer.addVertex(lastPose, 1, 1, 0).setColor(-1).setUv(lavaSprite.getU0(), lavaSprite.getV0()).setOverlay(packedOverlay).setLight(200).setNormal(lastPose, 0, 0, 1);
        lavaConsumer.addVertex(lastPose, 0, 1, 0).setColor(-1).setUv(lavaSprite.getU1(), lavaSprite.getV0()).setOverlay(packedOverlay).setLight(200).setNormal(lastPose, 0, 0, 1);
        poseStack.popPose();

        // 3. Render completed output item on the anvil
        ItemStack output = entity.getItem(0);
        if (!output.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.65D, 0.0D);
            long gameTime = entity.getLevel() == null ? 0L : entity.getLevel().getGameTime();
            poseStack.mulPose(Axis.YP.rotationDegrees((gameTime + partialTick) * 2.5F));
            poseStack.scale(0.6F, 0.6F, 0.6F);
            itemRenderer.renderStatic(
                    output,
                    ItemDisplayContext.GROUND,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    bufferSource,
                    entity.getLevel(),
                    0
            );
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(TCGolemBuilderBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(2.0);
    }
}

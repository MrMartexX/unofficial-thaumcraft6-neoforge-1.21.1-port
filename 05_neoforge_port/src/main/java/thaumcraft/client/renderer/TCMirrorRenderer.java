package thaumcraft.client.renderer;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.devices.TCMirrorBlock;
import thaumcraft.common.tiles.devices.TCMirrorBlockEntity;
import thaumcraft.common.tiles.devices.TCMirrorEssentiaBlockEntity;

final class TCMirrorRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private static final ResourceLocation MP =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/block/mirrorpane.png");
    private static final ResourceLocation MPT =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/block/mirrorpanetrans.png");

    TCMirrorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            T blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay) {
        if (!blockEntity.getBlockState().hasProperty(TCMirrorBlock.FACING)) {
            return;
        }

        Direction facing = blockEntity.getBlockState().getValue(TCMirrorBlock.FACING);
        boolean linked = false;
        if (blockEntity instanceof TCMirrorBlockEntity itemMirror) {
            linked = itemMirror.isLinked();
        } else if (blockEntity instanceof TCMirrorEssentiaBlockEntity essentiaMirror) {
            linked = essentiaMirror.isLinked();
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(facing.getRotation());
        poseStack.translate(-0.5, -0.5, -0.5);

        Minecraft mc = Minecraft.getInstance();
        if (linked && mc.player != null && mc.player.distanceToSqr(blockEntity.getBlockPos().getCenter()) < 1024.0) {
            float p = 0.1875f;
            Matrix4f m = poseStack.last().pose();
            VertexConsumer vc = bufferSource.getBuffer(RenderType.endPortal());
            vc.addVertex(m, p, 0.01f, p);
            vc.addVertex(m, p, 0.01f, 1.0f - p);
            vc.addVertex(m, 1.0f - p, 0.01f, 1.0f - p);
            vc.addVertex(m, 1.0f - p, 0.01f, p);
            pane(poseStack, bufferSource, MPT, packedLight);
        } else {
            pane(poseStack, bufferSource, MP, packedLight);
        }

        poseStack.popPose();
    }

    private static void pane(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation tex, int light) {
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityTranslucent(tex));
        PoseStack.Pose p = poseStack.last();
        float y = 0.02f;
        vc.addVertex(p, 0, y, 0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(p, 0, 1, 0);
        vc.addVertex(p, 0, y, 1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(p, 0, 1, 0);
        vc.addVertex(p, 1, y, 1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(p, 0, 1, 0);
        vc.addVertex(p, 1, y, 0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(p, 0, 1, 0);
    }
}

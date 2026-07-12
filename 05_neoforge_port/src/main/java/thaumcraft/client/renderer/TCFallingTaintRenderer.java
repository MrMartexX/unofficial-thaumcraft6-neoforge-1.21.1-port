package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.entities.TCFallingTaintEntity;

final class TCFallingTaintRenderer extends EntityRenderer<TCFallingTaintEntity> {
    TCFallingTaintRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.5F;
    }

    @Override
    public void render(TCFallingTaintEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        BlockState state = entity.getFallState();
        if (!state.isAir()
                && state.getRenderShape() == RenderShape.MODEL
                && !state.equals(entity.level().getBlockState(entity.blockPosition()))) {
            poseStack.pushPose();
            poseStack.translate(-0.5D, 0.0D, -0.5D);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TCFallingTaintEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

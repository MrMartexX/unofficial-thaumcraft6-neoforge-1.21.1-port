package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.world.entity.Entity;
import thaumcraft.client.lib.TintedBufferSource;
import thaumcraft.common.entities.TCMindSpiderEntity;

/**
 * Authentic renderer for Mind Spider:
 * Vanilla spider at 60% scale, nearly invisible (at most 10% alpha, fading in over 100 ticks).
 * Visible only to its hallucination target viewer.
 */
public class TCMindSpiderRenderer extends SpiderRenderer<TCMindSpiderEntity> {
    public TCMindSpiderRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        shadowRadius = 0.3f;
    }

    @Override
    protected void scale(TCMindSpiderEntity entity, PoseStack pose, float partialTick) {
        pose.scale(0.6f, 0.6f, 0.6f);
    }

    @Override
    public void render(TCMindSpiderEntity entity, float yaw, float partialTicks, PoseStack pose, MultiBufferSource buffer, int light) {
        Entity viewer = Minecraft.getInstance().getCameraEntity();
        if (entity.getViewer().isEmpty() || (viewer != null && entity.getViewer().equals(viewer.getName().getString()))) {
            float alpha = Math.min(0.1f, entity.tickCount / 100.0f);
            super.render(entity, yaw, partialTicks, pose, new TintedBufferSource(buffer, 1.0f, 1.0f, 1.0f, alpha), light);
        }
    }

    @Override
    protected RenderType getRenderType(TCMindSpiderEntity entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        return RenderType.entityTranslucent(getTextureLocation(entity));
    }
}

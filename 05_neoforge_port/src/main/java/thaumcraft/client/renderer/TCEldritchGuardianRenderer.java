package thaumcraft.client.renderer;

import javax.annotation.Nullable;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import thaumcraft.Thaumcraft;
import thaumcraft.client.lib.TintedBufferSource;
import thaumcraft.common.entities.TCEldritchGuardianEntity;

/**
 * Authentic renderer for Eldritch Guardian:
 * See-through translucent entity, at most 60% opacity, fading out with distance (24/32 blocks based on difficulty).
 */
public class TCEldritchGuardianRenderer extends MobRenderer<TCEldritchGuardianEntity, TCEldritchGuardianModel<TCEldritchGuardianEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/eldritch_guardian.png");

    public TCEldritchGuardianRenderer(EntityRendererProvider.Context context) {
        super(context, new TCEldritchGuardianModel<>(), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(TCEldritchGuardianEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(TCEldritchGuardianEntity guardian, float yaw, float partialTicks, PoseStack pose, MultiBufferSource buffer, int light) {
        float base = 0.6f;
        Entity camera = Minecraft.getInstance().getCameraEntity();
        if (camera != null) {
            float maxDistSq = (camera.level().getDifficulty() == Difficulty.HARD) ? 576.0f : 1024.0f;
            float minDistSq = 256.0f;
            double distSq = guardian.distanceToSqr(camera.getX(), camera.getY(), camera.getZ());
            if (distSq < minDistSq) {
                base = 0.6f;
            } else {
                base = (float) (1.0 - Math.min(maxDistSq - minDistSq, distSq - minDistSq) / (maxDistSq - minDistSq)) * 0.6f;
            }
        }
        pose.pushPose();
        super.render(guardian, yaw, partialTicks, pose, new TintedBufferSource(buffer, 1.0f, 1.0f, 1.0f, base), light);
        pose.popPose();
    }

    @Nullable
    @Override
    protected RenderType getRenderType(TCEldritchGuardianEntity entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        if (bodyVisible || translucent) {
            return RenderType.entityTranslucent(getTextureLocation(entity));
        }
        return super.getRenderType(entity, bodyVisible, translucent, glowing);
    }
}

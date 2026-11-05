package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCFirebatEntity;

/** The fire bat: a small bat that bobs while it flies, always full-bright (getBrightnessForRender of the original). */
public class TCFirebatRenderer extends MobRenderer<TCFirebatEntity, TCFirebatModel<TCFirebatEntity>> {
    private static final ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/firebat.png");

    public TCFirebatRenderer(EntityRendererProvider.Context rm) {
        super(rm, new TCFirebatModel<>(), 0.25f);
    }

    @Override
    protected void scale(TCFirebatEntity bat, PoseStack pose, float partialTick) {
        pose.scale(0.35f, 0.35f, 0.35f);
    }

    @Override
    protected void setupRotations(TCFirebatEntity bat, PoseStack pose, float bob, float yBodyRot, float partialTick, float scale) {
        if (!bat.isResting()) {
            pose.translate(0.0f, Mth.cos(bob * 0.3f) * 0.1f, 0.0f);
        } else {
            pose.translate(0.0f, -0.1f, 0.0f);
        }
        super.setupRotations(bat, pose, bob, yBodyRot, partialTick, scale);
    }

    @Override
    protected int getBlockLightLevel(TCFirebatEntity bat, BlockPos pos) {
        return 15;
    }

    @Override
    protected int getSkyLightLevel(TCFirebatEntity bat, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(TCFirebatEntity entity) {
        return rl;
    }
}

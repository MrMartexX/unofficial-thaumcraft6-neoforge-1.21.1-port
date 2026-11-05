package thaumcraft.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCTaintSeedEntity;

/** Port of TCTaintSeedRenderer, for both seeds. Drawn see-through and without back-face culling, like the original. */
public class TCTaintSeedRenderer extends MobRenderer<TCTaintSeedEntity, TCTaintSeedModel> {
    private static final ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/taintseed.png");

    public TCTaintSeedRenderer(EntityRendererProvider.Context rm) {
        this(rm, 0.4f);
    }

    TCTaintSeedRenderer(EntityRendererProvider.Context rm, float shadow) {
        super(rm, new TCTaintSeedModel(), shadow);
    }

    // Legacy RenderTaintSeed deliberately supplies zero body yaw.
    @Override
    protected void setupRotations(TCTaintSeedEntity seed, com.mojang.blaze3d.vertex.PoseStack pose,
            float age, float bodyYaw, float partialTick, float scale) {
        super.setupRotations(seed, pose, age, 0.0F, partialTick, scale);
    }

    @Override
    public ResourceLocation getTextureLocation(TCTaintSeedEntity entity) {
        return rl;
    }
}

package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCEldritchCrabEntity;

/** Port of TCEldritchCrabRenderer: the crab model drawn at 0.8 scale. */
public class TCEldritchCrabRenderer extends MobRenderer<TCEldritchCrabEntity, TCEldritchCrabModel> {
    private static final ResourceLocation SKIN = ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/crab.png");

    public TCEldritchCrabRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TCEldritchCrabModel(), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(TCEldritchCrabEntity entity) {
        return SKIN;
    }

    @Override
    protected void scale(TCEldritchCrabEntity entity, PoseStack pose, float partialTick) {
        pose.scale(0.8f, 0.8f, 0.8f);
        super.scale(entity, pose, partialTick);
    }
}

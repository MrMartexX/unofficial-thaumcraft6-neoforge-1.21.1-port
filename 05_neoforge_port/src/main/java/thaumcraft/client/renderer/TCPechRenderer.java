package thaumcraft.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCPechEntity;

final class TCPechRenderer extends MobRenderer<TCPechEntity, TCPechModel> {
    private static final ResourceLocation[] TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/pech_forage.png"),
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/pech_thaum.png"),
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/pech_stalker.png")
    };

    TCPechRenderer(EntityRendererProvider.Context context) {
        super(context, new TCPechModel(), 0.25F);
        addLayer(new TCPechHeldItemLayer(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(TCPechEntity entity) {
        return TEXTURES[Mth.clamp(entity.getPechType().legacyId(), 0, TEXTURES.length - 1)];
    }

    @Override
    public Vec3 getRenderOffset(TCPechEntity entity, float partialTick) {
        return entity.isCrouching() ? new Vec3(0, -0.125, 0) : super.getRenderOffset(entity, partialTick);
    }
}

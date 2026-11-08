package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCSpellbatEntity;

/**
 * Authentic renderer for Spell Bat:
 * A small translucent bat tinted in the color of its spell's effects, full-bright.
 */
public class TCSpellbatRenderer extends MobRenderer<TCSpellbatEntity, TCFirebatModel<TCSpellbatEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/spellbat.png");

    public TCSpellbatRenderer(EntityRendererProvider.Context context) {
        super(context, new TCFirebatModel<>(), 0.25F);
    }

    @Override
    public void render(TCSpellbatEntity bat, float entityYaw, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        int c = bat.getColor();
        int red = (c >> 16) & 0xFF;
        int green = (c >> 8) & 0xFF;
        int blue = c & 0xFF;
        model.tint = FastColor.ARGB32.color(127, red, green, blue);
        super.render(bat, entityYaw, partialTicks, pose, buffer, packedLight);
        model.tint = -1;
    }

    @Override
    protected void scale(TCSpellbatEntity bat, PoseStack pose, float partialTick) {
        pose.scale(0.35F, 0.35F, 0.35F);
    }

    @Override
    protected void setupRotations(TCSpellbatEntity bat, PoseStack pose, float bob, float yBodyRot, float partialTick, float scale) {
        pose.translate(0.0F, -0.1F, 0.0F);
        super.setupRotations(bat, pose, bob, yBodyRot, partialTick, scale);
    }

    @Override
    protected int getBlockLightLevel(TCSpellbatEntity bat, BlockPos pos) {
        return 15;
    }

    @Override
    protected int getSkyLightLevel(TCSpellbatEntity bat, BlockPos pos) {
        return 15;
    }

    @Override
    protected RenderType getRenderType(TCSpellbatEntity entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        return RenderType.entityTranslucent(getTextureLocation(entity));
    }

    @Override
    public ResourceLocation getTextureLocation(TCSpellbatEntity entity) {
        return TEXTURE;
    }
}

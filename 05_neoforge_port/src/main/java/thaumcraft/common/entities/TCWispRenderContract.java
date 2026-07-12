package thaumcraft.common.entities;

import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public final class TCWispRenderContract {
    public static final ResourceLocation PARTICLE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/particles.png");
    public static final ResourceLocation NODE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/auranodes.png");

    public static final Layer CORE = new Layer("core", PARTICLE_TEXTURE, 64, 64, 512, 0.4F, 0xFFFFFF, 1.0F);
    public static final Layer HALO = new Layer("halo", PARTICLE_TEXTURE, 64, 64, 320, 0.75F, 0xFFFFFF, 0.25F);
    public static final Layer ASPECT_NODE = new Layer("aspect_node", NODE_TEXTURE, 32, 32, 800, 0.75F, 0x000000, 0.5F);

    private TCWispRenderContract() {
    }

    public record Layer(
            String name,
            ResourceLocation texture,
            int gridX,
            int gridY,
            int frameBase,
            float scale,
            int color,
            float alpha
    ) {
    }
}

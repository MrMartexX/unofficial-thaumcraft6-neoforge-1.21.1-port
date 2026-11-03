package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;

final class TCGolemRenderTypes extends RenderType {
    private TCGolemRenderTypes() {
        super("tc_golem_reveal", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, () -> {}, () -> {});
    }
    // Vanilla NO_DEPTH_TEST is a no-op, not an unconditional disable. Establish
    // GL_ALWAYS explicitly so this also works when reveal is the first pass.
    static final DepthTestStateShard REVEAL_DEPTH = new DepthTestStateShard("tc_reveal_always", 519) {
        @Override public void setupRenderState() { RenderSystem.enableDepthTest(); RenderSystem.depthFunc(519); }
        @Override public void clearRenderState() { RenderSystem.disableDepthTest(); RenderSystem.depthFunc(515); }
    };
    private static final Function<ResourceLocation, RenderType> REVEAL = Util.memoize(texture -> create(
            "tc_golem_reveal", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
            CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDepthTestState(REVEAL_DEPTH)
                    .setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false)));
    static RenderType reveal(ResourceLocation texture) { return REVEAL.apply(texture); }
}

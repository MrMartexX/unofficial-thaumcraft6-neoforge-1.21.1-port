package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.client.lib.TCLegacyShaders;

final class TCSealRenderTypes extends RenderType {
    private record Key(ResourceLocation texture, boolean reveal, boolean area) {}
    private static final Map<Key, RenderType> TYPES = new HashMap<>();
    private TCSealRenderTypes() {
        super("tc_seal", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 4096, false, true, () -> {}, () -> {});
    }
    static RenderType get(ResourceLocation texture, boolean reveal, boolean area) {
        return TYPES.computeIfAbsent(new Key(texture, reveal, area), key -> create("tc_seal", DefaultVertexFormat.POSITION_TEX_COLOR,
                VertexFormat.Mode.QUADS, 4096, false, true, CompositeState.builder()
                        .setShaderState(new ShaderStateShard(() -> {
                            var shader = TCLegacyShaders.legacyParticleShader();
                            if (shader == null) return GameRenderer.getPositionTexColorShader();
                            var cutoff = shader.getUniform("AlphaCutoff");
                            if (cutoff != null) cutoff.set(1F / 255F);
                            return shader;
                        })).setTextureState(new TextureStateShard(key.texture, false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL)
                        .setDepthTestState(key.reveal ? TCGolemRenderTypes.REVEAL_DEPTH : LEQUAL_DEPTH_TEST)
                        .setWriteMaskState(key.area || key.reveal ? COLOR_WRITE : COLOR_DEPTH_WRITE).createCompositeState(false)));
    }
}

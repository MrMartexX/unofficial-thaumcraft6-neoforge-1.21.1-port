package thaumcraft.client.lib;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

@EventBusSubscriber(modid = "thaumcraft", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class TCLegacyShaders {
    private static ShaderInstance legacyParticleShader;
    private static ShaderInstance legacyParticleGuiShader;

    private TCLegacyShaders() {
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        /*
         * These shader resources are intentionally registered in the minecraft namespace because
         * vanilla ShaderInstance JSON program references resolve core program names the same way
         * vanilla does. The files are still shipped by the Thaumcraft mod jar under
         * assets/minecraft/shaders/core/.
         */
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath("minecraft", "tc_legacy_particle"),
                        DefaultVertexFormat.POSITION_TEX_COLOR
                ),
                shader -> legacyParticleShader = shader
        );

        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath("minecraft", "tc_legacy_particle_gui"),
                        DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP
                ),
                shader -> legacyParticleGuiShader = shader
        );
    }

    public static ShaderInstance legacyParticleShader() {
        return legacyParticleShader;
    }

    public static ShaderInstance legacyParticleGuiShader() {
        return legacyParticleGuiShader;
    }
}

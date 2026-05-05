package thaumcraft.client.fx.legacy;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import thaumcraft.Thaumcraft;

public final class TCLegacyParticleEngine {
    private static final ResourceLocation PARTICLE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/particles.png");

    private static final List<TCLegacyFXGeneric> EFFECTS = new ArrayList<>();
    private static final List<TCLegacyFXGeneric> PENDING = new ArrayList<>();
    private static final Random RANDOM = new Random();

    private TCLegacyParticleEngine() {
    }

    public static void clear() {
        EFFECTS.clear();
        PENDING.clear();
    }

    public static void drawWispyMotes(
            Level level,
            double x,
            double y,
            double z,
            double motionX,
            double motionY,
            double motionZ,
            int age,
            float red,
            float green,
            float blue,
            float gravity
    ) {
        if (!level.isClientSide()) {
            return;
        }

        PENDING.add(TCLegacyFXGeneric.wispyMote(
                x,
                y,
                z,
                motionX,
                motionY,
                motionZ,
                age,
                red,
                green,
                blue,
                gravity,
                level.random.nextFloat()
        ));
    }

    public static void tick() {
        if (!PENDING.isEmpty()) {
            EFFECTS.addAll(PENDING);
            PENDING.clear();
        }

        Iterator<TCLegacyFXGeneric> iterator = EFFECTS.iterator();

        while (iterator.hasNext()) {
            TCLegacyFXGeneric effect = iterator.next();
            effect.tick(RANDOM);

            if (effect.isRemoved()) {
                iterator.remove();
            }
        }
    }

    public static void render(Camera camera, float partialTicks) {
        if (!PENDING.isEmpty()) {
            EFFECTS.addAll(PENDING);
            PENDING.clear();
        }

        if (EFFECTS.isEmpty()) {
            return;
        }

        boolean hasRenderableEffect = false;

        for (TCLegacyFXGeneric effect : EFFECTS) {
            if (effect.canRender()) {
                hasRenderableEffect = true;
                break;
            }
        }

        if (!hasRenderableEffect) {
            return;
        }

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getParticleShader);
        RenderSystem.setShaderTexture(0, PARTICLE_TEXTURE);

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);

        for (TCLegacyFXGeneric effect : EFFECTS) {
            effect.render(buffer, camera, partialTicks);
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }
}
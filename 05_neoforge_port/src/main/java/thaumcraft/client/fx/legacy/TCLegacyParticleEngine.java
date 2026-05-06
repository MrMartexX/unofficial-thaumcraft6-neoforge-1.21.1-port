package thaumcraft.client.fx.legacy;

import com.mojang.blaze3d.platform.GlStateManager;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import thaumcraft.Thaumcraft;
import thaumcraft.common.lib.fx.TCLegacyFXData;

public final class TCLegacyParticleEngine {
    private static final int MAX_LAYERS = 6;
    private static final int MAX_WORLD_LAYER = 3;

    private static final ResourceLocation PARTICLE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/particles.png");

    private static final List<TCLegacyFXGeneric>[] EFFECTS = createLayerLists();
    private static final List<DelayedEffect> DELAYED = new ArrayList<>();
    private static final Random RANDOM = new Random();

    private TCLegacyParticleEngine() {
    }

    @SuppressWarnings("unchecked")
    private static List<TCLegacyFXGeneric>[] createLayerLists() {
        List<TCLegacyFXGeneric>[] lists = new List[MAX_LAYERS];

        for (int i = 0; i < lists.length; i++) {
            lists[i] = new ArrayList<>();
        }

        return lists;
    }

    public static void clear() {
        for (List<TCLegacyFXGeneric> layer : EFFECTS) {
            layer.clear();
        }

        DELAYED.clear();
    }

    public static void addEffect(
            Level level,
            TCLegacyFXData data,
            double x,
            double y,
            double z,
            double motionX,
            double motionY,
            double motionZ,
            int delay
    ) {
        if (!level.isClientSide() || Minecraft.getInstance().level == null) {
            return;
        }

        TCLegacyFXGeneric effect = new TCLegacyFXGeneric(data, x, y, z, motionX, motionY, motionZ);

        if (delay > 0) {
            DELAYED.add(new DelayedEffect(effect, delay));
            return;
        }

        addEffectNow(effect);
    }

    public static void tick() {
        updateDelayedEffects();

        for (List<TCLegacyFXGeneric> layer : EFFECTS) {
            Iterator<TCLegacyFXGeneric> iterator = layer.iterator();

            while (iterator.hasNext()) {
                TCLegacyFXGeneric effect = iterator.next();
                effect.tick(RANDOM);

                if (effect.isRemoved()) {
                    iterator.remove();
                }
            }
        }
    }

    public static void render(Camera camera, float partialTicks) {
        if (getWorldRenderableEffectCount() == 0) {
            return;
        }

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.disableCull();

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, PARTICLE_TEXTURE);

        for (int layerIndex = MAX_WORLD_LAYER; layerIndex >= 0; layerIndex--) {
            applyLegacyLayerState(layerIndex);
            renderLayer(EFFECTS[layerIndex], camera, partialTicks);
            restoreLegacyLayerState(layerIndex);
        }

        RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }

    private static void updateDelayedEffects() {
        if (DELAYED.isEmpty()) {
            return;
        }

        Iterator<DelayedEffect> iterator = DELAYED.iterator();

        while (iterator.hasNext()) {
            DelayedEffect delayed = iterator.next();
            delayed.delay--;

            if (delayed.delay <= 0) {
                addEffectNow(delayed.effect);
                iterator.remove();
            }
        }
    }

    private static void addEffectNow(TCLegacyFXGeneric effect) {
        if (shouldCullForParticleSettings()) {
            return;
        }

        int layer = clampLayer(effect.getLayer());
        List<TCLegacyFXGeneric> parts = EFFECTS[layer];
        int limit = getParticleLimit();

        while (parts.size() >= limit) {
            parts.remove(0);
        }

        parts.add(effect);
    }

    private static boolean shouldCullForParticleSettings() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            return true;
        }

        int particleSetting = getLegacyParticleSetting(minecraft);

        if (minecraft.getFps() < 30) {
            particleSetting++;
        }

        return minecraft.level.random.nextInt(3) < particleSetting;
    }

    private static int getParticleLimit() {
        ParticleStatus status = Minecraft.getInstance().options.particles().get();

        return switch (status) {
            case MINIMAL -> 500;
            case DECREASED -> 1000;
            case ALL -> 2000;
        };
    }

    private static int getLegacyParticleSetting(Minecraft minecraft) {
        ParticleStatus status = minecraft.options.particles().get();

        return switch (status) {
            case ALL -> 0;
            case DECREASED -> 1;
            case MINIMAL -> 2;
        };
    }

    private static void applyLegacyLayerState(int layer) {
        switch (layer) {
            case 0, 2 -> RenderSystem.blendFunc(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE
            );
            case 1, 3 -> RenderSystem.blendFunc(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
            );
            default -> RenderSystem.defaultBlendFunc();
        }

        if (layer == 2 || layer == 3) {
            RenderSystem.disableDepthTest();
        }
    }

    private static void restoreLegacyLayerState(int layer) {
        if (layer == 2 || layer == 3) {
            RenderSystem.enableDepthTest();
        }
    }

    private static void renderLayer(List<TCLegacyFXGeneric> layer, Camera camera, float partialTicks) {
        boolean hasRenderableEffect = false;

        for (TCLegacyFXGeneric effect : layer) {
            if (effect.canRender()) {
                hasRenderableEffect = true;
                break;
            }
        }

        if (!hasRenderableEffect) {
            return;
        }

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        for (TCLegacyFXGeneric effect : layer) {
            effect.render(buffer, camera, partialTicks);
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static int clampLayer(int layer) {
        if (layer < 0) {
            return 0;
        }

        return Math.min(layer, MAX_LAYERS - 1);
    }

    private static int getWorldRenderableEffectCount() {
        int count = 0;

        for (int i = 0; i <= MAX_WORLD_LAYER; i++) {
            for (TCLegacyFXGeneric effect : EFFECTS[i]) {
                if (effect.canRender()) {
                    count++;
                }
            }
        }

        return count;
    }

    public static int getDelayedEffectCount() {
        return DELAYED.size();
    }

    public static int getActiveEffectCount() {
        int count = 0;

        for (List<TCLegacyFXGeneric> layer : EFFECTS) {
            count += layer.size();
        }

        return count;
    }

    public static int getRenderableEffectCount() {
        int count = 0;

        for (List<TCLegacyFXGeneric> layer : EFFECTS) {
            for (TCLegacyFXGeneric effect : layer) {
                if (effect.canRender()) {
                    count++;
                }
            }
        }

        return count;
    }

    public static String getDebugStats() {
        return "delayed=" + getDelayedEffectCount()
                + ", active=" + getActiveEffectCount()
                + ", renderable=" + getRenderableEffectCount();
    }

    private static final class DelayedEffect {
        private final TCLegacyFXGeneric effect;
        private int delay;

        private DelayedEffect(TCLegacyFXGeneric effect, int delay) {
            this.effect = effect;
            this.delay = delay;
        }
    }
}
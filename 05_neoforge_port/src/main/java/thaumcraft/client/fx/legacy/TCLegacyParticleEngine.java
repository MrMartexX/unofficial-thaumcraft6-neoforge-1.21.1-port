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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import thaumcraft.Thaumcraft;
import thaumcraft.client.lib.TCLegacyShaders;
import thaumcraft.common.lib.fx.TCLegacyFXData;

public final class TCLegacyParticleEngine {
    private static final int MAX_LAYERS = 6;
    private static final int MAX_WORLD_LAYER = 3;
    private static final float LEGACY_ALPHA_CUTOFF = 0.003921569F;

    public static final ResourceLocation PARTICLE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/particles.png");

    private static final List<TCLegacyFXGeneric>[] EFFECTS = createLayerLists();
    private static final List<TCLegacyFXGenericGui>[] GUI_EFFECTS = createGuiLayerLists();
    private static final List<DelayedEffect> DELAYED = new ArrayList<>();
    private static final List<DelayedGuiEffect> DELAYED_GUI = new ArrayList<>();
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

    @SuppressWarnings("unchecked")
    private static List<TCLegacyFXGenericGui>[] createGuiLayerLists() {
        List<TCLegacyFXGenericGui>[] lists = new List[MAX_LAYERS];

        for (int i = 0; i < lists.length; i++) {
            lists[i] = new ArrayList<>();
        }

        return lists;
    }

    public static void clear() {
        for (List<TCLegacyFXGeneric> layer : EFFECTS) {
            layer.clear();
        }

        for (List<TCLegacyFXGenericGui> layer : GUI_EFFECTS) {
            layer.clear();
        }

        DELAYED.clear();
        DELAYED_GUI.clear();
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
        if (level == null || !level.isClientSide() || Minecraft.getInstance().level == null) {
            return;
        }

        TCLegacyFXGeneric effect = new TCLegacyFXGeneric(level, data, x, y, z, motionX, motionY, motionZ);

        if (delay > 0) {
            DELAYED.add(new DelayedEffect(effect, delay));
            return;
        }

        addEffectNow(effect);
    }

    public static void addGuiEffect(
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
        if (level == null || !level.isClientSide() || Minecraft.getInstance().level == null) {
            return;
        }

        TCLegacyFXGenericGui effect = new TCLegacyFXGenericGui(data, x, y, z, motionX, motionY, motionZ);

        if (delay > 0) {
            DELAYED_GUI.add(new DelayedGuiEffect(effect, delay));
            return;
        }

        addGuiEffectNow(effect);
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

    public static void tickGui() {
        updateDelayedGuiEffects();

        for (List<TCLegacyFXGenericGui> layer : GUI_EFFECTS) {
            Iterator<TCLegacyFXGenericGui> iterator = layer.iterator();

            while (iterator.hasNext()) {
                TCLegacyFXGenericGui effect = iterator.next();
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

        setLegacyParticleShader();
        RenderSystem.setShaderTexture(0, PARTICLE_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

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

    public static void renderGui(GuiGraphics guiGraphics, float partialTicks) {
        if (getGuiRenderableEffectCount() == 0) {
            return;
        }

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.disableCull();

        setLegacyParticleShader();
        RenderSystem.setShaderTexture(0, PARTICLE_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (int layerIndex = 5; layerIndex >= 4; layerIndex--) {
            applyLegacyGuiLayerState(layerIndex);
            renderGuiLayer(GUI_EFFECTS[layerIndex], guiGraphics, partialTicks);
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void setLegacyParticleShader() {
        ShaderInstance shader = TCLegacyShaders.legacyParticleShader();

        if (shader != null) {
            RenderSystem.setShader(() -> shader);
        } else {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        }
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

    private static void updateDelayedGuiEffects() {
        if (DELAYED_GUI.isEmpty()) {
            return;
        }

        Iterator<DelayedGuiEffect> iterator = DELAYED_GUI.iterator();

        while (iterator.hasNext()) {
            DelayedGuiEffect delayed = iterator.next();
            delayed.delay--;

            if (delayed.delay <= 0) {
                addGuiEffectNow(delayed.effect);
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

    private static void addGuiEffectNow(TCLegacyFXGenericGui effect) {
        if (shouldCullForParticleSettings()) {
            return;
        }

        int layer = clampLayer(effect.getLayer());
        List<TCLegacyFXGenericGui> parts = GUI_EFFECTS[layer];
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

    private static void applyLegacyGuiLayerState(int layer) {
        switch (layer) {
            case 4 -> RenderSystem.blendFunc(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE
            );
            case 5 -> RenderSystem.blendFunc(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
            );
            default -> RenderSystem.defaultBlendFunc();
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

        /*
         * Legacy ParticleEngine used alphaFunc(1/255) before rendering world FX. The custom
         * tc_legacy_particle shader provides that cutoff in modern shader form.
         */
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        for (TCLegacyFXGeneric effect : layer) {
            effect.render(buffer, camera, partialTicks);
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void renderGuiLayer(List<TCLegacyFXGenericGui> layer, GuiGraphics guiGraphics, float partialTicks) {
        for (TCLegacyFXGenericGui effect : layer) {
            if (effect.canRender()) {
                effect.render(guiGraphics, partialTicks);
            }
        }
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

    private static int getGuiRenderableEffectCount() {
        int count = 0;

        for (int i = 4; i <= 5; i++) {
            for (TCLegacyFXGenericGui effect : GUI_EFFECTS[i]) {
                if (effect.canRender()) {
                    count++;
                }
            }
        }

        return count;
    }

    public static int getDelayedEffectCount() {
        return DELAYED.size() + DELAYED_GUI.size();
    }

    public static int getActiveEffectCount() {
        int count = 0;

        for (List<TCLegacyFXGeneric> layer : EFFECTS) {
            count += layer.size();
        }

        for (List<TCLegacyFXGenericGui> layer : GUI_EFFECTS) {
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

        for (List<TCLegacyFXGenericGui> layer : GUI_EFFECTS) {
            for (TCLegacyFXGenericGui effect : layer) {
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
                + ", renderable=" + getRenderableEffectCount()
                + ", alphaCutoff=" + LEGACY_ALPHA_CUTOFF;
    }

    private static final class DelayedEffect {
        private final TCLegacyFXGeneric effect;
        private int delay;

        private DelayedEffect(TCLegacyFXGeneric effect, int delay) {
            this.effect = effect;
            this.delay = delay;
        }
    }

    private static final class DelayedGuiEffect {
        private final TCLegacyFXGenericGui effect;
        private int delay;

        private DelayedGuiEffect(TCLegacyFXGenericGui effect, int delay) {
            this.effect = effect;
            this.delay = delay;
        }
    }
}

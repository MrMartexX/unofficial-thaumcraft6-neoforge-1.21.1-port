package thaumcraft.client.gui;

import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.client.lib.TCClientRenderTime;
import thaumcraft.client.lib.TCGuiUtils;
import thaumcraft.common.research.TCKnowledgeClientCache;
import thaumcraft.common.research.TCKnowledgeGainPayload;
import thaumcraft.common.research.TCKnowledgeType;
import thaumcraft.common.research.TCPlayerKnowledge;
import thaumcraft.common.research.TCResearchCategoryDefinition;
import thaumcraft.common.research.TCResearchManager;

/**
 * Client HUD port of legacy HudHandler.renderKnowledgeGains.
 */
public final class TCKnowledgeGainHud {
    private static final ResourceLocation LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "knowledge_gain_hud");
    private static final ResourceLocation BOOK =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/items/thaumonomicon.png");
    private static final ResourceLocation KNOWLEDGE_THEORY =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/research/knowledge_theory.png");
    private static final ResourceLocation KNOWLEDGE_OBSERVATION =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/research/knowledge_observation.png");
    private static final ResourceLocation PARTICLES =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/particles.png");
    private static final SoundEvent SOUND_LEARN =
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "learn"));
            /*
     * Legacy draws one additive quad through TexturedQuadTC with lightmap brightness=200.
     * The modern GUI shader path makes that same single quad either too white or too hard-edged.
     * This two-pass emulation keeps the legacy RGB range/frame/timing, but separates the effect
     * into a low-alpha outer halo and a smaller core so the edge falls off more like TC6.
     */
/*
     * Modern GUI rendering lacks the legacy lightmap path used by TexturedQuadTC.draw(brightness=200).
     * These constants intentionally attenuate the additive flare so it matches the softer TC6 look.
     */
/*
     * Legacy renderQuadCentered used lightmap brightness=200 with additive blending.
     * The modern GUI shader path has no equivalent lightmap, so emulate it explicitly:
     * - scale the RGB contribution against vanilla fullbright 240
     * - lower alpha a little so additive SRC_ALPHA, ONE does not blow out to white
     */
    private static final Queue<KnowledgeGainTracker> KNOWLEDGE_GAIN_TRACKERS = new ConcurrentLinkedQueue<>();
    private static float kgFade = 0.0F;
    private static int lastClientTick = Integer.MIN_VALUE;

    private TCKnowledgeGainHud() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(TCKnowledgeGainHud::onRegisterGuiLayers);
    }

    public static void accept(TCKnowledgeGainPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null || payload == null) {
            return;
        }

        int progress = 40 + minecraft.level.random.nextInt(20);
        long seed = minecraft.level.random.nextLong();
        KNOWLEDGE_GAIN_TRACKERS.add(new KnowledgeGainTracker(payload.knowledgeType(), payload.category(), progress, seed));
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SOUND_LEARN, 1.0F));
    }

    public static void renderOverScreen(GuiGraphics guiGraphics) {
        renderInternal(guiGraphics, TCClientRenderTime.guiPartialTick());
    }

    public static void renderOverScreen(GuiGraphics guiGraphics, float partialTick) {
        renderInternal(guiGraphics, TCClientRenderTime.update(partialTick));
    }

    private static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(LAYER_ID, TCKnowledgeGainHud::renderLayer);
    }

    private static void renderLayer(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        float renderTickTime = TCClientRenderTime.update(deltaTracker);
        if (Minecraft.getInstance().screen instanceof TCResearchTableScreen) {
            return;
        }

        renderInternal(guiGraphics, renderTickTime);
    }

    private static void renderInternal(GuiGraphics guiGraphics, float renderTickTime) {
        Minecraft minecraft = Minecraft.getInstance();
        tickOncePerClientTick(minecraft);

        if (minecraft.player == null || kgFade <= 0.0F) {
            return;
        }

        int ww = minecraft.getWindow().getGuiScaledWidth();
        int hh = minecraft.getWindow().getGuiScaledHeight();

        renderBookIcon(guiGraphics, ww, hh);

        if (KNOWLEDGE_GAIN_TRACKERS.isEmpty()) {
            return;
        }

        Queue<KnowledgeGainTracker> temp = new ConcurrentLinkedQueue<>();
        int index = 0;
        while (!KNOWLEDGE_GAIN_TRACKERS.isEmpty()) {
            KnowledgeGainTracker current = KNOWLEDGE_GAIN_TRACKERS.poll();
            if (current != null) {
                renderKnowledgeGain(guiGraphics, current, renderTickTime, ww, hh, index);
                temp.offer(current);
                index++;
            }
        }

        while (!temp.isEmpty()) {
            KNOWLEDGE_GAIN_TRACKERS.offer(temp.poll());
        }
    }

    private static void tickOncePerClientTick(Minecraft minecraft) {
        if (minecraft.level == null || minecraft.player == null) {
            KNOWLEDGE_GAIN_TRACKERS.clear();
            kgFade = 0.0F;
            lastClientTick = Integer.MIN_VALUE;
            return;
        }

        int tick = minecraft.player.tickCount;
        if (tick == lastClientTick) {
            return;
        }
        lastClientTick = tick;

        TCKnowledgeGainPayload payload;
        while ((payload = TCKnowledgeClientCache.pollKnowledgeGain()) != null) {
            accept(payload);
        }

        if (KNOWLEDGE_GAIN_TRACKERS.isEmpty()) {
            if (kgFade > 0.0F) {
                kgFade--;
            }
            return;
        }

        kgFade += 10.0F;
        if (kgFade > 40.0F) {
            kgFade = 40.0F;
        }

        Queue<KnowledgeGainTracker> temp = new ConcurrentLinkedQueue<>();
        while (!KNOWLEDGE_GAIN_TRACKERS.isEmpty()) {
            KnowledgeGainTracker current = KNOWLEDGE_GAIN_TRACKERS.poll();
            if (current != null && current.progress > 0) {
                current.progress--;
                temp.offer(current);
            }
        }

        while (!temp.isEmpty()) {
            KNOWLEDGE_GAIN_TRACKERS.offer(temp.poll());
        }
    }

    private static void renderBookIcon(GuiGraphics guiGraphics, int ww, int hh) {
        TCGuiUtils.drawTexturedQuadFull(
                guiGraphics,
                BOOK,
                ww - 17.0F,
                hh - 17.0F,
                900.0F,
                16.0F,
                16.0F,
                1.0F,
                1.0F,
                1.0F,
                kgFade / 40.0F,
                771
        );
    }

    private static void renderKnowledgeGain(GuiGraphics guiGraphics, KnowledgeGainTracker current, float renderTickTime, int ww, int hh, int stackIndex) {
        Random rand = new Random(current.seed);
        float s = 16.0F;
        float x = ww / 4.0F + rand.nextInt(32);
        float y = hh / 3.0F + rand.nextInt(32);
        float wot = 0.0F;

        if (current.progress < current.max * 0.66F) {
            float q = (current.progress - renderTickTime) / (current.max * 0.66F);
            s *= q;
            float m = (float) Math.sin(q * Math.PI - (Math.PI / 2.0D)) * 0.5F + 0.5F;
            y *= m;
            float d = (float) Math.sin(m * Math.PI * 0.5D);
            x *= d;
        } else {
            wot = current.max - current.progress + renderTickTime;
            float wot2 = wot / (current.max * 0.33F);
            float m = (float) Math.sin(wot2 * Math.PI * 2.0D - (Math.PI / 2.0D)) * 0.5F + 1.5F;
            if (wot2 < 0.5F) {
                s *= wot2 * 2.0F;
            }
            s *= m;
        }

        float xx = ww - 12 + rand.nextInt(8) - x;
        float yy = hh - 12 + rand.nextInt(8) - y;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(xx, yy, 930.0F + stackIndex);
        guiGraphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-(84 + rand.nextInt(12))));

        renderStartFlareIfNeeded(guiGraphics, current, wot, rand);

        TCGuiUtils.renderQuadCentered(guiGraphics, knowledgeTexture(current.type), s, 1.0F, 1.0F, 1.0F, 220, 771, 1.0F);

        ResourceLocation categoryIcon = categoryIcon(current.category);
        if (categoryIcon != null) {
            guiGraphics.pose().translate(0.0F, 0.0F, 1.0F);
            TCGuiUtils.renderQuadCentered(guiGraphics, categoryIcon, s * 0.75F, 1.0F, 1.0F, 1.0F, 220, 771, 1.0F);
        }

        renderEndFlareIfNeeded(guiGraphics, current, renderTickTime, rand, xx, yy, ww, hh);

        guiGraphics.pose().popPose();
    }

    private static void renderStartFlareIfNeeded(GuiGraphics guiGraphics, KnowledgeGainTracker current, float wot, Random rand) {
        if (current.progress <= current.max * 0.9F) {
            return;
        }

        float wot3 = wot / (current.max * 0.1F);
        float m2 = (float) Math.sin(wot3 * Math.PI * 2.0D - (Math.PI / 2.0D)) * 0.25F + 0.25F;
        float size = 64.0F * m2;

        if (size <= 0.0F) {
            return;
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-rand.nextInt(360)));
        renderLegacyKnowledgeFlareQuad(guiGraphics, rand, size);
        guiGraphics.pose().popPose();
    }
    private static void renderEndFlareIfNeeded(GuiGraphics guiGraphics, KnowledgeGainTracker current, float renderTickTime, Random rand, float xx, float yy, int ww, int hh) {
        if (current.progress >= current.max * 0.1F) {
            return;
        }

        float wot3 = 1.0F - (current.progress - renderTickTime) / (current.max * 0.1F);
        float m2 = (float) Math.sin(wot3 * Math.PI * 2.0D - (Math.PI / 2.0D)) * 0.25F + 0.25F;
        float size = 32.0F * m2;

        if (size <= 0.0F) {
            return;
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-rand.nextInt(360)));
        renderLegacyKnowledgeFlareQuad(guiGraphics, rand, size);
        guiGraphics.pose().popPose();
    }
    private static void renderLegacyKnowledgeFlareQuad(GuiGraphics guiGraphics, Random rand, float size) {
        float r2 = legacyKnowledgeParticleColor(rand, 255, 255);
        float g2 = legacyKnowledgeParticleColor(rand, 189, 255);
        float b2 = legacyKnowledgeParticleColor(rand, 64, 255);
        TCGuiUtils.renderQuadCentered(
                guiGraphics,
                PARTICLES,
                64,
                64,
                320 + rand.nextInt(16),
                size,
                r2,
                g2,
                b2,
                200,
                1,
                1.0F
        );
    }
    private static float legacyKnowledgeParticleColor(Random rand, int minInclusive, int maxInclusive) {
        if (maxInclusive <= minInclusive) {
            return minInclusive / 255.0F;
        }
        return (minInclusive + rand.nextInt(maxInclusive - minInclusive + 1)) / 255.0F;
    }
    private static ResourceLocation knowledgeTexture(TCKnowledgeType type) {
        return type == TCKnowledgeType.THEORY ? KNOWLEDGE_THEORY : KNOWLEDGE_OBSERVATION;
    }

    private static ResourceLocation categoryIcon(String category) {
        String normalized = TCPlayerKnowledge.normalizeCategory(category);
        if (normalized.isBlank()) {
            return null;
        }

        for (TCResearchCategoryDefinition definition : TCResearchManager.categories()) {
            if (definition.key().equalsIgnoreCase(normalized)) {
                return definition.icon();
            }
        }
        return null;
    }

    private static final class KnowledgeGainTracker {
        private final TCKnowledgeType type;
        private final String category;
        private int progress;
        private final int max;
        private final long seed;
        @SuppressWarnings("unused")
        private boolean sparks = false;

        private KnowledgeGainTracker(TCKnowledgeType type, String category, int progress, long seed) {
            this.type = Objects.requireNonNull(type, "type");
            this.category = TCPlayerKnowledge.normalizeCategory(category);
            if (type == TCKnowledgeType.THEORY) {
                progress += 10;
            }
            this.progress = progress;
            this.max = progress;
            this.seed = seed;
        }
    }
}

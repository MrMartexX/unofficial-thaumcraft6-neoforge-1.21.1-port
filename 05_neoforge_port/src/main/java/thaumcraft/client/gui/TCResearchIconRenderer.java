package thaumcraft.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.casters.TCFocusElementDefinition;
import thaumcraft.common.items.casters.TCFocusElements;
import thaumcraft.common.research.TCResearchIconResolver;
import thaumcraft.common.research.TCResearchIconTextureLayout;

public final class TCResearchIconRenderer {
    private static final int LEGACY_ICON_SIZE = 16;
    private static final long LEGACY_FRAME_TIME_MILLIS = 150L;
    private static final Map<ResourceLocation, TCResearchIconTextureLayout> TEXTURE_LAYOUTS =
            new ConcurrentHashMap<>();

    private TCResearchIconRenderer() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(TCResearchIconRenderer::registerReloadListener);
    }

    private static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) resourceManager -> TEXTURE_LAYOUTS.clear());
    }

    static boolean render(GuiGraphics graphics, String raw, int x, int y, int focusBrightness) {
        TCResearchIconResolver.ResolvedIcon icon = TCResearchIconResolver.resolve(raw);
        return switch (icon.kind()) {
            case TEXTURE -> {
                drawResearchTexture(graphics, icon.resource(), x, y);
                yield true;
            }
            case ITEM -> {
                ItemStack stack = BuiltInRegistries.ITEM.getOptional(icon.resource())
                        .map(ItemStack::new)
                        .orElse(ItemStack.EMPTY);
                if (!stack.isEmpty()) {
                    graphics.renderItem(stack, x, y);
                    yield true;
                }
                yield false;
            }
            case FOCUS -> TCFocusElements.get(icon.focusKey())
                    .map(definition -> {
                        renderFocus(graphics, definition, x + 8, y + 8, focusBrightness);
                        return true;
                    })
                    .orElse(false);
            case UNKNOWN -> false;
        };
    }

    private static void drawResearchTexture(
            GuiGraphics graphics,
            ResourceLocation texture,
            int x,
            int y
    ) {
        TCResearchIconTextureLayout layout =
                TEXTURE_LAYOUTS.computeIfAbsent(texture, TCResearchIconRenderer::inspectTexture);
        if (!layout.known()) {
            drawFullTexture(graphics, texture, x, y, LEGACY_ICON_SIZE, LEGACY_ICON_SIZE);
            return;
        }

        int frame = layout.frameAt(System.currentTimeMillis(), LEGACY_FRAME_TIME_MILLIS);
        int u = layout.uOffset(frame);
        int v = layout.vOffset(frame);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.blit(
                texture,
                x,
                y,
                LEGACY_ICON_SIZE,
                LEGACY_ICON_SIZE,
                u,
                v,
                layout.frameWidth(),
                layout.frameHeight(),
                layout.textureWidth(),
                layout.textureHeight()
        );
    }

    private static TCResearchIconTextureLayout inspectTexture(ResourceLocation texture) {
        Resource resource = Minecraft.getInstance().getResourceManager().getResource(texture).orElse(null);
        if (resource == null) {
            Thaumcraft.LOGGER.warn("Missing Thaumonomicon research icon texture {}", texture);
            return TCResearchIconTextureLayout.unknown();
        }

        try (InputStream stream = resource.open(); NativeImage image = NativeImage.read(stream)) {
            return TCResearchIconTextureLayout.fromDimensions(image.getWidth(), image.getHeight());
        } catch (IOException | RuntimeException exception) {
            Thaumcraft.LOGGER.warn("Could not inspect Thaumonomicon research icon texture {}", texture, exception);
            return TCResearchIconTextureLayout.unknown();
        }
    }

    private static void renderFocus(
            GuiGraphics graphics,
            TCFocusElementDefinition definition,
            int centerX,
            int centerY,
            int brightness
    ) {
        float scale = definition.kind() == TCFocusElementDefinition.Kind.MOD
                || definition.kind() == TCFocusElementDefinition.Kind.ROOT ? 48.0F : 24.0F;
        String path = definition.key().substring(definition.key().lastIndexOf('.') + 1)
                .replace("splittarget", "split_target")
                .replace("splittrajectory", "split_trajectory");
        ResourceLocation elementTexture = ResourceLocation.fromNamespaceAndPath(
                Thaumcraft.MODID,
                "textures/foci/" + path + ".png"
        );

        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 5.0F);
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(-90.0F));
        if (definition.kind() == TCFocusElementDefinition.Kind.EFFECT
                || definition.kind() == TCFocusElementDefinition.Kind.MEDIUM) {
            ResourceLocation backing = ResourceLocation.fromNamespaceAndPath(
                    Thaumcraft.MODID,
                    definition.kind() == TCFocusElementDefinition.Kind.EFFECT
                            ? "textures/foci/_effect.png"
                            : "textures/foci/_medium.png"
            );
            int backingSize = Math.round(scale * 0.9F);
            int color = definition.color();
            graphics.setColor(
                    ((color >> 16) & 255) / 255.0F,
                    ((color >> 8) & 255) / 255.0F,
                    (color & 255) / 255.0F,
                    220.0F / 255.0F
            );
            drawFullTexture(
                    graphics,
                    backing,
                    -backingSize / 2,
                    -backingSize / 2,
                    backingSize,
                    backingSize
            );
        }

        int iconSize = Math.round(scale / 2.0F);
        graphics.setColor(1.0F, 1.0F, 1.0F, Math.max(0, Math.min(255, brightness)) / 255.0F);
        graphics.pose().translate(0.0F, 0.0F, 1.0F);
        drawFullTexture(
                graphics,
                elementTexture,
                -iconSize / 2,
                -iconSize / 2,
                iconSize,
                iconSize
        );
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.pose().popPose();
    }

    private static void drawFullTexture(
            GuiGraphics graphics,
            ResourceLocation texture,
            int x,
            int y,
            int width,
            int height
    ) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.blit(texture, x, y, 0.0F, 0.0F, width, height, width, height);
    }

}

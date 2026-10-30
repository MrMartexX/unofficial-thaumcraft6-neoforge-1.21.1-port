package thaumcraft.client.gui;

import java.util.Locale;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.warp.TCPlayerWarp;
import thaumcraft.common.warp.TCWarpClientCache;
import thaumcraft.common.warp.TCWarpType;
import thaumcraft.common.world.aura.TCAuraClientCache;
import thaumcraft.common.world.aura.TCAuraSyncPayload;

/** Restores the TC6 Thaumometer/Goggles aura meter and Sanity Checker meter. */
public final class TCInstrumentHud {
    private static final ResourceLocation LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "instrument_hud");
    private static final ResourceLocation HUD =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/hud.png");

    private TCInstrumentHud() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(TCInstrumentHud::onRegisterGuiLayers);
    }

    private static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(LAYER_ID, TCInstrumentHud::render);
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.options.hideGui) {
            return;
        }

        int y = 2;
        if (showsAura(player)) {
            TCAuraClientCache.getCurrentAura().ifPresent(aura -> renderAura(graphics, minecraft, aura, 2));
            y += 82;
        }
        if (holds(player, TCItems.SANITY_CHECKER.get())) {
            renderSanity(graphics, minecraft, TCWarpClientCache.currentWarp(), y);
        }
    }

    private static boolean showsAura(Player player) {
        return holds(player, TCItems.THAUMOMETER.get())
                || player.getItemBySlot(EquipmentSlot.HEAD).is(TCItems.GOGGLES.get());
    }

    private static boolean holds(Player player, net.minecraft.world.item.Item item) {
        return player.getMainHandItem().is(item) || player.getOffhandItem().is(item);
    }

    private static void renderAura(GuiGraphics graphics, Minecraft minecraft, TCAuraSyncPayload aura, int y) {
        int x = 2;
        int height = 64;
        float capacity = Math.max(1.0F, aura.base());
        int visHeight = Math.min(height, Math.round(height * Math.max(0.0F, aura.vis()) / capacity));
        int fluxHeight = Math.min(height - visHeight, Math.round(height * Math.max(0.0F, aura.flux()) / capacity));

        graphics.blit(HUD, x, y, 72, 48, 16, 80);
        if (visHeight > 0) {
            graphics.fill(x + 5, y + 74 - visHeight, x + 13, y + 74, 0xD0B266E6);
        }
        if (fluxHeight > 0) {
            graphics.fill(x + 5, y + 74 - visHeight - fluxHeight, x + 13, y + 74 - visHeight, 0xD05A246E);
        }
        int markerY = y + 74 - Math.min(height, Math.round(height * Math.min(aura.base(), 525.0F) / 525.0F));
        graphics.fill(x + 2, markerY, x + 16, markerY + 1, 0xFFE8D59A);
        graphics.drawString(minecraft.font, "Vis " + oneDecimal(aura.vis()), x + 19, y + 16, 0xEEC6FF, true);
        graphics.drawString(minecraft.font, "Flux " + oneDecimal(aura.flux()), x + 19, y + 28, 0xB57AC7, true);
        graphics.drawString(minecraft.font, "Base " + aura.base(), x + 19, y + 40, 0xD8C48E, true);
    }

    private static void renderSanity(GuiGraphics graphics, Minecraft minecraft, TCPlayerWarp warp, int y) {
        int x = 1;
        int permanent = warp.get(TCWarpType.PERMANENT);
        int normal = warp.get(TCWarpType.NORMAL);
        int temporary = warp.get(TCWarpType.TEMPORARY);
        int total = Math.max(1, permanent + normal + temporary);
        float scale = total > 100 ? 100.0F / total : 1.0F;
        int p = Math.round(permanent * scale * 48.0F / 100.0F);
        int n = Math.round(normal * scale * 48.0F / 100.0F);
        int t = Math.round(temporary * scale * 48.0F / 100.0F);
        int bottom = y + 69;

        graphics.blit(HUD, x, y, 152, 0, 20, 76);
        if (t > 0) graphics.fill(x + 7, bottom - t, x + 15, bottom, 0xD0FF80FF);
        if (n > 0) graphics.fill(x + 7, bottom - t - n, x + 15, bottom - t, 0xD0BF00BF);
        if (p > 0) graphics.fill(x + 7, bottom - t - n - p, x + 15, bottom - t - n, 0xD0800080);
        graphics.blit(HUD, x, y, 176, 0, 20, 76);
        graphics.drawString(minecraft.font, "Warp " + (permanent + normal + temporary), x + 23, y + 18, 0xDDA0DD, true);
        graphics.drawString(minecraft.font, "P " + permanent + "  S " + normal + "  T " + temporary, x + 23, y + 31, 0xB784C7, true);
    }

    private static String oneDecimal(float value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }
}

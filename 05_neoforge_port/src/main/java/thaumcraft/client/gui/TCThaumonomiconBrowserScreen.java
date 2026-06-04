package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.Thaumcraft;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.research.TCResearchFlag;
import thaumcraft.common.research.TCResearchStatus;
import thaumcraft.common.research.TCThaumonomiconActionPayload;
import thaumcraft.common.research.TCThaumonomiconCategoryView;
import thaumcraft.common.research.TCThaumonomiconClientCache;
import thaumcraft.common.research.TCThaumonomiconEntryPayload;
import thaumcraft.common.research.TCThaumonomiconIndexPayload;
import thaumcraft.common.research.TCThaumonomiconResearchView;

public final class TCThaumonomiconBrowserScreen extends Screen {
    private static final ResourceLocation BROWSER =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_research_browser.png");
    private static final ResourceLocation UNKNOWN =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/aspects/_unknown.png");
    private static final int VIEWPORT_MARGIN = 16;
    private static final int GRID_SIZE = 24;
    private static String selectedCategory = "";
    private static final Map<String, Double> LAST_PAN_X = new HashMap<>();
    private static final Map<String, Double> LAST_PAN_Y = new HashMap<>();

    private double panX;
    private double panY;
    private double zoom = 1.0D;
    private String pendingResearch = "";
    private TCThaumonomiconResearchView hoveredResearch;
    private TCThaumonomiconCategoryView hoveredCategory;

    public TCThaumonomiconBrowserScreen() {
        super(Component.translatable("item.thaumcraft.thaumonomicon"));
    }

    @Override
    protected void init() {
        ensureSelectedCategory();
        panX = LAST_PAN_X.getOrDefault(selectedCategory, initialPanX());
        panY = LAST_PAN_Y.getOrDefault(selectedCategory, initialPanY());
    }

    @Override
    public void tick() {
        TCThaumonomiconEntryPayload result = TCThaumonomiconClientCache.pollLastEntryResult();
        if (result == null || pendingResearch.isBlank() || !result.researchKey().equals(pendingResearch)) {
            return;
        }
        pendingResearch = "";
        if (result.accepted() && result.entry().isPresent()) {
            playPage();
            minecraft.setScreen(new TCThaumonomiconEntryScreen(result.entry().get()));
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        TCThaumonomiconIndexPayload index = TCThaumonomiconClientCache.index();
        ensureSelectedCategory();
        hoveredResearch = null;
        hoveredCategory = null;

        graphics.fill(0, 0, width, height, 0xFF08070B);
        renderCategoryBackground(graphics);
        renderResearchLinks(graphics, index);
        renderResearchNodes(graphics, index, mouseX, mouseY);
        renderFrame(graphics);
        renderCategories(graphics, index, mouseX, mouseY);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (hoveredCategory != null) {
            selectCategory(hoveredCategory.key());
            playPage();
            return true;
        }
        if (hoveredResearch != null && pendingResearch.isBlank()) {
            int action = hoveredResearch.status() == TCResearchStatus.UNKNOWN
                    ? TCThaumonomiconActionPayload.START_RESEARCH
                    : TCThaumonomiconActionPayload.ACKNOWLEDGE_ENTRY;
            if (hoveredResearch.status() != TCResearchStatus.UNKNOWN || hoveredResearch.unlockable()) {
                pendingResearch = hoveredResearch.key();
                PacketDistributor.sendToServer(new TCThaumonomiconActionPayload(action, pendingResearch));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && insideViewport(mouseX, mouseY)) {
            panX -= dragX * zoom;
            panY -= dragY * zoom;
            rememberPan();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!insideViewport(mouseX, mouseY)) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        zoom = Math.max(1.0D, Math.min(2.0D, zoom - Math.signum(scrollY) * 0.25D));
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void renderCategoryBackground(GuiGraphics graphics) {
        TCThaumonomiconCategoryView category = selectedCategoryView();
        ResourceLocation background = parseLocation(category == null ? "" : category.background());
        int x = VIEWPORT_MARGIN;
        int y = VIEWPORT_MARGIN;
        int w = Math.max(1, width - VIEWPORT_MARGIN * 2);
        int h = Math.max(1, height - VIEWPORT_MARGIN * 2);
        if (background == null) {
            graphics.fill(x, y, x + w, y + h, 0xFF191521);
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float u = (float) Math.floorMod((long) (panX / 2.0D), 1024L);
        float v = (float) Math.floorMod((long) (panY / 2.0D), 1024L);
        graphics.blit(background, x, y, u, v, w, h, 1024, 1024);

        ResourceLocation overlay = parseLocation(category.overlay());
        if (overlay != null) {
            graphics.setColor(1.0F, 1.0F, 1.0F, 0.55F);
            graphics.blit(overlay, x, y, u, v, w, h, 1024, 1024);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private void renderResearchLinks(GuiGraphics graphics, TCThaumonomiconIndexPayload index) {
        Map<String, TCThaumonomiconResearchView> visible = new HashMap<>();
        for (TCThaumonomiconResearchView entry : categoryEntries(index)) {
            visible.put(entry.key(), entry);
        }
        for (TCThaumonomiconResearchView entry : visible.values()) {
            for (String rawParent : entry.parents()) {
                if (rawParent.startsWith("~")) {
                    continue;
                }
                TCThaumonomiconResearchView parent = visible.get(baseResearchKey(rawParent));
                if (parent == null) {
                    continue;
                }
                int color = parent.status() == TCResearchStatus.COMPLETE ? 0xFF999999 : 0xFF333333;
                drawLine(
                        graphics,
                        nodeCenterX(entry),
                        nodeCenterY(entry),
                        nodeCenterX(parent),
                        nodeCenterY(parent),
                        color
                );
            }
        }
    }

    private void renderResearchNodes(
            GuiGraphics graphics,
            TCThaumonomiconIndexPayload index,
            int mouseX,
            int mouseY
    ) {
        for (TCThaumonomiconResearchView entry : categoryEntries(index)) {
            double centerX = nodeCenterX(entry);
            double centerY = nodeCenterY(entry);
            double radius = 12.0D / zoom;
            if (centerX < VIEWPORT_MARGIN - radius || centerY < VIEWPORT_MARGIN - radius
                    || centerX > width - VIEWPORT_MARGIN + radius || centerY > height - VIEWPORT_MARGIN + radius) {
                continue;
            }
            if (mouseX >= centerX - radius && mouseX <= centerX + radius
                    && mouseY >= centerY - radius && mouseY <= centerY + radius) {
                hoveredResearch = entry;
            }
            renderResearchNode(graphics, entry, centerX, centerY);
        }
    }

    private void renderResearchNode(
            GuiGraphics graphics,
            TCThaumonomiconResearchView entry,
            double centerX,
            double centerY
    ) {
        float brightness;
        if (entry.status() == TCResearchStatus.COMPLETE) {
            brightness = 1.0F;
        } else if (entry.unlockable()) {
            brightness = (float) (Math.sin(System.currentTimeMillis() % 600L / 600.0D * Math.PI * 2.0D) * 0.25D + 0.75D);
        } else {
            brightness = 0.3F;
        }

        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 20.0D);
        graphics.pose().scale((float) (1.0D / zoom), (float) (1.0D / zoom), 1.0F);
        graphics.setColor(brightness, brightness, brightness, 1.0F);
        int frameU = entry.meta().contains("HEX") ? 112 : entry.meta().contains("ROUND") ? 144 : 80;
        int frameV = entry.meta().contains("HIDDEN") ? 80 : 48;
        blit(graphics, BROWSER, -16, -16, frameU, frameV, 32, 32, 256, 256);
        if (entry.meta().contains("SPIKY")) {
            blit(graphics, BROWSER, -16, -16, 176, frameV, 32, 32, 256, 256);
        }
        graphics.setColor(brightness, brightness, brightness, 1.0F);
        renderResearchIcon(graphics, entry, -8, -8);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (entry.flags().contains(TCResearchFlag.RESEARCH)) {
            blit(graphics, BROWSER, -17, -17, 176, 16, 16, 16, 256, 256);
        }
        if (entry.flags().contains(TCResearchFlag.PAGE)) {
            blit(graphics, BROWSER, -17, 1, 208, 16, 16, 16, 256, 256);
        }
        graphics.pose().popPose();
    }

    private void renderResearchIcon(GuiGraphics graphics, TCThaumonomiconResearchView entry, int x, int y) {
        if (entry.icons().isEmpty()) {
            drawFullTexture(graphics, UNKNOWN, x, y, 16, 16);
            return;
        }
        String raw = entry.icons().get((int) (System.currentTimeMillis() / 1000L % entry.icons().size()));
        if (raw.contains("textures/")) {
            ResourceLocation texture = parseLocation(raw);
            drawFullTexture(graphics, texture == null ? UNKNOWN : texture, x, y, 16, 16);
            return;
        }

        ResourceLocation itemId = parseLocation(raw.split(";")[0]);
        ItemStack stack = itemId == null
                ? ItemStack.EMPTY
                : BuiltInRegistries.ITEM.getOptional(itemId).map(ItemStack::new).orElse(ItemStack.EMPTY);
        if (stack.isEmpty()) {
            drawFullTexture(graphics, UNKNOWN, x, y, 16, 16);
        } else {
            graphics.renderItem(stack, x, y);
        }
    }

    private void renderFrame(GuiGraphics graphics) {
        for (int x = VIEWPORT_MARGIN; x < width - VIEWPORT_MARGIN; x += 64) {
            int segment = Math.min(64, width - VIEWPORT_MARGIN - x);
            blit(graphics, BROWSER, x, -2, 48, 13, segment, 22, 256, 256);
            blit(graphics, BROWSER, x, height - 20, 48, 13, segment, 22, 256, 256);
        }
        for (int y = VIEWPORT_MARGIN; y < height - VIEWPORT_MARGIN; y += 64) {
            int segment = Math.min(64, height - VIEWPORT_MARGIN - y);
            blit(graphics, BROWSER, -2, y, 13, 48, 22, segment, 256, 256);
            blit(graphics, BROWSER, width - 20, y, 13, 48, 22, segment, 256, 256);
        }
        blit(graphics, BROWSER, -2, -2, 13, 13, 22, 22, 256, 256);
        blit(graphics, BROWSER, -2, height - 20, 13, 13, 22, 22, 256, 256);
        blit(graphics, BROWSER, width - 20, -2, 13, 13, 22, 22, 256, 256);
        blit(graphics, BROWSER, width - 20, height - 20, 13, 13, 22, 22, 256, 256);
    }

    private void renderCategories(
            GuiGraphics graphics,
            TCThaumonomiconIndexPayload index,
            int mouseX,
            int mouseY
    ) {
        List<TCThaumonomiconCategoryView> categories = index.categories();
        int maxVisible = Math.max(1, (height - 28) / 24);
        for (int i = 0; i < Math.min(maxVisible, categories.size()); i++) {
            TCThaumonomiconCategoryView category = categories.get(i);
            int x = 1;
            int y = 10 + i * 24;
            if (category.key().equals(selectedCategory)) {
                graphics.fill(x - 1, y - 1, x + 19, y + 19, 0xAAE2C264);
            }
            ResourceLocation icon = parseLocation(category.icon());
            drawFullTexture(graphics, icon == null ? UNKNOWN : icon, x, y, 16, 16);
            if (mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18) {
                hoveredCategory = category;
            }
        }
    }

    private void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (hoveredCategory != null) {
            graphics.renderTooltip(
                    font,
                    Component.translatable("tc.research_category." + hoveredCategory.key()),
                    mouseX,
                    mouseY
            );
            return;
        }
        if (hoveredResearch == null) {
            return;
        }
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable(hoveredResearch.name()).withStyle(ChatFormatting.GOLD));
        if (hoveredResearch.status() == TCResearchStatus.UNKNOWN) {
            lines.add(Component.translatable(
                    hoveredResearch.unlockable()
                            ? "gui.thaumcraft.thaumonomicon.begin"
                            : "gui.thaumcraft.thaumonomicon.locked"
            ).withStyle(hoveredResearch.unlockable() ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY));
        } else if (hoveredResearch.status() == TCResearchStatus.IN_PROGRESS) {
            lines.add(Component.translatable(
                    "gui.thaumcraft.thaumonomicon.stage",
                    hoveredResearch.currentStage(),
                    hoveredResearch.totalStages()
            ).withStyle(ChatFormatting.AQUA));
        }
        if (hoveredResearch.flags().contains(TCResearchFlag.RESEARCH)) {
            lines.add(Component.translatable("tc.research.newresearch").withStyle(ChatFormatting.GOLD));
        }
        if (hoveredResearch.flags().contains(TCResearchFlag.PAGE)) {
            lines.add(Component.translatable("tc.research.newpage").withStyle(ChatFormatting.GREEN));
        }
        if (!pendingResearch.isBlank() && pendingResearch.equals(hoveredResearch.key())) {
            lines.add(Component.translatable("gui.thaumcraft.thaumonomicon.loading").withStyle(ChatFormatting.GRAY));
        }
        graphics.renderComponentTooltip(font, lines, mouseX, mouseY);
    }

    private List<TCThaumonomiconResearchView> categoryEntries(TCThaumonomiconIndexPayload index) {
        return index.entries().stream()
                .filter(entry -> entry.category().equals(selectedCategory))
                .toList();
    }

    private TCThaumonomiconCategoryView selectedCategoryView() {
        return TCThaumonomiconClientCache.index().categories().stream()
                .filter(category -> category.key().equals(selectedCategory))
                .findFirst()
                .orElse(null);
    }

    private void ensureSelectedCategory() {
        List<TCThaumonomiconCategoryView> categories = TCThaumonomiconClientCache.index().categories();
        if (categories.stream().noneMatch(category -> category.key().equals(selectedCategory))) {
            selectedCategory = categories.isEmpty() ? "" : categories.getFirst().key();
        }
    }

    private void selectCategory(String category) {
        rememberPan();
        selectedCategory = category;
        panX = LAST_PAN_X.getOrDefault(category, initialPanX());
        panY = LAST_PAN_Y.getOrDefault(category, initialPanY());
    }

    private double initialPanX() {
        List<TCThaumonomiconResearchView> entries = categoryEntries(TCThaumonomiconClientCache.index());
        return entries.stream().mapToDouble(entry -> entry.locationX() * GRID_SIZE).average().orElse(0.0D);
    }

    private double initialPanY() {
        List<TCThaumonomiconResearchView> entries = categoryEntries(TCThaumonomiconClientCache.index());
        return entries.stream().mapToDouble(entry -> entry.locationY() * GRID_SIZE).average().orElse(0.0D);
    }

    private void rememberPan() {
        if (!selectedCategory.isBlank()) {
            LAST_PAN_X.put(selectedCategory, panX);
            LAST_PAN_Y.put(selectedCategory, panY);
        }
    }

    private double nodeCenterX(TCThaumonomiconResearchView entry) {
        return width / 2.0D + (entry.locationX() * GRID_SIZE - panX) / zoom;
    }

    private double nodeCenterY(TCThaumonomiconResearchView entry) {
        return height / 2.0D + (entry.locationY() * GRID_SIZE - panY) / zoom;
    }

    private boolean insideViewport(double mouseX, double mouseY) {
        return mouseX >= VIEWPORT_MARGIN && mouseY >= VIEWPORT_MARGIN
                && mouseX < width - VIEWPORT_MARGIN && mouseY < height - VIEWPORT_MARGIN;
    }

    private void playPage() {
        if (minecraft != null && minecraft.player != null) {
            minecraft.player.playSound(TCSounds.PAGE.get(), 0.66F, 1.0F);
        }
    }

    private static String baseResearchKey(String raw) {
        String key = raw == null ? "" : raw.trim();
        while (key.startsWith("~")) {
            key = key.substring(1);
        }
        int stage = key.indexOf('@');
        if (stage >= 0) {
            key = key.substring(0, stage);
        }
        return key;
    }

    private static ResourceLocation parseLocation(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return ResourceLocation.tryParse(raw.trim().toLowerCase(Locale.ROOT));
    }

    private static void drawLine(GuiGraphics graphics, double x1, double y1, double x2, double y2, int color) {
        int startX = (int) Math.round(x1);
        int startY = (int) Math.round(y1);
        int endX = (int) Math.round(x2);
        int endY = (int) Math.round(y2);
        int dx = Math.abs(endX - startX);
        int sx = startX < endX ? 1 : -1;
        int dy = -Math.abs(endY - startY);
        int sy = startY < endY ? 1 : -1;
        int error = dx + dy;
        while (true) {
            graphics.fill(startX, startY, startX + 2, startY + 2, color);
            if (startX == endX && startY == endY) {
                return;
            }
            int twice = error * 2;
            if (twice >= dy) {
                error += dy;
                startX += sx;
            }
            if (twice <= dx) {
                error += dx;
                startY += sy;
            }
        }
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

    private static void blit(
            GuiGraphics graphics,
            ResourceLocation texture,
            int x,
            int y,
            float u,
            float v,
            int width,
            int height,
            int textureWidth,
            int textureHeight
    ) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.blit(texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }
}

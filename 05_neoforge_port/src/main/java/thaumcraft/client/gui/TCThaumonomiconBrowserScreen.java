package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import thaumcraft.Thaumcraft;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.research.TCResearchFlag;
import thaumcraft.common.research.TCResearchStatus;
import thaumcraft.common.research.TCThaumonomiconActionPayload;
import thaumcraft.common.research.TCThaumonomiconCategoryView;
import thaumcraft.common.research.TCThaumonomiconClientCache;
import thaumcraft.common.research.TCThaumonomiconEntryPayload;
import thaumcraft.common.research.TCThaumonomiconIndexPayload;
import thaumcraft.common.research.TCThaumonomiconRecipeSearchView;
import thaumcraft.common.research.TCThaumonomiconResearchView;

public final class TCThaumonomiconBrowserScreen extends Screen {
    private static final ResourceLocation BROWSER =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_research_browser.png");
    private static final ResourceLocation UNKNOWN =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/aspects/_unknown.png");

    private static final int VIEWPORT_MARGIN = 16;
    private static final int GRID_SIZE = 24;
    private static final int LEGACY_GUI_UV_SIZE = 256;
    private static final int SEARCH_FIELD_X = 20;
    private static final int SEARCH_FIELD_Y = 20;
    private static final int SEARCH_WIDTH = 89;
    private static final int SEARCH_RESULT_X = 32;
    private static final int SEARCH_RESULT_Y = 32;
    private static final int SEARCH_RESULT_ROW_HEIGHT = 10;
    private static final int SEARCH_BUTTON_X = 1;
    private static final int SEARCH_BUTTON_SIZE = 16;
    private static final double MIN_ZOOM = 1.0D;
    private static final double MAX_ZOOM = 2.0D;

    private static String selectedCategory = "";
    private static boolean searching;
    private static final Map<String, Double> LAST_PAN_X = new HashMap<>();
    private static final Map<String, Double> LAST_PAN_Y = new HashMap<>();

    private double panX;
    private double panY;
    private double zoom = 1.0D;
    private String pendingResearch = "";
    private RecipeSearchTarget pendingRecipeTarget;
    private TCThaumonomiconResearchView hoveredResearch;
    private TCThaumonomiconCategoryView hoveredCategory;
    private TCThaumonomiconResearchView hoveredSearchResult;
    private String hoveredSearchCategory = "";
    private SearchHit hoveredSearchHit;
    private EditBox searchBox;

    public TCThaumonomiconBrowserScreen() {
        super(Component.translatable("item.thaumcraft.thaumonomicon"));
    }

    @Override
    protected void init() {
        ensureSelectedCategory();
        panX = LAST_PAN_X.getOrDefault(selectedCategory, initialPanX());
        panY = LAST_PAN_Y.getOrDefault(selectedCategory, initialPanY());
        clampPan();
        rememberPan();
        searchBox = new EditBox(font, SEARCH_FIELD_X, SEARCH_FIELD_Y, SEARCH_WIDTH, font.lineHeight, Component.translatable("tc.search"));
        searchBox.setMaxLength(15);
        searchBox.setValue("");
        searchBox.setTextColor(0xFFFFFF);
        searchBox.visible = searching;
        searchBox.active = searching;
        searchBox.setFocused(searching);
        if (searching) {
            setFocused(searchBox);
        }
        addWidget(searchBox);
    }

    @Override
    public void tick() {
        TCThaumonomiconEntryPayload result = TCThaumonomiconClientCache.pollLastEntryResult();
        if (result == null || pendingResearch.isBlank() || !result.researchKey().equals(pendingResearch)) {
            return;
        }
        RecipeSearchTarget recipeTarget = pendingRecipeTarget;
        pendingResearch = "";
        pendingRecipeTarget = null;
        if (result.accepted() && result.entry().isPresent()) {
            playPage();
            if (minecraft != null) {
                minecraft.setScreen(recipeTarget == null
                        ? new TCThaumonomiconEntryScreen(result.entry().get())
                        : new TCThaumonomiconEntryScreen(result.entry().get(), recipeTarget.bookmarkId(), recipeTarget.pageIndex()));
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        TCThaumonomiconIndexPayload index = TCThaumonomiconClientCache.index();
        ensureSelectedCategory();
        hoveredResearch = null;
        hoveredCategory = null;
        hoveredSearchResult = null;
        hoveredSearchCategory = "";
        hoveredSearchHit = null;

        graphics.fill(0, 0, width, height, 0xB0100B16);
        if (!searching) {
            renderCategoryBackground(graphics);
            graphics.enableScissor(VIEWPORT_MARGIN, VIEWPORT_MARGIN, width - VIEWPORT_MARGIN, height - VIEWPORT_MARGIN);
            renderLegacyResearchLinks(graphics, index);
            renderResearchNodes(graphics, index, mouseX, mouseY);
            graphics.disableScissor();
        }
        renderFrame(graphics);
        if (!searching) {
            renderLegacyCategories(graphics, index, mouseX, mouseY);
        }
        renderSearchButton(graphics, mouseX, mouseY);
        renderSearch(graphics, index, mouseX, mouseY, partialTick);
        renderLegacyTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (insideSearchButton(mouseX, mouseY)) {
            openSearch();
            playPage();
            return true;
        }
        if (searching) {
            if (searchBox != null && searchBox.mouseClicked(mouseX, mouseY, button)) {
                setFocused(searchBox);
                return true;
            }
            if (hoveredSearchHit != null && activateSearchHit(hoveredSearchHit)) {
                closeSearch();
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (searchBox != null && searchBox.mouseClicked(mouseX, mouseY, button)) {
            setFocused(searchBox);
            return true;
        }
        if (searchBox != null) {
            searchBox.setFocused(false);
        }
        if (hoveredSearchResult != null && activateResearch(hoveredSearchResult)) {
            return true;
        }
        if (hoveredCategory != null) {
            selectCategory(hoveredCategory.key());
            playPage();
            return true;
        }
        if (hoveredResearch != null && activateResearch(hoveredResearch)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && insideViewport(mouseX, mouseY)) {
            panX -= dragX * zoom;
            panY -= dragY * zoom;
            clampPan();
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
        zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom - Math.signum(scrollY) * 0.25D));
        clampPan();
        rememberPan();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searching && searchBox != null && searchBox.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                closeSearch();
                return true;
            }
            return searchBox.keyPressed(keyCode, scanCode, modifiers);
        }
        if (keyCode == GLFW.GLFW_KEY_F && hasControlDown()) {
            openSearch();
            playPage();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_HOME || keyCode == GLFW.GLFW_KEY_H || keyCode == GLFW.GLFW_KEY_R) {
            resetPan();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searching && searchBox != null && searchBox.isFocused()) {
            return searchBox.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void renderCategoryBackground(GuiGraphics graphics) {
        TCThaumonomiconCategoryView category = selectedCategoryView();
        ResourceLocation background = parseLocation(category == null ? "" : category.background());
        ResourceLocation overlay = parseLocation(category == null ? "" : category.overlay());

        int screenX = Math.max(1, width - VIEWPORT_MARGIN * 2);
        int screenY = Math.max(1, height - VIEWPORT_MARGIN * 2);
        int drawX = VIEWPORT_MARGIN - 2;
        int drawY = VIEWPORT_MARGIN - 2;
        int drawW = screenX + 4;
        int drawH = screenY + 4;
        int locX = legacyLocX();
        int locY = legacyLocY();

        int scaledX = (int) Math.floor(drawX * zoom);
        int scaledY = (int) Math.floor(drawY * zoom);
        int scaledW = Math.max(1, (int) Math.ceil(drawW * zoom));
        int scaledH = Math.max(1, (int) Math.ceil(drawH * zoom));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.pose().pushPose();
        graphics.pose().scale((float) (1.0D / zoom), (float) (1.0D / zoom), 1.0F);
        graphics.fill(scaledX, scaledY, scaledX + scaledW, scaledY + scaledH, 0xFF191521);

        if (background != null) {
            legacyResearchBackgroundBlit(graphics, background, scaledX, scaledY,
                    (float) (locX / 2.0D), (float) (locY / 2.0D), scaledW, scaledH, 1.0F);
        }

        if (overlay != null) {
            legacyResearchBackgroundBlit(graphics, overlay, scaledX, scaledY,
                    (float) (locX / 1.5D), (float) (locY / 1.5D), scaledW, scaledH, 1.0F);
        }
        graphics.pose().popPose();
    }

    private void renderLegacyResearchLinks(GuiGraphics graphics, TCThaumonomiconIndexPayload index) {
        Map<String, TCThaumonomiconResearchView> visible = new HashMap<>();
        for (TCThaumonomiconResearchView entry : categoryEntries(index)) {
            visible.put(entry.key(), entry);
        }

        int locX = legacyLocX();
        int locY = legacyLocY();
        for (TCThaumonomiconResearchView entry : visible.values()) {
            for (String rawParent : entry.parents()) {
                if (rawParent == null || rawParent.startsWith("~")) {
                    continue;
                }
                TCThaumonomiconResearchView parent = visible.get(baseResearchKey(rawParent));
                if (parent == null) {
                    continue;
                }
                boolean parentKnown = parent.status() == TCResearchStatus.COMPLETE;
                drawLegacyLine(
                        graphics,
                        entry.locationX(),
                        entry.locationY(),
                        parent.locationX(),
                        parent.locationY(),
                        parentKnown ? 0.6F : 0.2F,
                        parentKnown ? 0.6F : 0.2F,
                        parentKnown ? 0.6F : 0.2F,
                        locX,
                        locY,
                        true,
                        entry.meta().contains("REVERSE")
                );
            }

            for (String rawSibling : entry.siblings()) {
                if (rawSibling == null || rawSibling.startsWith("~")) {
                    continue;
                }
                TCThaumonomiconResearchView sibling = visible.get(baseResearchKey(rawSibling));
                if (sibling == null) {
                    continue;
                }
                boolean siblingKnown = sibling.status() == TCResearchStatus.COMPLETE;
                drawLegacyLine(
                        graphics,
                        sibling.locationX(),
                        sibling.locationY(),
                        entry.locationX(),
                        entry.locationY(),
                        siblingKnown ? 0.3F : 0.1875F,
                        siblingKnown ? 0.3F : 0.1875F,
                        siblingKnown ? 0.4F : 0.25F,
                        locX,
                        locY,
                        false,
                        entry.meta().contains("REVERSE")
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

        if (entry.flags().contains(TCResearchFlag.RESEARCH)) {
            drawLegacyNodeFlag(graphics, -9, -9, 176, 16);
        }
        if (entry.flags().contains(TCResearchFlag.PAGE)) {
            drawLegacyNodeFlag(graphics, -9, 9, 208, 16);
        }

        renderResearchIcon(graphics, entry, -8, -8);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
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

    private void renderLegacyCategories(
            GuiGraphics graphics,
            TCThaumonomiconIndexPayload index,
            int mouseX,
            int mouseY
    ) {
        List<TCThaumonomiconCategoryView> categories = index.categories();
        int maxVisible = Math.max(1, (height - 52) / GRID_SIZE);
        int visible = Math.min(maxVisible, categories.size());

        for (int i = 0; i < visible; i++) {
            TCThaumonomiconCategoryView category = categories.get(i);
            int x = 1;
            int y = 10 + (i + 1) * GRID_SIZE;
            boolean selected = category.key().equals(selectedCategory);
            boolean hovered = mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18;
            if (hovered) {
                hoveredCategory = category;
            }

            graphics.setColor(selected ? 0.6F : 1.0F, 1.0F, 1.0F, 1.0F);
            blit(graphics, BROWSER, x - 3, y - 3, 13, 13, 22, 22, 256, 256);

            ResourceLocation icon = parseLocation(category.icon());
            if (!selected && !hovered) {
                graphics.setColor(0.66F, 0.66F, 0.66F, 0.8F);
            } else {
                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
            drawFullTexture(graphics, icon == null ? UNKNOWN : icon, x, y, 16, 16);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

            boolean hasNewResearch = categoryHasFlag(index, category.key(), TCResearchFlag.RESEARCH);
            boolean hasNewPage = categoryHasFlag(index, category.key(), TCResearchFlag.PAGE);
            if (hasNewResearch) {
                drawLegacyCornerFlag(graphics, x - 2, y - 2, 176, 16, 0.7F);
            }
            if (hasNewPage) {
                drawLegacyCornerFlag(graphics, x - 2, y + 9, 208, 16, 0.7F);
            }

            if (hovered) {
                int textY = y + 4;
                graphics.drawString(
                        font,
                        Component.translatable("tc.research_category." + category.key()),
                        x + 22,
                        textY,
                        0xFFFFFF,
                        false
                );
                textY += 9;
                if (hasNewResearch) {
                    graphics.drawString(font, Component.translatable("tc.research.newresearch"), x + 22, textY, 0xFFFFFF, false);
                    textY += 9;
                }
                if (hasNewPage) {
                    graphics.drawString(font, Component.translatable("tc.research.newpage"), x + 22, textY, 0xFFFFFF, false);
                }
            }
        }
    }

    private void renderLegacyTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (hoveredSearchResult != null) {
            renderResearchTooltip(graphics, hoveredSearchResult, mouseX, mouseY);
            return;
        }
        if (hoveredCategory != null) {
            return;
        }
        if (hoveredResearch == null) {
            return;
        }
        renderResearchTooltip(graphics, hoveredResearch, mouseX, mouseY);
    }

    private void renderResearchTooltip(
            GuiGraphics graphics,
            TCThaumonomiconResearchView research,
            int mouseX,
            int mouseY
    ) {
        List<LegacyTooltipLine> lines = new ArrayList<>();
        lines.add(LegacyTooltipLine.normal(Component.translatable(research.name()).getString(), 0xFFFFAA00));
        if (research.unlockable()) {
            if (research.status() != TCResearchStatus.COMPLETE && research.totalStages() > 0) {
                if (research.currentStage() > 0) {
                    lines.add(LegacyTooltipLine.small(
                            Component.translatable("tc.research.stage").getString()
                                    + " " + research.currentStage() + "/" + research.totalStages(),
                            0xFF55FFFF
                    ));
                } else {
                    lines.add(LegacyTooltipLine.small(
                            Component.translatable("tc.research.begin").getString(),
                            0xFF55FF55
                    ));
                }
            }
        } else {
            lines.add(LegacyTooltipLine.small(Component.translatable("tc.researchmissing").getString(), 0xFFFF5555));
            for (String parent : research.parents()) {
                String clean = baseResearchKey(parent);
                if (clean.isBlank()) {
                    continue;
                }
                TCThaumonomiconResearchView parentView = researchView(clean);
                if (parentView != null && parentView.status() == TCResearchStatus.COMPLETE) {
                    continue;
                }
                String parentName = parentView == null
                        ? Component.translatable("research." + clean + ".title").getString()
                        : Component.translatable(parentView.name()).getString();
                lines.add(LegacyTooltipLine.small(" - " + parentName, 0xFFFFFF55));
            }
        }
        if (research.flags().contains(TCResearchFlag.RESEARCH)) {
            lines.add(LegacyTooltipLine.small(Component.translatable("tc.research.newresearch").getString(), 0xFFFFAA00));
        }
        if (research.flags().contains(TCResearchFlag.PAGE)) {
            lines.add(LegacyTooltipLine.small(Component.translatable("tc.research.newpage").getString(), 0xFF55FF55));
        }
        if (!pendingResearch.isBlank() && pendingResearch.equals(research.key())) {
            lines.add(LegacyTooltipLine.small(Component.translatable("gui.thaumcraft.thaumonomicon.loading").getString(), 0xFFAAAAAA));
        }
        renderLegacyCustomTooltip(graphics, lines, mouseX + 3, mouseY - 3);
    }

    private boolean categoryHasFlag(TCThaumonomiconIndexPayload index, String categoryKey, TCResearchFlag flag) {
        for (TCThaumonomiconResearchView entry : index.entries()) {
            if (entry.category().equals(categoryKey)
                    && entry.status() != TCResearchStatus.UNKNOWN
                    && entry.flags().contains(flag)) {
                return true;
            }
        }
        return false;
    }

    private static void drawLegacyCornerFlag(GuiGraphics graphics, int x, int y, int u, int v, float alpha) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0F);
        graphics.pose().scale(0.25F, 0.25F, 1.0F);
        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        blit(graphics, BROWSER, 0, 0, u, v, 32, 32, 256, 256);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.pose().popPose();
    }

    private static void drawLegacyNodeFlag(GuiGraphics graphics, int x, int y, int u, int v) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0F);
        graphics.pose().scale(0.5F, 0.5F, 1.0F);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        blit(graphics, BROWSER, 0, 0, u, v, 32, 32, 256, 256);
        graphics.pose().popPose();
    }

    private void renderSearch(
            GuiGraphics graphics,
            TCThaumonomiconIndexPayload index,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        if (searchBox == null) {
            return;
        }
        searchBox.visible = searching;
        searchBox.active = searching;
        if (!searching) {
            return;
        }
        searchBox.render(graphics, mouseX, mouseY, partialTick);

        String query = normalizedSearchQuery();
        List<SearchHit> results = searchResults(index, query);
        if (results.isEmpty()) {
            return;
        }

        int legacyScreenX = Math.max(1, width - 32);
        int legacyScreenY = Math.max(1, height - 32);
        int row = 0;
        for (SearchHit result : results) {
            int rowY = SEARCH_RESULT_Y + row * SEARCH_RESULT_ROW_HEIGHT;
            boolean hovered = mouseX > 22
                    && mouseX < 18 + legacyScreenX
                    && mouseY >= rowY
                    && mouseY < rowY + 8;
            if (hovered) {
                if (result.category()) {
                    hoveredSearchCategory = result.categoryKey();
                } else {
                    hoveredSearchResult = result.research();
                }
                hoveredSearchHit = result;
            }
            int color = result.category()
                    ? 14527146
                    : result.recipe() ? 11184861 : 14540253;
            if (hovered) {
                color = result.recipe()
                        ? 13421823
                        : result.category() ? 16764108 : 16777215;
            }
            if (result.recipe()) {
                graphics.pose().pushPose();
                graphics.pose().scale(0.5F, 0.5F, 1.0F);
                blit(graphics, BROWSER, 44, rowY * 2, 224, 48, 16, 16, 256, 256);
                graphics.pose().popPose();
            }
            graphics.drawString(font, result.label(), SEARCH_RESULT_X, rowY, color, false);
            row++;
            if (SEARCH_RESULT_Y + (row + 1) * SEARCH_RESULT_ROW_HEIGHT > legacyScreenY) {
                graphics.drawString(
                        font,
                        Component.translatable("tc.search.more"),
                        22,
                        SEARCH_RESULT_Y + 2 + row * SEARCH_RESULT_ROW_HEIGHT,
                        11184810,
                        false
                );
                break;
            }
        }
    }

    private List<TCThaumonomiconResearchView> categoryEntries(TCThaumonomiconIndexPayload index) {
        return index.entries().stream()
                .filter(entry -> entry.category().equals(selectedCategory))
                .toList();
    }

    private List<SearchHit> searchResults(
            TCThaumonomiconIndexPayload index,
            String query
    ) {
        ArrayList<SearchHit> results = new ArrayList<>();
        for (TCThaumonomiconCategoryView category : index.categories()) {
            if (query.isBlank() || category.key().toLowerCase(Locale.ROOT).contains(query)) {
                results.add(SearchHit.category(
                        Component.translatable("tc.research_category." + category.key()).getString(),
                        category.key()
                ));
            }
        }
        for (TCThaumonomiconResearchView entry : index.entries()) {
            if (entry.status() == TCResearchStatus.UNKNOWN) {
                continue;
            }
            String translatedName = Component.translatable(entry.name()).getString();
            if (query.isBlank()
                    || translatedName.toLowerCase(Locale.ROOT).contains(query)
                    || entry.key().toLowerCase(Locale.ROOT).contains(query)) {
                results.add(SearchHit.research(translatedName, entry, false));
            }
        }
        Set<String> duplicateRecipeIds = new HashSet<>();
        for (TCThaumonomiconResearchView entry : index.entries()) {
            if (entry.status() == TCResearchStatus.UNKNOWN) {
                continue;
            }
            for (TCThaumonomiconRecipeSearchView recipeSearch : entry.recipeSearch()) {
                ItemStack result = recipeSearch.result();
                if (result.isEmpty()) {
                    continue;
                }
                String duplicateKey = recipeSearch.bookmarkId() + "#" + recipeSearch.pageIndex();
                if (!duplicateRecipeIds.add(duplicateKey)) {
                    continue;
                }
                String translatedName = result.getHoverName().getString();
                if (query.isBlank() || translatedName.toLowerCase(Locale.ROOT).contains(query)) {
                    results.add(SearchHit.recipe(translatedName, entry, recipeSearch));
                }
            }
        }
        results.sort(Comparator.comparing(SearchHit::label, String.CASE_INSENSITIVE_ORDER));
        return results;
    }

    private String normalizedSearchQuery() {
        return searchBox == null ? "" : searchBox.getValue().trim().toLowerCase(Locale.ROOT);
    }

    private void renderSearchButton(GuiGraphics graphics, int mouseX, int mouseY) {
        int y = searchButtonY();
        boolean hovered = insideSearchButton(mouseX, mouseY);
        graphics.setColor(hovered ? 1.0F : 0.8F, hovered ? 1.0F : 0.8F, hovered ? 1.0F : 0.8F, 1.0F);
        blit(graphics, BROWSER, SEARCH_BUTTON_X, y, 160, 16, SEARCH_BUTTON_SIZE, SEARCH_BUTTON_SIZE, 256, 256);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (hovered) {
            graphics.drawString(font, Component.translatable("tc.search"), SEARCH_BUTTON_X + 19, y + 4, 0xFFFFFF, false);
        }
    }

    private boolean insideSearchButton(double mouseX, double mouseY) {
        int y = searchButtonY();
        return mouseX >= SEARCH_BUTTON_X
                && mouseY >= y
                && mouseX < SEARCH_BUTTON_X + SEARCH_BUTTON_SIZE
                && mouseY < y + SEARCH_BUTTON_SIZE;
    }

    private int searchButtonY() {
        return height - 17;
    }

    private void openSearch() {
        searching = true;
        if (searchBox != null) {
            searchBox.visible = true;
            searchBox.active = true;
            searchBox.setValue("");
            searchBox.setFocused(true);
            setFocused(searchBox);
        }
    }

    private void closeSearch() {
        searching = false;
        hoveredSearchCategory = "";
        hoveredSearchResult = null;
        if (searchBox != null) {
            searchBox.setValue("");
            searchBox.setFocused(false);
            searchBox.visible = false;
            searchBox.active = false;
        }
    }

    private boolean activateSearchHit(SearchHit hit) {
        if (hit.category()) {
            selectCategory(hit.categoryKey());
            playPage();
            return true;
        }
        return activateResearch(hit.research(), hit.recipeSearch() == null
                ? null
                : new RecipeSearchTarget(hit.recipeSearch().bookmarkId(), hit.recipeSearch().pageIndex()));
    }

    private boolean activateResearch(TCThaumonomiconResearchView research) {
        return activateResearch(research, null);
    }

    private boolean activateResearch(TCThaumonomiconResearchView research, RecipeSearchTarget recipeTarget) {
        if (research == null || !pendingResearch.isBlank()) {
            return false;
        }
        if (!research.category().equals(selectedCategory)) {
            selectCategory(research.category());
        }
        if (research.status() != TCResearchStatus.UNKNOWN) {
            var cached = TCThaumonomiconClientCache.entry(research.key());
            if (cached.isPresent() && minecraft != null) {
                playPage();
                minecraft.setScreen(recipeTarget == null
                        ? new TCThaumonomiconEntryScreen(cached.get())
                        : new TCThaumonomiconEntryScreen(cached.get(), recipeTarget.bookmarkId(), recipeTarget.pageIndex()));
                return true;
            }
        }

        int action = research.status() == TCResearchStatus.UNKNOWN
                ? TCThaumonomiconActionPayload.START_RESEARCH
                : TCThaumonomiconActionPayload.ACKNOWLEDGE_ENTRY;
        if (research.status() != TCResearchStatus.UNKNOWN || research.unlockable()) {
            pendingResearch = research.key();
            pendingRecipeTarget = recipeTarget;
            PacketDistributor.sendToServer(new TCThaumonomiconActionPayload(
                    action,
                    pendingResearch,
                    TCThaumonomiconClientCache.revision()
            ));
            playPage();
            return true;
        }
        return false;
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
        clampPan();
        rememberPan();
    }

    private double initialPanX() {
        List<TCThaumonomiconResearchView> entries = categoryEntries(TCThaumonomiconClientCache.index());
        if (entries.isEmpty()) {
            return 0.0D;
        }
        return (legacyBoundLeft(entries) + legacyBoundRight(entries)) / 2.0D;
    }

    private double initialPanY() {
        List<TCThaumonomiconResearchView> entries = categoryEntries(TCThaumonomiconClientCache.index());
        if (entries.isEmpty()) {
            return 0.0D;
        }
        return (legacyBoundTop(entries) + legacyBoundBottom(entries)) / 2.0D;
    }

    private void resetPan() {
        panX = initialPanX();
        panY = initialPanY();
        clampPan();
        rememberPan();
    }

    private void rememberPan() {
        if (!selectedCategory.isBlank()) {
            LAST_PAN_X.put(selectedCategory, panX);
            LAST_PAN_Y.put(selectedCategory, panY);
        }
    }

    private double nodeCenterX(TCThaumonomiconResearchView entry) {
        return (VIEWPORT_MARGIN + entry.locationX() * GRID_SIZE - legacyLocX() + 8.0D) / zoom;
    }

    private double nodeCenterY(TCThaumonomiconResearchView entry) {
        return (VIEWPORT_MARGIN + entry.locationY() * GRID_SIZE - legacyLocY() + 8.0D) / zoom;
    }

    private boolean insideViewport(double mouseX, double mouseY) {
        return mouseX >= VIEWPORT_MARGIN && mouseY >= VIEWPORT_MARGIN
                && mouseX < width - VIEWPORT_MARGIN && mouseY < height - VIEWPORT_MARGIN;
    }

    private void clampPan() {
        List<TCThaumonomiconResearchView> entries = categoryEntries(TCThaumonomiconClientCache.index());
        if (entries.isEmpty()) {
            panX = 0.0D;
            panY = 0.0D;
            return;
        }

        double left = legacyBoundLeft(entries) * zoom;
        double right = legacyBoundRight(entries) * zoom - 1.0D;
        double top = legacyBoundTop(entries) * zoom;
        double bottom = legacyBoundBottom(entries) * zoom - 1.0D;

        panX = clampBetween(panX, left, right);
        panY = clampBetween(panY, top, bottom);
    }

    private int legacyLocX() {
        List<TCThaumonomiconResearchView> entries = categoryEntries(TCThaumonomiconClientCache.index());
        if (entries.isEmpty()) {
            return 0;
        }
        double left = legacyBoundLeft(entries) * zoom;
        double right = legacyBoundRight(entries) * zoom - 1.0D;
        return (int) Math.floor(clampBetween(panX, left, right));
    }

    private int legacyLocY() {
        List<TCThaumonomiconResearchView> entries = categoryEntries(TCThaumonomiconClientCache.index());
        if (entries.isEmpty()) {
            return 0;
        }
        double top = legacyBoundTop(entries) * zoom;
        double bottom = legacyBoundBottom(entries) * zoom - 1.0D;
        return (int) Math.floor(clampBetween(panY, top, bottom));
    }

    private double legacyBoundLeft(List<TCThaumonomiconResearchView> entries) {
        int screenX = Math.max(1, width - VIEWPORT_MARGIN * 2);
        return entries.stream()
                .mapToDouble(entry -> entry.locationX() * GRID_SIZE - screenX + 48)
                .min()
                .orElse(0.0D);
    }

    private double legacyBoundRight(List<TCThaumonomiconResearchView> entries) {
        return entries.stream()
                .mapToDouble(entry -> entry.locationX() * GRID_SIZE - 24)
                .max()
                .orElse(0.0D);
    }

    private double legacyBoundTop(List<TCThaumonomiconResearchView> entries) {
        int screenY = Math.max(1, height - VIEWPORT_MARGIN * 2);
        return entries.stream()
                .mapToDouble(entry -> entry.locationY() * GRID_SIZE - screenY + 48)
                .min()
                .orElse(0.0D);
    }

    private double legacyBoundBottom(List<TCThaumonomiconResearchView> entries) {
        return entries.stream()
                .mapToDouble(entry -> entry.locationY() * GRID_SIZE - 24)
                .max()
                .orElse(0.0D);
    }

    private static double clampBetween(double value, double min, double max) {
        if (min > max) {
            double center = (min + max) / 2.0D;
            min = center - 1.0D;
            max = center + 1.0D;
        }
        return Math.max(min, Math.min(max, value));
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
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if (!normalized.contains(":") && normalized.contains("/")) {
            normalized = Thaumcraft.MODID + ":" + normalized;
        }
        return ResourceLocation.tryParse(normalized);
    }

    private void drawLegacyLine(
            GuiGraphics graphics,
            int x,
            int y,
            int x2,
            int y2,
            float r,
            float g,
            float b,
            int locX,
            int locY,
            boolean arrow,
            boolean flipped
    ) {
        boolean bigCorner = false;
        int xd;
        int yd;
        int xm;
        int ym;
        int xx;
        int yy;
        if (flipped) {
            xd = Math.abs(x2 - x);
            yd = Math.abs(y2 - y);
            xm = xd == 0 ? 0 : (x2 - x > 0 ? -1 : 1);
            ym = yd == 0 ? 0 : (y2 - y > 0 ? -1 : 1);
            if (xd > 1 && yd > 1) {
                bigCorner = true;
            }
            xx = x2 * GRID_SIZE - 4 - locX + VIEWPORT_MARGIN;
            yy = y2 * GRID_SIZE - 4 - locY + VIEWPORT_MARGIN;
        } else {
            xd = Math.abs(x - x2);
            yd = Math.abs(y - y2);
            xm = xd == 0 ? 0 : (x - x2 > 0 ? -1 : 1);
            ym = yd == 0 ? 0 : (y - y2 > 0 ? -1 : 1);
            if (xd > 1 && yd > 1) {
                bigCorner = true;
            }
            xx = x * GRID_SIZE - 4 - locX + VIEWPORT_MARGIN;
            yy = y * GRID_SIZE - 4 - locY + VIEWPORT_MARGIN;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.pose().pushPose();
        graphics.pose().scale((float) (1.0D / zoom), (float) (1.0D / zoom), 1.0F);

        if (arrow) {
            if (flipped) {
                int xx3 = x * GRID_SIZE - 8 - locX + VIEWPORT_MARGIN;
                int yy3 = y * GRID_SIZE - 8 - locY + VIEWPORT_MARGIN;
                if (xm < 0) {
                    tintedBrowserBlit(graphics, xx3, yy3, 160, 112, 32, 32, r, g, b, 1.0F);
                } else if (xm > 0) {
                    tintedBrowserBlit(graphics, xx3, yy3, 128, 112, 32, 32, r, g, b, 1.0F);
                } else if (ym > 0) {
                    tintedBrowserBlit(graphics, xx3, yy3, 64, 112, 32, 32, r, g, b, 1.0F);
                } else if (ym < 0) {
                    tintedBrowserBlit(graphics, xx3, yy3, 96, 112, 32, 32, r, g, b, 1.0F);
                }
            } else if (ym < 0) {
                tintedBrowserBlit(graphics, xx - 4, yy - 4, 64, 112, 32, 32, r, g, b, 1.0F);
            } else if (ym > 0) {
                tintedBrowserBlit(graphics, xx - 4, yy - 4, 96, 112, 32, 32, r, g, b, 1.0F);
            } else if (xm > 0) {
                tintedBrowserBlit(graphics, xx - 4, yy - 4, 160, 112, 32, 32, r, g, b, 1.0F);
            } else if (xm < 0) {
                tintedBrowserBlit(graphics, xx - 4, yy - 4, 128, 112, 32, 32, r, g, b, 1.0F);
            }
        }

        int v = 1;
        int h = 0;
        while (v < yd - (bigCorner ? 1 : 0)) {
            tintedBrowserBlit(graphics, xx + xm * GRID_SIZE * h, yy + ym * GRID_SIZE * v, 0, 228, 24, 24, r, g, b, 1.0F);
            v++;
        }

        if (bigCorner) {
            if (xm < 0 && ym > 0) {
                tintedBrowserBlit(graphics, xx + xm * GRID_SIZE * h - 24, yy + ym * GRID_SIZE * v, 0, 180, 48, 48, r, g, b, 1.0F);
            }
            if (xm > 0 && ym > 0) {
                tintedBrowserBlit(graphics, xx + xm * GRID_SIZE * h, yy + ym * GRID_SIZE * v, 48, 180, 48, 48, r, g, b, 1.0F);
            }
            if (xm < 0 && ym < 0) {
                tintedBrowserBlit(graphics, xx + xm * GRID_SIZE * h - 24, yy + ym * GRID_SIZE * v - 24, 96, 180, 48, 48, r, g, b, 1.0F);
            }
            if (xm > 0 && ym < 0) {
                tintedBrowserBlit(graphics, xx + xm * GRID_SIZE * h, yy + ym * GRID_SIZE * v - 24, 144, 180, 48, 48, r, g, b, 1.0F);
            }
        } else {
            if (xm < 0 && ym > 0) {
                tintedBrowserBlit(graphics, xx + xm * GRID_SIZE * h, yy + ym * GRID_SIZE * v, 48, 228, 24, 24, r, g, b, 1.0F);
            }
            if (xm > 0 && ym > 0) {
                tintedBrowserBlit(graphics, xx + xm * GRID_SIZE * h, yy + ym * GRID_SIZE * v, 72, 228, 24, 24, r, g, b, 1.0F);
            }
            if (xm < 0 && ym < 0) {
                tintedBrowserBlit(graphics, xx + xm * GRID_SIZE * h, yy + ym * GRID_SIZE * v, 96, 228, 24, 24, r, g, b, 1.0F);
            }
            if (xm > 0 && ym < 0) {
                tintedBrowserBlit(graphics, xx + xm * GRID_SIZE * h, yy + ym * GRID_SIZE * v, 120, 228, 24, 24, r, g, b, 1.0F);
            }
        }

        v += bigCorner ? 1 : 0;
        for (int i = h + (bigCorner ? 2 : 1); i < xd; i++) {
            tintedBrowserBlit(graphics, xx + xm * GRID_SIZE * i, yy + ym * GRID_SIZE * v, 24, 228, 24, 24, r, g, b, 1.0F);
        }

        graphics.pose().popPose();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void legacyResearchBackgroundBlit(
            GuiGraphics graphics,
            ResourceLocation texture,
            int x,
            int y,
            float u,
            float v,
            int width,
            int height,
            float alpha
    ) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        int drawnY = 0;
        while (drawnY < height) {
            float tileV = positiveModulo(v + drawnY, LEGACY_GUI_UV_SIZE);
            int segmentHeight = Math.min(height - drawnY, Math.max(1, (int) Math.floor(LEGACY_GUI_UV_SIZE - tileV)));
            int drawnX = 0;
            while (drawnX < width) {
                float tileU = positiveModulo(u + drawnX, LEGACY_GUI_UV_SIZE);
                int segmentWidth = Math.min(width - drawnX, Math.max(1, (int) Math.floor(LEGACY_GUI_UV_SIZE - tileU)));
                graphics.blit(
                        texture,
                        x + drawnX,
                        y + drawnY,
                        tileU,
                        tileV,
                        segmentWidth,
                        segmentHeight,
                        LEGACY_GUI_UV_SIZE,
                        LEGACY_GUI_UV_SIZE
                );
                drawnX += segmentWidth;
            }
            drawnY += segmentHeight;
        }
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private TCThaumonomiconResearchView researchView(String key) {
        String clean = baseResearchKey(key);
        for (TCThaumonomiconResearchView research : TCThaumonomiconClientCache.index().entries()) {
            if (research.key().equalsIgnoreCase(clean)) {
                return research;
            }
        }
        return null;
    }

    private static float positiveModulo(float value, int modulo) {
        float result = value % modulo;
        return result < 0.0F ? result + modulo : result;
    }

    private static void tintedBrowserBlit(
            GuiGraphics graphics,
            int x,
            int y,
            float u,
            float v,
            int width,
            int height,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.setColor(red, green, blue, alpha);
        graphics.blit(BROWSER, x, y, u, v, width, height, 256, 256);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
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

    private void renderLegacyCustomTooltip(
            GuiGraphics graphics,
            List<LegacyTooltipLine> lines,
            int x,
            int y
    ) {
        if (lines.isEmpty()) {
            return;
        }

        int maxWidth = 240;
        int widest = 0;
        int totalHeight = -2;
        for (LegacyTooltipLine line : lines) {
            int width = line.small() ? (int) Math.ceil(font.width(line.text()) / 2.0D) : font.width(line.text());
            widest = Math.max(widest, width);
            totalHeight += line.small() ? 7 : 10;
        }
        if (lines.size() > 1) {
            totalHeight += 2;
        }
        widest = Math.min(widest, maxWidth);

        int drawX = x + 12;
        int drawY = y - 12;
        if (drawY + totalHeight > height) {
            drawY = height - totalHeight - 5;
        }
        if (drawX + widest + 4 > width) {
            drawX = Math.max(4, x - widest - 12);
        }

        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 400.0F);
        graphics.fill(drawX - 3, drawY - 4, drawX + widest + 3, drawY - 3, 0xF0100010);
        graphics.fill(drawX - 3, drawY + totalHeight + 3, drawX + widest + 3, drawY + totalHeight + 4, 0xF0100010);
        graphics.fill(drawX - 3, drawY - 3, drawX + widest + 3, drawY + totalHeight + 3, 0xF0100010);
        graphics.fill(drawX - 4, drawY - 3, drawX - 3, drawY + totalHeight + 3, 0xF0100010);
        graphics.fill(drawX + widest + 3, drawY - 3, drawX + widest + 4, drawY + totalHeight + 3, 0xF0100010);
        graphics.fill(drawX - 3, drawY - 2, drawX - 2, drawY + totalHeight + 2, 0x505000FF);
        graphics.fill(drawX + widest + 2, drawY - 2, drawX + widest + 3, drawY + totalHeight + 2, 0x5028007F);
        graphics.fill(drawX - 3, drawY - 3, drawX + widest + 3, drawY - 2, 0x505000FF);
        graphics.fill(drawX - 3, drawY + totalHeight + 2, drawX + widest + 3, drawY + totalHeight + 3, 0x5028007F);

        int lineY = drawY;
        for (int i = 0; i < lines.size(); i++) {
            LegacyTooltipLine line = lines.get(i);
            if (line.small()) {
                graphics.pose().pushPose();
                graphics.pose().translate(drawX, lineY, 1.0F);
                graphics.pose().scale(0.5F, 0.5F, 1.0F);
                graphics.drawString(font, line.text(), 0, 3, line.color(), true);
                graphics.pose().popPose();
                lineY += 7;
            } else {
                graphics.drawString(font, line.text(), drawX, lineY, line.color(), true);
                lineY += 10;
            }
            if (i == 0) {
                lineY += 2;
            }
        }
        graphics.pose().popPose();
    }

    private record SearchHit(
            String label,
            String categoryKey,
            TCThaumonomiconResearchView research,
            boolean category,
            boolean recipe,
            TCThaumonomiconRecipeSearchView recipeSearch
    ) {
        static SearchHit category(String label, String categoryKey) {
            return new SearchHit(label, categoryKey, null, true, false, null);
        }

        static SearchHit research(String label, TCThaumonomiconResearchView research, boolean recipe) {
            return new SearchHit(label, "", research, false, recipe, null);
        }

        static SearchHit recipe(String label, TCThaumonomiconResearchView research, TCThaumonomiconRecipeSearchView recipeSearch) {
            return new SearchHit(label, "", research, false, true, recipeSearch);
        }
    }

    private record RecipeSearchTarget(ResourceLocation bookmarkId, int pageIndex) {
    }

    private record LegacyTooltipLine(String text, int color, boolean small) {
        static LegacyTooltipLine normal(String text, int color) {
            return new LegacyTooltipLine(text, color, false);
        }

        static LegacyTooltipLine small(String text, int color) {
            return new LegacyTooltipLine(text, color, true);
        }
    }
}

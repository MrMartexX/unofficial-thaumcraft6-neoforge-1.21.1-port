package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.research.TCArcaneRecipePageView;
import thaumcraft.common.research.TCBlueprintRecipePageView;
import thaumcraft.common.research.TCCraftingRecipePageView;
import thaumcraft.common.research.TCCrucibleRecipePageView;
import thaumcraft.common.research.TCDisplayRecipePageType;
import thaumcraft.common.research.TCDisplayRecipePageView;
import thaumcraft.common.research.TCInfusionRecipePageView;
import thaumcraft.common.research.TCKnowledgeClientCache;
import thaumcraft.common.research.TCKnowledgeType;
import thaumcraft.common.research.TCResearchPageAvailability;
import thaumcraft.common.research.TCResearchPageBookmark;
import thaumcraft.common.research.TCResearchPageView;
import thaumcraft.common.research.TCThaumonomiconCategoryView;
import thaumcraft.common.research.TCThaumonomiconActionPayload;
import thaumcraft.common.research.TCThaumonomiconClientCache;
import thaumcraft.common.research.TCThaumonomiconDrilldownPayload;
import thaumcraft.common.research.TCThaumonomiconDrilldownRequestPayload;
import thaumcraft.common.research.TCThaumonomiconEntryPayload;
import thaumcraft.common.research.TCThaumonomiconEntryView;

public final class TCThaumonomiconEntryScreen extends Screen {
    private static final ResourceLocation BOOK =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_researchbook.png");
    private static final ResourceLocation BROWSER =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_research_browser.png");
    private static final ResourceLocation BOOK_OVERLAY =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_researchbook_overlay.png");
    private static final ResourceLocation PAPER =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/paper.png");
    private static final ResourceLocation ASPECT_BACK =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/aspects/_back.png");
    private static final ResourceLocation UNKNOWN =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/aspects/_unknown.png");
    private static final ResourceLocation KNOWLEDGE_THEORY =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/research/knowledge_theory.png");
    private static final ResourceLocation KNOWLEDGE_OBSERVATION =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/research/knowledge_observation.png");
    private static final ResourceLocation AURA_NODES =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/auranodes.png");
    private static final int BOOK_WIDTH = 333;
    private static final int BOOK_HEIGHT = 235;
    private static final int RECIPE_PAGE_SIZE = 256;
    private static final int PAGE_TEXT_WIDTH = 126;
    private static final int LINES_PER_PAGE = 21;
    private static final int ASPECTS_PER_PAGE = 5;

    private static int aspectPage;
    private TCThaumonomiconEntryView entry;
    private List<FormattedCharSequence> lines = List.of();
    private int spread;
    private boolean pendingAdvance;
    private Component lastResult = Component.empty();
    private TCResearchPageBookmark hoveredBookmark;
    private ResourceLocation activeRecipeId;
    private List<TCResearchPageView> activeRecipePages = List.of();
    private int activeRecipeIndex;
    private final Deque<RecipePageState> recipeHistory = new ArrayDeque<>();
    private ItemStack hoveredRecipeStack = ItemStack.EMPTY;
    private List<Component> hoveredUiTooltip = List.of();
    private boolean pendingDrilldown;
    private SideInsert sideInsert = SideInsert.NONE;

    public TCThaumonomiconEntryScreen(TCThaumonomiconEntryView entry) {
        super(Component.translatable(entry.research().name()));
        this.entry = entry;
    }

    @Override
    protected void init() {
        rebuildLines();
    }

    @Override
    public void tick() {
        TCThaumonomiconEntryPayload result = TCThaumonomiconClientCache.pollLastEntryResult();
        if (result != null && result.researchKey().equals(entry.research().key())) {
            pendingAdvance = false;
            lastResult = Component.translatable("gui.thaumcraft.thaumonomicon.result." + result.resultKey());
            if (result.accepted() && result.entry().isPresent()) {
                entry = result.entry().get();
                spread = 0;
                closeRecipePage();
                rebuildLines();
            }
        }

        TCThaumonomiconDrilldownPayload drilldown = TCThaumonomiconClientCache.pollLastDrilldownResult();
        if (drilldown == null) {
            return;
        }
        pendingDrilldown = false;
        lastResult = Component.translatable("gui.thaumcraft.thaumonomicon.result." + drilldown.resultKey());
        if (drilldown.accepted() && drilldown.bookmark().isPresent()) {
            openDrilldown(drilldown.bookmark().get(), drilldown.pageIndex());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        hoveredBookmark = null;
        hoveredRecipeStack = ItemStack.EMPTY;
        hoveredUiTooltip = List.of();
        graphics.fill(0, 0, width, height, 0xB0100B16);
        int x = bookX();
        int y = bookY();
        renderBook(graphics, x, y);
        renderTitle(graphics, x, y);
        renderPageText(graphics, x, y);
        renderNavigation(graphics, x, y, mouseX, mouseY);
        renderSideTabs(graphics, x, y, mouseX, mouseY);
        renderWarpWarning(graphics, x, y, mouseX, mouseY);
        renderBookmarks(graphics, x, y, mouseX, mouseY);
        renderResult(graphics, x, y);
        if (sideInsert != SideInsert.NONE) {
            renderSideInsert(graphics, mouseX, mouseY);
            renderHoveredUiTooltip(graphics, mouseX, mouseY);
            return;
        }
        if (!activeRecipePages.isEmpty()) {
            renderRecipePage(graphics, mouseX, mouseY);
            if (!hoveredRecipeStack.isEmpty()) {
                renderRecipeStackTooltip(graphics, mouseX, mouseY);
            }
            renderHoveredUiTooltip(graphics, mouseX, mouseY);
            return;
        }
        if (hoveredBookmark != null) {
            renderBookmarkTooltip(graphics, hoveredBookmark, mouseX, mouseY);
            return;
        }
        renderHoveredUiTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!activeRecipePages.isEmpty()) {
            if (button == 1) {
                goBackRecipePage();
                playPage();
                return true;
            }
            if (button == 0 && handleRecipePageClick(mouseX, mouseY)) {
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        int x = bookX();
        int y = bookY();
        if (handleSideTabClick(mouseX, mouseY, x, y)) {
            return true;
        }
        if (sideInsert != SideInsert.NONE && handleSideInsertClick(mouseX, mouseY)) {
            return true;
        }
        if (inside(mouseX, mouseY, x + 140, y + 218, 53, 12)) {
            playPage();
            minecraft.setScreen(new TCThaumonomiconBrowserScreen());
            return true;
        }
        if (inside(mouseX, mouseY, x + 6, y + 218, 24, 12) && spread > 0) {
            spread--;
            playPageTurn();
            return true;
        }
        if (inside(mouseX, mouseY, x + 303, y + 218, 24, 12) && spread < maxSpread()) {
            spread++;
            playPageTurn();
            return true;
        }
        if (inside(mouseX, mouseY, x + 134, y + 194, 64, 14) && canAdvance() && !pendingAdvance) {
            pendingAdvance = true;
            lastResult = Component.translatable("gui.thaumcraft.thaumonomicon.loading");
            PacketDistributor.sendToServer(new TCThaumonomiconActionPayload(
                    TCThaumonomiconActionPayload.ADVANCE_CURRENT_STAGE,
                    entry.research().key(),
                    TCThaumonomiconClientCache.revision()
            ));
            playWrite();
            return true;
        }
        if (hoveredBookmark != null) {
            openBookmark(hoveredBookmark);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && sideInsert != SideInsert.NONE) {
            sideInsert = SideInsert.NONE;
            playPage();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && !activeRecipePages.isEmpty()) {
            goBackRecipePage();
            playPage();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void rebuildLines() {
        ArrayList<FormattedCharSequence> rebuilt = new ArrayList<>();
        addLocalizedBody(rebuilt, entry.stageText());
        for (String addendum : entry.addendumTexts()) {
            rebuilt.add(FormattedCharSequence.EMPTY);
            addLocalizedBody(rebuilt, addendum);
        }
        if (!entry.complete()) {
            rebuilt.add(FormattedCharSequence.EMPTY);
            rebuilt.add(Component.translatable("gui.thaumcraft.thaumonomicon.requirements")
                    .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD)
                    .getVisualOrderText());
            addRequirementLines(rebuilt, entry.satisfiedRequirements(), ChatFormatting.DARK_GREEN);
            addRequirementLines(rebuilt, entry.missingRequirements(), ChatFormatting.DARK_RED);
            addRequirementLines(rebuilt, entry.blockedRequirements(), ChatFormatting.DARK_GRAY);
        }
        lines = List.copyOf(rebuilt);
    }

    private void addLocalizedBody(List<FormattedCharSequence> target, String translationKey) {
        String body = Component.translatable(translationKey).getString().replace("<BR>", "\n").replace("<br>", "\n");
        String[] paragraphs = body.split("\\n", -1);
        for (int index = 0; index < paragraphs.length; index++) {
            if (!paragraphs[index].isBlank()) {
                target.addAll(font.split(Component.literal(paragraphs[index]), PAGE_TEXT_WIDTH));
            }
            if (index < paragraphs.length - 1) {
                target.add(FormattedCharSequence.EMPTY);
            }
        }
    }

    private void addRequirementLines(
            List<FormattedCharSequence> target,
            List<String> requirements,
            ChatFormatting color
    ) {
        for (String requirement : requirements) {
            Component line = Component.literal("- " + requirement).withStyle(color);
            target.addAll(font.split(line, PAGE_TEXT_WIDTH));
        }
    }

    private void renderBook(GuiGraphics graphics, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0F);
        graphics.pose().scale(1.3F, 1.3F, 1.0F);
        graphics.blit(BOOK, 0, 0, 0.0F, 0.0F, 256, 181, 512, 512);
        graphics.pose().popPose();
    }

    private void renderTitle(GuiGraphics graphics, int x, int y) {
        Component title = Component.translatable(entry.research().name()).withStyle(ChatFormatting.DARK_PURPLE);
        String titleText = title.getString();
        if (font.width(titleText) > PAGE_TEXT_WIDTH) {
            titleText = font.plainSubstrByWidth(titleText, PAGE_TEXT_WIDTH - font.width("...")) + "...";
        }
        graphics.drawString(font, titleText, x + 82 - font.width(titleText) / 2, y + 16, 0xFF202020, false);
        graphics.drawString(
                font,
                Component.translatable(
                        "gui.thaumcraft.thaumonomicon.stage",
                        entry.selectedStage() + 1,
                        entry.research().totalStages()
                ),
                x + 183,
                y + 16,
                0xFF66553A,
                false
        );
    }

    private void renderPageText(GuiGraphics graphics, int x, int y) {
        int firstLine = spread * LINES_PER_PAGE * 2;
        renderTextPage(graphics, x + 22, y + 34, firstLine);
        renderTextPage(graphics, x + 183, y + 34, firstLine + LINES_PER_PAGE);
    }

    private void renderTextPage(GuiGraphics graphics, int x, int y, int firstLine) {
        for (int row = 0; row < LINES_PER_PAGE && firstLine + row < lines.size(); row++) {
            graphics.drawString(font, lines.get(firstLine + row), x, y + row * 8, 0xFF302616, false);
        }
    }

    private void renderNavigation(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        if (spread > 0) {
            blit(graphics, BROWSER, x + 6, y + 218, 0, 184, 12, 8, 256, 256);
        }
        if (spread < maxSpread()) {
            blit(graphics, BROWSER, x + 315, y + 218, 12, 184, 12, 8, 256, 256);
        }

        int backColor = inside(mouseX, mouseY, x + 140, y + 218, 53, 12) ? 0xFF805A24 : 0xFF4B351B;
        graphics.drawCenteredString(font, Component.translatable("recipe.return"), x + 166, y + 219, backColor);

        int advanceColor = canAdvance() ? 0xFF3F7A2F : 0xFF6B5E4E;
        if (inside(mouseX, mouseY, x + 134, y + 194, 64, 14) && canAdvance()) {
            advanceColor = 0xFF65A34D;
        }
        graphics.drawCenteredString(
                font,
                Component.translatable(entry.complete()
                        ? "gui.thaumcraft.thaumonomicon.complete"
                        : "gui.thaumcraft.thaumonomicon.advance"),
                x + 166,
                y + 196,
                advanceColor
        );
    }

    private void renderBookmarks(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        for (int index = 0; index < entry.bookmarks().size(); index++) {
            TCResearchPageBookmark bookmark = entry.bookmarks().get(index);
            int space = Math.min(25, Math.max(12, 200 / Math.max(1, entry.bookmarks().size())));
            int tabX = x + 280;
            int tabY = y - 8 + index * space;
            boolean hovered = inside(mouseX, mouseY, tabX, tabY, 30, 16);
            boolean selected = bookmark.id().equals(activeRecipeId);
            int le = hovered || selected ? 0 : 3;
            graphics.setColor(1.0F, selected ? 0.55F : 1.0F, selected ? 0.55F : 1.0F, 1.0F);
            blit(graphics, BOOK, tabX + le, tabY, 120 + le, 232, 28 - le, 16, 512, 512);
            blit(graphics, BOOK, tabX, tabY, 116, 232, 4, 16, 512, 512);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            ItemStack stack = firstBookmarkStack(bookmark);
            if (!stack.isEmpty()) {
                graphics.renderItem(stack, tabX + 7 - le, tabY, index);
            } else {
                int color = bookmark.pages().stream().allMatch(page -> page.availability() == TCResearchPageAvailability.READY)
                        ? 0xFF6A944B
                        : 0xFF9A7135;
                graphics.drawString(font, Integer.toString(index + 1), tabX + 9 - le, tabY + 4, color, true);
            }
            if (hovered) {
                hoveredBookmark = bookmark;
            }
        }
    }

    private ItemStack firstBookmarkStack(TCResearchPageBookmark bookmark) {
        for (TCResearchPageView page : bookmark.pages()) {
            if (page.craftingRecipe().isPresent()) {
                return page.craftingRecipe().get().result();
            }
            if (page.arcaneRecipe().isPresent()) {
                return page.arcaneRecipe().get().result();
            }
            if (page.crucibleRecipe().isPresent()) {
                return page.crucibleRecipe().get().result();
            }
            if (page.infusionRecipe().isPresent()) {
                return page.infusionRecipe().get().result();
            }
            if (page.blueprintRecipe().isPresent()) {
                return page.blueprintRecipe().get().displayStack();
            }
            if (page.displayRecipe().isPresent()) {
                return page.displayRecipe().get().result();
            }
        }
        return ItemStack.EMPTY;
    }

    private void renderBookmarkTooltip(
            GuiGraphics graphics,
            TCResearchPageBookmark bookmark,
            int mouseX,
            int mouseY
    ) {
        ArrayList<Component> lines = new ArrayList<>();
        lines.add(Component.literal(bookmark.id().toString()).withStyle(ChatFormatting.GOLD));
        for (TCResearchPageView page : bookmark.pages()) {
            ChatFormatting color = page.availability() == TCResearchPageAvailability.READY
                    ? ChatFormatting.GREEN
                    : ChatFormatting.GRAY;
            lines.add(Component.literal(page.kind() + " - " + page.availability()).withStyle(color));
        }
        boolean renderable = bookmark.pages().stream().anyMatch(TCThaumonomiconEntryScreen::hasRenderableRecipe);
        lines.add(Component.translatable(renderable
                        ? "gui.thaumcraft.thaumonomicon.bookmark_open"
                        : "gui.thaumcraft.thaumonomicon.bookmark_deferred")
                .withStyle(renderable ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_GRAY));
        graphics.renderComponentTooltip(font, lines, mouseX, mouseY);
    }

    private void openBookmark(TCResearchPageBookmark bookmark) {
        List<TCResearchPageView> renderable = bookmark.pages().stream()
                .filter(page -> page.availability() == TCResearchPageAvailability.READY)
                .filter(TCThaumonomiconEntryScreen::hasRenderableRecipe)
                .toList();
        if (renderable.isEmpty()) {
            lastResult = Component.translatable("gui.thaumcraft.thaumonomicon.recipe_unavailable");
            return;
        }
        openRecipePages(bookmark.id(), renderable, 0, true);
        playPage();
    }

    private void renderSideTabs(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        if (canShowAspectTab()) {
            int tabX = x - 48;
            int tabY = y + 9;
            boolean hovered = inside(mouseX, mouseY, tabX, tabY, 25, 16);
            int le = hovered || sideInsert == SideInsert.ASPECTS ? 0 : 3;
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            blit(graphics, BOOK, tabX + le, tabY, 76, 232, 24 - le, 16, 512, 512);
            blit(graphics, BOOK, tabX + 20, tabY, 100, 232, 4, 16, 512, 512);
            if (hovered) {
                hoveredUiTooltip = List.of(Component.translatable("tc.aspect.name"));
            }
        }
        if (canShowKnowledgeTab()) {
            int tabX = x - 49;
            int tabY = y + 32;
            boolean hovered = inside(mouseX, mouseY, tabX, tabY, 25, 16);
            int le = hovered || sideInsert == SideInsert.KNOWLEDGE ? 0 : 3;
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            blit(graphics, BOOK, tabX + le, tabY, 44, 232, 24 - le, 16, 512, 512);
            blit(graphics, BOOK, tabX + 20, tabY, 100, 232, 4, 16, 512, 512);
            if (hovered) {
                hoveredUiTooltip = List.of(Component.translatable("tc.knowledge.name"));
            }
        }
    }

    private void renderWarpWarning(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        if (sideInsert != SideInsert.NONE || !activeRecipePages.isEmpty() || entry.complete() || entry.warp() <= 0) {
            return;
        }
        int warp = Math.min(5, entry.warp());
        int iconX = x - 57;
        int iconY = y + 154;
        graphics.pose().pushPose();
        graphics.pose().translate(iconX + 10.0F, iconY + 10.0F, 30.0F);
        float scale = 0.42F + (float) Math.sin(System.currentTimeMillis() / 220.0D) * 0.04F;
        graphics.pose().scale(scale, scale, 1.0F);
        graphics.setColor(0.9F, 0.0F, 0.44F, 0.9F);
        blit(graphics, AURA_NODES, -16, -16, 160, 0, 32, 32, 2048, 2048);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.pose().popPose();
        Component level = Component.translatable("tc.forbidden.level." + warp);
        graphics.drawString(font, level, iconX - font.width(level) / 2 + 10, iconY - 3, 0xFFAA999F, false);
        if (inside(mouseX, mouseY, iconX - 10, iconY - 10, 28, 28)) {
            hoveredUiTooltip = List.of(Component.translatable("tc.warp.warn", level));
        }
    }

    private boolean handleSideTabClick(double mouseX, double mouseY, int x, int y) {
        if (canShowAspectTab() && inside(mouseX, mouseY, x - 48, y + 9, 25, 16)) {
            closeRecipePage(true);
            sideInsert = sideInsert == SideInsert.ASPECTS ? SideInsert.NONE : SideInsert.ASPECTS;
            aspectPage = Math.min(aspectPage, maxAspectPage());
            playPage();
            return true;
        }
        if (canShowKnowledgeTab() && inside(mouseX, mouseY, x - 49, y + 32, 25, 16)) {
            closeRecipePage(true);
            sideInsert = sideInsert == SideInsert.KNOWLEDGE ? SideInsert.NONE : SideInsert.KNOWLEDGE;
            playPage();
            return true;
        }
        return false;
    }

    private boolean handleSideInsertClick(double mouseX, double mouseY) {
        if (sideInsert != SideInsert.ASPECTS) {
            return false;
        }
        int x = recipePageX();
        int y = recipePageY();
        if (inside(mouseX, mouseY, x + 40, y + 232, 14, 14) && aspectPage > 0) {
            aspectPage--;
            playPage();
            return true;
        }
        if (inside(mouseX, mouseY, x + 204, y + 232, 14, 14) && aspectPage < maxAspectPage()) {
            aspectPage++;
            playPage();
            return true;
        }
        return false;
    }

    private void renderSideInsert(GuiGraphics graphics, int mouseX, int mouseY) {
        int x = recipePageX();
        int y = recipePageY();
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 100.0F);
        blit(graphics, PAPER, x, y, 0, 0, RECIPE_PAGE_SIZE - 1, RECIPE_PAGE_SIZE - 1, RECIPE_PAGE_SIZE, RECIPE_PAGE_SIZE);
        if (sideInsert == SideInsert.ASPECTS) {
            renderAspectInsert(graphics, x + 60, y + 24, mouseX, mouseY);
        } else if (sideInsert == SideInsert.KNOWLEDGE) {
            renderKnowledgeInsert(graphics, x, y, mouseX, mouseY);
        }
        graphics.pose().popPose();
    }

    private void renderAspectInsert(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        List<Aspect> aspects = knownAspects();
        if (aspects.isEmpty()) {
            graphics.drawCenteredString(font, Component.translatable("tc.aspect.name"), x + 64, y + 86, 0xFF505050);
            return;
        }
        aspectPage = Math.max(0, Math.min(aspectPage, maxAspectPage()));
        int start = aspectPage * ASPECTS_PER_PAGE;
        int end = Math.min(aspects.size(), start + ASPECTS_PER_PAGE);
        for (int index = start; index < end; index++) {
            Aspect aspect = aspects.get(index);
            int row = index - start;
            int rowY = y + row * 40;
            if (inside(mouseX, mouseY, x, rowY, 40, 40)) {
                drawFullTexture(graphics, ASPECT_BACK, x - 2, rowY - 2, 32, 32);
                hoveredUiTooltip = List.of(
                        Component.translatable("tc.aspect." + aspect.getTag()).withStyle(ChatFormatting.GOLD),
                        Component.literal(aspect.getTag()).withStyle(ChatFormatting.GRAY)
                );
            }
            drawAspectIcon(graphics, aspect, x + 2, rowY + 2, 24);
            drawCenteredSmall(graphics, Component.translatable("tc.aspect." + aspect.getTag()), x + 16, rowY + 29, 0xFF505050);
            Aspect[] components = aspect.getComponents();
            if (components == null || components.length < 2) {
                graphics.drawString(font, Component.translatable("tc.aspect.primal"), x + 54, rowY + 12, 0xFF777777, false);
                continue;
            }
            graphics.drawString(font, "=", x + 41, rowY + 12, 0xFF999999, false);
            renderAspectComponent(graphics, components[0], x + 60, rowY + 4, mouseX, mouseY);
            graphics.drawString(font, "+", x + 89, rowY + 12, 0xFF999999, false);
            renderAspectComponent(graphics, components[1], x + 102, rowY + 4, mouseX, mouseY);
        }
        if (aspectPage > 0) {
            blit(graphics, BROWSER, x - 20, y + 208, 0, 184, 12, 8, 256, 256);
        }
        if (aspectPage < maxAspectPage()) {
            blit(graphics, BROWSER, x + 144, y + 208, 12, 184, 12, 8, 256, 256);
        }
    }

    private void renderAspectComponent(GuiGraphics graphics, Aspect aspect, int x, int y, int mouseX, int mouseY) {
        boolean known = TCKnowledgeClientCache.hasResearch("!" + aspect.getTag());
        if (known) {
            drawAspectIcon(graphics, aspect, x, y, 20);
            drawCenteredSmall(graphics, Component.translatable("tc.aspect." + aspect.getTag()), x + 10, y + 25, 0xFF505050);
        } else {
            drawFullTexture(graphics, UNKNOWN, x, y, 20, 20);
        }
        if (inside(mouseX, mouseY, x, y, 20, 20)) {
            hoveredUiTooltip = known
                    ? List.of(Component.translatable("tc.aspect." + aspect.getTag()).withStyle(ChatFormatting.GOLD))
                    : List.of(Component.literal("???").withStyle(ChatFormatting.GRAY));
        }
    }

    private void renderKnowledgeInsert(GuiGraphics graphics, int paperX, int paperY, int mouseX, int mouseY) {
        graphics.drawCenteredString(font, Component.translatable("tc.knowledge.name"), paperX + 128, paperY + 24, 0xFF4B351B);
        Map<String, TCThaumonomiconCategoryView> categories = new LinkedHashMap<>();
        for (TCThaumonomiconCategoryView category : TCThaumonomiconClientCache.index().categories()) {
            categories.put(category.key(), category);
        }
        int row = 0;
        for (TCKnowledgeType type : List.of(TCKnowledgeType.THEORY, TCKnowledgeType.OBSERVATION)) {
            Map<String, Integer> raw = TCKnowledgeClientCache.rawKnowledgeByCategory(type);
            if (raw.isEmpty()) {
                continue;
            }
            int column = 0;
            int columnSpacing = Math.max(20, 164 / Math.max(1, Math.max(categories.size(), raw.size())));
            for (Map.Entry<String, Integer> rawEntry : raw.entrySet()) {
                int iconX = paperX + 50 + column * columnSpacing;
                int iconY = paperY + 57 + row * 28;
                renderKnowledgeIcon(graphics, type, rawEntry.getKey(), rawEntry.getValue(), categories.get(rawEntry.getKey()), iconX, iconY, mouseX, mouseY);
                column++;
            }
            row++;
        }
        if (row == 0) {
            graphics.drawCenteredString(font, Component.translatable("gui.thaumcraft.thaumonomicon.knowledge_empty"), paperX + 128, paperY + 104, 0xFF777777);
        }
    }

    private void renderKnowledgeIcon(
            GuiGraphics graphics,
            TCKnowledgeType type,
            String category,
            int raw,
            TCThaumonomiconCategoryView categoryView,
            int x,
            int y,
            int mouseX,
            int mouseY
    ) {
        drawFullTexture(graphics, type == TCKnowledgeType.THEORY ? KNOWLEDGE_THEORY : KNOWLEDGE_OBSERVATION, x, y, 16, 16);
        ResourceLocation icon = parseLocation(categoryView == null ? "" : categoryView.icon());
        if (icon != null) {
            graphics.setColor(1.0F, 1.0F, 1.0F, 0.75F);
            drawFullTexture(graphics, icon, x + 4, y + 4, 10, 10);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        String amount = Integer.toString(type.rawToPoints(raw));
        graphics.drawString(font, amount, x + 16 - font.width(amount), y + 8, 0xFFFFFFFF, true);
        int partial = raw % type.rawUnitsPerPoint();
        if (partial > 0) {
            int width = Math.max(1, (int) (partial / (float) type.rawUnitsPerPoint() * 16.0F));
            graphics.setColor(1.0F, 1.0F, 1.0F, 0.75F);
            blit(graphics, BOOK, x, y + 17, 0, 232, width, 2, 512, 512);
            blit(graphics, BOOK, x + width, y + 17, width, 234, 16 - width, 2, 512, 512);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        if (inside(mouseX, mouseY, x, y, 16, 18)) {
            hoveredUiTooltip = List.of(
                    Component.translatable("tc.type." + type.id()).withStyle(ChatFormatting.GOLD),
                    Component.translatable("tc.research_category." + category).withStyle(ChatFormatting.GRAY),
                    Component.literal(raw + "/" + type.rawUnitsPerPoint()).withStyle(ChatFormatting.DARK_GRAY)
            );
        }
    }

    private List<Aspect> knownAspects() {
        return Aspect.aspects.values().stream()
                .filter(aspect -> TCKnowledgeClientCache.hasResearch("!" + aspect.getTag()))
                .sorted(Comparator.comparing(aspect -> Component.translatable("tc.aspect." + aspect.getTag()).getString()))
                .toList();
    }

    private int maxAspectPage() {
        return Math.max(0, (knownAspects().size() - 1) / ASPECTS_PER_PAGE);
    }

    private boolean canShowAspectTab() {
        return TCKnowledgeClientCache.hasResearch("FIRSTSTEPS");
    }

    private boolean canShowKnowledgeTab() {
        return TCKnowledgeClientCache.hasResearch("KNOWLEDGETYPES")
                && !entry.research().key().equals("KNOWLEDGETYPES");
    }

    private void drawAspectIcon(GuiGraphics graphics, Aspect aspect, int x, int y, int size) {
        int color = aspect.getColor();
        float red = ((color >> 16) & 255) / 255.0F;
        float green = ((color >> 8) & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        graphics.setColor(red, green, blue, 1.0F);
        drawFullTexture(graphics, aspect.getImage(), x, y, size, size);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawCenteredSmall(GuiGraphics graphics, Component component, int centerX, int y, int color) {
        graphics.pose().pushPose();
        graphics.pose().translate(centerX, y, 0.0F);
        graphics.pose().scale(0.5F, 0.5F, 1.0F);
        String text = component.getString();
        graphics.drawString(font, text, -font.width(text) / 2, 0, color, false);
        graphics.pose().popPose();
    }

    private boolean handleRecipePageClick(double mouseX, double mouseY) {
        int x = recipePageX();
        int y = recipePageY();
        if (inside(mouseX, mouseY, x + 96, y + 236, 64, 16)) {
            goBackRecipePage();
            playPage();
            return true;
        }
        if (inside(mouseX, mouseY, x + 34, y + 226, 36, 24) && activeRecipeIndex > 0) {
            activeRecipeIndex--;
            playPageTurn();
            return true;
        }
        if (inside(mouseX, mouseY, x + 186, y + 226, 36, 24)
                && activeRecipeIndex < activeRecipePages.size() - 1) {
            activeRecipeIndex++;
            playPageTurn();
            return true;
        }
        if (!hoveredRecipeStack.isEmpty() && !pendingDrilldown) {
            pendingDrilldown = true;
            lastResult = Component.translatable("gui.thaumcraft.thaumonomicon.loading");
            PacketDistributor.sendToServer(new TCThaumonomiconDrilldownRequestPayload(
                    hoveredRecipeStack,
                    TCThaumonomiconClientCache.revision()
            ));
            playPage();
            return true;
        }
        return false;
    }

    private void closeRecipePage() {
        closeRecipePage(true);
    }

    private void closeRecipePage(boolean clearHistory) {
        activeRecipeId = null;
        activeRecipePages = List.of();
        activeRecipeIndex = 0;
        if (clearHistory) {
            recipeHistory.clear();
        }
        hoveredRecipeStack = ItemStack.EMPTY;
        pendingDrilldown = false;
    }

    private void goBackRecipePage() {
        if (!recipeHistory.isEmpty()) {
            RecipePageState state = recipeHistory.pop();
            activeRecipeId = state.id();
            activeRecipePages = state.pages();
            activeRecipeIndex = Math.max(0, Math.min(state.pageIndex(), Math.max(0, activeRecipePages.size() - 1)));
            hoveredRecipeStack = ItemStack.EMPTY;
            pendingDrilldown = false;
            return;
        }
        closeRecipePage(false);
    }

    private void openDrilldown(TCResearchPageBookmark bookmark, int pageIndex) {
        List<TCResearchPageView> renderable = bookmark.pages().stream()
                .filter(page -> page.availability() == TCResearchPageAvailability.READY)
                .filter(TCThaumonomiconEntryScreen::hasRenderableRecipe)
                .toList();
        if (renderable.isEmpty()) {
            lastResult = Component.translatable("gui.thaumcraft.thaumonomicon.recipe_unavailable");
            return;
        }
        if (!activeRecipePages.isEmpty()) {
            recipeHistory.push(new RecipePageState(activeRecipeId, activeRecipePages, activeRecipeIndex));
        }
        openRecipePages(bookmark.id(), renderable, renderableIndex(bookmark.pages(), pageIndex), false);
        playPage();
    }

    private void openRecipePages(
            ResourceLocation id,
            List<TCResearchPageView> pages,
            int pageIndex,
            boolean clearHistory
    ) {
        activeRecipeId = id;
        activeRecipePages = List.copyOf(pages);
        activeRecipeIndex = Math.max(0, Math.min(pageIndex, Math.max(0, activeRecipePages.size() - 1)));
        if (clearHistory) {
            recipeHistory.clear();
        }
        hoveredRecipeStack = ItemStack.EMPTY;
        pendingDrilldown = false;
    }

    private int renderableIndex(List<TCResearchPageView> pages, int rawPageIndex) {
        int index = 0;
        for (int raw = 0; raw < pages.size() && raw < rawPageIndex; raw++) {
            TCResearchPageView page = pages.get(raw);
            if (page.availability() == TCResearchPageAvailability.READY && hasRenderableRecipe(page)) {
                index++;
            }
        }
        return index;
    }

    private void renderRecipePage(GuiGraphics graphics, int mouseX, int mouseY) {
        TCResearchPageView page = activeRecipePages.get(activeRecipeIndex);
        int x = recipePageX();
        int y = recipePageY();
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 100.0F);
        blit(graphics, PAPER, x, y, 0, 0, RECIPE_PAGE_SIZE - 1, RECIPE_PAGE_SIZE - 1, RECIPE_PAGE_SIZE, RECIPE_PAGE_SIZE);
        page.craftingRecipe().ifPresentOrElse(
                recipe -> renderCraftingRecipe(graphics, recipe, x + 128, y + 128, mouseX, mouseY),
                () -> page.arcaneRecipe().ifPresentOrElse(
                        recipe -> renderArcaneRecipe(graphics, recipe, x + 128, y + 128, mouseX, mouseY),
                        () -> page.crucibleRecipe().ifPresentOrElse(
                                recipe -> renderCrucibleRecipe(graphics, recipe, x + 128, y + 128, mouseX, mouseY),
                                () -> page.infusionRecipe().ifPresentOrElse(
                                        recipe -> renderInfusionRecipe(graphics, recipe, x + 128, y + 128, mouseX, mouseY),
                                        () -> page.blueprintRecipe().ifPresent(
                                                recipe -> renderBlueprintRecipe(graphics, recipe, x + 128, y + 128, mouseX, mouseY)
                                        )
                                )
                        )
                )
        );
        page.displayRecipe().ifPresent(recipe -> renderDisplayRecipe(graphics, recipe, x + 128, y + 128, mouseX, mouseY));
        renderRecipeNavigation(graphics, x, y, mouseX, mouseY);
        graphics.pose().popPose();
    }

    private void renderCraftingRecipe(
            GuiGraphics graphics,
            TCCraftingRecipePageView recipe,
            int centerX,
            int centerY,
            int mouseX,
            int mouseY
    ) {
        Component type = Component.translatable(recipe.shaped()
                ? "recipe.type.workbench"
                : "recipe.type.workbenchshapeless");
        graphics.drawCenteredString(font, type, centerX, centerY - 104, 0xFF505050);

        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0.0F);
        graphics.pose().scale(2.0F, 2.0F, 1.0F);
        blit(graphics, BOOK_OVERLAY, -26, -26, 60, 15, 51, 52, 512, 512);
        blit(graphics, BOOK_OVERLAY, -8, -46, 20, 3, 16, 16, 512, 512);
        graphics.pose().popPose();

        renderRecipeStack(graphics, recipe.result(), centerX - 8, centerY - 84, mouseX, mouseY);
        for (int slot = 0; slot < recipe.ingredients().size() && slot < 9; slot++) {
            List<ItemStack> variants = recipe.ingredients().get(slot);
            if (variants.isEmpty()) {
                continue;
            }
            int column = recipe.shaped() ? slot % recipe.width() : slot % 3;
            int row = recipe.shaped() ? slot / recipe.width() : slot / 3;
            if (column >= 3 || row >= 3) {
                continue;
            }
            int variant = (int) ((System.currentTimeMillis() / 1000L + slot) % variants.size());
            renderRecipeStack(
                    graphics,
                    variants.get(variant),
                    centerX - 40 + column * 32,
                    centerY - 40 + row * 32,
                    mouseX,
                    mouseY
            );
        }
    }

    private void renderArcaneRecipe(
            GuiGraphics graphics,
            TCArcaneRecipePageView recipe,
            int centerX,
            int centerY,
            int mouseX,
            int mouseY
    ) {
        Component type = Component.translatable(recipe.shaped()
                ? "recipe.type.arcane"
                : "recipe.type.arcane.shapeless");
        graphics.drawCenteredString(font, type, centerX, centerY - 104, 0xFF505050);

        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0.0F);
        graphics.pose().scale(2.0F, 2.0F, 1.0F);
        blit(graphics, BOOK_OVERLAY, -26, -26, 112, 15, 52, 52, 512, 512);
        blit(graphics, BOOK_OVERLAY, -8, -46, 20, 3, 16, 16, 512, 512);
        graphics.pose().popPose();

        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0.0F);
        graphics.pose().scale(2.0F, 2.0F, 1.0F);
        graphics.setColor(1.0F, 1.0F, 1.0F, 0.4F);
        blit(graphics, BOOK_OVERLAY, -6, 40, 68, 76, 12, 12, 512, 512);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.pose().popPose();

        String vis = Integer.toString(recipe.vis());
        graphics.drawString(font, vis, centerX - font.width(vis) / 2, centerY + 90, 0xFF505050, false);

        renderRecipeStack(graphics, recipe.result(), centerX - 8, centerY - 84, mouseX, mouseY);

        int crystalCount = recipe.crystalStacks().size();
        for (int index = 0; index < crystalCount; index++) {
            renderRecipeStack(
                    graphics,
                    recipe.crystalStacks().get(index),
                    centerX + 4 - crystalCount * 10 + index * 20,
                    centerY + 59,
                    mouseX,
                    mouseY
            );
        }

        for (int slot = 0; slot < recipe.ingredients().size() && slot < 9; slot++) {
            List<ItemStack> variants = recipe.ingredients().get(slot);
            if (variants.isEmpty()) {
                continue;
            }
            int column = recipe.shaped() ? slot % recipe.width() : slot % 3;
            int row = recipe.shaped() ? slot / recipe.width() : slot / 3;
            if (column >= 3 || row >= 3) {
                continue;
            }
            int variant = (int) ((System.currentTimeMillis() / 1000L + slot) % variants.size());
            renderRecipeStack(
                    graphics,
                    variants.get(variant),
                    centerX - 40 + column * 32,
                    centerY - 40 + row * 32,
                    mouseX,
                    mouseY
            );
        }
    }


    private void renderCrucibleRecipe(
            GuiGraphics graphics,
            TCCrucibleRecipePageView recipe,
            int centerX,
            int centerY,
            int mouseX,
            int mouseY
    ) {
        graphics.drawCenteredString(font, Component.translatable("recipe.type.crucible"), centerX, centerY - 104, 0xFF505050);

        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0.0F);
        graphics.pose().scale(2.0F, 2.0F, 1.0F);
        blit(graphics, BOOK_OVERLAY, -26, -26, 60, 15, 51, 52, 512, 512);
        graphics.pose().popPose();

        renderRecipeStack(graphics, recipe.result(), centerX - 8, centerY - 84, mouseX, mouseY);

        if (!recipe.catalystVariants().isEmpty()) {
            int variant = (int) ((System.currentTimeMillis() / 1000L) % recipe.catalystVariants().size());
            renderRecipeStack(graphics, recipe.catalystVariants().get(variant), centerX - 8, centerY - 8, mouseX, mouseY);
        }

        int aspectCount = recipe.aspectStacks().size();
        for (int index = 0; index < aspectCount; index++) {
            renderRecipeStack(
                    graphics,
                    recipe.aspectStacks().get(index),
                    centerX + 4 - aspectCount * 10 + index * 20,
                    centerY + 59,
                    mouseX,
                    mouseY
            );
        }
    }
    private void renderInfusionRecipe(
            GuiGraphics graphics,
            TCInfusionRecipePageView recipe,
            int centerX,
            int centerY,
            int mouseX,
            int mouseY
    ) {
        graphics.drawCenteredString(font, Component.translatable("recipe.type.infusion"), centerX, centerY - 104, 0xFF505050);

        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0.0F);
        graphics.pose().scale(2.0F, 2.0F, 1.0F);
        blit(graphics, BOOK_OVERLAY, -26, -26, 112, 15, 52, 52, 512, 512);
        graphics.pose().popPose();

        renderRecipeStack(graphics, recipe.result(), centerX - 8, centerY - 84, mouseX, mouseY);

        if (!recipe.catalystVariants().isEmpty()) {
            int variant = (int) ((System.currentTimeMillis() / 1000L) % recipe.catalystVariants().size());
            renderRecipeStack(graphics, recipe.catalystVariants().get(variant), centerX - 8, centerY - 8, mouseX, mouseY);
        }

        int componentCount = recipe.componentVariants().size();
        for (int index = 0; index < componentCount; index++) {
            List<ItemStack> variants = recipe.componentVariants().get(index);
            if (variants.isEmpty()) {
                continue;
            }
            int variant = (int) ((System.currentTimeMillis() / 1000L + index) % variants.size());
            double angle = Math.PI * 2.0D * index / Math.max(1, componentCount);
            int x = centerX - 8 + (int) Math.round(Math.cos(angle) * 64.0D);
            int y = centerY - 8 + (int) Math.round(Math.sin(angle) * 44.0D);
            renderRecipeStack(graphics, variants.get(variant), x, y, mouseX, mouseY);
        }

        int aspectCount = recipe.aspectStacks().size();
        for (int index = 0; index < aspectCount; index++) {
            renderRecipeStack(
                    graphics,
                    recipe.aspectStacks().get(index),
                    centerX + 4 - aspectCount * 10 + index * 20,
                    centerY + 59,
                    mouseX,
                    mouseY
            );
        }

        String instability = Integer.toString(recipe.instability());
        graphics.drawString(font, instability, centerX - font.width(instability) / 2, centerY + 88, 0xFF805A24, false);
    }

    private void renderDisplayRecipe(
            GuiGraphics graphics,
            TCDisplayRecipePageView recipe,
            int centerX,
            int centerY,
            int mouseX,
            int mouseY
    ) {
        Component type = switch (recipe.type()) {
            case FAKE_CRAFTING -> Component.translatable("recipe.type.workbenchshapeless");
            case INFUSION_ENCHANTMENT -> Component.translatable("recipe.type.infusion_enchantment");
            case RUNIC_AUGMENT -> Component.translatable("recipe.type.runic_augment");
        };
        graphics.drawCenteredString(font, type, centerX, centerY - 104, 0xFF505050);
        if (!recipe.titleKey().isBlank()) {
            graphics.drawCenteredString(font, Component.translatable(recipe.titleKey()), centerX, centerY - 92, 0xFF6D4C24);
        }

        if (recipe.type() == TCDisplayRecipePageType.FAKE_CRAFTING) {
            renderDisplayCraftingRecipe(graphics, recipe, centerX, centerY, mouseX, mouseY);
        } else {
            renderDisplayInfusionRecipe(graphics, recipe, centerX, centerY, mouseX, mouseY);
        }
    }

    private void renderDisplayCraftingRecipe(
            GuiGraphics graphics,
            TCDisplayRecipePageView recipe,
            int centerX,
            int centerY,
            int mouseX,
            int mouseY
    ) {
        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0.0F);
        graphics.pose().scale(2.0F, 2.0F, 1.0F);
        blit(graphics, BOOK_OVERLAY, -26, -26, 60, 15, 51, 52, 512, 512);
        blit(graphics, BOOK_OVERLAY, -8, -46, 20, 3, 16, 16, 512, 512);
        graphics.pose().popPose();

        renderRecipeStack(graphics, recipe.result(), centerX - 8, centerY - 84, mouseX, mouseY);
        for (int slot = 0; slot < recipe.componentStacks().size() && slot < 9; slot++) {
            int column = slot % 3;
            int row = slot / 3;
            renderRecipeStack(
                    graphics,
                    recipe.componentStacks().get(slot),
                    centerX - 40 + column * 32,
                    centerY - 40 + row * 32,
                    mouseX,
                    mouseY
            );
        }
    }

    private void renderDisplayInfusionRecipe(
            GuiGraphics graphics,
            TCDisplayRecipePageView recipe,
            int centerX,
            int centerY,
            int mouseX,
            int mouseY
    ) {
        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0.0F);
        graphics.pose().scale(2.0F, 2.0F, 1.0F);
        blit(graphics, BOOK_OVERLAY, -26, -26, 112, 15, 52, 52, 512, 512);
        graphics.pose().popPose();

        renderRecipeStack(graphics, recipe.result(), centerX - 8, centerY - 84, mouseX, mouseY);
        if (!recipe.catalystStacks().isEmpty()) {
            renderRecipeStack(graphics, recipe.catalystStacks().getFirst(), centerX - 8, centerY - 8, mouseX, mouseY);
        }

        int componentCount = recipe.componentStacks().size();
        for (int index = 0; index < componentCount; index++) {
            double angle = Math.PI * 2.0D * index / Math.max(1, componentCount);
            int x = centerX - 8 + (int) Math.round(Math.cos(angle) * 64.0D);
            int y = centerY - 8 + (int) Math.round(Math.sin(angle) * 44.0D);
            renderRecipeStack(graphics, recipe.componentStacks().get(index), x, y, mouseX, mouseY);
        }

        int aspectCount = recipe.aspectStacks().size();
        for (int index = 0; index < aspectCount; index++) {
            renderRecipeStack(
                    graphics,
                    recipe.aspectStacks().get(index),
                    centerX + 4 - aspectCount * 10 + index * 20,
                    centerY + 59,
                    mouseX,
                    mouseY
            );
        }

        if (recipe.instability() > 0) {
            String instability = Integer.toString(recipe.instability());
            graphics.drawString(font, instability, centerX - font.width(instability) / 2, centerY + 88, 0xFF805A24, false);
        }
    }

    private void renderBlueprintRecipe(
            GuiGraphics graphics,
            TCBlueprintRecipePageView recipe,
            int centerX,
            int centerY,
            int mouseX,
            int mouseY
    ) {
        graphics.drawCenteredString(font, Component.translatable("recipe.type.construct"), centerX, centerY - 104, 0xFF505050);
        if (!recipe.displayStack().isEmpty()) {
            renderRecipeStack(graphics, recipe.displayStack(), centerX - 8, centerY - 84, mouseX, mouseY);
        }

        int layerCount = recipe.layers().size();
        int gridSize = 18;
        int gridGap = 8;
        int maxRows = 0;
        int totalWidth = Math.max(0, (layerCount - 1) * gridGap);
        for (List<List<TCBlueprintRecipePageView.Cell>> layer : recipe.layers()) {
            int rows = layer.size();
            int columns = layer.stream().mapToInt(List::size).max().orElse(0);
            maxRows = Math.max(maxRows, rows);
            totalWidth += columns * gridSize;
        }
        int x = centerX - totalWidth / 2;
        int y = centerY - 48 - Math.max(0, maxRows - 3) * 3;
        for (int layerIndex = 0; layerIndex < layerCount; layerIndex++) {
            List<List<TCBlueprintRecipePageView.Cell>> layer = recipe.layers().get(layerIndex);
            int rows = layer.size();
            int columns = layer.stream().mapToInt(List::size).max().orElse(0);
            int layerWidth = columns * gridSize;
            String label = Integer.toString(layerIndex + 1);
            graphics.drawCenteredString(font, label, x + layerWidth / 2, y - 11, 0xFF6D4C24);
            for (int row = 0; row < rows; row++) {
                List<TCBlueprintRecipePageView.Cell> cells = layer.get(row);
                for (int column = 0; column < cells.size(); column++) {
                    TCBlueprintRecipePageView.Cell cell = cells.get(column);
                    int cellX = x + column * gridSize;
                    int cellY = y + row * gridSize;
                    int frame = cell.sourceStack().isEmpty() ? 0x309A7135 : 0xA06A944B;
                    graphics.fill(cellX, cellY, cellX + 17, cellY + 17, frame);
                    graphics.fill(cellX + 1, cellY + 1, cellX + 16, cellY + 16, 0x70E8D7A5);
                    if (!cell.sourceStack().isEmpty()) {
                        renderRecipeStack(graphics, cell.sourceStack(), cellX + 1, cellY + 1, mouseX, mouseY);
                    }
                    if (!cell.targetStack().isEmpty()
                            && (cell.sourceStack().isEmpty()
                            || !cell.sourceStack().is(cell.targetStack().getItem()))) {
                        graphics.pose().pushPose();
                        graphics.pose().translate(cellX + 10.0F, cellY + 10.0F, 120.0F);
                        graphics.pose().scale(0.5F, 0.5F, 1.0F);
                        graphics.renderItem(cell.targetStack(), 0, 0);
                        graphics.pose().popPose();
                    }
                }
            }
            x += layerWidth + gridGap;
        }

        int ingredientCount = recipe.ingredientStacks().size();
        if (ingredientCount > 0) {
            int ingredientsWidth = ingredientCount * 17;
            int startX = centerX - ingredientsWidth / 2;
            int startY = centerY + 90;
            for (int index = 0; index < ingredientCount; index++) {
                renderRecipeStack(
                        graphics,
                        recipe.ingredientStacks().get(index),
                        startX + index * 17,
                        startY,
                        mouseX,
                        mouseY
                );
            }
        }
    }

    private void renderRecipeStack(
            GuiGraphics graphics,
            ItemStack stack,
            int x,
            int y,
            int mouseX,
            int mouseY
    ) {
        if (stack.isEmpty()) {
            return;
        }
        graphics.renderItem(stack, x, y);
        graphics.renderItemDecorations(font, stack, x, y);
        if (inside(mouseX, mouseY, x, y, 16, 16)) {
            hoveredRecipeStack = stack;
        }
    }

    private void renderRecipeStackTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        ArrayList<Component> lines = new ArrayList<>(getTooltipFromItem(minecraft, hoveredRecipeStack));
        if (pendingDrilldown) {
            lines.add(Component.translatable("gui.thaumcraft.thaumonomicon.loading")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        } else {
            lines.add(Component.translatable("recipe.clickthrough")
                    .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
        }
        graphics.renderComponentTooltip(font, lines, mouseX, mouseY);
    }

    private void renderRecipeNavigation(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        if (activeRecipeIndex > 0) {
            blit(graphics, BROWSER, x + 40, y + 232, 0, 184, 12, 8, 256, 256);
        }
        if (activeRecipeIndex < activeRecipePages.size() - 1) {
            blit(graphics, BROWSER, x + 204, y + 232, 12, 184, 12, 8, 256, 256);
        }
        if (activeRecipePages.size() > 1) {
            String page = (activeRecipeIndex + 1) + "/" + activeRecipePages.size();
            graphics.drawCenteredString(font, page, x + 128, y + 224, 0xFF6D4C24);
        }
        int color = inside(mouseX, mouseY, x + 96, y + 236, 64, 16) ? 0xFF805A24 : 0xFF4B351B;
        graphics.drawCenteredString(font, Component.translatable("recipe.return"), x + 128, y + 238, color);
    }

    private static boolean hasRenderableRecipe(TCResearchPageView page) {
        return page.craftingRecipe().isPresent()
                || page.arcaneRecipe().isPresent()
                || page.crucibleRecipe().isPresent()
                || page.infusionRecipe().isPresent()
                || page.blueprintRecipe().isPresent()
                || page.displayRecipe().isPresent();
    }

    private void renderResult(GuiGraphics graphics, int x, int y) {
        if (lastResult.getString().isBlank()) {
            return;
        }
        graphics.drawCenteredString(font, lastResult, x + BOOK_WIDTH / 2, y + BOOK_HEIGHT + 4, 0xFFFFFFFF);
    }

    private void renderHoveredUiTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!hoveredUiTooltip.isEmpty()) {
            graphics.renderComponentTooltip(font, hoveredUiTooltip, mouseX, mouseY);
        }
    }

    private boolean canAdvance() {
        return !entry.complete()
                && entry.missingRequirements().isEmpty()
                && entry.blockedRequirements().isEmpty();
    }

    private int maxSpread() {
        int totalPages = Math.max(1, (lines.size() + LINES_PER_PAGE - 1) / LINES_PER_PAGE);
        return Math.max(0, (totalPages - 1) / 2);
    }

    private int bookX() {
        return (width - BOOK_WIDTH) / 2;
    }

    private int bookY() {
        return (height - BOOK_HEIGHT) / 2;
    }

    private int recipePageX() {
        return (width - RECIPE_PAGE_SIZE) / 2;
    }

    private int recipePageY() {
        return (height - RECIPE_PAGE_SIZE) / 2;
    }

    private void playPage() {
        if (minecraft != null && minecraft.player != null) {
            minecraft.player.playSound(TCSounds.PAGE.get(), 0.66F, 1.0F);
        }
    }

    private void playPageTurn() {
        if (minecraft != null && minecraft.player != null) {
            minecraft.player.playSound(TCSounds.PAGETURN.get(), 0.66F, 1.0F);
        }
    }

    private void playWrite() {
        if (minecraft != null && minecraft.player != null) {
            minecraft.player.playSound(TCSounds.WRITE.get(), 0.66F, 1.0F);
        }
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    private static ResourceLocation parseLocation(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return ResourceLocation.parse(raw);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private record RecipePageState(
            ResourceLocation id,
            List<TCResearchPageView> pages,
            int pageIndex
    ) {
        private RecipePageState {
            pages = List.copyOf(pages);
        }
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

    private enum SideInsert {
        NONE,
        ASPECTS,
        KNOWLEDGE
    }
}

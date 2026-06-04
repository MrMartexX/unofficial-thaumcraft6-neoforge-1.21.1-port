package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.Thaumcraft;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.research.TCResearchPageAvailability;
import thaumcraft.common.research.TCResearchPageBookmark;
import thaumcraft.common.research.TCResearchPageView;
import thaumcraft.common.research.TCThaumonomiconActionPayload;
import thaumcraft.common.research.TCThaumonomiconClientCache;
import thaumcraft.common.research.TCThaumonomiconEntryPayload;
import thaumcraft.common.research.TCThaumonomiconEntryView;

public final class TCThaumonomiconEntryScreen extends Screen {
    private static final ResourceLocation BOOK =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_researchbook.png");
    private static final ResourceLocation BROWSER =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_research_browser.png");
    private static final int BOOK_WIDTH = 333;
    private static final int BOOK_HEIGHT = 235;
    private static final int PAGE_TEXT_WIDTH = 126;
    private static final int LINES_PER_PAGE = 21;

    private TCThaumonomiconEntryView entry;
    private List<FormattedCharSequence> lines = List.of();
    private int spread;
    private boolean pendingAdvance;
    private Component lastResult = Component.empty();
    private TCResearchPageBookmark hoveredBookmark;

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
        if (result == null || !result.researchKey().equals(entry.research().key())) {
            return;
        }
        pendingAdvance = false;
        lastResult = Component.translatable("gui.thaumcraft.thaumonomicon.result." + result.resultKey());
        if (result.accepted() && result.entry().isPresent()) {
            entry = result.entry().get();
            spread = 0;
            rebuildLines();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        hoveredBookmark = null;
        graphics.fill(0, 0, width, height, 0xB0100B16);
        int x = bookX();
        int y = bookY();
        renderBook(graphics, x, y);
        renderTitle(graphics, x, y);
        renderPageText(graphics, x, y);
        renderNavigation(graphics, x, y, mouseX, mouseY);
        renderBookmarks(graphics, x, y, mouseX, mouseY);
        renderResult(graphics, x, y);
        if (hoveredBookmark != null) {
            renderBookmarkTooltip(graphics, hoveredBookmark, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        int x = bookX();
        int y = bookY();
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
                    entry.research().key()
            ));
            playWrite();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
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
            int tabX = x + BOOK_WIDTH - 4;
            int tabY = y + 25 + index * 18;
            int color = bookmark.pages().stream().allMatch(page -> page.availability() == TCResearchPageAvailability.READY)
                    ? 0xFF6A944B
                    : 0xFF9A7135;
            graphics.fill(tabX, tabY, tabX + 20, tabY + 13, color);
            graphics.drawString(font, Integer.toString(index + 1), tabX + 6, tabY + 2, 0xFFFFFFFF, true);
            if (inside(mouseX, mouseY, tabX, tabY, 20, 13)) {
                hoveredBookmark = bookmark;
            }
        }
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
        lines.add(Component.translatable("gui.thaumcraft.thaumonomicon.bookmark_deferred")
                .withStyle(ChatFormatting.DARK_GRAY));
        graphics.renderComponentTooltip(font, lines, mouseX, mouseY);
    }

    private void renderResult(GuiGraphics graphics, int x, int y) {
        if (lastResult.getString().isBlank()) {
            return;
        }
        graphics.drawCenteredString(font, lastResult, x + BOOK_WIDTH / 2, y + BOOK_HEIGHT + 4, 0xFFFFFFFF);
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

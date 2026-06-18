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
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import thaumcraft.Thaumcraft;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.research.TCArcaneRecipePageView;
import thaumcraft.common.research.TCCraftingRecipePageView;
import thaumcraft.common.research.TCCrucibleRecipePageView;
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
    private static final ResourceLocation BOOK_OVERLAY =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_researchbook_overlay.png");
    private static final ResourceLocation PAPER =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/paper.png");
    private static final int BOOK_WIDTH = 333;
    private static final int BOOK_HEIGHT = 235;
    private static final int RECIPE_PAGE_SIZE = 256;
    private static final int PAGE_TEXT_WIDTH = 126;
    private static final int LINES_PER_PAGE = 21;

    private TCThaumonomiconEntryView entry;
    private List<FormattedCharSequence> lines = List.of();
    private int spread;
    private boolean pendingAdvance;
    private Component lastResult = Component.empty();
    private TCResearchPageBookmark hoveredBookmark;
    private List<TCResearchPageView> activeRecipePages = List.of();
    private int activeRecipeIndex;
    private ItemStack hoveredRecipeStack = ItemStack.EMPTY;

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
            closeRecipePage();
            rebuildLines();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        hoveredBookmark = null;
        hoveredRecipeStack = ItemStack.EMPTY;
        graphics.fill(0, 0, width, height, 0xB0100B16);
        int x = bookX();
        int y = bookY();
        renderBook(graphics, x, y);
        renderTitle(graphics, x, y);
        renderPageText(graphics, x, y);
        renderNavigation(graphics, x, y, mouseX, mouseY);
        renderBookmarks(graphics, x, y, mouseX, mouseY);
        renderResult(graphics, x, y);
        if (!activeRecipePages.isEmpty()) {
            renderRecipePage(graphics, mouseX, mouseY);
            if (!hoveredRecipeStack.isEmpty()) {
                graphics.renderTooltip(font, hoveredRecipeStack, mouseX, mouseY);
            }
            return;
        }
        if (hoveredBookmark != null) {
            renderBookmarkTooltip(graphics, hoveredBookmark, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!activeRecipePages.isEmpty()) {
            if (button == 1) {
                closeRecipePage();
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
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && !activeRecipePages.isEmpty()) {
            closeRecipePage();
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
        activeRecipePages = renderable;
        activeRecipeIndex = 0;
        playPage();
    }

    private boolean handleRecipePageClick(double mouseX, double mouseY) {
        int x = recipePageX();
        int y = recipePageY();
        if (inside(mouseX, mouseY, x + 96, y + 236, 64, 16)) {
            closeRecipePage();
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
        return false;
    }

    private void closeRecipePage() {
        activeRecipePages = List.of();
        activeRecipeIndex = 0;
        hoveredRecipeStack = ItemStack.EMPTY;
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
                        () -> page.crucibleRecipe().ifPresent(
                                recipe -> renderCrucibleRecipe(graphics, recipe, x + 128, y + 128, mouseX, mouseY)
                        )
                )
        );
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

    private void renderRecipeNavigation(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        if (activeRecipeIndex > 0) {
            blit(graphics, BROWSER, x + 40, y + 232, 0, 184, 12, 8, 256, 256);
        }
        if (activeRecipeIndex < activeRecipePages.size() - 1) {
            blit(graphics, BROWSER, x + 204, y + 232, 12, 184, 12, 8, 256, 256);
        }
        int color = inside(mouseX, mouseY, x + 96, y + 236, 64, 16) ? 0xFF805A24 : 0xFF4B351B;
        graphics.drawCenteredString(font, Component.translatable("recipe.return"), x + 128, y + 238, color);
    }

    private static boolean hasRenderableRecipe(TCResearchPageView page) {
        return page.craftingRecipe().isPresent() || page.arcaneRecipe().isPresent() || page.crucibleRecipe().isPresent();
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

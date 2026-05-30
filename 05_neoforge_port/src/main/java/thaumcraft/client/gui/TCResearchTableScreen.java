package thaumcraft.client.gui;

import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.Thaumcraft;
import thaumcraft.common.menu.TCResearchTableMenu;
import thaumcraft.common.research.TCResearchCategoryDefinition;
import thaumcraft.common.research.TCResearchManager;
import thaumcraft.common.research.theorycraft.TCResearchTableActionPayload;
import thaumcraft.common.research.theorycraft.TCResearchTableActionResultPayload;
import thaumcraft.common.research.theorycraft.TCResearchTableClientCache;
import thaumcraft.common.research.theorycraft.TCResearchTableData;
import thaumcraft.common.research.theorycraft.TCResearchTableSyncPayload;
import thaumcraft.common.research.theorycraft.TCTheorycraftAid;
import thaumcraft.common.research.theorycraft.TCTheorycraftCard;
import thaumcraft.common.research.theorycraft.TCTheorycraftManager;
import thaumcraft.common.tiles.crafting.TCResearchTableBlockEntity;

public class TCResearchTableScreen extends AbstractContainerScreen<TCResearchTableMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_research_table.png");
    private static final ResourceLocation BASE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_base.png");
    private static final ResourceLocation PAPER =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/paper.png");
    private static final ResourceLocation PAPER_GILDED =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/papergilded.png");
    private static final ResourceLocation UNKNOWN =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/aspects/_unknown.png");
    private static final int AID_RECHECK_TICKS = 100;
    private static final int BASE_INSPIRATION_PREVIEW = 5;
    private static final int CARD_SHEET_SIZE = 96;
    private static final int CARD_HIT_WIDTH = 100;
    private static final int CARD_HIT_HEIGHT = 120;
    private static final int CARD_SPACING = 74;
    private static final int MAX_VISIBLE_CARDS = 3;

    private static final int LEGACY_BUTTON_U = 37;
    private static final int LEGACY_BUTTON_V = 66;
    private static final int LEGACY_BUTTON_TEX_WIDTH = 51;
    private static final int LEGACY_BUTTON_TEX_HEIGHT = 13;
    private static final int LEGACY_BUTTON_HIT_WIDTH = 49;
    private static final int LEGACY_BUTTON_HIT_HEIGHT = 11;

    private boolean createVisible;
    private boolean createActive;
    private boolean completeVisible;
    private boolean completeActive;
    private boolean scrapVisible;
    private boolean scrapActive;
    private Button drawButton;
    private List<String> currentAids = List.of();
    private final LinkedHashSet<String> selectedAids = new LinkedHashSet<>();
    private final LinkedHashMap<String, Integer> displayedCategoryTotals = new LinkedHashMap<>();
    private int nextAidCheckTick;
    private Component lastActionMessage = Component.empty();
    private int lastActionMessageTicks;
    private final float[] cardHover = new float[MAX_VISIBLE_CARDS];
    private final float[] cardZoomOut = new float[MAX_VISIBLE_CARDS];
    private final float[] cardZoomIn = new float[MAX_VISIBLE_CARDS];
    private long cardChoiceSignature;
    private int animatingSelectedCard = -1;
    private boolean selectionCommitSent;

    public TCResearchTableScreen(TCResearchTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 255;
        imageHeight = 255;
        titleLabelX = 0;
        titleLabelY = -1000;
        inventoryLabelX = 0;
        inventoryLabelY = -1000;
    }

    @Override
    protected void init() {
        super.init();
        drawButton = addRenderableWidget(Button.builder(
                        Component.literal("?"),
                        button -> sendAction(TCResearchTableActionPayload.ACTION_DRAW_CARDS, -1)
                )
                .bounds(leftPos + 52, topPos + 88, 28, 20)
                .build());
        updateButtons();
    }

    @Override
    protected void containerTick() {
        applyLatestSync();
        refreshCurrentAids();
        updateDisplayedCategoryTotals();
        if (lastActionMessageTicks > 0) {
            lastActionMessageTicks--;
        }
        updateButtons();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        applyLatestSync();
        refreshCurrentAids();
        updateButtons();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        renderCustomTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        blitGui(guiGraphics, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        renderInspirationIcons(guiGraphics);
        renderLegacyActionButtons(guiGraphics, mouseX, mouseY);
        renderAidSelection(guiGraphics, mouseX, mouseY);
        updateCardAnimations(mouseX, mouseY, partialTick);
        renderTheorySheets(guiGraphics, mouseX, mouseY);
        renderCategoryProgressPanel(guiGraphics);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        TCResearchTableData data = currentData();
        if (lastActionMessageTicks > 0) {
            guiGraphics.drawString(font, lastActionMessage, 76, 176, 0x5A3A08, false);
        }
        if (data == null) {
            return;
        }

        if (data.lastDraw != null) {
            guiGraphics.drawString(font, data.lastDraw.card.getLocalizedName(), 151, 154, 0x3F2A12, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && clickLegacyActionButton((int) mouseX, (int) mouseY)) {
            return true;
        }
        if (button == 0 && currentData() == null && clickAid((int) mouseX, (int) mouseY)) {
            return true;
        }
        if (button == 0) {
            int cardIndex = hoveredCardIndex((int) mouseX, (int) mouseY);
            if (cardIndex >= 0) {
                TCResearchTableData data = currentData();
                boolean cardAlreadySelected = data != null && data.cardChoices.stream().anyMatch(choice -> choice.selected);
                if (!cardAlreadySelected && animatingSelectedCard < 0) {
                    startCardSelectionAnimation(cardIndex);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void sendStartTheory() {
        PacketDistributor.sendToServer(new TCResearchTableActionPayload(
                TCResearchTableActionPayload.ACTION_START_THEORY,
                -1,
                new ArrayList<>(selectedAids)
        ));
    }

    private void sendAction(int actionId, int choiceIndex) {
        PacketDistributor.sendToServer(new TCResearchTableActionPayload(actionId, choiceIndex));
    }

    private void applyLatestSync() {
        TCResearchTableBlockEntity table = menu.blockEntity();
        if (table == null) {
            return;
        }

        TCResearchTableActionResultPayload result = TCResearchTableClientCache.pollResult(menu.blockPos());
        if (result != null) {
            table.applyTheoryDataFromSync(result.toTableSyncPayload());
            lastActionMessage = Component.translatable("gui.thaumcraft.research_table.action." + result.resultKey());
            lastActionMessageTicks = result.accepted() ? 40 : 80;
        }

        TCResearchTableSyncPayload payload = TCResearchTableClientCache.get(menu.blockPos());
        if (payload != null) {
            table.applyTheoryDataFromSync(payload);
        }
    }

    private TCResearchTableData currentData() {
        TCResearchTableBlockEntity table = menu.blockEntity();
        return table == null ? null : table.getTheoryData();
    }

    private void refreshCurrentAids() {
        TCResearchTableBlockEntity table = menu.blockEntity();
        if (minecraft == null || minecraft.player == null || table == null || table.getLevel() == null || table.getTheoryData() != null) {
            currentAids = List.of();
            selectedAids.clear();
            return;
        }

        int tick = minecraft.player.tickCount;
        if (tick < nextAidCheckTick) {
            return;
        }
        nextAidCheckTick = tick + AID_RECHECK_TICKS;

        LinkedHashSet<String> keys = TCTheorycraftManager.collectNearbyAidKeys(table.getLevel(), table.getBlockPos());
        currentAids = new ArrayList<>(keys);
        selectedAids.removeIf(key -> !keys.contains(key));
    }

    private void renderInspirationIcons(GuiGraphics guiGraphics) {
        TCResearchTableData data = currentData();
        int start;
        int remaining;
        int y;
        if (data == null) {
            start = BASE_INSPIRATION_PREVIEW;
            remaining = Math.max(0, start - selectedAids.size());
            y = topPos + 55;
        } else {
            start = data.inspirationStart;
            remaining = data.inspiration;
            y = topPos + 16;
        }

        if (start <= 0) {
            return;
        }

        int x = leftPos + 128 - start * 5;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
        for (int index = 0; index < start; index++) {
            int u = remaining <= index ? 48 : 32;
            blitGui(guiGraphics, BASE, (x + index * 10) * 2, y * 2, u, 96, 16, 16, 256, 256);
        }
        guiGraphics.pose().popPose();
    }

    private void renderLegacyActionButtons(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderLegacyButton(
                guiGraphics,
                mouseX,
                mouseY,
                createX(),
                createY(),
                Component.translatable("button.create.theory"),
                createVisible,
                createActive,
                0x88FF8A
        );
        renderLegacyButton(
                guiGraphics,
                mouseX,
                mouseY,
                completeX(),
                completeY(),
                Component.translatable("button.complete.theory"),
                completeVisible,
                completeActive,
                0x88FF8A
        );
        renderLegacyButton(
                guiGraphics,
                mouseX,
                mouseY,
                scrapX(),
                scrapY(),
                Component.translatable("button.scrap.theory"),
                scrapVisible,
                scrapActive,
                0xFF2CA2
        );
    }

    private void renderLegacyButton(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            int x,
            int y,
            Component label,
            boolean visible,
            boolean active,
            int textColor
    ) {
        if (!visible) {
            return;
        }

        boolean hovered = isInside(mouseX, mouseY, x, y, LEGACY_BUTTON_HIT_WIDTH, LEGACY_BUTTON_HIT_HEIGHT);
        float brightness = active ? (hovered ? 1.0F : 0.85F) : 0.45F;
        guiGraphics.setColor(brightness, brightness, brightness, 1.0F);
        blitGui(guiGraphics, BASE, x, y, LEGACY_BUTTON_U, LEGACY_BUTTON_V, LEGACY_BUTTON_TEX_WIDTH, LEGACY_BUTTON_TEX_HEIGHT, 256, 256);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int color = active ? textColor : 0x606060;
        guiGraphics.drawCenteredString(font, label, x + LEGACY_BUTTON_HIT_WIDTH / 2, y + 2, color);
    }

    private boolean clickLegacyActionButton(int mouseX, int mouseY) {
        if (createVisible && createActive && isInside(mouseX, mouseY, createX(), createY(), LEGACY_BUTTON_HIT_WIDTH, LEGACY_BUTTON_HIT_HEIGHT)) {
            sendStartTheory();
            return true;
        }
        if (completeVisible && completeActive && isInside(mouseX, mouseY, completeX(), completeY(), LEGACY_BUTTON_HIT_WIDTH, LEGACY_BUTTON_HIT_HEIGHT)) {
            sendAction(TCResearchTableActionPayload.ACTION_COMPLETE_THEORY, -1);
            return true;
        }
        if (scrapVisible && scrapActive && isInside(mouseX, mouseY, scrapX(), scrapY(), LEGACY_BUTTON_HIT_WIDTH, LEGACY_BUTTON_HIT_HEIGHT)) {
            sendAction(TCResearchTableActionPayload.ACTION_SCRAP_THEORY, -1);
            return true;
        }
        return false;
    }

    private void renderAidSelection(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (currentData() != null || currentAids.isEmpty()) {
            return;
        }

        for (int index = 0; index < currentAids.size(); index++) {
            String aidKey = currentAids.get(index);
            TCTheorycraftAid aid = TCTheorycraftManager.aids().get(aidKey);
            if (aid == null) {
                continue;
            }

            int x = aidX(index);
            int y = aidY(index);
            boolean selected = selectedAids.contains(aidKey);
            boolean hovered = isInside(mouseX, mouseY, x, y, 16, 16);
            if (selected || hovered) {
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, selected ? 1.0F : 0.28F);
                blitGui(guiGraphics, BASE, x, y, 0, 96, 16, 16, 256, 256);
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
            guiGraphics.renderItem(aid.displayStack(), x, y);
        }
    }

    private void updateCardAnimations(int mouseX, int mouseY, float partialTick) {
        TCResearchTableData data = currentData();
        long signature = cardChoiceSignature(data);
        if (signature != cardChoiceSignature) {
            cardChoiceSignature = signature;
            resetCardAnimations();
        }
        if (data == null || data.cardChoices.isEmpty()) {
            return;
        }

        int count = choiceCount();
        int hovered = hoveredCardIndex(mouseX, mouseY);
        for (int index = 0; index < MAX_VISIBLE_CARDS; index++) {
            if (index >= count) {
                cardHover[index] = 0.0F;
                cardZoomOut[index] = 0.0F;
                cardZoomIn[index] = 0.0F;
                continue;
            }

            if (index == count - 1 || cardZoomOut[index + 1] > 0.6F) {
                cardZoomOut[index] = approach(cardZoomOut[index], 1.0F, Math.max((1.0F - cardZoomOut[index]) / 5.0F * partialTick, 0.004F));
            }

            if (animatingSelectedCard >= 0) {
                if (index == animatingSelectedCard) {
                    cardZoomIn[index] = approach(cardZoomIn[index], 1.0F, Math.max((1.0F - cardZoomIn[index]) / 3.0F * partialTick, 0.006F));
                    cardHover[index] = approach(cardHover[index], Math.max(0.0F, 1.0F - cardZoomIn[index]) * 0.25F, 0.08F * partialTick);
                    if (cardZoomIn[index] >= 0.995F && !selectionCommitSent) {
                        selectionCommitSent = true;
                        sendAction(TCResearchTableActionPayload.ACTION_SELECT_AND_COMMIT, animatingSelectedCard);
                    }
                } else {
                    cardZoomIn[index] = approach(cardZoomIn[index], 1.0F, 0.16F * partialTick);
                    cardHover[index] = approach(cardHover[index], 0.0F, 0.1F * partialTick);
                }
            } else if (hovered == index && cardZoomOut[index] >= 0.95F) {
                cardHover[index] = approach(cardHover[index], 0.25F, Math.max((0.25F - cardHover[index]) / 3.0F * partialTick, 0.004F));
            } else {
                cardHover[index] = approach(cardHover[index], 0.0F, 0.1F * partialTick);
            }

            cardHover[index] = clamp(cardHover[index], 0.0F, 0.25F);
            cardZoomOut[index] = clamp(cardZoomOut[index], 0.0F, 1.0F);
            cardZoomIn[index] = clamp(cardZoomIn[index], 0.0F, 1.0F);
        }
    }

    private void resetCardAnimations() {
        for (int index = 0; index < MAX_VISIBLE_CARDS; index++) {
            cardHover[index] = 0.0F;
            cardZoomOut[index] = 0.0F;
            cardZoomIn[index] = 0.0F;
        }
        animatingSelectedCard = -1;
        selectionCommitSent = false;
    }

    private long cardChoiceSignature(TCResearchTableData data) {
        if (data == null || data.cardChoices.isEmpty()) {
            return 0L;
        }

        long signature = 1125899906842597L;
        int count = Math.min(data.cardChoices.size(), MAX_VISIBLE_CARDS);
        for (int index = 0; index < count; index++) {
            TCResearchTableData.CardChoice choice = data.cardChoices.get(index);
            signature = 31L * signature + choice.card.getSeed();
            signature = 31L * signature + (choice.fromAid ? 1L : 0L);
        }
        return signature;
    }

    private void startCardSelectionAnimation(int cardIndex) {
        if (cardIndex < 0 || cardIndex >= choiceCount()) {
            return;
        }
        animatingSelectedCard = cardIndex;
        selectionCommitSent = false;
        for (int index = 0; index < MAX_VISIBLE_CARDS; index++) {
            cardZoomIn[index] = 0.0F;
        }
    }

    private void renderTheorySheets(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        TCResearchTableData data = currentData();
        if (data == null) {
            return;
        }

        if (!data.isComplete() && data.cardChoices.isEmpty()) {
            renderBlankDrawStack(guiGraphics);
        }
        if (!data.savedCards.isEmpty()) {
            renderSavedStack(guiGraphics, data);
        }
        if (data.lastDraw != null) {
            renderSmallSheet(guiGraphics, leftPos + 191, topPos + 100, data.lastDraw.fromAid, 0.72F, 1.0F, data.lastDraw.card.getSeed(), 0.15F);
        }
        for (int index = 0; index < data.cardChoices.size(); index++) {
            renderCardChoice(guiGraphics, data.cardChoices.get(index), index, mouseX, mouseY);
        }
    }

    private void renderBlankDrawStack(GuiGraphics guiGraphics) {
        for (int index = 2; index >= 0; index--) {
            renderSmallSheet(guiGraphics, leftPos + 65 + index * 2, topPos + 100 - index, false, 0.68F, 0.85F, 5521L + index * 173L, 0.65F);
        }
        blitGui(guiGraphics, UNKNOWN, leftPos + 57, topPos + 91, 0, 0, 16, 16, 16, 16);
    }

    private void renderSavedStack(GuiGraphics guiGraphics, TCResearchTableData data) {
        int count = Math.min(data.savedCards.size(), 6);
        for (int index = 0; index < count; index++) {
            long seed = data.savedCards.get(index);
            renderSmallSheet(guiGraphics, leftPos + 191 + index, topPos + 100 - index, false, 0.66F, 0.72F, seed, 0.45F);
        }
    }

    private void renderCardChoice(
            GuiGraphics guiGraphics,
            TCResearchTableData.CardChoice choice,
            int index,
            int mouseX,
            int mouseY
    ) {
        if (index >= MAX_VISIBLE_CARDS || cardZoomOut[index] <= 0.01F) {
            return;
        }

        int count = choiceCount();
        float targetX = cardCenterX(index, count);
        float startX = leftPos + 65.0F;
        float savedX = leftPos + 191.0F;
        float centerX = startX + (targetX - startX) * cardZoomOut[index];
        float centerY = topPos + 100.0F;
        float zoomIn = cardZoomIn[index];
        boolean selectedAnimating = animatingSelectedCard == index;
        if (selectedAnimating) {
            centerX += (savedX - centerX) * zoomIn;
        }

        boolean hovered = isInside(mouseX, mouseY, Math.round(centerX) - CARD_HIT_WIDTH / 2, Math.round(centerY) - 60, CARD_HIT_WIDTH, CARD_HIT_HEIGHT)
                && animatingSelectedCard < 0
                && cardZoomOut[index] >= 0.95F;
        boolean inactiveDuringSelection = animatingSelectedCard >= 0 && !selectedAnimating;
        float scale = 0.65F + cardZoomOut[index] * 0.35F - zoomIn * 0.28F + cardHover[index] * 0.28F;
        float alpha = inactiveDuringSelection ? Math.max(0.0F, 1.0F - cardZoomIn[index]) : 1.0F;
        if (alpha <= 0.02F) {
            return;
        }

        renderCardSheet(guiGraphics, centerX, centerY, choice, scale, alpha, hovered, selectedAnimating);
    }

    private void renderCardSheet(
            GuiGraphics guiGraphics,
            float centerX,
            float centerY,
            TCResearchTableData.CardChoice choice,
            float scale,
            float alpha,
            boolean hovered,
            boolean selected
    ) {
        Random random = new Random(choice.card.getSeed());
        float offsetX = (float) random.nextGaussian();
        float offsetY = (float) random.nextGaussian();
        float rotation = (float) (random.nextGaussian() * (selected ? 0.15D : 0.8D));

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX + offsetX, centerY + offsetY, 0.0F);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(rotation));
        guiGraphics.pose().scale(scale, scale, 1.0F);
        renderLocalPaper(guiGraphics, choice.fromAid, alpha);
        renderCardCategoryWatermark(guiGraphics, choice.card.getResearchCategory(), alpha);
        if (hovered && !selected) {
            guiGraphics.fill(-42, -46, 42, 48, 0x24FFFFFF);
        }
        renderCardContents(guiGraphics, choice, selected);
        guiGraphics.pose().popPose();
    }

    private void renderSmallSheet(
            GuiGraphics guiGraphics,
            int centerX,
            int centerY,
            boolean gilded,
            float scale,
            float alpha,
            long seed,
            float tilt
    ) {
        Random random = new Random(seed);
        float offsetX = (float) random.nextGaussian();
        float offsetY = (float) random.nextGaussian();
        float rotation = (float) (random.nextGaussian() * tilt);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX + offsetX, centerY + offsetY, 0.0F);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(rotation));
        guiGraphics.pose().scale(scale, scale, 1.0F);
        renderLocalPaper(guiGraphics, gilded, alpha);
        guiGraphics.pose().popPose();
    }

    private void renderLocalPaper(GuiGraphics guiGraphics, boolean gilded, float alpha) {
        float textureScale = CARD_SHEET_SIZE / 256.0F;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(textureScale, textureScale, 1.0F);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        blitGui(guiGraphics, gilded ? PAPER_GILDED : PAPER, -128, -128, 0, 0, 256, 256, 256, 256);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.pose().popPose();
    }

    private void renderCardCategoryWatermark(GuiGraphics guiGraphics, String category, float alpha) {
        if (category == null || category.isBlank()) {
            return;
        }

        Optional<TCResearchCategoryDefinition> definition = TCResearchManager.categories().stream()
                .filter(candidate -> candidate.key().equalsIgnoreCase(category.trim()))
                .findFirst();
        if (definition.isEmpty() || definition.get().icon() == null) {
            return;
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(48.0F / 255.0F, 48.0F / 255.0F, 1.0F);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, Math.min(0.18F, alpha / 5.5F));
        blitGui(guiGraphics, definition.get().icon(), -128, -128, 0, 0, 255, 255, 255, 255);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.pose().popPose();
    }

    private void renderCardContents(
            GuiGraphics guiGraphics,
            TCResearchTableData.CardChoice choice,
            boolean selected
    ) {
        int textColor = selected ? 0x7A5A2A : 0x2D1A08;
        int left = -42;
        int top = -42;
        int width = 84;
        drawCenteredTrimmed(guiGraphics, font, choice.card.getLocalizedName(), 0, top + 7, width, textColor);
        drawWrapped(guiGraphics, choice.card.getLocalizedText(), left + 6, top + 21, width - 12, 5, textColor);
        renderCardCost(guiGraphics, choice.card.getInspirationCost(), left + 5, 23);
        renderRequiredItems(guiGraphics, choice, 0, 31);
    }

    private void renderCardCost(GuiGraphics guiGraphics, int cost, int x, int y) {
        int count = Math.min(Math.abs(cost), 5);
        int sourceU = cost < 0 ? 48 : 32;
        int sourceV = cost < 0 ? 0 : 96;
        for (int index = 0; index < count; index++) {
            blitGui(guiGraphics, BASE, x + index * 9, y, sourceU, sourceV, 16, 16, 256, 256);
        }
    }

    private void renderRequiredItems(GuiGraphics guiGraphics, TCResearchTableData.CardChoice choice, int centerX, int y) {
        List<ItemStack> requiredItems = choice.card.getRequiredItems();
        if (requiredItems.isEmpty()) {
            return;
        }
        List<Boolean> consumed = choice.card.getRequiredItemsConsumed();
        int visible = Math.min(requiredItems.size(), 4);
        int startX = centerX - visible * 9;
        for (int index = 0; index < visible; index++) {
            ItemStack stack = requiredItems.get(index);
            if (stack.isEmpty()) {
                continue;
            }
            int x = startX + index * 18;
            guiGraphics.renderItem(stack, x, y);
            guiGraphics.renderItemDecorations(font, stack, x, y);
            if (index < consumed.size() && Boolean.TRUE.equals(consumed.get(index))) {
                blitGui(guiGraphics, BASE, x + 8, y + 8, 64, 120, 16, 16, 256, 256);
            }
        }
    }

    private void renderCategoryProgressPanel(GuiGraphics guiGraphics) {
        TCResearchTableData data = currentData();
        if (data == null || data.categoryTotals.isEmpty()) {
            return;
        }

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(data.categoryTotals.entrySet());
        sorted.sort((left, right) -> Integer.compare(right.getValue(), left.getValue()));
        int row = 0;
        for (Map.Entry<String, Integer> entry : sorted) {
            if (row >= 7) {
                break;
            }

            String category = entry.getKey();
            int shown = displayedCategoryTotals.getOrDefault(category, entry.getValue());
            int y = topPos + 16 + row * 18;
            renderCategoryIcon(guiGraphics, category, leftPos + 253, y);
            int color = data.categoriesBlocked.contains(category) ? 0x8F4A3A : 0x3F2A12;
            guiGraphics.drawString(font, shown + "%", leftPos + 274, y + 4, color, false);
            row++;
        }
    }

    private void renderCategoryIcon(GuiGraphics guiGraphics, String category, int x, int y) {
        Optional<TCResearchCategoryDefinition> definition = TCResearchManager.categories().stream()
                .filter(candidate -> candidate.key().equalsIgnoreCase(category.trim()))
                .findFirst();
        if (definition.isEmpty() || definition.get().icon() == null) {
            return;
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0.0F);
        guiGraphics.pose().scale(16.0F / 255.0F, 16.0F / 255.0F, 1.0F);
        blitGui(guiGraphics, definition.get().icon(), 0, 0, 0, 0, 255, 255, 255, 255);
        guiGraphics.pose().popPose();
    }

    private void updateDisplayedCategoryTotals() {
        TCResearchTableData data = currentData();
        if (data == null) {
            displayedCategoryTotals.clear();
            return;
        }

        displayedCategoryTotals.keySet().removeIf(category -> !data.categoryTotals.containsKey(category));
        for (Map.Entry<String, Integer> entry : data.categoryTotals.entrySet()) {
            int target = entry.getValue();
            int shown = displayedCategoryTotals.getOrDefault(entry.getKey(), target);
            if (shown < target) {
                shown++;
            } else if (shown > target) {
                shown--;
            }
            displayedCategoryTotals.put(entry.getKey(), shown);
        }
    }

    private void drawCenteredTrimmed(
            GuiGraphics guiGraphics,
            Font font,
            Component component,
            int centerX,
            int y,
            int maxWidth,
            int color
    ) {
        String text = component.getString();
        if (font.width(text) > maxWidth) {
            text = font.plainSubstrByWidth(text, maxWidth - font.width("...")) + "...";
        }
        guiGraphics.drawCenteredString(font, text, centerX, y, color);
    }

    private void drawWrapped(
            GuiGraphics guiGraphics,
            Component component,
            int x,
            int y,
            int width,
            int maxLines,
            int color
    ) {
        List<FormattedCharSequence> lines = font.split(component, width);
        int rendered = Math.min(lines.size(), maxLines);
        for (int index = 0; index < rendered; index++) {
            guiGraphics.drawString(font, lines.get(index), x, y + index * 9, color, false);
        }
    }

    private void renderCustomTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        List<Component> tooltip = customTooltip(mouseX, mouseY);
        if (!tooltip.isEmpty()) {
            guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    private List<Component> customTooltip(int mouseX, int mouseY) {
        int cardIndex = hoveredCardIndex(mouseX, mouseY);
        TCResearchTableData data = currentData();
        if (data != null && cardIndex >= 0 && cardIndex < data.cardChoices.size()) {
            TCTheorycraftCard card = data.cardChoices.get(cardIndex).card;
            ArrayList<Component> lines = new ArrayList<>();
            lines.add(card.getLocalizedName());
            lines.add(card.getLocalizedText());
            lines.add(Component.translatable("gui.thaumcraft.research_table.card.inspiration_cost", card.getInspirationCost()));
            List<ItemStack> requiredItems = card.getRequiredItems();
            if (!requiredItems.isEmpty()) {
                lines.add(Component.translatable("gui.thaumcraft.research_table.card.required_items"));
                for (ItemStack stack : requiredItems) {
                    if (!stack.isEmpty()) {
                        lines.add(Component.literal(stack.getCount() + "x ").append(stack.getHoverName()));
                    }
                }
            }
            return lines;
        }

        if (currentData() == null) {
            String aidKey = hoveredAidKey(mouseX, mouseY);
            if (aidKey != null) {
                TCTheorycraftAid aid = TCTheorycraftManager.aids().get(aidKey);
                if (aid != null) {
                    return List.of(
                            aid.displayStack().getHoverName(),
                            Component.translatable("gui.thaumcraft.research_table.aid_hint")
                    );
                }
            }
        }
        return List.of();
    }

    private boolean clickAid(int mouseX, int mouseY) {
        for (int index = 0; index < currentAids.size(); index++) {
            String aidKey = currentAids.get(index);
            if (!isInside(mouseX, mouseY, aidX(index), aidY(index), 16, 16)) {
                continue;
            }

            if (selectedAids.contains(aidKey)) {
                selectedAids.remove(aidKey);
            } else if (selectedAids.size() + 1 < 5) {
                selectedAids.add(aidKey);
            }
            return true;
        }
        return false;
    }

    private String hoveredAidKey(int mouseX, int mouseY) {
        for (int index = 0; index < currentAids.size(); index++) {
            String aidKey = currentAids.get(index);
            if (isInside(mouseX, mouseY, aidX(index), aidY(index), 16, 16)) {
                return aidKey;
            }
        }
        return null;
    }

    private int hoveredCardIndex(int mouseX, int mouseY) {
        TCResearchTableData data = currentData();
        if (data == null || data.cardChoices.isEmpty()) {
            return -1;
        }
        int count = choiceCount();
        for (int index = 0; index < data.cardChoices.size(); index++) {
            int centerX = cardCenterX(index, count);
            int centerY = topPos + 100;
            if (isInside(mouseX, mouseY, centerX - CARD_HIT_WIDTH / 2, centerY - 60, CARD_HIT_WIDTH, CARD_HIT_HEIGHT)) {
                return index;
            }
        }
        return -1;
    }

    private int cardCenterX(int index, int count) {
        return leftPos + imageWidth / 2 - CARD_SPACING * (count - 1) / 2 + CARD_SPACING * index;
    }

    private int choiceCount() {
        TCResearchTableData data = currentData();
        return data == null ? 0 : Math.min(data.cardChoices.size(), MAX_VISIBLE_CARDS);
    }

    private int aidX(int index) {
        int side = Math.min(currentAids.size(), 6);
        int column = index % side;
        return leftPos + 128 + 20 * column - side * 10;
    }

    private int aidY(int index) {
        int side = Math.min(currentAids.size(), 6);
        int row = index / side;
        return topPos + 85 + 35 * row;
    }

    private int createX() {
        return leftPos + 128;
    }

    private int createY() {
        return topPos + 22;
    }

    private int completeX() {
        return leftPos + 191;
    }

    private int completeY() {
        return topPos + 96;
    }

    private int scrapX() {
        return leftPos + 128;
    }

    private int scrapY() {
        return topPos + 168;
    }

    private static boolean isInside(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    private static float approach(float current, float target, float step) {
        if (current < target) {
            return Math.min(target, current + step);
        }
        if (current > target) {
            return Math.max(target, current - step);
        }
        return current;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private void blitGui(
            GuiGraphics guiGraphics,
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
        guiGraphics.blit(RenderType::guiTextured, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    private void updateButtons() {
        if (drawButton == null) {
            return;
        }

        TCResearchTableBlockEntity table = menu.blockEntity();
        TCResearchTableData data = table == null ? null : table.getTheoryData();
        boolean hasUsableTools = table != null && table.hasUsableScribingTools();
        boolean hasPaper = table != null && table.getPaperCount() > 0;

        createVisible = data == null;
        createActive = data == null && hasUsableTools && hasPaper;

        completeVisible = data != null && data.isComplete();
        completeActive = completeVisible;

        scrapVisible = data != null && !data.isComplete();
        scrapActive = scrapVisible;

        drawButton.visible = data != null && !data.isComplete() && data.cardChoices.isEmpty();
        drawButton.active = drawButton.visible && hasPaper;
    }
}

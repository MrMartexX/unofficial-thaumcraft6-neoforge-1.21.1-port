package thaumcraft.client.gui;








import java.util.Collections;
import java.util.Arrays;
import thaumcraft.client.lib.TCClientRenderTime;
import thaumcraft.client.gui.TCKnowledgeGainHud;
import thaumcraft.common.research.TCPlayerKnowledge;
import thaumcraft.common.lib.fx.TCLegacyFXData;
import thaumcraft.client.fx.legacy.TCLegacyParticleEngine;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.Thaumcraft;
import thaumcraft.common.menu.TCResearchTableMenu;
import thaumcraft.common.research.TCResearchCategoryDefinition;
import thaumcraft.common.research.TCResearchClientKnowledgeHelper;
import thaumcraft.common.research.TCResearchManager;
import thaumcraft.common.research.theorycraft.TCResearchTableActionPayload;
import thaumcraft.common.research.theorycraft.TCResearchTableActionResultPayload;
import thaumcraft.common.research.theorycraft.TCResearchTableClientCache;
import thaumcraft.common.research.theorycraft.TCResearchTableData;
import thaumcraft.common.research.theorycraft.TCResearchTableSyncPayload;
import thaumcraft.common.research.theorycraft.TCTheorycraftAid;
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
    private static final ResourceLocation PARTICLES =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/particles.png");
    private static final ResourceLocation THAUMONOMICON_ICON =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/items/thaumonomicon.png");
    private static final int AID_RECHECK_TICKS = 100;
    private static final int FALLBACK_BASE_INSPIRATION_PREVIEW = 5;
    private static final int CARD_SHEET_SIZE = 16;
    private static final int CARD_HIT_WIDTH = 100;
    private static final int CARD_HIT_HEIGHT = 120;
    private static final int CARD_TARGET_CENTER_X = 183;
    private static final int CARD_TARGET_HALF_SPACING = 55;
    private static final int CARD_TARGET_SPACING = 110;
    private static final int MAX_VISIBLE_CARDS = 3;

    private static final int DRAW_STACK_CENTER_X = 65;
    private static final int DRAW_STACK_CENTER_Y = 100;
    private static final int SAVED_STACK_CENTER_X = 191;
    private static final int SAVED_STACK_CENTER_Y = 100;
    private static final int DRAW_CLICK_X = 25;
    private static final int DRAW_CLICK_Y = 55;
    private static final int DRAW_CLICK_WIDTH = 75;
    private static final int DRAW_CLICK_HEIGHT = 90;

    private static final int LEGACY_BUTTON_U = 37;
    private static final int LEGACY_BUTTON_V = 66;
    private static final int LEGACY_BUTTON_TEX_WIDTH = 51;
    private static final int LEGACY_BUTTON_TEX_HEIGHT = 13;
    private static final int LEGACY_BUTTON_HIT_WIDTH = 49;
    private static final int LEGACY_BUTTON_HIT_HEIGHT = 11;
    private static final int LEGACY_CREATE_BUTTON_TINT = 8978346;
    private static final int LEGACY_SCRAP_BUTTON_TINT = 16720418;

    private static final SoundEvent SOUND_CLACK = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "clack"));
    private static final SoundEvent SOUND_PAGE = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "page"));
    private static final SoundEvent SOUND_PAGETURN = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "pageturn"));
    private static final SoundEvent SOUND_WRITE = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "write"));
    private static final SoundEvent SOUND_LEARN = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "learn"));

    private boolean createVisible;
    private boolean createActive;
    private boolean completeVisible;
    private boolean completeActive;
    private boolean scrapVisible;
    private boolean scrapActive;
    private List<String> currentAids = List.of();
    private final LinkedHashSet<String> selectedAids = new LinkedHashSet<>();
    private final LinkedHashMap<String, Integer> displayedCategoryTotals = new LinkedHashMap<>();
    private final LinkedHashSet<String> sparklingCategories = new LinkedHashSet<>();
    private final LinkedHashMap<String, Integer> categorySparkleTicks = new LinkedHashMap<>();
    private final List<KnowledgeGainParticle> knowledgeGainParticles = new ArrayList<>();
    private int nextAidCheckTick;
    private boolean categoryTotalsPrimed;
    private final float[] cardHover = new float[MAX_VISIBLE_CARDS];
    private final float[] cardZoomOut = new float[MAX_VISIBLE_CARDS];
    private final float[] cardZoomIn = new float[MAX_VISIBLE_CARDS];
    private long cardChoiceSignature;
    private int animatingSelectedCard = -1;
    private int pendingSelectionRequest = -1;
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
        updateButtons();
    }

    @Override
    protected void containerTick() {
        applyLatestSync();
        refreshCurrentAids();
        updateDisplayedCategoryTotals();
        TCLegacyParticleEngine.tickGui();
        updateButtons();
        tickKnowledgeGainParticles();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        TCClientRenderTime.updateScreenFallback(partialTick);
applyLatestSync();
        refreshCurrentAids();
        updateButtons();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (currentData() != null) {
            renderLegacyResearchWarnings(guiGraphics);
        }
        if (currentData() != null) {
            renderMissingSuppliesWarnings(guiGraphics);
        }
        if (!isHoveringAidIcon(mouseX, mouseY)) {
            super.renderTooltip(guiGraphics, mouseX, mouseY);
        }
        TCKnowledgeGainHud.renderOverScreen(guiGraphics, TCClientRenderTime.guiPartialTick());
        TCKnowledgeGainHud.renderOverScreen(guiGraphics, TCClientRenderTime.guiPartialTick());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        blitGui(guiGraphics, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        renderInspirationIcons(guiGraphics);
        renderAidSelection(guiGraphics, mouseX, mouseY);
        updateCardAnimations(mouseX, mouseY, partialTick);
        renderTheorySheets(guiGraphics, mouseX, mouseY);
        renderCategoryProgressPanel(guiGraphics);
        TCLegacyParticleEngine.renderGui(guiGraphics, 0.0F);
        renderLegacyActionButtons(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Legacy Research Table does not render transient action-status text over the table.
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!isHoveringAidIcon(mouseX, mouseY)) {
            super.renderTooltip(guiGraphics, mouseX, mouseY);
        }
        TCKnowledgeGainHud.renderOverScreen(guiGraphics, TCClientRenderTime.guiPartialTick());
        TCKnowledgeGainHud.renderOverScreen(guiGraphics, TCClientRenderTime.guiPartialTick());
        if (renderRequiredItemTooltip(guiGraphics, mouseX, mouseY)) {
            return;
        }
        if (renderCategoryTooltip(guiGraphics, mouseX, mouseY)) {
            return;
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
        if (button == 0 && clickDrawStack((int) mouseX, (int) mouseY)) {
            return true;
        }
        if (button == 0) {
            int cardIndex = hoveredCardIndex((int) mouseX, (int) mouseY);
            if (cardIndex >= 0) {
                TCResearchTableData data = currentData();
                boolean cardAlreadySelected = data != null && data.cardChoices.stream().anyMatch(choice -> choice.selected);
                if (!cardAlreadySelected && animatingSelectedCard < 0 && pendingSelectionRequest < 0) {
                    pendingSelectionRequest = cardIndex;
                    sendAction(TCResearchTableActionPayload.ACTION_SELECT_CARD, cardIndex);
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
            handleActionResult(result, table);
        }

        TCResearchTableSyncPayload payload = TCResearchTableClientCache.get(menu.blockPos());
        if (payload != null) {
            table.applyTheoryDataFromSync(payload);
        }
    }

    private void handleActionResult(TCResearchTableActionResultPayload result, TCResearchTableBlockEntity table) {
        if (!result.accepted()) {
            if (result.actionId() == TCResearchTableActionPayload.ACTION_SELECT_CARD) {
                pendingSelectionRequest = -1;
            }
            return;
        }

        switch (result.actionId()) {
            case TCResearchTableActionPayload.ACTION_START_THEORY -> {
                selectedAids.clear();
                categoryTotalsPrimed = false;
            }
            case TCResearchTableActionPayload.ACTION_DRAW_CARDS -> {
                resetCardAnimations();
                cardChoiceSignature = 0L;
            }
            case TCResearchTableActionPayload.ACTION_SELECT_CARD -> {
                pendingSelectionRequest = -1;
                startSelectedCardAnimationFromSyncedData(table.getTheoryData());
            }
            case TCResearchTableActionPayload.ACTION_COMPLETE_THEORY -> {
                playLearn();
                resetCardAnimations();
                displayedCategoryTotals.clear();
                sparklingCategories.clear();
                categoryTotalsPrimed = false;
            }
            case TCResearchTableActionPayload.ACTION_SCRAP_THEORY -> {
                resetCardAnimations();
                displayedCategoryTotals.clear();
                sparklingCategories.clear();
                categoryTotalsPrimed = false;
            }
            default -> {
            }
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
        trimSelectedAidsToPreviewLimit();
    }

    private void trimSelectedAidsToPreviewLimit() {
        int max = Math.max(0, previewInspiration() - 1);
        while (selectedAids.size() > max) {
            String last = null;
            for (String key : selectedAids) {
                last = key;
            }
            if (last == null) {
                return;
            }
            selectedAids.remove(last);
        }
    }

    private int previewInspiration() {
        int value = TCResearchClientKnowledgeHelper.availableTheoryInspiration();
        return value <= 0 ? FALLBACK_BASE_INSPIRATION_PREVIEW : value;
    }

    private void renderInspirationIcons(GuiGraphics guiGraphics) {
        TCResearchTableData data = currentData();
        int start;
        int remaining;
        int y;
        if (data == null) {
            start = previewInspiration();
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
        renderLegacyButton(guiGraphics, mouseX, mouseY, createX(), createY(), Component.translatable("button.create.theory"), createVisible, createActive, LEGACY_CREATE_BUTTON_TINT);
        renderLegacyButton(guiGraphics, mouseX, mouseY, completeX(), completeY(), Component.translatable("button.complete.theory"), completeVisible, completeActive, LEGACY_CREATE_BUTTON_TINT);
        renderLegacyButton(guiGraphics, mouseX, mouseY, scrapX(), scrapY(), Component.translatable("button.scrap.theory"), scrapVisible, scrapActive, LEGACY_SCRAP_BUTTON_TINT);
    }

    private void renderLegacyButton(GuiGraphics guiGraphics, int mouseX, int mouseY, int centerX, int centerY, Component label, boolean visible, boolean active, int tintColor) {
        if (!visible) {
            return;
        }

        boolean hovered = isInside(mouseX, mouseY, centerX - LEGACY_BUTTON_HIT_WIDTH / 2, centerY - LEGACY_BUTTON_HIT_HEIGHT / 2, LEGACY_BUTTON_HIT_WIDTH, LEGACY_BUTTON_HIT_HEIGHT);
        float brightness = active ? (hovered ? 1.0F : 0.9F) : 0.5F;
        float alpha = active ? 1.0F : 0.9F;
        float red = brightness * ((tintColor >> 16 & 255) / 255.0F);
        float green = brightness * ((tintColor >> 8 & 255) / 255.0F);
        float blue = brightness * ((tintColor & 255) / 255.0F);
        guiGraphics.setColor(red, green, blue, alpha);
        blitGui(guiGraphics, BASE, centerX - LEGACY_BUTTON_TEX_WIDTH / 2, centerY - LEGACY_BUTTON_TEX_HEIGHT / 2, LEGACY_BUTTON_U, LEGACY_BUTTON_V, LEGACY_BUTTON_TEX_WIDTH, LEGACY_BUTTON_TEX_HEIGHT, 256, 256);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        int textColor = !active ? 10526880 : (hovered ? 16777120 : 16777215);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX, centerY, 0.0F);
        guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
        guiGraphics.drawCenteredString(font, label, 0, -4, textColor);
        guiGraphics.pose().popPose();
    }

    private boolean clickLegacyActionButton(int mouseX, int mouseY) {
        if (createVisible && createActive && isInside(mouseX, mouseY, createX() - LEGACY_BUTTON_HIT_WIDTH / 2, createY() - LEGACY_BUTTON_HIT_HEIGHT / 2, LEGACY_BUTTON_HIT_WIDTH, LEGACY_BUTTON_HIT_HEIGHT)) {
            playClack();
            sendStartTheory();
            return true;
        }
        if (completeVisible && completeActive && isInside(mouseX, mouseY, completeX() - LEGACY_BUTTON_HIT_WIDTH / 2, completeY() - LEGACY_BUTTON_HIT_HEIGHT / 2, LEGACY_BUTTON_HIT_WIDTH, LEGACY_BUTTON_HIT_HEIGHT)) {
            playClack();
            startKnowledgeGainAnimation(currentData());
            sendAction(TCResearchTableActionPayload.ACTION_COMPLETE_THEORY, -1);
            return true;
        }
        if (scrapVisible && scrapActive && isInside(mouseX, mouseY, scrapX() - LEGACY_BUTTON_HIT_WIDTH / 2, scrapY() - LEGACY_BUTTON_HIT_HEIGHT / 2, LEGACY_BUTTON_HIT_WIDTH, LEGACY_BUTTON_HIT_HEIGHT)) {
            playClack();
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
                float previousZoomOut = cardZoomOut[index];
                cardZoomOut[index] = approach(cardZoomOut[index], 1.0F, Math.max((1.0F - cardZoomOut[index]) / 5.0F * partialTick, 0.0025F));
                if (previousZoomOut <= 0.0F && cardZoomOut[index] > 0.0F) {
                    playPage();
                }
            }

            if (animatingSelectedCard >= 0) {
                if (index == animatingSelectedCard) {
                    cardZoomIn[index] = approach(cardZoomIn[index], 1.0F, Math.max((1.0F - cardZoomIn[index]) / 3.0F * partialTick, 0.0025F));
                    cardHover[index] = approach(cardHover[index], Math.max(0.0F, 1.0F - cardZoomIn[index]), 0.1F * partialTick);
                    if (cardZoomIn[index] >= 0.995F && !selectionCommitSent) {
                        selectionCommitSent = true;
                        playWrite();
                        sendAction(TCResearchTableActionPayload.ACTION_COMMIT_SELECTED, -1);
                    }
                } else {
                    cardZoomIn[index] = approach(cardZoomIn[index], 1.0F, 0.3F * partialTick);
                    cardHover[index] = approach(cardHover[index], 0.0F, 0.1F * partialTick);
                }
            } else if (hovered == index && cardZoomOut[index] >= 0.95F) {
                cardHover[index] = approach(cardHover[index], 0.25F, Math.max((0.25F - cardHover[index]) / 3.0F * partialTick, 0.0025F));
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
        pendingSelectionRequest = -1;
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

    private void startSelectedCardAnimationFromSyncedData(TCResearchTableData data) {
        int selectedIndex = selectedCardIndex(data);
        if (selectedIndex >= 0 && animatingSelectedCard < 0) {
            startCardSelectionAnimation(selectedIndex);
        }
    }

    private int selectedCardIndex(TCResearchTableData data) {
        if (data == null) {
            return -1;
        }
        int count = Math.min(data.cardChoices.size(), MAX_VISIBLE_CARDS);
        for (int index = 0; index < count; index++) {
            if (data.cardChoices.get(index).selected) {
                return index;
            }
        }
        return -1;
    }
    private void startCardSelectionAnimation(int cardIndex) {
        if (cardIndex < 0 || cardIndex >= choiceCount()) {
            return;
        }
        playPageTurn();
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

        renderBonusDraws(guiGraphics, data);
        if (!data.isComplete()) {
            renderBlankDrawStack(guiGraphics, mouseX, mouseY);
        }
        if (!data.savedCards.isEmpty()) {
            renderSavedStack(guiGraphics, data);
        }
        if (data.lastDraw != null) {
            renderSmallSheet(guiGraphics, leftPos + SAVED_STACK_CENTER_X, topPos + SAVED_STACK_CENTER_Y, data.lastDraw.fromAid, 6.0F, 1.0F, data.lastDraw.card.getSeed(), 0.15F);
        }
        for (int index = 0; index < data.cardChoices.size(); index++) {
            renderCardChoice(guiGraphics, data.cardChoices.get(index), index, mouseX, mouseY);
        }
    }

    private void renderBonusDraws(GuiGraphics guiGraphics, TCResearchTableData data) {
        for (int index = 0; index < data.bonusDraws; index++) {
            blitGui(guiGraphics, BASE, leftPos + 15 + index * 2, topPos + 150 + index, 64, 96, 16, 16, 256, 256);
        }
    }

    private void renderBlankDrawStack(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        TCResearchTableBlockEntity table = menu.blockEntity();
        int paperCount = table == null ? 0 : table.getPaperCount();
        int sheets = 1 + Math.max(0, paperCount) / 4;
        Random random = new Random(55L);
        for (int index = 0; index < sheets; index++) {
            renderSmallSheet(guiGraphics, leftPos + DRAW_STACK_CENTER_X, topPos + DRAW_STACK_CENTER_Y, false, 6.0F, 1.0F, random.nextLong(), 1.0F);
        }
        if (sheets > 0) {
            boolean highlight = isInside(mouseX, mouseY, leftPos + DRAW_CLICK_X, topPos + DRAW_CLICK_Y, DRAW_CLICK_WIDTH, DRAW_CLICK_HEIGHT);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(leftPos + DRAW_STACK_CENTER_X, topPos + DRAW_STACK_CENTER_Y, 0.0F);
            guiGraphics.pose().scale(highlight ? 1.75F : 1.5F, highlight ? 1.75F : 1.5F, 1.0F);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, highlight ? 1.0F : 0.5F);
            blitGui(guiGraphics, UNKNOWN, -8, -8, 0, 0, 16, 16, 16, 16);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.pose().popPose();
        }
    }

    private void renderSavedStack(GuiGraphics guiGraphics, TCResearchTableData data) {
        for (Long seed : data.savedCards) {
            renderSmallSheet(guiGraphics, leftPos + SAVED_STACK_CENTER_X, topPos + SAVED_STACK_CENTER_Y, false, 6.0F, 1.0F, seed, 1.0F);
        }
    }

    private void renderCardChoice(GuiGraphics guiGraphics, TCResearchTableData.CardChoice choice, int index, int mouseX, int mouseY) {
        if (index >= MAX_VISIBLE_CARDS || cardZoomOut[index] <= 0.01F) {
            return;
        }

        int count = choiceCount();
        float targetX = cardCenterX(index, count);
        float startX = leftPos + DRAW_STACK_CENTER_X;
        float savedX = leftPos + SAVED_STACK_CENTER_X;
        float centerX = startX + (targetX - startX) * cardZoomOut[index];
        float centerY = topPos + DRAW_STACK_CENTER_Y;
        float zoomIn = cardZoomIn[index];
        boolean selectedAnimating = animatingSelectedCard == index;
        if (selectedAnimating) {
            centerX += (savedX - centerX) * zoomIn;
        }

        boolean hovered = isInside(mouseX, mouseY, Math.round(centerX) - CARD_HIT_WIDTH / 2, Math.round(centerY) - 60, CARD_HIT_WIDTH, CARD_HIT_HEIGHT)
                && animatingSelectedCard < 0
                && pendingSelectionRequest < 0
                && cardZoomOut[index] >= 0.95F;
        boolean inactiveDuringSelection = animatingSelectedCard >= 0 && !selectedAnimating;
        float scale = 6.0F + cardZoomOut[index] * 2.0F - zoomIn * 2.0F + cardHover[index];
        float alpha = inactiveDuringSelection ? Math.max(0.0F, 1.0F - cardZoomIn[index]) : 1.0F;
        float tilt = Math.max(1.0F - cardZoomOut[index], zoomIn);
        if (alpha <= 0.02F) {
            return;
        }

        renderCardSheet(guiGraphics, centerX, centerY, choice, scale, alpha, tilt, hovered, selectedAnimating);
    }

    private void renderCardSheet(GuiGraphics guiGraphics, float centerX, float centerY, TCResearchTableData.CardChoice choice, float scale, float alpha, float tilt, boolean hovered, boolean selected) {
        Random random = new Random(choice.card.getSeed());
        float offsetX = (float) random.nextGaussian();
        float offsetY = (float) random.nextGaussian();
        float rotation = (float) (random.nextGaussian() * tilt);
        boolean flipZ = random.nextBoolean();
        boolean flipY = false;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX + offsetX, centerY + offsetY, 0.0F);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(rotation));
        guiGraphics.pose().scale(scale, scale, 1.0F);
        renderLocalPaper(guiGraphics, choice.fromAid, alpha, flipZ, flipY);
        renderCardCategoryWatermark(guiGraphics, choice.card.getResearchCategory(), alpha);
        if (alpha >= 0.99F) {
            renderCardContents(guiGraphics, choice, selected);
        }
        guiGraphics.pose().popPose();
    }

    private void renderSmallSheet(GuiGraphics guiGraphics, int centerX, int centerY, boolean gilded, float scale, float alpha, long seed, float tilt) {
        Random random = new Random(seed);
        float offsetX = (float) random.nextGaussian();
        float offsetY = (float) random.nextGaussian();
        float rotation = (float) (random.nextGaussian() * tilt);
        boolean flipZ = random.nextBoolean();
        boolean flipY = false;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX + offsetX, centerY + offsetY, 0.0F);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(rotation));
        guiGraphics.pose().scale(scale, scale, 1.0F);
        renderLocalPaper(guiGraphics, gilded, alpha, flipZ, flipY);
        guiGraphics.pose().popPose();
    }

    private void renderLocalPaper(GuiGraphics guiGraphics, boolean gilded, float alpha, boolean flipZ, boolean flipY) {
        float textureScale = CARD_SHEET_SIZE / 256.0F;
        guiGraphics.pose().pushPose();
        if (flipZ) {
            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
        if (flipY) {
            guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(180.0F));
        }
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
        guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha / 6.0F);
        blitGui(guiGraphics, definition.get().icon(), -8, -8, 0, 0, 16, 16, 16, 16);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.pose().popPose();
    }

    private void renderCardContents(GuiGraphics guiGraphics, TCResearchTableData.CardChoice choice, boolean selected) {
        int textColor = 0x000000;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.0625F, 0.0625F, 1.0F);
        drawCenteredTrimmed(guiGraphics, font, choice.card.getLocalizedName().copy().withStyle(ChatFormatting.BOLD), 0, -65, 140, textColor);
        drawWrapped(guiGraphics, choice.card.getLocalizedText(), -70, -48, 140, 6, textColor);
        guiGraphics.pose().popPose();
        renderCardCost(guiGraphics, choice.card.getInspirationCost());
        renderRequiredItems(guiGraphics, choice);
    }

    private void renderCardCost(GuiGraphics guiGraphics, int cost) {
        int count = Math.min(Math.abs(cost), 5);
        boolean add = false;
        if (cost < 0) {
            add = true;
            count = Math.min(Math.abs(cost) + 1, 5);
        }
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.0625F, 0.0625F, 1.0F);
        for (int index = 0; index < count; index++) {
            int u = index == 0 && add ? 48 : 32;
            int v = index == 0 && add ? 0 : 96;
            blitGui(guiGraphics, BASE, -10 * count + 20 * index, -95, u, v, 16, 16, 256, 256);
        }
        guiGraphics.pose().popPose();
    }

    private void renderRequiredItems(GuiGraphics guiGraphics, TCResearchTableData.CardChoice choice) {
        List<ItemStack> requiredItems = choice.card.getRequiredItems();
        if (requiredItems.isEmpty()) {
            return;
        }
        List<Boolean> consumed = choice.card.getRequiredItemsConsumed();
        int visible = requiredItems.size();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.125F, 0.125F, 1.0F);
        for (int index = 0; index < visible; index++) {
            ItemStack stack = requiredItems.get(index);
            int x = -9 * visible + 18 * index;
            int y = 35;
            if (!stack.isEmpty()) {
                guiGraphics.renderItem(stack, x, y);
                guiGraphics.renderItemDecorations(font, stack, x, y);
                if (index < consumed.size() && Boolean.TRUE.equals(consumed.get(index))) {
                    float pulse = consumedMarkerPulse(index);
                    blitGui(guiGraphics, BASE, x - 2, Math.round(y + 10.0F + pulse * 10.0F), 64, 120, 16, 16, 256, 256);
                }
            } else {
                guiGraphics.setColor(0.75F, 0.75F, 0.75F, 1.0F);
                blitGui(guiGraphics, UNKNOWN, x, y, 0, 0, 16, 16, 16, 16);
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
        guiGraphics.pose().popPose();
    }

    private void renderLegacyResearchWarnings(GuiGraphics guiGraphics) {
        TCResearchTableBlockEntity table = menu.blockEntity();
        if (table == null || currentData() == null) {
            return;
        }

        int xx = leftPos;
        int yy = topPos;
        int qq = 0;

        if (!table.hasUsableScribingTools()) {
            Component line0 = Component.literal("You have run out of ink!");
            Component line1 = Component.literal("Refill your scribing tools.");
            int sx = Math.max(font.width(line0), font.width(line1)) / 2;
            drawLegacyResearchWarningTooltip(guiGraphics, Arrays.asList(line0, line1), xx - sx + 116, yy + 60 + qq);
            qq += 40;
        }

        if (table.getPaperCount() <= 0) {
            Component line0 = Component.literal("You have run out of paper.");
            int sx = font.width(line0) / 2;
            drawLegacyResearchWarningTooltip(guiGraphics, Collections.singletonList(line0), xx - sx + 116, yy + 60 + qq);
        }
    }

    private void drawLegacyResearchWarningTooltip(GuiGraphics guiGraphics, List<Component> lines, int x, int y) {
        if (lines == null || lines.isEmpty()) {
            return;
        }

        int widestLineWidth = 0;
        for (Component line : lines) {
            widestLineWidth = Math.max(widestLineWidth, font.width(line));
        }

        int totalHeight = -2;
        for (int index = 0; index < lines.size(); index++) {
            totalHeight += 10;
        }
        if (lines.size() > 1) {
            totalHeight += 2;
        }

        int sX = x + 12;
        int sY = y - 12;
        int background = 0xF0100010;
        int borderStart = 0x505000FF;
        int borderEnd = (borderStart & 0xFEFEFE) >> 1 | (borderStart & 0xFF000000);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 300.0F);

        guiGraphics.fillGradient(sX - 3, sY - 4, sX + widestLineWidth + 3, sY - 3, background, background);
        guiGraphics.fillGradient(sX - 3, sY + totalHeight + 3, sX + widestLineWidth + 3, sY + totalHeight + 4, background, background);
        guiGraphics.fillGradient(sX - 3, sY - 3, sX + widestLineWidth + 3, sY + totalHeight + 3, background, background);
        guiGraphics.fillGradient(sX - 4, sY - 3, sX - 3, sY + totalHeight + 3, background, background);
        guiGraphics.fillGradient(sX + widestLineWidth + 3, sY - 3, sX + widestLineWidth + 4, sY + totalHeight + 3, background, background);

        guiGraphics.fillGradient(sX - 3, sY - 2, sX - 2, sY + totalHeight + 2, borderStart, borderEnd);
        guiGraphics.fillGradient(sX + widestLineWidth + 2, sY - 2, sX + widestLineWidth + 3, sY + totalHeight + 2, borderStart, borderEnd);
        guiGraphics.fillGradient(sX - 3, sY - 3, sX + widestLineWidth + 3, sY - 2, borderStart, borderStart);
        guiGraphics.fillGradient(sX - 3, sY + totalHeight + 2, sX + widestLineWidth + 3, sY + totalHeight + 3, borderEnd, borderEnd);

        int lineY = sY;
        for (int index = 0; index < lines.size(); index++) {
            Component styled = lines.get(index).copy().withStyle(index == 0 ? ChatFormatting.AQUA : ChatFormatting.GRAY);
            guiGraphics.drawString(font, styled, sX, lineY, -1, true);
            lineY += index == 0 ? 12 : 10;
        }

        guiGraphics.pose().popPose();
    }
    private void renderCategoryProgressPanel(GuiGraphics guiGraphics) {
        TCResearchTableData data = currentData();
        if (data == null || data.categoryTotals.isEmpty()) {
            return;
        }

        List<Map.Entry<String, Integer>> sorted = sortedDisplayedCategories();
        int row = 0;
        for (Map.Entry<String, Integer> entry : sorted) {
            if (row >= 7) {
                break;
            }

            String category = entry.getKey();
            int shown = entry.getValue();
            int iconY = topPos + 16 + row * 18 + (row > 0 ? 4 : 0);
            int textY = topPos + 20 + row * 18 + (row > data.penaltyStart ? 4 : 0);
            renderCategoryIcon(guiGraphics, category, leftPos + 253, iconY);
            String text = shown + "%";
            if (row > data.penaltyStart) {
                text += " (-" + shown / 3 + ")";
            }
            int color = data.categoriesBlocked.contains(category) ? 0x606060 : (row <= data.penaltyStart ? 0x00E0C0 : 0xFFFFFF);
            guiGraphics.drawString(font, text, leftPos + 276, textY, color, false);
            if (sparklingCategories.contains(category) || categorySparkleTicks.getOrDefault(category, 0) > 0) {
                renderCategorySparkles(guiGraphics, category, leftPos + 276, textY, text);
            }
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
        guiGraphics.pose().scale(0.0625F, 0.0625F, 1.0F);
        blitGui(guiGraphics, definition.get().icon(), 0, 0, 0, 0, 255, 255, 255, 255);
        guiGraphics.pose().popPose();
    }

    private void renderCategorySparkles(GuiGraphics guiGraphics, String category, int x, int y, String text) {
        // Legacy GUI sparkles are spawned when temp category totals increment and rendered by
        // TCLegacyParticleEngine.renderGui(...). This overload only preserves the old call site.
    }
    private void renderCategorySparkles(GuiGraphics guiGraphics) {
        TCLegacyParticleEngine.renderGui(guiGraphics, 0.0F);
    }

    private void renderMissingSuppliesWarnings(GuiGraphics guiGraphics) {
        TCResearchTableBlockEntity table = menu.blockEntity();
        TCResearchTableData data = currentData();
        if (table == null || data == null || data.isComplete()) {
            return;
        }

        int yOffset = 0;
        if (!table.hasUsableScribingTools()) {
            drawLegacyWarningTooltip(
                    guiGraphics,
                    List.of(
                            Component.translatable("tile.researchtable.noink.0"),
                            Component.translatable("tile.researchtable.noink.1")
                    ),
                    yOffset
            );
            yOffset += 40;
        }
        if (table.getPaperCount() <= 0) {
            drawLegacyWarningTooltip(
                    guiGraphics,
                    List.of(Component.translatable("tile.researchtable.nopaper.0")),
                    yOffset
            );
        }
    }

    private void drawLegacyWarningTooltip(GuiGraphics guiGraphics, List<Component> lines, int yOffset) {
        if (lines == null || lines.isEmpty()) {
            return;
        }

        int widestLineWidth = 0;
        for (Component line : lines) {
            widestLineWidth = Math.max(widestLineWidth, font.width(line));
        }

        int totalHeight = -2 + lines.size() * 10;
        if (lines.size() > 1) {
            totalHeight += 2;
        }

        int textX = leftPos + 128 - widestLineWidth / 2;
        int textY = topPos + 48 + yOffset;
        drawLegacyTooltipBox(guiGraphics, textX, textY, widestLineWidth, totalHeight);

        int lineY = textY;
        for (int index = 0; index < lines.size(); index++) {
            guiGraphics.drawString(font, lines.get(index), textX, lineY, 0xFFFFFF, true);
            lineY += 10;
        }
    }

    private void drawLegacyTooltipBox(GuiGraphics guiGraphics, int textX, int textY, int width, int height) {
        int background = 0xF0100010;
        int borderStart = 0x505000FF;
        int borderEnd = ((borderStart & 0x00FEFEFE) >> 1) | (borderStart & 0xFF000000);

        guiGraphics.fillGradient(textX - 3, textY - 4, textX + width + 3, textY - 3, background, background);
        guiGraphics.fillGradient(textX - 3, textY + height + 3, textX + width + 3, textY + height + 4, background, background);
        guiGraphics.fillGradient(textX - 3, textY - 3, textX + width + 3, textY + height + 3, background, background);
        guiGraphics.fillGradient(textX - 4, textY - 3, textX - 3, textY + height + 3, background, background);
        guiGraphics.fillGradient(textX + width + 3, textY - 3, textX + width + 4, textY + height + 3, background, background);

        guiGraphics.fillGradient(textX - 3, textY - 2, textX - 2, textY + height + 2, borderStart, borderEnd);
        guiGraphics.fillGradient(textX + width + 2, textY - 2, textX + width + 3, textY + height + 2, borderStart, borderEnd);
        guiGraphics.fillGradient(textX - 3, textY - 3, textX + width + 3, textY - 2, borderStart, borderStart);
        guiGraphics.fillGradient(textX - 3, textY + height + 2, textX + width + 3, textY + height + 3, borderEnd, borderEnd);
    }
    private float consumedMarkerPulse(int index) {
        float tick = minecraft == null || minecraft.player == null ? 0.0F : minecraft.player.tickCount;
        return (float) Math.sin((tick + index * 2.0F) / 2.0F) * 0.03F;
    }
    private int cardCenterX(int index, int count) {
        return leftPos + CARD_TARGET_CENTER_X - CARD_TARGET_HALF_SPACING * count + CARD_TARGET_SPACING * index;
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

    private void blitGui(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    private void startKnowledgeGainAnimation(TCResearchTableData data) {
        if (data == null || data.categoryTotals.isEmpty()) {
            return;
        }

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(data.categoryTotals.entrySet());
        sorted.sort((left, right) -> right.getValue().compareTo(left.getValue()));

        int targetX = Math.max(16, width - 24);
        int targetY = Math.max(16, height - 24);
        Random random = minecraft == null || minecraft.player == null
                ? new Random(System.nanoTime())
                : new Random(minecraft.player.getRandom().nextLong());

        int row = 0;
        for (Map.Entry<String, Integer> entry : sorted) {
            int value = Math.max(0, entry.getValue());
            if (value <= 0) {
                row++;
                continue;
            }

            int sourceX = leftPos + 276 + Math.max(4, font.width(value + "%") / 2);
            int sourceY = topPos + 20 + row * 18 + (row > data.penaltyStart ? 4 : 0);
            int count = Math.max(2, Math.min(7, 1 + value / 25));
            for (int index = 0; index < count; index++) {
                float r = 1.0F;
                float g = (189 + random.nextInt(67)) / 255.0F;
                float b = (64 + random.nextInt(192)) / 255.0F;
                int frameStart = random.nextFloat() < 0.2F ? 320 : 512;
                knowledgeGainParticles.add(new KnowledgeGainParticle(
                        sourceX + random.nextGaussian() * 5.0D,
                        sourceY + random.nextGaussian() * 3.0D,
                        targetX + random.nextGaussian() * 4.0D,
                        targetY + random.nextGaussian() * 4.0D,
                        48 + random.nextInt(16),
                        row * 3 + index * 2,
                        frameStart,
                        24.0F + random.nextFloat() * 8.0F,
                        r,
                        g,
                        b
                ));
            }
            row++;
        }
    }

    private void tickKnowledgeGainParticles() {
        if (knowledgeGainParticles.isEmpty()) {
            return;
        }
        knowledgeGainParticles.removeIf(KnowledgeGainParticle::tick);
    }

    private void renderKnowledgeGainParticles(GuiGraphics guiGraphics, float partialTick) {
        if (knowledgeGainParticles.isEmpty()) {
            return;
        }
        renderKnowledgeTargetIcon(guiGraphics);
        for (KnowledgeGainParticle particle : knowledgeGainParticles) {
            if (particle.delay > 0) {
                continue;
            }
            particle.render(guiGraphics, partialTick);
        }
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderKnowledgeTargetIcon(GuiGraphics guiGraphics) {
        int x = Math.max(0, width - 40);
        int y = Math.max(0, height - 40);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 230.0F);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 0.95F);
        blitGui(guiGraphics, THAUMONOMICON_ICON, x, y, 0, 0, 32, 32, 32, 32);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.pose().popPose();
    }

    private void renderLegacyParticleSprite(GuiGraphics guiGraphics, double centerX, double centerY, int frame, float legacyScale, float red, float green, float blue, float alpha) {
        int sprite = Math.floorMod(frame, 16 * 16);
        int u = (sprite % 16) * 64;
        int v = (sprite / 16) * 64;

        // FXGenericGui draws a quad from -0.1*scale to +0.1*scale, so visible size is 0.2*scale.
        float screenSize = Math.max(1.0F, legacyScale * 0.2F);
        float drawScale = screenSize / 64.0F;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX, centerY, 240.0F);
        guiGraphics.pose().scale(drawScale, drawScale, 1.0F);
        guiGraphics.setColor(red, green, blue, alpha);
        blitGui(guiGraphics, PARTICLES, -32, -32, u, v, 64, 64, 1024, 1024);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.pose().popPose();
    }

    private final class KnowledgeGainParticle {
        private final double startX;
        private final double startY;
        private final double targetX;
        private final double targetY;
        private final int maxAge;
        private int delay;
        private final int frameStart;
        private final float size;
        private final float red;
        private final float green;
        private final float blue;
        private int age;
        private final double wobbleSeed;

        private KnowledgeGainParticle(double startX, double startY, double targetX, double targetY, int maxAge, int delay, int frameStart, float size, float red, float green, float blue) {
            this.startX = startX;
            this.startY = startY;
            this.targetX = targetX;
            this.targetY = targetY;
            this.maxAge = Math.max(1, maxAge);
            this.delay = Math.max(0, delay);
            this.frameStart = frameStart;
            this.size = size;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.wobbleSeed = startX * 0.17D + startY * 0.31D + targetX * 0.07D;
        }

        private boolean tick() {
            if (delay > 0) {
                delay--;
                return false;
            }
            age++;
            return age > maxAge;
        }

        private void render(GuiGraphics guiGraphics, float partialTick) {
            float t = Math.min(1.0F, (age + partialTick) / (float) maxAge);
            float ease = t * t * (3.0F - 2.0F * t);
            double wobble = Math.sin((age + partialTick) * 0.22D + wobbleSeed) * 5.0D * (1.0D - t);
            double x = startX + (targetX - startX) * ease + wobble;
            double y = startY + (targetY - startY) * ease - Math.sin(t * Math.PI) * 14.0D;
            float alpha = t < 0.90F ? 1.0F : Math.max(0.0F, 1.0F - (t - 0.90F) / 0.10F);
            int frame = frameStart + Math.floorMod(age / 2, 16);
            renderLegacyParticleSprite(guiGraphics, x, y, frame, size, red, green, blue, alpha);
        }
    }
    private List<Map.Entry<String, Integer>> sortedDisplayedCategories() {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(displayedCategoryTotals.entrySet());
        sorted.removeIf(entry -> entry.getValue() == 0);
        sorted.sort((left, right) -> Integer.compare(right.getValue(), left.getValue()));
        if (sorted.size() > 7) {
            return new ArrayList<>(sorted.subList(0, 7));
        }
        return sorted;
    }

    private boolean seedDisplayedCategoryTotals(TCResearchTableData data) {
        if (data == null || !displayedCategoryTotals.isEmpty() || data.categoryTotals.isEmpty()) {
            return false;
        }

        displayedCategoryTotals.putAll(data.categoryTotals);
        return true;
    }
    private void updateDisplayedCategoryTotals() {
        TCResearchTableData data = currentData();

        
        if (seedDisplayedCategoryTotals(data)) {
            return;
        }
sparklingCategories.clear();
        if (categorySparkleTicks != null) {
            categorySparkleTicks.replaceAll((key, value) -> value - 1);
            categorySparkleTicks.entrySet().removeIf(entry -> entry.getValue() <= 0);
        }

        if (data == null) {
            displayedCategoryTotals.clear();
            if (categorySparkleTicks != null) {
                categorySparkleTicks.clear();
            }
            return;
        }

        displayedCategoryTotals.keySet().removeIf(category -> !data.categoryTotals.containsKey(category));
        for (Map.Entry<String, Integer> entry : data.categoryTotals.entrySet()) {
            int target = entry.getValue();
            int shown = displayedCategoryTotals.getOrDefault(entry.getKey(), 0);
            if (shown < target) {
                shown++;
                spawnLegacyCategorySparkles(entry.getKey());
                sparklingCategories.add(entry.getKey());
                if (categorySparkleTicks != null) {
                    categorySparkleTicks.put(entry.getKey(), 36);
                }
            } else if (shown > target) {
                shown--;
            }
            displayedCategoryTotals.put(entry.getKey(), shown);
        }
    }

    private boolean clickDrawStack(int mouseX, int mouseY) {
        TCResearchTableBlockEntity table = menu.blockEntity();
        TCResearchTableData data = currentData();
        if (table == null || data == null || data.isComplete() || !data.cardChoices.isEmpty()) {
            return false;
        }
        if (!isInside(mouseX, mouseY, leftPos + DRAW_CLICK_X, topPos + DRAW_CLICK_Y, DRAW_CLICK_WIDTH, DRAW_CLICK_HEIGHT)) {
            return false;
        }
        playPage();
        sendAction(TCResearchTableActionPayload.ACTION_DRAW_CARDS, data.bonusDraws > 0 ? 3 : 2);
        return true;
    }

    private void drawCenteredTrimmed(GuiGraphics guiGraphics, Font font, Component component, int centerX, int y, int maxWidth, int color) {
        Component rendered = component;
        if (font.width(rendered) > maxWidth) {
            String text = rendered.getString();
            String trimmed = font.plainSubstrByWidth(text, Math.max(1, maxWidth - font.width("..."))) + "...";
            rendered = Component.literal(trimmed).withStyle(ChatFormatting.BOLD);
        }
        guiGraphics.drawString(font, rendered, centerX - font.width(rendered) / 2, y, color, false);
    }

    private void drawWrapped(GuiGraphics guiGraphics, Component component, int x, int y, int width, int maxLines, int color) {
        List<FormattedCharSequence> lines = font.split(component, width);
        for (int index = 0; index < lines.size(); index++) {
            guiGraphics.drawString(font, lines.get(index), x, y + index * 9, color, false);
        }
    }

    private boolean isHoveringAidIcon(int mouseX, int mouseY) {
        if (currentData() != null || currentAids.isEmpty()) {
            return false;
        }

        for (int index = 0; index < currentAids.size(); index++) {
            if (isInside(mouseX, mouseY, aidX(index), aidY(index), 16, 16)) {
                return true;
            }
        }

        return false;
    }
    private boolean clickAid(int mouseX, int mouseY) {
        for (int index = 0; index < currentAids.size(); index++) {
            String aidKey = currentAids.get(index);
            if (!isInside(mouseX, mouseY, aidX(index), aidY(index), 16, 16)) {
                continue;
            }

            if (selectedAids.contains(aidKey)) {
                selectedAids.remove(aidKey);
            } else {
                int limit = TCResearchClientKnowledgeHelper.availableTheoryInspiration() - 1;
                if (selectedAids.size() < Math.max(0, limit)) {
                    selectedAids.add(aidKey);
                }
            }
            return true;
        }
        return false;
    }

    private int hoveredCardIndex(int mouseX, int mouseY) {
        TCResearchTableData data = currentData();
        if (data == null || data.cardChoices.isEmpty() || animatingSelectedCard >= 0) {
            return -1;
        }
        int count = choiceCount();
        for (int index = 0; index < Math.min(data.cardChoices.size(), MAX_VISIBLE_CARDS); index++) {
            int centerX = cardCenterX(index, count);
            int centerY = topPos + DRAW_STACK_CENTER_Y;
            if (cardZoomOut[index] >= 0.95F && isInside(mouseX, mouseY, centerX - CARD_HIT_WIDTH / 2, centerY - 60, CARD_HIT_WIDTH, CARD_HIT_HEIGHT)) {
                return index;
            }
        }
        return -1;
    }

    private boolean renderRequiredItemTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        TCResearchTableData data = currentData();
        if (data == null || data.cardChoices.isEmpty()) {
            return false;
        }

        int count = choiceCount();
        for (int cardIndex = 0; cardIndex < Math.min(data.cardChoices.size(), MAX_VISIBLE_CARDS); cardIndex++) {
            if (cardZoomOut[cardIndex] < 0.95F || animatingSelectedCard >= 0) {
                continue;
            }

            TCResearchTableData.CardChoice choice = data.cardChoices.get(cardIndex);
            List<ItemStack> required = choice.card.getRequiredItems();
            if (required.isEmpty()) {
                continue;
            }

            int visible = required.size();
            float scale = 6.0F + cardZoomOut[cardIndex] * 2.0F - cardZoomIn[cardIndex] * 2.0F + cardHover[cardIndex];
            int cardCenterX = cardCenterX(cardIndex, count);
            int cardCenterY = topPos + DRAW_STACK_CENTER_Y;
            for (int itemIndex = 0; itemIndex < visible; itemIndex++) {
                int localX = -9 * visible + 18 * itemIndex;
                int localY = 35;
                int x = Math.round(cardCenterX + localX * scale * 0.125F);
                int y = Math.round(cardCenterY + localY * scale * 0.125F);
                int size = Math.max(10, Math.round(16 * scale * 0.125F));
                if (!isInside(mouseX, mouseY, x, y, size, size)) {
                    continue;
                }

                ItemStack stack = required.get(itemIndex);
                if (stack.isEmpty()) {
                    guiGraphics.renderComponentTooltip(font, List.of(Component.translatable("tc.card.unknown")), mouseX, mouseY);
                } else {
                    guiGraphics.renderTooltip(font, stack, mouseX, mouseY);
                }
                return true;
            }
        }
        return false;
    }

    private boolean renderCategoryTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        TCResearchTableData data = currentData();
        if (data == null) {
            return false;
        }

        List<Map.Entry<String, Integer>> sorted = sortedDisplayedCategories();
        for (int row = 0; row < sorted.size(); row++) {
            int iconX = leftPos + 253;
            int iconY = topPos + 16 + row * 18 + (row > 0 ? 4 : 0);
            if (!isInside(mouseX, mouseY, iconX, iconY, 16, 16)) {
                continue;
            }
            String category = sorted.get(row).getKey();
            guiGraphics.renderComponentTooltip(font, List.of(Component.translatable("tc.research_category." + category.toLowerCase())), mouseX + 8, mouseY + 8);
            return true;
        }
        return false;
    }

    private void renderAidTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (currentData() != null || currentAids.isEmpty()) {
            return;
        }

        for (int index = 0; index < currentAids.size(); index++) {
            if (!isInside(mouseX, mouseY, aidX(index), aidY(index), 16, 16)) {
                continue;
            }
            String aidKey = currentAids.get(index);
            TCTheorycraftAid aid = TCTheorycraftManager.aids().get(aidKey);
            if (aid != null && !aid.displayStack().isEmpty()) {
                guiGraphics.renderTooltip(font, aid.displayStack(), mouseX, mouseY);
            } else {
                guiGraphics.renderComponentTooltip(font, List.of(Component.literal(aidKey)), mouseX, mouseY);
            }
            return;
        }
    }
    private void spawnLegacyCategorySparkles(String category) {
        if (minecraft == null || minecraft.player == null || minecraft.level == null || font == null) {
            return;
        }

        TCResearchTableData data = currentData();
        if (data == null) {
            return;
        }

        String normalized = TCPlayerKnowledge.normalizeCategory(category);
        java.util.List<java.util.Map.Entry<String, Integer>> sorted = sortedDisplayedCategories();
        for (int row = 0; row < sorted.size(); row++) {
            java.util.Map.Entry<String, Integer> entry = sorted.get(row);
            if (!entry.getKey().equals(normalized)) {
                continue;
            }

            String text = entry.getValue() + "%";
            if (row > data.penaltyStart) {
                text += " (-" + entry.getValue() / 3 + ")";
            }

            java.util.Random random = new java.util.Random(minecraft.player.getRandom().nextLong());
            for (int index = 0; index < 2; index++) {
                float x = leftPos + 276 + random.nextFloat() * Math.max(1, font.width(text));
                float y = topPos + 20 + random.nextFloat() * 8.0F + row * 18 + (row > data.penaltyStart ? 4 : 0);

                float red = 1.0F;
                float green = (189 + random.nextInt(67)) / 255.0F;
                float blue = (64 + random.nextInt(192)) / 255.0F;
                int startParticle = random.nextFloat() < 0.2F ? 320 : 512;

                TCLegacyFXData sparkle = new TCLegacyFXData(
                        32 + random.nextInt(8),
                        startParticle,
                        16,
                        1,
                        64,
                        true,
                        4,
                        red,
                        green,
                        blue,
                        red,
                        green,
                        blue,
                        new float[]{0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F},
                        new float[]{24.0F, 48.0F},
                        0.9D,
                        -1.0F,
                        0.025D,
                        0.025D,
                        0.0D,
                        0.0D,
                        0.0D,
                        0.0F,
                        true
                );

                TCLegacyParticleEngine.addGuiEffect(
                        minecraft.level,
                        sparkle,
                        x,
                        y,
                        0.0D,
                        random.nextGaussian() * 0.5D,
                        random.nextGaussian() * 0.5D,
                        0.0D,
                        0
                );
            }
            return;
        }
    }

    private void playClack() {
        playUiSound(SOUND_CLACK, 1.0F);
    }

    private void playPage() {
        playUiSound(SOUND_PAGE, 1.0F);
    }

    private void playPageTurn() {
        playUiSound(SOUND_PAGETURN, 1.0F);
    }

    private void playWrite() {
        playUiSound(SOUND_WRITE, 1.0F);
    }

    private void playLearn() {
        playUiSound(SOUND_LEARN, 1.0F);
    }

    private void playUiSound(SoundEvent sound, float pitch) {
        if (minecraft != null) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch));
        }
    }

    private void updateButtons() {
        TCResearchTableBlockEntity table = menu.blockEntity();
        TCResearchTableData data = table == null ? null : table.getTheoryData();
        boolean hasUsableTools = table != null && table.hasUsableScribingTools();
        createVisible = data == null;
        createActive = data == null && hasUsableTools;

        completeVisible = data != null && data.isComplete();
        completeActive = completeVisible;

        scrapVisible = data != null && !data.isComplete();
        scrapActive = scrapVisible;
    }
}


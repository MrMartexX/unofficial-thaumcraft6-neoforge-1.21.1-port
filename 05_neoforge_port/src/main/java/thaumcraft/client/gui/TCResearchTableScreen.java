package thaumcraft.client.gui;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.Thaumcraft;
import thaumcraft.common.menu.TCResearchTableMenu;
import thaumcraft.common.research.theorycraft.TCResearchTableActionPayload;
import thaumcraft.common.research.theorycraft.TCResearchTableActionResultPayload;
import thaumcraft.common.research.theorycraft.TCResearchTableClientCache;
import thaumcraft.common.research.theorycraft.TCResearchTableData;
import thaumcraft.common.research.theorycraft.TCResearchTableSyncPayload;
import thaumcraft.common.research.theorycraft.TCTheorycraftCard;
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
    private static final int AID_RECHECK_TICKS = 100;
    private static final int CARD_SHEET_SIZE = 92;
    private static final int CARD_HIT_WIDTH = 100;
    private static final int CARD_HIT_HEIGHT = 120;
    private static final int CARD_SPACING = 74;

    private Button createButton;
    private Button completeButton;
    private Button scrapButton;
    private Button drawButton;
    private List<String> currentAids = List.of();
    private final LinkedHashSet<String> selectedAids = new LinkedHashSet<>();
    private int nextAidCheckTick;
    private Component lastActionMessage = Component.empty();
    private int lastActionMessageTicks;

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
        createButton = addRenderableWidget(Button.builder(
                        Component.translatable("button.create.theory"),
                        button -> sendStartTheory()
                )
                .bounds(leftPos + 126, topPos + 20, 72, 16)
                .build());
        completeButton = addRenderableWidget(Button.builder(
                        Component.translatable("button.complete.theory"),
                        button -> sendAction(TCResearchTableActionPayload.ACTION_COMPLETE_THEORY, -1)
                )
                .bounds(leftPos + 186, topPos + 94, 74, 16)
                .build());
        scrapButton = addRenderableWidget(Button.builder(
                        Component.translatable("button.scrap.theory"),
                        button -> sendAction(TCResearchTableActionPayload.ACTION_SCRAP_THEORY, -1)
                )
                .bounds(leftPos + 126, topPos + 166, 72, 16)
                .build());
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
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        renderAidSelection(guiGraphics, mouseX, mouseY);
        renderTheorySheets(guiGraphics, mouseX, mouseY);
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

        guiGraphics.drawString(font, data.inspiration + "/" + data.inspirationStart, 117, 16, 0x3F2A12, false);
        int row = 0;
        for (String category : data.categoryTotals.keySet()) {
            String value = category + " " + data.categoryTotals.get(category) + "%";
            guiGraphics.drawString(font, value, 176, 18 + row * 10, 0x3F2A12, false);
            row++;
            if (row >= 7) {
                break;
            }
        }

        if (data.lastDraw != null) {
            guiGraphics.drawString(font, data.lastDraw.card.getLocalizedName(), 151, 154, 0x3F2A12, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && currentData() == null && clickAid((int) mouseX, (int) mouseY)) {
            return true;
        }
        if (button == 0) {
            int cardIndex = hoveredCardIndex((int) mouseX, (int) mouseY);
            if (cardIndex >= 0) {
                TCResearchTableData data = currentData();
                boolean cardAlreadySelected = data != null && data.cardChoices.stream().anyMatch(choice -> choice.selected);
                if (!cardAlreadySelected) {
                    sendAction(TCResearchTableActionPayload.ACTION_SELECT_AND_COMMIT, cardIndex);
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
            if (selectedAids.contains(aidKey)) {
                guiGraphics.fill(x - 1, y - 1, x + 17, y + 17, 0x805A3A08);
            } else if (isInside(mouseX, mouseY, x, y, 16, 16)) {
                guiGraphics.fill(x - 1, y - 1, x + 17, y + 17, 0x40FFFFFF);
            }
            guiGraphics.renderItem(aid.displayStack(), x, y);
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
            renderSavedStack(guiGraphics, Math.min(data.savedCards.size(), 6));
        }
        if (data.lastDraw != null) {
            renderSmallSheet(guiGraphics, leftPos + 191, topPos + 100, data.lastDraw.fromAid, 0.72F, 1.0F);
        }
        for (int index = 0; index < data.cardChoices.size(); index++) {
            renderCardChoice(guiGraphics, data.cardChoices.get(index), index, mouseX, mouseY);
        }
    }

    private void renderBlankDrawStack(GuiGraphics guiGraphics) {
        for (int index = 2; index >= 0; index--) {
            renderSmallSheet(guiGraphics, leftPos + 65 + index * 2, topPos + 100 - index, false, 0.68F, 0.85F);
        }
        guiGraphics.blit(UNKNOWN, leftPos + 57, topPos + 91, 0, 0, 16, 16, 16, 16);
    }

    private void renderSavedStack(GuiGraphics guiGraphics, int count) {
        for (int index = 0; index < count; index++) {
            renderSmallSheet(guiGraphics, leftPos + 191 + index, topPos + 100 - index, false, 0.66F, 0.72F);
        }
    }

    private void renderCardChoice(
            GuiGraphics guiGraphics,
            TCResearchTableData.CardChoice choice,
            int index,
            int mouseX,
            int mouseY
    ) {
        int centerX = cardCenterX(index, choiceCount());
        int centerY = topPos + 100;
        boolean hovered = isInside(mouseX, mouseY, centerX - CARD_HIT_WIDTH / 2, centerY - 60, CARD_HIT_WIDTH, CARD_HIT_HEIGHT);
        boolean selected = choice.selected;
        float scale = hovered && !selected ? 1.06F : 1.0F;
        float alpha = selected ? 0.72F : 1.0F;

        renderSmallSheet(guiGraphics, centerX, centerY, choice.fromAid, scale, alpha);
        if (hovered && !selected) {
            guiGraphics.fill(centerX - 42, centerY - 46, centerX + 42, centerY + 48, 0x24FFFFFF);
        }
        renderCardContents(guiGraphics, choice, centerX, centerY, selected);
    }

    private void renderSmallSheet(GuiGraphics guiGraphics, int centerX, int centerY, boolean gilded, float scale, float alpha) {
        float drawSize = CARD_SHEET_SIZE * scale;
        float textureScale = drawSize / 256.0F;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX - drawSize / 2.0F, centerY - drawSize / 2.0F, 0.0F);
        guiGraphics.pose().scale(textureScale, textureScale, 1.0F);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        guiGraphics.blit(gilded ? PAPER_GILDED : PAPER, 0, 0, 0, 0, 256, 256, 256, 256);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.pose().popPose();
    }

    private void renderCardContents(
            GuiGraphics guiGraphics,
            TCResearchTableData.CardChoice choice,
            int centerX,
            int centerY,
            boolean selected
    ) {
        int textColor = selected ? 0x7A5A2A : 0x2D1A08;
        int left = centerX - 42;
        int top = centerY - 42;
        int width = 84;
        drawCenteredTrimmed(guiGraphics, font, choice.card.getLocalizedName(), centerX, top + 7, width, textColor);
        drawWrapped(guiGraphics, choice.card.getLocalizedText(), left + 6, top + 21, width - 12, 5, textColor);
        renderCardCost(guiGraphics, choice.card.getInspirationCost(), left + 5, centerY + 23);
        renderRequiredItems(guiGraphics, choice, centerX, centerY + 31);
    }

    private void renderCardCost(GuiGraphics guiGraphics, int cost, int x, int y) {
        int count = Math.min(Math.abs(cost), 5);
        int sourceU = cost < 0 ? 48 : 32;
        int sourceV = cost < 0 ? 0 : 96;
        for (int index = 0; index < count; index++) {
            guiGraphics.blit(BASE, x + index * 9, y, sourceU, sourceV, 16, 16, 256, 256);
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
                guiGraphics.blit(BASE, x + 8, y + 8, 64, 120, 16, 16, 256, 256);
            }
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
        return data == null ? 0 : Math.min(data.cardChoices.size(), 3);
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

    private static boolean isInside(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    private void updateButtons() {
        if (createButton == null) {
            return;
        }

        TCResearchTableBlockEntity table = menu.blockEntity();
        TCResearchTableData data = table == null ? null : table.getTheoryData();
        boolean hasUsableTools = table != null && table.hasUsableScribingTools();
        boolean hasPaper = table != null && table.getPaperCount() > 0;

        createButton.visible = data == null;
        createButton.active = data == null && hasUsableTools && hasPaper;

        completeButton.visible = data != null && data.isComplete();
        completeButton.active = completeButton.visible;

        scrapButton.visible = data != null && !data.isComplete();
        scrapButton.active = scrapButton.visible;

        drawButton.visible = data != null && !data.isComplete() && data.cardChoices.isEmpty();
        drawButton.active = drawButton.visible && hasPaper;
    }
}

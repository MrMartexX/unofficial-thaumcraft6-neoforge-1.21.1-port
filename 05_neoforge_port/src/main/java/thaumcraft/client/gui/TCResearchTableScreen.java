package thaumcraft.client.gui;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.Thaumcraft;
import thaumcraft.common.menu.TCResearchTableMenu;
import thaumcraft.common.research.theorycraft.TCResearchTableActionPayload;
import thaumcraft.common.research.theorycraft.TCResearchTableClientCache;
import thaumcraft.common.research.theorycraft.TCResearchTableData;
import thaumcraft.common.research.theorycraft.TCResearchTableSyncPayload;
import thaumcraft.common.research.theorycraft.TCTheorycraftAid;
import thaumcraft.common.research.theorycraft.TCTheorycraftManager;
import thaumcraft.common.tiles.crafting.TCResearchTableBlockEntity;

public class TCResearchTableScreen extends AbstractContainerScreen<TCResearchTableMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_research_table.png");
    private static final ResourceLocation GUI_BASE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_base.png");
    private static final int AID_RECHECK_TICKS = 100;
    private static final int BASE_INSPIRATION_PREVIEW = 5;

    private static final int BUTTON_U = 37;
    private static final int BUTTON_V = 66;
    private static final int BUTTON_TEX_WIDTH = 51;
    private static final int BUTTON_TEX_HEIGHT = 13;
    private static final int BUTTON_HIT_WIDTH = 49;
    private static final int BUTTON_HIT_HEIGHT = 11;

    private boolean createVisible;
    private boolean createActive;
    private boolean completeVisible;
    private boolean completeActive;
    private boolean scrapVisible;
    private boolean scrapActive;
    private Button drawButton;
    private final Button[] cardButtons = new Button[3];
    private List<String> currentAids = List.of();
    private final LinkedHashSet<String> selectedAids = new LinkedHashSet<>();
    private int nextAidCheckTick;

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
        for (int index = 0; index < cardButtons.length; index++) {
            final int choice = index;
            cardButtons[index] = addRenderableWidget(Button.builder(
                            Component.empty(),
                            button -> {
                                sendAction(TCResearchTableActionPayload.ACTION_SELECT_CARD, choice);
                                sendAction(TCResearchTableActionPayload.ACTION_COMMIT_SELECTED, -1);
                            }
                    )
                    .bounds(leftPos + 16 + index * 72, topPos + 54, 68, 18)
                    .build());
        }
        updateButtons();
    }

    @Override
    protected void containerTick() {
        applyLatestSync();
        refreshCurrentAids();
        updateButtons();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        applyLatestSync();
        refreshCurrentAids();
        updateButtons();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        renderInspirationIcons(guiGraphics);
        renderLegacyActionButtons(guiGraphics, mouseX, mouseY);
        renderAidSelection(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        TCResearchTableData data = currentData();
        if (data == null) {
            return;
        }

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
            guiGraphics.drawString(font, data.lastDraw.card.getLocalizedName(), 150, 132, 0x3F2A12, false);
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
            guiGraphics.blit(GUI_BASE, (x + index * 10) * 2, y * 2, u, 96, 16, 16, 256, 256);
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

        boolean hovered = isInside(mouseX, mouseY, x, y, BUTTON_HIT_WIDTH, BUTTON_HIT_HEIGHT);
        float brightness = active ? (hovered ? 1.0F : 0.85F) : 0.45F;
        guiGraphics.setColor(brightness, brightness, brightness, 1.0F);
        guiGraphics.blit(GUI_BASE, x, y, BUTTON_U, BUTTON_V, BUTTON_TEX_WIDTH, BUTTON_TEX_HEIGHT, 256, 256);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int color = active ? textColor : 0x606060;
        guiGraphics.drawCenteredString(font, label, x + BUTTON_HIT_WIDTH / 2, y + 2, color);
    }

    private boolean clickLegacyActionButton(int mouseX, int mouseY) {
        if (createVisible && createActive && isInside(mouseX, mouseY, createX(), createY(), BUTTON_HIT_WIDTH, BUTTON_HIT_HEIGHT)) {
            sendStartTheory();
            return true;
        }
        if (completeVisible && completeActive && isInside(mouseX, mouseY, completeX(), completeY(), BUTTON_HIT_WIDTH, BUTTON_HIT_HEIGHT)) {
            sendAction(TCResearchTableActionPayload.ACTION_COMPLETE_THEORY, -1);
            return true;
        }
        if (scrapVisible && scrapActive && isInside(mouseX, mouseY, scrapX(), scrapY(), BUTTON_HIT_WIDTH, BUTTON_HIT_HEIGHT)) {
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
                guiGraphics.blit(GUI_BASE, x, y, 0, 96, 16, 16, 256, 256);
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
            guiGraphics.renderItem(aid.displayStack(), x, y);
        }
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

        for (int index = 0; index < cardButtons.length; index++) {
            boolean visible = data != null && index < data.cardChoices.size();
            cardButtons[index].visible = visible;
            cardButtons[index].active = visible && hasUsableTools && data.cardChoices.stream().noneMatch(choice -> choice.selected);
            if (visible) {
                cardButtons[index].setMessage(data.cardChoices.get(index).card.getLocalizedName());
            }
        }
    }
}

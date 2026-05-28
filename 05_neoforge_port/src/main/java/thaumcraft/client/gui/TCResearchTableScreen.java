package thaumcraft.client.gui;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.Thaumcraft;
import thaumcraft.common.menu.TCResearchTableMenu;
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
    private static final int AID_RECHECK_TICKS = 100;

    private Button createButton;
    private Button completeButton;
    private Button scrapButton;
    private Button drawButton;
    private final Button[] cardButtons = new Button[3];
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
        for (int index = 0; index < cardButtons.length; index++) {
            final int choice = index;
            cardButtons[index] = addRenderableWidget(Button.builder(
                            Component.empty(),
                            button -> {
                                sendAction(TCResearchTableActionPayload.ACTION_SELECT_AND_COMMIT, choice);
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
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        renderAidSelection(guiGraphics, mouseX, mouseY);
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
            guiGraphics.drawString(font, data.lastDraw.card.getLocalizedName(), 150, 132, 0x3F2A12, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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

package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.Thaumcraft;
import thaumcraft.common.golems.TCLogisticsPayloads;
import thaumcraft.common.menu.TCLogisticsMenu;
import thaumcraft.common.registry.TCSounds;

/**
 * 1:1 legacy GuiLogistics:
 * Grid of items offered by Provide seals within 32 blocks.
 * Supports scrolling, searching, adjusting order quantity, and requesting delivery.
 */
public class TCLogisticsScreen extends AbstractContainerScreen<TCLogisticsMenu> {
    private static final ResourceLocation TEX =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_logistics.png");
    private static final ResourceLocation BASE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_base.png");

    private int selectedSlot = -1;
    private ItemStack selectedStack = ItemStack.EMPTY;
    private int stackSize = 1;
    private long lastUpdate = 0L;
    private int lastScrollPos = 0;

    private Slider scrollbar;
    private Slider countbar;
    private EditBox searchField;

    public TCLogisticsScreen(TCLogisticsMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 215;
        this.imageHeight = 215;
        this.titleLabelX = 19;
        this.titleLabelY = 6;
        this.inventoryLabelX = -1000; // No player inventory labels
    }

    static class Slider {
        final int x, y, width, height;
        float min, max, sliderPosition;
        final boolean vertical;
        boolean isMouseDown;
        boolean visible = true;

        Slider(int x, int y, int w, int h, float min, float max, float defaultValue, boolean vertical) {
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
            this.min = min;
            this.max = max;
            this.sliderPosition = max > min ? (defaultValue - min) / (max - min) : 0.0F;
            this.vertical = vertical;
        }

        float getSliderValue() {
            return min + (max - min) * sliderPosition;
        }

        void setSliderValue(float value) {
            sliderPosition = max > min ? Math.max(0.0F, Math.min(1.0F, (value - min) / (max - min))) : 0.0F;
        }

        private void moveTo(double mx, double my) {
            sliderPosition = vertical
                    ? (float) (my - (y + 4)) / (height - 8)
                    : (float) (mx - (x + 4)) / (width - 8);
            sliderPosition = Math.max(0.0F, Math.min(1.0F, sliderPosition));
        }

        boolean over(double mx, double my) {
            return mx >= x && my >= y && mx < x + width && my < y + height;
        }

        void render(GuiGraphics g, int mx, int my) {
            if (!visible) return;
            g.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            g.pose().pushPose();
            if (vertical) {
                g.pose().translate(x + 2, y, 0.0F);
                g.pose().scale(1.0F, height / 32.0F, 1.0F);
                g.blit(BASE, 0, 0, 240, 176, 4, 32);
            } else {
                g.pose().translate(x, y + 2, 0.0F);
                g.pose().scale(width / 32.0F, 1.0F, 1.0F);
                g.blit(BASE, 0, 0, 208, 176, 32, 4);
            }
            g.pose().popPose();

            if (isMouseDown) {
                moveTo(mx, my);
            }

            if (vertical) {
                g.blit(BASE, x, y + (int) (sliderPosition * (height - 8)), 20, 20, 8, 8);
            } else {
                g.blit(BASE, x + (int) (sliderPosition * (width - 8)), y, 20, 20, 8, 8);
            }
        }

        boolean mousePressed(double mx, double my) {
            if (visible && over(mx, my)) {
                moveTo(mx, my);
                return isMouseDown = true;
            }
            return false;
        }
    }

    @Override
    protected void init() {
        super.init();
        scrollbar = new Slider(leftPos + 196, topPos + 28, 8, 149, 0.0F, (float) menu.getEnd(), (float) menu.getStart(), true);
        countbar = new Slider(leftPos + 24, topPos + 196, 32, 8, 1.0F, 64.0F, 1.0F, false);

        searchField = new EditBox(font, leftPos + 143, topPos + 196, 55, font.lineHeight + 2, Component.empty());
        searchField.setMaxLength(16);
        searchField.setBordered(true);
        searchField.setTextColor(0xFFFFFF);
        searchField.setCanLoseFocus(true);
        searchField.setValue("");
        searchField.setResponder(text -> PacketDistributor.sendToServer(new TCLogisticsPayloads.Search(text)));
        addRenderableWidget(searchField);
    }

    private boolean hasSelection() {
        return selectedSlot >= 0 && !selectedStack.isEmpty();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Periodic 1-second auto refresh
        long now = System.currentTimeMillis();
        if (now > lastUpdate) {
            lastUpdate = now + 1000L;
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 22);
            }
        }

        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Base background (215 x 215)
        graphics.blit(TEX, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Highlight selected slot
        if (hasSelection() && selectedSlot >= 0 && selectedSlot < 81) {
            graphics.blit(TEX, leftPos + 17 + (selectedSlot % 9) * 19, topPos + 17 + (selectedSlot / 9) * 19, 222, 46, 20, 20);
        }

        // Scroll buttons at (195, 16) and (195, 180)
        drawScrollButton(graphics, leftPos + 195, topPos + 16, true, mouseX, mouseY);
        drawScrollButton(graphics, leftPos + 195, topPos + 180, false, mouseX, mouseY);

        // Vertical scrollbar
        scrollbar.max = (float) menu.getEnd();
        scrollbar.render(graphics, mouseX, mouseY);

        // Quantity controls and request button (only when item is selected)
        countbar.visible = hasSelection();
        if (hasSelection()) {
            countbar.max = Math.min(64.0F, (float) selectedStack.getCount());
            countbar.render(graphics, mouseX, mouseY);

            // Plus / Minus buttons
            drawPlusMinus(graphics, leftPos + 13, topPos + 195, true, mouseX, mouseY);
            drawPlusMinus(graphics, leftPos + 57, topPos + 195, false, mouseX, mouseY);

            // Quantity number display
            graphics.drawString(font, String.valueOf(stackSize), leftPos + 72, topPos + 196, 0xFFFFFF);

            // Request button at (116, 200)
            int bx = leftPos + 116;
            int by = topPos + 200;
            boolean over = isOverRequest(mouseX, mouseY);
            float tint = over ? 1.0F : 0.85F;
            graphics.setColor(tint, tint, tint, 1.0F);
            graphics.blit(BASE, bx - 20, by - 6, 37, 82, 40, 13);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

            graphics.pose().pushPose();
            graphics.pose().translate(bx, by, 0.0F);
            graphics.pose().scale(0.5F, 0.5F, 1.0F);
            Component label = Component.translatable("tc.logistics.request");
            int textWidth = font.width(label);
            graphics.drawString(font, label, -textWidth / 2, -4, over ? 0xFFFF80 : 0xFFFFFF);
            graphics.pose().popPose();
        }

        RenderSystem.disableBlend();
    }

    private void drawScrollButton(GuiGraphics g, int x, int y, boolean up, int mx, int my) {
        boolean over = mx >= x && my >= y && mx < x + 10 && my < y + 10;
        int u = up ? 208 : 218;
        int v = over ? 180 : 170;
        g.blit(BASE, x, y, u, v, 10, 10);
    }

    private void drawPlusMinus(GuiGraphics g, int x, int y, boolean minus, int mx, int my) {
        boolean over = mx >= x && my >= y && mx < x + 10 && my < y + 10;
        int u = minus ? 228 : 238;
        int v = over ? 180 : 170;
        g.blit(BASE, x, y, u, v, 10, 10);
    }

    private boolean isOverRequest(int mx, int my) {
        return mx >= leftPos + 96 && mx <= leftPos + 136 && my >= topPos + 194 && my <= topPos + 207;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // Scroll up
            if (mouseX >= leftPos + 195 && mouseX < leftPos + 205 && mouseY >= topPos + 16 && mouseY < topPos + 26) {
                if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1);
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
            // Scroll down
            if (mouseX >= leftPos + 195 && mouseX < leftPos + 205 && mouseY >= topPos + 180 && mouseY < topPos + 190) {
                if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
            // Minus button
            if (hasSelection() && mouseX >= leftPos + 13 && mouseX < leftPos + 23 && mouseY >= topPos + 195 && mouseY < topPos + 205) {
                stackSize = Math.max(1, stackSize - 1);
                countbar.setSliderValue(stackSize);
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
            // Plus button
            if (hasSelection() && mouseX >= leftPos + 57 && mouseX < leftPos + 67 && mouseY >= topPos + 195 && mouseY < topPos + 205) {
                stackSize = Math.min((int) countbar.max, stackSize + 1);
                countbar.setSliderValue(stackSize);
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
            // Request button
            if (hasSelection() && isOverRequest((int) mouseX, (int) mouseY)) {
                PacketDistributor.sendToServer(new TCLogisticsPayloads.Request(
                        menu.getTargetPos(),
                        menu.getTargetSide(),
                        selectedStack,
                        stackSize
                ));
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(TCSounds.CLACK.get(), 1.0F));
                return true;
            }
            // Scrollbar drag start
            if (scrollbar.mousePressed(mouseX, mouseY)) {
                return true;
            }
            // Countbar drag start
            if (countbar.mousePressed(mouseX, mouseY)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (scrollbar.isMouseDown) {
            scrollbar.isMouseDown = false;
            int sv = Math.round(scrollbar.getSliderValue());
            if (sv != lastScrollPos) {
                lastScrollPos = sv;
                if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 100 + sv);
                }
            }
        }
        if (countbar.isMouseDown) {
            countbar.isMouseDown = false;
            stackSize = Math.round(countbar.getSliderValue());
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, net.minecraft.world.inventory.ClickType type) {
        if (slot != null && slot.hasItem()) {
            selectedSlot = slot.index;
            selectedStack = slot.getItem().copy();
            stackSize = 1;
            countbar.setSliderValue(1.0F);
            if (minecraft != null) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }
    }
}

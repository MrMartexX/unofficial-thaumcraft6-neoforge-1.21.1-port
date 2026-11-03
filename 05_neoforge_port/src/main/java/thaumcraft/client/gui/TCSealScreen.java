package thaumcraft.client.gui;

import java.util.function.IntSupplier;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.DyeColor;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.seals.*;
import thaumcraft.common.menu.TCSealMenu;

public final class TCSealScreen extends AbstractContainerScreen<TCSealMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
    private int shownCategory = -1;

    public TCSealScreen(TCSealMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 232;
    }

    @Override
    protected void init() { super.init(); rebuild(); }

    private void action(int id) {
        if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
    }

    private void icon(int id, int x, int y, int size, IntSupplier u, int v, String key) {
        addRenderableWidget(new AbstractButton(leftPos + x, topPos + y, size, size, Component.empty()) {
            public void onPress() {
                int command = id;
                if (id == 25) command = menu.seal.isLocked() ? 26 : 25;
                else if (id == 27) command = menu.seal.isRedstoneSensitive() ? 28 : 27;
                else if (id == 20 && menu.seal.getSeal() instanceof ISealConfigFilter filter) command = filter.isBlacklist() ? 21 : 20;
                else if (id >= 30 && id < 60 && menu.seal.getSeal() instanceof ISealConfigToggles toggles
                        && id - 30 < toggles.getToggles().length && toggles.getToggles()[id - 30].getValue()) command = id + 30;
                action(command);
            }
            protected void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) { defaultButtonNarrationText(output); }
            protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
                RenderSystem.enableBlend();
                if (id < menu.categories.length) {
                    float shade = menu.categories[id] == shownCategory ? 1 : isHovered() ? .9F : .7F;
                    graphics.setColor(shade, shade, shade, shade);
                } else {
                    float shade = isHovered() ? 1 : .9F;
                    graphics.setColor(shade, shade, shade, shade);
                }
                graphics.blit(TEXTURE, getX(), getY(), u.getAsInt(), v, width, height);
                graphics.setColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
                if (isHovered() && (id < menu.categories.length || id == 27 || id == 25 || id == 20)) {
                    String labelKey = key;
                    if (id == 27) labelKey = menu.seal.isRedstoneSensitive() ? "golem.prop.redon" : "golem.prop.redoff";
                    if (id == 25) labelKey = menu.seal.isLocked() ? "golem.prop.lock" : "golem.prop.unlock";
                    if (id == 20 && menu.seal.getSeal() instanceof ISealConfigFilter filter) labelKey = filter.isBlacklist() ? "button.bl" : "button.wl";
                    Component text = Component.translatable(labelKey);
                    graphics.pose().pushPose();
                    graphics.pose().translate(0, 0, 300);
                    int x = id == 25 || id == 20 ? getX() + 8 - font.width(text) / 2 : getX() - 2 - font.width(text);
                    graphics.drawString(font, text, x, getY() + (id == 25 || id == 20 ? 17 : 4), 0xFFFFFF, true);
                    graphics.pose().popPose();
                }
            }
        });
    }

    private void rebuild() {
        clearWidgets();
        shownCategory = menu.category();
        float slice = 60.0F / menu.categories.length;
        float start = -180.0F + (menu.categories.length - 1) * slice / 2;
        slice = Math.max(12, Math.min(24, slice));
        for (int i = 0; i < menu.categories.length; i++) {
            int cat = menu.categories[i];
            double angle = Math.toRadians(start - i * slice);
            icon(i, 80 + (int)(Math.cos(angle) * 86), 64 + (int)(Math.sin(angle) * 86), 16,
                    () -> cat * 16, 120, "button.category." + cat);
        }
        double redAngle = Math.toRadians(start - menu.categories.length * slice);
        icon(27, 80 + (int)(Math.cos(redAngle) * 86), 64 + (int)(Math.sin(redAngle) * 86), 16,
                () -> menu.seal.isRedstoneSensitive() ? 64 : 80, 136, "golem.prop.redon");
        if (shownCategory == 0) {
            step(80, 69, 55); step(81, 97, 55);
            step(82, 94, 76); step(83, 117, 76);
            icon(25, 56, 72, 16, () -> menu.seal.isLocked() ? 32 : 48, 136, "golem.prop.lock");
        } else if (shownCategory == 1 && menu.seal.getSeal() instanceof ISealConfigFilter filter) {
            int s = filter.getFilterSize(), sy = 16 + (s - 1) / 3 * 12;
            icon(20, 80, 72 + (s - 1) / 3 * 24 - sy + 27, 16, () -> filter.isBlacklist() ? 0 : 16, 136, "button.bl");
        } else if (shownCategory == 2) {
            step(90, 69, 47); step(91, 97, 47);
            step(92, 69, 72); step(93, 97, 72);
            step(94, 69, 97); step(95, 97, 97);
        } else if (shownCategory == 3 && menu.seal.getSeal() instanceof ISealConfigToggles toggles) {
            int n = toggles.getToggles().length;
            int spacing = n < 4 ? 8 : n < 6 ? 7 : n < 9 ? 6 : 5;
            int halfWidth = 12;
            for (var toggle : toggles.getToggles()) halfWidth = Math.max(halfWidth, (12 + Math.min(100, font.width(Component.translatable(toggle.getName())))) / 2);
            for (int i = 0; i < n; i++) {
                int index = i;
                icon(30 + i, 86 - halfWidth, 65 - (n - 1) * spacing + i * spacing * 2, 12,
                        () -> toggles.getToggles()[index].getValue() ? 18 : 2, 18, toggles.getToggles()[i].getName());
            }
        }
    }

    private void step(int id, int x, int y) { icon(id, x, y, 10, () -> id % 2 == 0 ? 0 : 10, 0, "button.category." + shownCategory); }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (shownCategory != menu.category()) rebuild();
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        graphics.blit(TEXTURE, leftPos + 8, topPos - 8, 96, 0, 160, 160);
        graphics.blit(TEXTURE, leftPos, topPos + 143, 0, 167, 176, 89);
        RenderSystem.disableBlend();
        centered(graphics, Component.translatable("button.category." + shownCategory), 88, 8, 0xFFFFFF);
        if (shownCategory == 0) {
            centered(graphics, Component.translatable("golem.prop.priority"), 88, 44, 0xBBAAFF);
            centered(graphics, Component.literal("" + menu.seal.getPriority()), 88, 56, 0xFFFFFF);
            graphics.blit(TEXTURE, leftPos + 105, topPos + 75, 2, 18, 12, 12);
            if (menu.seal.getColor() > 0 && menu.seal.getColor() <= 16) {
                int rgb = DyeColor.byId(menu.seal.getColor() - 1).getTextureDiffuseColor();
                graphics.fill(leftPos + 108, topPos + 78, leftPos + 114, topPos + 84, 0xFF000000 | rgb);
            }
            if (minecraft.player != null && minecraft.player.getStringUUID().equals(menu.seal.getOwner()))
                centered(graphics, Component.translatable("golem.prop.owner"), 88, 104, 0xBBAAFF);
        } else if (shownCategory == 1 && menu.seal.getSeal() instanceof ISealConfigFilter filter) {
            int s = filter.getFilterSize(), sx = 16 + (s - 1) % 3 * 12, sy = 16 + (s - 1) / 3 * 12;
            for (int i = 0; i < s; i++) graphics.blit(TEXTURE, leftPos + 88 + i % 3 * 24 - sx, topPos + 72 + i / 3 * 24 - sy, 0, 56, 32, 32);
        } else if (shownCategory == 2) {
            int[] values = {menu.seal.getArea().getY(), menu.seal.getArea().getX(), menu.seal.getArea().getZ()};
            String[] axes = {"y", "x", "z"};
            for (int i = 0; i < 3; i++) {
                centered(graphics, Component.translatable("button.caption." + axes[i]), 88, 39 + i * 24, 0xDDDDDD);
                centered(graphics, Component.literal("" + values[i]), 88, 48 + i * 24, 0xFFFFFF);
            }
        } else if (shownCategory == 3 && menu.seal.getSeal() instanceof ISealConfigToggles toggles) {
            int n = toggles.getToggles().length, spacing = n < 4 ? 8 : n < 6 ? 7 : n < 9 ? 6 : 5;
            int w = 12;
            for (var toggle : toggles.getToggles()) w = Math.max(w, (12 + Math.min(100, font.width(Component.translatable(toggle.getName())))) / 2);
            for (int i = 0; i < n; i++) graphics.drawString(font, Component.translatable(toggles.getToggles()[i].getName()), leftPos + 100 - w, topPos + 67 - (n - 1) * spacing + i * spacing * 2, 0xFFFFFF, true);
        } else if (shownCategory == 4) {
            centered(graphics, Component.translatable("button.caption.required"), 88, 46, 0xDDDDDD);
            centered(graphics, Component.translatable("button.caption.forbidden"), 88, 78, 0xDDDDDD);
            traits(graphics, menu.seal.getSeal().getRequiredTags(), 56, mouseX, mouseY);
            traits(graphics, menu.seal.getSeal().getForbiddenTags(), 88, mouseX, mouseY);
        }
    }

    private void traits(GuiGraphics graphics, EnumGolemTrait[] traits, int y, int mouseX, int mouseY) {
        if (traits == null) return;
        for (int i = 0; i < traits.length; i++) {
            int x = leftPos + 80 + i * 18 - (traits.length - 1) * 9;
            graphics.blit(traits[i].icon, x, topPos + y, 0, 0, 16, 16, 16, 16);
            if (mouseX >= x && mouseX < x + 16 && mouseY >= topPos + y && mouseY < topPos + y + 16)
                setTooltipForNextRenderPass(java.util.List.of(traits[i].getLocalizedName().getVisualOrderText(), traits[i].getLocalizedDescription().getVisualOrderText()));
        }
    }

    private void centered(GuiGraphics graphics, Component text, int x, int y, int color) {
        graphics.drawString(font, text, leftPos + x - font.width(text) / 2, topPos + y, color, true);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        if (shownCategory == 1 && menu.seal.getSeal() instanceof ISealConfigFilter filter && filter.hasStacksizeLimiters()) {
            for (int i = 0; i < filter.getFilterSize(); i++) if (!menu.slots.get(i).getItem().isEmpty()) {
                var slot = menu.slots.get(i);
                graphics.pose().pushPose();
                graphics.pose().translate(0, 0, 250);
                String count = "" + filter.getFilterSlotSize(i);
                graphics.drawString(font, count, slot.x + 17 - font.width(count), slot.y + 9, 0xFFFFFF, true);
                graphics.pose().popPose();
            }
        }
    }

}

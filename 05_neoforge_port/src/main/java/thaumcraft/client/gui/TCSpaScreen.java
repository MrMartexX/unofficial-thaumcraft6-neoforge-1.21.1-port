package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import thaumcraft.Thaumcraft;
import thaumcraft.common.menu.TCSpaMenu;
import thaumcraft.common.registry.TCSounds;

/**
 * 1:1 legacy Arcane Spa GUI screen.
 * Displays bath salts slot, fluid level tank with tooltips, and mix mode toggle button.
 */
public class TCSpaScreen extends AbstractContainerScreen<TCSpaMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_spa.png");

    public TCSpaScreen(TCSpaMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 176;
        imageHeight = 166;
        titleLabelX = 8;
        titleLabelY = 6;
        inventoryLabelX = 8;
        inventoryLabelY = 72;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        renderCustomTooltips(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Base frame
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Mix mode toggle button indicator at (89, 35)
        if (menu.isMix()) {
            graphics.blit(BACKGROUND, leftPos + 89, topPos + 35, 208, 16, 8, 8);
        } else {
            graphics.blit(BACKGROUND, leftPos + 89, topPos + 35, 208, 32, 8, 8);
        }

        // Fluid tank level render at (106, 11)
        int fluidAmount = menu.getFluidAmount();
        Fluid fluid = menu.getFluid();
        if (fluidAmount > 0 && fluid != Fluids.EMPTY) {
            float ratio = Math.min(1.0F, (float) fluidAmount / 5000.0F);
            int barHeight = Math.round(48.0F * ratio);

            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid);
            ResourceLocation stillTexture = props.getStillTexture();
            if (stillTexture != null) {
                TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
                int tintColor = props.getTintColor();
                float r = ((tintColor >> 16) & 0xFF) / 255.0F;
                float g = ((tintColor >> 8) & 0xFF) / 255.0F;
                float b = (tintColor & 0xFF) / 255.0F;
                graphics.setColor(r, g, b, 1.0F);

                int startY = topPos + 15 + (48 - barHeight);
                graphics.blit(leftPos + 107, startY, 0, 8, barHeight, sprite);
                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        // Glass gauge overlay ticks
        graphics.blit(BACKGROUND, leftPos + 106, topPos + 11, 232, 0, 10, 55);

        RenderSystem.disableBlend();
    }

    private void renderCustomTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        // Fluid tank tooltip (x: 104..118, y: 10..65)
        if (mouseX >= leftPos + 104 && mouseX <= leftPos + 118 && mouseY >= topPos + 10 && mouseY <= topPos + 65) {
            Fluid fluid = menu.getFluid();
            int amount = menu.getFluidAmount();
            List<Component> tooltip = new ArrayList<>();
            if (fluid != Fluids.EMPTY && amount > 0) {
                tooltip.add(Component.translatable(fluid.getFluidType().getDescriptionId()));
                tooltip.add(Component.literal(amount + " / 5000 mB"));
            } else {
                tooltip.add(Component.translatable("gui.thaumcraft.empty"));
            }
            graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        }

        // Mix button tooltip (x: 88..98, y: 34..44)
        if (mouseX >= leftPos + 88 && mouseX <= leftPos + 98 && mouseY >= topPos + 34 && mouseY <= topPos + 44) {
            Component mixTooltip = Component.translatable(menu.isMix() ? "text.spa.mix.true" : "text.spa.mix.false");
            graphics.renderTooltip(font, mixTooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // Mix button clicked
            if (mouseX >= leftPos + 88 && mouseX <= leftPos + 98 && mouseY >= topPos + 34 && mouseY <= topPos + 44) {
                if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1);
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(TCSounds.CLACK.get(), 1.0F));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}

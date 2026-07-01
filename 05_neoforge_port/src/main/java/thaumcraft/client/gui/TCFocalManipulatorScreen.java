package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.casters.TCFocalManipulatorDesignPayload;
import thaumcraft.common.items.casters.TCFocusPackageHelper;
import thaumcraft.common.menu.TCFocalManipulatorMenu;

public class TCFocalManipulatorScreen extends AbstractContainerScreen<TCFocalManipulatorMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_wandtable.png");
    private static final String[] MEDIUM_KEYS = {
            "thaumcraft.TOUCH",
            "thaumcraft.BOLT",
            "thaumcraft.PROJECTILE",
            "thaumcraft.CLOUD",
            "thaumcraft.MINE",
            "thaumcraft.PLAN",
            "thaumcraft.SPELLBAT"
    };
    private static final String[] EFFECT_KEYS = {
            "thaumcraft.FIRE",
            "thaumcraft.FROST",
            "thaumcraft.AIR",
            "thaumcraft.EARTH",
            "thaumcraft.FLUX",
            "thaumcraft.BREAK",
            "thaumcraft.RIFT",
            "thaumcraft.EXCHANGE",
            "thaumcraft.CURSE",
            "thaumcraft.HEAL"
    };

    private int mediumIndex;
    private int effectIndex;
    private EditBox nameField;
    private Button mediumButton;
    private Button effectButton;

    public TCFocalManipulatorScreen(TCFocalManipulatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 190;
        imageHeight = 234;
        titleLabelX = 8;
        titleLabelY = 6;
        inventoryLabelX = 16;
        inventoryLabelY = 139;
    }

    @Override
    protected void init() {
        super.init();
        nameField = new EditBox(font, leftPos + 94, topPos + 38, 72, 16, Component.translatable("gui.thaumcraft.focal_manipulator.name"));
        nameField.setMaxLength(64);
        nameField.setValue(Component.translatable("item.thaumcraft.focus_1").getString());
        addRenderableWidget(nameField);

        mediumButton = addRenderableWidget(Button.builder(
                        mediumLabel(),
                        button -> {
                            mediumIndex = (mediumIndex + 1) % MEDIUM_KEYS.length;
                            button.setMessage(mediumLabel());
                            sendDesign(false);
                        })
                .bounds(leftPos + 94, topPos + 58, 72, 16)
                .build());
        effectButton = addRenderableWidget(Button.builder(
                        effectLabel(),
                        button -> {
                            effectIndex = (effectIndex + 1) % EFFECT_KEYS.length;
                            button.setMessage(effectLabel());
                            sendDesign(false);
                        })
                .bounds(leftPos + 94, topPos + 76, 72, 16)
                .build());
        addRenderableWidget(Button.builder(
                        Component.translatable("gui.thaumcraft.focal_manipulator.apply"),
                        button -> sendDesign(false))
                .bounds(leftPos + 94, topPos + 94, 72, 16)
                .build());
        addRenderableWidget(Button.builder(
                        Component.translatable("gui.thaumcraft.focal_manipulator.start"),
                        button -> sendDesign(true))
                .bounds(leftPos + 94, topPos + 112, 72, 20)
                .build());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        if (menu.remainingVis() > 0) {
            guiGraphics.drawString(font, Component.translatable("gui.thaumcraft.focal_manipulator.vis", menu.remainingVis()), leftPos + 94, topPos + 136, 0xC0FFFF, false);
        }
        if (menu.xpCost() > 0) {
            guiGraphics.drawString(font, Component.translatable("gui.thaumcraft.focal_manipulator.xp", menu.xpCost()), leftPos + 94, topPos + 148, 0xE0C080, false);
        }
        RenderSystem.disableBlend();
    }

    private void sendDesign(boolean startCraft) {
        TCFocusPackageHelper.NodeInstance root = new TCFocusPackageHelper.NodeInstance(0, "ROOT", -1, List.of(1), Map.of());
        TCFocusPackageHelper.NodeInstance medium = new TCFocusPackageHelper.NodeInstance(1, MEDIUM_KEYS[mediumIndex], 0, List.of(2), Map.of());
        TCFocusPackageHelper.NodeInstance effect = new TCFocusPackageHelper.NodeInstance(2, EFFECT_KEYS[effectIndex], 1, List.of(), Map.of());
        String encoded = TCFocusPackageHelper.buildPackage(List.of(root, medium, effect)).nodes();
        String focusName = nameField == null ? "" : nameField.getValue();
        PacketDistributor.sendToServer(new TCFocalManipulatorDesignPayload(menu.blockPos(), encoded, focusName, startCraft));
    }

    private Component mediumLabel() {
        return Component.translatable("gui.thaumcraft.focal_manipulator.medium", displayKey(MEDIUM_KEYS[mediumIndex]));
    }

    private Component effectLabel() {
        return Component.translatable("gui.thaumcraft.focal_manipulator.effect", displayKey(EFFECT_KEYS[effectIndex]));
    }

    private static String displayKey(String key) {
        int dot = key.lastIndexOf('.');
        return dot < 0 ? key : key.substring(dot + 1);
    }
}

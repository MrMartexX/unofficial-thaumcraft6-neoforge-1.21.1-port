package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.casters.TCFocalManipulatorDesignPayload;
import thaumcraft.common.items.casters.TCFocusElementDefinition;
import thaumcraft.common.items.casters.TCFocusElements;
import thaumcraft.common.items.casters.TCFocusPackageHelper;
import thaumcraft.common.items.components.TCFocusPackageComponent;
import thaumcraft.common.menu.TCFocalManipulatorMenu;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.research.TCKnowledgeClientCache;

/**
 * 1:1 legacy Focal Manipulator Screen.
 * Full node graph tree editor for Thaumcraft foci:
 * - Left slide-out drawer: unlocked focus parts with scrolling
 * - Center viewport: panning node tree graph with branching connector lines and frames
 * - Right panel: complexity meter, XP cost, vis cost, required aspect crystals, node setting spinners
 * - Top brass frame: custom focus name input box
 * - Bottom right: brass confirmation craft button
 */
public class TCFocalManipulatorScreen extends AbstractContainerScreen<TCFocalManipulatorMenu> {
    private static final ResourceLocation TEX =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_wandtable.png");
    private static final ResourceLocation TEX2 =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_wandtable2.png");
    private static final ResourceLocation TEX3 =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_wandtable3.png");
    private static final ResourceLocation TEX_BASE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_base.png");
    private static final ResourceLocation ICON_MEDIUM =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/foci/_medium.png");
    private static final ResourceLocation ICON_EFFECT =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/foci/_effect.png");

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private final Map<Integer, ClientNode> nodes = new HashMap<>();
    private final List<String> availableParts = new ArrayList<>();
    private final List<SpinnerWidget> spinners = new ArrayList<>();

    private EditBox nameField;
    private Slider scrollbarParts;

    private int selectedNodeId = 0;
    private int hoverNodeId = -1;
    private int nextNodeId = 1;
    private int partsScrollOffset = 0;

    private int scrollX = 0;
    private int scrollY = 0;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private boolean isPanning = false;

    private int totalComplexity = 0;
    private int maxComplexity = 0;
    private int costXp = 0;
    private int costVis = 0;
    private float costCast = 0.0F;
    private Map<String, Integer> requiredCrystals = Map.of();
    private boolean isValidDesign = false;

    public TCFocalManipulatorScreen(TCFocalManipulatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 231;
        this.imageHeight = 231;
    }

    public static class ClientNode {
        public int id;
        public String key; // null if empty placeholder
        public int parent = -1;
        public int x = 0;
        public int y = 0;
        public boolean target = false;
        public boolean trajectory = false;
        public final List<Integer> children = new ArrayList<>();
        public final Map<String, Integer> settings = new HashMap<>();

        public ClientNode(int id, String key, int parent) {
            this.id = id;
            this.key = key;
            this.parent = parent;
        }

        public boolean isPlaceholder() {
            return key == null || key.isBlank();
        }
    }

    static class Slider {
        final int x, y, width, height;
        float min, max, sliderPosition;
        final boolean vertical;
        boolean isMouseDown;

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

        void render(GuiGraphics g, int mx, int my) {
            g.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            g.pose().pushPose();
            if (vertical) {
                g.pose().translate(x + 2, y, 0.0F);
                g.pose().scale(1.0F, height / 32.0F, 1.0F);
                g.blit(TEX_BASE, 0, 0, 240, 176, 4, 32);
            }
            g.pose().popPose();
            if (vertical) {
                g.blit(TEX_BASE, x, y + (int) (sliderPosition * (height - 8)), 20, 20, 8, 8);
            }
        }
    }

    class SpinnerWidget {
        final String settingKey;
        final int x, y;
        int min, max, value;

        SpinnerWidget(String settingKey, int x, int y, int min, int max, int value) {
            this.settingKey = settingKey;
            this.x = x;
            this.y = y;
            this.min = min;
            this.max = max;
            this.value = value;
        }

        void render(GuiGraphics g, int mx, int my) {
            g.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            // Minus button at (x, y)
            boolean overMinus = mx >= x && mx < x + 8 && my >= y && my < y + 8;
            g.blit(TEX_BASE, x, y, overMinus ? 16 : 0, 216, 8, 8);

            // Value text
            String valStr = String.valueOf(value);
            int tw = font.width(valStr);
            g.drawString(font, valStr, x + 24 - tw / 2, y, 0xFFFFFF, true);

            // Plus button at (x + 36, y)
            boolean overPlus = mx >= x + 36 && mx < x + 44 && my >= y && my < y + 8;
            g.blit(TEX_BASE, x + 36, y, overPlus ? 24 : 8, 216, 8, 8);
        }

        boolean mouseClicked(double mx, double my) {
            if (mx >= x && mx < x + 8 && my >= y && my < y + 8) {
                if (value > min) {
                    value--;
                    onSettingChanged(settingKey, value);
                    if (minecraft != null) {
                        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    }
                    return true;
                }
            }
            if (mx >= x + 36 && mx < x + 44 && my >= y && my < y + 8) {
                if (value < max) {
                    value++;
                    onSettingChanged(settingKey, value);
                    if (minecraft != null) {
                        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    }
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    protected void init() {
        super.init();
        nameField = new EditBox(font, leftPos + 30, topPos + 11, 160, 12, Component.empty());
        nameField.setTextColor(0xFFFFFF);
        nameField.setBordered(false);
        nameField.setMaxLength(32);
        nameField.setValue(Component.translatable("item.thaumcraft.focus_1").getString());
        addRenderableWidget(nameField);

        scrollbarParts = new Slider(leftPos - 12, topPos + 28, 8, 149, 0.0F, 1.0F, 0.0F, true);

        refreshAvailableParts();
        resetToDefaultGraph();
    }

    private void refreshAvailableParts() {
        availableParts.clear();
        for (Map.Entry<String, TCFocusElementDefinition> entry : TCFocusElements.definitions().entrySet()) {
            TCFocusElementDefinition def = entry.getValue();
            if (def.kind() == TCFocusElementDefinition.Kind.ROOT) {
                continue;
            }
            // Check research requirement
            if (def.research().isBlank() || TCKnowledgeClientCache.hasResearch(def.research())) {
                availableParts.add(def.key());
            }
        }
    }

    private void resetToDefaultGraph() {
        nodes.clear();
        nextNodeId = 1;
        // Root node
        ClientNode root = new ClientNode(0, "ROOT", -1);
        root.x = 0;
        root.y = 0;
        root.target = true;
        root.trajectory = true;
        nodes.put(0, root);

        // Child placeholder
        ClientNode placeholder = new ClientNode(1, null, 0);
        placeholder.x = 0;
        placeholder.y = 1;
        root.children.add(1);
        nodes.put(1, placeholder);

        selectedNodeId = 1;
        recalculateGraph();
    }

    private void recalculateGraph() {
        // Layout positions
        layoutNode(0, 0, 0);

        // Calculate complexity, costs and crystals
        List<TCFocusPackageHelper.NodeInstance> instances = toNodeInstances();
        TCFocusPackageComponent pkg = TCFocusPackageHelper.buildPackage(instances);

        totalComplexity = pkg.complexity();
        ItemStack focusStack = menu.getSlot(TCFocalManipulatorMenu.SLOT_FOCUS).getItem();
        if (focusStack.getItem() instanceof ItemFocus focusItem) {
            maxComplexity = focusItem.maxComplexity();
        } else {
            maxComplexity = 0;
        }

        costXp = Math.max(1, Math.round((float) Math.sqrt(totalComplexity)));
        costVis = Math.round(totalComplexity * 10.0F + (maxComplexity > 0 ? maxComplexity / 5.0F : 0.0F));
        costCast = TCFocusPackageHelper.getVisCost(pkg);
        requiredCrystals = TCFocusPackageHelper.crystalCosts(pkg);

        boolean hasEffect = false;
        for (ClientNode n : nodes.values()) {
            if (!n.isPlaceholder()) {
                TCFocusElementDefinition def = TCFocusElements.get(n.key).orElse(null);
                if (def != null && def.kind() == TCFocusElementDefinition.Kind.EFFECT) {
                    hasEffect = true;
                    break;
                }
            }
        }
        isValidDesign = !instances.isEmpty() && hasEffect && totalComplexity > 0 && (maxComplexity <= 0 || totalComplexity <= maxComplexity);

        rebuildSpinners();
        sendDesign(false);
    }

    private int layoutNode(int nodeId, int x, int y) {
        ClientNode node = nodes.get(nodeId);
        if (node == null) return x;
        node.x = x;
        node.y = y;

        if (node.children.isEmpty()) {
            return x;
        }

        if (node.children.size() == 1) {
            return layoutNode(node.children.get(0), x, y + 1);
        }

        // Branching / Split layout
        int currentX = x - (node.children.size() - 1);
        for (int childId : node.children) {
            layoutNode(childId, currentX, y + 1);
            currentX += 2;
        }
        return x;
    }

    private void rebuildSpinners() {
        spinners.clear();
        ClientNode selected = nodes.get(selectedNodeId);
        if (selected == null || selected.isPlaceholder()) return;

        TCFocusElementDefinition def = TCFocusElements.get(selected.key).orElse(null);
        if (def == null || def.settingOrder().isEmpty()) return;

        int startY = topPos + 120;
        int index = 0;
        for (String setting : def.settingOrder()) {
            int currentVal = selected.settings.getOrDefault(setting, defaultSetting(setting, def.key()));
            int min = 1;
            int max = 5;
            if ("duration".equals(setting)) {
                min = 0;
                max = 10;
            } else if ("radius".equals(setting)) {
                min = 1;
                max = 3;
            } else if ("depth".equals(setting)) {
                min = 8;
                max = 32;
            } else if ("forks".equals(setting)) {
                min = 2;
                max = 10;
            } else if ("silk".equals(setting)) {
                min = 0;
                max = 1;
            } else if ("fortune".equals(setting)) {
                min = 0;
                max = 3;
            }
            spinners.add(new SpinnerWidget(setting, leftPos + 220, startY + index * 22, min, max, currentVal));
            index++;
        }
    }

    private void onSettingChanged(String settingKey, int val) {
        ClientNode selected = nodes.get(selectedNodeId);
        if (selected != null && !selected.isPlaceholder()) {
            selected.settings.put(settingKey, val);
            recalculateGraph();
        }
    }

    private List<TCFocusPackageHelper.NodeInstance> toNodeInstances() {
        List<TCFocusPackageHelper.NodeInstance> list = new ArrayList<>();
        for (ClientNode n : nodes.values()) {
            if (n.isPlaceholder()) continue;
            List<Integer> validChildren = new ArrayList<>();
            for (int cid : n.children) {
                ClientNode child = nodes.get(cid);
                if (child != null && !child.isPlaceholder()) {
                    validChildren.add(cid);
                }
            }
            list.add(new TCFocusPackageHelper.NodeInstance(n.id, n.key, n.parent, validChildren, n.settings));
        }
        return list;
    }

    private void sendDesign(boolean startCraft) {
        List<TCFocusPackageHelper.NodeInstance> instances = toNodeInstances();
        String encoded = instances.isEmpty() ? "" : TCFocusPackageHelper.buildPackage(instances).nodes();
        String name = nameField != null ? nameField.getValue() : "";
        PacketDistributor.sendToServer(new TCFocalManipulatorDesignPayload(menu.blockPos(), encoded, name, startCraft));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Panning drag in center viewport
        if (isPanning) {
            scrollX -= mouseX - lastMouseX;
            scrollY -= mouseY - lastMouseY;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }

        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        renderPartTooltips(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        int k = leftPos;
        int l = topPos;

        // Background paper panel
        graphics.blit(TEX2, k, l, 0, 0, imageWidth, imageHeight);

        // Slide-out drawer on left (71 x 239)
        graphics.blit(TEX3, k - 71, l - 3, 0, 0, 71, 239);

        // Center viewport nodes and connector lines
        drawNodeTree(graphics, k + 132 - scrollX, l + 48 - scrollY, mouseX, mouseY);

        // Foreground brass chassis
        graphics.blit(TEX, k, l, 0, 0, imageWidth, imageHeight);

        // Stats on right
        if (maxComplexity > 0) {
            int compColor = totalComplexity > maxComplexity ? 0xFF5555 : 0xFFE080;
            graphics.drawString(font, totalComplexity + " / " + maxComplexity, k + 242, l + 36, compColor, true);
        }
        graphics.drawString(font, String.valueOf(costXp) + " XP", k + 242, l + 50, costXp > (minecraft != null && minecraft.player != null ? minecraft.player.experienceLevel : 0) ? 0xFF5555 : 0x80FF80, true);
        int vis = menu.remainingVis() > 0 ? menu.remainingVis() : costVis;
        graphics.drawString(font, ChatFormatting.AQUA + String.valueOf(vis) + " Vis", k + 242, l + 64, 0x80FFFF, true);

        if (costCast > 0.0F) {
            graphics.drawString(font, Component.translatable("item.Focus.cost1", DECIMAL_FORMAT.format(costCast)), k + 220, l + 80, 0x80FFFF, true);
        }

        // Required aspect crystals
        if (!requiredCrystals.isEmpty()) {
            graphics.drawString(font, Component.translatable("wandtable.text4"), k + 220, l + 94, 0xFFD700, true);
            int crystalIdx = 0;
            for (Map.Entry<String, Integer> entry : requiredCrystals.entrySet()) {
                Aspect aspect = Aspect.getAspect(entry.getKey());
                if (aspect != null) {
                    int cx = k + 220 + (crystalIdx % 3) * 18;
                    int cy = l + 104 + (crystalIdx / 3) * 18;
                    drawAspectIconSmall(graphics, aspect, cx, cy);
                    graphics.drawString(font, String.valueOf(entry.getValue()), cx + 10, cy + 8, 0xFFFFFF, true);
                    crystalIdx++;
                }
            }
        }

        // Draw node settings spinners on bottom right
        for (SpinnerWidget spinner : spinners) {
            spinner.render(graphics, mouseX, mouseY);
            graphics.drawString(font, Component.translatable("focus.setting." + spinner.settingKey), spinner.x - 2, spinner.y - 10, 0xE0C080, true);
        }

        // Draw available parts in left drawer
        drawAvailableParts(graphics, mouseX, mouseY);

        // Start Craft button (confirm) at (k + 160, l + 208, 24 x 16)
        boolean canCraft = isValidDesign && menu.remainingVis() <= 0 && menu.getSlot(TCFocalManipulatorMenu.SLOT_FOCUS).hasItem();
        boolean overCraft = mouseX >= k + 160 && mouseX < k + 184 && mouseY >= l + 208 && mouseY < l + 224;
        float craftTint = canCraft ? (overCraft ? 1.0F : 0.85F) : 0.4F;
        graphics.setColor(craftTint, craftTint, craftTint, 1.0F);
        graphics.blit(TEX_BASE, k + 160, l + 208, 232, 240, 24, 16);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.disableBlend();
    }

    private void drawNodeTree(GuiGraphics graphics, int originX, int originY, int mx, int my) {
        hoverNodeId = -1;

        for (ClientNode node : nodes.values()) {
            int nx = originX + node.x * 24;
            int ny = originY + node.y * 32;

            boolean mouseover = mx >= nx - 10 && mx <= nx + 10 && my >= ny - 10 && my <= ny + 10
                    && isInsideCenterViewport(mx, my);
            if (mouseover) {
                hoverNodeId = node.id;
            }

            // Draw connector lines to parent
            ClientNode parent = nodes.get(node.parent);
            if (parent != null) {
                int px = originX + parent.x * 24;
                int py = originY + parent.y * 32;
                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                // Vertical connector
                drawClipped(graphics, nx - 6, ny - 22, 54, 232, 12, 12);
                if (nx != px) {
                    // Branch horizontal connector
                    int step = nx < px ? 24 : -24;
                    drawClipped(graphics, nx - (nx < px ? 4 : 12), ny - 36, nx < px ? 8 : 24, 240, 16, 16);
                }
            }

            // Draw node frame or placeholder
            if (!node.isPlaceholder()) {
                drawFocusNode(graphics, node.key, nx, ny, mouseover);
            } else {
                // Empty slot circle (120, 232, 24, 24)
                drawClipped(graphics, nx - 12, ny - 12, 120, 232, 24, 24);
            }

            // Draw selection ring
            if (selectedNodeId == node.id || mouseover) {
                drawClipped(graphics, nx - 12, ny - 12, 96, 232, 24, 24);
            }
        }
    }

    private void drawFocusNode(GuiGraphics graphics, String key, int x, int y, boolean mouseover) {
        TCFocusElementDefinition def = TCFocusElements.get(key).orElse(null);
        if (def == null) return;

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0F);
        graphics.pose().mulPose(Axis.ZN.rotationDegrees(90.0F));

        int c = def.color();
        float r = ((c >> 16) & 0xFF) / 255.0F;
        float g = ((c >> 8) & 0xFF) / 255.0F;
        float b = (c & 0xFF) / 255.0F;

        // Background frame: effect or medium
        if (def.kind() == TCFocusElementDefinition.Kind.EFFECT) {
            graphics.setColor(r, g, b, 1.0F);
            graphics.blit(ICON_EFFECT, -16, -16, 0, 0, 32, 32, 32, 32);
        } else if (def.kind() == TCFocusElementDefinition.Kind.MEDIUM && !"ROOT".equals(def.key())) {
            graphics.setColor(r, g, b, 1.0F);
            graphics.blit(ICON_MEDIUM, -16, -16, 0, 0, 32, 32, 32, 32);
        }

        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        // Focus part icon
        ResourceLocation iconPath = getPartIconPath(key);
        if (iconPath != null) {
            graphics.blit(iconPath, -8, -8, 0, 0, 16, 16, 16, 16);
        }

        graphics.pose().popPose();
    }

    private void drawAvailableParts(GuiGraphics graphics, int mx, int my) {
        int gx = leftPos - 71;
        int gy = topPos + 25;
        int maxVisible = 6;
        int total = availableParts.size();

        for (int i = 0; i < maxVisible && (i + partsScrollOffset) < total; i++) {
            String partKey = availableParts.get(i + partsScrollOffset);
            int itemY = gy + i * 26;
            boolean hovered = mx >= gx + 20 && mx <= gx + 52 && my >= itemY && my <= itemY + 24;

            drawFocusNode(graphics, partKey, gx + 36, itemY + 12, hovered);
        }
    }

    private void drawClipped(GuiGraphics g, int x, int y, int sx, int sy, int w, int h) {
        if (isInsideCenterViewport(x + w / 2, y + h / 2)) {
            g.blit(TEX, x, y, sx, sy, w, h);
        }
    }

    private boolean isInsideCenterViewport(int px, int py) {
        return px >= leftPos + 50 && px <= leftPos + 195 && py >= topPos + 25 && py <= topPos + 195;
    }

    private static ResourceLocation getPartIconPath(String key) {
        String name = key.toLowerCase(java.util.Locale.ROOT);
        int dot = name.lastIndexOf('.');
        if (dot >= 0) {
            name = name.substring(dot + 1);
        }
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/foci/" + name + ".png");
    }

    private static void drawAspectIconSmall(GuiGraphics graphics, Aspect aspect, int x, int y) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0F);
        graphics.pose().scale(0.375F, 0.375F, 1.0F);
        RenderSystem.enableBlend();
        graphics.blit(aspect.getImage(), 0, 0, 0, 0, 32, 32, 32, 32);
        graphics.pose().popPose();
    }

    private void renderPartTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        // Left drawer part tooltips
        int gx = leftPos - 71;
        int gy = topPos + 25;
        int maxVisible = 6;
        int total = availableParts.size();

        for (int i = 0; i < maxVisible && (i + partsScrollOffset) < total; i++) {
            int itemY = gy + i * 26;
            if (mouseX >= gx + 20 && mouseX <= gx + 52 && mouseY >= itemY && mouseY <= itemY + 24) {
                String partKey = availableParts.get(i + partsScrollOffset);
                TCFocusElementDefinition def = TCFocusElements.get(partKey).orElse(null);
                if (def != null) {
                    List<Component> tip = new ArrayList<>();
                    tip.add(Component.translatable("focus." + def.key()));
                    tip.add(Component.literal("Type: " + def.kind().name()).withStyle(ChatFormatting.GRAY));
                    tip.add(Component.literal("Complexity: " + def.complexity(Map.of())).withStyle(ChatFormatting.GOLD));
                    graphics.renderComponentTooltip(font, tip, mouseX, mouseY);
                    return;
                }
            }
        }

        // Center viewport hover tooltip
        if (hoverNodeId >= 0) {
            ClientNode node = nodes.get(hoverNodeId);
            if (node != null && !node.isPlaceholder()) {
                TCFocusElementDefinition def = TCFocusElements.get(node.key).orElse(null);
                if (def != null) {
                    List<Component> tip = new ArrayList<>();
                    tip.add(Component.translatable("focus." + def.key()));
                    tip.add(Component.literal("Complexity: " + def.complexity(node.settings)).withStyle(ChatFormatting.GOLD));
                    graphics.renderComponentTooltip(font, tip, mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int) mouseX;
        int my = (int) mouseY;

        // Check spinners
        for (SpinnerWidget spinner : spinners) {
            if (spinner.mouseClicked(mouseX, mouseY)) {
                return true;
            }
        }

        // Craft button clicked
        if (button == 0 && mx >= leftPos + 160 && mx < leftPos + 184 && my >= topPos + 208 && my < topPos + 224) {
            if (isValidDesign && menu.remainingVis() <= 0 && menu.getSlot(TCFocalManipulatorMenu.SLOT_FOCUS).hasItem()) {
                sendDesign(true);
                if (minecraft != null) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(TCSounds.CRAFTSTART.get(), 1.0F));
                }
                return true;
            }
        }

        // Center viewport node clicked
        if (hoverNodeId >= 0) {
            selectedNodeId = hoverNodeId;
            ClientNode clicked = nodes.get(hoverNodeId);
            if (button == 1 && clicked != null && clicked.id != 0 && !clicked.isPlaceholder()) {
                // Right-click removes node and reverts to placeholder
                removeNodeAndChildren(clicked.id);
                recalculateGraph();
                if (minecraft != null) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(TCSounds.CLACK.get(), 1.0F));
                }
                return true;
            }
            rebuildSpinners();
            if (minecraft != null) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            return true;
        }

        // Left drawer part clicked
        int gx = leftPos - 71;
        int gy = topPos + 25;
        for (int i = 0; i < 6 && (i + partsScrollOffset) < availableParts.size(); i++) {
            int itemY = gy + i * 26;
            if (mx >= gx + 20 && mx <= gx + 52 && my >= itemY && my <= itemY + 24) {
                String chosenKey = availableParts.get(i + partsScrollOffset);
                addPartToSelected(chosenKey);
                if (minecraft != null) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(TCSounds.CLACK.get(), 1.0F));
                }
                return true;
            }
        }

        // Viewport pan start
        if (button == 0 && isInsideCenterViewport(mx, my)) {
            isPanning = true;
            lastMouseX = mx;
            lastMouseY = my;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void addPartToSelected(String partKey) {
        ClientNode selected = nodes.get(selectedNodeId);
        if (selected == null) return;

        TCFocusElementDefinition def = TCFocusElements.get(partKey).orElse(null);
        if (def == null) return;

        selected.key = def.key();
        selected.target = def.kind() == TCFocusElementDefinition.Kind.MEDIUM || def.kind() == TCFocusElementDefinition.Kind.MOD;
        selected.trajectory = def.kind() == TCFocusElementDefinition.Kind.MEDIUM;

        // If medium or mod, spawn placeholder child nodes
        selected.children.clear();
        if (def.kind() == TCFocusElementDefinition.Kind.MOD) {
            // Split branches into 2 placeholder children
            int c1 = ++nextNodeId;
            ClientNode child1 = new ClientNode(c1, null, selected.id);
            nodes.put(c1, child1);
            selected.children.add(c1);

            int c2 = ++nextNodeId;
            ClientNode child2 = new ClientNode(c2, null, selected.id);
            nodes.put(c2, child2);
            selected.children.add(c2);
            selectedNodeId = c1;
        } else if (def.kind() == TCFocusElementDefinition.Kind.MEDIUM) {
            int c1 = ++nextNodeId;
            ClientNode child = new ClientNode(c1, null, selected.id);
            nodes.put(c1, child);
            selected.children.add(c1);
            selectedNodeId = c1;
        }

        recalculateGraph();
    }

    private void removeNodeAndChildren(int nodeId) {
        ClientNode node = nodes.get(nodeId);
        if (node == null || node.id == 0) return;

        // Recursively remove children
        for (int childId : new ArrayList<>(node.children)) {
            removeNodeAndChildren(childId);
        }
        node.children.clear();
        node.key = null; // Revert to placeholder
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isPanning = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX >= leftPos - 71 && mouseX <= leftPos && mouseY >= topPos && mouseY <= topPos + 230) {
            if (scrollY < 0 && partsScrollOffset + 6 < availableParts.size()) {
                partsScrollOffset++;
                return true;
            } else if (scrollY > 0 && partsScrollOffset > 0) {
                partsScrollOffset--;
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private static int defaultSetting(String setting, String definitionKey) {
        return switch (setting) {
            case "power" -> 1;
            case "duration" -> "thaumcraft.cloud".equals(definitionKey) ? 5 : 2;
            case "radius" -> 1;
            case "depth" -> 8;
            case "forks" -> 2;
            case "cone" -> 10;
            default -> 0;
        };
    }
}

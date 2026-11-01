package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.Thaumcraft;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.golems.parts.GolemAddon;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.api.golems.parts.GolemHead;
import thaumcraft.api.golems.parts.GolemLeg;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.common.golems.GolemProperties;
import thaumcraft.common.golems.TCGolemBuilderCraftPayload;
import thaumcraft.common.menu.TCGolemBuilderMenu;
import thaumcraft.common.tiles.crafting.TCGolemBuilderBlockEntity;

public final class TCGolemBuilderScreen extends AbstractContainerScreen<TCGolemBuilderMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_golembuilder.png");

    private static int headIndex = 0;
    private static int matIndex = 0;
    private static int armIndex = 0;
    private static int legIndex = 0;
    private static int addonIndex = 0;

    private final List<GolemHead> valHeads = new ArrayList<>();
    private final List<GolemMaterial> valMats = new ArrayList<>();
    private final List<GolemArm> valArms = new ArrayList<>();
    private final List<GolemLeg> valLegs = new ArrayList<>();
    private final List<GolemAddon> valAddons = new ArrayList<>();

    private IGolemProperties props;
    private float hearts;
    private float armor;
    private float damage;
    private int cost;
    private boolean allFound;
    private ItemStack[] components = new ItemStack[0];
    private boolean[] owns = new boolean[0];

    public TCGolemBuilderScreen(TCGolemBuilderMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 208;
        this.imageHeight = 224;
        this.titleLabelY = -1000;
        this.inventoryLabelY = -1000;
    }

    @Override
    protected void init() {
        super.init();
        valHeads.clear();
        for (GolemHead h : GolemHead.getHeads()) {
            if (h != null) valHeads.add(h);
        }
        valMats.clear();
        for (GolemMaterial m : GolemMaterial.getMaterials()) {
            if (m != null) valMats.add(m);
        }
        valArms.clear();
        for (GolemArm a : GolemArm.getArms()) {
            if (a != null) valArms.add(a);
        }
        valLegs.clear();
        for (GolemLeg l : GolemLeg.getLegs()) {
            if (l != null) valLegs.add(l);
        }
        valAddons.clear();
        for (GolemAddon ad : GolemAddon.getAddons()) {
            if (ad != null) valAddons.add(ad);
        }

        clampIndices();
        recalculateProps();
    }

    private void clampIndices() {
        if (!valHeads.isEmpty()) headIndex = Math.floorMod(headIndex, valHeads.size());
        if (!valMats.isEmpty()) matIndex = Math.floorMod(matIndex, valMats.size());
        if (!valArms.isEmpty()) armIndex = Math.floorMod(armIndex, valArms.size());
        if (!valLegs.isEmpty()) legIndex = Math.floorMod(legIndex, valLegs.size());
        if (!valAddons.isEmpty()) addonIndex = Math.floorMod(addonIndex, valAddons.size());
    }

    private void recalculateProps() {
        if (valHeads.isEmpty() || valMats.isEmpty() || valArms.isEmpty() || valLegs.isEmpty() || valAddons.isEmpty()) {
            props = GolemProperties.fromLong(0L);
            return;
        }

        GolemProperties p = (GolemProperties) GolemProperties.fromLong(0L);
        p.setHead(valHeads.get(headIndex));
        p.setMaterial(valMats.get(matIndex));
        p.setArms(valArms.get(armIndex));
        p.setLegs(valLegs.get(legIndex));
        p.setAddon(valAddons.get(addonIndex));
        this.props = p;

        int hh = 10 + p.getMaterial().healthMod;
        if (p.hasTrait(EnumGolemTrait.FRAGILE)) {
            hh = (int) (hh * 0.75F);
        }
        this.hearts = hh / 2.0F;

        int aa = p.getMaterial().armor;
        if (p.hasTrait(EnumGolemTrait.ARMORED)) {
            aa = (int) Math.max(aa * 1.5F, aa + 1);
        }
        if (p.hasTrait(EnumGolemTrait.FRAGILE)) {
            aa = (int) (aa * 0.75F);
        }
        this.armor = aa / 2.0F;

        double dd = p.hasTrait(EnumGolemTrait.FIGHTER) ? p.getMaterial().damage : 0.0;
        if (p.hasTrait(EnumGolemTrait.BRUTAL)) {
            dd = Math.max(dd * 1.5, dd + 1.0);
        }
        this.damage = (float) (dd / 2.0);

        redoComponents();
    }

    private void redoComponents() {
        allFound = true;
        cost = props.getTraits().size() * 2;
        components = props.generateComponents();
        owns = new boolean[components.length];
        Player player = minecraft != null ? minecraft.player : null;

        for (int i = 0; i < components.length; i++) {
            cost += components[i].getCount();
            owns[i] = player != null && hasPlayerItem(player, components[i]);
            if (!owns[i]) {
                allFound = false;
            }
        }
    }

    private boolean hasPlayerItem(Player player, ItemStack needed) {
        int count = 0;
        for (ItemStack invStack : player.getInventory().items) {
            if (ItemStack.isSameItemSameComponents(invStack, needed)) {
                count += invStack.getCount();
                if (count >= needed.getCount()) return true;
            }
        }
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        renderGolemBuilderTooltips(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        // Crafting progress bar
        int curCost = menu.getCost();
        int maxCost = menu.getMaxCost();
        if (curCost > 0 && maxCost > 0) {
            int progress = (int) (46.0F * (1.0F - (float) curCost / (float) maxCost));
            graphics.blit(BACKGROUND, leftPos + 145, topPos + 89, 209, 89, Math.max(0, Math.min(46, progress)), 6, 256, 256);
        }

        // Stats numbers
        graphics.drawCenteredString(font, String.valueOf(hearts), leftPos + 48, topPos + 108, 0xFFFFFF);
        graphics.drawCenteredString(font, String.valueOf(armor), leftPos + 72, topPos + 108, 0xFFFFFF);
        graphics.drawCenteredString(font, String.valueOf(damage), leftPos + 97, topPos + 108, 0xFFFFFF);

        // Machina Essentia cost
        if (components.length > 0) {
            String costStr = String.valueOf(cost);
            graphics.drawString(font, costStr, leftPos + 162 - font.width(costStr), topPos + 24, 0xFFFFFF);
        }

        // Component item icons and availability
        if (components.length > 0) {
            int i = 1;
            int q = 0;
            for (int a = 0; a < components.length; a++) {
                int cx = leftPos + 152 + q * 16;
                int cy = topPos + 24 + 16 * i;
                graphics.renderItem(components[a], cx, cy);
                graphics.renderItemDecorations(font, components[a], cx, cy);
                if (!owns[a]) {
                    // Render red cross box indicating missing item
                    graphics.blit(BACKGROUND, cx, cy, 240, 0, 16, 16, 256, 256);
                }
                if (++i > 3) {
                    i = 0;
                    ++q;
                }
            }
        }

        // Part slot background indicators
        renderPartSelectorBoxes(graphics);

        // Craft button render
        boolean canCraft = allFound && curCost <= 0;
        int btnU = canCraft ? 209 : 225;
        graphics.blit(BACKGROUND, leftPos + 120, topPos + 104, btnU, 105, 16, 16, 256, 256);
    }

    private void renderPartSelectorBoxes(GuiGraphics graphics) {
        // Material at 24, 24
        if (!valMats.isEmpty()) {
            GolemMaterial mat = valMats.get(matIndex);
            graphics.drawString(font, mat.key.substring(0, Math.min(3, mat.key.length())).toUpperCase(), leftPos + 26, topPos + 28, 0xDDDDDD);
        }
        // Head at 120, 24
        if (!valHeads.isEmpty()) {
            GolemHead head = valHeads.get(headIndex);
            graphics.drawString(font, head.key.substring(0, Math.min(3, head.key.length())).toUpperCase(), leftPos + 122, topPos + 28, 0xDDDDDD);
        }
        // Arms at 120, 48
        if (!valArms.isEmpty()) {
            GolemArm arm = valArms.get(armIndex);
            graphics.drawString(font, arm.key.substring(0, Math.min(3, arm.key.length())).toUpperCase(), leftPos + 122, topPos + 52, 0xDDDDDD);
        }
        // Legs at 120, 72
        if (!valLegs.isEmpty()) {
            GolemLeg leg = valLegs.get(legIndex);
            graphics.drawString(font, leg.key.substring(0, Math.min(3, leg.key.length())).toUpperCase(), leftPos + 122, topPos + 76, 0xDDDDDD);
        }
        // Addon at 24, 72
        if (!valAddons.isEmpty()) {
            GolemAddon addon = valAddons.get(addonIndex);
            graphics.drawString(font, addon.key.substring(0, Math.min(3, addon.key.length())).toUpperCase(), leftPos + 26, topPos + 76, 0xDDDDDD);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int mx = (int) mouseX;
            int my = (int) mouseY;

            // Head prev/next (left arrow: 101, 19; right arrow: 129, 19)
            if (isHovering(101, 19, 10, 10, mouseX, mouseY)) {
                headIndex--;
                clampIndices();
                recalculateProps();
                return true;
            }
            if (isHovering(129, 19, 10, 10, mouseX, mouseY)) {
                headIndex++;
                clampIndices();
                recalculateProps();
                return true;
            }

            // Material prev/next (left arrow: 5, 19; right arrow: 33, 19)
            if (isHovering(5, 19, 10, 10, mouseX, mouseY)) {
                matIndex--;
                clampIndices();
                recalculateProps();
                return true;
            }
            if (isHovering(33, 19, 10, 10, mouseX, mouseY)) {
                matIndex++;
                clampIndices();
                recalculateProps();
                return true;
            }

            // Arms prev/next (left arrow: 101, 43; right arrow: 129, 43)
            if (isHovering(101, 43, 10, 10, mouseX, mouseY)) {
                armIndex--;
                clampIndices();
                recalculateProps();
                return true;
            }
            if (isHovering(129, 43, 10, 10, mouseX, mouseY)) {
                armIndex++;
                clampIndices();
                recalculateProps();
                return true;
            }

            // Legs prev/next (left arrow: 101, 67; right arrow: 129, 67)
            if (isHovering(101, 67, 10, 10, mouseX, mouseY)) {
                legIndex--;
                clampIndices();
                recalculateProps();
                return true;
            }
            if (isHovering(129, 67, 10, 10, mouseX, mouseY)) {
                legIndex++;
                clampIndices();
                recalculateProps();
                return true;
            }

            // Addon prev/next (left arrow: 5, 67; right arrow: 33, 67)
            if (isHovering(5, 67, 10, 10, mouseX, mouseY)) {
                addonIndex--;
                clampIndices();
                recalculateProps();
                return true;
            }
            if (isHovering(33, 67, 10, 10, mouseX, mouseY)) {
                addonIndex++;
                clampIndices();
                recalculateProps();
                return true;
            }

            // Craft Button at 120, 104 (size 16x16)
            if (isHovering(120, 104, 16, 16, mouseX, mouseY)) {
                if (allFound && menu.getCost() <= 0 && minecraft != null && minecraft.player != null) {
                    TCGolemBuilderBlockEntity be = menu.getBlockEntity();
                    if (be != null) {
                        PacketDistributor.sendToServer(new TCGolemBuilderCraftPayload(be.getBlockPos(), props.toLong()));
                    }
                    if (minecraft.gameMode != null) {
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 99);
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderGolemBuilderTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        if (isHovering(120, 104, 16, 16, mouseX, mouseY)) {
            List<Component> tip = new ArrayList<>();
            tip.add(Component.translatable("gui.golem_builder.craft").withStyle(ChatFormatting.GOLD));
            if (!allFound) {
                tip.add(Component.translatable("gui.golem_builder.missing_materials").withStyle(ChatFormatting.RED));
            } else if (menu.getCost() > 0) {
                tip.add(Component.translatable("gui.golem_builder.crafting").withStyle(ChatFormatting.YELLOW));
            } else {
                tip.add(Component.translatable("gui.golem_builder.ready").withStyle(ChatFormatting.GREEN));
            }
            graphics.renderComponentTooltip(font, tip, mouseX, mouseY);
            return;
        }

        // Material Tooltip
        if (isHovering(24, 24, 16, 16, mouseX, mouseY) && !valMats.isEmpty()) {
            GolemMaterial m = valMats.get(matIndex);
            List<Component> tip = List.of(
                    Component.empty().append(m.getLocalizedName()).withStyle(ChatFormatting.AQUA),
                    Component.empty().append(m.getLocalizedDescription()).withStyle(ChatFormatting.GRAY)
            );
            graphics.renderComponentTooltip(font, tip, mouseX, mouseY);
            return;
        }

        // Head Tooltip
        if (isHovering(120, 24, 16, 16, mouseX, mouseY) && !valHeads.isEmpty()) {
            GolemHead h = valHeads.get(headIndex);
            List<Component> tip = List.of(
                    Component.empty().append(h.getLocalizedName()).withStyle(ChatFormatting.AQUA),
                    Component.empty().append(h.getLocalizedDescription()).withStyle(ChatFormatting.GRAY)
            );
            graphics.renderComponentTooltip(font, tip, mouseX, mouseY);
            return;
        }

        // Arms Tooltip
        if (isHovering(120, 48, 16, 16, mouseX, mouseY) && !valArms.isEmpty()) {
            GolemArm a = valArms.get(armIndex);
            List<Component> tip = List.of(
                    Component.empty().append(a.getLocalizedName()).withStyle(ChatFormatting.AQUA),
                    Component.empty().append(a.getLocalizedDescription()).withStyle(ChatFormatting.GRAY)
            );
            graphics.renderComponentTooltip(font, tip, mouseX, mouseY);
            return;
        }

        // Legs Tooltip
        if (isHovering(120, 72, 16, 16, mouseX, mouseY) && !valLegs.isEmpty()) {
            GolemLeg l = valLegs.get(legIndex);
            List<Component> tip = List.of(
                    Component.empty().append(l.getLocalizedName()).withStyle(ChatFormatting.AQUA),
                    Component.empty().append(l.getLocalizedDescription()).withStyle(ChatFormatting.GRAY)
            );
            graphics.renderComponentTooltip(font, tip, mouseX, mouseY);
            return;
        }

        // Addon Tooltip
        if (isHovering(24, 72, 16, 16, mouseX, mouseY) && !valAddons.isEmpty()) {
            GolemAddon ad = valAddons.get(addonIndex);
            List<Component> tip = List.of(
                    Component.empty().append(ad.getLocalizedName()).withStyle(ChatFormatting.AQUA),
                    Component.empty().append(ad.getLocalizedDescription()).withStyle(ChatFormatting.GRAY)
            );
            graphics.renderComponentTooltip(font, tip, mouseX, mouseY);
            return;
        }

        // Component tooltips
        if (components.length > 0) {
            int i = 1;
            int q = 0;
            for (int a = 0; a < components.length; a++) {
                int cx = 152 + q * 16;
                int cy = 24 + 16 * i;
                if (isHovering(cx, cy, 16, 16, mouseX, mouseY)) {
                    graphics.renderTooltip(font, components[a], mouseX, mouseY);
                    return;
                }
                if (++i > 3) {
                    i = 0;
                    ++q;
                }
            }
        }
    }
}

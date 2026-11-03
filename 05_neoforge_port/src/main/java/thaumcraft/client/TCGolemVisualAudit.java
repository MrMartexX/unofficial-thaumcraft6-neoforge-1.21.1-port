package thaumcraft.client;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import thaumcraft.api.golems.parts.*;
import thaumcraft.api.golems.seals.*;
import thaumcraft.client.gui.TCSealScreen;
import thaumcraft.common.entities.TCGolemEntity;
import thaumcraft.common.golems.GolemProperties;
import thaumcraft.common.golems.seals.*;
import thaumcraft.common.menu.TCSealMenu;
import thaumcraft.common.registry.TCEntityTypes;

/** Opt-in rendered evidence only. Use a disposable world, never a player save. */
@EventBusSubscriber(modid = "thaumcraft", value = Dist.CLIENT)
public final class TCGolemVisualAudit {
    private static boolean started;
    private static int phase, frames;
    private static boolean actionsSent;
    private static net.minecraft.core.BlockPos testPos;
    private static net.minecraft.world.level.block.state.BlockState supportBefore;
    private static final Path OUTPUT = Path.of("../../tools/reports/local/review-20261102/visual-seals-golems");

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        var mc = Minecraft.getInstance();
        if (!Boolean.getBoolean("tc.golemVisualAudit") || mc.level == null || mc.player == null) return;
        if (started) {
            if (phase == 10 && testPos != null && SealHandler.getSealEntity(mc.level, new SealPos(testPos, Direction.UP)) == null) {
                com.mojang.logging.LogUtils.getLogger().info("TC_GOLEM_VISUAL_AUDIT_CAPTURED phases=10 serverMenus=true ghostClicks=true cacheRemoval=true path={}", OUTPUT.toAbsolutePath());
                phase = 11;
                mc.stop();
            }
            return;
        }
        if (mc.screen != null) return;
        var server = mc.getSingleplayerServer();
        if (server == null || !server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).toAbsolutePath().normalize()
                .getFileName().toString().startsWith("tc_visual_")) throw new IllegalStateException("Visual audit must use a disposable integrated world");
        started = true;
        next(mc);
    }

    private static void next(Minecraft mc) {
        frames = 0;
        actionsSent = false;
        if (phase < 2) { mc.setScreen(new ModelSheet(phase)); return; }
        if (phase < 10) {
            mc.setScreen(null);
            mc.getSingleplayerServer().execute(() -> {
                var player = mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());
                if (player == null) throw new IllegalStateException("Visual audit player missing");
                var level = player.serverLevel();
                if (testPos == null) { testPos = player.blockPosition().east(2); supportBefore = level.getBlockState(testPos); }
                player.closeContainer();
                SealHandler.removeSealEntity(level, new SealPos(testPos, Direction.UP), true);
                level.setBlock(testPos, (phase == 8 ? net.minecraft.world.level.block.Blocks.CHEST : net.minecraft.world.level.block.Blocks.STONE).defaultBlockState(), 3);
                thaumcraft.api.golems.seals.ISeal prototype = switch (phase) {
                    case 5 -> new SealGuardAdvanced();
                    case 7 -> new SealUse();
                    case 8 -> new SealStock();
                    case 9 -> new SealPickupAdvanced();
                    default -> new SealPickup();
                };
                SealHandler.addSealEntity(level, testPos, Direction.UP, prototype, player);
                var seal = SealHandler.getSealEntity(level, new SealPos(testPos, Direction.UP));
                if (seal.getSeal() instanceof ISealConfigFilter filter) {
                    var icons = new net.minecraft.world.item.Item[]{net.minecraft.world.item.Items.IRON_INGOT, net.minecraft.world.item.Items.GOLD_INGOT,
                            net.minecraft.world.item.Items.COPPER_INGOT, net.minecraft.world.item.Items.AMETHYST_SHARD, net.minecraft.world.item.Items.DIAMOND,
                            net.minecraft.world.item.Items.EMERALD, net.minecraft.world.item.Items.REDSTONE, net.minecraft.world.item.Items.LAPIS_LAZULI, net.minecraft.world.item.Items.QUARTZ};
                    for (int i = 0; i < filter.getFilterSize(); i++) {
                        filter.setFilterSlot(i, new net.minecraft.world.item.ItemStack(icons[i % icons.length]));
                        if (filter.hasStacksizeLimiters()) filter.setFilterSlotSize(i, 15 + i);
                    }
                }
                seal.syncToClient(level);
                SealHandler.openSealGui(player, seal);
                var menu = (TCSealMenu)player.containerMenu;
                for (int i = 0; i < menu.categories.length; i++) if (menu.categories[i] == categoryForPhase()) menu.clickMenuButton(player, i);
            });
            return;
        }
        mc.getSingleplayerServer().execute(() -> {
            var player = mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());
            player.closeContainer();
            SealHandler.removeSealEntity(player.level(), new SealPos(testPos, Direction.UP), true);
            player.serverLevel().setBlock(testPos, supportBefore, 3);
        });
    }

    private static int categoryForPhase() { return phase < 7 ? phase - 2 : phase == 7 ? 3 : 1; }

    @SubscribeEvent
    public static void rendered(ScreenEvent.Render.Post event) {
        if (!started || !Boolean.getBoolean("tc.golemVisualAudit")) return;
        var mc = Minecraft.getInstance();
        if (!(event.getScreen() instanceof ModelSheet) && !(event.getScreen() instanceof TCSealScreen)) return;
        frames++;
        if (event.getScreen() instanceof TCSealScreen screen) {
            var menu = screen.getMenu();
            if (menu.category() != categoryForPhase()) { if (frames > 240) throw new IllegalStateException("Seal category snapshot timeout"); return; }
            if (frames >= 5 && !actionsSent) {
                actionsSent = true;
                switch (phase) {
                    case 2 -> {
                        mc.gameMode.handleInventoryButtonClick(menu.containerId, 81);
                        mc.gameMode.handleInventoryButtonClick(menu.containerId, 81);
                        mc.gameMode.handleInventoryButtonClick(menu.containerId, 83);
                        mc.gameMode.handleInventoryButtonClick(menu.containerId, 25);
                    }
                    case 4 -> mc.gameMode.handleInventoryButtonClick(menu.containerId, 93);
                    case 5 -> mc.gameMode.handleInventoryButtonClick(menu.containerId, 60);
                    case 7 -> mc.gameMode.handleInventoryButtonClick(menu.containerId, 36);
                    case 8 -> {
                        mc.gameMode.handleInventoryMouseClick(menu.containerId, 0, 0, net.minecraft.world.inventory.ClickType.PICKUP, mc.player);
                        mc.gameMode.handleInventoryMouseClick(menu.containerId, 0, 0, net.minecraft.world.inventory.ClickType.QUICK_MOVE, mc.player);
                    }
                    case 9 -> mc.gameMode.handleInventoryMouseClick(menu.containerId, 0, 1, net.minecraft.world.inventory.ClickType.PICKUP, mc.player);
                    default -> { }
                }
            }
            if (frames >= 30) {
                var cached = SealHandler.getSealEntity(mc.level, menu.seal.getSealPos());
                if (cached == null) { if (frames > 240) throw new IllegalStateException("Seal cache snapshot timeout"); return; }
                boolean valid = cached != null && cached.getOwner().equals(mc.player.getStringUUID());
                if (phase == 2) valid &= menu.seal.getPriority() == 2 && menu.seal.getColor() == 1 && menu.seal.isLocked()
                        && cached.getPriority() == 2 && cached.getColor() == 1 && cached.isLocked();
                if (phase == 4) valid &= menu.seal.getArea().getX() == 4 && cached.getArea().getX() == 4;
                if (phase == 5) valid &= !((ISealConfigToggles)menu.seal.getSeal()).getToggles()[0].getValue()
                        && !((ISealConfigToggles)cached.getSeal()).getToggles()[0].getValue();
                if (phase == 7) valid &= ((ISealConfigToggles)menu.seal.getSeal()).getToggles()[6].getValue()
                        && ((ISealConfigToggles)cached.getSeal()).getToggles()[6].getValue();
                if (phase == 8) valid &= ((ISealConfigFilter)menu.seal.getSeal()).getFilterSlotSize(0) == 26
                        && ((ISealConfigFilter)cached.getSeal()).getFilterSlotSize(0) == 26 && menu.getCarried().isEmpty();
                if (phase == 9) valid &= menu.slots.get(0).getItem().isEmpty()
                        && ((ISealConfigFilter)cached.getSeal()).getFilterSlot(0).isEmpty() && menu.getCarried().isEmpty();
                if (!valid) { if (frames > 240) throw new IllegalStateException("Seal menu/cache acknowledgement timeout"); return; }
            }
        }
        if (frames < 30) return;
        try {
            event.getGuiGraphics().flush();
            Files.createDirectories(OUTPUT);
            try (var image = Screenshot.takeScreenshot(mc.getMainRenderTarget())) {
                image.writeToFile(OUTPUT.resolve("phase-" + phase + ".png"));
            }
        } catch (java.io.IOException exception) { throw new IllegalStateException("Cannot write visual evidence", exception); }
        phase++;
        mc.execute(() -> next(mc));
    }

    private static final class ModelSheet extends Screen {
        private final List<TCGolemEntity> golems = new ArrayList<>();
        private final int view;
        ModelSheet(int view) {
            super(Component.empty());
            this.view = view;
            var mc = Minecraft.getInstance();
            var materials = GolemMaterial.getMaterials();
            for (int i = 0; i < materials.length; i++) {
                var props = new GolemProperties();
                props.setMaterial(materials[i]);
                props.setHead(GolemHead.getHeads()[i % GolemHead.getHeads().length]);
                props.setArms(GolemArm.getArms()[i % GolemArm.getArms().length]);
                props.setLegs(GolemLeg.getLegs()[i % GolemLeg.getLegs().length]);
                props.setAddon(GolemAddon.getAddons()[i % GolemAddon.getAddons().length]);
                var entity = new TCGolemEntity(TCEntityTypes.GOLEM.get(), mc.level);
                entity.setProperties(props);
                entity.setGolemColor((byte)(i + 1));
                golems.add(entity);
            }
        }
        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial) {
            graphics.fill(0, 0, width, height, 0xFF353B40);
            int cellWidth = width / 3, cellHeight = height / 2;
            for (int i = 0; i < golems.size(); i++) {
                int x = i % 3 * cellWidth, y = i / 3 * cellHeight;
                InventoryScreen.renderEntityInInventoryFollowsAngle(graphics, x + 5, y + 5, x + cellWidth - 5, y + cellHeight - 20,
                        Math.min(cellWidth, cellHeight) / 2, .1F, view == 0 ? 0 : .7F, .15F, golems.get(i));
                graphics.drawString(font, golems.get(i).getProperties().getMaterial().getLocalizedName(), x + 8, y + cellHeight - 16, 0xFFFFFF, false);
            }
        }
    }
}

package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.joml.Matrix4f;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.*;
import thaumcraft.common.registry.TCEntityTypes;

/** Opt-in renderer contracts and captures, not a certificate of matching legacy pixels. */
@EventBusSubscriber(modid = Thaumcraft.MODID, value = Dist.CLIENT)
public final class TCDonorEntityVisualAudit {
    private static final String WORLD = System.getProperty("tc.donorVisualAuditWorld", "");
    private static final Path OUTPUT = Path.of(System.getProperty("tc.donorVisualAuditOutput",
            "../../tools/reports/local/donor-visual-20261105"));
    private static final List<Sample> SAMPLES = new ArrayList<>();
    private static boolean opening, started, finished;
    private static int view, frames;

    private TCDonorEntityVisualAudit() {}

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if (WORLD.isEmpty() || finished) return;
        var mc = Minecraft.getInstance();
        if (!WORLD.matches("tc_visual_donor_[A-Za-z0-9_]+"))
            throw new IllegalStateException("Donor audit requires a disposable world name");
        if (!opening && mc.screen instanceof TitleScreen) {
            opening = true;
            mc.getWindow().setTitle("Thaumcraft donor model audit - closes automatically");
            mc.createWorldOpenFlows().openWorld(WORLD, () -> {
                throw new IllegalStateException("Cannot open disposable donor fixture");
            });
        }
        if (started || mc.level == null || mc.player == null || mc.screen != null) return;
        var server = mc.getSingleplayerServer();
        if (server == null || !server.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize()
                .getFileName().toString().equals(WORLD))
            throw new IllegalStateException("Never run the donor audit on a player save or remote server");
        started = true;
        prepare();
        checkContracts();
        mc.setScreen(new Sheet());
    }

    private static <T extends LivingEntity> T add(String name, EntityType<T> type, int vertices) {
        T entity = type.create(Minecraft.getInstance().level);
        if (entity == null) throw new IllegalStateException("Missing audit entity " + name);
        entity.tickCount = 80;
        SAMPLES.add(new Sample(name, entity, vertices));
        return entity;
    }

    private static void prepare() {
        // Counts follow the original models' boxes, not the newly imported Java classes.
        add("Crab / bare", TCEntityTypes.ELDRITCH_CRAB.get(), 20 * 24).setHelm(false);
        add("Crab / helm", TCEntityTypes.ELDRITCH_CRAB.get(), 20 * 24).setHelm(true);
        add("Firebat / flight", TCEntityTypes.FIREBAT.get(), 9 * 24).setResting(false);
        add("Firebat / hanging", TCEntityTypes.FIREBAT.get(), 9 * 24).setResting(true);
        add("Taint seed", TCEntityTypes.TAINT_SEED.get(), 8 * 24);
        add("Prime seed", TCEntityTypes.TAINT_SEED_PRIME.get(), 8 * 24);
        add("Taintacle / 10", TCEntityTypes.TAINTACLE.get(), 12 * 24);
        add("Tendril / 6", TCEntityTypes.TAINTACLE_TINY.get(), 8 * 24);
        add("Giant / 14", TCEntityTypes.TAINTACLE_GIANT.get(), 16 * 24);
        for (var type : TCPechEntity.PechType.values()) {
            add("Pech / " + type.name(), TCEntityTypes.PECH.get(), 9 * 24).setPechType(type);
        }
    }

    private static Counts collect(LivingEntity entity) {
        var mc = Minecraft.getInstance();
        var renderer = mc.getEntityRenderDispatcher().getRenderer(entity);
        if (mc.getResourceManager().getResource(renderer.getTextureLocation(entity)).isEmpty())
            throw new IllegalStateException("Missing runtime texture " + renderer.getTextureLocation(entity));
        var counts = new Counts();
        var pose = new PoseStack();
        var before = new Matrix4f(pose.last().pose());
        renderer.render(entity, 0, 0.5F, pose, counts, LightTexture.FULL_BRIGHT);
        if (!pose.clear() || !before.equals(pose.last().pose(), 0.00001F))
            throw new IllegalStateException("Renderer leaked its pose stack");
        return counts;
    }

    private static void checkContracts() {
        var report = new ArrayList<String>();
        for (Sample sample : SAMPLES) {
            Counts counts = collect(sample.entity);
            if (counts.vertices != sample.vertices || counts.maxY - counts.minY < 0.05F)
                throw new IllegalStateException(sample.name + ": expected " + sample.vertices + " vertices, got " + counts.vertices);
            report.add(sample.name + " PASS vertices=" + counts.vertices + " height=" + (counts.maxY - counts.minY));
        }
        // Shared model instances must not keep another entity's helm or hanging pose.
        for (int index : new int[] {0, 2}) {
            long initial = collect(SAMPLES.get(index).entity).hash;
            collect(SAMPLES.get(index + 1).entity);
            if (collect(SAMPLES.get(index).entity).hash != initial)
                throw new IllegalStateException("Cross-entity pose leak: " + SAMPLES.get(index).name);
        }
        TCPechEntity pech = (TCPechEntity) SAMPLES.get(11).entity;
        pech.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        if (collect(pech).vertices <= 9 * 24) throw new IllegalStateException("Pech held item missing");
        pech.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        if (collect(pech).vertices != 9 * 24) throw new IllegalStateException("Pech held item leaked");
        // Render all three real held-item variants in the captures, after the bare-model assertions.
        ((TCPechEntity) SAMPLES.get(9).entity).setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_PICKAXE));
        ((TCPechEntity) SAMPLES.get(10).entity).setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STICK));
        pech.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        report.add("PASS no cross-entity pose leak; Pech held-item add/remove; pose stacks balanced; finite vertices/UV/normals");
        checkChest(report);
        try {
            Files.createDirectories(OUTPUT);
            Files.write(OUTPUT.resolve("model-contracts.txt"), report);
        } catch (java.io.IOException exception) { throw new IllegalStateException(exception); }
        Thaumcraft.LOGGER.info("TC_DONOR_ENTITY_CONTRACT_PASS variants={}", SAMPLES.size());
    }

    private static void checkChest(List<String> report) {
        var mc = Minecraft.getInstance();
        var state = thaumcraft.common.registry.TCBlocks.HUNGRY_CHEST.get().defaultBlockState();
        var chest = new thaumcraft.common.tiles.devices.TCHungryChestBlockEntity(net.minecraft.core.BlockPos.ZERO, state);
        chest.setLevel(mc.level);
        var renderer = mc.getBlockEntityRenderDispatcher().getRenderer(chest);
        if (!(renderer instanceof TCHungryChestRenderer)
                || state.getRenderShape() != net.minecraft.world.level.block.RenderShape.ENTITYBLOCK_ANIMATED)
            throw new IllegalStateException("Hungry Chest has no renderer or has duplicate baked world geometry");
        var closed = new Counts();
        renderer.render(chest, 1, new PoseStack(), closed, LightTexture.FULL_BRIGHT, 0);
        if (!chest.triggerEvent(1, 1)) throw new IllegalStateException("Missing opener event");
        for (int i = 0; i < 10; i++)
            thaumcraft.common.tiles.devices.TCHungryChestBlockEntity.tick(mc.level, chest.getBlockPos(), state, chest);
        if (Math.abs(chest.getOpenNess(1) - 1) > 0.0001F
                || Math.abs(chest.getOpenNess(0.5F) - 0.95F) > 0.0001F)
            throw new IllegalStateException("Hungry Chest lid cadence/interpolation changed");
        var opened = new Counts();
        renderer.render(chest, 1, new PoseStack(), opened, LightTexture.FULL_BRIGHT, 0);
        if (closed.vertices != 72 || opened.vertices != 72 || closed.hash == opened.hash)
            throw new IllegalStateException("Hungry Chest lid geometry does not animate");
        chest.triggerEvent(1, 0);
        for (int i = 0; i < 10; i++)
            thaumcraft.common.tiles.devices.TCHungryChestBlockEntity.tick(mc.level, chest.getBlockPos(), state, chest);
        if (chest.getOpenNess(1) > 0.0001F) throw new IllegalStateException("Hungry Chest lid did not close");
        var item = new ItemStack(thaumcraft.common.registry.TCBlocks.HUNGRY_CHEST.get());
        var baked = mc.getItemRenderer().getModel(item, mc.level, mc.player, 0);
        if (baked.getQuads(null, null, net.minecraft.util.RandomSource.create()).size() != 18)
            throw new IllegalStateException("Hungry Chest inventory model is not the original three cuboids");
        report.add("Hungry Chest PASS event-driven 10-tick lid/interpolation; 72 animated vertices; 18 inventory quads; no double world model");
    }

    @SubscribeEvent
    public static void rendered(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof Sheet) || finished || ++frames < 30) return;
        var mc = Minecraft.getInstance();
        try {
            event.getGuiGraphics().flush();
            try (var screenshot = Screenshot.takeScreenshot(mc.getMainRenderTarget())) {
                screenshot.writeToFile(OUTPUT.resolve("models-" + view + ".png"));
            }
        } catch (java.io.IOException exception) { throw new IllegalStateException(exception); }
        frames = 0;
        if (++view == 3) {
            finished = true;
            SAMPLES.forEach(sample -> sample.entity.discard());
            Thaumcraft.LOGGER.info("TC_DONOR_ENTITY_AUDIT_CAPTURED variants={} views=3 path={}", SAMPLES.size(), OUTPUT.toAbsolutePath());
            mc.execute(mc::stop);
        }
    }

    private record Sample(String name, LivingEntity entity, int vertices) {}

    private static final class Sheet extends Screen {
        Sheet() { super(Component.empty()); }
        @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial) {
            graphics.fill(0, 0, width, height, 0xFF353B40);
            int w = width / 4, h = height / 3;
            for (int i = 0; i < SAMPLES.size(); i++) {
                Sample sample = SAMPLES.get(i);
                int x = i % 4 * w, y = i / 4 * h;
                int scale = (int) (Math.min(w * 0.50F, h * 0.63F) / Math.max(1.3F, sample.entity.getBbHeight()));
                if (sample.entity instanceof TCFirebatEntity) scale = (int) (h * 0.8F);
                InventoryScreen.renderEntityInInventoryFollowsAngle(graphics, x + 4, y + 4, x + w - 4, y + h - 15,
                        Math.max(6, scale), .05F, view == 0 ? 0 : view == 1 ? .8F : -.8F, .12F, sample.entity);
                graphics.drawString(font, sample.name, x + 5, y + h - 12, 0xFFFFFF, false);
            }
        }
    }

    private static final class Counts implements MultiBufferSource, VertexConsumer {
        int vertices;
        long hash = 1;
        float minY = Float.POSITIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;
        @Override public VertexConsumer getBuffer(RenderType type) { return this; }
        @Override public VertexConsumer addVertex(float x, float y, float z) {
            finite(x, y, z);
            vertices++;
            minY = Math.min(minY, y); maxY = Math.max(maxY, y);
            hash = 31 * hash + Float.floatToIntBits(x);
            hash = 31 * hash + Float.floatToIntBits(y);
            hash = 31 * hash + Float.floatToIntBits(z);
            return this;
        }
        @Override public VertexConsumer setColor(int r, int g, int b, int a) { return this; }
        @Override public VertexConsumer setUv(float u, float v) { finite(u, v, 0); return this; }
        @Override public VertexConsumer setUv1(int u, int v) { return this; }
        @Override public VertexConsumer setUv2(int u, int v) { return this; }
        @Override public VertexConsumer setNormal(float x, float y, float z) { finite(x, y, z); return this; }
        private static void finite(float x, float y, float z) {
            if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(z))
                throw new IllegalStateException("Nonfinite renderer vertex attribute");
        }
    }
}

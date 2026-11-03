package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.entities.TCGolemEntity;
import thaumcraft.common.golems.GolemProperties;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;

/** Opt-in real renderer vertex contract; does not claim matching legacy pixels. */
public final class TCGolemClientRenderAudit {
    private TCGolemClientRenderAudit() {}
    public static void run() {
        checkDepthState();
        var mc = Minecraft.getInstance();
        var player = mc.player;
        ItemStack held = player.getMainHandItem().copy();
        boolean crouching = player.isShiftKeyDown();
        boolean inputCrouching = player.input.shiftKeyDown;
        var golem = new TCGolemEntity(TCEntityTypes.GOLEM.get(), mc.level);
        golem.setProperties(new GolemProperties());
        golem.setPos(player.getX(), mc.level.getMinBuildHeight() - 10, player.getZ());
        var holder = new net.minecraft.world.entity.decoration.ArmorStand(mc.level, golem.getX() + 1, golem.getY(), golem.getZ());
        try {
            if (player.hasLineOfSight(golem)) throw new IllegalStateException("Reveal fixture must be occluded by terrain");
            var renderer = mc.getEntityRenderDispatcher().getRenderer(golem);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(TCItems.GOLEM_BELL.get()));
            player.setShiftKeyDown(true);
            player.input.shiftKeyDown = true;
            var revealed = new Counts();
            renderer.render(golem, 0, 0, new PoseStack(), revealed, LightTexture.FULL_BRIGHT);
            if (revealed.reveal == 0 || !revealed.revealColor) throw new IllegalStateException("Missing legacy gray/alpha.25 reveal geometry: vertices=" + revealed.reveal + ", color=" + revealed.revealColor + ", shift=" + player.isShiftKeyDown());
            player.setShiftKeyDown(false);
            player.input.shiftKeyDown = false;
            var standing = new Counts();
            renderer.render(golem, 0, 0, new PoseStack(), standing, LightTexture.FULL_BRIGHT);
            if (standing.reveal != 0 || standing.normal == 0) throw new IllegalStateException("Reveal leaked into standing render");
            player.setShiftKeyDown(true);
            player.input.shiftKeyDown = true;
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            var empty = new Counts();
            renderer.render(golem, 0, 0, new PoseStack(), empty, LightTexture.FULL_BRIGHT);
            if (empty.reveal != 0) throw new IllegalStateException("Reveal without a seal displayer");
            golem.setLeashedTo(holder, false);
            var leashed = new Counts();
            renderer.render(golem, 0, 0, new PoseStack(), leashed, LightTexture.FULL_BRIGHT);
            if (leashed.leash != 100) throw new IllegalStateException("Vanilla leash geometry missing: " + leashed.leash);
            var dart = new thaumcraft.common.entities.TCGolemDartEntity(TCEntityTypes.GOLEM_DART.get(), mc.level);
            var dartGeometry = new Counts();
            try {
                var dartRenderer = mc.getEntityRenderDispatcher().getRenderer(dart);
                if (!(dartRenderer instanceof TCGolemDartRenderer) || mc.getResourceManager().getResource(dartRenderer.getTextureLocation(dart)).isEmpty())
                    throw new IllegalStateException("Missing real dart renderer/texture");
                dartRenderer.render(dart, 0, 0, new PoseStack(), dartGeometry, LightTexture.FULL_BRIGHT);
                float span = Math.max(dartGeometry.maxX - dartGeometry.minX, Math.max(dartGeometry.maxY - dartGeometry.minY, dartGeometry.maxZ - dartGeometry.minZ));
                if (dartGeometry.normal != 24 || Math.abs(span - .528F) > .0001F)
                    throw new IllegalStateException("Dart geometry/scale differs: vertices=" + dartGeometry.normal + ", span=" + span);
            } finally { dart.discard(); }
            com.mojang.logging.LogUtils.getLogger().info("TC_GOLEM_RENDER_CONTRACT_PASS revealVertices={} leashVertices={} noLeak=true depthState=true dartVertices=24", revealed.reveal, leashed.leash);
        } finally {
            player.setItemInHand(InteractionHand.MAIN_HAND, held);
            player.setShiftKeyDown(crouching);
            player.input.shiftKeyDown = inputCrouching;
            golem.discard(); holder.discard();
        }
    }

    private static void checkDepthState() {
        boolean enabled = org.lwjgl.opengl.GL11.glIsEnabled(org.lwjgl.opengl.GL11.GL_DEPTH_TEST);
        int function = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL11.GL_DEPTH_FUNC);
        try {
            TCGolemRenderTypes.REVEAL_DEPTH.setupRenderState();
            if (!org.lwjgl.opengl.GL11.glIsEnabled(org.lwjgl.opengl.GL11.GL_DEPTH_TEST)
                    || org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL11.GL_DEPTH_FUNC) != 519)
                throw new IllegalStateException("Reveal did not establish unconditional GPU depth acceptance");
            TCGolemRenderTypes.REVEAL_DEPTH.clearRenderState();
            if (org.lwjgl.opengl.GL11.glIsEnabled(org.lwjgl.opengl.GL11.GL_DEPTH_TEST)
                    || org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL11.GL_DEPTH_FUNC) != 515)
                throw new IllegalStateException("Reveal did not restore the RenderType depth baseline");
        } finally {
            if (enabled) com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();
            else com.mojang.blaze3d.systems.RenderSystem.disableDepthTest();
            com.mojang.blaze3d.systems.RenderSystem.depthFunc(function);
        }
    }

    private static final class Counts implements MultiBufferSource {
        int reveal, normal, leash;
        boolean revealColor = true;
        float minX = Float.POSITIVE_INFINITY, minY = Float.POSITIVE_INFINITY, minZ = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY, maxZ = Float.NEGATIVE_INFINITY;
        @Override public VertexConsumer getBuffer(RenderType type) {
            boolean hidden = type.toString().contains("tc_golem_reveal");
            boolean rope = type == RenderType.leash();
            return new VertexConsumer() {
                @Override public VertexConsumer addVertex(float x, float y, float z) {
                    if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(z)) throw new IllegalStateException("Nonfinite golem vertex");
                    minX = Math.min(minX, x); minY = Math.min(minY, y); minZ = Math.min(minZ, z);
                    maxX = Math.max(maxX, x); maxY = Math.max(maxY, y); maxZ = Math.max(maxZ, z);
                    if (hidden) reveal++; else if (rope) leash++; else normal++;
                    return this;
                }
                @Override public VertexConsumer setColor(int r, int g, int b, int a) { if (hidden) revealColor &= r == 64 && g == 64 && b == 64 && a == 64; return this; }
                @Override public VertexConsumer setUv(float u, float v) { return this; }
                @Override public VertexConsumer setUv1(int u, int v) { return this; }
                @Override public VertexConsumer setUv2(int u, int v) { return this; }
                @Override public VertexConsumer setNormal(float x, float y, float z) { return this; }
            };
        }
    }
}

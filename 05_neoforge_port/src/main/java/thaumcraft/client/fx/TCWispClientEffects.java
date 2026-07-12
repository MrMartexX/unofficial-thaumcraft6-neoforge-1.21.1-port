package thaumcraft.client.fx;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.TCWispEntity;
import thaumcraft.common.entities.TCWispZapPayload;

public final class TCWispClientEffects {
    private static final ResourceLocation ESSENTIA_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/essentia.png");
    private static final List<ZapBolt> ZAPS = new ArrayList<>();

    private TCWispClientEffects() {
    }

    public static void accept(TCWispZapPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) {
            return;
        }
        Entity source = getEntityById(minecraft, level, payload.sourceEntityId());
        Entity target = getEntityById(minecraft, level, payload.targetEntityId());
        if (source == null || target == null) {
            return;
        }

        float red = 1.0F;
        float green = 1.0F;
        float blue = 1.0F;
        if (source instanceof TCWispEntity wisp) {
            Aspect aspect = Aspect.getAspect(wisp.getWispType());
            if (aspect != null) {
                int color = aspect.getColor();
                red = ((color >> 16) & 0xFF) / 255.0F;
                green = ((color >> 8) & 0xFF) / 255.0F;
                blue = (color & 0xFF) / 255.0F;
            }
        }

        ZAPS.add(new ZapBolt(source.position(), target.position(), red, green, blue, 0.6F, level.random.nextLong()));
    }

    public static void clear() {
        ZAPS.clear();
    }

    public static void tick() {
        Iterator<ZapBolt> iterator = ZAPS.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().tick()) {
                iterator.remove();
            }
        }
    }

    public static void render(Camera camera, float partialTick) {
        if (ZAPS.isEmpty()) {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, ESSENTIA_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        PoseStack poseStack = new PoseStack();
        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        Vec3 cameraPos = camera.getPosition();
        for (ZapBolt zap : ZAPS) {
            zap.render(buffer, matrix, cameraPos, partialTick);
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
    }

    private static Entity getEntityById(Minecraft minecraft, Level level, int id) {
        if (minecraft.player != null && minecraft.player.getId() == id) {
            return minecraft.player;
        }
        return level.getEntity(id);
    }

    private static final class ZapBolt {
        private final Vec3 start;
        private final Vec3 end;
        private final float red;
        private final float green;
        private final float blue;
        private final float width;
        private final float length;
        private final float phase;
        private final long seed;
        private int age;

        private ZapBolt(Vec3 start, Vec3 end, float red, float green, float blue, float width, long seed) {
            this.start = start;
            this.end = end;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.width = width;
            this.length = (float) (end.subtract(start).length() * Math.PI);
            this.phase = (float) (Math.floorMod(seed, 50L) * Math.PI);
            this.seed = seed;
        }

        private boolean tick() {
            age++;
            return age >= 3;
        }

        private void render(BufferBuilder buffer, Matrix4f matrix, Vec3 cameraPos, float partialTick) {
            List<Vec3> points = points(partialTick);
            if (points.size() < 2) {
                return;
            }
            float alpha = Mth.clamp(1.0F - age / 3.0F, 0.1F, 1.0F);
            for (int index = 0; index < points.size() - 1; index++) {
                Vec3 first = points.get(index);
                Vec3 second = points.get(index + 1);
                double radius = ((index & 3) == 0 ? Math.max(0.0F, 1.0F - age * 0.25F) : 1.0F) * width / 10.0D;
                addSegment(buffer, matrix, first, second, cameraPos, radius, alpha, index / (float) points.size());
                addSegment(buffer, matrix, first, second, cameraPos, radius / 3.0D, alpha, index / (float) points.size());
            }
        }

        private List<Vec3> points(float partialTick) {
            Vec3 delta = end.subtract(start);
            int steps = Math.max(2, (int) length);
            ArrayList<Vec3> points = new ArrayList<>(steps + 1);
            points.add(start);
            Random random = new Random(seed);
            float amplitude = (age + partialTick) / 10.0F;
            for (int index = 1; index < steps - 1; index++) {
                float dist = index * (length / steps) + phase;
                double x = start.x + delta.x / steps * index + Mth.sin(dist / 4.0F) * amplitude;
                double y = start.y + delta.y / steps * index + Mth.sin(dist / 3.0F) * amplitude;
                double z = start.z + delta.z / steps * index + Mth.sin(dist / 2.0F) * amplitude;
                x += (random.nextFloat() - random.nextFloat()) * 0.1F;
                y += (random.nextFloat() - random.nextFloat()) * 0.1F;
                z += (random.nextFloat() - random.nextFloat()) * 0.1F;
                points.add(new Vec3(x, y, z));
            }
            points.add(end);
            return points;
        }

        private void addSegment(
                BufferBuilder buffer,
                Matrix4f matrix,
                Vec3 first,
                Vec3 second,
                Vec3 cameraPos,
                double radius,
                float alpha,
                float u
        ) {
            Vec3 direction = second.subtract(first);
            Vec3 midpoint = first.add(second).scale(0.5D);
            Vec3 normal = direction.cross(cameraPos.subtract(midpoint));
            if (normal.lengthSqr() < 1.0E-8D) {
                normal = new Vec3(0.0D, 1.0D, 0.0D);
            }
            normal = normal.normalize().scale(radius);
            vertex(buffer, matrix, first.add(normal).subtract(cameraPos), u, 0.0F, alpha);
            vertex(buffer, matrix, second.add(normal).subtract(cameraPos), u + 0.1F, 0.0F, alpha);
            vertex(buffer, matrix, second.subtract(normal).subtract(cameraPos), u + 0.1F, 1.0F, alpha);
            vertex(buffer, matrix, first.subtract(normal).subtract(cameraPos), u, 1.0F, alpha);
        }

        private void vertex(BufferBuilder buffer, Matrix4f matrix, Vec3 point, float u, float v, float alpha) {
            buffer.addVertex(matrix, (float) point.x, (float) point.y, (float) point.z)
                    .setUv(u, v)
                    .setColor(red, green, blue, alpha);
        }
    }
}

package thaumcraft.client.fx;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import thaumcraft.Thaumcraft;
import thaumcraft.common.crafting.infusion.TCInfusionClientFXCache;
import thaumcraft.common.crafting.infusion.TCInfusionEssentiaSourcePayload;
import thaumcraft.common.crafting.infusion.TCInfusionSourcePayload;
import thaumcraft.common.lib.fx.TCFXDispatcher;
import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;
import thaumcraft.common.tiles.crafting.TCInfusionPedestalBlockEntity;

/** Client-only legacy infusion streams and source debris. */
public final class TCInfusionClientEffects {
    private static final int LEGACY_DEFAULT_SOURCE_LIFETIME = 15;
    private static final int LEGACY_PEDESTAL_SOURCE_LIFETIME = 60;
    private static final int STREAM_SIDES = 8;
    private static final ResourceLocation ESSENTIA_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/essentia.png");
    private static final ResourceLocation PARTICLE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/particles.png");
    private static final Map<StreamKey, EssentiaStream> STREAMS = new LinkedHashMap<>();
    private static final Map<SourceKey, SourceEffect> SOURCES = new LinkedHashMap<>();
    private static final Map<BlockPos, Integer> CRAFTING_HALOS = new LinkedHashMap<>();
    private static final List<BoreDebris> DEBRIS = new ArrayList<>();
    private static final List<BoreSparkle> SOURCE_SPARKLES = new ArrayList<>();
    private static int tickCount;

    private TCInfusionClientEffects() {
    }

    public static void tick(Level level) {
        if (level == null || !level.isClientSide()) {
            return;
        }
        tickCount++;
        acceptQueuedPayloads(level);
        tickStreams(level);
        tickSources(level);
        DEBRIS.removeIf(debris -> !debris.tick());
        SOURCE_SPARKLES.removeIf(sparkle -> !sparkle.tick());
    }

    public static void render(Camera camera, float partialTick) {
        if (camera == null) {
            return;
        }
        renderCraftingHalos(camera);
        renderStreams(camera, partialTick);
        renderDebris(camera, partialTick);
        renderSourceSparkles(camera, partialTick);
    }

    /** Called by the matrix BER so the legacy halo can be drawn in the level transparency pass. */
    public static void noteCraftingMatrix(BlockPos position, int craftCount) {
        if (position != null && craftCount > 0) {
            CRAFTING_HALOS.put(position.immutable(), craftCount);
        }
    }

    public static void clear() {
        STREAMS.clear();
        SOURCES.clear();
        CRAFTING_HALOS.clear();
        DEBRIS.clear();
        SOURCE_SPARKLES.clear();
        tickCount = 0;
    }

    public static int activeStreamCount() {
        return STREAMS.size();
    }

    public static int activeSourceCount() {
        return SOURCES.size();
    }

    private static void acceptQueuedPayloads(Level level) {
        for (TCInfusionEssentiaSourcePayload payload : TCInfusionClientFXCache.drainEssentiaTrails()) {
            if (!(level.getBlockEntity(payload.matrixPos()) instanceof TCInfusionMatrixBlockEntity)) {
                continue;
            }
            BlockPos destination = payload.matrixPos().below();
            StreamKey key = new StreamKey(payload.sourcePos(), destination, payload.color());
            EssentiaStream existing = STREAMS.get(key);
            if (existing == null || existing.removed) {
                STREAMS.put(key, new EssentiaStream(level, payload.sourcePos(), destination, payload.color(), payload.extension(), tickCount));
            } else {
                existing.extend(payload.extension());
            }
        }
        for (TCInfusionSourcePayload payload : TCInfusionClientFXCache.drainSources()) {
            if (!(level.getBlockEntity(payload.matrixPos()) instanceof TCInfusionMatrixBlockEntity)) {
                continue;
            }
            SourceKey key = new SourceKey(payload.matrixPos(), payload.targetPos(), payload.color());
            int lifetime = level.getBlockEntity(payload.targetPos()) instanceof TCInfusionPedestalBlockEntity
                    ? LEGACY_PEDESTAL_SOURCE_LIFETIME
                    : LEGACY_DEFAULT_SOURCE_LIFETIME;
            SourceEffect existing = SOURCES.get(key);
            if (existing == null) {
                SOURCES.put(key, new SourceEffect(payload, lifetime));
            } else {
                existing.ticks = lifetime;
            }
        }
    }

    private static void tickStreams(Level level) {
        Iterator<EssentiaStream> iterator = STREAMS.values().iterator();
        while (iterator.hasNext()) {
            EssentiaStream stream = iterator.next();
            stream.tick(level);
            if (stream.removed) {
                iterator.remove();
            }
        }
    }

    private static void tickSources(Level level) {
        Iterator<SourceEffect> iterator = SOURCES.values().iterator();
        while (iterator.hasNext()) {
            SourceEffect source = iterator.next();
            if (source.ticks-- <= 0
                    || !(level.getBlockEntity(source.payload.matrixPos()) instanceof TCInfusionMatrixBlockEntity)) {
                iterator.remove();
                continue;
            }
            if (source.payload.targetPos().equals(source.payload.matrixPos())) {
                Entity entity = level.getEntity(source.payload.color());
                if (entity == null) {
                    iterator.remove();
                } else {
                    spawnEntitySource(level, entity, source.payload.matrixPos());
                }
                continue;
            }
            if (!(level.getBlockEntity(source.payload.targetPos()) instanceof TCInfusionPedestalBlockEntity pedestal)) {
                iterator.remove();
                continue;
            }
            ItemStack stack = pedestal.getStoredStack();
            if (!stack.isEmpty()) {
                spawnPedestalSource(level, source.payload.targetPos(), source.payload.matrixPos(), stack);
            }
        }
    }

    private static void spawnEntitySource(Level level, Entity entity, BlockPos matrixPos) {
        AABB box = entity.getBoundingBox();
        RandomSource random = level.random;
        for (int index = 0; index < 4; index++) {
            double x = entity.getX() + (random.nextFloat() - random.nextFloat()) * entity.getBbWidth();
            double y = box.minY + random.nextFloat() * entity.getBbHeight();
            double z = entity.getZ() + (random.nextFloat() - random.nextFloat()) * entity.getBbWidth();
            spawnSparkle(level, x, y, z, matrixPos, 0.2F, 0.6F + random.nextFloat() * 0.3F, 0.3F);
        }
    }

    private static void spawnPedestalSource(Level level, BlockPos pedestalPos, BlockPos matrixPos, ItemStack stack) {
        RandomSource random = level.random;
        if (random.nextInt(3) == 0) {
            spawnSparkle(
                    level,
                    pedestalPos.getX() + random.nextFloat(),
                    pedestalPos.getY() + 1.0F + random.nextFloat(),
                    pedestalPos.getZ() + random.nextFloat(),
                    matrixPos,
                    0.4F + random.nextFloat() * 0.2F,
                    0.2F,
                    0.6F + random.nextFloat() * 0.3F
            );
            return;
        }
        for (int index = 0; index < 4; index++) {
            boolean block = stack.getItem() instanceof BlockItem;
            double x = pedestalPos.getX() + (block ? random.nextFloat() : 0.4F + random.nextFloat() * 0.2F);
            double y = pedestalPos.getY() + (block ? 1.0F + random.nextFloat() : 1.23F + random.nextFloat() * 0.2F);
            double z = pedestalPos.getZ() + (block ? random.nextFloat() : 0.4F + random.nextFloat() * 0.2F);
            DEBRIS.add(new BoreDebris(level, x, y, z, matrixPos, stack));
        }
    }

    private static void spawnSparkle(
            Level level,
            double x,
            double y,
            double z,
            BlockPos matrixPos,
            float red,
            float green,
            float blue
    ) {
        SOURCE_SPARKLES.add(new BoreSparkle(
                level,
                new Vec3(x, y, z),
                new Vec3(matrixPos.getX() + 0.5D, matrixPos.getY() - 0.5D, matrixPos.getZ() + 0.5D),
                red,
                green,
                blue
        ));
    }

    private static void renderCraftingHalos(Camera camera) {
        if (CRAFTING_HALOS.isEmpty()) {
            return;
        }
        int rayCount = Minecraft.getInstance().options.graphicsMode().get() == GraphicsStatus.FAST ? 10 : 20;
        Vec3 cameraPos = camera.getPosition();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.enableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        for (Map.Entry<BlockPos, Integer> entry : CRAFTING_HALOS.entrySet()) {
            addCraftingHalo(buffer, cameraPos, entry.getKey(), entry.getValue(), rayCount);
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        CRAFTING_HALOS.clear();
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private static void addCraftingHalo(
            BufferBuilder buffer,
            Vec3 cameraPos,
            BlockPos position,
            int craftCount,
            int rayCount
    ) {
        float progress = Math.min(craftCount, 50) / 50.0F;
        float centerAlpha = Mth.clamp(1.0F - craftCount / 500.0F, 0.0F, 1.0F);
        float centerX = (float) (position.getX() + 0.5D - cameraPos.x());
        float centerY = (float) (position.getY() + 0.5D - cameraPos.y());
        float centerZ = (float) (position.getZ() + 0.5D - cameraPos.z());
        Random random = new Random(245L);
        Matrix4f rotation = new Matrix4f();
        for (int index = 0; index < rayCount; index++) {
            rotation.rotateX((float) Math.toRadians(random.nextFloat() * 360.0F));
            rotation.rotateY((float) Math.toRadians(random.nextFloat() * 360.0F));
            rotation.rotateZ((float) Math.toRadians(random.nextFloat() * 360.0F));
            rotation.rotateX((float) Math.toRadians(random.nextFloat() * 360.0F));
            rotation.rotateY((float) Math.toRadians(random.nextFloat() * 360.0F));
            rotation.rotateZ((float) Math.toRadians(random.nextFloat() * 360.0F + craftCount / 500.0F * 360.0F));
            float length = (random.nextFloat() * 20.0F + 5.0F) * progress / 20.0F;
            float width = (random.nextFloat() * 2.0F + 1.0F) * progress / 20.0F;
            Vector3f first = haloPoint(-0.866F * width, length, -0.5F * width, rotation, centerX, centerY, centerZ);
            Vector3f second = haloPoint(0.866F * width, length, -0.5F * width, rotation, centerX, centerY, centerZ);
            Vector3f third = haloPoint(0.0F, length, width, rotation, centerX, centerY, centerZ);
            addHaloTriangle(buffer, centerX, centerY, centerZ, centerAlpha, first, second);
            addHaloTriangle(buffer, centerX, centerY, centerZ, centerAlpha, second, third);
            addHaloTriangle(buffer, centerX, centerY, centerZ, centerAlpha, third, first);
        }
    }

    private static Vector3f haloPoint(
            float x,
            float y,
            float z,
            Matrix4f rotation,
            float centerX,
            float centerY,
            float centerZ
    ) {
        return new Vector3f(x, y, z).mulPosition(rotation).add(centerX, centerY, centerZ);
    }

    private static void addHaloTriangle(
            BufferBuilder buffer,
            float centerX,
            float centerY,
            float centerZ,
            float centerAlpha,
            Vector3f first,
            Vector3f second
    ) {
        buffer.addVertex(centerX, centerY, centerZ).setColor(1.0F, 1.0F, 1.0F, centerAlpha);
        buffer.addVertex(first.x(), first.y(), first.z()).setColor(1.0F, 0.0F, 1.0F, 0.0F);
        buffer.addVertex(second.x(), second.y(), second.z()).setColor(1.0F, 0.0F, 1.0F, 0.0F);
    }

    private static void renderStreams(Camera camera, float partialTick) {
        if (STREAMS.values().stream().noneMatch(EssentiaStream::canRender)) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, ESSENTIA_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        int vertices = 0;
        for (EssentiaStream stream : STREAMS.values()) {
            vertices += stream.render(buffer, camera, partialTick);
        }
        if (vertices > 0) {
            BufferUploader.drawWithShader(buffer.buildOrThrow());
        }
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private static void renderDebris(Camera camera, float partialTick) {
        if (DEBRIS.isEmpty()) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        for (BoreDebris debris : DEBRIS) {
            debris.render(buffer, camera, partialTick);
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private static void renderSourceSparkles(Camera camera, float partialTick) {
        if (SOURCE_SPARKLES.isEmpty()) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, PARTICLE_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        for (BoreSparkle sparkle : SOURCE_SPARKLES) {
            sparkle.render(buffer, camera, partialTick);
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private record StreamKey(BlockPos sourcePos, BlockPos destinationPos, int color) {
    }

    private record SourceKey(BlockPos matrixPos, BlockPos targetPos, int color) {
    }

    private static final class SourceEffect {
        private final TCInfusionSourcePayload payload;
        private int ticks;

        private SourceEffect(TCInfusionSourcePayload payload, int ticks) {
            this.payload = payload;
            this.ticks = ticks;
        }
    }

    private static final class EssentiaStream {
        private final Vec3 start;
        private final Vec3 target;
        private final int color;
        private final List<StreamPoint> points = new ArrayList<>();
        private Vec3 position;
        private Vec3 previousPosition;
        private Vec3 velocity;
        private float scale;
        private int age;
        private int maxAge;
        private int length;
        private boolean removed;

        private EssentiaStream(Level level, BlockPos source, BlockPos destination, int color, int extension, int count) {
            start = Vec3.atCenterOf(source);
            target = Vec3.atCenterOf(destination);
            position = start;
            previousPosition = start;
            scale = (float) (0.1F * (1.0D + level.random.nextGaussian() * 0.15D));
            length = Math.max(20, extension);
            maxAge = Math.max(1, (int) (start.distanceTo(target) * 21.0D));
            velocity = new Vec3(
                    Mth.sin(count / 4.0F) * 0.015F,
                    Mth.sin(count / 3.0F) * 0.015F,
                    Mth.sin(count / 2.0F) * 0.015F
            );
            this.color = color;
            points.add(new StreamPoint(Vec3.ZERO, 0.001F));
            points.add(new StreamPoint(Vec3.ZERO, 0.001F));
        }

        private void extend(int extension) {
            int added = Math.max(extension, 5);
            length += added;
            maxAge += added;
        }

        private void tick(Level level) {
            if (age++ >= maxAge || length < 1) {
                removed = true;
                return;
            }
            previousPosition = position;
            position = position.add(velocity);
            velocity = velocity.add(0.0D, 0.002D, 0.0D).scale(0.985D);
            velocity = new Vec3(
                    Mth.clamp(velocity.x(), -0.05D, 0.05D),
                    Mth.clamp(velocity.y(), -0.05D, 0.05D),
                    Mth.clamp(velocity.z(), -0.05D, 0.05D)
            );
            Vec3 delta = target.subtract(position);
            double distance = Math.max(0.0001D, delta.length());
            velocity = velocity.add(delta.scale(0.01D / Math.min(1.0D, distance) / distance));
            float currentScale = scale * (0.75F + Mth.sin((tickCount + age) / 2.0F) * 0.25F);
            if (distance < 1.0D) {
                float taper = Mth.sin((float) (distance * Math.PI / 2.0D));
                currentScale *= taper;
                scale *= taper;
            }
            if (scale > 0.001F) {
                points.add(new StreamPoint(position.subtract(start), currentScale));
            } else {
                length--;
                spawnEndpointDrop(level);
            }
            while (points.size() > length) {
                points.removeFirst();
            }
            if (points.size() > 2 && level.random.nextBoolean()) {
                int index = level.random.nextInt(3);
                if (level.random.nextBoolean()) {
                    index = points.size() - 2;
                }
                spawnDrop(level, start.add(points.get(index).offset()));
            }
        }

        private void spawnEndpointDrop(Level level) {
            spawnDrop(level, target);
        }

        private void spawnDrop(Level level, Vec3 location) {
            float red = (color >> 16 & 0xFF) / 255.0F;
            float green = (color >> 8 & 0xFF) / 255.0F;
            float blue = (color & 0xFF) / 255.0F;
            TCFXDispatcher.drawSimpleSparkle(
                    level,
                    location.x() + level.random.nextGaussian() * 0.075D,
                    location.y() + level.random.nextGaussian() * 0.075D,
                    location.z() + level.random.nextGaussian() * 0.075D,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.5F,
                    red,
                    green,
                    blue,
                    0,
                    1.0F,
                    0.0F,
                    4
            );
        }

        private int render(BufferBuilder buffer, Camera camera, float partialTick) {
            List<RenderPoint> renderPoints = createRenderPoints();
            if (renderPoints.size() < 3) {
                return 0;
            }
            Vec3 cameraPos = camera.getPosition();
            int red = color >> 16 & 0xFF;
            int green = color >> 8 & 0xFF;
            int blue = color & 0xFF;
            int vertices = 0;
            for (int index = 1; index < renderPoints.size(); index++) {
                RenderPoint previous = renderPoints.get(index - 1);
                RenderPoint current = renderPoints.get(index);
                Vec3 p0 = start.add(previous.offset());
                Vec3 p1 = start.add(current.offset());
                Vec3 tangent = p1.subtract(p0);
                if (tangent.lengthSqr() < 1.0E-8D) {
                    continue;
                }
                tangent = tangent.normalize();
                Vec3 side = tangent.cross(new Vec3(0.0D, 1.0D, 0.0D));
                if (side.lengthSqr() < 1.0E-6D) {
                    side = tangent.cross(new Vec3(1.0D, 0.0D, 0.0D));
                }
                side = side.normalize();
                Vec3 up = tangent.cross(side).normalize();
                float radius0 = previous.radius();
                float radius1 = current.radius();
                for (int sideIndex = 0; sideIndex < STREAM_SIDES; sideIndex++) {
                    double angle0 = sideIndex * Math.PI * 2.0D / STREAM_SIDES;
                    double angle1 = (sideIndex + 1) * Math.PI * 2.0D / STREAM_SIDES;
                    Vec3 radial0 = side.scale(Math.cos(angle0)).add(up.scale(Math.sin(angle0)));
                    Vec3 radial1 = side.scale(Math.cos(angle1)).add(up.scale(Math.sin(angle1)));
                    Vec3 a = p0.add(radial0.scale(radius0)).subtract(cameraPos);
                    Vec3 b = p0.add(radial1.scale(radius0)).subtract(cameraPos);
                    Vec3 c = p1.add(radial1.scale(radius1)).subtract(cameraPos);
                    Vec3 d = p1.add(radial0.scale(radius1)).subtract(cameraPos);
                    float u0 = sideIndex / (float) STREAM_SIDES;
                    float u1 = (sideIndex + 1) / (float) STREAM_SIDES;
                    float v0 = (index - 1) / (float) renderPoints.size();
                    float v1 = index / (float) renderPoints.size();
                    addStreamVertex(buffer, a, u0, v0, red, green, blue, previous.colorScale());
                    addStreamVertex(buffer, b, u1, v0, red, green, blue, previous.colorScale());
                    addStreamVertex(buffer, c, u1, v1, red, green, blue, current.colorScale());
                    addStreamVertex(buffer, d, u0, v1, red, green, blue, current.colorScale());
                    vertices += 4;
                }
            }
            return vertices;
        }

        private boolean canRender() {
            List<RenderPoint> renderPoints = createRenderPoints();
            for (int index = 1; index < renderPoints.size(); index++) {
                if (renderPoints.get(index).offset().distanceToSqr(renderPoints.get(index - 1).offset()) >= 1.0E-8D) {
                    return true;
                }
            }
            return false;
        }

        private List<RenderPoint> createRenderPoints() {
            ArrayList<RenderPoint> rendered = new ArrayList<>(points.size());
            for (int index = 0; index < points.size(); index++) {
                StreamPoint source = points.get(points.size() - 1 - index);
                float variance = 1.0F + Mth.sin((index + age) / 3.0F) * 0.2F;
                Vec3 offset = source.offset().add(
                        Mth.sin((index + age) / 6.0F) * 0.03F,
                        Mth.sin((index + age) / 7.0F) * 0.03F,
                        Mth.sin((index + age) / 8.0F) * 0.03F
                );
                float radius = source.radius() * variance;
                if (index > points.size() - 10) {
                    radius *= Mth.cos((index - (points.size() - 12)) / 10.0F * Mth.HALF_PI);
                }
                radius = switch (index) {
                    case 0, 1 -> 0.0F;
                    case 2 -> (scale * 0.5F + radius) / 2.0F;
                    case 3 -> (scale + radius) / 2.0F;
                    case 4 -> (scale + radius * 2.0F) / 3.0F;
                    default -> radius;
                };
                float colorScale = 1.0F - Mth.sin((index + age) / 2.0F) * 0.1F;
                rendered.add(new RenderPoint(offset, Math.max(0.0F, radius), colorScale));
            }
            return rendered;
        }

        private static void addStreamVertex(
                BufferBuilder buffer,
                Vec3 point,
                float u,
                float v,
                int red,
                int green,
                int blue,
                float colorScale
        ) {
            buffer.addVertex((float) point.x(), (float) point.y(), (float) point.z())
                    .setUv(u, v)
                    .setColor(
                            Mth.clamp((int) (red * colorScale), 0, 255),
                            Mth.clamp((int) (green * colorScale), 0, 255),
                            Mth.clamp((int) (blue * colorScale), 0, 255),
                            255
                    );
        }
    }

    private record StreamPoint(Vec3 offset, float radius) {
    }

    private record RenderPoint(Vec3 offset, float radius, float colorScale) {
    }

    private static final class BoreSparkle {
        private final Vec3 target;
        private final RandomSource random;
        private final int maxAge;
        private final float red;
        private final float green;
        private final float blue;
        private Vec3 position;
        private Vec3 previousPosition;
        private Vec3 velocity;
        private float scale;
        private int age;

        private BoreSparkle(Level level, Vec3 position, Vec3 target, float red, float green, float blue) {
            this.position = position;
            previousPosition = position;
            this.target = target;
            this.red = red;
            this.green = green;
            this.blue = blue;
            random = level.random;
            scale = 0.5F + random.nextFloat() * 0.5F;
            velocity = new Vec3(
                    random.nextGaussian() * 0.01D,
                    random.nextGaussian() * 0.01D,
                    random.nextGaussian() * 0.01D
            );
            int base = Math.max(1, (int) (position.distanceTo(target) * 10.0D));
            Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
            int visibleDistance = Minecraft.getInstance().options.graphicsMode().get() == GraphicsStatus.FAST ? 32 : 64;
            maxAge = cameraEntity != null && cameraEntity.distanceToSqr(position) > visibleDistance * visibleDistance
                    ? 0
                    : base / 2 + random.nextInt(base);
        }

        private boolean tick() {
            if (age++ >= maxAge || BoreDebris.sameBlock(position, target)) {
                return false;
            }
            previousPosition = position;
            position = position.add(velocity);
            velocity = new Vec3(velocity.x() * 0.985D, velocity.y() * 0.95D, velocity.z() * 0.985D);
            Vec3 delta = target.subtract(position);
            double distance = Math.max(0.001D, delta.length());
            double clamp = Math.min(0.25D, distance / 15.0D);
            if (distance < 2.0D) {
                scale *= 0.9F;
            }
            velocity = velocity.add(delta.scale(clamp / distance));
            velocity = new Vec3(
                    Mth.clamp(velocity.x(), -clamp, clamp),
                    Mth.clamp(velocity.y(), -clamp, clamp),
                    Mth.clamp(velocity.z(), -clamp, clamp)
            ).add(
                    random.nextGaussian() * 0.01D,
                    random.nextGaussian() * 0.01D,
                    random.nextGaussian() * 0.01D
            );
            return true;
        }

        private void render(BufferBuilder buffer, Camera camera, float partialTick) {
            Vec3 cameraPos = camera.getPosition();
            float x = (float) (Mth.lerp(partialTick, previousPosition.x(), position.x()) - cameraPos.x());
            float y = (float) (Mth.lerp(partialTick, previousPosition.y(), position.y()) - cameraPos.y());
            float z = (float) (Mth.lerp(partialTick, previousPosition.z(), position.z()) - cameraPos.z());
            float size = 0.1F * scale * (Mth.sin(age / 3.0F) * 0.5F + 1.0F);
            int frame = age % 4;
            float u0 = frame / 64.0F;
            float u1 = u0 + 1.0F / 64.0F;
            float v0 = 4.0F / 64.0F;
            float v1 = v0 + 1.0F / 64.0F;
            Quaternionf rotation = camera.rotation();
            Vector3f[] corners = {
                    new Vector3f(-size, -size, 0.0F),
                    new Vector3f(-size, size, 0.0F),
                    new Vector3f(size, size, 0.0F),
                    new Vector3f(size, -size, 0.0F)
            };
            for (Vector3f corner : corners) {
                corner.rotate(rotation).add(x, y, z);
            }
            addSparkleVertex(buffer, corners[0], u1, v1);
            addSparkleVertex(buffer, corners[1], u1, v0);
            addSparkleVertex(buffer, corners[2], u0, v0);
            addSparkleVertex(buffer, corners[3], u0, v1);
        }

        private void addSparkleVertex(BufferBuilder buffer, Vector3f point, float u, float v) {
            buffer.addVertex(point.x(), point.y(), point.z()).setUv(u, v).setColor(red, green, blue, 0.75F);
        }
    }

    private static final class BoreDebris {
        private final TextureAtlasSprite sprite;
        private final float red;
        private final float green;
        private final float blue;
        private float size;
        private final Vec3 target;
        private Vec3 position;
        private Vec3 previousPosition;
        private Vec3 velocity;
        private final RandomSource random;
        private int age;
        private final int maxAge;

        private BoreDebris(Level level, double x, double y, double z, BlockPos matrixPos, ItemStack stack) {
            Minecraft minecraft = Minecraft.getInstance();
            sprite = minecraft.getItemRenderer().getModel(stack, level, null, 0).getParticleIcon();
            int tint = stack.getItem() instanceof BlockItem blockItem
                    ? minecraft.getBlockColors().getColor(
                            blockItem.getBlock().defaultBlockState(),
                            level,
                            BlockPos.containing(x, y, z),
                            0
                    )
                    : minecraft.getItemColors().getColor(stack, 0);
            red = 0.6F * (tint == -1 ? 1.0F : (tint >> 16 & 0xFF) / 255.0F);
            green = 0.6F * (tint == -1 ? 1.0F : (tint >> 8 & 0xFF) / 255.0F);
            blue = 0.6F * (tint == -1 ? 1.0F : (tint & 0xFF) / 255.0F);
            size = 0.04F + level.random.nextFloat() * 0.03F;
            target = new Vec3(matrixPos.getX() + 0.5D, matrixPos.getY() - 0.5D, matrixPos.getZ() + 0.5D);
            position = new Vec3(x, y, z);
            previousPosition = position;
            random = level.random;
            boolean block = stack.getItem() instanceof BlockItem;
            velocity = new Vec3(
                    block
                            ? level.random.nextGaussian() * 0.01D
                            : level.random.nextGaussian() * 0.03D + level.random.nextGaussian() * 0.01D,
                    block
                            ? level.random.nextGaussian() * 0.01D
                            : level.random.nextGaussian() * 0.03D + level.random.nextGaussian() * 0.01D,
                    block
                            ? level.random.nextGaussian() * 0.01D
                            : level.random.nextGaussian() * 0.03D + level.random.nextGaussian() * 0.01D
            );
            int base = Math.max(1, (int) (position.distanceTo(target) * 10.0D));
            Entity cameraEntity = minecraft.getCameraEntity();
            int visibleDistance = minecraft.options.graphicsMode().get() == GraphicsStatus.FAST ? 32 : 64;
            maxAge = cameraEntity != null && cameraEntity.distanceToSqr(position) > visibleDistance * visibleDistance
                    ? 0
                    : base / 2 + level.random.nextInt(base);
        }

        private boolean tick() {
            if (age++ >= maxAge || sameBlock(position, target)) {
                return false;
            }
            previousPosition = position;
            position = position.add(velocity);
            velocity = new Vec3(velocity.x() * 0.985D, velocity.y() * 0.95D, velocity.z() * 0.985D);
            Vec3 delta = target.subtract(position);
            double distance = Math.max(0.001D, delta.length());
            double clamp = Math.min(0.25D, distance / 15.0D);
            if (distance < 2.0D) {
                size *= 0.9F;
            }
            velocity = velocity.add(delta.scale(clamp / distance));
            velocity = new Vec3(
                    Mth.clamp(velocity.x(), -clamp, clamp),
                    Mth.clamp(velocity.y(), -clamp, clamp),
                    Mth.clamp(velocity.z(), -clamp, clamp)
            );
            velocity = velocity.add(
                    random.nextGaussian() * 0.005D,
                    random.nextGaussian() * 0.005D,
                    random.nextGaussian() * 0.005D
            );
            return true;
        }

        private void render(BufferBuilder buffer, Camera camera, float partialTick) {
            Vec3 cameraPos = camera.getPosition();
            float x = (float) (Mth.lerp(partialTick, previousPosition.x(), position.x()) - cameraPos.x());
            float y = (float) (Mth.lerp(partialTick, previousPosition.y(), position.y()) - cameraPos.y());
            float z = (float) (Mth.lerp(partialTick, previousPosition.z(), position.z()) - cameraPos.z());
            Quaternionf rotation = camera.rotation();
            Vector3f[] corners = {
                    new Vector3f(-size, -size, 0.0F),
                    new Vector3f(-size, size, 0.0F),
                    new Vector3f(size, size, 0.0F),
                    new Vector3f(size, -size, 0.0F)
            };
            for (Vector3f corner : corners) {
                corner.rotate(rotation).add(x, y, z);
            }
            addDebrisVertex(buffer, corners[0], sprite.getU1(), sprite.getV1());
            addDebrisVertex(buffer, corners[1], sprite.getU1(), sprite.getV0());
            addDebrisVertex(buffer, corners[2], sprite.getU0(), sprite.getV0());
            addDebrisVertex(buffer, corners[3], sprite.getU0(), sprite.getV1());
        }

        private void addDebrisVertex(BufferBuilder buffer, Vector3f point, float u, float v) {
            buffer.addVertex(point.x(), point.y(), point.z()).setUv(u, v).setColor(red, green, blue, 0.3F);
        }

        private static boolean sameBlock(Vec3 first, Vec3 second) {
            return Mth.floor(first.x()) == Mth.floor(second.x())
                    && Mth.floor(first.y()) == Mth.floor(second.y())
                    && Mth.floor(first.z()) == Mth.floor(second.z());
        }
    }
}

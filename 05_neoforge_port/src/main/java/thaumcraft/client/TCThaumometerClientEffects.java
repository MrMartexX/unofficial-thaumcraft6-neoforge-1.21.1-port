package thaumcraft.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import com.mojang.math.Axis;
import org.joml.Matrix4f;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.fx.TCFXDispatcher;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.research.TCKnowledgeClientCache;
import thaumcraft.common.research.TCScanningManager;
import thaumcraft.common.research.TCScanTargeting;

public final class TCThaumometerClientEffects {
    private static final double HIGHLIGHT_ENTITY_RANGE = 16.0D;
    private static final double HIGHLIGHT_BLOCK_RANGE = 16.0D;
    private static final double USE_ENTITY_RANGE = 9.0D;
    private static final double USE_BLOCK_RANGE = 5.0D;
    private static final float HIGHLIGHT_ENTITY_PADDING = 5.0F;
    private static final int ROW_SIZE = 5;
    private static final int DIGIT_WIDTH = 5;
    private static final int DIGIT_SPACING = 1;
    private static final String[][] DIGIT_PIXELS = {
            { "01110", "10001", "10011", "10101", "11001", "10001", "01110" },
            { "00100", "01100", "00100", "00100", "00100", "00100", "01110" },
            { "01110", "10001", "00001", "00010", "00100", "01000", "11111" },
            { "11110", "00001", "00001", "01110", "00001", "00001", "11110" },
            { "00010", "00110", "01010", "10010", "11111", "00010", "00010" },
            { "11111", "10000", "10000", "11110", "00001", "00001", "11110" },
            { "00110", "01000", "10000", "11110", "10001", "10001", "01110" },
            { "11111", "00001", "00010", "00100", "01000", "01000", "01000" },
            { "01110", "10001", "10001", "01110", "10001", "10001", "01110" },
            { "01110", "10001", "10001", "01111", "00001", "00010", "01100" }
    };

    private static LivingEntity thaumTarget;
    private static float tagScale;

    private TCThaumometerClientEffects() {
    }

    public static void onClientTick(Minecraft minecraft) {
        Level level = minecraft.level;
        Player player = minecraft.player;

        if (level == null || player == null || !hasThaumometerReady(player)) {
            thaumTarget = null;
            decayTagScale();
            return;
        }

        if (player.tickCount % 5 != 0) {
            if (thaumTarget == null) {
                decayTagScale();
            }
            return;
        }

        Entity target = findLookedEntity(level, player, HIGHLIGHT_ENTITY_RANGE, HIGHLIGHT_ENTITY_PADDING);
        List<String> targetResearchKeys = target == null ? List.of() : TCScanningManager.clientPotentialResearchKeys(target);
        boolean targetStillScannable = TCKnowledgeClientCache.hasUnknownResearch(targetResearchKeys);
        if (target != null && targetStillScannable) {
            TCFXDispatcher.scanHighlight(level, target.getBoundingBox());
        }

        if (target instanceof LivingEntity living && !(target instanceof Player) && hasEntityAspects(living)) {
            thaumTarget = living;
        } else {
            thaumTarget = null;
        }

        highlightWildBlock(level, player);
    }

    public static void onUse(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            return;
        }

        Entity target = findLookedEntity(level, player, USE_ENTITY_RANGE, 0.0F);
        if (target != null) {
            for (int index = 0; index < 10; index++) {
                TCFXDispatcher.blockRunes(
                        level,
                        target.getX() - 0.5D,
                        target.getY() + target.getBbHeight() / 2.0D,
                        target.getZ() - 0.5D,
                        0.3F + level.random.nextFloat() * 0.7F,
                        0.0F,
                        0.3F + level.random.nextFloat() * 0.7F,
                        Math.max(1, (int) (target.getBbHeight() * 15.0F)),
                        0.03F
                );
            }
            return;
        }

        BlockHitResult blockHit = TCScanTargeting.rayTrace(level, player, USE_BLOCK_RANGE, true);
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = blockHit.getBlockPos();
            for (int index = 0; index < 10; index++) {
                TCFXDispatcher.blockRunes(
                        level,
                        pos.getX(),
                        pos.getY() + 0.25D,
                        pos.getZ(),
                        0.3F + level.random.nextFloat() * 0.7F,
                        0.0F,
                        0.3F + level.random.nextFloat() * 0.7F,
                        15,
                        0.03F
                );
            }
        }
    }

    public static void renderAspectOverlay(RenderLevelStageEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        Level level = minecraft.level;

        if (level == null || player == null || !hasThaumometerReady(player)) {
            thaumTarget = null;
            decayTagScale();
            return;
        }

        LivingEntity target = thaumTarget;
        if (target == null || !target.isAlive() || target.level() != level) {
            decayTagScale();
            return;
        }

        AspectList aspects = AspectHelper.getEntityAspects(target);
        if (!hasAspects(aspects)) {
            decayTagScale();
            return;
        }

        if (tagScale < 0.5F) {
            tagScale += 0.031F - tagScale / 10.0F;
        }

        float partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        double x = Mth.lerp(partialTicks, target.xOld, target.getX());
        double y = Mth.lerp(partialTicks, target.yOld, target.getY()) + target.getBbHeight();
        double z = Mth.lerp(partialTicks, target.zOld, target.getZ());

        drawTagsOnContainer(event.getPoseStack(), event.getCamera(), minecraft.font, x - 0.5D, y, z - 0.5D, aspects);
    }

    private static void highlightWildBlock(Level level, Player player) {
        BlockHitResult hit = TCScanTargeting.wildRayTrace(level, player, HIGHLIGHT_BLOCK_RANGE, true, level.random);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return;
        }

        if (hasUnknownBlockScan(state)) {
            TCFXDispatcher.scanHighlight(level, pos);
        }
    }

    private static boolean hasUnknownBlockScan(BlockState state) {
        return TCKnowledgeClientCache.hasUnknownResearch(TCScanningManager.clientPotentialResearchKeys(state));
    }

    private static boolean hasEntityAspects(Entity entity) {
        return hasAspects(AspectHelper.getEntityAspects(entity));
    }

    private static boolean hasAspects(AspectList aspects) {
        return aspects != null && aspects.size() > 0 && aspects.visSize() > 0;
    }

    private static boolean hasThaumometerReady(Player player) {
        return isThaumometer(player.getMainHandItem())
                || isThaumometer(player.getInventory().getItem(0));
    }

    private static boolean isThaumometer(ItemStack stack) {
        return !stack.isEmpty() && stack.is(TCItems.THAUMOMETER.get());
    }

    private static Entity findLookedEntity(Level level, Player player, double range, float padding) {
        return TCScanTargeting.findPointedEntity(level, player, 1.0D, range, padding, true);
    }

    private static void drawTagsOnContainer(
            PoseStack poseStack,
            Camera camera,
            Font font,
            double x,
            double y,
            double z,
            AspectList tags
    ) {
        Vec3 cameraPos = camera.getPosition();
        int current = 0;
        float shiftY = 0.0F;
        int left = tags.size();

        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (Aspect aspect : tags.getAspects()) {
            int div = Math.min(left, ROW_SIZE);
            if (current >= ROW_SIZE) {
                current = 0;
                shiftY -= tagScale * 1.05F;
                left -= ROW_SIZE;
                if (left < ROW_SIZE) {
                    div = Math.floorMod(left, ROW_SIZE);
                }
            }

            float shift = (current - div / 2.0F + 0.5F) * tagScale * 4.0F;
            shift *= tagScale;

            poseStack.pushPose();
            poseStack.translate(x + 0.5D - cameraPos.x(), y - shiftY + 0.5D - cameraPos.y(), z + 0.5D - cameraPos.z());
            float xDistance = (float) (cameraPos.x() - (x + 0.5D));
            float zDistance = (float) (cameraPos.z() - (z + 0.5D));
            float yaw = (float) (Math.atan2(xDistance, zDistance) * 180.0D / Math.PI);
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw + 180.0F));
            poseStack.translate(shift, 0.0D, 0.0D);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            poseStack.scale(tagScale, tagScale, tagScale);
            drawAspectIcon(poseStack, aspect);
            drawAspectAmount(poseStack, font, tags.getAmount(aspect));
            poseStack.popPose();
            current++;
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
    }

    private static void drawAspectIcon(PoseStack poseStack, Aspect aspect) {
        int color = aspect.getColor();
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, aspect.getImage());

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.addVertex(matrix, -0.5F, 0.5F, 0.0F).setUv(1.0F, 1.0F).setColor(red, green, blue, 180);
        buffer.addVertex(matrix, 0.5F, 0.5F, 0.0F).setUv(1.0F, 0.0F).setColor(red, green, blue, 180);
        buffer.addVertex(matrix, 0.5F, -0.5F, 0.0F).setUv(0.0F, 0.0F).setColor(red, green, blue, 180);
        buffer.addVertex(matrix, -0.5F, -0.5F, 0.0F).setUv(0.0F, 1.0F).setColor(red, green, blue, 180);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void drawAspectAmount(PoseStack poseStack, Font font, int amount) {
        if (amount < 0) {
            return;
        }

        String text = Integer.toString(amount);

        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        poseStack.scale(0.04F, 0.04F, 0.04F);
        poseStack.translate(0.0D, 6.0D, -0.1D);
        int width = digitTextWidth(text);
        drawAspectAmountLayer(poseStack, text, 14.0F - width, 1.0F, 0x111111);
        drawAspectAmountLayer(poseStack, text, 13.0F - width, 0.0F, 0xFFFFFF);
        poseStack.popPose();
    }

    private static void drawAspectAmountLayer(PoseStack poseStack, String text, float x, float y, int color) {
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        float drawX = x;

        for (int i = 0; i < text.length(); i++) {
            char digit = text.charAt(i);
            if (digit >= '0' && digit <= '9') {
                addDigit(buffer, matrix, drawX, y, digit - '0', red, green, blue);
            }
            drawX += DIGIT_WIDTH + DIGIT_SPACING;
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void addDigit(BufferBuilder buffer, Matrix4f matrix, float x, float y, int digit, int red, int green, int blue) {
        String[] rows = DIGIT_PIXELS[digit];

        for (int row = 0; row < rows.length; row++) {
            String pixels = rows[row];
            for (int column = 0; column < pixels.length(); column++) {
                if (pixels.charAt(column) == '1') {
                    addDigitPixel(buffer, matrix, x + column, y + row, red, green, blue);
                }
            }
        }
    }

    private static void addDigitPixel(BufferBuilder buffer, Matrix4f matrix, float x, float y, int red, int green, int blue) {
        buffer.addVertex(matrix, x, y + 1.0F, 0.0F).setColor(red, green, blue, 230);
        buffer.addVertex(matrix, x + 1.0F, y + 1.0F, 0.0F).setColor(red, green, blue, 230);
        buffer.addVertex(matrix, x + 1.0F, y, 0.0F).setColor(red, green, blue, 230);
        buffer.addVertex(matrix, x, y, 0.0F).setColor(red, green, blue, 230);
    }

    private static int digitTextWidth(String text) {
        if (text.isEmpty()) {
            return 0;
        }

        return text.length() * DIGIT_WIDTH + (text.length() - 1) * DIGIT_SPACING;
    }

    private static void decayTagScale() {
        if (tagScale > 0.0F) {
            tagScale = Math.max(0.0F, tagScale - 0.005F);
        }
    }
}

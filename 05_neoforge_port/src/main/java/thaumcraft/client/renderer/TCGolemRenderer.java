package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import java.util.ArrayList;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.parts.PartModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.common.entities.TCGolemEntity;

final class TCGolemRenderer extends EntityRenderer<TCGolemEntity> {
    private boolean normalPass;
    private boolean revealPass;
    private static final ResourceLocation BASE = ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_base.obj");
    private final WeakHashMap<TCGolemEntity, float[]> grinderAnimation = new WeakHashMap<>();
    private static final ResourceLocation TEX_WOOD =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/golems/mat_wood.png");
    private static final ResourceLocation TEX_IRON =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/golems/mat_iron.png");
    private static final ResourceLocation TEX_CLAY =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/golems/mat_clay.png");
    private static final ResourceLocation TEX_BRASS =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/golems/mat_brass.png");
    private static final ResourceLocation TEX_THAUMIUM =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/golems/mat_thaumium.png");
    private static final ResourceLocation TEX_VOID =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/golems/mat_void.png");

    TCGolemRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.3F;
    }

    @Override
    public ResourceLocation getTextureLocation(TCGolemEntity entity) {
        IGolemProperties props = entity.getProperties();
        if (props != null && props.getMaterial() != null) {
            String key = props.getMaterial().key;
            if (key != null) {
                return switch (key.toLowerCase()) {
                    case "iron" -> TEX_IRON;
                    case "clay" -> TEX_CLAY;
                    case "brass" -> TEX_BRASS;
                    case "thaumium" -> TEX_THAUMIUM;
                    case "void" -> TEX_VOID;
                    default -> TEX_WOOD;
                };
            }
        }
        return TEX_WOOD;
    }

    @Override
    public void render(TCGolemEntity entity, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        var player = Minecraft.getInstance().player;
        normalPass = !entity.isInvisible() || (player != null && !entity.isInvisibleTo(player));
        revealPass = player != null && player.isShiftKeyDown()
                && (player.getMainHandItem().getItem() instanceof thaumcraft.api.golems.ISealDisplayer
                    || player.getOffhandItem().getItem() instanceof thaumcraft.api.golems.ISealDisplayer)
                && !player.hasLineOfSight(entity);
        if (!normalPass && !revealPass) { super.render(entity, yaw, partial, pose, buffers, light); return; }
        var props = entity.getProperties();
        List<PartModel> parts = new ArrayList<>();
        if (props.getHead().model != null) parts.add(props.getHead().model);
        if (props.getArms().model != null) parts.add(props.getArms().model);
        if (props.getLegs().model != null) parts.add(props.getLegs().model);
        if (props.getAddon().model != null) parts.add(props.getAddon().model);
        int overlay = OverlayTexture.pack(0, entity.hurtTime > 0 || entity.deathTime > 0);
        int color = entity.isInvisible() ? 0x26FFFFFF : 0xFFFFFFFF;
        float age = entity.tickCount + partial;
        float rx = (float)Math.toDegrees(Mth.sin(age * .067F) * .03F);
        float rz = (float)Math.toDegrees(Mth.cos(age * .09F) * .05F + .05F);
        float walk = entity.walkAnimation.position(partial), amount = Math.min(1, entity.walkAnimation.speed(partial));
        boolean floating = props.hasTrait(EnumGolemTrait.WHEELED) || props.hasTrait(EnumGolemTrait.FLYER);
        boolean holding = !entity.getFrontCarriedItem().isEmpty();
        float rightX = holding ? 90 - rz / 2 : floating ? rx * 2 : (float)Math.toDegrees(Mth.cos(walk * .6662F + Mth.PI) * amount) + rx;
        float leftX = holding ? 90 - rz / 2 : floating ? -rx * 2 : (float)Math.toDegrees(Mth.cos(walk * .6662F) * amount) - rx;
        float rightZ = holding ? -2 : rz + 2, leftZ = holding ? 2 : -rz - 2;
        float bodyY = 0, rightY = 0;
        float swing = entity.getAttackAnim(partial);
        if (swing > 0) {
            float wiggle = -Mth.sin(Mth.sqrt(swing) * Mth.TWO_PI) * .2F;
            bodyY = (float)Math.toDegrees(wiggle);
            rightZ = -(float)Math.toDegrees(Mth.sin(wiggle) * 3);
            rightX = (float)Math.toDegrees(-Mth.cos(wiggle) * 5);
            rightY = bodyY;
        }
        pose.pushPose();
        float bodyYaw = Mth.rotLerp(partial, entity.yBodyRotO, entity.yBodyRot);
        rotate(pose, 0, 180 - bodyYaw, 0);
        if (entity.deathTime > 0) rotate(pose, 0, 0, Math.min(1, Mth.sqrt(((entity.deathTime + partial - 1) / 20) * 1.6F)) * 90);
        rotate(pose, 0, bodyY, 0);
        double speed = Mth.square(entity.getX() - entity.xo) + Mth.square(entity.getZ() - entity.zo);
        rotate(pose, (float)(-speed * (floating ? 75 : 25)), 0,
                (float)(-speed * (floating ? 75 : 25) * .06 * (entity.getYRot() - entity.yRotO)));
        pose.pushPose(); pose.translate(0, .5, 0);
        base(entity, "chest", pose, buffers, light, overlay, color);
        base(entity, "waist", pose, buffers, light, overlay, color);
        if (entity.getGolemColor() > 0 && entity.getGolemColor() <= 16)
            base(entity, "flag", pose, buffers, light, overlay, (color & 0xFF000000) | DyeColor.byId(entity.getGolemColor() - 1).getTextureDiffuseColor());
        parts(entity, parts, PartModel.EnumAttachPoint.BODY, PartModel.EnumLimbSide.MIDDLE, pose, buffers, light, overlay, color, partial);
        pose.popPose();
        pose.pushPose(); pose.translate(0, .75, -.03125);
        pose.mulPose(Axis.YP.rotationDegrees(-Mth.wrapDegrees(Mth.rotLerp(partial, entity.yHeadRotO, entity.yHeadRot) - bodyYaw)));
        pose.mulPose(Axis.XP.rotationDegrees(-Mth.lerp(partial, entity.xRotO, entity.getXRot())));
        parts(entity, parts, PartModel.EnumAttachPoint.HEAD, PartModel.EnumLimbSide.MIDDLE, pose, buffers, light, overlay, color, partial);
        base(entity, "head", pose, buffers, light, overlay, color); pose.popPose();
        for (int side = 0; side < 2; side++) {
            boolean left = side == 1;
            var limb = left ? PartModel.EnumLimbSide.LEFT : PartModel.EnumLimbSide.RIGHT;
            float armX = left ? leftX : rightX, armY = left ? 0 : rightY, armZ = left ? leftZ : rightZ;
            if (props.getArms().key.equalsIgnoreCase("DARTS") && entity.isInCombat()) { armX = 90 - entity.xRotO + armX / 10; armY /= 10; armZ /= 10; }
            pose.pushPose(); pose.translate(left ? -.20625 : .20625, .6875, 0);
            rotate(pose, armX, armY + (left ? 180 : 0), left ? -armZ : armZ);
            base(entity, "arm", pose, buffers, light, overlay, color);
            parts(entity, parts, PartModel.EnumAttachPoint.ARMS, limb, pose, buffers, light, overlay, color, partial); pose.popPose();
            pose.pushPose(); pose.translate(left ? -.09375 : .09375, .375, 0);
            rotate(pose, (float)Math.toDegrees(Mth.cos(walk * .6662F + (left ? Mth.PI : 0)) * amount), 0, 0);
            parts(entity, parts, PartModel.EnumAttachPoint.LEGS, limb, pose, buffers, light, overlay, color, partial); pose.popPose();
        }
        if (holding && normalPass) {
            pose.pushPose(); pose.translate(0, .625, 0); rotate(pose, -rz * .5F, 0, 0);
            pose.scale(.375F, .375F, .375F); pose.translate(0, .25, -1.5);
            if (!(entity.getFrontCarriedItem().getItem() instanceof BlockItem)) pose.translate(0, -.6, 0);
            Minecraft.getInstance().getItemRenderer().renderStatic(entity, entity.getFrontCarriedItem(), ItemDisplayContext.HEAD, false, pose, buffers, entity.level(), light, overlay, entity.getId());
            pose.popPose();
        }
        pose.popPose();
        super.render(entity, yaw, partial, pose, buffers, light);
    }

    private static void rotate(PoseStack pose, float x, float y, float z) {
        // Match legacy OpenGL X -> Y -> Z multiplication order.
        pose.mulPose(Axis.XP.rotationDegrees(x)); pose.mulPose(Axis.YP.rotationDegrees(y)); pose.mulPose(Axis.ZP.rotationDegrees(z));
    }

    private void base(TCGolemEntity entity, String group, PoseStack pose, MultiBufferSource buffers, int light, int overlay, int color) {
        mesh(BASE, group, getTextureLocation(entity), pose, buffers, light, overlay, color);
    }

    private void mesh(ResourceLocation model, String group, ResourceLocation texture, PoseStack pose, MultiBufferSource buffers, int light, int overlay, int color) {
        if (normalPass) TCGolemObjMesh.get(model).render(group, pose, buffers.getBuffer(RenderType.entityTranslucent(texture)), light, overlay, color);
        if (revealPass) TCGolemObjMesh.get(model).render(group, pose, buffers.getBuffer(TCGolemRenderTypes.reveal(texture)), light, overlay, 0x40404040);
    }

    private void parts(TCGolemEntity entity, List<PartModel> parts, PartModel.EnumAttachPoint point, PartModel.EnumLimbSide side,
                       PoseStack pose, MultiBufferSource buffers, int light, int overlay, int color, float partial) {
        for (PartModel part : parts) if (part.getAttachPoint() == point) {
            var model = TCGolemObjMesh.get(part.getObjModel());
            for (String group : model.groups.keySet()) {
                pose.pushPose();
                if (group.equals("wheel")) {
                    pose.translate(0, -.375, 0);
                    rotate(pose, -entity.getWheelRotation(), 0, 0);
                } else if (group.startsWith("claw")) {
                    pose.translate(0, -.2, 0);
                    float angle = entity.getAttackAnim(partial) * 4.1F;
                    rotate(pose, angle * angle * (group.endsWith("1") ? 1 : -1), 0, 0);
                } else if (group.equals("grinder")) {
                    float[] state = grinderAnimation.computeIfAbsent(entity, key -> new float[2]);
                    state[0] = Math.max(state[0], entity.getAttackAnim(partial) * 20);
                    state[1] += state[0]; state[0] *= .99F;
                    pose.translate(0, -.34, 0);
                    float angle = (entity.tickCount + partial) / 2 + state[1] + (side == PartModel.EnumLimbSide.LEFT ? 22 : 0);
                    rotate(pose, side == PartModel.EnumLimbSide.LEFT ? -angle : angle, 0, 0);
                }
                ResourceLocation texture = part.getTexture() == null || part.useMaterialTextureForObjectPart(group) ? getTextureLocation(entity) : part.getTexture();
                mesh(part.getObjModel(), group, texture, pose, buffers, light, overlay, color);
                if (normalPass && part.getObjModel().getPath().endsWith("golem_hauler.obj") && !entity.getBackCarriedItem().isEmpty()) {
                    pose.pushPose(); pose.scale(.375F, .375F, .375F); pose.translate(0, .33, .825);
                    if (!(entity.getBackCarriedItem().getItem() instanceof BlockItem)) pose.translate(0, 0, -.25);
                    Minecraft.getInstance().getItemRenderer().renderStatic(entity, entity.getBackCarriedItem(), ItemDisplayContext.HEAD, false, pose, buffers, entity.level(), light, overlay, entity.getId());
                    pose.popPose();
                }
                pose.popPose();
            }
        }
    }
}

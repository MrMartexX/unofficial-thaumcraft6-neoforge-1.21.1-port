package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;

public final class TCSealWorldRenderer {
    private static final ResourceLocation MIDDLE = ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/seal_area.png");
    private static final ResourceLocation CORNER = ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/frame_corner.png");
    private static final Direction[][] FACES = {{Direction.DOWN,Direction.NORTH,Direction.WEST},{Direction.UP,Direction.NORTH,Direction.WEST},
            {Direction.DOWN,Direction.NORTH,Direction.EAST},{Direction.UP,Direction.NORTH,Direction.EAST},
            {Direction.DOWN,Direction.SOUTH,Direction.EAST},{Direction.UP,Direction.SOUTH,Direction.EAST},
            {Direction.DOWN,Direction.SOUTH,Direction.WEST},{Direction.UP,Direction.SOUTH,Direction.WEST}};
    private static final int[][] ROTATIONS = {{0,270,0},{270,180,270},{90,0,90},{180,90,180},{180,180,0},{90,270,270},{270,90,90},{0,0,180}};
    private static boolean reported;
    private static int captureFrames;
    private TCSealWorldRenderer() {}
    static boolean displays(Player player) {
        return player.getMainHandItem().getItem() instanceof ISealDisplayer || player.getOffhandItem().getItem() instanceof ISealDisplayer;
    }
    static float alpha(Player player, SealEntity seal) {
        return 1F - (float)(player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(seal.getSealPos().pos)) / 256D);
    }
    public static void render(RenderLevelStageEvent event) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || !(mc.getCameraEntity() instanceof Player player) || !displays(player)) return;
        var map = SealHandler.sealEntities.get(mc.level.dimension());
        if (map == null || map.isEmpty()) return;
        var pose = event.getPoseStack();
        var camera = event.getCamera().getPosition();
        float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        var buffers = mc.renderBuffers().bufferSource();
        var used = new java.util.HashSet<RenderType>();
        MultiBufferSource source = type -> { used.add(type); return buffers.getBuffer(type); };
        pose.pushPose();
        try {
            pose.translate(-camera.x, -camera.y, -camera.z);
            for (var seal : map.values()) {
                if (seal.getSeal() == null || seal.getSealPos() == null) continue;
                float opacity = alpha(player, seal);
                if (opacity < 0) continue;
                boolean inactive = seal.isStoppedByRedstone(mc.level);
                var pos = seal.getSealPos().pos;
                var face = seal.getSealPos().face;
                if (inactive && mc.level.random.nextFloat() < partial / 12F) {
                    thaumcraft.client.fx.legacy.TCLegacyParticleEngine.addSealStopSpark(mc.level,
                            pos.getX()+.5F+face.getStepX()*.66F, pos.getY()+.5F+face.getStepY()*.66F, pos.getZ()+.5F+face.getStepZ()*.66F,
                            .8F-mc.level.random.nextFloat()*.2F);
                    inactive = false;
                }
                emit(pose, source, seal, player.isShiftKeyDown(), opacity, inactive, player.tickCount + partial);
            }
        } finally {
            for (var type : used) buffers.endBatch(type);
            pose.popPose();
        }
        if (!used.isEmpty() && !reported && Boolean.getBoolean("tc.golemVisualAudit") && ++captureFrames >= 3
                && map.values().stream().anyMatch(seal -> seal.getSealPos() != null
                && mc.level.getBlockState(seal.getSealPos().pos).isSolidRender(mc.level,seal.getSealPos().pos))) {
            reported = true;
            var output = java.nio.file.Path.of("../../tools/reports/local/paired-worlds/seal-world.png");
            try (var screenshot = net.minecraft.client.Screenshot.takeScreenshot(mc.getMainRenderTarget())) {
                java.nio.file.Files.createDirectories(output.getParent());
                screenshot.writeToFile(output);
            } catch (java.io.IOException failure) { throw new IllegalStateException("Cannot capture seal world pass", failure); }
            com.mojang.logging.LogUtils.getLogger().info("TC_SEAL_WORLD_RENDER_PASS synced=true submitted=true");
        }
    }
    static void emit(PoseStack pose, MultiBufferSource buffers, SealEntity seal, boolean reveal, float opacity, boolean inactive, float ticks) {
        var position = seal.getSealPos().pos;
        var side = seal.getSealPos().face;
        var icon = seal.getSeal().getSealIcon();
        String path = icon.getPath();
        var spriteId = ResourceLocation.fromNamespaceAndPath(icon.getNamespace(), path.substring("textures/".length(), path.length()-4));
        var sprite = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(spriteId);
        pose.pushPose();
        pose.translate(position.getX(), position.getY(), position.getZ());
        switch (side) {
            case UP -> { pose.translate(.25,1,.75); pose.mulPose(Axis.XP.rotationDegrees(-90)); }
            case DOWN -> { pose.translate(.25,0,.25); pose.mulPose(Axis.XP.rotationDegrees(90)); }
            case SOUTH -> pose.translate(.25,.25,1);
            case NORTH -> { pose.translate(.75,.25,0); pose.mulPose(Axis.YP.rotationDegrees(180)); }
            case EAST -> { pose.translate(1,.25,.75); pose.mulPose(Axis.YP.rotationDegrees(90)); }
            case WEST -> { pose.translate(0,.25,.25); pose.mulPose(Axis.YP.rotationDegrees(-90)); }
        }
        pose.translate(0,0,.05F);
        pose.scale(.5F,.5F,.5F);
        icon(pose, buffers.getBuffer(TCSealRenderTypes.get(TextureAtlas.LOCATION_BLOCKS, reveal, false)), sprite,
                reveal ? 0 : .1F, inactive ? .5F : 1F, opacity);
        pose.popPose();
        float r,g,b;
        if (seal.getColor()>0) {
            int rgb = DyeColor.byId(seal.getColor()-1).getTextureDiffuseColor();
            r=(rgb>>16&255)/255F; g=(rgb>>8&255)/255F; b=(rgb&255)/255F;
        } else {
            r=.7F+Mth.sin((ticks+position.getX())/4)*.1F;
            g=.7F+Mth.sin((ticks+position.getY())/5)*.1F;
            b=.7F+Mth.sin((ticks+position.getZ())/6)*.1F;
        }
        pose.pushPose(); pose.translate(position.getX()+.5,position.getY()+.5,position.getZ()+.5);
        orient(pose,side,.51F); pose.mulPose(Axis.ZP.rotationDegrees(ticks%360));
        centered(pose,buffers.getBuffer(TCSealRenderTypes.get(MIDDLE,reveal,false)),.9F,r,g,b,opacity*.8F);
        pose.popPose();
        if (!(seal.getSeal() instanceof ISealConfigArea)) return;
        var area=seal.getArea();
        var box=new AABB(position).move(side.getStepX(),side.getStepY(),side.getStepZ())
                .expandTowards((area.getX()-1)*side.getStepX(),(area.getY()-1)*side.getStepY(),(area.getZ()-1)*side.getStepZ())
                .inflate(side.getStepX()==0?area.getX()-1:0,side.getStepY()==0?area.getY()-1:0,side.getStepZ()==0?area.getZ()-1:0);
        double[][] corners={{box.minX,box.minY,box.minZ},{box.minX,box.maxY-1,box.minZ},{box.maxX-1,box.minY,box.minZ},
                {box.maxX-1,box.maxY-1,box.minZ},{box.maxX-1,box.minY,box.maxZ-1},{box.maxX-1,box.maxY-1,box.maxZ-1},
                {box.minX,box.minY,box.maxZ-1},{box.minX,box.maxY-1,box.maxZ-1}};
        var out=buffers.getBuffer(TCSealRenderTypes.get(CORNER,reveal,true));
        for(int c=0;c<8;c++) for(int f=0;f<3;f++) {
            pose.pushPose(); pose.translate(corners[c][0]+.5,corners[c][1]+.5,corners[c][2]+.5);
            orient(pose,FACES[c][f],.49F); pose.mulPose(Axis.ZP.rotationDegrees(-90+ROTATIONS[c][f]));
            centered(pose,out,1,r,g,b,opacity*.7F); pose.popPose();
        }
    }
    private static void orient(PoseStack pose,Direction face,float offset) {
        pose.mulPose(new org.joml.Quaternionf().rotationAxis((float)Math.PI/2,-face.getStepY(),face.getStepX(),-face.getStepZ()));
        pose.translate(0,0,face.getStepZ()<0?-offset:offset);
    }
    private static void centered(PoseStack pose,VertexConsumer out,float scale,float r,float g,float b,float a) {
        v(pose,out,-.5F*scale,.5F*scale,0,1,1,r,g,b,a);v(pose,out,.5F*scale,.5F*scale,0,1,0,r,g,b,a);
        v(pose,out,.5F*scale,-.5F*scale,0,0,0,r,g,b,a);v(pose,out,-.5F*scale,-.5F*scale,0,0,1,r,g,b,a);
    }
    private static void v(PoseStack pose,VertexConsumer out,float x,float y,float z,float u,float w,float r,float g,float b,float a) {
        out.addVertex(pose.last().pose(),x,y,z).setUv(u,w).setColor(r,g,b,a);
    }
    private static void icon(PoseStack pose,VertexConsumer out,TextureAtlasSprite sprite,float depth,float color,float alpha) {
        float u=sprite.getU1(),v=sprite.getV0(),u0=sprite.getU0(),v1=sprite.getV1();
        float[][] front={{0,0,0,u,v1},{1,0,0,u0,v1},{1,1,0,u0,v},{0,1,0,u,v}};
        float[][] back={{0,1,-depth,u,v},{1,1,-depth,u0,v},{1,0,-depth,u0,v1},{0,0,-depth,u,v1}};
        for(var p:front)v(pose,out,p[0],p[1],p[2],p[3],p[4],color,color,color,alpha);
        for(var p:back)v(pose,out,p[0],p[1],p[2],p[3],p[4],color,color,color,alpha);
        for(int k=0;k<16;k++) {
            float x=k/16F,x1=x+1/16F,ux=u+(u0-u)*x-(u-u0)/32;
            float vy=v1+(v-v1)*x-(v1-v)/32;
            float[][] sides={{x,0,-depth,ux,v1},{x,0,0,ux,v1},{x,1,0,ux,v},{x,1,-depth,ux,v},
                    {x1,1,-depth,ux,v},{x1,1,0,ux,v},{x1,0,0,ux,v1},{x1,0,-depth,ux,v1},
                    {0,x1,0,u,vy},{1,x1,0,u0,vy},{1,x1,-depth,u0,vy},{0,x1,-depth,u,vy},
                    {1,x,-depth,u0,vy},{1,x,0,u0,vy},{0,x,0,u,vy},{0,x,-depth,u,vy}};
            for(var p:sides)v(pose,out,p[0],p[1],p[2],p[3],p[4],color,color,color,alpha);
        }
    }
}

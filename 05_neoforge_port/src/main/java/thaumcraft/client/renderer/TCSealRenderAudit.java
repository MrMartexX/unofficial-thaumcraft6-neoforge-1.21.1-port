package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealPickup;
import thaumcraft.common.registry.TCItems;

/** Opt-in actual atlas/vertex contracts; does not certify matching legacy pixels. */
public final class TCSealRenderAudit {
    private TCSealRenderAudit() {}
    public static void run() {
        checkDepthWrites();
        var mc=Minecraft.getInstance(); var player=mc.player;
        var main=player.getMainHandItem().copy(); var off=player.getOffhandItem().copy(); var position=player.position();
        int total=0;
        try {
            player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.STICK));
            player.setItemInHand(InteractionHand.OFF_HAND,ItemStack.EMPTY);
            if(TCSealWorldRenderer.displays(player))throw new IllegalStateException("Seal visibility without displayer");
            player.setItemInHand(InteractionHand.OFF_HAND,new ItemStack(TCItems.GOLEM_BELL.get()));
            if(!TCSealWorldRenderer.displays(player))throw new IllegalStateException("Missing offhand visibility");
            player.setItemInHand(InteractionHand.OFF_HAND,ItemStack.EMPTY);
            player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(TCItems.SEAL_PICKUP.get()));
            if(!TCSealWorldRenderer.displays(player))throw new IllegalStateException("Missing seal-item visibility");
            player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(TCItems.GOLEM_PLACER.get()));
            if(!TCSealWorldRenderer.displays(player))throw new IllegalStateException("Missing golem-item visibility");
            var pos=new BlockPos(0,64,0);
            for(var face:Direction.values())for(boolean reveal:new boolean[]{false,true}) {
                var seal=new SealEntity(mc.level,new SealPos(pos,face),new SealPickup());
                var iconType=TCSealRenderTypes.get(TextureAtlas.LOCATION_BLOCKS,reveal,false);
                int[] count={0,0}; float[] range={Float.POSITIVE_INFINITY,Float.NEGATIVE_INFINITY};
                MultiBufferSource buffers=type->new VertexConsumer(){
                    public VertexConsumer addVertex(float x,float y,float z){
                        if(!Float.isFinite(x)||!Float.isFinite(y)||!Float.isFinite(z))throw new IllegalStateException("Nonfinite seal vertex");
                        count[1]++;
                        if(type==iconType){ count[0]++; float normal=(x-pos.getX()-.5F)*face.getStepX()
                                +(y-pos.getY()-.5F)*face.getStepY()+(z-pos.getZ()-.5F)*face.getStepZ();
                            range[0]=Math.min(range[0],normal);range[1]=Math.max(range[1],normal); }
                        return this;
                    }
                    public VertexConsumer setColor(int r,int g,int b,int a){return this;}
                    public VertexConsumer setUv(float u,float v){
                        if(!Float.isFinite(u)||!Float.isFinite(v)||u<0||u>1||v<0||v>1)throw new IllegalStateException("Invalid seal UV");
                        return this;
                    }
                    public VertexConsumer setUv1(int u,int v){return this;} public VertexConsumer setUv2(int u,int v){return this;}
                    public VertexConsumer setNormal(float x,float y,float z){return this;}
                };
                TCSealWorldRenderer.emit(new PoseStack(),buffers,seal,reveal,1,false,10.5F);
                if(count[0]!=264||count[1]!=364||Math.abs(range[0]-(reveal?.55F:.5F))>.0001F||Math.abs(range[1]-.55F)>.0001F)
                    throw new IllegalStateException("Seal face/depth mismatch: "+face+" reveal="+reveal+" icon="+count[0]+" all="+count[1]+" depth="+java.util.Arrays.toString(range));
                total+=count[1];
            }
            var seal=new SealEntity(mc.level,new SealPos(pos,Direction.UP),new SealPickup());
            player.setPos(.5,64.5,.5);if(TCSealWorldRenderer.alpha(player,seal)!=1)throw new IllegalStateException("Wrong center alpha");
            player.setPos(16.5,64.5,.5);if(TCSealWorldRenderer.alpha(player,seal)!=0)throw new IllegalStateException("Wrong range alpha");
            for(var prototype:thaumcraft.common.golems.seals.SealHandler.types.values()) {
                var texture=prototype.getSealIcon(); var path=texture.getPath();
                var sprite=mc.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(
                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(texture.getNamespace(),path.substring(9,path.length()-4)));
                if(sprite.contents().name().getPath().equals("missingno"))throw new IllegalStateException("Seal atlas sprite missing: "+prototype.getKey());
            }
            var server=mc.getSingleplayerServer();
            if(server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).toAbsolutePath().normalize().getFileName().toString().startsWith("tc_visual_ground")) {
                var uuid=player.getUUID();
                server.execute(()->{
                    var level=server.overworld();
                    int top=level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,48,48);
                    var owner=server.getPlayerList().getPlayer(uuid);
                    long types=owner.getInventory().items.stream().filter(s->s.getItem() instanceof thaumcraft.common.golems.seals.ItemSealPlacer).count();
                    var chest=(net.minecraft.world.level.block.entity.ChestBlockEntity)level.getBlockEntity(new BlockPos(-3,64,-9));
                    int stacks=0; if(chest!=null)for(int i=0;i<chest.getContainerSize();i++)if(chest.getItem(i).getItem() instanceof thaumcraft.common.golems.seals.ItemSealPlacer)stacks++;
                    if(top!=64||types!=16||stacks!=16)throw new IllegalStateException("Fixture ground/kit mismatch: top="+top+", inventory="+types+", chest="+stacks);
                    com.mojang.logging.LogUtils.getLogger().info("TC_PARITY_FLAT_GROUND_PASS surfaceY=63 inventorySeals=16 chestSeals=16");
                });
            }
            com.mojang.logging.LogUtils.getLogger().info("TC_SEAL_GEOMETRY_PASS faces=6 modes=2 vertices={} visibility=true alpha=true atlas=true",total);
        }finally{
            player.setItemInHand(InteractionHand.MAIN_HAND,main);player.setItemInHand(InteractionHand.OFF_HAND,off);
            player.setPos(position.x,position.y,position.z);
        }
    }

    private static void checkDepthWrites() {
        boolean depth=org.lwjgl.opengl.GL11.glIsEnabled(org.lwjgl.opengl.GL11.GL_DEPTH_TEST);
        boolean mask=org.lwjgl.opengl.GL11.glGetBoolean(org.lwjgl.opengl.GL11.GL_DEPTH_WRITEMASK);
        int function=org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL11.GL_DEPTH_FUNC);
        try {
            for(boolean reveal:new boolean[]{false,true})for(boolean area:new boolean[]{false,true}) {
                var type=TCSealRenderTypes.get(TextureAtlas.LOCATION_BLOCKS,reveal,area);
                type.setupRenderState();
                try {
                    if(org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL11.GL_DEPTH_FUNC)!=(reveal?519:515)
                            || org.lwjgl.opengl.GL11.glGetBoolean(org.lwjgl.opengl.GL11.GL_DEPTH_WRITEMASK)!=( !reveal && !area))
                        throw new IllegalStateException("Seal reveal/area writes incorrect GPU depth state");
                } finally { type.clearRenderState(); }
            }
        }finally{
            if(depth)com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();else com.mojang.blaze3d.systems.RenderSystem.disableDepthTest();
            com.mojang.blaze3d.systems.RenderSystem.depthMask(mask);com.mojang.blaze3d.systems.RenderSystem.depthFunc(function);
        }
    }
}

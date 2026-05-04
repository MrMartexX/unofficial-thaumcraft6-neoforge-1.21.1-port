package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.casters.ICaster;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.items.tools.ItemElementalShovel;

public class PacketItemKeyToServer implements IMessage, IMessageHandler<PacketItemKeyToServer, IMessage> {
   private byte key;
   private byte mod;

   public PacketItemKeyToServer() {
   }

   public PacketItemKeyToServer(int key) {
      this.key = (byte)key;
      this.mod = 0;
   }

   public PacketItemKeyToServer(int key, int mod) {
      this.key = (byte)key;
      this.mod = (byte)mod;
   }

   public void toBytes(ByteBuf buffer) {
      buffer.writeByte(this.key);
      buffer.writeByte(this.mod);
   }

   public void fromBytes(ByteBuf buffer) {
      this.key = buffer.readByte();
      this.mod = buffer.readByte();
   }

   public IMessage onMessage(final PacketItemKeyToServer message, final MessageContext ctx) {
      IThreadListener mainThread = ctx.getServerHandler().field_147369_b.func_71121_q();
      mainThread.func_152344_a(new Runnable() {
         @Override
         public void run() {
            World world = ctx.getServerHandler().field_147369_b.func_71121_q();
            if (world != null) {
               Entity player = ctx.getServerHandler().field_147369_b;
               if (player != null && player instanceof EntityPlayer) {
                  boolean flag = false;
                  if (((EntityPlayer)player).func_184614_ca() != null) {
                     if (message.key == 1 && ((EntityPlayer)player).func_184614_ca().func_77973_b() instanceof ICaster) {
                        CasterManager.toggleMisc(((EntityPlayer)player).func_184614_ca(), world, (EntityPlayer)player, message.mod);
                        flag = true;
                     }

                     if (!flag && message.key == 1 && ((EntityPlayer)player).func_184592_cb().func_77973_b() instanceof ICaster) {
                        CasterManager.toggleMisc(((EntityPlayer)player).func_184592_cb(), world, (EntityPlayer)player, message.mod);
                     }

                     if (message.key == 1 && ((EntityPlayer)player).func_184614_ca().func_77973_b() instanceof ItemElementalShovel) {
                        ItemElementalShovel var10000 = (ItemElementalShovel)((EntityPlayer)player).func_184614_ca().func_77973_b();
                        byte b = ItemElementalShovel.getOrientation(((EntityPlayer)player).func_184614_ca());
                        var10000 = (ItemElementalShovel)((EntityPlayer)player).func_184614_ca().func_77973_b();
                        ItemElementalShovel.setOrientation(((EntityPlayer)player).func_184614_ca(), (byte)(b + 1));
                        flag = true;
                     }
                  }
               }
            }
         }
      });
      return null;
   }
}

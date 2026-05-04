package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketPlayerFlagToServer implements IMessage, IMessageHandler<PacketPlayerFlagToServer, IMessage> {
   byte flag;

   public PacketPlayerFlagToServer() {
   }

   public PacketPlayerFlagToServer(EntityLivingBase player, int i) {
      this.flag = (byte)i;
   }

   public void toBytes(ByteBuf buffer) {
      buffer.writeByte(this.flag);
   }

   public void fromBytes(ByteBuf buffer) {
      this.flag = buffer.readByte();
   }

   public IMessage onMessage(final PacketPlayerFlagToServer message, final MessageContext ctx) {
      IThreadListener mainThread = ctx.getServerHandler().field_147369_b.func_71121_q();
      mainThread.func_152344_a(new Runnable() {
         @Override
         public void run() {
            if (ctx.getServerHandler().field_147369_b != null) {
               EntityPlayer player = ctx.getServerHandler().field_147369_b;
               switch (message.flag) {
                  case 1:
                     player.field_70143_R = 0.0F;
               }
            }
         }
      });
      return null;
   }
}

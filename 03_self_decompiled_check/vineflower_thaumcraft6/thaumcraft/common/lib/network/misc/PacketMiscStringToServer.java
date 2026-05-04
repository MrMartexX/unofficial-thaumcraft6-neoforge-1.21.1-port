package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.container.ContainerLogistics;

public class PacketMiscStringToServer implements IMessage, IMessageHandler<PacketMiscStringToServer, IMessage> {
   private int id;
   private String text;

   public PacketMiscStringToServer() {
   }

   public PacketMiscStringToServer(int id, String text) {
      this.id = id;
      this.text = text;
   }

   public void toBytes(ByteBuf buffer) {
      buffer.writeInt(this.id);
      ByteBufUtils.writeUTF8String(buffer, this.text);
   }

   public void fromBytes(ByteBuf buffer) {
      this.id = buffer.readInt();
      this.text = ByteBufUtils.readUTF8String(buffer);
   }

   public IMessage onMessage(final PacketMiscStringToServer message, final MessageContext ctx) {
      IThreadListener mainThread = ctx.getServerHandler().field_147369_b.func_71121_q();
      mainThread.func_152344_a(new Runnable() {
         @Override
         public void run() {
            EntityPlayerMP player = ctx.getServerHandler().field_147369_b;
            if (PacketMiscStringToServer.this.id == 0 && player.field_71070_bA instanceof ContainerLogistics) {
               ContainerLogistics container = (ContainerLogistics)player.field_71070_bA;
               container.searchText = message.text;
               container.refreshItemList(true);
            }
         }
      });
      return null;
   }
}

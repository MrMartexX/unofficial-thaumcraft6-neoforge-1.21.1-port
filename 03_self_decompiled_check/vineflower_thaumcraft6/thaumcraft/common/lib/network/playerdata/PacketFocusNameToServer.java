package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;

public class PacketFocusNameToServer implements IMessage, IMessageHandler<PacketFocusNameToServer, IMessage> {
   private long loc;
   private String name;

   public PacketFocusNameToServer() {
   }

   public PacketFocusNameToServer(BlockPos pos, String name) {
      this.loc = pos.func_177986_g();
      this.name = name;
   }

   public void toBytes(ByteBuf buffer) {
      buffer.writeLong(this.loc);
      ByteBufUtils.writeUTF8String(buffer, this.name);
   }

   public void fromBytes(ByteBuf buffer) {
      this.loc = buffer.readLong();
      this.name = ByteBufUtils.readUTF8String(buffer);
   }

   public IMessage onMessage(final PacketFocusNameToServer message, final MessageContext ctx) {
      IThreadListener mainThread = ctx.getServerHandler().field_147369_b.func_71121_q();
      mainThread.func_152344_a(new Runnable() {
         @Override
         public void run() {
            if (ctx.getServerHandler().field_147369_b != null) {
               BlockPos pos = BlockPos.func_177969_a(message.loc);
               TileEntity rt = ctx.getServerHandler().field_147369_b.field_70170_p.func_175625_s(pos);
               if (rt != null && rt instanceof TileFocalManipulator) {
                  ((TileFocalManipulator)rt).focusName = message.name;
                  ((TileFocalManipulator)rt).func_70296_d();
               }
            }
         }
      });
      return null;
   }
}

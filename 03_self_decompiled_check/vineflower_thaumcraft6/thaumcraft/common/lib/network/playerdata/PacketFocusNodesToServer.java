package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.tiles.crafting.FocusElementNode;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;

public class PacketFocusNodesToServer implements IMessage, IMessageHandler<PacketFocusNodesToServer, IMessage> {
   private long loc;
   private HashMap<Integer, FocusElementNode> data = new HashMap<>();
   private String name;

   public PacketFocusNodesToServer() {
   }

   public PacketFocusNodesToServer(BlockPos pos, HashMap<Integer, FocusElementNode> data, String name) {
      this.loc = pos.func_177986_g();
      this.data = data;
      this.name = name;
   }

   public void toBytes(ByteBuf buffer) {
      buffer.writeLong(this.loc);
      buffer.writeByte(this.data.size());

      for (FocusElementNode node : this.data.values()) {
         Utils.writeNBTTagCompoundToBuffer(buffer, node.serialize());
      }

      ByteBufUtils.writeUTF8String(buffer, this.name);
   }

   public void fromBytes(ByteBuf buffer) {
      this.loc = buffer.readLong();
      int m = buffer.readByte();

      for (int a = 0; a < m; a++) {
         FocusElementNode node = new FocusElementNode();
         node.deserialize(Utils.readNBTTagCompoundFromBuffer(buffer));
         this.data.put(node.id, node);
      }

      this.name = ByteBufUtils.readUTF8String(buffer);
   }

   public IMessage onMessage(final PacketFocusNodesToServer message, final MessageContext ctx) {
      IThreadListener mainThread = ctx.getServerHandler().field_147369_b.func_71121_q();
      mainThread.func_152344_a(new Runnable() {
         @Override
         public void run() {
            if (ctx.getServerHandler().field_147369_b != null) {
               BlockPos pos = BlockPos.func_177969_a(message.loc);
               TileEntity rt = ctx.getServerHandler().field_147369_b.field_70170_p.func_175625_s(pos);
               if (rt != null && rt instanceof TileFocalManipulator) {
                  ((TileFocalManipulator)rt).data.clear();
                  ((TileFocalManipulator)rt).data = message.data;
                  ((TileFocalManipulator)rt).focusName = message.name;
                  ((TileFocalManipulator)rt).func_70296_d();
               }
            }
         }
      });
      return null;
   }
}

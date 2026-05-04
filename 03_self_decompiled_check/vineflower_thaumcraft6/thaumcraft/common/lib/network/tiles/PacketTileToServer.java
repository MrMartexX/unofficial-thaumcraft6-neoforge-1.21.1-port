package thaumcraft.common.lib.network.tiles;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.tiles.TileThaumcraft;

public class PacketTileToServer implements IMessage, IMessageHandler<PacketTileToServer, IMessage> {
   private long pos;
   private NBTTagCompound nbt;

   public PacketTileToServer() {
   }

   public PacketTileToServer(BlockPos pos, NBTTagCompound nbt) {
      this.pos = pos.func_177986_g();
      this.nbt = nbt;
   }

   public void toBytes(ByteBuf buffer) {
      buffer.writeLong(this.pos);
      Utils.writeNBTTagCompoundToBuffer(buffer, this.nbt);
   }

   public void fromBytes(ByteBuf buffer) {
      this.pos = buffer.readLong();
      this.nbt = Utils.readNBTTagCompoundFromBuffer(buffer);
   }

   public IMessage onMessage(final PacketTileToServer message, final MessageContext ctx) {
      IThreadListener mainThread = ctx.getServerHandler().field_147369_b.func_71121_q();
      mainThread.func_152344_a(new Runnable() {
         @Override
         public void run() {
            World world = ctx.getServerHandler().field_147369_b.func_71121_q();
            BlockPos bp = BlockPos.func_177969_a(message.pos);
            if (world != null && bp != null) {
               TileEntity te = world.func_175625_s(bp);
               if (te != null && te instanceof TileThaumcraft) {
                  ((TileThaumcraft)te).messageFromClient(message.nbt == null ? new NBTTagCompound() : message.nbt, ctx.getServerHandler().field_147369_b);
               }
            }
         }
      });
      return null;
   }
}

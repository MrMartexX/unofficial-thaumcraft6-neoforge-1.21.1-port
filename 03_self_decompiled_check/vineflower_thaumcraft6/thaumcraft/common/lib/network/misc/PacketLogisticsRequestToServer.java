package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.common.lib.utils.Utils;

public class PacketLogisticsRequestToServer implements IMessage, IMessageHandler<PacketLogisticsRequestToServer, IMessage> {
   private BlockPos pos;
   private ItemStack stack;
   private EnumFacing side;
   private int stacksize;

   public PacketLogisticsRequestToServer() {
   }

   public PacketLogisticsRequestToServer(BlockPos pos, EnumFacing side, ItemStack stack, int size) {
      this.pos = pos;
      this.stack = stack;
      this.side = side;
      this.stacksize = size;
   }

   public void toBytes(ByteBuf buffer) {
      if (this.pos != null && this.side != null) {
         buffer.writeBoolean(true);
         buffer.writeLong(this.pos.func_177986_g());
         buffer.writeByte(this.side.func_176745_a());
      } else {
         buffer.writeBoolean(false);
      }

      Utils.writeItemStackToBuffer(buffer, this.stack);
      buffer.writeInt(this.stacksize);
   }

   public void fromBytes(ByteBuf buffer) {
      if (buffer.readBoolean()) {
         this.pos = BlockPos.func_177969_a(buffer.readLong());
         this.side = EnumFacing.values()[buffer.readByte()];
      }

      this.stack = Utils.readItemStackFromBuffer(buffer);
      this.stacksize = buffer.readInt();
   }

   public IMessage onMessage(final PacketLogisticsRequestToServer message, final MessageContext ctx) {
      IThreadListener mainThread = ctx.getServerHandler().field_147369_b.func_71121_q();
      mainThread.func_152344_a(new Runnable() {
         @Override
         public void run() {
            World world = ctx.getServerHandler().field_147369_b.func_71121_q();
            Entity player = ctx.getServerHandler().field_147369_b;

            for (int ui = 0; message.stacksize > 0; ui++) {
               ItemStack s = message.stack.func_77946_l();
               s.func_190920_e(Math.min(message.stacksize, s.func_77976_d()));
               PacketLogisticsRequestToServer var5 = message;
               var5.stacksize = var5.stacksize - s.func_190916_E();
               if (message.pos != null) {
                  GolemHelper.requestProvisioning(world, message.pos, message.side, s, ui);
               } else {
                  GolemHelper.requestProvisioning(world, ctx.getServerHandler().field_147369_b, s, ui);
               }
            }
         }
      });
      return null;
   }
}

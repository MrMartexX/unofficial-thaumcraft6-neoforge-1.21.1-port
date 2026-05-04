package thaumcraft.common.lib.network.tiles;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.Thaumcraft;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.tiles.TileThaumcraft;

public class PacketTileToClient implements IMessage, IMessageHandler<PacketTileToClient, IMessage> {
   private long pos;
   private NBTTagCompound nbt;

   public PacketTileToClient() {
   }

   public PacketTileToClient(BlockPos pos, NBTTagCompound nbt) {
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

   public IMessage onMessage(final PacketTileToClient message, MessageContext ctx) {
      Minecraft.func_71410_x().func_152344_a(new Runnable() {
         @Override
         public void run() {
            World world = Thaumcraft.proxy.getClientWorld();
            BlockPos bp = BlockPos.func_177969_a(message.pos);
            if (world != null && bp != null) {
               TileEntity te = world.func_175625_s(bp);
               if (te != null && te instanceof TileThaumcraft) {
                  ((TileThaumcraft)te).messageFromServer(message.nbt == null ? new NBTTagCompound() : message.nbt);
               }
            }
         }
      });
      return null;
   }
}

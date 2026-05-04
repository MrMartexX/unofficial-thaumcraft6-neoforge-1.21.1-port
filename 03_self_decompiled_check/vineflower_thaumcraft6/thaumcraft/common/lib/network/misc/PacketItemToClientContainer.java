package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.utils.Utils;

public class PacketItemToClientContainer implements IMessage, IMessageHandler<PacketItemToClientContainer, IMessage> {
   private int windowId;
   private int slot;
   private ItemStack item;

   public PacketItemToClientContainer() {
   }

   public PacketItemToClientContainer(int windowIdIn, int slotIn, ItemStack itemIn) {
      this.windowId = windowIdIn;
      this.slot = slotIn;
      this.item = itemIn;
   }

   public void toBytes(ByteBuf dos) {
      dos.writeInt(this.windowId);
      dos.writeInt(this.slot);
      Utils.writeItemStackToBuffer(dos, this.item);
   }

   public void fromBytes(ByteBuf dat) {
      this.windowId = dat.readInt();
      this.slot = dat.readInt();
      this.item = Utils.readItemStackFromBuffer(dat);
   }

   public IMessage onMessage(final PacketItemToClientContainer message, MessageContext ctx) {
      Minecraft.func_71410_x()
         .func_152344_a(
            new Runnable() {
               @Override
               public void run() {
                  try {
                     if (Minecraft.func_71410_x().field_71439_g.field_71070_bA != null
                        && Minecraft.func_71410_x().field_71439_g.field_71070_bA.field_75152_c == message.windowId) {
                        Minecraft.func_71410_x().field_71439_g.field_71070_bA.func_75141_a(message.slot, message.item);
                     }
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
               }
            }
         );
      return null;
   }
}

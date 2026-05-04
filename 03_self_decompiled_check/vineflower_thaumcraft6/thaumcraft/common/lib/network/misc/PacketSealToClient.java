package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.Thaumcraft;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.lib.utils.Utils;

public class PacketSealToClient implements IMessage, IMessageHandler<PacketSealToClient, IMessage> {
   BlockPos pos;
   EnumFacing face;
   String type;
   long area;
   boolean[] props = null;
   boolean blacklist;
   byte filtersize;
   NonNullList<ItemStack> filter;
   NonNullList<Integer> filterStackSize;
   byte priority;
   byte color;
   boolean locked;
   boolean redstone;
   String owner;

   public PacketSealToClient() {
   }

   public PacketSealToClient(ISealEntity se) {
      this.pos = se.getSealPos().pos;
      this.face = se.getSealPos().face;
      this.type = se.getSeal() == null ? "REMOVE" : se.getSeal().getKey();
      if (se.getSeal() != null && se.getSeal() instanceof ISealConfigArea) {
         this.area = se.getArea().func_177986_g();
      }

      if (se.getSeal() != null && se.getSeal() instanceof ISealConfigToggles) {
         ISealConfigToggles cp = (ISealConfigToggles)se.getSeal();
         this.props = new boolean[cp.getToggles().length];

         for (int a = 0; a < cp.getToggles().length; a++) {
            this.props[a] = cp.getToggles()[a].getValue();
         }
      }

      if (se.getSeal() != null && se.getSeal() instanceof ISealConfigFilter) {
         ISealConfigFilter cp = (ISealConfigFilter)se.getSeal();
         this.blacklist = cp.isBlacklist();
         this.filtersize = (byte)cp.getFilterSize();
         this.filter = cp.getInv();
         this.filterStackSize = cp.getSizes();
      }

      this.priority = se.getPriority();
      this.color = se.getColor();
      this.locked = se.isLocked();
      this.redstone = se.isRedstoneSensitive();
      this.owner = se.getOwner();
   }

   public void toBytes(ByteBuf dos) {
      dos.writeLong(this.pos.func_177986_g());
      dos.writeByte(this.face.ordinal());
      dos.writeByte(this.priority);
      dos.writeByte(this.color);
      dos.writeBoolean(this.locked);
      dos.writeBoolean(this.redstone);
      ByteBufUtils.writeUTF8String(dos, this.owner);
      ByteBufUtils.writeUTF8String(dos, this.type);
      dos.writeBoolean(this.blacklist);
      dos.writeByte(this.filtersize);

      for (int a = 0; a < this.filtersize; a++) {
         Utils.writeItemStackToBuffer(dos, (ItemStack)this.filter.get(a));
         dos.writeShort((Integer)this.filterStackSize.get(a));
      }

      if (this.area != 0L) {
         dos.writeLong(this.area);
      }

      if (this.props != null) {
         for (boolean b : this.props) {
            dos.writeBoolean(b);
         }
      }
   }

   public void fromBytes(ByteBuf dat) {
      this.pos = BlockPos.func_177969_a(dat.readLong());
      this.face = EnumFacing.field_82609_l[dat.readByte()];
      this.priority = dat.readByte();
      this.color = dat.readByte();
      this.locked = dat.readBoolean();
      this.redstone = dat.readBoolean();
      this.owner = ByteBufUtils.readUTF8String(dat);
      this.type = ByteBufUtils.readUTF8String(dat);
      this.blacklist = dat.readBoolean();
      this.filtersize = dat.readByte();
      this.filter = NonNullList.func_191197_a(this.filtersize, ItemStack.field_190927_a);
      this.filterStackSize = NonNullList.func_191197_a(this.filtersize, 0);

      for (int a = 0; a < this.filtersize; a++) {
         this.filter.set(a, Utils.readItemStackFromBuffer(dat));
         this.filterStackSize.set(a, Integer.valueOf(dat.readShort()));
      }

      if (!this.type.equals("REMOVE") && SealHandler.getSeal(this.type) != null) {
         if (SealHandler.getSeal(this.type) instanceof ISealConfigArea) {
            try {
               this.area = dat.readLong();
            } catch (Exception var4) {
            }
         }

         if (SealHandler.getSeal(this.type) instanceof ISealConfigToggles) {
            try {
               ISealConfigToggles cp = (ISealConfigToggles)SealHandler.getSeal(this.type);
               this.props = new boolean[cp.getToggles().length];

               for (int a = 0; a < cp.getToggles().length; a++) {
                  this.props[a] = dat.readBoolean();
               }
            } catch (Exception var5) {
            }
         }
      }
   }

   public IMessage onMessage(PacketSealToClient message, MessageContext ctx) {
      if (message.type.equals("REMOVE")) {
         SealHandler.removeSealEntity(Thaumcraft.proxy.getClientWorld(), new SealPos(message.pos, message.face), true);
      } else {
         try {
            SealEntity seal = new SealEntity(
               Thaumcraft.proxy.getClientWorld(), new SealPos(message.pos, message.face), (ISeal)SealHandler.getSeal(message.type).getClass().newInstance()
            );
            if (message.area != 0L) {
               seal.setArea(BlockPos.func_177969_a(message.area));
            }

            if (message.props != null && seal.getSeal() instanceof ISealConfigToggles) {
               ISealConfigToggles cp = (ISealConfigToggles)seal.getSeal();

               for (int a = 0; a < message.props.length; a++) {
                  cp.setToggle(a, message.props[a]);
               }
            }

            if (seal.getSeal() instanceof ISealConfigFilter) {
               ISealConfigFilter cp = (ISealConfigFilter)seal.getSeal();
               cp.setBlacklist(message.blacklist);

               for (int a = 0; a < message.filtersize; a++) {
                  cp.setFilterSlot(a, (ItemStack)message.filter.get(a));
                  cp.setFilterSlotSize(a, (Integer)message.filterStackSize.get(a));
               }
            }

            seal.setPriority(message.priority);
            seal.setColor(message.color);
            seal.setLocked(message.locked);
            seal.setRedstoneSensitive(message.redstone);
            seal.setOwner(message.owner);
            SealHandler.addSealEntity(Thaumcraft.proxy.getClientWorld(), seal);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return null;
   }
}

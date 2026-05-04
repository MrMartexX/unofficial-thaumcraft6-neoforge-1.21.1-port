package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.client.gui.ResearchToast;
import thaumcraft.common.lib.utils.Utils;

public class PacketSyncKnowledge implements IMessage, IMessageHandler<PacketSyncKnowledge, IMessage> {
   protected NBTTagCompound data;

   public PacketSyncKnowledge() {
   }

   public PacketSyncKnowledge(EntityPlayer player) {
      IPlayerKnowledge pk = ThaumcraftCapabilities.getKnowledge(player);
      this.data = (NBTTagCompound)pk.serializeNBT();

      for (String key : pk.getResearchList()) {
         pk.clearResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP);
      }
   }

   public void toBytes(ByteBuf buffer) {
      Utils.writeNBTTagCompoundToBuffer(buffer, this.data);
   }

   public void fromBytes(ByteBuf buffer) {
      this.data = Utils.readNBTTagCompoundFromBuffer(buffer);
   }

   @SideOnly(Side.CLIENT)
   public IMessage onMessage(final PacketSyncKnowledge message, MessageContext ctx) {
      Minecraft.func_71410_x().func_152344_a(new Runnable() {
         @Override
         public void run() {
            EntityPlayer player = Minecraft.func_71410_x().field_71439_g;
            IPlayerKnowledge pk = ThaumcraftCapabilities.getKnowledge(player);
            pk.deserializeNBT(message.data);

            for (String key : pk.getResearchList()) {
               if (pk.hasResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP)) {
                  ResearchEntry ri = ResearchCategories.getResearch(key);
                  if (ri != null) {
                     Minecraft.func_71410_x().func_193033_an().func_192988_a(new ResearchToast(ri));
                  }
               }

               pk.clearResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP);
            }
         }
      });
      return null;
   }
}

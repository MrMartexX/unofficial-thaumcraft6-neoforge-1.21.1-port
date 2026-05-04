package thaumcraft.common.lib.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import thaumcraft.common.lib.network.playerdata.PacketSyncKnowledge;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;

@EventBusSubscriber
public class EventHandlerNetwork {
   @SubscribeEvent
   public void playerLoggedInEvent(PlayerLoggedInEvent event) {
      Side side = FMLCommonHandler.instance().getEffectiveSide();
      if (side == Side.SERVER) {
         EntityPlayer p = event.player;
         PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(p), (EntityPlayerMP)p);
         PacketHandler.INSTANCE.sendTo(new PacketSyncKnowledge(p), (EntityPlayerMP)p);
      }
   }
}

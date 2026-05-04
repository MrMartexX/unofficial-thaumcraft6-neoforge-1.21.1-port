package thaumcraft.common.lib.events;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import thaumcraft.api.casters.ICaster;
import thaumcraft.common.golems.ItemGolemBell;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketFocusChangeToServer;
import thaumcraft.common.lib.network.misc.PacketItemKeyToServer;

@EventBusSubscriber(Side.CLIENT)
public class KeyHandler {
   public static KeyBinding keyF = new KeyBinding("Change Caster Focus", 33, "key.categories.thaumcraft");
   public static KeyBinding keyG = new KeyBinding("Misc Caster Toggle", 34, "key.categories.thaumcraft");
   private static boolean keyPressedF = false;
   private boolean keyPressedH = false;
   private static boolean keyPressedG = false;
   public static boolean radialActive = false;
   public static boolean radialLock = false;
   public static long lastPressF = 0L;
   public static long lastPressH = 0L;
   public static long lastPressG = 0L;

   public KeyHandler() {
      ClientRegistry.registerKeyBinding(keyF);
      ClientRegistry.registerKeyBinding(keyG);
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void playerTick(PlayerTickEvent event) {
      if (event.side != Side.SERVER) {
         if (event.phase == Phase.START) {
            if (keyF.func_151470_d()) {
               if (FMLClientHandler.instance().getClient().field_71415_G) {
                  EntityPlayer player = event.player;
                  if (player != null) {
                     if (!keyPressedF) {
                        lastPressF = System.currentTimeMillis();
                        radialLock = false;
                     }

                     if (!radialLock
                        && (
                           player.func_184614_ca() != null && player.func_184614_ca().func_77973_b() instanceof ICaster
                              || player.func_184592_cb() != null && player.func_184592_cb().func_77973_b() instanceof ICaster
                        )) {
                        if (player.func_70093_af()) {
                           PacketHandler.INSTANCE.sendToServer(new PacketFocusChangeToServer("REMOVE"));
                        } else {
                           radialActive = true;
                        }
                     } else if (player.func_184614_ca() != null && player.func_184614_ca().func_77973_b() instanceof ItemGolemBell && !keyPressedF) {
                        PacketHandler.INSTANCE.sendToServer(new PacketItemKeyToServer(0));
                     }
                  }

                  keyPressedF = true;
               }
            } else {
               radialActive = false;
               if (keyPressedF) {
                  lastPressF = System.currentTimeMillis();
               }

               keyPressedF = false;
            }

            if (keyG.func_151470_d()) {
               if (FMLClientHandler.instance().getClient().field_71415_G) {
                  EntityPlayer player = event.player;
                  if (player != null && !keyPressedG) {
                     lastPressG = System.currentTimeMillis();
                     PacketHandler.INSTANCE.sendToServer(new PacketItemKeyToServer(1, Keyboard.isKeyDown(29) ? 1 : (Keyboard.isKeyDown(42) ? 2 : 0)));
                  }

                  keyPressedG = true;
               }
            } else {
               if (keyPressedG) {
                  lastPressG = System.currentTimeMillis();
               }

               keyPressedG = false;
            }
         }
      }
   }
}

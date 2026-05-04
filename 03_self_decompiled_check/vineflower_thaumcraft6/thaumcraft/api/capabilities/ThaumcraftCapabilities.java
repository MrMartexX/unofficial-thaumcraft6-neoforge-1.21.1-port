package thaumcraft.api.capabilities;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class ThaumcraftCapabilities {
   @CapabilityInject(IPlayerKnowledge.class)
   public static final Capability<IPlayerKnowledge> KNOWLEDGE = null;
   @CapabilityInject(IPlayerWarp.class)
   public static final Capability<IPlayerWarp> WARP = null;

   public static IPlayerKnowledge getKnowledge(@Nonnull EntityPlayer player) {
      return (IPlayerKnowledge)player.getCapability(KNOWLEDGE, null);
   }

   public static boolean knowsResearch(@Nonnull EntityPlayer player, @Nonnull String... research) {
      for (String r : research) {
         if (r.contains("&&")) {
            String[] rr = r.split("&&");
            if (!knowsResearch(player, rr)) {
               return false;
            }
         } else if (r.contains("||")) {
            String[] rr = r.split("||");

            for (String str : rr) {
               if (knowsResearch(player, str)) {
                  return true;
               }
            }
         } else if (!getKnowledge(player).isResearchKnown(r)) {
            return false;
         }
      }

      return true;
   }

   public static boolean knowsResearchStrict(@Nonnull EntityPlayer player, @Nonnull String... research) {
      for (String r : research) {
         if (r.contains("&&")) {
            String[] rr = r.split("&&");
            if (!knowsResearchStrict(player, rr)) {
               return false;
            }
         } else if (r.contains("||")) {
            String[] rr = r.split("||");

            for (String str : rr) {
               if (knowsResearchStrict(player, str)) {
                  return true;
               }
            }
         } else if (r.contains("@")) {
            if (!getKnowledge(player).isResearchKnown(r)) {
               return false;
            }
         } else if (!getKnowledge(player).isResearchComplete(r)) {
            return false;
         }
      }

      return true;
   }

   public static IPlayerWarp getWarp(@Nonnull EntityPlayer player) {
      return (IPlayerWarp)player.getCapability(WARP, null);
   }
}

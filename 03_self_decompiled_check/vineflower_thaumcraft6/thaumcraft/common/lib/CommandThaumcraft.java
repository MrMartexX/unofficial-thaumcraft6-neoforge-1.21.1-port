package thaumcraft.common.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketWarpMessage;
import thaumcraft.common.lib.research.ResearchManager;

public class CommandThaumcraft extends CommandBase {
   private List aliases = new ArrayList();

   public CommandThaumcraft() {
      this.aliases.add("thaumcraft");
      this.aliases.add("thaum");
      this.aliases.add("tc");
   }

   public String func_71517_b() {
      return "thaumcraft";
   }

   public List<String> func_71514_a() {
      return this.aliases;
   }

   public String func_71518_a(ICommandSender icommandsender) {
      return "/thaumcraft <action> [<player> [<params>]]";
   }

   public int func_82362_a() {
      return 2;
   }

   public boolean func_82358_a(String[] astring, int i) {
      return i == 1;
   }

   public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length == 0) {
         sender.func_145747_a(new TextComponentTranslation("§cInvalid arguments", new Object[0]));
         sender.func_145747_a(new TextComponentTranslation("§cUse /thaumcraft help to get help", new Object[0]));
      } else {
         if (args[0].equalsIgnoreCase("reload")) {
            for (ResearchCategory rc : ResearchCategories.researchCategories.values()) {
               rc.research.clear();
            }

            ResearchManager.parseAllResearch();
            sender.func_145747_a(new TextComponentTranslation("§5Success!", new Object[0]));
         } else if (args[0].equalsIgnoreCase("help")) {
            sender.func_145747_a(new TextComponentTranslation("§3You can also use /thaum or /tc instead of /thaumcraft.", new Object[0]));
            sender.func_145747_a(new TextComponentTranslation("§3Use this to give research to a player.", new Object[0]));
            sender.func_145747_a(new TextComponentTranslation("  /thaumcraft research <list|player> <list|all|reset|<research>>", new Object[0]));
            sender.func_145747_a(new TextComponentTranslation("§3Use this to remove research from a player.", new Object[0]));
            sender.func_145747_a(new TextComponentTranslation("  /thaumcraft research <player> revoke <research>", new Object[0]));
            sender.func_145747_a(new TextComponentTranslation("§3Use this to give set a players warp level.", new Object[0]));
            sender.func_145747_a(new TextComponentTranslation("  /thaumcraft warp <player> <add|set> <amount> <PERM|TEMP>", new Object[0]));
            sender.func_145747_a(new TextComponentTranslation("  not specifying perm or temp will just add normal warp", new Object[0]));
            sender.func_145747_a(new TextComponentTranslation("§3Use this to reload json research data", new Object[0]));
            sender.func_145747_a(new TextComponentTranslation("  /thaumcraft reload", new Object[0]));
         } else if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("research") && args[1].equalsIgnoreCase("list")) {
               this.listResearch(sender);
            } else {
               EntityPlayerMP entityplayermp = func_184888_a(server, sender, args[1]);
               if (args[0].equalsIgnoreCase("research")) {
                  if (args.length == 3) {
                     if (args[2].equalsIgnoreCase("list")) {
                        this.listAllResearch(sender, entityplayermp);
                     } else if (args[2].equalsIgnoreCase("all")) {
                        this.giveAllResearch(sender, entityplayermp);
                     } else if (args[2].equalsIgnoreCase("reset")) {
                        this.resetResearch(sender, entityplayermp);
                     } else {
                        this.giveResearch(sender, entityplayermp, args[2]);
                     }
                  } else if (args.length == 4) {
                     if (args[2].equalsIgnoreCase("revoke")) {
                        this.revokeResearch(sender, entityplayermp, args[3]);
                     }
                  } else {
                     sender.func_145747_a(new TextComponentTranslation("§cInvalid arguments", new Object[0]));
                     sender.func_145747_a(new TextComponentTranslation("§cUse /thaumcraft research <list|player> <list|all|reset|<research>>", new Object[0]));
                  }
               } else if (args[0].equalsIgnoreCase("warp")) {
                  if (args.length >= 4 && args[2].equalsIgnoreCase("set")) {
                     int i = func_180528_a(args[3], 0);
                     this.setWarp(sender, entityplayermp, i, args.length == 5 ? args[4] : "");
                  } else if (args.length >= 4 && args[2].equalsIgnoreCase("add")) {
                     int i = func_175764_a(args[3], -100, 100);
                     this.addWarp(sender, entityplayermp, i, args.length == 5 ? args[4] : "");
                  } else {
                     sender.func_145747_a(new TextComponentTranslation("§cInvalid arguments", new Object[0]));
                     sender.func_145747_a(new TextComponentTranslation("§cUse /thaumcraft warp <player> <add|set> <amount> <PERM|TEMP>", new Object[0]));
                  }
               } else {
                  sender.func_145747_a(new TextComponentTranslation("§cInvalid arguments", new Object[0]));
                  sender.func_145747_a(new TextComponentTranslation("§cUse /thaumcraft help to get help", new Object[0]));
               }
            }
         } else {
            sender.func_145747_a(new TextComponentTranslation("§cInvalid arguments", new Object[0]));
            sender.func_145747_a(new TextComponentTranslation("§cUse /thaumcraft help to get help", new Object[0]));
         }
      }
   }

   private void setWarp(ICommandSender icommandsender, EntityPlayerMP player, int i, String type) {
      if (type.equalsIgnoreCase("PERM")) {
         ThaumcraftCapabilities.getWarp(player).set(IPlayerWarp.EnumWarpType.PERMANENT, i);
      } else if (type.equalsIgnoreCase("TEMP")) {
         ThaumcraftCapabilities.getWarp(player).set(IPlayerWarp.EnumWarpType.TEMPORARY, i);
      } else {
         ThaumcraftCapabilities.getWarp(player).set(IPlayerWarp.EnumWarpType.NORMAL, i);
      }

      ThaumcraftCapabilities.getWarp(player).sync(player);
      player.func_145747_a(new TextComponentTranslation("§5" + icommandsender.func_70005_c_() + " set your warp to " + i, new Object[0]));
      icommandsender.func_145747_a(new TextComponentTranslation("§5Success!", new Object[0]));
   }

   private void addWarp(ICommandSender icommandsender, EntityPlayerMP player, int i, String type) {
      if (type.equalsIgnoreCase("PERM")) {
         ThaumcraftCapabilities.getWarp(player).add(IPlayerWarp.EnumWarpType.PERMANENT, i);
      } else if (type.equalsIgnoreCase("TEMP")) {
         ThaumcraftCapabilities.getWarp(player).add(IPlayerWarp.EnumWarpType.TEMPORARY, i);
      } else {
         ThaumcraftCapabilities.getWarp(player).add(IPlayerWarp.EnumWarpType.NORMAL, i);
      }

      ThaumcraftCapabilities.getWarp(player).sync(player);
      PacketHandler.INSTANCE.sendTo(new PacketWarpMessage(player, (byte)0, i), player);
      player.func_145747_a(new TextComponentTranslation("§5" + icommandsender.func_70005_c_() + " added " + i + " warp to your total.", new Object[0]));
      icommandsender.func_145747_a(new TextComponentTranslation("§5Success!", new Object[0]));
   }

   private void listResearch(ICommandSender icommandsender) {
      for (ResearchCategory cat : ResearchCategories.researchCategories.values()) {
         for (ResearchEntry ri : cat.research.values()) {
            icommandsender.func_145747_a(new TextComponentTranslation("§5" + ri.getKey(), new Object[0]));
         }
      }
   }

   void giveResearch(ICommandSender icommandsender, EntityPlayerMP player, String research) {
      if (ResearchCategories.getResearch(research) != null) {
         giveRecursiveResearch(player, research);
         ThaumcraftCapabilities.getKnowledge(player).sync(player);
         player.func_145747_a(
            new TextComponentTranslation("§5" + icommandsender.func_70005_c_() + " gave you " + research + " research and its requisites.", new Object[0])
         );
         icommandsender.func_145747_a(new TextComponentTranslation("§5Success!", new Object[0]));
      } else {
         icommandsender.func_145747_a(new TextComponentTranslation("§cResearch does not exist.", new Object[0]));
      }
   }

   public static void giveRecursiveResearch(EntityPlayer player, String research) {
      if (research.contains("@")) {
         int i = research.indexOf("@");
         research = research.substring(0, i);
      }

      ResearchEntry res = ResearchCategories.getResearch(research);
      IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
      if (!knowledge.isResearchComplete(research)) {
         if (res != null && res.getParents() != null) {
            for (String rsi : res.getParentsStripped()) {
               giveRecursiveResearch(player, rsi);
            }
         }

         if (res != null && res.getStages() != null) {
            for (ResearchStage page : res.getStages()) {
               if (page.getResearch() != null) {
                  for (String gr : page.getResearch()) {
                     ResearchManager.completeResearch(player, gr);
                  }
               }
            }
         }

         ResearchManager.completeResearch(player, research);

         for (String rc : ResearchCategories.researchCategories.keySet()) {
            for (ResearchEntry ri : ResearchCategories.getResearchCategory(rc).research.values()) {
               if (ri.getStages() != null) {
                  for (ResearchStage stage : ri.getStages()) {
                     if (stage.getResearch() != null && Arrays.asList(stage.getResearch()).contains(research)) {
                        ThaumcraftCapabilities.getKnowledge(player).setResearchFlag(ri.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE);
                        break;
                     }
                  }
               }
            }
         }

         if (res != null && res.getSiblings() != null) {
            for (String rsi : res.getSiblings()) {
               giveRecursiveResearch(player, rsi);
            }
         }
      }
   }

   private void revokeResearch(ICommandSender icommandsender, EntityPlayerMP player, String research) {
      if (ResearchCategories.getResearch(research) != null) {
         revokeRecursiveResearch(player, research);
         ThaumcraftCapabilities.getKnowledge(player).sync(player);
         player.func_145747_a(
            new TextComponentTranslation("§5" + icommandsender.func_70005_c_() + " removed " + research + " research and its children.", new Object[0])
         );
         icommandsender.func_145747_a(new TextComponentTranslation("§5Success!", new Object[0]));
      } else {
         icommandsender.func_145747_a(new TextComponentTranslation("§cResearch does not exist.", new Object[0]));
      }
   }

   public static void revokeRecursiveResearch(EntityPlayer player, String research) {
      if (research.contains("@")) {
         int i = research.indexOf("@");
         research = research.substring(0, i);
      }

      ResearchEntry res = ResearchCategories.getResearch(research);
      IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
      if (knowledge.isResearchComplete(research)) {
         for (String rc : ResearchCategories.researchCategories.keySet()) {
            for (ResearchEntry ri : ResearchCategories.getResearchCategory(rc).research.values()) {
               if (ri != null && ri.getParents() != null && knowledge.isResearchComplete(ri.getKey())) {
                  for (String rsi : ri.getParentsStripped()) {
                     if (rsi.equals(research)) {
                        revokeRecursiveResearch(player, ri.getKey());
                     }
                  }
               }
            }
         }

         ThaumcraftCapabilities.getKnowledge(player).removeResearch(research);
      }
   }

   void listAllResearch(ICommandSender icommandsender, EntityPlayerMP player) {
      String ss = "";

      for (String key : ThaumcraftCapabilities.getKnowledge(player).getResearchList()) {
         if (ss.length() != 0) {
            ss = ss + ", ";
         }

         ss = ss + key;
      }

      icommandsender.func_145747_a(new TextComponentTranslation("§5Research for " + player.func_70005_c_(), new Object[0]));
      icommandsender.func_145747_a(new TextComponentTranslation("§5" + ss, new Object[0]));
   }

   void giveAllResearch(ICommandSender icommandsender, EntityPlayerMP player) {
      for (ResearchCategory cat : ResearchCategories.researchCategories.values()) {
         for (ResearchEntry ri : cat.research.values()) {
            giveRecursiveResearch(player, ri.getKey());
         }
      }

      player.func_145747_a(new TextComponentTranslation("§5" + icommandsender.func_70005_c_() + " has given you all research.", new Object[0]));
      icommandsender.func_145747_a(new TextComponentTranslation("§5Success!", new Object[0]));
   }

   void resetResearch(ICommandSender icommandsender, EntityPlayerMP player) {
      ThaumcraftCapabilities.getKnowledge(player).clear();

      for (ResearchCategory cat : ResearchCategories.researchCategories.values()) {
         for (ResearchEntry ri : cat.research.values()) {
            if (ri.hasMeta(ResearchEntry.EnumResearchMeta.AUTOUNLOCK)) {
               ResearchManager.completeResearch(player, ri.getKey(), false);
            }
         }
      }

      player.func_145747_a(new TextComponentTranslation("§5" + icommandsender.func_70005_c_() + " has reset all your research.", new Object[0]));
      icommandsender.func_145747_a(new TextComponentTranslation("§5Success!", new Object[0]));
      ThaumcraftCapabilities.getKnowledge(player).sync(player);
   }
}

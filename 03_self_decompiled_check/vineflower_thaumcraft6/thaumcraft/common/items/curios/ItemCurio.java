package thaumcraft.common.items.curios;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;

public class ItemCurio extends ItemTCBase {
   public ItemCurio() {
      super("curio", "arcane", "preserved", "ancient", "eldritch", "knowledge", "twisted", "rites");
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return EnumRarity.UNCOMMON;
   }

   @SideOnly(Side.CLIENT)
   public void func_77624_a(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(I18n.func_74838_a("item.curio.text"));
   }

   public ActionResult<ItemStack> func_77659_a(World worldIn, EntityPlayer player, EnumHand hand) {
      worldIn.func_184148_a(
         (EntityPlayer)null,
         player.field_70165_t,
         player.field_70163_u,
         player.field_70161_v,
         SoundsTC.learn,
         SoundCategory.NEUTRAL,
         0.5F,
         0.4F / (field_77697_d.nextFloat() * 0.4F + 0.8F)
      );
      if (!worldIn.field_72995_K) {
         int oProg = IPlayerKnowledge.EnumKnowledgeType.OBSERVATION.getProgression();
         int tProg = IPlayerKnowledge.EnumKnowledgeType.THEORY.getProgression();
         switch (player.func_184586_b(hand).func_77952_i()) {
            case 1:
               ThaumcraftApi.internalMethods
                  .addKnowledge(
                     player,
                     IPlayerKnowledge.EnumKnowledgeType.OBSERVATION,
                     ResearchCategories.getResearchCategory("ALCHEMY"),
                     MathHelper.func_76136_a(player.func_70681_au(), oProg / 2, oProg)
                  );
               ThaumcraftApi.internalMethods
                  .addKnowledge(
                     player,
                     IPlayerKnowledge.EnumKnowledgeType.THEORY,
                     ResearchCategories.getResearchCategory("ALCHEMY"),
                     MathHelper.func_76136_a(player.func_70681_au(), tProg / 3, tProg / 2)
                  );
               break;
            case 2:
               ThaumcraftApi.internalMethods
                  .addKnowledge(
                     player,
                     IPlayerKnowledge.EnumKnowledgeType.OBSERVATION,
                     ResearchCategories.getResearchCategory("GOLEMANCY"),
                     MathHelper.func_76136_a(player.func_70681_au(), oProg / 2, oProg)
                  );
               ThaumcraftApi.internalMethods
                  .addKnowledge(
                     player,
                     IPlayerKnowledge.EnumKnowledgeType.THEORY,
                     ResearchCategories.getResearchCategory("GOLEMANCY"),
                     MathHelper.func_76136_a(player.func_70681_au(), tProg / 3, tProg / 2)
                  );
               break;
            case 3:
               ThaumcraftApi.internalMethods
                  .addKnowledge(
                     player,
                     IPlayerKnowledge.EnumKnowledgeType.OBSERVATION,
                     ResearchCategories.getResearchCategory("ELDRITCH"),
                     MathHelper.func_76136_a(player.func_70681_au(), oProg / 2, oProg)
                  );
               ThaumcraftApi.internalMethods
                  .addKnowledge(
                     player,
                     IPlayerKnowledge.EnumKnowledgeType.THEORY,
                     ResearchCategories.getResearchCategory("ELDRITCH"),
                     MathHelper.func_76136_a(player.func_70681_au(), tProg / 3, tProg / 2)
                  );
               ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.NORMAL);
               ThaumcraftApi.internalMethods.addWarpToPlayer(player, 5, IPlayerWarp.EnumWarpType.TEMPORARY);
               break;
            case 4:
               ThaumcraftApi.internalMethods
                  .addKnowledge(
                     player,
                     IPlayerKnowledge.EnumKnowledgeType.OBSERVATION,
                     ResearchCategories.getResearchCategory("INFUSION"),
                     MathHelper.func_76136_a(player.func_70681_au(), oProg / 2, oProg)
                  );
               ThaumcraftApi.internalMethods
                  .addKnowledge(
                     player,
                     IPlayerKnowledge.EnumKnowledgeType.THEORY,
                     ResearchCategories.getResearchCategory("INFUSION"),
                     MathHelper.func_76136_a(player.func_70681_au(), tProg / 3, tProg / 2)
                  );
               break;
            case 5:
               ThaumcraftApi.internalMethods
                  .addKnowledge(
                     player,
                     IPlayerKnowledge.EnumKnowledgeType.OBSERVATION,
                     ResearchCategories.getResearchCategory("ARTIFICE"),
                     MathHelper.func_76136_a(player.func_70681_au(), oProg / 2, oProg)
                  );
               ThaumcraftApi.internalMethods
                  .addKnowledge(
                     player,
                     IPlayerKnowledge.EnumKnowledgeType.THEORY,
                     ResearchCategories.getResearchCategory("ARTIFICE"),
                     MathHelper.func_76136_a(player.func_70681_au(), tProg / 3, tProg / 2)
                  );
               break;
            case 6:
               int aw = ThaumcraftApi.internalMethods.getActualWarp(player);
               if (aw <= 20) {
                  player.func_145747_a(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.func_74838_a("fail.crimsonrites")));
                  return super.func_77659_a(worldIn, player, hand);
               }

               IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
               if (!knowledge.isResearchKnown("CrimsonRites")) {
                  ThaumcraftApi.internalMethods.completeResearch(player, "CrimsonRites");
               }

               ThaumcraftApi.internalMethods
                  .addKnowledge(
                     player,
                     IPlayerKnowledge.EnumKnowledgeType.OBSERVATION,
                     ResearchCategories.getResearchCategory("ELDRITCH"),
                     MathHelper.func_76136_a(player.func_70681_au(), oProg / 2, oProg)
                  );
               ThaumcraftApi.internalMethods
                  .addKnowledge(
                     player,
                     IPlayerKnowledge.EnumKnowledgeType.THEORY,
                     ResearchCategories.getResearchCategory("ELDRITCH"),
                     MathHelper.func_76136_a(player.func_70681_au(), tProg / 3, tProg / 2)
                  );
               ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.NORMAL);
               ThaumcraftApi.internalMethods.addWarpToPlayer(player, 5, IPlayerWarp.EnumWarpType.TEMPORARY);
               if (player.func_70681_au().nextBoolean()) {
                  ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.PERMANENT);
               }
               break;
            default:
               ThaumcraftApi.internalMethods
                  .addKnowledge(
                     player,
                     IPlayerKnowledge.EnumKnowledgeType.OBSERVATION,
                     ResearchCategories.getResearchCategory("AUROMANCY"),
                     MathHelper.func_76136_a(player.func_70681_au(), oProg / 2, oProg)
                  );
               ThaumcraftApi.internalMethods
                  .addKnowledge(
                     player,
                     IPlayerKnowledge.EnumKnowledgeType.THEORY,
                     ResearchCategories.getResearchCategory("AUROMANCY"),
                     MathHelper.func_76136_a(player.func_70681_au(), tProg / 3, tProg / 2)
                  );
         }

         ResearchCategory[] rc = ResearchCategories.researchCategories.values().toArray(new ResearchCategory[0]);
         ThaumcraftApi.internalMethods
            .addKnowledge(
               player,
               IPlayerKnowledge.EnumKnowledgeType.OBSERVATION,
               rc[player.func_70681_au().nextInt(rc.length)],
               MathHelper.func_76136_a(player.func_70681_au(), oProg / 2, oProg)
            );
         ThaumcraftApi.internalMethods
            .addKnowledge(
               player,
               IPlayerKnowledge.EnumKnowledgeType.THEORY,
               rc[player.func_70681_au().nextInt(rc.length)],
               MathHelper.func_76136_a(player.func_70681_au(), tProg / 3, tProg / 2)
            );
         if (!player.field_71075_bZ.field_75098_d) {
            player.func_184586_b(hand).func_190918_g(1);
         }

         player.func_145747_a(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.func_74838_a("tc.knowledge.gained")));
      }

      player.func_71029_a(StatList.func_188057_b(this));
      return super.func_77659_a(worldIn, player, hand);
   }
}

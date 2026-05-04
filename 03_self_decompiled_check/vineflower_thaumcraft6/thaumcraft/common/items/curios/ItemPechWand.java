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
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;

public class ItemPechWand extends ItemTCBase {
   public ItemPechWand() {
      super("pech_wand");
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return EnumRarity.RARE;
   }

   @SideOnly(Side.CLIENT)
   public void func_77624_a(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(I18n.func_74838_a("item.curio.text"));
   }

   public ActionResult<ItemStack> func_77659_a(World worldIn, EntityPlayer player, EnumHand hand) {
      IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
      if (!knowledge.isResearchKnown("BASEAUROMANCY")) {
         if (!worldIn.field_72995_K) {
            player.func_145747_a(new TextComponentString(TextFormatting.RED + I18n.func_74838_a("not.pechwand")));
         }

         return super.func_77659_a(worldIn, player, hand);
      } else {
         if (!player.field_71075_bZ.field_75098_d) {
            player.func_184586_b(hand).func_190918_g(1);
         }

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
            if (!knowledge.isResearchKnown("FOCUSPECH")) {
               ThaumcraftApi.internalMethods.progressResearch(player, "FOCUSPECH");
               player.func_145747_a(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.func_74838_a("got.pechwand")));
            }

            int oProg = IPlayerKnowledge.EnumKnowledgeType.OBSERVATION.getProgression();
            ResearchCategory[] rc = ResearchCategories.researchCategories.values().toArray(new ResearchCategory[0]);
            ThaumcraftApi.internalMethods
               .addKnowledge(
                  player,
                  IPlayerKnowledge.EnumKnowledgeType.OBSERVATION,
                  rc[player.func_70681_au().nextInt(rc.length)],
                  MathHelper.func_76136_a(player.func_70681_au(), oProg / 3, oProg / 2)
               );
            int tProg = IPlayerKnowledge.EnumKnowledgeType.THEORY.getProgression();
            ThaumcraftApi.internalMethods
               .addKnowledge(
                  player,
                  IPlayerKnowledge.EnumKnowledgeType.THEORY,
                  rc[player.func_70681_au().nextInt(rc.length)],
                  MathHelper.func_76136_a(player.func_70681_au(), tProg / 5, tProg / 4)
               );
         }

         player.func_71029_a(StatList.func_188057_b(this));
         return super.func_77659_a(worldIn, player, hand);
      }
   }
}

package thaumcraft.common.items.curios;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.CommandThaumcraft;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.research.ResearchManager;

public class ItemThaumonomicon extends ItemTCBase {
   public ItemThaumonomicon() {
      super("thaumonomicon", "normal", "cheat");
      this.func_77627_a(true);
      this.func_77625_d(1);
   }

   @Override
   public void func_150895_a(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (tab == ConfigItems.TABTC || tab == CreativeTabs.field_78027_g) {
         items.add(new ItemStack(this, 1, 0));
         if (ModConfig.CONFIG_MISC.allowCheatSheet) {
            items.add(new ItemStack(this, 1, 1));
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public void func_77624_a(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.func_77624_a(stack, worldIn, tooltip, flagIn);
      if (stack.func_77952_i() == 1) {
         tooltip.add(TextFormatting.DARK_PURPLE + "Creative only");
      }
   }

   public ActionResult<ItemStack> func_77659_a(World world, EntityPlayer player, EnumHand hand) {
      if (!world.field_72995_K) {
         if (ModConfig.CONFIG_MISC.allowCheatSheet && player.func_184586_b(hand).func_77952_i() == 1) {
            for (ResearchCategory cat : ResearchCategories.researchCategories.values()) {
               for (ResearchEntry ri : cat.research.values()) {
                  CommandThaumcraft.giveRecursiveResearch(player, ri.getKey());
               }
            }
         } else {
            for (ResearchCategory cat : ResearchCategories.researchCategories.values()) {
               for (ResearchEntry ri : cat.research.values()) {
                  if (ThaumcraftCapabilities.knowsResearch(player, ri.getKey()) && ri.getSiblings() != null) {
                     for (String sib : ri.getSiblings()) {
                        if (!ThaumcraftCapabilities.knowsResearch(player, sib)) {
                           ResearchManager.completeResearch(player, sib);
                        }
                     }
                  }
               }
            }
         }

         ThaumcraftCapabilities.getKnowledge(player).sync((EntityPlayerMP)player);
      } else {
         world.func_184134_a(player.field_70165_t, player.field_70163_u, player.field_70161_v, SoundsTC.page, SoundCategory.PLAYERS, 1.0F, 1.0F, false);
      }

      player.openGui(Thaumcraft.instance, 12, world, 0, 0, 0);
      return new ActionResult(EnumActionResult.SUCCESS, player.func_184586_b(hand));
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return itemstack.func_77952_i() != 1 ? EnumRarity.UNCOMMON : EnumRarity.EPIC;
   }
}

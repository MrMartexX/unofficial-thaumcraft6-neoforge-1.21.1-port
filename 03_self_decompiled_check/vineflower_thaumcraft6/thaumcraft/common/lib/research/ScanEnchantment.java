package thaumcraft.common.lib.research;

import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ScanningManager;

public class ScanEnchantment implements IScanThing {
   Enchantment enchantment;

   public ScanEnchantment(Enchantment ench) {
      this.enchantment = ench;
   }

   @Override
   public boolean checkThing(EntityPlayer player, Object obj) {
      return this.getEnchantment(player, obj) != null;
   }

   private Enchantment getEnchantment(EntityPlayer player, Object obj) {
      if (obj == null) {
         return null;
      }

      ItemStack is = ScanningManager.getItemFromParms(player, obj);
      if (is != null && !is.func_190926_b()) {
         Map<Enchantment, Integer> e = EnchantmentHelper.func_82781_a(is);

         for (Enchantment ench : e.keySet()) {
            if (ench == this.enchantment) {
               return ench;
            }
         }
      }

      return null;
   }

   @Override
   public String getResearchKey(EntityPlayer player, Object obj) {
      return "!" + this.enchantment.func_77320_a();
   }
}

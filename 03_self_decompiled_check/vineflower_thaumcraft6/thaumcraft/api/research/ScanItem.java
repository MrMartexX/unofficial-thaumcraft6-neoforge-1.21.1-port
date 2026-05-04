package thaumcraft.api.research;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftInvHelper;

public class ScanItem implements IScanThing {
   String research;
   ItemStack stack;

   public ScanItem(String research, ItemStack stack) {
      this.research = research;
      this.stack = stack;
   }

   @Override
   public boolean checkThing(EntityPlayer player, Object obj) {
      if (obj == null) {
         return false;
      }

      ItemStack is = null;
      if (obj instanceof ItemStack) {
         is = (ItemStack)obj;
      }

      if (obj instanceof EntityItem && ((EntityItem)obj).func_92059_d() != null) {
         is = ((EntityItem)obj).func_92059_d();
      }

      return is != null && !is.func_190926_b() && ThaumcraftInvHelper.areItemStacksEqualForCrafting(is, this.stack);
   }

   @Override
   public String getResearchKey(EntityPlayer player, Object object) {
      return this.research;
   }
}

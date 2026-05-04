package thaumcraft.api.golems.seals;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ISealConfigFilter {
   NonNullList<ItemStack> getInv();

   NonNullList<Integer> getSizes();

   int getFilterSize();

   ItemStack getFilterSlot(int var1);

   int getFilterSlotSize(int var1);

   void setFilterSlot(int var1, ItemStack var2);

   void setFilterSlotSize(int var1, int var2);

   boolean isBlacklist();

   void setBlacklist(boolean var1);

   boolean hasStacksizeLimiters();
}

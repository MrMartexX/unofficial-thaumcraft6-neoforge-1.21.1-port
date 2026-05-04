package thaumcraft.api.casters;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ICaster {
   float getConsumptionModifier(ItemStack var1, EntityPlayer var2, boolean var3);

   boolean consumeVis(ItemStack var1, EntityPlayer var2, float var3, boolean var4, boolean var5);

   Item getFocus(ItemStack var1);

   ItemStack getFocusStack(ItemStack var1);

   void setFocus(ItemStack var1, ItemStack var2);

   ItemStack getPickedBlock(ItemStack var1);
}

package thaumcraft.common.golems.seals;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.seals.ISealConfigToggles;

public class SealEmptyAdvanced extends SealEmpty implements ISealConfigToggles {
   ResourceLocation icon = new ResourceLocation("thaumcraft", "items/seals/seal_empty_advanced");

   @Override
   public String getKey() {
      return "thaumcraft:empty_advanced";
   }

   @Override
   public int getFilterSize() {
      return 9;
   }

   @Override
   public ResourceLocation getSealIcon() {
      return this.icon;
   }

   @Override
   public NonNullList<ItemStack> getInv(int c) {
      if (this.getToggles()[4].value && !this.isBlacklist()) {
         ArrayList<ItemStack> w = new ArrayList<>();

         for (ItemStack s : super.getInv()) {
            if (s != null && !s.func_190926_b()) {
               w.add(s);
            }
         }

         if (w.size() > 0) {
            int i = Math.abs(c % w.size());
            return NonNullList.func_191197_a(1, w.get(i));
         }
      }

      return super.getInv();
   }

   @Override
   public NonNullList<ItemStack> getInv() {
      return super.getInv();
   }

   @Override
   public ISealConfigToggles.SealToggle[] getToggles() {
      return this.props;
   }

   @Override
   public int[] getGuiCategories() {
      return new int[]{1, 3, 0, 4};
   }

   @Override
   public void setToggle(int indx, boolean value) {
      this.props[indx].setValue(value);
   }

   @Override
   public EnumGolemTrait[] getRequiredTags() {
      return new EnumGolemTrait[]{EnumGolemTrait.SMART};
   }
}

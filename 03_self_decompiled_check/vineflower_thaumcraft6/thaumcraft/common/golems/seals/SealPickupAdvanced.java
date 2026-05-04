package thaumcraft.common.golems.seals;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.seals.ISealConfigToggles;

public class SealPickupAdvanced extends SealPickup implements ISealConfigToggles {
   ResourceLocation icon = new ResourceLocation("thaumcraft", "items/seals/seal_pickup_advanced");

   @Override
   public String getKey() {
      return "thaumcraft:pickup_advanced";
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
   public int[] getGuiCategories() {
      return new int[]{2, 1, 3, 0, 4};
   }

   @Override
   public EnumGolemTrait[] getRequiredTags() {
      return new EnumGolemTrait[]{EnumGolemTrait.SMART};
   }

   @Override
   public ISealConfigToggles.SealToggle[] getToggles() {
      return this.props;
   }

   @Override
   public void setToggle(int indx, boolean value) {
      this.props[indx].setValue(value);
   }
}

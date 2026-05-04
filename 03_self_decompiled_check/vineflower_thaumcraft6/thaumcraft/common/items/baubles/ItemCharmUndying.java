package thaumcraft.common.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.ItemTCBase;

public class ItemCharmUndying extends ItemTCBase implements IBauble {
   public ItemCharmUndying() {
      super("charm_undying");
      this.field_77777_bU = 1;
      this.canRepair = false;
      this.func_77656_e(0);
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return EnumRarity.RARE;
   }

   @Override
   public BaubleType getBaubleType(ItemStack itemstack) {
      return BaubleType.CHARM;
   }
}

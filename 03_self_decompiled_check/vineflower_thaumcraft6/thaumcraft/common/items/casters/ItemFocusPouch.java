package thaumcraft.common.items.casters;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.ItemTCBase;

public class ItemFocusPouch extends ItemTCBase implements IBauble {
   public ItemFocusPouch() {
      super("focus_pouch");
      this.func_77625_d(1);
      this.func_77627_a(false);
      this.func_77656_e(0);
   }

   public boolean func_77651_p() {
      return true;
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return EnumRarity.UNCOMMON;
   }

   public boolean func_77636_d(ItemStack stack1) {
      return false;
   }

   public ActionResult<ItemStack> func_77659_a(World worldIn, EntityPlayer playerIn, EnumHand hand) {
      if (!worldIn.field_72995_K) {
         playerIn.openGui(
            Thaumcraft.instance,
            5,
            worldIn,
            MathHelper.func_76128_c(playerIn.field_70165_t),
            MathHelper.func_76128_c(playerIn.field_70163_u),
            MathHelper.func_76128_c(playerIn.field_70161_v)
         );
      }

      return super.func_77659_a(worldIn, playerIn, hand);
   }

   public NonNullList<ItemStack> getInventory(ItemStack item) {
      NonNullList<ItemStack> stackList = NonNullList.func_191197_a(18, ItemStack.field_190927_a);
      if (item.func_77942_o()) {
         ItemStackHelper.func_191283_b(item.func_77978_p(), stackList);
      }

      return stackList;
   }

   public void setInventory(ItemStack item, NonNullList<ItemStack> stackList) {
      if (item.func_77978_p() == null) {
         item.func_77982_d(new NBTTagCompound());
      }

      ItemStackHelper.func_191282_a(item.func_77978_p(), stackList);
   }

   @Override
   public BaubleType getBaubleType(ItemStack itemstack) {
      return BaubleType.BELT;
   }

   @Override
   public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
   }

   @Override
   public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
   }

   @Override
   public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
   }

   @Override
   public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
      return true;
   }

   @Override
   public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
      return true;
   }
}

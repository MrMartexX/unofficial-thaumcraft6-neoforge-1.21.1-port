package thaumcraft.api.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;

public class ItemGenericEssentiaContainer extends Item implements IEssentiaContainerItem {
   protected int base = 1;

   public ItemGenericEssentiaContainer(int base) {
      this.base = base;
      this.func_77625_d(64);
      this.func_77627_a(true);
      this.func_77656_e(0);
   }

   public void func_150895_a(CreativeTabs tab, NonNullList<ItemStack> items) {
      for (Aspect tag : Aspect.aspects.values()) {
         ItemStack i = new ItemStack(this);
         this.setAspects(i, new AspectList().add(tag, this.base));
         items.add(i);
      }
   }

   @Override
   public AspectList getAspects(ItemStack itemstack) {
      if (itemstack.func_77942_o()) {
         AspectList aspects = new AspectList();
         aspects.readFromNBT(itemstack.func_77978_p());
         return aspects.size() > 0 ? aspects : null;
      } else {
         return null;
      }
   }

   @Override
   public void setAspects(ItemStack itemstack, AspectList aspects) {
      if (!itemstack.func_77942_o()) {
         itemstack.func_77982_d(new NBTTagCompound());
      }

      aspects.writeToNBT(itemstack.func_77978_p());
   }

   @Override
   public boolean ignoreContainedAspects() {
      return false;
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
      if (!world.field_72995_K && !stack.func_77942_o()) {
         Aspect[] displayAspects = Aspect.aspects.values().toArray(new Aspect[0]);
         this.setAspects(stack, new AspectList().add(displayAspects[world.field_73012_v.nextInt(displayAspects.length)], this.base));
      }

      super.func_77663_a(stack, world, entity, par4, par5);
   }

   public void func_77622_d(ItemStack stack, World world, EntityPlayer player) {
      if (!world.field_72995_K && !stack.func_77942_o()) {
         Aspect[] displayAspects = Aspect.aspects.values().toArray(new Aspect[0]);
         this.setAspects(stack, new AspectList().add(displayAspects[world.field_73012_v.nextInt(displayAspects.length)], this.base));
      }
   }
}

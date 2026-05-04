package thaumcraft.common.entities;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntitySpecialItem extends EntityItem {
   public EntitySpecialItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack) {
      super(par1World);
      this.func_70105_a(0.25F, 0.25F);
      this.func_70107_b(par2, par4, par6);
      this.func_92058_a(par8ItemStack);
      this.field_70177_z = (float)(Math.random() * 360.0);
      this.field_70159_w = (float)(Math.random() * 0.2F - 0.1F);
      this.field_70181_x = 0.2F;
      this.field_70179_y = (float)(Math.random() * 0.2F - 0.1F);
   }

   public EntitySpecialItem(World par1World) {
      super(par1World);
      this.func_70105_a(0.25F, 0.25F);
   }

   public void func_70071_h_() {
      if (this.field_70173_aa > 1) {
         if (this.field_70181_x > 0.0) {
            this.field_70181_x *= 0.9F;
         }

         this.field_70181_x += 0.04F;
         super.func_70071_h_();
      }
   }

   public boolean func_70097_a(DamageSource source, float damage) {
      return source.func_94541_c() ? false : super.func_70097_a(source, damage);
   }
}

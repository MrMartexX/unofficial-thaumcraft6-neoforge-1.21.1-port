package thaumcraft.common.tiles.devices;

import java.util.ArrayList;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;

public class TileInfernalFurnace extends TileThaumcraftInventory {
   public int furnaceCookTime;
   public int furnaceMaxCookTime;
   public int speedyTime;
   public int facingX = -5;
   public int facingZ = -5;

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB(
         this.func_174877_v().func_177958_n() - 1.3,
         this.func_174877_v().func_177956_o() - 1.3,
         this.func_174877_v().func_177952_p() - 1.3,
         this.func_174877_v().func_177958_n() + 2.3,
         this.func_174877_v().func_177956_o() + 2.3,
         this.func_174877_v().func_177952_p() + 2.3
      );
   }

   public TileInfernalFurnace() {
      super(32);
      this.furnaceCookTime = 0;
      this.furnaceMaxCookTime = 0;
      this.speedyTime = 0;
   }

   @Override
   public int[] func_180463_a(EnumFacing par1) {
      return par1 == EnumFacing.UP ? super.func_180463_a(par1) : new int[0];
   }

   @Override
   public boolean func_180461_b(int par1, ItemStack stack2, EnumFacing par3) {
      return false;
   }

   @Override
   public void func_145839_a(NBTTagCompound nbttagcompound) {
      super.func_145839_a(nbttagcompound);
      this.furnaceCookTime = nbttagcompound.func_74765_d("CookTime");
      this.speedyTime = nbttagcompound.func_74765_d("SpeedyTime");
   }

   @Override
   public NBTTagCompound func_189515_b(NBTTagCompound nbttagcompound) {
      super.func_189515_b(nbttagcompound);
      nbttagcompound.func_74777_a("CookTime", (short)this.furnaceCookTime);
      nbttagcompound.func_74777_a("SpeedyTime", (short)this.speedyTime);
      return nbttagcompound;
   }

   @Override
   public void func_73660_a() {
      super.func_73660_a();
      if (this.facingX == -5) {
         this.setFacing();
      }

      if (!this.field_145850_b.field_72995_K) {
         boolean cookedflag = false;
         if (this.furnaceCookTime > 0) {
            this.furnaceCookTime--;
            cookedflag = true;
         }

         if (this.furnaceMaxCookTime <= 0) {
            this.furnaceMaxCookTime = this.calcCookTime();
         }

         if (this.furnaceCookTime > this.furnaceMaxCookTime) {
            this.furnaceCookTime = this.furnaceMaxCookTime;
         }

         if (this.furnaceCookTime <= 0 && cookedflag) {
            for (int a = 0; a < this.func_70302_i_(); a++) {
               if (this.func_70301_a(a) != null && !this.func_70301_a(a).func_190926_b()) {
                  ItemStack itemstack = FurnaceRecipes.func_77602_a().func_151395_a(this.func_70301_a(a));
                  if (itemstack != null && !itemstack.func_190926_b()) {
                     if (this.speedyTime > 0) {
                        this.speedyTime--;
                     }

                     this.ejectItem(itemstack.func_77946_l(), this.func_70301_a(a));
                     this.field_145850_b.func_175641_c(this.func_174877_v(), BlocksTC.infernalFurnace, 3, 0);
                     if (this.func_145831_w().field_73012_v.nextInt(20) == 0) {
                        AuraHelper.polluteAura(this.func_145831_w(), this.func_174877_v().func_177972_a(this.getFacing().func_176734_d()), 1.0F, true);
                     }

                     this.func_70298_a(a, 1);
                     break;
                  }

                  this.func_70299_a(a, ItemStack.field_190927_a);
               }
            }
         }

         if (this.speedyTime <= 0) {
            this.speedyTime = (int)AuraHelper.drainVis(this.func_145831_w(), this.func_174877_v(), 20.0F, false);
         }

         if (this.furnaceCookTime == 0 && !cookedflag) {
            for (int a = 0; a < this.func_70302_i_(); a++) {
               if (this.canSmelt(this.func_70301_a(a))) {
                  this.furnaceMaxCookTime = this.calcCookTime();
                  this.furnaceCookTime = this.furnaceMaxCookTime;
                  break;
               }
            }
         }
      }
   }

   private int getBellows() {
      int bellows = 0;

      for (EnumFacing dir : EnumFacing.field_82609_l) {
         if (dir != EnumFacing.UP) {
            BlockPos p2 = this.field_174879_c.func_177967_a(dir, 2);
            TileEntity tile = this.field_145850_b.func_175625_s(p2);
            if (tile != null
               && tile instanceof TileBellows
               && BlockStateUtils.getFacing(this.field_145850_b.func_180495_p(p2)) == dir.func_176734_d()
               && this.field_145850_b.func_175687_A(p2) == 0) {
               bellows++;
            }
         }
      }

      return Math.min(4, bellows);
   }

   private int calcCookTime() {
      int b = this.getBellows();
      if (b > 0) {
         b = (20 - (b - 1)) * b;
      }

      return Math.max(10, (this.speedyTime > 0 ? 80 : 140) - b);
   }

   public ItemStack addItemsToInventory(ItemStack items) {
      if (this.canSmelt(items)) {
         items = ThaumcraftInvHelper.insertStackAt(this.func_145831_w(), this.func_174877_v(), EnumFacing.UP, items, false);
      } else {
         this.destroyItem();
         items = ItemStack.field_190927_a;
      }

      return items;
   }

   private void destroyItem() {
      this.field_145850_b
         .func_184134_a(
            this.field_174879_c.func_177958_n() + 0.5F,
            this.field_174879_c.func_177956_o() + 0.5F,
            this.field_174879_c.func_177952_p() + 0.5F,
            SoundEvents.field_187659_cY,
            SoundCategory.BLOCKS,
            0.3F,
            2.6F + (this.field_145850_b.field_73012_v.nextFloat() - this.field_145850_b.field_73012_v.nextFloat()) * 0.8F,
            false
         );
      double var21 = this.field_174879_c.func_177958_n() + this.field_145850_b.field_73012_v.nextFloat();
      double var22 = this.field_174879_c.func_177956_o() + 1;
      double var23 = this.field_174879_c.func_177952_p() + this.field_145850_b.field_73012_v.nextFloat();
      this.field_145850_b.func_175688_a(EnumParticleTypes.LAVA, var21, var22, var23, 0.0, 0.0, 0.0, new int[0]);
   }

   public void ejectItem(ItemStack items, ItemStack furnaceItemStack) {
      if (items != null && !items.func_190926_b()) {
         ArrayList<ItemStack> ejecti = new ArrayList<>();
         ejecti.add(items.func_77946_l());
         int bellows = this.getBellows() + 1;
         float lx = 0.5F;
         lx += this.facingX * 1.2F;
         float lz = 0.5F;
         lz += this.facingZ * 1.2F;
         float mx = 0.0F;
         float mz = 0.0F;

         for (int a = 0; a < bellows; a++) {
            ItemStack[] boni = this.getSmeltingBonus(furnaceItemStack);
            if (boni != null) {
               for (ItemStack bonus : boni) {
                  if (!bonus.func_190926_b() && bonus.func_190916_E() > 0) {
                     ejecti.add(bonus);
                  }
               }
            }
         }

         for (ItemStack outItem : ejecti) {
            if (!outItem.func_190926_b()) {
               EnumFacing facing = BlockStateUtils.getFacing(this.func_145832_p()).func_176734_d();
               InventoryUtils.ejectStackAt(this.func_145831_w(), this.func_174877_v(), facing, outItem);
            }
         }

         int cnt = items.func_190916_E();
         float xpf = FurnaceRecipes.func_77602_a().func_151398_b(items);
         if (xpf == 0.0F) {
            cnt = 0;
         } else if (xpf < 1.0F) {
            int var4 = MathHelper.func_76141_d(cnt * xpf);
            if (var4 < MathHelper.func_76123_f(cnt * xpf) && (float)Math.random() < cnt * xpf - var4) {
               var4++;
            }

            cnt = var4;
         }

         while (cnt > 0) {
            int var4 = EntityXPOrb.func_70527_a(cnt);
            cnt -= var4;
            EntityXPOrb xp = new EntityXPOrb(
               this.field_145850_b,
               this.field_174879_c.func_177958_n() + lx,
               this.field_174879_c.func_177956_o() + 0.4F,
               this.field_174879_c.func_177952_p() + lz,
               var4
            );
            mx = this.facingX == 0
               ? (this.field_145850_b.field_73012_v.nextFloat() - this.field_145850_b.field_73012_v.nextFloat()) * 0.025F
               : this.facingX * 0.13F;
            mz = this.facingZ == 0
               ? (this.field_145850_b.field_73012_v.nextFloat() - this.field_145850_b.field_73012_v.nextFloat()) * 0.025F
               : this.facingZ * 0.13F;
            xp.field_70159_w = mx;
            xp.field_70179_y = mz;
            xp.field_70181_x = 0.0;
            this.field_145850_b.func_72838_d(xp);
         }
      }
   }

   private ItemStack[] getSmeltingBonus(ItemStack in) {
      ArrayList<ItemStack> out = new ArrayList<>();

      for (ThaumcraftApi.SmeltBonus bonus : CommonInternals.smeltingBonus) {
         if (!(bonus.in instanceof ItemStack)) {
            for (int id : OreDictionary.getOreIDs(in)) {
               String od = OreDictionary.getOreName(id);
               if (((String)bonus.in).equals(od)) {
                  if (this.field_145850_b.field_73012_v.nextFloat() <= bonus.chance) {
                     ItemStack is = bonus.out.func_77946_l();
                     if (is.func_190916_E() < 1) {
                        is.func_190920_e(1);
                     }

                     out.add(is);
                  }
                  break;
               }
            }
         } else if (in.func_77973_b() == ((ItemStack)bonus.in).func_77973_b()
            && (in.func_77952_i() == ((ItemStack)bonus.in).func_77952_i() || ((ItemStack)bonus.in).func_77952_i() == 32767)
            && this.field_145850_b.field_73012_v.nextFloat() <= bonus.chance) {
            ItemStack is = bonus.out.func_77946_l();
            if (is.func_190916_E() < 1) {
               is.func_190920_e(1);
            }

            out.add(is);
         }
      }

      return out.toArray(new ItemStack[0]);
   }

   private boolean canSmelt(ItemStack stack) {
      return !FurnaceRecipes.func_77602_a().func_151395_a(stack).func_190926_b();
   }

   private void setFacing() {
      this.facingX = 0;
      this.facingZ = 0;
      EnumFacing face = this.getFacing().func_176734_d();
      this.facingX = face.func_82601_c();
      this.facingZ = face.func_82599_e();
   }

   public boolean func_145842_c(int i, int j) {
      if (i != 3) {
         return super.func_145842_c(i, j);
      }

      if (this.field_145850_b.field_72995_K) {
         for (int a = 0; a < 5; a++) {
            FXDispatcher.INSTANCE
               .furnaceLavaFx(
                  this.field_174879_c.func_177958_n(), this.field_174879_c.func_177956_o(), this.field_174879_c.func_177952_p(), this.facingX, this.facingZ
               );
            this.field_145850_b
               .func_184134_a(
                  this.field_174879_c.func_177958_n() + 0.5F,
                  this.field_174879_c.func_177956_o() + 0.5F,
                  this.field_174879_c.func_177952_p() + 0.5F,
                  SoundEvents.field_187662_cZ,
                  SoundCategory.BLOCKS,
                  0.1F + this.field_145850_b.field_73012_v.nextFloat() * 0.1F,
                  0.9F + this.field_145850_b.field_73012_v.nextFloat() * 0.15F,
                  false
               );
         }
      }

      return true;
   }
}

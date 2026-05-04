package thaumcraft.common.tiles.devices;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ConfigAspects;
import thaumcraft.common.container.slot.SlotPotion;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;

public class TilePotionSprayer extends TileThaumcraftInventory implements IAspectContainer, IEssentiaTransport {
   public AspectList recipe = new AspectList();
   public AspectList recipeProgress = new AspectList();
   public int charges = 0;
   public int color = 0;
   int counter = 0;
   boolean activated = false;
   int venting = 0;
   Aspect currentSuction = null;

   public TilePotionSprayer() {
      super(1);
   }

   @Override
   public void func_73660_a() {
      super.func_73660_a();
      this.counter++;
      EnumFacing facing = BlockStateUtils.getFacing(this.func_145832_p());
      if (this.field_145850_b.field_72995_K) {
         if (this.venting > 0) {
            this.venting--;

            for (int a = 0; a < this.venting / 2; a++) {
               float fx = 0.1F - this.field_145850_b.field_73012_v.nextFloat() * 0.2F;
               float fz = 0.1F - this.field_145850_b.field_73012_v.nextFloat() * 0.2F;
               float fy = 0.1F - this.field_145850_b.field_73012_v.nextFloat() * 0.2F;
               float fx2 = (float)(this.field_145850_b.field_73012_v.nextGaussian() * 0.06);
               float fz2 = (float)(this.field_145850_b.field_73012_v.nextGaussian() * 0.06);
               float fy2 = (float)(this.field_145850_b.field_73012_v.nextGaussian() * 0.06);
               FXDispatcher.INSTANCE
                  .drawVentParticles2(
                     this.field_174879_c.func_177958_n() + 0.5F + fx + facing.func_82601_c() / 2.0F,
                     this.field_174879_c.func_177956_o() + 0.5F + fy + facing.func_96559_d() / 2.0F,
                     this.field_174879_c.func_177952_p() + 0.5F + fz + facing.func_82599_e() / 2.0F,
                     fx2 + facing.func_82601_c() * 0.25,
                     fy2 + facing.func_96559_d() * 0.25,
                     fz2 + facing.func_82599_e() * 0.25,
                     this.color,
                     4.0F
                  );
            }
         }
      } else {
         if (this.counter % 5 == 0) {
            this.currentSuction = null;
            if (this.func_70301_a(0).func_190926_b() || this.charges >= 8) {
               return;
            }

            boolean done = true;

            for (Aspect aspect : this.recipe.getAspectsSortedByName()) {
               if (this.recipeProgress.getAmount(aspect) < this.recipe.getAmount(aspect)) {
                  this.currentSuction = aspect;
                  done = false;
                  break;
               }
            }

            if (done) {
               this.recipeProgress = new AspectList();
               this.charges++;
               this.syncTile(false);
               this.func_70296_d();
            } else if (this.currentSuction != null) {
               this.fill();
            }
         }

         if (!BlockStateUtils.isEnabled(this.func_145832_p())) {
            if (!this.activated && this.charges > 0) {
               this.charges--;
               List<PotionEffect> effects = PotionUtils.func_185189_a(this.func_70301_a(0));
               if (effects != null && !effects.isEmpty()) {
                  int area = 1;
                  BlockPos p = this.field_174879_c.func_177967_a(facing, 2);
                  List<EntityLivingBase> targets = this.field_145850_b
                     .func_72872_a(
                        EntityLivingBase.class,
                        new AxisAlignedBB(
                           p.func_177958_n() - area,
                           p.func_177956_o() - area,
                           p.func_177952_p() - area,
                           p.func_177958_n() + 1 + area,
                           p.func_177956_o() + 1 + area,
                           p.func_177952_p() + 1 + area
                        )
                     );
                  boolean lifted = false;
                  if (targets.size() > 0) {
                     for (EntityLivingBase e : targets) {
                        if (!e.field_70128_L && e.func_184603_cC()) {
                           for (PotionEffect potioneffect1 : effects) {
                              Potion potion = potioneffect1.func_188419_a();
                              if (potion.func_76403_b()) {
                                 potion.func_180793_a(null, null, e, potioneffect1.func_76458_c(), 1.0);
                              } else {
                                 e.func_70690_d(new PotionEffect(potion, potioneffect1.func_76459_b(), potioneffect1.func_76458_c()));
                              }
                           }
                        }
                     }
                  }
               }

               this.field_145850_b
                  .func_184133_a(
                     null,
                     this.field_174879_c,
                     SoundEvents.field_187659_cY,
                     SoundCategory.BLOCKS,
                     0.25F,
                     2.6F + (this.field_145850_b.field_73012_v.nextFloat() - this.field_145850_b.field_73012_v.nextFloat()) * 0.8F
                  );
               this.field_145850_b.func_175641_c(this.func_174877_v(), this.func_145838_q(), 0, 0);
               this.syncTile(false);
               this.func_70296_d();
            }

            this.activated = true;
         } else if (this.activated) {
            this.activated = false;
         }
      }
   }

   private void drawFX(EnumFacing facing, double c) {
   }

   public boolean func_145842_c(int i, int j) {
      if (i >= 0) {
         if (this.field_145850_b.field_72995_K) {
            this.venting = 15;
         }

         return true;
      } else {
         return super.func_145842_c(i, j);
      }
   }

   @Override
   public void readSyncNBT(NBTTagCompound nbt) {
      this.recipe = new AspectList();
      this.recipe.readFromNBT(nbt, "recipe");
      this.recipeProgress = new AspectList();
      this.recipeProgress.readFromNBT(nbt, "progress");
      this.charges = nbt.func_74762_e("charges");
      this.color = nbt.func_74762_e("color");
   }

   @Override
   public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
      this.recipe.writeToNBT(nbt, "recipe");
      this.recipeProgress.writeToNBT(nbt, "progress");
      nbt.func_74768_a("charges", this.charges);
      nbt.func_74768_a("color", this.color);
      return nbt;
   }

   @Override
   public boolean func_94041_b(int par1, ItemStack stack) {
      return stack != null && !stack.func_190926_b() && SlotPotion.isValidPotion(stack);
   }

   @Override
   public void func_70299_a(int par1, ItemStack stack) {
      super.func_70299_a(par1, stack);
      this.recalcAspects();
   }

   @Override
   public ItemStack func_70298_a(int par1, int par2) {
      ItemStack stack = super.func_70298_a(par1, par2);
      this.recalcAspects();
      return stack;
   }

   private void recalcAspects() {
      if (!this.field_145850_b.field_72995_K) {
         ItemStack stack = this.func_70301_a(0);
         this.color = 3355443;
         if (!this.field_145850_b.field_72995_K) {
            if (stack != null && !stack.func_190926_b()) {
               this.recipe = ConfigAspects.getPotionAspects(stack);
               this.color = this.getPotionColor(stack);
            } else {
               this.recipe = new AspectList();
            }

            this.charges = 0;
            this.recipe = AspectHelper.cullTags(this.recipe, 10);
            this.recipeProgress = new AspectList();
            this.syncTile(false);
            this.func_70296_d();
         }
      }
   }

   public int getPotionColor(ItemStack itemstack) {
      PotionType potion = PotionUtils.func_185191_c(itemstack);
      return potion != null ? PotionUtils.func_185183_a(potion) : 3355443;
   }

   void fill() {
      EnumFacing facing = BlockStateUtils.getFacing(this.func_145832_p());
      TileEntity te = null;
      IEssentiaTransport ic = null;

      for (int y = 0; y <= 1; y++) {
         for (EnumFacing dir : EnumFacing.field_82609_l) {
            if (dir != facing) {
               te = ThaumcraftApiHelper.getConnectableTile(this.field_145850_b, this.field_174879_c.func_177981_b(y), dir);
               if (te != null) {
                  ic = (IEssentiaTransport)te;
                  if (ic.getEssentiaAmount(dir.func_176734_d()) > 0
                     && ic.getSuctionAmount(dir.func_176734_d()) < this.getSuctionAmount(null)
                     && this.getSuctionAmount(null) >= ic.getMinimumSuction()) {
                     int ess = ic.takeEssentia(this.currentSuction, 1, dir.func_176734_d());
                     if (ess > 0) {
                        this.addToContainer(this.currentSuction, ess);
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public int addToContainer(Aspect tt, int am) {
      int ce = this.recipe.getAmount(tt) - this.recipeProgress.getAmount(tt);
      if (ce <= 0) {
         return am;
      }

      int add = Math.min(ce, am);
      this.recipeProgress.add(tt, add);
      this.syncTile(false);
      this.func_70296_d();
      return am - add;
   }

   @Override
   public boolean takeFromContainer(Aspect tt, int am) {
      return false;
   }

   @Override
   public boolean takeFromContainer(AspectList ot) {
      return false;
   }

   @Override
   public boolean doesContainerContain(AspectList ot) {
      return false;
   }

   @Override
   public boolean doesContainerContainAmount(Aspect tt, int am) {
      return this.recipeProgress.getAmount(tt) >= am;
   }

   @Override
   public int containerContains(Aspect tt) {
      return this.recipeProgress.getAmount(tt);
   }

   @Override
   public boolean doesContainerAccept(Aspect tag) {
      return true;
   }

   @Override
   public boolean isConnectable(EnumFacing face) {
      return face != BlockStateUtils.getFacing(this.func_145832_p());
   }

   @Override
   public boolean canInputFrom(EnumFacing face) {
      return face != BlockStateUtils.getFacing(this.func_145832_p());
   }

   @Override
   public boolean canOutputTo(EnumFacing face) {
      return false;
   }

   @Override
   public void setSuction(Aspect aspect, int amount) {
      this.currentSuction = aspect;
   }

   @Override
   public Aspect getSuctionType(EnumFacing loc) {
      return this.currentSuction;
   }

   @Override
   public int getSuctionAmount(EnumFacing loc) {
      return this.currentSuction != null ? 128 : 0;
   }

   @Override
   public Aspect getEssentiaType(EnumFacing loc) {
      return null;
   }

   @Override
   public int getEssentiaAmount(EnumFacing loc) {
      return 0;
   }

   @Override
   public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
      return this.canOutputTo(face) && this.takeFromContainer(aspect, amount) ? amount : 0;
   }

   @Override
   public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
      return this.canInputFrom(face) ? amount - this.addToContainer(aspect, amount) : 0;
   }

   @Override
   public int getMinimumSuction() {
      return 0;
   }

   @Override
   public AspectList getAspects() {
      return this.recipeProgress;
   }

   @Override
   public void setAspects(AspectList aspects) {
      this.recipeProgress = aspects;
   }
}

package thaumcraft.common.tiles.essentia;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.essentia.BlockSmelter;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;
import thaumcraft.common.tiles.devices.TileBellows;

public class TileSmelter extends TileThaumcraftInventory {
   private static final int[] slots_bottom = new int[]{1};
   private static final int[] slots_top = new int[0];
   private static final int[] slots_sides = new int[]{0};
   public AspectList aspects = new AspectList();
   public int vis;
   private int maxVis = 256;
   public int smeltTime = 100;
   boolean speedBoost = false;
   public int furnaceBurnTime;
   public int currentItemBurnTime;
   public int furnaceCookTime;
   int count = 0;
   int bellows = -1;

   public TileSmelter() {
      super(2);
   }

   @Override
   public void readSyncNBT(NBTTagCompound nbttagcompound) {
      this.furnaceBurnTime = nbttagcompound.func_74765_d("BurnTime");
   }

   @Override
   public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
      nbttagcompound.func_74777_a("BurnTime", (short)this.furnaceBurnTime);
      return nbttagcompound;
   }

   @Override
   public void func_145839_a(NBTTagCompound nbtCompound) {
      super.func_145839_a(nbtCompound);
      this.speedBoost = nbtCompound.func_74767_n("speedBoost");
      this.furnaceCookTime = nbtCompound.func_74765_d("CookTime");
      this.currentItemBurnTime = TileEntityFurnace.func_145952_a(this.func_70301_a(1));
      this.aspects.readFromNBT(nbtCompound);
      this.vis = this.aspects.visSize();
   }

   @Override
   public NBTTagCompound func_189515_b(NBTTagCompound nbtCompound) {
      nbtCompound = super.func_189515_b(nbtCompound);
      nbtCompound.func_74757_a("speedBoost", this.speedBoost);
      nbtCompound.func_74777_a("CookTime", (short)this.furnaceCookTime);
      this.aspects.writeToNBT(nbtCompound);
      return nbtCompound;
   }

   @Override
   public void func_73660_a() {
      super.func_73660_a();
      boolean flag = this.furnaceBurnTime > 0;
      boolean flag1 = false;
      this.count++;
      if (this.furnaceBurnTime > 0) {
         this.furnaceBurnTime--;
      }

      if (this.field_145850_b != null && !this.field_145850_b.field_72995_K) {
         if (this.bellows < 0) {
            this.checkNeighbours();
         }

         int speed = this.getSpeed();
         if (this.speedBoost) {
            speed = (int)(speed * 0.8);
         }

         if (this.count % speed == 0 && this.aspects.size() > 0) {
            for (Aspect aspect : this.aspects.getAspects()) {
               if (this.aspects.getAmount(aspect) > 0 && TileAlembic.processAlembics(this.func_145831_w(), this.func_174877_v(), aspect)) {
                  this.takeFromContainer(aspect, 1);
                  break;
               }
            }

            for (EnumFacing face : EnumFacing.field_176754_o) {
               if (BlockStateUtils.getFacing(this.func_145832_p()) != face) {
                  IBlockState aux = this.field_145850_b.func_180495_p(this.func_174877_v().func_177972_a(face));
                  if (aux.func_177230_c() == BlocksTC.smelterAux && BlockStateUtils.getFacing(aux) == face.func_176734_d()) {
                     for (Aspect aspect : this.aspects.getAspects()) {
                        if (this.aspects.getAmount(aspect) > 0
                           && TileAlembic.processAlembics(this.func_145831_w(), this.func_174877_v().func_177972_a(face), aspect)) {
                           this.takeFromContainer(aspect, 1);
                           break;
                        }
                     }
                  }
               }
            }
         }

         if (this.furnaceBurnTime == 0) {
            if (this.canSmelt()) {
               this.currentItemBurnTime = this.furnaceBurnTime = TileEntityFurnace.func_145952_a(this.func_70301_a(1));
               if (this.furnaceBurnTime > 0) {
                  BlockSmelter.setFurnaceState(this.field_145850_b, this.func_174877_v(), true);
                  flag1 = true;
                  this.speedBoost = false;
                  ItemStack itemstack = this.func_70301_a(1);
                  if (!itemstack.func_190926_b()) {
                     if (itemstack.func_77969_a(new ItemStack(ItemsTC.alumentum))) {
                        this.speedBoost = true;
                     }

                     Item item = itemstack.func_77973_b();
                     itemstack.func_190918_g(1);
                     if (itemstack.func_190926_b()) {
                        ItemStack item1 = item.getContainerItem(itemstack);
                        this.func_70299_a(1, item1);
                     }
                  }
               } else {
                  BlockSmelter.setFurnaceState(this.field_145850_b, this.func_174877_v(), false);
               }
            } else {
               BlockSmelter.setFurnaceState(this.field_145850_b, this.func_174877_v(), false);
            }
         }

         if (BlockStateUtils.isEnabled(this.func_145832_p()) && this.canSmelt()) {
            this.furnaceCookTime++;
            if (this.furnaceCookTime >= this.smeltTime) {
               this.furnaceCookTime = 0;
               this.smeltItem();
               flag1 = true;
            }
         } else {
            this.furnaceCookTime = 0;
         }

         if (flag != this.furnaceBurnTime > 0) {
            flag1 = true;
         }
      }

      if (flag1) {
         this.func_70296_d();
      }
   }

   private boolean canSmelt() {
      if (this.func_70301_a(0).func_190926_b()) {
         return false;
      }

      AspectList al = ThaumcraftCraftingManager.getObjectTags(this.func_70301_a(0));
      if (al != null && al.size() != 0) {
         int vs = al.visSize();
         if (vs > this.maxVis - this.vis) {
            return false;
         }

         this.smeltTime = (int)(vs * 2 * (1.0F - 0.125F * this.bellows));
         return true;
      } else {
         return false;
      }
   }

   public void checkNeighbours() {
      EnumFacing[] faces = EnumFacing.field_176754_o;

      try {
         if (BlockStateUtils.getFacing(this.func_145832_p()) == EnumFacing.NORTH) {
            faces = new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST};
         }

         if (BlockStateUtils.getFacing(this.func_145832_p()) == EnumFacing.SOUTH) {
            faces = new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};
         }

         if (BlockStateUtils.getFacing(this.func_145832_p()) == EnumFacing.EAST) {
            faces = new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.WEST};
         }

         if (BlockStateUtils.getFacing(this.func_145832_p()) == EnumFacing.WEST) {
            faces = new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.NORTH};
         }
      } catch (Exception var3) {
      }

      this.bellows = TileBellows.getBellows(this.field_145850_b, this.field_174879_c, faces);
   }

   private int getType() {
      return this.func_145838_q() == BlocksTC.smelterBasic ? 0 : (this.func_145838_q() == BlocksTC.smelterThaumium ? 1 : 2);
   }

   private float getEfficiency() {
      float efficiency = 0.8F;
      if (this.getType() == 1) {
         efficiency = 0.9F;
      }

      if (this.getType() == 2) {
         efficiency = 0.95F;
      }

      return efficiency;
   }

   private int getSpeed() {
      return 20 - (this.getType() == 1 ? 10 : 5);
   }

   public void smeltItem() {
      if (this.canSmelt()) {
         int flux = 0;
         AspectList al = ThaumcraftCraftingManager.getObjectTags(this.func_70301_a(0));

         for (Aspect a : al.getAspects()) {
            if (this.getEfficiency() < 1.0F) {
               int qq = al.getAmount(a);

               for (int q = 0; q < qq; q++) {
                  if (this.field_145850_b.field_73012_v.nextFloat() > (a == Aspect.FLUX ? this.getEfficiency() * 0.66F : this.getEfficiency())) {
                     al.reduce(a, 1);
                     flux++;
                  }
               }
            }

            this.aspects.add(a, al.getAmount(a));
         }

         if (flux > 0) {
            int pp = 0;

            label56:
            for (int c = 0; c < flux; c++) {
               for (EnumFacing face : EnumFacing.field_176754_o) {
                  if (BlockStateUtils.getFacing(this.func_145832_p()) != face) {
                     IBlockState vent = this.field_145850_b.func_180495_p(this.func_174877_v().func_177972_a(face));
                     if (vent.func_177230_c() == BlocksTC.smelterVent
                        && BlockStateUtils.getFacing(vent) == face.func_176734_d()
                        && this.field_145850_b.field_73012_v.nextFloat() < 0.333) {
                        this.field_145850_b.func_175641_c(this.func_174877_v(), this.func_145838_q(), 1, face.func_176734_d().ordinal());
                        continue label56;
                     }
                  }
               }

               pp++;
            }

            AuraHelper.polluteAura(this.func_145831_w(), this.func_174877_v(), pp, true);
         }

         this.vis = this.aspects.visSize();
         this.func_70301_a(0).func_190918_g(1);
         if (this.func_70301_a(0).func_190916_E() <= 0) {
            this.func_70299_a(0, ItemStack.field_190927_a);
         }
      }
   }

   public static boolean isItemFuel(ItemStack par0ItemStack) {
      return TileEntityFurnace.func_145952_a(par0ItemStack) > 0;
   }

   @Override
   public boolean func_94041_b(int par1, ItemStack stack2) {
      if (par1 == 0) {
         AspectList al = ThaumcraftCraftingManager.getObjectTags(stack2);
         if (al != null && al.size() > 0) {
            return true;
         }
      }

      return par1 == 1 ? isItemFuel(stack2) : false;
   }

   @Override
   public int[] func_180463_a(EnumFacing par1) {
      return par1 == EnumFacing.DOWN ? slots_bottom : (par1 == EnumFacing.UP ? slots_top : slots_sides);
   }

   @Override
   public boolean func_180462_a(int par1, ItemStack stack2, EnumFacing par3) {
      return par3 == EnumFacing.UP ? false : this.func_94041_b(par1, stack2);
   }

   @Override
   public boolean func_180461_b(int par1, ItemStack stack2, EnumFacing par3) {
      return par3 != EnumFacing.UP || par1 != 1 || stack2.func_77973_b() == Items.field_151133_ar;
   }

   public boolean takeFromContainer(Aspect tag, int amount) {
      if (this.aspects != null && this.aspects.getAmount(tag) >= amount) {
         this.aspects.remove(tag, amount);
         this.vis = this.aspects.visSize();
         this.func_70296_d();
         return true;
      } else {
         return false;
      }
   }

   @SideOnly(Side.CLIENT)
   public int getCookProgressScaled(int par1) {
      if (this.smeltTime <= 0) {
         this.smeltTime = 1;
      }

      return this.furnaceCookTime * par1 / this.smeltTime;
   }

   @SideOnly(Side.CLIENT)
   public int getVisScaled(int par1) {
      return this.vis * par1 / this.maxVis;
   }

   @SideOnly(Side.CLIENT)
   public int getBurnTimeRemainingScaled(int par1) {
      if (this.currentItemBurnTime == 0) {
         this.currentItemBurnTime = 200;
      }

      return this.furnaceBurnTime * par1 / this.currentItemBurnTime;
   }

   @Override
   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      super.onDataPacket(net, pkt);
      if (this.field_145850_b != null) {
         this.field_145850_b.func_180500_c(EnumSkyBlock.BLOCK, this.field_174879_c);
      }
   }

   public boolean func_145842_c(int i, int j) {
      if (i != 1) {
         return super.func_145842_c(i, j);
      }

      if (this.field_145850_b.field_72995_K) {
         EnumFacing d = EnumFacing.field_82609_l[j];
         this.field_145850_b
            .func_184134_a(
               this.func_174877_v().func_177958_n() + 0.5 + d.func_176734_d().func_82601_c(),
               this.func_174877_v().func_177956_o() + 0.5,
               this.func_174877_v().func_177952_p() + 0.5 + d.func_176734_d().func_82599_e(),
               SoundEvents.field_187659_cY,
               SoundCategory.BLOCKS,
               0.25F,
               2.6F + (this.field_145850_b.field_73012_v.nextFloat() - this.field_145850_b.field_73012_v.nextFloat()) * 0.8F,
               true
            );

         for (int a = 0; a < 4; a++) {
            float fx = 0.1F - this.field_145850_b.field_73012_v.nextFloat() * 0.2F;
            float fz = 0.1F - this.field_145850_b.field_73012_v.nextFloat() * 0.2F;
            float fy = 0.1F - this.field_145850_b.field_73012_v.nextFloat() * 0.2F;
            float fx2 = 0.1F - this.field_145850_b.field_73012_v.nextFloat() * 0.2F;
            float fz2 = 0.1F - this.field_145850_b.field_73012_v.nextFloat() * 0.2F;
            float fy2 = 0.1F - this.field_145850_b.field_73012_v.nextFloat() * 0.2F;
            int color = 11184810;
            FXDispatcher.INSTANCE
               .drawVentParticles(
                  this.func_174877_v().func_177958_n() + 0.5F + fx + d.func_176734_d().func_82601_c(),
                  this.func_174877_v().func_177956_o() + 0.5F + fy,
                  this.func_174877_v().func_177952_p() + 0.5F + fz + d.func_176734_d().func_82599_e(),
                  d.func_176734_d().func_82601_c() / 4.0F + fx2,
                  d.func_176734_d().func_96559_d() / 4.0F + fy2,
                  d.func_176734_d().func_82599_e() / 4.0F + fz2,
                  color
               );
         }
      }

      return true;
   }
}

package thaumcraft.common.tiles.crafting;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileThaumcraft;

public class TilePatternCrafter extends TileThaumcraft implements ITickable {
   public byte type = 0;
   public int count = new Random(System.currentTimeMillis()).nextInt(20);
   private final InventoryCrafting craftMatrix = new InventoryCrafting(new Container() {
      public boolean func_75145_c(EntityPlayer playerIn) {
         return false;
      }
   }, 3, 3);
   float power = 0.0F;
   public float rot;
   public float rp;
   public int rotTicks = 0;
   ItemStack outStack = null;

   @Override
   public void readSyncNBT(NBTTagCompound nbt) {
      this.type = nbt.func_74771_c("type");
   }

   @Override
   public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
      nbt.func_74774_a("type", this.type);
      return nbt;
   }

   @Override
   public void func_145839_a(NBTTagCompound nbt) {
      this.power = nbt.func_74760_g("power");
      super.func_145839_a(nbt);
   }

   @Override
   public NBTTagCompound func_189515_b(NBTTagCompound nbt) {
      nbt.func_74776_a("power", this.power);
      return super.func_189515_b(nbt);
   }

   public void func_73660_a() {
      if (this.field_145850_b.field_72995_K) {
         if (this.rotTicks > 0) {
            this.rotTicks--;
            if (this.rotTicks % Math.floor(Math.max(1.0F, this.rp)) == 0.0) {
               this.field_145850_b
                  .func_184134_a(
                     this.field_174879_c.func_177958_n() + 0.5,
                     this.field_174879_c.func_177956_o() + 0.5,
                     this.field_174879_c.func_177952_p() + 0.5,
                     SoundsTC.clack,
                     SoundCategory.BLOCKS,
                     0.2F,
                     1.7F,
                     false
                  );
            }

            this.rp++;
         } else {
            this.rp *= 0.8F;
         }

         this.rot = this.rot + this.rp;
      }

      if (!this.field_145850_b.field_72995_K && this.count++ % 20 == 0 && BlockStateUtils.isEnabled(this.func_145832_p())) {
         if (this.power <= 0.0F) {
            this.power = this.power + AuraHelper.drainVis(this.func_145831_w(), this.func_174877_v(), 5.0F, false);
         }

         int amt = 9;
         switch (this.type) {
            case 0:
               amt = 9;
               break;
            case 1:
               amt = 1;
               break;
            case 2:
            case 3:
               amt = 2;
               break;
            case 4:
               amt = 4;
               break;
            case 5:
            case 6:
               amt = 3;
               break;
            case 7:
            case 8:
               amt = 6;
               break;
            case 9:
               amt = 8;
         }

         IItemHandler above = ThaumcraftInvHelper.getItemHandlerAt(this.func_145831_w(), this.func_174877_v().func_177984_a(), EnumFacing.DOWN);
         IItemHandler below = ThaumcraftInvHelper.getItemHandlerAt(this.func_145831_w(), this.func_174877_v().func_177977_b(), EnumFacing.UP);
         if (above != null && below != null) {
            for (int a = 0; a < above.getSlots(); a++) {
               ItemStack testStack = above.getStackInSlot(a).func_77946_l();
               if (!testStack.func_190926_b()) {
                  testStack.func_190920_e(amt);
                  if (InventoryUtils.removeStackFrom(
                              this.func_145831_w(),
                              this.func_174877_v().func_177984_a(),
                              EnumFacing.DOWN,
                              testStack.func_77946_l(),
                              ThaumcraftInvHelper.InvFilter.BASEORE,
                              true
                           )
                           .func_190916_E()
                        == amt
                     && this.craft(testStack)
                     && this.power >= 1.0F
                     && ItemHandlerHelper.insertItem(below, this.outStack.func_77946_l(), true).func_190926_b()) {
                     boolean b = true;

                     for (int i = 0; i < 9; i++) {
                        if (this.craftMatrix.func_70301_a(i) != null
                           && !ItemHandlerHelper.insertItem(below, this.craftMatrix.func_70301_a(i).func_77946_l(), true).func_190926_b()) {
                           b = false;
                           break;
                        }
                     }

                     if (b) {
                        ItemHandlerHelper.insertItem(below, this.outStack.func_77946_l(), false);

                        for (int i = 0; i < 9; i++) {
                           if (this.craftMatrix.func_70301_a(i) != null) {
                              ItemHandlerHelper.insertItem(below, this.craftMatrix.func_70301_a(i).func_77946_l(), false);
                           }
                        }

                        InventoryUtils.removeStackFrom(
                           this.func_145831_w(), this.func_174877_v().func_177984_a(), EnumFacing.DOWN, testStack, ThaumcraftInvHelper.InvFilter.BASEORE, false
                        );
                        this.field_145850_b.func_175641_c(this.func_174877_v(), this.func_145838_q(), 1, 0);
                        this.power--;
                        break;
                     }
                  }
               }
            }
         }
      }
   }

   private boolean craft(ItemStack inStack) {
      this.outStack = ItemStack.field_190927_a;
      this.craftMatrix.func_174888_l();
      switch (this.type) {
         case 0:
            for (int a = 0; a < 9; a++) {
               this.craftMatrix.func_70299_a(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
            }
            break;
         case 1:
            this.craftMatrix.func_70299_a(0, ItemHandlerHelper.copyStackWithSize(inStack, 1));
            break;
         case 2:
            for (int a = 0; a < 2; a++) {
               this.craftMatrix.func_70299_a(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
            }
            break;
         case 3:
            for (int a = 0; a < 2; a++) {
               this.craftMatrix.func_70299_a(a * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
            }
            break;
         case 4:
            for (int a = 0; a < 2; a++) {
               for (int b = 0; b < 2; b++) {
                  this.craftMatrix.func_70299_a(a + b * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
               }
            }
            break;
         case 5:
            for (int a = 0; a < 3; a++) {
               this.craftMatrix.func_70299_a(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
            }
            break;
         case 6:
            for (int a = 0; a < 3; a++) {
               this.craftMatrix.func_70299_a(a * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
            }
            break;
         case 7:
            for (int a = 0; a < 6; a++) {
               this.craftMatrix.func_70299_a(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
            }
            break;
         case 8:
            for (int a = 0; a < 2; a++) {
               for (int b = 0; b < 3; b++) {
                  this.craftMatrix.func_70299_a(a + b * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
               }
            }
            break;
         case 9:
            for (int a = 0; a < 9; a++) {
               if (a != 4) {
                  this.craftMatrix.func_70299_a(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
               }
            }
      }

      IRecipe ir = CraftingManager.func_192413_b(this.craftMatrix, this.field_145850_b);
      if (ir == null) {
         return false;
      }

      this.outStack = ir.func_77572_b(this.craftMatrix);
      NonNullList<ItemStack> aitemstack = CraftingManager.func_180303_b(this.craftMatrix, this.field_145850_b);

      for (int i = 0; i < aitemstack.size(); i++) {
         ItemStack itemstack1 = this.craftMatrix.func_70301_a(i);
         ItemStack itemstack2 = (ItemStack)aitemstack.get(i);
         if (!itemstack1.func_190926_b()) {
            this.craftMatrix.func_70299_a(i, ItemStack.field_190927_a);
         }

         if (!itemstack1.func_190926_b() && this.craftMatrix.func_70301_a(i).func_190926_b()) {
            this.craftMatrix.func_70299_a(i, itemstack2);
         }
      }

      return !this.outStack.func_190926_b();
   }

   public void cycle() {
      this.type++;
      if (this.type > 9) {
         this.type = 0;
      }

      this.syncTile(false);
      this.func_70296_d();
   }

   public boolean func_145842_c(int i, int j) {
      if (i == 1) {
         if (this.field_145850_b.field_72995_K) {
            this.rotTicks = 10;
         }

         return true;
      } else {
         return super.func_145842_c(i, j);
      }
   }

   public RayTraceResult rayTrace(World world, Vec3d vec3d, Vec3d vec3d1, RayTraceResult fullblock) {
      return fullblock;
   }

   public void addTraceableCuboids(List<IndexedCuboid6> cuboids) {
      EnumFacing facing = BlockStateUtils.getFacing(this.func_145832_p());
      cuboids.add(new IndexedCuboid6(0, this.getCuboidByFacing(facing)));
   }

   public Cuboid6 getCuboidByFacing(EnumFacing facing) {
      switch (facing) {
         case EAST:
            return new Cuboid6(
               this.func_174877_v().func_177958_n() + 0.125,
               this.func_174877_v().func_177956_o() + 0.125,
               this.func_174877_v().func_177952_p() + 0.375,
               this.func_174877_v().func_177958_n() + 0.25,
               this.func_174877_v().func_177956_o() + 0.375,
               this.func_174877_v().func_177952_p() + 0.625
            );
         case NORTH:
            return new Cuboid6(
               this.func_174877_v().func_177958_n() + 0.375,
               this.func_174877_v().func_177956_o() + 0.125,
               this.func_174877_v().func_177952_p() + 0.75,
               this.func_174877_v().func_177958_n() + 0.625,
               this.func_174877_v().func_177956_o() + 0.375,
               this.func_174877_v().func_177952_p() + 0.875
            );
         case SOUTH:
            return new Cuboid6(
               this.func_174877_v().func_177958_n() + 0.375,
               this.func_174877_v().func_177956_o() + 0.125,
               this.func_174877_v().func_177952_p() + 0.125,
               this.func_174877_v().func_177958_n() + 0.625,
               this.func_174877_v().func_177956_o() + 0.375,
               this.func_174877_v().func_177952_p() + 0.25
            );
         default:
            return new Cuboid6(
               this.func_174877_v().func_177958_n() + 0.75,
               this.func_174877_v().func_177956_o() + 0.125,
               this.func_174877_v().func_177952_p() + 0.375,
               this.func_174877_v().func_177958_n() + 0.875,
               this.func_174877_v().func_177956_o() + 0.375,
               this.func_174877_v().func_177952_p() + 0.625
            );
      }
   }
}

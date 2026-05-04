package thaumcraft.common.tiles.crafting;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.common.container.InventoryArcaneWorkbench;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;

public class TileArcaneWorkbench extends TileThaumcraft {
   public InventoryArcaneWorkbench inventoryCraft;
   public int auraVisServer = 0;
   public int auraVisClient = 0;

   public TileArcaneWorkbench() {
      this.inventoryCraft = new InventoryArcaneWorkbench(this, new ContainerDummy());
   }

   @Override
   public void func_145839_a(NBTTagCompound nbtCompound) {
      super.func_145839_a(nbtCompound);
      NonNullList<ItemStack> stacks = NonNullList.func_191197_a(this.inventoryCraft.func_70302_i_(), ItemStack.field_190927_a);
      ItemStackHelper.func_191283_b(nbtCompound, stacks);

      for (int a = 0; a < stacks.size(); a++) {
         this.inventoryCraft.func_70299_a(a, (ItemStack)stacks.get(a));
      }
   }

   @Override
   public NBTTagCompound func_189515_b(NBTTagCompound nbtCompound) {
      super.func_189515_b(nbtCompound);
      NonNullList<ItemStack> stacks = NonNullList.func_191197_a(this.inventoryCraft.func_70302_i_(), ItemStack.field_190927_a);

      for (int a = 0; a < stacks.size(); a++) {
         stacks.set(a, this.inventoryCraft.func_70301_a(a));
      }

      ItemStackHelper.func_191282_a(nbtCompound, stacks);
      return nbtCompound;
   }

   @Override
   public void readSyncNBT(NBTTagCompound nbtCompound) {
   }

   @Override
   public NBTTagCompound writeSyncNBT(NBTTagCompound nbtCompound) {
      return nbtCompound;
   }

   public void getAura() {
      if (!this.func_145831_w().field_72995_K) {
         int t = 0;
         if (this.field_145850_b.func_180495_p(this.func_174877_v().func_177984_a()).func_177230_c() != BlocksTC.arcaneWorkbenchCharger) {
            t = (int)AuraHandler.getVis(this.func_145831_w(), this.func_174877_v());
         } else {
            int sx = this.field_174879_c.func_177958_n() >> 4;
            int sz = this.field_174879_c.func_177952_p() >> 4;

            for (int xx = -1; xx <= 1; xx++) {
               for (int zz = -1; zz <= 1; zz++) {
                  AuraChunk ac = AuraHandler.getAuraChunk(this.field_145850_b.field_73011_w.getDimension(), sx + xx, sz + zz);
                  if (ac != null) {
                     t = (int)(t + ac.getVis());
                  }
               }
            }
         }

         this.auraVisServer = t;
      }
   }

   public void spendAura(int vis) {
      if (!this.func_145831_w().field_72995_K) {
         if (this.field_145850_b.func_180495_p(this.func_174877_v().func_177984_a()).func_177230_c() == BlocksTC.arcaneWorkbenchCharger) {
            int q = vis;
            int z = Math.max(1, vis / 9);
            int attempts = 0;

            while (q > 0) {
               attempts++;

               for (int xx = -1; xx <= 1; xx++) {
                  for (int zz = -1; zz <= 1; zz++) {
                     if (z > q) {
                        z = q;
                     }

                     q = (int)(q - AuraHandler.drainVis(this.func_145831_w(), this.func_174877_v().func_177982_a(xx * 16, 0, zz * 16), z, false));
                     if (q <= 0 || attempts > 1000) {
                        return;
                     }
                  }
               }
            }
         } else {
            AuraHandler.drainVis(this.func_145831_w(), this.func_174877_v(), vis, false);
         }
      }
   }
}

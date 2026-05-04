package thaumcraft.common.tiles.crafting;

import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;
import thaumcraft.common.world.aura.AuraHandler;

public class TileFocalManipulator extends TileThaumcraftInventory {
   public float vis = 0.0F;
   public HashMap<Integer, FocusElementNode> data = new HashMap<>();
   public String focusName = "";
   int ticks = 0;
   public boolean doGather;
   public float visCost = 0.0F;
   public int xpCost = 0;
   private AspectList crystals = new AspectList();
   public AspectList crystalsSync = new AspectList();
   public boolean doGuiReset = false;

   public TileFocalManipulator() {
      super(1);
      this.syncedSlots = new int[]{0};
   }

   @Override
   public void readSyncNBT(NBTTagCompound nbt) {
      super.readSyncNBT(nbt);
      this.vis = nbt.func_74760_g("vis");
      this.focusName = nbt.func_74779_i("focusName");
      this.crystalsSync = new AspectList();
      this.crystalsSync.readFromNBT(nbt, "crystals");
      NBTTagList nodelist = nbt.func_150295_c("nodes", 10);
      this.data.clear();

      for (int x = 0; x < nodelist.func_74745_c(); x++) {
         NBTTagCompound nodenbt = nodelist.func_150305_b(x);
         FocusElementNode node = new FocusElementNode();
         node.deserialize(nodenbt);
         this.data.put(node.id, node);
      }
   }

   @Override
   public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
      super.writeSyncNBT(nbt);
      nbt.func_74776_a("vis", this.vis);
      nbt.func_74778_a("focusName", this.focusName);
      this.crystalsSync.writeToNBT(nbt, "crystals");
      NBTTagList nodelist = new NBTTagList();

      for (FocusElementNode node : this.data.values()) {
         nodelist.func_74742_a(node.serialize());
      }

      nbt.func_74782_a("nodes", nodelist);
      return nbt;
   }

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB(
         this.field_174879_c.func_177958_n(),
         this.field_174879_c.func_177956_o(),
         this.field_174879_c.func_177952_p(),
         this.field_174879_c.func_177958_n() + 1,
         this.field_174879_c.func_177956_o() + 1,
         this.field_174879_c.func_177952_p() + 1
      );
   }

   @Override
   public void func_70299_a(int par1, ItemStack stack) {
      ItemStack prev = this.func_70301_a(par1);
      super.func_70299_a(par1, stack);
      if (stack.func_190926_b() || !ItemStack.func_77989_b(stack, prev)) {
         if (this.field_145850_b.field_72995_K) {
            this.data.clear();
            this.doGuiReset = true;
         } else {
            this.vis = 0.0F;
            this.crystalsSync = new AspectList();
            this.func_70296_d();
            this.syncSlots(null);
         }
      }
   }

   public float spendAura(float vis) {
      if (this.field_145850_b.func_180495_p(this.func_174877_v().func_177984_a()).func_177230_c() != BlocksTC.arcaneWorkbenchCharger) {
         return AuraHandler.drainVis(this.func_145831_w(), this.func_174877_v(), vis, false);
      }

      float q = vis;
      float z = vis / 9.0F;

      for (int xx = -1; xx <= 1; xx++) {
         for (int zz = -1; zz <= 1; zz++) {
            if (z > q) {
               z = q;
            }

            q -= AuraHandler.drainVis(this.func_145831_w(), this.func_174877_v().func_177982_a(xx * 16, 0, zz * 16), z, false);
            if (q <= 0.0F) {
               return vis - q;
            }
         }
      }

      return vis - q;
   }

   @Override
   public void func_73660_a() {
      super.func_73660_a();
      boolean complete = false;
      this.ticks++;
      if (!this.field_145850_b.field_72995_K) {
         if (this.ticks % 20 == 0) {
            if (this.vis > 0.0F
               && (this.func_70301_a(0) == null || this.func_70301_a(0).func_190926_b() || !(this.func_70301_a(0).func_77973_b() instanceof ItemFocus))) {
               complete = true;
               this.vis = 0.0F;
               this.field_145850_b.func_184133_a(null, this.field_174879_c, SoundsTC.wandfail, SoundCategory.BLOCKS, 0.33F, 1.0F);
            }

            if (!complete && this.vis > 0.0F) {
               float amt = this.spendAura(Math.min(20.0F, this.vis));
               if (amt > 0.0F) {
                  this.field_145850_b.func_175641_c(this.field_174879_c, this.func_145838_q(), 5, 1);
                  this.vis -= amt;
                  this.syncTile(false);
                  this.func_70296_d();
               }

               if (this.vis <= 0.0F
                  && this.func_70301_a(0) != null
                  && !this.func_70301_a(0).func_190926_b()
                  && this.func_70301_a(0).func_77973_b() instanceof ItemFocus) {
                  complete = true;
                  this.endCraft();
               }
            }
         }
      } else if (this.vis > 0.0F) {
         FXDispatcher.INSTANCE
            .drawGenericParticles(
               this.field_174879_c.func_177958_n()
                  + 0.5
                  + (this.field_145850_b.field_73012_v.nextFloat() - this.field_145850_b.field_73012_v.nextFloat()) * 0.3F,
               this.field_174879_c.func_177956_o()
                  + 1.4
                  + (this.field_145850_b.field_73012_v.nextFloat() - this.field_145850_b.field_73012_v.nextFloat()) * 0.3F,
               this.field_174879_c.func_177952_p()
                  + 0.5
                  + (this.field_145850_b.field_73012_v.nextFloat() - this.field_145850_b.field_73012_v.nextFloat()) * 0.3F,
               0.0,
               0.0,
               0.0,
               0.5F + this.field_145850_b.field_73012_v.nextFloat() * 0.4F,
               1.0F - this.field_145850_b.field_73012_v.nextFloat() * 0.4F,
               1.0F - this.field_145850_b.field_73012_v.nextFloat() * 0.4F,
               0.8F,
               false,
               448,
               9,
               1,
               6 + this.field_145850_b.field_73012_v.nextInt(5),
               0,
               0.3F + this.field_145850_b.field_73012_v.nextFloat() * 0.3F,
               0.0F,
               0
            );
      }

      if (complete) {
         this.vis = 0.0F;
         this.syncTile(false);
         this.func_70296_d();
      }
   }

   private FocusPackage generateFocus() {
      if (this.data != null && !this.data.isEmpty()) {
         FocusPackage core = new FocusPackage();
         int totalComplexity = 0;
         HashMap<String, Integer> compCount = new HashMap<>();

         for (FocusElementNode node : this.data.values()) {
            if (node.node != null) {
               int a = 0;
               if (compCount.containsKey(node.node.getKey())) {
                  a = compCount.get(node.node.getKey());
               }

               node.complexityMultiplier = 0.5F * (++a + 1);
               compCount.put(node.node.getKey(), a);
               totalComplexity = (int)(totalComplexity + node.node.getComplexity() * node.complexityMultiplier);
            }
         }

         core.setComplexity(totalComplexity);
         FocusElementNode root = this.data.get(0);
         this.traverseChildren(core, root);
         return core;
      } else {
         return null;
      }
   }

   private void traverseChildren(FocusPackage currentPackage, FocusElementNode currentNode) {
      if (currentPackage != null && currentNode != null) {
         currentPackage.addNode(currentNode.node);
         if (currentNode.children != null && currentNode.children.length != 0) {
            if (currentNode.children.length == 1) {
               this.traverseChildren(currentPackage, this.data.get(currentNode.children[0]));
            } else {
               FocusModSplit splitNode = (FocusModSplit)currentNode.node;
               splitNode.getSplitPackages().clear();

               for (int c : currentNode.children) {
                  FocusPackage splitPackage = new FocusPackage();
                  this.traverseChildren(splitPackage, this.data.get(c));
                  splitNode.getSplitPackages().add(splitPackage);
               }
            }
         }
      }
   }

   public void endCraft() {
      this.vis = 0.0F;
      if (this.func_70301_a(0) != null && !this.func_70301_a(0).func_190926_b() && this.func_70301_a(0).func_77973_b() instanceof ItemFocus) {
         FocusPackage core = this.generateFocus();
         if (core != null) {
            this.field_145850_b.func_184133_a(null, this.field_174879_c, SoundsTC.wand, SoundCategory.BLOCKS, 1.0F, 1.0F);
            ItemStack focus = this.func_70301_a(0);
            if (focus.func_77978_p() != null) {
               focus.func_77978_p().func_82580_o("color");
            }

            focus.func_151001_c(this.focusName);
            ItemFocus.setPackage(focus, core);
            this.func_70299_a(0, focus);
            this.crystalsSync = new AspectList();
            this.data.clear();
            this.syncTile(false);
            this.func_70296_d();
         }
      }
   }

   public boolean startCraft(int id, EntityPlayer p) {
      if (this.data != null
         && !this.data.isEmpty()
         && !(this.vis > 0.0F)
         && this.func_70301_a(0) != null
         && !this.func_70301_a(0).func_190926_b()
         && this.func_70301_a(0).func_77973_b() instanceof ItemFocus) {
         int maxComplexity = ((ItemFocus)this.func_70301_a(0).func_77973_b()).getMaxComplexity();
         int totalComplexity = 0;
         this.crystals = new AspectList();
         HashMap<String, Integer> compCount = new HashMap<>();

         for (FocusElementNode node : this.data.values()) {
            if (node.node == null) {
               return false;
            }

            if (!ThaumcraftCapabilities.knowsResearchStrict(p, node.node.getResearch())) {
               return false;
            }

            int a = 0;
            if (compCount.containsKey(node.node.getKey())) {
               a = compCount.get(node.node.getKey());
            }

            node.complexityMultiplier = 0.5F * (++a + 1);
            compCount.put(node.node.getKey(), a);
            totalComplexity = (int)(totalComplexity + node.node.getComplexity() * node.complexityMultiplier);
            if (node.node.getAspect() != null) {
               this.crystals.add(node.node.getAspect(), 1);
            }
         }

         this.vis = totalComplexity * 10 + maxComplexity / 5;
         this.xpCost = (int)Math.max(1L, Math.round(Math.sqrt(totalComplexity)));
         if (!p.field_71075_bZ.field_75098_d && p.field_71068_ca < this.xpCost) {
            this.vis = 0.0F;
            return false;
         }

         if (!p.field_71075_bZ.field_75098_d) {
            p.func_82242_a(-this.xpCost);
         }

         if (this.crystals.getAspects().length <= 0) {
            this.vis = 0.0F;
            return false;
         }

         ItemStack[] components = new ItemStack[this.crystals.getAspects().length];
         int r = 0;

         for (Aspect as : this.crystals.getAspects()) {
            components[r] = ThaumcraftApiHelper.makeCrystal(as, this.crystals.getAmount(as));
            r++;
         }

         if (components.length >= 0) {
            for (int a = 0; a < components.length; a++) {
               if (!InventoryUtils.isPlayerCarryingAmount(p, components[a], false)) {
                  this.vis = 0.0F;
                  return false;
               }
            }

            for (int a = 0; a < components.length; a++) {
               InventoryUtils.consumePlayerItem(p, components[a], true, false);
            }

            this.crystalsSync = this.crystals.copy();
         }

         this.func_70296_d();
         this.syncTile(false);
         this.field_145850_b.func_184133_a(null, this.field_174879_c, SoundsTC.craftstart, SoundCategory.BLOCKS, 1.0F, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean func_94041_b(int par1, ItemStack stack) {
      return stack.func_77973_b() instanceof ItemFocus;
   }

   public boolean func_145842_c(int i, int j) {
      if (i == 1) {
         this.doGuiReset = true;
      }

      if (i == 5) {
         if (this.field_145850_b.field_72995_K) {
            FXDispatcher.INSTANCE
               .visSparkle(
                  this.field_174879_c.func_177958_n() + this.func_145831_w().field_73012_v.nextInt(3) - this.func_145831_w().field_73012_v.nextInt(3),
                  this.field_174879_c.func_177956_o() + this.func_145831_w().field_73012_v.nextInt(3),
                  this.field_174879_c.func_177952_p() + this.func_145831_w().field_73012_v.nextInt(3) - this.func_145831_w().field_73012_v.nextInt(3),
                  this.field_174879_c.func_177958_n(),
                  this.field_174879_c.func_177956_o() + 1,
                  this.field_174879_c.func_177952_p(),
                  j
               );
         }

         return true;
      } else {
         return super.func_145842_c(i, j);
      }
   }
}

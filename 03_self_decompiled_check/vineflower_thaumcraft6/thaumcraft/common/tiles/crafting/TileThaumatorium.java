package thaumcraft.common.tiles.crafting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.container.InventoryFake;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;

public class TileThaumatorium extends TileThaumcraftInventory implements IAspectContainer, IEssentiaTransport, ITickable {
   public AspectList essentia = new AspectList();
   public ArrayList<Integer> recipeHash = new ArrayList<>();
   public ArrayList<AspectList> recipeEssentia = new ArrayList<>();
   public ArrayList<String> recipePlayer = new ArrayList<>();
   public int currentCraft = -1;
   public int maxRecipes = 1;
   public Aspect currentSuction = null;
   int venting = 0;
   int counter = 0;
   boolean heated = false;
   CrucibleRecipe currentRecipe = null;
   public Container eventHandler;
   public ArrayList<CrucibleRecipe> recipes = new ArrayList<>();

   public TileThaumatorium() {
      super(1);
   }

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB(
         this.func_174877_v().func_177958_n() - 0.1,
         this.func_174877_v().func_177956_o() - 0.1,
         this.func_174877_v().func_177952_p() - 0.1,
         this.func_174877_v().func_177958_n() + 1.1,
         this.func_174877_v().func_177956_o() + 2.1,
         this.func_174877_v().func_177952_p() + 1.1
      );
   }

   @Override
   public void readSyncNBT(NBTTagCompound nbttagcompound) {
      this.essentia.readFromNBT(nbttagcompound);
      this.maxRecipes = nbttagcompound.func_74771_c("maxrec");
      this.recipeEssentia = new ArrayList<>();
      this.recipeHash = new ArrayList<>();
      this.recipePlayer = new ArrayList<>();
      int[] hashes = nbttagcompound.func_74759_k("recipes");
      if (hashes != null) {
         for (int hash : hashes) {
            CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(hash);
            if (recipe != null) {
               this.recipeEssentia.add(recipe.getAspects().copy());
               this.recipePlayer.add("");
               this.recipeHash.add(hash);
            }
         }
      }
   }

   @Override
   public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
      nbttagcompound.func_74774_a("maxrec", (byte)this.maxRecipes);
      this.essentia.writeToNBT(nbttagcompound);
      int[] hashes = new int[this.recipeHash.size()];
      int a = 0;

      for (Integer i : this.recipeHash) {
         hashes[a] = i;
         a++;
      }

      nbttagcompound.func_74783_a("recipes", hashes);
      return nbttagcompound;
   }

   @Override
   public void func_145839_a(NBTTagCompound nbtCompound) {
      super.func_145839_a(nbtCompound);
      NBTTagList nbttaglist2 = nbtCompound.func_150295_c("OutputPlayer", 8);

      for (int a = 0; a < nbttaglist2.func_74745_c(); a++) {
         if (this.recipePlayer.size() > a) {
            this.recipePlayer.set(a, nbttaglist2.func_150307_f(a));
         }
      }
   }

   @Override
   public NBTTagCompound func_189515_b(NBTTagCompound nbtCompound) {
      super.func_189515_b(nbtCompound);
      NBTTagList nbttaglist2 = new NBTTagList();
      if (this.recipePlayer.size() > 0) {
         for (int a = 0; a < this.recipePlayer.size(); a++) {
            if (this.recipePlayer.get(a) != null) {
               NBTTagString nbttagcompound1 = new NBTTagString(this.recipePlayer.get(a));
               nbttaglist2.func_74742_a(nbttagcompound1);
            }
         }
      }

      nbtCompound.func_74782_a("OutputPlayer", nbttaglist2);
      return nbtCompound;
   }

   boolean checkHeat() {
      Material mat = this.field_145850_b.func_180495_p(this.field_174879_c.func_177979_c(2)).func_185904_a();
      Block bi = this.field_145850_b.func_180495_p(this.field_174879_c.func_177979_c(2)).func_177230_c();
      return mat == Material.field_151587_i || mat == Material.field_151581_o || BlocksTC.nitor.containsValue(bi) || bi == Blocks.field_189877_df;
   }

   public ItemStack getCurrentOutputRecipe() {
      ItemStack out = ItemStack.field_190927_a;
      if (this.currentCraft >= 0 && this.recipeHash != null && this.recipeHash.size() > 0) {
         CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(this.recipeHash.get(this.currentCraft));
         if (recipe != null) {
            out = recipe.getRecipeOutput().func_77946_l();
         }
      }

      return out;
   }

   @Override
   public void func_73660_a() {
      if (!this.field_145850_b.field_72995_K) {
         if (this.counter == 0 || this.counter % 40 == 0) {
            this.heated = this.checkHeat();
            this.getUpgrades();
         }

         this.counter++;
         if (this.heated && !this.gettingPower() && this.counter % 5 == 0 && this.recipeHash != null && this.recipeHash.size() > 0) {
            if (this.func_70301_a(0).func_190926_b()) {
               this.currentSuction = null;
               return;
            }

            if (this.currentCraft < 0
               || this.currentCraft >= this.recipeHash.size()
               || this.currentRecipe == null
               || !this.currentRecipe.catalystMatches(this.func_70301_a(0))) {
               for (int a = 0; a < this.recipeHash.size(); a++) {
                  CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(this.recipeHash.get(a));
                  if (recipe.catalystMatches(this.func_70301_a(0))) {
                     this.currentCraft = a;
                     this.currentRecipe = recipe;
                     break;
                  }
               }
            }

            if (this.currentCraft < 0 || this.currentCraft >= this.recipeHash.size()) {
               return;
            }

            boolean done = true;
            this.currentSuction = null;

            for (Aspect aspect : this.recipeEssentia.get(this.currentCraft).getAspectsSortedByName()) {
               if (this.essentia.getAmount(aspect) < this.recipeEssentia.get(this.currentCraft).getAmount(aspect)) {
                  this.currentSuction = aspect;
                  done = false;
                  break;
               }
            }

            if (done) {
               this.completeRecipe();
            } else if (this.currentSuction != null) {
               this.fill();
            }
         }
      } else if (this.venting > 0) {
         this.venting--;
         float fx = 0.1F - this.field_145850_b.field_73012_v.nextFloat() * 0.2F;
         float fz = 0.1F - this.field_145850_b.field_73012_v.nextFloat() * 0.2F;
         float fy = 0.1F - this.field_145850_b.field_73012_v.nextFloat() * 0.2F;
         float fx2 = 0.1F - this.field_145850_b.field_73012_v.nextFloat() * 0.2F;
         float fz2 = 0.1F - this.field_145850_b.field_73012_v.nextFloat() * 0.2F;
         float fy2 = 0.1F - this.field_145850_b.field_73012_v.nextFloat() * 0.2F;
         int color = 16777215;
         EnumFacing facing = BlockStateUtils.getFacing(this.func_145832_p());
         FXDispatcher.INSTANCE
            .drawVentParticles(
               this.field_174879_c.func_177958_n() + 0.5F + fx + facing.func_82601_c() / 2.0F,
               this.field_174879_c.func_177956_o() + 0.5F + fy,
               this.field_174879_c.func_177952_p() + 0.5F + fz + facing.func_82599_e() / 2.0F,
               facing.func_82601_c() / 4.0F + fx2,
               fy2,
               facing.func_82599_e() / 4.0F + fz2,
               color
            );
      }
   }

   private void completeRecipe() {
      if (this.currentRecipe != null
         && this.currentCraft < this.recipeHash.size()
         && this.currentRecipe.matches(this.essentia, this.func_70301_a(0))
         && this.func_70298_a(0, 1) != null) {
         this.essentia = new AspectList();
         ItemStack dropped = this.getCurrentOutputRecipe();
         EntityPlayer p = this.field_145850_b.func_72924_a(this.recipePlayer.get(this.currentCraft));
         if (p != null) {
            FMLCommonHandler.instance().firePlayerCraftingEvent(p, dropped, new InventoryFake(this.func_70301_a(0)));
         }

         EnumFacing facing = BlockStateUtils.getFacing(this.func_145832_p());
         InventoryUtils.ejectStackAt(this.func_145831_w(), this.func_174877_v(), facing, dropped);
         this.field_145850_b
            .func_184133_a(
               null,
               this.field_174879_c,
               SoundEvents.field_187659_cY,
               SoundCategory.BLOCKS,
               0.25F,
               2.6F + (this.field_145850_b.field_73012_v.nextFloat() - this.field_145850_b.field_73012_v.nextFloat()) * 0.8F
            );
         this.currentCraft = -1;
         this.syncTile(false);
         this.func_70296_d();
      }
   }

   void fill() {
      EnumFacing facing = BlockStateUtils.getFacing(this.func_145832_p());
      TileEntity te = null;
      IEssentiaTransport ic = null;

      for (int y = 0; y <= 1; y++) {
         for (EnumFacing dir : EnumFacing.field_82609_l) {
            if (dir != facing && dir != EnumFacing.DOWN && (y != 0 || dir != EnumFacing.UP)) {
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
      int ce = this.currentRecipe.getAspects().getAmount(tt) - this.essentia.getAmount(tt);
      if (this.currentRecipe != null && ce > 0) {
         int add = Math.min(ce, am);
         this.essentia.add(tt, add);
         this.syncTile(false);
         this.func_70296_d();
         return am - add;
      } else {
         return am;
      }
   }

   @Override
   public boolean takeFromContainer(Aspect tt, int am) {
      if (this.essentia.getAmount(tt) >= am) {
         this.essentia.remove(tt, am);
         this.syncTile(false);
         this.func_70296_d();
         return true;
      } else {
         return false;
      }
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
      return this.essentia.getAmount(tt) >= am;
   }

   @Override
   public int containerContains(Aspect tt) {
      return this.essentia.getAmount(tt);
   }

   @Override
   public boolean doesContainerAccept(Aspect tag) {
      return true;
   }

   public boolean func_145842_c(int i, int j) {
      if (i >= 0) {
         if (this.field_145850_b.field_72995_K) {
            this.venting = 7;
         }

         return true;
      } else {
         return super.func_145842_c(i, j);
      }
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
      return this.essentia;
   }

   @Override
   public void setAspects(AspectList aspects) {
      this.essentia = aspects;
   }

   public void func_70296_d() {
      super.func_70296_d();
      if (this.eventHandler != null) {
         this.eventHandler.func_75130_a(this);
      }
   }

   @Override
   public int[] func_180463_a(EnumFacing side) {
      return new int[]{0};
   }

   @Override
   public boolean gettingPower() {
      return this.field_145850_b.func_175687_A(this.field_174879_c) > 0
         || this.field_145850_b.func_175687_A(this.field_174879_c.func_177977_b()) > 0
         || this.field_145850_b.func_175687_A(this.field_174879_c.func_177984_a()) > 0;
   }

   public void getUpgrades() {
      EnumFacing facing = BlockStateUtils.getFacing(this.func_145832_p());
      int mr = 1;

      for (int yy = 0; yy <= 1; yy++) {
         for (EnumFacing dir : EnumFacing.field_82609_l) {
            if (dir != EnumFacing.DOWN && dir != facing) {
               int xx = this.field_174879_c.func_177958_n() + dir.func_82601_c();
               int zz = this.field_174879_c.func_177952_p() + dir.func_82599_e();
               BlockPos bp = new BlockPos(xx, this.field_174879_c.func_177956_o() + yy + dir.func_96559_d(), zz);
               IBlockState bs = this.field_145850_b.func_180495_p(bp);
               if (bs == BlocksTC.brainBox.func_176223_P().func_177226_a(IBlockFacing.FACING, dir.func_176734_d())) {
                  mr += 2;
               }
            }
         }
      }

      if (mr != this.maxRecipes) {
         this.maxRecipes = mr;

         while (this.recipeHash.size() > this.maxRecipes) {
            this.recipeHash.remove(this.recipeHash.size() - 1);
         }

         this.syncTile(false);
         this.func_70296_d();
      }
   }

   @Override
   public String func_70005_c_() {
      return "container.alchemyfurnace";
   }

   @Override
   public boolean func_145818_k_() {
      return false;
   }

   @Override
   public ITextComponent func_145748_c_() {
      return null;
   }

   public void updateRecipes(EntityPlayer player) {
      this.recipes.clear();
      ArrayList<CrucibleRecipe> recipesTemp = new ArrayList<>();
      if (this.func_70301_a(0) != null && !this.func_70301_a(0).func_190926_b() && this.recipeHash != null) {
         for (Object r : ThaumcraftApi.getCraftingRecipes().values()) {
            if (r instanceof CrucibleRecipe) {
               CrucibleRecipe creps = (CrucibleRecipe)r;
               if (ThaumcraftCapabilities.knowsResearchStrict(player, creps.getResearch()) && creps.catalystMatches(this.func_70301_a(0))) {
                  recipesTemp.add(creps);
               } else if (this.recipeHash != null && this.recipeHash.size() > 0) {
                  for (Integer hash : this.recipeHash) {
                     if (creps.hash == hash) {
                        recipesTemp.add(creps);
                        break;
                     }
                  }
               }
            }
         }
      }

      this.recipes = recipesTemp.stream().sorted(new TileThaumatorium.RecipeOutputComparator()).collect(Collectors.toList());
   }

   public ArrayList<Integer> generateRecipeHashlist() {
      ArrayList<Integer> hashList = new ArrayList<>();

      label32:
      for (int hash : this.recipeHash) {
         for (CrucibleRecipe cr : this.recipes) {
            if (cr.hash == hash) {
               continue label32;
            }
         }

         hashList.add(hash);
      }

      for (CrucibleRecipe cr : this.recipes) {
         hashList.add(cr.hash);
      }

      return hashList;
   }

   private class RecipeOutputComparator implements Comparator<CrucibleRecipe> {
      public RecipeOutputComparator() {
      }

      public int compare(CrucibleRecipe a, CrucibleRecipe b) {
         return a.equals(b) ? 0 : a.getRecipeOutput().func_82833_r().compareTo(b.getRecipeOutput().func_82833_r());
      }
   }
}

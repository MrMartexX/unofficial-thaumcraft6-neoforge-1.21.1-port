package thaumcraft.common.container.slot;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

public class SlotCraftingArcaneWorkbench extends Slot {
   private final InventoryCrafting craftMatrix;
   private EntityPlayer player;
   private int amountCrafted;
   private TileArcaneWorkbench tile;

   public SlotCraftingArcaneWorkbench(
      TileArcaneWorkbench te, EntityPlayer par1EntityPlayer, InventoryCrafting inventory, IInventory par3IInventory, int par4, int par5, int par6
   ) {
      super(par3IInventory, par4, par5, par6);
      this.player = par1EntityPlayer;
      this.craftMatrix = inventory;
      this.tile = te;
   }

   public boolean func_75214_a(ItemStack stack) {
      return false;
   }

   public ItemStack func_75209_a(int amount) {
      if (this.func_75216_d()) {
         this.amountCrafted = this.amountCrafted + Math.min(amount, this.func_75211_c().func_190916_E());
      }

      return super.func_75209_a(amount);
   }

   protected void func_75210_a(ItemStack stack, int amount) {
      this.amountCrafted += amount;
      this.func_75208_c(stack);
   }

   protected void func_190900_b(int p_190900_1_) {
      this.amountCrafted += p_190900_1_;
   }

   protected void func_75208_c(ItemStack stack) {
      if (this.amountCrafted > 0) {
         stack.func_77980_a(this.player.field_70170_p, this.player, this.amountCrafted);
         FMLCommonHandler.instance().firePlayerCraftingEvent(this.player, stack, this.craftMatrix);
      }

      this.amountCrafted = 0;
      InventoryCraftResult inventorycraftresult = (InventoryCraftResult)this.field_75224_c;
      IRecipe irecipe = inventorycraftresult.func_193055_i();
      if (irecipe != null && !irecipe.func_192399_d()) {
         this.player.func_192021_a(Lists.newArrayList(new IRecipe[]{irecipe}));
         inventorycraftresult.func_193056_a((IRecipe)null);
      }
   }

   public ItemStack func_190901_a(EntityPlayer thePlayer, ItemStack stack) {
      this.func_75208_c(stack);
      IArcaneRecipe recipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(this.craftMatrix, thePlayer);
      InventoryCrafting ic = this.craftMatrix;
      ForgeHooks.setCraftingPlayer(thePlayer);
      NonNullList<ItemStack> nonnulllist;
      if (recipe != null) {
         nonnulllist = CraftingManager.func_180303_b(this.craftMatrix, thePlayer.field_70170_p);
      } else {
         ic = new InventoryCrafting(new ContainerDummy(), 3, 3);

         for (int a = 0; a < 9; a++) {
            ic.func_70299_a(a, this.craftMatrix.func_70301_a(a));
         }

         ic.field_70465_c = this.craftMatrix.field_70465_c;
         nonnulllist = CraftingManager.func_180303_b(ic, thePlayer.field_70170_p);
      }

      ForgeHooks.setCraftingPlayer(null);
      int vis = 0;
      AspectList crystals = null;
      if (recipe != null) {
         vis = recipe.getVis();
         vis = (int)(vis * (1.0F - CasterManager.getTotalVisDiscount(thePlayer)));
         crystals = recipe.getCrystals();
         if (vis > 0) {
            this.tile.getAura();
            this.tile.spendAura(vis);
         }
      }

      for (int i = 0; i < Math.min(9, nonnulllist.size()); i++) {
         ItemStack itemstack = ic.func_70301_a(i);
         ItemStack itemstack1 = (ItemStack)nonnulllist.get(i);
         if (!itemstack.func_190926_b()) {
            this.craftMatrix.func_70298_a(i, 1);
            itemstack = ic.func_70301_a(i);
         }

         if (!itemstack1.func_190926_b()) {
            if (itemstack.func_190926_b()) {
               this.craftMatrix.func_70299_a(i, itemstack1);
            } else if (ItemStack.func_179545_c(itemstack, itemstack1) && ItemStack.func_77970_a(itemstack, itemstack1)) {
               itemstack1.func_190917_f(itemstack.func_190916_E());
               this.craftMatrix.func_70299_a(i, itemstack1);
            } else if (!this.player.field_71071_by.func_70441_a(itemstack1)) {
               this.player.func_71019_a(itemstack1, false);
            }
         }
      }

      if (crystals != null) {
         for (Aspect aspect : crystals.getAspects()) {
            ItemStack cs = ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect));

            for (int i = 0; i < 6; i++) {
               ItemStack itemstack1 = this.craftMatrix.func_70301_a(9 + i);
               if (itemstack1.func_77973_b() == ItemsTC.crystalEssence && ItemStack.func_77970_a(cs, itemstack1)) {
                  this.craftMatrix.func_70298_a(9 + i, cs.func_190916_E());
               }
            }
         }
      }

      return stack;
   }
}

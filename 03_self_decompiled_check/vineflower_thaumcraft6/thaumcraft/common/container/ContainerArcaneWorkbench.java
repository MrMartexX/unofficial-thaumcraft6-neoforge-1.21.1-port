package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.common.container.slot.SlotCraftingArcaneWorkbench;
import thaumcraft.common.container.slot.SlotCrystal;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

public class ContainerArcaneWorkbench extends Container {
   private TileArcaneWorkbench tileEntity;
   private InventoryPlayer ip;
   public InventoryCraftResult craftResult = new InventoryCraftResult();
   public static int[] xx = new int[]{64, 17, 112, 17, 112, 64};
   public static int[] yy = new int[]{13, 35, 35, 93, 93, 115};
   private int lastVis = -1;
   private long lastCheck = 0L;

   public ContainerArcaneWorkbench(InventoryPlayer par1InventoryPlayer, TileArcaneWorkbench e) {
      this.tileEntity = e;
      this.tileEntity.inventoryCraft.field_70465_c = this;
      this.ip = par1InventoryPlayer;
      e.getAura();
      this.func_75146_a(
         new SlotCraftingArcaneWorkbench(this.tileEntity, par1InventoryPlayer.field_70458_d, this.tileEntity.inventoryCraft, this.craftResult, 15, 160, 64)
      );

      for (int var6 = 0; var6 < 3; var6++) {
         for (int var7 = 0; var7 < 3; var7++) {
            this.func_75146_a(new Slot(this.tileEntity.inventoryCraft, var7 + var6 * 3, 40 + var7 * 24, 40 + var6 * 24));
         }
      }

      for (ShardType st : ShardType.values()) {
         if (st.getMetadata() < 6) {
            this.func_75146_a(new SlotCrystal(st.getAspect(), this.tileEntity.inventoryCraft, st.getMetadata() + 9, xx[st.getMetadata()], yy[st.getMetadata()]));
         }
      }

      for (int var9 = 0; var9 < 3; var9++) {
         for (int var7 = 0; var7 < 9; var7++) {
            this.func_75146_a(new Slot(par1InventoryPlayer, var7 + var9 * 9 + 9, 16 + var7 * 18, 151 + var9 * 18));
         }
      }

      for (int var10 = 0; var10 < 9; var10++) {
         this.func_75146_a(new Slot(par1InventoryPlayer, var10, 16 + var10 * 18, 209));
      }

      this.func_75130_a(this.tileEntity.inventoryCraft);
   }

   public void func_75132_a(IContainerListener par1ICrafting) {
      super.func_75132_a(par1ICrafting);
      this.tileEntity.getAura();
      par1ICrafting.func_71112_a(this, 0, this.tileEntity.auraVisServer);
   }

   public void func_75142_b() {
      super.func_75142_b();
      long t = System.currentTimeMillis();
      if (t > this.lastCheck) {
         this.lastCheck = t + 500L;
         this.tileEntity.getAura();
      }

      if (this.lastVis != this.tileEntity.auraVisServer) {
         this.func_75130_a(this.tileEntity.inventoryCraft);
      }

      for (int i = 0; i < this.field_75149_d.size(); i++) {
         IContainerListener icrafting = (IContainerListener)this.field_75149_d.get(i);
         if (this.lastVis != this.tileEntity.auraVisServer) {
            icrafting.func_71112_a(this, 0, this.tileEntity.auraVisServer);
         }
      }

      this.lastVis = this.tileEntity.auraVisServer;
   }

   @SideOnly(Side.CLIENT)
   public void func_75137_b(int par1, int par2) {
      if (par1 == 0) {
         this.tileEntity.auraVisClient = par2;
      }
   }

   public void func_75130_a(IInventory par1IInventory) {
      IArcaneRecipe recipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(this.tileEntity.inventoryCraft, this.ip.field_70458_d);
      boolean hasVis = true;
      boolean hasCrystals = true;
      if (recipe != null) {
         int vis = 0;
         AspectList crystals = null;
         vis = recipe.getVis();
         vis = (int)(vis * (1.0F - CasterManager.getTotalVisDiscount(this.ip.field_70458_d)));
         crystals = recipe.getCrystals();
         this.tileEntity.getAura();
         hasVis = this.tileEntity.func_145831_w().field_72995_K ? this.tileEntity.auraVisClient >= vis : this.tileEntity.auraVisServer >= vis;
         if (crystals != null && crystals.size() > 0) {
            for (Aspect aspect : crystals.getAspects()) {
               if (ThaumcraftInvHelper.countTotalItemsIn(
                     ThaumcraftInvHelper.wrapInventory(this.tileEntity.inventoryCraft, EnumFacing.UP),
                     ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect)),
                     ThaumcraftInvHelper.InvFilter.STRICT
                  )
                  < crystals.getAmount(aspect)) {
                  hasCrystals = false;
                  break;
               }
            }
         }
      }

      if (hasVis && hasCrystals) {
         this.func_192389_a(this.tileEntity.func_145831_w(), this.ip.field_70458_d, this.tileEntity.inventoryCraft, this.craftResult);
      }

      super.func_75142_b();
   }

   protected void func_192389_a(World world, EntityPlayer player, InventoryCrafting craftMat, InventoryCraftResult craftRes) {
      if (!world.field_72995_K) {
         EntityPlayerMP entityplayermp = (EntityPlayerMP)player;
         ItemStack itemstack = ItemStack.field_190927_a;
         IArcaneRecipe arecipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(craftMat, entityplayermp);
         if (arecipe != null
            && (arecipe.func_192399_d() || !world.func_82736_K().func_82766_b("doLimitedCrafting") || entityplayermp.func_192037_E().func_193830_f(arecipe))
            && ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(arecipe.getResearch())) {
            craftRes.func_193056_a(arecipe);
            itemstack = arecipe.func_77572_b(craftMat);
         } else {
            InventoryCrafting craftInv = new InventoryCrafting(new ContainerDummy(), 3, 3);

            for (int a = 0; a < 9; a++) {
               craftInv.func_70299_a(a, craftMat.func_70301_a(a));
            }

            IRecipe irecipe = CraftingManager.func_192413_b(craftInv, world);
            if (irecipe != null
               && (irecipe.func_192399_d() || !world.func_82736_K().func_82766_b("doLimitedCrafting") || entityplayermp.func_192037_E().func_193830_f(irecipe))
               )
             {
               craftRes.func_193056_a(irecipe);
               itemstack = irecipe.func_77572_b(craftMat);
            }
         }

         craftRes.func_70299_a(0, itemstack);
         entityplayermp.field_71135_a.func_147359_a(new SPacketSetSlot(this.field_75152_c, 0, itemstack));
      }
   }

   public void func_75134_a(EntityPlayer par1EntityPlayer) {
      super.func_75134_a(par1EntityPlayer);
      if (!this.tileEntity.func_145831_w().field_72995_K) {
         this.tileEntity.inventoryCraft.field_70465_c = new ContainerDummy();
      }
   }

   public boolean func_75145_c(EntityPlayer par1EntityPlayer) {
      return this.tileEntity.func_145831_w().func_175625_s(this.tileEntity.func_174877_v()) != this.tileEntity
         ? false
         : par1EntityPlayer.func_174831_c(this.tileEntity.func_174877_v()) <= 64.0;
   }

   public ItemStack func_82846_b(EntityPlayer par1EntityPlayer, int par1) {
      ItemStack var2x = ItemStack.field_190927_a;
      Slot var3 = (Slot)this.field_75151_b.get(par1);
      if (var3 != null && var3.func_75216_d()) {
         ItemStack var4 = var3.func_75211_c();
         var2x = var4.func_77946_l();
         if (par1 == 0) {
            if (!this.func_75135_a(var4, 16, 52, true)) {
               return ItemStack.field_190927_a;
            }

            var3.func_75220_a(var4, var2x);
         } else if (par1 >= 16 && par1 < 52) {
            for (ShardType st : ShardType.values()) {
               if (st.getMetadata() < 6 && SlotCrystal.isValidCrystal(var4, st.getAspect())) {
                  if (!this.func_75135_a(var4, 10 + st.getMetadata(), 11 + st.getMetadata(), false)) {
                     return ItemStack.field_190927_a;
                  }

                  if (var4.func_190916_E() == 0) {
                     break;
                  }
               }
            }

            if (var4.func_190916_E() != 0) {
               if (par1 >= 16 && par1 < 43) {
                  if (!this.func_75135_a(var4, 43, 52, false)) {
                     return ItemStack.field_190927_a;
                  }
               } else if (par1 >= 43 && par1 < 52 && !this.func_75135_a(var4, 16, 43, false)) {
                  return ItemStack.field_190927_a;
               }
            }
         } else if (!this.func_75135_a(var4, 16, 52, false)) {
            return ItemStack.field_190927_a;
         }

         if (var4.func_190916_E() == 0) {
            var3.func_75215_d(ItemStack.field_190927_a);
         } else {
            var3.func_75218_e();
         }

         if (var4.func_190916_E() == var2x.func_190916_E()) {
            return ItemStack.field_190927_a;
         }

         var3.func_190901_a(this.ip.field_70458_d, var4);
      }

      return var2x;
   }

   public boolean func_94530_a(ItemStack stack, Slot slot) {
      return slot.field_75224_c != this.craftResult && super.func_94530_a(stack, slot);
   }
}

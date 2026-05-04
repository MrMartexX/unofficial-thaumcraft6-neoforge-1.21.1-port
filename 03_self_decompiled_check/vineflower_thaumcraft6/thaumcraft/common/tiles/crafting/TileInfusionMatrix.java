package thaumcraft.common.tiles.crafting;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.IInteractWithCaster;
import thaumcraft.api.crafting.IInfusionStabiliser;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.IGogglesDisplayExtended;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.basic.BlockPillar;
import thaumcraft.common.blocks.devices.BlockPedestal;
import thaumcraft.common.container.InventoryFake;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockArc;
import thaumcraft.common.lib.network.fx.PacketFXInfusionSource;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.tiles.devices.TileStabilizer;

public class TileInfusionMatrix extends TileThaumcraft implements IInteractWithCaster, IAspectContainer, ITickable, IGogglesDisplayExtended {
   private ArrayList<BlockPos> pedestals = new ArrayList<>();
   private int dangerCount = 0;
   public boolean active = false;
   public boolean crafting = false;
   public boolean checkSurroundings = true;
   public float costMult = 0.0F;
   private int cycleTime = 20;
   public int stabilityCap = 25;
   public float stability = 0.0F;
   public float stabilityReplenish = 0.0F;
   private AspectList recipeEssentia = new AspectList();
   private ArrayList<ItemStack> recipeIngredients = null;
   private Object recipeOutput = null;
   private String recipePlayer = null;
   private String recipeOutputLabel = null;
   private ItemStack recipeInput = null;
   private int recipeInstability = 0;
   private int recipeXP = 0;
   private int recipeType = 0;
   public HashMap<String, TileInfusionMatrix.SourceFX> sourceFX = new HashMap<>();
   public int count = 0;
   public int craftCount = 0;
   public float startUp;
   private int countDelay = this.cycleTime / 2;
   ArrayList<ItemStack> ingredients = new ArrayList<>();
   int itemCount = 0;
   private ArrayList<BlockPos> problemBlocks = new ArrayList<>();
   HashMap<Block, Integer> tempBlockCount = new HashMap<>();
   static DecimalFormat myFormatter = new DecimalFormat("#######.##");

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB(
         this.func_174877_v().func_177958_n() - 0.1,
         this.func_174877_v().func_177956_o() - 0.1,
         this.func_174877_v().func_177952_p() - 0.1,
         this.func_174877_v().func_177958_n() + 1.1,
         this.func_174877_v().func_177956_o() + 1.1,
         this.func_174877_v().func_177952_p() + 1.1
      );
   }

   @Override
   public void readSyncNBT(NBTTagCompound nbtCompound) {
      this.active = nbtCompound.func_74767_n("active");
      this.crafting = nbtCompound.func_74767_n("crafting");
      this.stability = nbtCompound.func_74760_g("stability");
      this.recipeInstability = nbtCompound.func_74762_e("recipeinst");
      this.recipeEssentia.readFromNBT(nbtCompound);
   }

   @Override
   public NBTTagCompound writeSyncNBT(NBTTagCompound nbtCompound) {
      nbtCompound.func_74757_a("active", this.active);
      nbtCompound.func_74757_a("crafting", this.crafting);
      nbtCompound.func_74776_a("stability", this.stability);
      nbtCompound.func_74768_a("recipeinst", this.recipeInstability);
      this.recipeEssentia.writeToNBT(nbtCompound);
      return nbtCompound;
   }

   @Override
   public void func_145839_a(NBTTagCompound nbtCompound) {
      super.func_145839_a(nbtCompound);
      NBTTagList nbttaglist = nbtCompound.func_150295_c("recipein", 10);
      this.recipeIngredients = new ArrayList<>();

      for (int i = 0; i < nbttaglist.func_74745_c(); i++) {
         NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
         this.recipeIngredients.add(new ItemStack(nbttagcompound1));
      }

      String rot = nbtCompound.func_74779_i("rotype");
      if (rot != null && rot.equals("@")) {
         this.recipeOutput = new ItemStack(nbtCompound.func_74775_l("recipeout"));
      } else if (rot != null) {
         this.recipeOutputLabel = rot;
         this.recipeOutput = nbtCompound.func_74781_a("recipeout");
      }

      this.recipeInput = new ItemStack(nbtCompound.func_74775_l("recipeinput"));
      this.recipeType = nbtCompound.func_74762_e("recipetype");
      this.recipeXP = nbtCompound.func_74762_e("recipexp");
      this.recipePlayer = nbtCompound.func_74779_i("recipeplayer");
      if (this.recipePlayer.isEmpty()) {
         this.recipePlayer = null;
      }
   }

   @Override
   public NBTTagCompound func_189515_b(NBTTagCompound nbtCompound) {
      super.func_189515_b(nbtCompound);
      if (this.recipeIngredients != null && this.recipeIngredients.size() > 0) {
         NBTTagList nbttaglist = new NBTTagList();

         for (ItemStack stack : this.recipeIngredients) {
            if (!stack.func_190926_b()) {
               NBTTagCompound nbttagcompound1 = new NBTTagCompound();
               nbttagcompound1.func_74774_a("item", (byte)this.count);
               stack.func_77955_b(nbttagcompound1);
               nbttaglist.func_74742_a(nbttagcompound1);
               this.count++;
            }
         }

         nbtCompound.func_74782_a("recipein", nbttaglist);
      }

      if (this.recipeOutput != null && this.recipeOutput instanceof ItemStack) {
         nbtCompound.func_74778_a("rotype", "@");
      }

      if (this.recipeOutput != null && this.recipeOutput instanceof NBTBase) {
         nbtCompound.func_74778_a("rotype", this.recipeOutputLabel);
      }

      if (this.recipeOutput != null && this.recipeOutput instanceof ItemStack) {
         nbtCompound.func_74782_a("recipeout", ((ItemStack)this.recipeOutput).func_77955_b(new NBTTagCompound()));
      }

      if (this.recipeOutput != null && this.recipeOutput instanceof NBTBase) {
         nbtCompound.func_74782_a("recipeout", (NBTBase)this.recipeOutput);
      }

      if (this.recipeInput != null) {
         nbtCompound.func_74782_a("recipeinput", this.recipeInput.func_77955_b(new NBTTagCompound()));
      }

      nbtCompound.func_74768_a("recipetype", this.recipeType);
      nbtCompound.func_74768_a("recipexp", this.recipeXP);
      if (this.recipePlayer == null) {
         nbtCompound.func_74778_a("recipeplayer", "");
      } else {
         nbtCompound.func_74778_a("recipeplayer", this.recipePlayer);
      }

      return nbtCompound;
   }

   private TileInfusionMatrix.EnumStability getStability() {
      return this.stability > this.stabilityCap / 2
         ? TileInfusionMatrix.EnumStability.VERY_STABLE
         : (
            this.stability >= 0.0F
               ? TileInfusionMatrix.EnumStability.STABLE
               : (this.stability > -25.0F ? TileInfusionMatrix.EnumStability.UNSTABLE : TileInfusionMatrix.EnumStability.VERY_UNSTABLE)
         );
   }

   private float getModFromCurrentStability() {
      switch (this.getStability()) {
         case VERY_STABLE:
            return 5.0F;
         case STABLE:
            return 6.0F;
         case UNSTABLE:
            return 7.0F;
         case VERY_UNSTABLE:
            return 8.0F;
         default:
            return 1.0F;
      }
   }

   public void func_73660_a() {
      this.count++;
      if (this.checkSurroundings) {
         this.checkSurroundings = false;
         this.getSurroundings();
      }

      if (this.field_145850_b.field_72995_K) {
         this.doEffects();
      } else {
         if (this.count % (this.crafting ? 20 : 100) == 0 && !this.validLocation()) {
            this.active = false;
            this.func_70296_d();
            this.syncTile(false);
            return;
         }

         if (this.active && !this.crafting && this.stability < this.stabilityCap && this.count % Math.max(5, this.countDelay) == 0) {
            this.stability = this.stability + Math.max(0.1F, this.stabilityReplenish);
            if (this.stability > this.stabilityCap) {
               this.stability = this.stabilityCap;
            }

            this.func_70296_d();
            this.syncTile(false);
         }

         if (this.active && this.crafting && this.count % this.countDelay == 0) {
            this.craftCycle();
            this.func_70296_d();
         }
      }
   }

   public boolean validLocation() {
      if (!(this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(0, -2, 0)).func_177230_c() instanceof BlockPedestal)) {
         return false;
      } else if (!(this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(1, -2, 1)).func_177230_c() instanceof BlockPillar)) {
         return false;
      } else if (!(this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(-1, -2, 1)).func_177230_c() instanceof BlockPillar)) {
         return false;
      } else {
         return !(this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(1, -2, -1)).func_177230_c() instanceof BlockPillar)
            ? false
            : this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(-1, -2, -1)).func_177230_c() instanceof BlockPillar;
      }
   }

   public void craftingStart(EntityPlayer player) {
      if (!this.validLocation()) {
         this.active = false;
         this.func_70296_d();
         this.syncTile(false);
      } else {
         this.getSurroundings();
         TileEntity te = null;
         this.recipeInput = ItemStack.field_190927_a;
         te = this.field_145850_b.func_175625_s(this.field_174879_c.func_177979_c(2));
         if (te != null && te instanceof TilePedestal) {
            TilePedestal ped = (TilePedestal)te;
            if (!ped.func_70301_a(0).func_190926_b()) {
               this.recipeInput = ped.func_70301_a(0).func_77946_l();
            }
         }

         if (this.recipeInput != null && !this.recipeInput.func_190926_b()) {
            ArrayList<ItemStack> components = new ArrayList<>();

            for (BlockPos cc : this.pedestals) {
               te = this.field_145850_b.func_175625_s(cc);
               if (te != null && te instanceof TilePedestal) {
                  TilePedestal ped = (TilePedestal)te;
                  if (!ped.func_70301_a(0).func_190926_b()) {
                     components.add(ped.func_70301_a(0).func_77946_l());
                  }
               }
            }

            if (components.size() != 0) {
               InfusionRecipe recipe = ThaumcraftCraftingManager.findMatchingInfusionRecipe(components, this.recipeInput, player);
               if (this.costMult < 0.5) {
                  this.costMult = 0.5F;
               }

               if (recipe != null) {
                  this.recipeType = 0;
                  this.recipeIngredients = components;
                  if (recipe.getRecipeOutput(player, this.recipeInput, components) instanceof Object[]) {
                     Object[] obj = (Object[])recipe.getRecipeOutput(player, this.recipeInput, components);
                     this.recipeOutputLabel = (String)obj[0];
                     this.recipeOutput = (NBTBase)obj[1];
                  } else {
                     this.recipeOutput = recipe.getRecipeOutput(player, this.recipeInput, components);
                  }

                  this.recipeInstability = recipe.getInstability(player, this.recipeInput, components);
                  AspectList al = recipe.getAspects(player, this.recipeInput, components);
                  AspectList al2 = new AspectList();

                  for (Aspect as : al.getAspects()) {
                     if ((int)(al.getAmount(as) * this.costMult) > 0) {
                        al2.add(as, (int)(al.getAmount(as) * this.costMult));
                     }
                  }

                  this.recipeEssentia = al2;
                  this.recipePlayer = player.func_70005_c_();
                  this.crafting = true;
                  this.field_145850_b.func_184133_a(null, this.field_174879_c, SoundsTC.craftstart, SoundCategory.BLOCKS, 0.5F, 1.0F);
                  this.syncTile(false);
                  this.func_70296_d();
               }
            }
         }
      }
   }

   private float getLossPerCycle() {
      return this.recipeInstability / this.getModFromCurrentStability();
   }

   public void craftCycle() {
      boolean valid = false;
      float ff = this.field_145850_b.field_73012_v.nextFloat() * this.getLossPerCycle();
      this.stability -= ff;
      this.stability = this.stability + this.stabilityReplenish;
      if (this.stability < -100.0F) {
         this.stability = -100.0F;
      }

      if (this.stability > this.stabilityCap) {
         this.stability = this.stabilityCap;
      }

      TileEntity te = this.field_145850_b.func_175625_s(this.field_174879_c.func_177979_c(2));
      if (te != null && te instanceof TilePedestal) {
         TilePedestal ped = (TilePedestal)te;
         if (!ped.func_70301_a(0).func_190926_b()) {
            ItemStack i2 = ped.func_70301_a(0).func_77946_l();
            if (this.recipeInput.func_77952_i() == 32767) {
               i2.func_77964_b(32767);
            }

            if (ThaumcraftInvHelper.areItemStacksEqualForCrafting(i2, this.recipeInput)) {
               valid = true;
            }
         }
      }

      if (!valid || this.stability < 0.0F && this.field_145850_b.field_73012_v.nextInt(1500) <= Math.abs(this.stability)) {
         switch (this.field_145850_b.field_73012_v.nextInt(24)) {
            case 0:
            case 1:
            case 2:
            case 3:
               this.inEvEjectItem(0);
               break;
            case 4:
            case 5:
            case 6:
               this.inEvWarp();
               break;
            case 7:
            case 8:
            case 9:
               this.inEvZap(false);
               break;
            case 10:
            case 11:
               this.inEvZap(true);
               break;
            case 12:
            case 13:
               this.inEvEjectItem(1);
               break;
            case 14:
            case 15:
               this.inEvEjectItem(2);
               break;
            case 16:
               this.inEvEjectItem(3);
               break;
            case 17:
               this.inEvEjectItem(4);
               break;
            case 18:
            case 19:
               this.inEvHarm(false);
               break;
            case 20:
            case 21:
               this.inEvEjectItem(5);
               break;
            case 22:
               this.inEvHarm(true);
               break;
            case 23:
               this.field_145850_b
                  .func_72876_a(
                     null,
                     this.field_174879_c.func_177958_n() + 0.5,
                     this.field_174879_c.func_177956_o() + 0.5,
                     this.field_174879_c.func_177952_p() + 0.5,
                     1.5F + this.field_145850_b.field_73012_v.nextFloat(),
                     false
                  );
         }

         this.stability = this.stability + (5.0F + this.field_145850_b.field_73012_v.nextFloat() * 5.0F);
         this.inResAdd();
         if (valid) {
            return;
         }
      }

      if (!valid) {
         this.crafting = false;
         this.recipeEssentia = new AspectList();
         this.recipeInstability = 0;
         this.syncTile(false);
         this.field_145850_b.func_184133_a(null, this.field_174879_c, SoundsTC.craftfail, SoundCategory.BLOCKS, 1.0F, 0.6F);
         this.func_70296_d();
      } else if (this.recipeType == 1 && this.recipeXP > 0) {
         List<EntityPlayer> targets = this.field_145850_b
            .func_72872_a(
               EntityPlayer.class,
               new AxisAlignedBB(
                     this.func_174877_v().func_177958_n(),
                     this.func_174877_v().func_177956_o(),
                     this.func_174877_v().func_177952_p(),
                     this.func_174877_v().func_177958_n() + 1,
                     this.func_174877_v().func_177956_o() + 1,
                     this.func_174877_v().func_177952_p() + 1
                  )
                  .func_72314_b(10.0, 10.0, 10.0)
            );
         if (targets != null && targets.size() > 0) {
            for (EntityPlayer target : targets) {
               if (target.field_71075_bZ.field_75098_d || target.field_71068_ca > 0) {
                  if (!target.field_71075_bZ.field_75098_d) {
                     target.func_82242_a(-1);
                  }

                  this.recipeXP--;
                  target.func_70097_a(DamageSource.field_76376_m, this.field_145850_b.field_73012_v.nextInt(2));
                  PacketHandler.INSTANCE
                     .sendToAllAround(
                        new PacketFXInfusionSource(this.field_174879_c, this.field_174879_c, target.func_145782_y()),
                        new TargetPoint(
                           this.func_145831_w().field_73011_w.getDimension(),
                           this.field_174879_c.func_177958_n(),
                           this.field_174879_c.func_177956_o(),
                           this.field_174879_c.func_177952_p(),
                           32.0
                        )
                     );
                  target.func_184185_a(SoundEvents.field_187659_cY, 1.0F, 2.0F + this.field_145850_b.field_73012_v.nextFloat() * 0.4F);
                  this.countDelay = this.cycleTime;
                  return;
               }
            }

            Aspect[] ingEss = this.recipeEssentia.getAspects();
            if (ingEss != null && ingEss.length > 0 && this.field_145850_b.field_73012_v.nextInt(3) == 0) {
               Aspect as = ingEss[this.field_145850_b.field_73012_v.nextInt(ingEss.length)];
               this.recipeEssentia.add(as, 1);
               this.stability -= 0.25F;
               this.syncTile(false);
               this.func_70296_d();
            }
         }
      } else {
         if (this.recipeType == 1 && this.recipeXP == 0) {
            this.countDelay = this.cycleTime / 2;
         }

         if (this.countDelay < 1) {
            this.countDelay = 1;
         }

         if (this.recipeEssentia.visSize() > 0) {
            for (Aspect aspect : this.recipeEssentia.getAspects()) {
               int na = this.recipeEssentia.getAmount(aspect);
               if (na > 0) {
                  if (EssentiaHandler.drainEssentia(this, aspect, null, 12, na > 1 ? this.countDelay : 0)) {
                     this.recipeEssentia.reduce(aspect, 1);
                     this.syncTile(false);
                     this.func_70296_d();
                     return;
                  }

                  this.stability -= 0.25F;
                  this.syncTile(false);
                  this.func_70296_d();
               }
            }

            this.checkSurroundings = true;
         } else if (this.recipeIngredients.size() <= 0) {
            this.crafting = false;
            this.craftingFinish(this.recipeOutput, this.recipeOutputLabel);
            this.recipeOutput = null;
            this.syncTile(false);
            this.func_70296_d();
         } else {
            for (int a = 0; a < this.recipeIngredients.size(); a++) {
               for (BlockPos cc : this.pedestals) {
                  te = this.field_145850_b.func_175625_s(cc);
                  if (te != null
                     && te instanceof TilePedestal
                     && ((TilePedestal)te).func_70301_a(0) != null
                     && !((TilePedestal)te).func_70301_a(0).func_190926_b()
                     && ThaumcraftInvHelper.areItemStacksEqualForCrafting(((TilePedestal)te).func_70301_a(0), this.recipeIngredients.get(a))) {
                     if (this.itemCount == 0) {
                        this.itemCount = 5;
                        PacketHandler.INSTANCE
                           .sendToAllAround(
                              new PacketFXInfusionSource(this.field_174879_c, cc, 0),
                              new TargetPoint(
                                 this.func_145831_w().field_73011_w.getDimension(),
                                 this.field_174879_c.func_177958_n(),
                                 this.field_174879_c.func_177956_o(),
                                 this.field_174879_c.func_177952_p(),
                                 32.0
                              )
                           );
                     } else if (this.itemCount-- <= 1) {
                        ItemStack is = ((TilePedestal)te).func_70301_a(0).func_77973_b().getContainerItem(((TilePedestal)te).func_70301_a(0));
                        ((TilePedestal)te).func_70299_a(0, is != null && !is.func_190926_b() ? is.func_77946_l() : ItemStack.field_190927_a);
                        ((TilePedestal)te).func_70296_d();
                        ((TilePedestal)te).syncTile(false);
                        this.recipeIngredients.remove(a);
                        this.func_70296_d();
                     }

                     return;
                  }
               }

               Aspect[] ingEss = this.recipeEssentia.getAspects();
               if (ingEss != null && ingEss.length > 0 && this.field_145850_b.field_73012_v.nextInt(1 + a) == 0) {
                  Aspect as = ingEss[this.field_145850_b.field_73012_v.nextInt(ingEss.length)];
                  this.recipeEssentia.add(as, 1);
                  this.stability -= 0.25F;
                  this.syncTile(false);
                  this.func_70296_d();
               }
            }
         }
      }
   }

   private void inEvZap(boolean all) {
      List<EntityLivingBase> targets = this.field_145850_b
         .func_72872_a(
            EntityLivingBase.class,
            new AxisAlignedBB(
                  this.func_174877_v().func_177958_n(),
                  this.func_174877_v().func_177956_o(),
                  this.func_174877_v().func_177952_p(),
                  this.func_174877_v().func_177958_n() + 1,
                  this.func_174877_v().func_177956_o() + 1,
                  this.func_174877_v().func_177952_p() + 1
               )
               .func_72314_b(10.0, 10.0, 10.0)
         );
      if (targets != null && targets.size() > 0) {
         for (EntityLivingBase target : targets) {
            PacketHandler.INSTANCE
               .sendToAllAround(
                  new PacketFXBlockArc(
                     this.field_174879_c,
                     target,
                     0.3F - this.field_145850_b.field_73012_v.nextFloat() * 0.1F,
                     0.0F,
                     0.3F - this.field_145850_b.field_73012_v.nextFloat() * 0.1F
                  ),
                  new TargetPoint(
                     this.field_145850_b.field_73011_w.getDimension(),
                     this.field_174879_c.func_177958_n(),
                     this.field_174879_c.func_177956_o(),
                     this.field_174879_c.func_177952_p(),
                     32.0
                  )
               );
            target.func_70097_a(DamageSource.field_76376_m, 4 + this.field_145850_b.field_73012_v.nextInt(4));
            if (!all) {
               break;
            }
         }
      }
   }

   private void inEvHarm(boolean all) {
      List<EntityLivingBase> targets = this.field_145850_b
         .func_72872_a(
            EntityLivingBase.class,
            new AxisAlignedBB(
                  this.func_174877_v().func_177958_n(),
                  this.func_174877_v().func_177956_o(),
                  this.func_174877_v().func_177952_p(),
                  this.func_174877_v().func_177958_n() + 1,
                  this.func_174877_v().func_177956_o() + 1,
                  this.func_174877_v().func_177952_p() + 1
               )
               .func_72314_b(10.0, 10.0, 10.0)
         );
      if (targets != null && targets.size() > 0) {
         for (EntityLivingBase target : targets) {
            if (this.field_145850_b.field_73012_v.nextBoolean()) {
               target.func_70690_d(new PotionEffect(PotionFluxTaint.instance, 120, 0, false, true));
            } else {
               PotionEffect pe = new PotionEffect(PotionVisExhaust.instance, 2400, 0, true, true);
               pe.getCurativeItems().clear();
               target.func_70690_d(pe);
            }

            if (!all) {
               break;
            }
         }
      }
   }

   private void inResAdd() {
      List<EntityPlayer> targets = this.field_145850_b
         .func_72872_a(
            EntityPlayer.class,
            new AxisAlignedBB(
                  this.func_174877_v().func_177958_n(),
                  this.func_174877_v().func_177956_o(),
                  this.func_174877_v().func_177952_p(),
                  this.func_174877_v().func_177958_n() + 1,
                  this.func_174877_v().func_177956_o() + 1,
                  this.func_174877_v().func_177952_p() + 1
               )
               .func_186662_g(10.0)
         );
      if (targets != null && targets.size() > 0) {
         for (EntityPlayer player : targets) {
            IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
            if (!knowledge.isResearchKnown("!INSTABILITY")) {
               knowledge.addResearch("!INSTABILITY");
               knowledge.sync((EntityPlayerMP)player);
               player.func_146105_b(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.func_74838_a("got.instability")), true);
            }
         }
      }
   }

   private void inEvWarp() {
      List<EntityPlayer> targets = this.field_145850_b
         .func_72872_a(
            EntityPlayer.class,
            new AxisAlignedBB(
                  this.func_174877_v().func_177958_n(),
                  this.func_174877_v().func_177956_o(),
                  this.func_174877_v().func_177952_p(),
                  this.func_174877_v().func_177958_n() + 1,
                  this.func_174877_v().func_177956_o() + 1,
                  this.func_174877_v().func_177952_p() + 1
               )
               .func_186662_g(10.0)
         );
      if (targets != null && targets.size() > 0) {
         EntityPlayer target = targets.get(this.field_145850_b.field_73012_v.nextInt(targets.size()));
         if (this.field_145850_b.field_73012_v.nextFloat() < 0.25F) {
            ThaumcraftApi.internalMethods.addWarpToPlayer(target, 1, IPlayerWarp.EnumWarpType.NORMAL);
         } else {
            ThaumcraftApi.internalMethods.addWarpToPlayer(target, 2 + this.field_145850_b.field_73012_v.nextInt(4), IPlayerWarp.EnumWarpType.TEMPORARY);
         }
      }
   }

   private void inEvEjectItem(int type) {
      for (int retries = 0; retries < 25 && this.pedestals.size() > 0; retries++) {
         BlockPos cc = this.pedestals.get(this.field_145850_b.field_73012_v.nextInt(this.pedestals.size()));
         TileEntity te = this.field_145850_b.func_175625_s(cc);
         if (te != null && te instanceof TilePedestal && ((TilePedestal)te).func_70301_a(0) != null && !((TilePedestal)te).func_70301_a(0).func_190926_b()) {
            BlockPos stabPos = ((TilePedestal)te).findInstabilityMitigator();
            if (stabPos != null) {
               TileEntity ste = this.field_145850_b.func_175625_s(stabPos);
               if (ste != null && ste instanceof TileStabilizer) {
                  TileStabilizer tste = (TileStabilizer)ste;
                  if (tste.mitigate(MathHelper.func_76136_a(this.field_145850_b.field_73012_v, 5, 10))) {
                     this.field_145850_b.func_175641_c(cc, this.field_145850_b.func_180495_p(cc).func_177230_c(), 5, 0);
                     PacketHandler.INSTANCE
                        .sendToAllAround(
                           new PacketFXBlockArc(
                              this.field_174879_c,
                              cc.func_177984_a(),
                              0.3F - this.field_145850_b.field_73012_v.nextFloat() * 0.1F,
                              0.0F,
                              0.3F - this.field_145850_b.field_73012_v.nextFloat() * 0.1F
                           ),
                           new TargetPoint(this.field_145850_b.field_73011_w.getDimension(), cc.func_177958_n(), cc.func_177956_o(), cc.func_177952_p(), 32.0)
                        );
                     PacketHandler.INSTANCE
                        .sendToAllAround(
                           new PacketFXBlockArc(
                              cc.func_177984_a(),
                              stabPos,
                              0.3F - this.field_145850_b.field_73012_v.nextFloat() * 0.1F,
                              0.0F,
                              0.3F - this.field_145850_b.field_73012_v.nextFloat() * 0.1F
                           ),
                           new TargetPoint(
                              this.field_145850_b.field_73011_w.getDimension(), stabPos.func_177958_n(), stabPos.func_177956_o(), stabPos.func_177952_p(), 32.0
                           )
                        );
                     return;
                  }
               }
            }

            if (type > 3 && type != 5) {
               ((TilePedestal)te).func_70299_a(0, ItemStack.field_190927_a);
            } else {
               InventoryUtils.dropItems(this.field_145850_b, cc);
            }

            ((TilePedestal)te).func_70296_d();
            ((TilePedestal)te).syncTile(false);
            if (type == 1 || type == 3) {
               this.field_145850_b.func_175656_a(cc.func_177984_a(), BlocksTC.fluxGoo.func_176223_P());
               this.field_145850_b.func_184133_a(null, cc, SoundEvents.field_187615_H, SoundCategory.BLOCKS, 0.3F, 1.0F);
            } else if (type == 2 || type == 4) {
               int a = 5 + this.field_145850_b.field_73012_v.nextInt(5);
               AuraHelper.polluteAura(this.field_145850_b, cc, a, true);
            } else if (type == 5) {
               this.field_145850_b.func_72876_a(null, cc.func_177958_n() + 0.5F, cc.func_177956_o() + 0.5F, cc.func_177952_p() + 0.5F, 1.0F, false);
            }

            this.field_145850_b.func_175641_c(cc, this.field_145850_b.func_180495_p(cc).func_177230_c(), 11, 0);
            PacketHandler.INSTANCE
               .sendToAllAround(
                  new PacketFXBlockArc(
                     this.field_174879_c,
                     cc.func_177984_a(),
                     0.3F - this.field_145850_b.field_73012_v.nextFloat() * 0.1F,
                     0.0F,
                     0.3F - this.field_145850_b.field_73012_v.nextFloat() * 0.1F
                  ),
                  new TargetPoint(this.field_145850_b.field_73011_w.getDimension(), cc.func_177958_n(), cc.func_177956_o(), cc.func_177952_p(), 32.0)
               );
            return;
         }
      }
   }

   public void craftingFinish(Object out, String label) {
      TileEntity te = this.field_145850_b.func_175625_s(this.field_174879_c.func_177979_c(2));
      if (te != null && te instanceof TilePedestal) {
         float dmg = 1.0F;
         if (out instanceof ItemStack) {
            ItemStack qs = ((ItemStack)out).func_77946_l();
            if (((TilePedestal)te).func_70301_a(0).func_77984_f() && ((TilePedestal)te).func_70301_a(0).func_77951_h()) {
               dmg = (float)((TilePedestal)te).func_70301_a(0).func_77952_i() / ((TilePedestal)te).func_70301_a(0).func_77958_k();
               if (qs.func_77984_f() && !qs.func_77951_h()) {
                  qs.func_77964_b((int)(qs.func_77958_k() * dmg));
               }
            }

            ((TilePedestal)te).setInventorySlotContentsFromInfusion(0, qs);
         } else if (out instanceof NBTBase) {
            ItemStack temp = ((TilePedestal)te).func_70301_a(0);
            NBTBase tag = (NBTBase)out;
            temp.func_77983_a(label, tag);
            this.syncTile(false);
            te.func_70296_d();
         } else if (out instanceof Enchantment) {
            ItemStack temp = ((TilePedestal)te).func_70301_a(0);
            Map enchantments = EnchantmentHelper.func_82781_a(temp);
            enchantments.put((Enchantment)out, EnchantmentHelper.func_77506_a((Enchantment)out, temp) + 1);
            EnchantmentHelper.func_82782_a(enchantments, temp);
            this.syncTile(false);
            te.func_70296_d();
         }

         if (this.recipePlayer != null) {
            EntityPlayer p = this.field_145850_b.func_72924_a(this.recipePlayer);
            if (p != null) {
               FMLCommonHandler.instance().firePlayerCraftingEvent(p, ((TilePedestal)te).func_70301_a(0), new InventoryFake(this.recipeIngredients));
            }
         }

         this.recipeEssentia = new AspectList();
         this.recipeInstability = 0;
         this.syncTile(false);
         this.func_70296_d();
         this.field_145850_b
            .func_175641_c(this.field_174879_c.func_177979_c(2), this.field_145850_b.func_180495_p(this.field_174879_c.func_177979_c(2)).func_177230_c(), 12, 0);
         this.field_145850_b.func_184133_a(null, this.field_174879_c, SoundsTC.wand, SoundCategory.BLOCKS, 0.5F, 1.0F);
      }
   }

   private void getSurroundings() {
      Set<Long> stuff = new HashSet<>();
      this.pedestals.clear();
      this.tempBlockCount.clear();
      this.problemBlocks.clear();
      this.cycleTime = 10;
      this.stabilityReplenish = 0.0F;
      this.costMult = 1.0F;

      try {
         for (int xx = -8; xx <= 8; xx++) {
            for (int zz = -8; zz <= 8; zz++) {
               boolean skip = false;

               for (int yy = -3; yy <= 7; yy++) {
                  if (xx != 0 || zz != 0) {
                     int x = this.field_174879_c.func_177958_n() + xx;
                     int y = this.field_174879_c.func_177956_o() - yy;
                     int z = this.field_174879_c.func_177952_p() + zz;
                     BlockPos bp = new BlockPos(x, y, z);
                     Block bi = this.field_145850_b.func_180495_p(bp).func_177230_c();
                     if (bi instanceof BlockPedestal) {
                        this.pedestals.add(bp);
                     }

                     try {
                        if (bi == Blocks.field_150465_bP
                           || bi instanceof IInfusionStabiliser && ((IInfusionStabiliser)bi).canStabaliseInfusion(this.func_145831_w(), bp)) {
                           stuff.add(bp.func_177986_g());
                        }
                     } catch (Exception var15) {
                     }
                  }
               }
            }
         }

         while (!stuff.isEmpty()) {
            Long[] posArray = stuff.toArray(new Long[stuff.size()]);
            if (posArray == null || posArray[0] == null) {
               break;
            }

            long lp = posArray[0];

            try {
               BlockPos c1 = BlockPos.func_177969_a(lp);
               int x1 = this.field_174879_c.func_177958_n() - c1.func_177958_n();
               int z1 = this.field_174879_c.func_177952_p() - c1.func_177952_p();
               int x2 = this.field_174879_c.func_177958_n() + x1;
               int z2 = this.field_174879_c.func_177952_p() + z1;
               BlockPos c2 = new BlockPos(x2, c1.func_177956_o(), z2);
               Block sb1 = this.field_145850_b.func_180495_p(c1).func_177230_c();
               Block sb2 = this.field_145850_b.func_180495_p(c2).func_177230_c();
               float amt1 = 0.1F;
               float amt2 = 0.1F;
               if (sb1 instanceof IInfusionStabiliserExt) {
                  amt1 = ((IInfusionStabiliserExt)sb1).getStabilizationAmount(this.func_145831_w(), c1);
               }

               if (sb2 instanceof IInfusionStabiliserExt) {
                  amt2 = ((IInfusionStabiliserExt)sb2).getStabilizationAmount(this.func_145831_w(), c2);
               }

               if (sb1 == sb2 && amt1 == amt2) {
                  if (sb1 instanceof IInfusionStabiliserExt && ((IInfusionStabiliserExt)sb1).hasSymmetryPenalty(this.func_145831_w(), c1, c2)) {
                     this.stabilityReplenish = this.stabilityReplenish - ((IInfusionStabiliserExt)sb1).getSymmetryPenalty(this.func_145831_w(), c1);
                     this.problemBlocks.add(c1);
                  } else {
                     this.stabilityReplenish = this.stabilityReplenish + this.calcDeminishingReturns(sb1, amt1);
                  }
               } else {
                  this.stabilityReplenish = this.stabilityReplenish - Math.max(amt1, amt2);
                  this.problemBlocks.add(c1);
               }

               stuff.remove(c2.func_177986_g());
            } catch (Exception var16) {
            }

            stuff.remove(lp);
         }

         if (this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(-1, -2, -1)).func_177230_c() instanceof BlockPillar
            && this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(1, -2, -1)).func_177230_c() instanceof BlockPillar
            && this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(1, -2, 1)).func_177230_c() instanceof BlockPillar
            && this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(-1, -2, 1)).func_177230_c() instanceof BlockPillar) {
            if (this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(-1, -2, -1)).func_177230_c() == BlocksTC.pillarAncient
               && this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(1, -2, -1)).func_177230_c() == BlocksTC.pillarAncient
               && this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(1, -2, 1)).func_177230_c() == BlocksTC.pillarAncient
               && this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(-1, -2, 1)).func_177230_c() == BlocksTC.pillarAncient) {
               this.cycleTime--;
               this.costMult -= 0.1F;
               this.stabilityReplenish -= 0.1F;
            }

            if (this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(-1, -2, -1)).func_177230_c() == BlocksTC.pillarEldritch
               && this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(1, -2, -1)).func_177230_c() == BlocksTC.pillarEldritch
               && this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(1, -2, 1)).func_177230_c() == BlocksTC.pillarEldritch
               && this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(-1, -2, 1)).func_177230_c() == BlocksTC.pillarEldritch) {
               this.cycleTime -= 3;
               this.costMult += 0.05F;
               this.stabilityReplenish += 0.2F;
            }
         }

         int[] xm = new int[]{-1, 1, 1, -1};
         int[] zm = new int[]{-1, -1, 1, 1};

         for (int a = 0; a < 4; a++) {
            Block b = this.field_145850_b.func_180495_p(this.field_174879_c.func_177982_a(xm[a], -3, zm[a])).func_177230_c();
            if (b == BlocksTC.matrixSpeed) {
               this.cycleTime--;
               this.costMult += 0.01F;
            }

            if (b == BlocksTC.matrixCost) {
               this.cycleTime++;
               this.costMult -= 0.02F;
            }
         }

         this.countDelay = this.cycleTime / 2;
         int apc = 0;

         for (BlockPos cc : this.pedestals) {
            boolean items = false;
            int x = this.field_174879_c.func_177958_n() - cc.func_177958_n();
            int z = this.field_174879_c.func_177952_p() - cc.func_177952_p();
            Block bb = this.field_145850_b.func_180495_p(cc).func_177230_c();
            if (bb == BlocksTC.pedestalEldritch) {
               this.costMult += 0.0025F;
            }

            if (bb == BlocksTC.pedestalAncient) {
               this.costMult -= 0.01F;
            }
         }
      } catch (Exception var17) {
      }
   }

   private float calcDeminishingReturns(Block b, float base) {
      float bb = base;
      int c = this.tempBlockCount.containsKey(b) ? this.tempBlockCount.get(b) : 0;
      if (c > 0) {
         bb = (float)(bb * Math.pow(0.75, c));
      }

      this.tempBlockCount.put(b, c + 1);
      return bb;
   }

   @Override
   public boolean onCasterRightClick(World world, ItemStack wandstack, EntityPlayer player, BlockPos pos, EnumFacing side, EnumHand hand) {
      if (world.field_72995_K && this.active && !this.crafting) {
         this.checkSurroundings = true;
      }

      if (!world.field_72995_K && this.active && !this.crafting) {
         this.craftingStart(player);
         return false;
      } else if (!world.field_72995_K && !this.active && this.validLocation()) {
         world.func_184133_a(null, pos, SoundsTC.craftstart, SoundCategory.BLOCKS, 0.5F, 1.0F);
         this.active = true;
         this.syncTile(false);
         this.func_70296_d();
         return false;
      } else {
         return false;
      }
   }

   private void doEffects() {
      if (this.crafting) {
         if (this.craftCount == 0) {
            this.field_145850_b
               .func_184134_a(
                  this.field_174879_c.func_177958_n(),
                  this.field_174879_c.func_177956_o(),
                  this.field_174879_c.func_177952_p(),
                  SoundsTC.infuserstart,
                  SoundCategory.BLOCKS,
                  0.5F,
                  1.0F,
                  false
               );
         } else if (this.craftCount == 0 || this.craftCount % 65 == 0) {
            this.field_145850_b
               .func_184134_a(
                  this.field_174879_c.func_177958_n(),
                  this.field_174879_c.func_177956_o(),
                  this.field_174879_c.func_177952_p(),
                  SoundsTC.infuser,
                  SoundCategory.BLOCKS,
                  0.5F,
                  1.0F,
                  false
               );
         }

         this.craftCount++;
         FXDispatcher.INSTANCE
            .blockRunes(
               this.field_174879_c.func_177958_n(),
               this.field_174879_c.func_177956_o() - 2,
               this.field_174879_c.func_177952_p(),
               0.5F + this.field_145850_b.field_73012_v.nextFloat() * 0.2F,
               0.1F,
               0.7F + this.field_145850_b.field_73012_v.nextFloat() * 0.3F,
               25,
               -0.03F
            );
      } else if (this.craftCount > 0) {
         this.craftCount -= 2;
         if (this.craftCount < 0) {
            this.craftCount = 0;
         }

         if (this.craftCount > 50) {
            this.craftCount = 50;
         }
      }

      if (this.active && this.startUp != 1.0F) {
         if (this.startUp < 1.0F) {
            this.startUp = this.startUp + Math.max(this.startUp / 10.0F, 0.001F);
         }

         if (this.startUp > 0.999) {
            this.startUp = 1.0F;
         }
      }

      if (!this.active && this.startUp > 0.0F) {
         if (this.startUp > 0.0F) {
            this.startUp = this.startUp - this.startUp / 10.0F;
         }

         if (this.startUp < 0.001) {
            this.startUp = 0.0F;
         }
      }

      for (String fxk : this.sourceFX.keySet().toArray(new String[0])) {
         TileInfusionMatrix.SourceFX fx = this.sourceFX.get(fxk);
         if (fx.ticks <= 0) {
            this.sourceFX.remove(fxk);
         } else {
            if (fx.loc.equals(this.field_174879_c)) {
               Entity player = this.field_145850_b.func_73045_a(fx.color);
               if (player != null) {
                  for (int a = 0; a < 4; a++) {
                     FXDispatcher.INSTANCE
                        .drawInfusionParticles4(
                           player.field_70165_t
                              + (this.field_145850_b.field_73012_v.nextFloat() - this.field_145850_b.field_73012_v.nextFloat()) * player.field_70130_N,
                           player.func_174813_aQ().field_72338_b + this.field_145850_b.field_73012_v.nextFloat() * player.field_70131_O,
                           player.field_70161_v
                              + (this.field_145850_b.field_73012_v.nextFloat() - this.field_145850_b.field_73012_v.nextFloat()) * player.field_70130_N,
                           this.field_174879_c.func_177958_n(),
                           this.field_174879_c.func_177956_o(),
                           this.field_174879_c.func_177952_p()
                        );
                  }
               }
            } else {
               TileEntity tile = this.field_145850_b.func_175625_s(fx.loc);
               if (tile instanceof TilePedestal) {
                  ItemStack is = ((TilePedestal)tile).getSyncedStackInSlot(0);
                  if (is != null && !is.func_190926_b()) {
                     if (this.field_145850_b.field_73012_v.nextInt(3) == 0) {
                        FXDispatcher.INSTANCE
                           .drawInfusionParticles3(
                              fx.loc.func_177958_n() + this.field_145850_b.field_73012_v.nextFloat(),
                              fx.loc.func_177956_o() + this.field_145850_b.field_73012_v.nextFloat() + 1.0F,
                              fx.loc.func_177952_p() + this.field_145850_b.field_73012_v.nextFloat(),
                              this.field_174879_c.func_177958_n(),
                              this.field_174879_c.func_177956_o(),
                              this.field_174879_c.func_177952_p()
                           );
                     } else {
                        Item bi = is.func_77973_b();
                        if (bi instanceof ItemBlock) {
                           for (int a = 0; a < 4; a++) {
                              FXDispatcher.INSTANCE
                                 .drawInfusionParticles2(
                                    fx.loc.func_177958_n() + this.field_145850_b.field_73012_v.nextFloat(),
                                    fx.loc.func_177956_o() + this.field_145850_b.field_73012_v.nextFloat() + 1.0F,
                                    fx.loc.func_177952_p() + this.field_145850_b.field_73012_v.nextFloat(),
                                    this.field_174879_c,
                                    Block.func_149634_a(bi).func_176223_P(),
                                    is.func_77952_i()
                                 );
                           }
                        } else {
                           for (int a = 0; a < 4; a++) {
                              FXDispatcher.INSTANCE
                                 .drawInfusionParticles1(
                                    fx.loc.func_177958_n() + 0.4F + this.field_145850_b.field_73012_v.nextFloat() * 0.2F,
                                    fx.loc.func_177956_o() + 1.23F + this.field_145850_b.field_73012_v.nextFloat() * 0.2F,
                                    fx.loc.func_177952_p() + 0.4F + this.field_145850_b.field_73012_v.nextFloat() * 0.2F,
                                    this.field_174879_c,
                                    is
                                 );
                           }
                        }
                     }
                  }
               } else {
                  fx.ticks = 0;
               }
            }

            fx.ticks--;
            this.sourceFX.put(fxk, fx);
         }
      }

      if (this.crafting && this.stability < 0.0F && this.field_145850_b.field_73012_v.nextInt(250) <= Math.abs(this.stability)) {
         FXDispatcher.INSTANCE
            .spark(
               this.func_174877_v().func_177958_n() + this.field_145850_b.field_73012_v.nextFloat(),
               this.func_174877_v().func_177956_o() + this.field_145850_b.field_73012_v.nextFloat(),
               this.func_174877_v().func_177952_p() + this.field_145850_b.field_73012_v.nextFloat(),
               3.0F + this.field_145850_b.field_73012_v.nextFloat() * 2.0F,
               0.7F + this.field_145850_b.field_73012_v.nextFloat() * 0.1F,
               0.1F,
               0.65F + this.field_145850_b.field_73012_v.nextFloat() * 0.1F,
               0.8F
            );
      }

      if (this.active && !this.problemBlocks.isEmpty() && this.field_145850_b.field_73012_v.nextInt(25) == 0) {
         BlockPos p = this.problemBlocks.get(this.field_145850_b.field_73012_v.nextInt(this.problemBlocks.size()));
         FXDispatcher.INSTANCE
            .spark(
               p.func_177958_n() + this.field_145850_b.field_73012_v.nextFloat(),
               p.func_177956_o() + this.field_145850_b.field_73012_v.nextFloat(),
               p.func_177952_p() + this.field_145850_b.field_73012_v.nextFloat(),
               2.0F + this.field_145850_b.field_73012_v.nextFloat(),
               0.7F + this.field_145850_b.field_73012_v.nextFloat() * 0.1F,
               0.1F,
               0.65F + this.field_145850_b.field_73012_v.nextFloat() * 0.1F,
               0.8F
            );
      }
   }

   @Override
   public AspectList getAspects() {
      return this.recipeEssentia;
   }

   @Override
   public void setAspects(AspectList aspects) {
   }

   @Override
   public int addToContainer(Aspect tag, int amount) {
      return 0;
   }

   @Override
   public boolean takeFromContainer(Aspect tag, int amount) {
      return false;
   }

   @Override
   public boolean takeFromContainer(AspectList ot) {
      return false;
   }

   @Override
   public boolean doesContainerContainAmount(Aspect tag, int amount) {
      return false;
   }

   @Override
   public boolean doesContainerContain(AspectList ot) {
      return false;
   }

   @Override
   public int containerContains(Aspect tag) {
      return 0;
   }

   @Override
   public boolean doesContainerAccept(Aspect tag) {
      return true;
   }

   public boolean canRenderBreaking() {
      return true;
   }

   @Override
   public String[] getIGogglesText() {
      float lpc = this.getLossPerCycle();
      return lpc != 0.0F
         ? new String[]{
            TextFormatting.BOLD + I18n.func_74838_a("stability." + this.getStability().name()),
            TextFormatting.GOLD + "" + TextFormatting.ITALIC + myFormatter.format(this.stabilityReplenish) + " " + I18n.func_74838_a("stability.gain"),
            TextFormatting.RED
               + ""
               + I18n.func_74838_a("stability.range")
               + TextFormatting.ITALIC
               + myFormatter.format(lpc)
               + " "
               + I18n.func_74838_a("stability.loss")
         }
         : new String[]{
            TextFormatting.BOLD + I18n.func_74838_a("stability." + this.getStability().name()),
            TextFormatting.GOLD + "" + TextFormatting.ITALIC + myFormatter.format(this.stabilityReplenish) + " " + I18n.func_74838_a("stability.gain")
         };
   }

   private enum EnumStability {
      VERY_STABLE,
      STABLE,
      UNSTABLE,
      VERY_UNSTABLE;
   }

   public class SourceFX {
      public BlockPos loc;
      public int ticks;
      public int color;
      public int entity;

      public SourceFX(BlockPos loc, int ticks, int color) {
         this.loc = loc;
         this.ticks = ticks;
         this.color = color;
      }
   }
}

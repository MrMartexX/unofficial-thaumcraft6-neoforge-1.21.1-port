package thaumcraft.common.items.armor;

import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.renderers.models.gear.ModelRobe;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;

public class ItemVoidRobeArmor extends ItemArmor implements IVisDiscountGear, IGoggles, IRevealer, ISpecialArmor, IWarpingGear, IThaumcraftItems {
   ModelBiped model1 = null;
   ModelBiped model2 = null;
   ModelBiped model = null;

   public ItemVoidRobeArmor(String name, ArmorMaterial enumarmormaterial, int j, EntityEquipmentSlot k) {
      super(enumarmormaterial, j, k);
      this.func_77637_a(ConfigItems.TABTC);
      this.setRegistryName(name);
      this.func_77655_b(name);
      ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
   }

   @Override
   public Item getItem() {
      return this;
   }

   @Override
   public String[] getVariantNames() {
      return new String[]{"normal"};
   }

   @Override
   public int[] getVariantMeta() {
      return new int[]{0};
   }

   @SideOnly(Side.CLIENT)
   @Override
   public ItemMeshDefinition getCustomMesh() {
      return null;
   }

   @Override
   public ModelResourceLocation getCustomModelResourceLocation(String variant) {
      return new ModelResourceLocation("thaumcraft:" + variant);
   }

   public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
      return type == null ? "thaumcraft:textures/entity/armor/void_robe_armor_overlay.png" : "thaumcraft:textures/entity/armor/void_robe_armor.png";
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return EnumRarity.EPIC;
   }

   public boolean func_82789_a(ItemStack stack1, ItemStack stack2) {
      return stack2.func_77969_a(new ItemStack(ItemsTC.ingots, 1, 1)) ? true : super.func_82789_a(stack1, stack2);
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
      super.func_77663_a(stack, world, entity, p_77663_4_, p_77663_5_);
      if (!world.field_72995_K && stack.func_77951_h() && entity.field_70173_aa % 20 == 0 && entity instanceof EntityLivingBase) {
         stack.func_77972_a(-1, (EntityLivingBase)entity);
      }
   }

   public void onArmorTick(World world, EntityPlayer player, ItemStack armor) {
      super.onArmorTick(world, player, armor);
      if (!world.field_72995_K && armor.func_77952_i() > 0 && player.field_70173_aa % 20 == 0) {
         armor.func_77972_a(-1, player);
      }
   }

   @Override
   public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
      EntityEquipmentSlot type = ((ItemArmor)itemstack.func_77973_b()).field_77881_a;
      return type == EntityEquipmentSlot.HEAD;
   }

   @Override
   public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
      EntityEquipmentSlot type = ((ItemArmor)itemstack.func_77973_b()).field_77881_a;
      return type == EntityEquipmentSlot.HEAD;
   }

   @Override
   public int getVisDiscount(ItemStack stack, EntityPlayer player) {
      return 5;
   }

   @SideOnly(Side.CLIENT)
   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
      if (this.model1 == null) {
         this.model1 = new ModelRobe(1.0F);
      }

      if (this.model2 == null) {
         this.model2 = new ModelRobe(0.5F);
      }

      this.model = CustomArmorHelper.getCustomArmorModel(entityLiving, itemStack, armorSlot, this.model, this.model1, this.model2);
      return this.model;
   }

   public boolean func_82816_b_(ItemStack stack1) {
      return true;
   }

   public int func_82814_b(ItemStack stack1) {
      NBTTagCompound nbttagcompound = stack1.func_77978_p();
      if (nbttagcompound == null) {
         return 6961280;
      }

      NBTTagCompound nbttagcompound1 = nbttagcompound.func_74775_l("display");
      return nbttagcompound1 == null ? 6961280 : (nbttagcompound1.func_74764_b("color") ? nbttagcompound1.func_74762_e("color") : 6961280);
   }

   public void func_82815_c(ItemStack stack1) {
      NBTTagCompound nbttagcompound = stack1.func_77978_p();
      if (nbttagcompound != null) {
         NBTTagCompound nbttagcompound1 = nbttagcompound.func_74775_l("display");
         if (nbttagcompound1.func_74764_b("color")) {
            nbttagcompound1.func_82580_o("color");
         }
      }
   }

   public void func_82813_b(ItemStack stack1, int par2) {
      NBTTagCompound nbttagcompound = stack1.func_77978_p();
      if (nbttagcompound == null) {
         nbttagcompound = new NBTTagCompound();
         stack1.func_77982_d(nbttagcompound);
      }

      NBTTagCompound nbttagcompound1 = nbttagcompound.func_74775_l("display");
      if (!nbttagcompound.func_74764_b("display")) {
         nbttagcompound.func_74782_a("display", nbttagcompound1);
      }

      nbttagcompound1.func_74768_a("color", par2);
   }

   public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
      int priority = 0;
      double ratio = this.field_77879_b / 25.0;
      if (source.func_82725_o()) {
         priority = 1;
         ratio = this.field_77879_b / 35.0;
      } else if (source.func_76363_c()) {
         priority = 0;
         ratio = 0.0;
      }

      return new ArmorProperties(priority, ratio, armor.func_77958_k() + 1 - armor.func_77952_i());
   }

   public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
      return this.field_77879_b;
   }

   public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
      if (source != DamageSource.field_76379_h) {
         stack.func_77972_a(damage, entity);
      }
   }

   public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
      IBlockState bs = world.func_180495_p(pos);
      if (bs.func_177230_c() == Blocks.field_150383_bp) {
         int i = (Integer)bs.func_177229_b(BlockCauldron.field_176591_a);
         if (!world.field_72995_K && i > 0) {
            this.func_82815_c(player.func_184586_b(hand));
            Blocks.field_150383_bp.func_176590_a(world, pos, bs, i - 1);
            return EnumActionResult.SUCCESS;
         }
      }

      return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
   }

   @Override
   public int getWarp(ItemStack itemstack, EntityPlayer player) {
      return 3;
   }
}

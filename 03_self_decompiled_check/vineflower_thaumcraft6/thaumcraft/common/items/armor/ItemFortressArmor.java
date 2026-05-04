package thaumcraft.common.items.armor;

import java.util.List;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.renderers.models.gear.ModelFortressArmor;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;

public class ItemFortressArmor extends ItemArmor implements ISpecialArmor, IGoggles, IRevealer, IThaumcraftItems {
   ModelBiped model1 = null;
   ModelBiped model2 = null;
   ModelBiped model = null;

   public ItemFortressArmor(String name, ArmorMaterial material, int renderIndex, EntityEquipmentSlot armorType) {
      super(material, renderIndex, armorType);
      this.setRegistryName(name);
      this.func_77655_b(name);
      ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
      this.func_77637_a(ConfigItems.TABTC);
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

   @SideOnly(Side.CLIENT)
   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
      if (this.model1 == null) {
         this.model1 = new ModelFortressArmor(1.0F);
      }

      if (this.model2 == null) {
         this.model2 = new ModelFortressArmor(0.5F);
      }

      this.model = CustomArmorHelper.getCustomArmorModel(entityLiving, itemStack, armorSlot, this.model, this.model1, this.model2);
      return this.model;
   }

   public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
      return "thaumcraft:textures/entity/armor/fortress_armor.png";
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return EnumRarity.RARE;
   }

   @SideOnly(Side.CLIENT)
   public void func_77624_a(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (stack.func_77942_o() && stack.func_77978_p().func_74764_b("goggles")) {
         tooltip.add(TextFormatting.DARK_PURPLE + I18n.func_74838_a("item.goggles.name"));
      }

      if (stack.func_77942_o() && stack.func_77978_p().func_74764_b("mask")) {
         tooltip.add(TextFormatting.GOLD + I18n.func_74838_a("item.fortress_helm.mask." + stack.func_77978_p().func_74762_e("mask")));
      }

      super.func_77624_a(stack, worldIn, tooltip, flagIn);
   }

   public boolean func_82789_a(ItemStack stack1, ItemStack stack2) {
      return stack2.func_77969_a(new ItemStack(ItemsTC.ingots, 1, 0)) ? true : super.func_82789_a(stack1, stack2);
   }

   public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
      int priority = 0;
      double ratio = this.field_77879_b / 25.0;
      if (source.func_82725_o()) {
         priority = 1;
         ratio = this.field_77879_b / 35.0;
      } else if (source.func_76347_k() || source.func_94541_c()) {
         priority = 1;
         ratio = this.field_77879_b / 20.0;
      } else if (source.func_76363_c()) {
         priority = 0;
         ratio = 0.0;
      }

      ArmorProperties ap = new ArmorProperties(priority, ratio, armor.func_77958_k() + 1 - armor.func_77952_i());
      if (player instanceof EntityPlayer) {
         int q = 0;

         for (int a = 1; a < 4; a++) {
            ItemStack piece = (ItemStack)((EntityPlayer)player).field_71071_by.field_70460_b.get(a);
            if (piece != null && !piece.func_190926_b() && piece.func_77973_b() instanceof ItemFortressArmor) {
               if (piece.func_77942_o() && piece.func_77978_p().func_74764_b("mask")) {
                  ap.Armor++;
               }

               if (++q <= 1) {
                  ap.Armor++;
                  ap.Toughness++;
               }
            }
         }
      }

      return ap;
   }

   public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
      int q = 0;
      int ar = 0;

      for (int a = 1; a < 4; a++) {
         ItemStack piece = (ItemStack)player.field_71071_by.field_70460_b.get(a);
         if (piece != null && !piece.func_190926_b() && piece.func_77973_b() instanceof ItemFortressArmor) {
            if (piece.func_77942_o() && piece.func_77978_p().func_74764_b("mask")) {
               ar++;
            }

            if (++q <= 1) {
               ar++;
            }
         }
      }

      return ar;
   }

   public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
      if (source != DamageSource.field_76379_h) {
         stack.func_77972_a(damage, entity);
      }
   }

   @Override
   public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
      return itemstack.func_77942_o() && itemstack.func_77978_p().func_74764_b("goggles");
   }

   @Override
   public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
      return itemstack.func_77942_o() && itemstack.func_77978_p().func_74764_b("goggles");
   }
}

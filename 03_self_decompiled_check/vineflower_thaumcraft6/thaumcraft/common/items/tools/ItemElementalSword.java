package thaumcraft.common.items.tools;

import java.util.List;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;
import thaumcraft.common.lib.utils.EntityUtils;

public class ItemElementalSword extends ItemSword implements IThaumcraftItems {
   public ItemElementalSword(ToolMaterial enumtoolmaterial) {
      super(enumtoolmaterial);
      this.func_77637_a(ConfigItems.TABTC);
      this.setRegistryName("elemental_sword");
      this.func_77655_b("elemental_sword");
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

   @Override
   public ItemMeshDefinition getCustomMesh() {
      return null;
   }

   @Override
   public ModelResourceLocation getCustomModelResourceLocation(String variant) {
      return new ModelResourceLocation("thaumcraft:" + variant);
   }

   public void func_150895_a(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (tab != ConfigItems.TABTC && tab != CreativeTabs.field_78027_g) {
         super.func_150895_a(tab, items);
      } else {
         ItemStack w1 = new ItemStack(this);
         EnumInfusionEnchantment.addInfusionEnchantment(w1, EnumInfusionEnchantment.ARCING, 2);
         items.add(w1);
      }
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return EnumRarity.RARE;
   }

   public boolean func_82789_a(ItemStack stack1, ItemStack stack2) {
      return stack2.func_77969_a(new ItemStack(ItemsTC.ingots, 1, 0)) ? true : super.func_82789_a(stack1, stack2);
   }

   public EnumAction func_77661_b(ItemStack stack) {
      return EnumAction.NONE;
   }

   public int func_77626_a(ItemStack stack) {
      return 72000;
   }

   public ActionResult<ItemStack> func_77659_a(World worldIn, EntityPlayer playerIn, EnumHand hand) {
      playerIn.func_184598_c(hand);
      return new ActionResult(EnumActionResult.SUCCESS, playerIn.func_184586_b(hand));
   }

   public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
      super.onUsingTick(stack, player, count);
      int ticks = this.func_77626_a(stack) - count;
      if (player.field_70181_x < 0.0) {
         player.field_70181_x /= 1.2F;
         player.field_70143_R /= 1.2F;
      }

      player.field_70181_x += 0.08F;
      if (player.field_70181_x > 0.5) {
         player.field_70181_x = 0.2F;
      }

      if (player instanceof EntityPlayerMP) {
         EntityUtils.resetFloatCounter((EntityPlayerMP)player);
      }

      List targets = player.field_70170_p.func_72839_b(player, player.func_174813_aQ().func_72314_b(2.5, 2.5, 2.5));
      if (targets.size() > 0) {
         for (int var9 = 0; var9 < targets.size(); var9++) {
            Entity entity = (Entity)targets.get(var9);
            if (!(entity instanceof EntityPlayer)
               && entity instanceof EntityLivingBase
               && !entity.field_70128_L
               && (player.func_184187_bx() == null || player.func_184187_bx() != entity)) {
               Vec3d p = new Vec3d(player.field_70165_t, player.field_70163_u, player.field_70161_v);
               Vec3d t = new Vec3d(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v);
               double distance = p.func_72438_d(t) + 0.1;
               Vec3d r = new Vec3d(t.field_72450_a - p.field_72450_a, t.field_72448_b - p.field_72448_b, t.field_72449_c - p.field_72449_c);
               entity.field_70159_w = entity.field_70159_w + r.field_72450_a / 2.5 / distance;
               entity.field_70181_x = entity.field_70181_x + r.field_72448_b / 2.5 / distance;
               entity.field_70179_y = entity.field_70179_y + r.field_72449_c / 2.5 / distance;
            }
         }
      }

      if (player.field_70170_p.field_72995_K) {
         int miny = (int)(player.func_174813_aQ().field_72338_b - 2.0);
         if (player.field_70122_E) {
            miny = MathHelper.func_76128_c(player.func_174813_aQ().field_72338_b);
         }

         for (int a = 0; a < 5; a++) {
            FXDispatcher.INSTANCE
               .smokeSpiral(
                  player.field_70165_t,
                  player.func_174813_aQ().field_72338_b + player.field_70131_O / 2.0F,
                  player.field_70161_v,
                  1.5F,
                  player.field_70170_p.field_73012_v.nextInt(360),
                  miny,
                  14540253
               );
         }

         if (player.field_70122_E) {
            float r1 = player.field_70170_p.field_73012_v.nextFloat() * 360.0F;
            float mx = -MathHelper.func_76126_a(r1 / 180.0F * (float) Math.PI) / 5.0F;
            float mz = MathHelper.func_76134_b(r1 / 180.0F * (float) Math.PI) / 5.0F;
            player.field_70170_p
               .func_175688_a(
                  EnumParticleTypes.SMOKE_NORMAL,
                  player.field_70165_t,
                  player.func_174813_aQ().field_72338_b + 0.1F,
                  player.field_70161_v,
                  mx,
                  0.0,
                  mz,
                  new int[0]
               );
         }
      } else if (ticks == 0 || ticks % 20 == 0) {
         player.func_184185_a(SoundsTC.wind, 0.5F, 0.9F + player.field_70170_p.field_73012_v.nextFloat() * 0.2F);
      }

      if (ticks % 20 == 0) {
         stack.func_77972_a(1, player);
      }
   }
}

package thaumcraft.common.items.tools;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.items.IArchitect;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.lib.utils.Utils;

public class ItemElementalShovel extends ItemSpade implements IArchitect, IThaumcraftItems {
   private static final Block[] isEffective = new Block[]{
      Blocks.field_150349_c,
      Blocks.field_150346_d,
      Blocks.field_150354_m,
      Blocks.field_150351_n,
      Blocks.field_150431_aC,
      Blocks.field_150433_aE,
      Blocks.field_150435_aG,
      Blocks.field_150458_ak,
      Blocks.field_150425_aM,
      Blocks.field_150391_bh
   };
   EnumFacing side = EnumFacing.DOWN;

   public ItemElementalShovel(ToolMaterial enumtoolmaterial) {
      super(enumtoolmaterial);
      this.func_77637_a(ConfigItems.TABTC);
      this.setRegistryName("elemental_shovel");
      this.func_77655_b("elemental_shovel");
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

   public Set<String> getToolClasses(ItemStack stack) {
      return ImmutableSet.of("shovel");
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return EnumRarity.RARE;
   }

   public boolean func_82789_a(ItemStack stack1, ItemStack stack2) {
      return stack2.func_77969_a(new ItemStack(ItemsTC.ingots, 1, 0)) ? true : super.func_82789_a(stack1, stack2);
   }

   public EnumActionResult func_180614_a(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float par8, float par9, float par10) {
      IBlockState bs = world.func_180495_p(pos);
      TileEntity te = world.func_175625_s(pos);
      if (te == null) {
         for (int aa = -1; aa <= 1; aa++) {
            for (int bb = -1; bb <= 1; bb++) {
               int xx = 0;
               int yy = 0;
               int zz = 0;
               byte o = getOrientation(player.func_184586_b(hand));
               if (o == 1) {
                  yy = bb;
                  if (side.ordinal() <= 1) {
                     int l = MathHelper.func_76128_c(player.field_70177_z * 4.0F / 360.0F + 0.5) & 3;
                     if (l != 0 && l != 2) {
                        zz = aa;
                     } else {
                        xx = aa;
                     }
                  } else if (side.ordinal() <= 3) {
                     zz = aa;
                  } else {
                     xx = aa;
                  }
               } else if (o == 2) {
                  if (side.ordinal() <= 1) {
                     int l = MathHelper.func_76128_c(player.field_70177_z * 4.0F / 360.0F + 0.5) & 3;
                     yy = bb;
                     if (l != 0 && l != 2) {
                        zz = aa;
                     } else {
                        xx = aa;
                     }
                  } else {
                     zz = bb;
                     xx = aa;
                  }
               } else if (side.ordinal() <= 1) {
                  xx = aa;
                  zz = bb;
               } else if (side.ordinal() <= 3) {
                  xx = aa;
                  yy = bb;
               } else {
                  zz = aa;
                  yy = bb;
               }

               BlockPos p2 = pos.func_177972_a(side).func_177982_a(xx, yy, zz);
               IBlockState b2 = world.func_180495_p(p2);
               if (bs.func_177230_c().func_176196_c(world, p2)) {
                  if (player.field_71075_bZ.field_75098_d
                     || InventoryUtils.consumePlayerItem(player, Item.func_150898_a(bs.func_177230_c()), bs.func_177230_c().func_176201_c(bs))) {
                     world.func_184134_a(
                        p2.func_177958_n(),
                        p2.func_177956_o(),
                        p2.func_177952_p(),
                        bs.func_177230_c().func_185467_w().func_185845_c(),
                        SoundCategory.BLOCKS,
                        0.6F,
                        0.9F + world.field_73012_v.nextFloat() * 0.2F,
                        false
                     );
                     world.func_175656_a(p2, bs);
                     player.func_184586_b(hand).func_77972_a(1, player);
                     if (world.field_72995_K) {
                        FXDispatcher.INSTANCE.drawBamf(p2, 8401408, false, false, side);
                     }

                     player.func_184609_a(hand);
                  } else if (bs.func_177230_c() == Blocks.field_150349_c
                     && (player.field_71075_bZ.field_75098_d || InventoryUtils.consumePlayerItem(player, Item.func_150898_a(Blocks.field_150346_d), 0))) {
                     world.func_184134_a(
                        p2.func_177958_n(),
                        p2.func_177956_o(),
                        p2.func_177952_p(),
                        bs.func_177230_c().func_185467_w().func_185845_c(),
                        SoundCategory.BLOCKS,
                        0.6F,
                        0.9F + world.field_73012_v.nextFloat() * 0.2F,
                        false
                     );
                     world.func_175656_a(p2, Blocks.field_150346_d.func_176223_P());
                     player.func_184586_b(hand).func_77972_a(1, player);
                     if (world.field_72995_K) {
                        FXDispatcher.INSTANCE.drawBamf(p2, 8401408, false, false, side);
                     }

                     player.func_184609_a(hand);
                     if (player.func_184586_b(hand).func_190926_b() || player.func_184586_b(hand).func_190916_E() < 1) {
                        break;
                     }
                  }
               }
            }
         }
      }

      return EnumActionResult.FAIL;
   }

   private boolean isEffectiveAgainst(Block block) {
      for (int var3 = 0; var3 < isEffective.length; var3++) {
         if (isEffective[var3] == block) {
            return true;
         }
      }

      return false;
   }

   public void func_150895_a(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (tab != ConfigItems.TABTC && tab != CreativeTabs.field_78027_g) {
         super.func_150895_a(tab, items);
      } else {
         ItemStack w1 = new ItemStack(this);
         EnumInfusionEnchantment.addInfusionEnchantment(w1, EnumInfusionEnchantment.DESTRUCTIVE, 1);
         items.add(w1);
      }
   }

   @Override
   public ArrayList<BlockPos> getArchitectBlocks(ItemStack focusstack, World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
      ArrayList<BlockPos> b = new ArrayList<>();
      if (!player.func_70093_af()) {
         return b;
      }

      IBlockState bs = world.func_180495_p(pos);

      for (int aa = -1; aa <= 1; aa++) {
         for (int bb = -1; bb <= 1; bb++) {
            int xx = 0;
            int yy = 0;
            int zz = 0;
            byte o = getOrientation(focusstack);
            if (o == 1) {
               yy = bb;
               if (side.ordinal() <= 1) {
                  int l = MathHelper.func_76128_c(player.field_70177_z * 4.0F / 360.0F + 0.5) & 3;
                  if (l != 0 && l != 2) {
                     zz = aa;
                  } else {
                     xx = aa;
                  }
               } else if (side.ordinal() <= 3) {
                  zz = aa;
               } else {
                  xx = aa;
               }
            } else if (o == 2) {
               if (side.ordinal() <= 1) {
                  int l = MathHelper.func_76128_c(player.field_70177_z * 4.0F / 360.0F + 0.5) & 3;
                  yy = bb;
                  if (l != 0 && l != 2) {
                     zz = aa;
                  } else {
                     xx = aa;
                  }
               } else {
                  zz = bb;
                  xx = aa;
               }
            } else if (side.ordinal() <= 1) {
               xx = aa;
               zz = bb;
            } else if (side.ordinal() <= 3) {
               xx = aa;
               yy = bb;
            } else {
               zz = aa;
               yy = bb;
            }

            BlockPos p2 = pos.func_177972_a(side).func_177982_a(xx, yy, zz);
            IBlockState b2 = world.func_180495_p(p2);
            if (bs.func_177230_c().func_176196_c(world, p2)) {
               b.add(p2);
            }
         }
      }

      return b;
   }

   @Override
   public boolean showAxis(ItemStack stack, World world, EntityPlayer player, EnumFacing side, IArchitect.EnumAxis axis) {
      return false;
   }

   public static byte getOrientation(ItemStack stack) {
      return stack.func_77942_o() && stack.func_77978_p().func_74764_b("or") ? stack.func_77978_p().func_74771_c("or") : 0;
   }

   public static void setOrientation(ItemStack stack, byte o) {
      if (!stack.func_77942_o()) {
         stack.func_77982_d(new NBTTagCompound());
      }

      if (stack.func_77942_o()) {
         stack.func_77978_p().func_74774_a("or", (byte)(o % 3));
      }
   }

   @Override
   public RayTraceResult getArchitectMOP(ItemStack stack, World world, EntityLivingBase player) {
      return Utils.rayTrace(world, player, false);
   }

   @Override
   public boolean useBlockHighlight(ItemStack stack) {
      return true;
   }
}

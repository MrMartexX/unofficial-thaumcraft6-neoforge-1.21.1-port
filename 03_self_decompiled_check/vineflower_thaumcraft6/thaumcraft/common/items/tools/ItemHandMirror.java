package thaumcraft.common.items.tools;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.Thaumcraft;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.devices.TileMirror;

public class ItemHandMirror extends ItemTCBase {
   public ItemHandMirror() {
      super("hand_mirror");
      this.func_77625_d(1);
   }

   public boolean func_77651_p() {
      return true;
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return EnumRarity.UNCOMMON;
   }

   public boolean func_77636_d(ItemStack stack1) {
      return stack1.func_77942_o();
   }

   public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float par8, float par9, float par10, EnumHand hand) {
      Block bi = world.func_180495_p(pos).func_177230_c();
      if (bi == BlocksTC.mirror) {
         if (world.field_72995_K) {
            player.func_184609_a(hand);
            return super.onItemUseFirst(player, world, pos, side, par8, par9, par10, hand);
         }

         TileEntity tm = world.func_175625_s(pos);
         if (tm != null && tm instanceof TileMirror) {
            player.func_184586_b(hand).func_77983_a("linkX", new NBTTagInt(pos.func_177958_n()));
            player.func_184586_b(hand).func_77983_a("linkY", new NBTTagInt(pos.func_177956_o()));
            player.func_184586_b(hand).func_77983_a("linkZ", new NBTTagInt(pos.func_177952_p()));
            player.func_184586_b(hand).func_77983_a("linkDim", new NBTTagInt(world.field_73011_w.getDimension()));
            world.func_184148_a(null, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), SoundsTC.jar, SoundCategory.BLOCKS, 1.0F, 2.0F);
            player.func_145747_a(new TextComponentTranslation("tc.handmirrorlinked", new Object[0]));
            player.field_71069_bz.func_75142_b();
         }

         return EnumActionResult.PASS;
      } else {
         return EnumActionResult.FAIL;
      }
   }

   public ActionResult<ItemStack> func_77659_a(World world, EntityPlayer player, EnumHand hand) {
      if (!world.field_72995_K && player.func_184586_b(hand).func_77942_o()) {
         int lx = player.func_184586_b(hand).func_77978_p().func_74762_e("linkX");
         int ly = player.func_184586_b(hand).func_77978_p().func_74762_e("linkY");
         int lz = player.func_184586_b(hand).func_77978_p().func_74762_e("linkZ");
         int ldim = player.func_184586_b(hand).func_77978_p().func_74762_e("linkDim");
         World targetWorld = DimensionManager.getWorld(ldim);
         if (targetWorld == null) {
            return super.func_77659_a(world, player, hand);
         }

         TileEntity te = targetWorld.func_175625_s(new BlockPos(lx, ly, lz));
         if (te == null || !(te instanceof TileMirror)) {
            player.func_184586_b(hand).func_77982_d(null);
            player.func_184185_a(SoundsTC.zap, 1.0F, 0.8F);
            player.func_145747_a(new TextComponentTranslation("tc.handmirrorerror", new Object[0]));
            return super.func_77659_a(world, player, hand);
         }

         player.openGui(
            Thaumcraft.instance,
            4,
            world,
            MathHelper.func_76128_c(player.field_70165_t),
            MathHelper.func_76128_c(player.field_70163_u),
            MathHelper.func_76128_c(player.field_70161_v)
         );
      }

      return super.func_77659_a(world, player, hand);
   }

   @SideOnly(Side.CLIENT)
   public void func_77624_a(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (stack.func_77942_o()) {
         int lx = stack.func_77978_p().func_74762_e("linkX");
         int ly = stack.func_77978_p().func_74762_e("linkY");
         int lz = stack.func_77978_p().func_74762_e("linkZ");
         int ldim = stack.func_77978_p().func_74762_e("linkDim");
         tooltip.add(I18n.func_74838_a("tc.handmirrorlinkedto") + " " + lx + "," + ly + "," + lz + " in " + ldim);
      }
   }

   public static boolean transport(ItemStack mirror, ItemStack items, EntityPlayer player, World worldObj) {
      if (mirror.func_77942_o()) {
         int lx = mirror.func_77978_p().func_74762_e("linkX");
         int ly = mirror.func_77978_p().func_74762_e("linkY");
         int lz = mirror.func_77978_p().func_74762_e("linkZ");
         int ldim = mirror.func_77978_p().func_74762_e("linkDim");
         World targetWorld = DimensionManager.getWorld(ldim);
         if (targetWorld == null) {
            return false;
         }

         TileEntity te = targetWorld.func_175625_s(new BlockPos(lx, ly, lz));
         if (te != null && te instanceof TileMirror) {
            TileMirror tm = (TileMirror)te;
            if (tm.transportDirect(items)) {
               items = ItemStack.field_190927_a;
               player.func_184185_a(SoundEvents.field_187534_aX, 0.1F, 1.0F);
            }

            return true;
         } else {
            mirror.func_77982_d(null);
            player.func_184185_a(SoundsTC.zap, 1.0F, 0.8F);
            player.func_145747_a(new TextComponentTranslation("tc.handmirrorerror", new Object[0]));
            return false;
         }
      } else {
         return false;
      }
   }
}

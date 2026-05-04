package thaumcraft.common.blocks.essentia;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.tiles.essentia.TileAlembic;
import thaumcraft.common.tiles.essentia.TileJarFillable;

public class BlockJarItem extends ItemBlock implements IEssentiaContainerItem {
   public BlockJarItem(Block block) {
      super(block);
      this.func_185043_a(new ResourceLocation("fill"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float func_185085_a(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            if (stack.func_77973_b().getDurabilityForDisplay(stack) == 1.0) {
               return 0.0F;
            } else if (stack.func_77973_b().getDurabilityForDisplay(stack) >= 0.75) {
               return 1.0F;
            } else if (stack.func_77973_b().getDurabilityForDisplay(stack) >= 0.5) {
               return 2.0F;
            } else {
               return stack.func_77973_b().getDurabilityForDisplay(stack) >= 0.25 ? 3.0F : 4.0F;
            }
         }
      });
   }

   public boolean showDurabilityBar(ItemStack stack) {
      return this.getAspects(stack) != null;
   }

   public double getDurabilityForDisplay(ItemStack stack) {
      AspectList al = this.getAspects(stack);
      return al == null ? 1.0 : 1.0 - al.visSize() / 250.0;
   }

   public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
      Block bi = world.func_180495_p(pos).func_177230_c();
      ItemStack itemstack = player.func_184586_b(hand);
      if (bi == BlocksTC.alembic && !world.field_72995_K) {
         TileAlembic tile = (TileAlembic)world.func_175625_s(pos);
         if (tile.amount > 0) {
            if (this.getFilter(itemstack) != null && this.getFilter(itemstack) != tile.aspect) {
               return EnumActionResult.FAIL;
            }

            if (this.getAspects(itemstack) != null && this.getAspects(itemstack).getAspects()[0] != tile.aspect) {
               return EnumActionResult.FAIL;
            }

            int amt = tile.amount;
            if (this.getAspects(itemstack) != null && this.getAspects(itemstack).visSize() + amt > 250) {
               amt = Math.abs(this.getAspects(itemstack).visSize() - 250);
            }

            if (amt <= 0) {
               return EnumActionResult.FAIL;
            }

            Aspect a = tile.aspect;
            if (tile.takeFromContainer(tile.aspect, amt)) {
               int base = this.getAspects(itemstack) == null ? 0 : this.getAspects(itemstack).visSize();
               if (itemstack.func_190916_E() > 1) {
                  ItemStack stack = itemstack.func_77946_l();
                  this.setAspects(stack, new AspectList().add(a, base + amt));
                  itemstack.func_190918_g(1);
                  stack.func_190920_e(1);
                  if (!player.field_71071_by.func_70441_a(stack)) {
                     world.func_72838_d(new EntityItem(world, player.field_70165_t, player.field_70163_u, player.field_70161_v, stack));
                  }
               } else {
                  this.setAspects(itemstack, new AspectList().add(a, base + amt));
               }

               player.func_184185_a(SoundEvents.field_187615_H, 0.25F, 1.0F);
               player.field_71069_bz.func_75142_b();
               return EnumActionResult.SUCCESS;
            }
         }
      }

      return EnumActionResult.PASS;
   }

   public boolean placeBlockAt(
      ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState
   ) {
      boolean b = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
      if (b && !world.field_72995_K) {
         TileEntity te = world.func_175625_s(pos);
         if (te != null && te instanceof TileJarFillable) {
            TileJarFillable jar = (TileJarFillable)te;
            jar.setAspects(this.getAspects(stack));
            if (stack.func_77942_o() && stack.func_77978_p().func_74764_b("AspectFilter")) {
               jar.aspectFilter = Aspect.getAspect(stack.func_77978_p().func_74779_i("AspectFilter"));
            }

            te.func_70296_d();
            world.markAndNotifyBlock(pos, world.func_175726_f(pos), newState, newState, 3);
         }
      }

      return b;
   }

   @SideOnly(Side.CLIENT)
   public void func_77624_a(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (stack.func_77942_o() && stack.func_77978_p().func_74764_b("AspectFilter")) {
         String tf = stack.func_77978_p().func_74779_i("AspectFilter");
         Aspect tag = Aspect.getAspect(tf);
         tooltip.add("§5" + tag.getName());
      }

      super.func_77624_a(stack, worldIn, tooltip, flagIn);
   }

   @Override
   public AspectList getAspects(ItemStack itemstack) {
      if (itemstack.func_77942_o()) {
         AspectList aspects = new AspectList();
         aspects.readFromNBT(itemstack.func_77978_p());
         return aspects.size() > 0 ? aspects : null;
      } else {
         return null;
      }
   }

   public Aspect getFilter(ItemStack itemstack) {
      return itemstack.func_77942_o() ? Aspect.getAspect(itemstack.func_77978_p().func_74779_i("AspectFilter")) : null;
   }

   @Override
   public void setAspects(ItemStack itemstack, AspectList aspects) {
      if (!itemstack.func_77942_o()) {
         itemstack.func_77982_d(new NBTTagCompound());
      }

      aspects.writeToNBT(itemstack.func_77978_p());
   }

   @Override
   public boolean ignoreContainedAspects() {
      return false;
   }
}

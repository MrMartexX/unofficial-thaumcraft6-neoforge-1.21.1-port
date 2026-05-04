package thaumcraft.api.items;

import java.util.ArrayList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public interface IArchitect {
   RayTraceResult getArchitectMOP(ItemStack var1, World var2, EntityLivingBase var3);

   boolean useBlockHighlight(ItemStack var1);

   ArrayList<BlockPos> getArchitectBlocks(ItemStack var1, World var2, BlockPos var3, EnumFacing var4, EntityPlayer var5);

   boolean showAxis(ItemStack var1, World var2, EntityPlayer var3, EnumFacing var4, IArchitect.EnumAxis var5);

   enum EnumAxis {
      X,
      Y,
      Z;
   }
}

package thaumcraft.common.tiles.essentia;

import java.util.Random;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileJar extends TileThaumcraft implements ITickable {
   protected static Random rand = new Random();

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB(
         this.func_174877_v().func_177958_n(),
         this.func_174877_v().func_177956_o(),
         this.func_174877_v().func_177952_p(),
         this.func_174877_v().func_177958_n() + 1,
         this.func_174877_v().func_177956_o() + 1,
         this.func_174877_v().func_177952_p() + 1
      );
   }

   public void func_73660_a() {
   }
}

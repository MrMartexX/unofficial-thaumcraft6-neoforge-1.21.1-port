package thaumcraft.api.items;

import net.minecraft.util.math.Vec3d;

public interface IGogglesDisplayExtended {
   String[] getIGogglesText();

   default Vec3d getIGogglesTextOffset() {
      return new Vec3d(0.0, 0.0, 0.0);
   }
}

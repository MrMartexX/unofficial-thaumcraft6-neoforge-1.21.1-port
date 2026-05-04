package thaumcraft.common.items.casters.foci;

import java.awt.Color;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXZap;
import thaumcraft.common.lib.utils.EntityUtils;

public class FocusMediumBolt extends FocusMediumTouch {
   @Override
   public String getResearch() {
      return "FOCUSBOLT";
   }

   @Override
   public String getKey() {
      return "thaumcraft.BOLT";
   }

   @Override
   public int getComplexity() {
      return 5;
   }

   @Override
   public Aspect getAspect() {
      return Aspect.ENERGY;
   }

   @Override
   public boolean execute(Trajectory trajectory) {
      float range = 16.0F;
      Vec3d end = trajectory.direction.func_72432_b();
      RayTraceResult ray = EntityUtils.getPointedEntityRay(
         this.getPackage().world, this.getPackage().getCaster(), trajectory.source, end, 0.25, range, 0.25F, false
      );
      if (ray == null) {
         Vec3d var13 = end.func_186678_a(range);
         end = var13.func_178787_e(trajectory.source);
         ray = this.getPackage().world.func_72933_a(trajectory.source, end);
         if (ray != null) {
            end = ray.field_72307_f;
         }
      } else if (ray.field_72308_g != null) {
         end = end.func_186678_a(trajectory.source.func_72438_d(ray.field_72308_g.func_174791_d()));
         end = end.func_178787_e(trajectory.source);
      }

      int r = 0;
      int g = 0;
      int b = 0;

      for (FocusEffect ef : this.getPackage().getFocusEffects()) {
         Color c = new Color(FocusEngine.getElementColor(ef.getKey()));
         r += c.getRed();
         g += c.getGreen();
         b += c.getBlue();
      }

      r /= this.getPackage().getFocusEffects().length;
      g /= this.getPackage().getFocusEffects().length;
      b /= this.getPackage().getFocusEffects().length;
      Color c = new Color(r, g, b);
      PacketHandler.INSTANCE
         .sendToAllAround(
            new PacketFXZap(trajectory.source, end, c.getRGB(), this.getPackage().getPower() * 0.66F),
            new TargetPoint(
               this.getPackage().world.field_73011_w.getDimension(),
               trajectory.source.field_72450_a,
               trajectory.source.field_72448_b,
               trajectory.source.field_72449_c,
               64.0
            )
         );
      return true;
   }
}

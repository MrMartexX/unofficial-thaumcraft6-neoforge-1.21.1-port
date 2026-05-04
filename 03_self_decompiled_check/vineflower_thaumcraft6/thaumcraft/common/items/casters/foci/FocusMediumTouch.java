package thaumcraft.common.items.casters.foci;

import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusEffect;
import thaumcraft.common.lib.utils.EntityUtils;

public class FocusMediumTouch extends FocusMedium {
   @Override
   public String getResearch() {
      return "BASEAUROMANCY";
   }

   @Override
   public String getKey() {
      return "thaumcraft.TOUCH";
   }

   @Override
   public int getComplexity() {
      return 2;
   }

   @Override
   public FocusNode.EnumSupplyType[] willSupply() {
      return new FocusNode.EnumSupplyType[]{FocusNode.EnumSupplyType.TRAJECTORY, FocusNode.EnumSupplyType.TARGET};
   }

   @Override
   public Aspect getAspect() {
      return Aspect.AVERSION;
   }

   @Override
   public Trajectory[] supplyTrajectories() {
      if (this.getParent() == null) {
         return new Trajectory[0];
      }

      ArrayList<Trajectory> trajectories = new ArrayList<>();
      double range = this instanceof FocusMediumBolt ? 16.0 : RayTracer.getBlockReachDistance((EntityPlayer)this.getPackage().getCaster());

      for (Trajectory sT : this.getParent().supplyTrajectories()) {
         Vec3d end = sT.direction.func_72432_b();
         RayTraceResult ray = EntityUtils.getPointedEntityRay(this.getPackage().world, this.getPackage().getCaster(), sT.source, end, 0.25, range, 0.25F, false);
         if (ray == null) {
            Vec3d var10 = end.func_186678_a(range);
            end = var10.func_178787_e(sT.source);
            ray = this.getPackage().world.func_72933_a(sT.source, end);
            if (ray != null) {
               end = ray.field_72307_f;
            }
         } else if (ray.field_72308_g != null) {
            end = end.func_186678_a(sT.source.func_72438_d(ray.field_72308_g.func_174791_d()));
            end = end.func_178787_e(sT.source);
         }

         trajectories.add(new Trajectory(end, sT.direction.func_72432_b()));
      }

      return trajectories.toArray(new Trajectory[0]);
   }

   @Override
   public RayTraceResult[] supplyTargets() {
      if (this.getParent() != null && this.getPackage().getCaster() instanceof EntityPlayer) {
         ArrayList<RayTraceResult> targets = new ArrayList<>();
         double range = this instanceof FocusMediumBolt ? 16.0 : RayTracer.getBlockReachDistance((EntityPlayer)this.getPackage().getCaster());

         for (Trajectory sT : this.getParent().supplyTrajectories()) {
            Vec3d end = sT.direction.func_72432_b();
            RayTraceResult ray = EntityUtils.getPointedEntityRay(
               this.getPackage().world, this.getPackage().getCaster(), sT.source, end, 0.25, range, 0.25F, false
            );
            if (ray == null) {
               end = end.func_186678_a(range);
               end = end.func_178787_e(sT.source);
               ray = this.getPackage().world.func_72933_a(sT.source, end);
            }

            if (ray != null) {
               targets.add(ray);
            }
         }

         return targets.toArray(new RayTraceResult[0]);
      } else {
         return new RayTraceResult[0];
      }
   }

   @Override
   public boolean execute(Trajectory trajectory) {
      FocusEffect[] fe = this.getPackage().getFocusEffects();
      if (fe != null && fe.length > 0) {
         String[] effects = new String[fe.length];

         for (int a = 0; a < fe.length; a++) {
            effects[a] = fe[a].getKey();
         }

         PacketHandler.INSTANCE
            .sendToAllAround(
               new PacketFXFocusEffect(
                  (float)trajectory.source.field_72450_a,
                  (float)trajectory.source.field_72448_b,
                  (float)trajectory.source.field_72449_c,
                  (float)trajectory.direction.field_72450_a / 2.0F,
                  (float)trajectory.direction.field_72448_b / 2.0F,
                  (float)trajectory.direction.field_72449_c / 2.0F,
                  effects
               ),
               new TargetPoint(
                  this.getPackage().world.field_73011_w.getDimension(),
                  (float)trajectory.source.field_72450_a,
                  (float)trajectory.source.field_72448_b,
                  (float)trajectory.source.field_72449_c,
                  64.0
               )
            );
      }

      return true;
   }
}

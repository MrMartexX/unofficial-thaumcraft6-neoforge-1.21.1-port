package thaumcraft.api.casters;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import thaumcraft.api.aspects.Aspect;

public class FocusMediumRoot extends FocusMedium {
   Trajectory[] trajectories = null;
   RayTraceResult[] targets = null;

   public FocusMediumRoot() {
   }

   public FocusMediumRoot(Trajectory[] trajectories, RayTraceResult[] targets) {
      this.trajectories = trajectories;
      this.targets = targets;
   }

   @Override
   public String getResearch() {
      return "BASEAUROMANCY";
   }

   @Override
   public String getKey() {
      return "ROOT";
   }

   @Override
   public int getComplexity() {
      return 0;
   }

   @Override
   public FocusNode.EnumSupplyType[] willSupply() {
      return new FocusNode.EnumSupplyType[]{FocusNode.EnumSupplyType.TARGET, FocusNode.EnumSupplyType.TRAJECTORY};
   }

   @Override
   public RayTraceResult[] supplyTargets() {
      return this.targets;
   }

   @Override
   public Trajectory[] supplyTrajectories() {
      return this.trajectories;
   }

   public void setupFromCaster(EntityLivingBase caster) {
      this.trajectories = new Trajectory[]{new Trajectory(this.generateSourceVector(caster), caster.func_70040_Z())};
      this.targets = new RayTraceResult[]{new RayTraceResult(caster)};
   }

   public void setupFromCasterToTarget(EntityLivingBase caster, Entity target, double offset) {
      Vec3d sv = this.generateSourceVector(caster);
      double d0 = target.field_70165_t - sv.field_72450_a;
      double d1 = target.func_174813_aQ().field_72338_b + target.field_70131_O / 2.0F - sv.field_72448_b;
      double d2 = target.field_70161_v - sv.field_72449_c;
      Vec3d lv = new Vec3d(d0, d1 + offset, d2);
      this.trajectories = new Trajectory[]{new Trajectory(sv, lv.func_72432_b())};
      this.targets = new RayTraceResult[]{new RayTraceResult(caster)};
   }

   public void setupFromCasterToTargetLoc(EntityLivingBase caster, double x, double y, double z) {
      Vec3d sv = this.generateSourceVector(caster);
      double d0 = x - sv.field_72450_a;
      double d1 = y - sv.field_72448_b;
      double d2 = z - sv.field_72449_c;
      Vec3d lv = new Vec3d(d0, d1, d2);
      this.trajectories = new Trajectory[]{new Trajectory(sv, lv.func_72432_b())};
      this.targets = new RayTraceResult[]{new RayTraceResult(caster)};
   }

   private Vec3d generateSourceVector(EntityLivingBase e) {
      Vec3d v = e.func_174791_d();
      return v.func_72441_c(0.0, e.func_70047_e() - 0.1F, 0.0);
   }

   @Override
   public Aspect getAspect() {
      return null;
   }
}

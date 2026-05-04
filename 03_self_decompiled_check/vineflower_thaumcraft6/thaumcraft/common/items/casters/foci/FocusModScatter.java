package thaumcraft.common.items.casters.foci;

import java.util.ArrayList;
import net.minecraft.util.math.Vec3d;
import thaumcraft.api.casters.FocusMod;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;

public class FocusModScatter extends FocusMod {
   @Override
   public String getResearch() {
      return "FOCUSSCATTER";
   }

   @Override
   public String getKey() {
      return "thaumcraft.SCATTER";
   }

   @Override
   public int getComplexity() {
      return (int)Math.max(2.0F, 2.0F * (this.getSettingValue("forks") - this.getSettingValue("cone") / 45.0F));
   }

   @Override
   public NodeSetting[] createSettings() {
      int[] angles = new int[]{10, 30, 60, 90, 180, 270, 360};
      String[] anglesDesc = new String[]{"10", "30", "60", "90", "180", "270", "360"};
      return new NodeSetting[]{
         new NodeSetting("forks", "focus.scatter.forks", new NodeSetting.NodeSettingIntRange(2, 10)),
         new NodeSetting("cone", "focus.scatter.cone", new NodeSetting.NodeSettingIntList(angles, anglesDesc))
      };
   }

   @Override
   public FocusNode.EnumSupplyType[] mustBeSupplied() {
      return new FocusNode.EnumSupplyType[]{FocusNode.EnumSupplyType.TRAJECTORY};
   }

   @Override
   public FocusNode.EnumSupplyType[] willSupply() {
      return new FocusNode.EnumSupplyType[]{FocusNode.EnumSupplyType.TRAJECTORY};
   }

   @Override
   public Trajectory[] supplyTrajectories() {
      if (this.getParent() == null) {
         return new Trajectory[0];
      }

      ArrayList<Trajectory> tT = new ArrayList<>();
      int forks = this.getSettingValue("forks");
      int angle = this.getSettingValue("cone");
      if (this.getParent().supplyTrajectories() != null) {
         for (Trajectory sT : this.getParent().supplyTrajectories()) {
            for (int a = 0; a < forks; a++) {
               Vec3d sV = sT.source;
               Vec3d dV = sT.direction;
               dV = dV.func_72432_b();
               dV = dV.func_72441_c(
                  this.getPackage().world.field_73012_v.nextGaussian() * 0.0075F * angle,
                  this.getPackage().world.field_73012_v.nextGaussian() * 0.0075F * angle,
                  this.getPackage().world.field_73012_v.nextGaussian() * 0.0075F * angle
               );
               tT.add(new Trajectory(sV, dV.func_72432_b()));
            }
         }
      }

      return tT.toArray(new Trajectory[0]);
   }

   @Override
   public float getPowerMultiplier() {
      return 1.0F / (this.getSettingValue("forks") / 2.0F);
   }

   @Override
   public boolean execute() {
      return true;
   }

   @Override
   public boolean isExclusive() {
      return true;
   }
}

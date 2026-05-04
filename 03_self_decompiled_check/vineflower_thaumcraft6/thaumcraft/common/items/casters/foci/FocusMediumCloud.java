package thaumcraft.common.items.casters.foci;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.entities.projectile.EntityFocusCloud;

public class FocusMediumCloud extends FocusMedium {
   @Override
   public String getResearch() {
      return "FOCUSCLOUD";
   }

   @Override
   public String getKey() {
      return "thaumcraft.CLOUD";
   }

   @Override
   public Aspect getAspect() {
      return Aspect.ALCHEMY;
   }

   @Override
   public int getComplexity() {
      return 4 + this.getSettingValue("radius") * 2 + this.getSettingValue("duration") / 5;
   }

   @Override
   public FocusNode.EnumSupplyType[] willSupply() {
      return new FocusNode.EnumSupplyType[]{FocusNode.EnumSupplyType.TARGET};
   }

   @Override
   public boolean execute(Trajectory trajectory) {
      EntityFocusCloud cloud = new EntityFocusCloud(this.getRemainingPackage(), trajectory, this.getSettingValue("radius"), this.getSettingValue("duration"));
      return this.getPackage().getCaster().field_70170_p.func_72838_d(cloud);
   }

   @Override
   public boolean hasIntermediary() {
      return true;
   }

   @Override
   public NodeSetting[] createSettings() {
      return new NodeSetting[]{
         new NodeSetting("radius", "focus.common.radius", new NodeSetting.NodeSettingIntRange(1, 3)),
         new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntRange(5, 30))
      };
   }

   @Override
   public float getPowerMultiplier() {
      return 0.5F;
   }
}

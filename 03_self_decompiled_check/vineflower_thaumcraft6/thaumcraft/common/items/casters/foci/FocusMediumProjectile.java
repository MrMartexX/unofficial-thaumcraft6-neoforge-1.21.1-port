package thaumcraft.common.items.casters.foci;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.entities.projectile.EntityFocusProjectile;

public class FocusMediumProjectile extends FocusMedium {
   @Override
   public String getResearch() {
      return "FOCUSPROJECTILE@2";
   }

   @Override
   public String getKey() {
      return "thaumcraft.PROJECTILE";
   }

   @Override
   public int getComplexity() {
      int c = 4 + (this.getSettingValue("speed") - 1) / 2;
      switch (this.getSettingValue("option")) {
         case 1:
            c += 3;
            break;
         case 2:
         case 3:
            c += 5;
      }

      return c;
   }

   @Override
   public FocusNode.EnumSupplyType[] willSupply() {
      return new FocusNode.EnumSupplyType[]{FocusNode.EnumSupplyType.TARGET, FocusNode.EnumSupplyType.TRAJECTORY};
   }

   @Override
   public boolean execute(Trajectory trajectory) {
      float speed = this.getSettingValue("speed") / 3.0F;
      FocusPackage p = this.getRemainingPackage();
      if (p.getCaster() != null) {
         EntityFocusProjectile projectile = new EntityFocusProjectile(p, speed, trajectory, this.getSettingValue("option"));
         return this.getPackage().getCaster().field_70170_p.func_72838_d(projectile);
      } else {
         return false;
      }
   }

   @Override
   public boolean hasIntermediary() {
      return true;
   }

   @Override
   public NodeSetting[] createSettings() {
      int[] option = new int[]{0, 1, 2, 3};
      String[] optionDesc = new String[]{
         "focus.common.none", "focus.projectile.bouncy", "focus.projectile.seeking.hostile", "focus.projectile.seeking.friendly"
      };
      return new NodeSetting[]{
         new NodeSetting("option", "focus.common.options", new NodeSetting.NodeSettingIntList(option, optionDesc), "FOCUSPROJECTILE"),
         new NodeSetting("speed", "focus.projectile.speed", new NodeSetting.NodeSettingIntRange(1, 5))
      };
   }

   @Override
   public Aspect getAspect() {
      return Aspect.MOTION;
   }
}

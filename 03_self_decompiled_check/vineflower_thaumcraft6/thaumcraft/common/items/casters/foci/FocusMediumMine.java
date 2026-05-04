package thaumcraft.common.items.casters.foci;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.entities.projectile.EntityFocusMine;

public class FocusMediumMine extends FocusMedium {
   @Override
   public String getResearch() {
      return "FOCUSMINE";
   }

   @Override
   public String getKey() {
      return "thaumcraft.MINE";
   }

   @Override
   public int getComplexity() {
      return 4;
   }

   @Override
   public Aspect getAspect() {
      return Aspect.TRAP;
   }

   @Override
   public FocusNode.EnumSupplyType[] willSupply() {
      return new FocusNode.EnumSupplyType[]{FocusNode.EnumSupplyType.TARGET, FocusNode.EnumSupplyType.TRAJECTORY};
   }

   @Override
   public boolean execute(Trajectory trajectory) {
      EntityFocusMine projectile = new EntityFocusMine(this.getRemainingPackage(), trajectory, this.getSettingValue("target") == 1);
      return this.getPackage().getCaster().field_70170_p.func_72838_d(projectile);
   }

   @Override
   public boolean hasIntermediary() {
      return true;
   }

   @Override
   public NodeSetting[] createSettings() {
      int[] friend = new int[]{0, 1};
      String[] friendDesc = new String[]{"focus.common.enemy", "focus.common.friend"};
      return new NodeSetting[]{new NodeSetting("target", "focus.common.target", new NodeSetting.NodeSettingIntList(friend, friendDesc))};
   }
}

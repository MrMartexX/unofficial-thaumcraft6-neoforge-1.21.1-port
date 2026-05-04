package thaumcraft.common.items.casters.foci;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.entities.monster.EntitySpellBat;

public class FocusMediumSpellBat extends FocusMedium {
   @Override
   public String getResearch() {
      return "FOCUSSPELLBAT";
   }

   @Override
   public String getKey() {
      return "thaumcraft.SPELLBAT";
   }

   @Override
   public Aspect getAspect() {
      return Aspect.BEAST;
   }

   @Override
   public int getComplexity() {
      return 8;
   }

   @Override
   public FocusNode.EnumSupplyType[] willSupply() {
      return new FocusNode.EnumSupplyType[]{FocusNode.EnumSupplyType.TARGET};
   }

   @Override
   public boolean execute(Trajectory trajectory) {
      EntitySpellBat bat = new EntitySpellBat(this.getRemainingPackage(), this.getSettingValue("target") == 1);
      bat.func_70107_b(trajectory.source.field_72450_a, trajectory.source.field_72448_b, trajectory.source.field_72449_c);
      return this.getPackage().getCaster().field_70170_p.func_72838_d(bat);
   }

   @Override
   public boolean hasIntermediary() {
      return true;
   }

   @Override
   public float getPowerMultiplier() {
      return 0.33F;
   }

   @Override
   public NodeSetting[] createSettings() {
      int[] friend = new int[]{0, 1};
      String[] friendDesc = new String[]{"focus.common.enemy", "focus.common.friend"};
      return new NodeSetting[]{new NodeSetting("target", "focus.common.target", new NodeSetting.NodeSettingIntList(friend, friendDesc))};
   }
}

package thaumcraft.common.items.casters.foci;

import net.minecraft.util.math.RayTraceResult;
import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.api.casters.FocusNode;

public class FocusModSplitTarget extends FocusModSplit {
   @Override
   public String getResearch() {
      return "FOCUSSPLIT";
   }

   @Override
   public String getKey() {
      return "thaumcraft.SPLITTARGET";
   }

   @Override
   public int getComplexity() {
      return 4;
   }

   @Override
   public FocusNode.EnumSupplyType[] mustBeSupplied() {
      return new FocusNode.EnumSupplyType[]{FocusNode.EnumSupplyType.TARGET};
   }

   @Override
   public FocusNode.EnumSupplyType[] willSupply() {
      return new FocusNode.EnumSupplyType[]{FocusNode.EnumSupplyType.TARGET};
   }

   @Override
   public RayTraceResult[] supplyTargets() {
      return this.getParent().supplyTargets();
   }

   @Override
   public boolean execute() {
      return true;
   }
}

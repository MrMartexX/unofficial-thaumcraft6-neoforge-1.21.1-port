package thaumcraft.api.casters;

public abstract class FocusMedium extends FocusNode {
   @Override
   public IFocusElement.EnumUnitType getType() {
      return IFocusElement.EnumUnitType.MEDIUM;
   }

   @Override
   public final FocusNode.EnumSupplyType[] mustBeSupplied() {
      return this instanceof FocusMediumRoot ? null : new FocusNode.EnumSupplyType[]{FocusNode.EnumSupplyType.TRAJECTORY};
   }

   @Override
   public FocusNode.EnumSupplyType[] willSupply() {
      return new FocusNode.EnumSupplyType[]{FocusNode.EnumSupplyType.TARGET};
   }

   public boolean hasIntermediary() {
      return false;
   }

   public boolean execute(Trajectory trajectory) {
      return true;
   }
}

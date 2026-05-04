package thaumcraft.api.casters;

public interface IFocusElement {
   String getKey();

   String getResearch();

   IFocusElement.EnumUnitType getType();

   enum EnumUnitType {
      EFFECT,
      MEDIUM,
      MOD,
      PACKAGE;
   }
}

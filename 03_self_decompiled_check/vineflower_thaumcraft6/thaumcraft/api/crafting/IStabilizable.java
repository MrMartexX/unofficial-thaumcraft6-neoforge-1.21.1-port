package thaumcraft.api.crafting;

@Deprecated
public interface IStabilizable {
   @Deprecated
   void addStability();

   @Deprecated
   IStabilizable.EnumStability getStability();

   @Deprecated
   enum EnumStability {
      VERY_STABLE,
      STABLE,
      UNSTABLE,
      VERY_UNSTABLE;
   }
}

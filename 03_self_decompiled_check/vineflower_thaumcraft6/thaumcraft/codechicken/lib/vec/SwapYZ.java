package thaumcraft.codechicken.lib.vec;

public class SwapYZ extends VariableTransformation {
   public SwapYZ() {
      super(new Matrix4(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0));
   }

   public void apply(Vector3 vec) {
      double vz = vec.z;
      vec.z = vec.y;
      vec.y = vz;
   }

   public Transformation inverse() {
      return this;
   }
}

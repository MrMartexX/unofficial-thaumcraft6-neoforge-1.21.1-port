package thaumcraft.codechicken.lib.vec;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RedundantTransformation extends Transformation {
   public void apply(Vector3 vec) {
   }

   @Override
   public void apply(Matrix4 mat) {
   }

   @Override
   public void applyN(Vector3 normal) {
   }

   @Override
   public Transformation at(Vector3 point) {
      return this;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void glApply() {
   }

   public Transformation inverse() {
      return this;
   }

   public Transformation merge(Transformation next) {
      return next;
   }

   @Override
   public boolean isRedundant() {
      return true;
   }

   @Override
   public String toString() {
      return "Nothing()";
   }
}

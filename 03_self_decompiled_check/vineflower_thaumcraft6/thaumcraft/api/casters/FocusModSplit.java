package thaumcraft.api.casters;

import java.util.ArrayList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class FocusModSplit extends FocusMod {
   private ArrayList<FocusPackage> packages = new ArrayList<>();

   public final ArrayList<FocusPackage> getSplitPackages() {
      return this.packages;
   }

   public void deserialize(NBTTagCompound nbt) {
      NBTTagList nodelist = nbt.func_150295_c("packages", 10);
      this.packages.clear();

      for (int x = 0; x < nodelist.func_74745_c(); x++) {
         NBTTagCompound nodenbt = nodelist.func_150305_b(x);
         FocusPackage fp = new FocusPackage();
         fp.deserialize(nodenbt);
         this.packages.add(fp);
      }
   }

   public NBTTagCompound serialize() {
      NBTTagCompound nbt = new NBTTagCompound();
      NBTTagList nodelist = new NBTTagList();

      for (FocusPackage node : this.packages) {
         nodelist.func_74742_a(node.serialize());
      }

      nbt.func_74782_a("packages", nodelist);
      return nbt;
   }

   @Override
   public float getPowerMultiplier() {
      return 0.75F;
   }
}

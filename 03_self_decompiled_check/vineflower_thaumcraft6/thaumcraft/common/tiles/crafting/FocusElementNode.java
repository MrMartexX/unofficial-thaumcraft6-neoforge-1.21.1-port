package thaumcraft.common.tiles.crafting;

import java.util.HashMap;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.IFocusElement;

public class FocusElementNode {
   public int x;
   public int y;
   public int id;
   public boolean target = false;
   public boolean trajectory = false;
   public int parent = -1;
   public int[] children = new int[0];
   public float complexityMultiplier = 1.0F;
   public FocusNode node = null;

   public float getPower(HashMap<Integer, FocusElementNode> data) {
      if (this.node == null) {
         return 1.0F;
      }

      float pow = this.node.getPowerMultiplier();
      FocusElementNode p = data.get(this.parent);
      if (p != null && p.node != null) {
         pow *= p.getPower(data);
      }

      return pow;
   }

   public void deserialize(NBTTagCompound nbt) {
      this.x = nbt.func_74762_e("x");
      this.y = nbt.func_74762_e("y");
      this.id = nbt.func_74762_e("id");
      this.target = nbt.func_74767_n("target");
      this.trajectory = nbt.func_74767_n("trajectory");
      this.parent = nbt.func_74762_e("parent");
      this.children = nbt.func_74759_k("children");
      this.complexityMultiplier = nbt.func_74760_g("complexity");
      IFocusElement fe = FocusEngine.getElement(nbt.func_74779_i("key"));
      if (fe != null) {
         this.node = (FocusNode)fe;
         ((FocusNode)fe).initialize();
         if (((FocusNode)fe).getSettingList() != null) {
            for (String ns : ((FocusNode)fe).getSettingList()) {
               ((FocusNode)fe).getSetting(ns).setValue(nbt.func_74762_e("setting." + ns));
            }
         }
      }
   }

   public NBTTagCompound serialize() {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.func_74768_a("x", this.x);
      nbt.func_74768_a("y", this.y);
      nbt.func_74768_a("id", this.id);
      nbt.func_74757_a("target", this.target);
      nbt.func_74757_a("trajectory", this.trajectory);
      nbt.func_74768_a("parent", this.parent);
      nbt.func_74783_a("children", this.children);
      nbt.func_74776_a("complexity", this.complexityMultiplier);
      if (this.node != null) {
         nbt.func_74778_a("key", this.node.getKey());
         if (this.node.getSettingList() != null) {
            for (String ns : this.node.getSettingList()) {
               nbt.func_74768_a("setting." + ns, this.node.getSettingValue(ns));
            }
         }
      }

      return nbt;
   }
}

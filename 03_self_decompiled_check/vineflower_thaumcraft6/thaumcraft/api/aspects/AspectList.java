package thaumcraft.api.aspects;

import java.io.Serializable;
import java.util.LinkedHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class AspectList implements Serializable {
   public LinkedHashMap<Aspect, Integer> aspects = new LinkedHashMap<>();

   public AspectList(ItemStack stack) {
      try {
         AspectList temp = AspectHelper.getObjectAspects(stack);
         if (temp != null) {
            for (Aspect tag : temp.getAspects()) {
               this.add(tag, temp.getAmount(tag));
            }
         }
      } catch (Exception var7) {
      }
   }

   public AspectList() {
   }

   public AspectList copy() {
      AspectList out = new AspectList();

      for (Aspect a : this.getAspects()) {
         out.add(a, this.getAmount(a));
      }

      return out;
   }

   public int size() {
      return this.aspects.size();
   }

   public int visSize() {
      int q = 0;

      for (Aspect as : this.aspects.keySet()) {
         q += this.getAmount(as);
      }

      return q;
   }

   public Aspect[] getAspects() {
      return this.aspects.keySet().toArray(new Aspect[0]);
   }

   public Aspect[] getAspectsSortedByName() {
      try {
         Aspect[] out = this.aspects.keySet().toArray(new Aspect[0]);
         boolean change = false;

         do {
            change = false;

            for (int a = 0; a < out.length - 1; a++) {
               Aspect e1 = out[a];
               Aspect e2 = out[a + 1];
               if (e1 != null && e2 != null && e1.getTag().compareTo(e2.getTag()) > 0) {
                  out[a] = e2;
                  out[a + 1] = e1;
                  change = true;
                  break;
               }
            }
         } while (change);

         return out;
      } catch (Exception e) {
         return this.getAspects();
      }
   }

   public Aspect[] getAspectsSortedByAmount() {
      try {
         Aspect[] out = this.aspects.keySet().toArray(new Aspect[0]);
         boolean change = false;

         do {
            change = false;

            for (int a = 0; a < out.length - 1; a++) {
               int e1 = this.getAmount(out[a]);
               int e2 = this.getAmount(out[a + 1]);
               if (e1 > 0 && e2 > 0 && e2 > e1) {
                  Aspect ea = out[a];
                  Aspect eb = out[a + 1];
                  out[a] = eb;
                  out[a + 1] = ea;
                  change = true;
                  break;
               }
            }
         } while (change);

         return out;
      } catch (Exception e) {
         return this.getAspects();
      }
   }

   public int getAmount(Aspect key) {
      return this.aspects.get(key) == null ? 0 : this.aspects.get(key);
   }

   public boolean reduce(Aspect key, int amount) {
      if (this.getAmount(key) >= amount) {
         int am = this.getAmount(key) - amount;
         this.aspects.put(key, am);
         return true;
      } else {
         return false;
      }
   }

   public AspectList remove(Aspect key, int amount) {
      int am = this.getAmount(key) - amount;
      if (am <= 0) {
         this.aspects.remove(key);
      } else {
         this.aspects.put(key, am);
      }

      return this;
   }

   public AspectList remove(Aspect key) {
      this.aspects.remove(key);
      return this;
   }

   public AspectList add(Aspect aspect, int amount) {
      if (this.aspects.containsKey(aspect)) {
         int oldamount = this.aspects.get(aspect);
         amount += oldamount;
      }

      this.aspects.put(aspect, amount);
      return this;
   }

   public AspectList merge(Aspect aspect, int amount) {
      if (this.aspects.containsKey(aspect)) {
         int oldamount = this.aspects.get(aspect);
         if (amount < oldamount) {
            amount = oldamount;
         }
      }

      this.aspects.put(aspect, amount);
      return this;
   }

   public AspectList add(AspectList in) {
      for (Aspect a : in.getAspects()) {
         this.add(a, in.getAmount(a));
      }

      return this;
   }

   public AspectList remove(AspectList in) {
      for (Aspect a : in.getAspects()) {
         this.remove(a, in.getAmount(a));
      }

      return this;
   }

   public AspectList merge(AspectList in) {
      for (Aspect a : in.getAspects()) {
         this.merge(a, in.getAmount(a));
      }

      return this;
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      this.aspects.clear();
      NBTTagList tlist = nbttagcompound.func_150295_c("Aspects", 10);

      for (int j = 0; j < tlist.func_74745_c(); j++) {
         NBTTagCompound rs = tlist.func_150305_b(j);
         if (rs.func_74764_b("key")) {
            this.add(Aspect.getAspect(rs.func_74779_i("key")), rs.func_74762_e("amount"));
         }
      }
   }

   public void readFromNBT(NBTTagCompound nbttagcompound, String label) {
      this.aspects.clear();
      NBTTagList tlist = nbttagcompound.func_150295_c(label, 10);

      for (int j = 0; j < tlist.func_74745_c(); j++) {
         NBTTagCompound rs = tlist.func_150305_b(j);
         if (rs.func_74764_b("key")) {
            this.add(Aspect.getAspect(rs.func_74779_i("key")), rs.func_74762_e("amount"));
         }
      }
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      NBTTagList tlist = new NBTTagList();
      nbttagcompound.func_74782_a("Aspects", tlist);

      for (Aspect aspect : this.getAspects()) {
         if (aspect != null) {
            NBTTagCompound f = new NBTTagCompound();
            f.func_74778_a("key", aspect.getTag());
            f.func_74768_a("amount", this.getAmount(aspect));
            tlist.func_74742_a(f);
         }
      }
   }

   public void writeToNBT(NBTTagCompound nbttagcompound, String label) {
      NBTTagList tlist = new NBTTagList();
      nbttagcompound.func_74782_a(label, tlist);

      for (Aspect aspect : this.getAspects()) {
         if (aspect != null) {
            NBTTagCompound f = new NBTTagCompound();
            f.func_74778_a("key", aspect.getTag());
            f.func_74768_a("amount", this.getAmount(aspect));
            tlist.func_74742_a(f);
         }
      }
   }
}

package thaumcraft.api.aspects;

import java.util.List;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.internal.CommonInternals;

public class AspectEventProxy {
   public void registerObjectTag(ItemStack item, AspectList aspects) {
      if (aspects == null) {
         aspects = new AspectList();
      }

      try {
         CommonInternals.objectTags.put(CommonInternals.generateUniqueItemstackId(item), aspects);
      } catch (Exception var4) {
      }
   }

   public void registerObjectTag(String oreDict, AspectList aspects) {
      if (aspects == null) {
         aspects = new AspectList();
      }

      List<ItemStack> ores = ThaumcraftApiHelper.getOresWithWildCards(oreDict);
      if (ores != null && ores.size() > 0) {
         for (ItemStack ore : ores) {
            try {
               ItemStack oc = ore.func_77946_l();
               oc.func_190920_e(1);
               this.registerObjectTag(oc, aspects.copy());
            } catch (Exception var7) {
            }
         }
      }
   }

   public void registerComplexObjectTag(ItemStack item, AspectList aspects) {
      if (!ThaumcraftApi.exists(item)) {
         AspectList tmp = AspectHelper.generateTags(item);
         if (tmp != null && tmp.size() > 0) {
            for (Aspect tag : tmp.getAspects()) {
               aspects.add(tag, tmp.getAmount(tag));
            }
         }

         this.registerObjectTag(item, aspects);
      } else {
         AspectList tmp = AspectHelper.getObjectAspects(item);

         for (Aspect tag : aspects.getAspects()) {
            tmp.merge(tag, tmp.getAmount(tag));
         }

         this.registerObjectTag(item, tmp);
      }
   }

   public void registerComplexObjectTag(String oreDict, AspectList aspects) {
      if (aspects == null) {
         aspects = new AspectList();
      }

      List<ItemStack> ores = ThaumcraftApiHelper.getOresWithWildCards(oreDict);
      if (ores != null && ores.size() > 0) {
         for (ItemStack ore : ores) {
            try {
               ItemStack oc = ore.func_77946_l();
               oc.func_190920_e(1);
               this.registerComplexObjectTag(oc, aspects.copy());
            } catch (Exception var7) {
            }
         }
      }
   }
}

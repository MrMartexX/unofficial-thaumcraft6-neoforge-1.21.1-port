package thaumcraft.api.research;

import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.internal.CommonInternals;

public class ScanOreDictionary implements IScanThing {
   String research;
   String[] entries;
   public ConcurrentHashMap<Integer, Boolean> cache = new ConcurrentHashMap<>();

   public ScanOreDictionary(String research, String... entries) {
      this.research = research;
      this.entries = entries;
   }

   @Override
   public boolean checkThing(EntityPlayer player, Object obj) {
      ItemStack stack = null;
      if (obj != null) {
         if (obj instanceof BlockPos) {
            IBlockState state = player.field_70170_p.func_180495_p((BlockPos)obj);
            stack = state.func_177230_c().func_185473_a(player.field_70170_p, (BlockPos)obj, state);
         } else if (obj instanceof ItemStack) {
            stack = (ItemStack)obj;
         } else if (obj instanceof EntityItem && ((EntityItem)obj).func_92059_d() != null) {
            stack = ((EntityItem)obj).func_92059_d();
         }
      }

      if (stack != null && !stack.func_190926_b()) {
         int hid = CommonInternals.generateUniqueItemstackId(stack);
         if (this.cache.containsKey(hid)) {
            return this.cache.get(hid);
         }

         int[] ids = OreDictionary.getOreIDs(stack);

         for (String entry : this.entries) {
            for (int id : ids) {
               if (OreDictionary.getOreName(id).equals(entry)) {
                  synchronized (this.cache) {
                     this.cache.put(hid, true);
                     return true;
                  }
               }
            }
         }

         synchronized (this.cache) {
            this.cache.put(hid, false);
         }
      }

      return false;
   }

   @Override
   public String getResearchKey(EntityPlayer player, Object object) {
      return this.research;
   }
}

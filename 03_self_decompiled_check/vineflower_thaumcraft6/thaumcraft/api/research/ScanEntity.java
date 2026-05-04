package thaumcraft.api.research;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;

public class ScanEntity implements IScanThing {
   String research;
   Class entityClass;
   ThaumcraftApi.EntityTagsNBT[] NBTData;
   boolean inheritedClasses = false;

   public ScanEntity(String research, Class entityClass, boolean inheritedClasses) {
      this.research = research;
      this.entityClass = entityClass;
      this.inheritedClasses = inheritedClasses;
   }

   public ScanEntity(String research, Class entityClass, boolean inheritedClasses, ThaumcraftApi.EntityTagsNBT... nbt) {
      this.research = research;
      this.entityClass = entityClass;
      this.inheritedClasses = inheritedClasses;
      this.NBTData = nbt;
   }

   @Override
   public boolean checkThing(EntityPlayer player, Object obj) {
      if (obj != null && (!this.inheritedClasses && this.entityClass == obj.getClass() || this.inheritedClasses && this.entityClass.isInstance(obj))) {
         if (this.NBTData != null && this.NBTData.length > 0) {
            boolean b = true;
            NBTTagCompound tc = new NBTTagCompound();
            ((Entity)obj).func_189511_e(tc);

            for (ThaumcraftApi.EntityTagsNBT nbt : this.NBTData) {
               if (!tc.func_74764_b(nbt.name) || !ThaumcraftApiHelper.getNBTDataFromId(tc, tc.func_150299_b(nbt.name), nbt.name).equals(nbt.value)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public String getResearchKey(EntityPlayer player, Object object) {
      return this.research;
   }
}

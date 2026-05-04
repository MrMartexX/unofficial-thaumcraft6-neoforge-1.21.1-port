package thaumcraft.common.tiles.essentia;

import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

public class TileTubeFilter extends TileTube implements IAspectContainer {
   public Aspect aspectFilter = null;

   @Override
   public void readSyncNBT(NBTTagCompound nbttagcompound) {
      super.readSyncNBT(nbttagcompound);
      this.aspectFilter = Aspect.getAspect(nbttagcompound.func_74779_i("AspectFilter"));
   }

   @Override
   public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
      nbttagcompound = super.writeSyncNBT(nbttagcompound);
      if (this.aspectFilter != null) {
         nbttagcompound.func_74778_a("AspectFilter", this.aspectFilter.getTag());
      }

      return nbttagcompound;
   }

   @Override
   void calculateSuction(Aspect filter, boolean restrict, boolean dir) {
      super.calculateSuction(this.aspectFilter, restrict, dir);
   }

   @Override
   public AspectList getAspects() {
      return this.aspectFilter != null ? new AspectList().add(this.aspectFilter, -1) : null;
   }

   @Override
   public void setAspects(AspectList aspects) {
   }

   @Override
   public boolean doesContainerAccept(Aspect tag) {
      return false;
   }

   @Override
   public int addToContainer(Aspect tag, int amount) {
      return 0;
   }

   @Override
   public boolean takeFromContainer(Aspect tag, int amount) {
      return false;
   }

   @Override
   public boolean takeFromContainer(AspectList ot) {
      return false;
   }

   @Override
   public boolean doesContainerContainAmount(Aspect tag, int amount) {
      return false;
   }

   @Override
   public boolean doesContainerContain(AspectList ot) {
      return false;
   }

   @Override
   public int containerContains(Aspect tag) {
      return 0;
   }
}

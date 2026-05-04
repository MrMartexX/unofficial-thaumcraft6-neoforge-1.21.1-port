package thaumcraft.common.tiles.essentia;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileJarFillable extends TileJar implements IAspectSource, IEssentiaTransport {
   public static final int CAPACITY = 250;
   public Aspect aspect = null;
   public Aspect aspectFilter = null;
   public int amount = 0;
   public int facing = 2;
   public boolean blocked = false;
   int count = 0;

   @Override
   public void readSyncNBT(NBTTagCompound nbttagcompound) {
      this.aspect = Aspect.getAspect(nbttagcompound.func_74779_i("Aspect"));
      this.aspectFilter = Aspect.getAspect(nbttagcompound.func_74779_i("AspectFilter"));
      this.amount = nbttagcompound.func_74765_d("Amount");
      this.facing = nbttagcompound.func_74771_c("facing");
      this.blocked = nbttagcompound.func_74767_n("blocked");
   }

   @Override
   public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
      if (this.aspect != null) {
         nbttagcompound.func_74778_a("Aspect", this.aspect.getTag());
      }

      if (this.aspectFilter != null) {
         nbttagcompound.func_74778_a("AspectFilter", this.aspectFilter.getTag());
      }

      nbttagcompound.func_74777_a("Amount", (short)this.amount);
      nbttagcompound.func_74774_a("facing", (byte)this.facing);
      nbttagcompound.func_74757_a("blocked", this.blocked);
      return nbttagcompound;
   }

   @Override
   public AspectList getAspects() {
      AspectList al = new AspectList();
      if (this.aspect != null && this.amount > 0) {
         al.add(this.aspect, this.amount);
      }

      return al;
   }

   @Override
   public void setAspects(AspectList aspects) {
      if (aspects != null && aspects.size() > 0) {
         this.aspect = aspects.getAspectsSortedByAmount()[0];
         this.amount = aspects.getAmount(aspects.getAspectsSortedByAmount()[0]);
      }
   }

   @Override
   public int addToContainer(Aspect tt, int am) {
      if (am == 0) {
         return am;
      }

      if (this.amount < 250 && tt == this.aspect || this.amount == 0) {
         this.aspect = tt;
         int added = Math.min(am, 250 - this.amount);
         this.amount += added;
         am -= added;
      }

      this.syncTile(false);
      this.func_70296_d();
      return am;
   }

   @Override
   public boolean takeFromContainer(Aspect tt, int am) {
      if (this.amount >= am && tt == this.aspect) {
         this.amount -= am;
         if (this.amount <= 0) {
            this.aspect = null;
            this.amount = 0;
         }

         this.syncTile(false);
         this.func_70296_d();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean takeFromContainer(AspectList ot) {
      return false;
   }

   @Override
   public boolean doesContainerContainAmount(Aspect tag, int amt) {
      return this.amount >= amt && tag == this.aspect;
   }

   @Override
   public boolean doesContainerContain(AspectList ot) {
      for (Aspect tt : ot.getAspects()) {
         if (this.amount > 0 && tt == this.aspect) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int containerContains(Aspect tag) {
      return tag == this.aspect ? this.amount : 0;
   }

   @Override
   public boolean doesContainerAccept(Aspect tag) {
      return this.aspectFilter != null ? tag.equals(this.aspectFilter) : true;
   }

   @Override
   public boolean isConnectable(EnumFacing face) {
      return face == EnumFacing.UP;
   }

   @Override
   public boolean canInputFrom(EnumFacing face) {
      return face == EnumFacing.UP;
   }

   @Override
   public boolean canOutputTo(EnumFacing face) {
      return face == EnumFacing.UP;
   }

   @Override
   public void setSuction(Aspect aspect, int amount) {
   }

   @Override
   public int getMinimumSuction() {
      return this.aspectFilter != null ? 64 : 32;
   }

   @Override
   public Aspect getSuctionType(EnumFacing loc) {
      return this.aspectFilter != null ? this.aspectFilter : this.aspect;
   }

   @Override
   public int getSuctionAmount(EnumFacing loc) {
      if (this.amount < 250) {
         return this.aspectFilter != null ? 64 : 32;
      } else {
         return 0;
      }
   }

   @Override
   public Aspect getEssentiaType(EnumFacing loc) {
      return this.aspect;
   }

   @Override
   public int getEssentiaAmount(EnumFacing loc) {
      return this.amount;
   }

   @Override
   public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
      return this.canOutputTo(face) && this.takeFromContainer(aspect, amount) ? amount : 0;
   }

   @Override
   public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
      return this.canInputFrom(face) ? amount - this.addToContainer(aspect, amount) : 0;
   }

   @Override
   public void func_73660_a() {
      if (!this.field_145850_b.field_72995_K && ++this.count % 5 == 0 && this.amount < 250) {
         this.fillJar();
      }
   }

   void fillJar() {
      TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.field_145850_b, this.field_174879_c, EnumFacing.UP);
      if (te != null) {
         IEssentiaTransport ic = (IEssentiaTransport)te;
         if (!ic.canOutputTo(EnumFacing.DOWN)) {
            return;
         }

         Aspect ta = null;
         if (this.aspectFilter != null) {
            ta = this.aspectFilter;
         } else if (this.aspect != null && this.amount > 0) {
            ta = this.aspect;
         } else if (ic.getEssentiaAmount(EnumFacing.DOWN) > 0
            && ic.getSuctionAmount(EnumFacing.DOWN) < this.getSuctionAmount(EnumFacing.UP)
            && this.getSuctionAmount(EnumFacing.UP) >= ic.getMinimumSuction()) {
            ta = ic.getEssentiaType(EnumFacing.DOWN);
         }

         if (ta != null && ic.getSuctionAmount(EnumFacing.DOWN) < this.getSuctionAmount(EnumFacing.UP)) {
            this.addToContainer(ta, ic.takeEssentia(ta, 1, EnumFacing.DOWN));
         }
      }
   }

   @Override
   public boolean isBlocked() {
      return this.blocked;
   }
}

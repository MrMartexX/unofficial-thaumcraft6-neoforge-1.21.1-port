package thaumcraft.common.tiles.essentia;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.devices.TileBellows;

public class TileTubeBuffer extends TileTube implements IAspectContainer {
   public AspectList aspects = new AspectList();
   public final int MAXAMOUNT = 10;
   public byte[] chokedSides = new byte[]{0, 0, 0, 0, 0, 0};
   int count = 0;
   int bellows = -1;

   @Override
   public void readSyncNBT(NBTTagCompound nbttagcompound) {
      this.aspects.readFromNBT(nbttagcompound);
      byte[] sides = nbttagcompound.func_74770_j("open");
      if (sides != null && sides.length == 6) {
         for (int a = 0; a < 6; a++) {
            this.openSides[a] = sides[a] == 1;
         }
      }

      this.chokedSides = nbttagcompound.func_74770_j("choke");
      if (this.chokedSides == null || this.chokedSides.length < 6) {
         this.chokedSides = new byte[]{0, 0, 0, 0, 0, 0};
      }

      this.facing = EnumFacing.field_82609_l[nbttagcompound.func_74762_e("side")];
   }

   @Override
   public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
      this.aspects.writeToNBT(nbttagcompound);
      byte[] sides = new byte[6];

      for (int a = 0; a < 6; a++) {
         sides[a] = (byte)(this.openSides[a] ? 1 : 0);
      }

      nbttagcompound.func_74773_a("open", sides);
      nbttagcompound.func_74773_a("choke", this.chokedSides);
      nbttagcompound.func_74768_a("side", this.facing.ordinal());
      return nbttagcompound;
   }

   @Override
   public AspectList getAspects() {
      return this.aspects;
   }

   @Override
   public void setAspects(AspectList aspects) {
   }

   @Override
   public int addToContainer(Aspect tt, int am) {
      if (am != 1) {
         return am;
      } else if (this.aspects.visSize() < 10) {
         this.aspects.add(tt, am);
         this.func_70296_d();
         this.syncTile(false);
         return 0;
      } else {
         return am;
      }
   }

   @Override
   public boolean takeFromContainer(Aspect tt, int am) {
      if (this.aspects.getAmount(tt) >= am) {
         this.aspects.remove(tt, am);
         this.func_70296_d();
         this.syncTile(false);
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
      return this.aspects.getAmount(tag) >= amt;
   }

   @Override
   public boolean doesContainerContain(AspectList ot) {
      return false;
   }

   @Override
   public int containerContains(Aspect tag) {
      return this.aspects.getAmount(tag);
   }

   @Override
   public boolean doesContainerAccept(Aspect tag) {
      return true;
   }

   @Override
   public boolean isConnectable(EnumFacing face) {
      return this.openSides[face.ordinal()];
   }

   @Override
   public boolean canInputFrom(EnumFacing face) {
      return this.openSides[face.ordinal()];
   }

   @Override
   public boolean canOutputTo(EnumFacing face) {
      return this.openSides[face.ordinal()];
   }

   @Override
   public void setSuction(Aspect aspect, int amount) {
   }

   @Override
   public int getMinimumSuction() {
      return 0;
   }

   @Override
   public Aspect getSuctionType(EnumFacing loc) {
      return null;
   }

   @Override
   public int getSuctionAmount(EnumFacing loc) {
      return this.chokedSides[loc.ordinal()] == 2 ? 0 : (this.bellows > 0 && this.chokedSides[loc.ordinal()] != 1 ? this.bellows * 32 : 1);
   }

   @Override
   public Aspect getEssentiaType(EnumFacing loc) {
      return this.aspects.size() > 0 ? this.aspects.getAspects()[this.field_145850_b.field_73012_v.nextInt(this.aspects.getAspects().length)] : null;
   }

   @Override
   public int getEssentiaAmount(EnumFacing loc) {
      return this.aspects.visSize();
   }

   @Override
   public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
      if (!this.canOutputTo(face)) {
         return 0;
      }

      TileEntity te = null;
      IEssentiaTransport ic = null;
      int suction = 0;
      te = ThaumcraftApiHelper.getConnectableTile(this.field_145850_b, this.field_174879_c, face);
      if (te != null) {
         ic = (IEssentiaTransport)te;
         suction = ic.getSuctionAmount(face.func_176734_d());
      }

      for (EnumFacing dir : EnumFacing.field_82609_l) {
         if (this.canOutputTo(dir) && dir != face) {
            te = ThaumcraftApiHelper.getConnectableTile(this.field_145850_b, this.field_174879_c, dir);
            if (te != null) {
               ic = (IEssentiaTransport)te;
               int sa = ic.getSuctionAmount(dir.func_176734_d());
               Aspect su = ic.getSuctionType(dir.func_176734_d());
               if ((su == aspect || su == null) && suction < sa && this.getSuctionAmount(dir) < sa) {
                  return 0;
               }
            }
         }
      }

      if (amount > this.aspects.getAmount(aspect)) {
         amount = this.aspects.getAmount(aspect);
      }

      return this.takeFromContainer(aspect, amount) ? amount : 0;
   }

   @Override
   public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
      return this.canInputFrom(face) ? amount - this.addToContainer(aspect, amount) : 0;
   }

   @Override
   public void func_73660_a() {
      this.count++;
      if (this.bellows < 0 || this.count % 20 == 0) {
         this.getBellows();
      }

      if (!this.field_145850_b.field_72995_K && this.count % 5 == 0 && this.aspects.visSize() < 10) {
         this.fillBuffer();
      }
   }

   void fillBuffer() {
      TileEntity te = null;
      IEssentiaTransport ic = null;

      for (EnumFacing dir : EnumFacing.field_82609_l) {
         if (this.canInputFrom(dir)) {
            te = ThaumcraftApiHelper.getConnectableTile(this.field_145850_b, this.field_174879_c, dir);
            if (te != null) {
               ic = (IEssentiaTransport)te;
               if (ic.getEssentiaAmount(dir.func_176734_d()) > 0
                  && ic.getSuctionAmount(dir.func_176734_d()) < this.getSuctionAmount(dir)
                  && this.getSuctionAmount(dir) >= ic.getMinimumSuction()) {
                  Aspect ta = ic.getEssentiaType(dir.func_176734_d());
                  this.addToContainer(ta, ic.takeEssentia(ta, 1, dir.func_176734_d()));
                  return;
               }
            }
         }
      }
   }

   public void getBellows() {
      this.bellows = TileBellows.getBellows(this.field_145850_b, this.field_174879_c, EnumFacing.field_82609_l);
   }

   @Override
   public boolean onCasterRightClick(World world, ItemStack wandstack, EntityPlayer player, BlockPos bp, EnumFacing side, EnumHand hand) {
      RayTraceResult hit = RayTracer.retraceBlock(world, player, this.field_174879_c);
      if (hit == null) {
         return false;
      }

      if (hit.subHit >= 0 && hit.subHit < 6) {
         player.func_184609_a(hand);
         if (player.func_70093_af()) {
            player.field_70170_p
               .func_184134_a(
                  bp.func_177958_n() + 0.5,
                  bp.func_177956_o() + 0.5,
                  bp.func_177952_p() + 0.5,
                  SoundsTC.squeek,
                  SoundCategory.BLOCKS,
                  0.6F,
                  2.0F + world.field_73012_v.nextFloat() * 0.2F,
                  false
               );
            if (!world.field_72995_K) {
               this.chokedSides[hit.subHit]++;
               if (this.chokedSides[hit.subHit] > 2) {
                  this.chokedSides[hit.subHit] = 0;
               }

               this.func_70296_d();
               this.syncTile(true);
            }
         } else {
            player.field_70170_p
               .func_184134_a(
                  bp.func_177958_n() + 0.5,
                  bp.func_177956_o() + 0.5,
                  bp.func_177952_p() + 0.5,
                  SoundsTC.tool,
                  SoundCategory.BLOCKS,
                  0.5F,
                  0.9F + player.field_70170_p.field_73012_v.nextFloat() * 0.2F,
                  false
               );
            this.openSides[hit.subHit] = !this.openSides[hit.subHit];
            EnumFacing dir = EnumFacing.field_82609_l[hit.subHit];
            TileEntity tile = world.func_175625_s(this.field_174879_c.func_177972_a(dir));
            if (tile != null && tile instanceof TileTube) {
               ((TileTube)tile).openSides[dir.func_176734_d().ordinal()] = this.openSides[hit.subHit];
               ((TileTube)tile).syncTile(true);
               tile.func_70296_d();
            }

            this.func_70296_d();
            this.syncTile(true);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean canConnectSide(EnumFacing side) {
      TileEntity tile = this.field_145850_b.func_175625_s(this.field_174879_c.func_177972_a(side));
      return tile != null && tile instanceof IEssentiaTransport;
   }

   @Override
   public void addTraceableCuboids(List<IndexedCuboid6> cuboids) {
      float min = 0.375F;
      float max = 0.625F;
      if (this.canConnectSide(EnumFacing.DOWN)) {
         cuboids.add(
            new IndexedCuboid6(
               0,
               new Cuboid6(
                  this.field_174879_c.func_177958_n() + min,
                  this.field_174879_c.func_177956_o(),
                  this.field_174879_c.func_177952_p() + min,
                  this.field_174879_c.func_177958_n() + max,
                  this.field_174879_c.func_177956_o() + 0.5,
                  this.field_174879_c.func_177952_p() + max
               )
            )
         );
      }

      if (this.canConnectSide(EnumFacing.UP)) {
         cuboids.add(
            new IndexedCuboid6(
               1,
               new Cuboid6(
                  this.field_174879_c.func_177958_n() + min,
                  this.field_174879_c.func_177956_o() + 0.5,
                  this.field_174879_c.func_177952_p() + min,
                  this.field_174879_c.func_177958_n() + max,
                  this.field_174879_c.func_177956_o() + 1,
                  this.field_174879_c.func_177952_p() + max
               )
            )
         );
      }

      if (this.canConnectSide(EnumFacing.NORTH)) {
         cuboids.add(
            new IndexedCuboid6(
               2,
               new Cuboid6(
                  this.field_174879_c.func_177958_n() + min,
                  this.field_174879_c.func_177956_o() + min,
                  this.field_174879_c.func_177952_p(),
                  this.field_174879_c.func_177958_n() + max,
                  this.field_174879_c.func_177956_o() + max,
                  this.field_174879_c.func_177952_p() + 0.5
               )
            )
         );
      }

      if (this.canConnectSide(EnumFacing.SOUTH)) {
         cuboids.add(
            new IndexedCuboid6(
               3,
               new Cuboid6(
                  this.field_174879_c.func_177958_n() + min,
                  this.field_174879_c.func_177956_o() + min,
                  this.field_174879_c.func_177952_p() + 0.5,
                  this.field_174879_c.func_177958_n() + max,
                  this.field_174879_c.func_177956_o() + max,
                  this.field_174879_c.func_177952_p() + 1
               )
            )
         );
      }

      if (this.canConnectSide(EnumFacing.WEST)) {
         cuboids.add(
            new IndexedCuboid6(
               4,
               new Cuboid6(
                  this.field_174879_c.func_177958_n(),
                  this.field_174879_c.func_177956_o() + min,
                  this.field_174879_c.func_177952_p() + min,
                  this.field_174879_c.func_177958_n() + 0.5,
                  this.field_174879_c.func_177956_o() + max,
                  this.field_174879_c.func_177952_p() + max
               )
            )
         );
      }

      if (this.canConnectSide(EnumFacing.EAST)) {
         cuboids.add(
            new IndexedCuboid6(
               5,
               new Cuboid6(
                  this.field_174879_c.func_177958_n() + 0.5,
                  this.field_174879_c.func_177956_o() + min,
                  this.field_174879_c.func_177952_p() + min,
                  this.field_174879_c.func_177958_n() + 1,
                  this.field_174879_c.func_177956_o() + max,
                  this.field_174879_c.func_177952_p() + max
               )
            )
         );
      }

      cuboids.add(
         new IndexedCuboid6(
            6,
            new Cuboid6(
               this.field_174879_c.func_177958_n() + 0.25F,
               this.field_174879_c.func_177956_o() + 0.25F,
               this.field_174879_c.func_177952_p() + 0.25F,
               this.field_174879_c.func_177958_n() + 0.75F,
               this.field_174879_c.func_177956_o() + 0.75F,
               this.field_174879_c.func_177952_p() + 0.75F
            )
         )
      );
   }
}

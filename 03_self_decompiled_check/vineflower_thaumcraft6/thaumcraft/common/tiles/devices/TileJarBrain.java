package thaumcraft.common.tiles.devices;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.essentia.TileJar;

public class TileJarBrain extends TileJar {
   public float field_40063_b;
   public float field_40061_d;
   public float field_40059_f;
   public float field_40066_q;
   public float rota;
   public float rotb;
   public int xp = 0;
   public int xpMax = 2000;
   public int eatDelay = 0;
   long lastsigh = System.currentTimeMillis() + 1500L;

   @Override
   public void readSyncNBT(NBTTagCompound nbttagcompound) {
      this.xp = nbttagcompound.func_74762_e("XP");
   }

   @Override
   public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
      nbttagcompound.func_74768_a("XP", this.xp);
      return nbttagcompound;
   }

   @Override
   public void func_73660_a() {
      Entity entity = null;
      if (this.xp > this.xpMax) {
         this.xp = this.xpMax;
      }

      if (this.xp < this.xpMax) {
         entity = this.getClosestXPOrb();
         if (entity != null && this.eatDelay == 0) {
            double var3 = (this.field_174879_c.func_177958_n() + 0.5 - entity.field_70165_t) / 25.0;
            double var5 = (this.field_174879_c.func_177956_o() + 0.5 - entity.field_70163_u) / 25.0;
            double var7 = (this.field_174879_c.func_177952_p() + 0.5 - entity.field_70161_v) / 25.0;
            double var9 = Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
            double var11 = 1.0 - var9;
            if (var11 > 0.0) {
               var11 *= var11;
               entity.field_70159_w += var3 / var9 * var11 * 0.3;
               entity.field_70181_x += var5 / var9 * var11 * 0.5;
               entity.field_70179_y += var7 / var9 * var11 * 0.3;
            }
         }
      }

      if (this.field_145850_b.field_72995_K) {
         this.rotb = this.rota;
         if (entity == null) {
            entity = this.field_145850_b
               .func_184137_a(
                  this.field_174879_c.func_177958_n() + 0.5, this.field_174879_c.func_177956_o() + 0.5, this.field_174879_c.func_177952_p() + 0.5, 6.0, false
               );
            if (entity != null && this.lastsigh < System.currentTimeMillis()) {
               this.field_145850_b
                  .func_184134_a(
                     this.field_174879_c.func_177958_n() + 0.5,
                     this.field_174879_c.func_177956_o() + 0.5,
                     this.field_174879_c.func_177952_p() + 0.5,
                     SoundsTC.brain,
                     SoundCategory.AMBIENT,
                     0.15F,
                     0.8F + this.field_145850_b.field_73012_v.nextFloat() * 0.4F,
                     false
                  );
               this.lastsigh = System.currentTimeMillis() + 5000L + this.field_145850_b.field_73012_v.nextInt(25000);
            }
         }

         if (entity != null) {
            double d = entity.field_70165_t - (this.field_174879_c.func_177958_n() + 0.5F);
            double d1 = entity.field_70161_v - (this.field_174879_c.func_177952_p() + 0.5F);
            this.field_40066_q = (float)Math.atan2(d1, d);
            this.field_40059_f += 0.1F;
            if (this.field_40059_f < 0.5F || rand.nextInt(40) == 0) {
               float f3 = this.field_40061_d;

               do {
                  this.field_40061_d = this.field_40061_d + (rand.nextInt(4) - rand.nextInt(4));
               } while (f3 == this.field_40061_d);
            }
         } else {
            this.field_40066_q += 0.01F;
         }

         while (this.rota >= 3.141593F) {
            this.rota -= 6.283185F;
         }

         while (this.rota < -3.141593F) {
            this.rota += 6.283185F;
         }

         while (this.field_40066_q >= 3.141593F) {
            this.field_40066_q -= 6.283185F;
         }

         while (this.field_40066_q < -3.141593F) {
            this.field_40066_q += 6.283185F;
         }

         float f = this.field_40066_q - this.rota;

         while (f >= 3.141593F) {
            f -= 6.283185F;
         }

         while (f < -3.141593F) {
            f += 6.283185F;
         }

         this.rota += f * 0.04F;
      }

      if (this.eatDelay > 0) {
         this.eatDelay--;
      } else if (this.xp < this.xpMax) {
         List ents = this.field_145850_b
            .func_72872_a(
               EntityXPOrb.class,
               new AxisAlignedBB(
                  this.field_174879_c.func_177958_n() - 0.1,
                  this.field_174879_c.func_177956_o() - 0.1,
                  this.field_174879_c.func_177952_p() - 0.1,
                  this.field_174879_c.func_177958_n() + 1.1,
                  this.field_174879_c.func_177956_o() + 1.1,
                  this.field_174879_c.func_177952_p() + 1.1
               )
            );
         if (ents.size() > 0) {
            for (Object ent : ents) {
               EntityXPOrb eo = (EntityXPOrb)ent;
               this.xp = this.xp + eo.func_70526_d();
               eo.func_184185_a(
                  SoundEvents.field_187537_bA,
                  0.1F,
                  (this.field_145850_b.field_73012_v.nextFloat() - this.field_145850_b.field_73012_v.nextFloat()) * 0.2F + 1.0F
               );
               eo.func_70106_y();
            }

            this.syncTile(false);
            this.func_70296_d();
         }
      }
   }

   public Entity getClosestXPOrb() {
      double cdist = Double.MAX_VALUE;
      Entity orb = null;
      List ents = this.field_145850_b
         .func_72872_a(
            EntityXPOrb.class,
            new AxisAlignedBB(
                  this.field_174879_c.func_177958_n(),
                  this.field_174879_c.func_177956_o(),
                  this.field_174879_c.func_177952_p(),
                  this.field_174879_c.func_177958_n() + 1,
                  this.field_174879_c.func_177956_o() + 1,
                  this.field_174879_c.func_177952_p() + 1
               )
               .func_72314_b(8.0, 8.0, 8.0)
         );
      if (ents.size() > 0) {
         for (Object ent : ents) {
            EntityXPOrb eo = (EntityXPOrb)ent;
            double d = this.func_145835_a(eo.field_70165_t, eo.field_70163_u, eo.field_70161_v);
            if (d < cdist) {
               orb = eo;
               cdist = d;
            }
         }
      }

      return orb;
   }
}

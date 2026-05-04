package thaumcraft.common.tiles.misc;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.casters.foci.FocusEffectRift;

public class TileHole extends TileMemory implements ITickable {
   public short countdown = 0;
   public short countdownmax = 120;
   public byte count = 0;
   public EnumFacing direction = null;

   public TileHole() {
   }

   public TileHole(IBlockState bi, short max, byte count, EnumFacing direction) {
      super(bi);
      this.count = count;
      this.countdownmax = max;
      this.direction = direction;
   }

   public TileHole(byte count) {
      this.count = count;
   }

   public void func_73660_a() {
      if (this.field_145850_b.field_72995_K) {
         for (int a = 0; a < 2; a++) {
            this.surroundwithsparkles();
         }
      } else {
         if (this.countdown == 0 && this.count > 1 && this.direction != null) {
            switch (this.direction.func_176740_k()) {
               case Y:
                  for (int a = 0; a < 9; a++) {
                     if (a / 3 != 1 || a % 3 != 1) {
                        FocusEffectRift.createHole(
                           this.field_145850_b, this.func_174877_v().func_177982_a(-1 + a / 3, 0, -1 + a % 3), null, (byte)1, this.countdownmax
                        );
                     }
                  }
                  break;
               case Z:
                  for (int a = 0; a < 9; a++) {
                     if (a / 3 != 1 || a % 3 != 1) {
                        FocusEffectRift.createHole(
                           this.field_145850_b, this.func_174877_v().func_177982_a(-1 + a / 3, -1 + a % 3, 0), null, (byte)1, this.countdownmax
                        );
                     }
                  }
                  break;
               case X:
                  for (int a = 0; a < 9; a++) {
                     if (a / 3 != 1 || a % 3 != 1) {
                        FocusEffectRift.createHole(
                           this.field_145850_b, this.func_174877_v().func_177982_a(0, -1 + a / 3, -1 + a % 3), null, (byte)1, this.countdownmax
                        );
                     }
                  }
            }

            if (!FocusEffectRift.createHole(
               this.field_145850_b,
               this.func_174877_v().func_177972_a(this.direction.func_176734_d()),
               this.direction,
               (byte)(this.count - 1),
               this.countdownmax
            )) {
               this.count = 0;
            }
         }

         this.countdown++;
         if (this.countdown % 20 == 0) {
            this.func_70296_d();
         }

         if (this.countdown >= this.countdownmax) {
            this.field_145850_b.func_180501_a(this.func_174877_v(), this.oldblock, 3);
         }
      }
   }

   private void surroundwithsparkles() {
      for (EnumFacing d1 : EnumFacing.values()) {
         IBlockState b1 = this.field_145850_b.func_180495_p(this.func_174877_v().func_177972_a(d1));
         if (b1.func_177230_c() != BlocksTC.hole && !b1.func_185914_p()) {
            for (EnumFacing d2 : EnumFacing.values()) {
               if (d1.func_176740_k() != d2.func_176740_k()
                  && (
                     this.field_145850_b.func_180495_p(this.func_174877_v().func_177972_a(d2)).func_185914_p()
                        || this.field_145850_b.func_180495_p(this.func_174877_v().func_177972_a(d1).func_177972_a(d2)).func_185914_p()
                  )) {
                  float sx = 0.5F * d1.func_82601_c();
                  float sy = 0.5F * d1.func_96559_d();
                  float sz = 0.5F * d1.func_82599_e();
                  if (sx == 0.0F) {
                     sx = 0.5F * d2.func_82601_c();
                  }

                  if (sy == 0.0F) {
                     sy = 0.5F * d2.func_96559_d();
                  }

                  if (sz == 0.0F) {
                     sz = 0.5F * d2.func_82599_e();
                  }

                  if (sx == 0.0F) {
                     sx = this.field_145850_b.field_73012_v.nextFloat();
                  } else {
                     sx += 0.5F;
                  }

                  if (sy == 0.0F) {
                     sy = this.field_145850_b.field_73012_v.nextFloat();
                  } else {
                     sy += 0.5F;
                  }

                  if (sz == 0.0F) {
                     sz = this.field_145850_b.field_73012_v.nextFloat();
                  } else {
                     sz += 0.5F;
                  }

                  FXDispatcher.INSTANCE
                     .sparkle(
                        this.func_174877_v().func_177958_n() + sx,
                        this.func_174877_v().func_177956_o() + sy,
                        this.func_174877_v().func_177952_p() + sz,
                        0.25F,
                        0.25F,
                        1.0F
                     );
               }
            }
         }
      }
   }

   @Override
   public void func_145839_a(NBTTagCompound nbttagcompound) {
      super.func_145839_a(nbttagcompound);
      this.countdown = nbttagcompound.func_74765_d("countdown");
      this.countdownmax = nbttagcompound.func_74765_d("countdownmax");
      this.count = nbttagcompound.func_74771_c("count");
      byte db = nbttagcompound.func_74771_c("direction");
      this.direction = db >= 0 ? EnumFacing.values()[db] : null;
   }

   @Override
   public NBTTagCompound func_189515_b(NBTTagCompound nbttagcompound) {
      super.func_189515_b(nbttagcompound);
      nbttagcompound.func_74777_a("countdown", this.countdown);
      nbttagcompound.func_74777_a("countdownmax", this.countdownmax);
      nbttagcompound.func_74774_a("count", this.count);
      nbttagcompound.func_74774_a("direction", this.direction == null ? -1 : (byte)this.direction.ordinal());
      return nbttagcompound;
   }
}

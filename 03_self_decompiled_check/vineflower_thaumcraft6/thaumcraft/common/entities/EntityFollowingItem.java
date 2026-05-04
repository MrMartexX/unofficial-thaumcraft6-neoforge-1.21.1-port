package thaumcraft.common.entities;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.client.fx.FXDispatcher;

public class EntityFollowingItem extends EntitySpecialItem implements IEntityAdditionalSpawnData {
   double targetX = 0.0;
   double targetY = 0.0;
   double targetZ = 0.0;
   int type = 3;
   public Entity target = null;
   int field_70292_b = 20;
   public double gravity = 0.04F;

   public EntityFollowingItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack) {
      super(par1World);
      this.func_70105_a(0.25F, 0.25F);
      this.func_70107_b(par2, par4, par6);
      this.func_92058_a(par8ItemStack);
      this.field_70177_z = (float)(Math.random() * 360.0);
   }

   public EntityFollowingItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack, Entity target, int t) {
      this(par1World, par2, par4, par6, par8ItemStack);
      this.target = target;
      this.targetX = target.field_70165_t;
      this.targetY = target.func_174813_aQ().field_72338_b + target.field_70131_O / 2.0F;
      this.targetZ = target.field_70161_v;
      this.type = t;
      this.field_70145_X = true;
   }

   public EntityFollowingItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack, double tx, double ty, double tz) {
      this(par1World, par2, par4, par6, par8ItemStack);
      this.targetX = tx;
      this.targetY = ty;
      this.targetZ = tz;
   }

   public EntityFollowingItem(World par1World) {
      super(par1World);
      this.func_70105_a(0.25F, 0.25F);
   }

   @Override
   public void func_70071_h_() {
      if (this.target != null) {
         this.targetX = this.target.field_70165_t;
         this.targetY = this.target.func_174813_aQ().field_72338_b + this.target.field_70131_O / 2.0F;
         this.targetZ = this.target.field_70161_v;
      }

      if (this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0) {
         this.field_70181_x = this.field_70181_x - this.gravity;
      } else {
         float xd = (float)(this.targetX - this.field_70165_t);
         float yd = (float)(this.targetY - this.field_70163_u);
         float zd = (float)(this.targetZ - this.field_70161_v);
         if (this.field_70292_b > 1) {
            this.field_70292_b--;
         }

         double distance = MathHelper.func_76129_c(xd * xd + yd * yd + zd * zd);
         if (distance > 0.5) {
            distance *= this.field_70292_b;
            this.field_70159_w = xd / distance;
            this.field_70181_x = yd / distance;
            this.field_70179_y = zd / distance;
         } else {
            this.field_70159_w *= 0.1F;
            this.field_70181_x *= 0.1F;
            this.field_70179_y *= 0.1F;
            this.targetX = 0.0;
            this.targetY = 0.0;
            this.targetZ = 0.0;
            this.target = null;
            this.field_70145_X = false;
         }

         if (this.field_70170_p.field_72995_K) {
            float h = (float)((this.func_174813_aQ().field_72337_e - this.func_174813_aQ().field_72338_b) / 2.0)
               + MathHelper.func_76126_a(this.func_174872_o() / 10.0F + this.field_70290_d) * 0.1F
               + 0.1F;
            if (this.type != 10) {
               FXDispatcher.INSTANCE
                  .drawNitorCore(
                     (float)this.field_70169_q + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.125F,
                     (float)this.field_70167_r + h + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.125F,
                     (float)this.field_70166_s + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.125F,
                     this.field_70146_Z.nextGaussian() * 0.01F,
                     this.field_70146_Z.nextGaussian() * 0.01F,
                     this.field_70146_Z.nextGaussian() * 0.01F
                  );
            } else {
               FXDispatcher.INSTANCE
                  .crucibleBubble(
                     (float)this.field_70169_q + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.125F,
                     (float)this.field_70167_r + h + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.125F,
                     (float)this.field_70166_s + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.125F,
                     0.33F,
                     0.33F,
                     1.0F
                  );
            }
         }
      }

      super.func_70071_h_();
   }

   public void func_70014_b(NBTTagCompound par1NBTTagCompound) {
      super.func_70014_b(par1NBTTagCompound);
      par1NBTTagCompound.func_74777_a("type", (short)this.type);
   }

   public void func_70037_a(NBTTagCompound par1NBTTagCompound) {
      super.func_70037_a(par1NBTTagCompound);
      this.type = par1NBTTagCompound.func_74765_d("type");
   }

   public void writeSpawnData(ByteBuf data) {
      if (this.target != null) {
         data.writeInt(this.target == null ? -1 : this.target.func_145782_y());
         data.writeDouble(this.targetX);
         data.writeDouble(this.targetY);
         data.writeDouble(this.targetZ);
         data.writeByte(this.type);
      }
   }

   public void readSpawnData(ByteBuf data) {
      try {
         int ent = data.readInt();
         if (ent > -1) {
            this.target = this.field_70170_p.func_73045_a(ent);
         }

         this.targetX = data.readDouble();
         this.targetY = data.readDouble();
         this.targetZ = data.readDouble();
         this.type = data.readByte();
      } catch (Exception var3) {
      }
   }
}

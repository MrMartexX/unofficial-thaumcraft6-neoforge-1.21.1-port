package thaumcraft.common.entities.monster;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityFireBat extends EntityMob {
   private BlockPos currentFlightTarget;
   public EntityLivingBase owner = null;
   private static final DataParameter<Boolean> HANGING = EntityDataManager.func_187226_a(EntityFireBat.class, DataSerializers.field_187198_h);
   public int damBonus = 0;
   private int attackTime;

   public EntityFireBat(World par1World) {
      super(par1World);
      this.func_70105_a(0.5F, 0.9F);
      this.setIsBatHanging(true);
      this.field_70178_ae = true;
   }

   public void func_70088_a() {
      super.func_70088_a();
      this.func_184212_Q().func_187214_a(HANGING, false);
   }

   @SideOnly(Side.CLIENT)
   public int func_70070_b() {
      return 15728880;
   }

   public float func_70013_c() {
      return 1.0F;
   }

   protected float func_70599_aP() {
      return 0.1F;
   }

   protected float func_70647_i() {
      return super.func_70647_i() * 0.95F;
   }

   protected SoundEvent func_184639_G() {
      return this.getIsBatHanging() && this.field_70146_Z.nextInt(4) != 0 ? null : SoundEvents.field_187740_w;
   }

   protected SoundEvent func_184601_bQ(DamageSource damageSourceIn) {
      return SoundEvents.field_187743_y;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187742_x;
   }

   public boolean func_70104_M() {
      return false;
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(5.0);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(1.0);
   }

   public boolean getIsBatHanging() {
      return (Boolean)this.func_184212_Q().func_187225_a(HANGING);
   }

   public void setIsBatHanging(boolean par1) {
      this.func_184212_Q().func_187227_b(HANGING, par1);
   }

   public void func_70636_d() {
      if (this.func_70026_G()) {
         this.func_70097_a(DamageSource.field_76369_e, 1.0F);
      }

      super.func_70636_d();
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.getIsBatHanging()) {
         this.field_70159_w = this.field_70181_x = this.field_70179_y = 0.0;
         this.field_70163_u = MathHelper.func_76128_c(this.field_70163_u) + 1.0 - this.field_70131_O;
      } else {
         this.field_70181_x *= 0.6F;
      }
   }

   protected void func_70619_bc() {
      super.func_70619_bc();
      if (this.attackTime > 0) {
         this.attackTime--;
      }

      BlockPos blockpos = new BlockPos(this);
      BlockPos blockpos1 = blockpos.func_177984_a();
      if (this.getIsBatHanging()) {
         if (!this.field_70170_p.func_180495_p(blockpos1).func_185915_l()) {
            this.setIsBatHanging(false);
            this.field_70170_p.func_180498_a((EntityPlayer)null, 1025, blockpos, 0);
         } else {
            if (this.field_70146_Z.nextInt(200) == 0) {
               this.field_70759_as = this.field_70146_Z.nextInt(360);
            }

            if (this.field_70170_p.func_72890_a(this, 4.0) != null) {
               this.setIsBatHanging(false);
               this.field_70170_p.func_180498_a((EntityPlayer)null, 1025, blockpos, 0);
            }
         }
      } else if (this.func_70638_az() == null) {
         if (this.currentFlightTarget != null && (!this.field_70170_p.func_175623_d(this.currentFlightTarget) || this.currentFlightTarget.func_177956_o() < 1)) {
            this.currentFlightTarget = null;
         }

         if (this.currentFlightTarget == null || this.field_70146_Z.nextInt(30) == 0 || this.func_174831_c(this.currentFlightTarget) < 4.0) {
            this.currentFlightTarget = new BlockPos(
               (int)this.field_70165_t + this.field_70146_Z.nextInt(7) - this.field_70146_Z.nextInt(7),
               (int)this.field_70163_u + this.field_70146_Z.nextInt(6) - 2,
               (int)this.field_70161_v + this.field_70146_Z.nextInt(7) - this.field_70146_Z.nextInt(7)
            );
         }

         double var1 = this.currentFlightTarget.func_177958_n() + 0.5 - this.field_70165_t;
         double var3 = this.currentFlightTarget.func_177956_o() + 0.1 - this.field_70163_u;
         double var5 = this.currentFlightTarget.func_177952_p() + 0.5 - this.field_70161_v;
         this.field_70159_w = this.field_70159_w + (Math.signum(var1) * 0.5 - this.field_70159_w) * 0.1F;
         this.field_70181_x = this.field_70181_x + (Math.signum(var3) * 0.7F - this.field_70181_x) * 0.1F;
         this.field_70179_y = this.field_70179_y + (Math.signum(var5) * 0.5 - this.field_70179_y) * 0.1F;
         float var7 = (float)(Math.atan2(this.field_70179_y, this.field_70159_w) * 180.0 / Math.PI) - 90.0F;
         float var8 = MathHelper.func_76142_g(var7 - this.field_70177_z);
         this.field_191988_bg = 0.5F;
         this.field_70177_z += var8;
         if (this.field_70146_Z.nextInt(100) == 0 && this.field_70170_p.func_180495_p(blockpos1).func_185915_l()) {
            this.setIsBatHanging(true);
         }
      } else {
         double var1 = this.func_70638_az().field_70165_t - this.field_70165_t;
         double var3 = this.func_70638_az().field_70163_u + this.func_70638_az().func_70047_e() * 0.66F - this.field_70163_u;
         double var5 = this.func_70638_az().field_70161_v - this.field_70161_v;
         this.field_70159_w = this.field_70159_w + (Math.signum(var1) * 0.5 - this.field_70159_w) * 0.1F;
         this.field_70181_x = this.field_70181_x + (Math.signum(var3) * 0.7F - this.field_70181_x) * 0.1F;
         this.field_70179_y = this.field_70179_y + (Math.signum(var5) * 0.5 - this.field_70179_y) * 0.1F;
         float var7 = (float)(Math.atan2(this.field_70179_y, this.field_70159_w) * 180.0 / Math.PI) - 90.0F;
         float var8 = MathHelper.func_76142_g(var7 - this.field_70177_z);
         this.field_191988_bg = 0.5F;
         this.field_70177_z += var8;
      }

      if (this.func_70638_az() == null) {
         this.func_70624_b(this.findPlayerToAttack());
      } else if (this.func_70638_az().func_70089_S()) {
         float f = this.func_70638_az().func_70032_d(this);
         if (this.func_70089_S() && this.func_70685_l(this.func_70638_az())) {
            this.attackEntity(this.func_70638_az(), f);
         }
      } else {
         this.func_70624_b(null);
      }

      if (this.func_70638_az() instanceof EntityPlayer && ((EntityPlayer)this.func_70638_az()).field_71075_bZ.field_75102_a) {
         this.func_70624_b(null);
      }
   }

   protected boolean func_70041_e_() {
      return false;
   }

   public void func_180430_e(float par1, float damageMultiplier) {
   }

   protected void func_184231_a(double p_180433_1_, boolean p_180433_3_, IBlockState state, BlockPos pos) {
   }

   public boolean func_145773_az() {
      return true;
   }

   public boolean func_70097_a(DamageSource par1DamageSource, float par2) {
      if (!this.func_180431_b(par1DamageSource) && !par1DamageSource.func_76347_k() && !par1DamageSource.func_94541_c()) {
         if (!this.field_70170_p.field_72995_K && this.getIsBatHanging()) {
            this.setIsBatHanging(false);
         }

         return super.func_70097_a(par1DamageSource, par2);
      } else {
         return false;
      }
   }

   protected void attackEntity(Entity entity, float par2) {
      if (this.attackTime <= 0
         && par2 < Math.max(2.5F, entity.field_70130_N * 1.1F)
         && entity.func_174813_aQ().field_72337_e > this.func_174813_aQ().field_72338_b
         && entity.func_174813_aQ().field_72338_b < this.func_174813_aQ().field_72337_e) {
         this.attackTime = 20 + this.field_70170_p.field_73012_v.nextInt(20);
         if (this.field_70170_p.field_73012_v.nextInt(10) == 0 && !this.field_70170_p.field_72995_K) {
            entity.field_70172_ad = 0;
            this.field_70170_p.func_72885_a(this, this.field_70165_t, this.field_70163_u, this.field_70161_v, 1.5F, false, false);
            this.func_70106_y();
         }

         this.func_184185_a(SoundEvents.field_187743_y, 0.5F, 0.9F + this.field_70170_p.field_73012_v.nextFloat() * 0.2F);
         this.func_70652_k(entity);
      }
   }

   protected EntityLivingBase findPlayerToAttack() {
      double var1 = 12.0;
      return this.field_70170_p.func_72890_a(this, var1);
   }

   public void func_70037_a(NBTTagCompound nbt) {
      super.func_70037_a(nbt);
      this.setIsBatHanging(nbt.func_74767_n("hang"));
      this.damBonus = nbt.func_74771_c("damBonus");
   }

   public void func_70014_b(NBTTagCompound nbt) {
      super.func_70014_b(nbt);
      nbt.func_74757_a("hang", this.getIsBatHanging());
      nbt.func_74774_a("damBonus", (byte)this.damBonus);
   }

   public boolean func_70601_bi() {
      int i = MathHelper.func_76128_c(this.field_70165_t);
      int j = MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b);
      int k = MathHelper.func_76128_c(this.field_70161_v);
      BlockPos blockpos = new BlockPos(i, j, k);
      int var4 = this.field_70170_p.func_175699_k(blockpos);
      byte var5 = 7;
      return var4 > this.field_70146_Z.nextInt(var5) ? false : super.func_70601_bi();
   }

   protected Item func_146068_u() {
      return Items.field_151016_H;
   }

   protected boolean func_70814_o() {
      return true;
   }
}

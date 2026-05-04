package thaumcraft.common.entities.monster;

import io.netty.buffer.ByteBuf;
import java.awt.Color;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.Utils;

public class EntitySpellBat extends EntityMob implements IEntityAdditionalSpawnData {
   private BlockPos currentFlightTarget;
   public EntityLivingBase owner = null;
   FocusPackage focusPackage;
   private UUID ownerUniqueId;
   private static final DataParameter<Boolean> FRIENDLY = EntityDataManager.func_187226_a(EntitySpellBat.class, DataSerializers.field_187198_h);
   public int damBonus = 0;
   private int attackTime;
   FocusEffect[] effects = null;
   public int color = 16777215;

   public EntitySpellBat(World world) {
      super(world);
      this.func_70105_a(0.5F, 0.9F);
   }

   public EntitySpellBat(FocusPackage pac, boolean friendly) {
      super(pac.world);
      this.func_70105_a(0.5F, 0.9F);
      this.focusPackage = pac;
      this.setOwner(pac.getCaster());
      this.setIsFriendly(friendly);
   }

   public void func_70088_a() {
      super.func_70088_a();
      this.func_184212_Q().func_187214_a(FRIENDLY, false);
   }

   public boolean getIsFriendly() {
      return (Boolean)this.func_184212_Q().func_187225_a(FRIENDLY);
   }

   public void setIsFriendly(boolean par1) {
      this.func_184212_Q().func_187227_b(FRIENDLY, par1);
   }

   public void writeSpawnData(ByteBuf data) {
      Utils.writeNBTTagCompoundToBuffer(data, this.focusPackage.serialize());
   }

   public void readSpawnData(ByteBuf data) {
      try {
         this.focusPackage = new FocusPackage();
         this.focusPackage.deserialize(Utils.readNBTTagCompoundFromBuffer(data));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void setOwner(@Nullable EntityLivingBase ownerIn) {
      this.owner = ownerIn;
      this.ownerUniqueId = ownerIn == null ? null : ownerIn.func_110124_au();
   }

   @Nullable
   public EntityLivingBase getOwner() {
      if (this.owner == null && this.ownerUniqueId != null && this.field_70170_p instanceof WorldServer) {
         Entity entity = ((WorldServer)this.field_70170_p).func_175733_a(this.ownerUniqueId);
         if (entity instanceof EntityLivingBase) {
            this.owner = (EntityLivingBase)entity;
         }
      }

      return this.owner;
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
      return SoundEvents.field_187740_w;
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

   public Team func_96124_cp() {
      EntityLivingBase entitylivingbase = this.getOwner();
      return entitylivingbase != null ? entitylivingbase.func_96124_cp() : super.func_96124_cp();
   }

   public boolean func_184191_r(Entity otherEntity) {
      EntityLivingBase owner = this.getOwner();
      if (otherEntity == owner) {
         return true;
      } else {
         return owner == null ? super.func_184191_r(otherEntity) : owner.func_184191_r(otherEntity) || otherEntity.func_184191_r(owner);
      }
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (!this.field_70170_p.field_72995_K && (this.field_70173_aa > 600 || this.getOwner() == null)) {
         this.func_70106_y();
      }

      this.field_70181_x *= 0.6F;
      if (this.func_70089_S() && this.field_70170_p.field_72995_K) {
         if (this.effects == null) {
            this.effects = this.focusPackage.getFocusEffects();
            int r = 0;
            int g = 0;
            int b = 0;

            for (FocusEffect ef : this.effects) {
               Color c = new Color(FocusEngine.getElementColor(ef.getKey()));
               r += c.getRed();
               g += c.getGreen();
               b += c.getBlue();
            }

            r /= this.effects.length;
            g /= this.effects.length;
            b /= this.effects.length;
            Color c = new Color(r, g, b);
            this.color = c.getRGB();
         }

         if (this.effects != null && this.effects.length > 0) {
            FocusEffect eff = this.effects[this.field_70146_Z.nextInt(this.effects.length)];
            eff.renderParticleFX(
               this.field_70170_p,
               this.field_70165_t + this.field_70170_p.field_73012_v.nextGaussian() * 0.125,
               this.field_70163_u + this.field_70131_O / 2.0F + this.field_70170_p.field_73012_v.nextGaussian() * 0.125,
               this.field_70161_v + this.field_70170_p.field_73012_v.nextGaussian() * 0.125,
               0.0,
               0.0,
               0.0
            );
         }
      }
   }

   protected void func_70619_bc() {
      super.func_70619_bc();
      if (this.attackTime > 0) {
         this.attackTime--;
      }

      BlockPos blockpos = new BlockPos(this);
      BlockPos blockpos1 = blockpos.func_177984_a();
      if (this.func_70638_az() == null) {
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
         this.func_70624_b(this.findTargetToAttack());
      } else if (this.func_70638_az().func_70089_S()) {
         float f = this.func_70638_az().func_70032_d(this);
         if (this.func_70089_S() && this.func_70685_l(this.func_70638_az())) {
            this.attackEntity(this.func_70638_az(), f);
         }
      } else {
         this.func_70624_b(null);
      }

      if (!this.getIsFriendly() && this.func_70638_az() instanceof EntityPlayer && ((EntityPlayer)this.func_70638_az()).field_71075_bZ.field_75102_a) {
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
      return super.func_70097_a(par1DamageSource, par2);
   }

   protected void attackEntity(Entity target, float par2) {
      if (this.attackTime <= 0
         && par2 < Math.max(2.5F, target.field_70130_N * 1.1F)
         && target.func_174813_aQ().field_72337_e > this.func_174813_aQ().field_72338_b
         && target.func_174813_aQ().field_72338_b < this.func_174813_aQ().field_72337_e) {
         this.attackTime = 40;
         if (!this.field_70170_p.field_72995_K) {
            RayTraceResult ray = new RayTraceResult(target);
            ray.field_72307_f = target.func_174791_d().func_72441_c(0.0, target.field_70131_O / 2.0F, 0.0);
            Trajectory tra = new Trajectory(this.func_174791_d(), this.func_174791_d().func_72444_a(ray.field_72307_f));
            FocusEngine.runFocusPackage(this.focusPackage.copy(this.getOwner()), new Trajectory[]{tra}, new RayTraceResult[]{ray});
            this.func_70606_j(this.func_110143_aJ() - 1.0F);
         }

         this.func_184185_a(SoundEvents.field_187743_y, 0.5F, 0.9F + this.field_70170_p.field_73012_v.nextFloat() * 0.2F);
      }
   }

   protected void func_82167_n(Entity entityIn) {
      if (!this.getIsFriendly()) {
         super.func_82167_n(entityIn);
      }
   }

   protected EntityLivingBase findTargetToAttack() {
      double var1 = 12.0;
      List<EntityLivingBase> list = EntityUtils.getEntitiesInRange(
         this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, this, EntityLivingBase.class, var1
      );
      double d = Double.MAX_VALUE;
      EntityLivingBase ret = null;

      for (EntityLivingBase e : list) {
         if (!e.field_70128_L
            && (this.getIsFriendly() ? EntityUtils.isFriendly(this.getOwner(), e) : !EntityUtils.isFriendly(this.getOwner(), e) && !this.func_184191_r(e))) {
            double ed = this.func_70068_e(e);
            if (ed < d) {
               d = ed;
               ret = e;
            }
         }
      }

      return ret;
   }

   public void func_70037_a(NBTTagCompound nbt) {
      super.func_70037_a(nbt);
      this.ownerUniqueId = nbt.func_186857_a("OwnerUUID");
      this.setIsFriendly(nbt.func_74767_n("friendly"));

      try {
         this.focusPackage = new FocusPackage();
         this.focusPackage.deserialize(nbt.func_74775_l("pack"));
      } catch (Exception var3) {
      }
   }

   public void func_70014_b(NBTTagCompound nbt) {
      super.func_70014_b(nbt);
      if (this.ownerUniqueId != null) {
         nbt.func_186854_a("OwnerUUID", this.ownerUniqueId);
      }

      nbt.func_74782_a("pack", this.focusPackage.serialize());
      nbt.func_74757_a("friendly", this.getIsFriendly());
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

   protected boolean func_146066_aG() {
      return false;
   }

   protected boolean func_70814_o() {
      return true;
   }
}

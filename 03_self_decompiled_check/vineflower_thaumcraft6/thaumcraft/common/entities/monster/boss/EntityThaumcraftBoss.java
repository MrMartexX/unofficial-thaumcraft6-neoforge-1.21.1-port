package thaumcraft.common.entities.monster.boss;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.utils.EntityUtils;

public class EntityThaumcraftBoss extends EntityMob {
   protected final BossInfoServer bossInfo = (BossInfoServer)new BossInfoServer(this.func_145748_c_(), Color.PURPLE, Overlay.PROGRESS).func_186741_a(true);
   private static final DataParameter<Integer> AGGRO = EntityDataManager.func_187226_a(EntityThaumcraftBoss.class, DataSerializers.field_187192_b);
   HashMap<Integer, Integer> aggro = new HashMap<>();
   int spawnTimer = 0;

   public EntityThaumcraftBoss(World world) {
      super(world);
      this.field_70728_aV = 50;
   }

   public void func_70037_a(NBTTagCompound nbt) {
      super.func_70037_a(nbt);
      if (nbt.func_74764_b("HomeD")) {
         this.func_175449_a(new BlockPos(nbt.func_74762_e("HomeX"), nbt.func_74762_e("HomeY"), nbt.func_74762_e("HomeZ")), nbt.func_74762_e("HomeD"));
      }
   }

   public void func_70014_b(NBTTagCompound nbt) {
      super.func_70014_b(nbt);
      if (this.func_180486_cf() != null && this.func_110174_bM() > 0.0F) {
         nbt.func_74768_a("HomeD", (int)this.func_110174_bM());
         nbt.func_74768_a("HomeX", this.func_180486_cf().func_177958_n());
         nbt.func_74768_a("HomeY", this.func_180486_cf().func_177956_o());
         nbt.func_74768_a("HomeZ", this.func_180486_cf().func_177952_p());
      }
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111266_c).func_111128_a(0.95);
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(40.0);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.func_184212_Q().func_187214_a(AGGRO, 0);
   }

   protected void func_70619_bc() {
      if (this.getSpawnTimer() == 0) {
         super.func_70619_bc();
      }

      if (this.func_70638_az() != null && this.func_70638_az().field_70128_L) {
         this.func_70624_b(null);
      }

      this.bossInfo.func_186735_a(this.func_110143_aJ() / this.func_110138_aP());
   }

   public void func_184203_c(EntityPlayerMP player) {
      super.func_184203_c(player);
      this.bossInfo.func_186761_b(player);
   }

   public void func_184178_b(EntityPlayerMP player) {
      super.func_184178_b(player);
      this.bossInfo.func_186760_a(player);
   }

   public boolean func_184222_aU() {
      return false;
   }

   public IEntityLivingData func_180482_a(DifficultyInstance diff, IEntityLivingData data) {
      this.func_175449_a(this.func_180425_c(), 24);
      this.generateName();
      this.bossInfo.func_186739_a(this.func_145748_c_());
      return data;
   }

   public int getAnger() {
      return (Integer)this.func_184212_Q().func_187225_a(AGGRO);
   }

   public void setAnger(int par1) {
      this.func_184212_Q().func_187227_b(AGGRO, par1);
   }

   public int getSpawnTimer() {
      return this.spawnTimer;
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.getSpawnTimer() > 0) {
         this.spawnTimer--;
      }

      if (this.getAnger() > 0) {
         this.setAnger(this.getAnger() - 1);
      }

      if (this.field_70170_p.field_72995_K && this.field_70146_Z.nextInt(15) == 0 && this.getAnger() > 0) {
         double d0 = this.field_70146_Z.nextGaussian() * 0.02;
         double d1 = this.field_70146_Z.nextGaussian() * 0.02;
         double d2 = this.field_70146_Z.nextGaussian() * 0.02;
         this.field_70170_p
            .func_175688_a(
               EnumParticleTypes.VILLAGER_ANGRY,
               this.field_70165_t + this.field_70146_Z.nextFloat() * this.field_70130_N - this.field_70130_N / 2.0,
               this.func_174813_aQ().field_72338_b + this.field_70131_O + this.field_70146_Z.nextFloat() * 0.5,
               this.field_70161_v + this.field_70146_Z.nextFloat() * this.field_70130_N - this.field_70130_N / 2.0,
               d0,
               d1,
               d2,
               new int[0]
            );
      }

      if (!this.field_70170_p.field_72995_K) {
         if (this.field_70173_aa % 30 == 0) {
            this.func_70691_i(1.0F);
         }

         if (this.func_70638_az() != null && this.field_70173_aa % 20 == 0) {
            ArrayList<Integer> dl = new ArrayList<>();
            int players = 0;
            int hei = this.func_70638_az().func_145782_y();
            int ad = this.aggro.containsKey(hei) ? this.aggro.get(hei) : 0;
            int ld = ad;
            Entity newTarget = null;

            for (Integer ei : this.aggro.keySet()) {
               int ca = this.aggro.get(ei);
               if (ca > ad + 25 && ca > ad * 1.1 && ca > ld) {
                  newTarget = this.field_70170_p.func_73045_a(hei);
                  if (newTarget != null && !newTarget.field_70128_L && !(this.func_70068_e(newTarget) > 16384.0)) {
                     hei = ei;
                     ld = ei;
                     if (newTarget instanceof EntityPlayer) {
                        players++;
                     }
                  } else {
                     dl.add(ei);
                  }
               }
            }

            for (Integer ei : dl) {
               this.aggro.remove(ei);
            }

            if (newTarget != null && hei != this.func_70638_az().func_145782_y()) {
               this.func_70624_b((EntityLivingBase)newTarget);
            }

            float om = this.func_110138_aP();
            IAttributeInstance iattributeinstance = this.func_110148_a(SharedMonsterAttributes.field_111267_a);
            IAttributeInstance iattributeinstance2 = this.func_110148_a(SharedMonsterAttributes.field_111264_e);

            for (int a = 0; a < 5; a++) {
               iattributeinstance2.func_111124_b(EntityUtils.DMGBUFF[a]);
               iattributeinstance.func_111124_b(EntityUtils.HPBUFF[a]);
            }

            for (int a = 0; a < Math.min(5, players - 1); a++) {
               iattributeinstance.func_111121_a(EntityUtils.HPBUFF[a]);
               iattributeinstance2.func_111121_a(EntityUtils.DMGBUFF[a]);
            }

            double mm = this.func_110138_aP() / om;
            this.func_70606_j((float)(this.func_110143_aJ() * mm));
         }
      }
   }

   public boolean func_180431_b(DamageSource ds) {
      return super.func_180431_b(ds) || this.getSpawnTimer() > 0;
   }

   public boolean func_70648_aU() {
      return true;
   }

   public boolean func_70104_M() {
      return super.func_70104_M() && !this.func_180431_b(DamageSource.field_76366_f);
   }

   protected int func_70682_h(int air) {
      return air;
   }

   public void func_70110_aj() {
   }

   public boolean func_98052_bS() {
      return false;
   }

   protected void func_180483_b(DifficultyInstance diff) {
   }

   protected boolean func_70692_ba() {
      return false;
   }

   public boolean func_184191_r(Entity el) {
      return el instanceof IEldritchMob;
   }

   protected void func_70628_a(boolean flag, int fortune) {
      EntityUtils.entityDropSpecialItem(this, new ItemStack(ItemsTC.primordialPearl), this.field_70131_O / 2.0F);
      this.func_70099_a(new ItemStack(ItemsTC.lootBag, 1, 2), 1.5F);
   }

   public boolean func_70097_a(DamageSource source, float damage) {
      if (!this.field_70170_p.field_72995_K) {
         if (source.func_76346_g() != null && source.func_76346_g() instanceof EntityLivingBase) {
            int target = source.func_76346_g().func_145782_y();
            int ad = (int)damage;
            if (this.aggro.containsKey(target)) {
               ad += this.aggro.get(target);
            }

            this.aggro.put(target, ad);
         }

         if (damage > 35.0F) {
            if (this.getAnger() == 0) {
               try {
                  try {
                     this.func_70690_d(new PotionEffect(MobEffects.field_76428_l, 200, (int)(damage / 15.0F)));
                     this.func_70690_d(new PotionEffect(MobEffects.field_76420_g, 200, (int)(damage / 10.0F)));
                     this.func_70690_d(new PotionEffect(MobEffects.field_76422_e, 200, (int)(damage / 40.0F)));
                  } catch (Exception var5) {
                  }

                  this.setAnger(200);
               } catch (Exception var6) {
               }

               if (source.func_76346_g() != null && source.func_76346_g() instanceof EntityPlayer) {
                  ((EntityPlayer)source.func_76346_g())
                     .func_146105_b(new TextComponentTranslation(this.func_70005_c_() + " " + I18n.func_74838_a("tc.boss.enrage"), new Object[0]), true);
               }
            }

            damage = 35.0F;
         }
      }

      return super.func_70097_a(source, damage);
   }

   public void generateName() {
   }
}

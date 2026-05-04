package thaumcraft.common.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMediumRoot;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.world.taint.TaintHelper;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeedPrime;
import thaumcraft.common.items.casters.foci.FocusEffectFlux;
import thaumcraft.common.items.casters.foci.FocusMediumCloud;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockBamf;
import thaumcraft.common.lib.potions.PotionInfectiousVisExhaust;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.RandomItemChooser;
import thaumcraft.common.world.aura.AuraHandler;

public class EntityFluxRift extends Entity {
   private static final DataParameter<Integer> SEED = EntityDataManager.func_187226_a(EntityFluxRift.class, DataSerializers.field_187192_b);
   private static final DataParameter<Integer> SIZE = EntityDataManager.func_187226_a(EntityFluxRift.class, DataSerializers.field_187192_b);
   private static final DataParameter<Float> STABILITY = EntityDataManager.func_187226_a(EntityFluxRift.class, DataSerializers.field_187193_c);
   private static final DataParameter<Boolean> COLLAPSE = EntityDataManager.func_187226_a(EntityFluxRift.class, DataSerializers.field_187198_h);
   int maxSize = 0;
   int lastSize = -1;
   static ArrayList<RandomItemChooser.Item> events = new ArrayList<>();
   public ArrayList<Vec3d> points = new ArrayList<>();
   public ArrayList<Float> pointsWidth = new ArrayList<>();

   public EntityFluxRift(World par1World) {
      super(par1World);
      this.func_70105_a(2.0F, 2.0F);
   }

   protected void func_70088_a() {
      this.func_184212_Q().func_187214_a(SEED, 0);
      this.func_184212_Q().func_187214_a(SIZE, 5);
      this.func_184212_Q().func_187214_a(STABILITY, 0.0F);
      this.func_184212_Q().func_187214_a(COLLAPSE, false);
   }

   public boolean getCollapse() {
      return (Boolean)this.func_184212_Q().func_187225_a(COLLAPSE);
   }

   public void setCollapse(boolean b) {
      if (b) {
         this.maxSize = this.getRiftSize();
      }

      this.func_184212_Q().func_187227_b(COLLAPSE, b);
   }

   public float getRiftStability() {
      return (Float)this.func_184212_Q().func_187225_a(STABILITY);
   }

   public void setRiftStability(float s) {
      if (s > 100.0F) {
         s = 100.0F;
      }

      if (s < -100.0F) {
         s = -100.0F;
      }

      this.func_184212_Q().func_187227_b(STABILITY, s);
   }

   public int getRiftSize() {
      return (Integer)this.func_184212_Q().func_187225_a(SIZE);
   }

   public void setRiftSize(int s) {
      this.func_184212_Q().func_187227_b(SIZE, s);
      this.setSize();
   }

   public double func_70033_W() {
      return -this.field_70131_O / 2.0F;
   }

   protected void setSize() {
      this.calcSteps(this.points, this.pointsWidth, new Random(this.getRiftSeed()));
      this.lastSize = this.getRiftSize();
      double x0 = Double.MAX_VALUE;
      double y0 = Double.MAX_VALUE;
      double z0 = Double.MAX_VALUE;
      double x1 = Double.MIN_VALUE;
      double y1 = Double.MIN_VALUE;
      double z1 = Double.MIN_VALUE;

      for (Vec3d v : this.points) {
         if (v.field_72450_a < x0) {
            x0 = v.field_72450_a;
         }

         if (v.field_72450_a > x1) {
            x1 = v.field_72450_a;
         }

         if (v.field_72448_b < y0) {
            y0 = v.field_72448_b;
         }

         if (v.field_72448_b > y1) {
            y1 = v.field_72448_b;
         }

         if (v.field_72449_c < z0) {
            z0 = v.field_72449_c;
         }

         if (v.field_72449_c > z1) {
            z1 = v.field_72449_c;
         }
      }

      this.func_174826_a(
         new AxisAlignedBB(
            this.field_70165_t + x0,
            this.field_70163_u + y0,
            this.field_70161_v + z0,
            this.field_70165_t + x1,
            this.field_70163_u + y1,
            this.field_70161_v + z1
         )
      );
      this.field_70130_N = Math.abs((float)Math.max(x1 - x0, z1 - z0));
      this.field_70131_O = Math.abs((float)(y1 - y0));
   }

   public void func_70107_b(double x, double y, double z) {
      this.field_70165_t = x;
      this.field_70163_u = y;
      this.field_70161_v = z;
      if (this.func_184212_Q() != null) {
         this.setSize();
      } else {
         super.func_70107_b(x, y, z);
      }
   }

   public int getRiftSeed() {
      return (Integer)this.func_184212_Q().func_187225_a(SEED);
   }

   public void setRiftSeed(int s) {
      this.func_184212_Q().func_187227_b(SEED, s);
   }

   public void func_70014_b(NBTTagCompound nbttagcompound) {
      nbttagcompound.func_74768_a("MaxSize", this.maxSize);
      nbttagcompound.func_74768_a("RiftSize", this.getRiftSize());
      nbttagcompound.func_74768_a("RiftSeed", this.getRiftSeed());
      nbttagcompound.func_74776_a("Stability", this.getRiftStability());
      nbttagcompound.func_74757_a("collapse", this.getCollapse());
   }

   public void func_70037_a(NBTTagCompound nbttagcompound) {
      this.maxSize = nbttagcompound.func_74762_e("MaxSize");
      this.setRiftSize(nbttagcompound.func_74762_e("RiftSize"));
      this.setRiftSeed(nbttagcompound.func_74762_e("RiftSeed"));
      this.setRiftStability(nbttagcompound.func_74762_e("Stability"));
      this.setCollapse(nbttagcompound.func_74767_n("collapse"));
   }

   public void func_70091_d(MoverType type, double x, double y, double z) {
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.lastSize != this.getRiftSize()) {
         this.setSize();
      }

      if (!this.field_70170_p.field_72995_K) {
         if (this.getRiftSeed() == 0) {
            this.setRiftSeed(this.field_70146_Z.nextInt());
         }

         if (!this.points.isEmpty()) {
            int pi = this.field_70146_Z.nextInt(this.points.size() - 1);
            Vec3d v1 = this.points.get(pi).func_72441_c(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            Vec3d v2 = this.points.get(pi + 1).func_72441_c(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            RayTraceResult rt = this.field_70170_p.func_72901_a(v1, v2, false);
            if (rt != null && rt.func_178782_a() != null) {
               BlockPos p = new BlockPos(rt.func_178782_a());
               IBlockState bs = this.field_70170_p.func_180495_p(p);
               if (!this.field_70170_p.func_175623_d(p) && bs.func_185887_b(this.field_70170_p, p) >= 0.0F && bs.func_177230_c().func_176209_a(bs, false)) {
                  this.field_70170_p.func_180498_a(null, 2001, p, Block.func_176210_f(this.field_70170_p.func_180495_p(p)));
                  this.field_70170_p.func_175698_g(p);
               }
            }

            for (Entity e : EntityUtils.getEntitiesInRange(this.func_130014_f_(), v1.field_72450_a, v1.field_72448_b, v1.field_72449_c, this, Entity.class, 0.5)) {
               if (!e.field_70128_L && (!(e instanceof EntityPlayer) || !((EntityPlayer)e).func_184812_l_())) {
                  try {
                     e.func_70097_a(DamageSource.field_76380_i, 2.0F);
                     if (e instanceof EntityItem) {
                        e.func_70106_y();
                     }
                  } catch (Exception var9) {
                  }
               }
            }
         }

         if (this.points.size() < 3 && !this.getCollapse()) {
            this.setCollapse(true);
         }

         if (this.getCollapse()) {
            this.setRiftSize(this.getRiftSize() - 1);
            if (this.field_70146_Z.nextBoolean()) {
               AuraHelper.addVis(this.field_70170_p, this.func_180425_c(), 1.0F);
            } else {
               AuraHelper.polluteAura(this.field_70170_p, this.func_180425_c(), 1.0F, false);
            }

            if (this.field_70146_Z.nextInt(10) == 0) {
               this.field_70170_p
                  .func_72876_a(
                     this,
                     this.field_70165_t + this.field_70146_Z.nextGaussian() * 2.0,
                     this.field_70163_u + this.field_70146_Z.nextGaussian() * 2.0,
                     this.field_70161_v + this.field_70146_Z.nextGaussian() * 2.0,
                     this.field_70146_Z.nextFloat() / 2.0F,
                     false
                  );
            }

            if (this.getRiftSize() <= 1) {
               this.completeCollapse();
               return;
            }
         }

         if (this.field_70173_aa % 120 == 0) {
            this.setRiftStability(this.getRiftStability() - 0.2F);
         }

         if (this.field_70173_aa % 600 == this.func_145782_y() % 600) {
            float taint = AuraHandler.getFlux(this.field_70170_p, this.func_180425_c());
            double size = Math.sqrt(this.getRiftSize() * 2);
            if (taint >= size && this.getRiftSize() < 100 && this.getStability() != EntityFluxRift.EnumStability.VERY_STABLE) {
               AuraHandler.drainFlux(this.func_130014_f_(), this.func_180425_c(), (float)size, false);
               this.setRiftSize(this.getRiftSize() + 1);
            }

            if (this.getRiftStability() < 0.0F && this.field_70146_Z.nextInt(1000) < Math.abs(this.getRiftStability()) + this.getRiftSize()) {
               this.executeRiftEvent();
            }
         }

         if (!this.field_70128_L && this.field_70173_aa % 300 == 0) {
            this.func_184185_a(
               SoundsTC.evilportal, (float)(0.15F + this.field_70146_Z.nextGaussian() * 0.066), (float)(0.75 + this.field_70146_Z.nextGaussian() * 0.1)
            );
         }
      } else {
         if (!this.points.isEmpty()
            && this.points.size() > 2
            && !this.getCollapse()
            && this.getRiftStability() < 0.0F
            && this.field_70146_Z.nextInt(150) < Math.abs(this.getRiftStability())) {
            int pi = 1 + this.field_70146_Z.nextInt(this.points.size() - 2);
            Vec3d v1 = this.points.get(pi).func_72441_c(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            FXDispatcher.INSTANCE
               .drawCurlyWisp(
                  v1.field_72450_a,
                  v1.field_72448_b,
                  v1.field_72449_c,
                  0.0,
                  0.0,
                  0.0,
                  0.1F + this.pointsWidth.get(pi) * 3.0F,
                  1.0F,
                  1.0F,
                  1.0F,
                  0.25F,
                  null,
                  1,
                  0,
                  0
               );
         }

         if (!this.points.isEmpty() && this.points.size() > 2 && this.getCollapse()) {
            int pi = 1 + this.field_70146_Z.nextInt(this.points.size() - 2);
            Vec3d v1 = this.points.get(pi).func_72441_c(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            FXDispatcher.INSTANCE
               .drawCurlyWisp(
                  v1.field_72450_a,
                  v1.field_72448_b,
                  v1.field_72449_c,
                  0.0,
                  0.0,
                  0.0,
                  0.1F + this.pointsWidth.get(pi) * 3.0F,
                  1.0F,
                  0.3F + this.field_70146_Z.nextFloat() * 0.1F,
                  0.3F + this.field_70146_Z.nextFloat() * 0.1F,
                  0.4F,
                  null,
                  1,
                  0,
                  0
               );
         }
      }
   }

   public static void createRift(World world, BlockPos pos) {
      pos = pos.func_177982_a(world.field_73012_v.nextInt(16), 0, world.field_73012_v.nextInt(16));
      BlockPos p2 = world.func_175725_q(pos);
      if (!world.field_73011_w.func_191066_m()) {
         for (p2 = new BlockPos(p2.func_177958_n(), 10, p2.func_177952_p());
            !world.func_175623_d(p2);
            p2 = p2.func_177981_b(world.field_73012_v.nextInt(5) + 1)
         ) {
            if (p2.func_177956_o() > world.func_72940_L() - 5) {
               return;
            }
         }
      }

      if (p2.func_177956_o() < world.func_72940_L() - 4) {
         if (EntityUtils.getEntitiesInRange(world, p2, null, EntityFluxRift.class, 32.0).size() > 0) {
            return;
         }

         EntityFluxRift rift = new EntityFluxRift(world);
         rift.setRiftSeed(world.field_73012_v.nextInt());
         rift.func_70012_b(p2.func_177958_n() + 0.5, p2.func_177956_o() + 0.5, p2.func_177952_p() + 0.5, world.field_73012_v.nextInt(360), 0.0F);
         float taint = AuraHandler.getFlux(world, p2);
         double size = Math.sqrt(taint * 3.0F);
         if (size > 5.0 && world.func_72838_d(rift)) {
            rift.setRiftSize((int)size);
            AuraHandler.drainFlux(world, p2, (float)size, false);
            List<EntityPlayer> targets2 = world.func_72872_a(
               EntityPlayer.class,
               new AxisAlignedBB(
                     p2.func_177958_n(), p2.func_177956_o(), p2.func_177952_p(), p2.func_177958_n() + 1, p2.func_177956_o() + 1, p2.func_177952_p() + 1
                  )
                  .func_72314_b(32.0, 32.0, 32.0)
            );
            if (targets2 != null && targets2.size() > 0) {
               for (EntityPlayer target : targets2) {
                  IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(target);
                  if (!knowledge.isResearchKnown("f_toomuchflux")) {
                     target.func_146105_b(new TextComponentString("§5§o" + I18n.func_74838_a("tc.fluxevent.3")), true);
                     ThaumcraftApi.internalMethods.completeResearch(target, "f_toomuchflux");
                  }
               }
            }
         }
      }
   }

   private void executeRiftEvent() {
      RandomItemChooser ric = new RandomItemChooser();
      EntityFluxRift.FluxEventEntry ei = (EntityFluxRift.FluxEventEntry)ric.chooseOnWeight(events);
      if (ei != null) {
         if (ei.nearTaintAllowed || !TaintHelper.isNearTaintSeed(this.field_70170_p, this.func_180425_c())) {
            boolean didit = false;
            switch (ei.event) {
               case 0:
                  EntityWisp wisp = new EntityWisp(this.field_70170_p);
                  wisp.func_70012_b(
                     this.field_70165_t + this.field_70146_Z.nextGaussian() * 5.0,
                     this.field_70163_u + this.field_70146_Z.nextGaussian() * 5.0,
                     this.field_70161_v + this.field_70146_Z.nextGaussian() * 5.0,
                     0.0F,
                     0.0F
                  );
                  if (this.field_70170_p.field_73012_v.nextInt(5) == 0) {
                     wisp.setType(Aspect.FLUX.getTag());
                  }

                  if (wisp.func_70601_bi() && this.field_70170_p.func_72838_d(wisp)) {
                     didit = true;
                  }
                  break;
               case 1:
                  EntityTaintSeedPrime seed = new EntityTaintSeedPrime(this.field_70170_p);
                  seed.func_70012_b(
                     (int)(this.field_70165_t + this.field_70146_Z.nextGaussian() * 5.0) + 0.5,
                     (int)(this.field_70163_u + this.field_70146_Z.nextGaussian() * 5.0),
                     (int)(this.field_70161_v + this.field_70146_Z.nextGaussian() * 5.0) + 0.5,
                     this.field_70170_p.field_73012_v.nextInt(360),
                     0.0F
                  );
                  if (seed.func_70601_bi() && this.field_70170_p.func_72838_d(seed)) {
                     didit = true;
                     seed.boost = this.getRiftSize();
                     AuraHelper.polluteAura(this.func_130014_f_(), this.func_180425_c(), this.getRiftSize() / 2, true);
                     this.func_70106_y();
                  }
                  break;
               case 2:
                  List<EntityLivingBase> targets2 = this.field_70170_p
                     .func_72872_a(EntityLivingBase.class, this.func_174813_aQ().func_72314_b(16.0, 16.0, 16.0));
                  if (targets2 != null && targets2.size() > 0) {
                     for (EntityLivingBase targetx : targets2) {
                        didit = true;
                        if (targetx instanceof EntityPlayer) {
                           ((EntityPlayer)targetx).func_146105_b(new TextComponentString("§5§o" + I18n.func_74838_a("tc.fluxevent.2")), true);
                        }

                        PotionEffect pe = new PotionEffect(PotionInfectiousVisExhaust.instance, 3000, 2);
                        pe.getCurativeItems().clear();

                        try {
                           targetx.func_70690_d(pe);
                        } catch (Exception var11) {
                        }
                     }
                  }
                  break;
               case 3:
                  EntityPlayer target = this.field_70170_p.func_72890_a(this, 16.0);
                  if (target != null) {
                     FocusPackage p = new FocusPackage(target);
                     FocusMediumRoot root = new FocusMediumRoot();
                     root.setupFromCasterToTarget(target, target, 0.5);
                     p.addNode(root);
                     FocusMediumCloud fp = new FocusMediumCloud();
                     fp.initialize();
                     fp.getSetting("radius").setValue(MathHelper.func_76136_a(this.field_70146_Z, 1, 3));
                     fp.getSetting("duration")
                        .setValue(MathHelper.func_76136_a(this.field_70146_Z, Math.min(this.getRiftSize() / 2, 30), Math.min(this.getRiftSize(), 120)));
                     p.addNode(fp);
                     p.addNode(new FocusEffectFlux());
                     FocusEngine.castFocusPackage(target, p, true);
                  }
                  break;
               case 4:
                  this.setCollapse(true);
            }

            if (didit) {
               this.setRiftStability(this.getRiftStability() + ei.cost);
            }
         }
      }
   }

   private void calcSteps(ArrayList<Vec3d> pp, ArrayList<Float> ww, Random rr) {
      pp.clear();
      ww.clear();
      Vec3d right = new Vec3d(rr.nextGaussian(), rr.nextGaussian(), rr.nextGaussian()).func_72432_b();
      Vec3d left = right.func_186678_a(-1.0);
      Vec3d lr = new Vec3d(0.0, 0.0, 0.0);
      Vec3d ll = new Vec3d(0.0, 0.0, 0.0);
      int steps = MathHelper.func_76123_f(this.getRiftSize() / 3.0F);
      float girth = this.getRiftSize() / 300.0F;
      double angle = 0.33;
      float dec = girth / steps;

      for (int a = 0; a < steps; a++) {
         girth -= dec;
         Vec3d var14 = right.func_178789_a((float)(rr.nextGaussian() * angle));
         right = var14.func_178785_b((float)(rr.nextGaussian() * angle));
         lr = lr.func_178787_e(right.func_186678_a(0.2));
         pp.add(new Vec3d(lr.field_72450_a, lr.field_72448_b, lr.field_72449_c));
         ww.add(girth);
         Vec3d var15 = left.func_178789_a((float)(rr.nextGaussian() * angle));
         left = var15.func_178785_b((float)(rr.nextGaussian() * angle));
         ll = ll.func_178787_e(left.func_186678_a(0.2));
         pp.add(0, new Vec3d(ll.field_72450_a, ll.field_72448_b, ll.field_72449_c));
         ww.add(0, girth);
      }

      lr = lr.func_178787_e(right.func_186678_a(0.1));
      pp.add(new Vec3d(lr.field_72450_a, lr.field_72448_b, lr.field_72449_c));
      ww.add(0.0F);
      ll = ll.func_178787_e(left.func_186678_a(0.1));
      pp.add(0, new Vec3d(ll.field_72450_a, ll.field_72448_b, ll.field_72449_c));
      ww.add(0, 0.0F);
   }

   public void addStability() {
      this.setRiftStability(this.getRiftStability() + 0.125F);
   }

   public EntityFluxRift.EnumStability getStability() {
      return this.getRiftStability() > 50.0F
         ? EntityFluxRift.EnumStability.VERY_STABLE
         : (
            this.getRiftStability() >= 0.0F
               ? EntityFluxRift.EnumStability.STABLE
               : (this.getRiftStability() > -25.0F ? EntityFluxRift.EnumStability.UNSTABLE : EntityFluxRift.EnumStability.VERY_UNSTABLE)
         );
   }

   public void func_70015_d(int seconds) {
   }

   public boolean func_70027_ad() {
      return false;
   }

   public boolean func_90999_ad() {
      return false;
   }

   private void completeCollapse() {
      int qq = (int)Math.sqrt(this.maxSize);
      if (this.field_70146_Z.nextInt(100) < qq) {
         this.func_70099_a(new ItemStack(ItemsTC.primordialPearl, 1, 4 + this.field_70146_Z.nextInt(4)), 0.0F);
      }

      for (int a = 0; a < qq; a++) {
         this.func_70099_a(new ItemStack(ItemsTC.voidSeed), 0.0F);
      }

      label62: {
         PacketHandler.INSTANCE
            .sendToAllAround(
               new PacketFXBlockBamf(this.field_70165_t, this.field_70163_u, this.field_70161_v, 0, true, true, null),
               new TargetPoint(this.field_70170_p.field_73011_w.getDimension(), this.field_70165_t, this.field_70163_u, this.field_70161_v, 64.0)
            );
         List<EntityLivingBase> list = EntityUtils.getEntitiesInRange(
            this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, this, EntityLivingBase.class, 32.0
         );
         switch (this.getStability()) {
            case VERY_UNSTABLE:
               for (EntityLivingBase p : list) {
                  int w = (int)((1.0 - p.func_70068_e(this) / 32.0) * 120.0);
                  if (w > 0) {
                     p.func_70690_d(new PotionEffect(PotionFluxTaint.instance, w * 20, 0));
                  }
               }
            case UNSTABLE:
               for (EntityLivingBase p : list) {
                  int w = (int)((1.0 - p.func_70068_e(this) / 32.0) * 300.0);
                  if (w > 0) {
                     p.func_70690_d(new PotionEffect(MobEffects.field_76437_t, w * 20, 0));
                  }
               }
            case STABLE:
               break;
            default:
               break label62;
         }

         for (EntityLivingBase p : list) {
            if (p instanceof EntityPlayer) {
               int w = (int)((1.0 - p.func_70068_e(this) / 32.0) * 25.0);
               if (w > 0) {
                  ThaumcraftApi.internalMethods.addWarpToPlayer((EntityPlayer)p, w, IPlayerWarp.EnumWarpType.NORMAL);
                  ThaumcraftApi.internalMethods.addWarpToPlayer((EntityPlayer)p, w, IPlayerWarp.EnumWarpType.TEMPORARY);
               }
            }
         }
      }

      this.func_70106_y();
   }

   static {
      events.add(new EntityFluxRift.FluxEventEntry(0, 50, 5, true));
      events.add(new EntityFluxRift.FluxEventEntry(1, 10, 0, false));
      events.add(new EntityFluxRift.FluxEventEntry(2, 20, 10, true));
      events.add(new EntityFluxRift.FluxEventEntry(3, 20, 10, true));
      events.add(new EntityFluxRift.FluxEventEntry(4, 1, 0, true));
   }

   public enum EnumStability {
      VERY_STABLE,
      STABLE,
      UNSTABLE,
      VERY_UNSTABLE;
   }

   static class FluxEventEntry implements RandomItemChooser.Item {
      int weight;
      int event;
      int cost;
      boolean nearTaintAllowed;

      protected FluxEventEntry(int event, int weight, int cost, boolean nearTaintAllowed) {
         this.weight = weight;
         this.event = event;
         this.cost = cost;
         this.nearTaintAllowed = nearTaintAllowed;
      }

      @Override
      public double getWeight() {
         return this.weight;
      }
   }
}

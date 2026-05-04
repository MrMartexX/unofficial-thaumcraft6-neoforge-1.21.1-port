package thaumcraft.common.entities.monster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMediumRoot;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.ai.pech.AINearestAttackableTargetPech;
import thaumcraft.common.entities.ai.pech.AIPechItemEntityGoto;
import thaumcraft.common.entities.ai.pech.AIPechTradePlayer;
import thaumcraft.common.items.casters.foci.FocusEffectAir;
import thaumcraft.common.items.casters.foci.FocusEffectCurse;
import thaumcraft.common.items.casters.foci.FocusEffectEarth;
import thaumcraft.common.items.casters.foci.FocusEffectFire;
import thaumcraft.common.items.casters.foci.FocusEffectFlux;
import thaumcraft.common.items.casters.foci.FocusMediumProjectile;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.world.biomes.BiomeHandler;

public class EntityPech extends EntityMob implements IRangedAttackMob {
   public NonNullList<ItemStack> loot = NonNullList.func_191197_a(9, ItemStack.field_190927_a);
   public boolean trading = false;
   private final EntityAIAttackRanged aiArrowAttack = new EntityAIAttackRanged(this, 0.6, 20, 50, 15.0F);
   private final EntityAIAttackRanged aiBlastAttack = new EntityAIAttackRanged(this, 0.6, 20, 50, 15.0F);
   private final EntityAIAttackMelee aiMeleeAttack = new EntityAIAttackMelee(this, 0.6, false);
   private final EntityAIAvoidEntity aiAvoidPlayer = new EntityAIAvoidEntity(this, EntityPlayer.class, 8.0F, 0.5, 0.6);
   private static final DataParameter<Byte> TYPE = EntityDataManager.func_187226_a(EntityPech.class, DataSerializers.field_187191_a);
   private static final DataParameter<Integer> ANGER = EntityDataManager.func_187226_a(EntityPech.class, DataSerializers.field_187192_b);
   private static final DataParameter<Boolean> TAMED = EntityDataManager.func_187226_a(EntityPech.class, DataSerializers.field_187198_h);
   public static final ResourceLocation LOOT = LootTableList.func_186375_a(new ResourceLocation("thaumcraft", "pech"));
   public float mumble = 0.0F;
   int chargecount = 0;
   public static HashMap<Integer, Integer> valuedItems = new HashMap<>();
   public static HashMap<Integer, ArrayList<List>> tradeInventory = new HashMap<>();

   public EntityPech(World world) {
      super(world);
      this.func_70105_a(0.6F, 1.8F);
      this.field_70728_aV = 8;
   }

   public String func_70005_c_() {
      if (this.func_145818_k_()) {
         return this.func_95999_t();
      }

      switch (this.getPechType()) {
         case 0:
            return I18n.func_74838_a("entity.Pech.name");
         case 1:
            return I18n.func_74838_a("entity.Pech.1.name");
         case 2:
            return I18n.func_74838_a("entity.Pech.2.name");
         default:
            return I18n.func_74838_a("entity.Pech.name");
      }
   }

   protected void func_184651_r() {
      ((PathNavigateGround)this.func_70661_as()).func_179693_d(false);
      this.func_184642_a(EntityEquipmentSlot.MAINHAND, 0.2F);
      this.func_184642_a(EntityEquipmentSlot.OFFHAND, 0.2F);
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(1, new AIPechTradePlayer(this));
      this.field_70714_bg.func_75776_a(3, new AIPechItemEntityGoto(this));
      this.field_70714_bg.func_75776_a(5, new EntityAIOpenDoor(this, true));
      this.field_70714_bg.func_75776_a(6, new EntityAIMoveTowardsRestriction(this, 0.5));
      this.field_70714_bg.func_75776_a(6, new EntityAIMoveThroughVillage(this, 1.0, false));
      this.field_70714_bg.func_75776_a(9, new EntityAIWander(this, 0.6));
      this.field_70714_bg.func_75776_a(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
      this.field_70714_bg.func_75776_a(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
      this.field_70714_bg.func_75776_a(11, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.field_70715_bh.func_75776_a(2, new AINearestAttackableTargetPech(this, EntityPlayer.class, true));
   }

   public void setCombatTask() {
      if (this.field_70170_p != null && !this.field_70170_p.field_72995_K) {
         this.field_70714_bg.func_85156_a(this.aiMeleeAttack);
         this.field_70714_bg.func_85156_a(this.aiArrowAttack);
         this.field_70714_bg.func_85156_a(this.aiBlastAttack);
         ItemStack itemstack = this.func_184614_ca();
         if (itemstack.func_77973_b() == Items.field_151031_f) {
            this.field_70714_bg.func_75776_a(2, this.aiArrowAttack);
         } else if (itemstack.func_77973_b() == ItemsTC.pechWand) {
            this.field_70714_bg.func_75776_a(2, this.aiBlastAttack);
         } else {
            this.field_70714_bg.func_75776_a(2, this.aiMeleeAttack);
         }

         if (this.isTamed()) {
            this.field_70714_bg.func_85156_a(this.aiAvoidPlayer);
         } else {
            this.field_70714_bg.func_75776_a(4, this.aiAvoidPlayer);
         }
      }
   }

   public void func_82196_d(EntityLivingBase target, float f) {
      if (this.getPechType() == 2) {
         EntityTippedArrow entityarrow = new EntityTippedArrow(this.field_70170_p, this);
         if (this.field_70170_p.field_73012_v.nextFloat() < 0.2) {
            ItemStack itemstack = new ItemStack(Items.field_185167_i);
            PotionUtils.func_185184_a(itemstack, Collections.singletonList(new PotionEffect(MobEffects.field_76436_u, 40)));
            entityarrow.func_184555_a(itemstack);
         }

         double d0 = target.field_70165_t - this.field_70165_t;
         double d1 = target.func_174813_aQ().field_72338_b + target.field_70131_O / 3.0F - entityarrow.field_70163_u;
         double d2 = target.field_70161_v - this.field_70161_v;
         double d3 = MathHelper.func_76133_a(d0 * d0 + d2 * d2);
         entityarrow.func_70186_c(d0, d1 + d3 * 0.2F, d2, 1.6F, 14 - this.field_70170_p.func_175659_aa().func_151525_a() * 4);
         int i = EnchantmentHelper.func_185284_a(Enchantments.field_185309_u, this);
         int j = EnchantmentHelper.func_185284_a(Enchantments.field_185310_v, this);
         entityarrow.func_70239_b(f * 2.0F + this.field_70146_Z.nextGaussian() * 0.25 + this.field_70170_p.func_175659_aa().func_151525_a() * 0.11F);
         if (i > 0) {
            entityarrow.func_70239_b(entityarrow.func_70242_d() + i * 0.5 + 0.5);
         }

         if (j > 0) {
            entityarrow.func_70240_a(j);
         }

         if (EnchantmentHelper.func_185284_a(Enchantments.field_185311_w, this) > 0) {
            entityarrow.func_70015_d(100);
         }

         this.func_184185_a(SoundEvents.field_187737_v, 1.0F, 1.0F / (this.func_70681_au().nextFloat() * 0.4F + 0.8F));
         this.field_70170_p.func_72838_d(entityarrow);
      } else if (this.getPechType() == 1) {
         FocusPackage p = new FocusPackage(this);
         FocusMediumRoot root = new FocusMediumRoot();
         double off = this.func_70032_d(target) / 6.0F;
         root.setupFromCasterToTarget(this, target, off);
         p.addNode(root);
         FocusMediumProjectile fp = new FocusMediumProjectile();
         fp.initialize();
         fp.getSetting("speed").setValue(2);
         p.addNode(fp);
         p.addNode(
            this.field_70146_Z.nextBoolean()
               ? new FocusEffectCurse()
               : (
                  this.field_70146_Z.nextBoolean()
                     ? new FocusEffectFlux()
                     : (
                        this.field_70146_Z.nextBoolean()
                           ? new FocusEffectEarth()
                           : (this.field_70146_Z.nextBoolean() ? new FocusEffectAir() : new FocusEffectFire())
                     )
               )
         );
         FocusEngine.castFocusPackage(this, p, true);
         this.func_184609_a(this.func_184600_cs());
      }
   }

   public void func_184201_a(EntityEquipmentSlot slotIn, @Nullable ItemStack stack) {
      super.func_184201_a(slotIn, stack);
      if (!this.field_70170_p.field_72995_K && slotIn == EntityEquipmentSlot.MAINHAND) {
         this.setCombatTask();
      }
   }

   protected void func_180481_a(DifficultyInstance difficulty) {
      super.func_180481_a(difficulty);
      switch (this.field_70146_Z.nextInt(20)) {
         case 0:
         case 12:
            this.func_184611_a(this.func_184600_cs(), new ItemStack(ItemsTC.pechWand));
            break;
         case 1:
            this.func_184611_a(this.func_184600_cs(), new ItemStack(Items.field_151052_q));
            break;
         case 2:
         case 4:
         case 10:
         case 11:
         case 13:
            this.func_184611_a(this.func_184600_cs(), new ItemStack(Items.field_151031_f));
            break;
         case 3:
            this.func_184611_a(this.func_184600_cs(), new ItemStack(Items.field_151049_t));
            break;
         case 5:
            this.func_184611_a(this.func_184600_cs(), new ItemStack(Items.field_151040_l));
            break;
         case 6:
            this.func_184611_a(this.func_184600_cs(), new ItemStack(Items.field_151036_c));
            break;
         case 7:
            this.func_184611_a(this.func_184600_cs(), new ItemStack(Items.field_151112_aM));
            break;
         case 8:
            this.func_184611_a(this.func_184600_cs(), new ItemStack(Items.field_151050_s));
            break;
         case 9:
            this.func_184611_a(this.func_184600_cs(), new ItemStack(Items.field_151035_b));
      }
   }

   public IEntityLivingData func_180482_a(DifficultyInstance diff, IEntityLivingData data) {
      this.func_180481_a(diff);
      ItemStack itemstack = this.func_184586_b(this.func_184600_cs());
      if (itemstack.func_77973_b() == ItemsTC.pechWand) {
         this.setPechType(1);
         this.func_184642_a(this.func_184600_cs() == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, 0.1F);
      } else if (!itemstack.func_190926_b()) {
         if (itemstack.func_77973_b() == Items.field_151031_f) {
            this.setPechType(2);
         }

         this.func_180483_b(diff);
      }

      float f = diff.func_180170_c();
      this.func_98053_h(this.field_70146_Z.nextFloat() < 0.75F * f);
      this.setCombatTask();
      return super.func_180482_a(diff, data);
   }

   public boolean func_70601_bi() {
      Biome biome = this.field_70170_p.func_180494_b(new BlockPos(this));
      boolean magicBiome = false;
      if (biome != null) {
         magicBiome = BiomeDictionary.hasType(biome, Type.MAGICAL);
      }

      int count = 0;

      try {
         List l = this.field_70170_p.func_72872_a(EntityPech.class, this.func_174813_aQ().func_72314_b(16.0, 16.0, 16.0));
         if (l != null) {
            count = l.size();
         }
      } catch (Exception var5) {
      }

      if (this.field_70170_p.field_73011_w.getDimension() != ModConfig.CONFIG_WORLD.overworldDim
         && biome != BiomeHandler.MAGICAL_FOREST
         && biome != BiomeHandler.EERIE) {
         magicBiome = false;
      }

      return count < 4 && magicBiome && super.func_70601_bi();
   }

   public float func_70047_e() {
      return this.field_70131_O * 0.66F;
   }

   public void func_70636_d() {
      super.func_70636_d();
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.func_184212_Q().func_187214_a(TYPE, (byte)0);
      this.func_184212_Q().func_187214_a(ANGER, 0);
      this.func_184212_Q().func_187214_a(TAMED, false);
   }

   public int getPechType() {
      return (Byte)this.func_184212_Q().func_187225_a(TYPE);
   }

   public int getAnger() {
      return (Integer)this.func_184212_Q().func_187225_a(ANGER);
   }

   public boolean isTamed() {
      return (Boolean)this.func_184212_Q().func_187225_a(TAMED);
   }

   public void setPechType(int par1) {
      this.func_184212_Q().func_187227_b(TYPE, (byte)par1);
   }

   public void setAnger(int par1) {
      this.func_184212_Q().func_187227_b(ANGER, par1);
   }

   public void setTamed(boolean par1) {
      this.func_184212_Q().func_187227_b(TAMED, par1);
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(30.0);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(6.0);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.5);
   }

   public void func_70014_b(NBTTagCompound nbt) {
      super.func_70014_b(nbt);
      nbt.func_74774_a("PechType", (byte)this.getPechType());
      nbt.func_74777_a("Anger", (short)this.getAnger());
      nbt.func_74757_a("Tamed", this.isTamed());
      ItemStackHelper.func_191282_a(nbt, this.loot);
   }

   public void func_70037_a(NBTTagCompound nbt) {
      super.func_70037_a(nbt);
      if (nbt.func_74764_b("PechType")) {
         byte b0 = nbt.func_74771_c("PechType");
         this.setPechType(b0);
      }

      this.setAnger(nbt.func_74765_d("Anger"));
      this.setTamed(nbt.func_74767_n("Tamed"));
      this.loot = NonNullList.func_191197_a(9, ItemStack.field_190927_a);
      ItemStackHelper.func_191283_b(nbt, this.loot);
      this.setCombatTask();
   }

   protected boolean func_70692_ba() {
      try {
         if (this.loot == null) {
            return true;
         }

         int q = 0;

         for (ItemStack is : this.loot) {
            if (is != null && is.func_190916_E() > 0) {
               q++;
            }
         }

         return q < 5;
      } catch (Exception e) {
         return true;
      }
   }

   public boolean func_184652_a(EntityPlayer player) {
      return false;
   }

   protected ResourceLocation func_184647_J() {
      return LOOT;
   }

   protected void func_184610_a(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
      for (int a = 0; a < this.loot.size(); a++) {
         if (!((ItemStack)this.loot.get(a)).func_190926_b() && this.field_70170_p.field_73012_v.nextFloat() < 0.33F) {
            this.func_70099_a(((ItemStack)this.loot.get(a)).func_77946_l(), 1.5F);
         }
      }

      super.func_184610_a(wasRecentlyHit, lootingModifier, source);
   }

   @SideOnly(Side.CLIENT)
   public void func_70103_a(byte par1) {
      if (par1 == 16) {
         this.mumble = (float) Math.PI;
      } else if (par1 == 17) {
         this.mumble = (float) (Math.PI * 2);
      } else if (par1 == 18) {
         for (int i = 0; i < 5; i++) {
            double d0 = this.field_70146_Z.nextGaussian() * 0.02;
            double d1 = this.field_70146_Z.nextGaussian() * 0.02;
            double d2 = this.field_70146_Z.nextGaussian() * 0.02;
            this.field_70170_p
               .func_175688_a(
                  EnumParticleTypes.VILLAGER_HAPPY,
                  this.field_70165_t + this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F - this.field_70130_N,
                  this.field_70163_u + 0.5 + this.field_70146_Z.nextFloat() * this.field_70131_O,
                  this.field_70161_v + this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F - this.field_70130_N,
                  d0,
                  d1,
                  d2,
                  new int[0]
               );
         }
      }

      if (par1 == 19) {
         for (int i = 0; i < 5; i++) {
            double d0 = this.field_70146_Z.nextGaussian() * 0.02;
            double d1 = this.field_70146_Z.nextGaussian() * 0.02;
            double d2 = this.field_70146_Z.nextGaussian() * 0.02;
            this.field_70170_p
               .func_175688_a(
                  EnumParticleTypes.VILLAGER_ANGRY,
                  this.field_70165_t + this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F - this.field_70130_N,
                  this.field_70163_u + 0.5 + this.field_70146_Z.nextFloat() * this.field_70131_O,
                  this.field_70161_v + this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F - this.field_70130_N,
                  d0,
                  d1,
                  d2,
                  new int[0]
               );
         }

         this.mumble = (float) (Math.PI * 2);
      } else {
         super.func_70103_a(par1);
      }
   }

   public void func_70642_aH() {
      if (!this.field_70170_p.field_72995_K) {
         if (this.field_70146_Z.nextInt(3) == 0) {
            List list = this.field_70170_p.func_72839_b(this, this.func_174813_aQ().func_72314_b(4.0, 2.0, 4.0));

            for (int i = 0; i < list.size(); i++) {
               Entity entity1 = (Entity)list.get(i);
               if (entity1 instanceof EntityPech) {
                  this.field_70170_p.func_72960_a(this, (byte)17);
                  this.func_184185_a(SoundsTC.pech_trade, this.func_70599_aP(), this.func_70647_i());
                  return;
               }
            }
         }

         this.field_70170_p.func_72960_a(this, (byte)16);
      }

      super.func_70642_aH();
   }

   public int func_70627_aG() {
      return 120;
   }

   protected float func_70599_aP() {
      return 0.4F;
   }

   protected SoundEvent func_184639_G() {
      return SoundsTC.pech_idle;
   }

   protected SoundEvent func_184601_bQ(DamageSource damageSourceIn) {
      return SoundsTC.pech_hit;
   }

   protected SoundEvent func_184615_bR() {
      return SoundsTC.pech_death;
   }

   private void becomeAngryAt(Entity entity) {
      if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).func_184812_l_()) {
         if (this.getAnger() <= 0) {
            this.field_70170_p.func_72960_a(this, (byte)19);
            this.func_184185_a(SoundsTC.pech_charge, this.func_70599_aP(), this.func_70647_i());
         }

         this.func_70624_b((EntityLivingBase)entity);
         this.setAnger(400 + this.field_70146_Z.nextInt(400));
         this.setTamed(false);
         this.setCombatTask();
      }
   }

   public int func_70658_aO() {
      int i = super.func_70658_aO() + 2;
      if (i > 20) {
         i = 20;
      }

      return i;
   }

   public boolean func_70097_a(DamageSource damSource, float par2) {
      if (this.func_180431_b(damSource)) {
         return false;
      }

      Entity entity = damSource.func_76346_g();
      if (entity instanceof EntityPlayer) {
         List list = this.field_70170_p.func_72839_b(this, this.func_174813_aQ().func_72314_b(32.0, 16.0, 32.0));

         for (int i = 0; i < list.size(); i++) {
            Entity entity1 = (Entity)list.get(i);
            if (entity1 instanceof EntityPech) {
               EntityPech entitypech = (EntityPech)entity1;
               entitypech.becomeAngryAt(entity);
            }
         }

         this.becomeAngryAt(entity);
      }

      return super.func_70097_a(damSource, par2);
   }

   public void func_70071_h_() {
      if (this.mumble > 0.0F) {
         this.mumble *= 0.75F;
      }

      if (this.getAnger() > 0) {
         this.setAnger(this.getAnger() - 1);
      }

      if (this.getAnger() > 0 && this.func_70638_az() != null) {
         if (this.chargecount > 0) {
            this.chargecount--;
         }

         if (this.chargecount == 0) {
            this.chargecount = 100;
            this.func_184185_a(SoundsTC.pech_charge, this.func_70599_aP(), this.func_70647_i());
         }

         this.field_70170_p.func_72960_a(this, (byte)17);
      }

      if (this.field_70170_p.field_72995_K && this.field_70146_Z.nextInt(15) == 0 && this.getAnger() > 0) {
         double d0 = this.field_70146_Z.nextGaussian() * 0.02;
         double d1 = this.field_70146_Z.nextGaussian() * 0.02;
         double d2 = this.field_70146_Z.nextGaussian() * 0.02;
         this.field_70170_p
            .func_175688_a(
               EnumParticleTypes.VILLAGER_ANGRY,
               this.field_70165_t + this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F - this.field_70130_N,
               this.field_70163_u + 0.5 + this.field_70146_Z.nextFloat() * this.field_70131_O,
               this.field_70161_v + this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F - this.field_70130_N,
               d0,
               d1,
               d2,
               new int[0]
            );
      }

      if (this.field_70170_p.field_72995_K && this.field_70146_Z.nextInt(25) == 0 && this.isTamed()) {
         double d0 = this.field_70146_Z.nextGaussian() * 0.02;
         double d1 = this.field_70146_Z.nextGaussian() * 0.02;
         double d2 = this.field_70146_Z.nextGaussian() * 0.02;
         this.field_70170_p
            .func_175688_a(
               EnumParticleTypes.VILLAGER_HAPPY,
               this.field_70165_t + this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F - this.field_70130_N,
               this.field_70163_u + 0.5 + this.field_70146_Z.nextFloat() * this.field_70131_O,
               this.field_70161_v + this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F - this.field_70130_N,
               d0,
               d1,
               d2,
               new int[0]
            );
      }

      super.func_70071_h_();
   }

   public void func_70619_bc() {
      super.func_70619_bc();
      if (this.field_70173_aa % 40 == 0) {
         this.func_70691_i(1.0F);
      }
   }

   public boolean canPickup(ItemStack entityItem) {
      if (entityItem == null) {
         return false;
      }

      if (!this.isTamed() && valuedItems.containsKey(Item.func_150891_b(entityItem.func_77973_b()))) {
         return true;
      }

      for (int a = 0; a < this.loot.size(); a++) {
         if (!((ItemStack)this.loot.get(a)).func_190926_b() && ((ItemStack)this.loot.get(a)).func_190916_E() <= 0) {
            this.loot.set(a, ItemStack.field_190927_a);
         }

         if (((ItemStack)this.loot.get(a)).func_190926_b()) {
            return true;
         }

         if (InventoryUtils.areItemStacksEqualStrict(entityItem, (ItemStack)this.loot.get(a))
            && entityItem.func_190916_E() + ((ItemStack)this.loot.get(a)).func_190916_E() <= ((ItemStack)this.loot.get(a)).func_77976_d()) {
            return true;
         }
      }

      return false;
   }

   public ItemStack pickupItem(ItemStack entityItem) {
      if (entityItem != null && !entityItem.func_190926_b()) {
         if (!this.isTamed() && this.isValued(entityItem)) {
            if (this.field_70146_Z.nextInt(10) < this.getValue(entityItem)) {
               this.setTamed(true);
               this.setCombatTask();
               this.field_70170_p.func_72960_a(this, (byte)18);
            }

            entityItem.func_190918_g(1);
            return entityItem.func_190916_E() <= 0 ? ItemStack.field_190927_a : entityItem;
         } else {
            for (int a = 0; a < this.loot.size(); a++) {
               if (this.loot.get(a) != null && ((ItemStack)this.loot.get(a)).func_190916_E() <= 0) {
                  this.loot.set(a, ItemStack.field_190927_a);
               }

               if (entityItem != null
                  && !entityItem.func_190926_b()
                  && entityItem.func_190916_E() > 0
                  && !((ItemStack)this.loot.get(a)).func_190926_b()
                  && ((ItemStack)this.loot.get(a)).func_190916_E() < ((ItemStack)this.loot.get(a)).func_77976_d()
                  && InventoryUtils.areItemStacksEqualStrict(entityItem, (ItemStack)this.loot.get(a))) {
                  if (entityItem.func_190916_E() + ((ItemStack)this.loot.get(a)).func_190916_E() <= ((ItemStack)this.loot.get(a)).func_77976_d()) {
                     ((ItemStack)this.loot.get(a)).func_190917_f(entityItem.func_190916_E());
                     return ItemStack.field_190927_a;
                  }

                  int sz = Math.min(entityItem.func_190916_E(), ((ItemStack)this.loot.get(a)).func_77976_d() - ((ItemStack)this.loot.get(a)).func_190916_E());
                  ((ItemStack)this.loot.get(a)).func_190917_f(sz);
                  entityItem.func_190918_g(sz);
               }

               if (entityItem != null && !entityItem.func_190926_b() && entityItem.func_190916_E() <= 0) {
                  entityItem = ItemStack.field_190927_a;
               }
            }

            for (int a = 0; a < this.loot.size(); a++) {
               if (!((ItemStack)this.loot.get(a)).func_190926_b() && ((ItemStack)this.loot.get(a)).func_190916_E() <= 0) {
                  this.loot.set(a, ItemStack.field_190927_a);
               }

               if (entityItem != null && entityItem.func_190916_E() > 0 && ((ItemStack)this.loot.get(a)).func_190926_b()) {
                  this.loot.set(a, entityItem.func_77946_l());
                  return null;
               }
            }

            if (entityItem != null && !entityItem.func_190926_b() && entityItem.func_190916_E() <= 0) {
               entityItem = ItemStack.field_190927_a;
            }

            return entityItem;
         }
      } else {
         return ItemStack.field_190927_a;
      }
   }

   protected boolean func_184645_a(EntityPlayer player, EnumHand hand) {
      if (!player.func_70093_af() && (player.func_184586_b(hand) == null || !(player.func_184586_b(hand).func_77973_b() instanceof ItemNameTag))) {
         if (this.isTamed()) {
            if (!this.field_70170_p.field_72995_K) {
               player.openGui(Thaumcraft.instance, 1, this.field_70170_p, this.func_145782_y(), 0, 0);
            }

            return true;
         } else {
            return super.func_184645_a(player, hand);
         }
      } else {
         return false;
      }
   }

   public boolean isValued(ItemStack item) {
      if (item != null && !item.func_190926_b()) {
         boolean value = valuedItems.containsKey(Item.func_150891_b(item.func_77973_b()));
         if (!value) {
            AspectList al = ThaumcraftCraftingManager.getObjectTags(item);
            if (al.getAmount(Aspect.DESIRE) > 1) {
               value = true;
            }
         }

         return value;
      } else {
         return false;
      }
   }

   public int getValue(ItemStack item) {
      if (item != null && !item.func_190926_b()) {
         int value = valuedItems.containsKey(Item.func_150891_b(item.func_77973_b())) ? valuedItems.get(Item.func_150891_b(item.func_77973_b())) : 0;
         if (value == 0) {
            AspectList al = ThaumcraftCraftingManager.getObjectTags(item);
            value = Math.min(32, al.getAmount(Aspect.DESIRE) / 2);
         }

         return value;
      } else {
         return 0;
      }
   }

   public void func_184724_a(boolean swingingArms) {
   }
}

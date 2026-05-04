package thaumcraft.common.lib.events;

import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.monster.mods.ChampionModTainted;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.items.consumables.ItemBathSalts;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXShield;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.InventoryUtils;

@EventBusSubscriber
public class EntityEvents {
   @SubscribeEvent
   public static void itemExpire(ItemExpireEvent event) {
      if (event.getEntityItem().func_92059_d() != null
         && !event.getEntityItem().func_92059_d().func_190926_b()
         && event.getEntityItem().func_92059_d().func_77973_b() != null
         && event.getEntityItem().func_92059_d().func_77973_b() instanceof ItemBathSalts) {
         BlockPos bp = new BlockPos(event.getEntityItem());
         IBlockState bs = event.getEntityItem().field_70170_p.func_180495_p(bp);
         if (bs.func_177230_c() == Blocks.field_150355_j && bs.func_177230_c().func_176201_c(bs) == 0) {
            event.getEntityItem().field_70170_p.func_175656_a(bp, BlocksTC.purifyingFluid.func_176223_P());
         }
      }
   }

   @SubscribeEvent
   public static void livingTick(LivingUpdateEvent event) {
      if (event.getEntity() instanceof EntityCreature && !event.getEntity().field_70128_L) {
         EntityCreature mob = (EntityCreature)event.getEntity();
         if (mob.func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD) != null) {
            int t = (int)mob.func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD).func_111126_e();

            try {
               if (t >= 0 && ChampionModifier.mods[t].type == 0) {
                  ChampionModifier.mods[t].effect.performEffect(mob, null, null, 0.0F);
               }
            } catch (Exception e) {
               e.printStackTrace();
               if (t >= ChampionModifier.mods.length) {
                  mob.func_70106_y();
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void entityHurt(LivingHurtEvent event) {
      if (event.getSource().func_76347_k()
         && event.getEntity() instanceof EntityPlayer
         && ThaumcraftCapabilities.knowsResearchStrict((EntityPlayer)event.getEntity(), "BASEAUROMANCY@2")
         && !ThaumcraftCapabilities.knowsResearch((EntityPlayer)event.getEntity(), "f_onfire")) {
         IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge((EntityPlayer)event.getEntity());
         knowledge.addResearch("f_onfire");
         knowledge.sync((EntityPlayerMP)event.getEntity());
         ((EntityPlayer)event.getEntity()).func_146105_b(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.func_74838_a("got.onfire")), true);
      }

      if (event.getSource().func_76364_f() != null
         && event.getEntity() instanceof EntityPlayer
         && ThaumcraftCapabilities.knowsResearchStrict((EntityPlayer)event.getEntity(), "FOCUSPROJECTILE@2")) {
         IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge((EntityPlayer)event.getEntity());
         if (!ThaumcraftCapabilities.knowsResearch((EntityPlayer)event.getEntity(), "f_arrow") && event.getSource().func_76364_f() instanceof EntityArrow) {
            knowledge.addResearch("f_arrow");
            knowledge.sync((EntityPlayerMP)event.getEntity());
            ((EntityPlayer)event.getEntity()).func_146105_b(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.func_74838_a("got.projectile")), true);
         }

         if (!ThaumcraftCapabilities.knowsResearch((EntityPlayer)event.getEntity(), "f_fireball") && event.getSource().func_76364_f() instanceof EntityFireball
            )
          {
            knowledge.addResearch("f_fireball");
            knowledge.sync((EntityPlayerMP)event.getEntity());
            ((EntityPlayer)event.getEntity()).func_146105_b(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.func_74838_a("got.projectile")), true);
         }

         if (!ThaumcraftCapabilities.knowsResearch((EntityPlayer)event.getEntity(), "f_spit") && event.getSource().func_76364_f() instanceof EntityLlamaSpit) {
            knowledge.addResearch("f_spit");
            knowledge.sync((EntityPlayerMP)event.getEntity());
            ((EntityPlayer)event.getEntity()).func_146105_b(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.func_74838_a("got.projectile")), true);
         }
      }

      if (event.getSource().func_76346_g() != null && event.getSource().func_76346_g() instanceof EntityPlayer) {
         EntityPlayer leecher = (EntityPlayer)event.getSource().func_76346_g();
         ItemStack helm = (ItemStack)leecher.field_71071_by.field_70460_b.get(3);
         if (helm != null
            && !helm.func_190926_b()
            && helm.func_77973_b() instanceof ItemFortressArmor
            && helm.func_77942_o()
            && helm.func_77978_p().func_74764_b("mask")
            && helm.func_77978_p().func_74762_e("mask") == 2
            && leecher.field_70170_p.field_73012_v.nextFloat() < event.getAmount() / 12.0F) {
            leecher.func_70691_i(1.0F);
         }
      }

      if (event.getEntity() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntity();
         if (event.getSource().func_76346_g() != null && event.getSource().func_76346_g() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)event.getSource().func_76346_g();
            ItemStack helm = (ItemStack)player.field_71071_by.field_70460_b.get(3);
            if (helm != null
               && !helm.func_190926_b()
               && helm.func_77973_b() instanceof ItemFortressArmor
               && helm.func_77942_o()
               && helm.func_77978_p().func_74764_b("mask")
               && helm.func_77978_p().func_74762_e("mask") == 1
               && player.field_70170_p.field_73012_v.nextFloat() < event.getAmount() / 10.0F) {
               try {
                  attacker.func_70690_d(new PotionEffect(MobEffects.field_82731_v, 80));
               } catch (Exception var6) {
               }
            }
         }

         int charge = (int)player.func_110139_bj();
         if (charge > 0 && PlayerEvents.runicInfo.containsKey(player.func_145782_y()) && PlayerEvents.lastMaxCharge.containsKey(player.func_145782_y())) {
            long time = System.currentTimeMillis();
            int target = -1;
            if (event.getSource().func_76346_g() != null) {
               target = event.getSource().func_76346_g().func_145782_y();
            }

            if (event.getSource() == DamageSource.field_76379_h) {
               target = -2;
            }

            if (event.getSource() == DamageSource.field_82729_p) {
               target = -3;
            }

            PacketHandler.INSTANCE
               .sendToAllAround(
                  new PacketFXShield(event.getEntity().func_145782_y(), target),
                  new TargetPoint(
                     event.getEntity().field_70170_p.field_73011_w.getDimension(),
                     event.getEntity().field_70165_t,
                     event.getEntity().field_70163_u,
                     event.getEntity().field_70161_v,
                     32.0
                  )
               );
         }
      } else {
         if (!event.getEntityLiving().field_70170_p.field_72995_K
            && event.getEntityLiving().func_110143_aJ() < 2.0F
            && !event.getEntityLiving().func_70662_br()
            && !event.getEntityLiving().field_70128_L
            && !(event.getEntityLiving() instanceof EntityOwnedConstruct)
            && !(event.getEntityLiving() instanceof ITaintedMob)
            && event.getEntityLiving().func_70644_a(PotionFluxTaint.instance)
            && event.getEntityLiving().func_70681_au().nextBoolean()) {
            EntityUtils.makeTainted(event.getEntityLiving());
            return;
         }

         if (event.getEntity() instanceof EntityMob) {
            IAttributeInstance cai = ((EntityMob)event.getEntity()).func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD);
            if (cai != null && cai.func_111126_e() >= 0.0 || event.getEntity() instanceof IEldritchMob) {
               EntityMob mob = (EntityMob)event.getEntity();
               int t = (int)cai.func_111126_e();
               if ((t == 5 || event.getEntity() instanceof IEldritchMob) && mob.func_110139_bj() > 0.0F) {
                  int target = -1;
                  if (event.getSource().func_76346_g() != null) {
                     target = event.getSource().func_76346_g().func_145782_y();
                  }

                  if (event.getSource() == DamageSource.field_76379_h) {
                     target = -2;
                  }

                  if (event.getSource() == DamageSource.field_82729_p) {
                     target = -3;
                  }

                  PacketHandler.INSTANCE
                     .sendToAllAround(
                        new PacketFXShield(mob.func_145782_y(), target),
                        new TargetPoint(
                           event.getEntity().field_70170_p.field_73011_w.getDimension(),
                           event.getEntity().field_70165_t,
                           event.getEntity().field_70163_u,
                           event.getEntity().field_70161_v,
                           32.0
                        )
                     );
                  event.getEntity().func_184185_a(SoundsTC.runicShieldCharge, 0.66F, 1.1F + event.getEntity().field_70170_p.field_73012_v.nextFloat() * 0.1F);
               } else if (t >= 0
                  && ChampionModifier.mods[t].type == 2
                  && event.getSource().func_76346_g() != null
                  && event.getSource().func_76346_g() instanceof EntityLivingBase) {
                  EntityLivingBase attacker = (EntityLivingBase)event.getSource().func_76346_g();
                  event.setAmount(ChampionModifier.mods[t].effect.performEffect(mob, attacker, event.getSource(), event.getAmount()));
               }
            }

            if (event.getAmount() > 0.0F
               && event.getSource().func_76346_g() != null
               && event.getEntity() instanceof EntityLivingBase
               && event.getSource().func_76346_g() instanceof EntityMob
               && ((EntityMob)event.getSource().func_76346_g()).func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD).func_111126_e() >= 0.0) {
               EntityMob mob = (EntityMob)event.getSource().func_76346_g();
               int t = (int)mob.func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD).func_111126_e();
               if (ChampionModifier.mods[t].type == 1) {
                  event.setAmount(ChampionModifier.mods[t].effect.performEffect(mob, (EntityLivingBase)event.getEntity(), event.getSource(), event.getAmount()));
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void itemPickup(EntityItemPickupEvent event) {
      if (event.getEntityPlayer().func_70005_c_().startsWith("FakeThaumcraft")) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void entityConstuct(EntityConstructing event) {
      if (event.getEntity() instanceof EntityCreature && !(event.getEntity() instanceof EntityOwnedConstruct)) {
         EntityCreature mob = (EntityCreature)event.getEntity();
         mob.func_110140_aT().func_111150_b(ThaumcraftApiHelper.CHAMPION_MOD).func_111128_a(-2.0);
         mob.func_110140_aT().func_111150_b(ChampionModTainted.TAINTED_MOD).func_111128_a(0.0);
      }
   }

   @SubscribeEvent
   public static void livingDrops(LivingDropsEvent event) {
      boolean fakeplayer = event.getSource().func_76346_g() != null && event.getSource().func_76346_g() instanceof FakePlayer;
      if (!event.getEntity().field_70170_p.field_72995_K
         && event.isRecentlyHit()
         && !fakeplayer
         && event.getEntity() instanceof EntityMob
         && !(event.getEntity() instanceof EntityThaumcraftBoss)
         && ((EntityMob)event.getEntity()).func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD).func_111126_e() >= 0.0
         && ((EntityMob)event.getEntity()).func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD).func_111126_e() != 13.0) {
         int i = 5 + event.getEntity().field_70170_p.field_73012_v.nextInt(3);

         while (i > 0) {
            int j = EntityXPOrb.func_70527_a(i);
            i -= j;
            event.getEntity()
               .field_70170_p
               .func_72838_d(
                  new EntityXPOrb(
                     event.getEntity().field_70170_p, event.getEntity().field_70165_t, event.getEntity().field_70163_u, event.getEntity().field_70161_v, j
                  )
               );
         }

         int lb = Math.min(2, MathHelper.func_76141_d((event.getEntity().field_70170_p.field_73012_v.nextInt(9) + event.getLootingLevel()) / 5.0F));
         event.getDrops()
            .add(
               new EntityItem(
                  event.getEntity().field_70170_p,
                  event.getEntityLiving().field_70165_t,
                  event.getEntityLiving().field_70163_u + event.getEntityLiving().func_70047_e(),
                  event.getEntityLiving().field_70161_v,
                  new ItemStack(ItemsTC.lootBag, 1, lb)
               )
            );
      }

      if (event.getEntityLiving() instanceof EntityZombie
         && !(event.getEntityLiving() instanceof EntityBrainyZombie)
         && event.isRecentlyHit()
         && event.getEntity().field_70170_p.field_73012_v.nextInt(10) - event.getLootingLevel() < 1) {
         event.getDrops()
            .add(
               new EntityItem(
                  event.getEntity().field_70170_p,
                  event.getEntityLiving().field_70165_t,
                  event.getEntityLiving().field_70163_u + event.getEntityLiving().func_70047_e(),
                  event.getEntityLiving().field_70161_v,
                  new ItemStack(ItemsTC.brain)
               )
            );
      }

      if (event.getEntityLiving() instanceof EntityCultist
         && !fakeplayer
         && event.getSource().func_76346_g() != null
         && event.getSource().func_76346_g() instanceof EntityPlayer) {
         var p = (EntityPlayer & EntityPlayer)event.getSource().func_76346_g();
         int c = !ThaumcraftCapabilities.getKnowledge(p).isResearchKnown("!CrimsonCultist@2") ? 4 : 20;
         if (InventoryUtils.getPlayerSlotFor(p, new ItemStack(ItemsTC.curio, 1, 6)) >= 0) {
            c = 50;
         }

         if (event.getEntity().field_70170_p.field_73012_v.nextInt(c) == 0) {
            event.getDrops()
               .add(
                  new EntityItem(
                     event.getEntity().field_70170_p,
                     event.getEntityLiving().field_70165_t,
                     event.getEntityLiving().field_70163_u + event.getEntityLiving().func_70047_e(),
                     event.getEntityLiving().field_70161_v,
                     new ItemStack(ItemsTC.curio, 1, 6)
                  )
               );
         }
      }

      if (event.getSource() == DamageSourceThaumcraft.dissolve) {
         AspectList aspects = AspectHelper.getEntityAspects(event.getEntityLiving());
         if (aspects != null && aspects.size() > 0) {
            Aspect[] al = aspects.getAspects();
            int q = MathHelper.func_76136_a(event.getEntity().func_130014_f_().field_73012_v, 1, 1 + aspects.visSize() / 10);

            for (int a = 0; a < q; a++) {
               Aspect aspect = al[event.getEntity().func_130014_f_().field_73012_v.nextInt(al.length)];
               ItemStack stack = ThaumcraftApiHelper.makeCrystal(aspect);
               event.getDrops()
                  .add(
                     new EntityItem(
                        event.getEntity().field_70170_p,
                        event.getEntityLiving().field_70165_t,
                        event.getEntityLiving().field_70163_u + event.getEntityLiving().func_70047_e(),
                        event.getEntityLiving().field_70161_v,
                        stack
                     )
                  );
            }
         }
      }
   }

   @SubscribeEvent
   public static void entitySpawns(EntityJoinWorldEvent event) {
      if (!event.getWorld().field_72995_K) {
         if (event.getEntity() instanceof EntityCreature
            && ((EntityCreature)event.getEntity()).func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD) != null
            && ((EntityCreature)event.getEntity()).func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD).func_111126_e() == 13.0) {
            IAttributeInstance modai = ((EntityCreature)event.getEntity()).func_110148_a(ChampionModTainted.TAINTED_MOD);
            modai.func_111124_b(new AttributeModifier(UUID.fromString("2cb22137-a9d8-4417-ae06-de0e70f11b4c"), "istainted", 1.0, 0));
            modai.func_111121_a(new AttributeModifier(UUID.fromString("2cb22137-a9d8-4417-ae06-de0e70f11b4c"), "istainted", 0.0, 0));
         }

         if (event.getEntity() instanceof EntityMob) {
            EntityMob mob = (EntityMob)event.getEntity();
            if (mob.func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD).func_111126_e() < -1.0) {
               int c = event.getWorld().field_73012_v.nextInt(100);
               if (event.getWorld().func_175659_aa() == EnumDifficulty.EASY || !ModConfig.CONFIG_WORLD.allowChampionMobs) {
                  c += 2;
               }

               if (event.getWorld().func_175659_aa() == EnumDifficulty.HARD) {
                  c -= ModConfig.CONFIG_WORLD.allowChampionMobs ? 2 : 0;
               }

               if (event.getWorld().field_73011_w.getDimension() == ModConfig.CONFIG_WORLD.dimensionOuterId) {
                  c -= 3;
               }

               Biome bg = mob.field_70170_p.func_180494_b(new BlockPos(mob));
               if (BiomeDictionary.hasType(bg, Type.SPOOKY) || BiomeDictionary.hasType(bg, Type.NETHER) || BiomeDictionary.hasType(bg, Type.END)) {
                  c -= ModConfig.CONFIG_WORLD.allowChampionMobs ? 2 : 1;
               }

               if (isDangerousLocation(
                  mob.field_70170_p,
                  MathHelper.func_76143_f(mob.field_70165_t),
                  MathHelper.func_76143_f(mob.field_70163_u),
                  MathHelper.func_76143_f(mob.field_70161_v)
               )) {
                  c -= ModConfig.CONFIG_WORLD.allowChampionMobs ? 10 : 3;
               }

               int cc = 0;
               boolean whitelisted = false;

               for (Class clazz : ConfigEntities.championModWhitelist.keySet()) {
                  if (clazz.isAssignableFrom(event.getEntity().getClass())) {
                     whitelisted = true;
                     if (ModConfig.CONFIG_WORLD.allowChampionMobs || event.getEntity() instanceof EntityThaumcraftBoss) {
                        cc = Math.max(cc, ConfigEntities.championModWhitelist.get(clazz) - 1);
                     }
                  }
               }

               c -= cc;
               if (whitelisted && c <= 0 && mob.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111125_b() >= 10.0) {
                  EntityUtils.makeChampion(mob, false);
               } else {
                  IAttributeInstance modai = mob.func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD);
                  modai.func_111124_b(ChampionModifier.ATTRIBUTE_MOD_NONE);
                  modai.func_111121_a(ChampionModifier.ATTRIBUTE_MOD_NONE);
               }
            }
         }
      }
   }

   private static boolean isDangerousLocation(World world, int x, int y, int z) {
      return false;
   }
}

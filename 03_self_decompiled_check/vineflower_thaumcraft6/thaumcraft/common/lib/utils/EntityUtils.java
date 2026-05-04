package thaumcraft.common.lib.utils;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss;
import thaumcraft.common.entities.monster.mods.ChampionModifier;

public class EntityUtils {
   public static final AttributeModifier CHAMPION_HEALTH = new AttributeModifier(
      UUID.fromString("a62bef38-48cc-42a6-ac5e-ef913841c4fd"), "Champion health buff", 100.0, 0
   );
   public static final AttributeModifier CHAMPION_DAMAGE = new AttributeModifier(
      UUID.fromString("a340d2db-d881-4c25-ac62-f0ad14cd63b0"), "Champion damage buff", 2.0, 2
   );
   public static final AttributeModifier BOLDBUFF = new AttributeModifier(UUID.fromString("4b1edd33-caa9-47ae-a702-d86c05701037"), "Bold speed boost", 0.3, 1);
   public static final AttributeModifier MIGHTYBUFF = new AttributeModifier(
      UUID.fromString("7163897f-07f5-49b3-9ce4-b74beb83d2d3"), "Mighty damage boost", 2.0, 2
   );
   public static final AttributeModifier[] HPBUFF = new AttributeModifier[]{
      new AttributeModifier(UUID.fromString("54d621c1-dd4d-4b43-8bd2-5531c8875797"), "HEALTH BUFF 1", 50.0, 0),
      new AttributeModifier(UUID.fromString("f51257dc-b7fa-4f7a-92d7-75d68e8592c4"), "HEALTH BUFF 2", 50.0, 0),
      new AttributeModifier(UUID.fromString("3d6b2e42-4141-4364-b76d-0e8664bbd0bb"), "HEALTH BUFF 3", 50.0, 0),
      new AttributeModifier(UUID.fromString("02c97a08-801c-4131-afa2-1427a6151934"), "HEALTH BUFF 4", 50.0, 0),
      new AttributeModifier(UUID.fromString("0f354f6a-33c5-40be-93be-81b1338567f1"), "HEALTH BUFF 5", 50.0, 0),
      new AttributeModifier(UUID.fromString("0f354f6a-33c5-40be-93be-81b1338567f1"), "HEALTH BUFF 6", 25.0, 0)
   };
   public static final AttributeModifier[] DMGBUFF = new AttributeModifier[]{
      new AttributeModifier(UUID.fromString("534f8c57-929a-48cf-bbd6-0fd851030748"), "DAMAGE BUFF 1", 0.5, 0),
      new AttributeModifier(UUID.fromString("d317a76e-0e7c-4c61-acfd-9fa286053b32"), "DAMAGE BUFF 2", 0.5, 0),
      new AttributeModifier(UUID.fromString("ff462d63-26a2-4363-830e-143ed97e2a4f"), "DAMAGE BUFF 3", 0.5, 0),
      new AttributeModifier(UUID.fromString("cf1eb39e-0c67-495f-887c-0d3080828d2f"), "DAMAGE BUFF 4", 0.5, 0),
      new AttributeModifier(UUID.fromString("3cfab9da-2701-43d8-ac07-885f16fa4117"), "DAMAGE BUFF 5", 0.5, 0)
   };

   public static boolean isFriendly(Entity source, Entity target) {
      if (source != null && target != null) {
         if (source.func_145782_y() == target.func_145782_y()) {
            return true;
         }

         if (source.func_184215_y(target)) {
            return true;
         }

         if (source.func_184191_r(target)) {
            return true;
         }

         if (target instanceof IEntityOwnable && ((IEntityOwnable)target).func_70902_q() != null && ((IEntityOwnable)target).func_70902_q().equals(source)) {
            return true;
         }

         try {
            if (!target.func_130014_f_().field_72995_K
               && target instanceof EntityPlayer
               && !FMLCommonHandler.instance().getMinecraftServerInstance().func_71219_W()) {
               return true;
            }
         } catch (Exception var3) {
         }

         return false;
      } else {
         return false;
      }
   }

   public static Vec3d posToHand(Entity e, EnumHand hand) {
      double px = e.field_70165_t;
      double py = e.func_174813_aQ().field_72338_b + e.field_70131_O / 2.0F + 0.25;
      double pz = e.field_70161_v;
      float m = hand == EnumHand.MAIN_HAND ? 0.0F : 180.0F;
      px += -MathHelper.func_76134_b((e.field_70177_z + m) / 180.0F * 3.141593F) * 0.3F;
      pz += -MathHelper.func_76126_a((e.field_70177_z + m) / 180.0F * 3.141593F) * 0.3F;
      Vec3d vec3d = e.func_70676_i(1.0F);
      px += vec3d.field_72450_a * 0.3;
      py += vec3d.field_72448_b * 0.3;
      pz += vec3d.field_72449_c * 0.3;
      return new Vec3d(px, py, pz);
   }

   public static boolean hasGoggles(Entity e) {
      if (!(e instanceof EntityPlayer)) {
         return false;
      }

      EntityPlayer viewer = (EntityPlayer)e;
      if (viewer.func_184614_ca().func_77973_b() instanceof IGoggles && showPopups(viewer.func_184614_ca(), viewer)) {
         return true;
      }

      for (int a = 0; a < 4; a++) {
         if (((ItemStack)viewer.field_71071_by.field_70460_b.get(a)).func_77973_b() instanceof IGoggles
            && showPopups((ItemStack)viewer.field_71071_by.field_70460_b.get(a), viewer)) {
            return true;
         }
      }

      IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(viewer);

      for (int a = 0; a < baubles.getSlots(); a++) {
         if (baubles.getStackInSlot(a).func_77973_b() instanceof IGoggles && showPopups(baubles.getStackInSlot(a), viewer)) {
            return true;
         }
      }

      return false;
   }

   private static boolean showPopups(ItemStack stack, EntityPlayer player) {
      return ((IGoggles)stack.func_77973_b()).showIngamePopups(stack, player);
   }

   public static boolean hasRevealer(Entity e) {
      if (!(e instanceof EntityPlayer)) {
         return false;
      }

      EntityPlayer viewer = (EntityPlayer)e;
      if (viewer.func_184614_ca().func_77973_b() instanceof IRevealer && reveals(viewer.func_184614_ca(), viewer)) {
         return true;
      }

      if (viewer.func_184592_cb().func_77973_b() instanceof IRevealer && reveals(viewer.func_184592_cb(), viewer)) {
         return true;
      }

      for (int a = 0; a < 4; a++) {
         if (((ItemStack)viewer.field_71071_by.field_70460_b.get(a)).func_77973_b() instanceof IRevealer
            && reveals((ItemStack)viewer.field_71071_by.field_70460_b.get(a), viewer)) {
            return true;
         }
      }

      IInventory baubles = BaublesApi.getBaubles(viewer);

      for (int a = 0; a < baubles.func_70302_i_(); a++) {
         if (baubles.func_70301_a(a).func_77973_b() instanceof IRevealer && reveals(baubles.func_70301_a(a), viewer)) {
            return true;
         }
      }

      return false;
   }

   private static boolean reveals(ItemStack stack, EntityPlayer player) {
      return ((IRevealer)stack.func_77973_b()).showNodes(stack, player);
   }

   public static Entity getPointedEntity(World world, Entity entity, double minrange, double range, float padding, boolean nonCollide) {
      return getPointedEntity(
         world,
         new RayTraceResult(entity, entity.func_174791_d().func_72441_c(0.0, entity.func_70047_e(), 0.0)),
         entity.func_70040_Z(),
         minrange,
         range,
         padding,
         nonCollide
      );
   }

   public static Entity getPointedEntity(World world, Entity entity, Vec3d lookVec, double minrange, double range, float padding) {
      return getPointedEntity(
         world, new RayTraceResult(entity, entity.func_174791_d().func_72441_c(0.0, entity.func_70047_e(), 0.0)), lookVec, minrange, range, padding, false
      );
   }

   public static Entity getPointedEntity(World world, RayTraceResult ray, Vec3d lookVec, double minrange, double range, float padding) {
      return getPointedEntity(world, ray, lookVec, minrange, range, padding, false);
   }

   public static Entity getPointedEntity(World world, RayTraceResult ray, Vec3d lookVec, double minrange, double range, float padding, boolean nonCollide) {
      Entity pointedEntity = null;
      double d = range;
      Vec3d entityVec = new Vec3d(ray.field_72307_f.field_72450_a, ray.field_72307_f.field_72448_b, ray.field_72307_f.field_72449_c);
      Vec3d vec3d2 = entityVec.func_72441_c(lookVec.field_72450_a * d, lookVec.field_72448_b * d, lookVec.field_72449_c * d);
      float f1 = padding;
      AxisAlignedBB bb = ray.field_72308_g != null
         ? ray.field_72308_g.func_174813_aQ()
         : new AxisAlignedBB(
               ray.field_72307_f.field_72450_a,
               ray.field_72307_f.field_72448_b,
               ray.field_72307_f.field_72449_c,
               ray.field_72307_f.field_72450_a,
               ray.field_72307_f.field_72448_b,
               ray.field_72307_f.field_72449_c
            )
            .func_186662_g(0.5);
      List list = world.func_72839_b(
         ray.field_72308_g, bb.func_72321_a(lookVec.field_72450_a * d, lookVec.field_72448_b * d, lookVec.field_72449_c * d).func_72314_b(f1, f1, f1)
      );
      double d2 = 0.0;

      for (int i = 0; i < list.size(); i++) {
         Entity entity = (Entity)list.get(i);
         if (!(ray.field_72307_f.func_72438_d(entity.func_174791_d()) < minrange)
            && (entity.func_70067_L() || nonCollide)
            && world.func_147447_a(
                  ray.field_72307_f, new Vec3d(entity.field_70165_t, entity.field_70163_u + entity.func_70047_e(), entity.field_70161_v), false, true, false
               )
               == null) {
            float f2 = Math.max(0.8F, entity.func_70111_Y());
            AxisAlignedBB axisalignedbb = entity.func_174813_aQ().func_72314_b(f2, f2, f2);
            RayTraceResult RayTraceResult = axisalignedbb.func_72327_a(entityVec, vec3d2);
            if (axisalignedbb.func_72318_a(entityVec)) {
               if (0.0 < d2 || d2 == 0.0) {
                  pointedEntity = entity;
                  d2 = 0.0;
               }
            } else if (RayTraceResult != null) {
               double d3 = entityVec.func_72438_d(RayTraceResult.field_72307_f);
               if (d3 < d2 || d2 == 0.0) {
                  pointedEntity = entity;
                  d2 = d3;
               }
            }
         }
      }

      return pointedEntity;
   }

   public static RayTraceResult getPointedEntityRay(
      World world, Entity ignoreEntity, Vec3d startVec, Vec3d lookVec, double minrange, double range, float padding, boolean nonCollide
   ) {
      RayTraceResult pointedEntityRay = null;
      double d = range;
      Vec3d vec3d2 = startVec.func_72441_c(lookVec.field_72450_a * d, lookVec.field_72448_b * d, lookVec.field_72449_c * d);
      float f1 = padding;
      AxisAlignedBB bb = ignoreEntity != null
         ? ignoreEntity.func_174813_aQ()
         : new AxisAlignedBB(
               startVec.field_72450_a, startVec.field_72448_b, startVec.field_72449_c, startVec.field_72450_a, startVec.field_72448_b, startVec.field_72449_c
            )
            .func_186662_g(0.5);
      List list = world.func_72839_b(
         ignoreEntity, bb.func_72321_a(lookVec.field_72450_a * d, lookVec.field_72448_b * d, lookVec.field_72449_c * d).func_72314_b(f1, f1, f1)
      );
      double d2 = 0.0;

      for (int i = 0; i < list.size(); i++) {
         Entity entity = (Entity)list.get(i);
         if (!(startVec.func_72438_d(entity.func_174791_d()) < minrange)
            && (entity.func_70067_L() || nonCollide)
            && world.func_147447_a(
                  startVec, new Vec3d(entity.field_70165_t, entity.field_70163_u + entity.func_70047_e(), entity.field_70161_v), false, true, false
               )
               == null) {
            float f2 = Math.max(0.8F, entity.func_70111_Y());
            AxisAlignedBB axisalignedbb = entity.func_174813_aQ().func_72314_b(f2, f2, f2);
            RayTraceResult rayTraceResult = axisalignedbb.func_72327_a(startVec, vec3d2);
            if (axisalignedbb.func_72318_a(startVec)) {
               if (0.0 < d2 || d2 == 0.0) {
                  pointedEntityRay = new RayTraceResult(entity, rayTraceResult.field_72307_f);
                  d2 = 0.0;
               }
            } else if (rayTraceResult != null) {
               double d3 = startVec.func_72438_d(rayTraceResult.field_72307_f);
               if (d3 < d2 || d2 == 0.0) {
                  pointedEntityRay = new RayTraceResult(entity, rayTraceResult.field_72307_f);
                  d2 = d3;
               }
            }
         }
      }

      return pointedEntityRay;
   }

   public static Entity getPointedEntity(World world, EntityLivingBase player, double range, Class<?> clazz) {
      Entity pointedEntity = null;
      double d = range;
      Vec3d vec3d = new Vec3d(player.field_70165_t, player.field_70163_u + player.func_70047_e(), player.field_70161_v);
      Vec3d vec3d1 = player.func_70040_Z();
      Vec3d vec3d2 = vec3d.func_72441_c(vec3d1.field_72450_a * d, vec3d1.field_72448_b * d, vec3d1.field_72449_c * d);
      float f1 = 1.1F;
      List list = world.func_72839_b(
         player, player.func_174813_aQ().func_72321_a(vec3d1.field_72450_a * d, vec3d1.field_72448_b * d, vec3d1.field_72449_c * d).func_72314_b(f1, f1, f1)
      );
      double d2 = 0.0;

      for (int i = 0; i < list.size(); i++) {
         Entity entity = (Entity)list.get(i);
         if (entity.func_70067_L()
            && world.func_147447_a(
                  new Vec3d(player.field_70165_t, player.field_70163_u + player.func_70047_e(), player.field_70161_v),
                  new Vec3d(entity.field_70165_t, entity.field_70163_u + entity.func_70047_e(), entity.field_70161_v),
                  false,
                  true,
                  false
               )
               == null
            && !clazz.isInstance(entity)) {
            float f2 = Math.max(0.8F, entity.func_70111_Y());
            AxisAlignedBB axisalignedbb = entity.func_174813_aQ().func_72314_b(f2, f2, f2);
            RayTraceResult RayTraceResult = axisalignedbb.func_72327_a(vec3d, vec3d2);
            if (axisalignedbb.func_72318_a(vec3d)) {
               if (0.0 < d2 || d2 == 0.0) {
                  pointedEntity = entity;
                  d2 = 0.0;
               }
            } else if (RayTraceResult != null) {
               double d3 = vec3d.func_72438_d(RayTraceResult.field_72307_f);
               if (d3 < d2 || d2 == 0.0) {
                  pointedEntity = entity;
                  d2 = d3;
               }
            }
         }
      }

      return pointedEntity;
   }

   public static boolean canEntityBeSeen(Entity entity, TileEntity te) {
      return te.func_145831_w()
            .func_147447_a(
               new Vec3d(te.func_174877_v().func_177958_n() + 0.5, te.func_174877_v().func_177956_o() + 1.25, te.func_174877_v().func_177952_p() + 0.5),
               new Vec3d(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v),
               false,
               true,
               false
            )
         == null;
   }

   public static boolean canEntityBeSeen(Entity lookingEntity, double x, double y, double z) {
      return lookingEntity.field_70170_p
            .func_147447_a(
               new Vec3d(x, y, z), new Vec3d(lookingEntity.field_70165_t, lookingEntity.field_70163_u, lookingEntity.field_70161_v), false, true, false
            )
         == null;
   }

   public static boolean canEntityBeSeen(Entity lookingEntity, Entity targetEntity) {
      return lookingEntity.field_70170_p
            .func_147447_a(
               new Vec3d(lookingEntity.field_70165_t, lookingEntity.field_70163_u + lookingEntity.field_70131_O / 2.0F, lookingEntity.field_70161_v),
               new Vec3d(targetEntity.field_70165_t, targetEntity.field_70163_u + targetEntity.field_70131_O / 2.0F, targetEntity.field_70161_v),
               false,
               true,
               false
            )
         == null;
   }

   public static void resetFloatCounter(EntityPlayerMP player) {
      player.field_71135_a.field_147365_f = 0;
   }

   public static <T extends Entity> List<T> getEntitiesInRange(World world, BlockPos pos, Entity entity, Class<? extends T> classEntity, double range) {
      return getEntitiesInRange(world, pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5, entity, classEntity, range);
   }

   public static <T extends Entity> List<T> getEntitiesInRange(
      World world, double x, double y, double z, Entity entity, Class<? extends T> classEntity, double range
   ) {
      ArrayList<T> out = new ArrayList<>();
      List list = world.func_72872_a(classEntity, new AxisAlignedBB(x, y, z, x, y, z).func_72314_b(range, range, range));
      if (list.size() > 0) {
         for (Object e : list) {
            Entity ent = (Entity)e;
            if (entity == null || entity.func_145782_y() != ent.func_145782_y()) {
               out.add((T)ent);
            }
         }
      }

      return out;
   }

   public static <T extends Entity> List<T> getEntitiesInRangeSorted(World world, Entity entity, Class<? extends T> classEntity, double range) {
      List<T> list = getEntitiesInRange(world, entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, entity, classEntity, range);
      return list.stream().sorted(new EntityUtils.EntityDistComparator(entity)).collect(Collectors.toList());
   }

   public static boolean isVisibleTo(float fov, Entity ent, Entity ent2, float range) {
      double[] x = new double[]{ent2.field_70165_t, ent2.func_174813_aQ().field_72338_b + ent2.field_70131_O / 2.0F, ent2.field_70161_v};
      double[] t = new double[]{ent.field_70165_t, ent.func_174813_aQ().field_72338_b + ent.func_70047_e(), ent.field_70161_v};
      Vec3d q = ent.func_70040_Z();
      q = new Vec3d(q.field_72450_a * range, q.field_72448_b * range, q.field_72449_c * range);
      Vec3d l = q.func_72441_c(ent.field_70165_t, ent.func_174813_aQ().field_72338_b + ent.func_70047_e(), ent.field_70161_v);
      double[] b = new double[]{l.field_72450_a, l.field_72448_b, l.field_72449_c};
      return Utils.isLyingInCone(x, t, b, fov);
   }

   public static boolean isVisibleTo(float fov, Entity ent, double xx, double yy, double zz, float range) {
      double[] x = new double[]{xx, yy, zz};
      double[] t = new double[]{ent.field_70165_t, ent.func_174813_aQ().field_72338_b + ent.func_70047_e(), ent.field_70161_v};
      Vec3d q = ent.func_70040_Z();
      q = new Vec3d(q.field_72450_a * range, q.field_72448_b * range, q.field_72449_c * range);
      Vec3d l = q.func_72441_c(ent.field_70165_t, ent.func_174813_aQ().field_72338_b + ent.func_70047_e(), ent.field_70161_v);
      double[] b = new double[]{l.field_72450_a, l.field_72448_b, l.field_72449_c};
      return Utils.isLyingInCone(x, t, b, fov);
   }

   public static EntityItem entityDropSpecialItem(Entity entity, ItemStack stack, float dropheight) {
      if (stack.func_190916_E() != 0 && stack.func_77973_b() != null) {
         EntitySpecialItem entityitem = new EntitySpecialItem(
            entity.field_70170_p, entity.field_70165_t, entity.field_70163_u + dropheight, entity.field_70161_v, stack
         );
         entityitem.func_174869_p();
         entityitem.field_70181_x = 0.1F;
         entityitem.field_70159_w = 0.0;
         entityitem.field_70179_y = 0.0;
         if (entity.captureDrops) {
            entity.capturedDrops.add(entityitem);
         } else {
            entity.field_70170_p.func_72838_d(entityitem);
         }

         return entityitem;
      } else {
         return null;
      }
   }

   public static void makeChampion(EntityMob entity, boolean persist) {
      try {
         if (entity.func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD).func_111126_e() > -2.0) {
            return;
         }
      } catch (Exception e) {
         return;
      }

      int type = entity.field_70170_p.field_73012_v.nextInt(ChampionModifier.mods.length);
      if (entity instanceof EntityCreeper) {
         type = 0;
      }

      IAttributeInstance modai = entity.func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD);
      modai.func_111124_b(ChampionModifier.mods[type].attributeMod);
      modai.func_111121_a(ChampionModifier.mods[type].attributeMod);
      if (!(entity instanceof EntityThaumcraftBoss)) {
         IAttributeInstance iattributeinstance = entity.func_110148_a(SharedMonsterAttributes.field_111267_a);
         iattributeinstance.func_111124_b(CHAMPION_HEALTH);
         iattributeinstance.func_111121_a(CHAMPION_HEALTH);
         IAttributeInstance iattributeinstance2 = entity.func_110148_a(SharedMonsterAttributes.field_111264_e);
         iattributeinstance2.func_111124_b(CHAMPION_DAMAGE);
         iattributeinstance2.func_111121_a(CHAMPION_DAMAGE);
         entity.func_70691_i(25.0F);
         entity.func_96094_a(ChampionModifier.mods[type].getModNameLocalized() + " " + entity.func_70005_c_());
      } else {
         ((EntityThaumcraftBoss)entity).generateName();
      }

      if (persist) {
         entity.func_110163_bv();
      }

      switch (type) {
         case 0:
            IAttributeInstance sai = entity.func_110148_a(SharedMonsterAttributes.field_111263_d);
            sai.func_111124_b(BOLDBUFF);
            sai.func_111121_a(BOLDBUFF);
            break;
         case 3:
            IAttributeInstance mai = entity.func_110148_a(SharedMonsterAttributes.field_111264_e);
            mai.func_111124_b(MIGHTYBUFF);
            mai.func_111121_a(MIGHTYBUFF);
            break;
         case 5:
            int bh = (int)entity.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111125_b() / 2;
            entity.func_110149_m(entity.func_110139_bj() + bh);
      }
   }

   public static void makeTainted(EntityLivingBase target) {
      try {
         if (target.func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD) != null && target.func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD).func_111126_e() > -1.0) {
            return;
         }
      } catch (Exception e) {
         e.printStackTrace();
         return;
      }

      int type = 13;
      IAttributeInstance modai = target.func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD);
      if (modai != null) {
         if (target.func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD).func_111126_e() == -1.0) {
            modai.func_111121_a(ChampionModifier.ATTRIBUTE_MINUS_ONE);
         }

         modai.func_111124_b(ChampionModifier.mods[type].attributeMod);
         modai.func_111121_a(ChampionModifier.mods[type].attributeMod);
         if (!(target instanceof EntityThaumcraftBoss)) {
            IAttributeInstance iattributeinstance = target.func_110148_a(SharedMonsterAttributes.field_111267_a);
            iattributeinstance.func_111124_b(HPBUFF[5]);
            iattributeinstance.func_111121_a(HPBUFF[5]);
            IAttributeInstance iattributeinstance2 = target.func_110148_a(SharedMonsterAttributes.field_111264_e);
            if (iattributeinstance2 == null) {
               target.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e);
               target.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(Math.max(2.0F, (target.field_70131_O + target.field_70130_N) * 2.0F));
            } else {
               iattributeinstance2.func_111124_b(DMGBUFF[0]);
               iattributeinstance2.func_111121_a(DMGBUFF[0]);
            }

            target.func_70691_i(25.0F);
         } else {
            ((EntityThaumcraftBoss)target).generateName();
         }
      }
   }

   public static class EntityDistComparator implements Comparator<Entity> {
      private Entity source;

      public EntityDistComparator(Entity source) {
         this.source = source;
      }

      public int compare(Entity a, Entity b) {
         if (a.equals(b)) {
            return 0;
         }

         double da = this.source.func_174791_d().func_72436_e(a.func_174791_d());
         double db = this.source.func_174791_d().func_72436_e(b.func_174791_d());
         return da < db ? -1 : (da > db ? 1 : 0);
      }
   }
}

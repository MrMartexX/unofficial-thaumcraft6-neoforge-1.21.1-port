package thaumcraft.common.world.biomes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;

public class BiomeHandler {
   public static Biome EERIE;
   public static Biome MAGICAL_FOREST;
   public static Biome ELDRITCH;
   public static HashMap<Type, List> biomeInfo = new HashMap<>();
   public static Collection<Aspect> c = Aspect.aspects.values();
   public static ArrayList<Aspect> basicAspects = new ArrayList<>();
   public static ArrayList<Aspect> complexAspects = new ArrayList<>();
   public static HashMap<Integer, Integer> dimensionBlacklist = new HashMap<>();
   public static HashMap<Integer, Integer> biomeBlacklist = new HashMap<>();

   public static void registerBiomeInfo(Type type, float auraLevel, Aspect tag, boolean greatwood, float greatwoodchance) {
      biomeInfo.put(type, Arrays.asList(auraLevel, tag, greatwood, greatwoodchance));
   }

   public static float getBiomeAuraModifier(Biome biome) {
      try {
         Set<Type> types = BiomeDictionary.getTypes(biome);
         float average = 0.0F;
         int count = 0;

         for (Type type : types) {
            average += biomeInfo.get(type).get(0);
            count++;
         }

         return average / count;
      } catch (Exception var6) {
         return 0.5F;
      }
   }

   public static Aspect getRandomBiomeTag(int biomeId, Random random) {
      try {
         Set<Type> types = BiomeDictionary.getTypes(Biome.func_150568_d(biomeId));
         Type type = types.toArray(new Type[0])[random.nextInt(types.size())];
         return (Aspect)biomeInfo.get(type).get(1);
      } catch (Exception var4) {
         return null;
      }
   }

   public static float getBiomeSupportsGreatwood(int biomeId) {
      try {
         for (Type type : BiomeDictionary.getTypes(Biome.func_150568_d(biomeId))) {
            if ((Boolean)biomeInfo.get(type).get(2)) {
               return (Float)biomeInfo.get(type).get(3);
            }
         }
      } catch (Exception var4) {
      }

      return 0.0F;
   }

   public static int getFirstFreeBiomeSlot(int old) {
      for (int a = 0; a < Biome.field_185377_q.func_148742_b().size() * 2; a++) {
         if (Biome.func_150568_d(a) == null) {
            Thaumcraft.log.warn("Biome slot " + old + " already occupied. Using first free biome slot at " + a);
            return a;
         }
      }

      return -1;
   }

   public static void addDimBlacklist(int dim, int level) {
      dimensionBlacklist.put(dim, level);
   }

   public static int getDimBlacklist(int dim) {
      return !dimensionBlacklist.containsKey(dim) ? -1 : dimensionBlacklist.get(dim);
   }

   public static void addBiomeBlacklist(int biome, int level) {
      biomeBlacklist.put(biome, level);
   }

   public static int getBiomeBlacklist(int biome) {
      return !biomeBlacklist.containsKey(biome) ? -1 : biomeBlacklist.get(biome);
   }

   public static void registerBiomes() {
      BiomeDictionary.addTypes(EERIE, new Type[]{Type.MAGICAL, Type.SPOOKY});
      BiomeDictionary.addTypes(ELDRITCH, new Type[]{Type.MAGICAL, Type.SPOOKY, Type.END});
      BiomeDictionary.addTypes(MAGICAL_FOREST, new Type[]{Type.MAGICAL, Type.FOREST});
      registerBiomeInfo(Type.WATER, 0.33F, Aspect.WATER, false, 0.0F);
      registerBiomeInfo(Type.OCEAN, 0.33F, Aspect.WATER, false, 0.0F);
      registerBiomeInfo(Type.RIVER, 0.4F, Aspect.WATER, false, 0.0F);
      registerBiomeInfo(Type.WET, 0.4F, Aspect.WATER, false, 0.0F);
      registerBiomeInfo(Type.LUSH, 0.5F, Aspect.WATER, true, 0.5F);
      registerBiomeInfo(Type.HOT, 0.33F, Aspect.FIRE, false, 0.0F);
      registerBiomeInfo(Type.DRY, 0.25F, Aspect.FIRE, false, 0.0F);
      registerBiomeInfo(Type.NETHER, 0.125F, Aspect.FIRE, false, 0.0F);
      registerBiomeInfo(Type.MESA, 0.33F, Aspect.FIRE, false, 0.0F);
      registerBiomeInfo(Type.SPOOKY, 0.5F, Aspect.FIRE, false, 0.0F);
      registerBiomeInfo(Type.DENSE, 0.4F, Aspect.ORDER, false, 0.0F);
      registerBiomeInfo(Type.SNOWY, 0.25F, Aspect.ORDER, false, 0.0F);
      registerBiomeInfo(Type.COLD, 0.25F, Aspect.ORDER, false, 0.0F);
      registerBiomeInfo(Type.MUSHROOM, 0.75F, Aspect.ORDER, false, 0.0F);
      registerBiomeInfo(Type.MAGICAL, 0.75F, Aspect.ORDER, true, 1.0F);
      registerBiomeInfo(Type.CONIFEROUS, 0.33F, Aspect.EARTH, true, 0.2F);
      registerBiomeInfo(Type.FOREST, 0.5F, Aspect.EARTH, true, 1.0F);
      registerBiomeInfo(Type.SANDY, 0.25F, Aspect.EARTH, false, 0.0F);
      registerBiomeInfo(Type.BEACH, 0.3F, Aspect.EARTH, false, 0.0F);
      registerBiomeInfo(Type.JUNGLE, 0.6F, Aspect.EARTH, false, 0.0F);
      registerBiomeInfo(Type.SAVANNA, 0.25F, Aspect.AIR, true, 0.2F);
      registerBiomeInfo(Type.MOUNTAIN, 0.3F, Aspect.AIR, false, 0.0F);
      registerBiomeInfo(Type.HILLS, 0.33F, Aspect.AIR, false, 0.0F);
      registerBiomeInfo(Type.PLAINS, 0.3F, Aspect.AIR, true, 0.2F);
      registerBiomeInfo(Type.END, 0.125F, Aspect.AIR, false, 0.0F);
      registerBiomeInfo(Type.DRY, 0.125F, Aspect.ENTROPY, false, 0.0F);
      registerBiomeInfo(Type.SPARSE, 0.2F, Aspect.ENTROPY, false, 0.0F);
      registerBiomeInfo(Type.SWAMP, 0.5F, Aspect.ENTROPY, true, 0.2F);
      registerBiomeInfo(Type.WASTELAND, 0.125F, Aspect.ENTROPY, false, 0.0F);
      registerBiomeInfo(Type.DEAD, 0.1F, Aspect.ENTROPY, false, 0.0F);
   }
}

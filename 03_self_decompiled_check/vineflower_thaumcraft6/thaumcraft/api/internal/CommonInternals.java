package thaumcraft.api.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IThaumcraftRecipe;

public class CommonInternals {
   public static HashMap<String, ResourceLocation> jsonLocs = new HashMap<>();
   public static ArrayList<ThaumcraftApi.EntityTags> scanEntities = new ArrayList<>();
   public static HashMap<ResourceLocation, IThaumcraftRecipe> craftingRecipeCatalog = new HashMap<>();
   public static HashMap<ResourceLocation, Object> craftingRecipeCatalogFake = new HashMap<>();
   public static ArrayList<ThaumcraftApi.SmeltBonus> smeltingBonus = new ArrayList<>();
   public static ConcurrentHashMap<Integer, AspectList> objectTags = new ConcurrentHashMap<>();
   public static HashMap<Object, Integer> warpMap = new HashMap<>();
   public static HashMap<String, ItemStack> seedList = new HashMap<>();

   public static IThaumcraftRecipe getCatalogRecipe(ResourceLocation key) {
      return craftingRecipeCatalog.get(key);
   }

   public static Object getCatalogRecipeFake(ResourceLocation key) {
      return craftingRecipeCatalogFake.get(key);
   }

   public static int generateUniqueItemstackId(ItemStack stack) {
      ItemStack sc = stack.func_77946_l();
      sc.func_190920_e(1);
      String ss = sc.serializeNBT().toString();
      return ss.hashCode();
   }

   public static int generateUniqueItemstackIdStripped(ItemStack stack) {
      ItemStack sc = stack.func_77946_l();
      sc.func_190920_e(1);
      sc.func_77982_d(null);
      String ss = sc.serializeNBT().toString();
      return ss.hashCode();
   }
}

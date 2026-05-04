package thaumcraft.api.aspects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.apache.commons.lang3.text.WordUtils;
import thaumcraft.api.research.ScanAspect;
import thaumcraft.api.research.ScanningManager;

public class Aspect {
   String tag;
   Aspect[] components;
   int color;
   private String chatcolor;
   ResourceLocation image;
   int blend;
   public static HashMap<Integer, Aspect> mixList = new HashMap<>();
   private static ArrayList<Aspect> primals = new ArrayList<>();
   private static ArrayList<Aspect> compounds = new ArrayList<>();
   public static LinkedHashMap<String, Aspect> aspects = new LinkedHashMap<>();
   public static final Aspect AIR = new Aspect("aer", 16777086, "e", 1);
   public static final Aspect EARTH = new Aspect("terra", 5685248, "2", 1);
   public static final Aspect FIRE = new Aspect("ignis", 16734721, "c", 1);
   public static final Aspect WATER = new Aspect("aqua", 3986684, "3", 1);
   public static final Aspect ORDER = new Aspect("ordo", 14013676, "7", 1);
   public static final Aspect ENTROPY = new Aspect("perditio", 4210752, "8", 771);
   public static final Aspect VOID = new Aspect("vacuos", 8947848, new Aspect[]{AIR, ENTROPY}, 771);
   public static final Aspect LIGHT = new Aspect("lux", 16777152, new Aspect[]{AIR, FIRE});
   public static final Aspect MOTION = new Aspect("motus", 13487348, new Aspect[]{AIR, ORDER});
   public static final Aspect COLD = new Aspect("gelum", 14811135, new Aspect[]{FIRE, ENTROPY});
   public static final Aspect CRYSTAL = new Aspect("vitreus", 8454143, new Aspect[]{EARTH, AIR});
   public static final Aspect METAL = new Aspect("metallum", 11908557, new Aspect[]{EARTH, ORDER});
   public static final Aspect LIFE = new Aspect("victus", 14548997, new Aspect[]{EARTH, WATER});
   public static final Aspect DEATH = new Aspect("mortuus", 6946821, new Aspect[]{WATER, ENTROPY});
   public static final Aspect ENERGY = new Aspect("potentia", 12648447, new Aspect[]{ORDER, FIRE});
   public static final Aspect EXCHANGE = new Aspect("permutatio", 5735255, new Aspect[]{ENTROPY, ORDER});
   public static final Aspect MAGIC = new Aspect("praecantatio", 13566207, new Aspect[]{ENERGY, AIR});
   public static final Aspect AURA = new Aspect("auram", 16761087, new Aspect[]{MAGIC, AIR});
   public static final Aspect ALCHEMY = new Aspect("alkimia", 2337949, new Aspect[]{MAGIC, WATER});
   public static final Aspect FLUX = new Aspect("vitium", 8388736, new Aspect[]{ENTROPY, MAGIC});
   public static final Aspect DARKNESS = new Aspect("tenebrae", 2236962, new Aspect[]{VOID, LIGHT});
   public static final Aspect ELDRITCH = new Aspect("alienis", 8409216, new Aspect[]{VOID, DARKNESS});
   public static final Aspect FLIGHT = new Aspect("volatus", 15198167, new Aspect[]{AIR, MOTION});
   public static final Aspect PLANT = new Aspect("herba", 109568, new Aspect[]{LIFE, EARTH});
   public static final Aspect TOOL = new Aspect("instrumentum", 4210926, new Aspect[]{METAL, ENERGY});
   public static final Aspect CRAFT = new Aspect("fabrico", 8428928, new Aspect[]{EXCHANGE, TOOL});
   public static final Aspect MECHANISM = new Aspect("machina", 8421536, new Aspect[]{MOTION, TOOL});
   public static final Aspect TRAP = new Aspect("vinculum", 10125440, new Aspect[]{MOTION, ENTROPY});
   public static final Aspect SOUL = new Aspect("spiritus", 15461371, new Aspect[]{LIFE, DEATH});
   public static final Aspect MIND = new Aspect("cognitio", 16356991, new Aspect[]{FIRE, SOUL});
   public static final Aspect SENSES = new Aspect("sensus", 12648384, new Aspect[]{AIR, SOUL});
   public static final Aspect AVERSION = new Aspect("aversio", 12603472, new Aspect[]{SOUL, ENTROPY});
   public static final Aspect PROTECT = new Aspect("praemunio", 49344, new Aspect[]{SOUL, EARTH});
   public static final Aspect DESIRE = new Aspect("desiderium", 15121988, new Aspect[]{SOUL, VOID});
   public static final Aspect UNDEAD = new Aspect("exanimis", 3817472, new Aspect[]{MOTION, DEATH});
   public static final Aspect BEAST = new Aspect("bestia", 10445833, new Aspect[]{MOTION, LIFE});
   public static final Aspect MAN = new Aspect("humanus", 16766912, new Aspect[]{SOUL, LIFE});

   public Aspect(String tag, int color, Aspect[] components, ResourceLocation image, int blend) {
      if (aspects.containsKey(tag)) {
         throw new IllegalArgumentException(tag + " already registered!");
      }

      this.tag = tag;
      this.components = components;
      this.color = color;
      this.image = image;
      this.blend = blend;
      aspects.put(tag, this);
      ScanningManager.addScannableThing(new ScanAspect("!" + tag, this));
      if (components != null) {
         int h = (components[0].getTag() + components[1].getTag()).hashCode();
         mixList.put(h, this);
      }
   }

   public Aspect(String tag, int color, Aspect[] components) {
      this(tag, color, components, new ResourceLocation("thaumcraft", "textures/aspects/" + tag.toLowerCase() + ".png"), 1);
   }

   public Aspect(String tag, int color, Aspect[] components, int blend) {
      this(tag, color, components, new ResourceLocation("thaumcraft", "textures/aspects/" + tag.toLowerCase() + ".png"), blend);
   }

   public Aspect(String tag, int color, String chatcolor, int blend) {
      this(tag, color, (Aspect[])null, blend);
      this.setChatcolor(chatcolor);
   }

   public int getColor() {
      return this.color;
   }

   public String getName() {
      return WordUtils.capitalizeFully(this.tag);
   }

   public String getLocalizedDescription() {
      return I18n.func_74838_a("tc.aspect." + this.tag);
   }

   public String getTag() {
      return this.tag;
   }

   public void setTag(String tag) {
      this.tag = tag;
   }

   public Aspect[] getComponents() {
      return this.components;
   }

   public void setComponents(Aspect[] components) {
      this.components = components;
   }

   public ResourceLocation getImage() {
      return this.image;
   }

   public static Aspect getAspect(String tag) {
      return aspects.get(tag);
   }

   public int getBlend() {
      return this.blend;
   }

   public void setBlend(int blend) {
      this.blend = blend;
   }

   public boolean isPrimal() {
      return this.getComponents() == null || this.getComponents().length != 2;
   }

   public static ArrayList<Aspect> getPrimalAspects() {
      if (primals.isEmpty()) {
         for (Aspect aspect : aspects.values()) {
            if (aspect.isPrimal()) {
               primals.add(aspect);
            }
         }
      }

      return primals;
   }

   public static ArrayList<Aspect> getCompoundAspects() {
      if (compounds.isEmpty()) {
         for (Aspect aspect : aspects.values()) {
            if (!aspect.isPrimal()) {
               compounds.add(aspect);
            }
         }
      }

      return compounds;
   }

   public String getChatcolor() {
      return this.chatcolor;
   }

   public void setChatcolor(String chatcolor) {
      this.chatcolor = chatcolor;
   }
}

package thaumcraft.api.casters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EntitySelectors;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class FocusPackage implements IFocusElement {
   public World world;
   private EntityLivingBase caster;
   private UUID casterUUID;
   private float power = 1.0F;
   private int complexity = 0;
   int index;
   UUID uid;
   public List<IFocusElement> nodes = Collections.synchronizedList(new ArrayList<>());

   @Override
   public String getResearch() {
      return null;
   }

   public FocusPackage() {
   }

   public FocusPackage(EntityLivingBase caster) {
      this.world = caster.field_70170_p;
      this.caster = caster;
      this.casterUUID = caster.func_110124_au();
   }

   @Override
   public String getKey() {
      return "thaumcraft.PACKAGE";
   }

   @Override
   public IFocusElement.EnumUnitType getType() {
      return IFocusElement.EnumUnitType.PACKAGE;
   }

   public int getComplexity() {
      return this.complexity;
   }

   public void setComplexity(int complexity) {
      this.complexity = complexity;
   }

   public UUID getUniqueID() {
      return this.uid;
   }

   public void setUniqueID(UUID id) {
      this.uid = id;
   }

   public int getExecutionIndex() {
      return this.index;
   }

   public void setExecutionIndex(int idx) {
      this.index = idx;
   }

   public void addNode(IFocusElement e) {
      this.nodes.add(e);
   }

   public UUID getCasterUUID() {
      if (this.caster != null) {
         this.casterUUID = this.caster.func_110124_au();
      }

      return this.casterUUID;
   }

   public void setCasterUUID(UUID casterUUID) {
      this.casterUUID = casterUUID;
   }

   public EntityLivingBase getCaster() {
      try {
         if (this.caster == null) {
            this.caster = this.world.func_152378_a(this.getCasterUUID());
         }

         if (this.caster == null) {
            for (EntityLivingBase e : this.world.func_175644_a(EntityLivingBase.class, EntitySelectors.field_94557_a)) {
               if (this.getCasterUUID().equals(e.func_110124_au())) {
                  this.caster = e;
                  break;
               }
            }
         }
      } catch (Exception var3) {
      }

      return this.caster;
   }

   public FocusEffect[] getFocusEffects() {
      return this.getFocusEffectsPackage(this);
   }

   private FocusEffect[] getFocusEffectsPackage(FocusPackage fp) {
      ArrayList<FocusEffect> out = new ArrayList<>();

      for (IFocusElement el : fp.nodes) {
         if (el instanceof FocusEffect) {
            out.add((FocusEffect)el);
         } else if (el instanceof FocusPackage) {
            for (FocusEffect fep : this.getFocusEffectsPackage((FocusPackage)el)) {
               out.add(fep);
            }
         } else if (el instanceof FocusModSplit) {
            for (FocusPackage fsp : ((FocusModSplit)el).getSplitPackages()) {
               for (FocusEffect fep : this.getFocusEffectsPackage(fsp)) {
                  out.add(fep);
               }
            }
         }
      }

      return out.toArray(new FocusEffect[0]);
   }

   public void deserialize(NBTTagCompound nbt) {
      this.uid = nbt.func_186857_a("uid");
      this.index = nbt.func_74762_e("index");
      int dim = nbt.func_74762_e("dim");
      this.world = DimensionManager.getWorld(dim);
      this.setCasterUUID(nbt.func_186857_a("casterUUID"));
      this.power = nbt.func_74760_g("power");
      this.complexity = nbt.func_74762_e("complexity");
      NBTTagList nodelist = nbt.func_150295_c("nodes", 10);
      this.nodes.clear();

      for (int x = 0; x < nodelist.func_74745_c(); x++) {
         NBTTagCompound nodenbt = nodelist.func_150305_b(x);
         IFocusElement.EnumUnitType ut = IFocusElement.EnumUnitType.valueOf(nodenbt.func_74779_i("type"));
         if (ut != null) {
            if (ut == IFocusElement.EnumUnitType.PACKAGE) {
               FocusPackage fp = new FocusPackage();
               fp.deserialize(nodenbt.func_74775_l("package"));
               this.nodes.add(fp);
               break;
            }

            IFocusElement fn = FocusEngine.getElement(nodenbt.func_74779_i("key"));
            if (fn != null) {
               if (fn instanceof FocusNode) {
                  ((FocusNode)fn).initialize();
                  if (((FocusNode)fn).getSettingList() != null) {
                     for (String ns : ((FocusNode)fn).getSettingList()) {
                        ((FocusNode)fn).getSetting(ns).setValue(nodenbt.func_74762_e("setting." + ns));
                     }
                  }

                  if (fn instanceof FocusModSplit) {
                     ((FocusModSplit)fn).deserialize(nodenbt.func_74775_l("packages"));
                  }
               }

               this.addNode(fn);
            }
         }
      }
   }

   public NBTTagCompound serialize() {
      NBTTagCompound nbt = new NBTTagCompound();
      if (this.uid != null) {
         nbt.func_186854_a("uid", this.uid);
      }

      nbt.func_74768_a("index", this.index);
      if (this.getCasterUUID() != null) {
         nbt.func_186854_a("casterUUID", this.getCasterUUID());
      }

      if (this.world != null) {
         nbt.func_74768_a("dim", this.world.field_73011_w.getDimension());
      }

      nbt.func_74776_a("power", this.power);
      nbt.func_74768_a("complexity", this.complexity);
      NBTTagList nodelist = new NBTTagList();
      synchronized (this.nodes) {
         for (IFocusElement node : this.nodes) {
            if (node != null && node.getType() != null) {
               NBTTagCompound nodenbt = new NBTTagCompound();
               nodenbt.func_74778_a("type", node.getType().name());
               nodenbt.func_74778_a("key", node.getKey());
               if (node.getType() == IFocusElement.EnumUnitType.PACKAGE) {
                  nodenbt.func_74782_a("package", ((FocusPackage)node).serialize());
                  nodelist.func_74742_a(nodenbt);
                  break;
               }

               if (node instanceof FocusNode && ((FocusNode)node).getSettingList() != null) {
                  for (String ns : ((FocusNode)node).getSettingList()) {
                     nodenbt.func_74768_a("setting." + ns, ((FocusNode)node).getSettingValue(ns));
                  }
               }

               if (node instanceof FocusModSplit) {
                  nodenbt.func_74782_a("packages", ((FocusModSplit)node).serialize());
               }

               nodelist.func_74742_a(nodenbt);
            }
         }
      }

      nbt.func_74782_a("nodes", nodelist);
      return nbt;
   }

   public float getPower() {
      return this.power;
   }

   public void multiplyPower(float pow) {
      this.power *= pow;
   }

   public FocusPackage copy(EntityLivingBase caster) {
      FocusPackage fp = new FocusPackage(caster);
      fp.deserialize(this.serialize());
      return fp;
   }

   public void initialize(EntityLivingBase caster) {
      this.world = caster.func_130014_f_();
      IFocusElement node = this.nodes.get(0);
      if (node instanceof FocusMediumRoot && ((FocusMediumRoot)node).supplyTargets() == null) {
         ((FocusMediumRoot)node).setupFromCaster(caster);
      }
   }

   public int getSortingHelper() {
      String s = "";

      for (IFocusElement k : this.nodes) {
         s = s + k.getKey();
         if (k instanceof FocusNode && ((FocusNode)k).getSettingList() != null) {
            for (String ns : ((FocusNode)k).getSettingList()) {
               s = s + "" + ((FocusNode)k).getSettingValue(ns);
            }
         }
      }

      return s.hashCode();
   }
}

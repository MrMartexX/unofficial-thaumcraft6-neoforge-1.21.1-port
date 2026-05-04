package thaumcraft.api.casters;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;

public class NodeSetting {
   int value;
   public String key;
   String description;
   NodeSetting.INodeSettingType type;
   String research;

   public NodeSetting(String key, String description, NodeSetting.INodeSettingType setting, String research) {
      this.key = key;
      this.type = setting;
      this.value = setting.getDefault();
      this.description = description;
      this.research = research;
   }

   public NodeSetting(String key, String description, NodeSetting.INodeSettingType setting) {
      this(key, description, setting, null);
   }

   public int getValue() {
      return this.type.getValue(this.value);
   }

   public void setValue(int truevalue) {
      int lv = -1;
      this.value = 0;

      while (this.getValue() != truevalue && lv != this.value) {
         lv = this.value;
         this.increment();
      }
   }

   public String getResearch() {
      return this.research;
   }

   public String getValueText() {
      return I18n.func_74838_a(this.type.getValueText(this.value));
   }

   public void increment() {
      this.value++;
      this.value = this.getType().clamp(this.value);
   }

   public void decrement() {
      this.value--;
      this.value = this.getType().clamp(this.value);
   }

   public NodeSetting.INodeSettingType getType() {
      return this.type;
   }

   public String getLocalizedName() {
      return I18n.func_74838_a(this.description);
   }

   public interface INodeSettingType {
      int getDefault();

      int clamp(int var1);

      int getValue(int var1);

      String getValueText(int var1);
   }

   public static class NodeSettingIntList implements NodeSetting.INodeSettingType {
      int[] values;
      String[] descriptions;

      public NodeSettingIntList(int[] values, String[] descriptions) {
         this.values = values;
         this.descriptions = descriptions;
      }

      @Override
      public int getDefault() {
         return 0;
      }

      @Override
      public int clamp(int old) {
         return MathHelper.func_76125_a(old, 0, this.values.length - 1);
      }

      @Override
      public int getValue(int value) {
         return this.values[this.clamp(value)];
      }

      @Override
      public String getValueText(int value) {
         return this.descriptions[value];
      }
   }

   public static class NodeSettingIntRange implements NodeSetting.INodeSettingType {
      int min;
      int max;

      public NodeSettingIntRange(int min, int max) {
         this.min = min;
         this.max = max;
      }

      @Override
      public int getDefault() {
         return this.min;
      }

      @Override
      public int clamp(int old) {
         return MathHelper.func_76125_a(old, this.min, this.max);
      }

      @Override
      public int getValue(int value) {
         return this.clamp(value);
      }

      @Override
      public String getValueText(int value) {
         return "" + this.getValue(value);
      }
   }
}

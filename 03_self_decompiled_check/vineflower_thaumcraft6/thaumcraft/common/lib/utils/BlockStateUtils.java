package thaumcraft.common.lib.utils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public class BlockStateUtils {
   public static EnumFacing getFacing(IBlockState state) {
      return EnumFacing.func_82600_a(state.func_177230_c().func_176201_c(state) & 7);
   }

   public static EnumFacing getFacing(int meta) {
      return EnumFacing.func_82600_a(meta & 7);
   }

   public static boolean isEnabled(IBlockState state) {
      return (state.func_177230_c().func_176201_c(state) & 8) != 8;
   }

   public static boolean isEnabled(int meta) {
      return (meta & 8) != 8;
   }

   public static IProperty getPropertyByName(IBlockState blockState, String propertyName) {
      UnmodifiableIterator var2 = blockState.func_177228_b().keySet().iterator();

      while (var2.hasNext()) {
         IProperty property = (IProperty)var2.next();
         if (property.func_177701_a().equals(propertyName)) {
            return property;
         }
      }

      return null;
   }

   public static boolean isValidPropertyName(IBlockState blockState, String propertyName) {
      return getPropertyByName(blockState, propertyName) != null;
   }

   public static Comparable getPropertyValueByName(IBlockState blockState, IProperty property, String valueName) {
      UnmodifiableIterator var3 = ((ImmutableSet)property.func_177700_c()).iterator();

      while (var3.hasNext()) {
         Comparable value = (Comparable)var3.next();
         if (value.toString().equals(valueName)) {
            return value;
         }
      }

      return null;
   }

   public static ImmutableSet<IBlockState> getValidStatesForProperties(IBlockState baseState, IProperty... properties) {
      if (properties == null) {
         return null;
      }

      Set<IBlockState> validStates = Sets.newHashSet();
      BlockStateUtils.PropertyIndexer propertyIndexer = new BlockStateUtils.PropertyIndexer(properties);

      do {
         IBlockState currentState = baseState;

         for (IProperty property : properties) {
            BlockStateUtils.IndexedProperty indexedProperty = propertyIndexer.getIndexedProperty(property);
            currentState = currentState.func_177226_a(property, indexedProperty.getCurrentValue());
         }

         validStates.add(currentState);
      } while (propertyIndexer.increment());

      return ImmutableSet.copyOf(validStates);
   }

   private static class IndexedProperty {
      private ArrayList<Comparable> validValues = new ArrayList<>();
      private int maxCount;
      private int counter;
      private BlockStateUtils.IndexedProperty parent;
      private BlockStateUtils.IndexedProperty child;

      private IndexedProperty(IProperty property) {
         this.validValues.addAll(property.func_177700_c());
         this.maxCount = this.validValues.size() - 1;
      }

      public boolean increment() {
         if (this.counter < this.maxCount) {
            this.counter++;
            return true;
         } else if (this.hasParent()) {
            this.resetSelfAndChildren();
            return this.parent.increment();
         } else {
            return false;
         }
      }

      public void resetSelfAndChildren() {
         this.counter = 0;
         if (this.hasChild()) {
            this.child.resetSelfAndChildren();
         }
      }

      public boolean hasParent() {
         return this.parent != null;
      }

      public boolean hasChild() {
         return this.child != null;
      }

      public int getCounter() {
         return this.counter;
      }

      public int getMaxCount() {
         return this.maxCount;
      }

      public Comparable getCurrentValue() {
         return this.validValues.get(this.counter);
      }
   }

   private static class PropertyIndexer {
      private HashMap<IProperty, BlockStateUtils.IndexedProperty> indexedProperties = new HashMap<>();
      private IProperty finalProperty;

      private PropertyIndexer(IProperty... properties) {
         this.finalProperty = properties[properties.length - 1];
         BlockStateUtils.IndexedProperty previousIndexedProperty = null;

         for (IProperty property : properties) {
            BlockStateUtils.IndexedProperty indexedProperty = new BlockStateUtils.IndexedProperty(property);
            if (previousIndexedProperty != null) {
               indexedProperty.parent = previousIndexedProperty;
               previousIndexedProperty.child = indexedProperty;
            }

            this.indexedProperties.put(property, indexedProperty);
            previousIndexedProperty = indexedProperty;
         }
      }

      public boolean increment() {
         return this.indexedProperties.get(this.finalProperty).increment();
      }

      public BlockStateUtils.IndexedProperty getIndexedProperty(IProperty property) {
         return this.indexedProperties.get(property);
      }
   }
}

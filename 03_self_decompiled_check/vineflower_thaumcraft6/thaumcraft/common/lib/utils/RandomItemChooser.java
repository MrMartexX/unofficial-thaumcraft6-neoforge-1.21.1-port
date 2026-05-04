package thaumcraft.common.lib.utils;

import java.util.List;

public class RandomItemChooser {
   public RandomItemChooser.Item chooseOnWeight(List<RandomItemChooser.Item> items) {
      double completeWeight = 0.0;

      for (RandomItemChooser.Item item : items) {
         completeWeight += item.getWeight();
      }

      double r = Math.random() * completeWeight;
      double countWeight = 0.0;

      for (RandomItemChooser.Item item : items) {
         countWeight += item.getWeight();
         if (countWeight >= r) {
            return item;
         }
      }

      throw new RuntimeException("Should never be shown.");
   }

   public interface Item {
      double getWeight();
   }
}

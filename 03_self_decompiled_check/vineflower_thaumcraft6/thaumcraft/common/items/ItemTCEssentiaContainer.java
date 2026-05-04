package thaumcraft.common.items;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.common.config.ConfigItems;

public class ItemTCEssentiaContainer extends ItemGenericEssentiaContainer implements IEssentiaContainerItem, IThaumcraftItems {
   private final String BASE_NAME;
   protected String[] VARIANTS;
   protected int[] VARIANTS_META;

   public ItemTCEssentiaContainer(String name, int base, String... variants) {
      super(base);
      this.setRegistryName(name);
      this.func_77655_b(name);
      this.func_77637_a(ConfigItems.TABTC);
      this.setNoRepair();
      this.func_77627_a(variants.length > 1);
      this.BASE_NAME = name;
      if (variants.length == 0) {
         this.VARIANTS = new String[]{name};
      } else {
         this.VARIANTS = variants;
      }

      this.VARIANTS_META = new int[this.VARIANTS.length];
      int m = 0;

      while (m < this.VARIANTS.length) {
         this.VARIANTS_META[m] = m++;
      }

      ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
   }

   @Override
   public Item getItem() {
      return this;
   }

   @Override
   public String[] getVariantNames() {
      return this.VARIANTS;
   }

   @Override
   public int[] getVariantMeta() {
      return this.VARIANTS_META;
   }

   @Override
   public ItemMeshDefinition getCustomMesh() {
      return null;
   }

   @Override
   public ModelResourceLocation getCustomModelResourceLocation(String variant) {
      return variant.equals(this.BASE_NAME)
         ? new ModelResourceLocation("thaumcraft:" + this.BASE_NAME)
         : new ModelResourceLocation("thaumcraft:" + this.BASE_NAME, variant);
   }
}

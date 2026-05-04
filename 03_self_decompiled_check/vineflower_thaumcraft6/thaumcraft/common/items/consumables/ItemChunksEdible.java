package thaumcraft.common.items.consumables;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;

public class ItemChunksEdible extends ItemFood implements IThaumcraftItems {
   public final int field_77855_a;
   private String[] variants = new String[]{"beef", "chicken", "pork", "fish", "rabbit", "mutton"};

   public ItemChunksEdible() {
      super(1, 0.3F, false);
      this.field_77855_a = 10;
      this.func_77625_d(64);
      this.func_77627_a(true);
      this.func_77656_e(0);
      this.setRegistryName("chunk");
      this.func_77655_b("chunk");
      this.func_77637_a(ConfigItems.TABTC);
      ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
   }

   public int func_77626_a(ItemStack stack1) {
      return this.field_77855_a;
   }

   public void func_150895_a(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (tab == ConfigItems.TABTC || tab == CreativeTabs.field_78027_g) {
         for (int a = 0; a < this.variants.length; a++) {
            items.add(new ItemStack(this, 1, a));
         }
      }
   }

   public String func_77667_c(ItemStack itemStack) {
      return this.field_77787_bX && itemStack.func_77960_j() < this.variants.length && this.variants[itemStack.func_77960_j()] != "chunk"
         ? String.format(super.func_77658_a() + ".%s", this.variants[itemStack.func_77960_j()])
         : super.func_77667_c(itemStack);
   }

   @Override
   public String[] getVariantNames() {
      return this.variants;
   }

   @Override
   public int[] getVariantMeta() {
      return new int[]{0, 1, 2, 3, 4, 5};
   }

   @Override
   public Item getItem() {
      return this;
   }

   @Override
   public ItemMeshDefinition getCustomMesh() {
      return null;
   }

   @Override
   public ModelResourceLocation getCustomModelResourceLocation(String variant) {
      return variant.equals("chunk") ? new ModelResourceLocation("thaumcraft:chunk") : new ModelResourceLocation("thaumcraft:chunk", variant);
   }
}

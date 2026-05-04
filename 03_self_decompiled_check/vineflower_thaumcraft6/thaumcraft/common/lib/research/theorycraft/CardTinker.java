package thaumcraft.common.lib.research.theorycraft;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class CardTinker extends TheorycraftCard {
   ItemStack stack = ItemStack.field_190927_a;
   static ItemStack[] options = new ItemStack[]{
      new ItemStack(ItemsTC.visResonator),
      new ItemStack(ItemsTC.thaumometer),
      new ItemStack(Blocks.field_150467_bQ),
      new ItemStack(Blocks.field_150408_cc),
      new ItemStack(Blocks.field_150367_z),
      new ItemStack(Blocks.field_150409_cd),
      new ItemStack(Blocks.field_150381_bn),
      new ItemStack(Blocks.field_150477_bB),
      new ItemStack(Blocks.field_150421_aI),
      new ItemStack(Blocks.field_150453_bW),
      new ItemStack(Blocks.field_150331_J),
      new ItemStack(Blocks.field_150438_bZ),
      new ItemStack(Blocks.field_150320_F),
      new ItemStack(Items.field_151148_bJ),
      new ItemStack(Items.field_151111_aL),
      new ItemStack(Items.field_151142_bV),
      new ItemStack(Items.field_151132_bS),
      new ItemStack(Items.field_151113_aN)
   };

   @Override
   public NBTTagCompound serialize() {
      NBTTagCompound nbt = super.serialize();
      nbt.func_74782_a("stack", this.stack.serializeNBT());
      return nbt;
   }

   @Override
   public void deserialize(NBTTagCompound nbt) {
      super.deserialize(nbt);
      this.stack = new ItemStack(nbt.func_74775_l("stack"));
   }

   @Override
   public boolean initialize(EntityPlayer player, ResearchTableData data) {
      Random r = new Random(this.getSeed());
      this.stack = options[r.nextInt(options.length)].func_77946_l();
      return this.stack != null;
   }

   @Override
   public int getInspirationCost() {
      return 1;
   }

   @Override
   public String getResearchCategory() {
      return "ARTIFICE";
   }

   private int getVal() {
      int q = 0;

      try {
         q = (int)(q + Math.sqrt(ThaumcraftCraftingManager.getObjectTags(this.stack).visSize()));
      } catch (Exception var3) {
      }

      return q;
   }

   @Override
   public String getLocalizedName() {
      return new TextComponentTranslation("card.tinker.name", new Object[0]).func_150254_d();
   }

   @Override
   public String getLocalizedText() {
      int a = this.getVal() * 2;
      int b = a + 10;
      return new TextComponentTranslation("card.tinker.text", new Object[]{a, b}).func_150254_d();
   }

   @Override
   public ItemStack[] getRequiredItems() {
      return new ItemStack[]{this.stack};
   }

   @Override
   public boolean activate(EntityPlayer player, ResearchTableData data) {
      int q = this.getVal() * 2;
      data.addTotal(this.getResearchCategory(), MathHelper.func_76136_a(player.func_70681_au(), q, q + 10));
      return true;
   }
}

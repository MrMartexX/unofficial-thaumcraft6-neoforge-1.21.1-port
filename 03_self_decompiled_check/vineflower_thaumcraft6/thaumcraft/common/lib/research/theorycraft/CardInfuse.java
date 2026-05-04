package thaumcraft.common.lib.research.theorycraft;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class CardInfuse extends TheorycraftCard {
   Aspect aspect;
   ItemStack stack = ItemStack.field_190927_a;
   static ItemStack[] options = new ItemStack[]{
      new ItemStack(ItemsTC.alumentum),
      new ItemStack(BlocksTC.nitor.get(EnumDyeColor.YELLOW)),
      new ItemStack(ItemsTC.amber),
      new ItemStack(ItemsTC.brain),
      new ItemStack(ItemsTC.fabric),
      new ItemStack(ItemsTC.salisMundus),
      new ItemStack(ItemsTC.ingots, 1, 0),
      new ItemStack(ItemsTC.ingots, 1, 2),
      new ItemStack(ItemsTC.quicksilver),
      new ItemStack(Items.field_151043_k),
      new ItemStack(Items.field_151042_j),
      new ItemStack(Items.field_151045_i),
      new ItemStack(Items.field_151166_bC),
      new ItemStack(Items.field_151072_bj),
      new ItemStack(Items.field_151116_aA),
      new ItemStack(Blocks.field_150325_L),
      new ItemStack(Items.field_151118_aC),
      new ItemStack(Items.field_151032_g),
      new ItemStack(Items.field_151110_aK),
      new ItemStack(Items.field_151008_G),
      new ItemStack(Items.field_151114_aO),
      new ItemStack(Items.field_151137_ax),
      new ItemStack(Items.field_151073_bk),
      new ItemStack(Items.field_151016_H),
      new ItemStack(Items.field_151031_f),
      new ItemStack(Items.field_151010_B),
      new ItemStack(Items.field_151040_l),
      new ItemStack(Items.field_151035_b),
      new ItemStack(Items.field_151005_D),
      new ItemStack(Items.field_151128_bU),
      new ItemStack(Items.field_151034_e)
   };

   @Override
   public NBTTagCompound serialize() {
      NBTTagCompound nbt = super.serialize();
      nbt.func_74778_a("aspect", this.aspect.getTag());
      nbt.func_74782_a("stack", this.stack.serializeNBT());
      return nbt;
   }

   @Override
   public void deserialize(NBTTagCompound nbt) {
      super.deserialize(nbt);
      this.aspect = Aspect.getAspect(nbt.func_74779_i("aspect"));
      this.stack = new ItemStack(nbt.func_74775_l("stack"));
   }

   @Override
   public boolean initialize(EntityPlayer player, ResearchTableData data) {
      Random r = new Random(this.getSeed());
      int num = r.nextInt(Aspect.getCompoundAspects().size());
      this.aspect = Aspect.getCompoundAspects().get(num);
      this.stack = options[r.nextInt(options.length)].func_77946_l();
      return this.aspect != null && this.stack != null;
   }

   @Override
   public int getInspirationCost() {
      return 1;
   }

   @Override
   public String getResearchCategory() {
      return "INFUSION";
   }

   @Override
   public String getLocalizedName() {
      return new TextComponentTranslation("card.infuse.name", new Object[0]).func_150254_d();
   }

   @Override
   public String getLocalizedText() {
      return new TextComponentTranslation(
            "card.infuse.text", new Object[]{TextFormatting.BOLD + this.aspect.getName() + TextFormatting.RESET, this.stack.func_82833_r(), this.getVal()}
         )
         .func_150254_d();
   }

   private int getVal() {
      int q = 10;

      try {
         q = (int)(q + Math.sqrt(ThaumcraftCraftingManager.getObjectTags(this.stack).visSize()) * 1.5);
      } catch (Exception var3) {
      }

      return q;
   }

   @Override
   public ItemStack[] getRequiredItems() {
      return new ItemStack[]{this.stack, ItemPhial.makeFilledPhial(this.aspect)};
   }

   @Override
   public boolean[] getRequiredItemsConsumed() {
      return new boolean[]{true, true};
   }

   @Override
   public boolean activate(EntityPlayer player, ResearchTableData data) {
      data.addTotal(this.getResearchCategory(), this.getVal());
      return true;
   }
}

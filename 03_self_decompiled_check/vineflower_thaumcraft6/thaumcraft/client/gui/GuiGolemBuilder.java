package thaumcraft.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.golems.parts.GolemAddon;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.api.golems.parts.GolemHead;
import thaumcraft.api.golems.parts.GolemLeg;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.client.gui.plugins.GuiHoverButton;
import thaumcraft.client.gui.plugins.GuiScrollButton;
import thaumcraft.common.container.ContainerGolemBuilder;
import thaumcraft.common.golems.GolemProperties;
import thaumcraft.common.golems.client.gui.GuiGolemCraftButton;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.crafting.TileGolemBuilder;

@SideOnly(Side.CLIENT)
public class GuiGolemBuilder extends GuiContainer {
   private TileGolemBuilder builder;
   private EntityPlayer player;
   ResourceLocation tex = new ResourceLocation("thaumcraft", "textures/gui/gui_golembuilder.png");
   ArrayList<GolemHead> valHeads = new ArrayList<>();
   ArrayList<GolemMaterial> valMats = new ArrayList<>();
   ArrayList<GolemArm> valArms = new ArrayList<>();
   ArrayList<GolemLeg> valLegs = new ArrayList<>();
   ArrayList<GolemAddon> valAddons = new ArrayList<>();
   static int headIndex = 0;
   static int matIndex = 0;
   static int armIndex = 0;
   static int legIndex = 0;
   static int addonIndex = 0;
   IGolemProperties props = GolemProperties.fromLong(0L);
   float hearts = 0.0F;
   float armor = 0.0F;
   float damage = 0.0F;
   GuiGolemCraftButton craftButton = null;
   ResourceLocation matIcon = new ResourceLocation("thaumcraft", "textures/items/golem.png");
   int cost = 0;
   boolean allfound = false;
   ItemStack[] components = null;
   boolean[] owns = null;
   boolean disableAll = false;

   public GuiGolemBuilder(InventoryPlayer par1InventoryPlayer, TileGolemBuilder table) {
      super(new ContainerGolemBuilder(par1InventoryPlayer, table));
      this.player = par1InventoryPlayer.field_70458_d;
      this.builder = table;
      this.field_146999_f = 208;
      this.field_147000_g = 224;
   }

   public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
      this.func_146276_q_();
      super.func_73863_a(mouseX, mouseY, partialTicks);
      this.func_191948_b(mouseX, mouseY);
   }

   public void func_73866_w_() {
      super.func_73866_w_();
      this.valHeads.clear();

      for (GolemHead head : GolemHead.getHeads()) {
         if (ThaumcraftCapabilities.knowsResearchStrict(this.player, head.research)) {
            this.valHeads.add(head);
         }
      }

      this.valMats.clear();

      for (GolemMaterial mat : GolemMaterial.getMaterials()) {
         if (ThaumcraftCapabilities.knowsResearchStrict(this.player, mat.research)) {
            this.valMats.add(mat);
         }
      }

      this.valArms.clear();

      for (GolemArm arm : GolemArm.getArms()) {
         if (ThaumcraftCapabilities.knowsResearchStrict(this.player, arm.research)) {
            this.valArms.add(arm);
         }
      }

      this.valLegs.clear();

      for (GolemLeg leg : GolemLeg.getLegs()) {
         if (ThaumcraftCapabilities.knowsResearchStrict(this.player, leg.research)) {
            this.valLegs.add(leg);
         }
      }

      this.valAddons.clear();

      for (GolemAddon addon : GolemAddon.getAddons()) {
         if (ThaumcraftCapabilities.knowsResearchStrict(this.player, addon.research)) {
            this.valAddons.add(addon);
         }
      }

      if (headIndex >= this.valHeads.size()) {
         headIndex = 0;
      }

      if (matIndex >= this.valMats.size()) {
         matIndex = 0;
      }

      if (armIndex >= this.valArms.size()) {
         armIndex = 0;
      }

      if (legIndex >= this.valLegs.size()) {
         legIndex = 0;
      }

      if (addonIndex >= this.valAddons.size()) {
         addonIndex = 0;
      }

      this.gatherInfo();
   }

   protected void func_146976_a(float par1, int par2, int par3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.field_71446_o.func_110577_a(this.tex);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.func_73729_b(this.field_147003_i, this.field_147009_r, 0, 0, this.field_146999_f, this.field_147000_g);
      if (this.components != null && this.components.length > 0) {
         int i = 1;
         int q = 0;
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);

         for (int a = 0; a < this.components.length; a++) {
            if (!this.owns[a]) {
               this.func_73729_b(this.field_147003_i + 144 + q * 16, this.field_147009_r + 16 + 16 * i, 240, 0, 16, 16);
            }

            if (++i > 3) {
               i = 0;
               q++;
            }
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      if (this.builder.cost > 0) {
         this.func_73729_b(
            this.field_147003_i + 145, this.field_147009_r + 89, 209, 89, (int)(46.0F * (1.0F - (float)this.builder.cost / this.builder.maxCost)), 6
         );
         if (!this.disableAll) {
            this.disableAll = true;
            this.redoComps();
         }
      } else if (this.disableAll) {
         this.disableAll = false;
         this.redoComps();
      }

      this.func_73732_a(this.field_146289_q, "" + this.hearts, this.field_147003_i + 48, this.field_147009_r + 108, 16777215);
      this.func_73732_a(this.field_146289_q, "" + this.armor, this.field_147003_i + 72, this.field_147009_r + 108, 16777215);
      this.func_73732_a(this.field_146289_q, "" + this.damage, this.field_147003_i + 97, this.field_147009_r + 108, 16777215);
   }

   private void gatherInfo() {
      this.field_146292_n.clear();
      this.craftButton = new GuiGolemCraftButton(99, this.field_147003_i + 120, this.field_147009_r + 104);
      this.field_146292_n.add(this.craftButton);
      if (this.valHeads.size() > 1) {
         this.field_146292_n.add(new GuiScrollButton(0, this.field_147003_i + 112 - 5 - 6, this.field_147009_r - 5 + 16 + 8, 10, 10, true));
         this.field_146292_n.add(new GuiScrollButton(1, this.field_147003_i + 112 - 5 + 22, this.field_147009_r - 5 + 16 + 8, 10, 10, false));
      }

      if (this.valMats.size() > 1) {
         this.field_146292_n.add(new GuiScrollButton(2, this.field_147003_i + 16 - 5 - 6, this.field_147009_r - 5 + 16 + 8, 10, 10, true));
         this.field_146292_n.add(new GuiScrollButton(3, this.field_147003_i + 16 - 5 + 22, this.field_147009_r - 5 + 16 + 8, 10, 10, false));
      }

      if (this.valArms.size() > 1) {
         this.field_146292_n.add(new GuiScrollButton(4, this.field_147003_i + 112 - 5 - 6, this.field_147009_r - 5 + 40 + 8, 10, 10, true));
         this.field_146292_n.add(new GuiScrollButton(5, this.field_147003_i + 112 - 5 + 22, this.field_147009_r - 5 + 40 + 8, 10, 10, false));
      }

      if (this.valLegs.size() > 1) {
         this.field_146292_n.add(new GuiScrollButton(6, this.field_147003_i + 112 - 5 - 6, this.field_147009_r - 5 + 64 + 8, 10, 10, true));
         this.field_146292_n.add(new GuiScrollButton(7, this.field_147003_i + 112 - 5 + 22, this.field_147009_r - 5 + 64 + 8, 10, 10, false));
      }

      if (this.valAddons.size() > 1) {
         this.field_146292_n.add(new GuiScrollButton(8, this.field_147003_i + 16 - 5 - 6, this.field_147009_r - 5 + 64 + 8, 10, 10, true));
         this.field_146292_n.add(new GuiScrollButton(9, this.field_147003_i + 16 - 5 + 22, this.field_147009_r - 5 + 64 + 8, 10, 10, false));
      }

      if (this.valHeads.size() > 0) {
         this.field_146292_n
            .add(
               new GuiHoverButton(
                  this,
                  100,
                  this.field_147003_i + 120,
                  this.field_147009_r + 24,
                  16,
                  16,
                  this.valHeads.get(headIndex).getLocalizedName(),
                  this.valHeads.get(headIndex).getLocalizedDescription(),
                  this.valHeads.get(headIndex).icon
               )
            );
      }

      if (this.valMats.size() > 0) {
         this.field_146292_n
            .add(
               new GuiHoverButton(
                  this,
                  101,
                  this.field_147003_i + 24,
                  this.field_147009_r + 24,
                  16,
                  16,
                  this.valMats.get(matIndex).getLocalizedName(),
                  this.valMats.get(matIndex).getLocalizedDescription(),
                  this.matIcon,
                  this.valMats.get(matIndex).itemColor
               )
            );
      }

      if (this.valArms.size() > 0) {
         this.field_146292_n
            .add(
               new GuiHoverButton(
                  this,
                  102,
                  this.field_147003_i + 120,
                  this.field_147009_r + 48,
                  16,
                  16,
                  this.valArms.get(armIndex).getLocalizedName(),
                  this.valArms.get(armIndex).getLocalizedDescription(),
                  this.valArms.get(armIndex).icon
               )
            );
      }

      if (this.valLegs.size() > 0) {
         this.field_146292_n
            .add(
               new GuiHoverButton(
                  this,
                  103,
                  this.field_147003_i + 120,
                  this.field_147009_r + 72,
                  16,
                  16,
                  this.valLegs.get(legIndex).getLocalizedName(),
                  this.valLegs.get(legIndex).getLocalizedDescription(),
                  this.valLegs.get(legIndex).icon
               )
            );
      }

      if (this.valAddons.size() > 0 && !this.valAddons.get(addonIndex).key.equalsIgnoreCase("none")) {
         this.field_146292_n
            .add(
               new GuiHoverButton(
                  this,
                  103,
                  this.field_147003_i + 24,
                  this.field_147009_r + 72,
                  16,
                  16,
                  this.valAddons.get(addonIndex).getLocalizedName(),
                  this.valAddons.get(addonIndex).getLocalizedDescription(),
                  this.valAddons.get(addonIndex).icon
               )
            );
      }

      this.props = GolemProperties.fromLong(0L);
      this.props.setHead(this.valHeads.get(headIndex));
      this.props.setMaterial(this.valMats.get(matIndex));
      this.props.setArms(this.valArms.get(armIndex));
      this.props.setLegs(this.valLegs.get(legIndex));
      this.props.setAddon(this.valAddons.get(addonIndex));
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.func_74757_a("check", true);
      nbt.func_74772_a("golem", this.props.toLong());
      this.builder.sendMessageToServer(nbt);
      this.redoComps();
      EnumGolemTrait[] tags = this.props.getTraits().toArray(new EnumGolemTrait[0]);
      if (tags != null && tags.length > 0) {
         int yy = tags.length <= 4 ? (tags.length - 1) % 4 * 8 : 24;
         int xx = (tags.length - 1) / 4 % 4 * 8;
         int i = 0;
         int q = 0;
         int z = 0;

         for (EnumGolemTrait tag : tags) {
            this.field_146292_n
               .add(
                  new GuiHoverButton(
                     this,
                     30 + z,
                     this.field_147003_i + 72 + q * 16 - xx,
                     this.field_147009_r + 48 + 16 * i - yy,
                     16,
                     16,
                     tag.getLocalizedName(),
                     tag.getLocalizedDescription(),
                     tag.icon
                  )
               );
            if (++i > 3) {
               i = 0;
               q++;
            }

            z++;
         }
      }

      int hh = 10 + this.props.getMaterial().healthMod;
      if (this.props.hasTrait(EnumGolemTrait.FRAGILE)) {
         hh = (int)(hh * 0.75);
      }

      this.hearts = hh / 2.0F;
      int aa = this.props.getMaterial().armor;
      if (this.props.hasTrait(EnumGolemTrait.ARMORED)) {
         aa = (int)Math.max(aa * 1.5, aa + 1);
      }

      if (this.props.hasTrait(EnumGolemTrait.FRAGILE)) {
         aa = (int)(aa * 0.75);
      }

      this.armor = aa / 2.0F;
      double dd = this.props.hasTrait(EnumGolemTrait.FIGHTER) ? this.props.getMaterial().damage : 0.0;
      if (this.props.hasTrait(EnumGolemTrait.BRUTAL)) {
         dd = Math.max(dd * 1.5, dd + 1.0);
      }

      this.damage = (float)(dd / 2.0);
   }

   private void redoComps() {
      this.allfound = true;
      this.cost = this.props.getTraits().size() * 2;
      this.components = this.props.generateComponents();
      if (this.components.length >= 1) {
         this.owns = new boolean[this.components.length];

         for (int a = 0; a < this.components.length; a++) {
            this.cost = this.cost + this.components[a].func_190916_E();
            this.owns[a] = false;
            if (this.builder.hasStuff != null && this.builder.hasStuff.length > a) {
               this.owns[a] = this.builder.hasStuff[a];
            }

            if (!this.owns[a]) {
               this.owns[a] = InventoryUtils.isPlayerCarryingAmount(this.player, this.components[a], true);
            }

            if (!this.owns[a]) {
               this.allfound = false;
            }
         }
      }

      if (this.components != null && this.components.length > 0) {
         this.field_146292_n
            .add(
               new GuiHoverButton(
                  this,
                  10,
                  this.field_147003_i + 152,
                  this.field_147009_r + 24,
                  16,
                  16,
                  Aspect.MECHANISM.getName(),
                  Aspect.MECHANISM.getLocalizedDescription(),
                  Aspect.MECHANISM
               )
            );
         int i = 1;
         int q = 0;
         int z = 0;

         for (ItemStack stack : this.components) {
            this.field_146292_n
               .add(
                  new GuiHoverButton(
                     this, 11 + z, this.field_147003_i + 152 + q * 16, this.field_147009_r + 24 + 16 * i, 16, 16, stack.func_82833_r(), null, stack
                  )
               );
            if (++i > 3) {
               i = 0;
               q++;
            }

            z++;
         }
      }

      if (this.field_146292_n != null && this.field_146292_n.size() > 0) {
         for (Object b : this.field_146292_n) {
            if (b instanceof GuiButton) {
               ((GuiButton)b).field_146124_l = !this.disableAll;
               if (!this.disableAll && b == this.craftButton) {
                  this.craftButton.field_146124_l = this.allfound;
               }
            }
         }
      }
   }

   protected void func_146979_b(int mouseX, int mouseY) {
      if (this.components != null && this.components.length > 0) {
         this.func_73731_b(this.field_146289_q, "" + this.cost, 162 - this.field_146289_q.func_78256_a("" + this.cost), 24, 16777215);
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.field_71446_o.func_110577_a(this.tex);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.func_73729_b(12, 12, 228, 124, 24, 24);
      this.func_73729_b(12, 60, 228, 124, 24, 24);
      this.func_73729_b(108, 12, 228, 124, 24, 24);
      this.func_73729_b(108, 36, 228, 124, 24, 24);
      this.func_73729_b(108, 60, 228, 124, 24, 24);

      for (GuiButton guibutton : this.field_146292_n) {
         if (guibutton.func_146115_a()) {
            guibutton.func_146111_b(mouseX - this.field_147003_i, mouseY - this.field_147009_r);
            break;
         }
      }

      if (ContainerGolemBuilder.redo) {
         this.redoComps();
         ContainerGolemBuilder.redo = false;
      }

      GL11.glDisable(3042);
   }

   protected void func_146284_a(GuiButton button) throws IOException {
      if (button.field_146127_k == 0) {
         headIndex--;
         if (headIndex < 0) {
            headIndex = this.valHeads.size() - 1;
         }

         this.gatherInfo();
      } else if (button.field_146127_k == 1) {
         headIndex++;
         if (headIndex >= this.valHeads.size()) {
            headIndex = 0;
         }

         this.gatherInfo();
      } else if (button.field_146127_k == 2) {
         matIndex--;
         if (matIndex < 0) {
            matIndex = this.valMats.size() - 1;
         }

         this.gatherInfo();
      } else if (button.field_146127_k == 3) {
         matIndex++;
         if (matIndex >= this.valMats.size()) {
            matIndex = 0;
         }

         this.gatherInfo();
      } else if (button.field_146127_k == 4) {
         armIndex--;
         if (armIndex < 0) {
            armIndex = this.valArms.size() - 1;
         }

         this.gatherInfo();
      } else if (button.field_146127_k == 5) {
         armIndex++;
         if (armIndex >= this.valArms.size()) {
            armIndex = 0;
         }

         this.gatherInfo();
      } else if (button.field_146127_k == 6) {
         legIndex--;
         if (legIndex < 0) {
            legIndex = this.valLegs.size() - 1;
         }

         this.gatherInfo();
      } else if (button.field_146127_k == 7) {
         legIndex++;
         if (legIndex >= this.valLegs.size()) {
            legIndex = 0;
         }

         this.gatherInfo();
      } else if (button.field_146127_k == 8) {
         addonIndex--;
         if (addonIndex < 0) {
            addonIndex = this.valAddons.size() - 1;
         }

         this.gatherInfo();
      } else if (button.field_146127_k == 9) {
         addonIndex++;
         if (addonIndex >= this.valAddons.size()) {
            addonIndex = 0;
         }

         this.gatherInfo();
      } else if (button.field_146127_k == 99 && this.allfound) {
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.func_74772_a("golem", this.props.toLong());
         this.builder.sendMessageToServer(nbt);
         this.field_146297_k.field_71442_b.func_78756_a(this.field_147002_h.field_75152_c, 99);
         this.disableAll = true;
      }
   }
}

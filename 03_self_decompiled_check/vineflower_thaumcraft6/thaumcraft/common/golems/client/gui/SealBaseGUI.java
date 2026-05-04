package thaumcraft.common.golems.client.gui;

import java.awt.Color;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.client.gui.plugins.GuiHoverButton;
import thaumcraft.client.gui.plugins.GuiPlusMinusButton;
import thaumcraft.client.lib.CustomRenderItem;

@SideOnly(Side.CLIENT)
public class SealBaseGUI extends GuiContainer {
   ISealEntity seal;
   int middleX;
   int middleY;
   int category = -1;
   int[] categories;
   ResourceLocation tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");

   public SealBaseGUI(InventoryPlayer player, World world, ISealEntity seal) {
      super(new SealBaseContainer(player, world, seal));
      this.seal = seal;
      this.field_146999_f = 176;
      this.field_147000_g = 232;
      this.middleX = this.field_146999_f / 2;
      this.middleY = (this.field_147000_g - 72) / 2 - 8;
      if (seal.getSeal() instanceof ISealGui) {
         this.categories = ((ISealGui)seal.getSeal()).getGuiCategories();
      } else {
         this.categories = new int[]{0, 4};
      }
   }

   private ModelManager getModelmanager() {
      return Minecraft.func_71410_x().field_175617_aL;
   }

   public void func_73866_w_() {
      super.func_73866_w_();
      this.field_146296_j = new CustomRenderItem();
      this.setupCategories();
   }

   private void setupCategories() {
      this.field_146292_n.clear();
      int c = 0;
      float slice = 60.0F / this.categories.length;
      float start = -180.0F + (this.categories.length - 1) * slice / 2.0F;
      if (slice > 24.0F) {
         slice = 24.0F;
      }

      if (slice < 12.0F) {
         slice = 12.0F;
      }

      for (int cat : this.categories) {
         if (this.category < 0) {
            this.category = cat;
         }

         if (this.categories.length > 1) {
            int xx = (int)(MathHelper.func_76134_b((start - c * slice) / 180.0F * (float) Math.PI) * 86.0F);
            int yy = (int)(MathHelper.func_76126_a((start - c * slice) / 180.0F * (float) Math.PI) * 86.0F);
            this.field_146292_n
               .add(
                  new GuiGolemCategoryButton(
                     c,
                     this.field_147003_i + this.middleX + xx,
                     this.field_147009_r + this.middleY + yy,
                     16,
                     16,
                     "button.category." + cat,
                     cat,
                     this.category == cat
                  )
               );
         }

         c++;
      }

      int xx = (int)(MathHelper.func_76134_b((start - c * slice) / 180.0F * (float) Math.PI) * 86.0F);
      int yy = (int)(MathHelper.func_76126_a((start - c * slice) / 180.0F * (float) Math.PI) * 86.0F);
      this.field_146292_n
         .add(new GuiGolemRedstoneButton(27, this.field_147003_i + this.middleX + xx - 8, this.field_147009_r + this.middleY + yy - 8, 16, 16, this.seal));
      switch (this.category) {
         case 0:
            this.field_146292_n
               .add(new GuiPlusMinusButton(80, this.field_147003_i + this.middleX - 5 - 14, this.field_147009_r + this.middleY - 17, 10, 10, true));
            this.field_146292_n
               .add(new GuiPlusMinusButton(81, this.field_147003_i + this.middleX - 5 + 14, this.field_147009_r + this.middleY - 17, 10, 10, false));
            this.field_146292_n
               .add(new GuiPlusMinusButton(82, this.field_147003_i + this.middleX + 18 - 12, this.field_147009_r + this.middleY + 4, 10, 10, true));
            this.field_146292_n
               .add(new GuiPlusMinusButton(83, this.field_147003_i + this.middleX + 18 + 11, this.field_147009_r + this.middleY + 4, 10, 10, false));
            this.field_146292_n.add(new GuiGolemLockButton(25, this.field_147003_i + this.middleX - 32, this.field_147009_r + this.middleY, 16, 16, this.seal));
            break;
         case 1:
            if (this.seal.getSeal() instanceof ISealConfigFilter) {
               int s = ((ISealConfigFilter)this.seal.getSeal()).getFilterSize();
               int sy = 16 + (s - 1) / 3 * 12;
               this.field_146292_n
                  .add(
                     new GuiGolemBWListButton(
                        20,
                        this.field_147003_i + this.middleX - 8,
                        this.field_147009_r + this.middleY + (s - 1) / 3 * 24 - sy + 27,
                        16,
                        16,
                        (ISealConfigFilter)this.seal.getSeal()
                     )
                  );
            }
            break;
         case 2:
            this.field_146292_n
               .add(new GuiPlusMinusButton(90, this.field_147003_i + this.middleX - 5 - 14, this.field_147009_r + this.middleY - 25, 10, 10, true));
            this.field_146292_n
               .add(new GuiPlusMinusButton(91, this.field_147003_i + this.middleX - 5 + 14, this.field_147009_r + this.middleY - 25, 10, 10, false));
            this.field_146292_n.add(new GuiPlusMinusButton(92, this.field_147003_i + this.middleX - 5 - 14, this.field_147009_r + this.middleY, 10, 10, true));
            this.field_146292_n.add(new GuiPlusMinusButton(93, this.field_147003_i + this.middleX - 5 + 14, this.field_147009_r + this.middleY, 10, 10, false));
            this.field_146292_n
               .add(new GuiPlusMinusButton(94, this.field_147003_i + this.middleX - 5 - 14, this.field_147009_r + this.middleY + 25, 10, 10, true));
            this.field_146292_n
               .add(new GuiPlusMinusButton(95, this.field_147003_i + this.middleX - 5 + 14, this.field_147009_r + this.middleY + 25, 10, 10, false));
            break;
         case 3:
            if (this.seal.getSeal() instanceof ISealConfigToggles) {
               ISealConfigToggles cp = (ISealConfigToggles)this.seal.getSeal();
               int s = cp.getToggles().length < 4 ? 8 : (cp.getToggles().length < 6 ? 7 : (cp.getToggles().length < 9 ? 6 : 5));
               int h = (cp.getToggles().length - 1) * s;
               int w = 12;

               for (ISealConfigToggles.SealToggle prop : cp.getToggles()) {
                  int ww = 12 + Math.min(100, this.field_146289_q.func_78256_a(I18n.func_74838_a(prop.getName())));
                  ww /= 2;
                  if (ww > w) {
                     w = ww;
                  }
               }

               int p = 0;

               for (ISealConfigToggles.SealToggle prop : cp.getToggles()) {
                  this.field_146292_n
                     .add(
                        new GuiGolemPropButton(
                           30 + p, this.field_147003_i + this.middleX - w, this.field_147009_r + this.middleY - 5 - h + p * s * 2, 8, 8, prop.getName(), prop
                        )
                     );
                  p++;
               }
            }
            break;
         case 4:
            EnumGolemTrait[] tags = this.seal.getSeal().getRequiredTags();
            if (tags != null && tags.length > 0) {
               int p = 0;

               for (EnumGolemTrait tag : tags) {
                  this.field_146292_n
                     .add(
                        new GuiHoverButton(
                           this,
                           500 + p,
                           this.field_147003_i + this.middleX + p * 18 - (tags.length - 1) * 9,
                           this.field_147009_r + this.middleY - 8,
                           16,
                           16,
                           tag.getLocalizedName(),
                           tag.getLocalizedDescription(),
                           tag.icon
                        )
                     );
                  p++;
               }
            }

            tags = this.seal.getSeal().getForbiddenTags();
            if (tags != null && tags.length > 0) {
               int p = 0;

               for (EnumGolemTrait tag : tags) {
                  this.field_146292_n
                     .add(
                        new GuiHoverButton(
                           this,
                           600 + p,
                           this.field_147003_i + this.middleX + p * 18 - (tags.length - 1) * 9,
                           this.field_147009_r + this.middleY + 24,
                           16,
                           16,
                           tag.getLocalizedName(),
                           tag.getLocalizedDescription(),
                           tag.icon
                        )
                     );
                  p++;
               }
            }
      }
   }

   protected boolean func_146983_a(int par1) {
      return false;
   }

   protected void func_146976_a(float par1, int mouseX, int mouseY) {
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.field_146297_k.field_71446_o.func_110577_a(this.tex);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.func_73729_b(this.field_147003_i + this.middleX - 80, this.field_147009_r + this.middleY - 80, 96, 0, 160, 160);
      this.func_73729_b(this.field_147003_i, this.field_147009_r + 143, 0, 167, 176, 89);
      this.func_73732_a(
         this.field_146289_q,
         I18n.func_74838_a("button.category." + this.category),
         this.field_147003_i + this.middleX,
         this.field_147009_r + this.middleY - 64,
         16777215
      );
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.field_146297_k.field_71446_o.func_110577_a(this.tex);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      switch (this.category) {
         case 0:
            this.func_73729_b(this.field_147003_i + this.middleX + 17, this.field_147009_r + this.middleY + 3, 2, 18, 12, 12);
            if (this.seal.getColor() >= 1 && this.seal.getColor() <= 16) {
               Color c = new Color(EnumDyeColor.func_176764_b(this.seal.getColor() - 1).func_193350_e());
               GL11.glColor4f(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, 1.0F);
               this.func_73729_b(this.field_147003_i + this.middleX + 20, this.field_147009_r + this.middleY + 6, 74, 31, 6, 6);
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

            int mx = mouseX - this.field_147003_i;
            int my = mouseY - this.field_147009_r;
            if (mx >= this.middleX + 5 && mx <= this.middleX + 41 && my >= this.middleY + 3 && my <= this.middleY + 15) {
               if (this.seal.getColor() >= 1 && this.seal.getColor() <= 16) {
                  String s = "color." + EnumDyeColor.func_176764_b(this.seal.getColor() - 1).func_176610_l();
                  String s2 = I18n.func_74838_a("golem.prop.color");
                  s2 = s2.replace("%s", I18n.func_74838_a(s));
                  this.func_73732_a(this.field_146289_q, s2, this.field_147003_i + this.middleX + 23, this.field_147009_r + this.middleY + 17, 16777215);
               } else {
                  this.func_73732_a(
                     this.field_146289_q,
                     I18n.func_74838_a("golem.prop.colorall"),
                     this.field_147003_i + this.middleX + 23,
                     this.field_147009_r + this.middleY + 17,
                     16777215
                  );
               }
            }

            this.func_73732_a(
               this.field_146289_q,
               I18n.func_74838_a("golem.prop.priority"),
               this.field_147003_i + this.middleX,
               this.field_147009_r + this.middleY - 28,
               12299007
            );
            this.func_73732_a(
               this.field_146289_q, "" + this.seal.getPriority(), this.field_147003_i + this.middleX, this.field_147009_r + this.middleY - 16, 16777215
            );
            if (this.seal.getOwner().equals(this.field_146297_k.field_71439_g.func_110124_au().toString())) {
               this.func_73732_a(
                  this.field_146289_q,
                  I18n.func_74838_a("golem.prop.owner"),
                  this.field_147003_i + this.middleX,
                  this.field_147009_r + this.middleY + 32,
                  12299007
               );
            }
            break;
         case 1:
            if (this.seal.getSeal() instanceof ISealConfigFilter) {
               int s = ((ISealConfigFilter)this.seal.getSeal()).getFilterSize();
               int sx = 16 + (s - 1) % 3 * 12;
               int sy = 16 + (s - 1) / 3 * 12;

               for (int a = 0; a < s; a++) {
                  int x = a % 3;
                  int y = a / 3;
                  this.func_73729_b(this.field_147003_i + this.middleX + x * 24 - sx, this.field_147009_r + this.middleY + y * 24 - sy, 0, 56, 32, 32);
               }
            }
            break;
         case 2:
            this.func_73732_a(
               this.field_146289_q,
               I18n.func_74838_a("button.caption.y"),
               this.field_147003_i + this.middleX,
               this.field_147009_r + this.middleY - 24 - 9,
               14540253
            );
            this.func_73732_a(
               this.field_146289_q, I18n.func_74838_a("button.caption.x"), this.field_147003_i + this.middleX, this.field_147009_r + this.middleY - 9, 14540253
            );
            this.func_73732_a(
               this.field_146289_q,
               I18n.func_74838_a("button.caption.z"),
               this.field_147003_i + this.middleX,
               this.field_147009_r + this.middleY + 24 - 9,
               14540253
            );
            this.func_73732_a(
               this.field_146289_q,
               "" + this.seal.getArea().func_177956_o(),
               this.field_147003_i + this.middleX,
               this.field_147009_r + this.middleY - 24,
               16777215
            );
            this.func_73732_a(
               this.field_146289_q, "" + this.seal.getArea().func_177958_n(), this.field_147003_i + this.middleX, this.field_147009_r + this.middleY, 16777215
            );
            this.func_73732_a(
               this.field_146289_q,
               "" + this.seal.getArea().func_177952_p(),
               this.field_147003_i + this.middleX,
               this.field_147009_r + this.middleY + 24,
               16777215
            );
         case 3:
         default:
            break;
         case 4:
            this.func_73732_a(
               this.field_146289_q,
               I18n.func_74838_a("button.caption.required"),
               this.field_147003_i + this.middleX,
               this.field_147009_r + this.middleY - 26,
               14540253
            );
            this.func_73732_a(
               this.field_146289_q,
               I18n.func_74838_a("button.caption.forbidden"),
               this.field_147003_i + this.middleX,
               this.field_147009_r + this.middleY + 6,
               14540253
            );
      }
   }

   protected void func_146979_b(int mouseX, int mouseY) {
      RenderHelper.func_74518_a();

      for (GuiButton guibutton : this.field_146292_n) {
         if (guibutton.func_146115_a()) {
            guibutton.func_146111_b(mouseX - this.field_147003_i, mouseY - this.field_147009_r);
            break;
         }
      }

      RenderHelper.func_74520_c();
   }

   protected void func_146284_a(GuiButton button) throws IOException {
      if (button.field_146127_k < this.categories.length && this.categories[button.field_146127_k] != this.category) {
         this.category = this.categories[button.field_146127_k];
         ((SealBaseContainer)this.field_147002_h).category = this.category;
         ((SealBaseContainer)this.field_147002_h).setupCategories();
         this.field_146297_k.field_71442_b.func_78756_a(this.field_147002_h.field_75152_c, button.field_146127_k);
         this.setupCategories();
      } else if (this.category == 0
         && button.field_146127_k == 25
         && this.seal.getOwner().equals(this.field_146297_k.field_71439_g.func_110124_au().toString())) {
         this.seal.setLocked(!this.seal.isLocked());
         this.field_146297_k.field_71442_b.func_78756_a(this.field_147002_h.field_75152_c, this.seal.isLocked() ? 25 : 26);
      } else if (this.category == 1 && this.seal.getSeal() instanceof ISealConfigFilter && button.field_146127_k == 20) {
         ISealConfigFilter cp = (ISealConfigFilter)this.seal.getSeal();
         cp.setBlacklist(!cp.isBlacklist());
         this.field_146297_k.field_71442_b.func_78756_a(this.field_147002_h.field_75152_c, cp.isBlacklist() ? 20 : 21);
      } else if (this.category == 3
         && this.seal.getSeal() instanceof ISealConfigToggles
         && button.field_146127_k >= 30
         && button.field_146127_k < 30 + ((ISealConfigToggles)this.seal.getSeal()).getToggles().length) {
         ISealConfigToggles cp = (ISealConfigToggles)this.seal.getSeal();
         cp.setToggle(button.field_146127_k - 30, !cp.getToggles()[button.field_146127_k - 30].getValue());
         this.field_146297_k
            .field_71442_b
            .func_78756_a(
               this.field_147002_h.field_75152_c, cp.getToggles()[button.field_146127_k - 30].getValue() ? button.field_146127_k : button.field_146127_k + 30
            );
      } else if (button.field_146127_k == 27 && this.seal.getOwner().equals(this.field_146297_k.field_71439_g.func_110124_au().toString())) {
         this.seal.setRedstoneSensitive(!this.seal.isRedstoneSensitive());
         this.field_146297_k.field_71442_b.func_78756_a(this.field_147002_h.field_75152_c, this.seal.isRedstoneSensitive() ? 27 : 28);
      } else {
         this.field_146297_k.field_71442_b.func_78756_a(this.field_147002_h.field_75152_c, button.field_146127_k);
      }
   }

   public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
      super.func_73863_a(mouseX, mouseY, partialTicks);
      if (this.category == 1 && this.seal.getSeal() instanceof ISealConfigFilter && !((ISealConfigFilter)this.seal.getSeal()).isBlacklist()) {
         int x = this.field_147003_i;
         int y = this.field_147009_r;
         ISealConfigFilter cp = (ISealConfigFilter)this.seal.getSeal();
         int k = 240;
         int l = 240;
         OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 240.0F, 240.0F);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_73735_i = 100.0F;

         for (int i1 = 0; i1 < cp.getFilterSize(); i1++) {
            Slot slot = (Slot)this.field_147002_h.field_75151_b.get(i1);
            if (slot.func_111238_b() && !slot.func_75211_c().func_190926_b()) {
               int i = slot.field_75223_e;
               int j = slot.field_75221_f;
               int sz = cp.getFilterSlotSize(i1);
               String s = String.valueOf(cp.getFilterSlotSize(i1));
               if (sz == 0) {
                  s = TextFormatting.GOLD.toString() + "*";
               }

               GlStateManager.func_179140_f();
               GlStateManager.func_179097_i();
               GlStateManager.func_179084_k();
               this.field_146289_q.func_175063_a(s, x + i + 19 - 2 - this.field_146289_q.func_78256_a(s), y + j + 6 + 3, 16777215);
               GlStateManager.func_179145_e();
               GlStateManager.func_179126_j();
               GlStateManager.func_179147_l();
            }
         }

         this.field_73735_i = 0.0F;
         RenderHelper.func_74518_a();
         RenderHelper.func_74520_c();
         RenderHelper.func_74519_b();
      }
   }
}

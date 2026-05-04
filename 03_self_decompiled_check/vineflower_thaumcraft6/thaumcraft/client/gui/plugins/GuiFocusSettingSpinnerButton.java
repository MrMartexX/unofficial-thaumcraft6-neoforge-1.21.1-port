package thaumcraft.client.gui.plugins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.casters.NodeSetting;

public class GuiFocusSettingSpinnerButton extends GuiButton {
   private NodeSetting setting;
   static ResourceLocation tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");

   public GuiFocusSettingSpinnerButton(int buttonId, int x, int y, int width, NodeSetting ns) {
      super(buttonId, x, y, width, 10, "");
      this.setting = ns;
   }

   public void func_191745_a(Minecraft mc, int xx, int yy, float pt) {
      if (this.field_146125_m) {
         FontRenderer fontrenderer = mc.field_71466_p;
         mc.func_110434_K().func_110577_a(tex);
         GlStateManager.func_179131_c(0.9F, 0.9F, 0.9F, 0.9F);
         this.field_146123_n = xx >= this.field_146128_h
            && yy >= this.field_146129_i
            && xx < this.field_146128_h + this.field_146120_f + 10
            && yy < this.field_146129_i + this.field_146121_g;
         int k = this.func_146114_a(this.field_146123_n);
         if (k == 2) {
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         }

         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         GlStateManager.func_179112_b(770, 771);
         this.func_73729_b(this.field_146128_h, this.field_146129_i, 20, 0, 10, 10);
         this.func_73729_b(this.field_146128_h + this.field_146120_f, this.field_146129_i, 30, 0, 10, 10);
         String s = this.setting.getValueText();
         fontrenderer.func_175063_a(
            s, this.field_146128_h + (this.field_146120_f + 10) / 2 - fontrenderer.func_78256_a(s) / 2, this.field_146129_i + 1, 16777215
         );
         this.func_146119_b(mc, xx, yy);
      }
   }

   public boolean func_146116_c(Minecraft mc, int mouseX, int mouseY) {
      if (this.field_146124_l && this.field_146125_m) {
         if (mouseX >= this.field_146128_h
            && mouseY >= this.field_146129_i
            && mouseX < this.field_146128_h + 10
            && mouseY < this.field_146129_i + this.field_146121_g) {
            this.setting.decrement();
            return true;
         }

         if (mouseX >= this.field_146128_h + this.field_146120_f
            && mouseY >= this.field_146129_i
            && mouseX < this.field_146128_h + this.field_146120_f + 10
            && mouseY < this.field_146129_i + this.field_146121_g) {
            this.setting.increment();
            return true;
         }
      }

      return false;
   }
}

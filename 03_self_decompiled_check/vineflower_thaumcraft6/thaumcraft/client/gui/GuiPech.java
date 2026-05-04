package thaumcraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.container.ContainerPech;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.lib.SoundsTC;

@SideOnly(Side.CLIENT)
public class GuiPech extends GuiContainer {
   EntityPech pech;
   ResourceLocation tex = new ResourceLocation("thaumcraft", "textures/gui/gui_pech.png");

   public GuiPech(InventoryPlayer par1InventoryPlayer, World world, EntityPech pech) {
      super(new ContainerPech(par1InventoryPlayer, world, pech));
      this.field_146999_f = 175;
      this.field_147000_g = 232;
      this.pech = pech;
   }

   public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
      this.func_146276_q_();
      super.func_73863_a(mouseX, mouseY, partialTicks);
      this.func_191948_b(mouseX, mouseY);
   }

   protected void func_146979_b(int par1, int par2) {
   }

   protected void func_146976_a(float par1, int par2, int par3) {
      this.field_146297_k.field_71446_o.func_110577_a(this.tex);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int var5 = (this.field_146294_l - this.field_146999_f) / 2;
      int var6 = (this.field_146295_m - this.field_147000_g) / 2;
      GL11.glEnable(3042);
      this.func_73729_b(var5, var6, 0, 0, this.field_146999_f, this.field_147000_g);
      if (this.pech.isValued(this.field_147002_h.func_75139_a(0).func_75211_c())
         && !this.field_147002_h.func_75139_a(0).func_75211_c().func_190926_b()
         && this.field_147002_h.func_75139_a(1).func_75211_c().func_190926_b()
         && this.field_147002_h.func_75139_a(2).func_75211_c().func_190926_b()
         && this.field_147002_h.func_75139_a(3).func_75211_c().func_190926_b()
         && this.field_147002_h.func_75139_a(4).func_75211_c().func_190926_b()) {
         this.func_73729_b(var5 + 67, var6 + 24, 176, 0, 25, 25);
      }

      GL11.glDisable(3042);
   }

   protected void func_73864_a(int mx, int my, int par3) throws IOException {
      super.func_73864_a(mx, my, par3);
      int gx = (this.field_146294_l - this.field_146999_f) / 2;
      int gy = (this.field_146295_m - this.field_147000_g) / 2;
      int var7 = mx - (gx + 67);
      int var8 = my - (gy + 24);
      if (var7 >= 0
         && var8 >= 0
         && var7 < 25
         && var8 < 25
         && this.pech.isValued(this.field_147002_h.func_75139_a(0).func_75211_c())
         && !this.field_147002_h.func_75139_a(0).func_75211_c().func_190926_b()
         && this.field_147002_h.func_75139_a(1).func_75211_c().func_190926_b()
         && this.field_147002_h.func_75139_a(2).func_75211_c().func_190926_b()
         && this.field_147002_h.func_75139_a(3).func_75211_c().func_190926_b()
         && this.field_147002_h.func_75139_a(4).func_75211_c().func_190926_b()) {
         this.field_146297_k.field_71442_b.func_78756_a(this.field_147002_h.field_75152_c, 0);
         this.playButton();
      }
   }

   private void playButton() {
      this.field_146297_k
         .func_175606_aa()
         .func_184185_a(SoundsTC.pech_dice, 0.5F, 0.95F + this.field_146297_k.func_175606_aa().field_70170_p.field_73012_v.nextFloat() * 0.1F);
   }
}

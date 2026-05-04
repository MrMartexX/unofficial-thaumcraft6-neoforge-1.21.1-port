package thaumcraft.client.gui;

import com.sasmaster.glelwjgl.java.CoreGLE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.ender.ShaderCallback;
import thaumcraft.common.container.ContainerVoidSiphon;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;

@SideOnly(Side.CLIENT)
public class GuiVoidSiphon extends GuiContainer {
   private TileVoidSiphon inventory;
   private ContainerVoidSiphon container = null;
   private EntityPlayer player = null;
   CoreGLE gle = new CoreGLE();
   private final ShaderCallback shaderCallback;
   private static final ResourceLocation starsTexture = new ResourceLocation("textures/entity/end_portal.png");
   ResourceLocation tex = new ResourceLocation("thaumcraft", "textures/gui/gui_void_siphon.png");

   public GuiVoidSiphon(InventoryPlayer par1InventoryPlayer, TileVoidSiphon tileVoidSiphon) {
      super(new ContainerVoidSiphon(par1InventoryPlayer, tileVoidSiphon));
      this.field_146999_f = 176;
      this.field_147000_g = 166;
      this.inventory = tileVoidSiphon;
      this.container = (ContainerVoidSiphon)this.field_147002_h;
      this.player = par1InventoryPlayer.field_70458_d;
      this.shaderCallback = new ShaderCallback() {
         @Override
         public void call(int shader) {
            Minecraft mc = Minecraft.func_71410_x();
            int x = ARBShaderObjects.glGetUniformLocationARB(shader, "yaw");
            ARBShaderObjects.glUniform1fARB(x, (float)(mc.field_71439_g.field_70177_z * 2.0F * Math.PI / 360.0));
            int z = ARBShaderObjects.glGetUniformLocationARB(shader, "pitch");
            ARBShaderObjects.glUniform1fARB(z, -((float)(mc.field_71439_g.field_70125_A * 2.0F * Math.PI / 360.0)));
         }
      };
   }

   public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
      this.func_146276_q_();
      super.func_73863_a(mouseX, mouseY, partialTicks);
      this.func_191948_b(mouseX, mouseY);
   }

   protected void func_146976_a(float par1, int mx, int my) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.field_71446_o.func_110577_a(this.tex);
      int k = (this.field_146294_l - this.field_146999_f) / 2;
      int l = (this.field_146295_m - this.field_147000_g) / 2;
      GL11.glEnable(3042);
      this.func_73729_b(k, l, 0, 0, this.field_146999_f, this.field_147000_g);
   }
}

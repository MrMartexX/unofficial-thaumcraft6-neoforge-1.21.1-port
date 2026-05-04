package thaumcraft.client.fx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@EventBusSubscriber(Side.CLIENT)
public class ParticleEngine {
   public static final ResourceLocation particleTexture = new ResourceLocation("thaumcraft", "textures/misc/particles.png");
   protected World world;
   private static HashMap<Integer, ArrayList<Particle>>[] particles = new HashMap[]{
      new HashMap(), new HashMap(), new HashMap(), new HashMap(), new HashMap(), new HashMap()
   };
   private static ArrayList<ParticleEngine.ParticleDelay> particlesDelayed = new ArrayList<>();
   private Random rand = new Random();

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void renderTick(RenderTickEvent event) {
      if (Minecraft.func_71410_x().field_71441_e != null) {
         if (event.phase == Phase.END) {
            float frame = event.renderTickTime;
            Entity entity = Minecraft.func_71410_x().field_71439_g;
            TextureManager renderer = Minecraft.func_71410_x().field_71446_o;
            int dim = Minecraft.func_71410_x().field_71441_e.field_73011_w.getDimension();
            GL11.glPushMatrix();
            ScaledResolution sr = new ScaledResolution(Minecraft.func_71410_x());
            GL11.glClear(256);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, sr.func_78327_c(), sr.func_78324_d(), 0.0, 1000.0, 3000.0);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.func_179147_l();
            GL11.glEnable(3042);
            GL11.glAlphaFunc(516, 0.003921569F);
            renderer.func_110577_a(particleTexture);
            GlStateManager.func_179132_a(false);

            for (int layer = 5; layer >= 4; layer--) {
               if (particles[layer].containsKey(dim)) {
                  ArrayList<Particle> parts = particles[layer].get(dim);
                  if (parts.size() != 0) {
                     switch (layer) {
                        case 4:
                           GlStateManager.func_179112_b(770, 1);
                           break;
                        case 5:
                           GlStateManager.func_179112_b(770, 771);
                     }

                     Tessellator tessellator = Tessellator.func_178181_a();
                     BufferBuilder buffer = tessellator.func_178180_c();

                     for (int j = 0; j < parts.size(); j++) {
                        final Particle Particle = parts.get(j);
                        if (Particle != null) {
                           try {
                              Particle.func_180434_a(buffer, entity, frame, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
                           } catch (Throwable throwable) {
                              CrashReport crashreport = CrashReport.func_85055_a(throwable, "Rendering Particle");
                              CrashReportCategory crashreportcategory = crashreport.func_85058_a("Particle being rendered");
                              crashreportcategory.func_189529_a("Particle", new ICrashReportDetail<String>() {
                                 public String call() {
                                    return Particle.toString();
                                 }
                              });
                              crashreportcategory.func_189529_a("Particle Type", new ICrashReportDetail<String>() {
                                 public String call() {
                                    return "ENTITY_PARTICLE_TEXTURE";
                                 }
                              });
                              throw new ReportedException(crashreport);
                           }
                        }
                     }
                  }
               }
            }

            GlStateManager.func_179132_a(true);
            GlStateManager.func_179112_b(770, 771);
            GlStateManager.func_179084_k();
            GlStateManager.func_179092_a(516, 0.1F);
            GL11.glPopMatrix();
         }
      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void onRenderWorldLast(RenderWorldLastEvent event) {
      float frame = event.getPartialTicks();
      Entity entity = Minecraft.func_71410_x().field_71439_g;
      TextureManager renderer = Minecraft.func_71410_x().field_71446_o;
      int dim = Minecraft.func_71410_x().field_71441_e.field_73011_w.getDimension();
      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179147_l();
      GL11.glEnable(3042);
      GL11.glAlphaFunc(516, 0.003921569F);
      renderer.func_110577_a(particleTexture);
      GlStateManager.func_179132_a(false);

      for (int layer = 3; layer >= 0; layer--) {
         if (particles[layer].containsKey(dim)) {
            ArrayList<Particle> parts = particles[layer].get(dim);
            if (parts.size() != 0) {
               switch (layer) {
                  case 0:
                     GlStateManager.func_179112_b(770, 1);
                     break;
                  case 1:
                     GlStateManager.func_179112_b(770, 771);
                     break;
                  case 2:
                     GlStateManager.func_179112_b(770, 1);
                     GlStateManager.func_179097_i();
                     break;
                  case 3:
                     GlStateManager.func_179112_b(770, 771);
                     GlStateManager.func_179097_i();
               }

               float f1 = ActiveRenderInfo.func_178808_b();
               float f2 = ActiveRenderInfo.func_178803_d();
               float f3 = ActiveRenderInfo.func_178805_e();
               float f4 = ActiveRenderInfo.func_178807_f();
               float f5 = ActiveRenderInfo.func_178809_c();
               Particle.field_70556_an = entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * frame;
               Particle.field_70554_ao = entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * frame;
               Particle.field_70555_ap = entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * frame;
               Tessellator tessellator = Tessellator.func_178181_a();
               BufferBuilder buffer = tessellator.func_178180_c();
               buffer.func_181668_a(7, DefaultVertexFormats.field_181704_d);

               for (int j = 0; j < parts.size(); j++) {
                  final Particle Particle = parts.get(j);
                  if (Particle != null) {
                     try {
                        Particle.func_180434_a(buffer, entity, frame, f1, f5, f2, f3, f4);
                     } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.func_85055_a(throwable, "Rendering Particle");
                        CrashReportCategory crashreportcategory = crashreport.func_85058_a("Particle being rendered");
                        crashreportcategory.func_189529_a("Particle", new ICrashReportDetail<String>() {
                           public String call() {
                              return Particle.toString();
                           }
                        });
                        crashreportcategory.func_189529_a("Particle Type", new ICrashReportDetail<String>() {
                           public String call() {
                              return "ENTITY_PARTICLE_TEXTURE";
                           }
                        });
                        throw new ReportedException(crashreport);
                     }
                  }
               }

               tessellator.func_78381_a();
               switch (layer) {
                  case 2:
                  case 3:
                     GlStateManager.func_179126_j();
               }
            }
         }
      }

      GlStateManager.func_179132_a(true);
      GlStateManager.func_179112_b(770, 771);
      GlStateManager.func_179084_k();
      GlStateManager.func_179092_a(516, 0.1F);
      GL11.glPopMatrix();
   }

   public static void addEffect(World world, Particle fx) {
      addEffect(world.field_73011_w.getDimension(), fx);
   }

   private static int getParticleLimit() {
      return FMLClientHandler.instance().getClient().field_71474_y.field_74362_aa == 2
         ? 500
         : (FMLClientHandler.instance().getClient().field_71474_y.field_74362_aa == 1 ? 1000 : 2000);
   }

   public static void addEffect(int dim, Particle fx) {
      if (Minecraft.func_71410_x().field_71441_e != null) {
         int ps = FMLClientHandler.instance().getClient().field_71474_y.field_74362_aa;
         Minecraft.func_71410_x();
         if (Minecraft.func_175610_ah() < 30) {
            ps++;
         }

         if (Minecraft.func_71410_x().field_71441_e.field_73012_v.nextInt(3) >= ps) {
            if (!particles[fx.func_70537_b()].containsKey(dim)) {
               particles[fx.func_70537_b()].put(dim, new ArrayList<>());
            }

            ArrayList<Particle> parts = particles[fx.func_70537_b()].get(dim);
            if (parts.size() >= getParticleLimit()) {
               parts.remove(0);
            }

            parts.add(fx);
            particles[fx.func_70537_b()].put(dim, parts);
         }
      }
   }

   public static void addEffectWithDelay(World world, Particle fx, int delay) {
      particlesDelayed.add(new ParticleEngine.ParticleDelay(fx, world.field_73011_w.getDimension(), delay));
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void updateParticles(ClientTickEvent event) {
      if (event.side != Side.SERVER) {
         Minecraft mc = FMLClientHandler.instance().getClient();
         World world = mc.field_71441_e;
         if (mc.field_71441_e != null) {
            int dim = world.field_73011_w.getDimension();
            if (event.phase == Phase.START) {
               try {
                  Iterator<ParticleEngine.ParticleDelay> i = particlesDelayed.iterator();

                  while (i.hasNext()) {
                     ParticleEngine.ParticleDelay pd = i.next();
                     pd.delay--;
                     if (pd.delay <= 0) {
                        if (pd.dim == dim) {
                           addEffect(pd.dim, pd.particle);
                        }

                        i.remove();
                     }
                  }
               } catch (Exception var13) {
               }

               for (int layer = 0; layer < 6; layer++) {
                  if (particles[layer].containsKey(dim)) {
                     ArrayList<Particle> parts = particles[layer].get(dim);

                     for (int j = 0; j < parts.size(); j++) {
                        final Particle Particle = parts.get(j);

                        try {
                           if (Particle != null) {
                              Particle.func_189213_a();
                           }
                        } catch (Exception var12) {
                           Exception e = var12;

                           try {
                              CrashReport crashreport = CrashReport.func_85055_a(e, "Ticking Particle");
                              CrashReportCategory crashreportcategory = crashreport.func_85058_a("Particle being ticked");
                              crashreportcategory.func_71507_a("Particle", new Callable() {
                                 public String call() {
                                    return Particle.toString();
                                 }
                              });
                              crashreportcategory.func_71507_a("Particle Type", new Callable() {
                                 public String call() {
                                    return "ENTITY_PARTICLE_TEXTURE";
                                 }
                              });
                              Particle.func_187112_i();
                           } catch (Exception var11) {
                           }
                        }

                        if (Particle == null || !Particle.func_187113_k()) {
                           parts.remove(j--);
                           particles[layer].put(dim, parts);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static class ParticleDelay {
      Particle particle;
      int dim;
      int level;
      int delay;

      public ParticleDelay(Particle particle, int dim, int delay) {
         this.dim = dim;
         this.particle = particle;
         this.delay = delay;
      }
   }
}

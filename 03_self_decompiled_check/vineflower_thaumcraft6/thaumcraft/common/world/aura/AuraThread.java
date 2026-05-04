package thaumcraft.common.world.aura;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import thaumcraft.Thaumcraft;
import thaumcraft.common.lib.events.ServerEvents;

public class AuraThread implements Runnable {
   public int dim;
   private final long INTERVAL = 1000L;
   private boolean stop = false;
   Random rand = new Random(System.currentTimeMillis());
   private float phaseVis = 0.0F;
   private float phaseFlux = 0.0F;
   private float phaseMax = 0.0F;
   private long lastWorldTime = 0L;
   private float[] phaseTable = new float[]{0.25F, 0.15F, 0.1F, 0.05F, 0.0F, 0.05F, 0.1F, 0.15F};
   private float[] maxTable = new float[]{0.15F, 0.05F, 0.0F, -0.05F, -0.15F, -0.05F, 0.0F, 0.05F};

   public AuraThread(int dim2) {
      this.dim = dim2;
   }

   @Override
   public void run() {
      Thaumcraft.log.info("Starting aura thread for dim " + this.dim);

      while (!this.stop) {
         if (AuraHandler.auras.isEmpty()) {
            Thaumcraft.log.warn("No auras found!");
            break;
         }

         long startTime = System.currentTimeMillis();
         AuraWorld auraWorld = AuraHandler.getAuraWorld(this.dim);
         if (auraWorld != null) {
            World world = DimensionManager.getWorld(this.dim);
            if (this.lastWorldTime != world.func_72820_D()) {
               this.lastWorldTime = world.func_72820_D();
               if (world != null) {
                  this.phaseVis = this.phaseTable[world.field_73011_w.func_76559_b(world.func_72912_H().func_76073_f())];
                  this.phaseMax = 1.0F + this.maxTable[world.field_73011_w.func_76559_b(world.func_72912_H().func_76073_f())];
                  this.phaseFlux = 0.25F - this.phaseVis;
               }

               for (AuraChunk auraChunk : auraWorld.auraChunks.values()) {
                  this.processAuraChunk(auraWorld, auraChunk);
               }
            }
         } else {
            this.stop();
         }

         long executionTime = System.currentTimeMillis() - startTime;

         try {
            if (executionTime > 1000L) {
               Thaumcraft.log.warn("AURAS TAKING " + (executionTime - 1000L) + " ms LONGER THAN NORMAL IN DIM " + this.dim);
            }

            Thread.sleep(Math.max(1L, 1000L - executionTime));
         } catch (InterruptedException var8) {
         }
      }

      Thaumcraft.log.info("Stopping aura thread for dim " + this.dim);

      try {
         ServerEvents.auraThreads.remove(this.dim);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void processAuraChunk(AuraWorld auraWorld, AuraChunk auraChunk) {
      List<Integer> directions = Arrays.asList(0, 1, 2, 3);
      Collections.shuffle(directions, this.rand);
      int x = auraChunk.loc.field_77276_a;
      int y = auraChunk.loc.field_77275_b;
      float base = auraChunk.getBase() * this.phaseMax;
      boolean dirty = false;
      float currentVis = auraChunk.getVis();
      float currentFlux = auraChunk.getFlux();
      AuraChunk neighbourVisChunk = null;
      AuraChunk neighbourFluxChunk = null;
      float lowestVis = Float.MAX_VALUE;
      float lowestFlux = Float.MAX_VALUE;

      for (Integer a : directions) {
         EnumFacing dir = EnumFacing.func_176731_b(a);
         AuraChunk n = auraWorld.getAuraChunkAt(x + dir.func_82601_c(), y + dir.func_82599_e());
         if (n != null) {
            if ((neighbourVisChunk == null || lowestVis > n.getVis()) && n.getVis() + n.getFlux() < n.getBase() * this.phaseMax) {
               neighbourVisChunk = n;
               lowestVis = n.getVis();
            }

            if (neighbourFluxChunk == null || lowestFlux > n.getFlux()) {
               neighbourFluxChunk = n;
               lowestFlux = n.getFlux();
            }
         }
      }

      if (neighbourVisChunk != null && lowestVis < currentVis && lowestVis / currentVis < 0.75) {
         float inc = Math.min(currentVis - lowestVis, 1.0F);
         currentVis -= inc;
         neighbourVisChunk.setVis(lowestVis + inc);
         dirty = true;
         this.markChunkAsDirty(neighbourVisChunk, auraWorld.dim);
      }

      if (neighbourFluxChunk != null && currentFlux > Math.max(5.0F, auraChunk.getBase() / 10.0F) && lowestFlux < currentFlux / 1.75) {
         float inc = Math.min(currentFlux - lowestFlux, 1.0F);
         currentFlux -= inc;
         neighbourFluxChunk.setFlux(lowestFlux + inc);
         dirty = true;
         this.markChunkAsDirty(neighbourFluxChunk, auraWorld.dim);
      }

      if (currentVis + currentFlux < base) {
         float inc = Math.min(base - (currentVis + currentFlux), this.phaseVis);
         currentVis += inc;
         dirty = true;
      } else if (currentVis > base * 1.25 && this.rand.nextFloat() < 0.1) {
         currentFlux += this.phaseFlux;
         currentVis -= this.phaseFlux;
         dirty = true;
      } else if (currentVis <= base * 0.1 && currentVis >= currentFlux && this.rand.nextFloat() < 0.1) {
         currentFlux += this.phaseFlux;
         dirty = true;
      }

      if (dirty) {
         auraChunk.setVis(currentVis);
         auraChunk.setFlux(currentFlux);
         this.markChunkAsDirty(auraChunk, auraWorld.dim);
      }

      if (currentFlux > base * 0.75 && this.rand.nextFloat() < currentFlux / 500.0F / 10.0F) {
         AuraHandler.riftTrigger.put(auraWorld.dim, new BlockPos(x * 16, 0, y * 16));
      }
   }

   private void markChunkAsDirty(AuraChunk chunk, int dim) {
      if (!chunk.isModified()) {
         ChunkPos pos = new ChunkPos(chunk.loc.field_77276_a, chunk.loc.field_77275_b);
         if (!AuraHandler.dirtyChunks.containsKey(dim)) {
            AuraHandler.dirtyChunks.put(dim, new CopyOnWriteArrayList<>());
         }

         CopyOnWriteArrayList<ChunkPos> dc = AuraHandler.dirtyChunks.get(dim);
         if (!dc.contains(pos)) {
            dc.add(pos);
         }
      }
   }

   public void stop() {
      this.stop = true;
   }
}

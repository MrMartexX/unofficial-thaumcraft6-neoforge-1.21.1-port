package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.IFocusElement;

public class PacketFXFocusEffect implements IMessage, IMessageHandler<PacketFXFocusEffect, IMessage> {
   float x;
   float y;
   float z;
   float mx;
   float my;
   float mz;
   String parts;

   public PacketFXFocusEffect() {
   }

   public PacketFXFocusEffect(float x, float y, float z, float mx, float my, float mz, String[] parts) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.mx = mx;
      this.my = my;
      this.mz = mz;
      this.parts = "";

      for (int a = 0; a < parts.length; a++) {
         if (a > 0) {
            this.parts = this.parts + "%";
         }

         this.parts = this.parts + parts[a];
      }
   }

   public void toBytes(ByteBuf buffer) {
      buffer.writeFloat(this.x);
      buffer.writeFloat(this.y);
      buffer.writeFloat(this.z);
      buffer.writeFloat(this.mx);
      buffer.writeFloat(this.my);
      buffer.writeFloat(this.mz);
      ByteBufUtils.writeUTF8String(buffer, this.parts);
   }

   public void fromBytes(ByteBuf buffer) {
      this.x = buffer.readFloat();
      this.y = buffer.readFloat();
      this.z = buffer.readFloat();
      this.mx = buffer.readFloat();
      this.my = buffer.readFloat();
      this.mz = buffer.readFloat();
      this.parts = ByteBufUtils.readUTF8String(buffer);
   }

   public IMessage onMessage(final PacketFXFocusEffect message, MessageContext ctx) {
      Minecraft.func_71410_x().func_152344_a(new Runnable() {
         @Override
         public void run() {
            PacketFXFocusEffect.this.processMessage(message);
         }
      });
      return null;
   }

   @SideOnly(Side.CLIENT)
   void processMessage(PacketFXFocusEffect message) {
      String[] partKeys = message.parts.split("%");
      int amt = Math.max(1, 10 / partKeys.length);

      for (String k : partKeys) {
         IFocusElement part = FocusEngine.getElement(k);
         if (part != null && part instanceof FocusEffect) {
            for (int a = 0; a < amt; a++) {
               FocusEffect var10000 = (FocusEffect)part;
               double var10002 = message.x;
               double var10003 = message.y;
               var10000.renderParticleFX(
                  Minecraft.func_71410_x().field_71441_e,
                  var10002,
                  var10003,
                  message.z,
                  message.mx + Minecraft.func_71410_x().field_71441_e.field_73012_v.nextGaussian() / 20.0,
                  message.my + Minecraft.func_71410_x().field_71441_e.field_73012_v.nextGaussian() / 20.0,
                  message.mz + Minecraft.func_71410_x().field_71441_e.field_73012_v.nextGaussian() / 20.0
               );
            }
         }
      }
   }
}

package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import java.awt.Color;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;

public class PacketFXZap implements IMessage, IMessageHandler<PacketFXZap, IMessage> {
   private Vec3d source;
   private Vec3d target;
   private int color;
   private float width;

   public PacketFXZap() {
   }

   public PacketFXZap(Vec3d source, Vec3d target, int color, float width) {
      this.source = source;
      this.target = target;
      this.color = color;
      this.width = width;
   }

   public void toBytes(ByteBuf buffer) {
      buffer.writeDouble(this.source.field_72450_a);
      buffer.writeDouble(this.source.field_72448_b);
      buffer.writeDouble(this.source.field_72449_c);
      buffer.writeDouble(this.target.field_72450_a);
      buffer.writeDouble(this.target.field_72448_b);
      buffer.writeDouble(this.target.field_72449_c);
      buffer.writeInt(this.color);
      buffer.writeFloat(this.width);
   }

   public void fromBytes(ByteBuf buffer) {
      this.source = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
      this.target = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
      this.color = buffer.readInt();
      this.width = buffer.readFloat();
   }

   @SideOnly(Side.CLIENT)
   public IMessage onMessage(PacketFXZap message, MessageContext ctx) {
      Color c = new Color(message.color);
      FXDispatcher.INSTANCE
         .arcBolt(
            message.source.field_72450_a,
            message.source.field_72448_b,
            message.source.field_72449_c,
            message.target.field_72450_a,
            message.target.field_72448_b,
            message.target.field_72449_c,
            c.getRed() / 255.0F,
            c.getGreen() / 255.0F,
            c.getBlue() / 255.0F,
            message.width
         );
      return null;
   }
}

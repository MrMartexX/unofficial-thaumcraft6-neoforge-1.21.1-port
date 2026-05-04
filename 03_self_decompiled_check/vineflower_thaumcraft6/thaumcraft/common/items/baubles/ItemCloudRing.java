package thaumcraft.common.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeHooks;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketPlayerFlagToServer;

public class ItemCloudRing extends ItemTCBase implements IBauble {
   public static HashMap<String, Boolean> jumpList = new HashMap<>();

   public ItemCloudRing() {
      super("cloud_ring");
      this.field_77777_bU = 1;
      this.canRepair = false;
      this.func_77656_e(0);
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return EnumRarity.RARE;
   }

   @Override
   public BaubleType getBaubleType(ItemStack itemstack) {
      return BaubleType.RING;
   }

   @Override
   public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
      if (player.func_130014_f_().field_72995_K) {
         boolean spacePressed = Minecraft.func_71410_x().field_71474_y.field_74314_A.func_151468_f();
         if (spacePressed && !jumpList.containsKey(player.func_70005_c_())) {
            jumpList.put(player.func_70005_c_(), true);
         }

         if (spacePressed
            && !player.field_70122_E
            && !player.func_70090_H()
            && player.field_70773_bE == 0
            && jumpList.containsKey(player.func_70005_c_())
            && jumpList.get(player.func_70005_c_())) {
            FXDispatcher.INSTANCE
               .drawBamf(player.field_70165_t, player.field_70163_u + 0.5, player.field_70161_v, 1.0F, 1.0F, 1.0F, false, false, EnumFacing.UP);
            player.func_130014_f_()
               .func_184134_a(
                  player.field_70165_t,
                  player.field_70163_u,
                  player.field_70161_v,
                  SoundEvents.field_187730_dW,
                  SoundCategory.PLAYERS,
                  0.1F,
                  1.0F + (float)player.func_130014_f_().field_73012_v.nextGaussian() * 0.05F,
                  false
               );
            jumpList.put(player.func_70005_c_(), false);
            player.field_70181_x = 0.75;
            if (player.func_70644_a(MobEffects.field_76430_j)) {
               player.field_70181_x = player.field_70181_x + (player.func_70660_b(MobEffects.field_76430_j).func_76458_c() + 1) * 0.1F;
            }

            if (player.func_70051_ag()) {
               float f = player.field_70177_z * (float) (Math.PI / 180.0);
               player.field_70159_w = player.field_70159_w - MathHelper.func_76126_a(f) * 0.2F;
               player.field_70179_y = player.field_70179_y + MathHelper.func_76134_b(f) * 0.2F;
            }

            player.field_70143_R = 0.0F;
            PacketHandler.INSTANCE.sendToServer(new PacketPlayerFlagToServer(player, 1));
            ForgeHooks.onLivingJump(player);
         }

         if (player.field_70122_E && player.field_70773_bE == 0) {
            jumpList.remove(player.func_70005_c_());
         }
      }
   }
}

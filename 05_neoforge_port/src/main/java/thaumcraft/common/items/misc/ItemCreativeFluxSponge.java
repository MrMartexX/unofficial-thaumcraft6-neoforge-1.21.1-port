package thaumcraft.common.items.misc;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.entities.TCFluxRiftEntity;
import thaumcraft.common.registry.TCSounds;

public class ItemCreativeFluxSponge extends Item {

    public ItemCreativeFluxSponge(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Right-click to drain all").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("flux from 9x9 chunk area").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("Also removes flux rifts").withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.literal("if used while sneaking.").withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.literal("Creative only").withStyle(ChatFormatting.DARK_PURPLE));
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            player.swing(hand);
            level.playSound(player, player.getX(), player.getY(), player.getZ(), TCSounds.CRAFTSTART.get(), SoundSource.PLAYERS, 0.15F, 1.0F);
        } else {
            int drained = 0;
            BlockPos p = player.blockPosition();
            for (int x = -4; x <= 4; ++x) {
                for (int z = -4; z <= 4; ++z) {
                    drained += (int) AuraHelper.drainFlux(level, p.offset(16 * x, 0, 16 * z), 500.0F, false);
                }
            }
            player.displayClientMessage(Component.literal(ChatFormatting.GREEN + "" + drained + " flux drained from 81 chunks."), false);
            if (player.isShiftKeyDown()) {
                AABB box = new AABB(player.blockPosition()).inflate(32.0D);
                List<TCFluxRiftEntity> rifts = level.getEntitiesOfClass(TCFluxRiftEntity.class, box);
                for (TCFluxRiftEntity rift : rifts) {
                    rift.discard();
                }
                player.displayClientMessage(Component.literal(ChatFormatting.DARK_AQUA + "" + rifts.size() + " flux rifts removed."), false);
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }
}

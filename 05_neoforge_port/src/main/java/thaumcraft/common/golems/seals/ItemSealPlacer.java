package thaumcraft.common.golems.seals;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.common.registry.TCItems;

public class ItemSealPlacer extends Item implements ISealDisplayer {
    private final String sealKey;

    public ItemSealPlacer(Properties properties, String sealKey) {
        super(properties);
        this.sealKey = sealKey;
    }

    public String getSealKey() {
        return sealKey;
    }

    public static ItemStack getSealStack(String sealKey) {
        if (sealKey == null) return ItemStack.EMPTY;
        return switch (sealKey) {
            case "thaumcraft:pickup" -> new ItemStack(TCItems.SEAL_PICKUP.get());
            case "thaumcraft:pickup_advanced" -> new ItemStack(TCItems.SEAL_PICKUP_ADVANCED.get());
            case "thaumcraft:fill" -> new ItemStack(TCItems.SEAL_FILL.get());
            case "thaumcraft:fill_advanced" -> new ItemStack(TCItems.SEAL_FILL_ADVANCED.get());
            case "thaumcraft:empty" -> new ItemStack(TCItems.SEAL_EMPTY.get());
            case "thaumcraft:empty_advanced" -> new ItemStack(TCItems.SEAL_EMPTY_ADVANCED.get());
            case "thaumcraft:guard" -> new ItemStack(TCItems.SEAL_GUARD.get());
            case "thaumcraft:guard_advanced" -> new ItemStack(TCItems.SEAL_GUARD_ADVANCED.get());
            case "thaumcraft:breaker" -> new ItemStack(TCItems.SEAL_BREAKER.get());
            case "thaumcraft:breaker_advanced" -> new ItemStack(TCItems.SEAL_BREAKER_ADVANCED.get());
            case "thaumcraft:lumber" -> new ItemStack(TCItems.SEAL_LUMBER.get());
            case "thaumcraft:provider" -> new ItemStack(TCItems.SEAL_PROVIDER.get());
            case "thaumcraft:stock" -> new ItemStack(TCItems.SEAL_STOCK.get());
            case "thaumcraft:use" -> new ItemStack(TCItems.SEAL_USE.get());
            case "thaumcraft:butcher" -> new ItemStack(TCItems.SEAL_BUTCHER.get());
            case "thaumcraft:harvest" -> new ItemStack(TCItems.SEAL_HARVEST.get());
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player != null && player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ISeal proto = SealHandler.getSeal(sealKey);
        if (proto == null || !proto.canPlaceAt(level, pos, face)) {
            return InteractionResult.FAIL;
        }

        ISeal newSeal;
        try {
            newSeal = proto.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            newSeal = proto;
        }

        if (SealHandler.addSealEntity(level, pos, face, newSeal, player)) {
            level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (player == null || !player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.CONSUME;
        }

        return InteractionResult.FAIL;
    }
}

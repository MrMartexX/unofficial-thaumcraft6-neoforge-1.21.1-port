package thaumcraft.common.golems;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.common.entities.TCGolemEntity;
import thaumcraft.common.registry.TCEntityTypes;

public class ItemGolemPlacer extends Item implements ISealDisplayer {

    public ItemGolemPlacer(Properties properties) {
        super(properties);
    }

    public static IGolemProperties getProperties(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("props")) {
            long p = customData.copyTag().getLong("props");
            return GolemProperties.fromLong(p);
        }
        return GolemProperties.fromLong(0L);
    }

    public static void setProperties(ItemStack stack, IGolemProperties props) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putLong("props", props.toLong()));
    }

    public static int getXp(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("xp")) {
            return customData.copyTag().getInt("xp");
        }
        return 0;
    }

    public static void setXp(ItemStack stack, int xp) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt("xp", xp));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("props")) {
            CompoundTag tag = customData.copyTag();
            IGolemProperties props = GolemProperties.fromLong(tag.getLong("props"));
            if (props.hasTrait(EnumGolemTrait.SMART)) {
                if (props.getRank() >= 10) {
                    tooltip.add(Component.translatable("golem.rank").append(" " + props.getRank()).withStyle(ChatFormatting.GOLD));
                } else {
                    int rx = tag.getInt("xp");
                    int xn = (props.getRank() + 1) * (props.getRank() + 1) * 1000;
                    tooltip.add(Component.translatable("golem.rank").append(" " + props.getRank() + " (" + rx + "/" + xn + ")").withStyle(ChatFormatting.GOLD));
                }
            }
            tooltip.add(props.getMaterial().getLocalizedName().copy().withStyle(ChatFormatting.GREEN));
            for (EnumGolemTrait trait : props.getTraits()) {
                tooltip.add(Component.literal("-" + trait.name().toLowerCase()).withStyle(ChatFormatting.BLUE));
            }
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (player == null || !level.mayInteract(player, pos) || !player.mayUseItemAt(pos.relative(face), face, stack)) return InteractionResult.FAIL;

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos spawnPos = pos.relative(face);
        TCGolemEntity golem = TCEntityTypes.GOLEM.get().create(level);
        if (golem != null) {
            golem.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, player != null ? player.getYRot() : 0.0F, 0.0F);
            golem.setProperties(getProperties(stack));
            golem.restrictTo(spawnPos, golem.getProperties().hasTrait(EnumGolemTrait.SCOUT) ? 48 : 32);
            golem.setRankXp(getXp(stack));
            if (player != null) {
                golem.setOwnerUUID(player.getUUID());
                golem.setOwned(true);
            }
            if (!level.addFreshEntity(golem)) return InteractionResult.FAIL;
            if (player == null || !player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}

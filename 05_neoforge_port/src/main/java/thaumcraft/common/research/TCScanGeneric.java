package thaumcraft.common.research;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ScanningManager;

public final class TCScanGeneric implements IScanThing {
    @Override
    public boolean checkThing(ServerPlayer player, Object object) {
        AspectList aspects = getAspects(player, object);
        return aspects != null && aspects.size() > 0 && aspects.visSize() > 0;
    }

    @Override
    public String getResearchKey(ServerPlayer player, Object object) {
        if (object instanceof Entity entity && !(entity instanceof ItemEntity)) {
            return "!" + BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        }

        ItemStack stack = ScanningManager.getItemFromParams(player, object);
        if (!stack.isEmpty()) {
            return "!" + BuiltInRegistries.ITEM.getKey(stack.getItem());
        }

        if (object instanceof BlockPos pos) {
            return "!" + BuiltInRegistries.BLOCK.getKey(player.level().getBlockState(pos).getBlock());
        }

        return null;
    }

    @Override
    public void onSuccess(ServerPlayer player, Object object) {
        AspectList aspects = getAspects(player, object);
        if (aspects == null || aspects.size() == 0 || aspects.visSize() == 0) {
            return;
        }

        TCResearchManager.addObservationFromScan(player, aspects);
    }

    private static AspectList getAspects(ServerPlayer player, Object object) {
        if (object instanceof Entity entity && !(entity instanceof ItemEntity)) {
            return AspectHelper.getEntityAspects(entity);
        }

        ItemStack stack = ScanningManager.getItemFromParams(player, object);
        if (stack.isEmpty()) {
            return null;
        }

        AspectList aspects = AspectHelper.getScanAspects(stack);
        if (aspects == null || aspects.size() == 0 || aspects.visSize() == 0) {
            return AspectHelper.generateTags(stack);
        }

        return aspects;
    }
}

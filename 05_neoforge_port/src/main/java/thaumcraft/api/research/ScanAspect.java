package thaumcraft.api.research;

import java.util.Objects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.research.TCKnowledgeType;
import thaumcraft.common.research.TCPlayerKnowledgeStore;

public class ScanAspect implements IScanThing {
    private final String research;
    private final Aspect aspect;

    public ScanAspect(String research, Aspect aspect) {
        this.research = research;
        this.aspect = aspect;
    }

    @Override
    public boolean checkThing(ServerPlayer player, Object object) {
        AspectList aspects = getAspects(player, object);
        return aspects != null && aspects.getAmount(aspect) > 0;
    }

    @Override
    public void onSuccess(ServerPlayer player, Object object) {
        if (player == null) {
            return;
        }

        TCPlayerKnowledgeStore.mutate(player, knowledge -> {
            knowledge.addRaw(TCKnowledgeType.OBSERVATION, "AUROMANCY", 1);
            knowledge.addRaw(TCKnowledgeType.OBSERVATION, "BASICS", 1);
            knowledge.addRaw(TCKnowledgeType.OBSERVATION, "ALCHEMY", 1);
        });
    }

    @Override
    public String getResearchKey(ServerPlayer player, Object object) {
        return research;
    }

    private static AspectList getAspects(ServerPlayer player, Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof Entity entity && !(entity instanceof ItemEntity)) {
            return AspectHelper.getEntityAspects(entity);
        }

        ItemStack stack = ItemStack.EMPTY;
        if (object instanceof ItemStack itemStack) {
            stack = itemStack;
        } else if (object instanceof ItemEntity itemEntity) {
            stack = itemEntity.getItem();
        } else if (player != null) {
            stack = ScanningManager.getItemFromParams(player, object);
        }

        if (stack.isEmpty()) {
            return null;
        }

        return AspectHelper.getScanAspects(stack);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ScanAspect that)) {
            return false;
        }
        return Objects.equals(research, that.research)
                && Objects.equals(aspect == null ? null : aspect.getTag(), that.aspect == null ? null : that.aspect.getTag());
    }

    @Override
    public int hashCode() {
        return Objects.hash(research, aspect == null ? null : aspect.getTag());
    }
}

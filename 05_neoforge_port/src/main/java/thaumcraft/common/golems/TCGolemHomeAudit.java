package thaumcraft.common.golems;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import thaumcraft.common.entities.TCGolemEntity;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.golems.tasks.*;

final class TCGolemHomeAudit {
    static void run(ServerLevel level, List<TCGolemBehaviorAudit.Row> rows) {
        TCGolemEntity golem = new TCGolemEntity(TCEntityTypes.GOLEM.get(), level);
        BlockPos home = level.getSharedSpawnPos();
        golem.setProperties(new GolemProperties());
        golem.setPos(home.getX() + 10, home.getY(), home.getZ());
        golem.restrictTo(home, 32);
        TCGolemHomeGoal goal = new TCGolemHomeGoal(golem);
        boolean idle = true;
        for (int i = 0; i < 10; i++) idle &= !goal.canUse();
        boolean goesHome = idle && goal.canUse();
        golem.holdItem(new ItemStack(Items.IRON_INGOT, 3));
        golem.tick();
        boolean display = golem.getFrontCarriedItem().getCount() == 3 && golem.getMainHandItem().isEmpty()
                && golem.getCarrying().get(0).getCount() == 3;
        rows.add(new TCGolemBehaviorAudit.Row("golem_home_idle_and_visual_carry", goesHome && display,
                "Legacy 10-attempt idle then return-home selection; displayed carrying is synced without duplicating equipment"));
        CompoundTag tag = new CompoundTag();
        golem.addAdditionalSaveData(tag);
        TCGolemEntity restored = new TCGolemEntity(TCEntityTypes.GOLEM.get(), level);
        restored.readAdditionalSaveData(tag);
        boolean saved = restored.hasRestriction() && restored.getRestrictCenter().equals(home) && !restored.isFollowingOwner()
                && restored.getCarrying().get(0).getCount() == 3;
        var task = new thaumcraft.api.golems.tasks.Task(new thaumcraft.api.golems.seals.SealPos(home, net.minecraft.core.Direction.UP), home);
        task.setReserved(true);
        golem.setAssignedTask(task);
        golem.setFollowingOwner(true);
        boolean following = !golem.hasRestriction() && !task.isReserved()
                && !new TCGolemTaskGoal(golem, (byte)0).canUse();
        CompoundTag followTag = new CompoundTag();
        golem.addAdditionalSaveData(followTag);
        restored.readAdditionalSaveData(followTag);
        following &= restored.isFollowingOwner() && !restored.hasRestriction();
        golem.setFollowingOwner(false);
        following &= golem.hasRestriction() && golem.getRestrictCenter().equals(golem.blockPosition());
        rows.add(new TCGolemBehaviorAudit.Row("golem_home_follow_save_and_reservation", saved && following,
                "Home/follow survive serialization; follow releases work, disables seal acquisition and detaches home; stay creates a new home"));
        task.setReserved(true);
        golem.setAssignedTask(task);
        golem.discard();
        rows.add(new TCGolemBehaviorAudit.Row("golem_removal_releases_reservation", !task.isReserved(), "Unloading or removing a working golem releases its assigned task"));
        restored.discard();
    }
}

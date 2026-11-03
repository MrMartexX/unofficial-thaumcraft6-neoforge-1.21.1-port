package thaumcraft.common.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;

public class SealUse extends SealFiltered implements ISealConfigArea {
    private int delay;
    private int watchedTask = Integer.MIN_VALUE;

    @Override
    public String getKey() {
        return "thaumcraft:use";
    }

    @Override
    public boolean canPlaceAt(Level level, BlockPos pos, Direction side) {
        return !level.isEmptyBlock(pos);
    }

    @Override
    public void tickSeal(Level level, ISealEntity seal) {
        if (delay++ % 5 != 0 || level.isEmptyBlock(seal.getSealPos().pos)) return;
        var previous = thaumcraft.common.golems.tasks.TaskHandler.getTask(level, watchedTask);
        if (previous != null && !previous.isCompleted() && !previous.isSuspended()) return;
        var task = new Task(seal.getSealPos(), seal.getSealPos().pos);
        task.setPriority(seal.getPriority());
        watchedTask = task.getId();
        thaumcraft.common.golems.tasks.TaskHandler.addTask(level, task);
    }

    @Override
    public void onTaskStarted(Level level, IGolemAPI golem, Task task) {
    }

    @Override
    public boolean onTaskCompletion(Level level, IGolemAPI golem, Task task) {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel && !level.isEmptyBlock(task.getPos())) {
            net.minecraft.world.item.ItemStack selected = net.minecraft.world.item.ItemStack.EMPTY;
            for (var carried : golem.getCarrying()) {
                if (matchesFilter(carried)) { selected = carried.copy(); break; }
            }
            if (!selected.isEmpty()) {
                var fake = net.neoforged.neoforge.common.util.FakePlayerFactory.get(serverLevel,
                        new com.mojang.authlib.GameProfile(java.util.UUID.nameUUIDFromBytes("FakeThaumcraftGolem".getBytes(java.nio.charset.StandardCharsets.UTF_8)), "FakeThaumcraftGolem"));
                fake.getInventory().clearContent();
                fake.moveTo(golem.getGolemEntity().getX(), golem.getGolemEntity().getY(), golem.getGolemEntity().getZ(),
                        golem.getGolemEntity().getYRot(), golem.getGolemEntity().getXRot());
                fake.setShiftKeyDown(false);
                fake.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, golem.dropItem(selected));
                try {
                    fake.gameMode.useItemOn(fake, serverLevel, fake.getMainHandItem(), net.minecraft.world.InteractionHand.MAIN_HAND,
                            new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(task.getPos()), task.getSealPos().face, task.getPos(), false));
                    golem.addRankXp(1);
                    golem.swingArm();
                } finally {
                    for (int slot = 0; slot < fake.getInventory().getContainerSize(); slot++) {
                        var leftover = golem.holdItem(fake.getInventory().getItem(slot));
                        if (!leftover.isEmpty()) net.minecraft.world.level.block.Block.popResource(level, golem.getGolemEntity().blockPosition(), leftover);
                        fake.getInventory().setItem(slot, net.minecraft.world.item.ItemStack.EMPTY);
                    }
                }
            }
        }
        task.setSuspended(true);
        return true;
    }

    @Override
    public void onTaskSuspension(Level level, Task task) {
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        return golem.getProperties().hasTrait(EnumGolemTrait.DEFT)
                && golem.getProperties().hasTrait(EnumGolemTrait.SMART)
                && golem.getCarrying().stream().anyMatch(this::matchesFilter);
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/seals/seal_use.png");
    }

    @Override
    public void onRemoval(Level level, BlockPos pos, Direction side) {
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.DEFT, EnumGolemTrait.SMART};
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[0];
    }
}

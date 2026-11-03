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

public class SealUse extends SealFiltered implements thaumcraft.api.golems.seals.ISealConfigToggles {
    private int delay;
    private int watchedTask = Integer.MIN_VALUE;

    @Override
    public String getKey() {
        return "thaumcraft:use";
    }

    @Override
    public boolean canPlaceAt(Level level, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    public void tickSeal(Level level, ISealEntity seal) {
        if (delay++ % 5 != 0 || level.isEmptyBlock(seal.getSealPos().pos) != option("pempty")) return;
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
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel && level.isEmptyBlock(task.getPos()) == option("pempty")) {
            net.minecraft.world.item.ItemStack selected = net.minecraft.world.item.ItemStack.EMPTY;
            for (var carried : golem.getCarrying()) {
                if (matchesFilter(carried)) { selected = carried.copy(); break; }
            }
            if (!selected.isEmpty() || option("pemptyhand")) {
                var fake = net.neoforged.neoforge.common.util.FakePlayerFactory.get(serverLevel,
                        new com.mojang.authlib.GameProfile(java.util.UUID.nameUUIDFromBytes("FakeThaumcraftGolem".getBytes(java.nio.charset.StandardCharsets.UTF_8)), "FakeThaumcraftGolem"));
                fake.getInventory().clearContent();
                fake.moveTo(golem.getGolemEntity().getX(), golem.getGolemEntity().getY(), golem.getGolemEntity().getZ(),
                        golem.getGolemEntity().getYRot(), golem.getGolemEntity().getXRot());
                fake.setShiftKeyDown(option("psneak"));
                fake.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,
                        option("pemptyhand") ? net.minecraft.world.item.ItemStack.EMPTY : golem.dropItem(selected));
                try {
                    if (option("pleft")) {
                        fake.gameMode.handleBlockBreakAction(task.getPos(), net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK,
                                task.getSealPos().face, level.getMaxBuildHeight(), 0);
                    } else {
                        if (fake.getMainHandItem().getItem() instanceof net.minecraft.world.item.BlockItem blockItem
                                && !level.isUnobstructed(blockItem.getBlock().defaultBlockState(), task.getPos(), net.minecraft.world.phys.shapes.CollisionContext.empty())) {
                            var entity = golem.getGolemEntity();
                            entity.setPos(entity.getX() + task.getSealPos().face.getStepX(), entity.getY() + task.getSealPos().face.getStepY(), entity.getZ() + task.getSealPos().face.getStepZ());
                        }
                        fake.gameMode.useItemOn(fake, serverLevel, fake.getMainHandItem(), net.minecraft.world.InteractionHand.MAIN_HAND,
                                new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(task.getPos()), task.getSealPos().face, task.getPos(), false));
                    }
                    golem.addRankXp(1);
                    golem.swingArm();
                } finally {
                    fake.setShiftKeyDown(false);
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
        if (!golem.getProperties().hasTrait(EnumGolemTrait.DEFT) || !golem.getProperties().hasTrait(EnumGolemTrait.SMART)) return false;
        if (option("pemptyhand")) return true;
        boolean found = golem.getCarrying().stream().anyMatch(this::matchesFilter);
        if (!found && option("ppro") && !isBlacklist() && !getFilterSlot(0).isEmpty()) {
            var seal = SealHandler.getSealEntity(golem.getGolemWorld(), task.getSealPos());
            if (seal != null) thaumcraft.api.golems.GolemHelper.requestProvisioning(golem.getGolemWorld(), seal, getFilterSlot(0).copy(), !option("pmeta"));
        }
        return found;
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

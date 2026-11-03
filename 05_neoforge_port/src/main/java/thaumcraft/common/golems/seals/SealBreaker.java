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

public class SealBreaker extends SealFiltered implements ISealConfigArea, thaumcraft.api.golems.seals.ISealConfigToggles {
    private int delay;
    private final java.util.Map<Integer, BlockPos> pending = new java.util.HashMap<>();
    private final java.util.Map<Integer, Integer> crackOwners = new java.util.HashMap<>();
    private final SealToggle[] miningOptions = this instanceof SealBreakerAdvanced
            ? new SealToggle[]{new SealToggle(true, "pmeta", "golem.prop.meta"), new SealToggle(false, "psilk", "golem.prop.silk")}
            : new SealToggle[]{new SealToggle(true, "pmeta", "golem.prop.meta")};

    @Override public SealToggle[] getToggles() { return miningOptions; }

    private boolean validBlock(Level level, BlockPos pos) {
        if (!level.hasChunkAt(pos)) return false;
        var state = level.getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(level, pos) < 0) return false;
        var sample = new net.minecraft.world.item.ItemStack(state.getBlock());
        for (var filter : this.filter) if (!filter.isEmpty()) {
            boolean match = option("pmeta")
                    ? sample.getItem() == filter.getItem()
                    : TCSealItemIdentity.matchesWithoutMetadata(sample, withoutCustomData(filter));
            // Legacy Breaker requires every occupied whitelist slot to match.
            if (isBlacklist() ? match : !match) return false;
        }
        return true;
    }

    private net.minecraft.world.item.ItemStack withoutCustomData(net.minecraft.world.item.ItemStack stack) {
        // OreDictionary.itemMatches compared item/metadata, not item NBT.
        return new net.minecraft.world.item.ItemStack(stack.getItem());
    }

    @Override
    public String getKey() {
        return "thaumcraft:breaker";
    }

    @Override
    public boolean canPlaceAt(Level level, BlockPos pos, Direction side) {
        return !level.isEmptyBlock(pos);
    }

    @Override
    public void tickSeal(Level level, ISealEntity seal) {
        if (delay % 100 == 0) pending.keySet().removeIf(id -> thaumcraft.common.golems.tasks.TaskHandler.getTask(level, id) == null);
        BlockPos pos = thaumcraft.api.golems.GolemHelper.getPosInArea(seal, ++delay);
        if (!pending.containsValue(pos) && validBlock(level, pos)) {
            Task task = new Task(seal.getSealPos(), pos);
            task.setPriority(seal.getPriority());
            task.setData((int)(level.getBlockState(pos).getDestroySpeed(level, pos) * 10));
            pending.put(task.getId(), pos.immutable());
            thaumcraft.common.golems.tasks.TaskHandler.addTask(level, task);
        }
    }

    @Override
    public void onTaskStarted(Level level, IGolemAPI golem, Task task) {
    }

    @Override
    public boolean onTaskCompletion(Level level, IGolemAPI golem, Task task) {
        if (level instanceof net.minecraft.server.level.ServerLevel server && pending.containsKey(task.getId()) && validBlock(level, task.getPos())) {
            var state = level.getBlockState(task.getPos());
            golem.swingArm();
            boolean silk = option("psilk");
            int speed = silk ? 7 : 21;
            if (task.getData() > speed) {
                task.setData(task.getData() - speed);
                task.setLifespan((short)Math.max(task.getLifespan(), 10));
                int progress = (int)(9 * (1 - task.getData() / (state.getDestroySpeed(level, task.getPos()) * 10)));
                int breakerId = golem.getGolemEntity().getId();
                crackOwners.put(task.getId(), breakerId);
                server.destroyBlockProgress(breakerId, task.getPos(), progress);
                var sound = state.getSoundType(level, task.getPos(), golem.getGolemEntity());
                level.playSound(null, task.getPos(), sound.getBreakSound(), net.minecraft.sounds.SoundSource.BLOCKS,
                        (sound.getVolume() + .7F) / 8, sound.getPitch() * .5F);
                return false;
            }
            clearCracks(level, task);
            if (harvest(server, task.getPos(), golem, silk)) golem.addRankXp(1);
        }
        pending.remove(task.getId());
        task.setSuspended(true);
        return true;
    }

    private boolean harvest(net.minecraft.server.level.ServerLevel level, BlockPos pos, IGolemAPI golem, boolean silk) {
        var player = net.neoforged.neoforge.common.util.FakePlayerFactory.get(level,
                new com.mojang.authlib.GameProfile(java.util.UUID.nameUUIDFromBytes("FakeThaumcraftGolem".getBytes(java.nio.charset.StandardCharsets.UTF_8)), "FakeThaumcraftGolem"));
        player.moveTo(golem.getGolemEntity().getX(), golem.getGolemEntity().getY(), golem.getGolemEntity().getZ());
        var state = level.getBlockState(pos);
        if (state.getBlock() instanceof net.minecraft.world.level.block.GameMasterBlock || !level.mayInteract(player, pos)) return false;
        if (net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.level.BlockEvent.BreakEvent(level, pos, state, player)).isCanceled()
                || level.getBlockState(pos) != state) return false;
        var tool = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_PICKAXE);
        if (silk) {
            var enchantment = level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                    .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH);
            net.minecraft.world.item.enchantment.EnchantmentHelper.updateEnchantments(tool, values -> values.set(enchantment, 1));
        }
        var drops = net.minecraft.world.level.block.Block.getDrops(state, level, pos, level.getBlockEntity(pos), player, tool);
        if (!level.destroyBlock(pos, false, player)) return false;
        for (var drop : drops) net.minecraft.world.level.block.Block.popResource(level, pos, drop);
        return true;
    }

    private void clearCracks(Level level, Task task) {
        var owner = crackOwners.remove(task.getId());
        if (owner != null && level instanceof net.minecraft.server.level.ServerLevel server) server.destroyBlockProgress(owner, task.getPos(), -1);
    }

    @Override
    public void onTaskSuspension(Level level, Task task) {
        clearCracks(level, task);
        pending.remove(task.getId());
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        boolean valid = golem.getProperties().hasTrait(EnumGolemTrait.BREAKER)
                && pending.containsKey(task.getId()) && validBlock(golem.getGolemWorld(), task.getPos());
        if (!valid) task.setSuspended(true);
        return valid;
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/seals/seal_breaker.png");
    }

    @Override
    public void onRemoval(Level level, BlockPos pos, Direction side) {
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.BREAKER};
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[0];
    }
}

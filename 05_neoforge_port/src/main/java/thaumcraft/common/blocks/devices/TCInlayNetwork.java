package thaumcraft.common.blocks.devices;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blocks.crafting.TCInfusionPedestalBlock;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.tiles.devices.TCStabilizerBlockEntity;

/** Bounded server-side equivalent of legacy inlay charge propagation. */
public final class TCInlayNetwork {
    private static final int MAX_COMPONENT_SIZE = 1024;

    public static void recalculateAround(ServerLevel level, BlockPos changedPos) {
        Set<BlockPos> seeds = new HashSet<>();
        if (isNetworkNode(level.getBlockState(changedPos))) {
            seeds.add(changedPos.immutable());
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos adjacent = changedPos.relative(direction);
            if (isNetworkNode(level.getBlockState(adjacent))) {
                seeds.add(adjacent.immutable());
            }
        }
        Set<BlockPos> processed = new HashSet<>();
        for (BlockPos seed : seeds) {
            if (!processed.contains(seed)) {
                Set<BlockPos> component = collectComponent(level, seed);
                processed.addAll(component);
                applyCharges(level, component);
            }
        }
    }

    public static int sourceStrengthAt(ServerLevel level, BlockPos pos) {
        if (!level.getBlockState(pos).is(TCBlocks.STABILIZER.get())) {
            return 0;
        }
        return level.getBlockEntity(pos) instanceof TCStabilizerBlockEntity stabilizer
                ? stabilizer.getEnergy()
                : 0;
    }

    public static boolean isNetworkNode(BlockState state) {
        return state.is(TCBlocks.INLAY.get()) || state.getBlock() instanceof TCInfusionPedestalBlock;
    }

    public static int charge(BlockState state) {
        if (state.hasProperty(TCInlayBlock.CHARGE)) {
            return state.getValue(TCInlayBlock.CHARGE);
        }
        if (state.hasProperty(TCInfusionPedestalBlock.CHARGE)) {
            return state.getValue(TCInfusionPedestalBlock.CHARGE);
        }
        return 0;
    }

    private static Set<BlockPos> collectComponent(ServerLevel level, BlockPos seed) {
        Set<BlockPos> found = new HashSet<>();
        ArrayDeque<BlockPos> open = new ArrayDeque<>();
        open.add(seed);
        while (!open.isEmpty() && found.size() < MAX_COMPONENT_SIZE) {
            BlockPos pos = open.removeFirst();
            if (!found.add(pos.immutable())) {
                continue;
            }
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos adjacent = pos.relative(direction);
                if (!found.contains(adjacent) && isNetworkNode(level.getBlockState(adjacent))) {
                    open.addLast(adjacent);
                }
            }
        }
        return found;
    }

    private static void applyCharges(ServerLevel level, Set<BlockPos> component) {
        Map<BlockPos, Integer> charges = new HashMap<>();
        PriorityQueue<ChargedPos> open = new PriorityQueue<>((a, b) -> Integer.compare(b.charge(), a.charge()));
        for (BlockPos node : component) {
            charges.put(node, 0);
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                int strength = sourceStrengthAt(level, node.relative(direction));
                if (strength > charges.get(node)) {
                    charges.put(node, strength);
                    open.add(new ChargedPos(node, strength));
                }
            }
        }
        while (!open.isEmpty()) {
            ChargedPos current = open.poll();
            if (charges.getOrDefault(current.pos(), 0) != current.charge() || current.charge() <= 1) {
                continue;
            }
            int propagated = current.charge() - 1;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos adjacent = current.pos().relative(direction);
                if (component.contains(adjacent) && propagated > charges.getOrDefault(adjacent, 0)) {
                    charges.put(adjacent, propagated);
                    open.add(new ChargedPos(adjacent, propagated));
                }
            }
        }
        for (Map.Entry<BlockPos, Integer> entry : charges.entrySet()) {
            BlockState state = level.getBlockState(entry.getKey());
            int charge = entry.getValue();
            BlockState updated = state;
            if (state.hasProperty(TCInlayBlock.CHARGE)) {
                updated = state.setValue(TCInlayBlock.CHARGE, charge);
            } else if (state.hasProperty(TCInfusionPedestalBlock.CHARGE)) {
                updated = state.setValue(TCInfusionPedestalBlock.CHARGE, charge);
            }
            if (updated != state) {
                level.setBlock(entry.getKey(), updated, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
            }
        }
    }

    private record ChargedPos(BlockPos pos, int charge) {
    }

    private TCInlayNetwork() {
    }
}

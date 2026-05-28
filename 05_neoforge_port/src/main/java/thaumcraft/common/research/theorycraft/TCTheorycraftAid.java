package thaumcraft.common.research.theorycraft;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class TCTheorycraftAid {
    private final String legacyKey;
    private final Supplier<ItemStack> displayStack;
    private final List<String> cardKeys;
    private final Predicate<BlockState> blockMatcher;
    private final Predicate<ItemStack> droppedStackMatcher;
    private final Predicate<Entity> entityMatcher;

    private TCTheorycraftAid(
            String legacyKey,
            Supplier<ItemStack> displayStack,
            List<String> cardKeys,
            Predicate<BlockState> blockMatcher,
            Predicate<ItemStack> droppedStackMatcher,
            Predicate<Entity> entityMatcher
    ) {
        this.legacyKey = legacyKey;
        this.displayStack = displayStack;
        this.cardKeys = List.copyOf(cardKeys);
        this.blockMatcher = blockMatcher;
        this.droppedStackMatcher = droppedStackMatcher;
        this.entityMatcher = entityMatcher;
    }

    public static TCTheorycraftAid block(String legacyKey, ItemStack displayStack, Predicate<BlockState> matcher, List<String> cardKeys) {
        return block(legacyKey, () -> displayStack.copy(), matcher, cardKeys);
    }

    public static TCTheorycraftAid block(String legacyKey, Supplier<ItemStack> displayStack, Predicate<BlockState> matcher, List<String> cardKeys) {
        return new TCTheorycraftAid(legacyKey, displayStack, cardKeys, matcher, stack -> false, entity -> false);
    }

    public String legacyKey() {
        return legacyKey;
    }

    public ItemStack displayStack() {
        ItemStack stack = displayStack.get();
        return stack == null ? ItemStack.EMPTY : stack.copy();
    }

    public List<String> cardKeys() {
        return cardKeys;
    }

    public boolean matchesBlock(BlockState state) {
        return state != null && blockMatcher.test(state);
    }

    public boolean matchesDroppedStack(ItemStack stack) {
        return stack != null && !stack.isEmpty() && droppedStackMatcher.test(stack);
    }

    public boolean matchesEntity(Entity entity) {
        return entity != null && entityMatcher.test(entity);
    }
}

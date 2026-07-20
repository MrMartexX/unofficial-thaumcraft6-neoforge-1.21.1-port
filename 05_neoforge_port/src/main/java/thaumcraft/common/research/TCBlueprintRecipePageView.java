package thaumcraft.common.research;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record TCBlueprintRecipePageView(
        ResourceLocation recipeId,
        ItemStack displayStack,
        List<ItemStack> ingredientStacks,
        List<List<List<Cell>>> layers,
        String research
) {
    public TCBlueprintRecipePageView {
        displayStack = displayStack == null ? ItemStack.EMPTY : displayStack.copy();
        ingredientStacks = copyStacks(ingredientStacks);
        layers = copyLayers(layers);
        research = research == null ? "" : research;
    }

    public int layerCount() {
        return layers.size();
    }

    public int rowCount() {
        return layers.isEmpty() ? 0 : layers.getFirst().size();
    }

    public int columnCount() {
        return layers.isEmpty() || layers.getFirst().isEmpty() ? 0 : layers.getFirst().getFirst().size();
    }

    private static List<ItemStack> copyStacks(List<ItemStack> stacks) {
        if (stacks == null || stacks.isEmpty()) {
            return List.of();
        }
        ArrayList<ItemStack> copied = new ArrayList<>(stacks.size());
        for (ItemStack stack : stacks) {
            copied.add(stack == null ? ItemStack.EMPTY : stack.copy());
        }
        return List.copyOf(copied);
    }

    private static List<List<List<Cell>>> copyLayers(List<List<List<Cell>>> source) {
        if (source == null || source.isEmpty()) {
            return List.of();
        }
        ArrayList<List<List<Cell>>> copiedLayers = new ArrayList<>(source.size());
        for (List<List<Cell>> layer : source) {
            ArrayList<List<Cell>> copiedRows = new ArrayList<>(layer == null ? 0 : layer.size());
            if (layer != null) {
                for (List<Cell> row : layer) {
                    ArrayList<Cell> copiedCells = new ArrayList<>(row == null ? 0 : row.size());
                    if (row != null) {
                        for (Cell cell : row) {
                            copiedCells.add(cell == null ? Cell.empty() : cell.copy());
                        }
                    }
                    copiedRows.add(List.copyOf(copiedCells));
                }
            }
            copiedLayers.add(List.copyOf(copiedRows));
        }
        return List.copyOf(copiedLayers);
    }

    public record Cell(ItemStack sourceStack, ItemStack targetStack) {
        public Cell {
            sourceStack = sourceStack == null ? ItemStack.EMPTY : sourceStack.copy();
            targetStack = targetStack == null ? ItemStack.EMPTY : targetStack.copy();
        }

        static Cell empty() {
            return new Cell(ItemStack.EMPTY, ItemStack.EMPTY);
        }

        Cell copy() {
            return new Cell(sourceStack, targetStack);
        }
    }
}

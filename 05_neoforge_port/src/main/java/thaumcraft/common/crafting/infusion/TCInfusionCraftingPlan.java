package thaumcraft.common.crafting.infusion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.crafting.crucible.TCCrucibleAspectCost;

public record TCInfusionCraftingPlan(
        ResourceLocation recipeId,
        String research,
        int instability,
        ItemStack catalyst,
        List<ItemStack> components,
        List<BlockPos> componentPedestalPositions,
        AspectList requiredAspects,
        ItemStack result,
        String playerName,
        int cycleTime,
        int cycleDelay,
        float costMultiplier,
        float stabilityReplenish
) {
    private static final String STACKS_TAG = "Stacks";
    private static final int MAX_COMPONENTS = 64;
    private static final int CATALYST_SLOT = 0;
    private static final int RESULT_SLOT = 1;
    private static final int FIRST_COMPONENT_SLOT = 2;

    public TCInfusionCraftingPlan {
        if (recipeId == null) {
            throw new IllegalArgumentException("Infusion plan recipe id cannot be null");
        }
        research = research == null ? "" : research;
        catalyst = catalyst == null ? ItemStack.EMPTY : catalyst.copy();
        components = copyStacks(components);
        componentPedestalPositions = componentPedestalPositions == null
                ? List.of()
                : List.copyOf(componentPedestalPositions);
        if (components.size() != componentPedestalPositions.size()) {
            throw new IllegalArgumentException("Infusion plan component/position count mismatch");
        }
        requiredAspects = requiredAspects == null ? new AspectList() : requiredAspects.copy();
        result = result == null ? ItemStack.EMPTY : result.copy();
        playerName = playerName == null ? "" : playerName;
        cycleTime = Math.max(1, cycleTime);
        cycleDelay = Math.max(1, cycleDelay);
        costMultiplier = Math.max(0.5F, costMultiplier);
    }

    public TCInfusionCraftingPlan(
            ResourceLocation recipeId,
            String research,
            int instability,
            ItemStack catalyst,
            List<ItemStack> components,
            List<BlockPos> componentPedestalPositions,
            AspectList requiredAspects,
            ItemStack result,
            String playerName
    ) {
        this(
                recipeId,
                research,
                instability,
                catalyst,
                components,
                componentPedestalPositions,
                requiredAspects,
                result,
                playerName,
                TCInfusionCycleState.BASE_CYCLE_TIME,
                TCInfusionCycleState.BASE_CYCLE_DELAY,
                1.0F,
                0.0F
        );
    }

    public static BuildResult build(
            ResourceLocation recipeId,
            TCInfusionRecipe recipe,
            ItemStack catalyst,
            List<PedestalComponent> suppliedPedestals,
            String playerName,
            TCInfusionStructureProfile structure
    ) {
        if (recipeId == null) {
            return BuildResult.failed("missing_recipe_id");
        }
        if (recipe == null) {
            return BuildResult.failed("missing_recipe");
        }
        if (catalyst == null || catalyst.isEmpty()) {
            return BuildResult.failed("missing_catalyst");
        }
        if (structure == null || !structure.valid()) {
            return BuildResult.failed(structure == null ? "missing_structure_profile" : structure.reason());
        }
        List<PedestalComponent> supplied = suppliedPedestals == null ? List.of() : suppliedPedestals.stream()
                .filter(component -> component != null && !component.stack().isEmpty())
                .toList();
        if (supplied.size() != recipe.components().size()) {
            return BuildResult.failed("component_count_mismatch");
        }

        int[] assignment = assignComponents(recipe.components(), supplied);
        if (assignment == null) {
            return BuildResult.failed("component_pedestal_mapping_failed");
        }

        ArrayList<ItemStack> matchedComponents = new ArrayList<>(assignment.length);
        ArrayList<BlockPos> matchedPositions = new ArrayList<>(assignment.length);
        for (int suppliedIndex : assignment) {
            PedestalComponent component = supplied.get(suppliedIndex);
            matchedComponents.add(component.stack().copyWithCount(1));
            matchedPositions.add(component.pos());
        }

        return BuildResult.valid(new TCInfusionCraftingPlan(
                recipeId,
                recipe.getResearch(),
                recipe.instability(),
                catalyst.copyWithCount(1),
                List.copyOf(matchedComponents),
                List.copyOf(matchedPositions),
                requiredAspects(recipe, structure.costMultiplier()),
                recipe.result(),
                playerName,
                structure.cycleTime(),
                structure.cycleDelay(),
                structure.costMultiplier(),
                structure.stabilityReplenish()
        ));
    }

    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putString("RecipeId", recipeId.toString());
        tag.putString("Research", research);
        tag.putInt("Instability", instability);
        tag.putString("PlayerName", playerName);
        tag.putInt("ComponentCount", components.size());
        tag.putInt("CycleTime", cycleTime);
        tag.putInt("CycleDelay", cycleDelay);
        tag.putFloat("CostMultiplier", costMultiplier);
        tag.putFloat("StabilityReplenish", stabilityReplenish);

        NonNullList<ItemStack> stacks = NonNullList.withSize(components.size() + FIRST_COMPONENT_SLOT, ItemStack.EMPTY);
        stacks.set(CATALYST_SLOT, catalyst.copy());
        stacks.set(RESULT_SLOT, result.copy());
        for (int index = 0; index < components.size(); index++) {
            stacks.set(index + FIRST_COMPONENT_SLOT, components.get(index).copy());
        }
        CompoundTag stacksTag = new CompoundTag();
        ContainerHelper.saveAllItems(stacksTag, stacks, registries);
        tag.put(STACKS_TAG, stacksTag);

        requiredAspects.writeToNBT(tag, "RequiredAspects");

        ListTag positions = new ListTag();
        for (BlockPos pos : componentPedestalPositions) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("X", pos.getX());
            posTag.putInt("Y", pos.getY());
            posTag.putInt("Z", pos.getZ());
            positions.add(posTag);
        }
        tag.put("ComponentPositions", positions);
        return tag;
    }

    public static TCInfusionCraftingPlan load(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag == null || !tag.contains("RecipeId")) {
            return null;
        }
        ResourceLocation recipeId;
        try {
            recipeId = ResourceLocation.parse(tag.getString("RecipeId"));
        } catch (Exception ignored) {
            return null;
        }

        int componentCount = Math.max(0, tag.getInt("ComponentCount"));
        if (componentCount > MAX_COMPONENTS) {
            return null;
        }
        NonNullList<ItemStack> stacks = NonNullList.withSize(componentCount + FIRST_COMPONENT_SLOT, ItemStack.EMPTY);
        if (tag.contains(STACKS_TAG, Tag.TAG_COMPOUND)) {
            ContainerHelper.loadAllItems(tag.getCompound(STACKS_TAG), stacks, registries);
        }
        ItemStack catalyst = stacks.get(CATALYST_SLOT).copy();
        ItemStack result = stacks.get(RESULT_SLOT).copy();
        if (catalyst.isEmpty() || result.isEmpty()) {
            return null;
        }

        ArrayList<ItemStack> components = new ArrayList<>(componentCount);
        for (int index = 0; index < componentCount; index++) {
            ItemStack component = stacks.get(index + FIRST_COMPONENT_SLOT).copy();
            if (component.isEmpty()) {
                return null;
            }
            components.add(component);
        }

        ArrayList<BlockPos> positions = new ArrayList<>(componentCount);
        ListTag positionTags = tag.getList("ComponentPositions", Tag.TAG_COMPOUND);
        for (int index = 0; index < Math.min(componentCount, positionTags.size()); index++) {
            CompoundTag posTag = positionTags.getCompound(index);
            positions.add(new BlockPos(posTag.getInt("X"), posTag.getInt("Y"), posTag.getInt("Z")));
        }
        while (positions.size() < componentCount) {
            positions.add(BlockPos.ZERO);
        }

        AspectList requiredAspects = new AspectList();
        requiredAspects.readFromNBT(tag, "RequiredAspects");
        return new TCInfusionCraftingPlan(
                recipeId,
                tag.getString("Research"),
                tag.getInt("Instability"),
                catalyst,
                List.copyOf(components),
                List.copyOf(positions),
                requiredAspects,
                result,
                tag.getString("PlayerName"),
                tag.contains("CycleTime") ? tag.getInt("CycleTime") : TCInfusionCycleState.BASE_CYCLE_TIME,
                tag.contains("CycleDelay") ? tag.getInt("CycleDelay") : TCInfusionCycleState.BASE_CYCLE_DELAY,
                tag.contains("CostMultiplier") ? tag.getFloat("CostMultiplier") : 1.0F,
                tag.getFloat("StabilityReplenish")
        );
    }

    public int requiredAspectAmount() {
        return requiredAspects.visSize();
    }

    public ItemStack catalyst() {
        return catalyst.copy();
    }

    public boolean catalystMatches(ItemStack stack) {
        return craftingStackMatches(stack, catalyst);
    }

    public List<ItemStack> components() {
        return components.stream().map(ItemStack::copy).toList();
    }

    public boolean componentMatches(int index, ItemStack stack) {
        if (index < 0 || index >= components.size()) {
            return false;
        }
        return craftingStackMatches(stack, components.get(index));
    }

    public ItemStack component(int index) {
        if (index < 0 || index >= components.size()) {
            return ItemStack.EMPTY;
        }
        return components.get(index).copy();
    }

    public AspectList requiredAspects() {
        return requiredAspects.copy();
    }

    public ItemStack result() {
        return result.copy();
    }

    private static int[] assignComponents(List<Ingredient> required, List<PedestalComponent> supplied) {
        int[] assignment = new int[required.size()];
        Arrays.fill(assignment, -1);
        boolean[] used = new boolean[supplied.size()];
        return assignComponent(required, supplied, 0, used, assignment) ? assignment : null;
    }

    private static boolean assignComponent(
            List<Ingredient> required,
            List<PedestalComponent> supplied,
            int requiredIndex,
            boolean[] used,
            int[] assignment
    ) {
        if (requiredIndex >= required.size()) {
            return true;
        }
        Ingredient ingredient = required.get(requiredIndex);
        for (int suppliedIndex = 0; suppliedIndex < supplied.size(); suppliedIndex++) {
            if (used[suppliedIndex]) {
                continue;
            }
            ItemStack stack = supplied.get(suppliedIndex).stack().copyWithCount(1);
            if (!ingredient.test(stack)) {
                continue;
            }
            used[suppliedIndex] = true;
            assignment[requiredIndex] = suppliedIndex;
            if (assignComponent(required, supplied, requiredIndex + 1, used, assignment)) {
                return true;
            }
            assignment[requiredIndex] = -1;
            used[suppliedIndex] = false;
        }
        return false;
    }

    private static AspectList requiredAspects(TCInfusionRecipe recipe, float costMultiplier) {
        AspectList aspects = new AspectList();
        for (TCCrucibleAspectCost cost : recipe.aspectCosts()) {
            int adjusted = (int) (cost.amount() * Math.max(0.5F, costMultiplier));
            if (adjusted > 0) {
                aspects.add(cost.resolvedAspect(), adjusted);
            }
        }
        return aspects;
    }

    private static List<ItemStack> copyStacks(List<ItemStack> stacks) {
        if (stacks == null) {
            return List.of();
        }
        return stacks.stream()
                .map(stack -> stack == null ? ItemStack.EMPTY : stack.copy())
                .toList();
    }

    private static boolean craftingStackMatches(ItemStack currentStack, ItemStack expectedStack) {
        if (currentStack == null || expectedStack == null || currentStack.isEmpty() || expectedStack.isEmpty()) {
            return false;
        }
        return ItemStack.isSameItemSameComponents(currentStack.copyWithCount(1), expectedStack.copyWithCount(1));
    }

    public record PedestalComponent(BlockPos pos, ItemStack stack) {
        public PedestalComponent {
            if (pos == null) {
                pos = BlockPos.ZERO;
            }
            stack = stack == null ? ItemStack.EMPTY : stack.copyWithCount(1);
        }
    }

    public record BuildResult(boolean valid, String reason, TCInfusionCraftingPlan plan) {
        public BuildResult {
            reason = reason == null ? "" : reason;
        }

        public static BuildResult valid(TCInfusionCraftingPlan plan) {
            return new BuildResult(true, "valid", plan);
        }

        public static BuildResult failed(String reason) {
            return new BuildResult(false, reason, null);
        }
    }
}

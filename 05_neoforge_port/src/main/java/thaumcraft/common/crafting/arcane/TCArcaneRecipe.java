package thaumcraft.common.crafting.arcane;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.registry.TCRecipes;

public abstract class TCArcaneRecipe implements IArcaneRecipe {
    private static final int MAX_CRYSTAL_COSTS = 64;

    private final String group;
    private final String research;
    private final int vis;
    private final List<TCArcaneCrystalCost> crystalCosts;
    private final ItemStack result;

    protected TCArcaneRecipe(
            String group,
            String research,
            int vis,
            List<TCArcaneCrystalCost> crystalCosts,
            ItemStack result
    ) {
        this.group = group == null ? "" : group.trim();
        this.research = research == null ? "" : research.trim();
        if (this.research.isBlank()) {
            throw new IllegalArgumentException("Arcane recipe research key cannot be blank");
        }
        if (vis < 0) {
            throw new IllegalArgumentException("Arcane recipe vis cost cannot be negative");
        }
        this.vis = vis;
        this.crystalCosts = List.copyOf(crystalCosts);
        this.result = result.copy();
        if (this.result.isEmpty()) {
            throw new IllegalArgumentException("Arcane recipe result cannot be empty");
        }
    }

    public abstract boolean shaped();

    public abstract int width();

    public abstract int height();

    public List<TCArcaneCrystalCost> crystalCosts() {
        return crystalCosts;
    }

    public ItemStack result() {
        return result.copy();
    }

    @Override
    public int getVis() {
        return vis;
    }

    @Override
    public String getResearch() {
        return research;
    }

    @Override
    public AspectList getCrystals() {
        return TCArcaneCrystalCost.toAspectList(crystalCosts);
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public RecipeType<?> getType() {
        return TCRecipes.ARCANE_TYPE.get();
    }

    static void writeCommon(RegistryFriendlyByteBuf buffer, TCArcaneRecipe recipe) {
        buffer.writeUtf(recipe.getGroup());
        buffer.writeUtf(recipe.getResearch());
        buffer.writeVarInt(recipe.getVis());
        writeCrystalCosts(buffer, recipe.crystalCosts());
        ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
    }

    static CommonFields readCommon(RegistryFriendlyByteBuf buffer) {
        return new CommonFields(
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readVarInt(),
                readCrystalCosts(buffer),
                ItemStack.STREAM_CODEC.decode(buffer)
        );
    }

    private static void writeCrystalCosts(RegistryFriendlyByteBuf buffer, List<TCArcaneCrystalCost> costs) {
        if (costs.size() > MAX_CRYSTAL_COSTS) {
            throw new IllegalArgumentException("Too many arcane crystal costs: " + costs.size());
        }
        buffer.writeVarInt(costs.size());
        for (TCArcaneCrystalCost cost : costs) {
            buffer.writeUtf(cost.aspect());
            buffer.writeVarInt(cost.amount());
        }
    }

    private static List<TCArcaneCrystalCost> readCrystalCosts(RegistryFriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        if (size < 0 || size > MAX_CRYSTAL_COSTS) {
            throw new IllegalArgumentException("Invalid arcane crystal cost count: " + size);
        }
        ArrayList<TCArcaneCrystalCost> costs = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            costs.add(new TCArcaneCrystalCost(buffer.readUtf(), buffer.readVarInt()));
        }
        return List.copyOf(costs);
    }

    record CommonFields(
            String group,
            String research,
            int vis,
            List<TCArcaneCrystalCost> crystalCosts,
            ItemStack result
    ) {
    }
}

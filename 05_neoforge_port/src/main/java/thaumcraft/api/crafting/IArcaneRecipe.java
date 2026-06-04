package thaumcraft.api.crafting;

import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import thaumcraft.api.aspects.AspectList;

/**
 * Public arcane recipe contract.
 *
 * <p>The legacy API exposed the same three domain values. Modern recipe
 * implementations remain isolated under their own recipe type so a vanilla
 * crafting grid cannot craft them.</p>
 */
public interface IArcaneRecipe extends Recipe<CraftingInput> {
    int getVis();

    String getResearch();

    AspectList getCrystals();
}

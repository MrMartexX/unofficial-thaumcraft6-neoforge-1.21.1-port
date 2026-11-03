package thaumcraft.common.golems.seals;

import net.minecraft.resources.ResourceLocation;

public class SealGuardAdvanced extends SealGuard {
    @Override
    public int[] getGuiCategories() { return new int[]{CAT_AREA, CAT_TOGGLES, CAT_PRIORITY, CAT_TAGS}; }
    @Override
    public thaumcraft.api.golems.EnumGolemTrait[] getRequiredTags() {
        return new thaumcraft.api.golems.EnumGolemTrait[]{thaumcraft.api.golems.EnumGolemTrait.FIGHTER, thaumcraft.api.golems.EnumGolemTrait.SMART};
    }

    @Override
    public String getKey() {
        return "thaumcraft:guard_advanced";
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/seals/seal_guard_advanced.png");
    }
}

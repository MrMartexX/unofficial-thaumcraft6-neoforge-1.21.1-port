package thaumcraft.common.golems.seals;

import net.minecraft.resources.ResourceLocation;

public class SealPickupAdvanced extends SealPickup implements thaumcraft.api.golems.seals.ISealConfigToggles {
    @Override
    public thaumcraft.api.golems.EnumGolemTrait[] getRequiredTags() { return new thaumcraft.api.golems.EnumGolemTrait[]{thaumcraft.api.golems.EnumGolemTrait.SMART}; }

    @Override
    public String getKey() {
        return "thaumcraft:pickup_advanced";
    }

    @Override
    public int getFilterSize() {
        return 9;
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/seals/seal_pickup_advanced.png");
    }
}

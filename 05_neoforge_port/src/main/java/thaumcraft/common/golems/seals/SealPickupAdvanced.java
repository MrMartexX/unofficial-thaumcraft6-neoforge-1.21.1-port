package thaumcraft.common.golems.seals;

import net.minecraft.resources.ResourceLocation;

public class SealPickupAdvanced extends SealPickup {

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

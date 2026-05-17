package thaumcraft.common.research;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ScanningManager;

final class TCScanEnchantment implements IScanThing {
    private final Holder<Enchantment> enchantment;
    private final String researchKey;

    TCScanEnchantment(Holder<Enchantment> enchantment) {
        this.enchantment = enchantment;
        this.researchKey = "!" + enchantment.unwrapKey()
                .map(key -> key.location().toString())
                .orElseGet(() -> enchantment.value().description().getString());
    }

    @Override
    public boolean checkThing(ServerPlayer player, Object object) {
        ItemStack stack = ScanningManager.getItemFromParams(player, object);
        if (stack.isEmpty()) {
            return false;
        }

        ItemEnchantments enchantments = stack.is(Items.ENCHANTED_BOOK)
                ? stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                : stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().equals(enchantment)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getResearchKey(ServerPlayer player, Object object) {
        return researchKey;
    }
}

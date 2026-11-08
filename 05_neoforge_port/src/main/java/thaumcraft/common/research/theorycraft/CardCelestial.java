package thaumcraft.common.research.theorycraft;

import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.items.curios.ItemCelestialNotes;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.warp.TCWarpManager;
import thaumcraft.common.warp.TCWarpType;

public final class CardCelestial extends TCTheorycraftCard {
    private int md1;
    private int md2;
    private String category = "BASICS";

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putInt("md1", md1);
        tag.putInt("md2", md2);
        tag.putString("cat", category == null ? "" : category);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        md1 = tag.getInt("md1");
        md2 = tag.getInt("md2");
        category = tag.getString("cat");
    }

    @Override
    public String getResearchCategory() {
        return category;
    }

    @Override
    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        if (data.categoryTotals.isEmpty() || !TCPlayerKnowledgeStore.get(player).hasResearch("CELESTIALSCANNING")) {
            return false;
        }
        Random r = new Random(getSeed());
        md1 = r.nextInt(13);
        md2 = md1;
        while (md1 == md2) {
            md2 = r.nextInt(13);
        }

        int highestValue = 0;
        String highestKey = "";
        for (String currentCategory : data.categoryTotals.keySet()) {
            int q = data.getTotal(currentCategory);
            if (q > highestValue) {
                highestValue = q;
                highestKey = currentCategory;
            }
        }
        category = highestKey.isEmpty() ? "BASICS" : highestKey;
        return category != null;
    }

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.celestial.name");
    }

    @Override
    public Component getLocalizedText() {
        Component categoryComp = Component.translatable("tc.research_category." + category).withStyle(ChatFormatting.BOLD);
        return Component.translatable("card.celestial.text", categoryComp);
    }

    @Override
    public List<ItemStack> getRequiredItems() {
        return List.of(
                ItemCelestialNotes.createStack(md1, 1),
                ItemCelestialNotes.createStack(md2, 1)
        );
    }

    @Override
    public List<Boolean> getRequiredItemsConsumed() {
        return List.of(true, true);
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        data.addTotal(getResearchCategory(), Mth.randomBetweenInclusive(player.getRandom(), 25, 50));
        boolean sun = md1 == 0 || md2 == 0;
        boolean moon = md1 > 4 || md2 > 4;
        boolean stars = (md1 > 0 && md1 < 5) || (md2 > 0 && md2 < 5);
        if (stars) {
            int amt = Mth.randomBetweenInclusive(player.getRandom(), 0, 5);
            data.addTotal("ELDRITCH", amt * 2);
            TCWarpManager.add(player, TCWarpType.TEMPORARY, amt);
        }
        if (sun) {
            ++data.penaltyStart;
        }
        if (moon) {
            ++data.bonusDraws;
        }
        return true;
    }
}

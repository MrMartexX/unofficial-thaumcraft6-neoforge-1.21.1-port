package thaumcraft.common.research.theorycraft;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import thaumcraft.common.research.TCResearchManager;
import thaumcraft.common.tiles.crafting.TCResearchTableBlockEntity;

public class TCResearchTableData {
    private transient TCResearchTableBlockEntity table;
    public String player;
    public int inspiration;
    public int inspirationStart;
    public int bonusDraws;
    public int placedCards;
    public int aidsChosen;
    public int penaltyStart;
    public ArrayList<Long> savedCards = new ArrayList<>();
    public ArrayList<String> aidCards = new ArrayList<>();
    public TreeMap<String, Integer> categoryTotals = new TreeMap<>();
    public ArrayList<String> categoriesBlocked = new ArrayList<>();
    public ArrayList<CardChoice> cardChoices = new ArrayList<>();
    public CardChoice lastDraw;

    public TCResearchTableData() {
    }

    public TCResearchTableData(ServerPlayer player) {
        this.player = player.getName().getString();
    }

    public TCResearchTableBlockEntity getTable() {
        return table;
    }

    public void setTable(TCResearchTableBlockEntity table) {
        this.table = table;
    }

    public boolean isComplete() {
        return inspiration <= 0;
    }

    public boolean hasTotal(String category) {
        return categoryTotals.containsKey(category);
    }

    public int getTotal(String category) {
        return categoryTotals.getOrDefault(category, 0);
    }

    public void addTotal(String category, int amount) {
        if (category == null || category.isBlank() || amount == 0) {
            return;
        }
        int current = categoryTotals.getOrDefault(category, 0) + amount;
        if (current <= 0) {
            categoryTotals.remove(category);
        } else {
            categoryTotals.put(category, current);
        }
    }

    public void addInspiration(int amount) {
        inspiration += amount;
        if (inspiration > inspirationStart) {
            inspiration = inspirationStart;
        }
    }

    public void initialize(ServerPlayer player, Iterable<String> aids) {
        this.player = player.getName().getString();
        inspirationStart = TCResearchManager.availableTheoryInspiration(player);
        initializeAids(aids);
    }

    void initializeWithFixedInspirationForDiagnostics(String playerName, int fixedInspirationStart, Iterable<String> aids) {
        this.player = playerName;
        inspirationStart = fixedInspirationStart;
        initializeAids(aids);
    }

    private void initializeAids(Iterable<String> aids) {
        aidCards.clear();
        int aidCount = 0;
        if (aids != null) {
            for (String aidKey : aids) {
                TCTheorycraftAid aid = TCTheorycraftManager.aids().get(aidKey);
                if (aid == null) {
                    continue;
                }
                aidCount++;
                aidCards.addAll(aid.cardKeys());
            }
        }
        aidsChosen = aidCount;
        inspiration = inspirationStart - aidCount;
    }

    public ArrayList<String> getAvailableCategories(ServerPlayer player) {
        return new ArrayList<>(TCResearchManager.availableTheoryCategories(player, new HashSet<>(categoriesBlocked)));
    }

    public void drawCards(int draw, ServerPlayer player) {
        if (draw == 3) {
            if (bonusDraws > 0) {
                bonusDraws--;
            } else {
                draw = 2;
            }
        }

        cardChoices.clear();
        this.player = player.getName().getString();
        ArrayList<String> availableCategories = getAvailableCategories(player);
        ArrayList<String> drawnCards = new ArrayList<>();
        boolean aidDrawn = false;
        int failsafe = 0;

        while (draw > 0 && failsafe < 10000) {
            failsafe++;
            if (!aidDrawn && !aidCards.isEmpty() && player.getRandom().nextFloat() <= 0.25F) {
                int index = player.getRandom().nextInt(aidCards.size());
                String key = aidCards.get(index);
                TCTheorycraftCard card = generateCard(key, -1L, player);
                if (card == null || card.getInspirationCost() > inspiration || isCategoryBlocked(card.getResearchCategory())) {
                    continue;
                }
                if (drawnCards.contains(key)) {
                    continue;
                }
                drawnCards.add(key);
                cardChoices.add(new CardChoice(key, card, true, false));
                aidCards.remove(index);
            } else {
                try {
                    String[] cards = TCTheorycraftManager.cards().keySet().toArray(new String[0]);
                    int index = player.getRandom().nextInt(cards.length);
                    TCTheorycraftCard card = generateCard(cards[index], -1L, player);
                    if (card == null || card.isAidOnly() || card.getInspirationCost() > inspiration) {
                        continue;
                    }
                    if (card.getResearchCategory() != null && !availableCategories.contains(card.getResearchCategory())) {
                        continue;
                    }
                    if (drawnCards.contains(cards[index])) {
                        continue;
                    }
                    drawnCards.add(cards[index]);
                    cardChoices.add(new CardChoice(cards[index], card, false, false));
                } catch (RuntimeException ignored) {
                    continue;
                }
            }
            draw--;
        }
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putString("player", player == null ? "" : player);
        tag.putInt("inspiration", inspiration);
        tag.putInt("inspirationStart", inspirationStart);
        tag.putInt("placedCards", placedCards);
        tag.putInt("bonusDraws", bonusDraws);
        tag.putInt("aidsChosen", aidsChosen);
        tag.putInt("penaltyStart", penaltyStart);

        ListTag savedTag = new ListTag();
        for (Long card : savedCards) {
            CompoundTag cardTag = new CompoundTag();
            cardTag.putLong("card", card);
            savedTag.add(cardTag);
        }
        tag.put("savedCards", savedTag);

        ListTag blockedTag = new ListTag();
        for (String category : categoriesBlocked) {
            CompoundTag categoryTag = new CompoundTag();
            categoryTag.putString("category", category);
            blockedTag.add(categoryTag);
        }
        tag.put("categoriesBlocked", blockedTag);

        ListTag totalsTag = new ListTag();
        for (String category : categoryTotals.keySet()) {
            CompoundTag categoryTag = new CompoundTag();
            categoryTag.putString("category", category);
            categoryTag.putInt("total", categoryTotals.get(category));
            totalsTag.add(categoryTag);
        }
        tag.put("categoryTotals", totalsTag);

        ListTag aidCardsTag = new ListTag();
        for (String aidCard : aidCards) {
            CompoundTag aidCardTag = new CompoundTag();
            aidCardTag.putString("aidCard", aidCard);
            aidCardsTag.add(aidCardTag);
        }
        tag.put("aidCards", aidCardsTag);

        ListTag choicesTag = new ListTag();
        for (CardChoice choice : cardChoices) {
            choicesTag.add(serializeCardChoice(choice));
        }
        tag.put("cardChoices", choicesTag);

        if (lastDraw != null) {
            tag.put("lastDraw", serializeCardChoice(lastDraw));
        }
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        if (tag == null) {
            return;
        }

        inspiration = tag.getInt("inspiration");
        inspirationStart = tag.getInt("inspirationStart");
        placedCards = tag.getInt("placedCards");
        bonusDraws = tag.getInt("bonusDraws");
        aidsChosen = tag.getInt("aidsChosen");
        penaltyStart = tag.getInt("penaltyStart");
        player = tag.getString("player");

        savedCards = new ArrayList<>();
        ListTag savedTag = tag.getList("savedCards", Tag.TAG_COMPOUND);
        for (int index = 0; index < savedTag.size(); index++) {
            savedCards.add(savedTag.getCompound(index).getLong("card"));
        }

        categoriesBlocked = new ArrayList<>();
        ListTag blockedTag = tag.getList("categoriesBlocked", Tag.TAG_COMPOUND);
        for (int index = 0; index < blockedTag.size(); index++) {
            categoriesBlocked.add(blockedTag.getCompound(index).getString("category"));
        }

        categoryTotals = new TreeMap<>();
        ListTag totalsTag = tag.getList("categoryTotals", Tag.TAG_COMPOUND);
        for (int index = 0; index < totalsTag.size(); index++) {
            CompoundTag categoryTag = totalsTag.getCompound(index);
            categoryTotals.put(categoryTag.getString("category"), categoryTag.getInt("total"));
        }

        aidCards = new ArrayList<>();
        ListTag aidCardsTag = tag.getList("aidCards", Tag.TAG_COMPOUND);
        for (int index = 0; index < aidCardsTag.size(); index++) {
            aidCards.add(aidCardsTag.getCompound(index).getString("aidCard"));
        }

        cardChoices = new ArrayList<>();
        ListTag choicesTag = tag.getList("cardChoices", Tag.TAG_COMPOUND);
        for (int index = 0; index < choicesTag.size(); index++) {
            CardChoice choice = deserializeCardChoice(choicesTag.getCompound(index));
            if (choice != null) {
                cardChoices.add(choice);
            }
        }
        lastDraw = deserializeCardChoice(tag.getCompound("lastDraw"));
    }

    public CompoundTag serializeCardChoice(CardChoice choice) {
        CompoundTag tag = new CompoundTag();
        tag.putString("cardChoice", choice.key);
        tag.putBoolean("aid", choice.fromAid);
        tag.putBoolean("select", choice.selected);
        tag.put("cardNBT", choice.card.serialize());
        return tag;
    }

    public CardChoice deserializeCardChoice(CompoundTag tag) {
        if (tag == null) {
            return null;
        }
        String key = tag.getString("cardChoice");
        TCTheorycraftCard card = generateCardWithNBT(key, tag.getCompound("cardNBT"));
        if (card == null) {
            return null;
        }
        return new CardChoice(key, card, tag.getBoolean("aid"), tag.getBoolean("select"));
    }

    private boolean isCategoryBlocked(String category) {
        return categoriesBlocked.contains(category);
    }

    private TCTheorycraftCard generateCard(String key, long seed, ServerPlayer player) {
        if (key == null) {
            return null;
        }
        TCTheorycraftCard card = TCTheorycraftManager.create(key);
        if (card == null) {
            return null;
        }

        if (seed < 0L) {
            card.setSeed(player == null ? System.nanoTime() : player.getRandom().nextLong());
        } else {
            card.setSeed(seed);
        }

        if (player != null && !card.initialize(player, this)) {
            return null;
        }
        return card;
    }

    private TCTheorycraftCard generateCardWithNBT(String key, CompoundTag tag) {
        if (key == null) {
            return null;
        }
        TCTheorycraftCard card = TCTheorycraftManager.create(key);
        if (card == null) {
            return null;
        }
        card.deserialize(tag);
        return card;
    }

    public static final class CardChoice {
        public final TCTheorycraftCard card;
        public final String key;
        public final boolean fromAid;
        public boolean selected;

        public CardChoice(String key, TCTheorycraftCard card, boolean fromAid, boolean selected) {
            this.key = key;
            this.card = card;
            this.fromAid = fromAid;
            this.selected = selected;
        }
    }
}

package thaumcraft.common.research;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

final class TCThaumonomiconCodec {
    static final int MAX_KEY_LENGTH = 256;
    static final int MAX_RESULT_LENGTH = 128;
    private static final int MAX_VALUE_LENGTH = 1024;
    private static final int MAX_TEXT_LENGTH = 32767;
    private static final int MAX_CATEGORIES = 256;
    private static final int MAX_RESEARCH_ENTRIES = 4096;
    private static final int MAX_ENTRY_LIST = 512;
    private static final int MAX_BOOKMARKS = 512;
    private static final int MAX_PAGES_PER_BOOKMARK = 512;
    private static final int MAX_CRAFTING_INGREDIENTS = 9;
    private static final int MAX_CRAFTING_INGREDIENT_VARIANTS = 1024;
    private static final int MAX_ARCANE_CRYSTALS = 64;
    private static final int MAX_RESEARCH_FLAGS = 32;

    private TCThaumonomiconCodec() {
    }

    static void writeIndex(RegistryFriendlyByteBuf buffer, TCThaumonomiconIndexPayload payload) {
        buffer.writeBoolean(payload.openScreen());
        buffer.writeVarInt(payload.revision());
        writeList(buffer, payload.categories(), MAX_CATEGORIES, "categories", TCThaumonomiconCodec::writeCategory);
        writeList(buffer, payload.entries(), MAX_RESEARCH_ENTRIES, "research entries", TCThaumonomiconCodec::writeResearch);
    }

    static TCThaumonomiconIndexPayload readIndex(RegistryFriendlyByteBuf buffer) {
        boolean openScreen = buffer.readBoolean();
        int revision = buffer.readVarInt();
        return new TCThaumonomiconIndexPayload(
                readList(buffer, MAX_CATEGORIES, "categories", TCThaumonomiconCodec::readCategory),
                readList(buffer, MAX_RESEARCH_ENTRIES, "research entries", TCThaumonomiconCodec::readResearch),
                revision,
                openScreen
        );
    }

    static void writeEntryPayload(RegistryFriendlyByteBuf buffer, TCThaumonomiconEntryPayload payload) {
        buffer.writeBoolean(payload.accepted());
        writeString(buffer, payload.resultKey(), MAX_RESULT_LENGTH, "result key");
        writeString(buffer, payload.researchKey(), MAX_KEY_LENGTH, "research key");
        buffer.writeBoolean(payload.entry().isPresent());
        payload.entry().ifPresent(entry -> writeEntry(buffer, entry));
    }

    static TCThaumonomiconEntryPayload readEntryPayload(RegistryFriendlyByteBuf buffer) {
        boolean accepted = buffer.readBoolean();
        String resultKey = readString(buffer, MAX_RESULT_LENGTH);
        String researchKey = readString(buffer, MAX_KEY_LENGTH);
        Optional<TCThaumonomiconEntryView> entry = buffer.readBoolean()
                ? Optional.of(readEntry(buffer))
                : Optional.empty();
        return new TCThaumonomiconEntryPayload(accepted, resultKey, researchKey, entry);
    }

    private static void writeCategory(RegistryFriendlyByteBuf buffer, TCThaumonomiconCategoryView category) {
        writeString(buffer, category.key(), MAX_KEY_LENGTH, "category key");
        writeString(buffer, category.requiredResearch(), MAX_KEY_LENGTH, "category required research");
        writeString(buffer, category.icon(), MAX_VALUE_LENGTH, "category icon");
        writeString(buffer, category.background(), MAX_VALUE_LENGTH, "category background");
        writeString(buffer, category.overlay(), MAX_VALUE_LENGTH, "category overlay");
    }

    private static TCThaumonomiconCategoryView readCategory(RegistryFriendlyByteBuf buffer) {
        return new TCThaumonomiconCategoryView(
                readString(buffer, MAX_KEY_LENGTH),
                readString(buffer, MAX_KEY_LENGTH),
                readString(buffer, MAX_VALUE_LENGTH),
                readString(buffer, MAX_VALUE_LENGTH),
                readString(buffer, MAX_VALUE_LENGTH)
        );
    }

    private static void writeResearch(RegistryFriendlyByteBuf buffer, TCThaumonomiconResearchView research) {
        writeString(buffer, research.key(), MAX_KEY_LENGTH, "research key");
        writeString(buffer, research.name(), MAX_VALUE_LENGTH, "research name");
        writeStrings(buffer, research.icons(), MAX_ENTRY_LIST, "research icons");
        writeString(buffer, research.category(), MAX_KEY_LENGTH, "research category");
        buffer.writeInt(research.locationX());
        buffer.writeInt(research.locationY());
        writeStrings(buffer, research.parents(), MAX_ENTRY_LIST, "research parents");
        writeStrings(buffer, research.siblings(), MAX_ENTRY_LIST, "research siblings");
        writeStrings(buffer, research.meta(), MAX_ENTRY_LIST, "research meta");
        writeEnum(buffer, research.status());
        buffer.writeBoolean(research.unlockable());
        writeList(buffer, research.flags(), MAX_RESEARCH_FLAGS, "research flags", TCThaumonomiconCodec::writeEnum);
        buffer.writeVarInt(research.currentStage());
        buffer.writeVarInt(research.totalStages());
    }

    private static TCThaumonomiconResearchView readResearch(RegistryFriendlyByteBuf buffer) {
        return new TCThaumonomiconResearchView(
                readString(buffer, MAX_KEY_LENGTH),
                readString(buffer, MAX_VALUE_LENGTH),
                readStrings(buffer, MAX_ENTRY_LIST, "research icons"),
                readString(buffer, MAX_KEY_LENGTH),
                buffer.readInt(),
                buffer.readInt(),
                readStrings(buffer, MAX_ENTRY_LIST, "research parents"),
                readStrings(buffer, MAX_ENTRY_LIST, "research siblings"),
                readStrings(buffer, MAX_ENTRY_LIST, "research meta"),
                readEnum(buffer, TCResearchStatus.values(), "research status"),
                buffer.readBoolean(),
                readList(
                        buffer,
                        MAX_RESEARCH_FLAGS,
                        "research flags",
                        target -> readEnum(target, TCResearchFlag.values(), "research flag")
                ),
                buffer.readVarInt(),
                buffer.readVarInt()
        );
    }

    private static void writeEntry(RegistryFriendlyByteBuf buffer, TCThaumonomiconEntryView entry) {
        writeResearch(buffer, entry.research());
        buffer.writeVarInt(entry.selectedStage());
        buffer.writeBoolean(entry.complete());
        writeString(buffer, entry.stageText(), MAX_TEXT_LENGTH, "stage text");
        writeTextStrings(buffer, entry.addendumTexts(), MAX_ENTRY_LIST, "addendum texts");
        writeStrings(buffer, entry.requiredResearch(), MAX_ENTRY_LIST, "required research");
        writeStrings(buffer, entry.requiredCraft(), MAX_ENTRY_LIST, "required craft");
        writeStrings(buffer, entry.requiredItem(), MAX_ENTRY_LIST, "required item");
        writeStrings(buffer, entry.requiredKnowledge(), MAX_ENTRY_LIST, "required knowledge");
        buffer.writeVarInt(entry.warp());
        writeTextStrings(buffer, entry.satisfiedRequirements(), MAX_ENTRY_LIST, "satisfied requirements");
        writeTextStrings(buffer, entry.missingRequirements(), MAX_ENTRY_LIST, "missing requirements");
        writeTextStrings(buffer, entry.blockedRequirements(), MAX_ENTRY_LIST, "blocked requirements");
        writeList(buffer, entry.bookmarks(), MAX_BOOKMARKS, "bookmarks", TCThaumonomiconCodec::writeBookmark);
    }

    private static TCThaumonomiconEntryView readEntry(RegistryFriendlyByteBuf buffer) {
        return new TCThaumonomiconEntryView(
                readResearch(buffer),
                buffer.readVarInt(),
                buffer.readBoolean(),
                readString(buffer, MAX_TEXT_LENGTH),
                readTextStrings(buffer, MAX_ENTRY_LIST, "addendum texts"),
                readStrings(buffer, MAX_ENTRY_LIST, "required research"),
                readStrings(buffer, MAX_ENTRY_LIST, "required craft"),
                readStrings(buffer, MAX_ENTRY_LIST, "required item"),
                readStrings(buffer, MAX_ENTRY_LIST, "required knowledge"),
                buffer.readVarInt(),
                readTextStrings(buffer, MAX_ENTRY_LIST, "satisfied requirements"),
                readTextStrings(buffer, MAX_ENTRY_LIST, "missing requirements"),
                readTextStrings(buffer, MAX_ENTRY_LIST, "blocked requirements"),
                readList(buffer, MAX_BOOKMARKS, "bookmarks", TCThaumonomiconCodec::readBookmark)
        );
    }

    private static void writeBookmark(RegistryFriendlyByteBuf buffer, TCResearchPageBookmark bookmark) {
        writeResourceLocation(buffer, bookmark.id());
        writeList(
                buffer,
                bookmark.pages(),
                MAX_PAGES_PER_BOOKMARK,
                "bookmark pages",
                TCThaumonomiconCodec::writePage
        );
    }

    private static TCResearchPageBookmark readBookmark(RegistryFriendlyByteBuf buffer) {
        return new TCResearchPageBookmark(
                readResourceLocation(buffer),
                readList(buffer, MAX_PAGES_PER_BOOKMARK, "bookmark pages", TCThaumonomiconCodec::readPage)
        );
    }

    private static void writePage(RegistryFriendlyByteBuf buffer, TCResearchPageView page) {
        writeResourceLocation(buffer, page.id());
        writeEnum(buffer, page.kind());
        writeEnum(buffer, page.availability());
        writeString(buffer, page.requiredResearch(), MAX_KEY_LENGTH, "page required research");
        buffer.writeBoolean(page.legacyOutput().isPresent());
        page.legacyOutput().ifPresent(output -> writeLegacyOutput(buffer, output));
        buffer.writeBoolean(page.craftingRecipe().isPresent());
        page.craftingRecipe().ifPresent(recipe -> writeCraftingRecipe(buffer, recipe));
        buffer.writeBoolean(page.arcaneRecipe().isPresent());
        page.arcaneRecipe().ifPresent(recipe -> writeArcaneRecipe(buffer, recipe));
    }

    private static TCResearchPageView readPage(RegistryFriendlyByteBuf buffer) {
        ResourceLocation id = readResourceLocation(buffer);
        TCResearchPageKind kind = readEnum(buffer, TCResearchPageKind.values(), "page kind");
        TCResearchPageAvailability availability = readEnum(
                buffer,
                TCResearchPageAvailability.values(),
                "page availability"
        );
        String requiredResearch = readString(buffer, MAX_KEY_LENGTH);
        Optional<TCResearchPageLegacyOutput> output = buffer.readBoolean()
                ? Optional.of(readLegacyOutput(buffer))
                : Optional.empty();
        Optional<TCCraftingRecipePageView> craftingRecipe = buffer.readBoolean()
                ? Optional.of(readCraftingRecipe(buffer))
                : Optional.empty();
        Optional<TCArcaneRecipePageView> arcaneRecipe = buffer.readBoolean()
                ? Optional.of(readArcaneRecipe(buffer))
                : Optional.empty();
        return new TCResearchPageView(id, kind, availability, requiredResearch, output, craftingRecipe, arcaneRecipe);
    }

    private static void writeCraftingRecipe(RegistryFriendlyByteBuf buffer, TCCraftingRecipePageView recipe) {
        writeResourceLocation(buffer, recipe.recipeId());
        buffer.writeBoolean(recipe.shaped());
        buffer.writeVarInt(recipe.width());
        buffer.writeVarInt(recipe.height());
        ItemStack.STREAM_CODEC.encode(buffer, recipe.result());
        writeList(
                buffer,
                recipe.ingredients(),
                MAX_CRAFTING_INGREDIENTS,
                "crafting ingredients",
                (target, variants) -> writeList(
                        target,
                        variants,
                        MAX_CRAFTING_INGREDIENT_VARIANTS,
                        "crafting ingredient variants",
                        ItemStack.STREAM_CODEC::encode
                )
        );
    }

    private static TCCraftingRecipePageView readCraftingRecipe(RegistryFriendlyByteBuf buffer) {
        return new TCCraftingRecipePageView(
                readResourceLocation(buffer),
                buffer.readBoolean(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                ItemStack.STREAM_CODEC.decode(buffer),
                readList(
                        buffer,
                        MAX_CRAFTING_INGREDIENTS,
                        "crafting ingredients",
                        target -> readList(
                                target,
                                MAX_CRAFTING_INGREDIENT_VARIANTS,
                                "crafting ingredient variants",
                                ItemStack.STREAM_CODEC::decode
                        )
                )
        );
    }

    private static void writeArcaneRecipe(RegistryFriendlyByteBuf buffer, TCArcaneRecipePageView recipe) {
        writeResourceLocation(buffer, recipe.recipeId());
        buffer.writeBoolean(recipe.shaped());
        buffer.writeVarInt(recipe.width());
        buffer.writeVarInt(recipe.height());
        ItemStack.STREAM_CODEC.encode(buffer, recipe.result());
        writeList(
                buffer,
                recipe.ingredients(),
                MAX_CRAFTING_INGREDIENTS,
                "arcane ingredients",
                (target, variants) -> writeList(
                        target,
                        variants,
                        MAX_CRAFTING_INGREDIENT_VARIANTS,
                        "arcane ingredient variants",
                        ItemStack.STREAM_CODEC::encode
                )
        );
        writeString(buffer, recipe.research(), MAX_KEY_LENGTH, "arcane research");
        buffer.writeVarInt(recipe.vis());
        writeList(
                buffer,
                recipe.crystalStacks(),
                MAX_ARCANE_CRYSTALS,
                "arcane crystals",
                ItemStack.STREAM_CODEC::encode
        );
    }

    private static TCArcaneRecipePageView readArcaneRecipe(RegistryFriendlyByteBuf buffer) {
        return new TCArcaneRecipePageView(
                readResourceLocation(buffer),
                buffer.readBoolean(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                ItemStack.STREAM_CODEC.decode(buffer),
                readList(
                        buffer,
                        MAX_CRAFTING_INGREDIENTS,
                        "arcane ingredients",
                        target -> readList(
                                target,
                                MAX_CRAFTING_INGREDIENT_VARIANTS,
                                "arcane ingredient variants",
                                ItemStack.STREAM_CODEC::decode
                        )
                ),
                readString(buffer, MAX_KEY_LENGTH),
                buffer.readVarInt(),
                readList(buffer, MAX_ARCANE_CRYSTALS, "arcane crystals", ItemStack.STREAM_CODEC::decode)
        );
    }

    private static void writeLegacyOutput(RegistryFriendlyByteBuf buffer, TCResearchPageLegacyOutput output) {
        writeResourceLocation(buffer, output.item());
        buffer.writeInt(output.metadata());
        buffer.writeVarInt(output.count());
        writeString(buffer, output.nbt(), MAX_TEXT_LENGTH, "legacy output nbt");
    }

    private static TCResearchPageLegacyOutput readLegacyOutput(RegistryFriendlyByteBuf buffer) {
        return new TCResearchPageLegacyOutput(
                readResourceLocation(buffer),
                buffer.readInt(),
                buffer.readVarInt(),
                readString(buffer, MAX_TEXT_LENGTH)
        );
    }

    private static void writeStrings(RegistryFriendlyByteBuf buffer, List<String> values, int maxSize, String label) {
        writeList(buffer, values, maxSize, label, (target, value) -> writeString(target, value, MAX_VALUE_LENGTH, label));
    }

    private static List<String> readStrings(RegistryFriendlyByteBuf buffer, int maxSize, String label) {
        return readList(buffer, maxSize, label, target -> readString(target, MAX_VALUE_LENGTH));
    }

    private static void writeTextStrings(RegistryFriendlyByteBuf buffer, List<String> values, int maxSize, String label) {
        writeList(buffer, values, maxSize, label, (target, value) -> writeString(target, value, MAX_TEXT_LENGTH, label));
    }

    private static List<String> readTextStrings(RegistryFriendlyByteBuf buffer, int maxSize, String label) {
        return readList(buffer, maxSize, label, target -> readString(target, MAX_TEXT_LENGTH));
    }

    private static <T> void writeList(
            RegistryFriendlyByteBuf buffer,
            List<T> values,
            int maxSize,
            String label,
            BiConsumer<RegistryFriendlyByteBuf, T> writer
    ) {
        if (values.size() > maxSize) {
            throw new IllegalArgumentException("Too many Thaumonomicon " + label + ": " + values.size());
        }
        buffer.writeVarInt(values.size());
        for (T value : values) {
            writer.accept(buffer, value);
        }
    }

    private static <T> List<T> readList(
            RegistryFriendlyByteBuf buffer,
            int maxSize,
            String label,
            Function<RegistryFriendlyByteBuf, T> reader
    ) {
        int size = buffer.readVarInt();
        if (size < 0 || size > maxSize) {
            throw new IllegalArgumentException("Invalid Thaumonomicon " + label + " count: " + size);
        }
        ArrayList<T> values = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            values.add(reader.apply(buffer));
        }
        return values;
    }

    private static void writeString(RegistryFriendlyByteBuf buffer, String value, int maxLength, String label) {
        if (value.length() > maxLength) {
            throw new IllegalArgumentException("Thaumonomicon " + label + " is too long: " + value.length());
        }
        buffer.writeUtf(value, maxLength);
    }

    private static String readString(RegistryFriendlyByteBuf buffer, int maxLength) {
        return buffer.readUtf(maxLength);
    }

    private static void writeResourceLocation(RegistryFriendlyByteBuf buffer, ResourceLocation value) {
        writeString(buffer, value.toString(), MAX_VALUE_LENGTH, "resource location");
    }

    private static ResourceLocation readResourceLocation(RegistryFriendlyByteBuf buffer) {
        return TCResearchPageCatalogManager.canonicalId(readString(buffer, MAX_VALUE_LENGTH));
    }

    private static void writeEnum(RegistryFriendlyByteBuf buffer, Enum<?> value) {
        buffer.writeVarInt(value.ordinal());
    }

    private static <T extends Enum<T>> T readEnum(RegistryFriendlyByteBuf buffer, T[] values, String label) {
        int ordinal = buffer.readVarInt();
        if (ordinal < 0 || ordinal >= values.length) {
            throw new IllegalArgumentException("Invalid Thaumonomicon " + label + " ordinal: " + ordinal);
        }
        return values[ordinal];
    }
}

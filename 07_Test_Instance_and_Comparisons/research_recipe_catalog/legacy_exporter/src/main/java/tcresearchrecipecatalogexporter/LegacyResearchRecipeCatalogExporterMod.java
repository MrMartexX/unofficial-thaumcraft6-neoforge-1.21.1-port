package tcresearchrecipecatalogexporter;

import com.google.gson.stream.JsonWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.RecipeMisc;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.research.ResearchAddendum;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.common.config.ConfigRecipes;

@Mod(
        modid = LegacyResearchRecipeCatalogExporterMod.MODID,
        name = "Thaumcraft Research Recipe Catalog Legacy Exporter",
        version = "0.1.0",
        dependencies = "required-after:thaumcraft",
        acceptableRemoteVersions = "*"
)
public final class LegacyResearchRecipeCatalogExporterMod {
    public static final String MODID = "tcresearchrecipecatalogexporter";

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.researchRecipeCatalogDump", "false"))) {
            return;
        }

        Path output = Paths.get(System.getProperty(
                "tc.researchRecipeCatalogDumpPath",
                "research_recipe_catalog/thaumcraft_1_12_research_recipe_catalog.json"
        )).toAbsolutePath().normalize();

        try {
            dump(output);
            System.out.println("[tc-research-recipe-catalog-exporter] Wrote legacy catalog dump to " + output);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException("Failed to write legacy Thaumcraft research recipe catalog dump", exception);
        } finally {
            FMLCommonHandler.instance().getMinecraftServerInstance().initiateShutdown();
        }
    }

    private static void dump(Path output) throws IOException {
        Files.createDirectories(output.getParent());
        LinkedHashMap<String, Reference> references = collectReferences();

        try (BufferedWriter buffered = Files.newBufferedWriter(output, StandardCharsets.UTF_8);
             JsonWriter writer = new JsonWriter(buffered)) {
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("schema").value(2);
            writer.name("side").value("legacy_1_12_2");
            writer.name("environment").beginObject();
            writer.name("minecraft").value("1.12.2");
            writer.name("forge").value("14.23.5.2860");
            writer.name("thaumcraft").value("6.1.BETA26");
            writer.endObject();
            writer.name("occurrence_count").value(countOccurrences(references));
            writer.name("unique_reference_count").value(references.size());
            writer.name("references").beginArray();
            for (Reference reference : references.values()) {
                writeReference(writer, reference);
            }
            writer.endArray();
            writer.endObject();
        }
    }

    private static LinkedHashMap<String, Reference> collectReferences() {
        ArrayList<Occurrence> occurrences = new ArrayList<Occurrence>();
        ArrayList<String> categoryKeys = new ArrayList<String>(ResearchCategories.researchCategories.keySet());
        Collections.sort(categoryKeys);

        for (String categoryKey : categoryKeys) {
            ResearchCategory category = ResearchCategories.researchCategories.get(categoryKey);
            ArrayList<ResearchEntry> entries = new ArrayList<ResearchEntry>(category.research.values());
            Collections.sort(entries, new Comparator<ResearchEntry>() {
                @Override
                public int compare(ResearchEntry left, ResearchEntry right) {
                    return left.getKey().compareTo(right.getKey());
                }
            });

            for (ResearchEntry entry : entries) {
                ResearchStage[] stages = entry.getStages();
                if (stages != null) {
                    for (int index = 0; index < stages.length; index++) {
                        addOccurrences(occurrences, categoryKey, entry.getKey(), "stage", index, stages[index].getRecipes());
                    }
                }

                ResearchAddendum[] addenda = entry.getAddenda();
                if (addenda != null) {
                    for (int index = 0; index < addenda.length; index++) {
                        addOccurrences(occurrences, categoryKey, entry.getKey(), "addendum", index, addenda[index].getRecipes());
                    }
                }
            }
        }

        Collections.sort(occurrences, new Comparator<Occurrence>() {
            @Override
            public int compare(Occurrence left, Occurrence right) {
                int id = left.referenceId.compareTo(right.referenceId);
                if (id != 0) {
                    return id;
                }
                return left.locationKey().compareTo(right.locationKey());
            }
        });

        LinkedHashMap<String, Reference> references = new LinkedHashMap<String, Reference>();
        for (Occurrence occurrence : occurrences) {
            Reference reference = references.get(occurrence.referenceId);
            if (reference == null) {
                reference = new Reference(occurrence.referenceId, resolve(new ResourceLocation(occurrence.referenceId)));
                references.put(occurrence.referenceId, reference);
            }
            reference.occurrences.add(occurrence);
        }
        return references;
    }

    private static void addOccurrences(
            List<Occurrence> occurrences,
            String category,
            String entry,
            String section,
            int sectionIndex,
            ResourceLocation[] recipes
    ) {
        if (recipes == null) {
            return;
        }
        for (int recipeIndex = 0; recipeIndex < recipes.length; recipeIndex++) {
            occurrences.add(new Occurrence(
                    recipes[recipeIndex].toString(),
                    category,
                    entry,
                    section,
                    sectionIndex,
                    recipeIndex
            ));
        }
    }

    private static Resolved resolve(ResourceLocation id) {
        Object value = CommonInternals.getCatalogRecipe(id);
        if (value != null) {
            return describe("thaumcraft_catalog", value);
        }

        value = CommonInternals.getCatalogRecipeFake(id);
        if (value != null) {
            return describe("fake_catalog", value);
        }

        value = CraftingManager.getRecipe(id);
        if (value != null) {
            return describe("crafting_registry", value);
        }

        value = ConfigRecipes.recipeGroups.get(id.toString());
        if (value != null) {
            return describe("recipe_group", value);
        }

        return new Resolved("missing", "missing", "", "", "", null, Collections.<String>emptyList());
    }

    private static Resolved describe(String source, Object value) {
        String kind = kind(value);
        String research = "";
        String group = "";
        ItemStack output = ItemStack.EMPTY;
        ArrayList<String> groupMembers = new ArrayList<String>();

        if (value instanceof IThaumcraftRecipe) {
            IThaumcraftRecipe recipe = (IThaumcraftRecipe) value;
            research = safe(recipe.getResearch());
            group = safe(recipe.getGroup());
        }
        if (value instanceof IRecipe) {
            IRecipe recipe = (IRecipe) value;
            output = recipe.getRecipeOutput();
            group = safe(recipe.getGroup());
            if (recipe instanceof IArcaneRecipe) {
                research = safe(((IArcaneRecipe) recipe).getResearch());
            }
        } else if (value instanceof CrucibleRecipe) {
            output = ((CrucibleRecipe) value).getRecipeOutput();
        } else if (value instanceof InfusionRecipe) {
            Object recipeOutput = ((InfusionRecipe) value).getRecipeOutput();
            if (recipeOutput instanceof ItemStack) {
                output = (ItemStack) recipeOutput;
            }
        } else if (value instanceof RecipeMisc) {
            output = ((RecipeMisc) value).getOutput();
        } else if (value instanceof ThaumcraftApi.BluePrint) {
            output = ((ThaumcraftApi.BluePrint) value).getDisplayStack();
        } else if (value instanceof List) {
            for (Object member : (List<?>) value) {
                groupMembers.add(String.valueOf(member));
            }
            Collections.sort(groupMembers);
        }

        return new Resolved(
                source,
                kind,
                value.getClass().getName(),
                research,
                group,
                stack(output),
                groupMembers
        );
    }

    private static String kind(Object value) {
        if (value instanceof List) {
            return "group";
        }
        if (value instanceof ThaumcraftApi.BluePrint) {
            return "blueprint";
        }
        if (value instanceof CrucibleRecipe) {
            return "crucible";
        }
        if (value instanceof InfusionRecipe) {
            return "infusion";
        }
        if (value instanceof IArcaneRecipe) {
            return "arcane";
        }
        if (value instanceof RecipeMisc) {
            return "misc";
        }
        if (value instanceof IRecipe) {
            return "crafting";
        }
        return "other";
    }

    private static Stack stack(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getItem().getRegistryName() == null) {
            return null;
        }
        return new Stack(
                stack.getItem().getRegistryName().toString(),
                stack.getMetadata(),
                stack.getCount(),
                stack.hasTagCompound() ? stack.getTagCompound().toString() : ""
        );
    }

    private static void writeReference(JsonWriter writer, Reference reference) throws IOException {
        writer.beginObject();
        writer.name("id").value(reference.id);
        writer.name("source").value(reference.resolved.source);
        writer.name("kind").value(reference.resolved.kind);
        writer.name("class").value(reference.resolved.className);
        writer.name("research").value(reference.resolved.research);
        writer.name("group").value(reference.resolved.group);
        writer.name("output");
        writeStack(writer, reference.resolved.output);
        writer.name("group_members").beginArray();
        for (String member : reference.resolved.groupMembers) {
            writer.value(member);
        }
        writer.endArray();
        writer.name("group_member_resolutions").beginArray();
        for (String member : reference.resolved.groupMembers) {
            Resolved resolvedMember = resolve(new ResourceLocation(member));
            writer.beginObject();
            writer.name("id").value(member);
            writeResolved(writer, resolvedMember);
            writer.endObject();
        }
        writer.endArray();
        writer.name("occurrences").beginArray();
        for (Occurrence occurrence : reference.occurrences) {
            writer.beginObject();
            writer.name("category").value(occurrence.category);
            writer.name("entry").value(occurrence.entry);
            writer.name("section").value(occurrence.section);
            writer.name("section_index").value(occurrence.sectionIndex);
            writer.name("recipe_index").value(occurrence.recipeIndex);
            writer.endObject();
        }
        writer.endArray();
        writer.endObject();
    }

    private static void writeResolved(JsonWriter writer, Resolved resolved) throws IOException {
        writer.name("source").value(resolved.source);
        writer.name("kind").value(resolved.kind);
        writer.name("class").value(resolved.className);
        writer.name("research").value(resolved.research);
        writer.name("group").value(resolved.group);
        writer.name("output");
        writeStack(writer, resolved.output);
    }

    private static void writeStack(JsonWriter writer, Stack stack) throws IOException {
        if (stack == null) {
            writer.nullValue();
            return;
        }
        writer.beginObject();
        writer.name("item").value(stack.item);
        writer.name("metadata").value(stack.metadata);
        writer.name("count").value(stack.count);
        writer.name("nbt").value(stack.nbt);
        writer.endObject();
    }

    private static int countOccurrences(Map<String, Reference> references) {
        int count = 0;
        for (Reference reference : references.values()) {
            count += reference.occurrences.size();
        }
        return count;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static final class Reference {
        final String id;
        final Resolved resolved;
        final List<Occurrence> occurrences = new ArrayList<Occurrence>();

        Reference(String id, Resolved resolved) {
            this.id = id;
            this.resolved = resolved;
        }
    }

    private static final class Occurrence {
        final String referenceId;
        final String category;
        final String entry;
        final String section;
        final int sectionIndex;
        final int recipeIndex;

        Occurrence(String referenceId, String category, String entry, String section, int sectionIndex, int recipeIndex) {
            this.referenceId = referenceId;
            this.category = category;
            this.entry = entry;
            this.section = section;
            this.sectionIndex = sectionIndex;
            this.recipeIndex = recipeIndex;
        }

        String locationKey() {
            return category + ":" + entry + ":" + section + ":" + sectionIndex + ":" + recipeIndex;
        }
    }

    private static final class Resolved {
        final String source;
        final String kind;
        final String className;
        final String research;
        final String group;
        final Stack output;
        final List<String> groupMembers;

        Resolved(String source, String kind, String className, String research, String group, Stack output, List<String> groupMembers) {
            this.source = source;
            this.kind = kind;
            this.className = className;
            this.research = research;
            this.group = group;
            this.output = output;
            this.groupMembers = groupMembers;
        }
    }

    private static final class Stack {
        final String item;
        final int metadata;
        final int count;
        final String nbt;

        Stack(String item, int metadata, int count, String nbt) {
            this.item = item;
            this.metadata = metadata;
            this.count = count;
            this.nbt = nbt;
        }
    }
}

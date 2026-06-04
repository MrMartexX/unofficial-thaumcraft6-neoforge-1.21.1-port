package thaumcraft.common.research;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

final class TCThaumonomiconService {
    private TCThaumonomiconService() {
    }

    static TCThaumonomiconIndexPayload buildIndex(ServerPlayer player) {
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        ArrayList<TCThaumonomiconCategoryView> categories = new ArrayList<>();
        ArrayList<TCThaumonomiconResearchView> entries = new ArrayList<>();

        for (TCResearchCategoryDefinition category : TCResearchManager.categories()) {
            if (!TCResearchManager.isCategoryVisible(knowledge, category.key())) {
                continue;
            }
            categories.add(new TCThaumonomiconCategoryView(
                    category.key(),
                    category.requiredResearch(),
                    location(category.icon()),
                    location(category.background()),
                    location(category.overlay())
            ));
            for (TCResearchEntryDefinition entry : TCResearchManager.visibleEntriesByCategory(player, category.key())) {
                entries.add(researchView(entry, player, knowledge));
            }
        }
        return new TCThaumonomiconIndexPayload(categories, entries);
    }

    static Optional<TCThaumonomiconEntryView> buildEntry(ServerPlayer player, String researchKey) {
        Optional<TCResearchPageStageSelection> selectionOptional = TCResearchPageCatalogManager.selectCurrentStage(
                player,
                researchKey
        );
        if (selectionOptional.isEmpty()) {
            return Optional.empty();
        }

        TCResearchPageStageSelection selection = selectionOptional.get();
        TCResearchStageDefinition stage = selection.stage();
        TCResearchStageRequirementResult requirements = TCResearchManager.checkCurrentStageRequirements(
                player,
                selection.entry().key()
        );
        List<String> addendumTexts = selection.visibleAddenda().stream()
                .map(TCResearchStageDefinition::text)
                .toList();

        return Optional.of(new TCThaumonomiconEntryView(
                researchView(selection.entry(), player, TCPlayerKnowledgeStore.get(player)),
                selection.stageIndex(),
                selection.complete(),
                stage.text(),
                addendumTexts,
                stage.requiredResearch(),
                stage.requiredCraft(),
                stage.requiredItem(),
                stage.requiredKnowledge(),
                stage.warp(),
                requirements.satisfied(),
                requirements.missing(),
                requirements.blocked(),
                TCResearchPageCatalogManager.bookmarksForCurrentStage(player, selection.entry().key())
        ));
    }

    private static TCThaumonomiconResearchView researchView(
            TCResearchEntryDefinition entry,
            ServerPlayer player,
            TCPlayerKnowledge knowledge
    ) {
        List<TCResearchFlag> flags = knowledge.researchFlags()
                .getOrDefault(entry.key(), java.util.Set.of())
                .stream()
                .sorted()
                .toList();
        return new TCThaumonomiconResearchView(
                entry.key(),
                entry.name(),
                entry.icons(),
                entry.category(),
                entry.locationX(),
                entry.locationY(),
                entry.parents(),
                entry.siblings(),
                entry.meta(),
                TCResearchManager.getResearchStatus(knowledge, entry.key()),
                TCResearchManager.canUnlockResearch(player, entry.key()),
                flags,
                knowledge.getResearchStage(entry.key()),
                entry.stages().size()
        );
    }

    private static String location(ResourceLocation location) {
        return location == null ? "" : location.toString();
    }
}

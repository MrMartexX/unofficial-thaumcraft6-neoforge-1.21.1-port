package thaumcraft.common.research;

record TCResearchPageDrilldownResult(
        TCResearchPageBookmark bookmark,
        int pageIndex
) {
    TCResearchPageDrilldownResult {
        pageIndex = Math.max(0, Math.min(pageIndex, Math.max(0, bookmark.pages().size() - 1)));
    }
}

# Thaumcraft Research Recipe/Page Catalog Parity

This directory records the exact Forge 1.12.2 resolver behavior behind
`ResearchStage.getRecipes()` and `ResearchAddendum.getRecipes()`.

Legacy `GuiResearchPage` resolves each reference in this order:

1. `CommonInternals.craftingRecipeCatalog`;
2. `CommonInternals.craftingRecipeCatalogFake`;
3. `CraftingManager`;
4. `ConfigRecipes.recipeGroups`.

The legacy exporter records the selected source, concrete recipe/page kind,
research gate, group, output identity, group members, and every research
location that uses the reference. This is the authoritative input for the
NeoForge permanent research page catalog. It is not runtime gameplay content.

`tools/generate_and_compare_catalog.py` transforms the legacy dump into the
runtime `data/thaumcraft/research_page_catalog/legacy_builtin.json` seed and
verifies that every modern research occurrence and every direct catalog field
matches the runtime legacy export.

## Build the legacy exporter

```powershell
$env:GRADLE_OPTS='--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.invoke=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED'
& 'D:\Thaumcraft_6_port_to_1.21.1\02_existing_decompiled_repo\Thaumcraft-6-Source-Code-master\gradlew.bat' -p 'D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_recipe_catalog\legacy_exporter' build --no-daemon
```

## Run the legacy exporter

```powershell
cd 'D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_recipe_catalog\legacy_server'
$dump='D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_recipe_catalog\thaumcraft_1_12_research_recipe_catalog.json'
& 'C:\Users\Martin\AppData\Roaming\.minecraft\runtime\jre-legacy\windows\jre-legacy\bin\java.exe' -Xmx3G '-Dtc.researchRecipeCatalogDump=true' "-Dtc.researchRecipeCatalogDumpPath=$dump" -jar 'forge-1.12.2-14.23.5.2860.jar' nogui
```

## Generate and compare the NeoForge catalog seed

```powershell
& 'C:\Users\Martin\.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe' 'D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_recipe_catalog\tools\generate_and_compare_catalog.py'
```

## Run the NeoForge runtime audits

```powershell
cd 'D:\Thaumcraft_6_port_to_1.21.1\05_neoforge_port'
.\gradlew.bat runServer --no-daemon -PtcResearchPageCatalogAudit=true "-PtcResearchPageCatalogAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_recipe_catalog\thaumcraft_1_21_research_recipe_catalog.md"
.\gradlew.bat runServer --no-daemon -PtcThaumonomiconProtocolAudit=true "-PtcThaumonomiconProtocolAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_recipe_catalog\thaumonomicon_protocol_audit.md"
```

Latest checked results:

- catalog parity: `253` occurrences, `203` direct references, `325` total
  entries including group members, `0` field differences;
- catalog structural validation: `0` errors;
- server-authoritative Thaumonomicon protocol: `15/15` checks passed.

# Research requirement blocker audit

Status: current blocker audit for the `research-knowledge-scanning-design` branch after the non-interactive requirement audit exporter.

This document records the current research-stage requirement state and separates registry identity resolution from real gameplay readiness. That distinction matters: an item id can be resolvable while still being only a bridge for a subsystem that is not implemented yet.

## Latest automated snapshot

Command:

```powershell
.\gradlew.bat runServer --no-daemon -PtcResearchRequirementAudit=true "-PtcResearchRequirementAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_requirement_audit\thaumcraft_1_21_research_requirements.md" -PtcResearchRequirementAuditDetailLimit=200
```

Generated report:

```text
07_Test_Instance_and_Comparisons/research_requirement_audit/thaumcraft_1_21_research_requirements.md
```

Current summary:

| Requirement class | Result |
|---|---:|
| `required_item` | `69/69` resolved |
| `required_craft` | `34/34` modern-matchable |
| `required_knowledge` | `170/170` resolved |
| Total unresolved item/craft/knowledge requirements | `0` |
| Identity bridge / placeholder warnings | `28` |

Interpretation: the parser, registry lookup, legacy flattening, component-backed aspect stack matching, component-backed metadata-family matching, real vanilla stored-enchantment matching, and knowledge requirement conversion now cover every loaded stage requirement. The remaining risk is semantic, not parser-level: the remaining warnings are bridge identities or placeholders whose final source, recipe, item behavior, menu, or subsystem behavior is still owned by later slices.

## Bridge warning families

| Count | Requirement type | Family | Interpretation |
|---:|---|---|---|
| 5 | `required_craft` | auromancy placeholders | Focus/caster/vis ids are resolvable, but focus sockets, caster behavior, vis costs and related recipe flows are not implemented. |
| 5 | `required_craft` | thaumium tool placeholders | Tool ids are resolvable, but actual tool classes, material behavior, enchant/repair rules and elemental behavior are not implemented. |
| 4 | `required_craft` | research/crafting station placeholders | Arcane workbench, research table, scribing tools and wand workbench ids are resolvable; full menus, recipes and research-table flow remain blocked. |
| 3 | `required_craft` | alchemy placeholders | Tallow, smelter and crucible ids are resolvable; alchemy/crucible/smelter systems remain blocked. |
| 2 | `required_craft` | legacy flattened vanilla metadata bridge | Legacy `minecraft:dye` and `minecraft:web` craft markers are matchable through flattened modern ids, but exact direct craft-hash parity remains unresolved. |
| 2 | `required_item` | legacy flattened vanilla metadata bridge | Legacy `minecraft:noteblock`/`minecraft:dye;1;15` item requirements are matchable through flattened modern ids, but remain documented compatibility bridges. |
| 1 | `required_craft` | alchemy identity bridge | `thaumcraft:nitor;1;4` is mapped to the current yellow nitor identity, but nitor recipe/alchemy behavior is not implemented. |
| 1 | `required_craft` | infusion placeholder | Infusion matrix id is resolvable, but multiblock, recipe and stability behavior are not implemented. |
| 1 | `required_craft` | safe renamed Thaumcraft identity bridge | Legacy `arcane_stone` maps to `stone_arcane`; craft marker parity still needs the direct hash exporter. |
| 1 | `required_item` | artifice mirror placeholder | Mirrored glass id is resolvable, but mirror behavior is not implemented. |
| 1 | `required_item` | biological component identity | Zombie brain id is resolvable, but golem/brain subsystem behavior is not implemented. |
| 1 | `required_item` | curio metadata identity bridge | Crimson Rites curio is component-resolved, but curio acquisition/behavior remains a bridge. |
| 1 | `required_item` | legacy OreDictionary tag bridge | `oredict:chest` is tag-matchable, but exact legacy OreDictionary behavior stays documented as a bridge. |

The generated report contains the complete warning list. Aspect crystal essence, filled phial, material-family metadata and enchanted-placeholder requirements are no longer warning buckets because their requirement semantics are now represented by DataComponents or real vanilla enchantment components. This does not mean their producer subsystems are finished.

## Safe conclusions

- `required_knowledge` is clean for all loaded research stages.
- `required_item` and `required_craft` no longer have registry-identity gaps in the current branch.
- Aspect crystal essence and filled phial requirements now require matching aspect/amount components.
- Legacy material-family metadata requirements now require matching family/variant/metadata components.
- Legacy enchanted-placeholder requirements now match real modern enchanted item/book stacks with the required enchantment level, following the legacy `InventoryUtils.checkEnchantedPlaceholder` role.
- `0` unresolved does not mean research progression is gameplay-complete.
- The shared `TCResearchRequirementResolver` remains the single source for legacy requirement interpretation.
- The non-interactive exporter is now the preferred gate for requirement changes because it runs after real server data reload and does not depend on manual console input.
- Exact legacy direct `ItemStack.toString().hashCode()` craft marker parity remains unresolved and still requires a dedicated 1.12 exporter/mapping pass.

## Still blocked

- Crystal essence and filled phial production, filling/draining and essentia container behavior.
- Real arcane workbench, wand workbench, research table and scribing tools behavior.
- Focus/caster/vis behavior.
- Crucible, nitor, smelter and broader alchemy behavior.
- Infusion matrix and infusion recipe behavior.
- Thaumium tools and material behavior.
- Source recipes/drops for material-family bridge items.
- Full Thaumonomicon UI, rewards, recipe unlock side effects and broad player knowledge/stage sync.

## Pass criteria

- The generated audit must stay at `0` identity-unresolved requirements unless new legacy data is added.
- Bridge warning count may decrease only when the corresponding subsystem-owned semantics are real, not because a dummy id was added.
- Every new mapping must be safe for both requirement checking and item consumption or craft marker matching.
- Audit output must keep describing bridge warnings by semantic family, not just by registry id.

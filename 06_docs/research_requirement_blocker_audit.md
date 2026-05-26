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
| Identity bridge / placeholder warnings | `69` |

Interpretation: the parser, registry lookup, legacy flattening, material-family mapping, and knowledge requirement conversion now cover every loaded stage requirement. The remaining risk is semantic, not parser-level: many resolved ids are bridge identities or placeholders whose final source, recipe, item behavior, or container/component semantics are still owned by later subsystem slices.

## Bridge warning families

| Count | Requirement type | Family | Interpretation |
|---:|---|---|---|
| 23 | `required_item` | aspect crystal essence bridge | Flattened aspect-specific ids currently stand in for legacy NBT aspect stacks. This is acceptable for requirement identity validation, but final crystal essence behavior still needs DataComponent/container design. |
| 7 | `required_item` | essentia phial bridge | Flattened filled phial ids currently stand in for legacy aspect NBT. Real phial filling, draining, stack rules and essentia semantics remain blocked. |
| 5 | `required_craft` | auromancy placeholders | Focus/caster/vis ids are resolvable, but focus sockets, caster behavior, vis costs and related recipe flows are not implemented. |
| 5 | `required_craft` | thaumium tool placeholders | Tool ids are resolvable, but actual tool classes, material behavior, enchant/repair rules and elemental behavior are not implemented. |
| 4 | `required_craft` | legacy metadata material family | `ingot` and `metal` metadata families map to modern ids, but the real material/recipe source is still an identity bridge. |
| 4 | `required_craft` | research/crafting station placeholders | Arcane workbench, research table, scribing tools and wand workbench ids are resolvable; full menus, recipes and research-table flow remain blocked. |
| 4 | `required_item` | enchanted placeholder bridge | Legacy enchantment-placeholder requirements map to explicit placeholder ids. Final policy should use real modern enchantment components/stacks. |
| 3 | `required_craft` | alchemy placeholders | Tallow, smelter and crucible ids are resolvable; alchemy/crucible/smelter systems remain blocked. |
| 3 | `required_item` | legacy metadata material family | `plate` and `nugget` metadata families map to modern ids, but full material item semantics and recipe sources still need their own slice. |

The generated report contains the complete warning list, including single-reference bridges such as mirrored glass, zombie brain, curio metadata, `arcane_stone` rename, `oredict:chest`, and flattened vanilla metadata ids.

## Safe conclusions

- `required_knowledge` is clean for all loaded research stages.
- `required_item` and `required_craft` no longer have registry-identity gaps in the current branch.
- `0` unresolved does not mean research progression is gameplay-complete.
- The shared `TCResearchRequirementResolver` remains the single source for legacy requirement interpretation.
- The non-interactive exporter is now the preferred gate for requirement changes because it runs after real server data reload and does not depend on manual console input.
- Exact legacy direct `ItemStack.toString().hashCode()` craft marker parity remains unresolved and still requires a dedicated 1.12 exporter/mapping pass.

## Still blocked

- Full crystal essence and filled phial DataComponent/container semantics.
- Real arcane workbench, wand workbench, research table and scribing tools behavior.
- Focus/caster/vis behavior.
- Crucible, nitor, smelter and broader alchemy behavior.
- Infusion matrix and infusion recipe behavior.
- Thaumium tools and material behavior.
- Enchanted-placeholder replacement with real modern enchantment-component stack matching.
- Full Thaumonomicon UI, rewards, recipe unlock side effects and broad player knowledge/stage sync.

## Pass criteria

- The generated audit must stay at `0` identity-unresolved requirements unless new legacy data is added.
- Bridge warning count may decrease only when the corresponding subsystem-owned semantics are real, not because a dummy id was added.
- Every new mapping must be safe for both requirement checking and item consumption or craft marker matching.
- Audit output must keep describing bridge warnings by semantic family, not just by registry id.

# Legacy source selection

## Decision

| Role | Source | Authority |
|---|---|---|
| Primary readable source | `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/` | Class roles, readable behavior, formulas, registry construction, recipes, GUI/container and renderer references |
| Secondary decompiler cross-check | `03_self_decompiled_check/vineflower_thaumcraft6/` | Explicit conflict probe and fallback when the primary source is incomplete or suspicious |
| Packaged artifact | `01_original_jar/Thaumcraft-1.12.2-6.1.BETA26.jar` | Class/resource existence and final tie-breaker for packaged content |
| Runtime truth | Legacy and port runtime exporters | State-dependent behavior that cannot be proven from decompiled source alone |

The primary source is selected because its MCP/deobfuscated names and retained comments make it the most useful implementation reference. It is not treated as infallible. A readable decompile, a second decompile and the packaged jar answer different questions.

## Conflict policy

1. Use the primary source for the initial implementation hypothesis.
2. If a class, ID, resource or method is missing or contradictory, probe the Vineflower source.
3. If the sources still disagree, inspect the original jar class/resource inventory.
4. For dynamic values, metadata variants and side effects, use a runtime exporter/comparer instead of guessing.
5. Record the conflict and evidence. A secondary difference must never silently replace the primary baseline.

## Manifest policy

- Every cached legacy manifest records its schema version and SHA-256 fingerprints for all source files used.
- Explicit registry strings and deterministic loops are high-confidence evidence.
- Symbol-to-snake-case inference is retained for review, but is not eligible for strict identity comparison until confirmed or mapped.
- Legacy metadata variants remain variants of one legacy registry entry. Modern split IDs require an explicit `variant-mapping.json` rule in a later batch.
- No legacy class is executed by the audit framework.

This decision follows `NeoForge_legacy_migration_guide.md`: legacy code is a behavior and identity reference, while registration, storage, networking, menus and rendering must use NeoForge 1.21.1 architecture.

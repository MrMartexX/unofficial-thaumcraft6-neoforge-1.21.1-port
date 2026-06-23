# Legacy source selection

Status: authoritative source-of-truth policy for item/block parity audit work.

## Decision

| Role | Source | Authority |
|---|---|---|
| Primary readable source | `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/` | Class roles, readable behavior, formulas, registry construction, recipes, GUI/container and renderer references, comments and variant hints |
| Secondary decompiler cross-check | `03_self_decompiled_check/vineflower_thaumcraft6/` | Explicit conflict probe and fallback when primary source is incomplete, suspicious or contradictory |
| Packaged artifact | `01_original_jar/Thaumcraft-1.12.2-6.1.BETA26.jar` | Packaged class/resource existence and final tie-breaker for packaged content |
| Runtime truth | Legacy and port runtime exporters | Dynamic state, side effects, metadata behavior and values that cannot be proven from source alone |

The primary source is selected because its MCP/deobfuscated names and retained comments make it the most useful implementation reference. It is not infallible. A readable decompile, a second decompile and the packaged jar answer different questions.

## Conflict policy

1. Use the primary source for the initial implementation hypothesis.
2. If a class, ID, resource or method is missing or contradictory, probe the Vineflower source.
3. If the sources still disagree, inspect the original jar class/resource inventory.
4. For dynamic values, metadata variants and side effects, use a runtime exporter/comparer instead of guessing.
5. Record the conflict and evidence. A secondary difference must never silently replace the primary baseline.

## Conflict statuses

```text
PRIMARY_CONFIRMED
SECONDARY_MATCH
SECONDARY_DIFF_NON_BLOCKING
SECONDARY_DIFF_REVIEW_NEEDED
JAR_FALLBACK_REQUIRED
PRIMARY_SOURCE_GAP
```

## Manifest policy

- Every cached legacy manifest records schema version and SHA-256 fingerprints for all source files used.
- Explicit registry strings and deterministic loops are high-confidence evidence.
- Symbol-to-snake-case inference is retained for review, but is not eligible for strict identity comparison until confirmed or mapped.
- Legacy metadata variants remain variants of one legacy registry entry. Modern split IDs require explicit `variant-mapping.json` rules.
- No legacy class is executed by the audit framework.

## Practical source use

| Evidence needed | First source | Fallback |
|---|---|---|
| Behavior formulas | Primary source | Runtime exporter if stateful |
| Class roles | Primary source | Secondary source |
| Packaged assets | Original jar / imported asset corpus | Secondary source asset dump |
| Metadata variant hints | Primary source comments and registration code | Original jar/runtime exporter |
| Dynamic side effects | Runtime exporter/comparer | Manual source review |

# Exact-path resource triage

The strict linter originally reported 77 references, not 77 distinct missing
assets. No fallback redirection was restored to make the report green.

| Owner | References | Disposition |
| --- | ---: | --- |
| Active `hole` blockstate | 2 | Replaced invalid Forge-era variants with a modern empty-model variant. |
| Unregistered `activator_rail` reference | 12 | Retain until that family is implemented. |
| 17 unregistered `banner_*` references | 34 | Retain legacy references; not registrations in current TCBlocks. |
| Unregistered `brain_box` reference | 6 | Retain legacy reference. |
| Unregistered `essentia_input` alias reference | 7 | Current active transport identities use other IDs. |
| Unregistered `essentia_output` alias reference | 7 | Current active transport identities use other IDs. |
| Unregistered `redstone_relay` reference | 9 | Retain until this device family is implemented. |

The strict whole-import report therefore still contains 75 findings across 22
unregistered blockstate files. They remain visible rather than being marked valid.
Registration changes must revisit this classification before activating these IDs.

A real client startup additionally found an active glimmer model referencing
`minecraft:block/barrier`, which is not a valid block-atlas texture. Its empty
model now has no geometry and a valid particle texture, consistent with the
invisible block. The recharge sound event also lacked a matching sounds.json key;
the correct key was added while preserving the original misspelled alias.
After rebuilding, client smoke reached a rendered title screen without either
warning. This is resource/startup evidence, not full in-world visual parity.

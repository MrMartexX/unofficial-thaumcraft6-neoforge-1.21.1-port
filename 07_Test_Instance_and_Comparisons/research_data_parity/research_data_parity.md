# Research data parity comparison

The legacy `assets/thaumcraft/research` JSON is the authoritative TC6 data source. The comparison separately verifies the NeoForge resource copy and the data produced by the NeoForge runtime parser.

| Check | Result |
|---|---:|
| Legacy JSON files | `8` |
| NeoForge JSON files | `8` |
| Legacy entries | `148` |
| Source resource differences | `0` |
| Runtime parser differences | `0` |
| Legacy category differences | `0` |
| Progression semantic checks | `10/10` passed |

## Source resource parity

Exact normalized match.

## Runtime parser parity

Exact normalized match.

## Java-registered category parity

Exact normalized match.

## Progression semantic checks

| Check | Result | Actual | Expected |
|---|---|---|---|
| `start_gated_stage` | `PASS` | `1/false/0` | `1/false/0` |
| `advance_non_final_stage_double_warp` | `PASS` | `2/false/4` | `2/false/4` |
| `advance_final_stage_double_warp` | `PASS` | `3/true/10` | `3/true/10` |
| `single_empty_stage_auto_complete` | `PASS` | `2/true/3` | `2/true/3` |
| `final_empty_stage_combines_warp` | `PASS` | `3/true/5` | `3/true/5` |
| `split_warp_1` | `PASS` | `1/0` | `1/0` |
| `split_warp_2` | `PASS` | `1/1` | `1/1` |
| `split_warp_3` | `PASS` | `2/1` | `2/1` |
| `split_warp_5` | `PASS` | `3/2` | `3/2` |
| `parser_preserves_reward_and_addendum_fields` | `PASS` | `preserved` | `preserved` |

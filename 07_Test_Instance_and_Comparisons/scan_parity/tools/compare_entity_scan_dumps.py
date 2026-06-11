#!/usr/bin/env python3
import argparse
import json
from collections import Counter
from pathlib import Path
from typing import Any


ROOT = Path(__file__).resolve().parents[1]
LEGACY_DUMP = ROOT / "dumps" / "thaumcraft_1_12_scan_entities.json"
MODERN_DUMP = ROOT / "dumps" / "thaumcraft_1_21_scan_entities.json"
POLICY_FILE = ROOT / "input" / "entity_scan_diff_policy.json"

RAW_BUCKETS = [
    "SCAN_AND_ASPECTS_IDENTICAL",
    "SCAN_KEYS_IDENTICAL_ASPECT_DIFF",
    "SCAN_KEY_ORDER_ONLY_DIFF",
    "SCAN_KEY_SET_DIFF",
    "SCAN_FOUND_DIFF",
    "LEGACY_ONLY",
    "MODERN_ONLY",
]


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Compare legacy and modern Thaumcraft entity scan runtime dumps.")
    parser.add_argument("--legacy", type=Path, default=LEGACY_DUMP, help="Path to the Forge 1.12.2 entity scan dump.")
    parser.add_argument("--modern", type=Path, default=MODERN_DUMP, help="Path to the NeoForge 1.21.1 entity scan dump.")
    parser.add_argument("--policy", type=Path, default=POLICY_FILE, help="Entity scan mapping and expected-difference policy.")
    parser.add_argument("--out-dir", type=Path, default=ROOT / "reports", help="Directory for entity_scan_diff.json/md.")
    return parser.parse_args()


def load_json(path: Path) -> dict[str, Any]:
    with path.open("r", encoding="utf-8") as handle:
        return json.load(handle)


def load_optional_json(path: Path) -> dict[str, Any]:
    if not path.exists():
        return {}
    return load_json(path)


def normalize_key(entry: dict[str, Any], side: str, policy: dict[str, Any]) -> tuple[str | None, str | None, str]:
    original_key = entry.get("comparison_key")
    if not original_key:
        return None, None, "unmapped"
    if side == "legacy":
        aliases = policy.get("key_aliases", {})
        if original_key in aliases:
            return aliases[original_key], original_key, "key_alias"
    return original_key, original_key, "comparison_key"


def index_entries(
    dump: dict[str, Any],
    side: str,
    policy: dict[str, Any],
) -> tuple[dict[str, dict[str, Any]], dict[str, list[dict[str, Any]]]]:
    out: dict[str, dict[str, Any]] = {}
    duplicates: dict[str, list[dict[str, Any]]] = {}
    for original in dump.get("entries", []):
        key, original_key, source = normalize_key(original, side, policy)
        if not key:
            continue
        entry = dict(original)
        entry["_comparison_key_normalized"] = key
        entry["_comparison_key_original"] = original_key
        entry["_comparison_source"] = source
        if key in out:
            duplicates.setdefault(key, [out[key]]).append(entry)
            continue
        out[key] = entry
    return out, duplicates


def resolved_aspect_result(entry: dict[str, Any]) -> dict[str, Any]:
    object_aspects = entry.get("object_aspects", {})
    if object_aspects.get("result_kind") == "aspects":
        return object_aspects
    generated_aspects = entry.get("generated_aspects", {})
    if generated_aspects.get("result_kind") == "aspects":
        return generated_aspects
    return object_aspects or generated_aspects or {"result_kind": "missing", "aspects": []}


def aspect_signature(entry: dict[str, Any]) -> tuple[str, tuple[tuple[str | None, int], ...]]:
    result = resolved_aspect_result(entry)
    aspects = tuple(sorted((aspect.get("id"), int(aspect.get("amount", 0))) for aspect in result.get("aspects", [])))
    return result.get("result_kind", "missing"), aspects


def normalize_research_keys(
    entry: dict[str, Any],
    side: str,
    policy: dict[str, Any],
    paired_entry: dict[str, Any] | None = None,
) -> list[str]:
    aliases = policy.get("research_key_aliases", {})
    keys: list[str] = []
    for raw in entry.get("matched_research_keys", []):
        key = aliases.get(raw, raw)
        if side == "legacy" and paired_entry is not None:
            key = normalize_legacy_generic_entity_key(key, entry, paired_entry)
        keys.append(aliases.get(key, key))
    return keys


def normalize_legacy_generic_entity_key(key: str, legacy: dict[str, Any], modern: dict[str, Any]) -> str:
    legacy_entity_string = legacy.get("legacy_entity_string")
    modern_entity_id = modern.get("entity")
    if legacy_entity_string and modern_entity_id and key == "!" + legacy_entity_string:
        return "!" + modern_entity_id
    return key


def classify_raw(legacy: dict[str, Any], modern: dict[str, Any], policy: dict[str, Any]) -> str:
    legacy_keys = normalize_research_keys(legacy, "legacy", policy, modern)
    modern_keys = normalize_research_keys(modern, "modern", policy)
    legacy_found = bool(legacy.get("scan_found"))
    modern_found = bool(modern.get("scan_found"))

    if legacy_found != modern_found:
        return "SCAN_FOUND_DIFF"

    if legacy_keys != modern_keys:
        if sorted(legacy_keys) == sorted(modern_keys):
            return "SCAN_KEY_ORDER_ONLY_DIFF"
        return "SCAN_KEY_SET_DIFF"

    if aspect_signature(legacy) != aspect_signature(modern):
        return "SCAN_KEYS_IDENTICAL_ASPECT_DIFF"

    return "SCAN_AND_ASPECTS_IDENTICAL"


def classify_policy(
    raw_category: str,
    key: str,
    legacy: dict[str, Any] | None,
    modern: dict[str, Any] | None,
    policy: dict[str, Any],
) -> dict[str, str]:
    if raw_category == "SCAN_AND_ASPECTS_IDENTICAL":
        return {
            "classification": "PARITY_OK",
            "root_cause": "Entity scan result and resolved aspect list match after legacy-to-modern id normalization.",
            "action": "No action.",
        }

    expected_modern_entity_aspect_policy = policy.get("expected_modern_entity_aspect_policy", {}).get(key)
    if expected_modern_entity_aspect_policy:
        return {
            "classification": "EXPECTED_MODERN_ENTITY_ASPECT_POLICY",
            "root_cause": expected_modern_entity_aspect_policy,
            "action": "Keep documented as a deliberate post-parity gameplay policy row.",
        }

    if raw_category == "LEGACY_ONLY":
        original_key = legacy.get("_comparison_key_original") if legacy else key
        expected = policy.get("expected_legacy_only", {}).get(original_key) or policy.get("expected_legacy_only", {}).get(key)
        if expected:
            return {
                "classification": "EXPECTED_LEGACY_ONLY",
                "root_cause": expected,
                "action": "No action until that legacy subsystem/entity family is ported.",
            }
        return {
            "classification": "LEGACY_ONLY_MAPPING_REVIEW",
            "root_cause": "The entity exists only in the 1.12.2 scan dump after configured aliases.",
            "action": "Add a rename alias, document removal, or port the missing entity family.",
        }

    if raw_category == "MODERN_ONLY":
        if key.startswith("entity:minecraft:"):
            return {
                "classification": "EXPECTED_VERSION_DIFFERENCE",
                "root_cause": "The entity exists only in modern Minecraft after 1.12.2 or is a display/helper type with no legacy equivalent.",
                "action": "Keep documented; assign Thaumcraft-style aspects separately where gameplay-visible.",
            }
        return {
            "classification": "MODERN_ONLY_POLICY_REVIEW",
            "root_cause": "The key exists only in the 1.21.1 scan dump.",
            "action": "Document as new content or map if this is a renamed legacy entity.",
        }

    if raw_category == "SCAN_KEYS_IDENTICAL_ASPECT_DIFF":
        return {
            "classification": "PORT_GAP_ENTITY_ASPECTS",
            "root_cause": "The same entity scan keys matched, but the resolved entity aspects differ.",
            "action": "Trace TCEntityAspectAssignments and the legacy EntityList string/NBT behavior.",
        }

    return {
        "classification": "PORT_GAP_ENTITY_SCAN_LOGIC",
        "root_cause": "Comparable legacy-equivalent entity differs in scan matching or research keys.",
        "action": "Trace ScanEntity registration, generic entity key generation, and state-specific matching.",
    }


def classified_entry(
    raw_category: str,
    key: str,
    legacy: dict[str, Any] | None,
    modern: dict[str, Any] | None,
    policy: dict[str, Any],
) -> dict[str, Any]:
    policy_result = classify_policy(raw_category, key, legacy, modern, policy)
    out: dict[str, Any] = {"comparison_key": key, "raw_category": raw_category}
    out.update(policy_result)
    if legacy is not None:
        out["legacy_comparison_key"] = legacy.get("_comparison_key_original")
        out["legacy_comparison_source"] = legacy.get("_comparison_source")
        out["legacy_entity"] = legacy.get("entity")
        out["legacy_entity_string"] = legacy.get("legacy_entity_string")
        out["legacy_variant"] = legacy.get("variant")
        out["legacy_scan_found"] = legacy.get("scan_found")
        out["legacy_research_keys"] = normalize_research_keys(legacy, "legacy", policy, modern)
        out["legacy_aspects"] = resolved_aspect_result(legacy)
    if modern is not None:
        out["modern_comparison_key"] = modern.get("_comparison_key_original")
        out["modern_comparison_source"] = modern.get("_comparison_source")
        out["modern_entity"] = modern.get("entity")
        out["modern_variant"] = modern.get("variant")
        out["modern_scan_found"] = modern.get("scan_found")
        out["modern_research_keys"] = normalize_research_keys(modern, "modern", policy)
        out["modern_aspects"] = resolved_aspect_result(modern)
    return out


def main() -> int:
    args = parse_args()
    policy = load_optional_json(args.policy)
    legacy_dump = load_json(args.legacy)
    modern_dump = load_json(args.modern)
    legacy_entries, legacy_duplicates = index_entries(legacy_dump, "legacy", policy)
    modern_entries, modern_duplicates = index_entries(modern_dump, "modern", policy)

    legacy_keys = set(legacy_entries)
    modern_keys = set(modern_entries)
    comparable_keys = sorted(legacy_keys & modern_keys)
    buckets: dict[str, list[dict[str, Any]]] = {name: [] for name in RAW_BUCKETS}
    classified: list[dict[str, Any]] = []

    for key in comparable_keys:
        legacy = legacy_entries[key]
        modern = modern_entries[key]
        raw_category = classify_raw(legacy, modern, policy)
        item = classified_entry(raw_category, key, legacy, modern, policy)
        buckets[raw_category].append(item)
        classified.append(item)

    for key in sorted(legacy_keys - modern_keys):
        legacy = legacy_entries[key]
        item = classified_entry("LEGACY_ONLY", key, legacy, None, policy)
        buckets["LEGACY_ONLY"].append(item)
        classified.append(item)

    for key in sorted(modern_keys - legacy_keys):
        modern = modern_entries[key]
        item = classified_entry("MODERN_ONLY", key, None, modern, policy)
        buckets["MODERN_ONLY"].append(item)
        classified.append(item)

    classification_summary = Counter(item["classification"] for item in classified)
    report = {
        "legacy_dump": str(args.legacy),
        "modern_dump": str(args.modern),
        "policy_file": str(args.policy) if args.policy.exists() else None,
        "legacy_entry_count": legacy_dump.get("entry_count"),
        "modern_entry_count": modern_dump.get("entry_count"),
        "legacy_predicate_count": legacy_dump.get("predicate_count"),
        "modern_predicate_count": modern_dump.get("predicate_count"),
        "legacy_comparison_key_count": len(legacy_entries),
        "modern_comparison_key_count": len(modern_entries),
        "comparable_key_count": len(comparable_keys),
        "legacy_duplicate_comparison_keys": {key: len(values) for key, values in legacy_duplicates.items()},
        "modern_duplicate_comparison_keys": {key: len(values) for key, values in modern_duplicates.items()},
        "summary": {name: len(items) for name, items in buckets.items()},
        "classification_summary": dict(sorted(classification_summary.items())),
        "diffs": buckets,
        "classified_diffs": classified,
    }

    args.out_dir.mkdir(parents=True, exist_ok=True)
    json_path = args.out_dir / "entity_scan_diff.json"
    md_path = args.out_dir / "entity_scan_diff.md"
    json_path.write_text(json.dumps(report, indent=2, ensure_ascii=False), encoding="utf-8")
    write_markdown(report, md_path)
    return 0


def write_markdown(report: dict[str, Any], path: Path) -> None:
    lines = [
        "# Thaumcraft Entity Scan Dump Diff",
        "",
        f"- Legacy entries: `{report['legacy_entry_count']}`",
        f"- Modern entries: `{report['modern_entry_count']}`",
        f"- Legacy predicates: `{report['legacy_predicate_count']}`",
        f"- Modern predicates: `{report['modern_predicate_count']}`",
        f"- Legacy comparison keys: `{report['legacy_comparison_key_count']}`",
        f"- Modern comparison keys: `{report['modern_comparison_key_count']}`",
        f"- Comparable keys: `{report['comparable_key_count']}`",
        f"- Entity mapping policy: `{report['policy_file']}`",
        "",
        "## Raw Summary",
        "",
        "| Category | Count |",
        "|---|---:|",
    ]
    for name, count in report["summary"].items():
        lines.append(f"| `{name}` | {count} |")

    lines.extend([
        "",
        "## Classification Summary",
        "",
        "| Classification | Count |",
        "|---|---:|",
    ])
    for name, count in sorted(report["classification_summary"].items()):
        lines.append(f"| `{name}` | {count} |")

    lines.extend(["", "## First Actionable Entity Gaps", ""])
    gap_count = 0
    for item in report["classified_diffs"]:
        classification = item["classification"]
        if not classification.startswith("PORT_GAP"):
            continue
        lines.append(
            f"- `{item['comparison_key']}`: `{classification}` - legacy `{item.get('legacy_research_keys')}` / `{item.get('legacy_aspects', {}).get('aspects')}` vs modern `{item.get('modern_research_keys')}` / `{item.get('modern_aspects', {}).get('aspects')}`"
        )
        gap_count += 1
        if gap_count >= 80:
            break
    if gap_count == 0:
        lines.append("- None.")

    lines.extend(["", "## First Non-Identical Entries By Raw Category", ""])
    shown = 0
    for category in [
        "SCAN_FOUND_DIFF",
        "SCAN_KEY_SET_DIFF",
        "SCAN_KEY_ORDER_ONLY_DIFF",
        "SCAN_KEYS_IDENTICAL_ASPECT_DIFF",
        "LEGACY_ONLY",
        "MODERN_ONLY",
    ]:
        entries = report["diffs"][category]
        if not entries:
            continue
        lines.extend([f"### {category}", ""])
        for entry in entries[:25]:
            lines.append(f"- `{entry['comparison_key']}` -> `{entry['classification']}`")
            shown += 1
        lines.append("")
        if shown >= 175:
            break

    if report["legacy_duplicate_comparison_keys"] or report["modern_duplicate_comparison_keys"]:
        lines.extend(["", "## Duplicate Comparison Keys", ""])
        if report["legacy_duplicate_comparison_keys"]:
            lines.append(f"- Legacy duplicate keys: `{len(report['legacy_duplicate_comparison_keys'])}`")
        if report["modern_duplicate_comparison_keys"]:
            lines.append(f"- Modern duplicate keys: `{len(report['modern_duplicate_comparison_keys'])}`")

    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


if __name__ == "__main__":
    raise SystemExit(main())

#!/usr/bin/env python3
import argparse
import json
from collections import Counter
from pathlib import Path
from typing import Any


ROOT = Path(__file__).resolve().parents[1]
LEGACY_DUMP = ROOT / "dumps" / "thaumcraft_1_12_scan_items.json"
MODERN_DUMP = ROOT / "dumps" / "thaumcraft_1_21_scan_items.json"
ASPECT_POLICY_FILE = ROOT.parent / "aspect_parity" / "input" / "legacy_to_modern_stack_map.json"
SCAN_POLICY_FILE = ROOT / "input" / "scan_diff_policy.json"
DEFAULT_ASPECT_RESEARCH_KEYS = {
    "!aer",
    "!terra",
    "!ignis",
    "!aqua",
    "!ordo",
    "!perditio",
    "!vacuos",
    "!lux",
    "!motus",
    "!gelum",
    "!vitreus",
    "!metallum",
    "!victus",
    "!mortuus",
    "!potentia",
    "!permutatio",
    "!praecantatio",
    "!auram",
    "!alkimia",
    "!vitium",
    "!tenebrae",
    "!alienis",
    "!volatus",
    "!herba",
    "!instrumentum",
    "!fabrico",
    "!machina",
    "!vinculum",
    "!spiritus",
    "!cognitio",
    "!sensus",
    "!aversio",
    "!praemunio",
    "!desiderium",
    "!exanimis",
    "!bestia",
    "!humanus",
}


RAW_BUCKETS = [
    "SCAN_AND_ASPECTS_IDENTICAL",
    "SCAN_KEYS_IDENTICAL_ASPECT_DIFF",
    "SCAN_KEY_ORDER_ONLY_DIFF",
    "SCAN_KEY_SET_DIFF",
    "SCAN_FOUND_DIFF",
    "LEGACY_ONLY",
    "MODERN_ONLY",
]


def load_json(path: Path) -> dict[str, Any]:
    with path.open("r", encoding="utf-8") as handle:
        return json.load(handle)


def load_optional_json(path: Path) -> dict[str, Any]:
    if not path.exists():
        return {}
    return load_json(path)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Compare legacy and modern Thaumcraft scan runtime dumps.")
    parser.add_argument("--legacy", type=Path, default=LEGACY_DUMP, help="Path to the Forge 1.12.2 scan dump.")
    parser.add_argument("--modern", type=Path, default=MODERN_DUMP, help="Path to the NeoForge 1.21.1 scan dump.")
    parser.add_argument("--aspect-policy", type=Path, default=ASPECT_POLICY_FILE, help="Aspect parity stack mapping policy.")
    parser.add_argument("--scan-policy", type=Path, default=SCAN_POLICY_FILE, help="Scan-specific key alias policy.")
    parser.add_argument("--out-dir", type=Path, default=ROOT / "reports", help="Directory for scan_diff.json and scan_diff.md.")
    return parser.parse_args()


def comparison_key_for_entry(entry: dict[str, Any], side: str, aspect_policy: dict[str, Any]) -> tuple[str | None, str | None, str]:
    original_key = entry.get("comparison_key")
    stack_key = entry.get("stack_key")
    key_aliases = aspect_policy.get("key_aliases", {}) if side == "legacy" else {}
    stack_key_aliases = aspect_policy.get("stack_key_aliases", {}) if side == "legacy" else {}

    if original_key:
        return key_aliases.get(original_key, original_key), original_key, "key_alias" if original_key in key_aliases else "comparison_key"

    if stack_key and stack_key in stack_key_aliases:
        return stack_key_aliases[stack_key], None, "stack_key_alias"

    return None, original_key, "unmapped"


def index_entries(
    dump: dict[str, Any],
    side: str,
    aspect_policy: dict[str, Any],
) -> tuple[dict[str, dict[str, Any]], dict[str, list[dict[str, Any]]]]:
    out: dict[str, dict[str, Any]] = {}
    duplicates: dict[str, list[dict[str, Any]]] = {}
    for original in dump.get("entries", []):
        key, original_key, source = comparison_key_for_entry(original, side, aspect_policy)
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


def aspect_signature(entry: dict[str, Any]) -> tuple[str, tuple[tuple[str | None, int], ...]]:
    result = resolved_aspect_result(entry)
    aspects = tuple(sorted((aspect.get("id"), int(aspect.get("amount", 0))) for aspect in result.get("aspects", [])))
    return result.get("result_kind", "missing"), aspects


def resolved_aspect_result(entry: dict[str, Any]) -> dict[str, Any]:
    object_aspects = entry.get("object_aspects", {})
    if object_aspects.get("result_kind") == "aspects":
        return object_aspects
    generated_aspects = entry.get("generated_aspects", {})
    if generated_aspects.get("result_kind") == "aspects":
        return generated_aspects
    return object_aspects or generated_aspects or {"result_kind": "missing", "aspects": []}


def normalize_research_keys(
    entry: dict[str, Any],
    side: str,
    scan_policy: dict[str, Any],
    paired_modern_entry: dict[str, Any] | None = None,
) -> list[str]:
    aliases = scan_policy.get("research_key_aliases", {})
    keys = []
    for raw in entry.get("matched_research_keys", []):
        key = aliases.get(raw, raw)
        if side == "legacy" and paired_modern_entry is not None:
            key = normalize_legacy_generic_key(key, entry, paired_modern_entry)
        keys.append(key)
    return keys


def normalize_legacy_generic_key(key: str, legacy: dict[str, Any], modern: dict[str, Any]) -> str:
    item = legacy.get("item")
    if not item or not key.startswith("!" + item):
        return key

    modern_item = modern.get("item")
    if not modern_item:
        return key

    suffix = key[len("!" + item):]
    meta = str(legacy.get("meta", 0))
    if suffix == "" or suffix == meta:
        return "!" + modern_item
    return key


def aspect_research_keys(scan_policy: dict[str, Any]) -> set[str]:
    configured = scan_policy.get("aspect_research_keys")
    if isinstance(configured, list) and configured:
        return set(configured)
    return set(DEFAULT_ASPECT_RESEARCH_KEYS)


def non_aspect_research_keys(keys: list[str], scan_policy: dict[str, Any]) -> list[str]:
    aspect_keys = aspect_research_keys(scan_policy)
    return [key for key in keys if key not in aspect_keys]


def generic_object_key(entry: dict[str, Any], side: str, scan_policy: dict[str, Any], paired: dict[str, Any] | None) -> str | None:
    item = entry.get("item")
    if not item:
        return None

    key = "!" + item
    if side == "legacy" and paired is not None:
        key = normalize_legacy_generic_key(key, entry, paired)
    return scan_policy.get("research_key_aliases", {}).get(key, key)


def only_aspect_and_generic_keys(
    keys: list[str],
    entry: dict[str, Any] | None,
    side: str,
    scan_policy: dict[str, Any],
    paired: dict[str, Any] | None,
) -> bool:
    if entry is None:
        return not keys

    allowed = aspect_research_keys(scan_policy)
    generic = generic_object_key(entry, side, scan_policy, paired)
    if generic is not None:
        allowed.add(generic)
    return all(key in allowed for key in keys)


def is_aspect_parity_dependency(
    raw_category: str,
    legacy: dict[str, Any] | None,
    modern: dict[str, Any] | None,
    scan_policy: dict[str, Any],
) -> bool:
    if legacy is None or modern is None:
        return False
    if aspect_signature(legacy) == aspect_signature(modern):
        return False

    legacy_keys = normalize_research_keys(legacy, "legacy", scan_policy, modern)
    modern_keys = normalize_research_keys(modern, "modern", scan_policy)

    if raw_category == "SCAN_KEY_SET_DIFF":
        return non_aspect_research_keys(legacy_keys, scan_policy) == non_aspect_research_keys(modern_keys, scan_policy)

    if raw_category == "SCAN_FOUND_DIFF":
        return only_aspect_and_generic_keys(legacy_keys, legacy, "legacy", scan_policy, modern) \
                and only_aspect_and_generic_keys(modern_keys, modern, "modern", scan_policy, None)

    return False


def classify_raw(legacy: dict[str, Any], modern: dict[str, Any], scan_policy: dict[str, Any]) -> str:
    legacy_keys = normalize_research_keys(legacy, "legacy", scan_policy, modern)
    modern_keys = normalize_research_keys(modern, "modern", scan_policy)
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
    scan_policy: dict[str, Any],
) -> dict[str, str]:
    if raw_category == "SCAN_AND_ASPECTS_IDENTICAL":
        return {
            "classification": "PARITY_OK",
            "root_cause": "Scan result and resolved aspect list match after configured legacy-to-modern normalization.",
            "action": "No action.",
        }
    if raw_category == "MODERN_ONLY":
        return {
            "classification": "MODERN_ONLY_POLICY_REVIEW",
            "root_cause": "The key exists only in the 1.21.1 scan dump.",
            "action": "Document as new-version content or map if this is a renamed legacy stack.",
        }
    if raw_category == "LEGACY_ONLY":
        return {
            "classification": "LEGACY_ONLY_MAPPING_REVIEW",
            "root_cause": "The key exists only in the 1.12.2 scan dump.",
            "action": "Map renamed/flattened ids or mark removed/unported.",
        }
    if raw_category == "SCAN_KEYS_IDENTICAL_ASPECT_DIFF":
        return {
            "classification": "ASPECT_PARITY_DEPENDENCY",
            "root_cause": "Scan keys match, but the resolved aspect list differs.",
            "action": "Use the aspect parity harness to decide whether this is expected or a real aspect assignment gap.",
        }
    if is_aspect_parity_dependency(raw_category, legacy, modern, scan_policy):
        return {
            "classification": "ASPECT_PARITY_DEPENDENCY",
            "root_cause": "Scan key difference is explained by different resolved aspect keys and/or the generic object key appearing only because one side has aspects.",
            "action": "Fix the underlying aspect assignment/generation difference first, then rerun the scan comparer.",
        }
    return {
        "classification": "PORT_GAP_SCAN_LOGIC",
        "root_cause": "Comparable legacy-equivalent stack differs in scan matching or scan research keys.",
        "action": "Trace predicate registration, generic key generation, potion/enchantment key mapping, and explicit scannable data.",
    }


def classified_entry(
    raw_category: str,
    key: str,
    legacy: dict[str, Any] | None,
    modern: dict[str, Any] | None,
    scan_policy: dict[str, Any],
) -> dict[str, Any]:
    policy = classify_policy(raw_category, key, legacy, modern, scan_policy)
    out: dict[str, Any] = {"comparison_key": key, "raw_category": raw_category}
    out.update(policy)
    if legacy is not None:
        out["legacy_comparison_key"] = legacy.get("_comparison_key_original")
        out["legacy_comparison_source"] = legacy.get("_comparison_source")
        out["legacy_stack"] = legacy.get("stack_key")
        out["legacy_scan_found"] = legacy.get("scan_found")
        out["legacy_research_keys"] = normalize_research_keys(legacy, "legacy", scan_policy, modern)
        out["legacy_aspects"] = resolved_aspect_result(legacy)
    if modern is not None:
        out["modern_comparison_key"] = modern.get("_comparison_key_original")
        out["modern_comparison_source"] = modern.get("_comparison_source")
        out["modern_stack"] = modern.get("stack_key")
        out["modern_scan_found"] = modern.get("scan_found")
        out["modern_research_keys"] = normalize_research_keys(modern, "modern", scan_policy)
        out["modern_aspects"] = resolved_aspect_result(modern)
    return out


def main() -> int:
    args = parse_args()
    aspect_policy = load_optional_json(args.aspect_policy)
    scan_policy = load_optional_json(args.scan_policy)
    legacy_dump = load_json(args.legacy)
    modern_dump = load_json(args.modern)
    legacy_entries, legacy_duplicates = index_entries(legacy_dump, "legacy", aspect_policy)
    modern_entries, modern_duplicates = index_entries(modern_dump, "modern", aspect_policy)

    legacy_keys = set(legacy_entries)
    modern_keys = set(modern_entries)
    comparable_keys = sorted(legacy_keys & modern_keys)
    buckets: dict[str, list[dict[str, Any]]] = {name: [] for name in RAW_BUCKETS}
    classified: list[dict[str, Any]] = []

    for key in comparable_keys:
        legacy = legacy_entries[key]
        modern = modern_entries[key]
        raw_category = classify_raw(legacy, modern, scan_policy)
        item = classified_entry(raw_category, key, legacy, modern, scan_policy)
        buckets[raw_category].append(item)
        classified.append(item)

    for key in sorted(legacy_keys - modern_keys):
        legacy = legacy_entries[key]
        item = classified_entry("LEGACY_ONLY", key, legacy, None, scan_policy)
        buckets["LEGACY_ONLY"].append(item)
        classified.append(item)

    for key in sorted(modern_keys - legacy_keys):
        modern = modern_entries[key]
        item = classified_entry("MODERN_ONLY", key, None, modern, scan_policy)
        buckets["MODERN_ONLY"].append(item)
        classified.append(item)

    classification_summary = Counter(item["classification"] for item in classified)
    report = {
        "legacy_dump": str(args.legacy),
        "modern_dump": str(args.modern),
        "aspect_policy_file": str(args.aspect_policy) if args.aspect_policy.exists() else None,
        "scan_policy_file": str(args.scan_policy) if args.scan_policy.exists() else None,
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
    json_path = args.out_dir / "scan_diff.json"
    md_path = args.out_dir / "scan_diff.md"
    json_path.write_text(json.dumps(report, indent=2, ensure_ascii=False), encoding="utf-8")
    write_markdown(report, md_path)
    return 0


def write_markdown(report: dict[str, Any], path: Path) -> None:
    lines = [
        "# Thaumcraft Scan Dump Diff",
        "",
        f"- Legacy entries: `{report['legacy_entry_count']}`",
        f"- Modern entries: `{report['modern_entry_count']}`",
        f"- Legacy predicates: `{report['legacy_predicate_count']}`",
        f"- Modern predicates: `{report['modern_predicate_count']}`",
        f"- Legacy comparison keys: `{report['legacy_comparison_key_count']}`",
        f"- Modern comparison keys: `{report['modern_comparison_key_count']}`",
        f"- Comparable keys: `{report['comparable_key_count']}`",
        f"- Aspect mapping policy: `{report['aspect_policy_file']}`",
        f"- Scan key policy: `{report['scan_policy_file']}`",
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

    lines.extend(["", "## First Actionable Scan Gaps", ""])
    gap_count = 0
    for item in report["classified_diffs"]:
        classification = item["classification"]
        if not classification.startswith("PORT_GAP"):
            continue
        lines.append(
            f"- `{item['comparison_key']}`: `{classification}` - legacy `{item.get('legacy_research_keys')}` vs modern `{item.get('modern_research_keys')}`"
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

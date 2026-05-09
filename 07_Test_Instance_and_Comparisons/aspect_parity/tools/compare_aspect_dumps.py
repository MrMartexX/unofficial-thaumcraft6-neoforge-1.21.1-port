#!/usr/bin/env python3
import argparse
import json
from collections import Counter
from pathlib import Path
from typing import Any


ROOT = Path(__file__).resolve().parents[1]
LEGACY_DUMP = ROOT / "dumps" / "thaumcraft_1_12_aspects.json"
MODERN_DUMP = ROOT / "dumps" / "thaumcraft_1_21_aspects.json"
POLICY_FILE = ROOT / "input" / "legacy_to_modern_stack_map.json"
JSON_REPORT = ROOT / "reports" / "aspect_diff.json"
MD_REPORT = ROOT / "reports" / "aspect_diff.md"


RAW_BUCKETS = [
    "IDENTICAL",
    "ORDER_ONLY_DIFF",
    "AMOUNT_DIFF",
    "ASPECT_SET_DIFF",
    "NULL_EMPTY_DIFF",
    "RESULT_KIND_DIFF",
    "LEGACY_ONLY",
    "MODERN_ONLY",
]


def load_json(path: Path) -> dict[str, Any]:
    with path.open("r", encoding="utf-8") as handle:
        return json.load(handle)


def load_policy(path: Path) -> dict[str, Any]:
    if not path.exists():
        return {"schema": 0, "rules": []}
    return load_json(path)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Compare legacy and modern Thaumcraft aspect runtime dumps.")
    parser.add_argument("--legacy", type=Path, default=LEGACY_DUMP, help="Path to the Forge 1.12.2 aspect dump.")
    parser.add_argument("--modern", type=Path, default=MODERN_DUMP, help="Path to the NeoForge 1.21.1 aspect dump.")
    parser.add_argument("--policy", type=Path, default=POLICY_FILE, help="Path to the legacy-to-modern mapping/policy JSON.")
    parser.add_argument("--out-dir", type=Path, default=ROOT / "reports", help="Directory for aspect_diff.json and aspect_diff.md.")
    return parser.parse_args()


def comparison_key_for_entry(
    entry: dict[str, Any],
    side: str,
    policy: dict[str, Any],
) -> tuple[str | None, str | None, str]:
    original_key = entry.get("comparison_key")
    stack_key = entry.get("stack_key")
    key_aliases = policy.get("key_aliases", {}) if side == "legacy" else {}
    stack_key_aliases = policy.get("stack_key_aliases", {}) if side == "legacy" else {}

    if original_key:
        return key_aliases.get(original_key, original_key), original_key, "key_alias" if original_key in key_aliases else "comparison_key"

    if stack_key and stack_key in stack_key_aliases:
        return stack_key_aliases[stack_key], None, "stack_key_alias"

    return None, original_key, "unmapped"


def index_entries(
    dump: dict[str, Any],
    side: str,
    policy: dict[str, Any],
) -> tuple[dict[str, dict[str, Any]], dict[str, list[dict[str, Any]]], list[dict[str, Any]]]:
    out: dict[str, dict[str, Any]] = {}
    duplicates: dict[str, list[dict[str, Any]]] = {}
    mapped_entries: list[dict[str, Any]] = []
    for entry in dump.get("entries", []):
        key, original_key, source = comparison_key_for_entry(entry, side, policy)
        if not key:
            continue
        entry = dict(entry)
        entry["_comparison_key_normalized"] = key
        entry["_comparison_key_original"] = original_key
        entry["_comparison_source"] = source
        if source in {"key_alias", "stack_key_alias"}:
            mapped_entries.append({
                "normalized_key": key,
                "original_comparison_key": original_key,
                "stack_key": entry.get("stack_key"),
                "source": source,
            })
        if key in out:
            duplicates.setdefault(key, [out[key]]).append(entry)
            continue
        out[key] = entry
    return out, duplicates, mapped_entries


def aspect_signature(entry: dict[str, Any]) -> tuple[str, tuple[tuple[str | None, int], ...]]:
    result = entry.get("object_aspects", {})
    aspects = tuple((aspect.get("id"), int(aspect.get("amount", 0))) for aspect in result.get("aspects", []))
    return result.get("result_kind", "missing"), aspects


def classify_raw(legacy: dict[str, Any], modern: dict[str, Any]) -> str:
    legacy_kind, legacy_aspects = aspect_signature(legacy)
    modern_kind, modern_aspects = aspect_signature(modern)
    if legacy_kind == modern_kind and legacy_aspects == modern_aspects:
        return "IDENTICAL"

    if legacy_kind != modern_kind:
        if {legacy_kind, modern_kind} == {"null", "empty"}:
            return "NULL_EMPTY_DIFF"
        return "RESULT_KIND_DIFF"

    if sorted(legacy_aspects) == sorted(modern_aspects):
        return "ORDER_ONLY_DIFF"

    legacy_ids = {item[0] for item in legacy_aspects}
    modern_ids = {item[0] for item in modern_aspects}
    if legacy_ids == modern_ids:
        return "AMOUNT_DIFF"

    return "ASPECT_SET_DIFF"


def list_value(value: Any) -> list[Any]:
    if value is None:
        return []
    if isinstance(value, list):
        return value
    return [value]


def matches_rule(rule: dict[str, Any], raw_category: str, key: str, entry: dict[str, Any]) -> bool:
    match = rule.get("match", {})

    categories = set(list_value(match.get("categories")))
    if categories and raw_category not in categories:
        return False

    exact = set(list_value(match.get("key_exact")))
    if exact and key not in exact:
        return False

    prefixes = list_value(match.get("key_prefixes"))
    if prefixes and not any(key.startswith(prefix) for prefix in prefixes):
        return False

    contains_any = list_value(match.get("key_contains_any"))
    if contains_any and not any(fragment in key for fragment in contains_any):
        return False

    suffixes = list_value(match.get("key_suffixes"))
    if suffixes and not any(key.endswith(suffix) for suffix in suffixes):
        return False

    legacy_stack_contains = list_value(match.get("legacy_stack_contains_any"))
    if legacy_stack_contains:
        legacy_stack = str(entry.get("legacy_stack", ""))
        if not any(fragment in legacy_stack for fragment in legacy_stack_contains):
            return False

    modern_stack_contains = list_value(match.get("modern_stack_contains_any"))
    if modern_stack_contains:
        modern_stack = str(entry.get("modern_stack", ""))
        if not any(fragment in modern_stack for fragment in modern_stack_contains):
            return False

    return True


def classify_policy(raw_category: str, key: str, entry: dict[str, Any], policy: dict[str, Any]) -> dict[str, str]:
    if raw_category == "IDENTICAL":
        if entry.get("legacy_comparison_source") in {"key_alias", "stack_key_alias"}:
            return {
                "classification": "PARITY_OK_LEGACY_TO_MODERN_MAP",
                "root_cause": "Resolved runtime AspectList matches after an explicit legacy-to-modern id or metadata mapping.",
                "action": "No gameplay fix. Keep the mapping documented so this does not count as a port gap.",
            }
        return {
            "classification": "PARITY_OK",
            "root_cause": "Resolved runtime AspectList matches exactly.",
            "action": "No action.",
        }

    for rule in policy.get("rules", []):
        if matches_rule(rule, raw_category, key, entry):
            return {
                "classification": rule.get("classification", "UNCLASSIFIED"),
                "root_cause": rule.get("root_cause", ""),
                "action": rule.get("action", ""),
            }

    if raw_category == "MODERN_ONLY":
        return {
            "classification": "MODERN_ONLY_POLICY_REVIEW",
            "root_cause": "The key exists only in the 1.21.1 dump.",
            "action": "Document policy or add a legacy mapping if this is actually a renamed legacy stack.",
        }
    if raw_category == "LEGACY_ONLY":
        return {
            "classification": "LEGACY_ONLY_MAPPING_REVIEW",
            "root_cause": "The key exists only in the 1.12.2 dump.",
            "action": "Map renamed/flattened ids or mark removed/unported.",
        }

    return {
        "classification": "PORT_GAP_UNCLASSIFIED",
        "root_cause": "Comparable legacy-equivalent stack differs and no specific classification rule matched.",
        "action": "Trace direct assignment, generated recipe lookup, and runtime bonuses.",
    }


def classified_entry(raw_category: str, key: str, entry: dict[str, Any], policy: dict[str, Any]) -> dict[str, Any]:
    policy_result = classify_policy(raw_category, key, entry, policy)
    out = {"comparison_key": key, "raw_category": raw_category}
    out.update(policy_result)
    out.update(entry)
    return out


def main() -> int:
    args = parse_args()
    legacy_dump_path = args.legacy
    modern_dump_path = args.modern
    policy_path = args.policy
    json_report_path = args.out_dir / "aspect_diff.json"
    md_report_path = args.out_dir / "aspect_diff.md"

    policy = load_policy(policy_path)
    legacy_dump = load_json(legacy_dump_path)
    modern_dump = load_json(modern_dump_path)
    legacy_entries, legacy_duplicates, legacy_mapped_entries = index_entries(legacy_dump, "legacy", policy)
    modern_entries, modern_duplicates, modern_mapped_entries = index_entries(modern_dump, "modern", policy)

    legacy_keys = set(legacy_entries)
    modern_keys = set(modern_entries)
    comparable_keys = sorted(legacy_keys & modern_keys)

    buckets: dict[str, list[dict[str, Any]]] = {name: [] for name in RAW_BUCKETS}
    classified: list[dict[str, Any]] = []

    for key in comparable_keys:
        legacy = legacy_entries[key]
        modern = modern_entries[key]
        raw_category = classify_raw(legacy, modern)
        entry = {
            "legacy_comparison_key": legacy.get("_comparison_key_original"),
            "modern_comparison_key": modern.get("_comparison_key_original"),
            "legacy_comparison_source": legacy.get("_comparison_source"),
            "modern_comparison_source": modern.get("_comparison_source"),
            "legacy_stack": legacy.get("stack_key"),
            "modern_stack": modern.get("stack_key"),
            "legacy_object_aspects": legacy.get("object_aspects"),
            "modern_object_aspects": modern.get("object_aspects"),
        }
        item = classified_entry(raw_category, key, entry, policy)
        buckets[raw_category].append(item)
        classified.append(item)

    for key in sorted(legacy_keys - modern_keys):
        legacy = legacy_entries[key]
        entry = {
            "legacy_comparison_key": legacy.get("_comparison_key_original"),
            "legacy_comparison_source": legacy.get("_comparison_source"),
            "legacy_stack": legacy.get("stack_key"),
            "legacy_object_aspects": legacy.get("object_aspects"),
        }
        item = classified_entry("LEGACY_ONLY", key, entry, policy)
        buckets["LEGACY_ONLY"].append(item)
        classified.append(item)

    for key in sorted(modern_keys - legacy_keys):
        modern = modern_entries[key]
        entry = {
            "modern_comparison_key": modern.get("_comparison_key_original"),
            "modern_comparison_source": modern.get("_comparison_source"),
            "modern_stack": modern.get("stack_key"),
            "modern_object_aspects": modern.get("object_aspects"),
        }
        item = classified_entry("MODERN_ONLY", key, entry, policy)
        buckets["MODERN_ONLY"].append(item)
        classified.append(item)

    classification_summary = Counter(item["classification"] for item in classified)
    root_cause_summary = Counter(
        (item["classification"], item.get("root_cause", "")) for item in classified if item["classification"] != "PARITY_OK"
    )

    report = {
        "legacy_dump": str(legacy_dump_path),
        "modern_dump": str(modern_dump_path),
        "policy_file": str(policy_path) if policy_path.exists() else None,
        "legacy_entry_count": legacy_dump.get("entry_count"),
        "modern_entry_count": modern_dump.get("entry_count"),
        "legacy_comparison_key_count": len(legacy_entries),
        "modern_comparison_key_count": len(modern_entries),
        "comparable_key_count": len(comparable_keys),
        "legacy_to_modern_key_alias_count": len(policy.get("key_aliases", {})),
        "legacy_to_modern_stack_key_alias_count": len(policy.get("stack_key_aliases", {})),
        "legacy_mapped_entry_count": len(legacy_mapped_entries),
        "legacy_mapped_entries": legacy_mapped_entries,
        "legacy_duplicate_comparison_keys": {key: len(values) for key, values in legacy_duplicates.items()},
        "modern_duplicate_comparison_keys": {key: len(values) for key, values in modern_duplicates.items()},
        "summary": {name: len(items) for name, items in buckets.items()},
        "classification_summary": dict(sorted(classification_summary.items())),
        "root_cause_summary": [
            {"classification": classification, "root_cause": root_cause, "count": count}
            for (classification, root_cause), count in root_cause_summary.most_common()
        ],
        "diffs": buckets,
        "classified_diffs": classified,
    }

    json_report_path.parent.mkdir(parents=True, exist_ok=True)
    json_report_path.write_text(json.dumps(report, indent=2, ensure_ascii=False), encoding="utf-8")
    write_markdown(report, md_report_path)
    return 0


def write_markdown(report: dict[str, Any], path: Path) -> None:
    lines = [
        "# Thaumcraft Aspect Dump Diff",
        "",
        f"- Legacy entries: `{report['legacy_entry_count']}`",
        f"- Modern entries: `{report['modern_entry_count']}`",
        f"- Legacy comparison keys: `{report['legacy_comparison_key_count']}`",
        f"- Modern comparison keys: `{report['modern_comparison_key_count']}`",
        f"- Comparable keys: `{report['comparable_key_count']}`",
        f"- Policy file: `{report['policy_file']}`",
        f"- Legacy key aliases configured: `{report['legacy_to_modern_key_alias_count']}`",
        f"- Legacy stack-key aliases configured: `{report['legacy_to_modern_stack_key_alias_count']}`",
        f"- Legacy entries mapped through aliases: `{report['legacy_mapped_entry_count']}`",
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

    lines.extend([
        "",
        "## Root Cause Buckets",
        "",
        "| Classification | Count | Root cause |",
        "|---|---:|---|",
    ])
    for item in report["root_cause_summary"][:30]:
        lines.append(f"| `{item['classification']}` | {item['count']} | {item['root_cause']} |")

    lines.extend(["", "## First Actionable Port Gaps", ""])
    gap_count = 0
    for item in report["classified_diffs"]:
        classification = item["classification"]
        if not classification.startswith("PORT_GAP"):
            continue
        lines.append(f"- `{item['comparison_key']}`: `{classification}` - {item.get('root_cause', '')}")
        gap_count += 1
        if gap_count >= 60:
            break
    if gap_count == 0:
        lines.append("- None.")

    lines.extend(["", "## First Non-Identical Entries By Raw Category", ""])
    shown = 0
    for category in [
        "NULL_EMPTY_DIFF",
        "RESULT_KIND_DIFF",
        "ASPECT_SET_DIFF",
        "AMOUNT_DIFF",
        "ORDER_ONLY_DIFF",
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

    legacy_duplicates = report["legacy_duplicate_comparison_keys"]
    modern_duplicates = report["modern_duplicate_comparison_keys"]
    if legacy_duplicates or modern_duplicates:
        lines.extend(["", "## Duplicate Comparison Keys", ""])
        if legacy_duplicates:
            lines.append(f"- Legacy duplicate keys: `{len(legacy_duplicates)}`")
        if modern_duplicates:
            lines.append(f"- Modern duplicate keys: `{len(modern_duplicates)}`")

    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


if __name__ == "__main__":
    raise SystemExit(main())

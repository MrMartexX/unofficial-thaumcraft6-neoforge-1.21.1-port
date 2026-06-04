#!/usr/bin/env python3
"""Compare authoritative TC6 research JSON with NeoForge source and runtime data."""

from __future__ import annotations

import argparse
import json
from pathlib import Path
from typing import Any


LIST_FIELDS = (
    "icons",
    "parents",
    "siblings",
    "meta",
    "reward_item",
    "reward_knowledge",
)
STAGE_LIST_FIELDS = (
    "required_research",
    "required_craft",
    "required_item",
    "required_knowledge",
    "recipes",
)


def normalize_stage(stage: dict[str, Any]) -> dict[str, Any]:
    normalized = {"text": str(stage.get("text", "")).strip()}
    for field in STAGE_LIST_FIELDS:
        normalized[field] = [str(value) for value in stage.get(field, [])]
    normalized["warp"] = max(0, int(stage.get("warp", 0)))
    return normalized


def normalize_entry(entry: dict[str, Any]) -> dict[str, Any]:
    location = entry.get("location", [0, 0])
    if len(location) != 2:
        raise ValueError(f"Research {entry.get('key')} has invalid location {location}")
    normalized: dict[str, Any] = {
        "key": str(entry["key"]).strip(),
        "name": str(entry.get("name", "")).strip(),
        "category": str(entry.get("category", "BASICS")).strip().upper(),
        "location": [int(location[0]), int(location[1])],
    }
    for field in LIST_FIELDS:
        normalized[field] = [str(value) for value in entry.get(field, [])]
    normalized["stages"] = [normalize_stage(stage) for stage in entry.get("stages", [])]
    normalized["addenda"] = [normalize_stage(stage) for stage in entry.get("addenda", [])]
    return normalized


def load_source_directory(directory: Path) -> tuple[dict[str, dict[str, Any]], list[str]]:
    entries: dict[str, dict[str, Any]] = {}
    files: list[str] = []
    for path in sorted(directory.glob("*.json")):
        if path.name.startswith("_"):
            continue
        files.append(path.name)
        data = json.loads(path.read_text(encoding="utf-8"))
        for raw_entry in data.get("entries", []):
            entry = normalize_entry(raw_entry)
            key = entry["key"]
            if key in entries:
                raise ValueError(f"Duplicate research key {key} in {path}")
            entries[key] = entry
    return entries, files


def load_runtime_export(path: Path) -> tuple[dict[str, dict[str, Any]], dict[str, Any]]:
    data = json.loads(path.read_text(encoding="utf-8"))
    entries = {}
    for raw_entry in data.get("entries", []):
        entry = normalize_entry(raw_entry)
        entries[entry["key"]] = entry
    return entries, data


def category_map(categories: list[dict[str, Any]]) -> dict[str, dict[str, Any]]:
    return {str(category["key"]): category for category in categories}


def diff_entries(
    expected: dict[str, dict[str, Any]],
    actual: dict[str, dict[str, Any]],
) -> dict[str, Any]:
    expected_keys = set(expected)
    actual_keys = set(actual)
    changed: dict[str, dict[str, Any]] = {}
    for key in sorted(expected_keys & actual_keys):
        if expected[key] == actual[key]:
            continue
        fields = {}
        for field in expected[key]:
            if expected[key].get(field) != actual[key].get(field):
                fields[field] = {
                    "expected": expected[key].get(field),
                    "actual": actual[key].get(field),
                }
        changed[key] = fields
    return {
        "missing": sorted(expected_keys - actual_keys),
        "extra": sorted(actual_keys - expected_keys),
        "changed": changed,
    }


def diff_count(diff: dict[str, Any]) -> int:
    return len(diff["missing"]) + len(diff["extra"]) + len(diff["changed"])


def write_markdown(
    output: Path,
    legacy_files: list[str],
    modern_files: list[str],
    legacy_entries: dict[str, dict[str, Any]],
    source_diff: dict[str, Any],
    runtime_diff: dict[str, Any],
    category_diff: dict[str, Any],
    runtime_data: dict[str, Any],
) -> None:
    checks = runtime_data.get("progression_checks", [])
    failed_checks = [check for check in checks if not check.get("passed", False)]
    lines = [
        "# Research data parity comparison",
        "",
        "The legacy `assets/thaumcraft/research` JSON is the authoritative TC6 data source. "
        "The comparison separately verifies the NeoForge resource copy and the data produced by the NeoForge runtime parser.",
        "",
        "| Check | Result |",
        "|---|---:|",
        f"| Legacy JSON files | `{len(legacy_files)}` |",
        f"| NeoForge JSON files | `{len(modern_files)}` |",
        f"| Legacy entries | `{len(legacy_entries)}` |",
        f"| Source resource differences | `{diff_count(source_diff)}` |",
        f"| Runtime parser differences | `{diff_count(runtime_diff)}` |",
        f"| Legacy category differences | `{diff_count(category_diff)}` |",
        f"| Progression semantic checks | `{len(checks) - len(failed_checks)}/{len(checks)}` passed |",
        "",
        "## Source resource parity",
        "",
    ]
    append_diff(lines, source_diff)
    lines.extend(["", "## Runtime parser parity", ""])
    append_diff(lines, runtime_diff)
    lines.extend(["", "## Java-registered category parity", ""])
    append_diff(lines, category_diff)
    lines.extend(["", "## Progression semantic checks", ""])
    if not checks:
        lines.append("No runtime progression checks were exported.")
    else:
        lines.extend(["| Check | Result | Actual | Expected |", "|---|---|---|---|"])
        for check in checks:
            status = "PASS" if check.get("passed", False) else "FAIL"
            lines.append(
                f"| `{check.get('name', '')}` | `{status}` | `{check.get('actual', '')}` | `{check.get('expected', '')}` |"
            )
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text("\n".join(lines) + "\n", encoding="utf-8")


def append_diff(lines: list[str], diff: dict[str, Any]) -> None:
    if diff_count(diff) == 0:
        lines.append("Exact normalized match.")
        return
    if diff["missing"]:
        lines.append(f"- Missing entries: `{', '.join(diff['missing'])}`")
    if diff["extra"]:
        lines.append(f"- Extra entries: `{', '.join(diff['extra'])}`")
    for key, fields in diff["changed"].items():
        lines.append(f"- Changed `{key}` fields: `{', '.join(fields)}`")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--legacy", type=Path, required=True)
    parser.add_argument("--modern", type=Path, required=True)
    parser.add_argument("--runtime", type=Path, required=True)
    parser.add_argument("--legacy-categories", type=Path, required=True)
    parser.add_argument("--report", type=Path, required=True)
    parser.add_argument("--diff", type=Path, required=True)
    args = parser.parse_args()

    legacy_entries, legacy_files = load_source_directory(args.legacy)
    modern_entries, modern_files = load_source_directory(args.modern)
    runtime_entries, runtime_data = load_runtime_export(args.runtime)
    legacy_categories = category_map(json.loads(args.legacy_categories.read_text(encoding="utf-8")))
    runtime_categories = category_map(runtime_data.get("categories", []))
    source_diff = diff_entries(legacy_entries, modern_entries)
    runtime_diff = diff_entries(legacy_entries, runtime_entries)
    category_diff = diff_entries(legacy_categories, runtime_categories)
    failed_checks = [
        check for check in runtime_data.get("progression_checks", [])
        if not check.get("passed", False)
    ]

    result = {
        "legacy_files": legacy_files,
        "modern_files": modern_files,
        "legacy_entry_count": len(legacy_entries),
        "modern_entry_count": len(modern_entries),
        "runtime_entry_count": len(runtime_entries),
        "source_diff": source_diff,
        "runtime_diff": runtime_diff,
        "category_diff": category_diff,
        "failed_progression_checks": failed_checks,
    }
    args.diff.parent.mkdir(parents=True, exist_ok=True)
    args.diff.write_text(json.dumps(result, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    write_markdown(
        args.report,
        legacy_files,
        modern_files,
        legacy_entries,
        source_diff,
        runtime_diff,
        category_diff,
        runtime_data,
    )

    failures = diff_count(source_diff) + diff_count(runtime_diff) + diff_count(category_diff) + len(failed_checks)
    print(
        f"research parity: legacy={len(legacy_entries)} modern={len(modern_entries)} "
        f"runtime={len(runtime_entries)} source_diff={diff_count(source_diff)} "
        f"runtime_diff={diff_count(runtime_diff)} category_diff={diff_count(category_diff)} "
        f"progression_failures={len(failed_checks)}"
    )
    return 1 if failures else 0


if __name__ == "__main__":
    raise SystemExit(main())

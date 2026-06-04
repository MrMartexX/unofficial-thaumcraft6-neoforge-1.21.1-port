import collections
import json
from pathlib import Path


ROOT = Path(__file__).resolve().parents[3]
CATALOG_ROOT = ROOT / "07_Test_Instance_and_Comparisons" / "research_recipe_catalog"
LEGACY_DUMP = CATALOG_ROOT / "thaumcraft_1_12_research_recipe_catalog.json"
RUNTIME_CATALOG = (
    ROOT
    / "05_neoforge_port"
    / "src"
    / "main"
    / "resources"
    / "data"
    / "thaumcraft"
    / "research_page_catalog"
    / "legacy_builtin.json"
)
REPORT_JSON = CATALOG_ROOT / "research_recipe_catalog_diff.json"
REPORT_MD = CATALOG_ROOT / "research_recipe_catalog_parity.md"
RESEARCH_ROOT = (
    ROOT
    / "05_neoforge_port"
    / "src"
    / "main"
    / "resources"
    / "data"
    / "thaumcraft"
    / "research"
)


def canonical_id(raw):
    raw = raw.strip().lower()
    return raw if ":" in raw else f"thaumcraft:{raw}"


def runtime_entry(source, direct_reference, occurrence_count, targets=None):
    return {
        "id": canonical_id(source["id"]),
        "legacy_source": source["source"],
        "kind": source["kind"],
        "legacy_class": source["class"],
        "required_research": source["research"],
        "legacy_group": source["group"],
        "targets": sorted(canonical_id(value) for value in (targets or [])),
        "legacy_output": source["output"],
        "direct_reference": direct_reference,
        "occurrence_count": occurrence_count,
    }


def merge_entry(entries, entry):
    previous = entries.get(entry["id"])
    if previous is None:
        entries[entry["id"]] = entry
        return

    comparable_fields = (
        "legacy_source",
        "kind",
        "legacy_class",
        "required_research",
        "legacy_group",
        "targets",
        "legacy_output",
    )
    mismatches = [
        field for field in comparable_fields if previous[field] != entry[field]
    ]
    if mismatches:
        raise ValueError(
            f"Conflicting legacy catalog definitions for {entry['id']}: {mismatches}"
        )
    previous["direct_reference"] = previous["direct_reference"] or entry["direct_reference"]
    previous["occurrence_count"] += entry["occurrence_count"]


def build_runtime_catalog(legacy):
    entries = {}
    for reference in legacy["references"]:
        merge_entry(
            entries,
            runtime_entry(
                reference,
                True,
                len(reference["occurrences"]),
                reference["group_members"],
            ),
        )
        for member in reference.get("group_member_resolutions", []):
            merge_entry(entries, runtime_entry(member, False, 0))

    return {
        "schema": 1,
        "legacy_environment": legacy["environment"],
        "entries": [entries[key] for key in sorted(entries)],
    }


def collect_research_occurrences():
    occurrences = set()
    for path in sorted(RESEARCH_ROOT.glob("*.json")):
        data = json.loads(path.read_text(encoding="utf-8"))
        for entry in data["entries"]:
            for section, legacy_section in (("stages", "stage"), ("addenda", "addendum")):
                for section_index, stage in enumerate(entry.get(section, [])):
                    for recipe_index, reference in enumerate(stage.get("recipes", [])):
                        occurrences.add(
                            (
                                canonical_id(reference),
                                entry["key"],
                                legacy_section,
                                section_index,
                                recipe_index,
                            )
                        )
    return occurrences


def collect_legacy_occurrences(legacy):
    return {
        (
            canonical_id(reference["id"]),
            occurrence["entry"],
            occurrence["section"],
            occurrence["section_index"],
            occurrence["recipe_index"],
        )
        for reference in legacy["references"]
        for occurrence in reference["occurrences"]
    }


def compare(legacy, runtime):
    direct_entries = {
        entry["id"]: entry for entry in runtime["entries"] if entry["direct_reference"]
    }
    legacy_entries = {canonical_id(entry["id"]): entry for entry in legacy["references"]}
    field_mismatches = []

    for entry_id in sorted(set(legacy_entries) & set(direct_entries)):
        legacy_entry = legacy_entries[entry_id]
        runtime_entry_value = direct_entries[entry_id]
        expected = runtime_entry(
            legacy_entry,
            True,
            len(legacy_entry["occurrences"]),
            legacy_entry["group_members"],
        )
        for field in (
            "legacy_source",
            "kind",
            "legacy_class",
            "required_research",
            "legacy_group",
            "targets",
            "legacy_output",
            "occurrence_count",
        ):
            if expected[field] != runtime_entry_value[field]:
                field_mismatches.append(
                    {
                        "id": entry_id,
                        "field": field,
                        "legacy": expected[field],
                        "runtime": runtime_entry_value[field],
                    }
                )

    legacy_occurrences = collect_legacy_occurrences(legacy)
    research_occurrences = collect_research_occurrences()
    result = {
        "schema": 1,
        "legacy_occurrence_count": len(legacy_occurrences),
        "research_occurrence_count": len(research_occurrences),
        "legacy_direct_reference_count": len(legacy_entries),
        "runtime_direct_reference_count": len(direct_entries),
        "runtime_total_entry_count": len(runtime["entries"]),
        "legacy_only_direct_references": sorted(set(legacy_entries) - set(direct_entries)),
        "runtime_only_direct_references": sorted(set(direct_entries) - set(legacy_entries)),
        "legacy_only_occurrences": sorted(legacy_occurrences - research_occurrences),
        "research_only_occurrences": sorted(research_occurrences - legacy_occurrences),
        "field_mismatches": field_mismatches,
        "source_counts": dict(
            sorted(
                collections.Counter(
                    entry["legacy_source"] for entry in direct_entries.values()
                ).items()
            )
        ),
        "kind_counts": dict(
            sorted(
                collections.Counter(
                    entry["kind"] for entry in direct_entries.values()
                ).items()
            )
        ),
    }
    result["parity_ok"] = not any(
        (
            result["legacy_only_direct_references"],
            result["runtime_only_direct_references"],
            result["legacy_only_occurrences"],
            result["research_only_occurrences"],
            result["field_mismatches"],
        )
    )
    return result


def write_markdown(result):
    lines = [
        "# Research Recipe/Page Catalog Parity",
        "",
        "| Check | Result |",
        "|---|---:|",
        f"| Parity | `{'OK' if result['parity_ok'] else 'FAILED'}` |",
        f"| Legacy research occurrences | `{result['legacy_occurrence_count']}` |",
        f"| NeoForge research occurrences | `{result['research_occurrence_count']}` |",
        f"| Legacy direct references | `{result['legacy_direct_reference_count']}` |",
        f"| Runtime direct references | `{result['runtime_direct_reference_count']}` |",
        f"| Runtime entries including group members | `{result['runtime_total_entry_count']}` |",
        f"| Field mismatches | `{len(result['field_mismatches'])}` |",
        "",
        "## Legacy direct-reference sources",
        "",
        "| Source | Count |",
        "|---|---:|",
    ]
    for key, count in result["source_counts"].items():
        lines.append(f"| `{key}` | `{count}` |")
    lines.extend(["", "## Legacy direct-reference kinds", "", "| Kind | Count |", "|---|---:|"])
    for key, count in result["kind_counts"].items():
        lines.append(f"| `{key}` | `{count}` |")

    if not result["parity_ok"]:
        lines.extend(
            [
                "",
                "## Differences",
                "",
                f"- Legacy-only direct references: `{len(result['legacy_only_direct_references'])}`",
                f"- Runtime-only direct references: `{len(result['runtime_only_direct_references'])}`",
                f"- Legacy-only occurrences: `{len(result['legacy_only_occurrences'])}`",
                f"- Research-only occurrences: `{len(result['research_only_occurrences'])}`",
                f"- Field mismatches: `{len(result['field_mismatches'])}`",
            ]
        )

    REPORT_MD.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main():
    legacy = json.loads(LEGACY_DUMP.read_text(encoding="utf-8"))
    runtime = build_runtime_catalog(legacy)
    RUNTIME_CATALOG.parent.mkdir(parents=True, exist_ok=True)
    RUNTIME_CATALOG.write_text(
        json.dumps(runtime, indent=2, ensure_ascii=True) + "\n", encoding="utf-8"
    )

    result = compare(legacy, runtime)
    REPORT_JSON.write_text(
        json.dumps(result, indent=2, ensure_ascii=True) + "\n", encoding="utf-8"
    )
    write_markdown(result)
    print(
        "research recipe/page catalog parity: "
        f"direct={result['runtime_direct_reference_count']} "
        f"total={result['runtime_total_entry_count']} "
        f"occurrences={result['research_occurrence_count']} "
        f"field_mismatches={len(result['field_mismatches'])} "
        f"parity_ok={result['parity_ok']}"
    )
    if not result["parity_ok"]:
        raise SystemExit(1)


if __name__ == "__main__":
    main()

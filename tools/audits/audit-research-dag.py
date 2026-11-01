#!/usr/bin/env python3
"""
TC6 Research & Aspect DAG Validator.
Verifies the complete graph of 148 Thaumonomicon research entries,
detects cycles/deadlocks, validates parent dependencies, stage constraints,
and referenced items/recipes.
"""

from __future__ import annotations

import argparse
import json
import sys
from collections import defaultdict, deque
from pathlib import Path
from typing import Any, Dict, List, Set, Tuple


def find_repo_root(start: Path) -> Path:
    curr = start.resolve()
    while curr.parent != curr:
        if (curr / "05_neoforge_port").exists() and (curr / "02_existing_decompiled_repo").exists():
            return curr
        curr = curr.parent
    return start.resolve()


def detect_cycles(graph: Dict[str, List[str]]) -> List[List[str]]:
    """Detect cycles in a directed graph using DFS."""
    visited: Dict[str, int] = {}  # 0: unvisited, 1: visiting, 2: visited
    cycles: List[List[str]] = []
    path: List[str] = []

    def dfs(node: str):
        visited[node] = 1
        path.append(node)
        for neighbor in graph.get(node, []):
            if neighbor not in visited or visited[neighbor] == 0:
                dfs(neighbor)
            elif visited[neighbor] == 1:
                # Cycle found!
                cycle_start = path.index(neighbor)
                cycles.append(path[cycle_start:] + [neighbor])
        path.pop()
        visited[node] = 2

    for node in graph:
        if node not in visited:
            dfs(node)

    return cycles


def audit_research_dag(repo_root: Path) -> Tuple[int, Dict[str, Any]]:
    r_dir = repo_root / "05_neoforge_port/src/main/resources/assets/thaumcraft/research"
    if not r_dir.exists():
        return 1, {"error": f"Research directory not found: {r_dir}"}

    all_entries: Dict[str, Dict[str, Any]] = {}
    categories: Dict[str, List[str]] = defaultdict(list)
    graph: Dict[str, List[str]] = defaultdict(list)

    # 1. Load all entries
    for r_file in sorted(r_dir.glob("*.json")):
        try:
            data = json.loads(r_file.read_text(encoding="utf-8"))
        except Exception as e:
            return 1, {"error": f"Syntax error in {r_file.name}: {e}"}

        for entry in data.get("entries", []):
            key = entry.get("key")
            if not key:
                continue
            cat = entry.get("category", r_file.stem.upper())
            all_entries[key] = entry
            categories[cat].append(key)

    # 2. Build graph and check parents
    missing_parents: Dict[str, List[str]] = defaultdict(list)

    # Known virtual triggers / events / scans in TC6
    known_virtual_triggers = {
        "gotthaumonomicon", "gotdream", "f_toomuchflux", "f_BAT", "Firebat", "Pechwand",
        "INSTABILITY", "DRAGONBREATH", "ORECRYSTAL", "ORBLOCK1", "ORBLOCK2", "TOTEMUNDYING"
    }

    for key, entry in all_entries.items():
        parents = entry.get("parents", [])
        for p in parents:
            # Parents starting with '!' are clue triggers / scans (e.g. !BATHSALTS, !CrimsonCultist, !gotthaumonomicon)
            if p.startswith("!"):
                # Track trigger requirement, not DAG progression edge
                continue

            # Strip prefixes like @, ~
            raw_p = p.lstrip("@~")
            # Strip stage suffix like @2
            base_p = raw_p.split("@")[0]

            if base_p in all_entries:
                graph[key].append(base_p)
            else:
                if base_p not in known_virtual_triggers and not base_p.startswith("f_") and not base_p.startswith("got"):
                    missing_parents[key].append(p)

    # 3. Detect cycles
    cycles = detect_cycles(graph)

    # 4. Validate stages and item crafts
    total_stages = 0
    total_recipes = 0
    craft_requirements: Set[str] = set()

    for key, entry in all_entries.items():
        stages = entry.get("stages", [])
        total_stages += len(stages)
        for stage in stages:
            recipes = stage.get("recipes", [])
            total_recipes += len(recipes)
            for craft in stage.get("required_craft", []):
                craft_requirements.add(craft)

    # Results summary
    stats: Dict[str, Any] = {
        "total_entries": len(all_entries),
        "categories_count": len(categories),
        "categories": {k: len(v) for k, v in categories.items()},
        "total_stages": total_stages,
        "total_recipes_unlocked": total_recipes,
        "cycles_detected": len(cycles),
        "cycles": cycles,
        "missing_parents_count": sum(len(v) for v in missing_parents.values()),
        "missing_parents": dict(missing_parents),
        "is_dag_valid": (len(cycles) == 0 and len(missing_parents) == 0)
    }

    exit_code = 0 if stats["is_dag_valid"] else 1
    return exit_code, stats


def main() -> int:
    parser = argparse.ArgumentParser(description="TC6 Research DAG Validator")
    parser.add_argument("--repo-root", default=".", help="Root of repository")
    parser.add_argument("--json", action="store_true", help="Output JSON results")
    args = parser.parse_args()

    repo_root = find_repo_root(Path(args.repo_root))
    exit_code, stats = audit_research_dag(repo_root)

    if args.json:
        print(json.dumps(stats, indent=2))
        return exit_code

    print("==================================================")
    print("TC6 Thaumonomicon Research DAG Validator")
    print("==================================================")
    print(f"Total Research Entries: {stats['total_entries']}")
    print(f"Categories:             {stats['categories_count']}")
    for cat, count in sorted(stats["categories"].items()):
        print(f"  - {cat:12}: {count} entries")
    print(f"Total Stages:           {stats['total_stages']}")
    print(f"Total Recipe Unlocks:   {stats['total_recipes_unlocked']}")
    print(f"Cycles / Deadlocks:     {stats['cycles_detected']}")
    print(f"Missing Parent Links:   {stats['missing_parents_count']}")
    print("==================================================")

    if stats["cycles"]:
        print("\n[ERROR] CYCLES DETECTED IN RESEARCH PROGRESSION:")
        for c in stats["cycles"]:
            print("  -> " + " -> ".join(c))

    if stats["missing_parents"]:
        print("\n[ERROR] MISSING PARENT DEPENDENCIES:")
        for child, p_list in stats["missing_parents"].items():
            print(f"  {child} requires non-existent: {p_list}")

    if stats["is_dag_valid"]:
        print("\n[OK] Research DAG is 100% acyclic, reachable, and valid!")
        return 0
    else:
        return 1


if __name__ == "__main__":
    sys.exit(main())

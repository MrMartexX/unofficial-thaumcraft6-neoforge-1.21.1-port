#!/usr/bin/env python3
"""
Thaumcraft 6 -> NeoForge 1.21.1 parity audit helper.

Compares the legacy TC6 source/resources with the NeoForge port and writes:
  build/reports/tc6_web_parity_audit.md
  build/reports/tc6_web_parity_audit.json

The script is intentionally read-only. It is a checkpoint tool for finding missing
assets, missing recipe outputs and unsafe vanilla placeholder recipes before a JAR
is produced from the web parity branch.
"""

from __future__ import annotations

import argparse
import json
from collections import defaultdict
from dataclasses import asdict, dataclass
from pathlib import Path
from typing import Any, Iterable

SPECIAL_RECIPE_TOKENS = {
    "arcane", "alchemy", "crucible", "infusion", "matrix", "pedestal",
    "essentia", "jar", "tube", "buffer", "filter", "bellows", "furnace",
    "infernal", "cluster", "golem", "seal", "focus", "foci", "lens",
    "gauntlet", "wand", "thaumometer", "goggles", "hungry", "void",
    "eldritch", "cult", "traveller", "traveler", "runic", "charm",
    "amulet", "ring", "belt", "bauble", "curio", "mirror", "lamp",
    "thaumium", "voidmetal", "salismundus", "salis_mundus",
}

VANILLA_RECIPE_TYPES = {
    "minecraft:crafting_shaped", "minecraft:crafting_shapeless",
    "minecraft:smelting", "minecraft:blasting", "minecraft:smoking",
    "minecraft:campfire_cooking", "minecraft:stonecutting",
    "crafting_shaped", "crafting_shapeless", "smelting",
}

RESOURCE_SUBDIRS = (
    "assets/thaumcraft/blockstates",
    "assets/thaumcraft/lang",
    "assets/thaumcraft/models",
    "assets/thaumcraft/particles",
    "assets/thaumcraft/sounds",
    "assets/thaumcraft/textures",
)

FOCUS_TEXTURE_TOKENS = (
    "rift", "portal", "taint", "cult", "golem", "traveller", "traveler",
    "hungry", "essentia", "jar", "tube", "focus", "gauntlet",
)


@dataclass(frozen=True)
class RecipeRecord:
    path: str
    recipe_type: str
    outputs: tuple[str, ...]
    inputs: tuple[str, ...]


@dataclass(frozen=True)
class SuspiciousRecipe:
    path: str
    recipe_type: str
    output: str
    matched_tokens: tuple[str, ...]
    reason: str


def load_json(path: Path) -> Any | None:
    for encoding in ("utf-8-sig", "utf-8", "cp1251"):
        try:
            return json.loads(path.read_text(encoding=encoding))
        except Exception:
            pass
    return None


def walk_files(root: Path) -> Iterable[Path]:
    return (p for p in root.rglob("*") if p.is_file()) if root.exists() else []


def rel(path: Path, root: Path) -> str:
    return path.relative_to(root).as_posix()


def discover_port_root(repo_root: Path, explicit: str | None) -> Path:
    if explicit:
        return Path(explicit).resolve()
    direct = repo_root / "05_neoforge_port"
    if direct.exists():
        return direct.resolve()
    for build_gradle in repo_root.rglob("build.gradle"):
        if (build_gradle.parent / "src" / "main" / "resources").exists():
            return build_gradle.parent.resolve()
    raise SystemExit("Could not discover port root. Pass --port-root.")


def discover_legacy_root(repo_root: Path, explicit: str | None) -> Path:
    if explicit:
        return Path(explicit).resolve()
    preferred = repo_root / "Thaumcraft-6-Source-Code-master"
    if preferred.exists():
        return preferred.resolve()
    for mcmod in repo_root.rglob("mcmod.info"):
        candidate = mcmod.parent
        if (candidate / "src" / "main" / "resources" / "assets" / "thaumcraft").exists():
            return candidate.resolve()
    for assets in repo_root.rglob("assets/thaumcraft"):
        if "Thaumcraft" in str(assets):
            return assets.parents[2].resolve()
    raise SystemExit("Could not discover legacy TC6 root. Pass --legacy-root.")


def resource_roots(project_root: Path) -> list[Path]:
    candidates = [
        project_root / "src" / "main" / "resources",
        project_root / "src" / "generated" / "resources",
    ]
    return [p for p in candidates if p.exists()]


def collect_resources(project_root: Path) -> dict[str, set[str]]:
    found: dict[str, set[str]] = defaultdict(set)
    for root in resource_roots(project_root):
        for subdir in RESOURCE_SUBDIRS:
            base = root / subdir
            for path in walk_files(base):
                found[subdir].add(rel(path, base))
    return found


def recipe_roots(project_root: Path) -> list[Path]:
    """Support both 1.12-style data/*/recipes and 1.21-style data/*/recipe."""
    roots: list[Path] = []
    for root in resource_roots(project_root):
        data_root = root / "data"
        if not data_root.exists():
            continue
        for namespace in data_root.iterdir():
            for dirname in ("recipes", "recipe"):
                candidate = namespace / dirname
                if candidate.exists():
                    roots.append(candidate)
    return roots


def collect_ids(value: Any) -> set[str]:
    ids: set[str] = set()
    if isinstance(value, str):
        if ":" in value and not value.startswith("#"):
            ids.add(value)
    elif isinstance(value, dict):
        for key in ("item", "id"):
            value_id = value.get(key)
            if isinstance(value_id, str) and ":" in value_id and not value_id.startswith("#"):
                ids.add(value_id)
        for nested in value.values():
            ids.update(collect_ids(nested))
    elif isinstance(value, list):
        for nested in value:
            ids.update(collect_ids(nested))
    return ids


def recipe_outputs(data: dict[str, Any]) -> set[str]:
    outputs: set[str] = set()
    for key in ("result", "output", "results"):
        if key in data:
            outputs.update(collect_ids(data[key]))
    return outputs


def recipe_inputs(data: dict[str, Any]) -> set[str]:
    ignored = {"result", "output", "results", "conditions", "type", "group", "category", "show_notification"}
    return collect_ids([value for key, value in data.items() if key not in ignored])


def collect_recipes(project_root: Path) -> list[RecipeRecord]:
    records: list[RecipeRecord] = []
    for root in recipe_roots(project_root):
        for path in root.rglob("*.json"):
            data = load_json(path)
            if isinstance(data, dict):
                records.append(
                    RecipeRecord(
                        path=path.relative_to(project_root).as_posix(),
                        recipe_type=str(data.get("type", "minecraft:crafting_shaped")),
                        outputs=tuple(sorted(recipe_outputs(data))),
                        inputs=tuple(sorted(recipe_inputs(data))),
                    )
                )
    return records


def item_model_ids(project_root: Path) -> set[str]:
    ids: set[str] = set()
    for root in resource_roots(project_root):
        base = root / "assets" / "thaumcraft" / "models" / "item"
        for path in walk_files(base):
            if path.suffix == ".json":
                ids.add("thaumcraft:" + rel(path, base)[:-5])
    return ids


def blockstate_ids(project_root: Path) -> set[str]:
    ids: set[str] = set()
    for root in resource_roots(project_root):
        base = root / "assets" / "thaumcraft" / "blockstates"
        for path in walk_files(base):
            if path.suffix == ".json":
                ids.add("thaumcraft:" + rel(path, base)[:-5])
    return ids


def matching_tokens(text: str) -> tuple[str, ...]:
    normalized = text.lower().replace("-", "_").replace("/", "_")
    return tuple(sorted(t for t in SPECIAL_RECIPE_TOKENS if t in normalized))


def suspicious_vanilla_recipes(records: Iterable[RecipeRecord]) -> list[SuspiciousRecipe]:
    out: list[SuspiciousRecipe] = []
    for record in records:
        if record.recipe_type not in VANILLA_RECIPE_TYPES:
            continue
        for output in record.outputs:
            if not output.startswith("thaumcraft:"):
                continue
            tokens = matching_tokens(output + " " + record.path)
            if tokens:
                out.append(
                    SuspiciousRecipe(
                        path=record.path,
                        recipe_type=record.recipe_type,
                        output=output,
                        matched_tokens=tokens,
                        reason="Thaumcraft special mechanic output is still exposed through a vanilla recipe type.",
                    )
                )
    return out


def diff_resources(legacy: dict[str, set[str]], port: dict[str, set[str]]) -> dict[str, list[str]]:
    return {subdir: sorted(legacy.get(subdir, set()) - port.get(subdir, set())) for subdir in RESOURCE_SUBDIRS}


def first(values: Iterable[str], limit: int) -> list[str]:
    return list(values)[:limit]


def focused_textures(missing_textures: list[str]) -> dict[str, list[str]]:
    return {
        token: [p for p in missing_textures if token in p.lower()][:50]
        for token in FOCUS_TEXTURE_TOKENS
        if any(token in p.lower() for p in missing_textures)
    }


def write_reports(
    out_dir: Path,
    repo_root: Path,
    port_root: Path,
    legacy_root: Path,
    missing_resources: dict[str, list[str]],
    missing_recipe_outputs: list[str],
    suspicious: list[SuspiciousRecipe],
    legacy_items: set[str],
    port_items: set[str],
    legacy_blocks: set[str],
    port_blocks: set[str],
) -> None:
    out_dir.mkdir(parents=True, exist_ok=True)
    payload = {
        "repo_root": str(repo_root),
        "port_root": str(port_root),
        "legacy_root": str(legacy_root),
        "missing_resources": missing_resources,
        "missing_recipe_outputs": missing_recipe_outputs,
        "suspicious_vanilla_recipes": [asdict(x) for x in suspicious],
        "legacy_item_model_count": len(legacy_items),
        "port_item_model_count": len(port_items),
        "missing_item_models": sorted(legacy_items - port_items),
        "legacy_blockstate_count": len(legacy_blocks),
        "port_blockstate_count": len(port_blocks),
        "missing_blockstates": sorted(legacy_blocks - port_blocks),
        "focused_missing_textures": focused_textures(missing_resources.get("assets/thaumcraft/textures", [])),
    }
    (out_dir / "tc6_web_parity_audit.json").write_text(json.dumps(payload, ensure_ascii=False, indent=2), encoding="utf-8")

    lines = [
        "# TC6 Web Parity Audit Report", "",
        f"Port root: `{port_root}`",
        f"Legacy root: `{legacy_root}`", "",
        "## Summary", "",
        f"- Legacy item model IDs: `{len(legacy_items)}`",
        f"- Port item model IDs: `{len(port_items)}`",
        f"- Missing item model IDs: `{len(legacy_items - port_items)}`",
        f"- Legacy blockstate IDs: `{len(legacy_blocks)}`",
        f"- Port blockstate IDs: `{len(port_blocks)}`",
        f"- Missing blockstate IDs: `{len(legacy_blocks - port_blocks)}`",
        f"- Missing legacy recipe outputs: `{len(missing_recipe_outputs)}`",
        f"- Suspicious vanilla placeholder recipes: `{len(suspicious)}`", "",
        "## Missing resources by resource group", "",
    ]

    for subdir, values in missing_resources.items():
        lines += [f"### `{subdir}` — missing `{len(values)}`"]
        lines += [f"- `{item}`" for item in first(values, 80)]
        if len(values) > 80:
            lines.append(f"- ... `{len(values) - 80}` more")
        lines.append("")

    lines += ["## Missing legacy recipe outputs", ""]
    lines += [f"- `{item}`" for item in first(missing_recipe_outputs, 120)]
    if len(missing_recipe_outputs) > 120:
        lines.append(f"- ... `{len(missing_recipe_outputs) - 120}` more")
    lines.append("")

    lines += ["## Suspicious vanilla placeholder recipes", ""]
    for item in suspicious[:160]:
        lines.append(f"- `{item.path}` -> `{item.output}` via `{item.recipe_type}`; tokens: `{', '.join(item.matched_tokens)}`")
    if len(suspicious) > 160:
        lines.append(f"- ... `{len(suspicious) - 160}` more")
    lines.append("")

    lines += ["## Focused missing texture groups", ""]
    for token, values in focused_textures(missing_resources.get("assets/thaumcraft/textures", [])).items():
        lines.append(f"### `{token}`")
        lines += [f"- `{item}`" for item in values]
        lines.append("")

    (out_dir / "tc6_web_parity_audit.md").write_text("\n".join(lines) + "\n", encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser(description="Audit TC6 legacy parity against the NeoForge port.")
    parser.add_argument("--repo-root", default=".")
    parser.add_argument("--port-root")
    parser.add_argument("--legacy-root")
    parser.add_argument("--out-dir")
    args = parser.parse_args()

    repo_root = Path(args.repo_root).resolve()
    port_root = discover_port_root(repo_root, args.port_root)
    legacy_root = discover_legacy_root(repo_root, args.legacy_root)
    out_dir = Path(args.out_dir).resolve() if args.out_dir else port_root / "build" / "reports"

    missing_resources = diff_resources(collect_resources(legacy_root), collect_resources(port_root))
    legacy_recipes = collect_recipes(legacy_root)
    port_recipes = collect_recipes(port_root)
    legacy_outputs = {o for r in legacy_recipes for o in r.outputs if o.startswith("thaumcraft:")}
    port_outputs = {o for r in port_recipes for o in r.outputs if o.startswith("thaumcraft:")}
    suspicious = suspicious_vanilla_recipes(port_recipes)

    write_reports(
        out_dir=out_dir,
        repo_root=repo_root,
        port_root=port_root,
        legacy_root=legacy_root,
        missing_resources=missing_resources,
        missing_recipe_outputs=sorted(legacy_outputs - port_outputs),
        suspicious=suspicious,
        legacy_items=item_model_ids(legacy_root),
        port_items=item_model_ids(port_root),
        legacy_blocks=blockstate_ids(legacy_root),
        port_blocks=blockstate_ids(port_root),
    )

    print(f"Wrote {out_dir / 'tc6_web_parity_audit.md'}")
    print(f"Wrote {out_dir / 'tc6_web_parity_audit.json'}")
    print(f"Suspicious vanilla placeholder recipes: {len(suspicious)}")
    print(f"Missing legacy recipe outputs: {len(legacy_outputs - port_outputs)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

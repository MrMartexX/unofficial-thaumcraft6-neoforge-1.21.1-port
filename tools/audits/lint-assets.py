#!/usr/bin/env python3
"""
TC6 Asset & Texture Integrity Linter.
Validates all blockstate, model, and texture references in Thaumcraft 6 NeoForge port.
"""

from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path
from typing import Any, Dict, List, Set, Tuple


def find_repo_root(start: Path) -> Path:
    curr = start.resolve()
    while curr.parent != curr:
        if (curr / "05_neoforge_port").exists() and (curr / "02_existing_decompiled_repo").exists():
            return curr
        curr = curr.parent
    return start.resolve()


def lint_assets(repo_root: Path) -> Tuple[int, Dict[str, Any]]:
    port_assets = repo_root / "05_neoforge_port/src/main/resources/assets/thaumcraft"
    models_dir = port_assets / "models"
    textures_dir = port_assets / "textures"
    blockstates_dir = port_assets / "blockstates"

    errors: List[str] = []
    warnings: List[str] = []

    if not port_assets.exists():
        return 1, {"error": f"Assets directory not found: {port_assets}"}

    # 1. Check all model files
    model_files = list(models_dir.glob("**/*.json"))
    checked_models = len(model_files)

    for m_path in model_files:
        try:
            data = json.loads(m_path.read_text(encoding="utf-8"))
        except Exception as e:
            errors.append(f"[Syntax Error] Model '{m_path.relative_to(port_assets)}': {e}")
            continue

        # Check parent
        parent = data.get("parent")
        if parent and isinstance(parent, str) and parent.startswith("thaumcraft:"):
            p_rel = parent.replace("thaumcraft:", "")
            # Can be item/<name> or block/<name>
            target_json = models_dir / f"{p_rel}.json"
            if not target_json.exists():
                errors.append(f"[Missing Parent] Model '{m_path.name}' references non-existent parent '{parent}'")

        # Check textures
        textures = data.get("textures", {})
        if isinstance(textures, dict):
            for slot, tex in textures.items():
                if not isinstance(tex, str):
                    continue
                if tex.startswith("#"):
                    continue  # Variable reference
                if tex.startswith("thaumcraft:"):
                    tex_clean = tex.replace("thaumcraft:", "")
                    target_png = textures_dir / f"{tex_clean}.png"
                    if not target_png.exists():
                        errors.append(f"[Missing Texture] Model '{m_path.name}' ({slot}) references missing texture '{tex}'")

    # 2. Check all blockstates
    blockstate_files = list(blockstates_dir.glob("*.json"))
    checked_blockstates = len(blockstate_files)

    for b_path in blockstate_files:
        try:
            data = json.loads(b_path.read_text(encoding="utf-8"))
        except Exception as e:
            errors.append(f"[Syntax Error] Blockstate '{b_path.name}': {e}")
            continue

        model_refs: List[str] = []

        # Variants format
        if "variants" in data and isinstance(data["variants"], dict):
            for v_name, v_def in data["variants"].items():
                v_list = v_def if isinstance(v_def, list) else [v_def]
                for item in v_list:
                    if isinstance(item, dict) and "model" in item:
                        model_refs.append(item["model"])

        # Multipart format
        if "multipart" in data and isinstance(data["multipart"], list):
            for part in data["multipart"]:
                apply_data = part.get("apply", {})
                apply_list = apply_data if isinstance(apply_data, list) else [apply_data]
                for item in apply_list:
                    if isinstance(item, dict) and "model" in item:
                        model_refs.append(item["model"])

        for m_ref in model_refs:
            if m_ref.startswith("thaumcraft:"):
                clean = m_ref.replace("thaumcraft:", "")
                target1 = models_dir / f"{clean}.json"
                if not target1.exists():
                    errors.append(f"[Missing Model] Blockstate '{b_path.name}' references missing model '{m_ref}'")

    # 3. Check texture file integrity
    checked_textures = 0
    corrupt_textures = 0
    png_files = list(textures_dir.glob("**/*.png"))
    checked_textures = len(png_files)

    for png_path in png_files:
        try:
            size = png_path.stat().st_size
            if size == 0:
                errors.append(f"[Empty Texture] 0-byte file: '{png_path.relative_to(port_assets)}'")
                corrupt_textures += 1
                continue
            with open(png_path, "rb") as f:
                header = f.read(8)
                if header != b"\x89PNG\r\n\x1a\n":
                    errors.append(f"[Corrupted Texture] Invalid PNG header: '{png_path.relative_to(port_assets)}'")
                    corrupt_textures += 1
        except Exception as e:
            errors.append(f"[Read Error] '{png_path.name}': {e}")
            corrupt_textures += 1

    stats = {
        "models_checked": checked_models,
        "blockstates_checked": checked_blockstates,
        "textures_checked": checked_textures,
        "errors_count": len(errors),
        "warnings_count": len(warnings),
        "errors": errors,
        "warnings": warnings,
    }

    exit_code = 0 if len(errors) == 0 else 1
    return exit_code, stats


def main() -> int:
    parser = argparse.ArgumentParser(description="TC6 Asset & Texture Integrity Linter")
    parser.add_argument("--repo-root", default=".", help="Root of repository")
    parser.add_argument("--json", action="store_true", help="Output JSON results")
    args = parser.parse_args()

    repo_root = find_repo_root(Path(args.repo_root))
    exit_code, stats = lint_assets(repo_root)

    if args.json:
        print(json.dumps(stats, indent=2))
        return exit_code

    print("==================================================")
    print("TC6 Asset & Texture Integrity Linter")
    print("==================================================")
    print(f"Models checked:      {stats['models_checked']}")
    print(f"Blockstates checked: {stats['blockstates_checked']}")
    print(f"Textures verified:   {stats['textures_checked']}")
    print(f"Total Errors:        {stats['errors_count']}")
    print("==================================================")

    if stats["errors"]:
        print("\nERRORS DETECTED:")
        for err in stats["errors"]:
            print(f"  [ERROR] {err}")
        return 1
    else:
        print("\n[OK] All assets, model references, and textures are 100% valid!")
        return 0


if __name__ == "__main__":
    sys.exit(main())

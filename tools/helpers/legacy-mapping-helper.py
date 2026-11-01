#!/usr/bin/env python3
"""
TC6 Legacy-to-Mojmap Migration Helper.
Analyzes a 1.12.2 Thaumcraft Forge class and outputs the modern NeoForge 1.21.1
Mojmap replacements, class templates, method signatures, and required assets.
"""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path
from typing import Dict, List, Tuple

# Mapping of legacy 1.12.2 MCP method patterns to 1.21.1 Mojmap
METHOD_MAPPINGS: Dict[str, Dict[str, str]] = {
    # Block methods
    "onBlockActivated": {
        "legacy": "onBlockActivated(World, BlockPos, IBlockState, EntityPlayer, EnumHand, EnumFacing, float, float, float)",
        "modern": "useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)",
        "return_type": "InteractionResult",
        "notes": "In 1.21.1, return InteractionResult.SUCCESS or PASS."
    },
    "createTileEntity": {
        "legacy": "createTileEntity(World, IBlockState)",
        "modern": "newBlockEntity(BlockPos pos, BlockState state)",
        "return_type": "BlockEntity",
        "notes": "Class must implement EntityBlock; return new YourBlockEntity(pos, state)."
    },
    "hasTileEntity": {
        "legacy": "hasTileEntity(IBlockState)",
        "modern": "implement EntityBlock",
        "return_type": "interface",
        "notes": "Replace hasTileEntity by implementing EntityBlock on your Block class."
    },
    "breakBlock": {
        "legacy": "breakBlock(World, BlockPos, IBlockState)",
        "modern": "onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)",
        "return_type": "void",
        "notes": "Always call super.onRemove(state, level, pos, newState, isMoving) if state.getBlock() != newState.getBlock()."
    },
    "neighborChanged": {
        "legacy": "neighborChanged(IBlockState, World, BlockPos, Block, BlockPos)",
        "modern": "neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving)",
        "return_type": "void",
        "notes": "Used for redstone signals and block updates."
    },
    "canPlaceBlockAt": {
        "legacy": "canPlaceBlockAt(World, BlockPos)",
        "modern": "canSurvive(BlockState state, LevelReader level, BlockPos pos)",
        "return_type": "boolean",
        "notes": "Checks if block can remain placed."
    },
    "getDrops": {
        "legacy": "getDrops(NonNullList<ItemStack>, IBlockAccess, BlockPos, IBlockState, int)",
        "modern": "JSON Loot Table (data/thaumcraft/loot_table/blocks/<id>.json)",
        "return_type": "data",
        "notes": "In 1.21.1, block drops are strictly defined in data-driven loot tables."
    },
    "getBoundingBox": {
        "legacy": "getBoundingBox(IBlockState, IBlockAccess, BlockPos)",
        "modern": "getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)",
        "return_type": "VoxelShape",
        "notes": "Return VoxelShape constructed with Shapes.box() or Shapes.or()."
    },

    # TileEntity / BlockEntity methods
    "readFromNBT": {
        "legacy": "readFromNBT(NBTTagCompound)",
        "modern": "loadAdditional(CompoundTag tag, HolderLookup.Provider registries)",
        "return_type": "void",
        "notes": "Must call super.loadAdditional(tag, registries)."
    },
    "writeToNBT": {
        "legacy": "writeToNBT(NBTTagCompound)",
        "modern": "saveAdditional(CompoundTag tag, HolderLookup.Provider registries)",
        "return_type": "void",
        "notes": "Must call super.saveAdditional(tag, registries)."
    },
    "getUpdatePacket": {
        "legacy": "getUpdatePacket()",
        "modern": "getUpdatePacket() -> ClientboundBlockEntityDataPacket.create(this)",
        "return_type": "Packet<ClientGamePacketListener>",
        "notes": "Returns network synchronization packet."
    },
    "getUpdateTag": {
        "legacy": "getUpdateTag()",
        "modern": "getUpdateTag(HolderLookup.Provider registries)",
        "return_type": "CompoundTag",
        "notes": "Returns initial chunk sync tag."
    },

    # World calls
    "isRemote": {
        "legacy": "world.isRemote",
        "modern": "level.isClientSide()",
        "return_type": "boolean",
        "notes": "True if executing on client thread."
    },
    "getTileEntity": {
        "legacy": "world.getTileEntity(pos)",
        "modern": "level.getBlockEntity(pos)",
        "return_type": "BlockEntity",
        "notes": "Can cast to your specific BlockEntity type."
    },
    "spawnEntity": {
        "legacy": "world.spawnEntity(entity)",
        "modern": "level.addFreshEntity(entity)",
        "return_type": "boolean",
        "notes": "Spawns new entity into world."
    },

    # NBT on ItemStack
    "getTagCompound": {
        "legacy": "stack.getTagCompound()",
        "modern": "stack.get(TCDataComponents.*) or stack.getOrDefault(...)",
        "return_type": "DataComponent",
        "notes": "DO NOT USE NBT ON ITEMSTACK IN 1.21.1. Use registered DataComponent."
    },
    "setTagCompound": {
        "legacy": "stack.setTagCompound(nbt)",
        "modern": "stack.set(TCDataComponents.*, value)",
        "return_type": "void",
        "notes": "Use typed DataComponent."
    }
}


def analyze_legacy_file(file_path: Path) -> Dict[str, Any]:
    if not file_path.exists():
        return {"error": f"File not found: {file_path}"}

    content = file_path.read_text(encoding="utf-8", errors="ignore")
    detected_patterns: List[Tuple[str, Dict[str, str]]] = []

    for key, data in METHOD_MAPPINGS.items():
        if re.search(r'\b' + re.escape(key) + r'\b', content):
            detected_patterns.append((key, data))

    # Detect package and class name
    class_match = re.search(r'(?:public\s+)?(?:abstract\s+)?class\s+(\w+)(?:\s+extends\s+(\w+))?', content)
    class_name = class_match.group(1) if class_match else file_path.stem
    super_name = class_match.group(2) if class_match else "Object"

    return {
        "file": file_path.name,
        "class_name": class_name,
        "super_name": super_name,
        "patterns_found": detected_patterns
    }


def main() -> int:
    parser = argparse.ArgumentParser(description="TC6 Legacy-to-Mojmap Migration Helper")
    parser.add_argument("legacy_file", help="Path to legacy Java source file")
    args = parser.parse_args()

    legacy_path = Path(args.legacy_file)
    result = analyze_legacy_file(legacy_path)

    if "error" in result:
        print(f"Error: {result['error']}", file=sys.stderr)
        return 1

    print("==================================================")
    print(f"TC6 Migration Analysis: {result['class_name']}")
    print(f"Legacy Superclass:      {result['super_name']}")
    print(f"Detected Patterns:      {len(result['patterns_found'])}")
    print("==================================================")

    if not result["patterns_found"]:
        print("No special legacy lifecycle patterns detected.")
        return 0

    print("\nMODERN MOJMAP 1.21.1 MIGRATION TARGETS:")
    for key, data in result["patterns_found"]:
        print(f"\n* Legacy:  {data['legacy']}")
        print(f"  Modern:  {data['modern']}")
        print(f"  Notes:   {data['notes']}")

    return 0


if __name__ == "__main__":
    sys.exit(main())

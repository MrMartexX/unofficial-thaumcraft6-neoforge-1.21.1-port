"""Exports exact 1.12.2 Forge BiomeDictionary registrations from bytecode and compares crystal aspect bias against 1.21.1."""
import argparse
import csv
import json
from pathlib import Path
import struct
import zipfile

ROOT = Path(__file__).resolve().parents[3]
DEFAULT_FORGE_1122_JAR = Path.home() / (
    '.gradle/caches/forge_gradle/minecraft_user_repo/net/minecraftforge/forge/'
    '1.12.2-14.23.5.2860_mapped_snapshot_20171003-1.12/'
    'forge-1.12.2-14.23.5.2860_mapped_snapshot_20171003-1.12.jar'
)
VANILLA_1211_JAR = ROOT / '05_neoforge_port/build/moddev/artifacts/neoforge-21.1.265-client-extra-aka-minecraft-resources.jar'
PORT_RES = ROOT / '05_neoforge_port/src/main/resources'
PRIMALS = ['aer', 'ignis', 'aqua', 'terra', 'ordo', 'perditio']

# Exact final mapping in Legacy BiomeHandler.registerBiomes() after DRY is overwritten from FIRE to ENTROPY.
LEGACY_TYPE_INFO = {
    'WATER': (0.33, 'aqua', False, 0.0),
    'OCEAN': (0.33, 'aqua', False, 0.0),
    'RIVER': (0.40, 'aqua', False, 0.0),
    'WET': (0.40, 'aqua', False, 0.0),
    'LUSH': (0.50, 'aqua', True, 0.5),
    'HOT': (0.33, 'ignis', False, 0.0),
    'NETHER': (0.125, 'ignis', False, 0.0),
    'MESA': (0.33, 'ignis', False, 0.0),
    'SPOOKY': (0.50, 'ignis', False, 0.0),
    'DENSE': (0.40, 'ordo', False, 0.0),
    'SNOWY': (0.25, 'ordo', False, 0.0),
    'COLD': (0.25, 'ordo', False, 0.0),
    'MUSHROOM': (0.75, 'ordo', False, 0.0),
    'MAGICAL': (0.75, 'ordo', True, 1.0),
    'CONIFEROUS': (0.33, 'terra', True, 0.2),
    'FOREST': (0.50, 'terra', True, 1.0),
    'SANDY': (0.25, 'terra', False, 0.0),
    'BEACH': (0.30, 'terra', False, 0.0),
    'JUNGLE': (0.60, 'terra', False, 0.0),
    'SAVANNA': (0.25, 'aer', True, 0.2),
    'MOUNTAIN': (0.30, 'aer', False, 0.0),
    'HILLS': (0.33, 'aer', False, 0.0),
    'PLAINS': (0.30, 'aer', True, 0.2),
    'END': (0.125, 'aer', False, 0.0),
    'DRY': (0.125, 'perditio', False, 0.0),
    'SPARSE': (0.20, 'perditio', False, 0.0),
    'SWAMP': (0.50, 'perditio', True, 0.2),
    'WASTELAND': (0.125, 'perditio', False, 0.0),
    'DEAD': (0.10, 'perditio', False, 0.0),
}

# Mapping from 1.12.2 biome registry IDs to 1.21.1 biome registry IDs (or None if removed/merged).
LEGACY_TO_MODERN_BIOME = {
    'ocean': 'minecraft:ocean',
    'plains': 'minecraft:plains',
    'desert': 'minecraft:desert',
    'extreme_hills': 'minecraft:windswept_hills',
    'forest': 'minecraft:forest',
    'taiga': 'minecraft:taiga',
    'swampland': 'minecraft:swamp',
    'river': 'minecraft:river',
    'hell': 'minecraft:nether_wastes',
    'sky': 'minecraft:the_end',
    'frozen_ocean': 'minecraft:frozen_ocean',
    'frozen_river': 'minecraft:frozen_river',
    'ice_flats': 'minecraft:snowy_plains',
    'ice_mountains': 'minecraft:snowy_slopes',
    'mushroom_island': 'minecraft:mushroom_fields',
    'mushroom_island_shore': 'minecraft:mushroom_fields',
    'beaches': 'minecraft:beach',
    'desert_hills': 'minecraft:desert',
    'forest_hills': 'minecraft:forest',
    'taiga_hills': 'minecraft:taiga',
    'smaller_extreme_hills': 'minecraft:windswept_hills',
    'jungle': 'minecraft:jungle',
    'jungle_hills': 'minecraft:jungle',
    'jungle_edge': 'minecraft:sparse_jungle',
    'deep_ocean': 'minecraft:deep_ocean',
    'stone_beach': 'minecraft:stony_shore',
    'cold_beach': 'minecraft:snowy_beach',
    'birch_forest': 'minecraft:birch_forest',
    'birch_forest_hills': 'minecraft:birch_forest',
    'roofed_forest': 'minecraft:dark_forest',
    'taiga_cold': 'minecraft:snowy_taiga',
    'taiga_cold_hills': 'minecraft:snowy_taiga',
    'redwood_taiga': 'minecraft:old_growth_pine_taiga',
    'redwood_taiga_hills': 'minecraft:old_growth_pine_taiga',
    'extreme_hills_with_trees': 'minecraft:windswept_forest',
    'savanna': 'minecraft:savanna',
    'savanna_rock': 'minecraft:savanna_plateau',
    'mesa': 'minecraft:badlands',
    'mesa_rock': 'minecraft:wooded_badlands',
    'mesa_clear_rock': 'minecraft:badlands',
    'void': 'minecraft:the_void',
    'mutated_plains': 'minecraft:sunflower_plains',
    'mutated_desert': 'minecraft:desert',
    'mutated_extreme_hills': 'minecraft:windswept_gravelly_hills',
    'mutated_forest': 'minecraft:flower_forest',
    'mutated_taiga': 'minecraft:taiga',
    'mutated_swampland': 'minecraft:swamp',
    'mutated_ice_flats': 'minecraft:ice_spikes',
    'mutated_jungle': 'minecraft:jungle',
    'mutated_jungle_edge': 'minecraft:sparse_jungle',
    'mutated_birch_forest': 'minecraft:old_growth_birch_forest',
    'mutated_birch_forest_hills': 'minecraft:old_growth_birch_forest',
    'mutated_roofed_forest': 'minecraft:dark_forest',
    'mutated_taiga_cold': 'minecraft:snowy_taiga',
    'mutated_redwood_taiga': 'minecraft:old_growth_spruce_taiga',
    'mutated_redwood_taiga_hills': 'minecraft:old_growth_spruce_taiga',
    'mutated_extreme_hills_with_trees': 'minecraft:windswept_forest',
    'mutated_savanna': 'minecraft:windswept_savanna',
    'mutated_savanna_rock': 'minecraft:savanna_plateau',
    'mutated_mesa': 'minecraft:eroded_badlands',
    'mutated_mesa_rock': 'minecraft:wooded_badlands',
    'mutated_mesa_clear_rock': 'minecraft:badlands',
    'thaumcraft:magical_forest': 'thaumcraft:magical_forest',
    'thaumcraft:eerie': 'thaumcraft:eerie',
    'thaumcraft:eldritch': 'thaumcraft:eldritch',
}


def read_class_methods(data: bytes):
    pos = 8
    cp_count = struct.unpack('>H', data[pos:pos + 2])[0]
    pos += 2
    cp = [None] * cp_count
    i = 1
    while i < cp_count:
        tag = data[pos]
        pos += 1
        if tag == 1:
            length = struct.unpack('>H', data[pos:pos + 2])[0]
            pos += 2
            cp[i] = ('utf8', data[pos:pos + length].decode('utf-8', 'replace'))
            pos += length
        elif tag in (3, 4):
            cp[i] = (tag, struct.unpack('>I', data[pos:pos + 4])[0])
            pos += 4
        elif tag in (5, 6):
            cp[i] = (tag, struct.unpack('>Q', data[pos:pos + 8])[0])
            pos += 8
            i += 1
        elif tag in (7, 8, 16, 19, 20):
            cp[i] = (tag, struct.unpack('>H', data[pos:pos + 2])[0])
            pos += 2
        elif tag in (9, 10, 11, 12, 18):
            cp[i] = (tag, struct.unpack('>HH', data[pos:pos + 4]))
            pos += 4
        elif tag == 15:
            cp[i] = (tag, struct.unpack('>BH', data[pos:pos + 3]))
            pos += 3
        i += 1
    pos += 6
    ic = struct.unpack('>H', data[pos:pos + 2])[0]
    pos += 2 + 2 * ic
    fc = struct.unpack('>H', data[pos:pos + 2])[0]
    pos += 2
    for _ in range(fc):
        pos += 6
        ac = struct.unpack('>H', data[pos:pos + 2])[0]
        pos += 2
        for _ in range(ac):
            pos += 2
            al = struct.unpack('>I', data[pos:pos + 4])[0]
            pos += 4 + al
    mc = struct.unpack('>H', data[pos:pos + 2])[0]
    pos += 2
    methods = {}
    for _ in range(mc):
        _, name_idx, _, ac = struct.unpack('>HHHH', data[pos:pos + 8])
        pos += 8
        mname = cp[name_idx][1]
        for _ in range(ac):
            aname = cp[struct.unpack('>H', data[pos:pos + 2])[0]][1]
            pos += 2
            al = struct.unpack('>I', data[pos:pos + 4])[0]
            pos += 4
            if aname == 'Code':
                code_len = struct.unpack('>I', data[pos + 4:pos + 8])[0]
                methods[mname] = data[pos + 8:pos + 8 + code_len]
            pos += al
    return cp, methods


def field_ref_info(cp, idx):
    cls_idx, nt_idx = cp[idx][1]
    cls_name = cp[cp[cls_idx][1]][1]
    fname = cp[cp[nt_idx][1][0]][1]
    return cls_name, fname


def method_ref_info(cp, idx):
    cls_idx, nt_idx = cp[idx][1]
    cls_name = cp[cp[cls_idx][1]][1]
    mname = cp[cp[nt_idx][1][0]][1]
    return cls_name, mname


def extract_1122_biomes_and_types(jar_path: Path):
    with zipfile.ZipFile(jar_path) as jar:
        cp_b, m_b = read_class_methods(jar.read('net/minecraft/init/Biomes.class'))
        cp_d, m_d = read_class_methods(jar.read('net/minecraftforge/common/BiomeDictionary.class'))

    # 1. Extract Biomes.<FIELD> -> registry_id from Biomes.<clinit>
    code_b = m_b['<clinit>']
    i = 0
    last_str = None
    field_to_id = {}
    while i < len(code_b):
        op = code_b[i]
        if op == 0x12:
            idx = code_b[i + 1]
            if cp_b[idx][0] == 8:
                last_str = cp_b[cp_b[idx][1]][1]
            i += 2
        elif op == 0x13:
            idx = struct.unpack('>H', code_b[i + 1:i + 3])[0]
            if cp_b[idx][0] == 8:
                last_str = cp_b[cp_b[idx][1]][1]
            i += 3
        elif op == 0xB3:  # putstatic
            idx = struct.unpack('>H', code_b[i + 1:i + 3])[0]
            _, fname = field_ref_info(cp_b, idx)
            if last_str:
                field_to_id[fname] = last_str
            i += 3
        elif op in (0xB8, 0xB2, 0xB4, 0xB5, 0xB6, 0xB7, 0xBB, 0xBD, 0xC0, 0x99, 0x9A, 0xA7):
            i += 3
        else:
            i += 1

    # 2. Extract registerVanillaBiomes() addTypes calls from BiomeDictionary.class
    code_d = m_d['registerVanillaBiomes']
    i = 0
    current_biome_field = None
    current_types = []
    biome_types = {}
    while i < len(code_d):
        op = code_d[i]
        if op == 0xB2:  # getstatic
            idx = struct.unpack('>H', code_d[i + 1:i + 3])[0]
            cls_name, fname = field_ref_info(cp_d, idx)
            if cls_name == 'net/minecraft/init/Biomes':
                current_biome_field = fname
                current_types = []
            elif cls_name == 'net/minecraftforge/common/BiomeDictionary$Type':
                current_types.append(fname)
            i += 3
        elif op == 0xB8:  # invokestatic
            idx = struct.unpack('>H', code_d[i + 1:i + 3])[0]
            cls_name, mname = method_ref_info(cp_d, idx)
            if cls_name == 'net/minecraftforge/common/BiomeDictionary' and mname == 'addTypes' and current_biome_field:
                biome_id = field_to_id[current_biome_field]
                expanded = list(current_types)
                if ('OCEAN' in expanded or 'RIVER' in expanded) and 'WATER' not in expanded:
                    expanded.append('WATER')
                biome_types[biome_id] = expanded
                current_biome_field = None
                current_types = []
            i += 3
        elif op in (0x10, 0x12):
            i += 2
        elif op in (0x11, 0x13, 0xB3, 0xB4, 0xB5, 0xB6, 0xB7, 0xBB, 0xBD, 0xC0, 0x99, 0x9A, 0xA7):
            i += 3
        else:
            i += 1

    # 3. Add Thaumcraft's 3 legacy biomes from BiomeHandler.registerBiomes()
    biome_types['thaumcraft:eerie'] = ['MAGICAL', 'SPOOKY']
    biome_types['thaumcraft:eldritch'] = ['MAGICAL', 'SPOOKY', 'END']
    biome_types['thaumcraft:magical_forest'] = ['MAGICAL', 'FOREST']
    return biome_types


def compute_legacy_biome_metrics(types):
    aura_sum = 0.0
    aura_count = 0
    aspect_counts = {a: 0 for a in PRIMALS}
    null_type_count = 0
    for t in types:
        info = LEGACY_TYPE_INFO.get(t)
        if info is None:
            null_type_count += 1
        else:
            aura_sum += info[0]
            aura_count += 1
            aspect_counts[info[1]] += 1
    aura_mod = (aura_sum / aura_count) if (aura_count > 0 and null_type_count == 0) else 0.5
    n = len(types)
    bias_probs = {}
    uncond_probs = {}
    for a in PRIMALS:
        # When random.nextInt(3) == 0, pick 1 of n types uniformly. If type not in biomeInfo (e.g. RARE/VOID), NPE -> null -> uniform 1/6.
        p_bias = (aspect_counts[a] + null_type_count / 6.0) / n if n > 0 else 1.0 / 6.0
        bias_probs[a] = round(p_bias, 6)
        uncond_probs[a] = round((2.0 / 3.0) * (1.0 / 6.0) + (1.0 / 3.0) * p_bias, 6)
    return aura_mod, aspect_counts, null_type_count, bias_probs, uncond_probs


def load_modern_biomes_and_tags():
    biomes = {}
    tags = {}
    with zipfile.ZipFile(VANILLA_1211_JAR) as z:
        for name in z.namelist():
            if name.startswith('data/minecraft/worldgen/biome/') and name.endswith('.json'):
                bid = 'minecraft:' + Path(name).stem
                biomes[bid] = json.loads(z.read(name))
            elif name.startswith('data/minecraft/tags/worldgen/biome/') and name.endswith('.json'):
                tname = 'minecraft:' + name.split('data/minecraft/tags/worldgen/biome/', 1)[1][:-5]
                tags[tname] = json.loads(z.read(name))['values']
    for p in (PORT_RES / 'data/thaumcraft/worldgen/biome').glob('*.json'):
        biomes['thaumcraft:' + p.stem] = json.loads(p.read_text(encoding='utf-8-sig'))

    resolved_tags = {}

    def resolve_tag(tname):
        if tname in resolved_tags:
            return resolved_tags[tname]
        members = set()
        for entry in tags.get(tname, []):
            val = entry['id'] if isinstance(entry, dict) else entry
            if val.startswith('#'):
                members.update(resolve_tag(val[1:]))
            else:
                members.add(val)
        resolved_tags[tname] = members
        return members

    for tname in list(tags):
        resolve_tag(tname)
    return biomes, resolved_tags


# Exact 42 distinct 1.21.1 biome pools registered in TCCrystalClusterFeature.EXACT_LEGACY_BIOME_POOLS
EXACT_MODERN_BIOME_POOLS = {
    'thaumcraft:magical_forest': ['ordo', 'terra'],
    'thaumcraft:eerie': ['ordo', 'ignis'],
    'thaumcraft:eldritch': ['ordo', 'ignis', 'aer'],
    'minecraft:ocean': ['aqua', 'aqua'],
    'minecraft:deep_ocean': ['aqua', 'aqua'],
    'minecraft:frozen_ocean': ['ordo', 'aqua', 'ordo', 'aqua'],
    'minecraft:river': ['aqua', 'aqua'],
    'minecraft:frozen_river': ['ordo', 'aqua', 'ordo', 'aqua'],
    'minecraft:beach': ['terra'],
    'minecraft:stony_shore': ['terra'],
    'minecraft:snowy_beach': ['ordo', 'terra', 'ordo'],
    'minecraft:plains': ['aer'],
    'minecraft:sunflower_plains': ['aer', None],
    'minecraft:savanna': ['ignis', 'aer', 'aer', 'perditio'],
    'minecraft:savanna_plateau': ['ignis', 'aer', 'aer', 'perditio', None],
    'minecraft:windswept_savanna': ['ignis', 'perditio', 'perditio', 'aer', 'aer', None],
    'minecraft:desert': ['ignis', 'perditio', 'terra'],
    'minecraft:badlands': ['ignis', 'terra', 'perditio'],
    'minecraft:wooded_badlands': ['ignis', 'terra', 'perditio', 'perditio'],
    'minecraft:eroded_badlands': ['ignis', 'perditio', 'perditio', 'aer', None],
    'minecraft:windswept_hills': ['aer', 'aer'],
    'minecraft:windswept_gravelly_hills': ['aer', 'perditio', None],
    'minecraft:windswept_forest': ['aer', 'terra', 'perditio'],
    'minecraft:forest': ['terra'],
    'minecraft:flower_forest': ['terra', 'aer', None],
    'minecraft:birch_forest': ['terra'],
    'minecraft:old_growth_birch_forest': ['terra', 'ordo', 'aer', None],
    'minecraft:dark_forest': ['ignis', 'ordo', 'terra'],
    'minecraft:taiga': ['ordo', 'terra', 'terra'],
    'minecraft:snowy_taiga': ['ordo', 'terra', 'terra', 'ordo'],
    'minecraft:old_growth_pine_taiga': ['ordo', 'terra', 'terra'],
    'minecraft:old_growth_spruce_taiga': ['ordo', 'terra', None],
    'minecraft:swamp': ['aqua', 'perditio'],
    'minecraft:jungle': ['ignis', 'aqua', 'ordo', 'terra'],
    'minecraft:sparse_jungle': ['ignis', 'aqua', 'terra', 'terra', None],
    'minecraft:snowy_plains': ['ordo', 'ordo', 'perditio'],
    'minecraft:ice_spikes': ['ordo', 'ordo', 'aer', None],
    'minecraft:snowy_slopes': ['ordo', 'ordo', 'aer'],
    'minecraft:mushroom_fields': ['ordo', None],
    'minecraft:nether_wastes': ['ignis', 'perditio', 'ignis'],
    'minecraft:the_end': ['ordo', 'perditio', 'aer'],
    'minecraft:the_void': [None],
}

# 1.12.2 hill/mutated sub-biomes that Mojang removed and merged into a parent biome in 1.18+
MERGED_118_SUB_BIOMES = {
    'birch_forest_hills',
    'desert_hills',
    'forest_hills',
    'jungle_hills',
    'mesa_clear_rock',
    'mushroom_island_shore',
    'mutated_birch_forest_hills',
    'mutated_desert',
    'mutated_extreme_hills_with_trees',
    'mutated_jungle',
    'mutated_jungle_edge',
    'mutated_mesa_clear_rock',
    'mutated_mesa_rock',
    'mutated_redwood_taiga_hills',
    'mutated_roofed_forest',
    'mutated_savanna_rock',
    'mutated_swampland',
    'mutated_taiga',
    'mutated_taiga_cold',
    'redwood_taiga_hills',
    'smaller_extreme_hills',
    'taiga_cold_hills',
    'taiga_hills',
}


def compute_modern_biome_metrics(biome_id, biomes, resolved_tags):
    if biome_id in EXACT_MODERN_BIOME_POOLS:
        pool = list(EXACT_MODERN_BIOME_POOLS[biome_id])
    else:
        bdata = biomes[biome_id]
        temp = bdata.get('temperature', 0.5)
        downfall = bdata.get('downfall', 0.5)

        def in_tag(tag_short):
            return biome_id in resolved_tags.get(f'minecraft:{tag_short}', set())

        pool = []
        if in_tag('is_ocean'):
            pool.extend(['aqua', 'aqua'])
        if in_tag('is_river'):
            pool.extend(['aqua', 'aqua'])
        if downfall > 0.85:
            pool.append('aqua')
        if biome_id == 'minecraft:lush_caves':
            pool.append('aqua')
        if temp > 0.85:
            pool.append('ignis')
        if in_tag('is_nether'):
            pool.append('ignis')
        if in_tag('is_badlands'):
            pool.append('ignis')
        if temp < 0.15:
            pool.append('ordo')
        if biome_id in ('minecraft:mushroom_fields', 'thaumcraft:magical_forest'):
            pool.append('ordo')
        if in_tag('is_taiga'):
            pool.append('terra')
        if in_tag('is_forest'):
            pool.append('terra')
        if in_tag('is_jungle'):
            pool.append('terra')
        if in_tag('is_beach') or biome_id == 'minecraft:desert':
            pool.append('terra')
        if in_tag('is_mountain'):
            pool.append('aer')
        if in_tag('is_hill'):
            pool.append('aer')
        if in_tag('is_savanna') or biome_id in ('minecraft:plains', 'minecraft:sunflower_plains'):
            pool.append('aer')
        if downfall < 0.15:
            pool.append('perditio')
        if biome_id in ('minecraft:swamp', 'minecraft:mangrove_swamp', 'minecraft:deep_dark'):
            pool.append('perditio')

    counts = {a: pool.count(a) for a in PRIMALS}
    null_cnt = pool.count(None)
    n = len(pool)
    bias_probs = {}
    uncond_probs = {}
    for a in PRIMALS:
        p_bias = ((counts[a] + null_cnt / 6.0) / n) if n > 0 else (1.0 / 6.0)
        bias_probs[a] = round(p_bias, 6)
        uncond_probs[a] = round((2.0 / 3.0) * (1.0 / 6.0) + (1.0 / 3.0) * p_bias, 6)
    return [p if p is not None else 'null' for p in pool], counts, bias_probs, uncond_probs


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--forge-jar', type=Path, default=DEFAULT_FORGE_1122_JAR)
    parser.add_argument('--output', type=Path, default=ROOT / 'tools/reports/local/worldgen-20261103/contracts')
    args = parser.parse_args()
    args.output.mkdir(parents=True, exist_ok=True)

    biome_types = extract_1122_biomes_and_types(args.forge_jar)
    modern_biomes, modern_tags = load_modern_biomes_and_tags()

    dict_rows = []
    comp_rows = []
    exact_top_match = 0
    exact_dist_match = 0

    for b112, types in sorted(biome_types.items()):
        aura_mod, leg_counts, null_cnt, leg_bias, leg_uncond = compute_legacy_biome_metrics(types)
        modern_id = LEGACY_TO_MODERN_BIOME.get(b112)
        mod_pool, mod_counts, mod_bias, mod_uncond = compute_modern_biome_metrics(modern_id, modern_biomes, modern_tags)

        leg_top = {a for a, v in leg_bias.items() if v == max(leg_bias.values())}
        mod_top = {a for a, v in mod_bias.items() if v == max(mod_bias.values())}
        top_overlap = bool(leg_top & mod_top)
        dist_equal = all(abs(leg_bias[a] - mod_bias[a]) < 1e-4 for a in PRIMALS)
        if top_overlap:
            exact_top_match += 1
        if dist_equal:
            exact_dist_match += 1

        l1_diff = round(sum(abs(leg_bias[a] - mod_bias[a]) for a in PRIMALS), 4)
        dict_rows.append({
            'biome_1122': b112,
            'types_1122': ';'.join(types),
            'unmapped_types_1122': null_cnt,
            'legacy_aura_modifier': round(aura_mod, 4),
            'legacy_aspect_pool': json.dumps(leg_counts, separators=(',', ':')),
            'legacy_bias_probs': json.dumps(leg_bias, separators=(',', ':')),
            'legacy_uncond_probs': json.dumps(leg_uncond, separators=(',', ':')),
        })
        comp_rows.append({
            'biome_1122': b112,
            'modern_1211': modern_id,
            'types_1122': ';'.join(types),
            'modern_pool': ';'.join(mod_pool),
            'legacy_bias_probs': json.dumps(leg_bias, separators=(',', ':')),
            'modern_bias_probs': json.dumps(mod_bias, separators=(',', ':')),
            'top_aspect_overlap': top_overlap,
            'exact_distribution_match': dist_equal,
            'bias_l1_distance': l1_diff,
        })

    with (args.output / 'biome-dictionary-1122.csv').open('w', newline='', encoding='utf-8') as f:
        writer = csv.DictWriter(f, fieldnames=list(dict_rows[0]))
        writer.writeheader()
        writer.writerows(dict_rows)

    with (args.output / 'biome-aspect-comparison.csv').open('w', newline='', encoding='utf-8') as f:
        writer = csv.DictWriter(f, fieldnames=list(comp_rows[0]))
        writer.writeheader()
        writer.writerows(comp_rows)

    distinct_1211_exact = sum(
        1 for r in comp_rows if r['biome_1122'] not in MERGED_118_SUB_BIOMES and r['exact_distribution_match']
    )
    distinct_1211_total = sum(1 for r in comp_rows if r['biome_1122'] not in MERGED_118_SUB_BIOMES)
    summary = {
        'forge_1122_jar': str(args.forge_jar),
        'total_1122_biomes_exported': len(dict_rows),
        'distinct_1211_counterparts_exact_match': f'{distinct_1211_exact}/{distinct_1211_total}',
        'top_aspect_overlap_count': exact_top_match,
        'exact_distribution_match_count': exact_dist_match,
        'merged_118_sub_biomes_note': '23 1.12.2 hill/mutated sub-biomes were merged by Mojang in 1.18 into their parent 1.21.1 biome ID.',
        'divergent_biomes': [r for r in comp_rows if not r['exact_distribution_match']],
    }
    (args.output / 'biome-dictionary-audit.json').write_text(json.dumps(summary, indent=2) + '\n', encoding='utf-8')
    print(f"Exported {len(dict_rows)} 1.12.2 biomes. Distinct 1.21.1 counterparts exact match: "
          f"{distinct_1211_exact}/{distinct_1211_total}; Top-aspect overlap: {exact_top_match}/{len(dict_rows)}; "
          f"Exact bias distribution match across all 65 (including 23 Mojang-merged sub-biomes): {exact_dist_match}/{len(dict_rows)}")


if __name__ == '__main__':
    main()

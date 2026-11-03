"""Read-only source/data contracts and a generated inventory. Does not certify in-game visual parity."""
import argparse
import csv
import hashlib
import json
import math
from pathlib import Path
import re
import zipfile

ROOT = Path(__file__).resolve().parents[3]
RES = ROOT / '05_neoforge_port/src/main/resources'
WG = RES / 'data/thaumcraft/worldgen'
JAVA = ROOT / '05_neoforge_port/src/main/java/thaumcraft'


def load(group, name):
    return json.loads((WG / group / f'{name}.json').read_text(encoding='utf-8-sig'))


def mean_count(value):
    if isinstance(value, int):
        return value
    kind = value['type'].split(':')[-1]
    if kind == 'uniform':
        return (value['min_inclusive'] + value['max_inclusive']) / 2
    if kind == 'weighted_list':
        values = value['distribution']
        return sum(mean_count(x['data']) * x['weight'] for x in values) / sum(x['weight'] for x in values)
    raise ValueError(f'Unreviewed count provider: {kind}')


def expected_calls(placed):
    mean = 1.0
    for modifier in placed['placement']:
        if modifier['type'] in {'minecraft:noise_based_count', 'minecraft:noise_threshold_count', 'minecraft:count_on_every_layer', 'minecraft:carving_mask'}:
            raise ValueError(f"Position/terrain dependent multiplicity: {modifier['type']}")
        if modifier['type'] == 'minecraft:count':
            mean *= mean_count(modifier['count'])
        if modifier['type'] == 'minecraft:rarity_filter':
            mean /= modifier['chance']
    return mean


def strings(value):
    if isinstance(value, str):
        yield value
    elif isinstance(value, dict):
        for nested in value.values():
            yield from strings(nested)
    elif isinstance(value, list):
        for nested in value:
            yield from strings(nested)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--output', type=Path, default=ROOT / 'tools/reports/local/worldgen-20261103/contracts')
    args = parser.parse_args()
    args.output.mkdir(parents=True, exist_ok=True)
    checks = []

    def check(name, result, evidence):
        checks.append({'name': name, 'pass': bool(result), 'evidence': evidence})

    all_files = sorted(WG.rglob('*.json'))
    objects = {p: json.loads(p.read_text(encoding='utf-8-sig')) for p in all_files}
    placed = {p.stem: v for p, v in objects.items() if p.parent.name == 'placed_feature'}
    configured = {p.stem: v for p, v in objects.items() if p.parent.name == 'configured_feature'}
    active = set()
    for path, value in objects.items():
        if path.parent.name == 'biome':
            active.update(x.split(':')[1] for x in strings(value.get('features', [])) if x.startswith('thaumcraft:'))
    modifiers = RES / 'data/thaumcraft/neoforge/biome_modifier'
    for p in modifiers.glob('*.json'):
        value = json.loads(p.read_text())
        if value['type'] == 'neoforge:add_features':
            active.update(x.split(':')[1] for x in value['features'] if x.startswith('thaumcraft:'))
    while True:
        before = set(active)
        for name in list(active):
            feature = placed.get(name, {}).get('feature', '')
            if feature.startswith('thaumcraft:'):
                active.update(s.split(':')[1] for s in strings(configured[feature.split(':')[1]])
                              if s.startswith('thaumcraft:') and s.split(':')[1] in placed)
        if before == active:
            break
    check('active placed references resolve', active <= placed.keys(), sorted(active - placed.keys()))
    for name in ['amber', 'cinnabar', 'quartz']:
        kind = load('configured_feature', f'ore_{name}')['type']
        check(f'{name}: single replacement, not vein', kind == ('thaumcraft:surface_amber_ore' if name == 'amber' else 'minecraft:replace_single_block'), kind)
    check('amber surface sampled first', load('placed_feature', 'ore_amber')['placement'][2]
          == {'type': 'minecraft:heightmap', 'heightmap': 'WORLD_SURFACE_WG'}, load('placed_feature', 'ore_amber'))
    vegetation = load('biome', 'magical_forest')['features'][9]
    check('no duplicate forest flowers', 'minecraft:forest_flowers' not in vegetation, vegetation)
    for name, mean in [('flower_magical_forest', 10), ('patch_grass_magical_forest', 12),
                       ('patch_waterlily_magical_forest', 6), ('patch_vishroom', 8), ('trees_magical_forest', 2.1),
                       ('brown_mushroom_magical_forest', 1.5), ('red_mushroom_magical_forest', .75),
                       ('brown_mushroom_extra', .25), ('red_mushroom_extra', .125), ('crystal_cluster', 1)]:
        actual = expected_calls(placed[name])
        check(f'{name}: outer calls per chunk', math.isclose(actual, mean), actual)
    check('flowers use legacy height anchor', configured['flower_magical_forest']['type'] == 'thaumcraft:legacy_flowers', configured['flower_magical_forest']['type'])
    check('vishroom is eight single probes, not patches', configured['patch_vishroom']['config']['tries'] == 1
          and configured['patch_vishroom']['config']['xz_spread'] == 0 and configured['patch_vishroom']['config']['y_spread'] == 0,
          configured['patch_vishroom']['config']['tries'])
    check('grass patch has 128 probes', configured['patch_grass_magical_forest']['config']['tries'] == 128, 128)
    selector = configured['trees_magical_forest']['config']['features']
    check('conditional tree probabilities', math.isclose(selector[0]['chance'], 1 / 18, rel_tol=1e-6)
          and math.isclose(selector[1]['chance'], 1 / 12, rel_tol=1e-6), selector)
    for name in ['add_mound', 'add_cultist_portals']:
        check(f'{name}: no feature/structure double generation', json.loads((modifiers / f'{name}.json').read_text())['type'] == 'neoforge:none', 'structure sets only')
    structure = load('structure_set', 'mounds')['placement']
    check('mound candidate density 1/100 and useful locate offset', structure['spacing'] == 10
          and structure['locate_offset'] == [15, 0, 15], structure)
    portal = load('structure_set', 'cultist_portals')['placement']
    check('portal marginal chance .99/500', portal['spacing'] == 1 and math.isclose(portal['frequency'], .99 / 500), portal)
    data = (RES / 'data/thaumcraft/structures/ancient_mound.bin').read_bytes()
    coords = [(data[i], data[i + 1] - 1, data[i + 2]) for i in range(0, len(data), 4)]
    dims = [max(p[a] for p in coords) - min(p[a] for p in coords) + 1 for a in range(3)]
    check('mound binary bounds', len(data) % 4 == 0 and dims == [19, 17, 19], {'records': len(coords), 'dimensions': dims})
    source = (JAVA / 'common/world/biome/TCMagicalForestShapeFilter.java').read_text()
    raw = source.split('DILATION_DISK_OFFSETS = {', 1)[1].split('};', 1)[0]
    offsets = {tuple(map(int, pair)) for pair in re.findall(r'\{\s*(-?\d+)\s*,\s*(-?\d+)\s*\}', raw)}
    omitted = []
    for qx in range(4):
        for qz in range(4):
            for cx in range(-5, 6):
                for cz in range(-5, 6):
                    if (qx - (cx * 4 + 2)) ** 2 + (qz - (cz * 4 + 2)) ** 2 <= 180 and (cx, cz) not in offsets:
                        omitted.append([qx, qz, cx, cz])
    check('quart dilation exhaustive candidate coverage', len(offsets) == 49 and not omitted, omitted)
    ambient = (JAVA / 'client/world/TCMagicalForestAmbientClient.java').read_text()
    check('client uses a transmitted heightmap', 'getHeight(Heightmap.Types.WORLD_SURFACE,' in ambient
          and 'getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,' not in ambient, 'Heightmap.Usage.CLIENT')
    check('particle quality is not disabled twice', 'ParticleStatus.MINIMAL' not in ambient, 'Legacy particle engine owns quality/FPS throttling')
    for name in ['greatwood_biomes', 'silverwood_biomes']:
        tag = json.loads((RES / f'data/thaumcraft/tags/worldgen/biome/{name}.json').read_text())
        check(f'{name}: actual nested tag reference', any(isinstance(x, dict) and x['id'] == '#thaumcraft:legacy_magical_spawn_biomes' for x in tag['values']), tag)

    rows = []
    for name, value in placed.items():
        rows.append({'feature': name, 'route': 'ACTIVE' if name in active else 'REFERENCE_ONLY',
                     'expected_outer_calls': expected_calls(value), 'configured': value['feature'],
                     'placement': json.dumps(value['placement'], ensure_ascii=True, separators=(',', ':'))})
    with (args.output / 'placed-feature-inventory.csv').open('w', newline='', encoding='utf-8') as stream:
        writer = csv.DictWriter(stream, fieldnames=list(rows[0]))
        writer.writeheader()
        writer.writerows(rows)
    biome_rows = []
    vanilla_jar = ROOT / '05_neoforge_port/build/moddev/artifacts/neoforge-21.1.265-client-extra-aka-minecraft-resources.jar'
    with zipfile.ZipFile(vanilla_jar) as vanilla:
        for path, value in objects.items():
            if path.parent.name != 'biome':
                continue
            for step, features in enumerate(value['features']):
                for feature in features:
                    if feature.startswith('thaumcraft:'):
                        record = placed[feature.split(':')[1]]
                    else:
                        record = json.loads(vanilla.read(f"data/minecraft/worldgen/placed_feature/{feature.split(':')[1]}.json"))
                    try:
                        calls = expected_calls(record)
                    except (ValueError, KeyError):
                        calls = 'CONDITIONAL_PROVIDER_NOT_ESTIMATED'
                    biome_rows.append({'biome': path.stem, 'step': step, 'feature': feature,
                                       'expected_outer_calls': calls,
                                       'placement': json.dumps(record['placement'], separators=(',', ':'))})
    with (args.output / 'biome-feature-inventory.csv').open('w', newline='', encoding='utf-8') as stream:
        writer = csv.DictWriter(stream, fieldnames=['biome', 'step', 'feature', 'expected_outer_calls', 'placement'])
        writer.writeheader()
        writer.writerows(biome_rows)
    manifest = {}
    inputs = all_files + sorted(modifiers.glob('*.json')) + sorted((JAVA / 'common/world').rglob('*.java'))
    inputs += [JAVA / 'client/world/TCMagicalForestAmbientClient.java', RES / 'data/thaumcraft/structures/ancient_mound.bin']
    legacy = ROOT / '02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common'
    inputs += [legacy / 'world/ThaumcraftWorldGenerator.java', legacy / 'world/biomes/BiomeGenMagicalForest.java',
               legacy / 'world/biomes/BiomeHandler.java', legacy / 'world/objects/WorldGenMound.java',
               legacy / 'world/objects/WorldGenCustomFlowers.java', legacy / 'blocks/world/BlockGrassAmbient.java']
    for p in inputs:
        manifest[p.relative_to(ROOT).as_posix()] = hashlib.sha256(p.read_bytes()).hexdigest()
    report = {'allPass': all(x['pass'] for x in checks), 'passed': sum(x['pass'] for x in checks), 'checks': checks,
              'features': rows, 'sha256': manifest,
              'flower_flat_plane_math': {'surfaceY': 64, 'legacy_expected_valid_probes_before_collisions': 10 * 64 / 96,
                                         'old_modern_surface_anchored_expected_valid_probes': 6 * 64 / 4,
                                         'note': 'Analytic height-only model, NOT measured flowers per natural chunk.'}}
    (args.output / 'contracts.json').write_text(json.dumps(report, indent=2) + '\n', encoding='utf-8')
    print(f"Worldgen contracts: {report['passed']}/{len(checks)}; allPass={report['allPass']}")
    for row in checks:
        if not row['pass']:
            print('FAIL', row['name'], row['evidence'])
    raise SystemExit(0 if report['allPass'] else 1)


if __name__ == '__main__':
    main()

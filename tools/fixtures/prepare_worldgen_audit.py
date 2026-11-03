"""Create NEW NBT fixtures only. Never copies chunks, players or modifies an existing save."""
import argparse
import copy
from pathlib import Path

import nbtlib as n

ROOT = Path(__file__).resolve().parents[2]


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('name')
    parser.add_argument('--seed', type=int, default=61222121)
    parser.add_argument('--ambient', action='store_true')
    args = parser.parse_args()
    prefix = 'tc_worldgen_ambient_audit_' if args.ambient else 'tc_worldgen_audit_'
    if not args.name.startswith(prefix) or not args.name.replace('_', '').isalnum():
        raise ValueError('Disposable world name required')
    saves = ROOT / '05_neoforge_port/run/saves' if args.ambient else ROOT / '05_neoforge_port/run'
    target = saves / args.name
    if target.exists():
        raise FileExistsError(f'Refusing to overwrite {target}')
    source = ROOT / '05_neoforge_port/run/saves/New World/level.dat'
    data = copy.deepcopy(n.load(source)['Data'])
    for key in ['Player', 'DragonFight', 'CustomBossEvents', 'ScheduledEvents', 'ServerBrands']:
        data.pop(key, None)
    settings = data['WorldGenSettings']
    settings['seed'] = n.Long(args.seed)
    settings['generate_features'] = n.Byte(not args.ambient)
    if args.ambient:
        settings['dimensions']['minecraft:overworld']['generator'] = n.parse_nbt('''{
            type:"minecraft:flat",settings:{biome:"thaumcraft:magical_forest",features:0b,lakes:0b,structure_overrides:[],
            layers:[{block:"minecraft:bedrock",height:1},{block:"minecraft:dirt",height:126},{block:"minecraft:grass_block",height:1}]}}''')
    else:
        settings['dimensions']['minecraft:overworld']['generator'] = n.parse_nbt('''{
            type:"minecraft:noise",biome_source:{type:"minecraft:multi_noise",preset:"minecraft:overworld"},
            settings:"minecraft:overworld"}''')
    data['DataPacks'] = n.parse_nbt('{Enabled:["vanilla","mod:neoforge","mod:thaumcraft"],Disabled:[]}')
    data.update({'LevelName': n.String(args.name), 'SpawnX': n.Int(8), 'SpawnY': n.Int(64), 'SpawnZ': n.Int(8),
                 'Time': n.Long(0), 'DayTime': n.Long(18000), 'GameType': n.Int(1), 'allowCommands': n.Byte(1),
                 'initialized': n.Byte(1), 'raining': n.Byte(0), 'thundering': n.Byte(0), 'clearWeatherTime': n.Int(1000000)})
    rules = data.setdefault('GameRules', n.Compound())
    for key, value in {'doDaylightCycle': 'false', 'doWeatherCycle': 'false', 'doMobSpawning': 'false',
                       'randomTickSpeed': '0', 'spawnRadius': '0', 'spawnChunkRadius': '0'}.items():
        rules[key] = n.String(value)
    target.mkdir(parents=True)
    n.File({'Data': data}, gzipped=True).save(target / 'level.dat')
    print(target)


if __name__ == '__main__':
    main()

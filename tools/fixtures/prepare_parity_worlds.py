"""Create independent legacy/modern flat fixtures; never overwrite an existing save.

Requires nbtlib. Minecraft itself builds the stations from version-specific functions.
No numeric mod block IDs or player UUIDs are copied between game versions.
"""
import copy
import json
from pathlib import Path
import struct
import time
import zipfile
import nbtlib as n

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / '07_Test_Instance_and_Comparisons/paired_flat_worlds'
LEGACY = ROOT / '07_Test_Instance_and_Comparisons/01_Classic Thaumcraft 1.12.2 Forge/minecraft-instance/saves'
MODERN = ROOT / '05_neoforge_port/run/saves'
NAMES = {False: 'TC6_PARITY_FLAT_1_12_2', True: 'TC6_PARITY_FLAT_1_21_1'}
SEALS = ['pickup', 'pickup_advanced', 'fill', 'fill_advanced', 'empty', 'empty_advanced',
         'harvest', 'butcher', 'guard', 'guard_advanced', 'lumber', 'breaker', 'use',
         'provider', 'stock', 'breaker_advanced']


def compound(text):
    return n.parse_nbt(text)


def item(modern, name, count=1, tag=None, components=None, damage=0):
    value = n.Compound({'id': n.String(name)})
    if modern:
        value['count'] = n.Int(count)
        if components:
            value['components'] = n.Compound(components)
    else:
        value['Count'] = n.Byte(count)
        value['Damage'] = n.Short(damage)
        if tag:
            value['tag'] = n.Compound(tag)
    return value


def named(modern, name, label, **kwargs):
    value = item(modern, name, **kwargs)
    if modern:
        value.setdefault('components', n.Compound())['minecraft:custom_name'] = n.String(json.dumps({'text': label, 'italic': False}))
    else:
        value.setdefault('tag', n.Compound())['display'] = n.Compound({'Name': n.String(label)})
    return value


def caster(modern, silk, fortune):
    complexity = 2 + (fortune + 1) * 3
    label = 'Exchange ' + ('Silk' if silk else ('Fortune IV' if fortune else 'Normal'))
    if modern:
        nodes = f'0|root|-1|1|;1|thaumcraft.touch|0|2|;2|thaumcraft.exchange|1||fortune={fortune},silk={silk}'
        package = n.Compound({'nodes': n.String(nodes), 'complexity': n.Int(complexity),
                              'color': n.Int(0x578357), 'sorting_hash': n.Int(0)})
        return named(True, 'thaumcraft:caster_basic', label, components={
            'thaumcraft:caster_focus': n.Compound({'focus_item': n.String('thaumcraft:focus_3'),
                'custom_name': n.String(label), 'package': package}),
            'thaumcraft:caster_picked_block': n.List[n.Compound]([n.Compound({
                'slot': n.Int(0), 'item': item(True, 'minecraft:oak_planks')})])})
    nodes = n.List[n.Compound]([compound('{type:"MEDIUM",key:"ROOT"}'),
        compound('{type:"MEDIUM",key:"thaumcraft.TOUCH"}'),
        compound(f'{{type:"EFFECT",key:"thaumcraft.EXCHANGE","setting.fortune":{fortune},"setting.silk":{silk}}}')])
    package = n.Compound({'power': n.Float(1), 'complexity': n.Int(complexity), 'nodes': nodes})
    focus = item(False, 'thaumcraft:focus_3', tag={'package': package})
    return named(False, 'thaumcraft:caster_basic', label, tag={
        'focus': focus, 'picked': item(False, 'minecraft:planks')})


def golem(modern, material, head, arms, legs, addon, label):
    props = struct.unpack('>q', bytes([material, head, arms, legs, addon, 0, 0, 0]))[0]
    data = {'props': n.Long(props), 'xp': n.Int(0)}
    return named(modern, 'thaumcraft:golem', label, **({'components': {'minecraft:custom_data': n.Compound(data)}}
                if modern else {'tag': data}))


def functions(modern):
    plain = 'minecraft:oak_planks' if modern else 'minecraft:planks'
    build = ['# Generated parity fixture: global construction, once per world.', 'kill @e[tag=tc_parity_target]']
    build += [f'gamerule {key} {value}' for key, value in {
        'doDaylightCycle': 'false', 'doWeatherCycle': 'false', 'doMobSpawning': 'false',
        'randomTickSpeed': '0', 'keepInventory': 'true', 'commandBlockOutput': 'false',
        'sendCommandFeedback': 'false', 'spawnRadius': '0'}.items()]
    build += ['time set 6000', 'weather clear', 'setworldspawn 0 64 -12',
              'fill -32 63 -16 32 63 40 minecraft:stone',
              'fill -30 64 -14 30 70 38 minecraft:air']
    # Matching station boundaries, source blocks and observation viewpoints.
    for x1, z1, x2, z2, color, old in [(-28, -2, -2, 14, 'light_blue', 3),
            (2, -2, 28, 14, 'yellow', 4), (-28, 18, -2, 38, 'lime', 5), (2, 18, 28, 38, 'red', 14)]:
        block = f'minecraft:{color}_wool' if modern else f'minecraft:wool {old}'
        for a, b, c, d in [(x1,z1,x2,z1),(x1,z2,x2,z2),(x1,z1,x1,z2),(x2,z1,x2,z2)]:
            build.append(f'fill {a} 63 {b} {c} 63 {d} {block}')
    sources = ['stone', 'cobblestone', 'gravel', 'glass', 'coal_ore', 'iron_ore', 'gold_ore', 'lapis_ore', 'redstone_ore', 'diamond_ore']
    for i, block in enumerate(sources):
        x = -24 + (i % 5) * 4
        z = 3 + (i // 5) * 6
        build.append(f'setblock {x} 64 {z} minecraft:{block}')
    build += ['fill 14 64 6 14 67 11 minecraft:stone',
              'fill 10 64 23 10 67 28 minecraft:stone',
              'fill 21 68 24 27 68 30 minecraft:stone',
              'summon minecraft:zombie 24.5 64 27.5 {NoAI:1b,PersistenceRequired:1b,Silent:1b,Tags:["tc_parity_target"]}',
              'summon minecraft:cow 24.5 64 22.5 {NoAI:1b,PersistenceRequired:1b,Silent:1b,Tags:["tc_parity_target"]}']

    def chest(x, z, label, stacks):
        contents = []
        for slot, stack in enumerate(stacks):
            stack = copy.deepcopy(stack)
            stack['Slot'] = n.Byte(slot)
            contents.append(stack)
        tag = n.Compound({'Items': n.List[n.Compound](contents),
                          'CustomName': n.String(json.dumps({'text': label}) if modern else label)})
        state = 'minecraft:chest[facing=north]' if modern else 'minecraft:chest 2'
        build.append(f'setblock {x} 64 {z} {state} replace {tag.snbt()}')
        if modern:
            # Modern setblock puts block-entity NBT directly after the state.
            build[-1] = f'setblock {x} 64 {z} {state}{tag.snbt()} replace'

    basics = [item(modern, plain, 64), item(modern, 'minecraft:stone', 64),
              item(modern, 'minecraft:gravel', 64), item(modern, 'minecraft:diamond_ore', 64),
              item(modern, 'minecraft:chest', 16), item(modern, 'minecraft:diamond', 16)]
    chest(-26, -7, 'Exchange supplies', basics + [caster(modern, 0, 0), caster(modern, 1, 0), caster(modern, 0, 4)])
    chest(-20, 12, 'Replace me: 4 diamonds', [item(modern, 'minecraft:diamond', 4)])
    configurations = [(0,0,0,0,0,'Wood walker'), (1,1,1,0,0,'Iron smart fine'),
        (2,0,0,2,0,'Clay climber'), (3,1,1,3,0,'Brass flyer'),
        (4,2,2,0,2,'Thaumium fighter'), (5,2,4,0,2,'Void dart fighter')]
    chest(4, -7, 'Golems: place to own', [golem(modern,*c) for c in configurations]
          + [item(modern, 'thaumcraft:golem_bell'), item(modern, 'minecraft:lead', 16)])
    chest(6, 4, 'Transfer source', [item(modern, 'minecraft:iron_ingot', 32), item(modern, 'minecraft:gold_ingot', 16)])
    chest(24, 4, 'Transfer destination', [])
    seal_items = [item(modern, f'thaumcraft:seal_{key}', 4) if modern else
                  item(False, 'thaumcraft:seal', 4, damage=i+1) for i, key in enumerate(SEALS)]
    chest(-26, 17, 'All sixteen seals', seal_items)
    chest(-3, -9, 'ALL SEALS - spare kit', seal_items)
    for i in range(6):
        build.append(f'setblock {-23+i*4} 64 25 minecraft:stone')
    chest(-18, 31, 'Seal inventory', [item(modern, 'minecraft:iron_ingot', 16), item(modern, plain, 32)])
    for x, z, lines in [(-25,-3,['EXCHANGE','Normal / Silk','Fortune IV','Sources ahead']),
            (5,-3,['GOLEMS','Place from chest','Owner = placer','Transfer / wall']),
            (-25,20,['SEALS','16 types in chest','Place on stone','Bell: configure']),
            (5,20,['COMBAT / REVEAL','Zombie under roof','Wall for reveal','Cow for scanning'])]:
        texts = n.List[n.String]([n.String(json.dumps({'text': text})) for text in lines])
        if modern:
            tag = n.Compound({'front_text': n.Compound({'messages': texts}), 'back_text': n.Compound({'messages': texts})})
            build.append(f'setblock {x} 64 {z} minecraft:oak_sign[rotation=8]{tag.snbt()}')
        else:
            tag = n.Compound({f'Text{i+1}': text for i,text in enumerate(texts)})
            build.append(f'setblock {x} 64 {z} minecraft:standing_sign 8 replace {tag.snbt()}')
    build.append('tellraw @a {"text":"TC6 parity stations ready. Fresh research; /function tc_parity:unlock is optional.","color":"gold"}')
    build.append('data modify storage tc_parity:state built set value 1b' if modern else 'gamerule gameLoopFunction tc_parity:players')
    kit = ['gamemode creative @s', 'tp @s 0.5 64 -12.5 0 0', 'spawnpoint @s 0 64 -12']
    starter = [item(modern,'thaumcraft:thaumonomicon'), item(modern,'thaumcraft:thaumometer'),
               item(modern,'thaumcraft:golem_bell'), caster(modern,0,0), caster(modern,1,0), caster(modern,0,4),
               item(modern,plain,64), item(modern,'minecraft:stone',64), item(modern,'minecraft:chest',16)]
    def give_commands(stacks):
        commands = []
        for stack in stacks:
            if modern:
                components = stack.get('components', {})
                suffix = '[' + ','.join(f'{key}={value.snbt()}' for key,value in components.items()) + ']' if components else ''
                commands.append(f'give @s {stack["id"]}{suffix} {int(stack["count"])}')
            else:
                commands.append(f'give @s {stack["id"]} {int(stack["Count"])} {int(stack["Damage"])} {stack.get("tag",n.Compound()).snbt()}')
        return commands
    kit += give_commands(starter + seal_items)
    kit += ['tag @s add tc_parity_ready' if modern else 'scoreboard players tag @s add tc_parity_ready',
            'tellraw @s {"text":"Kit ready. Golems/seals are in labelled chests. Research is intentionally fresh.","color":"green"}']
    tick = ['execute unless data storage tc_parity:state built run function tc_parity:build',
            'execute as @a[tag=!tc_parity_ready] run function tc_parity:kit'] if modern else [
            'execute @a[tag=!tc_parity_ready] ~ ~ ~ function tc_parity:kit']
    unlock = ['tc research @s all' if modern else 'thaumcraft research @s all']
    return {'build': build, 'players': tick, 'kit': kit, 'unlock': unlock, 'seals': give_commands(seal_items),
            'home': ['tp @s 0.5 64 -12.5 0 0'],
            'refill': [line for line in kit if line.startswith('give ')]}


def prepare(modern):
    dest = (MODERN if modern else LEGACY) / NAMES[modern]
    if dest.exists():
        raise FileExistsError(f'Refusing to overwrite save: {dest}')
    dest.mkdir(parents=True)
    if modern:
        data = copy.deepcopy(n.load(MODERN / 'New World/level.dat')['Data'])
        for key in ['Player','DragonFight','CustomBossEvents','ScheduledEvents','ServerBrands']:
            data.pop(key,None)
        settings = data['WorldGenSettings']
        settings['seed'] = n.Long(61222121)
        settings['generate_features'] = n.Byte(0)
        settings['dimensions']['minecraft:overworld']['generator'] = compound('''{
            type:"minecraft:flat",settings:{biome:"minecraft:plains",features:0b,lakes:0b,structure_overrides:[],
            layers:[{block:"minecraft:bedrock",height:1},{block:"minecraft:dirt",height:126},{block:"minecraft:grass_block",height:1}]}}''')
        data['DataPacks'] = compound('{Enabled:["vanilla","mod:neoforge","mod:thaumcraft","file/tc_parity"],Disabled:[]}')
    else:
        data = compound('''{version:19133,DataVersion:1343,Version:{Id:1343,Name:"1.12.2",Snapshot:0b},
            RandomSeed:61222121L,generatorName:"flat",generatorVersion:0,
            generatorOptions:"3;minecraft:bedrock,62*minecraft:dirt,minecraft:grass;1;",
            MapFeatures:0b,BorderCenterX:0d,BorderCenterZ:0d,BorderSize:59999968d}''')
    data.update({'LevelName': n.String(NAMES[modern]), 'SpawnX':n.Int(0), 'SpawnY':n.Int(64), 'SpawnZ':n.Int(-12),
        'Time':n.Long(0), 'DayTime':n.Long(6000), 'LastPlayed':n.Long(int(time.time()*1000)),
        'GameType':n.Int(1), 'allowCommands':n.Byte(1), 'hardcore':n.Byte(0), 'Difficulty':n.Byte(2),
        'initialized':n.Byte(1), 'raining':n.Byte(0), 'thundering':n.Byte(0), 'clearWeatherTime':n.Int(1000000)})
    rules = data.setdefault('GameRules', n.Compound())
    for key,value in {'doDaylightCycle':'false','doWeatherCycle':'false','doMobSpawning':'false',
                      'randomTickSpeed':'0','keepInventory':'true','spawnRadius':'0'}.items():
        rules[key] = n.String(value)
    if not modern:
        rules['gameLoopFunction'] = n.String('tc_parity:build')
    n.File({'Data':data},gzipped=True).save(dest/'level.dat')
    if modern:
        pack = dest/'datapacks/tc_parity'
        folder = pack/'data/tc_parity/function'
        folder.mkdir(parents=True)
        (pack/'pack.mcmeta').write_text(json.dumps({'pack':{'pack_format':48,'description':'TC6 paired flat test fixture'}}),encoding='utf-8')
        tags = pack/'data/minecraft/tags/function'
        tags.mkdir(parents=True)
        (tags/'tick.json').write_text(json.dumps({'values':['tc_parity:players']}),encoding='utf-8')
    else:
        folder = dest/'data/functions/tc_parity'
        folder.mkdir(parents=True)
    for name,lines in functions(modern).items():
        (folder/f'{name}.mcfunction').write_text('\n'.join(lines)+'\n',encoding='utf-8')
    OUT.mkdir(parents=True,exist_ok=True)
    archive = OUT/f'{NAMES[modern]}_PRISTINE.zip'
    if archive.exists():
        raise FileExistsError(f'Refusing to overwrite baseline: {archive}')
    with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED) as z:
        for path in dest.rglob('*'):
            if path.is_file(): z.write(path,path.relative_to(dest.parent))
    assert n.load(dest/'level.dat')['Data']['SpawnY'] == 64
    print(f'CREATED {dest}\nBASELINE {archive}')


if __name__ == '__main__':
    import sys
    if '--refresh-baselines' in sys.argv:
        import gzip
        import io
        for version in (False,True):
            archive = OUT/f'{NAMES[version]}_PRISTINE.zip'
            with zipfile.ZipFile(archive) as z:
                original = {name:z.read(name) for name in z.namelist()}
            prefix = NAMES[version] + ('/datapacks/tc_parity/data/tc_parity/function/' if version else '/data/functions/tc_parity/')
            for name,lines in functions(version).items():
                original[prefix + name + '.mcfunction'] = ('\n'.join(lines)+'\n').encode('utf-8')
            key = NAMES[version] + '/level.dat'
            level = n.File.parse(io.BytesIO(gzip.decompress(original[key])))
            if version:
                layers = level['Data']['WorldGenSettings']['dimensions']['minecraft:overworld']['generator']['settings']['layers']
                layers[1]['height'] = n.Int(126)
                assert sum(int(layer['height']) for layer in layers) - 64 - 1 == 63
            buffer = io.BytesIO()
            level.write(buffer)
            original[key] = gzip.compress(buffer.getvalue())
            with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED) as z:
                for name,content in original.items(): z.writestr(name,content)
        print('Updated pristine baselines only; installed worlds and player progress untouched.')
        raise SystemExit(0)
    if '--refresh-functions' in sys.argv:
        for version in (False,True):
            dest = (MODERN if version else LEGACY)/NAMES[version]
            folder = dest/('datapacks/tc_parity/data/tc_parity/function' if version else 'data/functions/tc_parity')
            generated = functions(version)
            for name,lines in generated.items():
                (folder/f'{name}.mcfunction').write_text('\n'.join(lines)+'\n',encoding='utf-8')
            archive = OUT/f'{NAMES[version]}_PRISTINE.zip'
            with zipfile.ZipFile(archive) as z:
                original = {name:z.read(name) for name in z.namelist()}
            for name,lines in generated.items():
                key = (folder/f'{name}.mcfunction').relative_to(dest.parent).as_posix()
                original[key] = ('\n'.join(lines)+'\n').encode('utf-8')
            with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED) as z:
                for name,content in original.items(): z.writestr(name,content)
        print('Refreshed only this tool\'s fixture functions and pristine archives; level/chunk/player data unchanged.')
        raise SystemExit(0)
    for version in (False,True):
        if ((MODERN if version else LEGACY)/NAMES[version]).exists() or (OUT/f'{NAMES[version]}_PRISTINE.zip').exists():
            raise SystemExit('Existing parity world/baseline found; preserve it and use a fresh name instead.')
    for version in (False,True): prepare(version)

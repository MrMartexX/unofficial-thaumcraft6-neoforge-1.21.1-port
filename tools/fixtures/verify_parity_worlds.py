"""Read real saved region NBT and verify the paired fixture inventory contracts."""
import io
import struct
import sys
import zipfile
import zlib
import nbtlib as n
import prepare_parity_worlds as settings


def chunk(world, x, z):
    cx, cz = x // 16, z // 16
    region = world / 'region' / f'r.{cx // 32}.{cz // 32}.mca'
    with region.open('rb') as stream:
        stream.seek(4 * ((cx % 32) + 32 * (cz % 32)))
        location = int.from_bytes(stream.read(4), 'big')
        assert location >> 8, f'Missing chunk: {cx},{cz}'
        stream.seek((location >> 8) * 4096)
        size = struct.unpack('>I', stream.read(4))[0]
        compression = stream.read(1)
        assert compression == b'\x02', f'Unexpected region compression: {compression}'
        return n.File.parse(io.BytesIO(zlib.decompress(stream.read(size - 1))))


def chest(world, modern, x, z):
    value = chunk(world, x, z)
    if not modern:
        value = value['Level']
    entities = value['block_entities' if modern else 'TileEntities']
    matches = [e for e in entities if int(e['x']) == x and int(e['y']) == 64 and int(e['z']) == z]
    assert len(matches) == 1, f'Chest absent at {x},64,{z}'
    return matches[0]['Items']


def verify():
    props = []
    for modern in (False, True):
        world = (settings.MODERN if modern else settings.LEGACY) / settings.NAMES[modern]
        data = n.load(world / 'level.dat')['Data']
        assert int(data['SpawnY']) == 64
        if modern:
            aura = n.load(world / 'data/thaumcraft_aura.dat')['data']['chunks']
            local = [entry for entry in aura if int(entry['x']) == -2 and int(entry['z']) == 0]
            assert len(local) == 1 and float(local[0]['vis']) > 3, 'No usable Exchange aura'
        else:
            assert float(chunk(world, -24, 3)['Thaumcraft']['vis']) > 3, 'No usable legacy Exchange aura'
            assert len(data['Player']['Inventory']) >= 9, 'Legacy starter kit was not saved'
        supplies = chest(world, modern, -26, -7)
        assert len(supplies) == 9
        casters = [s for s in supplies if str(s['id']) == 'thaumcraft:caster_basic']
        assert len(casters) == 3
        for caster in casters:
            if modern:
                components = caster['components']
                assert str(components['thaumcraft:caster_picked_block'][0]['item']['id']) == 'minecraft:oak_planks'
                assert components['thaumcraft:caster_focus']['package']['nodes']
            else:
                assert str(caster['tag']['picked']['id']) == 'minecraft:planks'
                assert len(caster['tag']['focus']['tag']['package']['nodes']) == 3
        seals = chest(world, modern, -26, 17)
        assert len(seals) == 16
        for i, seal in enumerate(seals):
            assert str(seal['id']) == (f'thaumcraft:seal_{settings.SEALS[i]}' if modern else 'thaumcraft:seal')
            if not modern:
                assert int(seal['Damage']) == i + 1
        golems = chest(world, modern, 4, -7)
        assert len(golems) == 8
        values = [int(s['components']['minecraft:custom_data']['props'] if modern else s['tag']['props']) for s in golems[:6]]
        props.append(values)
        diamonds = chest(world, modern, -20, 12)
        assert len(diamonds) == 1 and str(diamonds[0]['id']) == 'minecraft:diamond'
        assert int(diamonds[0]['count' if modern else 'Count']) == 4
        assert len(chest(world, modern, 6, 4)) == 2
        assert len(chest(world, modern, 24, 4)) == 0
        with zipfile.ZipFile(settings.OUT / f'{settings.NAMES[modern]}_PRISTINE.zip') as archive:
            assert archive.testzip() is None
        if '--snapshot' in sys.argv:
            path = settings.OUT / f'{settings.NAMES[modern]}_READY.zip'
            with zipfile.ZipFile(path, 'x', zipfile.ZIP_DEFLATED) as archive:
                for file in world.rglob('*'):
                    if file.is_file() and file.name not in ('session.lock', 'level.dat_old'):
                        archive.write(file, file.relative_to(world.parent))
            print('READY SNAPSHOT:', path)
        print(f'PASS {settings.NAMES[modern]}: real saved chests, 3 configured casters, 16 seals, 6 golems, 4 chest diamonds, valid pristine ZIP')
    assert props[0] == props[1], 'Golem packed properties differ between versions'
    print('PASS matching golem material/head/arms/legs/addon/rank bytes between versions')


if __name__ == '__main__':
    verify()

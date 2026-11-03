"""Validate pristine setup independently of old, intentionally preserved saves."""
import gzip
import io
import zipfile
import nbtlib as n
import prepare_parity_worlds as settings


def verify():
    for modern in (False, True):
        name = settings.NAMES[modern]
        with zipfile.ZipFile(settings.OUT / f'{name}_PRISTINE.zip') as archive:
            assert archive.testzip() is None
            data = n.File.parse(io.BytesIO(gzip.decompress(archive.read(name + '/level.dat'))))['Data']
            if modern:
                layers = data['WorldGenSettings']['dimensions']['minecraft:overworld']['generator']['settings']['layers']
                surface = -64 + sum(int(layer['height']) for layer in layers) - 1
                prefix = name + '/datapacks/tc_parity/data/tc_parity/function/'
            else:
                assert data['generatorOptions'] == '3;minecraft:bedrock,62*minecraft:dirt,minecraft:grass;1;'
                surface = 0 + 1 + 62 + 1 - 1
                prefix = name + '/data/functions/tc_parity/'
                assert data['GameRules']['gameLoopFunction'] == 'tc_parity:build'
            assert surface == 63, f'{name}: actual flat surface would be Y={surface}'
            seals = archive.read(prefix + 'seals.mcfunction').decode('utf-8').splitlines()
            assert len(seals) == 16 and all(line.startswith('give @s thaumcraft:seal') for line in seals)
            kit = archive.read(prefix + 'kit.mcfunction').decode('utf-8').splitlines()
            assert sum(line.startswith('give ') for line in kit) == 25
            assert all(command in kit for command in seals)
            build = archive.read(prefix + 'build.mcfunction').decode('utf-8')
            assert 'setblock -3 64 -9 minecraft:chest' in build and 'ALL SEALS - spare kit' in build
            assert 'Player' not in data
            assert not any('/region/' in entry or '/playerdata/' in entry for entry in archive.namelist())
            print(f'PASS {name}: surfaceY=63, 16 seal give commands, 25 starter stacks, spawn seal chest, clean player/world reset')


if __name__ == '__main__':
    verify()

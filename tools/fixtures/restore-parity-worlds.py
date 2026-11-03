"""Restore BOTH pristine fixtures under NEW names, preserving all existing saves."""
from datetime import datetime
from pathlib import Path
import zipfile
import nbtlib as n
import prepare_parity_worlds as settings


def restore():
    stamp = datetime.now().strftime('%Y%m%d_%H%M%S_%f')
    for modern in (False, True):
        source = settings.OUT / f'{settings.NAMES[modern]}_PRISTINE.zip'
        parent = settings.MODERN if modern else settings.LEGACY
        name = settings.NAMES[modern] + '_RESET_' + stamp
        dest = parent / name
        dest.mkdir()
        with zipfile.ZipFile(source) as archive:
            for entry in archive.infolist():
                parts = Path(entry.filename).parts
                if not parts or parts[0] != settings.NAMES[modern]:
                    raise ValueError('Unexpected archive root')
                target = dest.joinpath(*parts[1:]).resolve()
                if target != dest.resolve() and dest.resolve() not in target.parents:
                    raise ValueError('Unsafe archive path')
                if entry.is_dir():
                    target.mkdir(parents=True, exist_ok=True)
                else:
                    target.parent.mkdir(parents=True, exist_ok=True)
                    target.write_bytes(archive.read(entry))
        level = n.load(dest / 'level.dat')
        level['Data']['LevelName'] = n.String(name)
        level.save()
        print('RESTORED NEW COPY:', dest)


if __name__ == '__main__':
    restore()

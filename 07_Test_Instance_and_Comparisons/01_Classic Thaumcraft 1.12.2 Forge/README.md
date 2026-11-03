# Classic Thaumcraft 1.12.2 Forge

Purpose: reference instance for comparing original Thaumcraft 6 behavior and visuals.

## Contents

- `minecraft-instance/mods/Thaumcraft-1.12.2-6.1.BETA26.jar`
- `minecraft-instance/mods/Baubles-1.12-1.5.2.jar`
- `minecraft-instance/mods/jei_1.12.2-4.16.1.302.jar` (optional recipe viewer, restored 2026-11-03).
- `reference-jars/` keeps another copy of the original jars for extraction/decompilation/reference.

## How To Launch

Forge `1.12.2-14.23.5.2860` has been installed into the official Minecraft Launcher.

The restored installation is named `Thaumcraft 6 - Legacy reference 1.12.2`.
It explicitly selects the launcher's Java 8 runtime, not the port's Java 21.
If it disappears again, close the launcher and run
`tools/ci/restore-legacy-launcher-profile.ps1` from the repository.
Use `-InstallJei` to download the pinned Forge 1.12.2 JEI release from Modrinth
and verify its SHA-512 before installing. Launcher settings are backed up before
the named profile is updated; other installations and saves are preserved.

In the official launcher:
1. Restart the launcher if it was already open.
2. Open `Installations`.
3. Create a new installation or edit the `forge` installation.
4. Set Version to `1.12.2-forge-14.23.5.2860`.
5. Set Game Directory to:
   `D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\01_Classic Thaumcraft 1.12.2 Forge\minecraft-instance`
6. Set JVM memory if needed, for example `-Xmx4G`.
7. Launch that profile.

In Prism/MultiMC:
1. Create a Minecraft 1.12.2 instance.
2. Install Forge `14.23.5.2860`.
3. Copy or point the instance mods folder to `minecraft-instance/mods`.

Do not add our 1.21.1 port jar here. This folder is only for the original baseline.

## Verification On 2026-11-03

- Original BETA26 jar matches `01_original_jar` by SHA-256:
  `9425f8643581b27ff8845b087c8bc6fc10425a32942f1a3f0e265ce6b38f7b5f`.
- Forge 2860, Windows Java libraries, native DLLs, client jar and asset index exist.
- A separate offline diagnostic launch reached `Forge Mod Loader has successfully
  loaded 7 mods` and `Starting JEI took 1.421 s`. It did not open a world.
- The diagnostic wrapper timed out because it expected a different JEI log
  phrase. Thus this is verified mod initialization, not an automated rendered-title
  or gameplay acceptance test. Only that diagnostic Java process was stopped.
- The untouched original jar logs unsupported OBJ material fields and missing
  texture references including `scanner.png.png` / `scanscreen.png.png` and old
  absolute material paths. Do not silently repair the reference jar to hide these
  messages; inspect actual visuals against the port.
- The normal launcher profile uses the player's normal launcher login. No account
  credentials are stored in the repository or in the restoration script.

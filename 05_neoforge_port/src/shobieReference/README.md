# Shobie 1.20.1 Reference Import

This tree is an isolated reference copy from `ShobieShy/Thaumcraft-6-Source-Code-1.20.1`.

It is intentionally not part of the active Gradle `main` source set. Do not move
these files into `src/main/java` or `src/main/resources` by bulk copy.

Purpose:

- preserve the full Shobie Java source and resources inside the experimental
  merge branch;
- make worldgen, biome modifiers, shaders, OBJ models, research files and
  behavior classes easy to inspect while porting;
- keep the NeoForge 1.21.1 port buildable while subsystem-specific migration is
  still incomplete.

Active runtime imports from this pass live under:

- `src/main/resources/data/thaumcraft/recipe/unsafe_shobie/`

Those JSON files were translated to the current 1.21.1 recipe serializers. Raw
1.20.1 Java, worldgen and old plural datapack paths in this reference tree are
not runtime content.

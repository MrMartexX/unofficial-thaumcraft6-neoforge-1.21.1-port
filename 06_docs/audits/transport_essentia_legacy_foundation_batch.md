# Legacy-aligned transport/essentia foundation batch

This batch starts the real transport/essentia subsystem from TC6 legacy source shapes instead of inventing a new design.

## Legacy source snapshot

Pinned legacy source commit:

`954022bb777b7546281fb36df8522f0ba6b43f81`

Downloaded source files:

- `src/main/java/thaumcraft/api/aspects/IEssentiaTransport.java`
- `src/main/java/thaumcraft/common/tiles/essentia/TileTube.java`
- `src/main/java/thaumcraft/common/tiles/essentia/TileTubeValve.java`
- `src/main/java/thaumcraft/common/tiles/essentia/TileTubeBuffer.java`
- `src/main/java/thaumcraft/common/tiles/essentia/TileTubeOneway.java`
- `src/main/java/thaumcraft/common/tiles/essentia/TileTubeFilter.java`
- `src/main/java/thaumcraft/common/tiles/essentia/TileTubeRestrict.java`
- `src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java`
- `src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java`
- `src/main/java/thaumcraft/common/blocks/essentia/BlockSmelter.java`
- `src/main/java/thaumcraft/common/blocks/essentia/BlockSmelterAux.java`

## Modern foundation classes added

- `TCEssentiaStack`
- `TCEssentiaSuction`
- `TCEssentiaStorage`
- `TCMutableEssentiaStorage`
- `TCEssentiaTubeMode`
- `TCEssentiaTransport`
- `TCLegacyEssentiaTransportNode`
- `TCLegacyEssentiaNetwork`

## Preserved legacy concepts

- directional connectability;
- input and output side checks;
- suction type and suction amount;
- minimum suction;
- filter aspect;
- valve open/closed state;
- one-way output face;
- buffer-like local storage;
- suction-driven transfer rule.

## Not included yet

This is not the final block/blockentity implementation. The next batch should wire this foundation into actual blocks and block entities:

- tube blockentity;
- tube variants;
- smelter endpoint blockentity;
- world tick updates;
- NBT/component persistence;
- client/server sync;
- renderer parity.

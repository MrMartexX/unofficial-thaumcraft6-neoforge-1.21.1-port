package thaumcraft.common.golems;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixers;

/** Diagnostic authoring tool. DataFixer results, not name heuristics, define flattened item families. */
final class TCSealLegacyFamilyExporter {
    // Source: the literal legacy IDs in Mojang's ItemStackTheFlatteningFix table.
    private static final String IDS = "anvil banner bed boat brick_block carpet coal cobblestone_wall concrete concrete_powder cooked_fish deadbush dye dirt double_plant end_bricks fence fence_gate firework_charge fireworks fish golden_apple golden_rail grass hardened_clay yellow_flower leaves leaves2 lit_pumpkin log log2 magma monster_egg nether_brick netherbrick noteblock planks prismarine pumpkin purpur_slab quartz_block quartz_ore record_11 record_13 record_blocks record_cat record_chirp record_far record_mall record_mellohi record_stal record_strad record_wait record_ward red_flower red_nether_brick red_sandstone reeds sand sandstone sapling silver_glazed_terracotta silver_shulker_box skull slime snow snow_layer sponge stained_glass stained_glass_pane stained_hardened_clay stone stone_slab stone_slab2 stone_stairs stonebrick tallgrass trapdoor waterlily web wooden_button wooden_door wooden_pressure_plate wooden_slab wool";

    static void exportIfRequested() {
        String destination = System.getProperty("tc.sealMetadataExport", "");
        if (destination.isBlank()) return;
        JsonObject families = new JsonObject();
        for (String legacy : IDS.split(" ")) {
            TreeSet<String> modern = new TreeSet<>();
            for (int meta = 0; meta < 16; meta++) {
                CompoundTag old = new CompoundTag();
                old.putString("id", "minecraft:" + legacy);
                old.putByte("Count", (byte)1);
                old.putShort("Damage", (short)meta);
                CompoundTag fixed = (CompoundTag)DataFixers.getDataFixer().update(net.minecraft.util.datafix.fixes.References.ITEM_STACK,
                        new com.mojang.serialization.Dynamic<>(net.minecraft.nbt.NbtOps.INSTANCE, old), 1343,
                        net.minecraft.SharedConstants.getCurrentVersion().getDataVersion().getVersion()).getValue();
                ResourceLocation id = ResourceLocation.parse(fixed.getString("id"));
                if (!BuiltInRegistries.ITEM.containsKey(id)) throw new IllegalStateException("Unresolved DFU item " + fixed);
                modern.add(id.toString());
            }
            if (modern.size() > 1) {
                JsonArray values = new JsonArray();
                modern.forEach(values::add);
                families.add("minecraft:" + legacy, values);
            }
        }
        JsonObject root = new JsonObject();
        root.addProperty("source_data_version", 1343);
        root.addProperty("target_data_version", net.minecraft.SharedConstants.getCurrentVersion().getDataVersion().getVersion());
        root.addProperty("source", "Mojang ItemStackTheFlatteningFix legacy IDs, full ITEM_STACK DataFixer to target version");
        root.add("families", families);
        try {
            Path path = Path.of(destination);
            Files.createDirectories(path.toAbsolutePath().getParent());
            Files.writeString(path, new GsonBuilder().setPrettyPrinting().create().toJson(root) + "\n");
        } catch (java.io.IOException exception) { throw new IllegalStateException("Cannot export seal item families", exception); }
    }
}

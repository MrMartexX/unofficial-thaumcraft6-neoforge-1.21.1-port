import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.world.gen.NoiseGeneratorSimplex;

/**
 * Executes the EXACT 1.12.2 Minecraft NoiseGeneratorSimplex and BiomeGenMagicalForest.pickRandomFlower formula
 * against the 1.12.2 forge jar to produce an authoritative reference JSON on a fixed grid of coordinates.
 */
public class generate_legacy_flower_reference {
    private static final String[] FLOWER_NAMES = {
        "minecraft:dandelion",
        "minecraft:poppy",
        "minecraft:blue_orchid",
        "minecraft:allium",
        "minecraft:azure_bluet",
        "minecraft:red_tulip",
        "minecraft:orange_tulip",
        "minecraft:white_tulip",
        "minecraft:pink_tulip",
        "minecraft:oxeye_daisy"
    };

    private static double clamp(double num, double min, double max) {
        if (num < min) return min;
        return num > max ? max : num;
    }

    public static void main(String[] args) throws IOException {
        NoiseGeneratorSimplex noise = new NoiseGeneratorSimplex(new Random(2345L));

        // Fixed grid of coordinates:
        // - Center, positive, negative, distant coordinates
        // - Boundaries around multiples of 48.0 (where x / 48.0 passes integer/step transitions)
        // - Boundaries around noise contour thresholds
        int[] baseCoords = {
            -4096, -2048, -1024, -512, -256, -128, -96, -49, -48, -47, -24, -1, 0, 1, 24, 47, 48, 49, 96, 128, 256, 512, 1024, 2048, 4096
        };

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"description\": \"Authoritative 1.12.2 BiomeGenMagicalForest flower selection on fixed coordinate grid\",\n");
        json.append("  \"noise_seed\": 2345,\n");
        json.append("  \"scale\": \"1 / 48.0\",\n");
        json.append("  \"points\": [\n");

        boolean first = true;
        int count = 0;
        for (int x : baseCoords) {
            for (int z : baseCoords) {
                double rawVal = noise.getValue((double) x / 48.0D, (double) z / 48.0D);
                double d0 = clamp((1.0D + rawVal) / 2.0D, 0.0D, 0.9999D);
                int index = (int) (d0 * (double) FLOWER_NAMES.length);
                String flower = FLOWER_NAMES[index];

                if (!first) {
                    json.append(",\n");
                }
                first = false;
                json.append(String.format(Locale.ROOT, "    {\"x\": %d, \"z\": %d, \"raw\": %.10f, \"d0\": %.10f, \"index\": %d, \"flower\": \"%s\"}",
                        x, z, rawVal, d0, index, flower));
                count++;
            }
        }

        json.append("\n  ],\n");
        json.append("  \"count\": ").append(count).append("\n");
        json.append("}\n");

        String outputPath = args.length > 0 ? args[0] : "tools/reports/local/worldgen-20261103/legacy_flower_grid_reference.json";
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(json.toString());
        }
        System.out.println("Generated " + count + " 1.12.2 flower reference points -> " + outputPath);
    }
}

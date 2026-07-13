package thaumcraft.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class TCConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_BOOTSTRAP_LOGGING = BUILDER
            .comment("Enables extra logging for the clean NeoForge bootstrap.")
            .define("enableBootstrapLogging", true);

    public static final ModConfigSpec.BooleanValue ENABLE_AURA_DEBUG_COMMANDS = BUILDER
            .comment("Registers permission-level-2 Thaumcraft aura debug commands for port validation.")
            .define("enableAuraDebugCommands", true);

    public static final ModConfigSpec.IntValue AURA_DEBUG_DEFAULT_BASE = BUILDER
            .comment("Default base and starting vis used by /thaumcraft aura seed when no base is supplied.")
            .defineInRange("auraDebugDefaultBase", 500, 0, 500);

    public static final ModConfigSpec.BooleanValue GENERATE_AURA = BUILDER
            .comment("Automatically initializes Thaumcraft aura data for loaded chunks.")
            .define("generateAura", true);


    public static final ModConfigSpec.BooleanValue ENABLE_KNOWLEDGE_DEBUG_COMMANDS = BUILDER
            .comment("Registers permission-level-2 Thaumcraft knowledge and research debug commands for port validation.")
            .define("enableKnowledgeDebugCommands", true);

    public static final ModConfigSpec.BooleanValue ENABLE_SCANNING_DEBUG_COMMANDS = BUILDER
            .comment("Registers permission-level-2 Thaumcraft scanning debug commands for port validation.")
            .define("enableScanningDebugCommands", true);

    public static final ModConfigSpec.BooleanValue ENABLE_WARP_DEBUG_COMMANDS = BUILDER
            .comment("Registers permission-level-2 Thaumcraft warp debug commands for port validation.")
            .define("enableWarpDebugCommands", true);

    public static final ModConfigSpec.BooleanValue WUSS_MODE = BUILDER
            .comment("Matches the legacy Thaumcraft wussMode option: research progression does not award warp.")
            .define("wussMode", false);

    public static final ModConfigSpec.DoubleValue TAINT_SPREAD_RATE = BUILDER
            .comment("Legacy Thaumcraft CONFIG_WORLD.taintSpreadRate. 100 means normal TC6 spread rate.")
            .defineInRange("taintSpreadRate", 100.0D, 0.0D, 1000.0D);

    public static final ModConfigSpec.IntValue TAINT_SPREAD_AREA = BUILDER
            .comment("Legacy Thaumcraft CONFIG_WORLD.taintSpreadArea radius used by Taint Seeds.")
            .defineInRange("taintSpreadArea", 32, 1, 256);

    public static final ModConfigSpec.BooleanValue ALLOW_SPAWN_WISP = BUILDER
            .comment("Legacy Thaumcraft CONFIG_WORLD.allowSpawnWisp. Disables natural Wisp spawn placement when false; rift-spawned Wisps are not controlled by this flag.")
            .define("allowSpawnWisp", true);

    public static final ModConfigSpec.BooleanValue ALLOW_SPAWN_ANGRY_ZOMBIE = BUILDER
            .comment("Legacy Thaumcraft CONFIG_WORLD.allowSpawnAngryZombie. Controls the natural Angry Zombie spawn row; Eerie-biome Angry/Furious Zombie rows remain biome-subsystem gated.")
            .define("allowSpawnAngryZombie", true);

    public static final ModConfigSpec.BooleanValue ALLOW_SPAWN_FIREBAT = BUILDER
            .comment("Legacy Thaumcraft CONFIG_WORLD.allowSpawnFireBat. Controls natural Nether Firebat spawns and the Oct 31 overworld Firebat row.")
            .define("allowSpawnFireBat", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private TCConfig() {
    }
}

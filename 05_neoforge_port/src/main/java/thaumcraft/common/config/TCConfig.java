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

    public static final ModConfigSpec SPEC = BUILDER.build();

    private TCConfig() {
    }
}

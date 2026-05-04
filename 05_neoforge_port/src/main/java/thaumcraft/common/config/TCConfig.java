package thaumcraft.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class TCConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_BOOTSTRAP_LOGGING = BUILDER
            .comment("Enables extra logging for the clean NeoForge bootstrap.")
            .define("enableBootstrapLogging", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private TCConfig() {
    }
}

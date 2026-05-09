package thaumcraft.common.world.aura;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import thaumcraft.common.config.TCConfig;

public final class TCAuraDebugCommands {
    private TCAuraDebugCommands() {
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if (!TCConfig.ENABLE_AURA_DEBUG_COMMANDS.get()) {
            return;
        }

        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("thaumcraft")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("aura")
                        .then(Commands.literal("get")
                                .executes(TCAuraDebugCommands::getAura))
                        .then(Commands.literal("stats")
                                .executes(TCAuraDebugCommands::getAuraStats))
                        .then(Commands.literal("sync")
                                .executes(TCAuraDebugCommands::syncAura))
                        .then(Commands.literal("seed")
                                .executes(context -> seedAura(context, TCConfig.AURA_DEBUG_DEFAULT_BASE.get()))
                                .then(Commands.argument("base", IntegerArgumentType.integer(0, AuraHandler.AURA_CEILING))
                                        .executes(context -> seedAura(context, IntegerArgumentType.getInteger(context, "base")))))
                        .then(Commands.literal("add_vis")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.0F, AuraChunk.VALUE_CEILING))
                                        .executes(context -> addVis(context, FloatArgumentType.getFloat(context, "amount")))))
                        .then(Commands.literal("add_flux")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.0F, AuraChunk.VALUE_CEILING))
                                        .executes(context -> addFlux(context, FloatArgumentType.getFloat(context, "amount")))))
                        .then(Commands.literal("drain_vis")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.0F, AuraChunk.VALUE_CEILING))
                                        .executes(context -> drainVis(context, FloatArgumentType.getFloat(context, "amount")))))
                        .then(Commands.literal("drain_flux")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.0F, AuraChunk.VALUE_CEILING))
                                        .executes(context -> drainFlux(context, FloatArgumentType.getFloat(context, "amount")))))));
    }

    private static int getAura(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Optional<AuraChunk> chunk = AuraHandler.getAuraChunk(source.getLevel(), sourceBlockPos(source));
        if (chunk.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No Thaumcraft aura chunk is initialized here."), false);
            return 0;
        }
        source.sendSuccess(() -> Component.literal(format(chunk.get())), false);
        return 1;
    }

    private static int getAuraStats(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        AuraHandler.AuraStats stats = AuraHandler.getAuraStats(source.getLevel());
        source.sendSuccess(() -> Component.literal(
                "Thaumcraft aura stats: savedChunks=" + stats.savedChunks() + ", loadedChunks=" + stats.loadedChunks()
        ), false);
        return stats.loadedChunks();
    }

    private static int syncAura(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Aura sync requires a player command source."));
            return 0;
        }
        boolean sent = TCAuraNetwork.sendAuraToPlayer(player, sourceBlockPos(source));
        source.sendSuccess(() -> Component.literal(sent ? "Sent current aura to client cache." : "No aura sent."), false);
        return sent ? 1 : 0;
    }

    private static int seedAura(CommandContext<CommandSourceStack> context, int base) {
        CommandSourceStack source = context.getSource();
        AuraChunk chunk = AuraHandler.seedAuraChunk(source.getLevel(), sourceBlockPos(source), base);
        source.sendSuccess(() -> Component.literal("Seeded " + format(chunk)), true);
        return 1;
    }

    private static int addVis(CommandContext<CommandSourceStack> context, float amount) {
        CommandSourceStack source = context.getSource();
        AuraHandler.addVis(source.getLevel(), sourceBlockPos(source), amount);
        source.sendSuccess(() -> Component.literal("Added vis: " + amount), true);
        return getAura(context);
    }

    private static int addFlux(CommandContext<CommandSourceStack> context, float amount) {
        CommandSourceStack source = context.getSource();
        AuraHandler.addFlux(source.getLevel(), sourceBlockPos(source), amount, false);
        source.sendSuccess(() -> Component.literal("Added flux: " + amount), true);
        return getAura(context);
    }

    private static int drainVis(CommandContext<CommandSourceStack> context, float amount) {
        CommandSourceStack source = context.getSource();
        float drained = AuraHandler.drainVis(source.getLevel(), sourceBlockPos(source), amount, false);
        source.sendSuccess(() -> Component.literal("Drained vis: " + drained), true);
        return getAura(context);
    }

    private static int drainFlux(CommandContext<CommandSourceStack> context, float amount) {
        CommandSourceStack source = context.getSource();
        float drained = AuraHandler.drainFlux(source.getLevel(), sourceBlockPos(source), amount, false);
        source.sendSuccess(() -> Component.literal("Drained flux: " + drained), true);
        return getAura(context);
    }

    private static BlockPos sourceBlockPos(CommandSourceStack source) {
        return BlockPos.containing(source.getPosition());
    }

    private static String format(AuraChunk chunk) {
        return String.format(
                Locale.ROOT,
                "Aura chunk [%d, %d]: base=%d vis=%.2f flux=%.2f",
                chunk.getChunkX(),
                chunk.getChunkZ(),
                chunk.getBase(),
                chunk.getVis(),
                chunk.getFlux()
        );
    }
}

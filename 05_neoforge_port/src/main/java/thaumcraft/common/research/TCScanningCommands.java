package thaumcraft.common.research;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import java.io.IOException;
import java.nio.file.Path;
import java.util.StringJoiner;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.TCConfig;

public final class TCScanningCommands {
    private static final String[] ROOT_ALIASES = {"thaumcraft", "thaum", "tc"};

    private TCScanningCommands() {
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if (!TCConfig.ENABLE_SCANNING_DEBUG_COMMANDS.get()) {
            return;
        }

        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        for (String root : ROOT_ALIASES) {
            dispatcher.register(Commands.literal(root)
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.literal("scan")
                            .then(Commands.literal("held")
                                    .executes(TCScanningCommands::scanHeld))
                            .then(Commands.literal("test_held")
                                    .executes(TCScanningCommands::scanHeld))
                            .then(Commands.literal("looking")
                                    .executes(TCScanningCommands::scanLooking))
                            .then(Commands.literal("test_looking")
                                    .executes(TCScanningCommands::scanLooking))
                            .then(Commands.literal("audit_items")
                                    .executes(TCScanningCommands::auditItems))
                            .then(Commands.literal("audit_entities")
                                    .executes(TCScanningCommands::auditEntities))));
        }
    }

    private static int scanHeld(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        TCScanResult result = TCScanningManager.scanHeld(player);
        return sendScanResult(context.getSource(), result, "held item");
    }

    private static int scanLooking(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        TCScanResult result = TCScanningManager.scanLooking(player);
        return sendScanResult(context.getSource(), result, "look target");
    }

    private static int auditItems(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        try {
            Path path = TCScanningAuditExporter.dumpItemAudit(player);
            context.getSource().sendSuccess(() -> Component.literal("Thaumcraft scan item audit written to " + path), false);
            return 1;
        } catch (IOException e) {
            context.getSource().sendFailure(Component.literal("Thaumcraft scan item audit failed: " + e.getMessage()));
            return 0;
        }
    }

    private static int auditEntities(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        try {
            Path path = TCScanningAuditExporter.dumpEntityAudit(player);
            context.getSource().sendSuccess(() -> Component.literal("Thaumcraft scan entity audit written to " + path), false);
            return 1;
        } catch (IOException e) {
            context.getSource().sendFailure(Component.literal("Thaumcraft scan entity audit failed: " + e.getMessage()));
            return 0;
        }
    }

    private static int sendScanResult(CommandSourceStack source, TCScanResult result, String scanType) {
        if (!result.success()) {
            source.sendFailure(Component.literal("Thaumcraft scan failed for " + scanType + ": " + result.message()));
            return 0;
        }

        String aspects = formatAspects(result.aspects());
        String keys = formatKeys(result.researchKeys());
        String fallbackText = result.generatedFallback() ? " generated fallback" : " explicit assignment";

        source.sendSuccess(() -> Component.literal(
                "Thaumcraft scan result for " + scanType + ": " + result.objectKey()
                        + " [" + result.displayName() + "]"
        ), false);

        source.sendSuccess(() -> Component.literal(
                "Aspects:" + aspects + " | keys:" + keys + " (" + fallbackText.trim() + ")"
        ), false);

        source.sendSuccess(() -> Component.literal(
                "This command is read-only. Thaumometer use may mutate scan, research and knowledge state."
        ), false);

        return Math.max(1, result.aspects().visSize());
    }

    private static String formatAspects(AspectList aspects) {
        if (aspects == null || aspects.size() == 0) {
            return " none";
        }

        StringJoiner joiner = new StringJoiner(", ", " ", "");

        for (Aspect aspect : aspects.getAspectsSortedByName()) {
            if (aspect != null) {
                joiner.add(aspect.getTag() + "=" + aspects.getAmount(aspect));
            }
        }

        return joiner.toString();
    }

    private static String formatKeys(Iterable<String> keys) {
        StringJoiner joiner = new StringJoiner(", ", " ", "");
        for (String key : keys) {
            joiner.add(key);
        }
        String out = joiner.toString();
        return out.isBlank() ? " none" : out;
    }
}

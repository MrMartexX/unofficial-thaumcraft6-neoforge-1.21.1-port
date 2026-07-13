package thaumcraft.common.warp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import thaumcraft.common.config.TCConfig;

public final class TCWarpCommands {
    private static final String[] ROOT_ALIASES = {"thaumcraft", "thaum", "tc"};

    private TCWarpCommands() {
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if (!TCConfig.ENABLE_WARP_DEBUG_COMMANDS.get()) {
            return;
        }

        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        for (String root : ROOT_ALIASES) {
            dispatcher.register(Commands.literal(root)
                    .requires(source -> source.hasPermission(2))
                    .then(warpTree()));
        }
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> warpTree() {
        return Commands.literal("warp")
                .then(Commands.literal("get")
                        .executes(TCWarpCommands::getWarp))
                .then(Commands.literal("clear")
                        .executes(TCWarpCommands::clearWarp))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("get")
                                .executes(TCWarpCommands::getWarp))
                        .then(Commands.literal("clear")
                                .executes(TCWarpCommands::clearWarp))
                        .then(Commands.literal("set")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0, TCPlayerWarp.MAX_WARP))
                                                .executes(TCWarpCommands::setWarp))))
                        .then(Commands.literal("add")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0, TCPlayerWarp.MAX_WARP))
                                                .executes(TCWarpCommands::addWarp)))));
    }

    private static int getWarp(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = resolvePlayer(context);
        TCPlayerWarp warp = TCWarpManager.get(player);
        context.getSource().sendSuccess(() -> Component.literal(
                "Thaumcraft warp for " + player.getGameProfile().getName()
                        + ": permanent=" + warp.get(TCWarpType.PERMANENT)
                        + ", normal=" + warp.get(TCWarpType.NORMAL)
                        + ", temporary=" + warp.get(TCWarpType.TEMPORARY)
                        + ", actual=" + warp.actualWarp()
                        + ", counter=" + warp.getCounter()
        ), false);
        return warp.actualWarp();
    }

    private static int setWarp(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        TCWarpType type = parseTypeOrFail(context);
        int amount = IntegerArgumentType.getInteger(context, "amount");
        int updated = TCWarpManager.set(player, type, amount);
        context.getSource().sendSuccess(() -> Component.literal(
                "Set " + player.getGameProfile().getName() + " " + type.id() + " warp to " + updated + "."
        ), true);
        return updated;
    }

    private static int addWarp(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        TCWarpType type = parseTypeOrFail(context);
        int amount = IntegerArgumentType.getInteger(context, "amount");
        int updated = TCWarpManager.add(player, type, amount);
        context.getSource().sendSuccess(() -> Component.literal(
                "Added " + amount + " " + type.id() + " warp to " + player.getGameProfile().getName()
                        + ". Now " + updated + "."
        ), true);
        return updated;
    }

    private static int clearWarp(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = resolvePlayer(context);
        TCWarpManager.clear(player);
        context.getSource().sendSuccess(() -> Component.literal(
                "Cleared Thaumcraft warp for " + player.getGameProfile().getName() + "."
        ), true);
        return 1;
    }

    private static TCWarpType parseTypeOrFail(CommandContext<CommandSourceStack> context) {
        String value = StringArgumentType.getString(context, "type");
        TCWarpType type = TCWarpType.parse(value);
        if (type == null) {
            context.getSource().sendFailure(Component.literal("Unknown warp type: " + value + ". Expected permanent, normal or temporary."));
            throw new IllegalArgumentException("Unknown warp type: " + value);
        }
        return type;
    }

    private static ServerPlayer resolvePlayer(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        try {
            return EntityArgument.getPlayer(context, "player");
        } catch (IllegalArgumentException ignored) {
            return context.getSource().getPlayerOrException();
        }
    }
}

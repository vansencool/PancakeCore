package dev.vansen.pancakecore.commands.user;

import dev.vansen.commandutils.CommandUtils;
import dev.vansen.commandutils.argument.CommandArgument;
import dev.vansen.commandutils.command.CheckType;
import dev.vansen.commandutils.subcommand.SubCommand;
import dev.vansen.pancakecore.PancakeCore;
import dev.vansen.pancakecore.economy.ValueTranslator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PayCommand {

    public PayCommand() {
        CommandUtils.command("pay")
                .argument(CommandArgument.string("amount")
                        .argument(CommandArgument.string("who")
                                .completion((context, wrapper) -> {
                                    Bukkit.getOnlinePlayers()
                                            .stream()
                                            .map(Player::getName)
                                            .filter(name -> name.startsWith(wrapper.helper().currentArgOr()))
                                            .forEach(wrapper::suggest);
                                    return wrapper.build();
                                })
                                .defaultExecute(context -> {
                                    context.check(CheckType.PLAYER);
                                    if (context.player().getName().equals(context.argString("who"))) {
                                        context.actionBar("<#ff6183>You cannot pay yourself!");
                                        return;
                                    }
                                    if (!Bukkit.getOfflinePlayer(context.argString("who")).isOnline()) {
                                        context.response("<#ff6183>Are you sure that you want to pay '" +
                                                context.argString("who") + "'? That player is offline! Click <click:run_command:/pay " +
                                                context.argString("amount") + " " + context.argString("who") + " --force><bold>Yes</bold></click> to continue");
                                        return;
                                    }
                                    try {
                                        double amount = ValueTranslator.convert(context.argString("amount"));
                                        if (!PancakeCore.economy().has(context.player(), amount)) {
                                            throw new Exception("You do not have enough money to pay " + context.argString("amount") + "!");
                                        }
                                        PancakeCore.economy().withdrawPlayer(context.player(), amount);
                                        PancakeCore.economy().depositPlayer(context.argString("who"), amount);
                                        context.response("<#87ff93>You paid " + context.argString("amount") + " to " + context.argString("who"));
                                    } catch (Exception e) {
                                        context.response("<#ff6183>Error: " + e.getMessage());
                                    }
                                })
                                .subCommand(SubCommand.of("--force")
                                        .defaultExecute(context -> {
                                            context.check(CheckType.PLAYER);
                                            if (context.player().equals(context.argPlayer("who"))) {
                                                context.actionBar("<#ff6183>You cannot pay yourself!");
                                                return;
                                            }
                                            try {
                                                double amount = ValueTranslator.convert(context.argString("amount"));
                                                if (!PancakeCore.economy().has(context.player(), amount)) {
                                                    throw new Exception("You do not have enough money to pay " + context.argString("amount") + "!");
                                                }
                                                PancakeCore.economy().withdrawPlayer(context.player(), amount);
                                                PancakeCore.economy().depositPlayer(context.argString("who"), amount);
                                                context.response("<#87ff93>You paid " + context.argString("amount") + " to " + context.argString("who"));
                                            } catch (Exception e) {
                                                context.response("<#ff6183>Error: " + e.getMessage());
                                            }
                                        }))))
                .register();
    }
}

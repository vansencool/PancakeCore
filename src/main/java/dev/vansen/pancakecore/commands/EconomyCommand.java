package dev.vansen.pancakecore.commands;

import dev.vansen.commandutils.CommandUtils;
import dev.vansen.commandutils.argument.CommandArgument;
import dev.vansen.commandutils.permission.CommandPermission;
import dev.vansen.commandutils.subcommand.SubCommand;
import dev.vansen.pancakecore.PancakeCore;
import dev.vansen.pancakecore.economy.ValueTranslator;

public class EconomyCommand {

    public EconomyCommand() {
        CommandUtils.command("economy")
                .aliases("eco")
                .permission(CommandPermission.OP)
                .subCommand(SubCommand.of("set")
                        .argument(CommandArgument.string("amount")
                                .argument(CommandArgument.player("who")
                                        .defaultExecute(context -> {
                                            try {
                                                double amount = ValueTranslator.convert(context.argString("amount"));
                                                PancakeCore.economy().depositPlayer(context.player(), amount);
                                                context.response("<color:#87ff93>You deposited " + amount + " to " + context.argPlayer("who").getName());
                                            } catch (Exception e) {
                                                context.response("<#ff6183>Error: " + e.getMessage());
                                            }
                                        }))))
                .subCommand(SubCommand.of("add")
                        .argument(CommandArgument.string("amount")
                                .argument(CommandArgument.player("who")
                                        .defaultExecute(context -> {
                                            try {
                                                double amount = ValueTranslator.convert(context.argString("amount"));
                                                PancakeCore.economy().depositPlayer(context.player(), amount);
                                                context.response("<#87ff93>You added " + amount + " to " + context.argPlayer("who").getName());
                                            } catch (Exception e) {
                                                context.response("<#ff6183>Error: " + e.getMessage());
                                            }
                                        }))))
                .subCommand(SubCommand.of("remove")
                        .argument(CommandArgument.string("amount")
                                .argument(CommandArgument.player("who")
                                        .defaultExecute(context -> {
                                            try {
                                                double amount = ValueTranslator.convert(context.argString("amount"));
                                                PancakeCore.economy().withdrawPlayer(context.argPlayer("who"), amount);
                                                context.response("<#87ff93>You removed " + amount + " from " + context.argPlayer("who").getName());
                                            } catch (Exception e) {
                                                context.response("<#ff6183>Error: " + e.getMessage());
                                            }
                                        }))))
                .subCommand(SubCommand.of("reset")
                        .argument(CommandArgument.player("who")
                                .defaultExecute(context -> {
                                    PancakeCore.economy().withdrawPlayer(context.argPlayer("who"), PancakeCore.economy().getBalance(context.argPlayer("who")));
                                    context.response("<#87ff93>You reset the balance of " + context.argPlayer("who").getName());
                                })))
                .register();
    }
}

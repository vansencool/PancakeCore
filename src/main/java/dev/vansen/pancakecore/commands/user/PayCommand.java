package dev.vansen.pancakecore.commands.user;

import dev.vansen.commandutils.CommandUtils;
import dev.vansen.commandutils.argument.CommandArgument;
import dev.vansen.commandutils.command.CheckType;
import dev.vansen.pancakecore.PancakeCore;
import dev.vansen.pancakecore.economy.ValueTranslator;

public class PayCommand {

    public PayCommand() {
        CommandUtils.command("pay")
                .argument(CommandArgument.string("amount")
                        .argument(CommandArgument.player("who")
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
                                        PancakeCore.economy().depositPlayer(context.argPlayer("who"), amount);
                                        context.response("<#87ff93>You paid " + amount + " to " + context.argPlayer("who").getName());
                                        context.argPlayer("who").sendMessage("<#87ff93>You received " + amount + " from " + context.player().getName());
                                    } catch (Exception e) {
                                        context.response("<#ff6183>Error: " + e.getMessage());
                                    }
                                })))
                .register();
    }
}

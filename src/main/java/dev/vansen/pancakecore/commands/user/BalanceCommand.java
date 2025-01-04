package dev.vansen.pancakecore.commands.user;

import dev.vansen.commandutils.CommandUtils;
import dev.vansen.commandutils.argument.CommandArgument;
import dev.vansen.pancakecore.PancakeCore;
import dev.vansen.pancakecore.economy.ValueTranslator;

public class BalanceCommand {

    public BalanceCommand() {
        CommandUtils.command("balance")
                .aliases("bal")
                .defaultExecute(context -> context.actionBar("<#87ff93>Your balance: " + ValueTranslator.format(PancakeCore.economy().getBalance(context.player()))))
                .argument(CommandArgument.player("who")
                        .defaultExecute(context -> context.actionBar("<#87ff93>" + context.argPlayer("who").getName() + "'s balance: " + ValueTranslator.format(PancakeCore.economy().getBalance(context.argPlayer("who"))))))
                .register();
    }
}

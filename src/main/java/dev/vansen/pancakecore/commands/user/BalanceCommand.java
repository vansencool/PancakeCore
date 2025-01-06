package dev.vansen.pancakecore.commands.user;

import dev.vansen.commandutils.CommandUtils;
import dev.vansen.commandutils.argument.CommandArgument;
import dev.vansen.pancakecore.PancakeCore;
import dev.vansen.pancakecore.economy.ValueTranslator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BalanceCommand {

    public BalanceCommand() {
        CommandUtils.command("balance")
                .aliases("bal")
                .defaultExecute(context -> context.actionBar("<#87ff93>Your balance: " + ValueTranslator.format(PancakeCore.economy().getBalance(context.player()))))
                .argument(CommandArgument.string("who")
                        .completion((context, wrapper) -> {
                            Bukkit.getOnlinePlayers()
                                    .stream()
                                    .map(Player::getName)
                                    .filter(name -> name.startsWith(wrapper.helper().currentArgOr()))
                                    .forEach(wrapper::suggest);
                            return wrapper.build();
                        })
                        .defaultExecute(context -> context.actionBar("<#87ff93>" + context.argString("who") + "'s balance: " + ValueTranslator.format(PancakeCore.economy().getBalance(context.argString("who"))))))
                .register();
    }
}

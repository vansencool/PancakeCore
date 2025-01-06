package dev.vansen.pancakecore.commands.staff;

import dev.vansen.commandutils.CommandUtils;
import dev.vansen.commandutils.argument.CommandArgument;
import dev.vansen.commandutils.command.CheckType;

public class FeedCommand {

    public FeedCommand() {
        CommandUtils.command("feed")
                .aliases("f")
                .permission("pancakecore.feed")
                .defaultExecute(context -> {
                    context.check(CheckType.PLAYER);
                    context.player().setFoodLevel(20);
                    context.player().setSaturation(20);
                    context.player().setArrowsInBody(0);
                    context.response("<#87ff93>You fed yourself!");
                })
                .argument(CommandArgument.player("who")
                        .defaultExecute(context -> context.response("<#87ff93>You fed " + context.argPlayer("who").getName())))
                .register();
    }
}

package dev.vansen.pancakecore.commands.home;

import dev.vansen.commandutils.CommandUtils;
import dev.vansen.commandutils.argument.CommandArgument;
import dev.vansen.commandutils.subcommand.SubCommand;
import dev.vansen.pancakecore.homes.HomeManager;

import java.util.Objects;

public class HomeCommand {

    public HomeCommand() {
        CommandUtils.command("home")
                .subCommand(SubCommand.of("set")
                        .argument(CommandArgument.integer("index")
                                .defaultExecute(context -> {
                                    HomeManager.createHome(context.player(), context.argInt("index"), context.location());
                                    context.response("<#87ff93>Home " + context.argInt("index") + " set!");
                                })))
                .subCommand(SubCommand.of("tp")
                        .argument(CommandArgument.integer("index")
                                .defaultExecute(context -> {
                                    if (!HomeManager.isSet(context.player(), context.argInt("index"))) {
                                        context.response("<#ff6183>Home " + context.argInt("index") + " is not set!");
                                        return;
                                    }
                                    context.teleportAsync(Objects.requireNonNull(HomeManager.getHome(context.player(), context.argInt("index"))).getLocation());
                                    context.response("<#87ff93>You teleported to home " + context.argInt("index"));
                                })))
                .register();
    }
}
